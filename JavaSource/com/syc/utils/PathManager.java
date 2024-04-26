package com.syc.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;

import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.hibernate.entities.imx_documento_id;
import com.syc.fortimax.hibernate.entities.imx_org_carpeta;
import com.syc.fortimax.hibernate.managers.imx_documento_manager;
import com.syc.fortimax.hibernate.managers.imx_org_carpeta_manager;
import com.syc.imaxfile.Carpeta;
import com.syc.imaxfile.CarpetaManager;
import com.syc.imaxfile.CarpetaManagerException;
import com.syc.imaxfile.GetDatosNodo;

public class PathManager {
	private static final Logger log = Logger.getLogger(PathManager.class);
	private BidiMap pathNodo = new DualHashBidiMap();
	private Map<GetDatosNodo,Serializable> nodoEntity = new HashMap<GetDatosNodo,Serializable>();
	private String usuario = null;
	
	public PathManager(GetDatosNodo nodo) throws Exception {
		this.add("/", nodo);
	}
	
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	public String getUsuario() {
		return this.usuario;
	}
	
	public String getPath(GetDatosNodo nodo) {
		return (String) pathNodo.getKey(nodo);
	}
	
	public Serializable getEntity(String path) throws Exception {
		GetDatosNodo nodo = getNodo(path);
		return getEntity(nodo);
	}
	
	private Serializable getEntity(GetDatosNodo nodo) throws EntityManagerException, CarpetaManagerException {
		if(nodo==null)
			return null;
		Serializable entity = nodoEntity.get(nodo);
		if(entity==null&&(nodo.isCarpeta()||nodo.isDocumento())){
			entity = (Serializable) nodo.getHibernateObject();
			if(entity!=null)
				nodoEntity.put(nodo, entity);
		}
		return entity;
	}

	public GetDatosNodo getNodo(String path) throws Exception {
		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);
		return (GetDatosNodo) pathNodo.get(path.toLowerCase());
	}
	
	public void add(String path, GetDatosNodo nodo) throws Exception {
		if(path == null)
			throw new Exception("No se puede añadir el path nulo");
		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);
		path = path.toLowerCase();
		pathNodo.put(path, nodo);
		log.trace("Añadido "+nodo+":"+path);
	}

	public void add(String path, Carpeta imx_carpeta) throws Exception {
		add(path, (Serializable)imx_carpeta);
	}

	public void add(String path, imx_documento imx_documento) throws Exception {
		add(path, (Serializable)imx_documento);
	}
	
	private void add(String path, Serializable entity) throws Exception {
		if(entity == null) {
			throw new Exception("No se puede añadir un entity nulo");
		} else if(entity instanceof Carpeta) {
			Carpeta imx_carpeta = (Carpeta)entity;
			GetDatosNodo nodo = new GetDatosNodo(imx_carpeta.getNodo());
			add(path, nodo);
			nodoEntity.put(nodo, imx_carpeta);
		} else if(entity instanceof imx_documento) {
			imx_documento imx_documento = (imx_documento)entity;
			GetDatosNodo nodo = new GetDatosNodo(imx_documento.getId().toString());
			add(path, nodo);
			nodoEntity.put(nodo, imx_documento);
		}
	}

	public imx_documento creaDocumento(String path) throws Exception {
		if(path.endsWith("/"))
			throw new Exception("Un documento no puede terminar en '/'");
		Serializable entity = getEntity(path);
		if(entity instanceof Carpeta)
			throw new Exception("No se puede crear el documento, ya existe una carpeta del mismo nombre");
		else if(entity instanceof imx_documento) {
			imx_documento imx_documento = (imx_documento)entity;
			if(imx_documento!=null)
				return imx_documento;
		}
		int index = path.lastIndexOf('/');
		String nombreDocumento = path.substring(index+1);
		Carpeta imx_carpeta = creaCarpeta(path.substring(0, index));
		
		imx_documento_manager imx_documento_manager = new imx_documento_manager();
		imx_documento_manager.setMaxResults(1);
		imx_documento imx_documento = imx_documento_manager.select(imx_carpeta.getNodo()).selectNombreDocumento(nombreDocumento).uniqueResult();
		if(imx_documento==null) {
			imx_documento_id imx_documento_id = new imx_documento_id();
			imx_documento_id.setTituloAplicacion(imx_carpeta.getTituloAplicacion());
			imx_documento_id.setIdGabinete(imx_carpeta.getIdGabinete());
			imx_documento_id.setIdCarpetaPadre(imx_carpeta.getIdCarpeta());
			
			imx_documento = new imx_documento();
			imx_documento.setId(imx_documento_id);
			imx_documento.setNombreDocumento(nombreDocumento);
			imx_documento.setNombreUsuario(this.getUsuario());
			imx_documento.setDescripcion(nombreDocumento+" subido por "+this.getUsuario());
			Serializable serializable = imx_documento_manager.save(imx_documento);
			if(serializable==null) //TODO: Revisar que el documento si se haya insertado con el id_documento, no parece funcionar
				throw new Exception("No se pudo crear el documento "+imx_documento.getNombreDocumento());
			this.add(path,imx_documento);
		}
		return imx_documento;
	}

	public Carpeta creaCarpeta(String path) throws Exception {
		log.trace("Creando carpeta '"+path+"'");
		Serializable entity = getEntity(path);
		if(entity instanceof imx_documento)
			throw new Exception("No se puede crear la carpeta, ya existe un documento del mismo nombre");
		else if(entity instanceof Carpeta) {
			Carpeta imx_carpeta = (Carpeta)entity;
			if(imx_carpeta!=null)
				return imx_carpeta;
		}
		if(!path.startsWith("/"))
			path = "/"+path;
		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);
		String nombreCarpeta=path;
		int index = path.lastIndexOf('/');
		nombreCarpeta = path.substring(index+1);
		String pathCarpetaPadre = path.substring(0, index);
		Carpeta imx_carpeta_padre = creaCarpeta(pathCarpetaPadre);
		GetDatosNodo nodoPadre = new GetDatosNodo(imx_carpeta_padre.getNodo());
		imx_org_carpeta_manager imx_org_carpeta_manager = new imx_org_carpeta_manager();
		imx_org_carpeta_manager.setMaxResults(1);
		imx_org_carpeta imx_org_carpeta = imx_org_carpeta_manager.select(nodoPadre.getGaveta(),nodoPadre.getGabinete(),null,nodoPadre.getIdCarpeta(),nombreCarpeta).uniqueResult();
		if(imx_org_carpeta==null) {
			Carpeta c = new Carpeta(nodoPadre.getGaveta(), nodoPadre.getGabinete(), nodoPadre.getIdCarpeta(), nombreCarpeta+" creada por "+this.usuario);
			c.setNombreUsuario(this.usuario);
			c.setNombreCarpeta(nombreCarpeta);
			CarpetaManager cm = new CarpetaManager(c);
			boolean exito = cm.insertCarpeta(c,false);
			if(exito) {
				Carpeta imx_carpeta = new CarpetaManager().selectCarpeta(c);
				this.add(path,imx_carpeta);
				return imx_carpeta;
			} else
				throw new Exception("No se puede crear la carpeta");
		} else {
			Carpeta c = new Carpeta(imx_org_carpeta.getId().getTituloAplicacion(), imx_org_carpeta.getId().getIdGabinete(), imx_org_carpeta.getId().getIdCarpetaHija());
			Carpeta imx_carpeta = new CarpetaManager().selectCarpeta(c);
			this.add(path,imx_carpeta);
			return imx_carpeta;
		}	
	}
}
