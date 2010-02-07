/*
 * Copyright 2009, 2010
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.gui.dialogs;

import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import com.beimin.eveapi.balance.ApiAccountBalance;
import com.beimin.eveapi.order.ApiMarketOrder;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;


public class ValuesDialog extends JDialogCentered implements ActionListener, ListEventListener<EveAsset> {

	public final static String ACTION_VALUES_CLOSE = "ACTION_VALUES_CLOSE";
	public final static String ACTION_OWNER_SELECTED = "ACTION_OWNER_SELECTED";
	public final static String ACTION_CORP_SELECTED = "ACTION_CORP_SELECTED";

	private final String NAME_TOTAL = "Total";
	private final String NAME_WALLET_BALANCE = "Wallet balance";
	private final String NAME_ASSETS_VALUE = "Assets";
	private final String NAME_ASSETS_SELL_ORDERS = "Sell Orders";
	private final String NAME_ASSETS_ESCROWS = "Escrows";
	private final String NAME_BEST_ASSET = "Best asset";
	private final String NAME_BEST_SHIP = "Best ship";
	private final String NAME_BEST_MODULE = "Best module";


	private final int COLUMN_WIDTH = 243;




	//GUI
	private JComboBox jOwners;
	private JEditorPane jOwner;
	private JComboBox jCorps;
	private JEditorPane jCorp;
	private JEditorPane jAll;
	private JButton jClose;

	//Data
	private List<String> owners;
	private List<String> corps;
	private EventList<EveAsset> eveAssetEventList;
	private Map<String, Double> ownersTotalAccountBalance;
	private Map<String, Double> ownersTotalItemsValue;
	private Map<String, Long> ownersTotalItemsCount;
	private Map<String, Double> ownersTotalSellOrders;
	private Map<String, Double> ownersTotalBuyOrders;
	private Map<String, EveAsset> ownersBestItem;
	private Map<String, EveAsset> ownersBestModule;
	private Map<String, EveAsset> ownersBestShip;
	private Map<String, Double> corpsTotalAccountBalance;
	private Map<String, Double> corpsTotalItemsValue;
	private Map<String, Long> corpsTotalItemsCount;
	private Map<String, Double> corpsTotalSellOrders;
	private Map<String, Double> corpsTotalBuyOrders;
	private Map<String, EveAsset> corpsBestItem;
	private Map<String, EveAsset> corpsBestModule;
	private Map<String, EveAsset> corpsBestShip;


	private double totalItemsValue = 0;
	private long totalItemsCount = 0;
	private double totalAccountBalance = 0;
	private double totalSellOrders = 0;
	private double totalBuyOrders = 0;
	private EveAsset bestItem = null;
	private EveAsset bestModule = null;
	private EveAsset bestShip = null;


	private String backgroundHexColor;
	private String gridHexColor;

	public ValuesDialog(Program program, Image image) {
		super(program, "Values", image);

		backgroundHexColor = Integer.toHexString(dialog.getBackground().getRGB());
		backgroundHexColor = backgroundHexColor.substring(2, backgroundHexColor.length());

		gridHexColor = Integer.toHexString(dialog.getBackground().darker().getRGB());
		gridHexColor = gridHexColor.substring(2, gridHexColor.length());

		jOwners = new JComboBox();
		jOwners.setActionCommand(ACTION_OWNER_SELECTED);
		jOwners.addActionListener(this);
		jPanel.add(jOwners);

		jOwner = new JEditorPane("text/html","<html>");
		JCopyPopup.install(jOwner);
		jOwner.setEditable(false);
		jOwner.setOpaque(false);
		jPanel.add(jOwner);

		jCorps = new JComboBox();
		jCorps.setActionCommand(ACTION_CORP_SELECTED);
		jCorps.addActionListener(this);
		jPanel.add(jCorps);

		jCorp = new JEditorPane("text/html","<html>");
		JCopyPopup.install(jCorp);
		jCorp.setEditable(false);
		jCorp.setOpaque(false);
		jPanel.add(jCorp);


		jAll = new JEditorPane("text/html","<html>");
		JCopyPopup.install(jAll);
		jAll.setEditable(false);
		jAll.setOpaque(false);
		jPanel.add(jAll);

		jClose = new JButton("Close");
		jClose.setActionCommand(ACTION_VALUES_CLOSE);
		jClose.addActionListener(this);
		jPanel.add(jClose);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jAll, COLUMN_WIDTH, COLUMN_WIDTH, COLUMN_WIDTH)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jOwner, COLUMN_WIDTH, COLUMN_WIDTH, COLUMN_WIDTH)
							.addGroup(layout.createSequentialGroup()
								.addGap(3)
								.addComponent(jOwners, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jCorp, COLUMN_WIDTH, COLUMN_WIDTH, COLUMN_WIDTH)
							.addGroup(layout.createSequentialGroup()
								.addGap(3)
								.addComponent(jCorps, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							)
						)
					)
					.addComponent(jClose, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jOwners, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCorps, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jAll, 450, 450, 450)
					.addComponent(jOwner, 450, 450, 450)
					.addComponent(jCorp, 450, 450, 450)
				)
				
				.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
		eveAssetEventList = program.getEveAssetEventList();
		eveAssetEventList.addListEventListener(this);
		update();

	}

	public void update() {
		calcTotal();
		jOwners.removeAllItems();
		for (int a = 0; a < owners.size(); a++){
			jOwners.addItem(owners.get(a));
		}
		if (jOwners.getModel().getSize() > 0){
			jOwners.setEnabled(true);
		} else {
			jOwners.addItem("No character found");
			jOwners.setEnabled(false);
		}
		jOwners.setSelectedIndex(0);

		jCorps.removeAllItems();
		for (int a = 0; a < corps.size(); a++){
			jCorps.addItem(corps.get(a));
		}
		if (jCorps.getModel().getSize() > 0){
			jCorps.setEnabled(true);
		} else {
			jCorps.addItem("No corporation found");
			jCorps.setEnabled(false);
		}
		jCorps.setSelectedIndex(0);

		Output output = new Output("Grand Total");
		output.addHeading(NAME_TOTAL);
		if (totalAccountBalance != 0
				|| totalItemsValue != 0
				|| totalSellOrders != 0
				|| totalBuyOrders != 0
				){
			output.addValue(Formater.isk(totalAccountBalance
					+totalItemsValue
					+totalSellOrders
					+totalBuyOrders));
		}
		output.addNone();

		output.addHeading(NAME_WALLET_BALANCE);
		if (totalAccountBalance != 0) output.addValue(Formater.isk(totalAccountBalance));
		output.addNone();

		output.addHeading(NAME_ASSETS_VALUE);
		if (totalItemsValue != 0) output.addValue(Formater.isk(totalItemsValue));
		output.addNone();

		output.addHeading(NAME_ASSETS_SELL_ORDERS);
		if (totalSellOrders != 0) output.addValue(Formater.isk(totalSellOrders));
		output.addNone();

		output.addHeading(NAME_ASSETS_ESCROWS);
		if (totalBuyOrders != 0) output.addValue(Formater.isk(totalBuyOrders));
		output.addNone();

		output.addHeading(NAME_BEST_ASSET);
		if (bestItem != null){
			output.addValue(bestItem.getName()+"<br/>"+Formater.isk(bestItem.getPrice()));
			output.addNone();
		} else {
			output.addNone(2);
		}
		

		output.addHeading(NAME_BEST_SHIP);
		if (bestShip != null){
			output.addValue(bestShip.getName()+"<br/>"+Formater.isk(bestShip.getPrice()));
			output.addNone();
		} else {
			output.addNone(2);
		}
		

		output.addHeading(NAME_BEST_MODULE);
		if (bestModule != null){
			output.addValue(bestModule.getName()+"<br/>"+Formater.isk(bestModule.getPrice()));
			output.addNone();
		} else {
			output.addNone(2);
		}
		

		jAll.setText(output.getOutput());
	}

	private boolean calcTotal(){
		owners = new Vector<String>();
		corps = new Vector<String>();
		ownersTotalAccountBalance = new HashMap<String, Double>();
		ownersTotalItemsValue = new HashMap<String, Double>();
		ownersTotalItemsCount = new HashMap<String, Long>();
		ownersTotalSellOrders = new HashMap<String, Double>();
		ownersTotalBuyOrders = new HashMap<String, Double>();
		ownersBestItem = new HashMap<String, EveAsset>();
		ownersBestModule = new HashMap<String, EveAsset>();
		ownersBestShip = new HashMap<String, EveAsset>();
		corpsTotalAccountBalance = new HashMap<String, Double>();
		corpsTotalItemsValue = new HashMap<String, Double>();
		corpsTotalItemsCount = new HashMap<String, Long>();
		corpsTotalSellOrders = new HashMap<String, Double>();
		corpsTotalBuyOrders = new HashMap<String, Double>();
		corpsBestItem = new HashMap<String, EveAsset>();
		corpsBestModule = new HashMap<String, EveAsset>();
		corpsBestShip = new HashMap<String, EveAsset>();

		totalItemsCount = 0;
		totalItemsValue = 0;
		totalAccountBalance = 0;
		totalSellOrders = 0;
		totalBuyOrders = 0;
		bestItem = null;
		bestShip = null;
		bestModule = null;
		for (int a = 0; a < eveAssetEventList.size(); a++){
			EveAsset eveAsset = eveAssetEventList.get(a);

			//Skip market orders
			if (eveAsset.getFlag().equals("Market Order"))	continue;
			
			if (eveAsset.isCorporationAsset()){ //Corp Asset
				//Corp Total Value
				String corp = eveAsset.getOwner();
				double corpTotalValue = 0;
				if (corpsTotalItemsValue.containsKey(corp)){
					corpTotalValue = corpsTotalItemsValue.get(corp);
					corpsTotalItemsValue.remove(corp);
				}
				corpTotalValue = corpTotalValue + (eveAsset.getPrice() * eveAsset.getCount());
				corpsTotalItemsValue.put(eveAsset.getOwner(), corpTotalValue);

				//Corp Total Count
				long corpTotalCount = 0;
				if (corpsTotalItemsCount.containsKey(corp)){
					corpTotalCount = corpsTotalItemsCount.get(corp);
					corpsTotalItemsCount.remove(corp);
				}
				corpTotalCount = corpTotalCount + eveAsset.getCount();
				corpsTotalItemsCount.put(eveAsset.getOwner(), corpTotalCount);



				//Corp Best Item
				EveAsset corpBestItem = corpsBestItem.get(corp);
				if (corpBestItem == null) corpBestItem = eveAsset;

				if (corpBestItem.getPrice() <= eveAsset.getPrice()){
					if (corpsBestItem.containsValue(corpBestItem)) corpsBestItem.remove(corp);
					corpsBestItem.put(corp, eveAsset);
				}

				//Corp Best Module
				EveAsset corpBestModule = corpsBestModule.get(corp);
				if (corpBestModule == null && eveAsset.getCategory().equals("Module")) corpBestModule = eveAsset;

				if (corpBestModule != null){
					if (corpBestModule.getPrice() <= eveAsset.getPrice() && eveAsset.getCategory().equals("Module")){
						if (corpsBestModule.containsValue(corpBestModule)) corpsBestModule.remove(corp);
						corpsBestModule.put(corp, eveAsset);
					}
				}

				//Corp Best Module
				EveAsset corpBestShip = corpsBestShip.get(corp);
				if (corpBestShip == null && eveAsset.getCategory().equals("Ship")) corpBestShip = eveAsset;

				if (corpBestShip != null){
					if (corpBestShip.getPrice() <= eveAsset.getPrice() && eveAsset.getCategory().equals("Ship")){
						if (corpsBestShip.containsValue(corpBestShip)) corpsBestShip.remove(corp);
						corpsBestShip.put(corp, eveAsset);
					}
				}
			} else { //User Asset
				String owner = eveAsset.getOwner();
				//Owner Total Item Value
				double ownerTotalValue = 0;
				if (ownersTotalItemsValue.containsKey(owner)){
					ownerTotalValue = ownersTotalItemsValue.get(owner);
					ownersTotalItemsValue.remove(owner);
				}
				ownerTotalValue = ownerTotalValue + (eveAsset.getPrice() * eveAsset.getCount());
				ownersTotalItemsValue.put(eveAsset.getOwner(), ownerTotalValue);

				//Owner Total Count
				long ownerTotalCount = 0;
				if (ownersTotalItemsCount.containsKey(owner)){
					ownerTotalCount = ownersTotalItemsCount.get(owner);
					ownersTotalItemsCount.remove(owner);
				}
				ownerTotalCount = ownerTotalCount + eveAsset.getCount();
				ownersTotalItemsCount.put(eveAsset.getOwner(), ownerTotalCount);

				//Owner Best Item
				EveAsset ownerBestItem = ownersBestItem.get(owner);
				if (ownerBestItem == null) ownerBestItem = eveAsset;

				if (ownerBestItem.getPrice() <= eveAsset.getPrice()){
					if (ownersBestItem.containsValue(ownerBestItem)) ownersBestItem.remove(owner);
					ownersBestItem.put(owner, eveAsset);
				}

				//Owner Best Module
				EveAsset ownerBestModule = ownersBestModule.get(owner);
				if (ownerBestModule == null && eveAsset.getCategory().equals("Module")) ownerBestModule = eveAsset;

				if (ownerBestModule != null){
					if (ownerBestModule.getPrice() <= eveAsset.getPrice() && eveAsset.getCategory().equals("Module")){
						if (ownersBestModule.containsValue(ownerBestModule)) ownersBestModule.remove(owner);
						ownersBestModule.put(owner, eveAsset);
					}
				}

				//Owner Best Ship
				EveAsset ownerBestShip = ownersBestShip.get(owner);
				if (ownerBestShip == null && eveAsset.getCategory().equals("Ship")) ownerBestShip = eveAsset;

				if (ownerBestShip != null){
					if (ownerBestShip.getPrice() <= eveAsset.getPrice() && eveAsset.getCategory().equals("Ship")){
						if (ownersBestShip.containsValue(ownerBestShip)) ownersBestShip.remove(owner);
						ownersBestShip.put(owner, eveAsset);
					}
				}
			}
			//Total items value
			totalItemsValue = totalItemsValue + (eveAsset.getPrice() * eveAsset.getCount());

			//Total Items Count
			totalItemsCount = totalItemsCount + eveAsset.getCount();

			//Best Item
			if (bestItem == null) bestItem = eveAsset;
			if (bestItem.getPrice() <= eveAsset.getPrice()){
				bestItem = eveAsset;
			}

			//Best Module
			if (bestModule == null && eveAsset.getCategory().equals("Module")) bestModule = eveAsset;
			if (bestModule != null) {
				if (bestModule.getPrice() <= eveAsset.getPrice() && eveAsset.getCategory().equals("Module")){
					bestModule = eveAsset;
				}
			}

			//Best Ship
			if (bestShip == null && eveAsset.getCategory().equals("Ship")) bestShip = eveAsset;
			if (bestShip != null) {
				if (bestShip.getPrice() <= eveAsset.getPrice() && eveAsset.getCategory().equals("Ship")){
					bestShip = eveAsset;
				}
			}
		}
		List<Account> accounts = program.getSettings().getAccounts();
		for (int a = 0; a < accounts.size(); a++){
			List<Human> humans = accounts.get(a).getHumans();
			for (int b = 0; b < humans.size(); b++){
				Human human = humans.get(b);
				if (human.isShowAssets()){
					if (!owners.contains(human.getName())){
						owners.add(human.getName());

						//Account Balance
						List<ApiAccountBalance> accountBalances = human.getAccountBalances();
						double ownerTotalAccountBalance = 0;
						for (int c = 0; c < accountBalances.size(); c++){
							ApiAccountBalance accountBalance = accountBalances.get(c);
							totalAccountBalance = totalAccountBalance + accountBalance.getBalance();
							ownerTotalAccountBalance = ownerTotalAccountBalance + accountBalance.getBalance();
						}
						ownersTotalAccountBalance.put(human.getName(), ownerTotalAccountBalance);

						//Orders
						List<ApiMarketOrder> marketOrders = human.getMarketOrders();
						double ownerTotalSellOrders = 0;
						double ownerTotalBuyOrders = 0;
						for (int c = 0; c < marketOrders.size(); c++){
							ApiMarketOrder apiMarketOrder = marketOrders.get(c);
							if (apiMarketOrder.getOrderState() == 0){
								if (apiMarketOrder.getBid() < 1){ //Sell Orders
									ownerTotalSellOrders = ownerTotalSellOrders + (apiMarketOrder.getPrice() * apiMarketOrder.getVolRemaining());
								} else { //Buy Orders
									ownerTotalBuyOrders = ownerTotalBuyOrders + apiMarketOrder.getEscrow();
								}
							}
						}
						if (ownerTotalSellOrders != 0){
							ownersTotalSellOrders.put(human.getName(), ownerTotalSellOrders);
						}
						if (ownerTotalBuyOrders != 0){
							ownersTotalBuyOrders.put(human.getName(), ownerTotalBuyOrders);
						}
						totalSellOrders = totalSellOrders + ownerTotalSellOrders;
						totalBuyOrders = totalBuyOrders + ownerTotalBuyOrders;
						
					}
					if (human.isUpdateCorporationAssets() && !corps.contains(human.getCorporation())){
						corps.add(human.getCorporation());

						//Account Balance
						List<ApiAccountBalance> corpAccountBalances = human.getAccountBalancesCorporation();
						double corpTotalAccountBalance = 0;
						for (int c = 0; c < corpAccountBalances.size(); c++){
							ApiAccountBalance accountBalance = corpAccountBalances.get(c);
							totalAccountBalance = totalAccountBalance + accountBalance.getBalance();
							corpTotalAccountBalance = corpTotalAccountBalance + accountBalance.getBalance();
						}
						corpsTotalAccountBalance.put(human.getCorporation(), corpTotalAccountBalance);

						//Orders
						List<ApiMarketOrder> marketOrders = human.getMarketOrdersCorporation();
						double corpTotalSellOrders = 0;
						double corpTotalBuyOrders = 0;
						for (int c = 0; c < marketOrders.size(); c++){
							ApiMarketOrder apiMarketOrder = marketOrders.get(c);
							if (apiMarketOrder.getOrderState() == 0){
								if (apiMarketOrder.getBid() < 1){ //Sell Orders
									corpTotalSellOrders = corpTotalSellOrders + (apiMarketOrder.getPrice() * apiMarketOrder.getVolRemaining());
								} else { //Buy Orders
									corpTotalBuyOrders = corpTotalBuyOrders + apiMarketOrder.getEscrow();
								}
							}
						}
						if (corpTotalSellOrders != 0){
							corpsTotalSellOrders.put(human.getCorporation(), corpTotalSellOrders);
						}
						if (corpTotalBuyOrders  != 0){
							corpsTotalBuyOrders.put(human.getCorporation(), corpTotalBuyOrders);
						}
						totalSellOrders = totalSellOrders + corpTotalSellOrders;
						totalBuyOrders = totalBuyOrders + corpTotalBuyOrders;
					}
				}
			}
		}
		return !eveAssetEventList.isEmpty();
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jOwners;
	}

	@Override
	protected JButton getDefaultButton() {
		return jClose;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {
		this.setVisible(false);
	}

	@Override
	public void listChanged(ListEvent listChanges) {
		update();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_VALUES_CLOSE.equals(e.getActionCommand())) {
			save();
		}
		if (ACTION_OWNER_SELECTED.equals(e.getActionCommand())) {
			String s = (String)jOwners.getSelectedItem();
			if (s == null || s.equals("Select a charecter...")){
				jOwner.setText("<html>");
				return;
			}
			
			Output output = new Output("Character");

			output.addHeading(NAME_TOTAL);
			double total = 0;
			//Account Balance
			if (ownersTotalAccountBalance.containsKey(s)) total = total + ownersTotalAccountBalance.get(s);
			//Items Value
			if (ownersTotalItemsValue.containsKey(s)) total = total + ownersTotalItemsValue.get(s);
			//Sell Orders
			if (ownersTotalSellOrders.containsKey(s)) total = total + ownersTotalSellOrders.get(s);
			//Buy Orders
			if (ownersTotalBuyOrders.containsKey(s)) total = total + ownersTotalBuyOrders.get(s);
			if (total != 0){
				output.addValue(Formater.isk(total));
			}
			output.addNone();

			output.addHeading(NAME_WALLET_BALANCE);
			if (ownersTotalAccountBalance.containsKey(s)){
				double l = ownersTotalAccountBalance.get(s);
				output.addValue(Formater.isk(l));
			}
			output.addNone();
			
			output.addHeading(NAME_ASSETS_VALUE);
			if (ownersTotalItemsValue.containsKey(s)){
				double l = ownersTotalItemsValue.get(s);
				output.addValue(Formater.isk(l));
			}
			output.addNone();

			output.addHeading(NAME_ASSETS_SELL_ORDERS);
			if (ownersTotalSellOrders.containsKey(s)){
				double l = ownersTotalSellOrders.get(s);
				output.addValue(Formater.isk(l));
			}
			output.addNone();

			output.addHeading(NAME_ASSETS_ESCROWS);
			if (ownersTotalBuyOrders.containsKey(s)){
				double l = ownersTotalBuyOrders.get(s);
				output.addValue(Formater.isk(l));
			}
			output.addNone();
			
			output.addHeading(NAME_BEST_ASSET);
			if (ownersBestItem.containsKey(s)){
				EveAsset ownerBestItem = ownersBestItem.get(s);
				output.addValue(ownerBestItem.getName()+"<br/>"+Formater.isk(ownerBestItem.getPrice()));
			}
			output.addNone(2);
			
			output.addHeading(NAME_BEST_SHIP);
			if (ownersBestShip.containsKey(s)){
				EveAsset ownerBestShip = ownersBestShip.get(s);
				output.addValue(ownerBestShip.getName()+"<br/>"+Formater.isk(ownerBestShip.getPrice()));
			}
			output.addNone(2);
			
			output.addHeading(NAME_BEST_MODULE);
			if (ownersBestModule.containsKey(s)){
				EveAsset ownerBestModule = ownersBestModule.get(s);
				output.addValue(ownerBestModule.getName()+"<br/>"+Formater.isk(ownerBestModule.getPrice()));
			}
			output.addNone(2);
			
			jOwner.setText(output.getOutput());
		}
		if (ACTION_CORP_SELECTED.equals(e.getActionCommand())) {
			//String output = "";
			String s = (String)jCorps.getSelectedItem();
			if (s == null || s.equals("Select a charecter...")){
				jCorp.setText("<html><div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 11px;\"></div>");
				return;
			}
			Output output = new Output("Corporation");

			output.addHeading(NAME_TOTAL);
			double total = 0;
			//Account Balance
			if (corpsTotalAccountBalance.containsKey(s)) total = total + corpsTotalAccountBalance.get(s);
			//Items Value
			if (corpsTotalItemsValue.containsKey(s)) total = total + corpsTotalItemsValue.get(s);
			//Sell Orders
			if (corpsTotalSellOrders.containsKey(s)) total = total + corpsTotalSellOrders.get(s);
			//Buy Orders
			if (corpsTotalBuyOrders.containsKey(s)) total = total + corpsTotalBuyOrders.get(s);
			if (total != 0){
				output.addValue(Formater.isk(total));
			}
			output.addNone();

			output.addHeading(NAME_WALLET_BALANCE);
			if (corpsTotalAccountBalance.containsKey(s)){
				double l = corpsTotalAccountBalance.get(s);
				output.addValue(Formater.isk(l));
			}
			output.addNone();

			output.addHeading(NAME_ASSETS_VALUE);
			if (corpsTotalItemsValue.containsKey(s)){
				double l = corpsTotalItemsValue.get(s);
				output.addValue(Formater.isk(l));
			}
			output.addNone();

			output.addHeading(NAME_ASSETS_SELL_ORDERS);
			if (corpsTotalSellOrders.containsKey(s)){
				double l = corpsTotalSellOrders.get(s);
				output.addValue(Formater.isk(l));
			}
			output.addNone();

			output.addHeading(NAME_ASSETS_ESCROWS);
			if (corpsTotalBuyOrders.containsKey(s)){
				double l = corpsTotalBuyOrders.get(s);
				output.addValue(Formater.isk(l));
			}
			output.addNone();

			output.addHeading(NAME_BEST_ASSET);
			if (corpsBestItem.containsKey(s)){
				EveAsset corpBestItem = corpsBestItem.get(s);
				output.addValue(corpBestItem.getName()+"<br/>"+Formater.isk(corpBestItem.getPrice()));
			}
			output.addNone(2);

			output.addHeading(NAME_BEST_SHIP);
			if (corpsBestShip.containsKey(s)){
				EveAsset corpBestShip = corpsBestShip.get(s);
				output.addValue(corpBestShip.getName()+"<br/>"+Formater.isk(corpBestShip.getPrice()));
			}
			output.addNone(2);

			output.addHeading(NAME_BEST_MODULE);
			if (corpsBestModule.containsKey(s)){
				EveAsset corpBestModule = corpsBestModule.get(s);
				output.addValue(corpBestModule.getName()+"<br/>"+Formater.isk(corpBestModule.getPrice()));
			}
			output.addNone(2);


			jCorp.setText(output.getOutput());
		}
	}

	private class Output{
		private String output;
		private String moduleOutput;

		public Output(String title) {
			output = "<html>"
				+"<div>"
				+"<table cellspacing=\"1\" style=\"padding: 0px; background: #"+gridHexColor+"; width: 100%; font-family: Arial, Helvetica, sans-serif; font-size: 9px;\">"
				+"<tr><td style=\"background: #222222; color: #ffffff; font-size: 11px; font-weight: bold;\">"+title+"</td></tr>";
			moduleOutput = "";
		}


		public void addHeading(String heading){
			output = output+"<tr><td style=\"background: #"+gridHexColor+"; color: #ffffff; font-size: 11px; font-weight: bold;\">"+heading+"</td></tr>";
			moduleOutput = "";
		}

		public void addValue(String module){
			moduleOutput = moduleOutput+"<tr><td style=\"background: #ffffff; text-align: right;\">"+module+"</td></tr>";
		}

		public void addNone(int i){
			if (moduleOutput.equals("")){
				String temp = "";
				for (int a = 1; a < i; a++){
					temp = temp + "<br/>";
				}
				addValue("<i>none</i><br/>"+temp);
				
			}
			output = output+moduleOutput;
		}

		public void addNone(){
			addNone(1);
		}

		public String getOutput(){
			return output+"</table></div>";
		}
	}

}
