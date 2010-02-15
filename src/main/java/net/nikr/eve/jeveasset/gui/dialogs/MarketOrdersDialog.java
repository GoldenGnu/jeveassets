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

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.table.MarketOrderTableFormat;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class MarketOrdersDialog extends JDialogCentered implements ActionListener {

	private final static String ACTION_CLOSE = "ACTION_CLOSE";
	private final static String ACTION_SELECTED = "ACTION_SELECTED";
	
	
	private JButton jClose;
	private JComboBox jCharacters;
	private JComboBox jState;
	private EventList<MarketOrder> sellOrdersEventList;
	private EventList<MarketOrder> buyOrdersEventList;

	private List<MarketOrder> all;
	private Map<String, List<MarketOrder>> orders;
	private Vector<String> characters;

	private JTable jSellOrders;
	private JTable jBuyOrders;

	private String[] orderStates = new String[]{"All", "Active", "Fulfilled", "Partially Fulfilled", "Expired", "Closed", "Cancelled", "Pending"};

	public MarketOrdersDialog(Program program, Image image) {
		super(program, "Market Orders", image);

		jClose = new JButton("Close");
		jClose.setActionCommand(ACTION_CLOSE);
		jClose.addActionListener(this);

		jCharacters = new JComboBox();
		jCharacters.setActionCommand(ACTION_SELECTED);
		jCharacters.addActionListener(this);

		jState = new JComboBox();
		jState.setActionCommand(ACTION_SELECTED);
		jState.addActionListener(this);

		//Table format
		MarketOrderTableFormat sellTableFormat = new MarketOrderTableFormat();
		MarketOrderTableFormat buyTableFormat = new MarketOrderTableFormat();
		//Backend
		sellOrdersEventList = new BasicEventList<MarketOrder>();
		buyOrdersEventList = new BasicEventList<MarketOrder>();
		//For soring the table
		SortedList<MarketOrder> sellOrdersSortedList = new SortedList<MarketOrder>(sellOrdersEventList);
		SortedList<MarketOrder> buyOrdersSortedList = new SortedList<MarketOrder>(buyOrdersEventList);
		//Table Model
		EventTableModel sellOrdersTableModel = new EventTableModel<MarketOrder>(sellOrdersSortedList, sellTableFormat);
		EventTableModel buyOrdersTableModel = new EventTableModel<MarketOrder>(buyOrdersSortedList, buyTableFormat);
		//Tables
		jSellOrders = new JAutoColumnTable(sellOrdersTableModel, sellTableFormat.getColumnNames());
		jBuyOrders = new JAutoColumnTable(buyOrdersTableModel, buyTableFormat.getColumnNames());
		//Sorters
		TableComparatorChooser.install(jSellOrders, sellOrdersSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, sellTableFormat);
		TableComparatorChooser.install(jBuyOrders, buyOrdersSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, buyTableFormat);
		//Labels
		JLabel jCharactersLabel = new JLabel("Character");
		JLabel jStateLabel = new JLabel("State");
		JLabel jSellLabel = new JLabel("Sell Orders");
		JLabel jBuyLabel = new JLabel("Buy Orders");
		//Scroll Panels
		JScrollPane jSellOrdersScrollPanel = new JScrollPane(jSellOrders);
		JScrollPane jBuyOrdersScrollPanel = new JScrollPane(jBuyOrders);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCharactersLabel)
					.addComponent(jSellLabel)
					.addComponent(jBuyLabel)
				)
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jCharacters, 200, 200, 200)
							.addGap(100)
							.addComponent(jStateLabel)
							.addComponent(jState, 200, 200, 200)
						)
						
						.addComponent(jSellOrdersScrollPanel, 650, 650, 650)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jBuyOrdersScrollPanel, 650, 650, 650)
						.addComponent(jClose, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCharactersLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCharacters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStateLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jState, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jSellLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSellOrdersScrollPanel, 200, 200, 200)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jBuyLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jBuyOrdersScrollPanel, 200, 200, 200)
				)
				.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jClose;
	}

	@Override
	protected JButton getDefaultButton() {
		return jClose;
	}

	@Override
	protected void windowShown() {
		characters = new Vector<String>();
		orders = new HashMap<String, List<MarketOrder>>();
		all = new Vector<MarketOrder>();
		List<Account> accounts = program.getSettings().getAccounts();
		for (int a = 0; a < accounts.size(); a++){
			List<Human> tempHumans = accounts.get(a).getHumans();
			for (int b = 0; b < tempHumans.size(); b++){
				Human human = tempHumans.get(b);
				List<MarketOrder> marketOrders = new Vector<MarketOrder>();
				orders.put(human.getName(), marketOrders);
				if (human.isShowAssets()){
					characters.add(human.getName());
					List<MarketOrder> characterMarketOrders = ApiConverter.apiMarketOrdersToMarketOrders(human.getMarketOrders(), program.getSettings().getConquerableStations(), program.getSettings().getLocations(), program.getSettings().getItems());
					orders.put(human.getName(), characterMarketOrders);
					all.addAll(characterMarketOrders);
					if (human.isUpdateCorporationAssets()){
						String corpKey = "["+human.getCorporation()+"]";
						characters.add(corpKey);
						List<MarketOrder> corporationMarketOrders = ApiConverter.apiMarketOrdersToMarketOrders(human.getMarketOrdersCorporation(), program.getSettings().getConquerableStations(), program.getSettings().getLocations(), program.getSettings().getItems());
						orders.put(corpKey, corporationMarketOrders);
						all.addAll(corporationMarketOrders);
					}
				}
			}
		}
		if (!characters.isEmpty()){
			jCharacters.setEnabled(true);
			jState.setEnabled(true);
			jSellOrders.setEnabled(true);
			jBuyOrders.setEnabled(true);
			Collections.sort(characters);
			characters.add(0, "All");
			jCharacters.setModel( new DefaultComboBoxModel(characters));
			jState.setModel( new DefaultComboBoxModel(orderStates));
			jCharacters.setSelectedIndex(0);
			jState.setSelectedIndex(0);
		} else {
			jCharacters.setEnabled(false);
			jState.setEnabled(false);
			jSellOrders.setEnabled(false);
			jBuyOrders.setEnabled(false);
			jCharacters.setModel( new DefaultComboBoxModel());
			jCharacters.getModel().setSelectedItem("No character found");
			jState.setModel( new DefaultComboBoxModel());
			jState.getModel().setSelectedItem("No character found");
			sellOrdersEventList.clear();
			buyOrdersEventList.clear();
		}
		
	}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_CLOSE.equals(e.getActionCommand())) {
			dialog.setVisible(false);
		}
		if (ACTION_SELECTED.equals(e.getActionCommand())) {
			String selected = (String) jCharacters.getSelectedItem();
			if (characters.size() > 1){
				List<MarketOrder> marketOrders;
				List<MarketOrder> sellMarketOrders = new Vector<MarketOrder>();
				List<MarketOrder> buyMarketOrders = new Vector<MarketOrder>();
				if (selected.equals("All")){
					marketOrders = all;
				} else {
					marketOrders = orders.get(selected);
				}
				String sState = (String) jState.getSelectedItem();
				int state = 0;
				if (sState.equals("All")) state = -1;
				if (sState.equals("Active")) state = 0;
				if (sState.equals("Closed")) state = 1;
				if (sState.equals("Expired") 
						|| sState.equals("Fulfilled")
						|| sState.equals("Partially Fulfilled")) state = 2;
				if (sState.equals("Cancelled")) state = 3;
				if (sState.equals("Pending")) state = 4;
				for (int a = 0; a < marketOrders.size(); a++){
					MarketOrder marketOrder = marketOrders.get(a);
					if (marketOrder.getOrderState() == state || state < 0){
						boolean add = true;
						if (state == 2){
							add = false;
							if (sState.equals("Expired") && marketOrder.getStatus().equals("Expired")){
								add = true;
							}
							if (sState.equals("Fulfilled") && marketOrder.getStatus().equals("Fulfilled")){
								add = true;
							}
							if (sState.equals("Partially Fulfilled") && marketOrder.getStatus().equals("Partially Fulfilled")){
								add = true;
							}
						}
						if (add){
							if (marketOrder.getBid() < 1){
								sellMarketOrders.add(marketOrder);
							} else {
								buyMarketOrders.add(marketOrder);
							}
						}
					}

				}
				sellOrdersEventList.clear();
				sellOrdersEventList.addAll( sellMarketOrders );
				buyOrdersEventList.clear();
				buyOrdersEventList.addAll( buyMarketOrders );
			}
		}
	}
}
