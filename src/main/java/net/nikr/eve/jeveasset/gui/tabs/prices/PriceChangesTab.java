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

package net.nikr.eve.jeveasset.gui.tabs.prices;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.optionalusertools.DateVetoPolicy;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.PriceDataSettings.PriceMode;
import net.nikr.eve.jeveasset.data.settings.PriceHistoryDatabase;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.EditablePriceType;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDateChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.i18n.TabsPriceChanges;


public class PriceChangesTab extends JMainTabSecondary {

	private enum PriceChangesAction {
		PRICE_MODE,
		OWNED,
		RESET_DATES
	}

	//GUI
	private final JComboBox<PriceMode> jPriceMode;
	private final JDateChooser jFrom;
	private final JDateChooser jTo;
	private final JCheckBox jOwned;
	private final JAutoColumnTable jTable;

	//Table
	private final PriceChangeFilterControl filterControl;
	private final EnumTableFormatAdaptor<PriceChangesTableFormat, PriceChange> tableFormat;
	private final DefaultEventTableModel<PriceChange> tableModel;
	private final EventList<PriceChange> eventList;
	private final FilterList<PriceChange> filterList;
	private final DefaultEventSelectionModel<PriceChange> selectionModel;

	//Date
	private Map<Integer, Long> ownedTypeIDs;
	private NavigableSet<String> priceChangesDate = new TreeSet<>();

	public static final String NAME = "pricechange"; //Not to be changed!

	public PriceChangesTab(final Program program) {
		super(program, NAME, TabsPriceChanges.get().title(), Images.TOOL_PRICE_CHANGE.getIcon(), true);
		layout.setAutoCreateGaps(true);

		ListenerClass listener = new ListenerClass();

		JFixedToolBar jToolBar = new JFixedToolBar();

		jPriceMode = new JComboBox<>(PriceMode.values());
		jPriceMode.setSelectedItem(Settings.get().getPriceDataSettings().getPriceType());
		jPriceMode.setActionCommand(PriceChangesAction.PRICE_MODE.name());
		jPriceMode.addActionListener(listener);
		jPriceMode.setPrototypeDisplayValue(PriceMode.PRICE_BUY_PERCENTILE);
		jToolBar.addPreferedSize(jPriceMode);

		jToolBar.addSpace(5);
		jToolBar.addSeparator();
		jToolBar.addSpace(5);

		JLabel jFromLabel = new JLabel(TabsPriceChanges.get().from());
		jToolBar.add(jFromLabel);

		jToolBar.addSpace(5);

		jFrom = new JDateChooser(false);
		jFrom.getSettings().setVetoPolicy(new FromVetoPolicy());
		jFrom.addDateChangeListener(listener);
		jToolBar.addPreferedSize(jFrom);

		jToolBar.addSpace(5);
		jToolBar.addSeparator();
		jToolBar.addSpace(5);

		JLabel jToLabel = new JLabel(TabsPriceChanges.get().to());
		jToolBar.add(jToLabel);

		jToolBar.addSpace(5);

		jTo = new JDateChooser(false);
		jTo.getSettings().setVetoPolicy(new ToVetoPolicy());
		jTo.addDateChangeListener(listener);
		jToolBar.addPreferedSize(jTo);

		jToolBar.addSpace(5);
		jToolBar.addSeparator();
		jToolBar.addSpace(5);

		JButton jResetDates = new JButton(TabsPriceChanges.get().resetDates(), Images.FILTER_CLEAR.getIcon());
		jResetDates.setActionCommand(PriceChangesAction.RESET_DATES.name());
		jResetDates.addActionListener(listener);
		jToolBar.addButton(jResetDates);

		jToolBar.addSpace(5);
		jToolBar.addSeparator();
		jToolBar.addSpace(5);

		
		
		jOwned = new JCheckBox(TabsPriceChanges.get().owned(), true);
		jOwned.setActionCommand(PriceChangesAction.OWNED.name());
		jOwned.addActionListener(listener);
		jToolBar.addPreferedSize(jOwned);

		//Table Format
		tableFormat = TableFormatFactory.priceChangesTableFormat();
		//Backend
		eventList = EventListManager.create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<PriceChange> sortedList = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();

		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(sortedList);
		eventList.getReadWriteLock().readLock().unlock();
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new JAutoColumnTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		//Sorting
		TableComparatorChooser<PriceChange> comparatorChooser = TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(filterList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);

		//Listeners
		installTable(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new PriceChangeFilterControl(sortedList);
		//Menu
		installTableTool(new PriceChangeTableMenu(), tableFormat, comparatorChooser, tableModel, jTable, filterControl, PriceChange.class);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateData() {
		boolean setDefaults = ownedTypeIDs == null; //First run
		//Owned
		ownedTypeIDs = new HashMap<>();		
		for (MyAsset asset : program.getProfileData().getAssetsList()) {
			add(ownedTypeIDs, asset.getTypeID(), asset.getItemCount());
		}
		for (MyIndustryJob industryJob : program.getProfileData().getIndustryJobsList()) {
			if (industryJob.getItem().isMarketGroup() && industryJob.isNotDeliveredToAssets()) {
				add(ownedTypeIDs, industryJob.getTypeID(), 1L);
			}
			
		}
		//Price changes
		priceChangesDate = PriceHistoryDatabase.getPriceChangesDate(ownedTypeIDs.keySet());
		//Defaults
		if (setDefaults) {
			try {
				jFrom.setDate(getDate(priceChangesDate.first()));
			} catch (ParseException ex) {

			}
			try {
				jTo.setDate(getDate(priceChangesDate.last()));
			} catch (ParseException ex) {

			}
		}
		//Update table data
		updateTable();
	}

	private void updateTable() {
		PriceMode priceMode = jPriceMode.getItemAt(jPriceMode.getSelectedIndex());
		boolean ownedOnly = jOwned.isSelected();
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(PriceHistoryDatabase.getPriceChanges(ownedTypeIDs, ownedOnly, priceMode, getFromDate(), getToDate()));
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	@Override
	public void clearData() {
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	@Override
	public void updateCache() { }

	@Override
	public Collection<LocationType> getLocations() {
		return new ArrayList<>(); //No Location
	}

		private void add(Map<Integer, Long> priceTypeIDs, Integer typeID, Long count) {
		Long previous = priceTypeIDs.getOrDefault(typeID, 0L);
		priceTypeIDs.put(typeID, previous + count);
	}

	private Date getFromDate() {
		return getDate(jFrom.getDate());
	}

	private Date getToDate() {
		return getDate(jTo.getDate());
	}

	private String getDateString(Date date) {
		return PriceHistoryDatabase.DATE.format(date);
	}

	private static Date getDate(String date) throws ParseException {
		return PriceHistoryDatabase.DATE.parse(date);
	}

	private Date getDate(LocalDate date) {
		if (date == null) {
			return null;
		}
		Instant instant = date.atTime(12, 0, 0, 0).atZone(ZoneId.of("GMT")).toInstant(); //End of day - GMT
		return Date.from(instant);
	}

	private class PriceChangeTableMenu implements TableMenu<PriceChange> {
		@Override
		public MenuData<PriceChange> getMenuData() {
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
		public void addInfoMenu(JPopupMenu jPopupMenu) {
			//JMenuInfo.infoItem(jPopupMenu, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class ListenerClass implements ActionListener, DateChangeListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(PriceChangesAction.PRICE_MODE.name())) {
				updateTable();
			} else if (e.getActionCommand().equals(PriceChangesAction.OWNED.name())) {
				updateTable();
			} else if (e.getActionCommand().equals(PriceChangesAction.RESET_DATES.name())) {
				try {
					jFrom.setDate(PriceHistoryDatabase.DATE.parse(priceChangesDate.first()));
				} catch (ParseException ex) {
					
				}
				try {
					jTo.setDate(PriceHistoryDatabase.DATE.parse(priceChangesDate.last()));
				} catch (ParseException ex) {
					
				}
			}
		}

		@Override
		public void dateChanged(DateChangeEvent event) {
			updateTable();
		}
	}

	private class PriceChangeFilterControl extends FilterControl<PriceChange> {

		public PriceChangeFilterControl(EventList<PriceChange> exportEventList) {
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
			program.saveSettings("Price Change Table: " + msg); //Save Asset Filters and Export Settings
		}
	}

	public class FromVetoPolicy implements DateVetoPolicy {

		@Override
		public boolean isDateAllowed(LocalDate localDate) {
			Date date = getDate(localDate);
			return priceChangesDate.contains(getDateString(date))
					&& getToDate().after(date);
		}
	}

	public class ToVetoPolicy implements DateVetoPolicy {

		@Override
		public boolean isDateAllowed(LocalDate localDate) {
			Date date = getDate(localDate);
			return priceChangesDate.contains(getDateString(date))
					&& date.after(getFromDate());
		}
	}

	public static class PriceChange implements ItemType, EditablePriceType, Comparable<PriceChange> {

		private final int typeID;
		private final Item item;
		private final Long count;
		private double priceFrom;
		private double priceTo;
		

		public PriceChange(int typeID, Item item, Long count) {
			this.typeID = typeID;
			this.item = item;
			this.count = count;
		}

		public int getTypeID() {
			return typeID;
		}

		public Long getCount() {
			return count;
		}

		public double getPriceFrom() {
			return priceFrom;
		}

		public void setPriceFrom(double priceFrom) {
			this.priceFrom = priceFrom;
		}

		public double getPriceTo() {
			return priceTo;
		}

		public void setPriceTo(double priceTo) {
			this.priceTo = priceTo;
		}

		public double getChange() {
			return getPriceTo() - getPriceFrom();
		}

		public Percent getChangePercent() {
			if (getPriceFrom() > 0 && getPriceTo() > 0) {
				return Percent.create(getPriceTo() / getPriceFrom());
			} else if (getPriceFrom() == 0 && getPriceTo() == 0) {
				return Percent.create(0);
			} else if (getPriceFrom() == 0 && getPriceTo() > 0) {
				return Percent.create(1);
			} else {
				return null;
			}
		}

		public double getTotal() {
			return getPriceTo() * getItemCount() - getPriceFrom() * getItemCount();
		}

		@Override
		public Item getItem() {
			return item;
		}

		@Override
		public long getItemCount() {
			return count;
		}

		@Override
		public void setDynamicPrice(double price) { }

		@Override
		public boolean isBPC() {
			return false;
		}

		@Override
		public Double getDynamicPrice() {
			return getPriceFrom();
		}

		@Override
		public int compareTo(PriceChange o) {
			return item.getTypeName().compareTo(o.item.getTypeName());
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 53 * hash + Objects.hashCode(this.item.getTypeName());
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
			final PriceChange other = (PriceChange) obj;
			return Objects.equals(this.item.getTypeName(), other.item.getTypeName());
		}
	}
}
