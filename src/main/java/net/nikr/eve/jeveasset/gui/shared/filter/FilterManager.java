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

package net.nikr.eve.jeveasset.gui.shared.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.GuiShared;

public class FilterManager<E> extends JDialogCentered {

	public static final String ACTION_DONE = "ACTION_DONE";
	public static final String ACTION_LOAD_FILTER = "ACTION_LOAD_FILTER";
	public static final String ACTION_RENAME_FILTER = "ACTION_RENAME_FILTER";
	public static final String ACTION_DELETE_FILTER = "ACTION_DELETE_FILTER";

	//GUI
	private DefaultListModel listModel;
	private JList jFilters;
	private JButton jDelete;
	private JButton jLoad;
	private JButton jRename;
	private JButton jDone;

	private final Map<String, List<Filter>> filters;
	private final Map<String, List<Filter>> defaultFilters;
	private final FilterGui<E> gui;

	private ListenerClass listener = new ListenerClass();

	FilterManager(final JFrame jFrame, final FilterGui<E> gui, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
		super(null, GuiShared.get().filterManager(), jFrame);
		this.gui = gui;
		this.filters = filters;
		this.defaultFilters = defaultFilters;

		//Load
		jLoad = new JButton(GuiShared.get().managerLoad());
		jLoad.setActionCommand(ACTION_LOAD_FILTER);
		jLoad.addActionListener(listener);
		jPanel.add(jLoad);

		//Rename
		jRename = new JButton(GuiShared.get().managerRename());
		jRename.setActionCommand(ACTION_RENAME_FILTER);
		jRename.addActionListener(listener);
		jPanel.add(jRename);

		//Delete
		jDelete = new JButton(GuiShared.get().managerDelete());
		jDelete.setActionCommand(ACTION_DELETE_FILTER);
		jDelete.addActionListener(listener);
		jPanel.add(jDelete);

		//List
		listModel = new DefaultListModel();
		jFilters = new JList(listModel);
		jFilters.addMouseListener(listener);
		jFilters.addListSelectionListener(listener);
		JScrollPane jScrollPanel = new JScrollPane(jFilters);
		jPanel.add(jScrollPanel);

		//Done
		jDone = new JButton(GuiShared.get().managerDone());
		jDone.setActionCommand(ACTION_DONE);
		jDone.addActionListener(listener);
		jPanel.add(jDone);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup()
				.addComponent(jScrollPanel, 282, 282, 282)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jLoad, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jRename, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jDelete, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jDone, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
				)
			)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jLoad, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRename, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDelete, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jScrollPanel)
				.addComponent(jDone, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}

	public String getSelectedString() {
		int selectedIndex =  jFilters.getSelectedIndex();
		if (selectedIndex != -1) {
			return (String) listModel.get(jFilters.getSelectedIndex());
		} else {
			return null;
		}

	}
	private void renameFilter() {
		//Get selected filter name
		String filterName = getSelectedString();
		if (filterName == null) {
			return;
		}

		String name = showNameDialog("", filterName, GuiShared.get().renameFilter());
		if (name == null) {
			return;
		}

		List<Filter> filter = filters.get(filterName);
		filters.remove(filterName); //Remove renamed filter (with old name)
		filters.remove(name); //Remove overwritten filter
		filters.put(name, filter); //Add renamed filter (with new name)
		updateFilters();
	}

	private void deleteFilters() {
		List<String> list = new ArrayList<String>();
		for (int index : jFilters.getSelectedIndices()) {
			String filterName = (String) listModel.get(index);
			list.add(filterName);
		}
		int value;
		if (list.size() > 1) {
			value = JOptionPane.showConfirmDialog(this.getDialog(), GuiShared.get().deleteFilters(list.size()), GuiShared.get().deleteFilter(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		} else if (list.size() == 1) {
			value = JOptionPane.showConfirmDialog(this.getDialog(), list.get(0), GuiShared.get().deleteFilter(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		} else {
			return;
		}
		if (value == JOptionPane.YES_OPTION) {
			for (String filterName : list) {
				filters.remove(filterName);
			}
			updateFilters();
		}
	}

	private void loadFilter() {
		String filterName = getSelectedString();
		if (filterName == null) {
			return;
		}
		List<Filter> filter = filters.get(filterName);
		gui.setFilters(filter);
		this.setVisible(false);
	}

	private void mergeFilters() {
		String name = showNameDialog("", "", GuiShared.get().mergeFilters());
		if (name == null) {
			return;
		}

		//Get filters to merge
		List<Filter> filter = new ArrayList<Filter>();
		for (Object obj : jFilters.getSelectedValues()) {
			for (Filter currentFilter : filters.get((String) obj)) {
				if (!filter.contains(currentFilter)) {
					filter.add(currentFilter);
				}
			}
		}
		filters.put(name, filter);
		updateFilters();
	}

	private String showNameDialog(final String oldValue, final String filterName, final String title) {
		//Show dialog
		String name = (String) JOptionPane.showInputDialog(this.getDialog(), GuiShared.get().enterFilterName(), title, JOptionPane.PLAIN_MESSAGE, null, null, oldValue);
		if (name == null) { //Cancel (do nothing)
			return null;
		}

		if (name.equals("")) { //No input (needed for name)
			JOptionPane.showMessageDialog(this.getDialog(), GuiShared.get().noFilterName(), title, JOptionPane.PLAIN_MESSAGE);
			return showNameDialog(name, filterName, title);
		}
		for (String filter : defaultFilters.keySet()) {
			if (filter.toLowerCase().equals(name.toLowerCase())) {
				JOptionPane.showMessageDialog(this.getDialog(), GuiShared.get().overwriteDefaultFilter(), title, JOptionPane.PLAIN_MESSAGE);
				return showNameDialog(name, filterName, title);
			}
		}
		if (filters.containsKey(name) && (filterName.isEmpty() || !filterName.equals(name))) {
			int nReturn = JOptionPane.showConfirmDialog(this.getDialog(), GuiShared.get().overwrite(), GuiShared.get().overwriteFilter(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.NO_OPTION) { //Overwrite cancelled
				return showNameDialog(name, filterName, title);
			}
		}
		return name;
	}

	public final void updateFilters() {
		listModel.clear();
		List<String> list = new ArrayList<String>(filters.keySet());
		Collections.sort(list, new CaseInsensitiveComparator());
		for (String filter: list) {
			listModel.addElement(filter);
		}
		if (!listModel.isEmpty()) {
			if (getSelectedString() == null) {
				jFilters.setSelectedIndex(0);
			}
			jDelete.setEnabled(true);
			jLoad.setEnabled(true);
			jRename.setEnabled(true);
		} else {
			jDelete.setEnabled(false);
			jLoad.setEnabled(false);
			jRename.setEnabled(false);
		}
		gui.updateFilters();
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			updateFilters();
		}
		super.setVisible(b);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jLoad;
	}

	@Override
	protected JButton getDefaultButton() {
		return jDone;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		this.setVisible(false);
	}

	private class ListenerClass implements ActionListener, MouseListener, ListSelectionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ACTION_DONE.equals(e.getActionCommand())) {
				save();
			}
			if (ACTION_LOAD_FILTER.equals(e.getActionCommand())) {
				if (jFilters.getSelectedIndices().length == 1) {
					loadFilter();
				} else {
					mergeFilters();
				}
			}
			if (ACTION_RENAME_FILTER.equals(e.getActionCommand())) {
				renameFilter();
			}
			if (ACTION_DELETE_FILTER.equals(e.getActionCommand())) {
				deleteFilters();
			}
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
			Object o = e.getSource();
			if (o instanceof JList && e.getClickCount() == 2
					&& !e.isControlDown() && !e.isShiftDown()) {
				loadFilter();
			}
		}

		@Override
		public void mousePressed(final MouseEvent e) { }

		@Override
		public void mouseReleased(final MouseEvent e) { }

		@Override
		public void mouseEntered(final MouseEvent e) { }

		@Override
		public void mouseExited(final MouseEvent e) { }

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (jFilters.getSelectedIndices().length > 1) {
				jLoad.setText(GuiShared.get().managerMerge());
				jRename.setEnabled(false);
			} else {
				jLoad.setText(GuiShared.get().managerLoad());
				jRename.setEnabled(true);
			}
		}
	}

}
