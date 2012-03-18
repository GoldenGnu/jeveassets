/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.routing;

import javax.swing.JList;
import javax.swing.ListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveJList<T> extends JList {

	private final static Logger LOG = LoggerFactory.getLogger(MoveJList.class);

	private static final long serialVersionUID = 1l;

	public MoveJList() {
		setModel(new EditableListModel<T>());
	}

	@SuppressWarnings("unchecked") // dealing with the non-generics ListModel
	public MoveJList(ListModel dataModel) {
		EditableListModel<T> m = new EditableListModel<T>();
		for (int i = 0; i < m.getSize(); ++i) {
			m.add((T) m.getElementAt(i));
		}
		setModel(m);
	}

	@SuppressWarnings("unchecked") // dealing with the non-generics ListModel/JList
	public EditableListModel<T> getEditableModel() {
		return (EditableListModel<T>) getModel();
	}

	/**
	 *
	 * @param to
	 * @param limit
	 * @return true if all the items were added.
	 */
	@SuppressWarnings("unchecked") // dealing with the non-generics ListModel/JList
	public boolean move(MoveJList<T> to, int limit) {
		EditableListModel<T> fModel = getEditableModel();
		EditableListModel<T> tModel = to.getEditableModel();
		for (Object obj : getSelectedValues()) {
			T ss = (T) obj;
			if (fModel.contains(ss)) {
				if (to.getModel().getSize() < limit) {
					LOG.debug("Moving {}", ss);
					if (fModel.remove(ss)) {
						tModel.add(ss);
					}
				} else {
					setSelectedIndices(new int[]{});
					to.setSelectedIndices(new int[]{});
					return false;
				}
			}
		}
		setSelectedIndices(new int[]{});
		to.setSelectedIndices(new int[]{});
		return true;
	}
}
