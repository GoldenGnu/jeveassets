/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ReactionRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingSecurity;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ReactionSecurity;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels.ItemFilterator;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItemMaterial;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileStock;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StockpileItemDialog extends JDialogCentered {

	private enum StockpileItemAction {
		CANCEL,
		OK,
		TYPE_CHANGE
	}

	private static final int WIDTH = 320;

	public static enum BlueprintAddType {
		NONE(TabsStockpile.get().none()),
		BPO(TabsStockpile.get().original()),
		FORMULA(TabsStockpile.get().formula()),
		BPC(TabsStockpile.get().copy()),
		RUNS(TabsStockpile.get().runs()),
		MANUFACTURING_MATERIALS_ONCE(TabsStockpile.get().materialsManufacturing()),
		MANUFACTURING_MATERIALS_EDITABLE(TabsStockpile.get().materialsManufacturingEditable()),
		REACTION_MATERIALS_ONCE(TabsStockpile.get().materialsReaction()),
		REACTION_MATERIALS_EDITABLE(TabsStockpile.get().materialsReactionEditable()),
		;
		final String name;

		public static BlueprintAddType[] EMPTY = {NONE};
		public static BlueprintAddType[] ADD_BLUEPRINT = {BPO, BPC, RUNS, MANUFACTURING_MATERIALS_ONCE, MANUFACTURING_MATERIALS_EDITABLE};
		public static BlueprintAddType[] EDIT_BLUEPRINT = {BPO, BPC, RUNS, MANUFACTURING_MATERIALS_EDITABLE};
		public static BlueprintAddType[] ADD_FORMULA = {FORMULA, REACTION_MATERIALS_ONCE, REACTION_MATERIALS_EDITABLE};
		public static BlueprintAddType[] EDIT_FORMULA = {FORMULA, REACTION_MATERIALS_EDITABLE};

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
	private final JLabel jCountMinimumLabel;
	private final JTextField jCountMinimum;
	private final JLabel jBlueprintTypeLabel;
	private final JComboBox<BlueprintAddType> jBlueprintType;
	private final JComboBox<Integer> jMaterialEfficiency;
	private final JComboBox<ManufacturingFacility> jFacility;
	private final JComboBox<ManufacturingRigs> jRigs;
	private final JComboBox<ManufacturingSecurity> jSecurity;
	private final JComboBox<ReactionRigs> jRigsReactions;
	private final JComboBox<ReactionSecurity> jSecurityReactions;
	private final JLabel jFacilityOverwriteLabel;
	private final JCheckBox jFacilityOverwrite;
	private final JLabel jRecursiveLevelLabel;
	private final JComboBox<Integer> jRecursiveLevel;
	private final JLabel jMaterialEfficiencyOverwriteLabel;
	private final JComboBox<MaterialEfficiencyOverwrite> jMaterialEfficiencyOverwrite;
	private final JLabel jIgnoreMultiplierLabel;
	private final JCheckBox jIgnoreMultiplier;
	private final JLabel jRoundALotLabel;
	private final JCheckBox jRoundALot;

	private final StockpileTab stockpileTab;
	private final List<JComponent> manufacturingComponents = new ArrayList<>();
	private final List<JComponent> manufacturingEditComponents = new ArrayList<>();
	private final List<JComponent> reactionComponents = new ArrayList<>();
	private final List<JComponent> reactionEditComponents = new ArrayList<>();
	private final EventList<Item> items = EventListManager.create();
	private Stockpile stockpile;
	private StockpileItem stockpileItem;
	private List<StockpileItem> stockpileItems;
	private BlueprintAddType lastBlueprintAddType = null;
	private boolean updating = false;

	public StockpileItemDialog(final StockpileTab stockpileTab, final Program program) {
		super(program, TabsStockpile.get().addStockpileItem(), Images.TOOL_STOCKPILE.getImage());
		this.stockpileTab = stockpileTab;

		ListenerClass listener = new ListenerClass();

		JLabel jItemsLabel = new JLabel(TabsStockpile.get().item());
	//Items
		jItems = new JComboBox<>();
		AutoCompleteSupport<Item> itemAutoComplete = AutoCompleteSupport.install(jItems, EventModels.createSwingThreadProxyList(items), new ItemFilterator());
		itemAutoComplete.setStrict(true);
		jItems.addItemListener(listener); //Must be added after AutoCompleteSupport

	//Subpile
		jSubpile = new JLabel();

	//Blueprint Type
		jBlueprintTypeLabel = new JLabel(TabsStockpile.get().blueprintType());

		jBlueprintType = new JComboBox<>(BlueprintAddType.values());
		jBlueprintType.setActionCommand(StockpileItemAction.TYPE_CHANGE.name());
		jBlueprintType.addActionListener(listener);

	//ME
		JLabel jMeLabel = new JLabel(TabsStockpile.get().blueprintMe());
		manufacturingComponents.add(jMeLabel);

		Integer[] me = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		jMaterialEfficiency = new JComboBox<>(me);
		jMaterialEfficiency.setPrototypeDisplayValue(10);
		jMaterialEfficiency.setMaximumRowCount(me.length);
		manufacturingComponents.add(jMaterialEfficiency);

	//Facility
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

	//Rigs
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

	//Security
		JLabel jSecurityLabel = new JLabel(TabsStockpile.get().blueprintSecurity());
		manufacturingComponents.add(jSecurityLabel);
		manufacturingEditComponents.add(jSecurityLabel);

		jSecurity = new JComboBox<>(ManufacturingSecurity.values());
		manufacturingComponents.add(jSecurity);
		manufacturingEditComponents.add(jSecurity);

	//Security Reactions
		JLabel jSecurityReactionsLabel = new JLabel(TabsStockpile.get().blueprintSecurity());
		reactionComponents.add(jSecurityReactionsLabel);
		jSecurityReactions = new JComboBox<>(ReactionSecurity.values());
		reactionComponents.add(jSecurityReactions);

	//Rigs Reactions
		JLabel jRigsReactionsLabel = new JLabel(TabsStockpile.get().blueprintRigs());
		reactionComponents.add(jRigsReactionsLabel);
		jRigsReactions = new JComboBox<>(ReactionRigs.values());
		jRigsReactions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ReactionRigs rigs = jRigsReactions.getItemAt(jRigsReactions.getSelectedIndex());
				if (rigs == ReactionRigs.NONE) {
					jSecurityReactions.setSelectedIndex(0);
					jSecurityReactions.setEnabled(false);
				} else {
					jSecurityReactions.setEnabled(true);
				}
			}
		});
		reactionComponents.add(jRigsReactions);

	//Overwrite Facility
		jFacilityOverwriteLabel = new JLabel(TabsStockpile.get().materialsRecursiveFacility());
		manufacturingEditComponents.add(jFacilityOverwriteLabel);
		reactionEditComponents.add(jFacilityOverwriteLabel);
		jFacilityOverwrite = new JCheckBox(TabsStockpile.get().materialsRecursiveOverwrite());
		manufacturingEditComponents.add(jFacilityOverwrite);
		reactionEditComponents.add(jFacilityOverwrite);

	//Recursive Level
		jRecursiveLevelLabel = new JLabel(TabsStockpile.get().materialsRecursiveLevel());
		manufacturingEditComponents.add(jRecursiveLevelLabel);
		reactionEditComponents.add(jRecursiveLevelLabel);
		jRecursiveLevel = new JComboBox<>();
		jRecursiveLevel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Integer level = jRecursiveLevel.getItemAt(jRecursiveLevel.getSelectedIndex());
				if (level > 0) {
					jFacilityOverwrite.setEnabled(true);
					jMaterialEfficiencyOverwrite.setEnabled(true);
				} else {
					jMaterialEfficiencyOverwrite.setEnabled(false);
					jFacilityOverwrite.setSelected(stockpileItem == null); //Add = true Else false
					jFacilityOverwrite.setEnabled(false);
					jMaterialEfficiencyOverwrite.setSelectedIndex(0);
				}
			}
		});
		manufacturingEditComponents.add(jRecursiveLevel);
		reactionEditComponents.add(jRecursiveLevel);

	//Material ME
		jMaterialEfficiencyOverwriteLabel = new JLabel(TabsStockpile.get().materialsRecursiveMe());
		manufacturingEditComponents.add(jMaterialEfficiencyOverwriteLabel);
		jMaterialEfficiencyOverwrite = new JComboBox<>();
		jMaterialEfficiencyOverwrite.setPrototypeDisplayValue(MaterialEfficiencyOverwrite.KEEP);
		jMaterialEfficiencyOverwrite.setMaximumRowCount(MaterialEfficiencyOverwrite.EDIT.length);
		manufacturingEditComponents.add(jMaterialEfficiencyOverwrite);

	//Ignore Multiplier
		jIgnoreMultiplierLabel = new JLabel(TabsStockpile.get().multiplier());
		jIgnoreMultiplier = new JCheckBox(TabsStockpile.get().multiplierIgnore());

	//Round a lot
		jRoundALotLabel = new JLabel(TabsStockpile.get().roundALot());
		jRoundALot = new JCheckBox(TabsStockpile.get().roundALot());

	//Count Minimum
		jCountMinimumLabel = new JLabel(TabsStockpile.get().countMinimum());
		jCountMinimum = new JTextField();
		jCountMinimum.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				jCountMinimum.selectAll();
			}
		});
		jCountMinimum.addCaretListener(listener);

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
						.addComponent(jRigsReactionsLabel)
						.addComponent(jSecurityReactionsLabel)
						.addComponent(jFacilityOverwriteLabel)
						.addComponent(jRecursiveLevelLabel)
						.addComponent(jMaterialEfficiencyOverwriteLabel)
						.addComponent(jIgnoreMultiplierLabel)
						.addComponent(jRoundALotLabel)
						.addComponent(jCountMinimumLabel)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jItems, WIDTH, WIDTH, WIDTH)
						.addComponent(jSubpile, WIDTH, WIDTH, WIDTH)
						.addComponent(jBlueprintType, WIDTH, WIDTH, WIDTH)
						.addComponent(jMaterialEfficiency, WIDTH, WIDTH, WIDTH)
						.addComponent(jFacility, WIDTH, WIDTH, WIDTH)
						.addComponent(jRigs, WIDTH, WIDTH, WIDTH)
						.addComponent(jSecurity, WIDTH, WIDTH, WIDTH)
						.addComponent(jRigsReactions, WIDTH, WIDTH, WIDTH)
						.addComponent(jSecurityReactions, WIDTH, WIDTH, WIDTH)
						.addComponent(jFacilityOverwrite, WIDTH, WIDTH, WIDTH)
						.addComponent(jRecursiveLevel, WIDTH, WIDTH, WIDTH)
						.addComponent(jMaterialEfficiencyOverwrite, WIDTH, WIDTH, WIDTH)
						.addComponent(jIgnoreMultiplier, WIDTH, WIDTH, WIDTH)
						.addComponent(jRoundALot, WIDTH, WIDTH, WIDTH)
						.addComponent(jCountMinimum, WIDTH, WIDTH, WIDTH)
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
					.addComponent(jMaterialEfficiency, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
					.addComponent(jRigsReactionsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRigsReactions, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jSecurityReactionsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSecurityReactions, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jFacilityOverwriteLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFacilityOverwrite, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jRecursiveLevelLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRecursiveLevel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jMaterialEfficiencyOverwriteLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMaterialEfficiencyOverwrite, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jIgnoreMultiplierLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jIgnoreMultiplier, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jRoundALotLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRoundALot, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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

	protected List<StockpileItem> showEdit(final StockpileItem editStockpileItem) {
		updateData();
		this.stockpileItem = editStockpileItem;
		this.getDialog().setTitle(TabsStockpile.get().editStockpileItem());
		Item item = ApiIdConverter.getItem(editStockpileItem.getTypeID());
		if (editStockpileItem instanceof SubpileStock) {
			jItems.setVisible(false);
			jSubpile.setText(editStockpileItem.getName());
			jSubpile.setVisible(true);
			jBlueprintTypeLabel.setVisible(false);
			jBlueprintType.setVisible(false);
			jIgnoreMultiplierLabel.setVisible(false);
			jIgnoreMultiplier.setVisible(false);
			jRoundALotLabel.setVisible(false);
			jRoundALot.setVisible(false);
			jCountMinimumLabel.setVisible(true);
			jCountMinimum.setVisible(true);
		} else if (editStockpileItem.isSubMaterial()) {
			jItems.setVisible(false);
			jItems.setSelectedItem(item);
			jSubpile.setText(editStockpileItem.getName());
			jSubpile.setVisible(true);
			jBlueprintTypeLabel.setVisible(false);
			jBlueprintType.setVisible(false);
			jIgnoreMultiplierLabel.setVisible(false);
			jIgnoreMultiplier.setVisible(false);
			jRoundALotLabel.setVisible(false);
			jRoundALot.setVisible(false);
			jCountMinimumLabel.setVisible(false);
			jCountMinimum.setVisible(false);
		} else {
			jItems.setVisible(true);
			jItems.setSelectedItem(item);
			jSubpile.setVisible(false);
			jBlueprintTypeLabel.setVisible(true);
			jBlueprintType.setVisible(true);
			jIgnoreMultiplierLabel.setVisible(true);
			jIgnoreMultiplier.setVisible(true);
			jRoundALotLabel.setVisible(true);
			jRoundALot.setVisible(true);
			jCountMinimumLabel.setVisible(true);
			jCountMinimum.setVisible(true);
		}
		if (item.isBlueprint()) {
			jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EDIT_BLUEPRINT));
		} else if (item.isFormula()) {
			jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EDIT_FORMULA));
		} else {
			jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EMPTY));
		}
		if (stockpileItem instanceof StockpileItemMaterial) {
			if (item.isBlueprint()) {
				jBlueprintType.setSelectedItem(BlueprintAddType.MANUFACTURING_MATERIALS_EDITABLE);
			} else {
				jBlueprintType.setSelectedItem(BlueprintAddType.REACTION_MATERIALS_EDITABLE);
			}
			StockpileItemMaterial materialItem = (StockpileItemMaterial) editStockpileItem;
			int recursiveLevel;
			if (materialItem.getItem().isFormula()) {
				recursiveLevel = materialItem.getFormulaRecursiveLevel();
				jRigsReactions.setSelectedItem(materialItem.getRigsReactions());
				jSecurityReactions.setSelectedItem(materialItem.getSecurityReactions());
			} else {
				recursiveLevel = materialItem.getBlueprintRecursiveLevel();
				jMaterialEfficiency.setSelectedItem(materialItem.getME());
				jFacility.setSelectedItem(materialItem.getFacility());
				jRigs.setSelectedItem(materialItem.getRigs());
				jSecurity.setSelectedItem(materialItem.getSecurity());
				jMaterialEfficiencyOverwrite.setModel(new DefaultComboBoxModel<>(MaterialEfficiencyOverwrite.EDIT));
				jMaterialEfficiencyOverwrite.setSelectedIndex(0);
				jMaterialEfficiencyOverwrite.setEnabled(!materialItem.getMaterials().isEmpty());
			}
			jFacilityOverwrite.setSelected(false);
			jFacilityOverwrite.setEnabled(!materialItem.getMaterials().isEmpty());
			jRecursiveLevel.setSelectedItem(recursiveLevel);
			
		} else if (editStockpileItem.getItem().isFormula()) {
			jBlueprintType.setSelectedItem(BlueprintAddType.FORMULA);
		} else if (editStockpileItem.isBPO()) {
			jBlueprintType.setSelectedItem(BlueprintAddType.BPO);
		} else if (editStockpileItem.isBPC()) {
			jBlueprintType.setSelectedItem(BlueprintAddType.BPC);
		} else if (editStockpileItem.isRuns()) {
			jBlueprintType.setSelectedItem(BlueprintAddType.RUNS);
		}
		jIgnoreMultiplier.setSelected(editStockpileItem.isIgnoreMultiplier());
		jRoundALot.setSelected(editStockpileItem.isRoundALot());
		jBlueprintType.setEnabled(item.isBlueprint());
		jCountMinimum.setText(String.valueOf(editStockpileItem.getCountMinimum()));
		show();
		return getReturn();
	}

	protected List<StockpileItem> showAdd(final Stockpile addStockpile) {
		updateData();
		this.stockpile = addStockpile;
		this.getDialog().setTitle(TabsStockpile.get().addStockpileItem());
		jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EMPTY));
		show();
		return getReturn();
	}

	private List<StockpileItem> getReturn() {
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
		stockpileItems = null;
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
		jBlueprintTypeLabel.setVisible(true);
		jBlueprintType.setVisible(true);
		jCountMinimumLabel.setVisible(true);
		jCountMinimum.setVisible(true);
		jCountMinimum.setText("");
		jIgnoreMultiplierLabel.setVisible(true);
		jIgnoreMultiplier.setVisible(true);
		jIgnoreMultiplier.setSelected(false);
		jRoundALotLabel.setVisible(true);
		jRoundALot.setVisible(true);
		jRoundALot.setSelected(false);
		jMaterialEfficiencyOverwrite.setModel(new DefaultComboBoxModel<>(MaterialEfficiencyOverwrite.ADD));
		jMaterialEfficiencyOverwrite.setSelectedIndex(0);
		jMaterialEfficiencyOverwrite.setEnabled(false);
		jFacilityOverwrite.setSelected(true);
		jFacilityOverwrite.setEnabled(false);
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
		return getStockpileItems().get(0);
	}

	private List<StockpileItem> getStockpileItems() {
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
		boolean roundALotLabel = jRoundALot.isSelected();
		if (jBlueprintType.isEnabled()) {
			BlueprintAddType blueprintAddType = jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex());
			Integer me = jMaterialEfficiency.getItemAt(jMaterialEfficiency.getSelectedIndex());
			ManufacturingFacility facility = jFacility.getItemAt(jFacility.getSelectedIndex());
			ManufacturingRigs rigs = jRigs.getItemAt(jRigs.getSelectedIndex());
			ReactionRigs rigsReactions = jRigsReactions.getItemAt(jRigsReactions.getSelectedIndex());
			ManufacturingSecurity security = jSecurity.getItemAt(jSecurity.getSelectedIndex());
			ReactionSecurity securityReactions = jSecurityReactions.getItemAt(jSecurityReactions.getSelectedIndex());
			Integer recursiveLevel = jRecursiveLevel.getItemAt(jRecursiveLevel.getSelectedIndex());
			if (stockpileItem instanceof StockpileItemMaterial && stockpileItem.isSubMaterial()) {
				StockpileItemMaterial material = (StockpileItemMaterial) stockpileItem;
				if (material.getItem().isFormula()) {
					recursiveLevel = material.getFormulaRecursiveLevel();
				} else if (stockpileItem.getItem().isBlueprint()) {
					recursiveLevel = material.getBlueprintRecursiveLevel();
				}
			}
			if (recursiveLevel == null) {
				recursiveLevel = 0;
			}
			boolean facilityOverwrite = jFacilityOverwrite.isSelected();
			MaterialEfficiencyOverwrite meOverwrite = jMaterialEfficiencyOverwrite.getItemAt(jMaterialEfficiencyOverwrite.getSelectedIndex());
			if (blueprintAddType == BlueprintAddType.MANUFACTURING_MATERIALS_ONCE) {
				//Manufacturing Materials
				List<StockpileItem> itemsMaterial = new ArrayList<>();
				for (IndustryMaterial material : item.getManufacturingMaterials()) {
					Item materialItem = ApiIdConverter.getItem(material.getTypeID());
					double count = ApiIdConverter.getManufacturingQuantity(material.getQuantity(), me, facility, rigs, security, countMinimum, false);
					itemsMaterial.add(new StockpileItem(getStockpile(), materialItem, material.getTypeID(), count, false, ignoreMultiplier, roundALotLabel));
				} 
				return itemsMaterial;
			} else if (blueprintAddType == BlueprintAddType.MANUFACTURING_MATERIALS_EDITABLE) {
				//Manufacturing Materials Editable
				return Collections.singletonList(new StockpileItemMaterial(getStockpile(), item, item.getProductTypeID(), countMinimum, ignoreMultiplier, roundALotLabel, recursiveLevel, meOverwrite.getME(), me, facilityOverwrite, facility, rigs, security));
			} else if (blueprintAddType == BlueprintAddType.REACTION_MATERIALS_ONCE) {
				//Reaction Materials
				List<StockpileItem> itemsMaterial = new ArrayList<>();
				for (IndustryMaterial material : item.getReactionMaterials()) {
					Item materialItem = ApiIdConverter.getItem(material.getTypeID());
					double count = ApiIdConverter.getReactionQuantity(material.getQuantity(), rigsReactions, securityReactions, countMinimum, false);
					itemsMaterial.add(new StockpileItem(getStockpile(), materialItem, material.getTypeID(), count, false, ignoreMultiplier, roundALotLabel));
				}
				return itemsMaterial;
			} else if (blueprintAddType == BlueprintAddType.REACTION_MATERIALS_EDITABLE) {
				//Reaction Materials Editable
				return Collections.singletonList(new StockpileItemMaterial(getStockpile(), item, item.getProductTypeID(), countMinimum, ignoreMultiplier, roundALotLabel, recursiveLevel, facilityOverwrite, rigsReactions, securityReactions));
			}
		}
		return Collections.singletonList(new StockpileItem(getStockpile(), item, typeID, countMinimum, runs, ignoreMultiplier, roundALotLabel));
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
		BlueprintAddType blueprintAddType = jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex());
		boolean materialsAdd = jBlueprintType.isEnabled() 
				&& (blueprintAddType == BlueprintAddType.MANUFACTURING_MATERIALS_ONCE || blueprintAddType == BlueprintAddType.REACTION_MATERIALS_ONCE);
		if (materialsAdd) {//Not editable
			return null;
		}
		boolean materialsEdit = jBlueprintType.isEnabled() && (blueprintAddType == BlueprintAddType.MANUFACTURING_MATERIALS_EDITABLE || blueprintAddType == BlueprintAddType.REACTION_MATERIALS_EDITABLE);
		boolean runs = jBlueprintType.isEnabled() && blueprintAddType == BlueprintAddType.RUNS;
		boolean copy = runs || (jBlueprintType.isEnabled() && blueprintAddType == BlueprintAddType.BPC);
		for (StockpileItem item : existing.getItems()) {
			if (item.getTypeID() == typeItem.getTypeID() && copy == item.isBPC() && runs == item.isRuns() && materialsEdit == item.isMaterial()) {
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
			if (stockpileItem != null) {
				if (item.isBlueprint()) {
					jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EDIT_BLUEPRINT));
					jBlueprintType.setEnabled(true);
				} else if (item.isFormula()) {
					jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.EDIT_FORMULA));
					jBlueprintType.setEnabled(true);
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
				jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.ADD_BLUEPRINT));
				jBlueprintType.setEnabled(true);
			} else if (item.isFormula()) {
				jBlueprintType.setModel(new DefaultComboBoxModel<>(BlueprintAddType.ADD_FORMULA));
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
					jRoundALot.setSelected(item.isRoundALot());
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
			stockpileItems = getStockpileItems();
			if (stockpileItems != null && stockpileItems.size() == 1) {
				StockpileItem item = stockpileItems.get(0);
				stockpileItems = null;
				if (itemExist()) { //EDIT + UPDATING (Editing to an existing item)
					System.out.println("EXITING");
					StockpileItem existingItem = getExistingItem();
					existingItem.getStockpile().remove(existingItem);
					stockpileTab.removeItem(existingItem);
				}
				if (item.getClass().equals(stockpileItem.getClass())) {
					System.out.println("UPDATE");
					stockpileItem.update(item);
				} else { //New class, remove old item
					System.out.println("EDIT NEW");
					stockpileItem.getStockpile().remove(stockpileItem);
					stockpileTab.removeItem(stockpileItem);
					stockpileItem = item;
				}
			}
		//} else if (itemExist()) { //UPDATING (Adding an existing item)
		//	stockpileItem = getExistingItem();
		//	stockpileItem.update(getStockpileItem());
		} else { //ADD
			System.out.println("ADD");
			stockpileItems = getStockpileItems();
			if (stockpileItems != null) {
				for (StockpileItem item : stockpileItems) {
					if (!stockpile.add(item)) { //ADD MERGE (Only used by manufacturing materials)
						for (StockpileItem current : stockpile.getItems()) {
							if (!current.equals(item)) {
								continue;
							}
							System.out.println("ADD MERGE");
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

	private Integer[] getRecursiveLevelBlueprints() {
		Item item = (Item) jItems.getSelectedItem();
		return getRecursiveLevelBlueprints(item);
	}

	private Integer[] getRecursiveLevelFormulas() {
		Item item = (Item) jItems.getSelectedItem();
		return getRecursiveLevelFormulas(item);
	}

	public static Integer[] getRecursiveLevelBlueprints(Item item) {
		int maxLevel = getRecursiveLevelBlueprints(item, 0);
		Integer[] levels = new Integer[maxLevel + 1];
		for (int i = 0; i <= maxLevel; i++) {
			levels[i] = i;
		}
		return levels;
	}
	public static Integer[] getRecursiveLevelFormulas(Item item) {
		int maxLevel = getRecursiveLevelFormulas(item, 0);
		Integer[] levels = new Integer[maxLevel + 1];
		for (int i = 0; i <= maxLevel; i++) {
			levels[i] = i;
		}
		return levels;
	}

	private static int getRecursiveLevelBlueprints(Item item, int level) {
		int returnLevel = level;
		for (IndustryMaterial material : item.getManufacturingMaterials()) {
			Item materialItem = ApiIdConverter.getItem(material.getTypeID());
			if (materialItem.getBlueprintTypeID() > 0) {
				Item blueprintItem = ApiIdConverter.getItem(materialItem.getBlueprintTypeID());
				returnLevel = Math.max(returnLevel, getRecursiveLevelBlueprints(blueprintItem, level + 1));
			}
		}
		return returnLevel;
	}

	private static int getRecursiveLevelFormulas(Item item, int level) {
		int returnLevel = level;
		for (IndustryMaterial material : item.getReactionMaterials()) {
			Item materialItem = ApiIdConverter.getItem(material.getTypeID());
			if (materialItem.getFormulaTypeID() > 0) {
				Item formulaItem = ApiIdConverter.getItem(materialItem.getFormulaTypeID());
				returnLevel = Math.max(returnLevel, getRecursiveLevelFormulas(formulaItem, level + 1));
			}
		}
		return returnLevel;
	}

	public static class MaterialEfficiencyOverwrite {
		public static final MaterialEfficiencyOverwrite KEEP = new MaterialEfficiencyOverwrite("Don't change");
		public static final MaterialEfficiencyOverwrite MAX = new MaterialEfficiencyOverwrite(10);
		public static final MaterialEfficiencyOverwrite[] EDIT = {
			KEEP,
			new MaterialEfficiencyOverwrite(0),
			new MaterialEfficiencyOverwrite(1),
			new MaterialEfficiencyOverwrite(2),
			new MaterialEfficiencyOverwrite(3),
			new MaterialEfficiencyOverwrite(4),
			new MaterialEfficiencyOverwrite(5),
			new MaterialEfficiencyOverwrite(6),
			new MaterialEfficiencyOverwrite(7),
			new MaterialEfficiencyOverwrite(8),
			new MaterialEfficiencyOverwrite(9),
			MAX
		};
		public static final MaterialEfficiencyOverwrite[] ADD = {
			new MaterialEfficiencyOverwrite(0),
			new MaterialEfficiencyOverwrite(1),
			new MaterialEfficiencyOverwrite(2),
			new MaterialEfficiencyOverwrite(3),
			new MaterialEfficiencyOverwrite(4),
			new MaterialEfficiencyOverwrite(5),
			new MaterialEfficiencyOverwrite(6),
			new MaterialEfficiencyOverwrite(7),
			new MaterialEfficiencyOverwrite(8),
			new MaterialEfficiencyOverwrite(9),
			MAX
		};
		final Integer me;
		final String value;

		public MaterialEfficiencyOverwrite(int me) {
			this.me = me;
			this.value = String.valueOf(me);
		}

		public MaterialEfficiencyOverwrite(String value) {
			this.me = null;
			this.value = value;
		}

		public Integer getME() {
			return me;
		}

		@Override
		public String toString() {
			return value;
		}

		
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
				if (lastBlueprintAddType != currentBlueprintAddType) {
					if (lastBlueprintAddType == null) {
						for (JComponent jComponent : manufacturingComponents) {
							jComponent.setVisible(false);
						}
						for (JComponent jComponent : manufacturingEditComponents) {
							jComponent.setVisible(false);
						}
						for (JComponent jComponent : reactionComponents) {
							jComponent.setVisible(false);
						}
						for (JComponent jComponent : reactionEditComponents) {
							jComponent.setVisible(false);
						}
					} else {
						switch (lastBlueprintAddType) {
							case MANUFACTURING_MATERIALS_ONCE:
							case MANUFACTURING_MATERIALS_EDITABLE:
								for (JComponent jComponent : manufacturingComponents) {
									jComponent.setVisible(false);
								}
								for (JComponent jComponent : manufacturingEditComponents) {
									jComponent.setVisible(false);
								}
								break;
							case REACTION_MATERIALS_ONCE:
							case REACTION_MATERIALS_EDITABLE:
								for (JComponent jComponent : reactionComponents) {
									jComponent.setVisible(false);
								}
								for (JComponent jComponent : reactionEditComponents) {
									jComponent.setVisible(false);
								}
								break;
						}
					}
					if (jBlueprintType.isEnabled()) {
						StockpileItem exitingItem = getExistingItem();
						switch (currentBlueprintAddType) {
							case MANUFACTURING_MATERIALS_ONCE:
							case MANUFACTURING_MATERIALS_EDITABLE:
								for (JComponent jComponent : manufacturingComponents) {
									jComponent.setVisible(true);
								}
								for (JComponent jComponent : manufacturingEditComponents) {
									jComponent.setVisible(currentBlueprintAddType == BlueprintAddType.MANUFACTURING_MATERIALS_EDITABLE);
								}
								jMaterialEfficiency.setSelectedIndex(0);
								jFacility.setSelectedIndex(0);
								jRigs.setSelectedIndex(0);
								Integer[] recursiveLevelBlueprints = getRecursiveLevelBlueprints();
								Boolean recursiveBlueprint = null;
								if (exitingItem instanceof StockpileItemMaterial ) {
									StockpileItemMaterial materialItem = (StockpileItemMaterial) exitingItem;
									if (materialItem.isSubMaterial()) {
										recursiveBlueprint = !materialItem.getMaterials().isEmpty(); //Edit Sub Item: Have sub blueprints
										//Can't edit recursive level on subs
										jRecursiveLevelLabel.setVisible(false);
										jRecursiveLevel.setVisible(false);
									}
								}
								if (recursiveBlueprint == null) {
									recursiveBlueprint = recursiveLevelBlueprints.length > 1; //Add/Edit: Have sub blueprints
									jRecursiveLevelLabel.setVisible(recursiveBlueprint);
									jRecursiveLevel.setVisible(recursiveBlueprint);
								}
								jFacilityOverwriteLabel.setVisible(recursiveBlueprint);
								jFacilityOverwrite.setVisible(recursiveBlueprint);
								jRecursiveLevel.setModel(new DefaultComboBoxModel<>(recursiveLevelBlueprints));
								jRecursiveLevel.setSelectedIndex(0);
								jMaterialEfficiencyOverwriteLabel.setVisible(recursiveBlueprint);
								jMaterialEfficiencyOverwrite.setVisible(recursiveBlueprint);
								jMaterialEfficiencyOverwrite.setSelectedIndex(0);
								jMaterialEfficiencyOverwrite.setEnabled(false);
								break;
							case REACTION_MATERIALS_ONCE:
							case REACTION_MATERIALS_EDITABLE:
								for (JComponent jComponent : reactionComponents) {
									jComponent.setVisible(true);
									jComponent.setEnabled(true);
								}
								for (JComponent jComponent : reactionEditComponents) {
									jComponent.setVisible(currentBlueprintAddType == BlueprintAddType.REACTION_MATERIALS_EDITABLE);
								}
								jRigsReactions.setSelectedIndex(0);
								jRigsReactions.setEnabled(true);
								Integer[] recursiveLevelFormulas = getRecursiveLevelFormulas(); //Eveything
								Boolean recursiveFormula = null;
								if (exitingItem instanceof StockpileItemMaterial ) {
									StockpileItemMaterial materialItem = (StockpileItemMaterial) exitingItem;
									if (materialItem.isSubMaterial()) {
										recursiveFormula = !materialItem.getMaterials().isEmpty(); //Edit Sub Item: Have sub blueprints
										//Can't edit recursive level on subs
										jRecursiveLevelLabel.setVisible(false);
										jRecursiveLevel.setVisible(false);
									}
								}
								if (recursiveFormula == null) {
									recursiveFormula = recursiveLevelFormulas.length > 1; //Add/Edit: Have sub blueprints
									jRecursiveLevelLabel.setVisible(recursiveFormula);
									jRecursiveLevel.setVisible(recursiveFormula);
								}
								jFacilityOverwriteLabel.setVisible(recursiveFormula);
								jFacilityOverwrite.setVisible(recursiveFormula);
								jRecursiveLevel.setVisible(recursiveFormula);
								jRecursiveLevel.setModel(new DefaultComboBoxModel<>(recursiveLevelFormulas));
								jRecursiveLevel.setSelectedIndex(0);
								break;
						}
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
