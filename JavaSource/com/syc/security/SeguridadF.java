package com.syc.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.syc.utils.*;
import com.syc.user.*;

import org.apache.log4j.Logger;

public class SeguridadF implements Filter {
	private FilterConfig filterConfig;
	private static final Logger log = Logger.getLogger(SeguridadF.class);

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) {
		String logged = null;
		HttpSession session = null;
		Object sAtrib = null;

		try {
			session = ((HttpServletRequest) request).getSession(false);			
			Usuario u = null;
			String urlL = ((HttpServletRequest)request).getRequestURI();
			if(session!=null){
				if(session.getAttribute(ParametersInterface.USER_KEY) != null){
					if((u = (Usuario) session.getAttribute(ParametersInterface.USER_KEY)) != null){
						log.trace("El usuario esta loggueado en Fortimax");
					}
				}
				else if(urlL.toLowerCase().indexOf("entregadocumento")!=-1){
					log.trace("El usuario entro a ver un documento compartido");
				}
				else if(urlL.toLowerCase().indexOf("visualizador")!=-1||urlL.toLowerCase().indexOf("visualizadordeimagentab")!=-1
						||urlL.toLowerCase().indexOf("listadeimagenes")!=-1||urlL.toLowerCase().indexOf("visordeimagenes")!=-1){
					log.trace("El usuario entro a ver un documento compartido: Visualizador");
				}
				
				else {
					String url = ((HttpServletRequest) request).getRequestURI();
					log.trace("No se ha iniciado session " + url);
					if (!"login.jsp".equalsIgnoreCase(url.substring(url
							.lastIndexOf("/") + 1))) {
						try {
							request.getRequestDispatcher("index.jsp").forward(
									request, response);
						} catch (Exception ex) {
							log.error(ex, ex);

							request.getRequestDispatcher(url).forward(request,
									response);
						}
					}
				}
			}
			else if(urlL.toLowerCase().indexOf("entregadocumento")!=-1){
				log.trace("El usuario entro a ver un documento compartido");
			}
			else if(urlL.toLowerCase().indexOf("visualizador")!=-1||urlL.toLowerCase().indexOf("visualizadordeimagentab")!=-1
					||urlL.toLowerCase().indexOf("listadeimagenes")!=-1||urlL.toLowerCase().indexOf("visordeimagenes")!=-1){
				log.trace("El usuario entro a ver un documento compartido: Visualizador");
			}
			else {
				String url = ((HttpServletRequest) request).getRequestURI();
				log.trace("No se ha iniciado session " + url);
				if (!"login.jsp".equalsIgnoreCase(url.substring(url
						.lastIndexOf("/") + 1))) {
					try {
						request.getRequestDispatcher("index.jsp").forward(
								request, response);
					} catch (Exception ex) {
						log.error(ex, ex);

						request.getRequestDispatcher(url).forward(request,
								response);
					}
				}
			}

			chain.doFilter(request, response);

		} catch (IOException io) {
			log.error(io, io);

			
		} catch (ServletException se) {
			log.error(se, se);


		} catch (Exception e) {
			log.error(e, e);


		}

	}

	public FilterConfig getFilterConfig() {
		return this.filterConfig;
	}

	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

}
