package com.syc.tree;
import java.io.Serializable;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.jenkov.prizetags.tree.itf.IUnSelectListener;

public class UnselectListener implements Serializable, IUnSelectListener {

private static final Logger log = Logger.getLogger(UnselectListener.class);

	private static final long serialVersionUID = 6195497848931222571L;

	public void nodeUnselected(ITreeNode node, ITree tree) {
		//if ((node != null) && (tree != null)) {}
	}
}
