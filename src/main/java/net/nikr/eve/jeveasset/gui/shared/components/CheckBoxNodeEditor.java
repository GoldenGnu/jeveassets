/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.components;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;


public class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

	CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();

	JTree tree;

	public CheckBoxNodeEditor(JTree tree) {
		this.tree = tree;
	}

	private CheckBoxNode getCheckBoxNode() {
		TreePath selectionPath = tree.getSelectionPath();
		Object object = selectionPath.getLastPathComponent();
		if (object instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
			Object userObject = node.getUserObject();
			if (userObject instanceof CheckBoxNode) {
				CheckBoxNode checkBoxNode = (CheckBoxNode) userObject;
				return checkBoxNode;
			}
		}
		return null;
	}

	@Override
	public Object getCellEditorValue() {
		
		return getCheckBoxNode();
	}

	@Override
	public boolean isCellEditable(EventObject event) {
		boolean returnValue = false;
		if (event instanceof MouseEvent) {
			MouseEvent mouseEvent = (MouseEvent) event;
			TreePath path = tree.getPathForLocation(mouseEvent.getX(),
					mouseEvent.getY());
			if (path != null) {
				Object node = path.getLastPathComponent();
				if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
					Object userObject = treeNode.getUserObject();
					returnValue = userObject instanceof CheckBoxNode;
				}
			}
		}
		return returnValue;
	}

	@Override
	public Component getTreeCellEditorComponent(final JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row) {

		Component editor = renderer.getTreeCellRendererComponent(tree, value,
				true, expanded, leaf, row, true);

		// editor always selected / focused
		if (editor instanceof JCheckBox) {
			final JCheckBox checkBox = (JCheckBox) editor;
			checkBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (stopCellEditing()) {
						fireEditingStopped();
					}
					CheckBoxNode checkBoxNode = getCheckBoxNode();
					if (checkBoxNode != null) {
						boolean updated = checkBoxNode.setSelected(checkBox.isSelected());
						if (updated) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									tree.repaint();
								}
							});
						}
					}
					checkBox.removeItemListener(this);
				}
			});
		}

		return editor;
	}
}
