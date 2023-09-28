/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import javax.swing.JCheckBox;
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
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingFacility;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingSecurity;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels.ItemFilterator;
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
	private final JLabel jBlueprintTypeLabel;
	private final JComboBox<BlueprintAddType> jBlueprintType;
	private final JComboBox<Integer> jMe;
	private final JComboBox<ManufacturingFacility> jFacility;
	private final JComboBox<ManufacturingRigs> jRigs;
	private final JComboBox<ManufacturingSecurity> jSecurity;
	private final JLabel jIgnoreMultiplierLabel;
	private final JCheckBox jIgnoreMultiplier;

	private final List<JComponent> manufacturingComponents = new ArrayList<>();
	private final EventList<Item> items = EventListManager.create();
	private Stockpile stockpile;
	private StockpileItem stockpileItem;
	private List<StockpileItem> stockpileItems;
	private BlueprintAddType lastBlueprintAddType = null;
	private boolean updating = false;

	public StockpileItemDialog(final Program program) {
		super(program, TabsStockpile.get().addStockpileItem(), Images.TOOL_STOCKPILE.getImage());

		ListenerClass listener = new ListenerClass();

		JLabel jItemsLabel = new JLabel(TabsStockpile.get().item());
		jItems = new JComboBox<>();
		AutoCompleteSupport<Item> itemAutoComplete = AutoCompleteSupport.install(jItems, EventModels.createSwingThreadProxyList(items), new ItemFilterator());
		itemAutoComplete.setStrict(true);
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

		jIgnoreMultiplierLabel = new JLabel(TabsStockpile.get().multiplier());
		jIgnoreMultiplier = new JCheckBox(TabsStockpile.get().multiplierIgnore());

		jBlueprintTypeLabel = new JLabel(TabsStockpile.get().blueprintType());
		jBlueprintType = new JComboBox<>(BlueprintAddType.values());
		jBlueprintType.setActionCommand(StockpileItemAction.TYPE_CHANGE.name());
		jBlueprintType.addActionListener(listener);

		JLabel jMeLabel = new JLabel(TabsStockpile.get().blueprintMe());
		manufacturingComponents.add(jMeLabel);
		Integer[] me = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		jMe = new JComboBox<>(me);
		jMe.setPrototypeDisplayValue(10);
		jMe.setMaximumRowCount(me.length);
		manufacturingComponents.add(jMe);

		JLabel jFacilityLabel = new JLabel(TabsStockpile.get().blueprintFacility());
		manufacturingComponents.add(jFacilityLabel);
		jFacility = new JComboBox<>(ManufacturingSettings.ManufacturingFacility.values());
		jFacility.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ManufacturingFacility facility = jFacility.getItemAt(jFacility.getSelectedIndex());
				if (facility == ManufacturingFacility.STATION) {
					jRigs.setSelectedIndex(0);
					jRigs.setEnabled(false);
				} else {
					jRigs.setEnabled(true);
				}
			}
		});
		manufacturingComponents.add(jFacility);

		JLabel jRigsLabel = new JLabel(TabsStockpile.get().blueprintRigs());
		manufacturingComponents.add(jRigsLabel);
		jRigs = new JComboBox<>(ManufacturingRigs.values());
		jRigs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ManufacturingRigs rigs = jRigs.getItemAt(jRigs.getSelectedIndex());
				if (rigs == ManufacturingRigs.NONE) {
					jSecurity.setSelectedIndex(0);
					jSecurity.setEnabled(false);
				} else {
					jSecurity.setEnabled(true);
				}
			}
		});
		manufacturingComponents.add(jRigs);

		JLabel jSecurityLabel = new JLabel(TabsStockpile.get().blueprintSecurity());
		manufacturingComponents.add(jSecurityLabel);
		jSecurity = new JComboBox<>(ManufacturingSecurity.values());
		manufacturingComponents.add(jSecurity);

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
						.addComponent(jMeLabel)
						.addComponent(jFacilityLabel)
						.addComponent(jRigsLabel)
						.addComponent(jSecurityLabel)
						.addComponent(jIgnoreMultiplierLabel)
						.addComponent(jCountMinimumLabel)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jItems, 300, 300, 300)
						.addComponent(jSubpile, 300, 300, 300)
						.addComponent(jBlueprintType, 300, 300, 300)
						.addComponent(jMe, 300, 300, 300)
						.addComponent(jFacility, 300, 300, 300)
						.addComponent(jRigs, 300, 300, 300)
						.addComponent(jSecurity, 300, 300, 300)
						.addComponent(jIgnoreMultiplier, 300, 300, 300)
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
					.addComponent(jMeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMe, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jFacilityLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFacility, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jRigsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRigs, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jSecurityLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSecurity, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jIgnoreMultiplierLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jIgnoreMultiplier, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
			jBlueprintTypeLabel.setVisible(false);
			jBlueprintType.setVisible(false);
			jIgnoreMultiplierLabel.setVisible(false);
			jIgnoreMultiplier.setVisible(false);
			jItems.setVisible(false);
		} else {
			jItems.setSelectedItem(item);
			jSubpile.setVisible(false);
			jBlueprintTypeLabel.setVisible(true);
			jBlueprintType.setVisible(true);
			jIgnoreMultiplierLabel.setVisible(true);
			jIgnoreMultiplier.setVisible(true);
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
		} else if (stockpileItem != null) {
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
		jIgnoreMultiplier.setSelected(false);
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
			countMinimum = Double.parseDouble(jCountMinimum.getText());
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
		boolean ignoreMultiplier = jIgnoreMultiplier.isSelected();
		return new StockpileItem(getStockpile(), item, typeID, countMinimum, runs, ignoreMultiplier);
	}

	private List<StockpileItem> getStockpileItems() {
		List<StockpileItem> itemsMaterial = new ArrayList<>();
		Item item = (Item) jItems.getSelectedItem();
		double countMinimum;
		try {
			countMinimum = Double.parseDouble(jCountMinimum.getText());
		} catch (NumberFormatException ex) {
			countMinimum = 0;
		}
		boolean ignoreMultiplier = jIgnoreMultiplier.isSelected();
		Integer me = jMe.getItemAt(jMe.getSelectedIndex());
		ManufacturingFacility facility = jFacility.getItemAt(jFacility.getSelectedIndex());
		ManufacturingRigs rigs = jRigs.getItemAt(jRigs.getSelectedIndex());
		ManufacturingSecurity security = jSecurity.getItemAt(jSecurity.getSelectedIndex());
		if (jBlueprintType.isEnabled() && jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex()) == BlueprintAddType.MANUFACTURING_MATERIALS) {
			 //Manufacturing Materials
			for (IndustryMaterial material : item.getManufacturingMaterials()) {
				Item materialItem = ApiIdConverter.getItem(material.getTypeID());
				double count = ApiIdConverter.getManufacturingQuantity(material.getQuantity(), me, facility, rigs, security, countMinimum, false);
				itemsMaterial.add(new StockpileItem(getStockpile(), materialItem, material.getTypeID(), count, false, ignoreMultiplier));
			}
		} else if (jBlueprintType.isEnabled() && jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex()) == BlueprintAddType.REACTION_MATERIALS) {
			//Reaction Materials
			for (IndustryMaterial material : item.getReactionMaterials()) {
				Item materialItem = ApiIdConverter.getItem(material.getTypeID());
				double count = ApiIdConverter.getManufacturingQuantity(material.getQuantity(), me, facility, rigs, security, countMinimum, false);
				itemsMaterial.add(new StockpileItem(getStockpile(), materialItem, material.getTypeID(), count, false, ignoreMultiplier));
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
		Object object = jItems.getSelectedItem();
		if (object == null || !(object instanceof Item)) {
			valid = false; //No item selected
		}
		if (itemExist()) { //Editing existing item
			colorIsSet = true;
			ColorSettings.config(jCountMinimum, ColorEntry.GLOBAL_ENTRY_WARNING);
		}
		try {
			double d = Double.parseDouble(jCountMinimum.getText());
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
			ColorSettings.configReset(jCountMinimum);
		}
		jOK.setEnabled(valid);
		updating = oldUpdateValue;
	}

	private void autoSet() {
		if (jItems.getSelectedItem() == null || !(jItems.getSelectedItem() instanceof Item)) {
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
			} else if (item.isBlueprint()) {
				jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EDIT_BLUEPRINT));
				jBlueprintType.setEnabled(true);
			} else if (item.isFormula()) {
				jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EDIT_FORMULA));
				jBlueprintType.setEnabled(true);
			} else {
				jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EMPTY));
				jBlueprintType.setEnabled(false);
			}
			jBlueprintType.setSelectedItem(oldValue);
		}
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
					jIgnoreMultiplier.setSelected(item.isIgnoreMultiplier());
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
				program.getStockpileTab().removeItem(existingItem);
			}
			stockpileItem.update(getStockpileItem());
		} else if (itemExist()) { //UPDATING (Adding an existing item)
			stockpileItem = getExistingItem();
			stockpileItem.update(getStockpileItem());
		} else { //ADD
			stockpileItems = getStockpileItems();
			if (stockpileItems != null) {
				for (StockpileItem item : stockpileItems) {
					if (!stockpile.add(item)) { //ADD MERGE (Only used by manufacturing materials)
						for (StockpileItem current : stockpile.getItems()) {
							if (!current.getTypeID().equals(item.getTypeID())) {
								continue;
							}
							current.setCountMinimum(item.getCountMinimum() + current.getCountMinimum());
							break;
						}
					}
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
				BlueprintAddType currentBlueprintAddType = jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex());
				if ((lastBlueprintAddType == null || lastBlueprintAddType != BlueprintAddType.MANUFACTURING_MATERIALS) && jBlueprintType.isEnabled() && currentBlueprintAddType == BlueprintAddType.MANUFACTURING_MATERIALS) {
					for (JComponent jComponent : manufacturingComponents) {
						jComponent.setVisible(true);
					}
					jMe.setSelectedIndex(0);
					jFacility.setSelectedIndex(0);
					jRigs.setSelectedIndex(0);
					getDialog().pack();
				} else if ((lastBlueprintAddType == null || lastBlueprintAddType == BlueprintAddType.MANUFACTURING_MATERIALS) && currentBlueprintAddType != BlueprintAddType.MANUFACTURING_MATERIALS) {
					for (JComponent jComponent : manufacturingComponents) {
						jComponent.setVisible(false);
					}
					getDialog().pack();
				}
				lastBlueprintAddType = currentBlueprintAddType;
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
}
