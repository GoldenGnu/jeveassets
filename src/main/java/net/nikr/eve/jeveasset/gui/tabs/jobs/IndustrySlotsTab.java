/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.jobs;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.i18n.TabsIndustrySlots;


public class IndustrySlotsTab extends JMainTabSecondary {

	//GUI
	private final JAutoColumnTable jTable;
	private final JLabel jManufacturing;
	private final JLabel jManufacturingDone;
	private final JLabel jManufacturingFree;
	private final JLabel jManufacturingActive;
	private final JLabel jManufacturingMax;
	private final JLabel jResearch;
	private final JLabel jResearchDone;
	private final JLabel jResearchFree;
	private final JLabel jResearchActive;
	private final JLabel jResearchMax;
	private final JLabel jReactions;
	private final JLabel jReactionsDone;
	private final JLabel jReactionsFree;
	private final JLabel jReactionsActive;
	private final JLabel jReactionsMax;

	//Table
	private final IndustrySlotFilterControl filterControl;
	private final DefaultEventTableModel<IndustrySlot> tableModel;
	private final EventList<IndustrySlot> eventList;
	private final FilterList<IndustrySlot> filterList;
	private final EnumTableFormatAdaptor<IndustrySlotTableFormat, IndustrySlot> tableFormat;
	private final DefaultEventSelectionModel<IndustrySlot> selectionModel;

	public static final String NAME = "industryslots"; //Not to be changed!

	private final IndustrySlotsData industrySlotsData;

	public IndustrySlotsTab(final Program program) {
		super(program, NAME, TabsIndustrySlots.get().title(), Images.TOOL_INDUSTRY_SLOTS.getIcon(), true);

		ListenerClass listener = new ListenerClass();

		industrySlotsData = new IndustrySlotsData(program);
		//Table Format
		tableFormat = TableFormatFactory.industrySlotTableFormat();
		//Backend
		eventList = EventListManager.create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<IndustrySlot> columnSortedList = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Sorting Total
		eventList.getReadWriteLock().readLock().lock();
		SortedList<IndustrySlot> totalSortedList = new SortedList<>(columnSortedList, new TotalComparator());
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(totalSortedList);
		eventList.getReadWriteLock().readLock().unlock();
		filterList.addListEventListener(listener);
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new IndustrySlotTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		PaddingTableCellRenderer.install(jTable, 3);
		//Sorting
		TableComparatorChooser.install(jTable, columnSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(filterList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new IndustrySlotFilterControl(totalSortedList);
		//Menu
		installTableTool(new IndustrySlotTableMenu(), tableFormat, tableModel, jTable, filterControl, IndustrySlot.class);

		jManufacturing = StatusPanel.createLabel(TabsIndustrySlots.get().manufacturing(), Images.MISC_MANUFACTURING.getIcon());
		this.addStatusbarLabel(jManufacturing);
		jManufacturingDone = StatusPanel.createLabel(TabsIndustrySlots.get().columnManufacturingDone(), Images.EDIT_SET.getIcon());
		this.addStatusbarLabel(jManufacturingDone);
		jManufacturingFree = StatusPanel.createLabel(TabsIndustrySlots.get().columnManufacturingFree(), Images.EDIT_ADD.getIcon());
		this.addStatusbarLabel(jManufacturingFree);
		jManufacturingActive = StatusPanel.createLabel(TabsIndustrySlots.get().columnManufacturingActive(), Images.UPDATE_WORKING.getIcon());
		this.addStatusbarLabel(jManufacturingActive);
		jManufacturingMax = StatusPanel.createLabel(TabsIndustrySlots.get().columnManufacturingDone(), Images.UPDATE_DONE_OK.getIcon());
		this.addStatusbarLabel(jManufacturingMax);

		jResearch = StatusPanel.createLabel(TabsIndustrySlots.get().research(), Images.MISC_INVENTION.getIcon());
		this.addStatusbarLabel(jResearch);
		jResearchDone = StatusPanel.createLabel(TabsIndustrySlots.get().columnResearchDone(), Images.EDIT_SET.getIcon());
		this.addStatusbarLabel(jResearchDone);
		jResearchFree = StatusPanel.createLabel(TabsIndustrySlots.get().columnResearchFree(), Images.EDIT_ADD.getIcon());
		this.addStatusbarLabel(jResearchFree);
		jResearchActive = StatusPanel.createLabel(TabsIndustrySlots.get().columnResearchActive(), Images.UPDATE_WORKING.getIcon());
		this.addStatusbarLabel(jResearchActive);
		jResearchMax = StatusPanel.createLabel(TabsIndustrySlots.get().columnResearchMax(), Images.UPDATE_DONE_OK.getIcon());
		this.addStatusbarLabel(jResearchMax);

		jReactions = StatusPanel.createLabel(TabsIndustrySlots.get().reactions(), Images.MISC_REACTION.getIcon());
		this.addStatusbarLabel(jReactions);
		jReactionsDone = StatusPanel.createLabel(TabsIndustrySlots.get().columnReactionsDone(), Images.EDIT_SET.getIcon());
		this.addStatusbarLabel(jReactionsDone);
		jReactionsFree = StatusPanel.createLabel(TabsIndustrySlots.get().columnReactionsFree(), Images.EDIT_ADD.getIcon());
		this.addStatusbarLabel(jReactionsFree);
		jReactionsActive = StatusPanel.createLabel(TabsIndustrySlots.get().columnReactionsActive(), Images.UPDATE_WORKING.getIcon());
		this.addStatusbarLabel(jReactionsActive);
		jReactionsMax = StatusPanel.createLabel(TabsIndustrySlots.get().columnReactionsMax(), Images.UPDATE_DONE_OK.getIcon());
		this.addStatusbarLabel(jReactionsMax);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateData() {
		//Update Data
		industrySlotsData.updateData(eventList);
	}

	@Override
	public void clearData() {
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		filterControl.clearCache();
	}

	@Override
	public void updateCache() {
		filterControl.createCache();
	}

	@Override
	public Collection<LocationType> getLocations() {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return new ArrayList<>(eventList);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}

	public EventList<IndustrySlot> getEventList() {
		return eventList;
	}

	private class IndustrySlotTableMenu implements TableMenu<IndustrySlot> {
		@Override
		public MenuData<IndustrySlot> getMenuData() {
			return new MenuData<>(selectionModel.getSelected());
		}

		@Override
		public JMenu getFilterMenu() {
			return filterControl.getMenu(jTable, selectionModel.getSelected());
		}

		@Override
		public JMenu getColumnMenu() {
			return new JMenuColumns<>(program, tableFormat, tableModel, jTable, NAME);
		}

		@Override
		public void addInfoMenu(JComponent jComponent) {
			JMenuInfo.industrySlots(jComponent, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class ListenerClass implements ListEventListener<IndustrySlot> {
		@Override
		public void listChanged(final ListEvent<IndustrySlot> listChanges) {
			IndustrySlot total = new IndustrySlot("");
			try {
				filterList.getReadWriteLock().readLock().lock();
				for (IndustrySlot industrySlot : filterList) {
					if (industrySlot.isGrandTotal()) {
						continue;
					}
					total.count(industrySlot);
				}
			} finally {
				filterList.getReadWriteLock().readLock().unlock();
			}
			jManufacturingDone.setText(Formater.longFormat(total.getManufacturingDone()));
			jManufacturingFree.setText(Formater.longFormat(total.getManufacturingFree()));
			jManufacturingActive.setText(Formater.longFormat(total.getManufacturingActive()));
			jManufacturingMax.setText(Formater.longFormat(total.getManufacturingMax()));
			jReactionsDone.setText(Formater.longFormat(total.getReactionsDone()));
			jReactionsFree.setText(Formater.longFormat(total.getReactionsFree()));
			jReactionsActive.setText(Formater.longFormat(total.getReactionsActive()));
			jReactionsMax.setText(Formater.longFormat(total.getReactionsMax()));
			jResearchDone.setText(Formater.longFormat(total.getResearchDone()));
			jResearchFree.setText(Formater.longFormat(total.getResearchFree()));
			jResearchActive.setText(Formater.longFormat(total.getResearchActive()));
			jResearchMax.setText(Formater.longFormat(total.getResearchMax()));
		}
	}

	private class IndustrySlotFilterControl extends FilterControl<IndustrySlot> {

		public IndustrySlotFilterControl(EventList<IndustrySlot> exportEventList) {
			super(program.getMainWindow().getFrame(),
					NAME,
					tableFormat,
					eventList,
					exportEventList,
					filterList
					);
		}

		@Override
		public void saveSettings(final String msg) {
			program.saveSettings("ISK Table: " + msg); //Save ISK Filters and Export Setttings
		}
	}

	public static class TotalComparator implements Comparator<IndustrySlot> {
		@Override
		public int compare(final IndustrySlot o1, final IndustrySlot o2) {
			if (o1.isGrandTotal() && o2.isGrandTotal()) {
				return 0; //Equal (both StockpileTotal)
			} else if (o1.isGrandTotal()) {
				return 1; //After
			} else if (o2.isGrandTotal()) {
				return -1; //Before
			} else {
				return 0; //Equal (not StockpileTotal)
			}
		}
	}

}
