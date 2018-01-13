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

package net.nikr.eve.jeveasset.gui.shared.filter;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;
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
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.components.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class FilterSave extends JDialogCentered {

	private enum FilterSaveAction {
		SAVE, CANCEL
	}

	private final EventList<String> filters;
	private final List<String> defaultFilters = new ArrayList<String>();
	private final JComboBox<String> jName;
	private final JButton jSave;

	private String returnString;

	public FilterSave(final Window window) {
		super(null, GuiShared.get().saveFilter(), window);

		ListenerClass listener = new ListenerClass();

		JLabel jText = new JLabel(GuiShared.get().enterFilterName());

		jName = new JComboBox<String>();
		JCopyPopup.install((JTextComponent) jName.getEditor().getEditorComponent());
		filters = new EventListManager<String>().create();
		AutoCompleteSupport.install(jName, EventModels.createSwingThreadProxyList(filters), new Filterator());
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
				.addGroup(layout.createParallelGroup()
					.addComponent(jSave, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	String show(final List<String> filters, final List<String> defaultFilters) {
		returnString = null;
		Collections.sort(filters, new CaseInsensitiveComparator());
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
		return returnString;
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
			returnString = (String) jName.getSelectedItem();
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

	private static class Filterator implements TextFilterator<String> {
		@Override
		public void getFilterStrings(final List<String> baseList, final String element) {
			if (element.length() > 0) {
				baseList.add(element);
			}
		}
	}
}
