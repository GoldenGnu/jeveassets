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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
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
		DELETE
	}

	private DefaultListModel listModel;
	private JList jList;
	private JButton jDelete;
	private JButton jLoad;
	private JButton jRename;
	private JButton jDone;
	private boolean supportMerge = true;

	public JManageDialog(Program program, JFrame jFrame, String title) {
		super(program, title, jFrame, Images.DIALOG_SETTINGS.getImage());

		ListenerClass listener = new ListenerClass();

		//Load
		jLoad = new JButton(GuiShared.get().managerLoad());
		jLoad.setActionCommand(ManageDialogAction.LOAD.name());
		jLoad.addActionListener(listener);
		jPanel.add(jLoad);

		//Rename
		jRename = new JButton(GuiShared.get().managerRename());
		jRename.setActionCommand(ManageDialogAction.RENAME.name());
		jRename.addActionListener(listener);
		jPanel.add(jRename);

		//Delete
		jDelete = new JButton(GuiShared.get().managerDelete());
		jDelete.setActionCommand(ManageDialogAction.DELETE.name());
		jDelete.addActionListener(listener);
		jPanel.add(jDelete);

		//List
		listModel = new DefaultListModel();
		jList = new JList(listModel);
		jList.addMouseListener(listener);
		jList.addListSelectionListener(listener);
		JScrollPane jScrollPanel = new JScrollPane(jList);
		jPanel.add(jScrollPanel);

		//Done
		jDone = new JButton(GuiShared.get().managerDone());
		jDone.setActionCommand(ManageDialogAction.DONE.name());
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

	public boolean isSupportMerge() {
		return supportMerge;
	}

	public void setSupportMerge(boolean supportMerge) {
		this.supportMerge = supportMerge;
	}

	private String getSelectedString() {
		int selectedIndex =  jList.getSelectedIndex();
		if (selectedIndex != -1) {
			return (String) listModel.get(jList.getSelectedIndex());
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

	protected abstract void load(final String name);
	protected abstract void merge(final String name, final Object[] objects);
	protected abstract void rename(final String name, final String oldName);
	protected abstract void delete(final List<String> list);
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
			String filterName = (String) listModel.get(index);
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

		merge(name, jList.getSelectedValues());
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
				} else {
					jLoad.setEnabled(false);
				}
				jRename.setEnabled(false);
			} else {
				jLoad.setText(GuiShared.get().managerLoad());
				jLoad.setEnabled(true);
				jRename.setEnabled(true);
			}
		}
	}
	
}
