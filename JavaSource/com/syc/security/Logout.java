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

public class Logout implements Filter {
	private static final Logger log = Logger.getLogger(Logout.class);
	private FilterConfig filterConfig;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) {
		// String logged = null;
		HttpSession session = null;

		try {
			// System.out.print ("Ejecutando filtro de Logout ... ");
			// System.out.println ("Filtering the Request ...");

			session = ((HttpServletRequest) request).getSession(false);

			if (session != null && session.getAttribute("logged") != null)
				session.setAttribute("logged", "false");

			chain.doFilter(request, response);

		} catch (IOException io) {
			log.error(io, io);

			// System.out.println ("IOException en filtro Logout");
		} catch (ServletException se) {
			log.error(se, se);

			// System.out.println ("ServletException en filtro Logout");
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
