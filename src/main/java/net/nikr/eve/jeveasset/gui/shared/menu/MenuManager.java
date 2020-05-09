/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
import java.awt.Point;
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
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.data.settings.types.JumpType;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.data.settings.types.LocationsType;
import net.nikr.eve.jeveasset.data.settings.types.PriceType;
import net.nikr.eve.jeveasset.data.settings.types.TagsType;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset;


public class MenuManager<Q> {

	public enum MenuEnum {
		ASSET_FILTER,
		TAGS,
		STOCKPILE,
		LOOKUP,
		LOCATION,
		UI,
		PRICE,
		NAME,
		REPROCESSED,
		JUMPS,
		ROUTING,
		COPY_PLUS,
		SUM,
	}

	private boolean priceSupported = false;
	private boolean itemSupported = false;
	private boolean locationSupported = false;
	private boolean jumpsSupported = false;
	private boolean assets = false;
	private boolean stockpile = false;
	private boolean tagsSupported = false;

	private final Map<MenuEnum, AutoMenu<Q>> mainMenu =  new EnumMap<>(MenuEnum.class);
	private final Map<MenuEnum, AutoMenu<Q>> tablePopupMenu =  new EnumMap<>(MenuEnum.class);
	
	private final TableMenu<Q> tableMenu;
	private final JTable jTable;
	private final Program program;
	private static final Map<Class<?>, MenuManager<?>> MANAGERS = new HashMap<Class<?>, MenuManager<?>>();

	public static <Q> void install(final Program program, final TableMenu<Q> tableMenu, final JTable jTable, final Class<Q> clazz) {
		MenuManager<Q> menuManager = new MenuManager<>(program, tableMenu, jTable, clazz);
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
		assets = MyAsset.class.isAssignableFrom(clazz) && !TreeAsset.class.isAssignableFrom(clazz);
		stockpile = Stockpile.StockpileItem.class.isAssignableFrom(clazz);
		locationSupported = LocationType.class.isAssignableFrom(clazz) || LocationsType.class.isAssignableFrom(clazz);
		jumpsSupported = JumpType.class.isAssignableFrom(clazz);
		itemSupported = ItemType.class.isAssignableFrom(clazz);
		tagsSupported = TagsType.class.isAssignableFrom(clazz);
		priceSupported = PriceType.class.isAssignableFrom(clazz) || Item.class.isAssignableFrom(clazz);
		createCashe(program, mainMenu, clazz);
		createCashe(program, tablePopupMenu, clazz);
		ListenerClass listener  = new ListenerClass();
		jTable.addMouseListener(listener);
		jTable.getTableHeader().addMouseListener(listener);
		jTable.getSelectionModel().addListSelectionListener(listener);
		jTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
		updateMainTableMenu();
	}

	public final void createCashe(final Program program, final Map<MenuEnum, AutoMenu<Q>> menus, final Class<Q> clazz) {
	//COPY SIMPLE
		if (itemSupported) {
			menus.put(MenuEnum.COPY_PLUS, new JMenuCopyPlus<>(program));
		}
	//ASSET FILTER
		if (!assets && (itemSupported || locationSupported)) {
			menus.put(MenuEnum.ASSET_FILTER, new JMenuAssetFilter<>(program));
		}
	//STOCKPILE (Add To)
		if (!stockpile && itemSupported) {
			menus.put(MenuEnum.STOCKPILE, new JMenuStockpile<>(program));
		}
	//LOOKUP
		if (itemSupported || locationSupported) {
			menus.put(MenuEnum.LOOKUP, new JMenuLookup<>(program));
		}
	//EDIT
		if (priceSupported) {
			menus.put(MenuEnum.PRICE, new JMenuPrice<>(program));
		}
		if (assets) {
			menus.put(MenuEnum.NAME, new JMenuName<>(program));
		}
		if (tagsSupported) {
			menus.put(MenuEnum.TAGS, new JMenuTags<>(program));
		}
	//REPROCESSED
		if (itemSupported) {
			menus.put(MenuEnum.REPROCESSED, new JMenuReprocessed<>(program));
		}
	//JUMPS
		if (jumpsSupported) {
			menus.put(MenuEnum.JUMPS, new JMenuJumps<>(program, clazz));
		}
		if (locationSupported) {
			menus.put(MenuEnum.ROUTING, new JMenuRouting<>(program));
		}
	//LOCATION
		if (locationSupported) {
			menus.put(MenuEnum.LOCATION, new JMenuLocation<>(program));
		}
		if (locationSupported || priceSupported || itemSupported) {
			menus.put(MenuEnum.UI, new JMenuUI<>(program));
		}
	//SUM
		menus.put(MenuEnum.SUM, new JMenuSum<>(program, jTable));
	
	}

	private void createMenu(JPopupMenu jPopupMenu) {
		createMenu(jPopupMenu, tablePopupMenu);
	}

	public void createMenu(JMenu jMenu) {
		createMenu(jMenu, mainMenu);
	}

	private void createMenu(JComponent jComponent, Map<MenuEnum, AutoMenu<Q>> menus) {
		jComponent.removeAll();
		boolean notEmpty = false;
	//UPDATE
		MenuData<Q> menuData = tableMenu.getMenuData();
		for (AutoMenu<Q> jAutoMenu : menus.values()) {
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
	//COPY+
		AutoMenu<Q> jcopyPlus = menus.get(MenuEnum.COPY_PLUS);
		if (jcopyPlus != null) {
			jComponent.add(jcopyPlus.getComponent());
			notEmpty = true;
		}
	//FILTER
		JMenu filterMenu = tableMenu.getFilterMenu();
		if (filterMenu != null) {
			jComponent.add(filterMenu);
			notEmpty = true;
		}
	//ASSET FILTER
		AutoMenu<Q> jAssetFilter = menus.get(MenuEnum.ASSET_FILTER);
		if (jAssetFilter != null) {
			jComponent.add(jAssetFilter.getComponent());
			notEmpty = true;
			if (jAssetFilter instanceof JMenuAssetFilter) {
				JMenuAssetFilter<?> jMenuAssetFilter = (JMenuAssetFilter) jAssetFilter;
				jMenuAssetFilter.setTool(tableMenu);
			}
		}
	//STOCKPILE (Add To)
		AutoMenu<Q> jStockpile = menus.get(MenuEnum.STOCKPILE);
		if (jStockpile != null) {
			jComponent.add(jStockpile.getComponent());
			notEmpty = true;
		}
	//LOOKUP
		AutoMenu<Q> jLookup = menus.get(MenuEnum.LOOKUP);
		if (jLookup != null) {
			jComponent.add(jLookup.getComponent());
			notEmpty = true;
			if (jLookup instanceof JMenuLookup) {
				JMenuLookup<?> jMenuLookup = (JMenuLookup) jLookup;
				jMenuLookup.setTool(tableMenu);
			}
		}
	//EDIT
		AutoMenu<Q> jPrice = menus.get(MenuEnum.PRICE);
		if (jPrice != null) {
			jComponent.add(jPrice.getComponent());
			notEmpty = true;
		}
		AutoMenu<Q> jName = menus.get(MenuEnum.NAME);
		if (jName != null) {
			jComponent.add(jName.getComponent());
			notEmpty = true;
		}
		AutoMenu<Q> jTags = menus.get(MenuEnum.TAGS);
		if (jTags != null) {
			jComponent.add(jTags.getComponent());
			notEmpty = true;
		}
	//REPROCESSED
		AutoMenu<Q> jReprocessed = menus.get(MenuEnum.REPROCESSED);
		if (jReprocessed != null) {
			jComponent.add(jReprocessed.getComponent());
			notEmpty = true;
		}
	//JUMPS
		AutoMenu<Q> jJumps = menus.get(MenuEnum.JUMPS);
		if (jJumps != null) {
			jComponent.add(jJumps.getComponent());
			notEmpty = true;
		}
		AutoMenu<Q> jRouting = menus.get(MenuEnum.ROUTING);
		if (jRouting != null) {
			jComponent.add(jRouting.getComponent());
			notEmpty = true;
		}
	//LOCATION
		AutoMenu<Q> jLocation = menus.get(MenuEnum.LOCATION);
		if (jLocation != null) {
			jComponent.add(jLocation.getComponent());
			notEmpty = true;
		}
		AutoMenu<Q> jUi = menus.get(MenuEnum.UI);
		if (jUi != null) {
			jComponent.add(jUi.getComponent());
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
	//SUM
		AutoMenu<Q> jSum = menus.get(MenuEnum.SUM);
		if (jSum != null) {
			jComponent.add(jSum.getComponent());
			notEmpty = true;
		}
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
		//SUM
		jTableHeaderPopupMenu.addSeparator();
		AutoMenu<Q> jSum = tablePopupMenu.get(MenuEnum.SUM);
		if (jSum instanceof JMenuSum) {
			((JMenuSum)jSum).updateMenuDataColumn(jTable.columnAtPoint(e.getPoint()));
			jTableHeaderPopupMenu.add(jSum.getComponent());
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

	private void selectClickedCell(final MouseEvent e) {
		Object source = e.getSource();
		if (source instanceof JTable) {
			JTable jSelectTable = (JTable) source;

			Point point = e.getPoint();
			if (!jSelectTable.getVisibleRect().contains(point)) { //Ignore clickes outside table
				return;
			}

			int clickedRow = jSelectTable.rowAtPoint(point);
			int clickedColumn = jSelectTable.columnAtPoint(point);

			//Rows
			boolean clickInRowsSelection;
			if (jSelectTable.getRowSelectionAllowed()) { //clicked in selected rows?
				clickInRowsSelection = false;
				int[] selectedRows = jSelectTable.getSelectedRows();
				for (int i = 0; i < selectedRows.length; i++) {
					if (selectedRows[i] == clickedRow) {
						clickInRowsSelection = true;
						break;
					}
				}
			} else { //Row selection not allowed - all rows selected
				clickInRowsSelection = true;
			}

			//Column
			boolean clickInColumnsSelection;
			if (jSelectTable.getColumnSelectionAllowed()) { //clicked in selected columns?
				clickInColumnsSelection = false;
				int[] selectedColumns = jSelectTable.getSelectedColumns();
				for (int i = 0; i < selectedColumns.length; i++) {
					if (selectedColumns[i] == clickedColumn) {
						clickInColumnsSelection = true;
						break;
					}
				}
			} else { //Column selection not allowed - all columns selected
				clickInColumnsSelection = true;
			}

			//Clicked outside selection, select clicked cell
			if ( (!clickInRowsSelection || !clickInColumnsSelection) && clickedRow >= 0 && clickedColumn >= 0) {
				jSelectTable.setRowSelectionInterval(clickedRow, clickedRow);
				jSelectTable.setColumnSelectionInterval(clickedColumn, clickedColumn);
			}
		}
	}

	private class ListenerClass implements MouseListener, ListSelectionListener {

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
		public JComponent getComponent();
	}

	protected abstract static class JAutoMenu<T> extends JMenu implements AutoMenu<T> {

		protected Program program;
		protected MenuData<T> menuData;

		public JAutoMenu(final String s, final Program program) {
			super(s);
			this.program = program;
		}

		@Override
		public final void setMenuData(MenuData<T> menuData) {
			this.menuData = menuData;
			updateMenuData();
		}

		@Override
		public JComponent getComponent() {
			return this;
		}

		protected abstract void updateMenuData();
	}

	protected abstract static class JAutoMenuComponent<T> implements AutoMenu<T> {

		protected Program program;
		protected MenuData<T> menuData;

		public JAutoMenuComponent(final Program program) {
			this.program = program;
		}

		@Override
		public final void setMenuData(MenuData<T> menuData) {
			this.menuData = menuData;
			updateMenuData();
		}
	
		@Override
		public abstract JComponent getComponent();

		protected abstract void updateMenuData();
	}

	public static interface TableMenu<T> {
		public MenuData<T> getMenuData();
		public JMenu getFilterMenu();
		public JMenu getColumnMenu();
		public void addInfoMenu(JComponent jComponent);
		public void addToolMenu(JComponent jComponent);
	}
}
