import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.DocumentoManagerException;
import com.syc.imaxfile.PaginaManager;
import com.syc.tree.ActualizaArbolClass;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.user.UsuarioPermisos;
import com.syc.utils.Json;
import com.syc.utils.ParametersInterface;

public class DelPageDocumentServlet extends HttpServlet implements	ParametersInterface {
	private static final Logger log = Logger.getLogger(DelPageDocumentServlet.class);
	private static final long serialVersionUID = -2754554434626814155L;
	
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
	public void doWork(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		String action=request.getParameter("action");
		log.debug("Ejecutando accion: "+action);
		
		if("delPageDocument".equals(action))
			eliminarPagina(request,response);
		
		else if("deleteAllPages".equals(action))
			deleteAllPages(request,response);
			
	}
	private void eliminarPagina(HttpServletRequest request, HttpServletResponse response){
		log.debug("Eliminando pagina");
		Json json=new Json();
		String msjRegreso="";
		json.add("success", false);
		json.add("index", 0);
		PaginaManager _paginaManager=new PaginaManager();
		try{
			int posicion_pagina=Integer.parseInt(request.getParameter("index"));
			int id_pagina=Integer.parseInt(request.getParameter("id_pagina"));
			HttpSession session=request.getSession(false);
			if(session!=null){
				Usuario u = (Usuario)session.getAttribute(USER_KEY);
				if(u!=null){
						String select=(String)request.getParameter("select");
						DocumentoManager _documentoManager=new DocumentoManager();
						Documento d=_documentoManager.selectDocumento(select);
						if(new UsuarioPermisos(u,d.getTituloAplicacion()).isEliminar()||"USR_GRALES".equals(d.getTituloAplicacion())){
							if(posicion_pagina>0&&posicion_pagina<=d.getNumero_paginas()){
							String ruta=d.getPaginasDocumento()[posicion_pagina-1].getAbsolutePath();
									if(ruta!=null&&ruta!=""){
										if(_paginaManager.eliminarPaginas(session, select, new Integer[]{id_pagina})) {
											json.add("success", true);
											msjRegreso="Pagina eliminada correctamente";
										}
										else
											msjRegreso="Error al eliminar pagina del documento.";
									}
									else
										msjRegreso="No se encuentra la ruta de la página";
							}
							else
								msjRegreso="Indice de página incorrecto.";
						}
						else
							msjRegreso="No cuentas con los permisos para realizar esta accion";
				}
			}
		}
		catch(Exception e){
			log.error("Error al eliminar pagina: "+e.getMessage());
			msjRegreso = e.getMessage();
		}
		json.add("message", msjRegreso);
		json.returnJson(response);
	}


	private void actualizaArbol(HttpSession session, String usuario_nombre,
			ITreeNode treeNode, Set<?> expandedNodes, Set<?> selectedNodes,
			boolean isMyDocs, Documento doc) {

		ArbolManager amd = new ArbolManager(doc.getTituloAplicacion(),
				doc.getIdGabinete());
		ITree tree = amd.generaExpediente(usuario_nombre);

		if (tree != null) {
			Iterator<?> i = expandedNodes.iterator();
			while (i.hasNext()) {
				ITreeNode tn = (ITreeNode) i.next();
				tree.expand(tn.getId());
			}

			i = selectedNodes.iterator();
			while (i.hasNext()) {
				ITreeNode tn = (ITreeNode) i.next();
				tree.select(tn.getId());
			}

			session.setAttribute((isMyDocs ? TREE_MDC_KEY : TREE_EXP_KEY), tree);
			session.setAttribute(TREE_KEY, tree);
		}
	}
	
	private void deleteAllPages(HttpServletRequest req,HttpServletResponse resp)
		throws IOException, ServletException {

		resp.setContentType("text/plain");

		HttpSession session = req.getSession(false);
		if (session == null) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"No se encontro sesion.");
			return;
		}

		Usuario u = (Usuario) session.getAttribute(USER_KEY);
		if (u == null) {
			throw new ServletException("Sin usuario");
		}

		ITree tree = (ITree) session.getAttribute(TREE_KEY);
		if (tree == null) {
			throw new ServletException("Sin arbol");
		}

		String selectId = req.getParameter("select");
		
		if (selectId == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Sin nodo seleccionado");
			return;
		}
		
		ITreeNode treeNode = tree.findNode(selectId);
		Documento doc = null;
		try {
			doc = (Documento) treeNode.getObject();
		} catch (NullPointerException e) {
			log.error(e, e);
			
			tree = null;
			tree = ActualizaArbolClass.ActualizaArbol(selectId, session);
			treeNode = tree.findNode(selectId);
			doc = (Documento) treeNode.getObject();
		}
		
		try {
			DocumentoManager dm = new DocumentoManager();
			dm.deleteAllPaginasDocumento(doc, u.getNombreUsuario());
		} catch (DocumentoManagerException e) {
			log.error(e,e);
		}

		boolean isMyDocs = "USR_GRALES".equals(doc.getTituloAplicacion());
		
		actualizaArbol(session, u.getNombreUsuario(), treeNode,
				tree.getExpandedNodes(), tree.getSelectedNodes(), isMyDocs, doc);
		
		PrintWriter out = resp.getWriter();
		out.println("{'msg':'Contenido del documento eliminmado exitosamente'}");
		out.flush();
		out.close();
	}

}
