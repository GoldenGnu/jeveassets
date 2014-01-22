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

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public abstract class JAutoCompleteDialog<T> extends JDialogCentered {

	private enum AutoCompleteAction {
		OK, CANCEL
	}

	private final EventList<T> eventList;
	private final AutoCompleteSupport<T> autoComplete;
	private final JComboBox jItems;
	private final JButton jOK;

	private final boolean strict;
	private final boolean allowOverwrite;

	private T value;

	public JAutoCompleteDialog(Program program, String title, Image image, String msg, boolean strict, boolean allowOverwrite) {
		super(program, title, image);

		this.strict = strict;
		this.allowOverwrite = allowOverwrite;

		ListenerClass listener = new ListenerClass();

		JLabel jText = new JLabel(msg);

		jItems = new JComboBox();
		eventList = new BasicEventList<T>();
		SortedList<T> sortedList = new SortedList<T>(eventList, getComparator());
		autoComplete = AutoCompleteSupport.install(jItems, sortedList, getFilterator());

		jOK = new JButton(GuiShared.get().ok());
		jOK.setActionCommand(AutoCompleteAction.OK.name());
		jOK.addActionListener(listener);

		JButton jCancel = new JButton(GuiShared.get().cancel());
		jCancel.setActionCommand(AutoCompleteAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jText)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jItems, 220, 220, 220)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jText, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jItems, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	protected abstract Comparator<T> getComparator();
	protected abstract TextFilterator<T> getFilterator();
	protected abstract T getValue(Object object);

	public final void updateData(List<T> list) {
		eventList.getReadWriteLock().writeLock().lock();
		try {
			eventList.clear();
			eventList.addAll(list);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Can not set strict on empty EventList - so we do it now (if possible)
		if (!eventList.isEmpty()) {
			autoComplete.setStrict(strict);
		}
	}

	public EventList<T> getEventList() {
		return eventList;
	}

	public T show() {
		autoComplete.removeFirstItem();
		if (jItems.getModel().getSize() > 0) {
			jItems.setSelectedIndex(0);
		}
		if (!strict) { //No effect when strict (except a beep)
			jItems.getModel().setSelectedItem("");
		}
		value = null;
		setVisible(true);
		return value;
	}

	protected boolean valied(T value) {
		if (allowOverwrite) {
			return true;
		} else {
			if (eventList.contains(value)) {
				int nReturn = JOptionPane.showConfirmDialog(getDialog(), GuiShared.get().overwrite(), GuiShared.get().overwriteView(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (nReturn == JOptionPane.NO_OPTION) { //Overwrite cancelled
					return false;
				}
			}
			return true;
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jItems;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		value = getValue(jItems.getSelectedItem());
		if (valied(value)) {
			setVisible(false);
		}
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (AutoCompleteAction.OK.name().equals(e.getActionCommand())) {
				save();
			}
			if (AutoCompleteAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
		}
	}
}
