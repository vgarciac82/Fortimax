package com.syc.catalogos;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.Map;

import com.syc.fortimax.hibernate.entities.imx_catalogo;
import com.syc.fortimax.hibernate.managers.imx_catalogo_manager;
import com.syc.fortimax.config.HibernateUtils;

import org.hibernate.Transaction;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.syc.servlets.models.DatosCatalogoModel;
import com.syc.utils.Json;



public class CatalogosManager {
	private static final Logger log = Logger.getLogger(CatalogosManager.class);
	
	public boolean catalogoUnico( String Nombre ){
		log.debug("Revisando si existe el catalogo : " + Nombre);
		boolean disponible=false;
		Session session = HibernateUtils.getSession();
		try{
			Query query=session.createQuery("from imx_catalogo WHERE NOMBRE_CATALOGO= :Nombre");
			query.setString("Nombre", Nombre);
			if(query.uniqueResult()!=null){
				disponible=false;
			}
			else{
				disponible=true;
			}
				
		return disponible;		
		}
		finally {
			try {
				session.close();
			} catch (Exception exc) {
				log.error("Entro Catch  verificar catalogo unico");
				log.error(exc, exc);
			}
		}
	}
	public boolean insertarCatalogo(String Nombre, String Tipo,String DatosJson, String definicion){
		log.debug("Insertando Catalogo : " + Nombre);
		Session session = HibernateUtils.getSession();
		try{
			DatosCatalogoModel datosCatalogoModel[]=Json.getObject(DatosJson, DatosCatalogoModel[].class);
			Transaction trans = session.beginTransaction();
			imx_catalogo ca=new imx_catalogo();
			ca.setNombreCatalogo(Nombre);
			if(datosCatalogoModel.length==0&&!definicion.isEmpty()) {
				ca.setTblCatalogo(null);
				ca.setLongitudCampo(35);
				ca.setDefinicion(definicion);
				session.save(ca);
				trans.commit();
				return true;
			} else {
				int mayor=Mayor(datosCatalogoModel);
				String tabla="MX"+Nombre;
				ca.setTblCatalogo(tabla);
				ca.setLongitudCampo(mayor);
				session.save(ca);
				
				if(trans!=null){
					if(creaTabla(tabla,DatosJson,mayor)==true){
						trans.commit();
						return true;
					}
					else{
						return false;
					}
				}
				else{
					return false;
				}
			}
			
		}
		catch (Exception exc) {
				log.error("Entro al catch  insertar catalogo");
				log.error(exc, exc);
				return false;
			}
	}
	public boolean creaTabla(String NombreTabla, String DatosJson,int mayor){
		Session session = HibernateUtils.getSession();
		try{
			Transaction trans=session.beginTransaction();
		Query queryT=session.createSQLQuery("CREATE TABLE "+NombreTabla+" ( CONSECUTIVO int PRIMARY KEY, VALOR VARCHAR("+mayor+"))");
	    queryT.executeUpdate();    
	    trans.commit();
	    DatosCatalogoModel datosCatalogoModel[]=Json.getObject(DatosJson, DatosCatalogoModel[].class);
		for(int i=0;i<datosCatalogoModel.length;i++){
			 insertaDato(datosCatalogoModel[i].getId(),datosCatalogoModel[i].getNombre(),NombreTabla);
		}	    
		return true;
		}
		catch (Exception exc) {
				log.error("Entro al Catch de Crar Tabla para Catalogo");
				log.error(exc, exc);
				return false;
			}
		
	}
	public void insertaDato(int id,String Nombre,String Tabla){
		Session session = HibernateUtils.getSession();
		try{
		Transaction trans = session.beginTransaction();
		Query queryD = session.createSQLQuery("INSERT INTO "+Tabla+" (CONSECUTIVO,VALOR) VALUES ("+id+",'"+Nombre+"')");
		queryD.executeUpdate();
		trans.commit();
		}
		catch(Exception E){
			log.error("Entro al catch de insertarDatos");
			log.error(E,E);
		}
	}
	public int Mayor(DatosCatalogoModel datosCatalogoModel[]){
		int mayor=0;
		try{
			for(int i=0;i<datosCatalogoModel.length;i++){
			log.trace("Compara: "+datosCatalogoModel[i].getNombre()+" : "+datosCatalogoModel[i].getNombre().length()+" con : "+Integer.toString(mayor));
				if(datosCatalogoModel[i].getNombre().length()>mayor){
					mayor=datosCatalogoModel[i].getNombre().length();
				}
			}	
			return mayor;
		}
		catch (Exception exc) {
			log.error("Entro Catch de Mayor");
			log.error(exc, exc);
			return 256;
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Map<String,Object>> getDatos(imx_catalogo imx_catalogo, String filtro) {
		if(imx_catalogo.getDefinicion()==null) {
			return getDatos(imx_catalogo.getNombreCatalogo());
		} else {
			Map<String, Object> definicion = Json.getMap(imx_catalogo.getDefinicion());
			definicion = new CaseInsensitiveMap(definicion);
			String gaveta = definicion.get("gaveta").toString();
			String campo = definicion.get("campo").toString();
			
			//START
			
			ArrayList<Map<String,Object>> DatosArreglo = new ArrayList<Map<String,Object>>();
			HashMap<String, Object> rec = new HashMap<String, Object>();
			log.trace("Generando arreglo de datos de catalogo : " + imx_catalogo.getNombreCatalogo());
			String tblGaveta="IMX"+gaveta;
			Session session = HibernateUtils.getSession();
			try{
				Query query=session.createSQLQuery("SELECT DISTINCT "+campo+" FROM "+tblGaveta+" ORDER BY "+campo+" ASC");
				query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
				List<Map<String,Object>> li=query.list();
				Map<String,Object> map= new HashMap<String,Object>();
				
				for (Map<String,Object> object : li)
				{
					map= object;
					rec.clear();
					rec.put("id",map.get(campo));
					rec.put("nombre",map.get(campo));	
					DatosArreglo.add((Map<String, Object>) rec.clone());
				}	
			}
			catch (Exception exc) {
				log.error("Entro al catch getDatos");
				log.error(exc, exc);
				return null;
			}
			return DatosArreglo;
			
			
			//END	
		}
	}
	
	private ArrayList<Map<String,Object>> getDatos(String Nombre) {
		ArrayList<Map<String,Object>> DatosArreglo = new ArrayList<Map<String,Object>>();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		log.trace("Generando arreglo de datos de catalogo : " + Nombre);
		Nombre="MX"+Nombre;
		Session session = HibernateUtils.getSession();
		try{
			Query query=session.createSQLQuery("SELECT * FROM "+Nombre+" ORDER BY VALOR ASC");
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> li=query.list();
			Map<String,Object> map= new HashMap<String,Object>();
			for (Map<String,Object> object : li)
			{
			map= object;
			rec.clear();
			if(map.get("CONSECUTIVO")!=null){
				rec.put("id",map.get("CONSECUTIVO"));
				rec.put("nombre",map.get("VALOR"));	
			}
			else{
				rec.put("id",map.get("consecutivo"));
				rec.put("nombre",map.get("valor"));	
			}
			DatosArreglo.add((Map<String, Object>) rec.clone());
			}	
		}
		catch (Exception exc) {
			log.error("Entro al catch getDatos");
			log.error(exc, exc);
			return null;
		}
		return DatosArreglo;
	}
	public boolean ExisteDatos(String Tabla, int id){
		ArrayList<Object> DatosArreglo = new ArrayList<Object>();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		boolean Existe=false;
		log.trace("Buscando el dato numero : " + Integer.toString(id)+" en Tabla: "+Tabla);
		Session session = HibernateUtils.getSession();
		try{
			Query query=session.createSQLQuery("select COUNT(*) as total from "+Tabla+" WHERE CONSECUTIVO="+Integer.toString(id));
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			List li=query.list();
			Map<String,Object> map= new HashMap<String,Object>();
			for (Object object : li)
			{
			map= (Map<String,Object>)object;
			log.trace("Resultado del Count: "+map.get("total"));
			if(Integer.parseInt(map.get("total").toString())>0){
				Existe= true;
			}
			else{
				Existe= false;
			}
			}	
			return Existe;
		}
		catch (Exception exc) {
			log.error("Entro al Catch Existe Dato");
			log.error(exc, exc);
			return false;
		}
	}
	public void ActualizaCampo(int id,String Nombre,String Tabla){
		Session session = HibernateUtils.getSession();
		try{
		Transaction trans = session.beginTransaction();
		Query queryD = session.createSQLQuery("UPDATE "+Tabla+" SET valor='"+Nombre+"' WHERE CONSECUTIVO="+Integer.toString(id));
		queryD.executeUpdate();
		trans.commit();
		}
		catch(Exception E){
			log.error("Entro al Catch de ActualizarCampo");
			log.error(E,E);
		}
	}
	public void EliminaDato(int id,String Tabla){
		Session session = HibernateUtils.getSession();
		try{
			Transaction trans = session.beginTransaction();
			Query queryD = session.createSQLQuery("DELETE  FROM "+Tabla+" WHERE CONSECUTIVO="+id);
			queryD.executeUpdate();
			trans.commit();
		}
		catch (Exception exc) {
			log.error("Entro al catch actualiza tabla");
			log.error(exc, exc);
		}
	}
	public boolean ModificaTabla(String Catalogo,int Maximo){
		boolean Correcto=false;
		Session session = HibernateUtils.getSession();

		try{

			Criteria criteria= session.createCriteria(imx_catalogo.class).add(Restrictions.eq("NombreCatalogo", Catalogo));
			imx_catalogo ca = (imx_catalogo) criteria.uniqueResult();
			int Actual=ca.getLongitudCampo();
			if(Actual<Maximo){
				log.trace("Modificando Valor maximo en imx_catalogo a: "+ Integer.toString(Maximo));
				Transaction trans = session.beginTransaction();
				Query query = session.createQuery("UPDATE imx_catalogo set LONGITUD_CAMPO = :Maximo where NOMBRE_CATALOGO = :Catalogo");
				query.setParameter("Maximo", Maximo);
				query.setParameter("Catalogo", Catalogo);
				query.executeUpdate();
				Query querySql=session.createSQLQuery("ALTER TABLE MX"+Catalogo+" MODIFY VALOR VARCHAR("+Integer.toString(Maximo)+") ;");
				querySql.executeUpdate();
				trans.commit();			
				Correcto=true;
			}
			else{
				Correcto=true;
			}
			return Correcto;
		}
		catch (Exception exc) {
			log.error("Entro al catch actualiza tabla");
			log.error(exc, exc);
			return false;
		}
	}
	public boolean Actualiza(String NombreC,String DatosActualiza,String DatosElimina, String definicion){
		try{
			imx_catalogo_manager imx_catalogo_manager = new imx_catalogo_manager();
			imx_catalogo imx_catalogo = imx_catalogo_manager.select(NombreC).uniqueResult();
			
			if(imx_catalogo.getTblCatalogo()==null) {
				imx_catalogo.setDefinicion(definicion);
				imx_catalogo_manager.update(imx_catalogo);
				return true;
			} 
			
			String Nombre="MX"+NombreC;
			DatosCatalogoModel datosActualiza[]=Json.getObject(DatosActualiza, DatosCatalogoModel[].class);
			int Maximo=Mayor(datosActualiza);
			if(ModificaTabla(NombreC,Maximo)==true){
				DatosCatalogoModel datosBorrados[]=Json.getObject(DatosElimina, DatosCatalogoModel[].class);
				for(int i=0;i<datosBorrados.length;i++){
					if(ExisteDatos(Nombre,datosBorrados[i].getId())==true){
					 EliminaDato(datosBorrados[i].getId(),Nombre);
					}
				}
				
			for(int i=0;i<datosActualiza.length;i++){
				if(ExisteDatos(Nombre,datosActualiza[i].getId())==false){
					log.trace("Inserta Dato: ID: "+datosActualiza[i].getId()+" : "+datosActualiza[i].getNombre());
					insertaDato(datosActualiza[i].getId(),datosActualiza[i].getNombre(),Nombre);
				}
				else{
					log.trace("Actualiza Dato: "+datosActualiza[i].getNombre());
					ActualizaCampo(datosActualiza[i].getId(),datosActualiza[i].getNombre(),Nombre);
				}
			}
			
			
			return true;
			}
			else{
				return false;
			}
		}
			
		catch (Exception exc) {
			log.error("Entro al catch actualiza datos");
			log.error(exc, exc);
			return false;
		}
	}
}
