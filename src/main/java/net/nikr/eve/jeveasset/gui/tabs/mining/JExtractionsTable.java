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

package net.nikr.eve.jeveasset.gui.tabs.mining;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Component;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyExtraction;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;


public class JExtractionsTable extends JAutoColumnTable {

	private static final Calendar CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

	private final DefaultEventTableModel<MyExtraction> tableModel;

	public JExtractionsTable(final Program program, final DefaultEventTableModel<MyExtraction> tableModel) {
		super(program, tableModel);
		this.tableModel = tableModel;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean isSelected = isCellSelected(row, column);
		MyExtraction extraction = tableModel.getElementAt(row);
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

		if (columnName.equals(ExtractionsTableFormat.ARRIVAL.getColumnName())) {
			Date start = extraction.getChunkArrivalTime();
			if ((nextDays(start, 2) || lastDays(start, 2))) {
				ColorSettings.configCell(component, ColorEntry.EXTRACTIONS_DAYS, isSelected);
			} else if (nextDays(start, 7)) {
				ColorSettings.configCell(component, ColorEntry.EXTRACTIONS_WEEK, isSelected);
			} else if (!past(start)) {
				ColorSettings.configCell(component, ColorEntry.EXTRACTIONS_WEEKS, isSelected);
			}
		}
		if (columnName.equals(ExtractionsTableFormat.DECAY.getColumnName())) {
			Date end = extraction.getNaturalDecayTime();
			if (lastDays(end, 2)) {
				ColorSettings.configCell(component, ColorEntry.EXTRACTIONS_DAYS, isSelected);
			}
		}
		return component;
	}

	private boolean lastDays(final Date date, final int days) {
		CALENDAR.setTime(new Date());
		CALENDAR.add(Calendar.DAY_OF_MONTH, -days);
		return date.before(Settings.getNow()) && date.after(CALENDAR.getTime());
	}

	private boolean past(final Date date) {
		return date.before(Settings.getNow());
	}

	private boolean nextDays(final Date date, final int days) {
		CALENDAR.setTime(new Date());
		CALENDAR.add(Calendar.DAY_OF_MONTH, days);
		return date.after(Settings.getNow()) && date.before(CALENDAR.getTime());
	}
}
