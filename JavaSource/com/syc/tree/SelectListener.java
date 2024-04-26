package com.syc.tree;
import java.io.Serializable;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ISelectListener;
import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;

public class SelectListener implements Serializable, ISelectListener {

private static final Logger log = Logger.getLogger(SelectListener.class);

	private static final long serialVersionUID = 2188125462362311419L;

	public void nodeSelected(ITreeNode node, ITree tree) {
		if ((tree != null) && (node != null)) {
			if ("carpeta.hija".equals(node.getType())) {
				if (!tree.isExpanded(node.getId())) {
					/*if (tree.isExpanded(node.getId())) {
						tree.collapse(node.getId());
					} else {*/
					tree.expand(node.getId());

					ITreeNode root = node.getParent();
					while (root != null) {
						if (!tree.isExpanded(root.getId()))
							tree.expand(root.getId());
						root = root.getParent();
					}
				}
			}
		}
	}
}
