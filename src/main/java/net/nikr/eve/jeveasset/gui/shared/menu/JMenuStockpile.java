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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.IndustryMaterial;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.MenuScroller;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileBpDialog;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileBpDialog.BpData;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTab;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class JMenuStockpile<T> extends JAutoMenu<T> {

	private enum MenuStockpileAction {
		ADD_TO
	}

	private static StockpileBpDialog stockpileBpDialog = null;

	private final JMenuItem jAddToNew;

	ListenerClass listener = new ListenerClass();

	public JMenuStockpile(final Program program) {
		super(GuiShared.get().stockpile(), program);
		this.setIcon(Images.TOOL_STOCKPILE.getIcon());

		MenuScroller menuScroller = new MenuScroller(this);
		menuScroller.keepVisible(2);
		menuScroller.setTopFixedCount(2);
		menuScroller.setInterval(125);

		jAddToNew = new JStockpileMenu(GuiShared.get().newStockpile());
		jAddToNew.setIcon(Images.EDIT_ADD.getIcon());
		jAddToNew.setActionCommand(MenuStockpileAction.ADD_TO.name());
		jAddToNew.addActionListener(listener);
	}

	@Override
	public void updateMenuData() {
		List<Stockpile> stockpiles = StockpileTab.getShownStockpiles(program);
		boolean enabled = !menuData.getTypeIDs().isEmpty();

		removeAll();

		jAddToNew.setEnabled(enabled);
		add(jAddToNew); //Add "To new Stockpile"

		if (!stockpiles.isEmpty()) { //Add Separator (if we have stockpiles)
			addSeparator();
		}

		for (Stockpile stockpile : stockpiles) { //Create menu items
			JMenuItem jMenuItem = new JStockpileMenu(stockpile);
			jMenuItem.setIcon(Images.TOOL_STOCKPILE.getIcon());
			jMenuItem.setActionCommand(MenuStockpileAction.ADD_TO.name());
			jMenuItem.addActionListener(listener);
			jMenuItem.setEnabled(enabled);
			add(jMenuItem);
		}
	}

	private class ListenerClass implements ActionListener {
	@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuStockpileAction.ADD_TO.name().equals(e.getActionCommand())) {
				Object source = e.getSource();
				if (source instanceof JStockpileMenu) {
					JStockpileMenu jStockpileMenu = (JStockpileMenu) source;
					Stockpile stockpile = jStockpileMenu.getStockpile();
					List<StockpileItem> items = toStockpileItems(program, stockpile, menuData.getTypeIDs(), menuData.getCounts(), menuData.getRuns(), true);
					if (items == null) {
						return; //Cancel
					}
					stockpile = program.getStockpileTab().addToStockpile(stockpile, items, true, true);
					if (stockpile != null) {
						program.getMainWindow().addTab(program.getStockpileTab(), Settings.get().isStockpileFocusTab());
						if (Settings.get().isStockpileFocusTab()) {
							program.getStockpileTab().scrollToSctockpile(stockpile); //Updated when other tools gain focus
						} else {
							program.updateTableMenu(); //Needs update (to include new stockpile)
						}
					}
				}
			}
		}
	}

	public static BpOptions selectBpImportOptions(Program program, Collection<Integer> typeIDs, boolean source) {
		BpData blueprintSelect = null;
		BpData formulaSelect = null;
		for (int typeID : typeIDs) {
			Item item = ApiIdConverter.getItem(Math.abs(typeID));
			if (item.isBlueprint() && blueprintSelect == null) {
				blueprintSelect = getBlueprintSelect(program, source);
				if (blueprintSelect == null) {
					return null; //Cancel
				}
			}
			if (item.getItem().isFormula() && formulaSelect == null) {
				formulaSelect = getFormulaSelect(program);
				if (formulaSelect == null) {
					return null; //Cancel
				}
			}
		}
		return new BpOptions(blueprintSelect, formulaSelect);
	}

	public static List<StockpileItem> toStockpileItems(Program program, BpOptions bpOptions, Stockpile stockpile, Collection<Integer> typeIDs, Map<Integer, Double> counts, Map<Integer, Double> runs) {
		return toStockpileItems(program, bpOptions.getBlueprintSelect(), bpOptions.getFormulaSelect(), stockpile, typeIDs, counts, runs, false);
	}

	public static List<StockpileItem> toStockpileItems(Program program, Stockpile stockpile, Collection<Integer> typeIDs, Map<Integer, Double> counts, Map<Integer, Double> runs, boolean source) {
		return toStockpileItems(program, null, null, stockpile, typeIDs, counts, runs, source);
	}

	private static List<StockpileItem> toStockpileItems(Program program, BpData blueprintSelect, BpData formulaSelect, Stockpile stockpile, Collection<Integer> typeIDs, Map<Integer, Double> counts, Map<Integer, Double> runs, boolean source) {
		List<StockpileItem> items = new ArrayList<>();
		for (int typeID : typeIDs) {
			Item item = ApiIdConverter.getItem(Math.abs(typeID));
			if (item.isBlueprint() && blueprintSelect == null) {
				blueprintSelect = getBlueprintSelect(program, source);
				if (blueprintSelect == null) {
					return null; //Cancel
				}
			}
			if (item.getItem().isFormula() && formulaSelect == null) {
				formulaSelect = getFormulaSelect(program);
				if (formulaSelect == null) {
					return null; //Cancel
				}
			}
			if (match(item, blueprintSelect, formulaSelect, TabsStockpile.get().original())) {
				//PBO
				items.add(new StockpileItem(stockpile, item, Math.abs(typeID), total(counts, typeID), false));
			} else if (match(item, blueprintSelect, null, TabsStockpile.get().copy())) {
				//BPC
				items.add(new StockpileItem(stockpile, item, -Math.abs(typeID), total(counts, typeID), false));
			} else if (match(item, blueprintSelect, null, TabsStockpile.get().runs())) {
				//BPC Runs
				items.add(new StockpileItem(stockpile, item, -Math.abs(typeID), runs(counts, runs, typeID), true));
			} else if (match(item, blueprintSelect, null, TabsStockpile.get().materialsManufacturing())) {
				//BP Materials
				for (IndustryMaterial material : item.getManufacturingMaterials()) {
					double count = blueprintSelect.doMath(material.getQuantity(), total(counts, typeID));
					Item materialItem = ApiIdConverter.getItem(material.getTypeID());
					items.add(new StockpileItem(stockpile, materialItem, material.getTypeID(), count, false));
				}
			} else if (match(item, null, formulaSelect, TabsStockpile.get().materialsReaction())) {
				//Reaction Materials
				for (IndustryMaterial material : item.getReactionMaterials()) {
					Item materialItem = ApiIdConverter.getItem(material.getTypeID());
					double count = formulaSelect.doMath(material.getQuantity(), total(counts, typeID));
					items.add(new StockpileItem(stockpile, materialItem, material.getTypeID(), count, false));
				}
			} else { //source or not bluepint/formula
				//PBO or not BP
				Double count = counts.get(Math.abs(typeID));
				if (count != null) {
					items.add(new StockpileItem(stockpile, item, Math.abs(typeID), count, false));
				}
				//BPC
				Double bpc = counts.get(-Math.abs(typeID));
				if (bpc != null) {
					items.add(new StockpileItem(stockpile, item, -Math.abs(typeID), bpc, false));
				}
			}
		}
		return items;
	}

	public static BpData getBlueprintSelect(Program program, boolean source) {
		String[] sources = {TabsStockpile.get().source(), TabsStockpile.get().original(), TabsStockpile.get().copy(), TabsStockpile.get().runs(), TabsStockpile.get().materialsManufacturing()};
		String[] options = {TabsStockpile.get().original(), TabsStockpile.get().copy(), TabsStockpile.get().runs(), TabsStockpile.get().materialsManufacturing()};
		if (stockpileBpDialog == null) {
			stockpileBpDialog = new StockpileBpDialog(program);
		}
		return stockpileBpDialog.show(TabsStockpile.get().addBlueprintTitle(), TabsStockpile.get().addBlueprintMsg(), source ? sources : options);
	}

	public static BpData getFormulaSelect(Program program) {
		String[] options = {TabsStockpile.get().original(), TabsStockpile.get().materialsReaction()};
		if (stockpileBpDialog == null) {
			stockpileBpDialog = new StockpileBpDialog(program);
		}
		return stockpileBpDialog.show(TabsStockpile.get().addFormulaTitle(), TabsStockpile.get().addFormulaMsg(), options);
	}

	public static boolean match(Item item, BpData blueprintSelect, BpData formulaSelect, String match) {
		return (item.isBlueprint() && blueprintSelect != null && blueprintSelect.matches(match))
			|| (item.isFormula() && formulaSelect != null && formulaSelect.matches(match));
	}

	private static double runs(Map<Integer, Double> counts, Map<Integer, Double> runs, int typeID) {
		Double bpc = runs.get(-Math.abs(typeID)); //PBC Runs
		if (bpc == null) { //If no runs data, use count instead
			bpc = runs.getOrDefault(-Math.abs(typeID), 0.0); //BPC count
		} //Else use count
		return bpc + counts.getOrDefault(Math.abs(typeID), 0.0); //BPO
	}

	private static double total(Map<Integer, Double> counts, int typeID) {
		return counts.getOrDefault(Math.abs(typeID), 0.0) //PBOs
			+ counts.getOrDefault(-Math.abs(typeID), 0.0); //BPCs
	}

	public static class JStockpileMenu extends JMenuItem {

		private final Stockpile stockpile;

		public JStockpileMenu(final String text) {
			super(text);
			this.stockpile = null;
		}

		public JStockpileMenu(final Stockpile stockpile) {
			super(stockpile.getName());
			this.stockpile = stockpile;
		}

		public Stockpile getStockpile() {
			return stockpile;
		}
	}

	public static class BpOptions {
		private final BpData blueprintSelect;
		private final BpData formulaSelect;

		public BpOptions(BpData blueprintSelect, BpData formulaSelect) {
			this.blueprintSelect = blueprintSelect;
			this.formulaSelect = formulaSelect;
		}

		public BpData getBlueprintSelect() {
			return blueprintSelect;
		}

		public BpData getFormulaSelect() {
			return formulaSelect;
		}
	}
}
