package com.syc.tree;
import java.io.Serializable;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ICollapseListener;
import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;

public class CollapseListener implements Serializable, ICollapseListener {

private static final Logger log = Logger.getLogger(CollapseListener.class);

	private static final long serialVersionUID = 4151435309832033226L;

	public void nodeCollapsed(ITreeNode node, ITree tree) {
		//if ((node != null) && (tree != null)) {}
	}
}
