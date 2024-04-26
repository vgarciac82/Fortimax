package com.syc.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.fortimax.hibernate.entities.imx_grupo_privilegio;
import com.syc.fortimax.hibernate.entities.imx_grupo_privilegio_id;
import com.syc.fortimax.hibernate.managers.imx_grupo_privilegio_manager;
import com.syc.fortimax.ldap.ActiveDirectoryManager;
import com.syc.gavetas.Gaveta;
import com.syc.gavetas.GavetaManager;
import com.syc.utils.Json;

public class LDAPServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(LDAPServlet.class);
	private static final long serialVersionUID = 1222465349870635382L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		   throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String action = request.getParameter("action");
		log.debug("Ejecutando accion: " + action);
		if (action != null) {
			if ("obtenerGavetasDisponibles".equals(action))
				obtenerGavetasDisponibles(request, response);
			else if ("obtenerGrupos".equals(action))
				obtenerGrupos(request, response);
			else if ("obtenerGavetasNoAsignadas".equals(action))
				obtenerGavetasNoAsignadas(request, response);
			else if ("obtenerGavetasAsignadas".equals(action))
				obtenerGavetasAsignadas(request, response);
			else if ("obtenerPermisosGaveta".equals(action))
				obtenerPermisosGaveta(request, response);
			else if ("setPermisosGaveta".equals(action))
				setPermisosGaveta(request, response);
			else if ("asignarGaveta".equals(action))
				asignarGaveta(request, response);
			else if ("desasignarGaveta".equals(action))
				desasignarGaveta(request, response);
		}
	}
	
	private void obtenerGavetasDisponibles(HttpServletRequest request,
			HttpServletResponse response) {
		Json json = new Json();
		json.add("gavetasDisponibles", obtenerGavetasDisponibles());
		json.returnJson(response);
	}

	private void obtenerGrupos(HttpServletRequest request,
			HttpServletResponse response) {
		Json json = new Json();
		json.add("grupos", toList("name",obtenerGrupos()));		
		json.returnJson(response);
	}

	private void obtenerGavetasAsignadas(HttpServletRequest request,
			HttpServletResponse response) {
		String grupo = request.getParameter("grupo");
		log.debug("Obteniendo las Gavetas Asignadas del grupo "+grupo);
		Json json = new Json();
		json.add("grupo", grupo);
		json.add("gavetasAsignadas", toList("name",new Privilegios().getGavetasAsignadas(grupo)));
		json.returnJson(response);
	}

	private void obtenerGavetasNoAsignadas(HttpServletRequest request,
			HttpServletResponse response) {
		String grupo = request.getParameter("grupo");
		log.debug("Obteniendo las Gavetas No Asignadas del grupo "+grupo);
		Json json = new Json();
		json.add("grupo", grupo);
		json.add("gavetasNoAsignadas", toList("name",new Privilegios().getGavetasNoAsignadas(grupo)));
		json.returnJson(response);
	}
	
	private List<HashMap<String, String>> toList(String nombreColumna, List<String> lista) {
		List<HashMap<String, String>> listaSalida = new ArrayList<HashMap<String, String>>();
		Iterator<String> iteratorLista = lista.iterator();
		while(iteratorLista.hasNext()) {
			String objetoActual = iteratorLista.next();
			HashMap<String,String> columnaActualHashMap = new HashMap<String,String>();
			columnaActualHashMap.put(nombreColumna, objetoActual);
		    listaSalida.add(columnaActualHashMap);
		}
		return listaSalida;
	}
	
	private List<HashMap<String, String>> toList(String nombreColumna, ArrayList<String> arrayList) {
		return toList(nombreColumna,(List<String>)arrayList);
	}
	
	private void setPermisosGaveta(HttpServletRequest request,
			HttpServletResponse response) {
		String grupo = request.getParameter("grupo");
		String gaveta = request.getParameter("gaveta");
		String accion = request.getParameter("accion");
		String permitido = request.getParameter("permitido");
		Json json = new Json();
		if (permitido.equals("true")||permitido.equals("false")) {
			imx_grupo_privilegio permisos=null;
			json.add("grupo", grupo);
			json.add("gaveta", gaveta);
			Privilegios privilegios = new Privilegios();
			try { 
				permisos = privilegios.getPermisos(grupo,gaveta);
			} catch (GavetaNoAsignadaException e) {
				json.add("error", "El grupo no tiene asignada esa Gaveta");
			} catch (GrupoSinGavetasAsignadasException e) {
				json.add("error", "Este grupo no tiene ninguna Gaveta Asignada");
			}
			if (permisos!=null) {
				boolean permitidoBoolean = false;
				if (permitido.equals("true"))
					permitidoBoolean=true;
				permisos.setPrivilegio(accion,permitidoBoolean);
				String resultado = privilegios.enviarPrivilegios();

				json.add("permisos", permisos.getPrivilegioMap());
				json.add("resultado", resultado);
			}
				json.returnJson(response);
		} else {
			json.add("error", "El parametro \"permitido\" debe ser booleano");
		}
	}
	
	private void obtenerPermisosGaveta(HttpServletRequest request,
			HttpServletResponse response) {
		String grupo = request.getParameter("grupo");
		String gaveta = request.getParameter("gaveta");
		Json json = new Json();
		json.add("grupo", grupo);
		json.add("gaveta", gaveta);
		try {
			json.add("permisos", new Privilegios().getPermisos(grupo,gaveta).getPrivilegioMap());
		} catch (GavetaNoAsignadaException e) {
			json.add("error", "El grupo no tiene asignada esa Gaveta");
		} catch (GrupoSinGavetasAsignadasException e) {
			json.add("error", "Este grupo no tiene ninguna Gaveta Asignada");
		}
		json.returnJson(response);
	}
	
	private void asignarGaveta(HttpServletRequest request,
			HttpServletResponse response) {
		String grupo = request.getParameter("grupo");
		String gaveta = request.getParameter("gaveta");
		
		Privilegios privilegios = new Privilegios();
		privilegios.asignarGaveta(grupo,gaveta);
		String resultado = privilegios.enviarPrivilegios();
		
		Json json = new Json();
		json.add("grupo", grupo);
		json.add("gaveta", gaveta);
		json.add("resultado", resultado);
		json.returnJson(response);
	}
	
	private void desasignarGaveta(HttpServletRequest request,
			HttpServletResponse response) {
		String grupo = request.getParameter("grupo");
		String gaveta = request.getParameter("gaveta");
		
		Privilegios privilegios = new Privilegios();
		privilegios.desasignarGaveta(grupo,gaveta);
		String resultado = privilegios.enviarPrivilegios();
		
		Json json = new Json();
		json.add("grupo", grupo);
		json.add("gaveta", gaveta);
		json.add("resultado", resultado);
		json.returnJson(response);
	}
	
	private List<String> obtenerGrupos() {
		log.debug("Obteniendo grupos");
		List<String> grupos = ActiveDirectoryManager.getAllGroups();
		Iterator<String> iteratorGrupos = grupos.iterator();
		String logGrupos = "Se han obtenido los siguientes grupos: ";
		while(iteratorGrupos.hasNext()) {
			String grupoActual = iteratorGrupos.next();
			logGrupos += grupoActual+",";
		}
		log.debug(logGrupos);
		return grupos;
	}
	
	private ArrayList<String> obtenerGavetasDisponibles() {
		ArrayList<Gaveta> gavetas = new ArrayList<Gaveta>();
		try {
			gavetas = new GavetaManager().listGavetas();
		} catch (Exception e) {
			log.error(e,e);
		}
		ArrayList<String> gavetasDisponibles = new ArrayList<String>();
		for(int i=0;i<gavetas.size();i++){
			gavetasDisponibles.add(gavetas.get(i).getNombre());
		}
		return gavetasDisponibles;
	}
	
	private class PrivilegiosGrupo {
		
		private HashMap<String,imx_grupo_privilegio> permisos = new HashMap<String,imx_grupo_privilegio>();
		
		public PrivilegiosGrupo() {
		}
		
		@SuppressWarnings("unused")
		public void clear() {
			permisos.clear();
		}
		
		public void add(String gaveta, imx_grupo_privilegio grupo_privilegio) {
			permisos.put(gaveta,grupo_privilegio);
		}
		
		public ArrayList<String> getGavetasAsignadas() {
			Object[] gavetasArray = permisos.keySet().toArray();
			String[] stringArray = Arrays.copyOf(gavetasArray, gavetasArray.length, String[].class);
			ArrayList<String> gavetasArrayList = new ArrayList<String>(Arrays.asList(stringArray));
			return gavetasArrayList;
		}
		
		public imx_grupo_privilegio getPermisos(String gaveta) throws GavetaNoAsignadaException {
			imx_grupo_privilegio imx_permisos = permisos.get(gaveta);
			if(imx_permisos==null)
				throw new GavetaNoAsignadaException(new Exception());
			return imx_permisos;
		}
	}
	
	private class GavetaNoAsignadaException extends Exception {
		private static final long serialVersionUID = -4025476221421211875L;

		public GavetaNoAsignadaException(Exception e){
		}
	}
	
	private class GrupoSinGavetasAsignadasException extends Exception {
		private static final long serialVersionUID = -4025476221421211875L;

		public GrupoSinGavetasAsignadasException(Exception e){
		}
	}
	
	private class Privilegios {
		HashMap<String,PrivilegiosGrupo> privilegios = new HashMap<String,PrivilegiosGrupo>();
		
		public Privilegios() {
			ArrayList<imx_grupo_privilegio> gp =  imx_grupo_privilegio_manager.get();
			String lastGroup="";
			PrivilegiosGrupo privilegiosGrupo = new PrivilegiosGrupo();
			for(int i=0; i<gp.size();i++){
				String grupo=gp.get(i).getId().getNombreGrupo();
				if(!grupo.equals(lastGroup)&&!lastGroup.equals("")){
					privilegios.put(lastGroup, privilegiosGrupo);
					privilegiosGrupo = new PrivilegiosGrupo();
				}
				String tituloAplicacion=gp.get(i).getId().getTituloAplicacion();
				privilegiosGrupo.add(tituloAplicacion, gp.get(i));
				lastGroup=grupo;
			}
			privilegios.put(lastGroup, privilegiosGrupo);
		}
		
		public imx_grupo_privilegio getPermisos(String grupo, String gaveta) throws GavetaNoAsignadaException, GrupoSinGavetasAsignadasException{
			if (privilegios.get(grupo)==null)
				throw new GrupoSinGavetasAsignadasException(new Exception());
			return privilegios.get(grupo).getPermisos(gaveta);
		}

		public ArrayList<String> getGavetasNoAsignadas(String grupo) {
			ArrayList<String> gavetasAsignadas = getGavetasAsignadas(grupo);
			ArrayList<String> gavetasDisponibles = obtenerGavetasDisponibles();
			gavetasDisponibles.removeAll(gavetasAsignadas);
			return gavetasDisponibles;
		}

		public ArrayList<String> getGavetasAsignadas(String grupo) {
			ArrayList<String> gavetasAsignadas = new ArrayList<String>(0);
			try {
				gavetasAsignadas = privilegios.get(grupo).getGavetasAsignadas();
			} catch (NullPointerException e){
			}
			return gavetasAsignadas;
		}
		
		public void asignarGaveta(String grupo, String gaveta) {
			//asigna la gaveta al grupo, generar asignaci�n y darle privilegios 0;
			imx_grupo_privilegio_id grupoPrivilegioID = new imx_grupo_privilegio_id(gaveta,grupo);
			imx_grupo_privilegio grupoPrivilegio = new imx_grupo_privilegio(grupoPrivilegioID,"PERSONALIZADO",0);
			PrivilegiosGrupo privilegiosGrupo = new PrivilegiosGrupo();
			privilegiosGrupo.add(gaveta, grupoPrivilegio);
			privilegios.put(grupo, privilegiosGrupo);
		}
		
		public void desasignarGaveta(String grupo, String gaveta) {
			imx_grupo_privilegio_id grupoPrivilegioID = new imx_grupo_privilegio_id(gaveta,grupo);
			imx_grupo_privilegio grupoPrivilegio = new imx_grupo_privilegio(grupoPrivilegioID,"PERSONALIZADO",-1);
			PrivilegiosGrupo privilegiosGrupo = new PrivilegiosGrupo();
			privilegiosGrupo.add(gaveta, grupoPrivilegio);
			privilegios.put(grupo, privilegiosGrupo);
		}
		
		public String enviarPrivilegios() {
			/*Envia los privilegios a la base de datos usando ActiveDirectoryManager;
			 *Convierte la nueva estructura de datos a la vieja para que ActiveDirectoryManager lo comprenda,
			 *Lo ideal sería sencillamente que ActiveDirectoryManager usara la nueva estructura de datos.
			*/
			HashMap<String, HashMap<String,Integer>> gruposPrivilegios = new HashMap<String, HashMap<String, Integer>>();
			Object[] grupoArray = privilegios.keySet().toArray();
			for(int i=0;i<grupoArray.length;i++) {
				PrivilegiosGrupo privilegiosGrupoActual = privilegios.get(grupoArray[i].toString());
				HashMap<String,Integer> gavetas = new HashMap<String,Integer>();
				ArrayList<String> gavetasAsignadas = privilegiosGrupoActual.getGavetasAsignadas(); 
				for (int j=0;j<gavetasAsignadas.size();j++){
					String gavetaActual = gavetasAsignadas.get(j).toString(); 
					try {
						gavetas.put(gavetaActual,privilegiosGrupoActual.getPermisos(gavetaActual).getPrivilegio());
					} catch (GavetaNoAsignadaException e) {
					}
				}
				gruposPrivilegios.put(grupoArray[i].toString(), gavetas);
			}		
			return ActiveDirectoryManager.reconfigurePrivilegios(gruposPrivilegios);
		}
	}
}
