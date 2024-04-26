package com.syc.gavetas;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import com.google.gson.Gson;
import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_aplicacion;
import com.syc.fortimax.hibernate.entities.imx_catalogo_estructuras;
import com.syc.fortimax.hibernate.entities.imx_estruc_doctos;
import com.syc.fortimax.hibernate.entities.imx_estruc_doctos_id;
import com.syc.servlets.models.estructurasModel;
import com.syc.utils.Json;

public class EstructuraManager {

	private static final Logger log = Logger.getLogger(EstructuraManager.class);
	
	public ArrayList<Object> ObtenerEstructuras(){
		ArrayList<Object> Estructuras=new ArrayList<Object>();
		Session session=HibernateUtils.getSession();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		List<imx_catalogo_estructuras> ListE=null;
		try{
			Query query=session.createQuery("from imx_catalogo_estructuras");
			ListE=(List<imx_catalogo_estructuras>)query.list();
			for(imx_catalogo_estructuras var:ListE){			
				rec.clear();
				rec.put("Nombre", var.getNombre());
				Estructuras.add(rec.clone());
			}
			
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener estructura: EstructuraManager");
			log.error(exc, exc);
		}
		return Estructuras;
	}
	public boolean crearEstructura(String estructuraJson){
		log.debug("Entro a crear estructura");
		boolean result=false;
		Session session = HibernateUtils.getSession();
		try{			
			imx_catalogo_estructuras estructuraJ=Json.getObject(estructuraJson, imx_catalogo_estructuras.class);
			imx_catalogo_estructuras estruc=new imx_catalogo_estructuras();
			Transaction trans=session.beginTransaction();
			estruc.setNombre(estructuraJ.getNombre());
			estruc.setDescripcion(estructuraJ.getDescripcion());
			estruc.setDefinicion(estructuraJ.getDefinicion());
			session.save(estruc);
			trans.commit();
			result=true;
			
		}
		catch (Exception exc) {
			log.error("Entro Catch  insertar estructura");
			log.error(exc, exc);
			result =false;
		}
	 return result;
	}
	
	public ArrayList<Object> ObtenerEstructura(String nombre){
		ArrayList<Object> Estructuras=new ArrayList<Object>();
		Session session=HibernateUtils.getSession();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		List<imx_catalogo_estructuras> ListE=null;
		try{
			Query query=session.createQuery("from imx_catalogo_estructuras WHERE NOMBRE=:nombre");
			query.setParameter("nombre", nombre);
			ListE=(List<imx_catalogo_estructuras>)query.list();
			for(imx_catalogo_estructuras var:ListE){			
				rec.clear();
				rec.put("Nombre", var.getNombre());
				rec.put("Descripcion", var.getDescripcion());
				Estructuras.add(rec.clone());
			}
			
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener estructura unica: EstructuraManager");
			log.error(exc, exc);
		}
		return Estructuras;
	}
	public  ArrayList<Object> getArbolE(String estructura){
		ArrayList<Object> Estructuras=new ArrayList<Object>();
		Session session=HibernateUtils.getSession();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		List<imx_catalogo_estructuras> ListE=null;
		String datos="";
		try{
			Query query=session.createQuery("from imx_catalogo_estructuras WHERE NOMBRE=:nombre");
			query.setParameter("nombre", estructura);
			ListE=(List<imx_catalogo_estructuras>)query.list();
			for(imx_catalogo_estructuras var:ListE){			
				datos=var.getDefinicion();
			}
			Estructuras=EmpaquetarArbol(datos,"root");
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener arbol estructura unica: EstructuraManager");
			log.error(exc, exc);
		}
		return Estructuras;
	}
	public ArrayList<Object> EmpaquetarArbol(String estructuraJson,String root){
		/* Clases css*/ 
		String iconoC="task-folder"/*"carpetaIcon"*/,clsC="carpeta",iconoD="documento",clsD="documentoCls";
		String leafC="false",leafD="true";
		/* Clases css*/
	
		ArrayList<Object> Arbol=new ArrayList<Object>();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		try{
			estructurasModel est[]=Json.getObject(estructuraJson, estructurasModel[].class);
			for(int i=0;i<est.length;i++){
				
				if(est[i].getParent().equals(root)){
					rec.clear();
//					if(est[i].getParent().equals("root")){
//						rec.put("expanded","true");
//					}  Solo expande al Root
					if(est[i].getTipo().equals("carpeta")){
					rec.put("expanded","true");
				} 
					rec.put("texto",est[i].getTexto());
					rec.put("tipo", est[i].getTipo());
					rec.put("ide", est[i].getIde());
					if(est[i].getTipo().equals("carpeta")){
						rec.put("iconCls", iconoC);
						rec.put("cls", clsC);
						rec.put("leaf", leafC);
					}
					else{
						rec.put("iconCls", iconoD);
						rec.put("cls", clsD);
						rec.put("leaf", leafD);
						rec.put("atributos", est[i].getAtributos());
					}
					rec.put("id", est[i].getIde());
					rec.put("parentId", est[i].getParent());
					rec.put("depth", est[i].getProfundidad());
					
					rec.put("children",EmpaquetarArbol(estructuraJson,est[i].getIde()));
					Arbol.add(rec.clone());
				}
			}
		}
		catch (Exception exc) {
			log.error("Entro Catch empaquetar estructura: EstructuraManager");
			log.error(exc, exc);
		}
		return Arbol;
	}
	public boolean actualizaEstructura(String nombre,String estructuraJson){
		log.debug("Entro a actualizar estructura");
		boolean result = false;
		Session session = HibernateUtils.getSession();
		try{			
			imx_catalogo_estructuras estructuraJ = Json.getObject(estructuraJson, imx_catalogo_estructuras.class);
			Transaction trans = session.beginTransaction();
			Query query=session.createQuery("UPDATE imx_catalogo_estructuras set DESCRIPCION=:descripcion " +
					", DEFINICION=:definicion WHERE NOMBRE=:nombre");
			query.setParameter("descripcion", estructuraJ.getDescripcion());
			query.setParameter("definicion", estructuraJ.getDefinicion());
			query.setParameter("nombre", nombre);
			query.executeUpdate();
			trans.commit();
			
			//Sección que actualiza imx_estruc_doctos
			List<HashMap<String, String>> gavetasAsignadas = ObtenerGavetas(nombre, false);
			//lo sig sólo se hace porque asignacion() requiere json en vez de aceptar un simple arreglo de String.
			List<imx_catalogo_estructuras> listaCatalogoEstr = new ArrayList<imx_catalogo_estructuras>();
			
			Gson g = new Gson();
			for (HashMap<String, String> gavetaAsignada : gavetasAsignadas) {
				imx_catalogo_estructuras ice = new imx_catalogo_estructuras();
				ice.setNombre(gavetaAsignada.get("Nombre"));
				listaCatalogoEstr.add(ice);
			}
			
			String jsonCatalogEstr = g.toJson(listaCatalogoEstr);
			listaCatalogoEstr.clear();

			asignacion(g.toJson(listaCatalogoEstr), nombre); //se envía json vacío para desasignar
			asignacion(jsonCatalogEstr, nombre);
			
			result = true;
		}
		catch (Exception exc) {
			log.error("Entro Catch  insertar estructura");
			log.error(exc, exc);
			result = false;
		}
	 return result;
	}
	public boolean Disponible(String estructuraJson){
		boolean disponible=false;
		Session session = HibernateUtils.getSession();
		try{
			imx_catalogo_estructuras estructuraJ=Json.getObject(estructuraJson, imx_catalogo_estructuras.class);
			String Nombre=estructuraJ.getNombre();
			Query query=session.createQuery("from imx_catalogo_estructuras WHERE NOMBRE= :Nombre");
			query.setString("Nombre", Nombre);
			if(query.uniqueResult()!=null){
				disponible=false;
			}
			else{
				disponible=true;
			}	
	
		}
			catch (Exception exc) {
				log.error("Entro Catch  verificar estructura unica");
				disponible=false;
				log.error(exc, exc);
			}
		return disponible;			
	}
	public boolean tieneGavetas(String Nombre){
		boolean result=false;
		try{
			Session session = HibernateUtils.getSession();
			Query query=session.createQuery("from imx_estruc_doctos WHERE L_NOMBRE_ESTRUCTURA= :Nombre");
			query.setString("Nombre", Nombre);
			if(query.list().size()!=0){
				result=false;
			}
			else{
				result=true;
			}	

			
		}
		catch (Exception exc) {
			log.error("Entro Catch  tiene gavetas");
			log.error(exc, exc);
			result=false;
		}
		return result;
	}
	public boolean eliminaEstructura(String Nombre){
		boolean result=false;
		try{
			Session session = HibernateUtils.getSession();
			Transaction trans=session.beginTransaction();
			Query query=session.createQuery("DELETE imx_catalogo_estructuras WHERE NOMBRE=:Nombre");
			query.setParameter("Nombre", Nombre);
			query.executeUpdate();
			trans.commit();
			result=true;

			
		}
		catch (Exception exc) {
			log.error("Entro Catch  eliminar estructura");
			log.error(exc, exc);
			result=false;
		}
		return result;
	}
	public List<HashMap<String,String>> ObtenerGavetas(String Nombre, Boolean disponibles){
		List<HashMap<String,String>> Gavetas=new ArrayList<HashMap<String,String>>();		
		try{
			HibernateManager hibernateManager = new HibernateManager();
			Conjunction conjunction = Restrictions.conjunction();
			DetachedCriteria criteriaEstructDoctos = DetachedCriteria.forClass(imx_estruc_doctos.class);
			criteriaEstructDoctos = criteriaEstructDoctos.setProjection(Property.forName("id.TituloAplicacion"));
			
			
			if(disponibles){	
				conjunction.add(Subqueries.propertyNotIn("tituloAplicacion", criteriaEstructDoctos));
			} else {
				criteriaEstructDoctos.add(Restrictions.eq("id.NombreEstructura", Nombre));
				conjunction.add(Subqueries.propertyIn("tituloAplicacion", criteriaEstructDoctos));
			}
				
			hibernateManager.setCriterion(conjunction);
			hibernateManager.addProjection(Projections.property("tituloAplicacion"));
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("tituloAplicacion"));
			hibernateManager.setOrders(orders);
			@SuppressWarnings("unchecked")
			List<String> ListNombreGavetas = (List<String>) hibernateManager.list(imx_aplicacion.class);
			hibernateManager.close();
			
			ListNombreGavetas.remove("USR_GRALES");
			
			for(String nombreGaveta : ListNombreGavetas){
				HashMap<String, String> rec = new HashMap<String, String>();
				rec.put("Nombre", nombreGaveta);
				Gavetas.add(rec); 
			}
		}
		catch (Exception exc) {
			log.error("Entro Catch  obtener gavetas");
			log.error(exc, exc);
		}
		return Gavetas;
	}
	public Boolean asignacion(String gavetas,String Nombre){
		Boolean result=false;
		try{
			//Solo se esta usando este modelo unicamente para extraer el nombre de la gaveta ya que 
			//la variable se llama Nombre.
			imx_catalogo_estructuras gav[]=Json.getObject(gavetas, imx_catalogo_estructuras[].class);
			List<String> GavE=new ArrayList<String>();
			for(int i=0;i<gav.length;i++){
				GavE.add(gav[i].getNombre());
			}			
			if(GavE.size()==0){
				Session se=HibernateUtils.getSession();
				Transaction trans=se.beginTransaction();
				Query qu=se.createQuery("DELETE imx_estruc_doctos WHERE L_NOMBRE_ESTRUCTURA=:Nombre");
				qu.setParameter("Nombre", Nombre);
				qu.executeUpdate();
				trans.commit();
				result=true;
			}
			else{
				GavE.add("");
				Session session=HibernateUtils.getSession();
				Transaction trans=session.beginTransaction();
				Query query=session.createQuery("DELETE imx_estruc_doctos WHERE L_NOMBRE_ESTRUCTURA=:Nombre " +
						"AND TITULO_APLICACION not in(:lista)");
				query.setParameter("Nombre", Nombre);
				query.setParameterList("lista", GavE);
				query.executeUpdate();
				trans.commit();
				Session ses=HibernateUtils.getSession();
				 Query q=ses.createQuery("from imx_estruc_doctos WHERE L_NOMBRE_ESTRUCTURA=:Nombre");
				 q.setParameter("Nombre", Nombre);
				 List<imx_estruc_doctos> lista=(List<imx_estruc_doctos>)q.list();
				 List<String> GActuales=new ArrayList<String>();
				for(imx_estruc_doctos var: lista){
					GActuales.add(var.getid().getTituloAplicacion());
				}
				HashSet<String> ha = new HashSet<String>(GActuales); 
				GActuales.clear();
				GActuales.addAll(ha);
				if(ArboltoTabla(Nombre)==true){			
				for(String vari:GavE){
					if(GActuales.indexOf(vari)==-1&&vari!=""){
						if(InserttoTabla(vari,Nombre)==true){
							log.trace("Estructura agregada a Gaveta: "+vari);
						}
						else{
							break;
						}
						
					}
				}
				}
				result=true;
			}
			
		}
		catch (Exception exc) {
			log.error("Entro Catch  asignacion de gavetas");
			log.error(exc, exc);
			result=false;
		}
		return result;
	}
	Vector<estructurasModel> V=new Vector<estructurasModel>();
	public Boolean rec(estructurasModel Arbol[],String root){
		Boolean result=false;
		try{
			for(int i=0;i<Arbol.length;i++){
				if(Arbol[i].getParent().equals(root)){
					V.add(Arbol[i]);
					rec(Arbol,Arbol[i].getIde());
				}
			}
		}
		catch (Exception exc) {
			log.error("Entro Catch rec");
			log.error(exc, exc);
			result=false;
		}
		return result;
	}
	public Boolean ArboltoTabla(String estructura){
		Boolean result=false;
		Session session=HibernateUtils.getSession();
		 String EJson="";
		try{
			Query query=session.createQuery("from imx_catalogo_estructuras WHERE NOMBRE=:Nombre");
			query.setParameter("Nombre", estructura);
			List<imx_catalogo_estructuras>listaE=(List<imx_catalogo_estructuras>)query.list();
			for(imx_catalogo_estructuras var:listaE){
				EJson=var.getDefinicion();
			}
			estructurasModel Arbol[]=Json.getObject(EJson, estructurasModel[].class);
			rec(Arbol,"0");
			result=true;

		}
		catch (Exception exc) {
			log.error("Entro Catch  Arbol to Tabla");
			log.error(exc, exc);
			result=false;
		}
		return result;
	}
	public Boolean InserttoTabla(String aplicacion,String estructura){
		Boolean result=false;
		
		try{
			imx_estruc_doctos es=new imx_estruc_doctos();
			imx_estruc_doctos_id es_id=new imx_estruc_doctos_id();
			/*for(int y=0;y<2;y++){
				Session session=HibernateUtils.getSession();
				Transaction trans=session.beginTransaction();
				es_id.setTituloAplicacion(aplicacion);
				if(y==0){
					es_id.setNombreEstructura(estructura);
				}
				else{
					es_id.setNombreEstructura("");
				}
				es_id.setPosicionElemento(0);
				es.setid(es_id);
				es.setNombreElemento("Carpeta raiz");
				es.setProfundidad(1);
				es.setPrioridad(-1);
				es.setLNombreEstructura(estructura);
				session.save(es);
				trans.commit();
			}*/
			for(int i=0;i<V.size();i++){
				if(i==0){
					Session session=HibernateUtils.getSession();
					Transaction trans=session.beginTransaction();
					es_id.setTituloAplicacion(aplicacion);
					es_id.setNombreEstructura(estructura);
					es_id.setPosicionElemento(0);
					es.setid(es_id);
					es.setNombreElemento("Carpeta raiz");
					es.setProfundidad(1);
					es.setPrioridad(-1);
					es.setLNombreEstructura(estructura);
					session.save(es);
					trans.commit();
				}
				Session session=HibernateUtils.getSession();
				Transaction trans=session.beginTransaction();
				es_id.setTituloAplicacion(aplicacion);
				es_id.setNombreEstructura(estructura);
				es_id.setPosicionElemento(i+1);
				es.setid(es_id);
				es.setNombreElemento(V.get(i).getTexto());
				es.setProfundidad(Integer.parseInt(V.get(i).getProfundidad()));
				if(V.get(i).getTipo().equals("carpeta")){
					es.setPrioridad(-1);
				}
				else{
					es.setPrioridad(3);
				}
				es.setLNombreEstructura(estructura);
				session.save(es);
				trans.commit();
			}
			result=true;
		}
		catch (Exception exc) {
			log.error("Entro Catch  Arbol to Tabla");
			log.error(exc, exc);
			result=false;
		}
		return result;
	}
	public String gavetaTieneEstructura(String gaveta){
		String resultado="";
		try{
			HibernateManager hm=new HibernateManager();
			hm.setCriterion(Restrictions.eq("id.TituloAplicacion", gaveta));
			hm.setMaxResults(1);
			imx_estruc_doctos estructura=(imx_estruc_doctos)hm.uniqueResult((imx_estruc_doctos.class));
			if(estructura!=null)
				resultado=estructura.getLNombreEstructura();
			
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return resultado;
	}
}
