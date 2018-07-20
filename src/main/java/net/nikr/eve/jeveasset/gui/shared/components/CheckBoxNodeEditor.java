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

import ca.odell.glazedlists.TreeList;
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
		return getCheckBoxNode(tree.getSelectionPath());
	}

	private CheckBoxNode getCheckBoxNode(TreePath path) {
		Object value = path.getLastPathComponent();
		Object userObject = null;
		if (value != null && value instanceof DefaultMutableTreeNode) {
			userObject = ((DefaultMutableTreeNode) value).getUserObject();
		}
		if (value != null && value instanceof TreeList.Node) {
			userObject = ((TreeList.Node) value).getElement();
		}
		if (userObject != null && userObject instanceof CheckBoxNode) {
			return (CheckBoxNode) userObject;
		}
		return null;
	}

	@Override
	public Object getCellEditorValue() {
		return getCheckBoxNode();
	}

	@Override
	public boolean isCellEditable(EventObject event) {
		if (event instanceof MouseEvent) {
			MouseEvent mouseEvent = (MouseEvent) event;
			TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			return getCheckBoxNode(path) != null;
		}
		return false;
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
