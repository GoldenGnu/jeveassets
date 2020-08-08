/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMultiSelectionDialog<T> extends JDialogCentered {

	private enum MultiSelectionActions {
		OK, CANCEL, CHECK_ALL
	}

	//GUI
	private final JCheckBox jAll;
	private final JMultiSelectionList<T> jList;
	private final JButton jOK;

	//Data
	private List<T> data;
	private boolean emptyAllowed;

	public JMultiSelectionDialog(final Program program, String title) {
		super(program, title);

		ListenerClass listener = new ListenerClass();

		jAll = new JCheckBox(GuiShared.get().all());
		jAll.setActionCommand(MultiSelectionActions.CHECK_ALL.name());
		jAll.addActionListener(listener);

		jList = new JMultiSelectionList<>();
		jList.addListSelectionListener(listener);
		JScrollPane jListScroll = new JScrollPane(jList);

		jOK = new JButton(GuiShared.get().ok());
		jOK.setActionCommand(MultiSelectionActions.OK.name());
		jOK.addActionListener(listener);
		jOK.setEnabled(false);

		JButton jCancel = new JButton(GuiShared.get().cancel());
		jCancel.setActionCommand(MultiSelectionActions.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jAll)
				.addComponent(jListScroll, 300, 300, 300)
				.addGroup(layout.createSequentialGroup()
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jAll)
				.addComponent(jListScroll, 200, 200, 200)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jOK;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	public List<T> show(Collection<T> items, boolean emptyAllowed) {
		return show(items, new ArrayList<>(), emptyAllowed);
	}

	public List<T> show(Collection<T> items, Collection<T> selected, boolean emptyAllowed) {
		this.emptyAllowed = emptyAllowed;
		ArrayList<T> list = new ArrayList<>(items);
		jList.setModel(new DataListModel<>(list));
		for (T item : selected) {
			int index = list.indexOf(item);
			if (index >= 0) {
				jList.addSelectionInterval(index, index);
			}
		}
		jAll.setSelected(jList.getSelectedIndices().length  == list.size());
		this.data = null;
		this.setVisible(true);
		return this.data;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		data = new ArrayList<>(jList.getSelectedValuesList());
		this.setVisible(false);
	}

	private class ListenerClass implements ListSelectionListener, ActionListener {
		@Override
		public void valueChanged(final ListSelectionEvent e) {
			jOK.setEnabled(emptyAllowed || jList.getSelectedIndices().length > 0);
			jAll.setSelected(jList.getSelectedIndices().length == jList.getModel().getSize());
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MultiSelectionActions.OK.name().equals(e.getActionCommand())) {
				save();
			} else if (MultiSelectionActions.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			} else if (MultiSelectionActions.CHECK_ALL.name().equals(e.getActionCommand())) {
				if (jAll.isSelected()) {
					jList.selectAll();
				} else {
					jList.clearSelection();
				}
			}
		}
	}

	private static class DataListModel<T> extends AbstractListModel<T> {

		private final List<T> data;

		public DataListModel(final List<T> data) {
			this.data = data;
		}

		@Override
		public int getSize() {
			return data.size();
		}

		@Override
		public T getElementAt(final int index) {
			return data.get(index);
		}
	}
}
