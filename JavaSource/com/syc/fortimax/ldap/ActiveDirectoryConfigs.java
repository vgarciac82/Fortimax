package com.syc.fortimax.ldap;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ActiveDirectoryConfigs {

	private static final Logger log = Logger
			.getLogger(ActiveDirectoryConfigs.class);

	private String dominio;
	private String groupsOU = "ou=Groups,o=forethought.com";
	private String host;
	private String passd;
	private int port = 389;
	private String user;
	private String usersOU = "ou=People,o=forethought.com";

	public ActiveDirectoryConfigs() {
		super();
	}

	public ActiveDirectoryConfigs(ActiveDirectoryConfigs conf) {
		super();
		//this.context = conf.context;
		this.dominio = conf.dominio;
		this.groupsOU = conf.groupsOU;
		this.host = conf.host;
		this.passd = conf.passd;
		this.port = conf.port;
		this.user = conf.user;
		this.usersOU = conf.usersOU;
	}

	public DirContext getContext() {
		DirContext context=null;
		try {
			context = LDAPManager.getInitialContext(host, port, user + "@"
					+ dominio, passd);
		} catch (NamingException e) {
			log.error(e, e);
		}
		
		return context;
	}

	public String getDominio() {
		return dominio;
	}

	public String getGroupsOU() {
		return groupsOU;
	}

	public String getHost() {
		return host;
	}

	public String getPassd() {
		return passd;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getUsersOU() {
		return usersOU;
	}

	public boolean isUsed() {
		boolean use = false;

		if (StringUtils.isNotBlank(this.dominio)
				&& StringUtils.isNotBlank(this.groupsOU)
				&& StringUtils.isNotBlank(this.host)
				&& StringUtils.isNotBlank(this.usersOU)) {
			use = true;
		}

		return use;
	}

	public void setDominio(String dominio) {
		this.dominio = dominio;
	}

	public void setGroupsOU(String groupsOU) {
		this.groupsOU = groupsOU;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPassd(String passd) {
		this.passd = passd;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setUsersOU(String usersOU) {
		this.usersOU = usersOU;
	}

}
