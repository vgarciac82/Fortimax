package com.syc.fortimax.managers;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.gavetas.EstructuraManager;
import com.syc.servlets.models.GavetaCampoModel;
import com.syc.utils.Json;
import com.syc.fortimax.hibernate.entities.imx_catalogo_tipo_documento;

public class DocumentosManager {
	private static final Logger log = Logger.getLogger(EstructuraManager.class);
	
	public boolean insertaDocumento(String Nombre,String Descripcion,String estructuraJson){
		boolean result=false;
		GavetaCampoModel[] estructuraModel = Json.getObject(estructuraJson, GavetaCampoModel[].class);
		String estructura="[";
		try{
			for(int i=0;i<estructuraModel.length;i++){
				estructura=estructura+
				"{'nombre':'"+
				estructuraModel[i].getNombre()+"',"+
				"'etiqueta':'"+
				estructuraModel[i].getEtiqueta()+"',"+
				"'orden':'"+
				Integer.toString(estructuraModel[i].getOrden())+"',"+
				"'longitud':'"+
				Integer.toString(estructuraModel[i].getTamano())+"',"+
				"'valor':'"+
				estructuraModel[i].getValor()+"',"+
				"'tipo':'"+
				estructuraModel[i].getTipo()+"',"+
				"'indice':'"+
				estructuraModel[i].getIndice()+"',"+
				"'requerido':'"+
				Boolean.toString(estructuraModel[i].getRequerido())+"',"+
				"'editable':'"+
				Boolean.toString(estructuraModel[i].getEditable())+"',"+
				"'lista':'"+
				estructuraModel[i].getLista()+"'}";
				if((i+1)<estructuraModel.length){
					estructura=estructura+",";
				}
				else{
					estructura=estructura+"]";
				}
			}
			imx_catalogo_tipo_documento doc=new imx_catalogo_tipo_documento();
			Session session=HibernateUtils.getSession();
			Transaction trans=session.beginTransaction();
			doc.setNombre(Nombre);
			doc.setDescripcion(Descripcion);
			doc.setEstructuraFormulario(estructura);
			session.save(doc);
			trans.commit();
			result=true;
		}
		catch(Exception e){
			log.error("Entro a catch de insertarDocumento:DocumentoManager");
			log.error(e,e);
			result=false;
		}
		return result;
	}
	public ArrayList<Object> ObtenerDocumento(String Nombre){
		ArrayList<Object> DatosArreglo = new ArrayList<Object>();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		Session session=HibernateUtils.getSession();
		String Descripcion="";
		String Campos="";
		int id=0;
		try{
			Query query=session.createQuery("FROM imx_catalogo_tipo_documento WHERE NOMBRE=:Nombre");
			query.setParameter("Nombre", Nombre);
			ArrayList<imx_catalogo_tipo_documento> list=(ArrayList<imx_catalogo_tipo_documento>)query.list();
			for(imx_catalogo_tipo_documento var:list){
				id=var.getId();
				Descripcion=var.getDescripcion();
				Campos=var.getEstructuraFormulario();
			}
			GavetaCampoModel[] estructuraModel = Json.getObject(Campos, GavetaCampoModel[].class);
			for(GavetaCampoModel vari:estructuraModel){
				rec.clear();
				rec.put("nombre", vari.getNombre());
				rec.put("etiqueta", vari.getEtiqueta());
				rec.put("orden", vari.getOrden());
				rec.put("longitud", vari.getTamano());
				rec.put("valor", vari.getValor());
				rec.put("tipo", vari.getTipo());
				rec.put("indice",vari.getIndice());
				rec.put("requerido", vari.getRequerido());
				rec.put("editable", vari.getEditable());
				rec.put("lista", vari.getLista());
				rec.put("NombreD", Nombre);
				rec.put("DescripcionD", Descripcion);
				rec.put("Id", id);
				DatosArreglo.add(rec.clone());
			}
			
		}
		catch(Exception e){
			log.error("Entro a catch de obtener documento: DocumentoManager");
			log.error(e,e);
		}
		return DatosArreglo;
	}
	public boolean editaDocumento(String Nombre,String Descripcion,String Estructura){
		boolean result=false;
		Session session=HibernateUtils.getSession();
		try{
			GavetaCampoModel[] estructuraModel = Json.getObject(Estructura, GavetaCampoModel[].class);
			String estructura="[";
				for(int i=0;i<estructuraModel.length;i++){
					estructura=estructura+
					"{'nombre':'"+
					estructuraModel[i].getNombre()+"',"+
					"'etiqueta':'"+
					estructuraModel[i].getEtiqueta()+"',"+
					"'orden':'"+
					Integer.toString(estructuraModel[i].getOrden())+"',"+
					"'longitud':'"+
					Integer.toString(estructuraModel[i].getTamano())+"',"+
					"'valor':'"+
					estructuraModel[i].getValor()+"',"+
					"'tipo':'"+
					estructuraModel[i].getTipo()+"',"+
					"'indice':'"+
					estructuraModel[i].getIndice()+"',"+
					"'requerido':'"+
					Boolean.toString(estructuraModel[i].getRequerido())+"',"+
					"'editable':'"+
					Boolean.toString(estructuraModel[i].getEditable())+"',"+
					"'lista':'"+
					estructuraModel[i].getLista()+"'}";
					if((i+1)<estructuraModel.length){
						estructura=estructura+",";
					}
					else{
						estructura=estructura+"]";
					}
				}
				Transaction trans=session.beginTransaction();
				Query q=session.createQuery("UPDATE imx_catalogo_tipo_documento SET DESCRIPCION=:Descripcion," +
						" ESTRUCTURA_FORMULARIO=:Estructura WHERE NOMBRE=:Nombre");
				q.setParameter("Descripcion", Descripcion);
				q.setParameter("Estructura", estructura);
				q.setParameter("Nombre", Nombre);
				q.executeUpdate();
				trans.commit();
				result=true;
		}
		catch(Exception e){
			log.error("Entro a catch de editaDocumento:DocumetoManager");
			log.error(e,e);
		}
		return result;
	}
	public ArrayList<Object> obtenerPlantillas(){
		ArrayList<Object> documentos=new ArrayList<Object>();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		Session session=HibernateUtils.getSession();
		try{
			Query q=session.createQuery("FROM imx_catalogo_tipo_documento");
			ArrayList<imx_catalogo_tipo_documento> lista=(ArrayList<imx_catalogo_tipo_documento>)q.list();
			for(imx_catalogo_tipo_documento var:lista){
				rec.clear();
				rec.put("Nombre", var.getNombre());
				documentos.add(rec.clone());
			}
		}
		catch(Exception e){
			log.error("Entro a catch de obtener documentos:DocumentosManager");
			log.error(e,e);
		}
		return documentos;
		
	}
}
