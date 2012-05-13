/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.assets;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterLogicalMatcher;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuEditItem;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuLookup;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuStockpile;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.tabs.assets.EveAssetTableFormat.LongInt;
import net.nikr.eve.jeveasset.i18n.TabsAssets;


public class AssetsTab extends JMainTab implements ListEventListener<Asset>{

	//GUI
	private JAssetTable jTable;
	private JLabel jValue;
	private JLabel jReprocessed;
	private JLabel jCount;
	private JLabel jAverage;
	private JLabel jVolume;

	//Table
	private EventTableModel<Asset> tableModel;
	private EventList<Asset> eventList;
	private FilterList<Asset> filterList;
	private AssetFilterControl filterControl;
	private EnumTableFormatAdaptor<EveAssetTableFormat, Asset> tableFormat;
	private EventSelectionModel<Asset> selectionModel;
	
	public static final String NAME = "assets"; //Not to be changed!
	
	public AssetsTab(Program program) {
		super(program, TabsAssets.get().assets(), Images.TOOL_ASSETS.getIcon(), false);
		layout.setAutoCreateGaps(true);

		eventList = program.getEveAssetEventList();
		tableFormat = new EnumTableFormatAdaptor<EveAssetTableFormat, Asset>(EveAssetTableFormat.class);
		tableFormat.setColumns(program.getSettings().getTableColumns().get(NAME));
		tableFormat.setResizeMode(program.getSettings().getTableResize().get(NAME));
		//For filtering the table
		filterList = new FilterList<Asset>(eventList);
		filterList.addListEventListener(this);
		//For soring the table
		SortedList<Asset> sortedList = new SortedList<Asset>(filterList);
		//Table Model
		tableModel = new EventTableModel<Asset>(sortedList, tableFormat);
		//Table
		jTable = new JAssetTable(program, tableModel);
		jTable.getTableHeader().setReorderingAllowed(true);
		jTable.getTableHeader().setResizingAllowed(true);
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		//install the sorting/filtering
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Table Selection
		selectionModel = new EventSelectionModel<Asset>(sortedList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTableMenu(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);

		jVolume = StatusPanel.createLabel(TabsAssets.get().totalVolume(), Images.ASSETS_VOLUME.getIcon());
		this.addStatusbarLabel(jVolume);

		jCount = StatusPanel.createLabel(TabsAssets.get().totalCount(), Images.EDIT_ADD.getIcon()); //Add
		this.addStatusbarLabel(jCount);

		jAverage = StatusPanel.createLabel(TabsAssets.get().average(), Images.ASSETS_AVERAGE.getIcon());
		this.addStatusbarLabel(jAverage);
		
		jReprocessed = StatusPanel.createLabel(TabsAssets.get().totalReprocessed(), Images.SETTINGS_REPROCESSING.getIcon());
		this.addStatusbarLabel(jReprocessed);

		jValue = StatusPanel.createLabel(TabsAssets.get().totalValue(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jValue);
		
		filterControl = new AssetFilterControl(
				program.getMainWindow().getFrame(),
				eventList,
				filterList,
				program.getSettings().getTableFilters(NAME)
				);

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
	public void updateSettings(){
		program.getSettings().getTableColumns().put(NAME, tableFormat.getColumns());
		program.getSettings().getTableResize().put(NAME, tableFormat.getResizeMode());
	}
	
	public boolean isFiltersEmpty(){
		return getFilters().isEmpty();
	}
	public void addFilter(Filter filter) {
		filterControl.addFilter(filter);
	}
	private List<Filter> getFilters(){
		return filterControl.getCurrentFilters();
	}
	public void clearFilters(){
		filterControl.clearCurrentFilters();
	}
	public FilterLogicalMatcher<Asset> getFilterLogicalMatcher(List<Filter> filters){
		return new FilterLogicalMatcher<Asset>(filterControl, filters);
	}
	public FilterLogicalMatcher<Asset> getFilterLogicalMatcher(){
		return new FilterLogicalMatcher<Asset>(filterControl, getFilters());
	}

	private void updateStatusbar(){
		float volume = 0;
		long count = 0;
		double average = 0;
		double totalReprocessed = 0;
		double total = 0;
		for (Asset asset : filterList){
			volume = volume + (asset.getVolume() * asset.getCount());
			count = count + asset.getCount();
			totalReprocessed = totalReprocessed + (asset.getPriceReprocessed() * asset.getCount());
			total = total + (asset.getPrice() * asset.getCount());
		}
		if (count > 0 && total > 0) average = total / count;
		jVolume.setText(Formater.doubleFormat(volume));
		jCount.setText(Formater.itemsFormat(count));
		jAverage.setText(Formater.iskFormat(average));
		jReprocessed.setText(Formater.iskFormat(totalReprocessed));
		jValue.setText(Formater.iskFormat(total));
	}

	public Asset getSelectedAsset(){
		return tableModel.getElementAt(jTable.getSelectedRow());
	}

	/**
	 * returns a new list of the filtered assets, thus the list is modifiable.
	 * @return a list of the filtered assets.
	 */
	public List<Asset> getFilteredAssets() {
		eventList.getReadWriteLock().writeLock().lock();
		List<Asset> ret = new ArrayList<Asset>(filterList);
		eventList.getReadWriteLock().writeLock().unlock();
		return ret;
	}

	@Override
	public void updateTableMenu(JComponent jComponent){
		jComponent.removeAll();
		jComponent.setEnabled(true);

		JMenuItem jMenuItem;
	//Logic
		int[] selectedRows = jTable.getSelectedRows();
		boolean isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);

	//COPY
		if (isSelected && jComponent instanceof JPopupMenu){
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
	//FILTER
		jComponent.add(filterControl.getMenu(jTable, selectionModel.getSelected()));
	//STOCKPILE
		jComponent.add(new JMenuStockpile<Asset>(program, selectionModel.getSelected()));
	//LOOKUP
		jComponent.add(new JMenuLookup<Asset>(program, selectionModel.getSelected()));
	//EDIT
		jComponent.add(new JMenuEditItem<Asset>(program, selectionModel.getSelected()));
	//COLUMNS
		jComponent.add(tableFormat.getMenu(program, tableModel, jTable));
	//INFO
		if (jComponent instanceof JPopupMenu){
			addSeparator(jComponent);

			jMenuItem = new JMenuItem(TabsAssets.get().selection());
			jMenuItem.setDisabledIcon(Images.DIALOG_ABOUT.getIcon());
			jMenuItem.setEnabled(false);
			jComponent.add(jMenuItem);

			JPanel jSpacePanel = new JPanel();
			jSpacePanel.setMinimumSize( new Dimension(50, 5) );
			jSpacePanel.setPreferredSize( new Dimension(50, 5) );
			jSpacePanel.setMaximumSize( new Dimension(50, 5) );
			jComponent.add(jSpacePanel);

			double total = 0;
			long count = 0;
			float volume = 0;
			for (int a = 0; a < selectedRows.length; a++){
				Asset eveAsset = tableModel.getElementAt(selectedRows[a]);
				total = total + (eveAsset.getPrice() * eveAsset.getCount());
				count = count + eveAsset.getCount();
				volume = volume + (eveAsset.getVolume() * eveAsset.getCount());
			}

			jMenuItem = new JMenuItem(Formater.iskFormat(total));
			jMenuItem.setDisabledIcon(Images.TOOL_VALUES.getIcon());
			jMenuItem.setEnabled(false);
			jMenuItem.setToolTipText(TabsAssets.get().value());
			jComponent.add(jMenuItem);

			jMenuItem = new JMenuItem( Formater.iskFormat(total/count) );
			jMenuItem.setDisabledIcon(Images.ASSETS_AVERAGE.getIcon());
			jMenuItem.setEnabled(false);
			jMenuItem.setToolTipText(TabsAssets.get().average1());
			jComponent.add(jMenuItem);

			jMenuItem = new JMenuItem( Formater.itemsFormat(count));
			jMenuItem.setDisabledIcon(Images.EDIT_ADD.getIcon());
			jMenuItem.setEnabled(false);
			jMenuItem.setToolTipText(TabsAssets.get().count());
			jComponent.add(jMenuItem);

			jMenuItem = new JMenuItem( Formater.doubleFormat(volume));
			jMenuItem.setDisabledIcon(Images.ASSETS_VOLUME.getIcon());
			jMenuItem.setEnabled(false);
			jMenuItem.setToolTipText(TabsAssets.get().volume());
			jComponent.add(jMenuItem);
		}
	}

	@Override
	public void updateData() {}

	@Override
	public void listChanged(ListEvent<Asset> listChanges) {
		updateStatusbar();
		program.getOverviewTab().updateTable();
	}
	
	public static class AssetFilterControl extends FilterControl<Asset>{

		public AssetFilterControl(JFrame jFrame, EventList<Asset> eventList, FilterList<Asset> filterList, Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, filterList, filters);
		}
		
		@Override
		protected Object getColumnValue(Asset item, String column) {
			EveAssetTableFormat format = EveAssetTableFormat.valueOf(column);
			if (format == EveAssetTableFormat.ITEM_ID){
				LongInt longInt = (LongInt)format.getColumnValue(item);
				return longInt.getNumber();
			} else {
				return format.getColumnValue(item);
			}
		}
		
		@Override
		protected boolean isNumericColumn(Enum column) {
			EveAssetTableFormat format = (EveAssetTableFormat) column;
			if (Number.class.isAssignableFrom(format.getType())) {
				return true;
			} else if (format == EveAssetTableFormat.ITEM_ID) {
				return true;
			} else if (format == EveAssetTableFormat.SECURITY) {
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		protected boolean isDateColumn(Enum column) {
			EveAssetTableFormat format = (EveAssetTableFormat) column;
			if (format.getType().getName().equals(Date.class.getName())) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Enum[] getColumns() {
			return EveAssetTableFormat.values();
		}
		
		@Override
		protected Enum valueOf(String column) {
			return EveAssetTableFormat.valueOf(column);
		}
		
		@Override
		protected List<EnumTableColumn<Asset>> getEnumColumns() {
			return columnsAsList(EveAssetTableFormat.values());
		}
	}
}
