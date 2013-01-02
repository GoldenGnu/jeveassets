/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class StockpileItemDialog extends JDialogCentered implements ActionListener, CaretListener, ItemListener {

	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_OK = "ACTION_OK";
	private static final String ACTION_COPY = "ACTION_COPY";

	private JButton jOK;
	private JButton jCancel;
	private JComboBox jItems;
	private JTextField jCountMinimum;
	private JCheckBox jCopy;

	private EventList<Item> items = new BasicEventList<Item>();
	private Stockpile stockpile;
	private StockpileItem stockpileItem;

	public StockpileItemDialog(final Program program) {
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
			public void focusGained(final FocusEvent e) {
				jCountMinimum.selectAll();
			}
		});
		jCountMinimum.addCaretListener(this);

		jCopy = new JCheckBox(TabsStockpile.get().copy());
		jCopy.setActionCommand(ACTION_COPY);
		jCopy.addActionListener(this);

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
						.addGroup(layout.createSequentialGroup()
							.addComponent(jCountMinimum, 100, 100, Short.MAX_VALUE)
							.addComponent(jCopy)
						)
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
					.addComponent(jCopy, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	StockpileItem showEdit(final StockpileItem addStockpileItem) {
		updateData();
		this.stockpileItem = addStockpileItem;
		this.getDialog().setTitle(TabsStockpile.get().editStockpileItem());
		Item item = program.getSettings().getItems().get(addStockpileItem.getTypeID());
		jItems.setSelectedItem(item);
		jCopy.setSelected(addStockpileItem.isBPC());
		jCountMinimum.setText(String.valueOf(addStockpileItem.getCountMinimum()));
		show();
		return stockpileItem;
	}

	StockpileItem showAdd(final Stockpile addStockpile) {
		updateData();
		this.stockpile = addStockpile;
		this.getDialog().setTitle(TabsStockpile.get().addStockpileItem());
		jCopy.setSelected(false);
		show();
		return stockpileItem;
	}

	private void updateData() {
		stockpile = null;
		stockpileItem = null;
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

	private void show() {
		autoValidate();
		autoSet();
		super.setVisible(true);
	}

	private Stockpile getStockpile() {
		if (stockpile != null) {
			return stockpile;
		} else if (stockpileItem != null) {
			return stockpileItem.getStockpile();
		} else {
			return null;
		}
	}

	private StockpileItem getStockpileItem() {
		Item item = (Item) jItems.getSelectedItem();
		long countMinimum;
		try {
			countMinimum = Long.valueOf(jCountMinimum.getText());
		} catch (NumberFormatException ex) {
			countMinimum = 0;
		}
		boolean copy = jCopy.isSelected() && jCopy.isEnabled();
		int typeID;
		if (copy) {
			typeID = -item.getTypeID();
		} else {
			typeID = item.getTypeID();
		}
		return new StockpileItem(getStockpile(), item.getName(), item.getGroup(), typeID, countMinimum);
	}

	private boolean itemExist() {
		return getExistingItem() != null;
	}

	private StockpileItem getExistingItem() {
		Item typeItem = (Item) jItems.getSelectedItem();
		boolean copy = jCopy.isSelected();
		if (getStockpile() != null && typeItem != null) {
			for (StockpileItem item : getStockpile().getItems()) {
				if (item.getTypeID() == typeItem.getTypeID() && (copy == item.isBPC())) {
					return item;
				}
			}
		}
		return null;
	}

	private void autoValidate() {
		boolean b = true;
		boolean color = false;
		if (jItems.getSelectedItem() == null) {
			b = false;
			jCopy.setEnabled(false);
			jCopy.setSelected(false);
		} else {
			Item item = (Item) jItems.getSelectedItem();
			boolean blueprint = item.getName().toLowerCase().contains("blueprint");
			jCopy.setEnabled(blueprint);
			if (!blueprint) {
				jCopy.setSelected(blueprint);
			}
		}
		if (itemExist() || stockpileItem != null) {
			color = true;
			jCountMinimum.setBackground(new Color(255, 255, 200));
		}
		try {
			long l = Long.valueOf(jCountMinimum.getText());
			if (l <= 0) {
				b = false; //Negative and zero is not valid
				color = true;
				jCountMinimum.setBackground(new Color(255, 200, 200));
			}
		} catch (NumberFormatException ex) {
			b = false; //Empty and NaN is not valid
			if (!jCountMinimum.getText().isEmpty()) {
				color = true;
				jCountMinimum.setBackground(new Color(255, 200, 200));
			}
		}
		if (!color) {
			jCountMinimum.setBackground(Color.WHITE);
		}
		jOK.setEnabled(b);
	}

	private void autoSet() {
		final StockpileItem item = getExistingItem();
		if (item != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					jCountMinimum.setText(String.valueOf(item.getCountMinimum()));
				}
			});
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		if (jItems.isEnabled()) {
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
	protected void windowShown() { }

	@Override
	protected void save() {
		if (stockpileItem != null) { //EDIT
			stockpileItem.update(getStockpileItem());
		} else if (itemExist()) { //UPDATING (Adding an existing item)
			stockpileItem = getExistingItem();
			stockpileItem.update(getStockpileItem());
		} else { //ADD 
			stockpileItem = getStockpileItem();
			stockpile.add(stockpileItem);
		}
		super.setVisible(false);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_OK.equals(e.getActionCommand())) {
			save();
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())) {
			this.setVisible(false);
		}
		if (ACTION_COPY.equals(e.getActionCommand())) {
			autoSet();
			autoValidate();
		}
	}

	@Override
	public void caretUpdate(final CaretEvent e) {
		autoValidate();
	}

	@Override
	public void itemStateChanged(final ItemEvent e) {
		autoValidate();
		autoSet();
	}

	static class ItemFilterator implements TextFilterator<Item> {
		@Override
		public void getFilterStrings(final List<String> baseList, final Item element) {
			baseList.add(element.getName());
		}
	}
}
