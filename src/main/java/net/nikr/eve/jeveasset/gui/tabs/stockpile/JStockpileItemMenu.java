/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class JStockpileItemMenu extends JMenu {

	private enum StockpileItemMenuAction {
		EDIT_ITEM,
		ADD_TO, 
		DELETE_ITEM
	}

	//private StockpileTab stockpileTab;
	private Program program;

	public JStockpileItemMenu(final Program program, final List<StockpileItem> items) {
		super(TabsStockpile.get().stockpile());
		this.program = program;
		this.setIcon(Images.TOOL_STOCKPILE.getIcon());

		ListenerClass listener = new ListenerClass();
		
		JMenuItem jMenuItem;

		JMenu jSubMenu = new JMenu(TabsStockpile.get().addToStockpile());
		jSubMenu.setEnabled(!items.isEmpty());
		this.add(jSubMenu);
		if (!items.isEmpty()) {
			jMenuItem = new JStockpileMenuItem(TabsStockpile.get().addToNewStockpile(), Images.EDIT_ADD.getIcon(), items);
			jMenuItem.setActionCommand(StockpileItemMenuAction.ADD_TO.name());
			jMenuItem.addActionListener(listener);
			jSubMenu.add(jMenuItem);

			jSubMenu.addSeparator();
			List<Stockpile> stockpiles = Settings.get().getStockpiles();
			Collections.sort(stockpiles);

			for (Stockpile stockpile : stockpiles) {
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
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//Add item to
			if (StockpileItemMenuAction.ADD_TO.name().equals(e.getActionCommand())) {
				Object source = e.getSource();
				if (source instanceof JStockpileMenuItem) {
					JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
					program.getStockpileTool().addToStockpile(jMenuItem.getStockpile(), jMenuItem.getItems(), true);
				}
			}
			//Edit item
			if (StockpileItemMenuAction.EDIT_ITEM.name().equals(e.getActionCommand())) {
				Object source = e.getSource();
				if (source instanceof JStockpileMenuItem) {
					JStockpileMenuItem jMenuItem = (JStockpileMenuItem) source;
					List<Stockpile.StockpileItem> items = jMenuItem.getItems();
					if (items.size() == 1) {
						program.getStockpileTool().editItem(items.get(0));
					}
				}
			}
			//Delete item
			if (StockpileItemMenuAction.DELETE_ITEM.name().equals(e.getActionCommand())) {
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
							for (Stockpile.StockpileItem item : items) {
								item.getStockpile().remove(item);
							}
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
					this.items.add((StockpileItem)item);
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
