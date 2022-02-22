/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.tree;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.ColorUtil;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetTableFormat;


public class JTreeTable extends JAutoColumnTable {

	private final DefaultEventTableModel<TreeAsset> tableModel;

	public JTreeTable(final Program program, final DefaultEventTableModel<TreeAsset> tableModel) {
		super(program, tableModel);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		TreeAsset treeAsset = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
		//Tree
		if (!isSelected && treeAsset.isParent()) {
			if (treeAsset.getDepth() == 0) {
				if (ColorUtil.isBrightColor(getBackground())) { //Light background color
					component.setBackground(new Color(170, 170, 170));
				} else { //Dark background color
					component.setBackground(Color.DARK_GRAY.darker().darker().darker());
				}
				return component;
			} else if (treeAsset.getDepth() == 1) {
				if (ColorUtil.isBrightColor(getBackground())) { //Light background color
					component.setBackground(new Color(190, 190, 190));
				} else { //Dark background color
					component.setBackground(Color.DARK_GRAY.darker().darker());
				}
				return component;
			} else if (treeAsset.getDepth() == 2) {
				if (ColorUtil.isBrightColor(getBackground())) { //Light background color
					component.setBackground(new Color(210, 210, 210));
				} else { //Dark background color
					component.setBackground(Color.DARK_GRAY.darker());
				}
				return component;
			} else if (treeAsset.getDepth() > 2) {
				if (ColorUtil.isBrightColor(getBackground())) { //Light background color
					component.setBackground(new Color(235, 235, 235));
				} else { //Dark background color
					component.setBackground(Color.DARK_GRAY);
				}
				return component;
			}
		}
		//User set price
		if (treeAsset.isUserPrice() && columnName.equals(AssetTableFormat.PRICE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.CUSTOM_PRICE, isSelected);
			return component;
		}
		//User set name
		if (treeAsset.isUserName() && columnName.equals(AssetTableFormat.NAME.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.CUSTOM_ASSET_NAME, isSelected);
			return component;
		}
		//User set location
		if (treeAsset.getLocation().isUserLocation() && columnName.equals(AssetTableFormat.LOCATION.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.CUSTOM_USER_LOCATION, isSelected);
			return component;
		}
		//Blueprint Original
		if (treeAsset.isBPO()
				&& treeAsset.getItem().isBlueprint()
				&& (columnName.equals(AssetTableFormat.PRICE.getColumnName())
				|| columnName.equals(AssetTableFormat.PRICE_SELL_MIN.getColumnName())
				|| columnName.equals(AssetTableFormat.PRICE_BUY_MAX.getColumnName())
				|| columnName.equals(AssetTableFormat.NAME.getColumnName()))) {
			ColorSettings.configCell(component, ColorEntry.GLOBAL_BPO, isSelected);
			return component;
		}
		//Blueprint Copy
		if (treeAsset.isBPC()
				&& treeAsset.getItem().isBlueprint()
				&& (columnName.equals(AssetTableFormat.PRICE.getColumnName())
				|| columnName.equals(AssetTableFormat.PRICE_SELL_MIN.getColumnName())
				|| columnName.equals(AssetTableFormat.PRICE_BUY_MAX.getColumnName())
				|| columnName.equals(AssetTableFormat.NAME.getColumnName()))) {
			ColorSettings.configCell(component, ColorEntry.GLOBAL_BPC, isSelected);
			return component;
		}

		//Reprocessing Colors
		if (Settings.get().isReprocessColors() && !isSelected) {
			//Zero price (White)
			if (treeAsset.getPriceReprocessed() == 0 || treeAsset.getDynamicPrice() == 0) {
				return component;
			}
			//Equal price (Yellow)
			boolean rowSelection = (this.isRowSelected(row) && Settings.get().isHighlightSelectedRows());
			if (treeAsset.getPriceReprocessed() == treeAsset.getDynamicPrice()) {
				ColorSettings.configCell(component, ColorEntry.ASSETS_REPROCESSING_EQUAL, rowSelection, true);
				return component;
			}
			//Reprocessed highest (Red)
			if (treeAsset.getPriceReprocessed() > treeAsset.getDynamicPrice()) {
				ColorSettings.configCell(component, ColorEntry.ASSETS_REPROCESSING_REPROCES, rowSelection, true);
				return component;
			}
			//Price highest (Green)
			if (treeAsset.getPriceReprocessed() < treeAsset.getDynamicPrice()) {
				ColorSettings.configCell(component, ColorEntry.ASSETS_REPROCESSING_SELL, rowSelection, true);
				return component;
			}
		}
		//Reproccessed is greater then price
		if (treeAsset.getPriceReprocessed() > treeAsset.getDynamicPrice() && columnName.equals(AssetTableFormat.PRICE_REPROCESSED.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.ASSETS_REPROCESS, isSelected);
			return component;
		}
		return component;
	}
}
