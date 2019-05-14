/*
 * Copyright 2009-2019 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JOptionInput;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


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

	public JStockpileItemMenu(final Program program, final List<StockpileItem> items) {
		super(TabsStockpile.get().stockpile());
		this.program = program;
		this.setIcon(Images.TOOL_STOCKPILE.getIcon());

		ListenerClass listener = new ListenerClass();

		JMenuItem jMenuItem;
		JMenu jMenu;

		JMenu jSubMenu = new JMenu(TabsStockpile.get().addToStockpile());
		jSubMenu.setIcon(Images.EDIT_ADD.getIcon());
		jSubMenu.setEnabled(!items.isEmpty());
		this.add(jSubMenu);
		if (!items.isEmpty()) {
			jMenuItem = new JStockpileMenuItem(TabsStockpile.get().addToNewStockpile(), Images.EDIT_ADD.getIcon(), items);
			jMenuItem.setActionCommand(StockpileItemMenuAction.ADD_TO.name());
			jMenuItem.addActionListener(listener);
			jSubMenu.add(jMenuItem);

			jSubMenu.addSeparator();

			for (Stockpile stockpile : StockpileTab.getShownStockpiles(program)) {
				jMenuItem = new JStockpileMenuItem(stockpile, Images.TOOL_STOCKPILE.getIcon(), items);
				jMenuItem.setActionCommand(StockpileItemMenuAction.ADD_TO.name());
				jMenuItem.addActionListener(listener);
				jSubMenu.add(jMenuItem);
			}
		}

		jMenuItem = new JStockpileMenuItem(TabsStockpile.get().editItem(), Images.EDIT_EDIT.getIcon(), items);
		jMenuItem.setActionCommand(StockpileItemMenuAction.EDIT_ITEM.name());
		jMenuItem.addActionListener(listener);
		jMenuItem.setEnabled(items.size() == 1);
		this.add(jMenuItem);

		jMenuItem = new JStockpileMenuItem(TabsStockpile.get().deleteItem(), Images.EDIT_DELETE.getIcon(), items);
		jMenuItem.setActionCommand(StockpileItemMenuAction.DELETE_ITEM.name());
		jMenuItem.addActionListener(listener);
		jMenuItem.setEnabled(!items.isEmpty());
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
					boolean blueprint = false;
					for (StockpileItem item : jMenuItem.getItems()) {
						if (item.isBlueprint()) {
							blueprint = true;
							break;
						}
					}
					List<StockpileItem> items = new ArrayList<>();
					if (blueprint) {
						String[] options = {TabsStockpile.get().source(), TabsStockpile.get().original(), TabsStockpile.get().copy(), TabsStockpile.get().runs()};
						Object object = JOptionInput.showInputDialog(program.getMainWindow().getFrame(), TabsStockpile.get().addBlueprintMsg(), TabsStockpile.get().addBlueprintTitle(), JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
						if (object == null) {
							return;
						}
						if (object.equals(TabsStockpile.get().original())) {
							for (StockpileItem item : jMenuItem.getItems()) {
								if (item.isBlueprint()) {
									items.add(new StockpileItem(item.getStockpile(), item.getItem(), Math.abs(item.getTypeID()), item.getCountMinimum(), false));
								} else {
									items.add(item);
								}
							}
							
						} else if (object.equals(TabsStockpile.get().copy())) {
							for (StockpileItem item : jMenuItem.getItems()) {
								if (item.isBlueprint()) {
									items.add(new StockpileItem(item.getStockpile(), item.getItem(), -Math.abs(item.getTypeID()), item.getCountMinimum(), false));
								} else {
									items.add(item);
								}
							}
						} else if (object.equals(TabsStockpile.get().runs())) {
							for (StockpileItem item : jMenuItem.getItems()) {
								if (item.isBlueprint()) {
									items.add(new StockpileItem(item.getStockpile(), item.getItem(), -Math.abs(item.getTypeID()), item.getCountMinimum(), true));
								} else {
									items.add(item);
								}
							}
						} else { //source
							items.addAll(jMenuItem.getItems());
						}
						program.getStockpileTool().addToStockpile(jMenuItem.getStockpile(), items, true);
					} else { //No bluepints
						program.getStockpileTool().addToStockpile(jMenuItem.getStockpile(), jMenuItem.getItems(), true);
					}
				}
			} else if (StockpileItemMenuAction.RUNS.name().equals(e.getActionCommand())) { //Runs
				Object source = e.getSource();
				if (source instanceof JStockpileMenuItem) {
					JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
					//Find items that will be changed
					Map<Stockpile, List<StockpileItem>> update = new HashMap<>();
					for (StockpileItem stockpileItem : jMenuItem.getItems()) {
						if (stockpileItem.isBlueprint() && !stockpileItem.isRuns()) {
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
						program.getStockpileTool().removeItems(entry.getValue());
					}
					//Change items
					for (Map.Entry<Stockpile, List<StockpileItem>> entry : update.entrySet()) {
						for (StockpileItem item : entry.getValue()) {
							item.update(new StockpileItem(item.getStockpile(), item.getItem(), -Math.abs(item.getTypeID()), item.getCountMinimum(), true));
						}
					}
					Settings.unlock("Stokcpile (Stockpile Menu)"); //Lock for Stokcpile (Stockpile Menu)
					//Add changed items
					for (Map.Entry<Stockpile, List<StockpileItem>> entry : update.entrySet()) {
						program.getStockpileTool().addToStockpile(entry.getKey(), entry.getValue(), true);
					}
				}
			} else if (StockpileItemMenuAction.ORIGINAL.name().equals(e.getActionCommand())) { //Original
				Object source = e.getSource();
				if (source instanceof JStockpileMenuItem) {
					JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
					//Find items that will be changed
					Map<Stockpile, List<StockpileItem>> update = new HashMap<>();
					for (StockpileItem stockpileItem : jMenuItem.getItems()) {
						if (stockpileItem.isBlueprint() && !stockpileItem.isBPO()) {
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
						program.getStockpileTool().removeItems(entry.getValue());
					}
					//Change items
					for (Map.Entry<Stockpile, List<StockpileItem>> entry : update.entrySet()) {
						for (StockpileItem item : entry.getValue()) {
							item.update(new StockpileItem(item.getStockpile(), item.getItem(), Math.abs(item.getTypeID()), item.getCountMinimum(), false));
						}
					}
					Settings.unlock("Stokcpile (Stockpile Menu)"); //Lock for Stokcpile (Stockpile Menu)
					//Add changed items
					for (Map.Entry<Stockpile, List<StockpileItem>> entry : update.entrySet()) {
						program.getStockpileTool().addToStockpile(entry.getKey(), entry.getValue(), true);
					}
				}
			} else if (StockpileItemMenuAction.COPY.name().equals(e.getActionCommand())) { //Copy
				Object source = e.getSource();
				if (source instanceof JStockpileMenuItem) {
					JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
					//Find items that will be changed
					Map<Stockpile, List<StockpileItem>> update = new HashMap<>();
					for (StockpileItem stockpileItem : jMenuItem.getItems()) {
						if (stockpileItem.isBlueprint() && (stockpileItem.isBPO() || stockpileItem.isRuns())) {
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
						program.getStockpileTool().removeItems(entry.getValue());
					}
					//Change items
					for (Map.Entry<Stockpile, List<StockpileItem>> entry : update.entrySet()) {
						for (StockpileItem item : entry.getValue()) {
							item.update(new StockpileItem(item.getStockpile(), item.getItem(), -Math.abs(item.getTypeID()), item.getCountMinimum(), false));
						}
					}
					Settings.unlock("Stokcpile (Stockpile Menu)"); //Lock for Stokcpile (Stockpile Menu)
					//Add changed items
					for (Map.Entry<Stockpile, List<StockpileItem>> entry : update.entrySet()) {
						program.getStockpileTool().addToStockpile(entry.getKey(), entry.getValue(), true);
					}
				}
			} else if (StockpileItemMenuAction.EDIT_ITEM.name().equals(e.getActionCommand())) { //Edit item
				Object source = e.getSource();
				if (source instanceof JStockpileMenuItem) {
					JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
					List<Stockpile.StockpileItem> items = jMenuItem.getItems();
					if (items.size() == 1) {
						program.getStockpileTool().editItem(items.get(0));
					}
				}
			} else if (StockpileItemMenuAction.DELETE_ITEM.name().equals(e.getActionCommand())) { //Delete item
				Object source = e.getSource();
				if (source instanceof JStockpileMenuItem) {
					JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
					List<Stockpile.StockpileItem> items = jMenuItem.getItems();
					if (!items.isEmpty()) {
						int value;
						if (items.size() == 1) {
							value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), items.get(0).getName(), TabsStockpile.get().deleteItemTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
						} else {
							value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsStockpile.get().deleteItems(items.size()), TabsStockpile.get().deleteItemTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
						}
						if (value == JOptionPane.OK_OPTION) {
							Settings.lock("Stokcpile (Stockpile Menu)"); //Lock for Stokcpile (Stockpile Menu)
							for (Stockpile.StockpileItem item : items) {
								item.getStockpile().remove(item);
							}
							Settings.unlock("Stokcpile (Stockpile Menu)"); //Unlock for Stokcpile (Stockpile Menu)
							program.saveSettings("Stokcpile (Stockpile Menu)"); //Save Stokcpile (Stockpile Menu)
							program.getStockpileTool().removeItems(items);
						}
					}
				}
			}
		}
	}

	public static class JStockpileMenuItem extends JMenuItem {

		private final List<StockpileItem> items = new ArrayList<StockpileItem>();
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
