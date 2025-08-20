/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import java.util.Collection;
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
import net.nikr.eve.jeveasset.gui.shared.StringComparators;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public abstract class JManagerDialog extends JDialogCentered {

	private enum ManageDialogAction {
		DONE,
		LOAD,
		EDIT,
		MERGE,
		COPY,
		RENAME,
		DELETE,
		EXPORT,
		IMPORT
	}

	private final DefaultListModel<String> listModel;
	private final JList<String> jList;
	private final JAutoCompleteDialog<String> jSaveDialog;
	private final JButton jDelete;
	private final JButton jLoad;
	private final JButton jEdit;
	private final JButton jMerge;
	private final JButton jCopy;
	private final JButton jRename;
	private final JButton jExport;
	private final JButton jImport;
	private final JButton jClose;
	private final boolean mergeReplaceLoad;
	private final boolean mergeReplaceEdit;

	public JManagerDialog(Program program, JFrame jFrame, String title, boolean supportLoad, boolean supportEdit, boolean supportCopy, boolean supportMerge, boolean supportExport) {
		super(program, title, jFrame, Images.DIALOG_SETTINGS.getImage());
		mergeReplaceLoad = supportLoad && supportMerge && !supportEdit;
		mergeReplaceEdit = !supportLoad && supportMerge && supportEdit;

		ListenerClass listener = new ListenerClass();

		jSaveDialog = new JAutoCompleteDialog<>(program, "", getDialog(), Images.DIALOG_SETTINGS.getImage(), textEnterName(), false, false, JAutoCompleteDialog.STRING_OPTIONS);

		//Load
		jLoad = new JButton(GuiShared.get().managerLoad(), Images.FILTER_LOAD.getIcon());
		jLoad.setActionCommand(ManageDialogAction.LOAD.name());
		jLoad.addActionListener(listener);
		jLoad.setVisible(supportLoad);
		jLoad.setHorizontalAlignment(SwingConstants.LEFT);

		//Edit
		jEdit = new JButton(GuiShared.get().managerEdit(), Images.EDIT_EDIT.getIcon());
		jEdit.setActionCommand(ManageDialogAction.EDIT.name());
		jEdit.addActionListener(listener);
		jEdit.setVisible(supportEdit);
		jEdit.setHorizontalAlignment(SwingConstants.LEFT);

		//Merge
		jMerge = new JButton(GuiShared.get().managerMerge(), Images.EDIT_ADD.getIcon());
		jMerge.setActionCommand(ManageDialogAction.MERGE.name());
		jMerge.addActionListener(listener);
		jMerge.setVisible(supportMerge && !mergeReplaceLoad && !mergeReplaceEdit);
		jMerge.setHorizontalAlignment(SwingConstants.LEFT);

		//Copy
		jCopy = new JButton(GuiShared.get().managerCopy(), Images.EDIT_COPY.getIcon());
		jCopy.setActionCommand(ManageDialogAction.COPY.name());
		jCopy.addActionListener(listener);
		jCopy.setVisible(supportCopy);
		jCopy.setHorizontalAlignment(SwingConstants.LEFT);

		//Rename
		jRename = new JButton(GuiShared.get().managerRename(), Images.EDIT_RENAME.getIcon());
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
		listModel = new DefaultListModel<>();
		jList = new JList<>(listModel);
		jList.addMouseListener(listener);
		jList.addListSelectionListener(listener);
		JScrollPane jScrollPanel = new JScrollPane(jList);
		jPanel.add(jScrollPanel);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jScrollPanel, 175, 175, 175)
				.addGroup(layout.createParallelGroup()
					.addComponent(jLoad, 100, 100, 100)
					.addComponent(jEdit, 100, 100, 100)
					.addComponent(jMerge, 100, 100, 100)
					.addComponent(jCopy, 100, 100, 100)
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
					.addComponent(jEdit, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMerge, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCopy, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
		int selectedIndex = jList.getSelectedIndex();
		if (selectedIndex != -1) {
			return listModel.get(jList.getSelectedIndex());
		} else {
			return null;
		}
	}

	private void setEnabledAll(boolean b) {
		jLoad.setEnabled(b);
		jEdit.setEnabled(b);
		jMerge.setEnabled(b);
		jRename.setEnabled(b);
		jCopy.setEnabled(b);
		jDelete.setEnabled(b);
		jExport.setEnabled(b);
	}

	protected final void update(Collection<String> list) {
		update(new ArrayList<>(list));
	}

	protected final void update(List<String> list) {
		jSaveDialog.updateData(list);
		listModel.clear();
		Collections.sort(list, StringComparators.CASE_INSENSITIVE);
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

	protected boolean validateName(final String name, final String oldName, final String title) {
		if (listModel.contains(name) && (oldName.isEmpty() || !oldName.equals(name))) {
			int nReturn = JOptionPane.showConfirmDialog(this.getDialog(), GuiShared.get().overwrite(), textOverwrite(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.NO_OPTION) { //Overwrite cancelled
				return false;
			}
		}
		return true;
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
	protected abstract void edit(final String name);
	protected abstract void merge(final String name, final List<String> list);
	protected abstract void copy(final String fromName, final String toName);
	protected abstract void rename(final String name, final String oldName);
	protected abstract void delete(final List<String> list);
	protected abstract void export(final List<String> list);
	protected abstract void importData();
	protected abstract String textDeleteMultipleMsg(int size);
	protected abstract String textDelete();
	protected abstract String textEnterName();
	protected abstract String textMerge();
	protected abstract String textRename();
	protected abstract String textOverwrite();

	private void delete() {
		List<String> list = new ArrayList<>();
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
		List<String> list = new ArrayList<>();
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

	private void edit() {
		String name = getSelectedString();
		if (name == null) {
			return;
		}
		edit(name);
	}

	private void merge() {
		String name = showNameDialog("", "", textMerge());
		if (name == null) {
			return;
		}
		List<String> list = new ArrayList<>();
		for (int index : jList.getSelectedIndices()) {
			String filterName = listModel.get(index);
			list.add(filterName);
		}
		merge(name, list);
	}

	private void copy() {
		String fromName = getSelectedString();
		if (fromName == null) {
			return;
		}
		String toName = showNameDialog("", "", textEnterName());
		if (toName == null) {
			return;
		}
		copy(fromName, toName);
	}

	private String showNameDialog(final String oldValue, final String oldName, final String title) {
		//Show dialog
		jSaveDialog.getDialog().setTitle(title);
		String name = jSaveDialog.show(oldValue);
		if (name == null) { //Cancel (do nothing)
			return null;
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
			} else if (ManageDialogAction.LOAD.name().equals(e.getActionCommand())) {
				load();
			} else if (ManageDialogAction.EDIT.name().equals(e.getActionCommand())) {
				edit();
			} else if (ManageDialogAction.MERGE.name().equals(e.getActionCommand())) {
				merge();
			} else if (ManageDialogAction.COPY.name().equals(e.getActionCommand())) {
				copy();
			} else if (ManageDialogAction.RENAME.name().equals(e.getActionCommand())) {
				rename();
			} else if (ManageDialogAction.DELETE.name().equals(e.getActionCommand())) {
				delete();
			} else if (ManageDialogAction.EXPORT.name().equals(e.getActionCommand())) {
				export();
			} else if (ManageDialogAction.IMPORT.name().equals(e.getActionCommand())) {
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
			boolean greatThanZero = jList.getSelectedIndices().length > 0;
			boolean greatThanOne = jList.getSelectedIndices().length > 1;
			boolean equalToOne = jList.getSelectedIndices().length == 1;
			jLoad.setEnabled(equalToOne);
			jEdit.setEnabled(equalToOne);
			jMerge.setEnabled(greatThanOne);
			jCopy.setEnabled(equalToOne);
			jRename.setEnabled(equalToOne);
			jDelete.setEnabled(greatThanZero);
			jExport.setEnabled(greatThanZero);
			if (mergeReplaceLoad) {
				jMerge.setVisible(greatThanOne);
				jLoad.setVisible(!greatThanOne);
			}
			if (mergeReplaceEdit) {
				jMerge.setVisible(greatThanOne);
				jEdit.setVisible(!greatThanOne);
			}
		}
	}

}
