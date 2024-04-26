package com.syc.fortimax.managers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import com.syc.catalogos.CatalogosManager;
import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_catalogo_privilegios;
import com.syc.fortimax.hibernate.entities.imx_grupo;
import com.syc.fortimax.hibernate.entities.imx_grupo_usuario;
import com.syc.fortimax.hibernate.entities.imx_grupo_usuario_id;
import com.syc.fortimax.hibernate.entities.imx_usuario;
import com.syc.fortimax.hibernate.entities.imx_grupo_privilegio;
import com.syc.fortimax.hibernate.entities.imx_grupo_privilegio_id;
import com.syc.servlets.models.PrivilegiosModel;
import com.syc.utils.Json;

public class GruposManager {
	private static final Logger log = Logger.getLogger(CatalogosManager.class);		
	
	public List<HashMap<String, String>> ObtenerGrupos(boolean todos,String grupo){
		List<HashMap<String, String>> Grupos=new ArrayList<HashMap<String, String>>();
		Session session=HibernateUtils.getSession();
		
		try{
			Criteria criteria = session.createCriteria(imx_grupo.class);
			if(!todos)
				criteria.add(Restrictions.eq("nombreGrupo", grupo));
				
			@SuppressWarnings("unchecked")
			List<imx_grupo> ListGr = criteria.list();
			
			for(imx_grupo var:ListGr){			
				HashMap<String, String> rec = new HashMap<String, String>();
				rec.put("nombreGrupo", var.getNombreGrupo());
				rec.put("descripcion", var.getDescripcion());
				Grupos.add(rec);
			}
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener Grupos GruposManager");
			log.error(exc, exc);
		}
		return Grupos;		
	}
	
	public ArrayList<Object> ObtenerUsuariosD(String grupo){
		log.trace("Usuarios disponibles para: "+grupo);
		ArrayList<Object> usuarios=new ArrayList<Object>();
	
		try{
			HibernateManager hibernateManager = new HibernateManager();
			DetachedCriteria criteriaGrupoUsuario = DetachedCriteria.forClass(imx_grupo_usuario.class).add(Restrictions.eq("id.nombreGrupo", grupo));
			Criterion criterion = Subqueries.propertyNotIn("nombreUsuario", criteriaGrupoUsuario.setProjection(Property.forName("id.nombreUsuario")));	
			hibernateManager.setCriterion(criterion);
			hibernateManager.addProjection(Projections.property("nombreUsuario"));
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("nombreUsuario"));
			hibernateManager.setOrders(orders);
			@SuppressWarnings("unchecked")
			List<String> ListNombreUsuarios = (List<String>) hibernateManager.list(imx_usuario.class);
			hibernateManager.close();
		
			for(String nombreUsuario:ListNombreUsuarios){
				HashMap<String, String> rec = new HashMap<String, String>();
				rec.put("nombreUsuario", nombreUsuario);
				usuarios.add(rec);
			}
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener usuarios disponibles: GruposManager");
			log.error(exc, exc);
		}
		return usuarios;
		
	}
	
	public ArrayList<Object> ObtenerUsuariosA(String grupo){
		log.trace("Usuarios asignados para: "+grupo);
		ArrayList<Object> usuarios=new ArrayList<Object>();
		Session session=HibernateUtils.getSession();
		try{
			Criteria criteria = session.createCriteria(imx_grupo_usuario.class);
			criteria.add(Restrictions.eq("id.nombreGrupo", grupo));
			@SuppressWarnings("unchecked")
			List<imx_grupo_usuario> ListGr = criteria.list();
			HashMap<String, Object> rec = new HashMap<String, Object>();
			for(imx_grupo_usuario var: ListGr){
				rec.clear();
				rec.put("nombreUsuario", var.getId().getNombreUsuario());
				usuarios.add(rec.clone());
			}
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener usuarios asignados: GruposManager");
			log.error(exc, exc);
		}
		return usuarios;	
	}
	
	public List<String> getGruposAsignados(String usuario){
		log.trace("Grupos asignados para: "+usuario);
		
		HibernateManager hm = new HibernateManager();
		hm.setCriterion(Restrictions.eq("id.nombreUsuario", usuario));
		hm.addProjection(Projections.property("id.nombreGrupo"));
		@SuppressWarnings("unchecked")
		List<String> gruposAsignados = (List<String>)hm.list(imx_grupo_usuario.class);
		return gruposAsignados;	
	}
	
	public boolean existe(String grupo){
		log.trace("Entro a buscar si existe grupo");
		Session session = HibernateUtils.getSession();
		boolean existe;
		try{
			Criteria criteria = session.createCriteria(imx_grupo.class);
			criteria.add(Restrictions.eq("nombreGrupo", grupo));
			if(criteria.uniqueResult()!=null){
				existe=true;
			} else {
				existe=false;
			}
			log.trace("El resultado de la Busqueda es: "+existe);
			return existe;
		}
		catch (Exception exc) {
			log.error("Entro Catch  Existe grupo");
			log.error(exc, exc);
			return true;
		}
	}
	
	public boolean insertarGrupo(String grupo,String descripcion){
		log.debug("Entro a insertar grupo");
		Session session = HibernateUtils.getSession();
		boolean correcto=false;
		try{
			imx_grupo grupoM=new imx_grupo();
			Transaction trans=session.beginTransaction();
			grupoM.setNombreGrupo(grupo);
			grupoM.setDescripcion(descripcion);
			session.save(grupoM);
			trans.commit();
			correcto=true;			
		}
		catch (Exception exc) {
			log.error("Entro Catch  insertar grupo");
			log.error(exc, exc);
			correcto =false;
		}
		return correcto;
	}
	
	public boolean modGrupo(String grupo,String Descripcion){
		log.debug("Entro a modificar grupo en privilegios");
		Session session = HibernateUtils.getSession();
		boolean correcto=false;
		try{
				Transaction trans=session.beginTransaction();
				Query queryG=session.createQuery("UPDATE imx_grupo SET DESCRIPCION=:descripcion " +
						"WHERE NOMBRE_GRUPO=:grupo");
				queryG.setParameter("descripcion", Descripcion);
				queryG.setParameter("grupo", grupo);			
				queryG.executeUpdate();
				trans.commit();
			
			
			correcto=true;			
		}
		catch (Exception exc) {
			log.error("Entro Catch  modificar grupo en privilegios");
			log.error(exc, exc);
			correcto =false;
		}
		return correcto;
	}
	public List<String> usuariosI(List<String> usuarios,String grupo){
		List<String> result=new ArrayList<String>();
		
		try{
			Session session = HibernateUtils.getSession();
			
			Criteria criteria = session.createCriteria(imx_grupo_usuario.class);
			criteria.add(Restrictions.eq("id.nombreGrupo", grupo));
			@SuppressWarnings("unchecked")
			List<imx_grupo_usuario> usrG = criteria.list();
			
			List<String> tmp=new ArrayList<String>();
			for(imx_grupo_usuario var: usrG){
				tmp.add(var.getId().getNombreUsuario());
			}
			
			for(String us:usuarios){
				if(!tmp.contains(us)){
					result.add(us);
				}
			}
		}
		catch (Exception exc) {
			log.error("Entro Catch  listar usuarios");
			log.error(exc, exc);
		}
		return result;
	}
	public boolean modUsuariosGrupo(String grupo,String usuariosJson){
		log.debug("Entro a modificar grupo en privilegios");
		Session session = HibernateUtils.getSession();
		boolean correcto=false;
		List<String> usr=new ArrayList<String>();
		List<String> insertar=new ArrayList<String>();
		try{
			imx_usuario usuario[]=Json.getObject(usuariosJson, imx_usuario[].class);
			for(int i=0;i<usuario.length;i++){
				usr.add(usuario[i].getNombreUsuario());
			}

			String sentencia = "DELETE imx_grupo_usuario WHERE NOMBRE_GRUPO=:grupo";
			if(!usr.isEmpty())
				sentencia += " AND NOMBRE_USUARIO not in(:lista)";
				
//			usr.add("");
			Transaction trans=session.beginTransaction();
			Query queryDel=session.createQuery(sentencia);
			queryDel.setParameter("grupo", grupo);
			if(!usr.isEmpty())
				queryDel.setParameterList("lista", usr);
			queryDel.executeUpdate();
			trans.commit();
			insertar=usuariosI(usr,grupo);
			if(insertar!=null){
	
				imx_grupo_usuario us=new imx_grupo_usuario();
				imx_grupo_usuario_id usId=new imx_grupo_usuario_id();
				for(String var : insertar){
					if(var!=""){
					Session sessionI = HibernateUtils.getSession();
					Transaction tr=sessionI.beginTransaction();
					usId.setNombreGrupo(grupo);
					usId.setNombreUsuario(var);
					us.setId(usId);
					sessionI.save(us);
					tr.commit();
					}
				}
				
			}
			correcto=true;			
		}
		catch (Exception exc) {
			log.error("Entro Catch  modificar grupo en privilegios");
			log.error(exc, exc);
			correcto =false;
		}
		return correcto;
	}
	public boolean eliminaGrupo(String grupo){
		boolean result=false;
		try{
			Session session = HibernateUtils.getSession();
			Transaction trans=session.beginTransaction();
			Query query=session.createQuery("DELETE imx_grupo_usuario WHERE NOMBRE_GRUPO=:grupo");
			query.setParameter("grupo", grupo);
			query.executeUpdate();
			Query queryP=session.createQuery("DELETE imx_grupo_privilegio WHERE NOMBRE_GRUPO=:grupo");
			queryP.setParameter("grupo", grupo);
			queryP.executeUpdate();
			Query queryG=session.createQuery("DELETE imx_grupo WHERE NOMBRE_GRUPO=:grupo");
			queryG.setParameter("grupo", grupo);
			queryG.executeUpdate();
			trans.commit();
			result=true;
			
		}
		catch (Exception exc) {
			log.error("Entro Catch  listar usuarios");
			log.error(exc, exc);
			result=false;
		}
		return result;
	}
	//Area de grupos privilegios
	//
	public ArrayList<Object> ObtenerGruposP(){
		ArrayList<Object> Grupos=new ArrayList<Object>();
		Session session=HibernateUtils.getSession();

		try{
			Criteria criteria = session.createCriteria(imx_grupo.class);	
			@SuppressWarnings("unchecked")
			List<imx_grupo> ListGr = criteria.list();
			
			HashMap<String, Object> rec = new HashMap<String, Object>();
			for(imx_grupo var:ListGr){			
				rec.clear();
				rec.put("nombreControl", var.getNombreGrupo());
				Grupos.add(rec.clone());
			}			
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener Grupos de privilegios GruposManager");
			log.error(exc, exc);
		}
		return Grupos;	
	}
	
	@SuppressWarnings("unchecked")
	public  List<HashMap<String, String>> getGavetasP(String nombreGrupo){
		List<HashMap<String, String>> Gave = new ArrayList<HashMap<String, String>>();
		Session session=HibernateUtils.getSession();
		List<imx_grupo_privilegio> ListS=null;
		try{
			Query query=session.createQuery("from imx_grupo_privilegio WHERE NOMBRE_GRUPO=:grupo AND TITULO_APLICACION<>:usrG");
			query.setParameter("grupo", nombreGrupo);
			query.setParameter("usrG", "USR_GRALES");
			ListS=(List<imx_grupo_privilegio>) query.list();
			for (imx_grupo_privilegio vari : ListS) {
				HashMap<String, String> rec = new HashMap<String, String>();
	            rec.put("gaveta", vari.getId().getTituloAplicacion());
	            Gave.add(rec);
	        }
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener gavetas de grupos");
			log.error(exc, exc);
		}
		return Gave;
	}
	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
	public  ArrayList<Object> getPrivilegiosG(String grupo,String gaveta){
		ArrayList<Object> Priv = new ArrayList<Object>();
		List<imx_grupo_privilegio> ListS=null;
		Session session=HibernateUtils.getSession();
		try{
			
			Query query=session.createQuery("from imx_grupo_privilegio where NOMBRE_GRUPO=:grupo " +
					"and TITULO_APLICACION=:gaveta");
			query.setParameter("grupo", grupo);
			query.setParameter("gaveta", gaveta);
			ListS=(List<imx_grupo_privilegio>) query.list();
			if(query.uniqueResult()==null){
				Priv=decodeP(0);
			}
			else{
				for(imx_grupo_privilegio vari : ListS ){
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
	@SuppressWarnings("unchecked")
	public  ArrayList<Object> getArbolPrivilegios(String grupo){
		/*** Iconos ***/ String icono="grupo";
		ArrayList<Object> Arbol = new ArrayList<Object>();
		List<imx_grupo_privilegio> ListU=null;
		HashMap<String, Object> rec = new HashMap<String, Object>();
		Session sessionU=HibernateUtils.getSession();
		@SuppressWarnings("rawtypes")
		ArrayList listaFinal = new ArrayList(); 
		try{
		Query queryU=sessionU.createQuery("FROM imx_grupo_privilegio WHERE NOMBRE_GRUPO=:grupo");
		queryU.setParameter("grupo", grupo);
		queryU.setMaxResults(1);
			ListU=(List<imx_grupo_privilegio>) queryU.list();
			for(imx_grupo_privilegio varU : ListU){
				listaFinal.add(varU.getId().getNombreGrupo());
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
			log.error("Entro Catch  Obtener Arbol de grupos");
			log.error(exc, exc);
		}
		return Arbol;
	}
	@SuppressWarnings("unchecked")
	public static ArrayList<Object> getArbolGa(String grupo){
		/*** Iconos ***/ String icono="task-folder";
		ArrayList<Object> Ga = new ArrayList<Object>();
		List<imx_grupo_privilegio> ListG=null;
		HashMap<String, Object> rec = new HashMap<String, Object>();
		Session sessionG=HibernateUtils.getSession();
		try{
			Query queryG=sessionG.createQuery("FROM imx_grupo_privilegio WHERE NOMBRE_GRUPO=:grupo " +
					"AND TITULO_APLICACION not in(:usrGen)");
			queryG.setParameter("grupo", grupo);
			queryG.setParameter("usrGen", "USR_GRALES");
			ListG=(List<imx_grupo_privilegio>) queryG.list();
			for(imx_grupo_privilegio varG : ListG){
				rec.clear();
				rec.put("texto", varG.getId().getTituloAplicacion());
				rec.put("iconCls", icono);
				rec.put("children", getArbolPriv(varG.getPrivilegio()));
				Ga.add(rec.clone());
			}

		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener Arbol Gavetas de grupos");
			log.error(exc, exc);
		}
		return Ga;
	}
	@SuppressWarnings("unchecked")
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
			log.error("Entro Catch  Obtener Arbol Gavetas de grupos");
			log.error(exc, exc);
		}
		return Privs;
	}
	public  boolean ModPrivUsu(String datosJson){
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
			log.error("Entro Catch  modificar privilegios grupo");
			log.error(exc, exc);
		}
		return result;	
	}
	public static boolean EliminaGaveUsu(String grupo,String gavetasJsonEli){
		boolean result=false;
		PrivilegiosModel pModel[]=Json.getObject(gavetasJsonEli, PrivilegiosModel[].class);		
		try{
			if(pModel.length>0){				
				for(int i=0;i<pModel.length;i++){
					Session session=HibernateUtils.getSession();
					Transaction trans=session.beginTransaction();
					Query query=session.createQuery("delete imx_grupo_privilegio where NOMBRE_GRUPO = :grupo AND TITULO_APLICACION=:gaveta");
					query.setParameter("grupo", grupo);
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
			log.error("Entro Catch eliminar gavetas de grupo");
			log.error(exc, exc);
			result=false;
		}
		return result;
	}
	public static boolean RecorreGavetas(String grupo,String gavetasJson){
		log.debug("Entro a recorre Gavetas de grupo");
		boolean result=false;
		Session session=HibernateUtils.getSession();
		
	
		PrivilegiosModel pModel[]=Json.getObject(gavetasJson, PrivilegiosModel[].class);
		int privilegio;
		try{
				if(pModel.length>0){
					for(int i=0;i<pModel.length;i++){
						privilegio=encodePriv(pModel[i].getprivilegios());
						Query query=session.createQuery("from imx_grupo_privilegio WHERE NOMBRE_GRUPO=:grupo " +
								"AND TITULO_APLICACION=:gaveta");
						query.setParameter("grupo", grupo);
						query.setParameter("gaveta", pModel[i].getgaveta());
						if(query.uniqueResult()!=null){						
		
							log.trace("Gaveta existe :Modificacion");
							Session sessionN=HibernateUtils.getSession();
							Transaction trans=sessionN.beginTransaction();
							Query queryU=sessionN.createQuery("UPDATE imx_grupo_privilegio set PRIVILEGIO=:priv " +
									" WHERE NOMBRE_GRUPO=:grupo AND TITULO_APLICACION=:gaveta");
							queryU.setParameter("priv", privilegio);
							queryU.setParameter("grupo",grupo);
							queryU.setParameter("gaveta",pModel[i].getgaveta());
							queryU.executeUpdate();
							trans.commit();
				
			
						}
						else{
							//insersion
					
							log.trace("Gaveta no existe se tiene que crear");
							Session sessionL=HibernateUtils.getSession();
							Transaction transa=sessionL.beginTransaction();
							imx_grupo_privilegio pr=new imx_grupo_privilegio();
							imx_grupo_privilegio_id prID=new imx_grupo_privilegio_id();
							prID.setNombreGrupo(grupo);
							prID.setTituloAplicacion(pModel[i].getgaveta());
							pr.setId(prID);
							pr.setNombreNivel("PERSONALIZADO");
							pr.setPrivilegio(privilegio);
							sessionL.save(pr);
							transa.commit();
						
						}
					}
					result=true;
				}
				else{					
					log.error("No enviaron Gavetas para modificar");
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
	@SuppressWarnings("unchecked")
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

}
