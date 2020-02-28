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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CopyHandler;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JIntegerField;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


class StockpileShoppingListDialog extends JDialogCentered {

	private enum StockpileShoppingListAction {
		CLIPBOARD_STOCKPILE,
		CLOSE,
		FORMAT_CHANGED
	}

	private final DecimalFormat number  = new DecimalFormat("0");

	private final JTextArea jText;
	private final JButton jClose;
	private final JTextField jPercentFull;
	private final JTextField jPercentIgnore;
	private final JComboBox<String> jEveMultiBuy;

	private String shoppingList = "";
	private String eveMultibuy = "";
	private List<Stockpile> stockpiles;
	private boolean updating = false;

	StockpileShoppingListDialog(final Program program) {
		super(program,  TabsStockpile.get().shoppingList(), Images.TOOL_STOCKPILE.getImage());

		this.getDialog().setResizable(true);
		
		ListenerClass listener = new ListenerClass();

		JButton jCopyToClipboard = new JButton(TabsStockpile.get().clipboardStockpile(), Images.EDIT_COPY.getIcon());
		jCopyToClipboard.setActionCommand(StockpileShoppingListAction.CLIPBOARD_STOCKPILE.name());
		jCopyToClipboard.addActionListener(listener);

		String[] formats = {TabsStockpile.get().shoppingList(), TabsStockpile.get().eveMultibuy()};
		jEveMultiBuy = new JComboBox<String>(formats);
		IconListCellRendererRenderer renderer = new IconListCellRendererRenderer();
		renderer.add(TabsStockpile.get().shoppingList(), Images.STOCKPILE_SHOPPING_LIST.getIcon());
		renderer.add(TabsStockpile.get().eveMultibuy(), Images.MISC_EVE.getIcon());
		jEveMultiBuy.setRenderer(renderer);
		jEveMultiBuy.setActionCommand(StockpileShoppingListAction.FORMAT_CHANGED.name());
		jEveMultiBuy.addActionListener(listener);

		JLabel jFullLabel = new JLabel(TabsStockpile.get().percentFull());
		JLabel jFullPercentLabel = new JLabel(TabsStockpile.get().percent());

		jPercentFull = new JIntegerField("");
		jPercentFull.addCaretListener(listener);

		JLabel jIgnoreLabel = new JLabel(TabsStockpile.get().percentIgnore());
		JLabel jIgnorePercentLabel = new JLabel(TabsStockpile.get().percent());

		jPercentIgnore = new JIntegerField("");
		jPercentIgnore.addCaretListener(listener);

		jClose = new JButton(TabsStockpile.get().close());
		jClose.setActionCommand(StockpileShoppingListAction.CLOSE.name());
		jClose.addActionListener(listener);

		jText = new JTextArea();
		jText.setEditable(false);
		jText.setFont(jPanel.getFont());
		jText.setBackground(jPanel.getBackground());

		JScrollPane jTextScroll = new JScrollPane(jText);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(jCopyToClipboard)
						.addComponent(jEveMultiBuy)
					)
					.addGap(0, 0, Integer.MAX_VALUE)
					.addGroup(layout.createParallelGroup()
						.addComponent(jFullLabel)
						.addComponent(jIgnoreLabel)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jPercentFull, 100, 100, 100)
						.addComponent(jPercentIgnore, 100, 100, 100)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jFullPercentLabel)
						.addComponent(jIgnorePercentLabel)
					)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(jTextScroll, 500, 500, Integer.MAX_VALUE)
					.addComponent(jClose)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCopyToClipboard, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFullLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPercentFull, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFullPercentLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jEveMultiBuy, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jIgnoreLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPercentIgnore, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jIgnorePercentLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jTextScroll, 400, 400, Integer.MAX_VALUE)
				.addComponent(jClose, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}

	void show(final Stockpile stockpile) {
		show(Collections.singletonList(stockpile));
	}

	void show(final List<Stockpile> addStockpiles) {
		updating = true;
		this.stockpiles = addStockpiles;
		jPercentFull.setText("100");
		jPercentIgnore.setText("100");
		jEveMultiBuy.setSelectedIndex(0);
		updateList();
		updating = false;
		super.setVisible(true);
	}

	private void updateList() {
		//Multiplier
		long percent;
		try {
			percent = Long.valueOf(jPercentFull.getText());
			if (percent <= 0) {
				percent = 100;
			}
		} catch (NumberFormatException e) {
			percent = 100;
		}
		long hide;
		try {
			hide = Long.valueOf(jPercentIgnore.getText());
			if (hide <= 0) {
				hide = 100;
			} else if (hide > 100) {
				hide = 100;
			}
		} catch (NumberFormatException e) {
			hide = 100;
		}

	//All claims
		Map<TypeIdentifier, List<StockClaim>> claims = new HashMap<TypeIdentifier, List<StockClaim>>();
		String stockpileNames = "";
		for (Stockpile stockpile : stockpiles) {
			//Stockpile names
			if (!stockpileNames.isEmpty()) {
				stockpileNames = stockpileNames + ", ";
			}
			stockpileNames = stockpileNames + stockpile.getName();
			for (StockpileItem stockpileItem : stockpile.getItems()) {
				TypeIdentifier typeID = new TypeIdentifier(stockpileItem);
				if (!typeID.isEmpty()) { //Ignore Total
					List<StockClaim> claimList  = claims.get(typeID);
					if (claimList == null) {
						claimList = new ArrayList<StockClaim>();
						claims.put(typeID, claimList);
					}
					claimList.add(new StockClaim(stockpileItem, percent));
				}
			}
		}

	//All items
		Map<TypeIdentifier, List<StockItem>> items = new HashMap<TypeIdentifier, List<StockItem>>();
		//Assets
		for (MyAsset asset : program.getAssetList()) {
			if (asset.isGenerated()) { //Skip generated assets
				continue;
			}
			int typeID = asset.isBPC() ? -asset.getTypeID() : asset.getTypeID();
			add(new TypeIdentifier(typeID, false), asset, claims, items);
			add(new TypeIdentifier(typeID, true), asset, claims, items);
		}
		//Market Orders
		for (MyMarketOrder marketOrder : program.getMarketOrdersList()) {
			add(new TypeIdentifier(marketOrder.getTypeID(), false), marketOrder, claims, items);
		}
		//Industry Jobs
		for (MyIndustryJob industryJob : program.getIndustryJobsList()) {
			add(new TypeIdentifier(industryJob.getProductTypeID(), false), industryJob, claims, items);
			add(new TypeIdentifier(-industryJob.getBlueprintTypeID(), true), industryJob, claims, items);
		}
		//Transactions
		for (MyTransaction transaction : program.getTransactionsList()) {
			add(new TypeIdentifier(transaction.getTypeID(), false), transaction, claims, items);
		}
		//ContractItems
		for (MyContractItem contractItem : program.getContractItemList()) {
			if (contractItem.getContract().isIgnoreContract()) {
				continue;
			}
			int typeID = contractItem.isBPC() ? -contractItem.getTypeID() : contractItem.getTypeID();
			add(new TypeIdentifier(typeID, false), contractItem, claims, items);
		}

	//Claim items
		for (Map.Entry<TypeIdentifier, List<StockItem>> entry : items.entrySet()) {
			for (StockItem stockItem : entry.getValue()) {
				stockItem.claim();
			}
		}

	//Show missing
		StringBuilder shoppingListBuilder = new StringBuilder();
		StringBuilder eveMultibuyBuilder = new StringBuilder();
		double volume = 0;
		double value = 0;
		for (Map.Entry<TypeIdentifier, List<StockClaim>> entry : claims.entrySet()) {
			boolean bpc = false;
			boolean bpo = false;
			boolean runs = false;
			Item item;
			if (entry.getKey().isBPC()) {
				item = ApiIdConverter.getItem(Math.abs(entry.getKey().getTypeID()));
				bpc = true;
				runs = entry.getKey().isRuns();
			} else {
				item = ApiIdConverter.getItem(entry.getKey().getTypeID());
				bpo = item.isBlueprint();
			}
			long countMinimum = 0;
			for (StockClaim stockClaim : entry.getValue()) {
				if (stockClaim.getPercentFull() > hide) {
					continue; //Ignore everything above x% percent
				}
				//Add missing
				countMinimum = countMinimum + stockClaim.getCountMinimum();
				//Add volume (will add zero if nothing is needed)
				volume = volume + (stockClaim.getCountMinimum() * stockClaim.getVolume());
				//Add value (will add zero if nothing is needed)
				value = value + (stockClaim.getCountMinimum() * stockClaim.getDynamicPrice());
			}
			if (countMinimum > 0) { //Add type string (if anything is needed)
				//Multibuy
				eveMultibuyBuilder.append(item.getTypeName());
				eveMultibuyBuilder.append(" ");
				eveMultibuyBuilder.append(number.format(countMinimum));
				eveMultibuyBuilder.append("\r\n");
				//Shopping List
				shoppingListBuilder.append(Formater.longFormat(countMinimum));
				shoppingListBuilder.append("x ");
				shoppingListBuilder.append(item.getTypeName());
				if (bpc) {
					if (runs) {
						shoppingListBuilder.append(" (Runs)");
					} else {
						shoppingListBuilder.append(" (BPC)");
					}
				} else if (bpo) {
					shoppingListBuilder.append(" (BPO)");
				}
				shoppingListBuilder.append("\r\n");
			}
		}
		String s = shoppingListBuilder.toString();
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
		shoppingList = s;
		eveMultibuy = eveMultibuyBuilder.toString();
		setText();
	}

	//Add claims to item
	private void add(final TypeIdentifier typeID, final Object object, final Map<TypeIdentifier, List<StockClaim>> claims, final Map<TypeIdentifier, List<StockItem>> items) {
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

		StockItem stockItem = null;
		for (StockClaim stockMinimum : minimumList) {
			Long count = stockMinimum.matches(object);
			if (count != null && count != 0){ //if match (have claim)
				if (stockItem == null) { //if item not added already - add to items list
					stockItem = new StockItem();
					itemList.add(stockItem);
				}
				stockItem.addClaim(stockMinimum, count); //Add claim
			}
		}
	}

	private void setText() {
		Object selectedItem = jEveMultiBuy.getSelectedItem();
		if (TabsStockpile.get().eveMultibuy().equals(selectedItem)) {
			jText.setText(eveMultibuy);
		} else { //Default
			jText.setText(shoppingList);
		}
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
				CopyHandler.toClipboard(jText.getText());
			}
			if (StockpileShoppingListAction.CLOSE.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
			if (StockpileShoppingListAction.FORMAT_CHANGED.name().equals(e.getActionCommand())) {
				setText();
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
		private final StockpileItem stockpileItem;
		private final long totalNeed;
		private long countMinimum;
		private long available = 0;

		public StockClaim(StockpileItem stockpileItem, long percent) {
			this.stockpileItem = stockpileItem;
			this.totalNeed = (long)(stockpileItem.getCountMinimumMultiplied() * percent / 100.0);
			this.countMinimum = this.totalNeed;
			
		}

		public double getPercentFull() {
			return (totalNeed - countMinimum) * 100.0 / totalNeed ;
		}

		public double getVolume() {
			return stockpileItem.getVolume();
		}

		public Double getDynamicPrice() {
			return stockpileItem.getDynamicPrice();
		}

		private Long matches(Object object) {
			return stockpileItem.matches(object);
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
		private final Map<Count, List<StockClaim>> claims = new HashMap<Count, List<StockClaim>>();

		public StockItem() { }

		public void addClaim(StockClaim stockMinimum, long count) {
			List<StockClaim> claimList = claims.get(new Count(count));
			if (claimList == null) {
				claimList = new ArrayList<StockClaim>();
				claims.put(new Count(count), claimList);
			}
			claimList.add(stockMinimum);
			stockMinimum.addAvailable(count);
		}

		public void claim() {
			for (Map.Entry<Count, List<StockClaim>> entry : claims.entrySet()) {
				List<StockClaim> claimList = entry.getValue();
				Count count = entry.getKey();
				Collections.sort(claimList); //Sort by need
				for (StockClaim stockMinimum : claimList) {
					if (stockMinimum.getCountMinimum() >= count.getCount()) { //Add all
						stockMinimum.addCount(count.getCount());
						count.takeAll();
						break;
					} else { //Add part of the count
						long missing = stockMinimum.getCountMinimum();
						stockMinimum.addCount(missing);
						count.take(missing);
					}
				}
			}
		}
	}

	private static class Count {
		private final long id;
		private long count;

		public Count(long count) {
			this.count = count;
			this.id = count;
		}

		public void takeAll() {
			count = 0;
		}

		public void take(long missing) {
			count = count - missing;
		}

		public long getCount() {
			return count;
		}

		@Override
		public int hashCode() {
			int hash = 5;
			hash = 67 * hash + (int) (this.id ^ (this.id >>> 32));
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
			final Count other = (Count) obj;
			if (this.id != other.id) {
				return false;
			}
			return true;
		}
	}

	private static class TypeIdentifier  {
		private final int typeID;
		private final boolean runs;

		public TypeIdentifier(StockpileItem stockpileItem) {
			this.typeID = stockpileItem.getItemTypeID();
			this.runs = stockpileItem.isRuns();
		}

		public TypeIdentifier(int typeID, boolean runs) {
			this.typeID = typeID;
			this.runs = runs;
		}

		public boolean isEmpty() {
			return typeID == 0;
		}

		public boolean isBPC() {
			return typeID < 0;
		}

		public boolean isRuns() {
			return runs;
		}

		public int getTypeID() {
			return typeID;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 59 * hash + this.typeID;
			hash = 59 * hash + (this.runs ? 1 : 0);
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
			final TypeIdentifier other = (TypeIdentifier) obj;
			if (this.typeID != other.typeID) {
				return false;
			}
			if (this.runs != other.runs) {
				return false;
			}
			return true;
		}
	}

	private static class IconListCellRendererRenderer implements ListCellRenderer<String> {
		DefaultListCellRenderer renderer = new DefaultListCellRenderer();
		Map<String, Icon> icons = new HashMap<>();

		@Override
		public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
			Component component = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (component instanceof JLabel) {
				JLabel jLabel = (JLabel) component;
				jLabel.setIcon(icons.get(value));
			}
			return component;
		}

		public void add(String text, Icon icon) {
			icons.put(text, icon);
		}

		public void remove(String text) {
			icons.remove(text);
		}

		public void clear() {
			icons.clear();
		}
	}
}
