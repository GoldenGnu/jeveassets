/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ColorSettings.ColorRow;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JSimpleColorPicker;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class ColorSettingsPanel extends JSettingsPanel {

	private final JColorTable jTable;
	//Table
	private final DefaultEventTableModel<ColorRow> tableModel;
	private final EnumTableFormatAdaptor<ColorsTableFormat, ColorRow> tableFormat;
	private final EventList<ColorRow> eventList;
	private final DefaultEventSelectionModel<ColorRow> selectionModel;

	public ColorSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().colors(), Images.SETTINGS_COLORS.getIcon());
		
		JSimpleColorPicker jSimpleColorPicker = new JSimpleColorPicker(settingsDialog.getDialog());
		
		//Table Format
		tableFormat = new EnumTableFormatAdaptor<>(ColorsTableFormat.class);
		//Backend
		eventList = EventListManager.create();

		//Separator
		SeparatorList<ColorRow> separatorList = new SeparatorList<>(eventList, new ColorRowSeparatorComparator(), 1, Integer.MAX_VALUE);
		
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		//Table
		jTable = new JColorTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new ColorSeparatorTableCell(jTable, separatorList));
		jTable.setSeparatorEditor(new ColorSeparatorTableCell(jTable, separatorList));
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		jTable.addMouseListener(new MouseAdapter() {
			int lastShownRow = -1;
			int lastShownColumn = -1;
			boolean ignore = false;

			@Override
			public void mousePressed(MouseEvent e) {
				int row = jTable.rowAtPoint(e.getPoint());
				int column = jTable.columnAtPoint(e.getPoint());
				if (column == lastShownColumn && row == lastShownRow) {
					ignore = true;
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (ignore) {
					ignore = false;
					lastShownRow = -1;
					lastShownColumn = -1;
					return; //Ignore same cell click
				}
				int row = jTable.rowAtPoint(e.getPoint());
				int column = jTable.columnAtPoint(e.getPoint());
				
				Object object = tableModel.getElementAt(row);
				if (!(object instanceof ColorRow)) {
					return; //Ignore Separator
				}
				ColorRow colorRow = (ColorRow) object;
				
				String columnName = (String) jTable.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
				
				Rectangle cellRect = jTable.getCellRect(row, column, false);
				Point table = jTable.getLocationOnScreen();
				Point point = new Point(table.x + cellRect.x + cellRect.width, table.y + cellRect.y + cellRect.height);
				if (columnName.equals(ColorsTableFormat.BACKGROUND.getColumnName())) {
					if (!colorRow.getColorEntry().isBackgroundEditable()) {
						return;
					}
					jTable.startEditCell(row, column);
					jSimpleColorPicker.show(colorRow.getBackground(), colorRow.getColorEntry().getBackground(), colorRow.getColorEntry().isBackgroundNullable(), point, new JSimpleColorPicker.ColorListenere() {
						@Override
						public void colorChanged(Color color) {
							colorRow.setBackground(color);
							jTable.stopEditCell();
						}

						@Override
						public void cancelled() {
							update(row, column);
							jTable.stopEditCell();
						}
					});
				}
				if (columnName.equals(ColorsTableFormat.FOREGROUND.getColumnName())) {
					if (!colorRow.getColorEntry().isForegroundEditable()) {
						return;
					}
					jTable.startEditCell(row, column);
					jSimpleColorPicker.show(colorRow.getForeground(), colorRow.getColorEntry().getForeground(), colorRow.getColorEntry().isForegroundNullable(), point, new JSimpleColorPicker.ColorListenere() {
						@Override
						public void colorChanged(Color color) {
							colorRow.setForeground(color);
							jTable.stopEditCell();
						}

						@Override
						public void cancelled() {
							update(row, column);
							jTable.stopEditCell();
						}
					});
				}
			}

			private void update(int row, int column) {
				lastShownRow = row;
				lastShownColumn = column;
				tableModel.fireTableCellUpdated(row, 3);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						//In case we click outside the table
						lastShownRow = -1;
						lastShownColumn = -1;
					}
				});
			}
		});
		//Selection Model
		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);

		JScrollPane jTableScroll = new JScrollPane(jTable);

		JButton jCollapse = new JButton(DialoguesSettings.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jTable.expandSeparators(false);
			}
		});

		JButton jExpand = new JButton(DialoguesSettings.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jTable.expandSeparators(true);
			}
		});

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jCollapse, 100, 100, 100)
					.addComponent(jExpand, 100, 100, 100)
				)
				.addComponent(jTableScroll)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jCollapse, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jTableScroll, 290, 290, 290)
		);
	}

	@Override
	public boolean save() {
		boolean update = false;
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (ColorRow row : eventList) {
				update = Settings.get().getColorSettings().set(row) || update;
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		return update;
	}

	@Override
	public void load() {
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(Settings.get().getColorSettings().get());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	public static class ColorRowSeparatorComparator implements Comparator<ColorRow> {
		@Override
		public int compare(final ColorRow o1, final ColorRow o2) {
			return o1.getColorEntry().getGroup().compareTo(o2.getColorEntry().getGroup());
		}
	}
}

