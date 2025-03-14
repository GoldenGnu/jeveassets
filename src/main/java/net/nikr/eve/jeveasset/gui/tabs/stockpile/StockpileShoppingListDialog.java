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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
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
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CopyHandler;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JIntegerField;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileStock;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.TypeIdentifier;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class StockpileShoppingListDialog extends JDialogCentered {

	private static final Logger LOG = LoggerFactory.getLogger(StockpileShoppingListDialog.class);

	private enum StockpileShoppingListAction {
		CLIPBOARD_STOCKPILE,
		CLOSE,
		FORMAT_CHANGED
	}

	private static final DecimalFormat NUMBER = new DecimalFormat("0");

	private final JTextArea jText;
	private final JButton jClose;
	private final JTextField jPercentFull;
	private final JTextField jPercentIgnore;
	private final JComboBox<ShoppingListType> jFormat;
	private final JComboBox<String> jOutput;
	private final Map<ShoppingListType, ShoppingListData> outputData = new EnumMap<>(ShoppingListType.class);

	private List<Stockpile> stockpiles;
	private boolean updating = false;

	StockpileShoppingListDialog(final Program program) {
		super(program, TabsStockpile.get().shoppingList(), Images.TOOL_STOCKPILE.getImage());

		this.getDialog().setResizable(true);

		ListenerClass listener = new ListenerClass();

		JButton jCopyToClipboard = new JButton(TabsStockpile.get().clipboardStockpile(), Images.EDIT_COPY.getIcon());
		jCopyToClipboard.setActionCommand(StockpileShoppingListAction.CLIPBOARD_STOCKPILE.name());
		jCopyToClipboard.addActionListener(listener);

		jFormat = new JComboBox<>(ShoppingListType.values());
		jFormat.setRenderer(new IconListCellRendererRenderer());
		jFormat.setActionCommand(StockpileShoppingListAction.FORMAT_CHANGED.name());
		jFormat.addActionListener(listener);

		String[] types = {TabsStockpile.get().itemsMissing(), TabsStockpile.get().itemsRequired(), TabsStockpile.get().itemsOwned()};
		jOutput = new JComboBox<>(types);
		jOutput.setActionCommand(StockpileShoppingListAction.FORMAT_CHANGED.name());
		jOutput.addActionListener(listener);

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
						.addComponent(jFormat)
					)
					.addComponent(jOutput)
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
					.addComponent(jFormat, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOutput, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
		jFormat.setSelectedIndex(0);
		updateList();
		updating = false;
		super.setVisible(true);
	}

	private void updateList() {
		//Multiplier
		long percent;
		try {
			percent = Long.parseLong(jPercentFull.getText());
			if (percent <= 0) {
				percent = 100;
			}
		} catch (NumberFormatException e) {
			percent = 100;
		}
		long hide;
		try {
			hide = Long.parseLong(jPercentIgnore.getText());
			if (hide <= 0) {
				hide = 100;
			} else if (hide > 100) {
				hide = 100;
			}
		} catch (NumberFormatException e) {
			hide = 100;
		}
		outputData.clear();
		for (ShoppingListType type : ShoppingListType.values()) {
			outputData.put(type, new ShoppingListData(type));
		}
	//All claims
		Map<TypeIdentifier, List<StockClaim>> claims = new HashMap<>();
		Map<String, Double> subpiles = new HashMap<>();
		StringBuilder stockpileNamesBuilder = new StringBuilder();
		for (Stockpile stockpile : stockpiles) {
			stockpileNamesBuilder.append(Formatter.copyFormat(stockpile.getMultiplier()));
			stockpileNamesBuilder.append("x ");
			stockpileNamesBuilder.append(stockpile.getName());
			stockpileNamesBuilder.append("\r\n");
			Set<Integer> contractIDs = null;
			Set<Long> assetsIDs = null;
			if (stockpile.isMatchAll()) {
				if (stockpile.isContracts()) {
					contractIDs = new HashSet<>();
					Map<MyContract, List<MyContractItem>> foundItems = StockpileData.contractsMatchAll(program.getProfileData(), stockpile, false);
					//Add
					for (MyContract contract : foundItems.keySet()) {
						contractIDs.add(contract.getContractID());
					}
				}
				if (stockpile.isAssets()) {
					assetsIDs = new HashSet<>();
					Map<MyAsset, List<MyAsset>> foundItems = StockpileData.assetsMatchAll(program.getProfileData(), stockpile, false);
					//Add
					for (Map.Entry<MyAsset, List<MyAsset>> entry : foundItems.entrySet()) {
						assetsIDs.add(entry.getKey().getItemID());
						for (MyAsset asset : entry.getValue()) {
							assetsIDs.add(asset.getItemID());
						}
					}
					
				}
			}
			for (SubpileStock stockpileItem : stockpile.getSubpileStocks()) {
				String key = stockpileItem.getName().trim();
				double value = subpiles.getOrDefault(key, 0.0);
				subpiles.put(key, value + stockpileItem.getSubMultiplier());
			}
			for (StockpileItem stockpileItem : stockpile.getClaims()) {
				TypeIdentifier type = stockpileItem.getType();
				if (!type.isEmpty()) { //Ignore Total
					List<StockClaim> claimList = claims.get(type);
					if (claimList == null) {
						claimList = new ArrayList<>();
						claims.put(type, claimList);
					}
					claimList.add(new StockClaim(stockpileItem, contractIDs, assetsIDs, percent));
				}
			}
		}
		StringBuilder subpileNamesBuilder = new StringBuilder();
		for (Map.Entry<String, Double> entry : subpiles.entrySet()) {
			subpileNamesBuilder.append(Formatter.copyFormat(entry.getValue()));
			subpileNamesBuilder.append("x ");
			subpileNamesBuilder.append(entry.getKey());
			subpileNamesBuilder.append("\r\n");
		}
		for (ShoppingListData data : outputData.values()) {
			data.printHeader(stockpileNamesBuilder, subpileNamesBuilder, subpiles, percent);
		}
		//All items
		Map<TypeIdentifier, List<StockItem>> items = new HashMap<>();
		//Assets
		for (MyAsset asset : program.getAssetsList()) {
			if (asset.isGenerated()) { //Skip generated assets
				continue;
			}
			Integer typeID = StockpileData.get(asset.getTypeID(), asset.isBPC());
			add(new TypeIdentifier(typeID, false), asset, claims, items);
			add(new TypeIdentifier(typeID, true), asset, claims, items);
		}
		//Market Orders
		for (MyMarketOrder marketOrder : program.getMarketOrdersList()) {
			add(new TypeIdentifier(marketOrder.getTypeID(), false), marketOrder, claims, items);
		}
		//Industry Jobs
		for (MyIndustryJob industryJob : program.getIndustryJobsList()) {
			//Manufacturing
			Integer productTypeID = industryJob.getProductTypeID();
			if (productTypeID != null) {
				add(new TypeIdentifier(productTypeID, false), industryJob, claims, items);
			}
			//Copying
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
			Integer typeID = StockpileData.get(contractItem.getTypeID(), contractItem.isBPC());
			add(new TypeIdentifier(typeID, false), contractItem, claims, items);
		}

	//Owned before claming
		double ownedVolume = 0;
		double ownedValue = 0;
		for (Map.Entry<TypeIdentifier, List<StockItem>> entry : items.entrySet()) {
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
			float volume = ApiIdConverter.getVolume(item, true);
			double price = ApiIdConverter.getPrice(entry.getKey().getTypeID(), bpc);
			long ownedCount = 0;
			for (StockItem stockItem : entry.getValue()) {
				Set<Count> counts = stockItem.getCounts();
				if (counts.size() != 1) {
					LOG.error("StockpileShoppingListDialog counts size was: " + counts.size() + " expected: 1", new Exception());
				}
				if (counts.isEmpty()) {
					continue;
				}
				Count count = counts.iterator().next();
				//Required count
				ownedCount = ownedCount + count.getCount();
				//Required volume
				ownedVolume = ownedVolume + (count.getCount() * volume);
				//Required value
				ownedValue = ownedValue + (count.getCount() * price);
			}
			for (ShoppingListData data : outputData.values()) {
				data.printOwned(ownedCount, item, bpc, bpo, runs);
			}
		}
	//Claim items
		for (Map.Entry<TypeIdentifier, List<StockItem>> entry : items.entrySet()) {
			for (StockItem stockItem : entry.getValue()) {
				stockItem.claim();
			}
		}
	//Show missing
		double missingVolume = 0;
		double missingValue = 0;
		double requiredVolume = 0;
		double requiredValue = 0;
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
			long missingCount = 0;
			long requiredCount = 0;
			for (StockClaim stockClaim : entry.getValue()) {
				if (stockClaim.getPercentFull() > hide) {
					continue; //Ignore everything above x% percent
				}
				//Missing count
				missingCount = missingCount + stockClaim.getCountMinimum();
				//Missing volume (will add zero if nothing is needed)
				missingVolume = missingVolume + (stockClaim.getCountMinimum() * stockClaim.getVolume());
				//Missing value (will add zero if nothing is needed)
				missingValue = missingValue + (stockClaim.getCountMinimum() * stockClaim.getDynamicPrice());
				//Required count
				requiredCount = requiredCount + stockClaim.getTotalNeed();
				//Required volume
				requiredVolume = requiredVolume + (stockClaim.getTotalNeed() * stockClaim.getVolume());
				//Required value
				requiredValue = requiredValue + (stockClaim.getTotalNeed() * stockClaim.getDynamicPrice());
			}
			for (ShoppingListData data : outputData.values()) {
				data.printMissing(missingCount, item, bpc, bpo, runs);
				data.printRequired(requiredCount, item, bpc, bpo, runs);
			}
		}
		for (ShoppingListData data : outputData.values()) {
			//Print sorted items
			data.printItems();
			//Print footer
			data.printFooterMissing(missingVolume, missingValue);
			data.printFooterRequired(requiredVolume, requiredValue);
			data.printFooterOwned(ownedVolume, ownedValue);
		}
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
			itemList = new ArrayList<>();
			items.put(typeID, itemList);
		}

		StockItem stockItem = null;
		for (StockClaim stockMinimum : minimumList) {
			if (stockMinimum.isContractMatchAll() && object instanceof MyContractItem) {
				MyContractItem contractItem = (MyContractItem) object;
				if (!stockMinimum.getContractIDs().contains(contractItem.getContract().getContractID())) {
					continue; //Ignore none matching contracts
				}
			}
			if (stockMinimum.isAssetMatchAll() && object instanceof MyAsset) {
				MyAsset asset = (MyAsset) object;
				if (!stockMinimum.getAssetIDs().contains(asset.getItemID())) {
					continue; //Ignore none matching assets
				}
			}
			Long count = stockMinimum.matches(object);
			if (count != null && count != 0) { //if match (have claim)
				if (stockItem == null) { //if item not added already - add to items list
					stockItem = new StockItem();
					itemList.add(stockItem);
				}
				stockItem.addClaim(stockMinimum, count); //Add claim
			}
		}
	}

	private void setText() {
		ShoppingListType type = jFormat.getItemAt(jFormat.getSelectedIndex());
		Object output = jOutput.getSelectedItem();
		ShoppingListData shoppingListData = outputData.get(type);
		if (shoppingListData != null) {
			jText.setText(shoppingListData.get(output));
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
		private final Set<Integer> contractIDs;
		private final Set<Long> assetIDs;
		private final long totalNeed;
		private long countMinimum;
		private long available = 0;

		public StockClaim(StockpileItem stockpileItem, Set<Integer> contractIDs, Set<Long> assetIDs, long percent) {
			this.stockpileItem = stockpileItem;
			this.contractIDs = contractIDs;
			this.assetIDs = assetIDs;
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

		public long getTotalNeed() {
			return totalNeed;
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

		public Set<Integer> getContractIDs() {
			return contractIDs;
		}

		public Set<Long> getAssetIDs() {
			return assetIDs;
		}

		public boolean isContractMatchAll() {
			return contractIDs != null;
		}

		public boolean isAssetMatchAll() {
			return assetIDs != null;
		}

		public boolean isMatchAll() {
			return assetIDs != null || contractIDs != null;
		}

		@Override
		public int compareTo(StockClaim o) {
			if (this.isMatchAll() && !o.isMatchAll()) {
				return 1;
			} else if (!this.isMatchAll() && o.isMatchAll()) {
				return -1;
			} else if (this.getNeed() > o.getNeed()) {
				return 1;
			} else if (this.getNeed() < o.getNeed()) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	private static class StockItem {
		private final Map<Count, List<StockClaim>> claims = new HashMap<>();

		public StockItem() { }

		public void addClaim(StockClaim stockMinimum, long count) {
			List<StockClaim> claimList = claims.get(new Count(count));
			if (claimList == null) {
				claimList = new ArrayList<>();
				claims.put(new Count(count), claimList);
			}
			claimList.add(stockMinimum);
			stockMinimum.addAvailable(count);
		}

		public Set<Count> getCounts() {
			return claims.keySet();
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

	private static enum ShoppingListType {
		SHOPPING_LIST(TabsStockpile.get().shoppingList(), Images.STOCKPILE_SHOPPING_LIST.getIcon()){
			@Override
			public void printHeader(StringBuilder stockpileNames, StringBuilder subpileNames, StringBuilder builder, Map<String, Double> subpiles, long percent) {
				//Add stockpile
				builder.append(TabsStockpile.get().stockpileShoppingList());
				if (percent != 100) { //adds percent if it's not 100%
					builder.append(" (");
					builder.append(percent);
					builder.append(TabsStockpile.get().percent());
					builder.append(")");
				}
				builder.append(":\r\n");
				builder.append(stockpileNames.toString());
				builder.append("\r\n");
				//Add subpiles
				if (!subpiles.isEmpty()) {
					builder.append(TabsStockpile.get().subpileShoppingList());
					builder.append("\r\n");
					builder.append(subpileNames.toString());
					builder.append("\r\n");
				}
				builder.append(TabsStockpile.get().itemsShoppingList());
				builder.append("\r\n");
			}
			@Override
			public void printItem(StringBuilder builder, long count, Item item, boolean bpc, boolean bpo, boolean runs) {
				//Shopping List
				builder.append(Formatter.longFormat(count));
				builder.append("x ");
				builder.append(item.getTypeName());
				if (bpc) {
					if (runs) {
						builder.append(" (Runs)");
					} else {
						builder.append(" (BPC)");
					}
				} else if (bpo) {
					builder.append(" (BPO)");
				}
				builder.append("\r\n");
			}
			@Override
			public void printFooter(StringBuilder builder, double volume, double value, boolean empty) {
				if (empty) { //if string is empty, nothing is needed
					builder.append(TabsStockpile.get().nothingNeeded());
				} else { //Add total volume and value
					builder.append("\r\n");
					builder.append(TabsStockpile.get().totalToHaul());
					builder.append(Formatter.doubleFormat(Math.abs(volume)));
					builder.append("\r\n");
					builder.append(TabsStockpile.get().estimatedMarketValue());
					builder.append(Formatter.iskFormat(Math.abs(value)));
				}
			}			
		},
		EVE_MULTIBUY(TabsStockpile.get().eveMultibuy(), Images.MISC_EVE.getIcon()){
			@Override
			public void printItem(StringBuilder builder, long count, Item item, boolean bpc, boolean bpo, boolean runs) {
				//Multibuy
				builder.append(item.getTypeName());
				builder.append(" ");
				builder.append(NUMBER.format(count));
				builder.append("\r\n");
			}
		},
		CSV(TabsStockpile.get().csv(), Images.TABLE_COLUMN_RESIZE.getIcon()){
			@Override
			public void printItem(StringBuilder builder, long count, Item item, boolean bpc, boolean bpo, boolean runs) {
				//CSV
				if (item.getTypeName().contains("\"")
						|| item.getTypeName().contains(",")) {
					builder.append("\"");
					builder.append(item.getTypeName().replace("\"", "\"\""));
					builder.append("\"");
				} else {
					builder.append(item.getTypeName());
				}
				builder.append(",");
				builder.append(NUMBER.format(count));
				builder.append("\r\n");
			}
		},
		SPREADSHEET(TabsStockpile.get().spreadsheet(), Images.TABLE_COLUMN_SHOW.getIcon()){
			@Override
			public void printItem(StringBuilder builder, long count, Item item, boolean bpc, boolean bpo, boolean runs) {
				//CSV
				builder.append(item.getTypeName());
				builder.append("\t");
				builder.append(NUMBER.format(count));
				builder.append("\r\n");
			}
		};

		private final String name;
		private final Icon icon;

		private ShoppingListType(String name, Icon icon) {
			this.name = name;
			this.icon = icon;
		}

		public Icon getIcon() {
			return icon;
		}

		@Override
		public String toString() {
			return name;
		}

		public void printHeader(StringBuilder stockpileNames, StringBuilder subpileNames, StringBuilder builder, Map<String, Double> subpiles, long percent) { }
		public abstract void printItem(StringBuilder builder, long count, Item item, boolean bpc, boolean bpo, boolean runs);
		public void printFooter(StringBuilder builder, double volume, double value, boolean empty) { }
	}

	private static class ShoppingListData {
		private final ShoppingListType type;
		private final Set<ShoppingListDataEntry> missing = new TreeSet<>();
		private final Set<ShoppingListDataEntry> required = new TreeSet<>();
		private final Set<ShoppingListDataEntry> owned = new TreeSet<>();
		private final StringBuilder missingBuilder = new StringBuilder();
		private final StringBuilder requiredBuilder = new StringBuilder();
		private final StringBuilder ownedBuilder = new StringBuilder();
		private boolean missingEmpty = true;
		private boolean requiredEmpty = true;
		private boolean ownedEmpty = true;

		public ShoppingListData(ShoppingListType type) {
			this.type = type;
		}

		public String get(Object output) {
			if (TabsStockpile.get().itemsMissing().equals(output)) {
				return missingBuilder.toString();
			} else if (TabsStockpile.get().itemsOwned().equals(output)) {
				return ownedBuilder.toString();
			} else {
				return requiredBuilder.toString();
			}
		}

		private void printOwned(long count, Item item, boolean bpc, boolean bpo, boolean runs) {
			if (count > 0) {
				ownedEmpty = false;
				owned.add(new ShoppingListDataEntry(count, item, bpc, bpo, runs));
			}
		}

		private void printRequired(long count, Item item, boolean bpc, boolean bpo, boolean runs) {
			if (count > 0) {
				requiredEmpty = false;
				required.add(new ShoppingListDataEntry(count, item, bpc, bpo, runs));
			}
		}

		private void printMissing(long count, Item item, boolean bpc, boolean bpo, boolean runs) {
			if (count > 0) {
				missingEmpty = false;
				missing.add(new ShoppingListDataEntry(count, item, bpc, bpo, runs));
			}
		}

		private void printItems() {
			for (ShoppingListDataEntry entry : missing) {
				entry.print(type, missingBuilder);
			}
			for (ShoppingListDataEntry entry : required) {
				entry.print(type, requiredBuilder);
			}
			for (ShoppingListDataEntry entry : owned) {
				entry.print(type, ownedBuilder);
			}
		}

		private void printFooterOwned(double volume, double value) {
			type.printFooter(ownedBuilder, volume, value, ownedEmpty);
		}
		private void printFooterRequired(double volume, double value) {
			type.printFooter(requiredBuilder, volume, value, requiredEmpty);
		}
		private void printFooterMissing(double volume, double value) {
			type.printFooter(missingBuilder, volume, value, missingEmpty);
		}

		private void printHeader(StringBuilder stockpileNamesBuilder, StringBuilder subpileNamesBuilder, Map<String, Double> subpiles, long percent) {
			type.printHeader(stockpileNamesBuilder, subpileNamesBuilder, ownedBuilder, subpiles, percent);
			type.printHeader(stockpileNamesBuilder, subpileNamesBuilder, requiredBuilder, subpiles, percent);
			type.printHeader(stockpileNamesBuilder, subpileNamesBuilder, missingBuilder, subpiles, percent);
		}	
	}

	private static class ShoppingListDataEntry implements Comparable<ShoppingListDataEntry> {
		private final long count;
		private final Item item;
		private final boolean bpc;
		private final boolean bpo;
		private final boolean runs;

		public ShoppingListDataEntry(long count, Item item, boolean bpc, boolean bpo, boolean runs) {
			this.count = count;
			this.item = item;
			this.bpc = bpc;
			this.bpo = bpo;
			this.runs = runs;
		}

		public void print(ShoppingListType type, StringBuilder builder) {
			type.printItem(builder, count, item, bpc, bpo, runs);
		}

		@Override
		public int compareTo(ShoppingListDataEntry o) {
			int compared;
			/*
			//Sort by group first (defualt in the table)
			compared = item.getGroup().compareTo(o.item.getGroup());
			if (compared != 0) {
				return compared;
			}
			*/
			compared = this.item.getTypeName().compareTo(o.item.getTypeName());
			if (compared != 0) {
				return compared;
			}
			compared = Boolean.compare(this.bpc, o.bpc);
			if (compared != 0) {
				return compared;
			}
			compared = Boolean.compare(this.bpo, o.bpo);
			if (compared != 0) {
				return compared;
			}
			compared = Boolean.compare(this.runs, o.runs);
			if (compared != 0) {
				return compared;
			}
			return 0;
		}

		@Override
		public int hashCode() {
			int hash = 5;
			hash = 41 * hash + (int) (this.count ^ (this.count >>> 32));
			hash = 41 * hash + Objects.hashCode(this.item);
			hash = 41 * hash + (this.bpc ? 1 : 0);
			hash = 41 * hash + (this.bpo ? 1 : 0);
			hash = 41 * hash + (this.runs ? 1 : 0);
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
			final ShoppingListDataEntry other = (ShoppingListDataEntry) obj;
			if (this.count != other.count) {
				return false;
			}
			if (this.bpc != other.bpc) {
				return false;
			}
			if (this.bpo != other.bpo) {
				return false;
			}
			if (this.runs != other.runs) {
				return false;
			}
			return Objects.equals(this.item, other.item);
		}
	}

	private static class IconListCellRendererRenderer implements ListCellRenderer<ShoppingListType> {
		DefaultListCellRenderer renderer = new DefaultListCellRenderer();

		@Override
		public Component getListCellRendererComponent(JList<? extends ShoppingListType> list, ShoppingListType value, int index, boolean isSelected, boolean cellHasFocus) {
			Component component = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (component instanceof JLabel) {
				JLabel jLabel = (JLabel) component;
				jLabel.setIcon(value.getIcon());
			}
			return component;
		}
	}
}
