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
	private CheckBoxNode parent;
	private final List<CheckBoxNode> children = new ArrayList<CheckBoxNode>();
	private boolean selected;

	public CheckBoxNode(CheckBoxNode parent, CheckBoxNode clone) {
		this(parent, clone.nodeId, clone.nodeName, clone.selected);
	}

	public CheckBoxNode(CheckBoxNode parent, String nodeId, String nodeName, boolean selected) {
		this.nodeId = nodeId;
		this.nodeName = nodeName;
		this.selected = selected;
		this.parent = parent;
		if (parent != null) {
			parent.addChild(this);
			parent.selectionFromChildren();
		}
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
		selected = newValue;
		boolean updated = selectionToChildren(newValue);
		if (parent != null) {
			updated = parent.selectionFromChildren() || updated;
		}
		return updated;
	}

	public String getNodeId() {
		return nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	@Override
	public String toString() {
		return nodeName;
	}

	private void addChild(CheckBoxNode child) {
		children.add(child);
	}

	private boolean selectionToChildren(boolean newValue) {
		boolean updated = false;
		for (CheckBoxNode node : children) {
			if (node.isSelected() != newValue) {
				node.selected = newValue;
				node.selectionToChildren(newValue);
				updated = true;
			}
		}
		return updated;
	}

	private boolean selectionFromChildren() {
		boolean isAllSelected = true;
		for (CheckBoxNode node : children) {
			if (!node.isSelected()) {
				isAllSelected = false;
				break;
			}
		}
		if (isSelected() == isAllSelected) {
			return false;
		} else {
			selected = isAllSelected;
			if (parent != null) {
				parent.selectionFromChildren();
			}
			return true;
		}
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
