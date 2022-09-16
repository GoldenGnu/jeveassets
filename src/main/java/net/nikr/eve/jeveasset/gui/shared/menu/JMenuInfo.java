/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

import ca.odell.glazedlists.SeparatorList;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.Border;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CopyHandler;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.InfoItem;
import net.nikr.eve.jeveasset.gui.tabs.loadout.Loadout;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material.MaterialType;
import net.nikr.eve.jeveasset.gui.tabs.overview.Overview;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;


public class JMenuInfo {

	private static Border border = null;
	private static final int COPY_DELAY = 500;

	private JMenuInfo() {
	}

	public static void treeAsset(final JComponent jComponent, final List<TreeAsset> list) {
		Set<TreeAsset> items = new HashSet<>();
		for (TreeAsset asset : list) {
			items.addAll(asset.getItems());
			if (asset.isItem()) {
				items.add(asset);
			}
		}
		infoItem(jComponent, new ArrayList<>(items));
	}

	public static void asset(final JComponent jComponent, final List<MyAsset> list) {
		infoItem(jComponent, new ArrayList<>(list));
	}

	public static void overview(final JComponent jComponent, final List<Overview> list) {
		infoItem(jComponent, new ArrayList<>(list));
	}

	private static void infoItem(final JComponent jComponent, final List<InfoItem> list) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			List<MenuItemValue> values = createDefault(jPopupMenu);

			double averageValue = 0;
			double totalValue = 0;
			long totalCount = 0;
			double totalVolume = 0;
			double totalReprocessed = 0;
			for (InfoItem infoItem : list) {
				totalValue = totalValue + infoItem.getValue();
				totalCount = totalCount + infoItem.getCount();
				totalVolume = totalVolume + infoItem.getVolumeTotal();
				totalReprocessed = totalReprocessed + infoItem.getValueReprocessed();
			}
			if (totalCount > 0 && totalValue > 0) {
				averageValue = totalValue / totalCount;
			}
			createMenuItem(values, jPopupMenu, totalValue, NumberFormat.ISK, GuiShared.get().selectionValue(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
			createMenuItem(values, jPopupMenu, totalReprocessed, NumberFormat.ISK, GuiShared.get().selectionValueReprocessed(), GuiShared.get().selectionShortReprocessedValue(), Images.SETTINGS_REPROCESSING.getIcon());
			createMenuItem(values, jPopupMenu, averageValue, NumberFormat.ISK, GuiShared.get().selectionAverage(), GuiShared.get().selectionShortAverage(), Images.ASSETS_AVERAGE.getIcon());
			createMenuItem(values, jPopupMenu, totalVolume, NumberFormat.DOUBLE, GuiShared.get().selectionVolume(), GuiShared.get().selectionShortVolume(), Images.ASSETS_VOLUME.getIcon());
			createMenuItem(values, jPopupMenu, totalCount, NumberFormat.ITEMS, GuiShared.get().selectionCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
		}
	}

	public static void contracts(final Program program, final JComponent jComponent, final List<MyContractItem> list) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			List<MenuItemValue> values = createDefault(jPopupMenu);

			double sellingPrice = 0;
			double sellingAssets = 0;
			double buying = 0;
			double sold = 0;
			double bought = 0;
			Set<MyContract> contracts = new HashSet<>();
			for (Object object : list) {
				if (object instanceof SeparatorList.Separator) {
					continue;
				}
				if (object == null) {
					continue;
				}
				MyContractItem contractItem = (MyContractItem) object;
				contracts.add(contractItem.getContract());
				MyContract contract = contractItem.getContract();
				if (contract.isIgnoreContract()) {
					continue;
				}
				boolean isIssuer = contract.isForCorp() ? program.getOwners().keySet().contains(contract.getIssuerCorpID()) : program.getOwners().keySet().contains(contract.getIssuerID());
				if (isIssuer && //Issuer
						contract.isOpen() //Not completed
						&& contractItem.isIncluded()) { //Selling
					sellingAssets = sellingAssets + contractItem.getDynamicPrice() * contractItem.getQuantity();
				}
			}
			for (MyContract contract : contracts) {
				if (contract.isIgnoreContract()) {
					continue;
				}
				boolean isIssuer = contract.isForCorp() ? program.getOwners().keySet().contains(contract.getIssuerCorpID()) : program.getOwners().keySet().contains(contract.getIssuerID());
				boolean isAcceptor = contract.getAcceptorID() > 0 && program.getOwners().keySet().contains(contract.getAcceptorID());
				if (isIssuer //Issuer
						&& contract.isOpen() //Not completed
						) { //Selling/Buying
					sellingPrice = sellingPrice + contract.getPrice(); //Positive
					buying = buying - contract.getReward(); //Negative
				} else if (contract.isCompletedSuccessful()) { //Completed
					if (isIssuer) { //Sold/Bought
						sold = sold + contract.getPrice(); //Positive
						bought = bought - contract.getReward(); //Negative
					}
					if (isAcceptor) { //Reverse of the above
						sold = sold + contract.getReward(); //Positive
						bought = bought - contract.getPrice(); //Negative
					}
				}
			}
			List<MenuItemValue> sell = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleSell(), Images.ORDERS_SELL.getIcon());
			createMenuItem(sell, jPopupMenu, sellingPrice, NumberFormat.ISK, GuiShared.get().selectionContractsSellingPriceToolTip(), GuiShared.get().selectionContractsSellingPrice(), Images.ORDERS_SELL.getIcon());
			createMenuItem(sell, jPopupMenu, sellingAssets, NumberFormat.ISK, GuiShared.get().selectionContractsSellingAssetsToolTip(), GuiShared.get().selectionContractsSellingAssets(), Images.TOOL_VALUES.getIcon());
			createMenuItem(sell, jPopupMenu, sold, NumberFormat.ISK, GuiShared.get().selectionContractsSoldToolTip(), GuiShared.get().selectionContractsSold(), Images.ORDERS_SOLD.getIcon());
			values.addAll(sell);
			List<MenuItemValue> buy = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleBuy(), Images.ORDERS_BUY.getIcon());
			createMenuItem(buy, jPopupMenu, buying, NumberFormat.ISK, GuiShared.get().selectionContractsBuyingToolTip(), GuiShared.get().selectionContractsBuying(), Images.ORDERS_BUY.getIcon());
			createMenuItem(buy, jPopupMenu, bought, NumberFormat.ISK, GuiShared.get().selectionContractsBoughtToolTip(), GuiShared.get().selectionContractsBought(), Images.ORDERS_BOUGHT.getIcon());
			values.addAll(buy);
		}
	}

	public static void marketOrder(final JComponent jComponent, final List<MyMarketOrder> list) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			List<MenuItemValue> values = createDefault(jPopupMenu);

			double sellOrdersTotal = 0;
			double sellBrokersFeeTotal = 0;
			double buyOrdersTotal = 0;
			double buyBrokersFeeTotal = 0;
			double toCoverTotal = 0;
			double escrowTotal = 0;
			long volumeRemain = 0;
			long volumeTotal = 0;
			for (MyMarketOrder marketOrder : list) {
				if (marketOrder.isBuyOrder()) { //Buy
					buyOrdersTotal += marketOrder.getPrice() * marketOrder.getVolumeTotal();
					escrowTotal += marketOrder.getEscrow();
					toCoverTotal += (marketOrder.getPrice() * marketOrder.getVolumeTotal()) - marketOrder.getEscrow();
					buyBrokersFeeTotal += marketOrder.getBrokersFeeNotNull();
				} else { //Sell
					sellOrdersTotal += marketOrder.getPrice() * marketOrder.getVolumeTotal();
					sellBrokersFeeTotal += marketOrder.getBrokersFeeNotNull();
				}
				volumeRemain += marketOrder.getVolumeRemain();
				volumeTotal += marketOrder.getVolumeTotal();
			}
			createMenuItem(values, jPopupMenu, GuiShared.get().selectionOrdersCountValue(Formater.longFormat(volumeRemain), Formater.itemsFormat(volumeTotal)), GuiShared.get().selectionOrdersCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
			createMenuItem(values, jPopupMenu, sellOrdersTotal + sellBrokersFeeTotal, NumberFormat.ISK, GuiShared.get().selectionOrdersSellTotal(), GuiShared.get().selectionShortSell(), Images.ORDERS_SELL.getIcon());
			createMenuItem(values, jPopupMenu, buyOrdersTotal + buyBrokersFeeTotal, NumberFormat.ISK, GuiShared.get().selectionOrdersBuyTotal(), GuiShared.get().selectionShortBuy(), Images.ORDERS_BUY.getIcon());
			createMenuItem(values, jPopupMenu, escrowTotal, NumberFormat.ISK, GuiShared.get().selectionOrdersBuyEscrow(), GuiShared.get().selectionShortEscrow(), Images.ORDERS_ESCROW.getIcon());
			createMenuItem(values, jPopupMenu, toCoverTotal, NumberFormat.ISK, GuiShared.get().selectionOrdersBuyToCover(), GuiShared.get().selectionShortIskToCover(), Images.ORDERS_TO_COVER.getIcon());
			createMenuItem(values, jPopupMenu, sellBrokersFeeTotal + buyBrokersFeeTotal, NumberFormat.ISK, GuiShared.get().selectionOrdersBrokersFee(), GuiShared.get().selectionShortBrokerFees(), Images.MISC_COLLAPSED.getIcon());
		}
	}

	public static void transctions(final JComponent jComponent, final List<MyTransaction> transactions) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			List<MenuItemValue> values = createDefault(jPopupMenu);

			double sellTotal = 0;
			double sellTaxTotal = 0;
			double buyTotal = 0;
			long sellCount = 0;
			long buyCount = 0;
			for (MyTransaction transaction : transactions) {
				if (transaction.isSell()) { //Sell
					sellTotal += transaction.getPrice() * transaction.getQuantity();
					sellCount += transaction.getQuantity();
					sellTaxTotal += transaction.getTaxNotNull();
				} else { //Buy
					buyTotal += transaction.getPrice() * transaction.getQuantity();
					buyCount += transaction.getQuantity();
				}
			}
			double sellAvg = 0;
			if (sellTotal > 0 && sellCount > 0) {
				sellAvg = (sellTotal + sellTaxTotal) / sellCount;
			}
			double buyAvg = 0;
			if (buyTotal > 0 && buyCount > 0) {
				buyAvg = buyTotal / buyCount;
			}
			double bothTotal = sellTotal + buyTotal;
			double bothCount = sellCount + buyCount;
			double bothAvg = 0;
			if (bothTotal > 0 && bothCount > 0) {
				bothAvg = (bothTotal + sellTaxTotal) / bothCount;
			}
			//Sell
			List<MenuItemValue> sell = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleSell(), Images.ORDERS_SELL.getIcon());
			createMenuItem(sell, jPopupMenu, sellCount, NumberFormat.ITEMS, GuiShared.get().selectionTransactionsSellCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
			createMenuItem(sell, jPopupMenu, sellTotal + sellTaxTotal, NumberFormat.ISK, GuiShared.get().selectionTransactionsSellTotal(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
			createMenuItem(sell, jPopupMenu, sellAvg, NumberFormat.ISK, GuiShared.get().selectionTransactionsSellAvg(), GuiShared.get().selectionShortAverage(), Images.ASSETS_AVERAGE.getIcon());
			createMenuItem(sell, jPopupMenu, sellTaxTotal, NumberFormat.ISK, GuiShared.get().selectionTransactionsSellTax(), GuiShared.get().selectionShortTax(), Images.MISC_COLLAPSED.getIcon());
			values.addAll(sell);
			//Both
			List<MenuItemValue> both = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleBoth(), Images.TOOL_TRANSACTION.getIcon());
			createMenuItem(both, jPopupMenu, sellCount + buyCount, NumberFormat.ITEMS, GuiShared.get().selectionTransactionsBothCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
			createMenuItem(both, jPopupMenu, bothTotal + sellTaxTotal, NumberFormat.ISK, GuiShared.get().selectionTransactionsBothTotal(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
			createMenuItem(both, jPopupMenu, bothAvg, NumberFormat.ISK, GuiShared.get().selectionTransactionsBothAvg(), GuiShared.get().selectionShortAverage(), Images.ASSETS_AVERAGE.getIcon());
			values.addAll(both);
			//Buy
			List<MenuItemValue> buy = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleBuy(), Images.ORDERS_BUY.getIcon());
			createMenuItem(buy, jPopupMenu, buyCount, NumberFormat.ITEMS, GuiShared.get().selectionTransactionsBuyCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
			createMenuItem(buy, jPopupMenu, buyTotal, NumberFormat.ISK, GuiShared.get().selectionTransactionsBuyTotal(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
			createMenuItem(buy, jPopupMenu, buyAvg, NumberFormat.ISK, GuiShared.get().selectionTransactionsBuyAvg(), GuiShared.get().selectionShortAverage(), Images.ASSETS_AVERAGE.getIcon());
			values.addAll(buy);
		}
	}

	public static void industryJob(final JComponent jComponent, final List<MyIndustryJob> list) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			List<MenuItemValue> values = createDefault(jPopupMenu);

			int inventionCount = 0;
			long count = 0;
			double success = 0;
			double outputValue = 0;
			for (MyIndustryJob industryJob : list) {
				count++;
				if (industryJob.isInvention() && industryJob.isCompleted()) {
					inventionCount++;
					if (industryJob.isDelivered()) {
						success++;
					}
				}
				if (!industryJob.isDelivered()) { //Only include active jobs
					outputValue += industryJob.getOutputValue();
				}
			}
			if (inventionCount <= 0) {
				createMenuItem(values, jPopupMenu, 0.0, NumberFormat.PERCENT, GuiShared.get().selectionInventionSuccess(), GuiShared.get().selectionShortInventionSuccess(), Images.JOBS_INVENTION_SUCCESS.getIcon());
			} else {
				createMenuItem(values, jPopupMenu, success / count, NumberFormat.PERCENT, GuiShared.get().selectionInventionSuccess(), GuiShared.get().selectionShortInventionSuccess(), Images.JOBS_INVENTION_SUCCESS.getIcon());
			}
			createMenuItem(values, jPopupMenu, outputValue, NumberFormat.ISK, GuiShared.get().selectionManufactureJobsValue(), GuiShared.get().selectionShortOutputValue(), Images.TOOL_VALUES.getIcon());
			createMenuItem(values, jPopupMenu, count, NumberFormat.ITEMS, GuiShared.get().selectionCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
		}
	}

	public static void stockpileItem(final JComponent jComponent, final List<StockpileItem> list) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			List<MenuItemValue> values = createDefault(jPopupMenu);

			double volumnNow = 0;
			double volumnNeeded = 0;
			double valueNow = 0;
			double valueNeeded = 0;

			for (int i = 0; i < list.size(); i++) {
				Object object = list.get(i);
				if (object instanceof SeparatorList.Separator) {
					continue;
				}
				if (object instanceof StockpileTotal) {
					continue;
				}
				if (object == null) {
					continue;
				}
				StockpileItem item = (StockpileItem) object;
				volumnNow = volumnNow + item.getVolumeNow();
				if (item.getVolumeNeeded() < 0) { //Only add if negative
					volumnNeeded = volumnNeeded + item.getVolumeNeeded();
				}
				valueNow = valueNow + item.getValueNow();
				if (item.getValueNeeded() < 0) { //Only add if negative
					valueNeeded = valueNeeded + item.getValueNeeded();
				}
			}
			List<MenuItemValue> now = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleNow());
			createMenuItem(now, jPopupMenu, valueNow, NumberFormat.ISK, GuiShared.get().selectionValueNow(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
			createMenuItem(now, jPopupMenu, volumnNow, NumberFormat.DOUBLE, GuiShared.get().selectionVolumeNow(), GuiShared.get().selectionShortVolume(), Images.ASSETS_VOLUME.getIcon());
			values.addAll(now);
			List<MenuItemValue> needed = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleNeeded());
			createMenuItem(needed, jPopupMenu, valueNeeded, NumberFormat.ISK, GuiShared.get().selectionValueNeeded(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
			createMenuItem(needed, jPopupMenu, volumnNeeded, NumberFormat.DOUBLE, GuiShared.get().selectionVolumeNeeded(), GuiShared.get().selectionShortVolume(), Images.ASSETS_VOLUME.getIcon());
			values.addAll(needed);
		}
	}

	public static void material(final JComponent jComponent, final List<Material> selected, final List<Material> all) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			List<MenuItemValue> values = createDefault(jPopupMenu);

			MaterialTotal materialTotal = calcMaterialTotal(new ArrayList<>(selected), all);

			createMenuItem(values, jPopupMenu, materialTotal.getTotalValue(), NumberFormat.ISK, GuiShared.get().selectionValue(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
			createMenuItem(values, jPopupMenu, materialTotal.getAverageValue(), NumberFormat.ISK, GuiShared.get().selectionAverage(), GuiShared.get().selectionShortAverage(), Images.ASSETS_AVERAGE.getIcon());
			createMenuItem(values, jPopupMenu, materialTotal.getTotalCount(), NumberFormat.ITEMS, GuiShared.get().selectionCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
		}
	}

	static MaterialTotal calcMaterialTotal(final List<Material> selectedList, final List<Material> all) {
		//Remove none-Material classes
		List<Material> selected = new ArrayList<>(selectedList);
		for (int i = 0; i < selected.size(); i++) {
			Object object = selected.get(i);
			if (!(object instanceof Material)) {
				selected.remove(i);
				i--;
			}
		}
		long totalCount = 0;
		double totalValue = 0;
		double averageValue = 0;
		boolean add;
		for (Material material : all) {
			if (material.getType() == MaterialType.LOCATIONS) {
				add = false;
				for (Material selectedMaterial : selected) {
					//Equals anything/all
					if (selectedMaterial.getType() == MaterialType.SUMMARY_ALL) {
						add = true;
						break;
					}
					//Equals group
					if (selectedMaterial.getType() == MaterialType.SUMMARY_TOTAL
							&& material.getGroup().equals(selectedMaterial.getName())) {
						add = true;
						break;
					}
					//Equals name
					if (selectedMaterial.getType() == MaterialType.SUMMARY
							&& material.getName().equals(selectedMaterial.getName())) {
						add = true;
						break;
					}
					//Equals location
					if (selectedMaterial.getType() == MaterialType.LOCATIONS_ALL
							&& material.getHeader().equals(selectedMaterial.getHeader())) {
						add = true;
						break;
					}
					//Equals location and group
					if (selectedMaterial.getType() == MaterialType.LOCATIONS_TOTAL
							&& material.getHeader().equals(selectedMaterial.getHeader())
							&& material.getGroup().equals(selectedMaterial.getName())) {
						add = true;
						break;
					}
					//Equals location and name
					if (selectedMaterial.getType() == MaterialType.LOCATIONS
							&& material.getHeader().equals(selectedMaterial.getHeader())
							&& material.getName().equals(selectedMaterial.getName())) {
						add = true;
						break;
					}
				}
				if (add) {
					totalCount = totalCount + material.getCount();
					totalValue = totalValue + material.getValue();
				}
			}
		}
		if (totalCount > 0 && totalValue > 0) {
			averageValue = totalValue / totalCount;
		}
		return new MaterialTotal(totalCount, totalValue, averageValue);
	}

	public static void module(final JComponent jComponent, final List<Loadout> selected) {
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;

			List<MenuItemValue> values = createDefault(jPopupMenu);

			long totalCount = 0;
			double totalValue = 0;
			double averageValue = 0;
			Loadout totalShip = null;
			Loadout totalModule = null;
			Loadout totalAll = null;
			for (int i = 0; i < selected.size(); i++) {
				Object object = selected.get(i);
				if (object instanceof Loadout) {
					Loadout module = (Loadout) object;
					if (module.getName().equals(TabsLoadout.get().totalShip())) {
						totalShip = module;
					} else if (module.getName().equals(TabsLoadout.get().totalModules())) {
						totalModule = module;
					} else if (module.getName().equals(TabsLoadout.get().totalAll())) {
						totalAll = module;
						break;
					} else {
						totalCount = totalCount + module.getCount();
						totalValue = totalValue + module.getValue();
					}
				}
			}
			if (totalAll != null) { //All
				totalValue = totalAll.getValue();
				totalCount = totalAll.getCount();
			} else {
				if (totalModule != null) { //Module IS total
					totalValue = totalModule.getValue();
					totalCount = totalModule.getCount();
				}
				if (totalShip != null) { //Ship is added to total
					totalValue = totalValue + totalShip.getValue();
					totalCount = totalCount + totalShip.getCount();
				}
			}
			if (totalCount > 0 && totalValue > 0) {
				averageValue = totalValue / totalCount;
			}
			createMenuItem(values, jPopupMenu, totalValue, NumberFormat.ISK, GuiShared.get().selectionValue(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
			createMenuItem(values, jPopupMenu, averageValue, NumberFormat.ISK, GuiShared.get().selectionAverage(), GuiShared.get().selectionShortAverage(), Images.ASSETS_AVERAGE.getIcon());
			createMenuItem(values, jPopupMenu, totalCount, NumberFormat.ISK, GuiShared.get().selectionCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
		}
	}

	public static JMenuItem createMenuItem(List<MenuItemValue> values, final JPopupMenu jPopupMenu, final Number number, NumberFormat numberFormat, final String toolTipText, String shortText, final Icon icon) {
		return createMenuItem(values, jPopupMenu, null, number, numberFormat, toolTipText, shortText, icon);
	}

	public static JMenuItem createMenuItem(List<MenuItemValue> values, final JPopupMenu jPopupMenu, final String text, final String toolTipText, String shortText, final Icon icon) {
		return createMenuItem(values, jPopupMenu, text, null, null, toolTipText, shortText, icon);
	}

	private static JMenuItem createMenuItem(List<MenuItemValue> values, final JPopupMenu jPopupMenu, final String text, final Number number, NumberFormat numberFormat, final String toolTipText, String shortText, final Icon icon) {
		if (values != null) {
			values.add(new MenuItemValue(shortText, text, number));
		}
		JMenuItem jMenuItem;
		if (text == null) { //Numeric Value
			jMenuItem = new JMenuItem(format(number, numberFormat));
		} else { //Text value
			jMenuItem = new JMenuItem(text);
		}
		jMenuItem.setToolTipText(toolTipText);
		jMenuItem.setEnabled(false);
		jMenuItem.setDisabledIcon(icon);
		jMenuItem.setForeground(Color.BLACK);
		jMenuItem.setHorizontalAlignment(SwingConstants.RIGHT);
		if (jPopupMenu != null) {
			jPopupMenu.add(jMenuItem);
		}
		jMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (number != null) {
						CopyHandler.toClipboard(Formater.copyFormat(number));
					} else {
						CopyHandler.toClipboard(text);
					}
					jMenuItem.setText(GuiShared.get().selectionCopiedToClipboard());
					jMenuItem.setDisabledIcon(Images.EDIT_COPY.getIcon());
					final Timer timer = new Timer(COPY_DELAY, null);
					timer.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (text == null) {
								jMenuItem.setText(format(number, numberFormat));
							} else {
								jMenuItem.setText(text);
							}
							jMenuItem.setDisabledIcon(icon);
							timer.stop();
						}
					});
					timer.start();
				}
			}
		});
		return jMenuItem;
	}

	private static List<MenuItemValue> createMenuItemGroup(final JPopupMenu jPopupMenu, final String text) {
		return createMenuItemGroup(jPopupMenu, text, null);
	}

	private static List<MenuItemValue> createMenuItemGroup(final JPopupMenu jPopupMenu, final String text, final Icon icon) {
		List<MenuItemValue> values = new ArrayList<>();
		JMenuItem jMenuItem = new JMenuItem(text);
		if (icon != null) {
			jMenuItem.setDisabledIcon(icon);
		}
		values.add(new MenuItemValue(null, GuiShared.get().selectionShortGroup(text), null));
		jMenuItem.setEnabled(false);
		if (border == null) {
			border = BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createMatteBorder(1, 0, 0, 0, jMenuItem.getBackground().darker()),
							 BorderFactory.createMatteBorder(1, 0, 0, 0, jMenuItem.getBackground().brighter())),
					 BorderFactory.createCompoundBorder(
							BorderFactory.createMatteBorder(0, 0, 1, 0, jMenuItem.getBackground().brighter()),
							 BorderFactory.createMatteBorder(0, 0, 1, 0, jMenuItem.getBackground().darker())));
		}
		jMenuItem.setForeground(Color.BLACK);
		jMenuItem.setBorder(border);
		jMenuItem.setBorderPainted(true);
		jMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					CopyHandler.toClipboard(format(values));
					jMenuItem.setText(GuiShared.get().selectionCopiedToClipboard());
					jMenuItem.setDisabledIcon(Images.EDIT_COPY.getIcon());
					final Timer timer = new Timer(COPY_DELAY, null);
					timer.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							jMenuItem.setText(text);
							jMenuItem.setDisabledIcon(icon);
							timer.stop();
						}
					});
					timer.start();
				}
			}
		});
		jPopupMenu.add(jMenuItem);
		return values;
	}

	public static List<MenuItemValue> createDefault(final JPopupMenu jPopupMenu) {
		return createDefault(jPopupMenu, new JMenuItem());
	}

	public static List<MenuItemValue> createDefault(final JPopupMenu jPopupMenu, final JMenuItem jMenuItem) {
		final List<MenuItemValue> values = new ArrayList<>();
		values.add(new MenuItemValue(null, GuiShared.get().selectionTitle(), null));

		jPopupMenu.addSeparator();

		jMenuItem.setText(GuiShared.get().selectionTitle());
		jMenuItem.setDisabledIcon(Images.DIALOG_ABOUT.getIcon());
		jMenuItem.setEnabled(false);
		jMenuItem.setForeground(Color.BLACK);
		jMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					CopyHandler.toClipboard(format(values));
					jMenuItem.setText(GuiShared.get().selectionCopiedToClipboard());
					jMenuItem.setDisabledIcon(Images.EDIT_COPY.getIcon());
					final Timer timer = new Timer(COPY_DELAY, null);
					timer.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							jMenuItem.setText(GuiShared.get().selectionTitle());
							jMenuItem.setDisabledIcon(Images.DIALOG_ABOUT.getIcon());
							timer.stop();
						}
					});
					timer.start();
				}
			}
		});
		jPopupMenu.add(jMenuItem);

		JPanel jSpacePanel = new JPanel();
		jSpacePanel.setOpaque(false);
		jSpacePanel.setMinimumSize(new Dimension(50, 5));
		jSpacePanel.setPreferredSize(new Dimension(50, 5));
		jSpacePanel.setMaximumSize(new Dimension(50, 5));
		jPopupMenu.add(jSpacePanel);

		return values;
	}

	private static String format(final Number number, NumberFormat numberFormat) {
		switch(numberFormat) {
			case ISK: return Formater.iskFormat(number);
			case DOUBLE: return Formater.doubleFormat(number);
			case ITEMS: return Formater.itemsFormat(number);
			case PERCENT: return Formater.percentFormat(number);
			case LONG: return Formater.longFormat(number);
			default: return String.valueOf(number);
		}
	}

	private static String format(final List<MenuItemValue> values) {
		StringBuilder builder = new StringBuilder();
		for (MenuItemValue item : values) {
			if (item.type != null) {
				builder.append(item.type);
				builder.append(": ");
			}
			if (item.number != null) {
				builder.append(Formater.copyFormat(item.number));
				builder.append("\r\n");
			} else if (item.text != null) {
				builder.append(item.text);
				builder.append("\r\n");
			} else {
				builder.append("\r\n");
			}
		}
		return builder.toString();
	}

	public static class MaterialTotal {

		private final long totalCount;
		private final double totalValue;
		private final double averageValue;

		public MaterialTotal(final long totalCount, final double totalValue, final double averageValue) {
			this.totalCount = totalCount;
			this.totalValue = totalValue;
			this.averageValue = averageValue;
		}

		public double getAverageValue() {
			return averageValue;
		}

		public long getTotalCount() {
			return totalCount;
		}

		public double getTotalValue() {
			return totalValue;
		}
	}

	public static class MenuItemValue {
		private final String type;
		private final String text;
		private final Number number;

		public MenuItemValue(String type, String text, Number number) {
			this.type = type;
			this.text = text;
			this.number = number;
		}

	}

	public enum NumberFormat {
		ISK, DOUBLE, ITEMS, PERCENT, LONG
	}

	public interface InfoItem {

		double getValue();

		long getCount();

		double getVolumeTotal();

		double getValueReprocessed();
	}
}
