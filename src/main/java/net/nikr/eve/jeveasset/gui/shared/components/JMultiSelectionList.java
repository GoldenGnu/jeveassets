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

package net.nikr.eve.jeveasset.gui.shared.components;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


public class JMultiSelectionList extends JList {

	private List<Integer> selectedList;
	private ListenerClass listener = new ListenerClass();

	public JMultiSelectionList() {
		this(new DefaultListModel());
	}

	public JMultiSelectionList(final List<?> listData) {
		this (
			new AbstractListModel() {
				@Override
				public int getSize() { return listData.size(); }
				@Override
				public Object getElementAt(final int i) { return listData.get(i); }
			}
		);
	}

	public JMultiSelectionList(final ListModel model) {
		super(model);
		selectedList = new ArrayList<Integer>();

		this.addMouseListener(listener);
		this.addKeyListener(listener);
		this.addMouseMotionListener(listener);
		this.setDragEnabled(false);

		this.setSelectionModel(new DefaultListSelectionModel());
	}

	@Override
	public void clearSelection() {
 		super.clearSelection();
 		selectedList.clear();
 	}
	@Override
	public void setSelectedIndex(final int index) {
		super.setSelectedIndex(index);
		selectedList.clear();
		selectedList.add((Integer) index);
	}

	@Override
	public void setSelectedIndices(final int[] indices) {
		super.setSelectedIndices(indices);
		selectedList.clear();
		for (int i = 0; i < indices.length; i++) {
			selectedList.add((Integer) indices[i]);
		}
	}

	@Override
	public void setSelectedValue(final Object anObject, final boolean shouldScrool) {
		super.setSelectedValue(anObject, shouldScrool);
		selectedList.clear();
		selectedList.add((Integer) getSelectedIndex());
	}

	@Override
	public void addSelectionInterval(final int anchor, final int lead) {
		super.addSelectionInterval(anchor, lead);
		int start;
		int end;
		if (anchor < lead) {
			start = anchor;
			end = lead;
		} else {
			start = lead;
			end = anchor;
		}
		for (int i = start; i <= end; i++) {
			if (!selectedList.contains((Integer) i)) {
				selectedList.add((Integer) i);
			}
		}
	}

	@Override
	public void removeSelectionInterval(final int index0, final int index1) {
		super.removeSelectionInterval(index0, index1);
		int start;
		int end;
		if (index0 < index1) {
			start = index0;
			end = index1;
		} else {
			start = index1;
			end = index0;
		}
		for (int i = start; i <= end; i++) {
			if (!selectedList.contains((Integer) i)) {
				selectedList.add((Integer) i);
			}
		}
	}

	@Override
	public void setModel(final ListModel model) {
		super.setModel(model);
		model.addListDataListener(listener);
	}

	private class ListenerClass implements MouseListener, KeyListener, ListDataListener, MouseMotionListener {
		//MouseListener
		@Override
		public void mouseClicked(final MouseEvent e) { }

		@Override
		public void mouseReleased(final MouseEvent e) { }

		@Override
		public void mouseEntered(final MouseEvent e) { }

		@Override
		public void mouseExited(final MouseEvent e) { }

		@Override
		public void mousePressed(final MouseEvent e) {
			int index = locationToIndex(e.getPoint());
			if (e.getButton() == MouseEvent.BUTTON1) {
				toggleSelectedIndex(index);
				e.consume();
			}
		}

		//KeyListener
		@Override
		public void keyTyped(final KeyEvent e) { }

		@Override
		public void keyReleased(final KeyEvent e) { }

		@Override
		public void keyPressed(final KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_A && e.getModifiers() == KeyEvent.CTRL_MASK) {
				toggleSelectAll();
			}
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				setAnchor(getAnchorSelectionIndex() - 1);
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				setAnchor(getAnchorSelectionIndex() + 1);
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
				int index = getAnchorSelectionIndex();
				toggleSelectedIndex(index);
			}
			e.consume();
		}
		//MouseMotionListener
		@Override
		public void mouseMoved(final MouseEvent e) { }

		@Override
		public void mouseDragged(final MouseEvent e) {
			updateSelections();
		}

		//ListDataListener
		@Override
		public void contentsChanged(final ListDataEvent e) { }

		@Override
		public void intervalAdded(final ListDataEvent e) {
			int index0 = e.getIndex0();
			int index1 = e.getIndex1();
			if (index0 == index1) {
				updateList(index0, 1);
			}
		}

		@Override
		public void intervalRemoved(final ListDataEvent e) {
			int index0 = e.getIndex0();
			int index1 = e.getIndex1();
			if (index0 == index1) {
				selectedList.remove((Integer) index0);
				updateList(index0, -1);
			} else {
				selectedList.clear();
			}
			ensureIndexIsVisible(index1);
		}
	}
	//Public Methods
	public void addSelection(final int index, final boolean bSelected) {
		Integer indexObj = Integer.valueOf(index);

		//is this selected? if so remove it.
		if (selectedList.contains(indexObj) && !bSelected) {
			selectedList.remove(indexObj);
		}
		if (!selectedList.contains(indexObj) && bSelected) {
			selectedList.add(indexObj);
		}

		//set selected indices
		updateSelections();
		setAnchor(index);
	}
	//Private Methods
	private void updateList(final int index, final int fix) {
		List<Integer> fixedIndices = new ArrayList<Integer>(selectedList.size());
		for (int i = 0; i < selectedList.size(); i++) {
			int item = selectedList.get(i).intValue();
			if (item >= index) {
				item = item + fix;
				fixedIndices.add(item);
			} else {
				fixedIndices.add(item);
			}
		}
		selectedList = fixedIndices;
	}
	private void setAnchor(final int nAnchor) {
		ListSelectionModel sm = this.getSelectionModel();
		ListModel lm = this.getModel();
		if (nAnchor >= 0 && nAnchor < lm.getSize()) {
			if (this.isSelectedIndex(nAnchor)) {
				sm.removeSelectionInterval(nAnchor, nAnchor);
				sm.addSelectionInterval(nAnchor, nAnchor);
			} else {
				sm.addSelectionInterval(nAnchor, nAnchor);
				sm.removeSelectionInterval(nAnchor, nAnchor);
			}
			ensureIndexIsVisible(nAnchor);
		}
	}
	private void toggleSelectedIndex(final int index) {
		if (!isEnabled()) {
			return;
		} //Ingnore update when disabled
		Integer indexObj = Integer.valueOf(index);

		//is this selected? if so remove it.
		if (selectedList.contains(indexObj)) {
			selectedList.remove(indexObj);
		} else { //otherwise add it to our list
			selectedList.add(indexObj);
		}

		//set selected indices
		updateSelections();
		setAnchor(index);
	}
	private void updateSelections() {
		//copy to an int array
		int[] arr = new int[selectedList.size()];
		for (int i = 0; i < arr.length; i++) {
			int item = selectedList.get(i).intValue();
			arr[i] = item;
		}
		//set selected indices
		setSelectedIndices(arr);
	}
	private void toggleSelectAll() {
		if (!isEnabled()) {
			return;
		} //Ingnore update when disabled
		ListModel lm = this.getModel();
		int size = selectedList.size();
		selectedList.clear();
		if (size != lm.getSize()) {
			for (Integer i = 0; i < lm.getSize(); i++) {
				selectedList.add(i);
			}
		}
		updateSelections();
		setAnchor(0);
	}
	public void selectAll() {
		ListModel lm = this.getModel();
		selectedList.clear();
		for (Integer i = 0; i < lm.getSize(); i++) {
			selectedList.add(i);
		}
		updateSelections();
		setAnchor(0);
	}
}
