package com.syc.fortimax.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.entities.Privilegio;
import com.syc.fortimax.hibernate.entities.imx_aplicacion;
import com.syc.fortimax.hibernate.entities.imx_catalogo_privilegios;
import com.syc.fortimax.hibernate.entities.imx_privilegio;
import com.syc.fortimax.hibernate.entities.imx_privilegio_id;
import com.syc.fortimax.hibernate.entities.imx_usuario;
import com.syc.servlets.models.PrivilegiosModel;
import com.syc.utils.Json;


public abstract class PrivilegioManager {

	private static Logger log = Logger.getLogger(PrivilegioManager.class);

	@SuppressWarnings("unchecked")
	public static ArrayList<Privilegio> getPrivilegios(Session sess,
			String nombreUsuario, String tituloAplicacion) {
		ArrayList<Privilegio> p = new ArrayList<Privilegio>();

		StringBuffer query = new StringBuffer();
		query.append("( ");
		query.append("   select ");
		query
				.append("   ia.TITULO_APLICACION, ia.DESCRIPCION , p.NOMBRE_NIVEL, p.PRIVILEGIO ");
		query.append("   from imx_privilegio p, imx_aplicacion ia ");
		query.append("   where p.TITULO_APLICACION = ia.TITULO_APLICACION ");
		//query.append("   and ia.TITULO_APLICACION <> 'USR_GRALES' ");
		query.append("   and p.NOMBRE_USUARIO =  :nombreUsuario");
		if (tituloAplicacion != null) {
			query.append(" AND ia.TITULO_APLICACION = :tituloAplicacion");
		}
		query.append(") ");
		query.append("union ");// Union entre privilegios de usuario y de Grupos
		query.append("( ");
		query.append("   select ");
		query
				.append("   ia.TITULO_APLICACION, ia.DESCRIPCION, gp.NOMBRE_NIVEL, gp.PRIVILEGIO ");
		query
				.append("   from imx_grupo_privilegio gp, imx_aplicacion ia, imx_grupo_usuario gu ");
		query.append("   where gp.TITULO_APLICACION = ia.TITULO_APLICACION ");
		//query.append("   and ia.TITULO_APLICACION <> 'USR_GRALES' ");
		query.append("   and gp.NOMBRE_GRUPO = gu.NOMBRE_GRUPO ");
		query.append("   and gu.NOMBRE_USUARIO =  :nombreUsuario");
		if (tituloAplicacion != null) {
			query.append(" AND ia.TITULO_APLICACION = :tituloAplicacion");
		}
		query.append(") ");
		query.append("ORDER by descripcion ");

		// Query con SQL estandar!!!
		// TODO ver la forma de hacerlo con HQL el UNION mas que nada
		SQLQuery q = sess.createSQLQuery(query.toString());
		q.setString("nombreUsuario", nombreUsuario);
		if (tituloAplicacion != null) {
			q.setString("tituloAplicacion", tituloAplicacion);
		}

		// TituloAplicacion, Descripcion, NombreNivel, Privilegio
		ArrayList<Object[]> rst = (ArrayList<Object[]>) q.list();

		// Kita los repetidos eligiendo el de mayor privilegio
		Privilegio last = new Privilegio();
		for (Object[] row : rst) {
			Privilegio next = new Privilegio(row);
			if (p.isEmpty()) { // Si no hay nada solo lo mete
				p.add(next);
			} else {
				if (last.getTituloAplicacion().equals(
						next.getTituloAplicacion())) {
					// Si ya existe revisa el privilegio
					// suma los privilegios
					p.get(p.size() - 1).setPrivilegio(
							next.getPrivilegio() | last.getPrivilegio());
				} else {
					// Si es una gaveta nueva la insrta
					p.add(next);
				}
			}
			last = next;
		}

		return p;
	}

	public static ArrayList<Privilegio> getPrivilegios(Session sess,
			String nombreUsuario) {
		return getPrivilegios(sess, nombreUsuario, null);
	}

	public static ArrayList<Privilegio> getPrivilegios(String nombreUsuario,
			String tituloAplicacion) {
		log.trace("Carga de privilegios "+nombreUsuario+" "+tituloAplicacion);
		Session sess = HibernateUtils.getSession();
		ArrayList<Privilegio> p = getPrivilegios(sess, nombreUsuario,
				tituloAplicacion);
		if (sess.isOpen()) {
			sess.close();
		}
		return p;
	}

	public static ArrayList<Privilegio> getPrivilegios(String nombreUsuario) {
		Session sess = HibernateUtils.getSession();
		ArrayList<Privilegio> p = getPrivilegios(sess, nombreUsuario);
		if (sess.isOpen()) {
			sess.close();
		}
		return p;
	}
	//Interfaz de Privilegios Extjs
	public static ArrayList<Object> getUsuariosP(){
		ArrayList<Object> Users = new ArrayList<Object>();
		Session session=HibernateUtils.getSession();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		List<imx_usuario> ListC=null;
		try{
			Query query=session.createQuery("from imx_usuario ORDER BY NOMBRE_USUARIO");
			ListC=(List<imx_usuario>) query.list();
			for (imx_usuario vari : ListC) {
	            rec.clear();
	            rec.put("nombreControl", vari.getNombreUsuario());
	            Users.add(rec.clone());
	        }
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener Usuarios");
			log.error(exc, exc);
		}
		return Users;
	}	
	public static boolean getValida(String usuario,String Aplicacion){
		boolean result=false;
		Session session = HibernateUtils.getSession();
		try{
			Query query=session.createQuery("from imx_privilegio WHERE TITULO_APLICACION= :aplicacion" +
					" AND NOMBRE_USUARIO=:usuario");
			query.setParameter("aplicacion", Aplicacion);
			query.setParameter("usuario", usuario);
			if(query.uniqueResult()!=null){
				result=true;
			}
			else{
				result=false;
				
			}

		}
		catch (Exception exc) {
			log.error("Entro Catch  getValida");
			log.error(exc, exc);
		}
		return result;
	}
	public static ArrayList<Object> getGavetasTotal(){
		ArrayList<Object> Gave = new ArrayList<Object>();
		Session session=HibernateUtils.getSession();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		List<imx_aplicacion> ListS=null;
		try{

			Query query=session.createQuery("from imx_aplicacion WHERE TITULO_APLICACION not in(:usrG) ORDER BY TITULO_APLICACION");
			query.setParameter("usrG", "USR_GRALES");
			ListS=(List<imx_aplicacion>) query.list();
			for (imx_aplicacion vari : ListS) {
	            rec.clear();
	            rec.put("gaveta", vari.getTituloAplicacion());
	            rec.put("descripcion", vari.getDescripcion());
	
	            Gave.add(rec.clone());
	        }
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener gavetas total");
			log.error(exc, exc);
		}
		return Gave;
	}
	@SuppressWarnings("unchecked")
	public static ArrayList<HashMap<String, String>> getGavetasP(String user){
		ArrayList<HashMap<String, String>> Gave = new ArrayList<HashMap<String, String>>();
		Session session=HibernateUtils.getSession();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		List<imx_privilegio> ListS=null;
		try{
			Query query=session.createQuery("from imx_privilegio WHERE NOMBRE_USUARIO=:user AND TITULO_APLICACION<>:usrG");
			query.setParameter("user", user);
			query.setParameter("usrG", "USR_GRALES");
			ListS=(List<imx_privilegio>) query.list();
			for (imx_privilegio vari : ListS) {
	            rec.clear();
	            rec.put("gaveta", vari.getid().getTituloAplicacion());
	            Gave.add((HashMap<String, String>) rec.clone());
	        }
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener gavetas");
			log.error(exc, exc);
		}
		return Gave;
	}
	public static ArrayList<Object> decodeP(int permiso){
		ArrayList<Object> Privs = new ArrayList<Object>();
		Session session=HibernateUtils.getSession();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		List<imx_catalogo_privilegios> ListS=null;
		try{
			Query query=session.createQuery("from imx_catalogo_privilegios");
			ListS=(List<imx_catalogo_privilegios>) query.list();
			for(imx_catalogo_privilegios var: ListS){
				rec.clear();
				rec.put("privilegio", var.getNombre());
				rec.put("descripcion", var.getDescripcion());
				if((permiso&var.getValor())==var.getValor()){
					rec.put("seleccionado", "1");
				}else{
					rec.put("seleccionado", "0");
				}
				Privs.add(rec.clone());
			}
		
		}
		catch (Exception exc) {
			log.error("Entro Catch  decodificar Privilegios");
			log.error(exc, exc);
		}
		return Privs;
	}
	public static ArrayList<Object> getPrivilegiosG(String user,String gaveta){
		ArrayList<Object> Priv = new ArrayList<Object>();
		List<imx_privilegio> ListS=null;
		Session session=HibernateUtils.getSession();
		try{
			
			Query query=session.createQuery("from imx_privilegio where NOMBRE_USUARIO=:user and TITULO_APLICACION=:gaveta");
			query.setParameter("user", user);
			query.setParameter("gaveta", gaveta);
			ListS=(List<imx_privilegio>) query.list();
			if(query.uniqueResult()==null){
				Priv=decodeP(0);
			}
			else{
				for(imx_privilegio vari : ListS ){
					Priv=decodeP(vari.getPrivilegio());
				}
			}
			return Priv;
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener Privilegios");
			log.error(exc, exc);
		}
		return Priv;
	}
	public static ArrayList<Object> getArbolPrivilegios(String user){
		/*** Iconos ***/ String icono="usuario";
		ArrayList<Object> Arbol = new ArrayList<Object>();
		List<imx_privilegio> ListU=null;
		HashMap<String, Object> rec = new HashMap<String, Object>();
		Session sessionU=HibernateUtils.getSession();
		ArrayList listaFinal = new ArrayList(); 
		try{
		Query queryU=sessionU.createQuery("FROM imx_privilegio WHERE NOMBRE_USUARIO=:user");
		queryU.setParameter("user", user);
		queryU.setMaxResults(1);
			ListU=(List<imx_privilegio>) queryU.list();
			for(imx_privilegio varU : ListU){
				listaFinal.add(varU.getid().getNombreUsuario());
			}
//			HashSet h = new HashSet(listaFinal);  Se deja por si se quiere hacer que muestre todos los usuario
//			listaFinal.clear();
//			listaFinal.addAll(h);
			for(int i=0;i<listaFinal.size();i++){
				rec.clear();
				rec.put("texto", listaFinal.get(i).toString());
				rec.put("iconCls", icono);
				rec.put("expanded", "true");
				rec.put("cls", "usrNode");
				rec.put("children",getArbolGa(listaFinal.get(i).toString()));
				Arbol.add(rec.clone());
			}
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener Arbol");
			log.error(exc, exc);
		}
		return Arbol;
	}
	public static ArrayList<Object> getArbolGa(String user){
		/*** Iconos ***/ String icono="task-folder";
		ArrayList<Object> Ga = new ArrayList<Object>();
		List<imx_privilegio> ListG=null;
		HashMap<String, Object> rec = new HashMap<String, Object>();
		Session sessionG=HibernateUtils.getSession();
		try{
			Query queryG=sessionG.createQuery("FROM imx_privilegio WHERE NOMBRE_USUARIO=:user " +
					"AND TITULO_APLICACION not in(:usrGen)");
			queryG.setParameter("user", user);
			queryG.setParameter("usrGen", "USR_GRALES");
			ListG=(List<imx_privilegio>) queryG.list();
			for(imx_privilegio varG : ListG){
				rec.clear();
				rec.put("texto", varG.getid().getTituloAplicacion());
				rec.put("iconCls", icono);
				rec.put("children", getArbolPriv(varG.getPrivilegio()));
				Ga.add(rec.clone());
			}

		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener Arbol Gavetas");
			log.error(exc, exc);
		}
		return Ga;
	}
	public static ArrayList<Object> getArbolPriv(int permisos){
		/*** Iconos ***/ String icono="task";
		ArrayList<Object> Privs = new ArrayList<Object>();
		Session session=HibernateUtils.getSession();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		List<imx_catalogo_privilegios> ListS=null;
		try{
			Query query=session.createQuery("from imx_catalogo_privilegios");
			ListS=(List<imx_catalogo_privilegios>) query.list();
			for(imx_catalogo_privilegios var: ListS){
				if((permisos&var.getValor())==var.getValor()){
					rec.clear();
					rec.put("texto", var.getNombre());
					rec.put("leaf","true");
					rec.put("iconCls", icono);
					Privs.add(rec.clone());
				}
				
			}
		
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener Arbol Gavetas");
			log.error(exc, exc);
		}
		return Privs;
	}
	public static boolean ModPrivUsu(String datosJson){
		boolean result=false;
		PrivilegiosModel pModel=Json.getObject(datosJson, PrivilegiosModel.class);
		try{
			if(EliminaGaveUsu(pModel.getnombreC(),pModel.getgavetasE())==true){
					if(RecorreGavetas(pModel.getnombreC(),pModel.getgavetas())==true){
						result=true;
					}
					else{
						result=false;
					}
			}
			else{
				result=false;
			}
		}
		catch (Exception exc) {
			log.error("Entro Catch  modificar privilegios usuarios");
			log.error(exc, exc);
		}
		return result;	
	}
	public static boolean EliminaGaveUsu(String user,String gavetasJsonEli){
		boolean result=false;
		PrivilegiosModel pModel[]=Json.getObject(gavetasJsonEli, PrivilegiosModel[].class);		
		try{
			if(pModel.length>0){				
				for(int i=0;i<pModel.length;i++){
					Session session=HibernateUtils.getSession();
					Transaction trans=session.beginTransaction();
					Query query=session.createQuery("delete imx_privilegio where NOMBRE_USUARIO = :user AND TITULO_APLICACION=:gaveta");
					query.setParameter("user", user);
					query.setParameter("gaveta", pModel[i].getgaveta());
					query.executeUpdate();
					trans.commit();
			
				}
				result=true;
			}
			else{
				result=true;
			}
		}
		catch (Exception exc) {
			log.error("Entro Catch eliminar gavetas de usuario");
			log.error(exc, exc);
			result=false;
		}
		return result;
	}
	public static int encodePriv(String PrivilegiosJson){
		int result=0;
		Session session=HibernateUtils.getSession();
		List<imx_catalogo_privilegios> Pr=null;
		try{
			PrivilegiosModel pModel[]=Json.getObject(PrivilegiosJson, PrivilegiosModel[].class);
			for(int i=0;i<pModel.length;i++){
				Query query=session.createQuery("from imx_catalogo_privilegios WHERE NOMBRE=:nombre");
				query.setParameter("nombre", pModel[i].getprivilegio());
				Pr=(List<imx_catalogo_privilegios>) query.list();
				for(imx_catalogo_privilegios var:Pr){
					result=result+var.getValor();
				}
			}
		}
		catch (Exception exc) {
			log.error("Entro Catch encodePrivs");
			log.error(exc, exc);
			result=0;
		}
		return result;
		
	}
	public static boolean RecorreGavetas(String user,String gavetasJson){
		log.debug("Entro a recorre Gavetas");
		boolean result=false;
		Session session=HibernateUtils.getSession();
		
	
		PrivilegiosModel pModel[]=Json.getObject(gavetasJson, PrivilegiosModel[].class);
		int privilegio;
		try{
				if(pModel.length>0){
					for(int i=0;i<pModel.length;i++){
						privilegio=encodePriv(pModel[i].getprivilegios());
						Query query=session.createQuery("from imx_privilegio WHERE NOMBRE_USUARIO=:user " +
								"AND TITULO_APLICACION=:gaveta");
						query.setParameter("user", user);
						query.setParameter("gaveta", pModel[i].getgaveta());
						if(query.uniqueResult()!=null){						
		
							log.trace("Gaveta existe :Modificacion");
							Session sessionN=HibernateUtils.getSession();
							Transaction trans=sessionN.beginTransaction();
							Query queryU=sessionN.createQuery("UPDATE imx_privilegio set PRIVILEGIO=:priv " +
									" WHERE NOMBRE_USUARIO=:user AND TITULO_APLICACION=:gaveta");
							queryU.setParameter("priv", privilegio);
							queryU.setParameter("user",user);
							queryU.setParameter("gaveta",pModel[i].getgaveta());
							queryU.executeUpdate();
							trans.commit();
				
			
						}
						else{
							//insersion
					
							log.trace("Gaveta no existe se tiene que crear");
							Session sessionL=HibernateUtils.getSession();
							Transaction transa=sessionL.beginTransaction();
							imx_privilegio pr=new imx_privilegio();
							imx_privilegio_id prID=new imx_privilegio_id();
							prID.setNombreUsuario(user);
							prID.setTituloAplicacion(pModel[i].getgaveta());
							pr.setid(prID);
							pr.setNombreNivel("PERSONALIZADO");
							pr.setPrivilegio(privilegio);
							sessionL.save(pr);
							transa.commit();
						
						}
					}
					result=true;
				}
				else{					
					log.trace("No enviaron Gavetas para modificar");
					result=true;
				}
		}
		catch (Exception exc) {
			log.error("Entro Catch RecorreGavetas");
			log.error(exc, exc);
			result=false;
		}
		return result;
	}

}
