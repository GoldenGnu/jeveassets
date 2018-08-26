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

import java.util.ArrayList;
import java.util.List;

public class CheckBoxNode implements Comparable<CheckBoxNode>{

	private final String nodeId;
	private final String nodeName;
	private final String match;
	private final List<CheckBoxNode> children = new ArrayList<>();
	private CheckBoxNode parent;
	private int childrenSelected = 0;
	private int childrenShown = 0;
	private boolean selected;
	private boolean shown;

	public CheckBoxNode(CheckBoxNode parent, CheckBoxNode clone) {
		this(parent, clone.nodeId, clone.nodeName, clone.selected);
	}

	public CheckBoxNode(CheckBoxNode parent, String nodeId, String nodeName, boolean selected) {
		this.nodeId = nodeId;
		this.nodeName = nodeName;
		this.match = nodeName.toLowerCase();
		this.selected = selected;
		this.parent = parent;
		this.shown = true;
		if (parent != null) {
			parent.addChild(this);
		}
	}

	public boolean isShown() {
		return shown;
	}

	public void hide() {
		setShown(false);
	}

	public void show() {
		updateShown(true);
	}

	public boolean matches(String text) {
		if (match.contains(text)) { //Matches Self (include everything)
			shownParents();
			updateShown(true);
			shownChildren();
			return true;
		}
		if (matchesParent(text)) { //Matches Parent (include everything)
			shownParents();
			updateShown(true);
			shownChildren();
			return true;
		}
		if (matchesChildren(text)) {  //Matches Child (include self and parent)
			shownParents();
			updateShown(true);
			return true;
		}
		return false;
	}

	public CheckBoxNode getParent() {
		return parent;
	}

	public boolean isParent() {
		return !children.isEmpty();
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean setSelected(boolean newValue) {
		boolean update = selected != newValue;
		if (!update) { //Nothing changed -> do nothing
			return false;
		}
		selected = newValue;
		if (isParent()) {  //If have children -> Update Tree
			update = selectionToChildren(newValue) || update;
		}
		if (selected != newValue) {
			throw new RuntimeException();
		}
		if (parent != null && isShown()) {
			if (selected) {
				parent.childSelected();
			} else {
				parent.childDeselected();
			}
			parent.updateSelection();
		}
		return update;
	}

	public String getNodeId() {
		return nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	private void addChild(CheckBoxNode child) {
		children.add(child);
		if (child.isSelected()) {
			childSelected();
		}
		if (child.isShown()) {
			childShown();
		}
		updateSelection();
	}

	private void childShown() {
		childrenShown++;
	}

	private void childHidden() {
		childrenShown--;
	}

	private void childSelected() {
		childrenSelected++;
	}

	private void childDeselected() {
		childrenSelected--;
	}

	private boolean setShown(boolean newValue) {
		boolean updated = this.shown != newValue;
		this.shown = newValue;
		if (parent != null && updated) {
			if (shown) { //If shown: add shown and update
				parent.childShown();
				if (selected) { //If selected -> add selection
					parent.childSelected();
				}
			} else { //Remove
				parent.childHidden();
				if (selected) { //If selected -> remove selection
					parent.childDeselected();
				}
			}
		}
		return updated;
	}

	private void updateShown(boolean newValue) {
		if (setShown(newValue) && parent != null) {
			parent.updateSelection();
		}
	}

	private boolean matchesParent(String text) {
		if (parent != null) {
			if (parent.match.contains(text)) {
				return true;
			}
			if (parent.matchesParent(text)) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesChildren(String text) {
		for (CheckBoxNode node : children) {
			if (node.match.contains(text)) {
				return true;
			}
			if (node.matchesChildren(text)) {
				return true;
			}
		}
		return false;
	}

	private void shownParents() {
		if (parent != null) {
			parent.shownParents();
			parent.updateShown(true);
		}
	}

	private void shownChildren() {
		for (CheckBoxNode child : children) {
			child.updateShown(true);
			child.shownChildren();
		}
	}

	private boolean updateSelection() {
		if (!isParent()) {
			return false;
		}
		boolean oldValue = selected;
		selected = childrenSelected == childrenShown;
		selected = childrenSelected == childrenShown;
		boolean updated = oldValue != selected;
		if (updated && parent != null) { //Value changed -> Update Tree
			if (selected) {
				parent.childSelected();
			} else {
				parent.childDeselected();
			}
			updated = parent.updateSelection() || updated;
		}
		return updated;
	}

	private boolean selectionToChildren(boolean newValue) {
		boolean updated = false;
		for (CheckBoxNode node : children) {
			if (!node.isShown()) {
				continue;
			}
			if (node.isSelected() != newValue) {
				node.selected = newValue;
				if (newValue) {
					childSelected();
				} else {
					childDeselected();
				}
				updated = true;
			}
			node.selectionToChildren(newValue);
		}
		if (newValue) {
			if (childrenShown != childrenSelected) {
				throw new RuntimeException();
			}
		} else {
			if (childrenSelected != 0) {
				throw new RuntimeException();
			}
		}
		return updated;
	}

	@Override
	public String toString() {
		return nodeName;
	}

	@Override
	public int compareTo(CheckBoxNode o) {
		return nodeId.compareToIgnoreCase(o.nodeId);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 47 * hash + (this.nodeId != null ? this.nodeId.hashCode() : 0);
		hash = 47 * hash + (this.nodeName != null ? this.nodeName.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CheckBoxNode other = (CheckBoxNode) obj;
		if ((this.nodeId == null) ? (other.nodeId != null) : !this.nodeId.equals(other.nodeId)) {
			return false;
		}
		if ((this.nodeName == null) ? (other.nodeName != null) : !this.nodeName.equals(other.nodeName)) {
			return false;
		}
		return true;
	}
}
