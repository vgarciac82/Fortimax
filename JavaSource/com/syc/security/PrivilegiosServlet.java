package com.syc.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.fortimax.managers.PrivilegioManager;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.user.Usuario;
import com.syc.utils.Json;
import com.syc.utils.ParametersInterface;

/**
 * Servlet implementation class PrivilegiosServlet
 */
public class PrivilegiosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final Logger log=Logger.getLogger(PrivilegiosServlet.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PrivilegiosServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doWork(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doWork(request,response);
	}
	public void doWork(HttpServletRequest request,HttpServletResponse response){
		String action=request.getParameter("action");
		log.debug("Ejecutando accion: "+action);
		if("getPrivilegios".equals(action))
			ObtenerPrivilegios(request,response);
		else
			AccionInvalida(request,response);
			
	}
	private void AccionInvalida(HttpServletRequest request,
			HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String message = "El action ingresado no es válido";
		json.add("message", message);
		json.returnJson(response);
	}
	private void ObtenerPrivilegios(HttpServletRequest request, HttpServletResponse response){
		log.debug("Obteniedo privilegios");
		Json json=new Json();
		json.add("success", false);
		String msjRegreso="";
		try{
			String nodo=request.getParameter("select");
			GetDatosNodo gdn = new GetDatosNodo(nodo);
			HttpSession session=request.getSession();
			Usuario u = (Usuario) session.getAttribute(ParametersInterface.USER_KEY);
			
			ArrayList<Object> privilegios=PrivilegioManager.getPrivilegiosG(u.getNombreUsuario(), gdn.getGaveta());
			json.add("privilegios", privilegios);
			json.add("success", true);
		}
		catch(Exception e){
			json.add("success", false);
			log.error("Error obtener privilegios: "+e.toString());
		}
		json.add("mesage", msjRegreso);
		json.returnJson(response);
	}
}
