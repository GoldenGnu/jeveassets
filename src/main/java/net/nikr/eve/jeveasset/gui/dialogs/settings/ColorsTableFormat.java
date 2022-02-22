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
package net.nikr.eve.jeveasset.gui.dialogs.settings;

import ca.odell.glazedlists.GlazedLists;
import java.awt.Color;
import java.util.Comparator;
import net.nikr.eve.jeveasset.data.settings.ColorSettings.ColorRow;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;

public enum ColorsTableFormat implements EnumTableColumn<ColorRow> {
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesSettings.get().columnName();
		}
		@Override
		public Object getColumnValue(final ColorRow from) {
			return from.getColorEntry().getDescription();
		}
	},
	BACKGROUND(Color.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesSettings.get().columnBackground();
		}
		@Override
		public Object getColumnValue(final ColorRow from) {
			return from.getBackground();
		}
	},
	FOREGROUND(Color.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesSettings.get().columnForeground();
		}
		@Override
		public Object getColumnValue(final ColorRow from) {
			return from.getForeground();
		}
	},
	PREVIEW(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesSettings.get().columnPreview();
		}
		@Override
		public Object getColumnValue(final ColorRow from) {
			return DialoguesSettings.get().testText();
		}
	},
	SELECTED(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesSettings.get().columnSelected();
		}
		@Override
		public Object getColumnValue(final ColorRow from) {
			return DialoguesSettings.get().testSelectedText();
		}
	};

	private final Class<?> type;
	private final Comparator<?> comparator;

	private ColorsTableFormat(final Class<?> type, final Comparator<?> comparator) {
		this.type = type;
		this.comparator = comparator;
	}
	@Override
	public Class<?> getType() {
		return type;
	}
	@Override
	public Comparator<?> getComparator() {
		return comparator;
	}
	@Override
	public boolean isColumnEditable(final Object baseObject) {
		return false;
	}
	@Override
	public boolean isShowDefault() {
		return true;
	}
	@Override
	public boolean setColumnValue(final Object baseObject, final Object editedValue) {
		return false;
	}
	@Override
	public String toString() {
		return getColumnName();
	}
	//XXX - TableFormat.getColumnValue(...) Workaround
	@Override public abstract Object getColumnValue(final ColorRow from);
	//XXX - TableFormat.getColumnName() Workaround
	@Override public abstract String getColumnName();
}
