/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.dialogs.addsystem;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import net.nikr.eve.jeveasset.data.model.Galaxy;
import net.nikr.eve.jeveasset.data.model.Region;
import net.nikr.eve.jeveasset.data.model.SolarSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemSelectTreePanel extends JPanel {

	public static final String TREE_CHANGE_PROPERTY_NAME = "SolarSystem";

	private static final int EXPAND_ALL_NODES_COUNT = 30;
	private static final Logger LOG = LoggerFactory.getLogger(AddSystemDialog.class);

	private JTree tree;
	private SolarSystem selectedSystem;
	private Galaxy galaxyModel;
	private int leafCount;

	SystemSelectTreePanel(Galaxy galaxy) {

		selectedSystem = null;
		galaxyModel = galaxy;
		tree = new JTree(buildTree(galaxyModel, ""));
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new SelectionListener());
		setLayout(new BorderLayout());
		add((new JScrollPane(tree)));
		leafCount = 0;
	}

	public SolarSystem selectedSolarSystem() {
		return selectedSystem;
	}

	public boolean isSolarSystemSelected() {
		return selectedSystem != null;
	}

	public void setFilterText(String text) {
		tree.setModel(new DefaultTreeModel(buildTree(galaxyModel, text)));
		if (getSystemCount() <= EXPAND_ALL_NODES_COUNT) {
			for (int i = 0 ; i < tree.getRowCount() ; i++) {
				tree.expandRow(i);
			}
		}
	}

	public int getSystemCount() {
		return leafCount;
	}

	private DefaultMutableTreeNode buildTree(Galaxy galaxy, String filter) {

		leafCount = 0;
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(galaxy);
		for (Region region : galaxy.getRegions()) {
			if (!region.isWSpace()) {
				DefaultMutableTreeNode regionNode = new DefaultMutableTreeNode(region);
				for (SolarSystem system : region.getSolarSystems()) {
					if (system.toString().toLowerCase().contains((filter.toLowerCase().subSequence(0, filter.length())))) {
						DefaultMutableTreeNode systemNode = new DefaultMutableTreeNode(system);
						regionNode.add(systemNode);
						leafCount++;
					}
				}
				if (regionNode.getChildCount() != 0)
					root.add(regionNode);
			}
		}
		return root;
	}

	private void expandAll(boolean expand) {

	}

	class SelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			SolarSystem oldSystem = selectedSystem;
			if (node == null || !node.isLeaf()) {
				selectedSystem = null;
			} else {
				selectedSystem = (SolarSystem) node.getUserObject();
			}
		firePropertyChange(TREE_CHANGE_PROPERTY_NAME, oldSystem, selectedSystem);
		}
	}
}
