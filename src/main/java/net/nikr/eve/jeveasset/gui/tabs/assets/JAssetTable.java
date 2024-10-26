/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.assets;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;


public class JAssetTable extends JAutoColumnTable {

	private final DefaultEventTableModel<MyAsset> tableModel;

	public JAssetTable(final Program program, final DefaultEventTableModel<MyAsset> tableModel) {
		super(program, tableModel);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		MyAsset asset = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

		//User set price
		if (asset.isUserPrice() && columnName.equals(AssetTableFormat.PRICE.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.CUSTOM_PRICE, isSelected);
			return component;
		}
		//User set name
		if (asset.isUserName() && columnName.equals(AssetTableFormat.NAME.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.CUSTOM_ASSET_NAME, isSelected);
			return component;
		}
		//User set location
		if (asset.getLocation().isUserLocation() && columnName.equals(AssetTableFormat.LOCATION.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.CUSTOM_USER_LOCATION, isSelected);
			return component;
		}
		//Blueprint Original
		if (asset.isBPO()
				&& asset.getItem().isBlueprint()
				&& (columnName.equals(AssetTableFormat.PRICE.getColumnName())
				|| columnName.equals(AssetTableFormat.PRICE_SELL_MIN.getColumnName())
				|| columnName.equals(AssetTableFormat.PRICE_BUY_MAX.getColumnName())
				|| columnName.equals(AssetTableFormat.NAME.getColumnName()))) {
			ColorSettings.configCell(component, ColorEntry.GLOBAL_BPO, isSelected);
			return component;
		}
		//Blueprint Copy
		if (asset.isBPC()
				&& asset.getItem().isBlueprint()
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
			if (asset.getPriceReprocessed() == 0 || asset.getDynamicPrice() == 0) {
				return component;
			}
			//Equal price (Yellow)
			boolean rowSelection = (this.isRowSelected(row) && Settings.get().isHighlightSelectedRows());
			if (asset.getPriceReprocessed() == asset.getDynamicPrice()) {
				ColorSettings.configCell(component, ColorEntry.ASSETS_REPROCESSING_EQUAL, rowSelection, true);
				return component;
			}
			//Reprocessed highest (Red)
			if (asset.getPriceReprocessed() > asset.getDynamicPrice()) {
				ColorSettings.configCell(component, ColorEntry.ASSETS_REPROCESSING_REPROCES, rowSelection, true);
				return component;
			}
			//Price highest (Green)
			if (asset.getPriceReprocessed() < asset.getDynamicPrice()) {
				ColorSettings.configCell(component, ColorEntry.ASSETS_REPROCESSING_SELL, rowSelection, true);
				return component;
			}
		}
		//Reproccessed is greater then price
		if (asset.getPriceReprocessed() > asset.getDynamicPrice() && columnName.equals(AssetTableFormat.PRICE_REPROCESSED.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.ASSETS_REPROCESS, isSelected);
			return component;
		}
		//Added date
		if (columnName.equals(AssetTableFormat.ADDED.getColumnName()) && Settings.get().getTableChanged(AssetsTab.NAME).before(asset.getAdded())) {
			ColorSettings.configCell(component, ColorEntry.ASSETS_NEW, isSelected);
		}
		return component;
	}
}
