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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public abstract class JManageDialog extends JDialogCentered {

	private enum ManageDialogAction {
		DONE,
		LOAD,
		RENAME,
		DELETE,
		EXPORT,
		IMPORT
	}

	private final DefaultListModel<String> listModel;
	private final JList<String> jList;
	private final JButton jDelete;
	private final JButton jLoad;
	private final JButton jRename;
	private final JButton jExport;
	private final JButton jImport;
	private final JButton jClose;
	private boolean supportMerge = true;

	public JManageDialog(Program program, JFrame jFrame, String title, boolean supportMerge, boolean supportExport) {
		super(program, title, jFrame, Images.DIALOG_SETTINGS.getImage());
		this.supportMerge = supportMerge;

		ListenerClass listener = new ListenerClass();

		//Load
		jLoad = new JButton(GuiShared.get().managerLoad(), Images.FILTER_LOAD.getIcon());
		jLoad.setActionCommand(ManageDialogAction.LOAD.name());
		jLoad.addActionListener(listener);
		jLoad.setHorizontalAlignment(SwingConstants.LEFT);

		//Rename
		jRename = new JButton(GuiShared.get().managerRename(), Images.EDIT_EDIT.getIcon());
		jRename.setActionCommand(ManageDialogAction.RENAME.name());
		jRename.addActionListener(listener);
		jRename.setHorizontalAlignment(SwingConstants.LEFT);

		//Delete
		jDelete = new JButton(GuiShared.get().managerDelete(), Images.EDIT_DELETE.getIcon());
		jDelete.setActionCommand(ManageDialogAction.DELETE.name());
		jDelete.addActionListener(listener);
		jDelete.setHorizontalAlignment(SwingConstants.LEFT);

		//Export
		jExport = new JButton(GuiShared.get().managerExport(), Images.DIALOG_CSV_EXPORT.getIcon());
		jExport.setActionCommand(ManageDialogAction.EXPORT.name());
		jExport.addActionListener(listener);
		jExport.setVisible(supportExport);
		jExport.setHorizontalAlignment(SwingConstants.LEFT);

		//Import
		jImport = new JButton(GuiShared.get().managerImport(), Images.EDIT_IMPORT.getIcon());
		jImport.setActionCommand(ManageDialogAction.IMPORT.name());
		jImport.addActionListener(listener);
		jImport.setVisible(supportExport);
		jImport.setHorizontalAlignment(SwingConstants.LEFT);
		
		//Done
		jClose = new JButton(GuiShared.get().managerClose());
		jClose.setActionCommand(ManageDialogAction.DONE.name());
		jClose.addActionListener(listener);

		//List
		listModel = new DefaultListModel<String>();
		jList = new JList<String>(listModel);
		jList.addMouseListener(listener);
		jList.addListSelectionListener(listener);
		JScrollPane jScrollPanel = new JScrollPane(jList);
		jPanel.add(jScrollPanel);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jScrollPanel, 175, 175, 175)
				.addGroup(layout.createParallelGroup()
					.addComponent(jLoad, 100, 100, 100)
					.addComponent(jRename, 100, 100, 100)
					.addComponent(jDelete, 100, 100, 100)
					.addComponent(jExport, 100, 100, 100)
					.addComponent(jImport, 100, 100, 100)
					.addComponent(jClose, 100, 100, 100)
				)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jScrollPanel, 250, 250, 250)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jLoad, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRename, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jDelete, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addGap(15, 15, 15)
					.addComponent(jExport, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jImport, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jClose, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	private String getSelectedString() {
		int selectedIndex =  jList.getSelectedIndex();
		if (selectedIndex != -1) {
			return listModel.get(jList.getSelectedIndex());
		} else {
			return null;
		}
	}

	private void setEnabledAll(boolean b) {
		jDelete.setEnabled(b);
		jLoad.setEnabled(b);
		jRename.setEnabled(b);
	}

	protected final void update(List<String> list) {
		listModel.clear();
		Collections.sort(list, new CaseInsensitiveComparator());
		for (String filter: list) {
			listModel.addElement(filter);
		}
		if (!listModel.isEmpty()) {
			if (getSelectedString() == null) {
				jList.setSelectedIndex(0);
			}
			setEnabledAll(true);
		} else {
			setEnabledAll(false);
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jList;
	}

	@Override
	protected JButton getDefaultButton() {
		return jClose;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		this.setVisible(false);
	}

	protected abstract void load(final String name);
	protected abstract void merge(final String name, final List<String> list);
	protected abstract void rename(final String name, final String oldName);
	protected abstract void delete(final List<String> list);
	protected abstract void export(final List<String> list);
	protected abstract void importData();
	protected abstract boolean validateName(final String name, final String oldName, final String title);
	protected abstract String textDeleteMultipleMsg(int size);
	protected abstract String textDelete();
	protected abstract String textEnterName();
	protected abstract String textNoName();
	protected abstract String textMerge();
	protected abstract String textRename();
	

	private void delete() {
		List<String> list = new ArrayList<String>();
		for (int index : jList.getSelectedIndices()) {
			String filterName = listModel.get(index);
			list.add(filterName);
		}
		int value;
		if (list.size() > 1) {
			value = JOptionPane.showConfirmDialog(this.getDialog(), textDeleteMultipleMsg(list.size()), textDelete(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		} else if (list.size() == 1) {
			value = JOptionPane.showConfirmDialog(this.getDialog(), list.get(0), textDelete(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		} else {
			return;
		}
		if (value == JOptionPane.YES_OPTION) {
			delete(list);
		}
	}

	private void export() {
		List<String> list = new ArrayList<String>();
		for (int index : jList.getSelectedIndices()) {
			String filterName = listModel.get(index);
			list.add(filterName);
		}
		export(list);
	}

	private void load() {
		String name = getSelectedString();
		if (name == null) {
			return;
		}
		load(name);
	}

	private void merge() {
		String name = showNameDialog("", "", textMerge());
		if (name == null) {
			return;
		}
		List<String> list = new ArrayList<String>();
		for (int index : jList.getSelectedIndices()) {
			String filterName = listModel.get(index);
			list.add(filterName);
		}
		merge(name, list);
	}

	private String showNameDialog(final String oldValue, final String oldName, final String title) {
		//Show dialog
		String name = (String) JOptionPane.showInputDialog(this.getDialog(), textEnterName(), title, JOptionPane.PLAIN_MESSAGE, null, null, oldValue);
		if (name == null) { //Cancel (do nothing)
			return null;
		}

		if (name.equals("")) { //No input (needed for name)
			JOptionPane.showMessageDialog(this.getDialog(), textNoName(), title, JOptionPane.PLAIN_MESSAGE);
			return showNameDialog(name, oldName, title);
		}

		if (!validateName(name, oldName, title)) {
			return showNameDialog(name, oldName, title);
		}
		return name;
	}

	private void rename() {
		//Get selected filter name
		String selectedName = getSelectedString();
		if (selectedName == null) {
			return;
		}

		String name = showNameDialog(selectedName, selectedName, textRename());
		if (name == null) {
			return;
		}
		rename(name, selectedName);
	}

	private class ListenerClass implements ActionListener, MouseListener, ListSelectionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ManageDialogAction.DONE.name().equals(e.getActionCommand())) {
				save();
			}
			if (ManageDialogAction.LOAD.name().equals(e.getActionCommand())) {
				if (jList.getSelectedIndices().length == 1) {
					load();
				} else {
					merge();
				}
			}
			if (ManageDialogAction.RENAME.name().equals(e.getActionCommand())) {
				rename();
			}
			if (ManageDialogAction.DELETE.name().equals(e.getActionCommand())) {
				delete();
			}
			if (ManageDialogAction.EXPORT.name().equals(e.getActionCommand())) {
				export();
			}
			if (ManageDialogAction.IMPORT.name().equals(e.getActionCommand())) {
				importData();
			}
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
			Object o = e.getSource();
			if (o instanceof JList && e.getClickCount() == 2
					&& !e.isControlDown() && !e.isShiftDown()) {
				load();
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
			if (jList.getSelectedIndices().length > 1) {
				if (supportMerge) {
					jLoad.setText(GuiShared.get().managerMerge());
					jLoad.setIcon(Images.EDIT_ADD.getIcon());
				} else {
					jLoad.setEnabled(false);
				}
				jRename.setEnabled(false);
			} else {
				jLoad.setText(GuiShared.get().managerLoad());
				jLoad.setIcon(Images.FILTER_LOAD.getIcon());
				jLoad.setEnabled(true);
				jRename.setEnabled(true);
			}
		}
	}
	
}
