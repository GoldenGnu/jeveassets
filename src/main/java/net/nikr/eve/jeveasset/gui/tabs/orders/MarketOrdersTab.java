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

package net.nikr.eve.jeveasset.gui.tabs.orders;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JMenuAssetFilter;
import net.nikr.eve.jeveasset.gui.shared.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.JMenuLookup;
import net.nikr.eve.jeveasset.gui.shared.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.JMenuStockpile;
import net.nikr.eve.jeveasset.i18n.TabsOrders;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;


public class MarketOrdersTab extends JMainTab implements ActionListener{

	private final static String ACTION_SELECTED = "ACTION_SELECTED";

	
	private JComboBox jCharacters;
	private JComboBox jState;
	private EventTableModel<MarketOrder> sellOrdersTableModel;
	private EventTableModel<MarketOrder> buyOrdersTableModel;
	private EventList<MarketOrder> sellOrdersEventList;
	private EventList<MarketOrder> buyOrdersEventList;

	private List<MarketOrder> all;
	private Map<String, List<MarketOrder>> orders;
	private List<String> characters;

	private JTable jSellTable;
	private JTable jBuyTable;

	private String[] orderStates = new String[]{"All", "Active", "Fulfilled", "Partially Fulfilled", "Expired", "Closed", "Cancelled", "Pending"};

	public MarketOrdersTab(Program program) {
		super(program, TabsOrders.get().market(), Images.TOOL_MARKET_ORDERS.getIcon(), true);

		jCharacters = new JComboBox();
		jCharacters.setActionCommand(ACTION_SELECTED);
		jCharacters.addActionListener(this);

		jState = new JComboBox();
		jState.setActionCommand(ACTION_SELECTED);
		jState.addActionListener(this);

		//Table format
		EnumTableFormatAdaptor<MarketTableFormat, MarketOrder> buyTableFormat =
				new EnumTableFormatAdaptor<MarketTableFormat, MarketOrder>(MarketTableFormat.class);
		EnumTableFormatAdaptor<MarketTableFormat, MarketOrder> sellTableFormat =
				new EnumTableFormatAdaptor<MarketTableFormat, MarketOrder>(MarketTableFormat.class);
		//Backend
		sellOrdersEventList = new BasicEventList<MarketOrder>();
		buyOrdersEventList = new BasicEventList<MarketOrder>();
		//For soring the table
		SortedList<MarketOrder> sellOrdersSortedList = new SortedList<MarketOrder>(sellOrdersEventList);
		SortedList<MarketOrder> buyOrdersSortedList = new SortedList<MarketOrder>(buyOrdersEventList);
		//Table Model
		sellOrdersTableModel = new EventTableModel<MarketOrder>(sellOrdersSortedList, sellTableFormat);
		buyOrdersTableModel = new EventTableModel<MarketOrder>(buyOrdersSortedList, buyTableFormat);
		//Tables
		jSellTable = new JAutoColumnTable(sellOrdersTableModel);
		jBuyTable = new JAutoColumnTable(buyOrdersTableModel);
		//Table Selection
		EventSelectionModel<MarketOrder> sellSelectionModel = new EventSelectionModel<MarketOrder>(sellOrdersEventList);
		sellSelectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jSellTable.setSelectionModel(sellSelectionModel);
		EventSelectionModel<MarketOrder> buySelectionModel = new EventSelectionModel<MarketOrder>(buyOrdersEventList);
		buySelectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jBuyTable.setSelectionModel(buySelectionModel);
		//Listeners
		installTableMenu(jSellTable);
		installTableMenu(jBuyTable);
		//Sorters
		TableComparatorChooser.install(jSellTable, sellOrdersSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, sellTableFormat);
		TableComparatorChooser.install(jBuyTable, buyOrdersSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, buyTableFormat);
		//Labels
		JLabel jCharactersLabel = new JLabel(TabsOrders.get().character());
		JLabel jStateLabel = new JLabel(TabsOrders.get().state());
		JLabel jSellLabel = new JLabel(TabsOrders.get().sell());
		JLabel jBuyLabel = new JLabel(TabsOrders.get().buy());
		//Scroll Panels
		JScrollPane jSellTableScroll = new JScrollPane(jSellTable);
		JScrollPane jBuyTableScroll = new JScrollPane(jBuyTable);

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
						.addComponent(jSellTableScroll, 0, 0, Short.MAX_VALUE)
					)
					.addComponent(jBuyTableScroll, 0, 0, Short.MAX_VALUE)
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
					.addComponent(jSellTableScroll, 0, 0, Short.MAX_VALUE)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jBuyLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jBuyTableScroll, 0, 0, Short.MAX_VALUE)
				)
		);
	}

	@Override
	protected void showTablePopupMenu(MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();

		if (e.getSource() instanceof JTable){
			JTable jTable = (JTable) e.getSource();
			EventTableModel<?> tableModel = (EventTableModel<?>) jTable.getModel();
			//Select clicked row
			jTable.setRowSelectionInterval(jTable.rowAtPoint(e.getPoint()), jTable.rowAtPoint(e.getPoint()));
			jTable.setColumnSelectionInterval(0, jTable.getColumnCount()-1);
			//is single row selected
			boolean isSingleRow = jTable.getSelectedRows().length == 1;
			//COPY
			if (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0){
				jTablePopupMenu.add(new JMenuCopy(jTable));
				addSeparator(jTablePopupMenu);
			}
			//FILTER & LOOKUP
			MarketOrder marketOrder = isSingleRow ? (MarketOrder) tableModel.getElementAt(jTable.getSelectedRow()): null;
			jTablePopupMenu.add(new JMenuAssetFilter(program, marketOrder));
			jTablePopupMenu.add(new JMenuStockpile(program, marketOrder));
			jTablePopupMenu.add(new JMenuLookup(program, marketOrder));
		}
		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void updateTableMenu(JComponent jComponent){
		JMenuItem  jMenuItem;
		
		jComponent.removeAll();
		jComponent.setEnabled(true);

		boolean isSellSingleRow = (jSellTable.getSelectedRows().length == 1);
		boolean isBuySingleRow = (jBuyTable.getSelectedRows().length == 1);

		MarketOrder sellMarketOrder = isSellSingleRow ? sellOrdersTableModel.getElementAt(jSellTable.getSelectedRow()): null;
		MarketOrder buyMarketOrder = isBuySingleRow ? buyOrdersTableModel.getElementAt(jBuyTable.getSelectedRow()) : null;

		jMenuItem = new JMenuItem(TabsOrders.get().sell1());
		jMenuItem.setEnabled(false);
		jComponent.add(jMenuItem);

		jComponent.add(new JMenuAssetFilter(program, sellMarketOrder));
		jComponent.add(new JMenuStockpile(program, sellMarketOrder));
		jComponent.add(new JMenuLookup(program, sellMarketOrder));

		addSeparator(jComponent);

		jMenuItem = new JMenuItem(TabsOrders.get().buy1());
		jMenuItem.setEnabled(false);
		jComponent.add(jMenuItem);

		jComponent.add(new JMenuAssetFilter(program, buyMarketOrder));
		jComponent.add(new JMenuStockpile(program, buyMarketOrder));
		jComponent.add(new JMenuLookup(program, buyMarketOrder));
	}

	@Override
	public void updateData() {
		List<String> unique = new ArrayList<String>();
		characters = new ArrayList<String>();
		orders = new HashMap<String, List<MarketOrder>>();
		all = new ArrayList<MarketOrder>();
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				if (human.isShowAssets()){
					String name;
					if (human.isCorporation()){
						name = TabsOrders.get().whitespace(human.getName());
					} else {
						name = human.getName();
					}
					//Only add names once
					if (!characters.contains(name)){
						characters.add(name);
						orders.put(name, new ArrayList<MarketOrder>()); //Make sure empty is not null
					}
					//Only add once and don't add empty orders
					List<MarketOrder> characterMarketOrders = ApiConverter.apiMarketOrdersToMarketOrders(human, human.getMarketOrders(), program.getSettings());
					if (!unique.contains(name) && !characterMarketOrders.isEmpty()){
						orders.put(name, characterMarketOrders);
						all.addAll(characterMarketOrders);
						unique.add(name);
					}
				}
			}
		}
		if (!characters.isEmpty()){
			jCharacters.setEnabled(true);
			jState.setEnabled(true);
			jSellTable.setEnabled(true);
			jBuyTable.setEnabled(true);
			Collections.sort(characters);
			characters.add(0, "All");
			jCharacters.setModel( new DefaultComboBoxModel(characters.toArray()));
			jState.setModel( new DefaultComboBoxModel(orderStates));
			jCharacters.setSelectedIndex(0);
			jState.setSelectedIndex(0);
		} else {
			jCharacters.setEnabled(false);
			jState.setEnabled(false);
			jSellTable.setEnabled(false);
			jBuyTable.setEnabled(false);
			jCharacters.setModel( new DefaultComboBoxModel());
			jCharacters.getModel().setSelectedItem(TabsOrders.get().no());
			jState.setModel( new DefaultComboBoxModel());
			jState.getModel().setSelectedItem(TabsOrders.get().no());
			sellOrdersEventList.clear();
			buyOrdersEventList.clear();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_SELECTED.equals(e.getActionCommand())) {
			String selected = (String) jCharacters.getSelectedItem();
			if (characters.size() > 1){
				List<MarketOrder> marketOrders;
				List<MarketOrder> sellMarketOrders = new ArrayList<MarketOrder>();
				List<MarketOrder> buyMarketOrders = new ArrayList<MarketOrder>();
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
				/*
				sellOrdersEventList.clear();
				sellOrdersEventList.addAll( sellMarketOrders );
				buyOrdersEventList.clear();
				buyOrdersEventList.addAll( buyMarketOrders );
				 * 
				 */
				try {
					sellOrdersEventList.getReadWriteLock().writeLock().lock();
					sellOrdersEventList.clear();
					sellOrdersEventList.addAll( sellMarketOrders );
					buyOrdersEventList.getReadWriteLock().writeLock().lock();
					buyOrdersEventList.clear();
					buyOrdersEventList.addAll( buyMarketOrders );
				} finally {
					sellOrdersEventList.getReadWriteLock().writeLock().unlock();
					buyOrdersEventList.getReadWriteLock().writeLock().unlock();
				}
			}
		}
	}
}
