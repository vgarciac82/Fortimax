package com.syc.tree;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;

import com.jenkov.prizetags.tree.impl.TreeNode;
import com.jenkov.prizetags.tree.impl.TreeTableMapping;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.fortimax.hibernate.HibernateManager;

public class TreeReader {

	private static Logger log = Logger.getLogger(TreeReader.class);
	private TreeTableMapping mapping = null;
	private ITreeNodeProcessor processor = null;

	public TreeReader() {
	}

	public TreeReader(TreeTableMapping mapping) {
		this.mapping = mapping;
	}

	/**
	 * Crea una nueva instancia de TreeReader
	 * 
	 * @param mapping
	 *            Mapa que contiene los elementos a leer
	 * @param processor
	 *            Implementacion del procesador de nodos
	 */
	
	public TreeReader(TreeTableMapping mapping, ITreeNodeProcessor processor) {
		this.mapping = mapping;
		this.processor = processor;
	}

	/**
	 * Lee y puebla un arbol ejecutando la consulta almacenada en sql. Procesa
	 * los elementos segun sean paginas o carpetas.
	 * 
	 * @param sql
	 *            Consulta a la base de datos
	 * @return Noda raiz del arbol poblado.
	 */
	@SuppressWarnings("unchecked")
	public ITreeNode readTree(String sql) {
		HibernateManager hm = new HibernateManager();
		List<Object[]> list = new ArrayList<Object[]>();
		try {		
			SQLQuery sqlQuery = hm.createSQLQuery(sql);
			list = sqlQuery.list();
		} catch (Exception e) {	
			log.error(e,e);
		} finally {
			hm.close();
		}
		log.debug("Se obtienen "+list.size()+" registros del Arbol");
		return readTree(list);
	}

	public ITreeNode readTree(List<Object[]> list) {

		ITreeNode root = null;
		Map<String, ITreeNode> nodes = new LinkedHashMap<String, ITreeNode>();
		Map<String, String> parentIds = new LinkedHashMap<String, String>();
		String lastId = "";

		try {
			for (Object[] rs : list) {
				ITreeNode treeNode = null;
				
				if (!lastId.equals(rs[1])) {
					treeNode = readTreeNode(rs);
					if (processor != null) {
						processor.process(rs, treeNode);
					}
				} 

				if (treeNode != null) {
					lastId = treeNode.getId();
					nodes.put(treeNode.getId(), treeNode);
				}
				if (mapping.getParentIdColumn() != null) {
					parentIds.put(treeNode.getId(), (String)rs[0]);
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}

		Iterator<String> nodeIds = nodes.keySet().iterator();
		while (nodeIds.hasNext()) {
			String nodeId = (String) nodeIds.next();
			ITreeNode node = (ITreeNode) nodes.get(nodeId);
			String parentId = (String) parentIds.get(node.getId());
			ITreeNode parent = (ITreeNode) nodes.get(parentId);
			if (parent != null && parent != node) {
				parent.addChild(node);
			} else {
				root = node;
			}
		}

		if (root != null) {
			while (root.getParent() != null) {
				root = root.getParent();
			}
		}

		nodes.clear();
		parentIds.clear();

		return root;
	}

	private ITreeNode readTreeNode(Object[] rs) {
		ITreeNode node = new TreeNode();

		if (mapping.getIdColumn() != null)
			node.setId(""+rs[1]);

		if (mapping.getNameColumn() != null)
			node.setName(""+rs[2]);

		if (mapping.getTypeColumn() != null)
			node.setType(""+rs[3]);

		if (mapping.getToolTipColumn() != null)
			node.setToolTip(""+rs[13]); //totchildcolumn

		return node;
	}
}
