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

import org.apache.log4j.Logger;

public class Seguridad implements Filter {
	private FilterConfig filterConfig;
	private static final Logger log = Logger.getLogger(Seguridad.class);

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) {
		String logged = null;
		HttpSession session = null;
		Object sAtrib = null;

		try {
			// System.out.print ("Ejecutando filtro de seguridad ... ");
			// System.out.println ("Filtering the Request ...");

			session = ((HttpServletRequest) request).getSession(false);
			
			if (session != null && session.getAttribute("logged") != null)
				sAtrib = session.getAttribute("logged");

			if (sAtrib != null)
				logged = (String) session.getAttribute("logged");

			if ("true".equalsIgnoreCase(logged)) {
				log.trace("El usuario esta loggueado");
			} else {

				String url = ((HttpServletRequest) request).getRequestURI();
				log.trace("No se ha iniciado session " + url);
				//System.out.println(url.substring(url.lastIndexOf("/")));

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
