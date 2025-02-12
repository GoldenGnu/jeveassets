/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import ca.odell.glazedlists.SeparatorList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.IndustryMaterial;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.MenuScroller;
import static net.nikr.eve.jeveasset.gui.shared.menu.JMenuStockpile.getBlueprintSelect;
import static net.nikr.eve.jeveasset.gui.shared.menu.JMenuStockpile.getFormulaSelect;
import static net.nikr.eve.jeveasset.gui.shared.menu.JMenuStockpile.match;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileBpDialog.BpData;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class JStockpileItemMenu extends JMenu {

	private enum StockpileItemMenuAction {
		EDIT_ITEM,
		ADD_TO,
		DELETE_ITEM,
		ORIGINAL,
		COPY,
		RUNS
	}

	private Program program;

	public JStockpileItemMenu(final Program program, final List<StockpileItem> edit, final List<StockpileItem> delete, final List<StockpileItem> items) {
		super(TabsStockpile.get().stockpile());
		this.program = program;
		this.setIcon(Images.TOOL_STOCKPILE.getIcon());

		ListenerClass listener = new ListenerClass();

		JMenuItem jMenuItem;
		JMenu jMenu;

		JMenu jAddToo = new JMenu(TabsStockpile.get().addToStockpile());
		jAddToo.setIcon(Images.EDIT_ADD.getIcon());
		jAddToo.setEnabled(!items.isEmpty());
		this.add(jAddToo);

		MenuScroller menuScroller = new MenuScroller(jAddToo);
		menuScroller.keepVisible(2);
		menuScroller.setTopFixedCount(2);
		menuScroller.setInterval(125);

		if (!items.isEmpty()) {
			jMenuItem = new JStockpileMenuItem(TabsStockpile.get().addToNewStockpile(), Images.EDIT_ADD.getIcon(), items);
			jMenuItem.setActionCommand(StockpileItemMenuAction.ADD_TO.name());
			jMenuItem.addActionListener(listener);
			jAddToo.add(jMenuItem);

			jAddToo.addSeparator();

			for (Stockpile stockpile : StockpileTab.getShownStockpiles(program)) {
				jMenuItem = new JStockpileMenuItem(stockpile, Images.TOOL_STOCKPILE.getIcon(), items);
				jMenuItem.setActionCommand(StockpileItemMenuAction.ADD_TO.name());
				jMenuItem.addActionListener(listener);
				jAddToo.add(jMenuItem);
			}
		}

		jMenuItem = new JStockpileMenuItem(TabsStockpile.get().editItem(), Images.EDIT_EDIT.getIcon(), edit);
		jMenuItem.setActionCommand(StockpileItemMenuAction.EDIT_ITEM.name());
		jMenuItem.addActionListener(listener);
		jMenuItem.setEnabled(edit.size() == 1);
		this.add(jMenuItem);

		jMenuItem = new JStockpileMenuItem(TabsStockpile.get().deleteItem(), Images.EDIT_DELETE.getIcon(), delete);
		jMenuItem.setActionCommand(StockpileItemMenuAction.DELETE_ITEM.name());
		jMenuItem.addActionListener(listener);
		jMenuItem.setEnabled(!delete.isEmpty());
		this.add(jMenuItem);

		boolean blueprint = false;
		for (Object object : items) {
			if (object instanceof SeparatorList.Separator || object instanceof StockpileTotal || (!(object instanceof StockpileItem))) {
				continue;
			}
			StockpileItem item = (StockpileItem) object;
			if (item.isBlueprint()) {
				blueprint = true;
				break;
			}
		}

		this.addSeparator();

		jMenu = new JMenu(TabsStockpile.get().blueprints());
		jMenu.setIcon(Images.MISC_BLUEPRINT.getIcon());
		this.add(jMenu);

		jMenuItem = new JStockpileMenuItem(TabsStockpile.get().original(), Images.MISC_BPO.getIcon(), items);
		jMenuItem.setActionCommand(StockpileItemMenuAction.ORIGINAL.name());
		jMenuItem.addActionListener(listener);
		jMenuItem.setEnabled(blueprint);
		jMenu.add(jMenuItem);

		jMenuItem = new JStockpileMenuItem(TabsStockpile.get().copy(), Images.MISC_BPC.getIcon(), items);
		jMenuItem.setActionCommand(StockpileItemMenuAction.COPY.name());
		jMenuItem.addActionListener(listener);
		jMenuItem.setEnabled(blueprint);
		jMenu.add(jMenuItem);

		jMenuItem = new JStockpileMenuItem(TabsStockpile.get().runs(), Images.MISC_RUNS.getIcon(), items);
		jMenuItem.setActionCommand(StockpileItemMenuAction.RUNS.name());
		jMenuItem.addActionListener(listener);
		jMenuItem.setEnabled(blueprint);
		jMenu.add(jMenuItem);
	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (StockpileItemMenuAction.ADD_TO.name().equals(e.getActionCommand())) { //Add item to
				Object source = e.getSource();
				if (source instanceof JStockpileMenuItem) {
					JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
					List<StockpileItem> items = new ArrayList<>();
					BpData blueprintSelect = null;
					BpData formulaSelect = null;
					for (StockpileItem stockpileItem : jMenuItem.getItems()) {
						Stockpile stockpile = stockpileItem.getStockpile();
						Item item = stockpileItem.getItem();
						if (stockpileItem.isBlueprint() && blueprintSelect == null) {
							blueprintSelect = getBlueprintSelect(program, true);
							if (blueprintSelect == null) {
								return; //Cancel
							}
						}
						if (item.isFormula() && formulaSelect == null) {
							formulaSelect = getFormulaSelect(program);
							if (formulaSelect == null) {
								return; //Cancel
							}
						}
						if (match(item, blueprintSelect, formulaSelect, TabsStockpile.get().original())) {
							//PBO
							items.add(new StockpileItem(stockpile, item, item.getTypeID(), stockpileItem.getCountMinimum(), false));
						} else if (match(item, blueprintSelect, null, TabsStockpile.get().copy())) {
							//BPC
							items.add(new StockpileItem(stockpile, item, -item.getTypeID(), stockpileItem.getCountMinimum(), false));
						} else if (match(item, blueprintSelect, null, TabsStockpile.get().runs())) {
							//BPC Runs
							items.add(new StockpileItem(stockpile, item, -item.getTypeID(), stockpileItem.getCountMinimum(), true));
						} else if (match(item, blueprintSelect, null, TabsStockpile.get().materialsManufacturing())) {
							//BP Materials
							for (IndustryMaterial material : item.getManufacturingMaterials()) {
								double count = blueprintSelect.doMath(material.getQuantity(), stockpileItem.getCountMinimum());
								Item materialItem = ApiIdConverter.getItem(material.getTypeID());
								items.add(new StockpileItem(stockpile, materialItem, material.getTypeID(), count, false));
							}
						} else if (match(item, null, formulaSelect, TabsStockpile.get().materialsReaction())) {
							//Reaction Materials
							for (IndustryMaterial material : item.getReactionMaterials()) {
								Item materialItem = ApiIdConverter.getItem(material.getTypeID());
								double count = formulaSelect.doMath(material.getQuantity(), stockpileItem.getCountMinimum());
								items.add(new StockpileItem(stockpile, materialItem, material.getTypeID(), count, false));
							}
						} else { //source or not bluepint/formula
							items.add(stockpileItem);
						}
					}
					program.getStockpileTab().addToStockpile(jMenuItem.getStockpile(), items, true, true);
				}
			} else if (StockpileItemMenuAction.RUNS.name().equals(e.getActionCommand())) { //Runs
				Object source = e.getSource();
				if (source instanceof JStockpileMenuItem) {
					updateBlueprint((JStockpileMenuItem) source, new ChangeBlueprintType() {
						@Override
						public StockpileItem getUpdatedItem(StockpileItem item) {
							//Runs: -typeID & runs=true
							return new StockpileItem(item.getStockpile(), item.getItem(), -item.getTypeID(), item.getCountMinimum(), true);
						}
						@Override
						public boolean include(StockpileItem item) {
							return !item.isRuns();
						}
					});
				}
			} else if (StockpileItemMenuAction.ORIGINAL.name().equals(e.getActionCommand())) { //Original
				Object source = e.getSource();
				if (source instanceof JStockpileMenuItem) {
					updateBlueprint((JStockpileMenuItem) source, new ChangeBlueprintType() {
						@Override
						public StockpileItem getUpdatedItem(StockpileItem item) {
							//BPO: +typeID & runs=false
							return new StockpileItem(item.getStockpile(), item.getItem(), item.getTypeID(), item.getCountMinimum(), false);
						}
						@Override
						public boolean include(StockpileItem item) {
							return !item.isBPO();
						}
					});
				}
			} else if (StockpileItemMenuAction.COPY.name().equals(e.getActionCommand())) { //Copy
				Object source = e.getSource();
				if (source instanceof JStockpileMenuItem) {
					updateBlueprint((JStockpileMenuItem) source, new ChangeBlueprintType() {
						@Override
						public StockpileItem getUpdatedItem(StockpileItem item) {
							//BPC: -typeID & runs=false
							return new StockpileItem(item.getStockpile(), item.getItem(), -item.getTypeID(), item.getCountMinimum(), false);
						}
						@Override
						public boolean include(StockpileItem item) {
							return item.isBPO() || item.isRuns();
						}
					});
				}
			} else if (StockpileItemMenuAction.EDIT_ITEM.name().equals(e.getActionCommand())) { //Edit item
				Object source = e.getSource();
				if (source instanceof JStockpileMenuItem) {
					JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
					List<StockpileItem> items = jMenuItem.getItems();
					if (items.size() == 1) {
						program.getStockpileTab().editItem(items.get(0));
					}
				}
			} else if (StockpileItemMenuAction.DELETE_ITEM.name().equals(e.getActionCommand())) { //Delete item
				Object source = e.getSource();
				if (source instanceof JStockpileMenuItem) {
					JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
					List<StockpileItem> items = jMenuItem.getItems();
					if (!items.isEmpty()) {
						int value;
						if (items.size() == 1) {
							value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), items.get(0).getName(), TabsStockpile.get().deleteItemTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
						} else {
							value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsStockpile.get().deleteItems(items.size()), TabsStockpile.get().deleteItemTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
						}
						if (value == JOptionPane.OK_OPTION) {
							Settings.lock("Stokcpile (Stockpile Menu)"); //Lock for Stokcpile (Stockpile Menu)
							for (StockpileItem item : items) {
								item.getStockpile().remove(item);
							}
							Settings.unlock("Stokcpile (Stockpile Menu)"); //Unlock for Stokcpile (Stockpile Menu)
							program.saveSettings("Stokcpile (Stockpile Menu)"); //Save Stokcpile (Stockpile Menu)
							program.getStockpileTab().removeItems(items);
						}
					}
				}
			}
		}
	}

	private void updateBlueprint(JStockpileMenuItem jMenuItem, ChangeBlueprintType blueprintTypeChange) {
		//Find items that will be changed
		Map<Stockpile, List<StockpileItem>> update = new HashMap<>();
		for (StockpileItem stockpileItem : jMenuItem.getItems()) {
			if (stockpileItem.isBlueprint() && blueprintTypeChange.include(stockpileItem)) {
				List<StockpileItem> list = update.get(stockpileItem.getStockpile());
				if (list == null) {
					list = new ArrayList<>();
					update.put(stockpileItem.getStockpile(), list);
				}
				list.add(stockpileItem);
			}
		}
		Settings.lock("Stokcpile (Stockpile Menu)"); //Lock for Stokcpile (Stockpile Menu)
		//Remove items that will be changed
		for (Map.Entry<Stockpile, List<StockpileItem>> entry : update.entrySet()) {
			for (StockpileItem item : entry.getValue()) {
				entry.getKey().remove(item);
			}
			program.getStockpileTab().removeItems(entry.getValue());
		}
		//Change items
		for (Map.Entry<Stockpile, List<StockpileItem>> entry : update.entrySet()) {
			for (StockpileItem item : entry.getValue()) {
				item.update(blueprintTypeChange.getUpdatedItem(item));
			}
		}
		Settings.unlock("Stokcpile (Stockpile Menu)"); //Unlock for Stokcpile (Stockpile Menu)
		//Add changed items
		for (Map.Entry<Stockpile, List<StockpileItem>> entry : update.entrySet()) {
			program.getStockpileTab().addToStockpile(entry.getKey(), entry.getValue(), true, false);
		}
		program.saveSettings("Stockpile (Stockpile Menu)"); //Save Stockpile (Stockpile Menu)
	}

	private interface ChangeBlueprintType {
		public StockpileItem getUpdatedItem(StockpileItem item);
		public boolean include(StockpileItem item);
	}

	public static class JStockpileMenuItem extends JMenuItem {

		private final List<StockpileItem> items = new ArrayList<>();
		private final Stockpile stockpile;

		public JStockpileMenuItem(final Stockpile stockpile, final Icon icon, final List<StockpileItem> items) {
			this(stockpile.getName(), icon, items, stockpile);
		}

		public JStockpileMenuItem(final String title, final Icon icon, final List<StockpileItem> items) {
			this(title, icon, items, null);
		}

		private JStockpileMenuItem(final String title, final Icon icon, final List<StockpileItem> items, final Stockpile stockpile) {
			super(title, icon);
			this.stockpile = stockpile;
			for (int i = 0; i < items.size(); i++) { //Remove StockpileTotal and SeparatorList.Separator
				Object item = items.get(i);
				if (!(item instanceof SeparatorList.Separator) && !(item instanceof StockpileTotal)) {
					this.items.add((StockpileItem) item);
				}
			}
		}

		public List<StockpileItem> getItems() {
			return items;
		}

		public Stockpile getStockpile() {
			return stockpile;
		}
	}

}
