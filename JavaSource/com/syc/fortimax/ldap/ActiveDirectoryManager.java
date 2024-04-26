package com.syc.fortimax.ldap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.syc.fortimax.config.Config;
import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.entities.imx_grupo;
import com.syc.fortimax.hibernate.entities.imx_grupo_privilegio;
import com.syc.fortimax.hibernate.entities.imx_grupo_privilegio_id;

public abstract class ActiveDirectoryManager {

	private static final Logger log = Logger
			.getLogger(ActiveDirectoryManager.class);
	
	public static String reconfigurePrivilegios(HashMap<String, HashMap<String, Integer>> groups){
		String msg = "Error, intente de nuevo";
		if (groups != null) {
			Session sess = HibernateUtils.getSession();
			if (sess != null) {
				Transaction trans = sess.beginTransaction();
				try {
					Iterator<Map.Entry<String, HashMap<String, Integer>>> it = groups
							.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, HashMap<String, Integer>> e = it
								.next();

						imx_grupo grup = (imx_grupo) sess.get(imx_grupo.class,
								e.getKey());
						if (grup == null) { // si no existe el grupo lo crea
							sess.save(new imx_grupo(e.getKey(), e.getKey()));
						}

						Iterator<Map.Entry<String, Integer>> it2 = e.getValue()
								.entrySet().iterator();
						while (it2.hasNext()) {
							Map.Entry<String, Integer> e2 = it2.next();
							imx_grupo_privilegio_id gpi = new imx_grupo_privilegio_id(
									e2.getKey(), e.getKey());
							imx_grupo_privilegio gpNew = new imx_grupo_privilegio(
									gpi, "PERSONALIZADO", e2.getValue());
							imx_grupo_privilegio gpOld = (imx_grupo_privilegio) sess
									.get(imx_grupo_privilegio.class, gpi);

							if (gpOld == null) { // Si no existe el id

								if (gpNew.getPrivilegio() >= 0) {
									// Y el privilegio existe
									sess.save(gpNew);
								}
							} else { // Si existe el id
								if (gpNew.getPrivilegio() < 0) {
									// Si el privilegio es negativo se borra
									sess.delete(gpOld);
								} else {
									// Si algo cambio se modifica
									if (gpNew.getNombreNivel().equals(
											gpOld.getNombreNivel()) == false) {
										gpOld.setNombreNivel(gpNew
												.getNombreNivel());
									}
									if (gpNew.getPrivilegio() != gpOld
											.getPrivilegio()) {
										gpOld.setPrivilegio(gpNew
												.getPrivilegio());
									}
								}
							}
						}
					}

					if (trans != null) {
						trans.commit();
					}
					msg = "Los grupos fueron actualizados Exitosamente!";
				} catch (HibernateException e) {
					log.error(e, e);
					if (trans != null) {
						trans.rollback();
					}
				} finally {
					if (sess.isOpen()) {
						sess.close();
					}
				}

			}
		}
		return msg;		
	}
	
	public static String reconfigurePrivilegios(String arreglo) {
		HashMap<String, HashMap<String, Integer>> groups = null;
		String msg = "Error, intente de nuevo";
		try {
			groups = toGroups(arreglo);
		} catch (Exception e) {
			msg = "Error al configurar los grupos\nRevise que no contengan algun caracter extra\u00F1o";
			log.error(msg);
			log.error(e,e);
		}
		return reconfigurePrivilegios(groups);
	}
	
	private static HashMap<String, HashMap<String, Integer>> toGroups(
			String arreglo) throws Exception {
		HashMap<String, HashMap<String, Integer>> g = new HashMap<String, HashMap<String, Integer>>();
		// Si te preguntas sobre el orden de los caracteres especiales revisa
		// GruposLdap.jsp la funcion validaEnvia

		String[] aux1 = StringUtils.split(arreglo, '?');
		for (int i = 0; i < aux1.length; i++) {
			String[] gruposldap = StringUtils.split(aux1[i], '|');
			if (gruposldap.length == 2) {
				HashMap<String, Integer> gavetas = new HashMap<String, Integer>();
				String[] aux2 = StringUtils.split(gruposldap[1], '^');
				for (int j = 0; j < aux2.length; j++) {
					String[] gavetapriv = StringUtils.split(aux2[j], '`');
					if (gavetapriv.length == 2
							&& StringUtils.isNumeric(gavetapriv[1])) {
						gavetas.put(gavetapriv[0],
								Integer.parseInt(gavetapriv[1]));
					} else if (gavetapriv.length == 2
							&& gavetapriv[1].equals("undefined")) {
						gavetas.put(gavetapriv[0], -1);
					}
				}
				g.put(gruposldap[0], gavetas);
			}
		}

		return g;
	}

	@SuppressWarnings("unchecked")
	public static List<String> getGroups(DirContext context, String user) {
		List<String> groups = new LinkedList<String>();

		try {
			SearchControls scTemp = new SearchControls();
			scTemp.setSearchScope(SearchControls.SUBTREE_SCOPE);
			scTemp.setReturningAttributes(new String[] { "memberOf" });
			for (NamingEnumeration<?> ne = context.search(
					Config.activeDirectoryConfigs.getGroupsOU(),
					"sAMAccountName" + "=" + user, scTemp); ne.hasMore();) {
				SearchResult srTemp = (SearchResult) ne.next();
				Attributes attrsTemp = srTemp.getAttributes();
				NamingEnumeration<String> a = (NamingEnumeration<String>) attrsTemp
						.get("memberOf").getAll();

				while (a.hasMore()) {

					String[] g = StringUtils.split(a.next(), ',');

					for (int i = 0; i < g.length; i++) {
						if (StringUtils.startsWithIgnoreCase(g[i], "CN=")) {
							String ng = StringUtils.removeStartIgnoreCase(g[i],
									"CN=");
							if (groups.contains(ng) == false) {
								groups.add(ng);
							}
						}
					}
				}
			}

		} catch (PartialResultException e) {
			// Ya no hay mas resultados (Se supone)
			if (log.isDebugEnabled()) {
				log.debug("Grupos LDAP " + ArrayUtils.toString(groups));
			}
		} catch (NamingException e) {
			log.error(e, e);
		}

		return groups;
	}

	public static List<String> getAllGroups() {
		List<String> groups = new LinkedList<String>();

		DirContext context = Config.activeDirectoryConfigs.getContext();
		if (context != null) {
			try {
				SearchControls scTemp = new SearchControls();
				scTemp.setSearchScope(SearchControls.SUBTREE_SCOPE);
				for (NamingEnumeration<?> ne = context.search("CN=Users,"
						+ Config.activeDirectoryConfigs.getUsersOU(),
						"(&(objectCategory=group)(objectClass=group))", scTemp); ne
						.hasMore();) {
					SearchResult srTemp = (SearchResult) ne.next();

					groups.add(srTemp.getName().replaceFirst("CN=", ""));

				}

			} catch (PartialResultException e) {
				// Ya no hay mas resultados (Se supone)
				if (log.isDebugEnabled()) {
					log.debug("Grupos LDAP " + ArrayUtils.toString(groups));
				}
			} catch (NamingException e) {
				log.error(e, e);
			} finally {
				try {
					context.close();
				} catch (NamingException e) {
					log.error(e, e);
				}
			}

		}

		Collections.sort(groups);

		return groups;
	}

}
