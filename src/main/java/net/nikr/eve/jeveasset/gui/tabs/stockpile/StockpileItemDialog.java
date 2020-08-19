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

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.IndustryMaterial;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.Colors;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileStock;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StockpileItemDialog extends JDialogCentered {

	private enum StockpileItemAction {
		CANCEL,
		OK,
		TYPE_CHANGE
	}

	public static enum BlueprintAddType {
		NONE(TabsStockpile.get().none()),
		BPO(TabsStockpile.get().original()),
		BPC(TabsStockpile.get().copy()),
		RUNS(TabsStockpile.get().runs()),
		MANUFACTURING_MATERIALS(TabsStockpile.get().materialsManufacturing()),
		REACTION_MATERIALS(TabsStockpile.get().materialsReaction()),
		;
		final String name;

		public static BlueprintAddType[] EMPTY = {NONE};
		public static BlueprintAddType[] EDIT_BLUEPRINT = {BPO, BPC, RUNS};
		public static BlueprintAddType[] EDIT_FORMULA = {BPO};
		public static BlueprintAddType[] ADD_BLUEPRINT = {BPO, BPC, RUNS, MANUFACTURING_MATERIALS};
		public static BlueprintAddType[] ADD_FORMULA = {BPO, REACTION_MATERIALS};

		private BlueprintAddType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private final JButton jOK;
	private final JComboBox<Item> jItems;
	private final JLabel jSubpile;
	private JTextField jCountMinimum;
	private final JComboBox<BlueprintAddType> jBlueprintType;

	private final EventList<Item> items = EventListManager.create();
	private Stockpile stockpile;
	private StockpileItem stockpileItem;
	private List<StockpileItem> stockpileItems;
	private boolean updating = false;

	public StockpileItemDialog(final Program program) {
		super(program, TabsStockpile.get().addStockpileItem(), Images.TOOL_STOCKPILE.getImage());

		ListenerClass listener = new ListenerClass();

		JLabel jItemsLabel = new JLabel(TabsStockpile.get().item());
		jItems = new JComboBox<>();
		AutoCompleteSupport<Item> itemAutoComplete = AutoCompleteSupport.install(jItems, EventModels.createSwingThreadProxyList(items), new ItemFilterator());
		itemAutoComplete.setStrict(true);
		itemAutoComplete.setCorrectsCase(true);
		jItems.addItemListener(listener); //Must be added after AutoCompleteSupport

		jSubpile = new JLabel();

		JLabel jCountMinimumLabel = new JLabel(TabsStockpile.get().countMinimum());
		jCountMinimum = new JTextField();
		jCountMinimum.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				jCountMinimum.selectAll();
			}
		});
		jCountMinimum.addCaretListener(listener);

		JLabel jBlueprintTypeLabel = new JLabel(TabsStockpile.get().blueprintType());
		jBlueprintType = new JComboBox<>(BlueprintAddType.values());
		jBlueprintType.setActionCommand(StockpileItemAction.TYPE_CHANGE.name());
		jBlueprintType.addActionListener(listener);

		jOK = new JButton(TabsStockpile.get().ok());
		jOK.setActionCommand(StockpileItemAction.OK.name());
		jOK.addActionListener(listener);
		jOK.setEnabled(false);

		JButton jCancel = new JButton(TabsStockpile.get().cancel());
		jCancel.setActionCommand(StockpileItemAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jItemsLabel)
						.addComponent(jBlueprintTypeLabel)
						.addComponent(jCountMinimumLabel)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jItems, 300, 300, 300)
						.addComponent(jSubpile, 300, 300, 300)
						.addComponent(jBlueprintType, 300, 300, 300)
						.addComponent(jCountMinimum, 300, 300, 300)
					)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jItemsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jItems, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSubpile, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jBlueprintTypeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBlueprintType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jCountMinimumLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCountMinimum, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	protected StockpileItem showEdit(final StockpileItem editStockpileItem) {
		updateData();
		this.stockpileItem = editStockpileItem;
		this.getDialog().setTitle(TabsStockpile.get().editStockpileItem());
		Item item = ApiIdConverter.getItem(editStockpileItem.getTypeID());
		if (editStockpileItem instanceof SubpileStock) {
			jSubpile.setText(editStockpileItem.getName());
			jSubpile.setVisible(true);
			jItems.setVisible(false);
		} else {
			jItems.setSelectedItem(item);
			jSubpile.setVisible(false);
			jItems.setVisible(true);
		}
		if (item.isBlueprint()) {
			jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EDIT_BLUEPRINT));
		} else if (item.isFormula()) {
			jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EDIT_FORMULA));
		} else {
			jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EMPTY));
		}
		if (editStockpileItem.isBPO()) {
			jBlueprintType.setSelectedItem(BlueprintAddType.BPO);
		} else if (editStockpileItem.isBPC()) {
			jBlueprintType.setSelectedItem(BlueprintAddType.BPC);
		} else if (editStockpileItem.isRuns()) {
			jBlueprintType.setSelectedItem(BlueprintAddType.RUNS);
		}
		jBlueprintType.setEnabled(item.isBlueprint());
		jCountMinimum.setText(String.valueOf(editStockpileItem.getCountMinimum()));
		show();
		return stockpileItem;
	}

	protected List<StockpileItem> showAdd(final Stockpile addStockpile) {
		updateData();
		this.stockpile = addStockpile;
		this.getDialog().setTitle(TabsStockpile.get().addStockpileItem());
		jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EMPTY));
		show();
		if (stockpileItems != null) {
			return stockpileItems;
		} else if (stockpileItem != null){
			return Collections.singletonList(stockpileItem);
		} else {
			return null;
		}
	}

	private void updateData() {
		stockpile = null;
		stockpileItem = null;
		this.stockpileItems = null;
		List<Item> itemsList = new ArrayList<>(StaticData.get().getItems().values());
		Collections.sort(itemsList);
		try {
			items.getReadWriteLock().writeLock().lock();
			items.clear();
			items.addAll(itemsList);
		} finally {
			items.getReadWriteLock().writeLock().unlock();
		}
		jSubpile.setVisible(false);
		jItems.setVisible(true);
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
		double countMinimum;
		try {
			countMinimum = Double.valueOf(jCountMinimum.getText());
		} catch (NumberFormatException ex) {
			countMinimum = 0;
		}
		boolean runs = jBlueprintType.isEnabled() && jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex()) == BlueprintAddType.RUNS;
		boolean copy = runs || (jBlueprintType.isEnabled() && jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex()) == BlueprintAddType.BPC);
		int typeID;
		if (copy) {
			typeID = -item.getTypeID();
		} else {
			typeID = item.getTypeID();
		}
		return new StockpileItem(getStockpile(), item, typeID, countMinimum, runs);
	}

	private List<StockpileItem> getStockpileItems() {
		List<StockpileItem> itemsMaterial = new ArrayList<>();
		Item item = (Item) jItems.getSelectedItem();
		double countMinimum;
		try {
			countMinimum = Double.valueOf(jCountMinimum.getText());
		} catch (NumberFormatException ex) {
			countMinimum = 0;
		}
		boolean manufacturingMaterials = jBlueprintType.isEnabled()
				&& jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex()) == BlueprintAddType.MANUFACTURING_MATERIALS;
		boolean reactionMaterials = jBlueprintType.isEnabled()
				&& jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex()) == BlueprintAddType.REACTION_MATERIALS;
		if (manufacturingMaterials) {
			for (IndustryMaterial material : item.getManufacturingMaterials()) {
				Item materialItem = ApiIdConverter.getItem(material.getTypeID());
				itemsMaterial.add(new StockpileItem(getStockpile(), materialItem, material.getTypeID(), material.getQuantity() * countMinimum, false));
			}
		} else if (reactionMaterials) {
			for (IndustryMaterial material : item.getReactionMaterials()) {
				Item materialItem = ApiIdConverter.getItem(material.getTypeID());
				itemsMaterial.add(new StockpileItem(getStockpile(), materialItem, material.getTypeID(), material.getQuantity() * countMinimum, false));
			}
		} else {
			return null;
		}
		return itemsMaterial;
	}

	private boolean itemExist() {
		return getExistingItem() != null;
	}

	private StockpileItem getExistingItem() {
		Object object = jItems.getSelectedItem();
		if (object == null) {
			return null;
		}
		if (!(object instanceof Item)) {
			return null;
		}
		Item typeItem = (Item) object;
		Stockpile existing = getStockpile();
		if (existing == null) {
			return null;
		}
		boolean materials = jBlueprintType.isEnabled() && 
				(jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex()) == BlueprintAddType.MANUFACTURING_MATERIALS
				|| jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex()) == BlueprintAddType.REACTION_MATERIALS);
		if (materials) { //Never exists
			return null;
		}
		boolean runs = jBlueprintType.isEnabled() && jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex()) == BlueprintAddType.RUNS;
		boolean copy = runs || (jBlueprintType.isEnabled() && jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex()) == BlueprintAddType.BPC);
		for (StockpileItem item : existing.getItems()) {
			if (item.getTypeID() == typeItem.getTypeID() && copy == item.isBPC() && runs == item.isRuns()) {
				return item;
			}
		}
		return null;
	}

	private void autoValidate() {
		if (updating) {
			return;
		}
		boolean oldUpdateValue = updating;
		updating = true;
		boolean valid = true;
		boolean colorIsSet = false;
		if (jItems.getSelectedItem() == null || !(jItems.getSelectedItem() instanceof Item)) {
			valid = false;
			jBlueprintType.setEnabled(false);
			jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EMPTY));
		} else {
			Item item = (Item) jItems.getSelectedItem();
			BlueprintAddType oldValue = jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex());
			if (stockpileItem != null) { //Can not add Materials
				if (item.isBlueprint()) {
					jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EDIT_BLUEPRINT));
					jBlueprintType.setEnabled(true);
				} else if (item.isFormula()) {
					jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EDIT_FORMULA));
					jBlueprintType.setEnabled(false);
				} else {
					jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EMPTY));
					jBlueprintType.setEnabled(false);
				}
			} else if (!item.getManufacturingMaterials().isEmpty()) {
				jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.ADD_BLUEPRINT));
				jBlueprintType.setEnabled(true);
			} else if (!item.getReactionMaterials().isEmpty()) {
				jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.ADD_FORMULA));
				jBlueprintType.setEnabled(true);
			} else if (item.isBlueprint()){
				jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EDIT_BLUEPRINT));
				jBlueprintType.setEnabled(true);
			} else if (item.isFormula()){
				jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EDIT_FORMULA));
				jBlueprintType.setEnabled(true);
			} else {
				jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EMPTY));
				jBlueprintType.setEnabled(false);
			}
			jBlueprintType.setSelectedItem(oldValue);
		}
		if (itemExist()) { //Editing existing item
			colorIsSet = true;
			ColorSettings.config(jCountMinimum, ColorEntry.GLOBAL_ENTRY_WARNING);
		}
		try {
			double d = Double.valueOf(jCountMinimum.getText());
			if (d <= 0) {
				valid = false; //Negative and zero is not valid
				colorIsSet = true;
				ColorSettings.config(jCountMinimum, ColorEntry.GLOBAL_ENTRY_INVALID);
			}
		} catch (NumberFormatException ex) {
			valid = false; //Empty and NaN is not valid
			if (!jCountMinimum.getText().isEmpty()) {
				colorIsSet = true;
				ColorSettings.config(jCountMinimum, ColorEntry.GLOBAL_ENTRY_INVALID);
			}
		}
		if (!colorIsSet) {
			jCountMinimum.setBackground(Colors.TEXTFIELD_BACKGROUND.getColor());
			jCountMinimum.setForeground(Colors.TEXTFIELD_FOREGROUND.getColor());
		}
		jOK.setEnabled(valid);
		updating = oldUpdateValue;
	}

	private void autoSet() {
		final StockpileItem item = getExistingItem();
		if (item != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (updating) {
						return;
					}
					boolean oldUpdateValue = updating;
					updating = true;
					jCountMinimum.setText(String.valueOf(item.getCountMinimum()));
					updating = oldUpdateValue;
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
		Settings.lock("Stockpile (Items Dialog)"); //Lock for Stockpile (Items Dialog)
		if (stockpileItem instanceof SubpileStock) { //Edit SubpileStock
			SubpileStock subpileStock = (SubpileStock) stockpileItem;
			StockpileItem editItem = getStockpileItem();
			subpileStock.setCountMinimum(editItem.getCountMinimum());
		} else if (stockpileItem != null) { //EDIT
			if (itemExist()) { //EDIT + UPDATING (Editing to an existing item)
				StockpileItem existingItem = getExistingItem();
				existingItem.getStockpile().remove(existingItem);
				program.getStockpileTool().removeItem(existingItem);
			}
			stockpileItem.update(getStockpileItem());
		} else if (itemExist()) { //UPDATING (Adding an existing item)
			stockpileItem = getExistingItem();
			stockpileItem.update(getStockpileItem());
		} else { //ADD 
			stockpileItems = getStockpileItems();
			if (stockpileItems != null) {
				for (StockpileItem item : stockpileItems) {
					stockpile.add(item);
				}
			} else {
				stockpileItem = getStockpileItem();
				stockpile.add(stockpileItem);
			}
		}
		Settings.unlock("Stockpile (Items Dialog)"); //Unlock for Stockpile (Items Dialog)
		program.saveSettings("Stockpile (Items Dialog)");
		super.setVisible(false);
	}

	private class ListenerClass implements ActionListener, CaretListener, ItemListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (StockpileItemAction.OK.name().equals(e.getActionCommand())) {
				save();
			} else if (StockpileItemAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			} else if (StockpileItemAction.TYPE_CHANGE.name().equals(e.getActionCommand())) {
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
	}

	static class ItemFilterator implements TextFilterator<Item> {
		@Override
		public void getFilterStrings(final List<String> baseList, final Item element) {
			baseList.add(element.getTypeName());
		}
	}
}
