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

package net.nikr.eve.jeveasset.gui.tabs.orders;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;


public class JMarketOrdersTable extends JAutoColumnTable {

	private final DefaultEventTableModel<MyMarketOrder> tableModel;

	public JMarketOrdersTable(final Program program, final DefaultEventTableModel<MyMarketOrder> tableModel) {
		super(program, tableModel);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		MyMarketOrder marketOrder = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

		if (columnName.equals(MarketTableFormat.EXPIRES.getColumnName())) {
			if (marketOrder.isExpired()) {
				ColorSettings.configCell(component, ColorEntry.MARKET_ORDERS_EXPIRED, isSelected);
			} else if (marketOrder.isNearExpired()) {
				ColorSettings.configCell(component, ColorEntry.MARKET_ORDERS_NEAR_EXPIRED, isSelected);
			}
		}
		if (columnName.equals(MarketTableFormat.OUTBID_PRICE.getColumnName())) {
			if (marketOrder.haveOutbid()) {
				if (marketOrder.isOutbid()) {
					if (marketOrder.isOutbidOwned()) {
						ColorSettings.configCell(component, ColorEntry.MARKET_ORDERS_OUTBID_NOT_BEST_OWNED, isSelected);
					} else {
						ColorSettings.configCell(component, ColorEntry.MARKET_ORDERS_OUTBID_NOT_BEST, isSelected);
					}
				} else {
					ColorSettings.configCell(component, ColorEntry.MARKET_ORDERS_OUTBID_BEST, isSelected);
				}
			} else if (marketOrder.isActive()) {
				ColorSettings.configCell(component, ColorEntry.MARKET_ORDERS_OUTBID_UNKNOWN, isSelected);
			}
		}
		//Order filled warning
		if (columnName.equals(MarketTableFormat.QUANTITY.getColumnName())) {
			if (marketOrder.isNearFilled()) {
				ColorSettings.configCell(component, ColorEntry.MARKET_ORDERS_NEAR_FILLED, isSelected);
			}
		}
		//User set location
		if (marketOrder.getLocation().isUserLocation() && columnName.equals(MarketTableFormat.LOCATION.getColumnName())) {
			ColorSettings.configCell(component, ColorEntry.CUSTOM_USER_LOCATION, isSelected);
			return component;
		}
		//Changed date
		if (columnName.equals(MarketTableFormat.CHANGED.getColumnName()) && Settings.get().getTableChanged(MarketOrdersTab.NAME).before(marketOrder.getChanged())) {
			ColorSettings.configCell(component, ColorEntry.MARKET_ORDERS_NEW, isSelected);
			return component;
		}

		return component;
	}
}
