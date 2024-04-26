package com.syc.tree;
import java.io.Serializable;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.IExpandListener;
import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;

public class ExpandListener implements Serializable, IExpandListener {

private static final Logger log = Logger.getLogger(ExpandListener.class);

	private static final long serialVersionUID = -7029014800170708523L;

	public void nodeExpanded(ITreeNode node, ITree tree) {
		if ((node != null) && (tree != null)) {
			tree.select(node.getId());
		}
	}
}
