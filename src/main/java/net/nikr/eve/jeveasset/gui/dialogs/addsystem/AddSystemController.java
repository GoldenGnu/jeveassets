/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.data.model.Galaxy;
import net.nikr.eve.jeveasset.data.model.Region;
import net.nikr.eve.jeveasset.data.model.SolarSystem;
import net.nikr.eve.jeveasset.gui.shared.TreeSelectDialog;
import net.nikr.eve.jeveasset.i18n.DialoguesAddSystem;

public class AddSystemController {

	private Galaxy model;
	private TreeSelectDialog view;
	private int leafCount;

	public AddSystemController(final Program program) {
		model = new Galaxy(StaticData.get().getLocations(), StaticData.get().getJumps());
		leafCount = 0;
		view = new TreeSelectDialog(program, DialoguesAddSystem.get().addSystem());
		view.getDialog().setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		view.getLeafFilterLabel().setText(DialoguesAddSystem.get().syetemFilter());
		view.getFilterInfoLabel().setText(DialoguesAddSystem.get().filterStatus());
		view.getFilterInfoResultLabel().setText(DialoguesAddSystem.get().defaultFilterResult());
		view.getSelectedLeafLabel().setText(DialoguesAddSystem.get().selectedSystem());
		view.getSelectedLeafValueLabel().setText(DialoguesAddSystem.get().defaultSelectedSystem());
		view.getAddButton().setText(DialoguesAddSystem.get().add());
		view.getAddButton().setEnabled(false);
		view.getCancelButton().setText(DialoguesAddSystem.get().cancel());
		view.getTree().setModel(new DefaultTreeModel(buildTree("")));
		view.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		registerListeners();
		view.setVisible(true);
	}

	private void registerListeners() {
		view.getDialog().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(final WindowEvent e) {
				view = null;
				model = null;
			}
		});

		view.getCancelButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				view.getDialog().dispose();
			}

		});

		view.getLeafFilterTextField().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent e) {
				JLabel label = view.getFilterInfoResultLabel();
				JTextField tf = (JTextField) e.getSource();
				setFilterText(tf.getText());
				int systemCount = leafCount;
				label.setText(generateFilterResultString(systemCount));
			}
		});

		view.getTree().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(final TreeSelectionEvent e) {
				JTree tree = view.getTree();
				JButton button = view.getAddButton();
				JLabel label = view.getSelectedLeafValueLabel();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (node == null || !node.isLeaf()) {
					label.setText(DialoguesAddSystem.get().defaultSelectedSystem());
					button.setEnabled(false);
				} else {
					SolarSystem system = (SolarSystem) node.getUserObject();
					label.setText(DialoguesAddSystem.get().treeLabel(system.getName()));
					button.setEnabled(true);
				}
			}
		});
	}

	private void setFilterText(final String text) {
		JTree tree = view.getTree();
		tree.setModel(new DefaultTreeModel(buildTree(text)));
		if (leafCount <= 30) {
			for (int i = 0; i < tree.getRowCount(); i++) {
				tree.expandRow(i);
			}
		}
	}

	private DefaultMutableTreeNode buildTree(final String filter) {

		leafCount = 0;
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(model);
		for (Region region : model.getRegions()) {
			if (!region.isWSpace()) {
				DefaultMutableTreeNode regionNode = new DefaultMutableTreeNode(region);
				for (SolarSystem system : region.getSolarSystems()) {
					if (system.toString().toLowerCase().contains((filter.toLowerCase().subSequence(0, filter.length())))) {
						DefaultMutableTreeNode systemNode = new DefaultMutableTreeNode(system);
						regionNode.add(systemNode);
						leafCount++;
					}
				}
				if (regionNode.getChildCount() != 0) {
					root.add(regionNode);
				}
			}
		}
		return root;
	}

	private String generateFilterResultString(final int resultCount) {
		JTextField tf = view.getLeafFilterTextField();
		if ("".equals(tf.getText())) {
			return DialoguesAddSystem.get().defaultFilterResult();
		} else {
			return DialoguesAddSystem.get().filterResult(resultCount);
		}
	}
}
