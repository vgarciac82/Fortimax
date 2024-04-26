package com.syc.tree;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.user.Usuario;
import com.syc.utils.ParametersInterface;


public class ActualizaArbolClass implements ParametersInterface{

private static final Logger log = Logger.getLogger(ActualizaArbolClass.class);
	
	
	public static ITree ActualizaArbol(String treeNodeId, HttpSession session){
			Usuario usuario = (Usuario)session.getAttribute(USER_KEY);
			ITree treeLcl = null;
			try{
				GetDatosNodo gdn = new GetDatosNodo(treeNodeId);
				gdn.separaDatosGabinete();
				
				ArbolManager am = new ArbolManager(gdn.getGaveta(),gdn.getGabinete());
				treeLcl = am.generaExpediente(usuario.getNombreUsuario());
				session.setAttribute(("USR_GRALES".equals(gdn.getGaveta()) ? ParametersInterface.TREE_MDC_KEY : ParametersInterface.TREE_EXP_KEY), treeLcl);
				
				if(session.getAttribute(TREE_KEY)==null){
					session.setAttribute(TREE_KEY, treeLcl);
				}

			}
			catch(Exception e){	log.error(e,e);

			}
			
			return treeLcl; 
	}
}
