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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

public class EditableListModel<T> extends AbstractListModel {

	private static final long serialVersionUID = 1L;
	List<T> backed = new ArrayList<T>();
	Comparator<T> sortComparator = new Comparator<T>() {
		@Override
		public int compare(T o1, T o2) {
			return o2.hashCode()-o1.hashCode();
		}
	};

	public EditableListModel() {
	}

	public EditableListModel(List<T> initial) {
		backed.addAll(initial);
	}

	public EditableListModel(List<T> initial, Comparator<T> sortComparator) {
		backed.addAll(initial);
		this.sortComparator = sortComparator;
	}

	public void setSortComparator(Comparator<T> sortComparator) {
		this.sortComparator = sortComparator;
	}

	public Comparator<T> getSortComparator() {
		return sortComparator;
	}

	public List<? extends T> getAll() {
		return Collections.unmodifiableList(backed);
	}

	@Override
	public int getSize() {
		return backed.size();
	}

	@Override
	public Object getElementAt(int index) {
		return backed.get(index);
	}

	public T remove(int index) {
		T b = backed.remove(index);
		changed();
		return b;
	}

	public void clear() {
		backed.clear();
		changed();
	}

	public boolean remove(T o) {
		boolean b = backed.remove(o);
		changed();
		return b;
	}

	public boolean add(T e) {
		boolean b = backed.add(e);
		Collections.sort(backed, sortComparator);
		changed();
		return b;
	}

	public boolean addAll(Collection<? extends T> c) {
		boolean b = backed.addAll(c);
		changed();
		return b;
	}

	public boolean contains(T o) {
		return backed.contains(o);
	}

	void changed() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				fireContentsChanged(this, 0, backed.size() - 1);
			}
		});
	}
}
