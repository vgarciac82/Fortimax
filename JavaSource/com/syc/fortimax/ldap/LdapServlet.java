package com.syc.fortimax.ldap;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LdapServlet extends HttpServlet {

	private static final long serialVersionUID = -3359577377120888565L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LdapServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String cadenota = request.getParameter("array");
		PrintWriter out = response.getWriter();
		out.println(ActiveDirectoryManager.reconfigurePrivilegios(cadenota));
		out.close();
	}

}
