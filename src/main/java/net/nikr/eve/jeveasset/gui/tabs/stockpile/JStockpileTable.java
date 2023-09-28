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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JLabel;
import javax.swing.ToolTipManager;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.InstantToolTip;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileStock;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class JStockpileTable extends JSeparatorTable {

	private final DefaultEventTableModel<StockpileItem> tableModel;
	private int lastColumn = -1;
	private int lastRow = -1;

	public JStockpileTable(final Program program, final DefaultEventTableModel<StockpileItem> tableModel, SeparatorList<?> separatorList) {
		super(program, tableModel, separatorList);
		this.tableModel = tableModel;
		InstantToolTip.install(this);
		final Cursor cursor = getCursor();
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) { }

			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();

				int column = columnAtPoint(p);
				int row = rowAtPoint(p);

				if (lastColumn == column && lastRow == row) {
					return;
				}
				lastColumn = column;
				lastRow = row;

				String columnName = (String) getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
				if (columnName.equals(StockpileTableFormat.COUNT_MINIMUM.getColumnName())) {
					Object object = tableModel.getElementAt(row);
					if (object instanceof StockpileItem) {
						StockpileItem stockpileItem = (StockpileItem) object;
						if (stockpileItem.isEditable()) {
							setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
							ToolTipManager.sharedInstance().setEnabled(false);
							ToolTipManager.sharedInstance().setEnabled(true);
							setToolTipText(TabsStockpile.get().editCell());
							return; //Don't set default
						}
					}
				}
				//Default
				setToolTipText(null);
				setCursor(cursor);
			}
		});
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		Object object = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

		if (component instanceof JLabel) {
			JLabel jLabel = (JLabel) component;
			jLabel.setIcon(null);
			jLabel.setIconTextGap(0);
		}
		if (object instanceof StockpileItem) {
			StockpileItem stockpileItem = (StockpileItem) object;
			//Background
			if (object instanceof SubpileStock) { //Subpile
				if (columnName.equals(StockpileTableFormat.COUNT_MINIMUM.getColumnName())) {
					if (!stockpileItem.isEditable()) {
						ColorSettings.configCell(component, ColorEntry.GLOBAL_GRAND_TOTAL, isSelected);
						return component;
					}
				} else if (columnName.equals(StockpileTableFormat.NAME.getColumnName())) {
					ColorSettings.configCell(component, ColorEntry.GLOBAL_GRAND_TOTAL, isSelected);
					return component;
				} else if (columnName.equals(StockpileTableFormat.TAGS.getColumnName())) {
					ColorSettings.configCell(component, ColorEntry.GLOBAL_GRAND_TOTAL, isSelected);
					return component;
				} else if (!columnName.equals(StockpileTableFormat.GROUP.getColumnName())
							&& !columnName.equals(StockpileTableFormat.COUNT_MINIMUM_MULTIPLIED.getColumnName())) {
					component.setForeground(component.getBackground());
					return component;
				}
			} else if (object instanceof SubpileItem) { //Total
				if (!stockpileItem.isEditable() && columnName.equals(StockpileTableFormat.COUNT_MINIMUM.getColumnName())) {
					ColorSettings.configCell(component, ColorEntry.GLOBAL_GRAND_TOTAL, isSelected);
				} else if (columnName.equals(StockpileTableFormat.TAGS.getColumnName())) {
					ColorSettings.configCell(component, ColorEntry.GLOBAL_GRAND_TOTAL, isSelected);
				}
			} else if (object instanceof StockpileTotal) { //Total
				ColorSettings.configCell(component, ColorEntry.GLOBAL_GRAND_TOTAL, isSelected);
			}
			if (stockpileItem.isEditable() && columnName.equals(StockpileTableFormat.COUNT_MINIMUM.getColumnName())) {
				if (component instanceof JLabel) {
					JLabel jLabel = (JLabel) component;
					jLabel.setIcon(Images.EDIT_EDIT_BACKGROUND.getIcon());
					jLabel.setHorizontalTextPosition(JLabel.TRAILING);
					int columnWidth = getColumnModel().getColumn(column).getWidth();
					int jLabelWidth = jLabel.getMaximumSize().width + 1;
					jLabel.setIconTextGap(Math.max(0, columnWidth - jLabelWidth));
				}
			} else if (columnName.equals(StockpileTableFormat.NAME.getColumnName())) {
				if (Settings.get().isStockpileHalfColors()) {
					if (stockpileItem.getPercentNeeded() >= (Settings.get().getStockpileColorGroup3() / 100.0) ) {
						//Group 3
						ColorSettings.configCell(component, ColorEntry.STOCKPILE_TABLE_OVER_THRESHOLD, isSelected);
					} else if (stockpileItem.getPercentNeeded() >= (Settings.get().getStockpileColorGroup2() / 100.0) ) {
						//Group 2
						ColorSettings.configCell(component, ColorEntry.STOCKPILE_TABLE_BELOW_THRESHOLD_2ND, isSelected);
					} else {
						//Group 1
						ColorSettings.configCell(component, ColorEntry.STOCKPILE_TABLE_BELOW_THRESHOLD, isSelected);
					}
				} else {
					if (stockpileItem.getPercentNeeded() >= (Settings.get().getStockpileColorGroup2() / 100.0) ) {
						//Group 2
						ColorSettings.configCell(component, ColorEntry.STOCKPILE_TABLE_OVER_THRESHOLD, isSelected);
					} else {
						//Group 1
						ColorSettings.configCell(component, ColorEntry.STOCKPILE_TABLE_BELOW_THRESHOLD, isSelected);
					}
				}
			} else if (stockpileItem.isIgnoreMultiplier() && columnName.equals(StockpileTableFormat.COUNT_MINIMUM_MULTIPLIED.getColumnName())) {
				ColorSettings.configCell(component, ColorEntry.GLOBAL_GRAND_TOTAL, isSelected);
			}
			//Foreground
			if (columnName.equals(StockpileTableFormat.COUNT_NOW_INVENTORY.getColumnName()) && !stockpileItem.getStockpile().isAssets()) {
				component.setForeground(component.getBackground());
			} else if (columnName.equals(StockpileTableFormat.COUNT_NOW_BUY_ORDERS.getColumnName()) && (!stockpileItem.getStockpile().isBuyOrders() || stockpileItem.isRuns())) {
				component.setForeground(component.getBackground());
			} else if (columnName.equals(StockpileTableFormat.COUNT_NOW_SELL_ORDERS.getColumnName()) && (!stockpileItem.getStockpile().isSellOrders() || stockpileItem.isRuns())) {
				component.setForeground(component.getBackground());
			} else if (columnName.equals(StockpileTableFormat.COUNT_NOW_BUY_TRANSACTIONS.getColumnName()) && (!stockpileItem.getStockpile().isBuyTransactions() || stockpileItem.isRuns())) {
				component.setForeground(component.getBackground());
			} else if (columnName.equals(StockpileTableFormat.COUNT_NOW_SELL_TRANSACTIONS.getColumnName()) && (!stockpileItem.getStockpile().isSellTransactions() || stockpileItem.isRuns())) {
				component.setForeground(component.getBackground());
			} else if (columnName.equals(StockpileTableFormat.COUNT_NOW_JOBS.getColumnName()) && !stockpileItem.getStockpile().isJobs()) {
				component.setForeground(component.getBackground());
			} else if (columnName.equals(StockpileTableFormat.COUNT_NOW_SELLING_CONTRACTS.getColumnName()) && (!stockpileItem.getStockpile().isSellingContracts() || stockpileItem.isRuns())) {
				component.setForeground(component.getBackground());
			} else if (columnName.equals(StockpileTableFormat.COUNT_NOW_SOLD_CONTRACTS.getColumnName()) && (!stockpileItem.getStockpile().isSoldContracts() || stockpileItem.isRuns())) {
				component.setForeground(component.getBackground());
			} else if (columnName.equals(StockpileTableFormat.COUNT_NOW_BUYING_CONTRACTS.getColumnName()) && (!stockpileItem.getStockpile().isBuyingContracts() || stockpileItem.isRuns())) {
				component.setForeground(component.getBackground());
			} else if (columnName.equals(StockpileTableFormat.COUNT_NOW_BOUGHT_CONTRACTS.getColumnName()) && (!stockpileItem.getStockpile().isBoughtContracts() || stockpileItem.isRuns())) {
				component.setForeground(component.getBackground());
			} else if (columnName.equals(StockpileTableFormat.COUNT_NEEDED.getColumnName()) && stockpileItem.getCountNeeded() < 0) {
				ColorSettings.configCell(component, ColorEntry.GLOBAL_VALUE_NEGATIVE, isSelected);
			} else if (columnName.equals(StockpileTableFormat.VALUE_NEEDED.getColumnName()) && stockpileItem.getValueNeeded() < 0) {
				ColorSettings.configCell(component, ColorEntry.GLOBAL_VALUE_NEGATIVE, isSelected);
			} else if (columnName.equals(StockpileTableFormat.VOLUME_NEEDED.getColumnName()) && stockpileItem.getVolumeNeeded() < 0) {
				ColorSettings.configCell(component, ColorEntry.GLOBAL_VALUE_NEGATIVE, isSelected);
			}
		}
		return component;
	}
}
