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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class JMenuStockpile<T>  extends JMenu implements ActionListener {

	private static final String ACTION_ADD_TO = "ACTION_ADD_TO";
	private static final int DEFAULT_ADD_COUNT = 1;
	private Program program;
	private MenuData<T> menuData;

	public JMenuStockpile(final Program program, final MenuData<T> menuData) {
		super(GuiShared.get().stockpile());
		this.program = program;
		this.menuData = menuData;

		this.setIcon(Images.TOOL_STOCKPILE.getIcon());

		JMenuItem jMenuItem;

		jMenuItem = new JStockpileMenu(GuiShared.get().newStockpile());
		jMenuItem.setIcon(Images.EDIT_ADD.getIcon());
		jMenuItem.setEnabled(!menuData.getTypeIDs().isEmpty());
		jMenuItem.setActionCommand(ACTION_ADD_TO);
		jMenuItem.addActionListener(this);
		add(jMenuItem);

		if (!program.getSettings().getStockpiles().isEmpty()) {
			this.addSeparator();
		}
		List<Stockpile> stockpiles = program.getSettings().getStockpiles();
		Collections.sort(stockpiles);
		for (Stockpile stockpile : stockpiles) {
			jMenuItem = new JStockpileMenu(stockpile);
			jMenuItem.setIcon(Images.TOOL_STOCKPILE.getIcon());
			jMenuItem.setEnabled(!menuData.getTypeIDs().isEmpty());
			jMenuItem.setActionCommand(ACTION_ADD_TO);
			jMenuItem.addActionListener(this);
			add(jMenuItem);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_ADD_TO.equals(e.getActionCommand())) {
			Object source = e.getSource();
			if (source instanceof JStockpileMenu) {
				JStockpileMenu jStockpileMenu = (JStockpileMenu) source;
				Stockpile stockpile = jStockpileMenu.getStockpile();
				List<StockpileItem> items = new ArrayList<StockpileItem>();
				for (int typeID : menuData.getBlueprintTypeIDs()) {
					Item item = ApiIdConverter.getItem(Math.abs(typeID), program.getSettings().getItems());
					StockpileItem stockpileItem = new StockpileItem(stockpile, item, typeID, DEFAULT_ADD_COUNT);
					items.add(stockpileItem);
				}
				stockpile = program.getStockpileTool().addToStockpile(stockpile, items);
				if (stockpile != null) {
					program.getMainWindow().addTab(program.getStockpileTool(), program.getSettings().isStockpileFocusTab());
					if (program.getSettings().isStockpileFocusTab()) {
						program.getStockpileTool().scrollToSctockpile(stockpile);
					}
				}
			}
		}
	}

	public static class JStockpileMenu extends JMenuItem {

		private Stockpile stockpile;

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

}
