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
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.InfoItem;
import net.nikr.eve.jeveasset.gui.tabs.loadout.Loadout;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material.MaterialType;
import net.nikr.eve.jeveasset.gui.tabs.overview.Overview;
import net.nikr.eve.jeveasset.gui.tabs.slots.Slots;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;


public class JMenuInfo {

	private static Border border = null;
	public static final int COPY_DELAY = 500;

	private JMenuInfo() {
	}

	public static void treeAsset(final JPopupMenu jPopupMenu, final List<TreeAsset> list) {
		Set<TreeAsset> items = new HashSet<>();
		for (TreeAsset asset : list) {
			items.addAll(asset.getItems());
			if (asset.isItem()) {
				items.add(asset);
			}
		}
		infoItem(jPopupMenu, new ArrayList<>(items));
	}

	public static void asset(final JPopupMenu jPopupMenu, final List<MyAsset> list) {
		infoItem(jPopupMenu, new ArrayList<>(list));
	}

	public static void overview(final JPopupMenu jPopupMenu, final List<Overview> list) {
		infoItem(jPopupMenu, new ArrayList<>(list));
	}

	private static void infoItem(final JPopupMenu jPopupMenu, final List<InfoItem> list) {
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
		createMenuItem(values, jPopupMenu, totalValue, AutoNumberFormat.ISK, GuiShared.get().selectionValue(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
		createMenuItem(values, jPopupMenu, totalReprocessed, AutoNumberFormat.ISK, GuiShared.get().selectionValueReprocessed(), GuiShared.get().selectionShortReprocessedValue(), Images.SETTINGS_REPROCESSING.getIcon());
		createMenuItem(values, jPopupMenu, averageValue, AutoNumberFormat.ISK, GuiShared.get().selectionAverage(), GuiShared.get().selectionShortAverage(), Images.ASSETS_AVERAGE.getIcon());
		createMenuItem(values, jPopupMenu, totalVolume, AutoNumberFormat.DOUBLE, GuiShared.get().selectionVolume(), GuiShared.get().selectionShortVolume(), Images.ASSETS_VOLUME.getIcon());
		createMenuItem(values, jPopupMenu, totalCount, AutoNumberFormat.ITEMS, GuiShared.get().selectionCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
	}

	public static void contracts(final Program program, final JPopupMenu jPopupMenu, final List<MyContractItem> list) {
		List<MenuItemValue> values = createDefault(jPopupMenu);

		double sellingPrice = 0;
		double sellingAssets = 0;
		double buying = 0;
		double sold = 0;
		double bought = 0;
		double collateralIssuer = 0;
		double collateralAcceptor = 0;
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
			boolean isIssuer = contract.isForCorp() ? program.getOwners().keySet().contains(contract.getIssuerCorpID()) : program.getOwners().keySet().contains(contract.getIssuerID());
			boolean isAcceptor = contract.getAcceptorID() > 0 && program.getOwners().keySet().contains(contract.getAcceptorID());
			if (contract.isCourierContract()) {
				if (isIssuer && (contract.isInProgress() || contract.isOpen())) { //Collateral Issuer
					collateralIssuer = collateralIssuer + contract.getCollateral();
				}
				if (isAcceptor && contract.isInProgress()) { //Collateral Acceptor
					collateralAcceptor = collateralAcceptor + contract.getCollateral();
				}
			}
			if (contract.isIgnoreContract()) {
				continue;
			}
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
		createMenuItem(sell, jPopupMenu, sellingPrice, AutoNumberFormat.ISK, GuiShared.get().selectionContractsSellingPriceToolTip(), GuiShared.get().selectionContractsSellingPrice(), Images.ORDERS_SELL.getIcon());
		createMenuItem(sell, jPopupMenu, sellingAssets, AutoNumberFormat.ISK, GuiShared.get().selectionContractsSellingAssetsToolTip(), GuiShared.get().selectionContractsSellingAssets(), Images.TOOL_VALUES.getIcon());
		createMenuItem(sell, jPopupMenu, sold, AutoNumberFormat.ISK, GuiShared.get().selectionContractsSoldToolTip(), GuiShared.get().selectionContractsSold(), Images.ORDERS_SOLD.getIcon());
		values.addAll(sell);
		List<MenuItemValue> buy = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleBuy(), Images.ORDERS_BUY.getIcon());
		createMenuItem(buy, jPopupMenu, buying, AutoNumberFormat.ISK, GuiShared.get().selectionContractsBuyingToolTip(), GuiShared.get().selectionContractsBuying(), Images.ORDERS_BUY.getIcon());
		createMenuItem(buy, jPopupMenu, bought, AutoNumberFormat.ISK, GuiShared.get().selectionContractsBoughtToolTip(), GuiShared.get().selectionContractsBought(), Images.ORDERS_BOUGHT.getIcon());
		values.addAll(buy);
		List<MenuItemValue> collateral = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleCollateral(), Images.ORDERS_ESCROW.getIcon());
		createMenuItem(collateral, jPopupMenu, collateralIssuer, AutoNumberFormat.ISK, GuiShared.get().selectionContractsCollateralIssuerToolTip(), GuiShared.get().selectionContractsCollateralIssuer(), Images.ORDERS_ESCROW.getIcon());
		createMenuItem(collateral, jPopupMenu, collateralAcceptor, AutoNumberFormat.ISK, GuiShared.get().selectionContractsCollateralAcceptorToolTip(), GuiShared.get().selectionContractsCollateralAcceptor(), Images.UPDATE_WORKING.getIcon());
		values.addAll(collateral);
	}

	public static void marketOrder(final JPopupMenu jPopupMenu, final List<MyMarketOrder> list) {
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
		createMenuItem(values, jPopupMenu, GuiShared.get().selectionOrdersCountValue(Formatter.longFormat(volumeRemain), Formatter.itemsFormat(volumeTotal)), GuiShared.get().selectionOrdersCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
		createMenuItem(values, jPopupMenu, sellOrdersTotal + sellBrokersFeeTotal, AutoNumberFormat.ISK, GuiShared.get().selectionOrdersSellTotal(), GuiShared.get().selectionShortSell(), Images.ORDERS_SELL.getIcon());
		createMenuItem(values, jPopupMenu, buyOrdersTotal + buyBrokersFeeTotal, AutoNumberFormat.ISK, GuiShared.get().selectionOrdersBuyTotal(), GuiShared.get().selectionShortBuy(), Images.ORDERS_BUY.getIcon());
		createMenuItem(values, jPopupMenu, escrowTotal, AutoNumberFormat.ISK, GuiShared.get().selectionOrdersBuyEscrow(), GuiShared.get().selectionShortEscrow(), Images.ORDERS_ESCROW.getIcon());
		createMenuItem(values, jPopupMenu, toCoverTotal, AutoNumberFormat.ISK, GuiShared.get().selectionOrdersBuyToCover(), GuiShared.get().selectionShortIskToCover(), Images.ORDERS_TO_COVER.getIcon());
		createMenuItem(values, jPopupMenu, sellBrokersFeeTotal + buyBrokersFeeTotal, AutoNumberFormat.ISK, GuiShared.get().selectionOrdersBrokersFee(), GuiShared.get().selectionShortBrokerFees(), Images.MISC_COLLAPSED.getIcon());
	}

	public static void transctions(final JPopupMenu jPopupMenu, final List<MyTransaction> transactions) {
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
		createMenuItem(sell, jPopupMenu, sellCount, AutoNumberFormat.ITEMS, GuiShared.get().selectionTransactionsSellCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
		createMenuItem(sell, jPopupMenu, sellTotal + sellTaxTotal, AutoNumberFormat.ISK, GuiShared.get().selectionTransactionsSellTotal(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
		createMenuItem(sell, jPopupMenu, sellAvg, AutoNumberFormat.ISK, GuiShared.get().selectionTransactionsSellAvg(), GuiShared.get().selectionShortAverage(), Images.ASSETS_AVERAGE.getIcon());
		createMenuItem(sell, jPopupMenu, sellTaxTotal, AutoNumberFormat.ISK, GuiShared.get().selectionTransactionsSellTax(), GuiShared.get().selectionShortTax(), Images.MISC_COLLAPSED.getIcon());
		values.addAll(sell);
		//Both
		List<MenuItemValue> both = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleBoth(), Images.TOOL_TRANSACTION.getIcon());
		createMenuItem(both, jPopupMenu, sellCount + buyCount, AutoNumberFormat.ITEMS, GuiShared.get().selectionTransactionsBothCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
		createMenuItem(both, jPopupMenu, bothTotal + sellTaxTotal, AutoNumberFormat.ISK, GuiShared.get().selectionTransactionsBothTotal(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
		createMenuItem(both, jPopupMenu, bothAvg, AutoNumberFormat.ISK, GuiShared.get().selectionTransactionsBothAvg(), GuiShared.get().selectionShortAverage(), Images.ASSETS_AVERAGE.getIcon());
		values.addAll(both);
		//Buy
		List<MenuItemValue> buy = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleBuy(), Images.ORDERS_BUY.getIcon());
		createMenuItem(buy, jPopupMenu, buyCount, AutoNumberFormat.ITEMS, GuiShared.get().selectionTransactionsBuyCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
		createMenuItem(buy, jPopupMenu, buyTotal, AutoNumberFormat.ISK, GuiShared.get().selectionTransactionsBuyTotal(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
		createMenuItem(buy, jPopupMenu, buyAvg, AutoNumberFormat.ISK, GuiShared.get().selectionTransactionsBuyAvg(), GuiShared.get().selectionShortAverage(), Images.ASSETS_AVERAGE.getIcon());
		values.addAll(buy);
	}

	public static void slots(final JPopupMenu jPopupMenu, final List<Slots> list) {
		List<MenuItemValue> values = createDefault(jPopupMenu);

		Slots total = new Slots("");
		for (Slots slots : list) {
			if (slots.isGrandTotal()) {
				continue;
			}
			total.count(slots);
		}

		List<MenuItemValue> manufacturing = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionSlotsManufacturing(), Images.MISC_MANUFACTURING.getIcon());
		createMenuItem(manufacturing, jPopupMenu, total.getManufacturingDone(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsManufacturingDoneToolTip(), GuiShared.get().selectionSlotsManufacturingDone(), Images.EDIT_SET.getIcon());
		createMenuItem(manufacturing, jPopupMenu, total.getManufacturingFree(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsManufacturingFreeToolTip(), GuiShared.get().selectionSlotsManufacturingFree(), Images.EDIT_ADD.getIcon());
		createMenuItem(manufacturing, jPopupMenu, total.getManufacturingActive(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsManufacturingActiveToolTip(), GuiShared.get().selectionSlotsManufacturingActive(), Images.UPDATE_WORKING.getIcon());
		createMenuItem(manufacturing, jPopupMenu, total.getManufacturingMax(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsManufacturingMaxToolTip(), GuiShared.get().selectionSlotsManufacturingMax(), Images.UPDATE_DONE_OK.getIcon());
		values.addAll(manufacturing);

		List<MenuItemValue> research = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionSlotsResearch(), Images.MISC_INVENTION.getIcon());
		createMenuItem(research, jPopupMenu, total.getResearchDone(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsResearchDoneToolTip(), GuiShared.get().selectionSlotsResearchDone(), Images.EDIT_SET.getIcon());
		createMenuItem(research, jPopupMenu, total.getResearchFree(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsResearchFreeToolTip(), GuiShared.get().selectionSlotsResearchFree(), Images.EDIT_ADD.getIcon());
		createMenuItem(research, jPopupMenu, total.getResearchActive(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsResearchActiveToolTip(), GuiShared.get().selectionSlotsResearchActive(), Images.UPDATE_WORKING.getIcon());
		createMenuItem(research, jPopupMenu, total.getResearchMax(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsResearchMaxToolTip(), GuiShared.get().selectionSlotsResearchMax(), Images.UPDATE_DONE_OK.getIcon());
		values.addAll(research);

		List<MenuItemValue> reactions = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionSlotsReactions(), Images.MISC_REACTION.getIcon());
		createMenuItem(reactions, jPopupMenu, total.getReactionsDone(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsReactionsDoneToolTip(), GuiShared.get().selectionSlotsReactionsDone(), Images.EDIT_SET.getIcon());
		createMenuItem(reactions, jPopupMenu, total.getReactionsFree(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsReactionsFreeToolTip(), GuiShared.get().selectionSlotsReactionsFree(), Images.EDIT_ADD.getIcon());
		createMenuItem(reactions, jPopupMenu, total.getReactionsActive(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsReactionsActiveToolTip(), GuiShared.get().selectionSlotsReactionsActive(), Images.UPDATE_WORKING.getIcon());
		createMenuItem(reactions, jPopupMenu, total.getReactionsMax(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsReactionsMaxToolTip(), GuiShared.get().selectionSlotsReactionsMax(), Images.UPDATE_DONE_OK.getIcon());
		values.addAll(reactions);

		List<MenuItemValue> marketOrders = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionSlotsMarketOrders(), Images.MISC_MARKET_ORDERS.getIcon());
		createMenuItem(marketOrders, jPopupMenu, total.getMarketOrdersFree(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsMarketOrdersFreeToolTip(), GuiShared.get().selectionSlotsMarketOrdersFree(), Images.EDIT_ADD.getIcon());
		createMenuItem(marketOrders, jPopupMenu, total.getMarketOrdersActive(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsMarketOrdersActiveToolTip(), GuiShared.get().selectionSlotsMarketOrdersActive(), Images.UPDATE_WORKING.getIcon());
		createMenuItem(marketOrders, jPopupMenu, total.getMarketOrdersMax(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsMarketOrdersMaxToolTip(), GuiShared.get().selectionSlotsMarketOrdersMax(), Images.UPDATE_DONE_OK.getIcon());
		values.addAll(marketOrders);

		List<MenuItemValue> contractCharacter = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionSlotsContractCharacter(), Images.MISC_CONTRACTS.getIcon());
		createMenuItem(contractCharacter, jPopupMenu, total.getContractCharacterFree(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsContractCharacterFreeToolTip(), GuiShared.get().selectionSlotsContractCharacterFree(), Images.EDIT_ADD.getIcon());
		createMenuItem(contractCharacter, jPopupMenu, total.getContractCharacterActive(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsContractCharacterActiveToolTip(), GuiShared.get().selectionSlotsContractCharacterActive(), Images.UPDATE_WORKING.getIcon());
		createMenuItem(contractCharacter, jPopupMenu, total.getContractCharacterMax(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsContractCharacterMaxToolTip(), GuiShared.get().selectionSlotsContractCharacterMax(), Images.UPDATE_DONE_OK.getIcon());
		values.addAll(contractCharacter);

		List<MenuItemValue> contractCorporation = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionSlotsContractCorporation(), Images.MISC_CONTRACTS_CORP.getIcon());
		createMenuItem(contractCorporation, jPopupMenu, total.getContractCorporationFree(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsContractCorporationFreeToolTip(), GuiShared.get().selectionSlotsContractCorporationFree(), Images.EDIT_ADD.getIcon());
		createMenuItem(contractCorporation, jPopupMenu, total.getContractCorporationActive(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsContractCorporationActiveToolTip(), GuiShared.get().selectionSlotsContractCorporationActive(), Images.UPDATE_WORKING.getIcon());
		createMenuItem(contractCorporation, jPopupMenu, total.getContractCorporationMax(), AutoNumberFormat.LONG, GuiShared.get().selectionSlotsContractCorporationMaxToolTip(), GuiShared.get().selectionSlotsContractCorporationMax(), Images.UPDATE_DONE_OK.getIcon());
		values.addAll(contractCorporation);
	}

	public static void industryJob(final JPopupMenu jPopupMenu, final List<MyIndustryJob> list) {
		List<MenuItemValue> values = createDefault(jPopupMenu);

		int inventionCount = 0;
		long count = 0;
		double success = 0;
		double outputValue = 0;
		for (MyIndustryJob industryJob : list) {
			count++;
			if (industryJob.isInvention() && industryJob.isDone()) {
				inventionCount++;
				if (industryJob.isCompletedSuccessful()) {
					success++;
				}
			}
			if (industryJob.isNotDeliveredToAssets()) { //Only include active jobs
				outputValue += industryJob.getOutputValue();
			}
		}
		if (inventionCount <= 0) {
			createMenuItem(values, jPopupMenu, 0.0, AutoNumberFormat.PERCENT, GuiShared.get().selectionInventionSuccess(), GuiShared.get().selectionShortInventionSuccess(), Images.JOBS_INVENTION_SUCCESS.getIcon());
		} else {
			createMenuItem(values, jPopupMenu, success / count, AutoNumberFormat.PERCENT, GuiShared.get().selectionInventionSuccess(), GuiShared.get().selectionShortInventionSuccess(), Images.JOBS_INVENTION_SUCCESS.getIcon());
		}
		createMenuItem(values, jPopupMenu, outputValue, AutoNumberFormat.ISK, GuiShared.get().selectionManufactureJobsValue(), GuiShared.get().selectionShortOutputValue(), Images.TOOL_VALUES.getIcon());
		createMenuItem(values, jPopupMenu, count, AutoNumberFormat.ITEMS, GuiShared.get().selectionCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
	}

	public static void stockpileItem(final JPopupMenu jPopupMenu, final List<StockpileItem> list) {
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
		createMenuItem(now, jPopupMenu, valueNow, AutoNumberFormat.ISK, GuiShared.get().selectionValueNow(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
		createMenuItem(now, jPopupMenu, volumnNow, AutoNumberFormat.DOUBLE, GuiShared.get().selectionVolumeNow(), GuiShared.get().selectionShortVolume(), Images.ASSETS_VOLUME.getIcon());
		values.addAll(now);
		List<MenuItemValue> needed = createMenuItemGroup(jPopupMenu, GuiShared.get().selectionTitleNeeded());
		createMenuItem(needed, jPopupMenu, valueNeeded, AutoNumberFormat.ISK, GuiShared.get().selectionValueNeeded(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
		createMenuItem(needed, jPopupMenu, volumnNeeded, AutoNumberFormat.DOUBLE, GuiShared.get().selectionVolumeNeeded(), GuiShared.get().selectionShortVolume(), Images.ASSETS_VOLUME.getIcon());
		values.addAll(needed);
	}

	public static void material(final JPopupMenu jPopupMenu, final List<Material> selected, final List<Material> all) {
		List<MenuItemValue> values = createDefault(jPopupMenu);

		MaterialTotal materialTotal = calcMaterialTotal(new ArrayList<>(selected), all);

		createMenuItem(values, jPopupMenu, materialTotal.getTotalValue(), AutoNumberFormat.ISK, GuiShared.get().selectionValue(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
		createMenuItem(values, jPopupMenu, materialTotal.getAverageValue(), AutoNumberFormat.ISK, GuiShared.get().selectionAverage(), GuiShared.get().selectionShortAverage(), Images.ASSETS_AVERAGE.getIcon());
		createMenuItem(values, jPopupMenu, materialTotal.getTotalCount(), AutoNumberFormat.ITEMS, GuiShared.get().selectionCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
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

	public static void module(final JPopupMenu jPopupMenu, final List<Loadout> selected) {
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
		createMenuItem(values, jPopupMenu, totalValue, AutoNumberFormat.ISK, GuiShared.get().selectionValue(), GuiShared.get().selectionShortValue(), Images.TOOL_VALUES.getIcon());
		createMenuItem(values, jPopupMenu, averageValue, AutoNumberFormat.ISK, GuiShared.get().selectionAverage(), GuiShared.get().selectionShortAverage(), Images.ASSETS_AVERAGE.getIcon());
		createMenuItem(values, jPopupMenu, totalCount, AutoNumberFormat.ISK, GuiShared.get().selectionCount(), GuiShared.get().selectionShortCount(), Images.EDIT_ADD.getIcon());
	}

	public static JMenuItem createMenuItem(List<MenuItemValue> values, final JPopupMenu jPopupMenu, final Number number, AutoNumberFormat numberFormat, final String toolTip, String copyText, final Icon icon) {
		return createMenuItem(values, jPopupMenu, null, number, numberFormat, toolTip, copyText, icon);
	}

	public static JMenuItem createMenuItem(List<MenuItemValue> values, final JPopupMenu jPopupMenu, final String text, final String toolTip, String copyText, final Icon icon) {
		return createMenuItem(values, jPopupMenu, text, null, null, toolTip, copyText, icon);
	}

	private static JMenuItem createMenuItem(List<MenuItemValue> values, final JPopupMenu jPopupMenu, final String text, final Number number, AutoNumberFormat numberFormat, final String toolTip, String copyText, final Icon icon) {
		if (values != null) {
			values.add(new MenuItemValue(copyText, text, number));
		}
		JMenuItem jMenuItem;
		if (text == null) { //Numeric Value
			jMenuItem = new JMenuItem(format(number, numberFormat));
		} else { //Text value
			jMenuItem = new JMenuItem(text);
		}
		jMenuItem.setToolTipText(GuiShared.get().clickToCopyWrap(toolTip));
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
						CopyHandler.toClipboard(Formatter.copyFormat(number));
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
		jMenuItem.setToolTipText(GuiShared.get().clickToCopyGroup());
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
		return createDefault(jPopupMenu, new JMenuItem(), GuiShared.get().selectionTitle(), GuiShared.get().clickToCopySelectionInfo(), Images.DIALOG_ABOUT.getIcon());
	}

	public static List<MenuItemValue> createDefault(final JPopupMenu jPopupMenu, final JMenuItem jMenuItem, String title, String toolTip, Icon icon) {
		final List<MenuItemValue> values = new ArrayList<>();
		values.add(new MenuItemValue(null, title, null));

		if (jPopupMenu != null) {
			jPopupMenu.addSeparator();
		}

		jMenuItem.setText(title);
		jMenuItem.setToolTipText(toolTip);
		jMenuItem.setDisabledIcon(icon);
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
		if (jPopupMenu != null) {
			jPopupMenu.add(jMenuItem);

			JPanel jSpacePanel = new JPanel();
			jSpacePanel.setOpaque(false);
			jSpacePanel.setMinimumSize(new Dimension(50, 5));
			jSpacePanel.setPreferredSize(new Dimension(50, 5));
			jSpacePanel.setMaximumSize(new Dimension(50, 5));
			jPopupMenu.add(jSpacePanel);
		}
		return values;
	}

	public static String format(final Number number, AutoNumberFormat numberFormat) {
		switch(numberFormat) {
			case ISK: return Formatter.iskFormat(number);
			case DOUBLE: return Formatter.doubleFormat(number);
			case ITEMS: return Formatter.itemsFormat(number);
			case PERCENT: return Formatter.percentFormat(number);
			case LONG: return Formatter.longFormat(number);
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
				builder.append(Formatter.copyFormat(item.number));
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

	public enum AutoNumberFormat {
		ISK, DOUBLE, ITEMS, PERCENT, LONG
	}

	public interface InfoItem {

		double getValue();

		long getCount();

		double getVolumeTotal();

		double getValueReprocessed();
	}
}
