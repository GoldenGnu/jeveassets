/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JIntegerField;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrder;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.transaction.Transaction;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


class StockpileShoppingListDialog extends JDialogCentered {

	private enum StockpileShoppingListAction {
		CLIPBOARD_STOCKPILE,
		CLOSE
	}

	private JTextArea jText;
	private JButton jClose;
	private JTextField jPercent;

	private List<Stockpile> stockpiles;
	private boolean updating = false;

	StockpileShoppingListDialog(final Program program) {
		super(program,  TabsStockpile.get().shoppingList(), Images.TOOL_STOCKPILE.getImage());

		this.getDialog().setResizable(true);
		
		ListenerClass listener = new ListenerClass();

		JButton jCopyToClipboard = new JButton(TabsStockpile.get().clipboardStockpile(), Images.EDIT_COPY.getIcon());
		jCopyToClipboard.setActionCommand(StockpileShoppingListAction.CLIPBOARD_STOCKPILE.name());
		jCopyToClipboard.addActionListener(listener);

		JLabel jPercentFullLabel = new JLabel(TabsStockpile.get().percentFull());
		JLabel jPercentLabel = new JLabel(TabsStockpile.get().percent());

		jPercent = new JIntegerField("");
		jPercent.addCaretListener(listener);

		jClose = new JButton(TabsStockpile.get().close());
		jClose.setActionCommand(StockpileShoppingListAction.CLOSE.name());
		jClose.addActionListener(listener);

		jText = new JTextArea();
		jText.setEditable(false);
		jText.setFont(jPanel.getFont());
		jText.setBackground(jPanel.getBackground());
		JCopyPopup.install(jText);

		JSeparator jSeparator = new JSeparator(SwingConstants.VERTICAL);

		JScrollPane jTextScroll = new JScrollPane(jText);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jCopyToClipboard)
					.addGap(10)
					.addComponent(jSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(jPercentFullLabel)
					.addComponent(jPercent, 100, 100, 100)
					.addComponent(jPercentLabel)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(jTextScroll, 500, 500, Integer.MAX_VALUE)
					.addComponent(jClose)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCopyToClipboard, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSeparator, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPercentFullLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPercent, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPercentLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTextScroll, 400, 400, Integer.MAX_VALUE)
				.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}

	void show(final Stockpile stockpile) {
		show(Collections.singletonList(stockpile));
	}

	void show(final List<Stockpile> addStockpiles) {
		updating = true;
		this.stockpiles = addStockpiles;
		jPercent.setText("100");
		updateList();
		updating = false;
		super.setVisible(true);
	}

	private void updateList() {
		//Multiplier
		long percent;
		try {
			percent = Long.valueOf(jPercent.getText());
			if (percent <= 0) {
				percent = 100;
			}
		} catch (NumberFormatException e) {
			percent = 100;
		}

	//All claims
		Map<Integer, List<StockClaim>> claims = new HashMap<Integer, List<StockClaim>>();
		String stockpileNames = "";
		for (Stockpile stockpile : stockpiles) {
			//Stockpile names
			if (!stockpileNames.isEmpty()) {
				stockpileNames = stockpileNames + ", ";
			}
			stockpileNames = stockpileNames + stockpile.getName();
			for (StockpileItem stockpileItem : stockpile.getItems()) {
				final int TYPEID = stockpileItem.getItemTypeID();
				if (TYPEID != 0) { //Ignore Total
					List<StockClaim> claimList  = claims.get(TYPEID);
					if (claimList == null) {
						claimList = new ArrayList<StockClaim>();
						claims.put(TYPEID, claimList);
					}
					claimList.add(new StockClaim(stockpileItem, percent));
				}
			}
		}

	//All items
		Map<Integer, List<StockItem>> items = new HashMap<Integer, List<StockItem>>();
		//Assets
		for (Asset asset : program.getAssetEventList()) {
			//Skip market orders
			if (asset.getFlag().equals(General.get().marketOrderSellFlag())) {
				continue; //Ignore market sell orders
			}
			if (asset.getFlag().equals(General.get().marketOrderBuyFlag())) {
				continue; //Ignore market buy orders
			}
			//Skip contracts
			if (asset.getFlag().equals(General.get().contractIncluded())) {
				continue; //Ignore contracts included
			}
			if (asset.getFlag().equals(General.get().contractExcluded())) {
				continue; //Ignore contracts excluded
			}
			add(asset.getItem().getTypeID(), asset, new StockItem(asset), claims, items);
		}
		//Market Orders
		for (MarketOrder marketOrder : program.getMarketOrdersEventList()) {
			add(marketOrder.getTypeID(), marketOrder, new StockItem(marketOrder), claims, items);
		}
		//Industry Jobs
		for (IndustryJob industryJob : program.getIndustryJobsEventList()) {
			add(industryJob.getOutputTypeID(), industryJob, new StockItem(industryJob), claims, items);
		}
		//Transactions
		for (Transaction transaction : program.getTransactionsEventList()) {
			add(transaction.getTypeID(), transaction, new StockItem(transaction), claims, items);
		}

	//Claim items
		for (Map.Entry<Integer, List<StockItem>> entry : items.entrySet()) {
			for (StockItem stockItem : entry.getValue()) {
				stockItem.claim();
			}
		}

	//Show missing
		String s = "";
		double volume = 0;
		double value = 0;
		for (Map.Entry<Integer, List<StockClaim>> entry : claims.entrySet()) {
			Item item = ApiIdConverter.getItem(entry.getKey());
			long countMinimum = 0;
			for (StockClaim stockClaim : entry.getValue()) {
				//Add missing
				countMinimum = countMinimum + stockClaim.getCountMinimum();
				//Add volume (will add zero if nothing is needed)
				volume = volume + (stockClaim.getCountMinimum() * stockClaim.getVolume());
				//Add value (will add zero if nothing is needed)
				value = value + (stockClaim.getCountMinimum() * stockClaim.getDynamicPrice());
			}
			if (countMinimum > 0) { //Add type string (if anything is needed)
				s = s + Formater.longFormat(countMinimum) + "x " + item.getTypeName() + "\r\n";
			}
		}
		if (s.isEmpty()) { //if string is empty, nothing is needed
			s = TabsStockpile.get().nothingNeeded();
		} else { //Add total volume and value
			s = s + "\r\n";
			s = s + TabsStockpile.get().totalToHaul() + Formater.doubleFormat(Math.abs(volume)) + "\r\n";
			s = s + TabsStockpile.get().estimatedMarketValue() + Formater.iskFormat(Math.abs(value)) + "\r\n";
		}
		if (percent != 100) { //Add stockpile names (adds percent if it's not 100%)
			s = stockpileNames + " (" + percent + TabsStockpile.get().percent() + ")\r\n\r\n" + s;
		} else { //(without percent)
			s = stockpileNames + "\r\n\r\n" + s;
		}
		jText.setText(s);
	}

	//Add claims to item
	private void add(final int typeID, final Object object, final StockItem stockItem, final Map<Integer, List<StockClaim>> claims, final Map<Integer, List<StockItem>> items) {
		//Get claims by typeID
		List<StockClaim> minimumList = claims.get(typeID);
		if (minimumList == null) { //if no claims for typeID: return
			return;
		}

		//Get item list by typeID
		List<StockItem> itemList = items.get(typeID);
		if (itemList == null) {
			itemList = new ArrayList<StockItem>();
			items.put(typeID, itemList);
		}

		boolean added = false;
		for (StockClaim stockMinimum : minimumList) {
			if (stockMinimum.matches(object)){ //if match (have claim)
				if (!added) { //if item not added already - add to items list
					itemList.add(stockItem);
					added = true;
				}
				stockItem.addClaim(stockMinimum); //Add claim
			}
		}
	}

	private void copyToClipboard() {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			try {
				sm.checkSystemClipboardAccess();
			} catch (Exception ex) {
				return;
			}
		}
		Toolkit tk = Toolkit.getDefaultToolkit();
		StringSelection data = new StringSelection(jText.getText());
		Clipboard cp = tk.getSystemClipboard();
		cp.setContents(data, null);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jClose;
	}

	@Override
	protected JButton getDefaultButton() {
		return null;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() { }

	private class ListenerClass implements ActionListener, CaretListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (StockpileShoppingListAction.CLIPBOARD_STOCKPILE.name().equals(e.getActionCommand())) {
				copyToClipboard();
			}
			if (StockpileShoppingListAction.CLOSE.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
		}

		@Override
		public void caretUpdate(final CaretEvent e) {
			if (!updating) {
				updateList();
			}
		}
	}

	private static class StockClaim implements Comparable<StockClaim>{
		private long countMinimum;
		private final StockpileItem stockpileItem;
		private long available = 0;

		public StockClaim(StockpileItem stockpileItem, long percent) {
			this.stockpileItem = stockpileItem;
			this.countMinimum = (long)(stockpileItem.getCountMinimumMultiplied() * percent / 100.0);
		}

		public double getVolume() {
			return stockpileItem.getVolume();
		}

		public Double getDynamicPrice() {
			return stockpileItem.getDynamicPrice();
		}

		private boolean matches(Object object) {
			if (object instanceof Asset) {
				return stockpileItem.matches((Asset) object);
			} else if (object instanceof MarketOrder) {
				return stockpileItem.matches((MarketOrder) object);
			} else if (object instanceof IndustryJob) {
				return stockpileItem.matches((IndustryJob) object);
			} else if (object instanceof Transaction) {
				return stockpileItem.matches((Transaction) object);
			}
			return false;
		}

		public long getCountMinimum() {
			return countMinimum;
		}

		public void addCount(long count) {
			countMinimum = countMinimum - count;
		}

		public void addAvailable(long available) {
			this.available = this.available + available;
		}

		private long getNeed() { //Claim optimization
			return available - countMinimum;
		}

		@Override
		public int compareTo(StockClaim o) {
			if (this.getNeed() > o.getNeed()) {
				return 1;
			} else if  (this.getNeed() < o.getNeed()){
				return -1;
			} else {
				return 0;
			}
		}
	}

	private static class StockItem {
		private final List<StockClaim> claims = new ArrayList<StockClaim>();
		private long count;

		private StockItem(Asset asset) {
			this(asset.getCount());
		}

		private StockItem(MarketOrder marketOrder) {
			this(marketOrder.getVolRemaining());
		}

		private StockItem(IndustryJob industryJob) {
			this((industryJob.getRuns() * industryJob.getPortion()));
		}
		private StockItem(Transaction transaction) {
			this(transaction.isBuy() ? transaction.getQuantity() : -transaction.getQuantity());
		}

		public StockItem(long count) {
			this.count = count;
		}

		public void addClaim(StockClaim stockMinimum) {
			claims.add(stockMinimum);
			stockMinimum.addAvailable(count);
		}

		public void claim() {
			Collections.sort(claims); //Sort by need
			for (StockClaim stockMinimum : claims) {
				if (stockMinimum.getCountMinimum() >= count) { //Add all
					stockMinimum.addCount(count);
					count = 0;
					break;
				} else { //Add part of the count
					long missing = stockMinimum.getCountMinimum();
					stockMinimum.addCount(missing);
					count = count - missing;
				}
			}
		}
	}
}
