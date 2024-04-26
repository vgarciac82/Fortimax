import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.fortimax.hibernate.entities.imx_pagina;
import com.syc.fortimax.hibernate.managers.imx_pagina_manager;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.user.Usuario;
import com.syc.user.UsuarioPermisos;
import com.syc.utils.ParametersInterface;
import com.syc.utils.ServletUtils;
import com.syc.utils.Utils;

public class GetFileServlet extends HttpServlet implements ParametersInterface {

	private static final Logger log = Logger.getLogger(GetFileServlet.class);

	private static final long serialVersionUID = 909304337728599812L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		if (session == null) {
			resp.sendRedirect("index.jsp");
			return;
		}

		Usuario u = (Usuario) session.getAttribute(USER_KEY);
		if (u == null) {
			resp.sendRedirect("index.jsp");
			return;
		}

		ITree tree = (ITree) session.getAttribute(TREE_KEY);
		if (tree == null) {
			resp.sendRedirect("index.jsp");
			return;
		}

		String selectId = req.getParameter("select");
		if (selectId == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Sin nodo seleccionado");
			return;
		}
		
		boolean pluginEdit = "true".equals(req.getParameter("edit"));
		
		GetDatosNodo gdn = new GetDatosNodo(selectId);	
		try {
			gdn.setPagina(new Integer(req.getParameter("num_pagina")).intValue());
		} catch (Exception e) {	
		}
		
		imx_pagina imx_pagina = null;
		try {
			imx_pagina_manager imx_pagina_manager = new imx_pagina_manager();
			imx_pagina_manager.select(gdn.getGaveta(),gdn.getGabinete(),gdn.getIdCarpeta(),gdn.getIdDocumento(),gdn.getPagina());
			
			if (gdn.getPagina()==null) {
				imx_pagina_manager.setMaxResults(1);
				List<imx_pagina> imx_paginas = imx_pagina_manager.list();
				if(!imx_paginas.isEmpty())
					imx_pagina = imx_paginas.get(0);
			} else {
				imx_pagina = imx_pagina_manager.uniqueResult();
				if(imx_pagina==null)
					throw new Exception("La pagina "+gdn+" no existe"); 
			}
		} catch (Exception e) {
			log.error(e,e);
			resp.sendError(HttpServletResponse.SC_NO_CONTENT, "No se encontro la página "+gdn);
		}
		/*
		 * try{ //d = (Documento)tree.findNode(selectId,session).getObject();
		 * 
		 * } catch(NullPointerException e){ log.error(e,e); tree = null; tree =
		 * ActualizaArbolClass.ActualizaArbol(selectId, session); d =
		 * (Documento)tree.findNode(selectId).getObject(); }
		 */

		// Documento d = ((Documento)
		// tree.findNode(selectId,session).getObject());
		//String[] filesNames = d.getFilesNames();
		//String[] fullPathFilesNames = d.getFullPathFilesNames();

		if (imx_pagina!=null) {
			String ext = null;
			int pos = imx_pagina.getNomArchivoOrg().lastIndexOf(".");

			if (pos != -1)
				ext = imx_pagina.getNomArchivoOrg().substring(pos);

//			System.out.println(fullPathFilesNames[0]);
//			System.out.println(filesNames[0]);
			/*
			 * try { doDownload(resp, fullPathFilesNames[0], filesNames[0]); }
			 * catch (IOException e) { log.error(e,e);
			 * 
			 * resp.sendError(HttpServletResponse.SC_NO_CONTENT,
			 * "Documento no encontrado"); return; }
			 */
			
			try {
				/*Pagina pagina = new Pagina();
				if (req.getParameter("appletImpresion") != null) {
					int index = new Integer(req.getParameter("num_pagina")).intValue();
					pagina = imx_pagina.getPaginaDocumento(index);
				} else if (req.getParameter("appletImpresion") == null) {
					pagina = d.getPaginaDocumento(0);
				} else if (req.getParameter("fromBackup") == null) {
				}*/
				
				File file = new File(imx_pagina.getAbsolutePath());
				String contentType = Utils.getContentType(file);
				if (contentType==null)
					contentType = Utils.guessContentTypeFromName(imx_pagina.getNomArchivoOrg());
				
				String characterEncoding = null;
				log.trace("User-Agent: "+req.getHeader("User-Agent"));
				if(true) {
					if("application/xml".equals(contentType)) {
						contentType = "text/plain";
						characterEncoding = "UTF-8";
					}
				}			
				ServletUtils.write(file, resp, contentType, characterEncoding);
			} catch (IOException e) {
				log.error(e, e);
				resp.sendError(HttpServletResponse.SC_NO_CONTENT, "Documento no encontrado");
				return;
			} catch (Exception e) {
				log.error(e, e);
				return;
			}
		} else {
			// resp.sendRedirect("jsp/GuardaDocumento.jsp?select=" +
			// selectId);
			if (new UsuarioPermisos(u,gdn.getGaveta()).isCrear() && (u.getTipoOperacion()=="4".charAt(0))){
			//	resp.sendRedirect("jsp/PreGuardaDocto.jsp?select="
			//			+ selectId+"&nuevo=false&editable=true");
				String basePath = req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+req.getContextPath()+"/";
				
				resp.sendRedirect(basePath+"/jsp/PreGuardaDocto.jsp?select="+selectId+"&nuevo=false&editable=true");
				/*
				PrintWriter out=resp.getWriter();
				out.println("<script type=\"text/javascript\">");
				out.println("parent.document.location.href = \""+basePath+"/jsp/PreGuardaDocto.jsp?select="+selectId+"&nuevo=false&editable=true\";");
				out.println("</script>");*/
			}
			else {
				session.setAttribute("msg",
						new String[] { "No tiene privilegios." });
				resp.sendRedirect("jsp/Messages.jsp");
			}
			return;
		}
	}
}