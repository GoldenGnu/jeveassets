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
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class StockpileItemDialog extends JDialogCentered implements ActionListener, CaretListener, ItemListener {

	private final static String ACTION_CANCEL = "ACTION_CANCEL";
	private final static String ACTION_OK = "ACTION_OK";
	
	private JButton jOK;
	private JButton jCancel;
	private JComboBox jItems;
	private JTextField jCountMinimum;
	
	private EventList<Item> items = new BasicEventList<Item>();
	private Stockpile stockpile;
	private StockpileItem stockpileItem;
	private boolean updated = false;
	
	public StockpileItemDialog(Program program) {
		super(program, TabsStockpile.get().addStockpileItem(), Images.TOOL_STOCKPILE.getImage());
		
		JLabel jItemsLabel = new JLabel(TabsStockpile.get().item());
		jItems = new JComboBox();
		jItems.addItemListener(this);
		AutoCompleteSupport<Item> itemAutoComplete = AutoCompleteSupport.install(jItems, items, new ItemFilterator());
		itemAutoComplete.setStrict(true);
		itemAutoComplete.setCorrectsCase(true);
		
		JLabel jCountMinimumLabel = new JLabel(TabsStockpile.get().countMinimum());
		jCountMinimum = new JTextField();
		jCountMinimum.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jCountMinimum.selectAll();
			}
		});
		jCountMinimum.addCaretListener(this);
		
		jOK = new JButton(TabsStockpile.get().ok());
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);
		jOK.setEnabled(false);
		
		jCancel = new JButton(TabsStockpile.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);
		
		
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jItemsLabel)
						.addComponent(jCountMinimumLabel)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jItems, 300, 300, 300)
						.addComponent(jCountMinimum, 300, 300, 300)
					)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jItemsLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jItems, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jCountMinimumLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCountMinimum, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}
	
	public void showEdit(StockpileItem stockpileItem) {
		updateData();
		this.stockpileItem = stockpileItem;
		this.getDialog().setTitle(TabsStockpile.get().editStockpileItem());
		Item item = program.getSettings().getItems().get(stockpileItem.getTypeID());
		jItems.setSelectedItem(item);
		jCountMinimum.setText(String.valueOf(stockpileItem.getCountMinimum()));
		show();
	}
	
	public void showAdd(Stockpile stockpile) {
		updateData();
		this.stockpile = stockpile;
		this.getDialog().setTitle(TabsStockpile.get().addStockpileItem());
		show();
	}
	
	public boolean showAdd(Stockpile stockpile, int typeID) {
		updateData();
		this.stockpile = stockpile;
		Item item = program.getSettings().getItems().get(typeID);
		jItems.setSelectedItem(item);
		jItems.setEnabled(false);
		this.getDialog().setTitle(TabsStockpile.get().addStockpileItem());
		show();
		return updated;
	}
	
	private void updateData(){
		stockpile = null;
		stockpileItem = null;
		updated = false;
		List<Item> itemsList = new ArrayList<Item>(program.getSettings().getItems().values());
		Collections.sort(itemsList);
		try {
			items.getReadWriteLock().writeLock().lock();
			items.clear();
			items.addAll(itemsList);
		} finally {
			items.getReadWriteLock().writeLock().unlock();
		}
		jItems.setSelectedIndex(0);
		jCountMinimum.setText("");
		jItems.setEnabled(true);
	}
	
	private void show(){
		super.setVisible(true);
	}
	
	private Stockpile getStockpile(){
		if (stockpile != null){
			return stockpile;
		} else {
			return stockpileItem.getStockpile();
		}
		
	}
	
	private StockpileItem getStockpileItem(){
		Item item = (Item)jItems.getSelectedItem();
		String countMinimum = jCountMinimum.getText();
		return new StockpileItem(getStockpile(), item.getName(), item.getTypeID(), countMinimum);
	}
	
	private void autoValidate(){
		boolean b = true;
		if (jItems.getSelectedItem() == null) b = false;
		try {
			long l = Long.valueOf(jCountMinimum.getText());
			if (l > 0){
				jCountMinimum.setBackground(Color.WHITE);
			} else {
				jCountMinimum.setBackground(new Color(255, 200, 200));
			}
		} catch (NumberFormatException ex){
			b = false;
			if (jCountMinimum.getText().isEmpty()){
				jCountMinimum.setBackground(Color.WHITE);
			} else {
				jCountMinimum.setBackground(new Color(255, 200, 200));
			}
		}
		jOK.setEnabled(b);
	}

	@Override
	protected JComponent getDefaultFocus() {
		if (jItems.isEnabled()){
			return jItems;
		} else {
			return jCountMinimum;
		}
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void save() {
		if (stockpileItem != null){ //EDIT
			stockpile = getStockpile();
			stockpile.remove(stockpileItem);
		}
		//ADD & EDIT
		stockpile.add(getStockpileItem());
		updated = true;
		super.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_OK.equals(e.getActionCommand())){
			save();
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			this.setVisible(false);
		}
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		autoValidate();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		autoValidate();
	}
	
	class ItemFilterator implements TextFilterator<Item>{
		@Override
		public void getFilterStrings(List<String> baseList, Item element) {
			baseList.add(element.getName());
		}
	}
}
