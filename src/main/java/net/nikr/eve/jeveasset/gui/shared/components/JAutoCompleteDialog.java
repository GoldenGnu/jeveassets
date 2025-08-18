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

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Comparator;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.gui.shared.StringComparators;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels.ItemFilterator;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels.LocationFilterator;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels.SolarSystemFilterator;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels.StringFilterator;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels.ViewFilterator;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JAutoCompleteDialog<T> extends JDialogCentered {

	public static final AutoCompleteOptions<String> STRING_OPTIONS = new AutoCompleteOptions<String>() {
		@Override
		public Comparator<String> getComparator() {
			return StringComparators.CASE_INSENSITIVE;
		}

		@Override
		public TextFilterator<String> getFilterator() {
			return new StringFilterator();
		}

		@Override
		public String getValue(Object object) {
			if (object instanceof String) {
				return (String) object;
			} else {
				return null;
			}
		}

		@Override
		public boolean isEmpty(String t) {
			return t.isEmpty();
		}
	};

	public static final AutoCompleteOptions<MyLocation> LOCATION_OPTIONS = new AutoCompleteOptions<MyLocation>() {
		@Override
		public Comparator<MyLocation> getComparator() {
			return GlazedLists.comparableComparator();
		}

		@Override
		public TextFilterator<MyLocation> getFilterator() {
			return new LocationFilterator();
		}

		@Override
		public MyLocation getValue(Object object) {
			if (object instanceof MyLocation) {
				return (MyLocation) object;
			} else {
				return null;
			}
		}

		@Override
		public boolean isEmpty(MyLocation t) {
			return false;
		}
	};

	public static final AutoCompleteOptions<View> VIEW_OPTIONS = new AutoCompleteOptions<View>() {
		@Override
		public Comparator<View> getComparator() {
			return GlazedLists.comparableComparator();
		}

		@Override
		public TextFilterator<View> getFilterator() {
			return new ViewFilterator();
		}

		@Override
		public View getValue(Object object) {
			if (object instanceof View) {
				return (View) object;
			} else if (object instanceof String) {
				return new View((String) object);
			} else {
				return null;
			}
		}

		@Override
		public boolean isEmpty(View t) {
			return t.getName().isEmpty();
		}
	};

	public static final AutoCompleteOptions<Item> ITEM_OPTIONS = new AutoCompleteOptions<Item>() {
		@Override
		public Comparator<Item> getComparator() {
			return GlazedLists.comparableComparator();
		}

		@Override
		public TextFilterator<Item> getFilterator() {
			return new ItemFilterator();
		}

		@Override
		public Item getValue(Object object) {
			if (object instanceof Item) {
				return (Item) object;
			} else {
				return null;
			}
		}

		@Override
		public boolean isEmpty(Item t) {
			return false;
		}
	};

	public static final AutoCompleteOptions<SolarSystem> SOLAR_SYSTEM_OPTIONS = new AutoCompleteOptions<SolarSystem>() {
		@Override
		public Comparator<SolarSystem> getComparator() {
			return new SystemComparator();
		}

		@Override
		public TextFilterator<SolarSystem> getFilterator() {
			return new SolarSystemFilterator();
		}

		@Override
		public SolarSystem getValue(Object object) {
			if (object instanceof SolarSystem) {
				return (SolarSystem) object;
			} else {
				return null;
			}
		}

		@Override
		public boolean isEmpty(SolarSystem t) {
			return false;
		}
	};

	private static class SystemComparator implements Comparator<SolarSystem> {
		@Override
		public int compare(SolarSystem o1, SolarSystem o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	}

	private enum AutoCompleteAction {
		OK, CANCEL
	}

	private final EventList<T> eventList;
	private final AutoCompleteSupport<T> autoComplete;
	private final JComboBox<T> jItems;
	private final JButton jOK;

	private final AutoCompleteOptions<T> autoCompleteOptions;
	private final boolean strict;
	private final boolean askOverwrite;

	private T value;

	public JAutoCompleteDialog(Program program, String title, Image image, String msg, boolean strict, AutoCompleteOptions<T> autoCompleteOptions) {
		this(program, title, program.getMainWindow().getFrame(), image, msg, strict, true, autoCompleteOptions);
	}

	public JAutoCompleteDialog(Program program, String title, Window window, Image image, String msg, boolean strict, AutoCompleteOptions<T> autoCompleteOptions) {
		this(program, title, window, image, msg, strict, true, autoCompleteOptions);
	}

	public JAutoCompleteDialog(Program program, String title, Image image, String msg, boolean strict, boolean askOverwrite, AutoCompleteOptions<T> autoCompleteOptions) {
		this(program, title, program.getMainWindow().getFrame(), image, msg, strict, askOverwrite, autoCompleteOptions);
	}

	public JAutoCompleteDialog(Program program, String title, Window window, Image image, String msg, boolean strict, boolean askOverwrite, AutoCompleteOptions<T> autoCompleteOptions) {
		super(program, title, window, image);
		this.strict = strict;
		this.askOverwrite = askOverwrite;
		this.autoCompleteOptions = autoCompleteOptions;
		
		ListenerClass listener = new ListenerClass();

		JLabel jText = new JLabel();
		if (msg != null) {
			jText.setText(msg);
		} else {
			jText.setVisible(false);
		}

		jItems = new JComboBox<>();
		eventList = EventListManager.create();

		eventList.getReadWriteLock().readLock().lock();
		SortedList<T> sortedList = new SortedList<>(eventList, autoCompleteOptions.getComparator());
		eventList.getReadWriteLock().readLock().unlock();

		autoComplete = AutoCompleteSupport.install(jItems, EventModels.createSwingThreadProxyList(sortedList), autoCompleteOptions.getFilterator());
		if (!strict) {
			autoComplete.setFilterMode(TextMatcherEditor.CONTAINS);
		}
		autoComplete.setStrict(strict);

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
						.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jText, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jItems, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	public final void updateData(Collection<T> list) {
		boolean same = false;
		try {
			eventList.getReadWriteLock().readLock().lock();
			same = eventList.equals(list);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		if (!same) {
			try {
				eventList.getReadWriteLock().writeLock().lock();
				eventList.clear();
				eventList.addAll(list);
			} finally {
				eventList.getReadWriteLock().writeLock().unlock();
			}
		}
	}

	public T show(T t) {
		jItems.getModel().setSelectedItem(t);
		value = null;
		setVisible(true);
		return value;
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

	protected boolean valid(T value) {
		if (value == null || autoCompleteOptions.isEmpty(value)) {
			JOptionPane.showMessageDialog(getDialog(), GuiShared.get().invalidMsg(), GuiShared.get().invalidTitle(), JOptionPane.PLAIN_MESSAGE);
			return false;
		}
		if (strict || !askOverwrite) {
			return true;
		} else {
			if (EventListManager.contains(eventList, value)) {
				int nReturn = JOptionPane.showConfirmDialog(getDialog(), GuiShared.get().overwrite(), GuiShared.get().overwriteTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
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
		value = autoCompleteOptions.getValue(jItems.getSelectedItem());
		if (valid(value)) {
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
				value = null;
				setVisible(false);
			}
		}
	}

	public static interface AutoCompleteOptions<T> {
		public Comparator<T> getComparator();
		public TextFilterator<T> getFilterator();
		public T getValue(Object object);
		public boolean isEmpty(T t);
	}
}
