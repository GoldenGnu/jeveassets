/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.data.types.PriceType;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset;


public class MenuManager<Q> {

	public enum MenuEnum {
		ASSET_FILTER,
		STOCKPILE,
		LOOKUP,
		PRICE,
		NAME,
		REPROCESSED
	}

	private boolean priceSupported = false;
	private boolean itemSupported = false;
	private boolean locationSupported = false;
	private boolean assets = false;
	private boolean stockpile = false;

	private final Map<MenuEnum, JAutoMenu<Q>> mainMenu =  new EnumMap<MenuEnum, JAutoMenu<Q>>(MenuEnum.class);
	private final Map<MenuEnum, JAutoMenu<Q>> tablePopupMenu =  new EnumMap<MenuEnum, JAutoMenu<Q>>(MenuEnum.class);
	
	private final TableMenu<Q> tableMenu;
	private final JTable jTable;
	private final Program program;
	private static final Map<Class<?>, MenuManager<?>> MANAGERS = new HashMap<Class<?>, MenuManager<?>>();

	public static <Q> void install(final Program program, final TableMenu<Q> tableMenu, final JTable jTable, final Class<Q> clazz) {
		MenuManager<Q> menuManager = new MenuManager<Q>(program, tableMenu, jTable, clazz);
		MenuManager<?> put = MANAGERS.put(clazz, menuManager);
		if (put != null)  {
			throw new RuntimeException("Duplicated MenuManager Class");
		}
	}

	public static void update(final Program program, final Class<?> clazz) {
		MenuManager<?> menuManager = MANAGERS.get(clazz);
		if (menuManager != null) {
			menuManager.updateMainTableMenu();
		} else { //No table menu
			JMenu jMenu = program.getMainWindow().getMenu().getTableMenu();
			jMenu.removeAll();
			jMenu.setEnabled(false);
		}
	}
	
	private MenuManager(final Program program, final TableMenu<Q> tableMenu, final JTable jTable, final Class<Q> clazz) {
		this.program = program;
		this.tableMenu = tableMenu;
		this.jTable = jTable;
		assets = Asset.class.isAssignableFrom(clazz) && !TreeAsset.class.isAssignableFrom(clazz);
		stockpile = Stockpile.StockpileItem.class.isAssignableFrom(clazz);
		locationSupported = LocationType.class.isAssignableFrom(clazz);
		itemSupported = ItemType.class.isAssignableFrom(clazz);
		priceSupported = PriceType.class.isAssignableFrom(clazz) || Item.class.isAssignableFrom(clazz);
		createCashe(program, mainMenu);
		createCashe(program, tablePopupMenu);
		TableMenuListener listener  = new TableMenuListener();
		jTable.addMouseListener(listener);
		jTable.getTableHeader().addMouseListener(listener);
		jTable.getSelectionModel().addListSelectionListener(listener);
		jTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
		updateMainTableMenu();
	}

	public final void createCashe(final Program program, final Map<MenuEnum, JAutoMenu<Q>> menus) {
	//ASSET FILTER - OK
		if (!assets && (itemSupported || locationSupported)) {
			menus.put(MenuEnum.ASSET_FILTER, new JMenuAssetFilter<Q>(program));
		}
	//STOCKPILE (Add To) - OK
		if (!stockpile && itemSupported) {
			menus.put(MenuEnum.STOCKPILE, new JMenuStockpile<Q>(program));
		}
	//LOOKUP - OK
		if (itemSupported || locationSupported) {
			menus.put(MenuEnum.LOOKUP, new JMenuLookup<Q>(program));
		}
	//EDIT
		if (priceSupported) {
			menus.put(MenuEnum.PRICE, new JMenuPrice<Q>(program));
		}
		if (assets) {
			menus.put(MenuEnum.NAME, new JMenuName<Q>(program));
		}
	//REPROCESSED - OK
		if (itemSupported) {
			menus.put(MenuEnum.REPROCESSED, new JMenuReprocessed<Q>(program));
		}
	}

	private void createMenu(JPopupMenu jPopupMenu) {
		createMenu(jPopupMenu, tablePopupMenu);
	}

	public void createMenu(JMenu jMenu) {
		createMenu(jMenu, mainMenu);
	}

	private void createMenu(JComponent jComponent, Map<MenuEnum, JAutoMenu<Q>> menus) {
		jComponent.removeAll();
		boolean notEmpty = false;
	//UPDATE
		MenuData<Q> menuData = tableMenu.getMenuData();
		for (JAutoMenu<Q> jAutoMenu : menus.values()) {
			jAutoMenu.setMenuData(menuData);
		}
	//COPY
		if (jComponent instanceof JPopupMenu) {
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
			notEmpty = true;
		}
	//TOOL MENU
		tableMenu.addToolMenu(jComponent);
	//FILTER
		JMenu filterMenu = tableMenu.getFilterMenu();
		if (filterMenu != null) {
			jComponent.add(filterMenu);
			notEmpty = true;
		}
	//ASSET FILTER
		JAutoMenu<Q> jAssetFilter = menus.get(MenuEnum.ASSET_FILTER);
		if (jAssetFilter != null) {
			jComponent.add(jAssetFilter);
			notEmpty = true;
			if (jAssetFilter instanceof JMenuAssetFilter) {
				JMenuAssetFilter jMenuAssetFilter = (JMenuAssetFilter) jAssetFilter;
				jMenuAssetFilter.setTool(tableMenu);
			}
		}
	//STOCKPILE (Add To)
		JAutoMenu<Q> jStockpile = menus.get(MenuEnum.STOCKPILE);
		if (jStockpile != null) {
			jComponent.add(jStockpile);
			notEmpty = true;
		}
	//LOOKUP
		JAutoMenu<Q> jLookup = menus.get(MenuEnum.LOOKUP);
		if (jLookup != null) {
			jComponent.add(jLookup);
			notEmpty = true;
			if (jLookup instanceof JMenuLookup) {
				JMenuLookup jMenuLookup = (JMenuLookup) jLookup;
				jMenuLookup.setTool(tableMenu);
			}
		}
	//EDIT
		JAutoMenu<Q> jPrice = menus.get(MenuEnum.PRICE);
		if (jPrice != null) {
			jComponent.add(jPrice);
			notEmpty = true;
		}
		JAutoMenu<Q> jName = menus.get(MenuEnum.NAME);
		if (jName != null) {
			jComponent.add(jName);
			notEmpty = true;
		}
	//REPROCESSED
		JAutoMenu<Q> jReprocessed = menus.get(MenuEnum.REPROCESSED);
		if (jReprocessed != null) {
			jComponent.add(jReprocessed);
			notEmpty = true;
		}
	//COLUMNS
		JMenu columnMenu = tableMenu.getColumnMenu();
		if (columnMenu != null) {
			jComponent.add(columnMenu);
			notEmpty = true;
		}
	//INFO
		if (assets) {
			JMenuInfo.asset(jTable, menuData.getAssets());
		}
		tableMenu.addInfoMenu(jComponent);
		jComponent.setEnabled(notEmpty);
	}

	private void updateMainTableMenu() {
		createMenu(program.getMainWindow().getMenu().getTableMenu());
	}

	private void showTableHeaderPopupMenu(final MouseEvent e) {
		JPopupMenu jTableHeaderPopupMenu = new JPopupMenu();
		JMenu columnMenu = tableMenu.getColumnMenu();
		if (columnMenu != null) {
			for (Component component : columnMenu.getMenuComponents()) { //Clone!
				jTableHeaderPopupMenu.add(component);
			}
		}
		jTableHeaderPopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	private void showTablePopupMenu(final MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();

		selectClickedCell(e);

		//updateTableMenu(jTablePopupMenu);
		createMenu(jTablePopupMenu);

		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	protected void selectClickedCell(final MouseEvent e) {
		Object source = e.getSource();
		if (source instanceof JTable) {
			JTable jSelectTable = (JTable) source;

			//Rows
			boolean clickInRowsSelection = false;
			int[] selectedRows = jSelectTable.getSelectedRows();
			for (int i = 0; i < selectedRows.length; i++) {
				if (selectedRows[i] == jSelectTable.rowAtPoint(e.getPoint())) {
					clickInRowsSelection = true;
					break;
				}
			}

			//Column
			boolean clickInColumnsSelection = false;
			int[] selectedColumns = jSelectTable.getSelectedColumns();
			for (int i = 0; i < selectedColumns.length; i++) {
				if (selectedColumns[i] == jSelectTable.columnAtPoint(e.getPoint())) {
					clickInColumnsSelection = true;
					break;
				}
			}

			//Clicked outside selection, select clicked cell
			if (!clickInRowsSelection || !clickInColumnsSelection) {
				jSelectTable.setRowSelectionInterval(jSelectTable.rowAtPoint(e.getPoint()), jSelectTable.rowAtPoint(e.getPoint()));
				jSelectTable.setColumnSelectionInterval(jSelectTable.columnAtPoint(e.getPoint()), jSelectTable.columnAtPoint(e.getPoint()));
			}
		}
	}

	private class TableMenuListener implements MouseListener, ListSelectionListener {

		int[] selectedColumns;
		int[] selectedRows;

		@Override
		public void mouseClicked(final MouseEvent e) { }

		@Override
		public void mousePressed(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				if (e.getSource().equals(jTable)) {
					showTablePopupMenu(e);
				}
				if (jTable != null && e.getSource().equals(jTable.getTableHeader())) {
					showTableHeaderPopupMenu(e);
				}
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				if (e.getSource().equals(jTable)) {
					showTablePopupMenu(e);
				}
				if (jTable != null && e.getSource().equals(jTable.getTableHeader())) {
					showTableHeaderPopupMenu(e);
				}
			}
		}

		@Override
		public void mouseEntered(final MouseEvent e) { }

		@Override
		public void mouseExited(final MouseEvent e) { }
	
		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				if (selectedColumns == null || selectedRows == null || !Arrays.equals(selectedColumns, jTable.getSelectedColumns()) || !Arrays.equals(selectedRows, jTable.getSelectedRows())) {
					selectedColumns = jTable.getSelectedColumns();
					selectedRows = jTable.getSelectedRows();
					updateMainTableMenu();
				}
			}
		}
	}

	public static void addSeparator(final JComponent jComponent) {
		if (jComponent instanceof JMenu) {
			JMenu jMenu = (JMenu) jComponent;
			jMenu.addSeparator();
		}
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;
			jPopupMenu.addSeparator();
		}
		if (jComponent instanceof JDropDownButton) {
			JDropDownButton jDropDownButton = (JDropDownButton) jComponent;
			jDropDownButton.addSeparator();
		}
	}

	protected static interface AutoMenu<T> {
		public void setMenuData(MenuData<T> menuData);
	}

	protected abstract static class JAutoMenu<T> extends JMenu implements AutoMenu<T> {

		protected Program program;
		
		public JAutoMenu(final String s, final Program program) {
			super(s);
			this.program = program;
		}
	}

	public static interface TableMenu<T> {
		public MenuData<T> getMenuData();
		public JMenu getFilterMenu();
		public JMenu getColumnMenu();
		public void addInfoMenu(JComponent jComponent);
		public void addToolMenu(JComponent jComponent);
	}
}
