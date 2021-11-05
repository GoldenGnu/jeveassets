/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.IndustryMaterial;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JOptionInput;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTab;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class JMenuStockpile<T> extends JAutoMenu<T> {

	private enum MenuStockpileAction {
		ADD_TO
	}

	private static final double DEFAULT_ADD_COUNT = 1;

	private List<Stockpile> stockpilesCashe = null;
	private final List<JMenuItem> jMenuItems = new ArrayList<JMenuItem>();
	private final JMenuItem jAddToNew;

	ListenerClass listener = new ListenerClass();

	public JMenuStockpile(final Program program) {
		super(GuiShared.get().stockpile(), program);
		this.setIcon(Images.TOOL_STOCKPILE.getIcon());

		jAddToNew = new JStockpileMenu(GuiShared.get().newStockpile());
		jAddToNew.setIcon(Images.EDIT_ADD.getIcon());
		jAddToNew.setActionCommand(MenuStockpileAction.ADD_TO.name());
		jAddToNew.addActionListener(listener);
	}

	@Override
	public void updateMenuData() {
		List<Stockpile> newValue = StockpileTab.getShownStockpiles(program);
		if (stockpilesCashe == null || !stockpilesCashe.equals(newValue)) {
			stockpilesCashe = new ArrayList<>(newValue); //Update Cache
			updateMenu(); //Stockpiles changed...
		}
		boolean enabled = !menuData.getTypeIDs().isEmpty();

		jAddToNew.setEnabled(enabled);

		for (JMenuItem jMenuItem : jMenuItems) {
			jMenuItem.setEnabled(enabled);
		}
	}

	private void updateMenu() {
		removeAll();

		add(jAddToNew); //Add "To new Stockpile"

		if (!stockpilesCashe.isEmpty()) { //Add Separator (if we have stockpiles)
			addSeparator();
		}

		jMenuItems.clear(); //Clear update list
		for (Stockpile stockpile : stockpilesCashe) { //Create menu items
			JMenuItem jMenuItem = new JStockpileMenu(stockpile);
			jMenuItem.setIcon(Images.TOOL_STOCKPILE.getIcon());
			jMenuItem.setActionCommand(MenuStockpileAction.ADD_TO.name());
			jMenuItem.addActionListener(listener);
			add(jMenuItem);
			jMenuItems.add(jMenuItem);
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
					List<StockpileItem> items = new ArrayList<>();
					Object blueprintSelect = null;
					Object formulaSelect = null;
					for (int typeID : menuData.getBpcTypeIDs()) {
						Item item = ApiIdConverter.getItem(Math.abs(typeID));
						if (item.isBlueprint() && blueprintSelect == null) {
							String[] options = {TabsStockpile.get().source(), TabsStockpile.get().original(), TabsStockpile.get().copy(), TabsStockpile.get().runs(), TabsStockpile.get().materialsManufacturing()};
							blueprintSelect = JOptionInput.showInputDialog(program.getMainWindow().getFrame(), TabsStockpile.get().addBlueprintMsg(), TabsStockpile.get().addBlueprintTitle(), JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
							if (blueprintSelect == null) {
								return; //Cancel
							}
						}
						if (item.getItem().isFormula() && formulaSelect == null) {
							String[] options = {TabsStockpile.get().original(), TabsStockpile.get().materialsReaction()};
							formulaSelect = JOptionInput.showInputDialog(program.getMainWindow().getFrame(), TabsStockpile.get().addFormulaMsg(), TabsStockpile.get().addFormulaTitle(), JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
							if (formulaSelect == null) {
								return; //Cancel
							}
						}
						if ((item.isBlueprint() && blueprintSelect != null && blueprintSelect.equals(TabsStockpile.get().original()))
								|| (item.isFormula() && formulaSelect != null && formulaSelect.equals(TabsStockpile.get().original()))) {
							items.add(new StockpileItem(stockpile, item.getItem(), Math.abs(item.getTypeID()), DEFAULT_ADD_COUNT, false));
						} else if (item.isBlueprint() && blueprintSelect != null && blueprintSelect.equals(TabsStockpile.get().copy())) {
							items.add(new StockpileItem(stockpile, item.getItem(), -Math.abs(item.getTypeID()), DEFAULT_ADD_COUNT, false));
						} else if (item.isBlueprint() && blueprintSelect != null && blueprintSelect.equals(TabsStockpile.get().runs())) {
							items.add(new StockpileItem(stockpile, item.getItem(), -Math.abs(item.getTypeID()), DEFAULT_ADD_COUNT, true));
						} else if (item.isBlueprint() && blueprintSelect != null && blueprintSelect.equals(TabsStockpile.get().materialsManufacturing())) {
							for (IndustryMaterial material : item.getItem().getManufacturingMaterials()) {
								Item materialItem = ApiIdConverter.getItem(material.getTypeID());
								items.add(new StockpileItem(stockpile, materialItem, material.getTypeID(), material.getQuantity(), false));
							}
						} else if (formulaSelect != null && formulaSelect.equals(TabsStockpile.get().materialsReaction())){
							for (IndustryMaterial material : item.getItem().getReactionMaterials()) {
								Item materialItem = ApiIdConverter.getItem(material.getTypeID());
								items.add(new StockpileItem(stockpile, materialItem, material.getTypeID(), material.getQuantity(), false));
							}
						} else { //source or not bluepint/formula
							items.add(new StockpileItem(stockpile, item, typeID, DEFAULT_ADD_COUNT, false));
						}
					}
					stockpile = program.getStockpileTab().addToStockpile(stockpile, items);
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
}
