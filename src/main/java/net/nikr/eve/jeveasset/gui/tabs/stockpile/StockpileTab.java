/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.ItemFlag;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JMenuAssetFilter;
import net.nikr.eve.jeveasset.gui.shared.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.JMenuEditItem;
import net.nikr.eve.jeveasset.gui.shared.JMenuLookup;
import net.nikr.eve.jeveasset.gui.shared.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StockpileTab extends JMainTab implements ActionListener {

	private final static String ACTION_ADD = "ACTION_ADD";
	private final static String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private final static String ACTION_EXPAND = "ACTION_EXPAND";
	private final static String ACTION_EDIT_ITEM = "ACTION_EDIT_ITEM";
	private final static String ACTION_DELETE_ITEM = "ACTION_DELETE_ITEM";
	
	private JButton jAdd;
	private JSeparatorTable jTable;
	private JButton jExpand;
	private JButton jCollapse;
	private JLabel jVolumeNow;
	private JLabel jVolumeNeeded;
	private JLabel jValueNow;
	private JLabel jValueNeeded;
	
	private EventTableModel<StockpileItem> stockpileTableModel;
	private EventList<StockpileItem> stockpileEventList;
	private SeparatorList<StockpileItem> separatorList;
	
	private StockpileDialog stockpileDialog;
	private StockpileItemDialog stockpileItemDialog;
	
	public StockpileTab(Program program) {
		super(program, TabsStockpile.get().stockpile(), Images.TOOL_STOCKPILE.getIcon(), true);
		
		stockpileDialog = new StockpileDialog(program);
		stockpileItemDialog = new StockpileItemDialog(program);
		
		jAdd = new JButton(TabsStockpile.get().newStockpile());
		jAdd.setActionCommand(ACTION_ADD);
		jAdd.addActionListener(this);
		
		jCollapse = new JButton(TabsStockpile.get().collapse());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(this);

		jExpand = new JButton(TabsStockpile.get().expand());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(this);
		
		EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem> stockpileTableFormat = new EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem>(StockpileTableFormat.class);
		stockpileEventList = new BasicEventList<StockpileItem>();
		separatorList = new SeparatorList<StockpileItem>(stockpileEventList, new StockpileSeparatorComparator(), 1, Integer.MAX_VALUE);
		stockpileTableModel = new EventTableModel<StockpileItem>(separatorList, stockpileTableFormat);
		//Tables
		jTable = new JStockpileTable(program, stockpileTableModel);
		jTable.setSeparatorRenderer(new StockpileSeparatorTableCell(program, jTable, separatorList, this));
		jTable.setSeparatorEditor(new StockpileSeparatorTableCell(program, jTable, separatorList, this));
		jTable.getTableHeader().setReorderingAllowed(true);
		PaddingTableCellRenderer.install(jTable, 3);
		//Listeners
		installTableMenu(jTable);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Selection Model
		EventSelectionModel<StockpileItem> selectionModel = new EventSelectionModel<StockpileItem>(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jAdd, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCollapse, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExpand, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jAdd, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCollapse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		
		jVolumeNow = StatusPanel.createLabel(TabsStockpile.get().shownVolumeNow(), Images.ASSETS_VOLUME.getIcon());
		this.addStatusbarLabel(jVolumeNow);
		
		jVolumeNeeded = StatusPanel.createLabel(TabsStockpile.get().shownVolumeNeeded(), Images.ASSETS_VOLUME.getIcon());
		this.addStatusbarLabel(jVolumeNeeded);
		
		jValueNow = StatusPanel.createLabel(TabsStockpile.get().shownValueNow(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jValueNow);
		
		jValueNeeded = StatusPanel.createLabel(TabsStockpile.get().shownValueNeeded(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jValueNeeded);
	}
	
	public boolean showAddItem(Stockpile stockpile, int typeID) {
		boolean updated = stockpileItemDialog.showAdd(stockpile, typeID);
		updateData();
		if (updated && program.getSettings().isStockpileFocusTab()) scrollToSctockpile(stockpile);
		return updated;
	}
	public Stockpile showAddStockpile(Asset asset) {
		Stockpile stockpile = stockpileDialog.showAdd(asset);
		updateData();
		if (stockpile != null && program.getSettings().isStockpileFocusTab()) scrollToSctockpile(stockpile);
		return stockpile;
	}
	public Stockpile showAddStockpile(long locationID) {
		Stockpile stockpile = stockpileDialog.showAdd(locationID);
		updateData();
		if (stockpile != null && program.getSettings().isStockpileFocusTab()) scrollToSctockpile(stockpile);
		return stockpile;
	}
	
	public void scrollToSctockpile(final Stockpile stockpile){
		StockpileItem item = stockpile.getItems().get(0);
		int row = separatorList.indexOf(item) - 1;
		if (row < 0){ //Collapsed: Expand and run again...
			for (int i = 0; i < separatorList.size(); i++){
				Object object = separatorList.get(i);
				if (object instanceof SeparatorList.Separator){
					SeparatorList.Separator separator = (SeparatorList.Separator) object;
					if (separator.first().equals(item)){
						separatorList.getReadWriteLock().writeLock().lock();
						try {
							separator.setLimit(Integer.MAX_VALUE);
						} finally {
							separatorList.getReadWriteLock().writeLock().unlock();
						}
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								scrollToSctockpile(stockpile);
							}
						});
						
					}
				}
			}
		} else { //Expanded
			Rectangle rect = jTable.getCellRect(row, 0, true);
			rect.setSize(jTable.getVisibleRect().getSize());
			jTable.scrollRectToVisible(rect);
		}
	}

	@Override
	protected void showTablePopupMenu(MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();
		jTable.setRowSelectionInterval(jTable.rowAtPoint(e.getPoint()), jTable.rowAtPoint(e.getPoint()));
		jTable.setColumnSelectionInterval(0, jTable.getColumnCount()-1);

		updateTableMenu(jTablePopupMenu);

		if (jTable.getSelectedRows().length == 1){
			Object o = stockpileTableModel.getElementAt(jTable.getSelectedRow());
			if (o instanceof StockpileItem){
				jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	@Override
	public void updateTableMenu(JComponent jComponent){
		jComponent.removeAll();
		jComponent.setEnabled(true);

		boolean isSingleRow = jTable.getSelectedRows().length == 1;
		boolean isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);

		Object obj = isSingleRow ? (Object) stockpileTableModel.getElementAt(jTable.getSelectedRow()) : null;
	//COPY
		if (isSelected && jComponent instanceof JPopupMenu){
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
		jComponent.add(new JMenuAssetFilter(program, obj));
		
		JMenu jMenu;
		JMenuItem jMenuItem;
		
		jMenu = new JMenu(TabsStockpile.get().stockpile());
		jMenu.setIcon(Images.TOOL_STOCKPILE.getIcon());
		jComponent.add(jMenu);
		
		if (obj instanceof StockpileItem){
			StockpileItem item = (StockpileItem) obj;
			jMenuItem = new JStockpileMenuItem(TabsStockpile.get().editItem(), item);
			jMenuItem.setActionCommand(ACTION_EDIT_ITEM);
			jMenuItem.addActionListener(this);
			jMenu.add(jMenuItem);

			jMenuItem = new JStockpileMenuItem(TabsStockpile.get().deleteItem(), item);
			jMenuItem.setActionCommand(ACTION_DELETE_ITEM);
			jMenuItem.addActionListener(this);
			jMenu.add(jMenuItem);
		}
		
		
		jComponent.add(new JMenuLookup(program, obj));
		jComponent.add(new JMenuEditItem(program, obj));
	}

	@Override
	public void updateData() {
		//Items
		List<StockpileItem> stockpileItems = new ArrayList<StockpileItem>();
		//Characters Look-Up
		Map<String, Long> chars = new HashMap<String, Long>();
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				chars.put(human.getName(), human.getCharacterID());
			}
		}
		//ItemFlag Look-Up
		Map<String, ItemFlag> flags = new HashMap<String, ItemFlag>();
		for (ItemFlag itemFlag : program.getSettings().getItemFlags().values()){
			flags.put(itemFlag.getFlagName(), itemFlag);
		}
		//Regions Look-Up
		Map<String, Long> regions = new HashMap<String, Long>();
		for (Location location : program.getSettings().getLocations().values()){
			if (location.isRegion()){
				regions.put(location.getName(), location.getLocationID());
			}
		}
		double volumnNow = 0;
		double volumnNeeded = 0;
		double valueNow = 0;
		double valueNeeded = 0;
		
		for (Stockpile stockpile : program.getSettings().getStockpiles()){
			stockpileItems.addAll(stockpile.getItems());
			stockpile.reset();
			if (!stockpile.isEmpty()){
				for (StockpileItem item : stockpile.getItems()){
					if (item instanceof Stockpile.StockpileTotal) continue;
					int typeID = item.getTypeID();
					double price = program.getSettings().getPrice(typeID, false);
					float volume = program.getSettings().getVolume(typeID, true);
					boolean marketGroup = ApiIdConverter.marketGroup(typeID, program.getSettings().getItems());
					item.updateValues(price, volume, marketGroup);
					//Inventory AKA Assets
					if (stockpile.isInventory()){
						for (Asset asset : program.getEveAssetEventList()){
							if (General.get().marketOrderFlag().equals(asset.getFlag())) continue; //Ignore market orders
							item.updateAsset(asset, flags.get(asset.getFlag()) , chars.get(asset.getOwner()), regions.get(asset.getRegion()));
						}
					}
					//Orders & Jobs
					if (stockpile.isBuyOrders() || stockpile.isSellOrders() || stockpile.isJobs()){
						for (Account account : program.getSettings().getAccounts()){
							for (Human human : account.getHumans()){
								if (human.isShowAssets()){
									//Market Orders
									for (ApiMarketOrder marketOrder : human.getMarketOrders()){
										Location location = program.getSettings().getLocations().get(marketOrder.getStationID());
										item.updateMarketOrder(marketOrder, human.getCharacterID(), location);
									}
									//Jobs
									for (ApiIndustryJob industryJob : human.getIndustryJobs()){
										Location location = program.getSettings().getLocations().get(industryJob.getOutputLocationID());
										Item itemType = program.getSettings().getItems().get(industryJob.getOutputTypeID());
										ItemFlag itemFlag = program.getSettings().getItemFlags().get(industryJob.getOutputFlag());
										item.updateIndustryJob(industryJob, itemFlag, human.getCharacterID(), location, itemType);
									}
								}
							}
						}
					}
				}
			}
			stockpile.updateTotal();
			volumnNow = volumnNow + stockpile.getTotal().getVolumeNow();
			volumnNeeded = volumnNeeded + stockpile.getTotal().getVolumeNeeded();
			valueNow = valueNow + stockpile.getTotal().getValueNow();
			valueNeeded = valueNeeded + stockpile.getTotal().getValueNeeded();
		}
		
		jVolumeNow.setText(TabsStockpile.get().now()+Formater.doubleFormat(volumnNow));
		jVolumeNeeded.setText(TabsStockpile.get().needed()+Formater.doubleFormat(volumnNeeded));
		jValueNow.setText(TabsStockpile.get().now()+Formater.iskFormat(valueNow));
		jValueNeeded.setText(TabsStockpile.get().needed()+Formater.iskFormat(valueNeeded));
		
		
		//Free Memory...
		regions = null;
		flags = null;
		chars = null;
		//Save separator expanded/collapsed state
		for (int i = 0; i < separatorList.size(); i++){
			Object object = separatorList.get(i);
			if (object instanceof SeparatorList.Separator){
				SeparatorList.Separator separator = (SeparatorList.Separator) object;
				StockpileItem item = (StockpileItem) separator.first();
				item.getStockpile().setExpanded(separator.getLimit() != 0);
			}
		}
		//Update list
		try {
			stockpileEventList.getReadWriteLock().writeLock().lock();
			stockpileEventList.clear();
			stockpileEventList.addAll(stockpileItems);
		} finally {
			stockpileEventList.getReadWriteLock().writeLock().unlock();
		}
		//Restore separator expanded/collapsed state
		for (int i = 0; i < separatorList.size(); i++){
			Object object = separatorList.get(i);
			if (object instanceof SeparatorList.Separator){
				SeparatorList.Separator separator = (SeparatorList.Separator) object;
				StockpileItem item = (StockpileItem) separator.first();
				separatorList.getReadWriteLock().writeLock().lock();
				try {
					separator.setLimit(item.getStockpile().isExpanded() ? Integer.MAX_VALUE : 0);
				} finally {
					separatorList.getReadWriteLock().writeLock().unlock();
				}
			}
		}
	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD.equals(e.getActionCommand())){
			stockpileDialog.showAdd();
			updateData();
		}
		if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
			jTable.expandSeparators(false, separatorList);
		}
		if (ACTION_EXPAND.equals(e.getActionCommand())) {
			jTable.expandSeparators(true, separatorList);
		}
		if (StockpileSeparatorTableCell.ACTION_EDIT_STOCKPILE.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Object o = stockpileTableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>){
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				stockpileDialog.showEdit(stockpile);
				updateData();
			}
		}
		if (StockpileSeparatorTableCell.ACTION_CLONE_STOCKPILE.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Object o = stockpileTableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>){
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				stockpileDialog.showClone(stockpile);
				updateData();
			}
		}
		if (StockpileSeparatorTableCell.ACTION_DELETE_STOCKPILE.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Object o = stockpileTableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>){
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), stockpile.getName(), TabsStockpile.get().deleteStockpileTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (value == JOptionPane.OK_OPTION){
					program.getSettings().getStockpiles().remove(stockpile);
					updateData();
				}
			}
		}
		if (StockpileSeparatorTableCell.ACTION_CLIPBOARD_STOCKPILE.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Object o = stockpileTableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>){
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				String s = "";
				double volume = 0;
				double value = 0;
				for (StockpileItem stockpileItem : stockpile.getItems()){
					if (stockpileItem.getTypeID() > 0 && stockpileItem.getCountNeeded() < 0){
						s = s + Math.abs(stockpileItem.getCountNeeded())+"x " +stockpileItem.getName()+"\r\n";
						volume = volume + stockpileItem.getVolumeNeeded();
						value = value + stockpileItem.getValueNeeded();
					}
				}
				s = s + "\r\n";
				s = s + TabsStockpile.get().totalToHaul()+Formater.doubleFormat(Math.abs(volume))+ "\r\n";
				s = s + TabsStockpile.get().estimatedMarketValue()+Formater.iskFormat(Math.abs(value))+ "\r\n";
				SecurityManager sm = System.getSecurityManager();
				if (sm != null) {
					try {
						sm.checkSystemClipboardAccess();
					} catch (Exception ex) {
						return;
					}
				}
				Toolkit tk = Toolkit.getDefaultToolkit();
				StringSelection data = new StringSelection(s);
				Clipboard cp = tk.getSystemClipboard();
				cp.setContents(data, null);
			}
		}
		if (StockpileSeparatorTableCell.ACTION_ADD_ITEM.equals(e.getActionCommand())){
			int index = jTable.getSelectedRow();
			Object o = stockpileTableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>){
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				StockpileItem item = (StockpileItem) separator.first();
				Stockpile stockpile = item.getStockpile();
				stockpileItemDialog.showAdd(stockpile);
				updateData();
			}
		}
		if (ACTION_EDIT_ITEM.equals(e.getActionCommand())){
			Object source = e.getSource();
			if (source instanceof JStockpileMenuItem){
				JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
				StockpileItem item = jMenuItem.getItem();
				stockpileItemDialog.showEdit(item);
				updateData();
			}
		}
		if (ACTION_DELETE_ITEM.equals(e.getActionCommand())){
			Object source = e.getSource();
			if (source instanceof JStockpileMenuItem){
				JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
				StockpileItem item = jMenuItem.getItem();
				int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), item.getName(), TabsStockpile.get().deleteItemTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (value == JOptionPane.OK_OPTION){
					item.getStockpile().remove(item);
					updateData();
				}
			}
		}
	}
	
	public class StockpileSeparatorComparator implements Comparator<StockpileItem> {
		@Override
		public int compare(StockpileItem o1, StockpileItem o2) {
			return o1.getSeperator().compareTo(o2.getSeperator());
		}
	}
	
	public static class JStockpileMenuItem extends JMenuItem{

		private StockpileItem item;
		
		public JStockpileMenuItem(String title, StockpileItem item) {
			super(title);
			if (item instanceof StockpileTotal){
				this.setEnabled(false);
			}
			this.item = item;
		}

		public StockpileItem getItem() {
			return item;
		}
	} 
}
