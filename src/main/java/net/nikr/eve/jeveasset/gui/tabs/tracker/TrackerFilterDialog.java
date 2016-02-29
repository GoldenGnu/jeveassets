/*
 * Copyright 2009-2015 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package net.nikr.eve.jeveasset.gui.tabs.tracker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.CheckBoxNode;
import net.nikr.eve.jeveasset.gui.shared.components.CheckBoxNodeEditor;
import net.nikr.eve.jeveasset.gui.shared.components.CheckBoxNodeRenderer;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
import net.nikr.eve.jeveasset.i18n.TabsTracker;


public class TrackerFilterDialog extends JDialogCentered {

	private final JTree jTree;
	private final DefaultMutableTreeNode rootNode;
	private final DefaultTreeModel treeModel;

	private boolean save = false;
	
	public TrackerFilterDialog(Program program) {
		super(program, TabsTracker.get().filterTitle(), Images.TOOL_TRACKER.getImage());

		rootNode = new DefaultMutableTreeNode(DialoguesSettings.get().root());
		treeModel = new DefaultTreeModel(rootNode);

		jTree = new JTree(treeModel);
		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		jTree.putClientProperty("JTree.lineStyle", "None");
		jTree.setExpandsSelectedPaths(true);
		jTree.setRootVisible(false);
		jTree.setShowsRootHandles(true);
		jTree.setVisibleRowCount(0);
		jTree.setCellRenderer(new CheckBoxNodeRenderer());
		jTree.setCellEditor(new CheckBoxNodeEditor(jTree));
		jTree.setEditable(true);

		JButton jOK = new JButton(TabsTracker.get().ok());
		jOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save = true;
				setVisible(false);
			}
		});
		JButton jCancel = new JButton(TabsTracker.get().cancel());
		jCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		

		JScrollPane jTreeScroll = new JScrollPane(jTree);
		jTreeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jTreeScroll, 0, GroupLayout.PREFERRED_SIZE, 600)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jTreeScroll, 0, GroupLayout.PREFERRED_SIZE, 500)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	private DefaultMutableTreeNode add(final CheckBoxNode checkBoxNode, final DefaultMutableTreeNode parentNode) {
		jTree.setVisibleRowCount(jTree.getVisibleRowCount() + 1);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(checkBoxNode);
		if (parentNode == null) {
			treeModel.insertNodeInto(node, rootNode, rootNode.getChildCount());
		} else {
			treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
		}
		return node;
	}

	private void expandAll(final TreePath parent, final boolean expand) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(path, expand);
			}
		}
		if (expand) {
			jTree.expandPath(parent);
		} else {
			jTree.collapsePath(parent);
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jTree;
	}

	@Override
	protected JButton getDefaultButton() {
		return null;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() { }
	
	public boolean show(Map<String, CheckBoxNode> nodes) {
		save = false;
		rootNode.removeAllChildren();
		treeModel.reload();
		jTree.setVisibleRowCount(0);

		Map<String, CheckBoxNode> cloneList = cloneList(nodes);

		Map<String, DefaultMutableTreeNode> cache = new HashMap<String, DefaultMutableTreeNode>();
		for (CheckBoxNode node : cloneList.values()) {
			addTree(cache, node);
		}
		expandAll(new TreePath((rootNode)), true);

		super.setVisible(true);

		if (save) {
			nodes.clear();
			nodes.putAll(cloneList);
		}
		return save;
	}

	public Map<String, CheckBoxNode> cloneList(Map<String, CheckBoxNode> nodes) {
		Map<String, CheckBoxNode> clonesCache = new HashMap<String, CheckBoxNode>();
		Map<String, CheckBoxNode> clonedNodes = new TreeMap<String, CheckBoxNode>();
		for (Map.Entry<String, CheckBoxNode> entry : nodes.entrySet()) {
			clonedNodes.put(entry.getKey(), cloneTree(clonesCache, entry.getValue()));
		}
		return clonedNodes;
	}

	public DefaultMutableTreeNode addTree(Map<String, DefaultMutableTreeNode> cache, CheckBoxNode node) {
		//Add parents if any...
		CheckBoxNode parent = node.getParent();
		DefaultMutableTreeNode parentNode = null;
		if (parent != null) {
			parentNode = addTree(cache, parent);
		}
		//Add this node, if not already added
		DefaultMutableTreeNode treeNode = cache.get(node.getNodeId());
		if (treeNode == null) {
			treeNode = add(node, parentNode);
			cache.put(node.getNodeId(), treeNode);
		}
		return treeNode;
	}

	public CheckBoxNode cloneTree(Map<String, CheckBoxNode> clonesCache, CheckBoxNode oldNode) {
		CheckBoxNode oldParent = oldNode.getParent();
		CheckBoxNode cloneParent = null;
		if (oldParent != null) {
			cloneParent = cloneTree(clonesCache, oldParent);
		}
		CheckBoxNode cloneNode = clonesCache.get(oldNode.getNodeId());
		if (cloneNode == null) {
			cloneNode = new CheckBoxNode(cloneParent, oldNode);
			clonesCache.put(cloneNode.getNodeId(), cloneNode);
		}
		return cloneNode;
	}
}
