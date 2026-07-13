/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.StringComparators;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels.StringFilterator;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JFilterSaveDialog extends JDialogCentered {

	private enum FilterSaveAction {
		SAVE, CANCEL
	}

	private final EventList<String> filters;
	private final List<String> defaultFilters = new ArrayList<>();
	private final JComboBox<String> jName;
	private final JCheckBox jSort;
	private final JCheckBox jColumns;
	private final JButton jSave;

	private FilterSave returnValue;

	public JFilterSaveDialog(final Window window) {
		super(null, GuiShared.get().saveFilter(), window);

		ListenerClass listener = new ListenerClass();

		JLabel jText = new JLabel(GuiShared.get().enterFilterName());

		jName = new JComboBox<>();
		filters = EventListManager.create();
		AutoCompleteSupport<String> nameAutoComplete = AutoCompleteSupport.install(jName, EventModels.createSwingThreadProxyList(filters), new StringFilterator());
		nameAutoComplete.setFilterMode(TextMatcherEditor.CONTAINS);

		jSort = new JCheckBox(GuiShared.get().saveFilterSort());
		jSort.setToolTipText(GuiShared.get().saveFilterSortToolTip());

		jColumns = new JCheckBox(GuiShared.get().saveFilterColumns());
		jColumns.setToolTipText(GuiShared.get().saveFilterColumnsToolTip());

		jSave = new JButton(GuiShared.get().save());
		jSave.setActionCommand(FilterSaveAction.SAVE.name());
		jSave.addActionListener(listener);

		JButton jCancel = new JButton(GuiShared.get().cancel());
		jCancel.setActionCommand(FilterSaveAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jText)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jName, 220, 220, 220)
					.addComponent(jSort, 220, 220, 220)
					.addComponent(jColumns, 220, 220, 220)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jSave, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jText, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jName, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSort, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jColumns, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGap(15)
				.addGroup(layout.createParallelGroup()
					.addComponent(jSave, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	FilterSave show(final List<String> filters, final List<String> defaultFilters, boolean sort, boolean view) {
		returnValue = null;
		Collections.sort(filters, StringComparators.CASE_INSENSITIVE);
		jSort.setSelected(false);
		jSort.setVisible(sort);
		jColumns.setSelected(false);
		jColumns.setVisible(view);
		try {
			this.filters.getReadWriteLock().writeLock().lock();
			this.filters.clear();
			this.filters.addAll(filters);
		} finally {
			this.filters.getReadWriteLock().writeLock().unlock();
		}
		this.defaultFilters.clear();
		this.defaultFilters.addAll(defaultFilters);
		this.setVisible(true);
		return returnValue;
	}

	private boolean validate() {
		String name = (String) jName.getSelectedItem();
		if (name == null) {
			JOptionPane.showMessageDialog(this.getDialog(), GuiShared.get().noFilterName(), GuiShared.get().saveFilter(), JOptionPane.PLAIN_MESSAGE);
			return false;
		}
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this.getDialog(), GuiShared.get().noFilterName(), GuiShared.get().saveFilter(), JOptionPane.PLAIN_MESSAGE);
			return false;
		}
		for (String filter : defaultFilters) {
			if (filter.toLowerCase().equals(name.toLowerCase())) { //Case insetitive contains
				JOptionPane.showMessageDialog(this.getDialog(), GuiShared.get().overwriteDefaultFilter(), GuiShared.get().saveFilter(), JOptionPane.PLAIN_MESSAGE);
				return false;
			}
		}
		try {
			filters.getReadWriteLock().readLock().lock();
			if (filters.contains(name)) {
				int nReturn = JOptionPane.showConfirmDialog(this.getDialog(), GuiShared.get().overwrite(), GuiShared.get().overwriteFilter(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (nReturn == JOptionPane.NO_OPTION) {
					return false;
				}
			}
		} finally {
			filters.getReadWriteLock().readLock().unlock();
		}
		return true;
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jName;
	}

	@Override
	protected JButton getDefaultButton() {
		return jSave;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		if (validate()) {
			String name = (String) jName.getSelectedItem();
			boolean sort = jSort.isSelected();
			boolean view = jColumns.isSelected();
			returnValue = new FilterSave(name, sort, view);
			setVisible(false);
		}
		//XXX - Workaround for strange bug:
		// 1. Tricker validation by pressing enter in the jName JComboBox
		// 2. Doing validate JOptionPane is shown and lose focus (to another program)
		// 3. JOptionPane is hidden (by mouse click)
		// 4. jName is not responding (string is locked)
		try {
			Robot robot = new Robot();
			robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (AWTException e) {

		}
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			jName.getModel().setSelectedItem("");
		}
		super.setVisible(b);
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (FilterSaveAction.SAVE.name().equals(e.getActionCommand())) {
				save();
			}
			if (FilterSaveAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
		}
	}

	public static class FilterSave {

		private final String name;
		private final boolean sort;
		private final boolean columns;

		public FilterSave(String name, boolean sort, boolean columns) {
			this.name = name;
			this.sort = sort;
			this.columns = columns;
		}

		public String getName() {
			return name;
		}

		public boolean isSort() {
			return sort;
		}

		public boolean isColumns() {
			return columns;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 41 * hash + Objects.hashCode(this.name);
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
			final FilterSave other = (FilterSave) obj;
			return Objects.equals(this.name, other.name);
		}
	}
}
