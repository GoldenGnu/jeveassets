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

package net.nikr.eve.jeveasset.gui.tabs.loyalty;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyLoyaltyPoints;
import net.nikr.eve.jeveasset.data.settings.tag.TagUpdate;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabPrimary;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.i18n.TabsLoyaltyPoints;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;


public class LoyaltyPointsTab extends JMainTabPrimary implements TagUpdate {

	//GUI
	private final JAutoColumnTable jTable;

	//Table
	private final LoyaltyPointsFilterControl filterControl;
	private final EnumTableFormatAdaptor<LoyaltyPointsTableFormat, MyLoyaltyPoints> tableFormat;
	private final DefaultEventTableModel<MyLoyaltyPoints> tableModel;
	private final EventList<MyLoyaltyPoints> eventList;
	private final FilterList<MyLoyaltyPoints> filterList;
	private final DefaultEventSelectionModel<MyLoyaltyPoints> selectionModel;

	public static final String NAME = "loyaltypoints"; //Not to be changed!

	public LoyaltyPointsTab(final Program program) {
		super(program, NAME, TabsLoyaltyPoints.get().loyaltyPoints(), Images.TOOL_LOYALTY_POINTS.getIcon(), true);
		layout.setAutoCreateGaps(true);

		//Table Format
		tableFormat = TableFormatFactory.loyaltyPointsTableFormat();
		//Backend
		eventList = program.getProfileData().getLoyaltyPointsEventList();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<MyLoyaltyPoints> sortedList = new SortedList<>(eventList);
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
		jTable.setRowHeight(MyLoyaltyPoints.IMAGE_SIZE.getSize());
		PaddingTableCellRenderer.install(jTable, 0, 5, 0, 5);
		//Sorting
		TableComparatorChooser<MyLoyaltyPoints> comparatorChooser = TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(filterList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);

		//Listeners
		installTable(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new LoyaltyPointsFilterControl(sortedList);
		//Menu
		installTableTool(new LoyaltyPointsTableMenu(), tableFormat, comparatorChooser, tableModel, jTable, filterControl, MyLoyaltyPoints.class);

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
	public void updateTags() {
		beforeUpdateData();
		tableModel.fireTableDataChanged();
		filterControl.refilter();
		afterUpdateData();
	}

	@Override
	public void clearData() { }

	@Override
	public void updateCache() {
		filterControl.createCache();
	}

	@Override
	public Collection<LocationType> getLocations() {
		return new ArrayList<>(); //LocationsType
	}

	public boolean isFiltersEmpty() {
		return filterControl.isFiltersEmpty();
	}

	public void addFilters(final List<Filter> filters) {
		filterControl.addFilters(filters);
	}

	public void clearFilters() {
		filterControl.clearCurrentFilters();
	}

	public String getCurrentFilterName() {
		return filterControl.getCurrentFilterName();
	}

	private class LoyaltyPointsTableMenu implements TableMenu<MyLoyaltyPoints> {
		@Override
		public MenuData<MyLoyaltyPoints> getMenuData() {
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
		public void addInfoMenu(JPopupMenu jPopupMenu) { }

		@Override
		public void addToolMenu(JComponent jComponent) {
			JMenu fuzzwork = new JMenu(TabsLoyaltyPoints.get().fuzzworkLoyaltyPointsStore());
			fuzzwork.setIcon(Images.LINK_FUZZWORK.getIcon());
			jComponent.add(fuzzwork);

			boolean enabled = !selectionModel.getSelected().isEmpty();

			JMenuItem jSell = new JMenuItem(TabsLoyaltyPoints.get().sell(), Images.ORDERS_SELL.getIcon());
			jSell.setEnabled(enabled);
			jSell.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Set<String> links = new HashSet<>();
					for (MyLoyaltyPoints loyaltyPoints : selectionModel.getSelected()) {
						links.add("https://www.fuzzwork.co.uk/lpstore/sell/10000002/"+loyaltyPoints.getCorporationID());
						
					}
					DesktopUtil.browse(links, program);
					
				}
			});
			fuzzwork.add(jSell);

			JMenuItem jBuy = new JMenuItem(TabsLoyaltyPoints.get().buy(), Images.ORDERS_BUY.getIcon());
			jBuy.setEnabled(enabled);
			jBuy.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Set<String> links = new HashSet<>();
					for (MyLoyaltyPoints loyaltyPoints : selectionModel.getSelected()) {
						links.add("https://www.fuzzwork.co.uk/lpstore/buy/10000002/"+loyaltyPoints.getCorporationID());
						
					}
					DesktopUtil.browse(links, program);
					
				}
			});
			fuzzwork.add(jBuy);
		}
	}

	private class LoyaltyPointsFilterControl extends FilterControl<MyLoyaltyPoints> {

		public LoyaltyPointsFilterControl(EventList<MyLoyaltyPoints> exportEventList) {
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
			program.saveSettings("Loyalty Points Table: " + msg); //Save Loyalty Points Filters and Export Settings
		}
	}
}
