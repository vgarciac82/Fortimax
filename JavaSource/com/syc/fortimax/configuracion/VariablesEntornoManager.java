package com.syc.fortimax.configuracion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdom.Element;

import com.syc.catalogos.CatalogosManager;
import com.syc.fortimax.config.Config;
import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_config;
import com.syc.fortimax.hibernate.managers.imx_config_manager;
import com.syc.utils.Json;

public class VariablesEntornoManager {
	private static final Logger log = Logger.getLogger(CatalogosManager.class);	
	public boolean EliminaFilas(String FilasJson){
		log.debug("Funcion de Eliminacion en Variables Manager Filas: "+FilasJson);
		boolean eliminada=false;
		try{
			imx_config FilasEliminadas[]=Json.getObject(FilasJson, imx_config[].class);
			if(FilasEliminadas.length>0){
			for(int i=0;i<FilasEliminadas.length;i++){
				if(FilasEliminadas[i].getID()!=0){
					EliminaFila(FilasEliminadas[i].getName());
					eliminada= true;
				}
				else{
					eliminada= true;
				}
			}
			}
			else{
				eliminada=true;
			}
			return eliminada;
		}
		catch (Exception exc) {
			log.info("Entro Catch  Actualiza Filas VariablesEntornoManager");
			log.error(exc, exc);
			return false;
		}
	}
	
	
	public Vector<String> ActualizaFilas(String FilasJson){
		log.debug("Funcion de Actualizacion en Variables Manager Filas: "+FilasJson);
		Vector<String> vector = new Vector<String>();
		try{
			imx_config FilasActualizadas[]=Json.getObject(FilasJson, imx_config[].class);
			for(int i=0;i<FilasActualizadas.length;i++){
				vector.add(ActualizaFila(FilasActualizadas[i]));
			}
		} catch (Exception exc) {
			log.info("Entro Catch  Actualiza Filas VariablesEntornoManager");
			log.error(exc, exc);
		}
		return vector;
	}
	
	public boolean existeNombre(String Nombre){
		log.debug("Entro a Buscar si Existe: "+Nombre);
		Session session = HibernateUtils.getSession();
		boolean existe;
		try{
			Query query=session.createQuery("from imx_config WHERE name= :Nombre");
			query.setParameter("Nombre", Nombre);
			if(query.uniqueResult()!=null){
				existe=true;
			}
			else{
				existe=false;
			}
			log.debug("El resultado de la Busqueda de "+Nombre+" es: "+existe);
			return existe;
		}
		catch (Exception exc) {
			log.info("Entro Catch  Existe VariablesEntornoManager");
			log.error(exc, exc);
			return false;
		}
	}
	
	public int getID(String name) throws EntityManagerException{
			imx_config_manager configManager = new imx_config_manager();
			configManager.selectName(name);
			imx_config config = configManager.uniqueResult();
			if(config!=null)
				return config.getID();
			else
				return -1;
	}
	
	public int idDisponible(){
		Session session = HibernateUtils.getSession();
		int Disponible;
		try{
			Query query=session.createQuery("select max(ID) from imx_config");
			if(query.uniqueResult()!=null){
				@SuppressWarnings("unchecked")
				List<Integer> list = query.list();
				Disponible=Integer.parseInt(list.get(0).toString())+1;
			}
			else{
				Disponible=1;
			}
			return Disponible;
		}
		catch (Exception exc) {
			log.info("Entro Catch  InsertarFila VariablesEntornoManager");
			log.error(exc, exc);
			return 0;
		}
	}

	public int InsertarFila(String Categoria,String Nombre,String Valor,String Descripcion) throws Exception{
		imx_config config = new imx_config();
		config.setCategory(Categoria);
		config.setName(Nombre);
		config.setValue(Valor);
		config.setDescription(Descripcion);
		
		imx_config_manager configManager = new imx_config_manager();
		return (Integer) configManager.save(config);
	}
	
	public String ActualizaFila(imx_config config){
		log.debug("Funcion Actualiza Fila con ID: "+config.getID()+" y Nombre: "+config.getName()); //TODO: Revisar los mensajes de log.
		Session session = HibernateUtils.getSession();
		String message = "";
		try{
			Transaction trans = session.beginTransaction();
			Query query = session.createQuery("UPDATE imx_config set value = :Valor, category=:Categoria, description=:Descripcion" +
    				" where ID = :ID AND name = :Nombre");
			query.setParameter("Valor", config.getValue());
			query.setParameter("Categoria", config.getCategory());
			query.setParameter("Descripcion", config.getDescription());
			query.setParameter("Nombre", config.getName());
			query.setParameter("ID", config.getID());
			int actualizados = query.executeUpdate();
			trans.commit();
			if(actualizados!=0) {
				message = "Se actualizo la configuración con ID: "+config.getID()+" y Nombre: "+config.getName();	
			} else {
				session = HibernateUtils.getSession();
				trans = session.beginTransaction();
				query = session.createQuery("UPDATE imx_config set name=:Nombre, value = :Valor, category=:Categoria, description=:Descripcion" +
	    				" where ID = :ID");
				query.setParameter("Valor", config.getValue());
				query.setParameter("Categoria", config.getCategory());
				query.setParameter("Descripcion", config.getDescription());
				query.setParameter("Nombre", config.getName());
				query.setParameter("ID", config.getID());
				message = "Se actualizo la configuración con ID: "+config.getID()+" y se le cambio el Nombre a: "+config.getName();
				actualizados = query.executeUpdate();
				trans.commit();
				if(actualizados!=0) {
					message = "Se actualizo la configuración con ID: "+config.getID()+" y se le cambio el Nombre a: "+config.getName();	
				} else { 
					InsertarFila(config.getCategory(), config.getName(), config.getValue(), config.getDescription());
					message = "Se inserto la configuración con Nombre: "+config.getName();
				}
			}
		} catch (Exception exc) {
			log.info("Entro Catch  InsertarFila VariablesEntornoManager");
			log.error(exc, exc);
		}
		return message;
	}
	
	public void EliminaFila(String Nombre){
		log.debug("Funcion  de Eliminacion de Fila con Nombre: "+Nombre);
		Session session=HibernateUtils.getSession();
		try{
			Transaction trans=session.beginTransaction();
			Query query=session.createQuery("delete imx_config where name = :Nombre");
			query.setParameter("Nombre", Nombre);
			query.executeUpdate();
			trans.commit();
			
		}
		catch (Exception exc) {
			log.info("Entro Catch  EliminaFila VariablesEntornoManager");
			log.error(exc, exc);
		}
	}
	
	public static String getValue(String name) throws EntityManagerException {
		imx_config_manager imx_config_manager = new imx_config_manager();
		imx_config_manager.selectName(name);
		imx_config imx_config = imx_config_manager.uniqueResult();
		if(imx_config==null)
			return Config.getValorVariableEntorno(name);
		else
			return imx_config.getValue();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String, Object>> obtenerVariables(){
		HashMap<String, Object> rec = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> VariablesE = new ArrayList<HashMap<String, Object>>();
		
		try{
			imx_config_manager configManager = new imx_config_manager();
			List<imx_config> listaVariablesEntorno = configManager.list();
			
			int id = idDisponible();
				
			List<Element> nombresVariables = Config.variablesEntorno;
				
			for (Element el : nombresVariables){
				listaVariablesEntorno.add(new imx_config(id++, el.getName(), el.getText(), el.getAttributeValue("categoria"), el.getAttributeValue("descripcion")));
			}
			
			List<String> nombres = new ArrayList<String>();
			for (imx_config vari : listaVariablesEntorno) {
				if(!nombres.contains(vari.getName())) {
					rec.clear();
					rec.put("ID", vari.getID());
					rec.put("category",vari.getCategory());
					rec.put("name",vari.getName());
					rec.put("value", vari.getValue());
					rec.put("description", vari.getDescription());
					VariablesE.add((HashMap<String, Object>) rec.clone());
					nombres.add(vari.getName());
				}
	        }
				
		} catch (Exception exc) {
			log.info("Entro Catch  Obtener Variables VariablesEntornoManager");
			log.error(exc, exc);
		}
		return VariablesE;
	}	
}
