/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.slots;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.SortableRenderer;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.JStatusLabel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.i18n.TabsSlots;


public class SlotsTab extends JMainTabSecondary {

	//GUI
	private final JAutoColumnTable jTable;
	private final JToggleButton jIcons;
	private final JToggleButton jText;
	private final JLabel jManufacturing;
	private final JStatusLabel jManufacturingDone;
	private final JStatusLabel jManufacturingFree;
	private final JStatusLabel jManufacturingActive;
	private final JStatusLabel jManufacturingMax;
	private final JLabel jResearch;
	private final JStatusLabel jResearchDone;
	private final JStatusLabel jResearchFree;
	private final JStatusLabel jResearchActive;
	private final JStatusLabel jResearchMax;
	private final JLabel jReactions;
	private final JStatusLabel jReactionsDone;
	private final JStatusLabel jReactionsFree;
	private final JStatusLabel jReactionsActive;
	private final JStatusLabel jReactionsMax;
	private final JLabel jMarketOrders;
	private final JStatusLabel jMarketOrdersFree;
	private final JStatusLabel jMarketOrdersActive;
	private final JStatusLabel jMarketOrdersMax;
	private final JLabel jContractCharacter;
	private final JStatusLabel jContractCharacterFree;
	private final JStatusLabel jContractCharacterActive;
	private final JStatusLabel jContractCharacterMax;
	private final JLabel jContractCorporation;
	private final JStatusLabel jContractCorporationFree;
	private final JStatusLabel jContractCorporationActive;
	private final JStatusLabel jContractCorporationMax;

	//Table
	private final SlotsFilterControl filterControl;
	private final DefaultEventTableModel<Slots> tableModel;
	private final EventList<Slots> eventList;
	private final FilterList<Slots> filterList;
	private final EnumTableFormatAdaptor<SlotsTableFormat, Slots> tableFormat;
	private final DefaultEventSelectionModel<Slots> selectionModel;
	private final IconHeaderRender iconHeaderRender;

	public static final String NAME = "industryslots"; //Not to be changed!

	private final SlotsData slotsData;

	public SlotsTab(final Program program) {
		super(program, NAME, TabsSlots.get().title(), Images.TOOL_SLOTS.getIcon(), true);

		ListenerClass listener = new ListenerClass();

		slotsData = new SlotsData(program);
		//Table Format
		tableFormat = TableFormatFactory.slotTableFormat();
		//Backend
		eventList = EventListManager.create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<Slots> columnSortedList = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Sorting Total
		eventList.getReadWriteLock().readLock().lock();
		SortedList<Slots> totalSortedList = new SortedList<>(columnSortedList, new TotalComparator());
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(totalSortedList);
		eventList.getReadWriteLock().readLock().unlock();
		filterList.addListEventListener(listener);
		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		jTable = new JSlotsTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		iconHeaderRender = new IconHeaderRender(jTable);
		jTable.getTableHeader().setDefaultRenderer(iconHeaderRender);
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
		filterControl = new SlotsFilterControl(totalSortedList);
		//Menu
		installTableTool(new SlotsTableMenu(), tableFormat, tableModel, jTable, filterControl, Slots.class);

		JFixedToolBar jToolBar = new JFixedToolBar();

		JLabel jTableHeaderLabel = new JLabel(TabsSlots.get().tableHeader());
		jToolBar.add(jTableHeaderLabel);

		jToolBar.addSpace(10);

		ButtonGroup buttonGroup = new ButtonGroup();

		jText = new JToggleButton(Images.SETTINGS_COLOR_FOREGROUND.getIcon());
		jText.setToolTipText(TabsSlots.get().tableHeaderText());
		jText.setSelected(true);
		jText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iconHeaderRender.setShowIcon(false);
				jTable.autoResizeColumns();
			}
		});
		jToolBar.addButtonIcon(jText);
		buttonGroup.add(jText);

		jIcons = new JToggleButton(Images.MISC_MANUFACTURING.getIcon());
		jIcons.setToolTipText(TabsSlots.get().tableHeaderIcon());
		jIcons.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iconHeaderRender.setShowIcon(true);
				jTable.autoResizeColumns();
			}
		});
		jToolBar.addButtonIcon(jIcons);
		buttonGroup.add(jIcons);

		jManufacturing = StatusPanel.createIcon(Images.MISC_MANUFACTURING.getIcon(), TabsSlots.get().manufacturing());
		this.addStatusbarLabel(jManufacturing);
		jManufacturingDone = StatusPanel.createLabel(TabsSlots.get().columnManufacturingDone(), Images.EDIT_SET.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jManufacturingDone);
		jManufacturingFree = StatusPanel.createLabel(TabsSlots.get().columnManufacturingFree(), Images.EDIT_ADD.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jManufacturingFree);
		jManufacturingActive = StatusPanel.createLabel(TabsSlots.get().columnManufacturingActive(), Images.UPDATE_WORKING.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jManufacturingActive);
		jManufacturingMax = StatusPanel.createLabel(TabsSlots.get().columnManufacturingDone(), Images.UPDATE_DONE_OK.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jManufacturingMax);

		jResearch = StatusPanel.createIcon(Images.MISC_INVENTION.getIcon(), TabsSlots.get().research());
		this.addStatusbarLabel(jResearch);
		jResearchDone = StatusPanel.createLabel(TabsSlots.get().columnResearchDone(), Images.EDIT_SET.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jResearchDone);
		jResearchFree = StatusPanel.createLabel(TabsSlots.get().columnResearchFree(), Images.EDIT_ADD.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jResearchFree);
		jResearchActive = StatusPanel.createLabel(TabsSlots.get().columnResearchActive(), Images.UPDATE_WORKING.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jResearchActive);
		jResearchMax = StatusPanel.createLabel(TabsSlots.get().columnResearchMax(), Images.UPDATE_DONE_OK.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jResearchMax);

		jReactions = StatusPanel.createIcon(Images.MISC_REACTION.getIcon(), TabsSlots.get().reactions());
		this.addStatusbarLabel(jReactions);
		jReactionsDone = StatusPanel.createLabel(TabsSlots.get().columnReactionsDone(), Images.EDIT_SET.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jReactionsDone);
		jReactionsFree = StatusPanel.createLabel(TabsSlots.get().columnReactionsFree(), Images.EDIT_ADD.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jReactionsFree);
		jReactionsActive = StatusPanel.createLabel(TabsSlots.get().columnReactionsActive(), Images.UPDATE_WORKING.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jReactionsActive);
		jReactionsMax = StatusPanel.createLabel(TabsSlots.get().columnReactionsMax(), Images.UPDATE_DONE_OK.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jReactionsMax);

		jMarketOrders = StatusPanel.createIcon(Images.MISC_MARKET_ORDERS.getIcon(), TabsSlots.get().marketOrders());
		this.addStatusbarLabel(jMarketOrders);
		jMarketOrdersFree = StatusPanel.createLabel(TabsSlots.get().columnMarketOrdersFree(), Images.EDIT_ADD.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jMarketOrdersFree);
		jMarketOrdersActive = StatusPanel.createLabel(TabsSlots.get().columnMarketOrdersActive(), Images.UPDATE_WORKING.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jMarketOrdersActive);
		jMarketOrdersMax = StatusPanel.createLabel(TabsSlots.get().columnMarketOrdersMax(), Images.UPDATE_DONE_OK.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jMarketOrdersMax);

		jContractCharacter = StatusPanel.createIcon(Images.MISC_CONTRACTS.getIcon(), TabsSlots.get().contractCharacter());
		this.addStatusbarLabel(jContractCharacter);
		jContractCharacterFree = StatusPanel.createLabel(TabsSlots.get().columnMarketOrdersFree(), Images.EDIT_ADD.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jContractCharacterFree);
		jContractCharacterActive = StatusPanel.createLabel(TabsSlots.get().columnMarketOrdersActive(), Images.UPDATE_WORKING.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jContractCharacterActive);
		jContractCharacterMax = StatusPanel.createLabel(TabsSlots.get().columnMarketOrdersMax(), Images.UPDATE_DONE_OK.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jContractCharacterMax);

		jContractCorporation = StatusPanel.createIcon(Images.MISC_CONTRACTS_CORP.getIcon(), TabsSlots.get().contractCorporation());
		this.addStatusbarLabel(jContractCorporation);
		jContractCorporationFree = StatusPanel.createLabel(TabsSlots.get().columnMarketOrdersFree(), Images.EDIT_ADD.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jContractCorporationFree);
		jContractCorporationActive = StatusPanel.createLabel(TabsSlots.get().columnMarketOrdersActive(), Images.UPDATE_WORKING.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jContractCorporationActive);
		jContractCorporationMax = StatusPanel.createLabel(TabsSlots.get().columnMarketOrdersMax(), Images.UPDATE_DONE_OK.getIcon(), AutoNumberFormat.LONG);
		this.addStatusbarLabel(jContractCorporationMax);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(jToolBar, jToolBar.getMinimumSize().width, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(filterControl.getPanel())
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
		//Update Data
		slotsData.updateData(eventList);
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

	public EventList<Slots> getEventList() {
		return eventList;
	}

	private class SlotsTableMenu implements TableMenu<Slots> {
		@Override
		public MenuData<Slots> getMenuData() {
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
			JMenuInfo.slots(jPopupMenu, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class ListenerClass implements ListEventListener<Slots> {
		@Override
		public void listChanged(final ListEvent<Slots> listChanges) {
			Slots total = new Slots("");
			try {
				filterList.getReadWriteLock().readLock().lock();
				for (Slots slots : filterList) {
					if (slots.isGrandTotal()) {
						continue;
					}
					total.count(slots);
				}
			} finally {
				filterList.getReadWriteLock().readLock().unlock();
			}
			jManufacturingDone.setNumber(total.getManufacturingDone());
			jManufacturingFree.setNumber(total.getManufacturingFree());
			jManufacturingActive.setNumber(total.getManufacturingActive());
			jManufacturingMax.setNumber(total.getManufacturingMax());
			jReactionsDone.setNumber(total.getReactionsDone());
			jReactionsFree.setNumber(total.getReactionsFree());
			jReactionsActive.setNumber(total.getReactionsActive());
			jReactionsMax.setNumber(total.getReactionsMax());
			jResearchDone.setNumber(total.getResearchDone());
			jResearchFree.setNumber(total.getResearchFree());
			jResearchActive.setNumber(total.getResearchActive());
			jResearchMax.setNumber(total.getResearchMax());
			jMarketOrdersFree.setNumber(total.getMarketOrdersFree());
			jMarketOrdersActive.setNumber(total.getMarketOrdersActive());
			jMarketOrdersMax.setNumber(total.getMarketOrdersMax());
			jContractCharacterFree.setNumber(total.getContractCharacterFree());
			jContractCharacterActive.setNumber(total.getContractCharacterActive());
			jContractCharacterMax.setNumber(total.getContractCharacterMax());
			jContractCorporationFree.setNumber(total.getContractCorporationFree());
			jContractCorporationActive.setNumber(total.getContractCorporationActive());
			jContractCorporationMax.setNumber(total.getContractCorporationMax());
		}
	}

	private class SlotsFilterControl extends FilterControl<Slots> {

		public SlotsFilterControl(EventList<Slots> exportEventList) {
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
			program.saveSettings("ISK Table: " + msg); //Save ISK Filters and Export Settings
		}
	}

	public static class TotalComparator implements Comparator<Slots> {
		@Override
		public int compare(final Slots o1, final Slots o2) {
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

	public static class IconHeaderRender implements TableCellRenderer, SortableRenderer {

		private static final Map<Icons, Icon> icons = new HashMap<>();
		private static final String MANUFACTURING = "Manufacturing";
		private static final String RESEARCH = "Research";
		private static final String REACTIONS = "Reactions";
		private static final String MARKET_ORDERS = "Market Orders";
		private static final String CONTRACTS_CHARACTER  = "Character Contracts";
		private static final String CONTRACTS_CORPORATION = "Corporation Contracts";
		private boolean showIcon = false;
		private TableCellRenderer delegateRenderer;
		private Icon sortIcon;

		public IconHeaderRender(JTable jTable) {
			this.delegateRenderer = jTable.getTableHeader().getDefaultRenderer();
		}

		public TableCellRenderer getDelegateRenderer() {
			return delegateRenderer;
		}

		public void setShowIcon(boolean showIcon) {
			this.showIcon = showIcon;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component rendered = getDelegateTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (rendered instanceof JLabel) {
				final JLabel jLabel = (JLabel) rendered;
				jLabel.setHorizontalTextPosition(SwingConstants.LEADING);
				if (showIcon) {
					String text = jLabel.getText();
					Icon icon = null;
					if (text.startsWith(MANUFACTURING)) {
						icon = Images.SLOTS_MANUFACTURING.getIcon();
						jLabel.setText(text.replace(MANUFACTURING, "").trim());
					} else if (text.startsWith(RESEARCH)) {
						icon = Images.SLOTS_RESEARCH.getIcon();
						jLabel.setText(text.replace(RESEARCH, "").trim());
					} else if (text.startsWith(REACTIONS)) {
						icon = Images.SLOTS_REACTIONS.getIcon();
						jLabel.setText(text.replace(REACTIONS, "").trim());
					} else if (text.startsWith(MARKET_ORDERS)) {
						icon = Images.SLOTS_MARKET_ORDERS.getIcon();
						jLabel.setText(text.replace(MARKET_ORDERS, "").trim());
					} else if (text.contains(CONTRACTS_CHARACTER)) {
						icon = Images.SLOTS_CONTRACTS.getIcon();
						jLabel.setText(text.replace(CONTRACTS_CHARACTER + " ", "").trim());
					} else if (text.contains(CONTRACTS_CORPORATION)) {
						icon = Images.SLOTS_CONTRACTS_CORP.getIcon();
						jLabel.setText(text.replace(CONTRACTS_CORPORATION + " ", "").trim());
					}
					if (sortIcon != null && icon != null) {
						jLabel.setIcon(getIcon(icon, sortIcon));
					} else if (sortIcon != null) {
						jLabel.setIcon(sortIcon);
					} else if (icon != null) {
						jLabel.setIcon(icon);
					} else {
						jLabel.setIcon(null);
					}
				} else {
					jLabel.setIcon(sortIcon);
				}
			}
			return rendered;
		}

		@Override
		public void setSortIcon(Icon sortIcon) {
			this.sortIcon = sortIcon;
		}

		private Component getDelegateTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			try {
				return delegateRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			} catch (RuntimeException e) {
				delegateRenderer = new DefaultTableCellRenderer();
				return delegateRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		}

		private Icon getIcon(Icon icon1, Icon icon2) {
			Icons key = new Icons(icon1, icon2);
			Icon value = icons.get(key);
			if (value != null) {
				return value;
			}
			value = createIcon(icon1, icon2);
			icons.put(key, value);
			return value;
		}

		private Icon createIcon(Icon icon1, Icon icon2) {
			int w = icon1.getIconWidth() + icon2.getIconWidth() + 3;
			int h = Math.max(icon1.getIconHeight(), icon2.getIconHeight());
			int y1 = 0;
			int y2 = 0;
			if (icon1.getIconHeight() > icon2.getIconHeight()) {
				y2 = (int) Math.ceil((h - icon2.getIconHeight()) / 2.0);
			} else if (icon2.getIconHeight() > icon1.getIconHeight()) {
				y1 = (int) Math.ceil((h - icon1.getIconHeight()) / 2.0);
			}
			Image image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D) image.getGraphics();
			icon1.paintIcon(null, g2, 0, y1);
			icon2.paintIcon(null, g2, icon1.getIconWidth() + 3, y2);
			g2.dispose();
			return new ImageIcon(image);
		}

		private static class Icons {
			private final Icon icon1;
			private final Icon icon2;

			public Icons(Icon icon1, Icon icon2) {
				this.icon1 = icon1;
				this.icon2 = icon2;
			}

			@Override
			public int hashCode() {
				int hash = 7;
				hash = 83 * hash + Objects.hashCode(this.icon1);
				hash = 83 * hash + Objects.hashCode(this.icon2);
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
				final Icons other = (Icons) obj;
				if (!Objects.equals(this.icon1, other.icon1)) {
					return false;
				}
				if (!Objects.equals(this.icon2, other.icon2)) {
					return false;
				}
				return true;
			}
		}
	}

}
