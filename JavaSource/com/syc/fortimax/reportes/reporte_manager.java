package com.syc.fortimax.reportes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jdom.Element;

import com.syc.fortimax.config.Config;
import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_catalogo_tipo_documento;
import com.syc.fortimax.hibernate.entities.imx_tipos_documentos_index;
import com.syc.fortimax.hibernate.managers.imx_catalogo_tipo_documento_manager;
import com.syc.fortimax.hibernate.managers.imx_tipos_documentos_index_manager;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;

public class reporte_manager {
	private static final Logger log = Logger.getLogger(reporte_manager.class);
	
	@SuppressWarnings("unchecked")
	public List<Object> getAll(Class<?> clase){
		List<Object> lista=null;
		try{
		HibernateManager Hm=new HibernateManager();
		lista=(List<Object>)Hm.list(clase);
		}
		catch(Exception ex){
			log.error(ex.toString());
		}
		return lista;
	}
	@SuppressWarnings("unchecked")
	public List<Object> getFilter(Class<?> clase,String columnaBuscar,String valorBuscar){
		List<Object> lista=null;
		try{
		Session sess = HibernateUtils.getSession();
		String hquery = "FROM "+clase.getName()+" WHERE "+columnaBuscar+" like '%"+valorBuscar+"%'";
		Query q = sess.createQuery(hquery);
		lista=(List<Object>)q.list();
		
		}
		catch(Exception ex){
			log.error(ex.toString());
		}
		return lista;
	}
	@SuppressWarnings("unchecked")
	public List<?> getInformacionGeneral(String valorBusqueda){
		List<HashMap<String, Object>> lista=null;
		try{
			Boolean busqueda=false;
			if(valorBusqueda!=null&&valorBusqueda!="")
				busqueda=true;
			Properties pr = System.getProperties();
			Enumeration<?> epr = pr.propertyNames();
			HashMap<String, Object> record = new HashMap<String, Object>();
			List<HashMap<String, Object>> informacion_model= new ArrayList<HashMap<String, Object>>();
			if(!busqueda){
			record.put("propiedad", "fortimax.installationPath");
			record.put("valor", Config.fortimaxPath);
			informacion_model.add((HashMap<String, Object>)record.clone());
			}
			else{
				if("fortimax.installationpath".indexOf(valorBusqueda)!=-1||Config.fortimaxPath.toLowerCase().indexOf(valorBusqueda)!=-1){
					record.put("propiedad", "fortimax.installationPath");
					record.put("valor", Config.fortimaxPath);
					informacion_model.add((HashMap<String, Object>)record.clone());
				}
			}
			while (epr.hasMoreElements()) {
				String propiedad=(String) epr.nextElement();
				record.clear();
				if(busqueda){
					if(propiedad.toLowerCase().indexOf(valorBusqueda)!=-1||((String)pr.get(propiedad)).toLowerCase().indexOf(valorBusqueda)!=-1){
						record.put("propiedad",propiedad );
						record.put("valor", (String) pr.get(propiedad));
						informacion_model.add((HashMap<String, Object>)record.clone());
					}
				}
				else{
					record.put("propiedad",propiedad );
					record.put("valor", (String) pr.get(propiedad));
					informacion_model.add((HashMap<String, Object>)record.clone());
				}
			}
			Collections.sort(informacion_model, new Comparator<HashMap<String, Object>>() {
				@Override
				public int compare(HashMap<String, Object> arg0,
						HashMap<String, Object> arg1) {
					String v1=arg0.get("propiedad").toString();
					String v2=arg1.get("propiedad").toString();
					if(v1.compareTo(v2)>0)
						return 1;
					else if(v1.compareTo(v2)<0)
						return 0;
					else
						return 1;
				}

		    });
			lista=informacion_model;
		}
		catch(Exception ex){
			log.error(ex.toString());
		}
		return lista;
	}
	static class Datos {

		List<Object> datos= new ArrayList<Object>();
		
		public void add(String nombre, Object valor) {
			HashMap<String,Object> dato = new HashMap<String,Object>();
			dato.put("nombre", nombre);
			dato.put("valor", valor);
			datos.add(dato);	
		}

		public List<Object> getList() {
			return datos;
		}
		
		
	}
	public List<Object> getDetallesDocumento(String select, String basePath){
		List<Object> lista=null;
		try{
			Documento documento = new DocumentoManager().selectDocumento(select);
			
			imx_tipos_documentos_index_manager indexManager = new imx_tipos_documentos_index_manager(documento);
			List<imx_tipos_documentos_index> camposDocumento = indexManager.list();
			
			String plantilla="-Ninguna-";
			if(camposDocumento.size()>0) {
				int idTipoDocumento = camposDocumento.get(0).getId().getIdTipoDocumento();
			
				imx_catalogo_tipo_documento_manager tipoDocumentoManager = new imx_catalogo_tipo_documento_manager();
				imx_catalogo_tipo_documento tipoDocumento = tipoDocumentoManager.getTipoDocumento(idTipoDocumento);
				plantilla = tipoDocumento.getNombre();
			}
			
			Datos datos = new Datos();
			datos.add("Nombre",documento.getNombreDocumento());
			datos.add("Descripcion", documento.getDescripcion());
			datos.add("Numero de paginas", documento.getNumero_paginas());
			datos.add("Tamaño (KB)", (int)Math.round(documento.getTamanoBytes()/1024));
			datos.add("Plantilla", plantilla);
			datos.add("Creado", documento.getFechaFormateadaCreacion()+" a las "+documento.getHoraFormateadaCreacion());
			datos.add("Modificado", documento.getFechaFormateadaModificacion()+" a las "+documento.getHoraFormateadaModificacion());
			if(!documento.noEstaCompartido()){
				SimpleDateFormat sdfParameter = new SimpleDateFormat("yyyy/MM/dd:HH:mm");
				Date fecha=new Date();
					if(fecha.before(sdfParameter.parse(documento.getDateExp()+":"+documento.getHoureExp()))){
						datos.add("Compartir", basePath+"jsp/entregaDocumento.jsp?select="+select+"&token="+documento.getTokenCompartir()+
								"&tipodoc="+documento.getNombreTipoDocto());
						datos.add("Gaveta", documento.getTituloAplicacion().equals("USR_GRALES")?"Mis documentos":documento.getTituloAplicacion());
						datos.add("Carpeta", documento.getNombreCarpeta());
						datos.add("Fecha expiración", documento.getDateExp());
						datos.add("Hora expiración", documento.getHoureExp());
					}
					else{
						datos.add("Compartir", "No compartido");
					}
			}
			else{
				datos.add("Compartir", "No compartido");
			}
			lista=datos.getList();
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return lista;
	}
	public List<Object> getVersiones(String valorBusqueda){
		List<Object> lista=null;
		try{
			Boolean busqueda=false;
			if(valorBusqueda!=null&&valorBusqueda!="")
				busqueda=true;
			
			List<?> Versiones=Config.countV("versiones",null);
			Element El=null;
			Element Ed=null;
			ArrayList<Object> versiones_model= new ArrayList<Object>();
			HashMap<String,String> Version = new HashMap<String,String>();
			for(int i=0;i<Versiones.size();i++){
				Version.clear();
				if(!busqueda){
				El=(Element)Versiones.get(i);
				Ed=(Element)Config.countV("versiones", El.getName()).get(0);			
				Version.put("nombre", Ed.getValue());
				Ed=(Element)Config.countV("versiones", El.getName()).get(1);	
				Version.put("valor", Ed.getValue());
				versiones_model.add(Version.clone());
				}
				else{
					El=(Element)Versiones.get(i);
					Ed=(Element)Config.countV("versiones", El.getName()).get(0);	
					if(Ed.getValue().toLowerCase().indexOf(valorBusqueda)!=-1){
						Version.put("nombre", Ed.getValue());
						Ed=(Element)Config.countV("versiones", El.getName()).get(1);	
						Version.put("valor", Ed.getValue());
						versiones_model.add(Version.clone());
					}
				}
			}
			lista=versiones_model;
		}
		catch(Exception ex){
			log.error(ex.toString());
		}
		return lista;
	}
}
