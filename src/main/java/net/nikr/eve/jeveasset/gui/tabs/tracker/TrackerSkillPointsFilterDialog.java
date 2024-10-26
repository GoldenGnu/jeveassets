/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.tracker;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.i18n.TabsTracker;


public class TrackerSkillPointsFilterDialog extends JDialogCentered {

	private final JCheckBox jAll;
	private final JAutoColumnTable jTable;
	private final EventList<TrackerSkillPointFilter> eventList;
	private final DefaultEventTableModel<TrackerSkillPointFilter> tableModel;
	private final JButton jOK;
	private boolean save = false;

	public TrackerSkillPointsFilterDialog(Program program) {
		super(program, TabsTracker.get().skillPointFilters(), Images.TOOL_TRACKER.getImage());

		jAll = new JCheckBox(TabsTracker.get().all());
		jAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAll(jAll.isSelected());
			}
		});

		eventList = EventListManager.create();
		eventList.addListEventListener(new ListEventListener<TrackerSkillPointFilter>() {
			@Override
			public void listChanged(ListEvent<TrackerSkillPointFilter> listChanges) {
				updateSelected();
			}
		});

		tableModel = EventModels.createTableModel(eventList, TableFormatFactory.trackerSkillPointsFilterTableFormat());
		jTable = new JAutoColumnTable(program, tableModel);
		jTable.getTableHeader().setReorderingAllowed(false);

		JScrollPane jTableScroll = new JScrollPane(jTable);
		jTableScroll.getVerticalScrollBar().setUnitIncrement(19);
		jTableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		DefaultEventSelectionModel<TrackerSkillPointFilter> selectionModel = EventModels.createSelectionModel(eventList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);

		jOK = new JButton(TabsTracker.get().ok());
		jOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		JButton jCancel = new JButton(TabsTracker.get().cancel());
		jCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(jAll)
				.addComponent(jTableScroll, 0, GroupLayout.PREFERRED_SIZE, 750)
				.addGroup(layout.createSequentialGroup()
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jAll)
				.addComponent(jTableScroll, 0, GroupLayout.PREFERRED_SIZE, 450)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	public boolean show() {
		Set<TrackerSkillPointFilter> filters = new TreeSet<>();
		for (OwnerType ownerType : program.getOwnerTypes()) {
			if (ownerType.isCorporation()) {
				continue; //Corporation can not have skill points
			}
			final String ownerName = ownerType.getOwnerName();
			TrackerSkillPointFilter filter = Settings.get().getTrackerSettings().getSkillPointFilters().get(ownerName);
			if (filter == null) {
				filter = new TrackerSkillPointFilter(ownerName);
			}
			filters.add(new TrackerSkillPointFilter(filter)); //Working Copy
		}
		//Update rows (Add all rows)
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(filters);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		jTable.setPreferredScrollableViewportSize(jTable.getPreferredSize());
		save = false;
		setVisible(true);
		return save;
	}

	private void selectAll(final boolean selected) {
		try {
			eventList.getReadWriteLock().writeLock().lock();
			for (TrackerSkillPointFilter filter : eventList) {
				filter.setEnabled(selected);
			}
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		for (int row = 0; row < jTable.getRowCount(); row++) {
			tableModel.fireTableCellUpdated(row, 0);
		}
	}

	private void updateSelected() {
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (TrackerSkillPointFilter filter : eventList) {
				if (!filter.isEnabled()) {
					jAll.setSelected(false);
					return;
				}
			}
			jAll.setSelected(true);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jOK;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		save = !compareLists(eventList, Settings.get().getTrackerSettings().getSkillPointFilters().values(), new ListComparator());
		if (save) {
			try {
				eventList.getReadWriteLock().readLock().lock();
				Settings.lock("Tracker Skill Points Filters: Update");
				for (TrackerSkillPointFilter filter : eventList) {
					Settings.get().getTrackerSettings().getSkillPointFilters().put(filter.getName(), filter);
				}
			} finally {
				Settings.unlock("Tracker Skill Points Filters: Update");
				program.saveSettings("Tracker Skill Points Filters: Update");
				eventList.getReadWriteLock().readLock().unlock();
			}
		}
		setVisible(false);
	}

	private <T> boolean compareLists(Collection<T> list1, Collection<T> list2, Comparator<? super T> comparator) {

		// if not the same size, lists are not equal
		if (list1.size() != list2.size()) {
			return false;
		}

		// create sorted copies to avoid modifying the original lists
		List<T> copy1 = new ArrayList<>(list1);
		List<T> copy2 = new ArrayList<>(list2);

		Collections.sort(copy1, comparator);
		Collections.sort(copy2, comparator);

		// iterate through the elements and compare them one by one using
		// the provided comparator.
		Iterator<T> it1 = copy1.iterator();
		Iterator<T> it2 = copy2.iterator();
		while (it1.hasNext()) {
			T t1 = it1.next();
			T t2 = it2.next();
			if (comparator.compare(t1, t2) != 0) {
				// as soon as a difference is found, stop looping
				return false;
			}
		}
		return true;
	}

	private static class ListComparator implements Comparator<TrackerSkillPointFilter> {

		@Override
		public int compare(TrackerSkillPointFilter o1, TrackerSkillPointFilter o2) {
			int compared;
			compared = GlazedLists.comparableComparator().compare(o1.getName(), o2.getName());
			if (compared != 0) {
				return compared;
			}
			compared = GlazedLists.comparableComparator().compare(o1.getMinimum(), o2.getMinimum());
			if (compared != 0) {
				return compared;
			}
			compared = GlazedLists.comparableComparator().compare(o1.isEnabled(), o2.isEnabled());
			if (compared != 0) {
				return compared;
			}
			return 0;
		}
	}

}
