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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingFacility;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingSecurity;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ReactionRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ReactionSecurity;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileItemDialog.MaterialEfficiencyOverwrite;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class StockpileBpDialog extends JDialogCentered {

	private enum StockpileBpAction {
		CANCEL,
		OK,
		TYPE_CHANGE
	}

	private static final int WIDTH = 320;

	private static StockpileBpDialog stockpileBpDialog = null;

	private final JButton jOK;
	private final JLabel jBlueprintTypeLabel;
	private final JComboBox<String> jBlueprintType;
	private final JComboBox<Integer> jMe;
	private final JComboBox<ManufacturingFacility> jFacility;
	private final JComboBox<ManufacturingRigs> jRigs;
	private final JComboBox<ManufacturingSecurity> jSecurity;
	private final JComboBox<Integer> jBlueprintRecursiveLevel;
	private final JComboBox<MaterialEfficiencyOverwrite> jMaterialEfficiencyOverwrite;
	private final JComboBox<ReactionRigs> jRigsReactions;
	private final JComboBox<ReactionSecurity> jSecurityReactions;
	private final JComboBox<Integer> jFormulaRecursiveLevel;
	private final JCheckBox jIgnoreMultiplier;
	private final JCheckBox jRoundALot;

	private final List<JComponent> manufacturingComponents = new ArrayList<>();
	private final List<JComponent> manufacturingEditComponents = new ArrayList<>();
	private final List<JComponent> reactionComponents = new ArrayList<>();
	private final List<JComponent> reactionEditComponents = new ArrayList<>();
	private BpData returnValue;

	public StockpileBpDialog(Program program) {
		super(program, "", Images.TOOL_STOCKPILE.getImage());

		ListenerClass listener = new ListenerClass();

		jBlueprintTypeLabel = new JLabel();
		jBlueprintType = new JComboBox<>();
		jBlueprintType.setActionCommand(StockpileBpAction.TYPE_CHANGE.name());
		jBlueprintType.addActionListener(listener);

	//ME
		JLabel jMeLabel = new JLabel(TabsStockpile.get().me());
		manufacturingComponents.add(jMeLabel);
		Integer[] me = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		jMe = new JComboBox<>(me);
		jMe.setPrototypeDisplayValue(10);
		jMe.setMaximumRowCount(me.length);
		manufacturingComponents.add(jMe);

	//Facility
		JLabel jFacilityLabel = new JLabel(TabsStockpile.get().blueprintFacility());
		manufacturingComponents.add(jFacilityLabel);
		jFacility = new JComboBox<>(ManufacturingFacility.values());
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
		jSecurity = new JComboBox<>(ManufacturingSecurity.values());
		manufacturingComponents.add(jSecurity);

	//Blueprint Recursive Level
		JLabel jBlueprintRecursiveLevelLabel = new JLabel(TabsStockpile.get().materialsRecursiveLevel());
		manufacturingEditComponents.add(jBlueprintRecursiveLevelLabel);
		jBlueprintRecursiveLevel = new JComboBox<>();
		manufacturingEditComponents.add(jBlueprintRecursiveLevel);

	//Material ME
		JLabel MaterialEfficiencyOverwriteLabel = new JLabel(TabsStockpile.get().materialsRecursiveMe());
		manufacturingEditComponents.add(MaterialEfficiencyOverwriteLabel);
		jMaterialEfficiencyOverwrite = new JComboBox<>(MaterialEfficiencyOverwrite.ADD);
		jMaterialEfficiencyOverwrite.setPrototypeDisplayValue(MaterialEfficiencyOverwrite.MAX);
		jMaterialEfficiencyOverwrite.setMaximumRowCount(MaterialEfficiencyOverwrite.ADD.length);
		manufacturingEditComponents.add(jMaterialEfficiencyOverwrite);

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

	//Formula Recursive Level
		JLabel jFormulaRecursiveLevelLabel = new JLabel(TabsStockpile.get().materialsRecursiveLevel());
		reactionEditComponents.add(jFormulaRecursiveLevelLabel);
		jFormulaRecursiveLevel = new JComboBox<>();
		reactionEditComponents.add(jFormulaRecursiveLevel);

	//Ignore Multiplier
		JLabel jIgnoreMultiplierLabel = new JLabel(TabsStockpile.get().multiplier());
		jIgnoreMultiplier = new JCheckBox(TabsStockpile.get().multiplierIgnore());

	//Ignore Multiplier
		JLabel jRoundALotLabel = new JLabel(TabsStockpile.get().roundALot());
		jRoundALot = new JCheckBox(TabsStockpile.get().roundALot());

		jOK = new JButton(TabsStockpile.get().ok());
		jOK.setActionCommand(StockpileBpAction.OK.name());
		jOK.addActionListener(listener);

		JButton jCancel = new JButton(TabsStockpile.get().cancel());
		jCancel.setActionCommand(StockpileBpAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createParallelGroup()
					.addComponent(jBlueprintTypeLabel, WIDTH, WIDTH, WIDTH)
					.addComponent(jBlueprintType, WIDTH, WIDTH, WIDTH)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
							.addComponent(jMeLabel)
							.addComponent(jFacilityLabel)
							.addComponent(jRigsLabel)
							.addComponent(jSecurityLabel)
							.addComponent(jBlueprintRecursiveLevelLabel)
							.addComponent(MaterialEfficiencyOverwriteLabel)
							.addComponent(jRigsReactionsLabel)
							.addComponent(jSecurityReactionsLabel)
							.addComponent(jFormulaRecursiveLevelLabel)
							.addComponent(jIgnoreMultiplierLabel)
							.addComponent(jRoundALotLabel)
						)
						.addGroup(layout.createParallelGroup()
							.addComponent(jMe)
							.addComponent(jFacility)
							.addComponent(jRigs)
							.addComponent(jSecurity)
							.addComponent(jBlueprintRecursiveLevel)
							.addComponent(jMaterialEfficiencyOverwrite)
							.addComponent(jRigsReactions)
							.addComponent(jSecurityReactions)
							.addComponent(jFormulaRecursiveLevel)
							.addComponent(jIgnoreMultiplier)
							.addComponent(jRoundALot)
						)
					)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jBlueprintTypeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGap(0)
				.addComponent(jBlueprintType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
					.addComponent(jBlueprintRecursiveLevelLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBlueprintRecursiveLevel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(MaterialEfficiencyOverwriteLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMaterialEfficiencyOverwrite, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
					.addComponent(jFormulaRecursiveLevelLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFormulaRecursiveLevel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	public static BpData getBlueprintSelect(Program program, Item item, boolean source) {
		String[] sources = {TabsStockpile.get().source(), TabsStockpile.get().original(), TabsStockpile.get().copy(), TabsStockpile.get().runs(), TabsStockpile.get().materialsManufacturing(), TabsStockpile.get().materialsManufacturingEditable()};
		String[] options = {TabsStockpile.get().original(), TabsStockpile.get().copy(), TabsStockpile.get().runs(), TabsStockpile.get().materialsManufacturing(), TabsStockpile.get().materialsManufacturingEditable()};
		if (stockpileBpDialog == null) {
			stockpileBpDialog = new StockpileBpDialog(program);
		}
		return stockpileBpDialog.show(TabsStockpile.get().addBlueprintTitle(), TabsStockpile.get().addBlueprintMsg(), source ? sources : options, item);
	}

	public static BpData getFormulaSelect(Program program, Item item) {
		String[] options = {TabsStockpile.get().formula(), TabsStockpile.get().materialsReaction(), TabsStockpile.get().materialsReactionEditable()};
		if (stockpileBpDialog == null) {
			stockpileBpDialog = new StockpileBpDialog(program);
		}
		return stockpileBpDialog.show(TabsStockpile.get().addFormulaTitle(), TabsStockpile.get().addFormulaMsg(), options, item);
	}

	private BpData show(String title, String msg, String[] options, Item item) {
		returnValue = null;
		jBlueprintTypeLabel.setText(msg);
		getDialog().setTitle(title);
		jBlueprintType.setModel(new DefaultComboBoxModel<>(options));
		jBlueprintType.setSelectedIndex(0);
		jMe.setSelectedIndex(0);
		jFacility.setSelectedIndex(0);
		jRigs.setSelectedIndex(0);
		jMe.setSelectedIndex(0);
		Integer[] recursiveLevelBlueprints = StockpileItemDialog.getRecursiveLevelBlueprints(item);
		jBlueprintRecursiveLevel.setModel(new DefaultComboBoxModel<>(recursiveLevelBlueprints));
		jBlueprintRecursiveLevel.setSelectedIndex(0);
		jBlueprintRecursiveLevel.setEnabled(recursiveLevelBlueprints.length > 1);
		jMaterialEfficiencyOverwrite.setSelectedIndex(0);
		jMaterialEfficiencyOverwrite.setEnabled(recursiveLevelBlueprints.length > 1);
		Integer[] recursiveLevelReactions = StockpileItemDialog.getRecursiveLevelFormulas(item);
		jFormulaRecursiveLevel.setModel(new DefaultComboBoxModel<>(recursiveLevelReactions));
		jFormulaRecursiveLevel.setSelectedIndex(0);
		jFormulaRecursiveLevel.setEnabled(recursiveLevelReactions.length > 1);
		setVisible(true);
		return returnValue;
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jBlueprintType;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		String type = jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex());
		if (type.equals(TabsStockpile.get().materialsManufacturing())
				|| type.equals(TabsStockpile.get().materialsManufacturingEditable())
				|| type.equals(TabsStockpile.get().materialsReaction())
				|| type.equals(TabsStockpile.get().materialsReactionEditable())
				) {
			Integer me = jMe.getItemAt(jMe.getSelectedIndex());
			ManufacturingFacility facility = jFacility.getItemAt(jFacility.getSelectedIndex());
			ManufacturingRigs rigs = jRigs.getItemAt(jRigs.getSelectedIndex());
			ReactionRigs rigsReactions = jRigsReactions.getItemAt(jRigsReactions.getSelectedIndex());
			ManufacturingSecurity security = jSecurity.getItemAt(jSecurity.getSelectedIndex());
			ReactionSecurity securityReactions = jSecurityReactions.getItemAt(jSecurityReactions.getSelectedIndex());
			Integer blueprintRecursiveLevel = jBlueprintRecursiveLevel.getItemAt(jBlueprintRecursiveLevel.getSelectedIndex());
			if (blueprintRecursiveLevel == null) {
				blueprintRecursiveLevel = 0;
			}
			Integer formulaRecursiveLevel = null;
			if (jFormulaRecursiveLevel.isVisible()) {
				formulaRecursiveLevel = jFormulaRecursiveLevel.getItemAt(jFormulaRecursiveLevel.getSelectedIndex());
			}
			if (formulaRecursiveLevel == null) {
				formulaRecursiveLevel = 0;
			}
			MaterialEfficiencyOverwrite meOverwrite = jMaterialEfficiencyOverwrite.getItemAt(jMaterialEfficiencyOverwrite.getSelectedIndex());
			boolean ignoreMultiplier = jIgnoreMultiplier.isSelected();
			boolean roundALot = jRoundALot.isSelected();
			returnValue = new BpData(type, ignoreMultiplier, roundALot, blueprintRecursiveLevel, formulaRecursiveLevel, meOverwrite.getME(), me, facility, rigs, security, rigsReactions, securityReactions);
		} else {
			returnValue = new BpData(type);
		}
		setVisible(false);
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (StockpileBpAction.OK.name().equals(e.getActionCommand())) {
				save();
			} else if (StockpileBpAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			} else if (StockpileBpAction.TYPE_CHANGE.name().equals(e.getActionCommand())) {
				String type = jBlueprintType.getItemAt(jBlueprintType.getSelectedIndex());
				boolean manufacturing = type.equals(TabsStockpile.get().materialsManufacturing());
				boolean manufacturingEdit =  type.equals(TabsStockpile.get().materialsManufacturingEditable());
				boolean reaction = type.equals(TabsStockpile.get().materialsReaction());
				boolean reactionEdit = type.equals(TabsStockpile.get().materialsReactionEditable());
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
				if (manufacturing || manufacturingEdit) {
					for (JComponent jComponent : manufacturingComponents) {
						jComponent.setVisible(true);
					}
					for (JComponent jComponent : manufacturingEditComponents) {
						jComponent.setVisible(manufacturingEdit);
					}
				} else if (reaction || reactionEdit){
					jRigsReactions.setEnabled(true);
					for (JComponent jComponent : reactionComponents) {
						jComponent.setVisible(true);
					}
					for (JComponent jComponent : reactionEditComponents) {
						jComponent.setVisible(reactionEdit);
					}
				}
				getDialog().pack();
			}
		}
	}

	public static class BpData {

		private final String type;
		private final boolean ignoreMultiplier;
		private final boolean roundALot;
		private final Integer blueprintRecursiveLevel;
		private final Integer formulaRecursiveLevel;
		private final Integer me;
		private final Integer materialEfficiencyOverwrite;
		private final ManufacturingFacility facility;
		private final ManufacturingRigs rigs;
		private final ManufacturingSecurity security;
		private final ReactionRigs rigsReactions;
		private final ReactionSecurity securityReactions;
		
		
		

		public BpData(String type) {
			this.type = type;
			this.ignoreMultiplier = false;
			this.roundALot = false;
			this.blueprintRecursiveLevel = null;
			this.formulaRecursiveLevel = null;
			this.materialEfficiencyOverwrite = null;
			this.me = null;
			this.facility = null;
			this.rigs = null;
			this.security = null;
			this.rigsReactions = null;
			this.securityReactions = null;
		}

		public BpData(String type, boolean ignoreMultiplier, boolean roundALot, Integer blueprintRecursiveLevel, int formulaRecursiveLevel, Integer materialEfficiencyOverwrite, Integer me, ManufacturingFacility facility, ManufacturingRigs rigs, ManufacturingSecurity security, ReactionRigs rigsReactions, ReactionSecurity securityReactions) {
			this.type = type;
			this.me = me;
			this.facility = facility;
			this.rigs = rigs;
			this.security = security;
			this.materialEfficiencyOverwrite = materialEfficiencyOverwrite;
			this.blueprintRecursiveLevel = blueprintRecursiveLevel;
			this.formulaRecursiveLevel = formulaRecursiveLevel;
			this.roundALot = roundALot;
			this.ignoreMultiplier = ignoreMultiplier;
			this.rigsReactions = rigsReactions;
			this.securityReactions = securityReactions;
		}

		public Integer getMe() {
			return me;
		}

		public ManufacturingFacility getFacility() {
			return facility;
		}

		public ManufacturingRigs getRigs() {
			return rigs;
		}

		public ManufacturingSecurity getSecurity() {
			return security;
		}

		public Integer getMaterialEfficiencyOverwrite() {
			return materialEfficiencyOverwrite;
		}

		public Integer getBlueprintRecursiveLevel() {
			return blueprintRecursiveLevel;
		}

		public Integer getFormulaRecursiveLevel() {
			return formulaRecursiveLevel;
		}

		public ReactionRigs getRigsReactions() {
			return rigsReactions;
		}

		public ReactionSecurity getSecurityReactions() {
			return securityReactions;
		}

		public boolean isIgnoreMultiplier() {
			return ignoreMultiplier;
		}

		public boolean isRoundALot() {
			return roundALot;
		}

		public boolean matches(String value) {
			return type.equals(value);
		}

		public double doMath(int quantity, double countMinimum) {
			return ApiIdConverter.getManufacturingQuantity(quantity, me, facility, rigs, security, countMinimum, false);
		}
	}
}
