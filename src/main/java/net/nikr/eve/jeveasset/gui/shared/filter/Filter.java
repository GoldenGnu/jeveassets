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

package net.nikr.eve.jeveasset.gui.shared.filter;

import java.util.Comparator;
import javax.swing.Icon;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class Filter {

	public static class AllColumn<T> implements EnumTableColumn<T> {

		public static final AllColumn<Object> ALL = new AllColumn<Object>();
		
		@Override
		public Class<?> getType() {
			return Object.class;
		}

		@Override
		public Comparator<?> getComparator() {
			return null;
		}

		@Override
		public String getColumnName() {
			return GuiShared.get().filterAll();
		}

		@Override
		public Object getColumnValue(T from) {
			return null;
		}

		@Override
		public String name() {
			return "ALL";
		}

		@Override
		public boolean isColumnEditable(Object baseObject) {
			return false;
		}

		@Override
		public boolean isShowDefault() {
			return false;
		}

		@Override
		public T setColumnValue(Object baseObject, Object editedValue) {
			return null;
		}

		@Override
		public String toString() {
			return GuiShared.get().filterAll();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final AllColumn<?> other = (AllColumn<?>) obj;
			return this.name().equals(other.name());
		}

		@Override
		public int hashCode() {
			int hash = 5;
			return hash;
		}
	}

	public enum CompareType {
		CONTAINS(Images.FILTER_CONTAIN.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterContains(); }
		},
		CONTAINS_NOT(Images.FILTER_NOT_CONTAIN.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterContainsNot(); }
		},
		EQUALS(Images.FILTER_EQUAL.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterEquals(); }
		},
		EQUALS_NOT(Images.FILTER_NOT_EQUAL.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterEqualsNot(); }
		},
		GREATER_THAN(Images.FILTER_GREATER_THAN.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterGreaterThan(); }
		},
		LESS_THAN(Images.FILTER_LESS_THAN.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterLessThan(); }
		},
		EQUALS_DATE(Images.FILTER_EQUAL_DATE.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterEqualsDate(); }
		},
		EQUALS_NOT_DATE(Images.FILTER_NOT_EQUAL_DATE.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterEqualsNotDate(); }
		},
		BEFORE(Images.FILTER_BEFORE.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterBefore(); }
		},
		AFTER(Images.FILTER_AFTER.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterAfter(); }
		},
		CONTAINS_COLUMN(Images.FILTER_CONTAIN_COLUMN.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterContainsColumn(); }
		},
		CONTAINS_NOT_COLUMN(Images.FILTER_NOT_CONTAIN_COLUMN.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterContainsNotColumn(); }
		},
		EQUALS_COLUMN(Images.FILTER_EQUAL_COLUMN.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterEqualsColumn(); }
		},
		EQUALS_NOT_COLUMN(Images.FILTER_NOT_EQUAL_COLUMN.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterEqualsNotColumn(); }
		},
		GREATER_THAN_COLUMN(Images.FILTER_GREATER_THAN_COLUMN.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterGreaterThanColumn(); }
		},
		LESS_THAN_COLUMN(Images.FILTER_LESS_THAN_COLUMN.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterLessThanColumn(); }
		},
		BEFORE_COLUMN(Images.FILTER_BEFORE_COLUMN.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterBeforeColumn(); }
		},
		AFTER_COLUMN(Images.FILTER_AFTER_COLUMN.getIcon()) {
			@Override String getI18N() { return GuiShared.get().filterAfterColumn(); }
		};

		private static final CompareType[] VALUES_ALL = new CompareType[]
			{CONTAINS,
			CONTAINS_NOT,
			EQUALS,
			EQUALS_NOT,
		};
		private static final CompareType[] VALUES_STRING = new CompareType[]
			{CONTAINS,
			CONTAINS_NOT,
			EQUALS,
			EQUALS_NOT,
			CONTAINS_COLUMN,
			CONTAINS_NOT_COLUMN,
			EQUALS_COLUMN,
			EQUALS_NOT_COLUMN
		};
		private static final CompareType[] VALUES_NUMERIC = new CompareType[]
			{CONTAINS,
			CONTAINS_NOT,
			EQUALS,
			EQUALS_NOT,
			GREATER_THAN,
			LESS_THAN,
			CONTAINS_COLUMN,
			CONTAINS_NOT_COLUMN,
			EQUALS_COLUMN,
			EQUALS_NOT_COLUMN,
			GREATER_THAN_COLUMN,
			LESS_THAN_COLUMN,
		};
		private static final CompareType[] VALUES_DATE = new CompareType[]
			{CONTAINS,
			CONTAINS_NOT,
			EQUALS,
			EQUALS_NOT,
			EQUALS_DATE,
			EQUALS_NOT_DATE,
			BEFORE,
			AFTER,
			//CONTAINS_COLUMN,
			//CONTAINS_NOT_COLUMN,
			EQUALS_COLUMN,
			EQUALS_NOT_COLUMN,
			BEFORE_COLUMN,
			AFTER_COLUMN,
		};

		private final Icon icon;
		private CompareType(final Icon icon) {
			this.icon = icon;
		}

		public Icon getIcon() {
			return icon;
		}

		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}
		public static CompareType[] valuesAll() {
			return VALUES_ALL;
		}
		public static CompareType[] valuesString() {
			return VALUES_STRING;
		}
		public static CompareType[] valuesNumeric() {
			return VALUES_NUMERIC;
		}
		public static CompareType[] valuesDate() {
			return VALUES_DATE;
		}

		public static boolean isNot(final CompareType compareType) {
			return compareType == CompareType.CONTAINS_NOT
				|| compareType == CompareType.CONTAINS_NOT_COLUMN
				|| compareType == CompareType.EQUALS_NOT
				|| compareType == CompareType.EQUALS_NOT_COLUMN
				|| compareType == CompareType.EQUALS_NOT_DATE;
		}
		public static boolean isColumnCompare(final CompareType compareType) {
			return compareType == CompareType.GREATER_THAN_COLUMN
				|| compareType == CompareType.LESS_THAN_COLUMN
				|| compareType == CompareType.EQUALS_COLUMN
				|| compareType == CompareType.EQUALS_NOT_COLUMN
				|| compareType == CompareType.CONTAINS_COLUMN
				|| compareType == CompareType.CONTAINS_NOT_COLUMN
				|| compareType == CompareType.BEFORE_COLUMN
				|| compareType == CompareType.AFTER_COLUMN;
		}
		public static boolean isNumericCompare(final CompareType compareType) {
			return compareType == CompareType.GREATER_THAN_COLUMN
				|| compareType == CompareType.LESS_THAN_COLUMN
				|| compareType == CompareType.GREATER_THAN
				|| compareType == CompareType.LESS_THAN;
		}
		public static boolean isDateCompare(final CompareType compareType) {
			return compareType == CompareType.BEFORE
				|| compareType == CompareType.AFTER
				|| compareType == CompareType.EQUALS_DATE
				|| compareType == CompareType.EQUALS_NOT_DATE
				|| compareType == CompareType.BEFORE_COLUMN
				|| compareType == CompareType.AFTER_COLUMN;
		}
	}
	public enum LogicType {
		AND() {
			@Override
			public String getI18N() {
				return GuiShared.get().filterAnd();
			}
		},
		OR() {
			@Override
			public String getI18N() {
				return GuiShared.get().filterOr();
			}
		};

		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}
	}

	private LogicType logic;
	private EnumTableColumn<?> column;
	private CompareType compare;
	private String text;

	public Filter(final String logic, final EnumTableColumn<?> column, final String compare, final String text) {
		this(LogicType.valueOf(logic), column, CompareType.valueOf(compare), text);
	}

	public Filter(final LogicType logic, final EnumTableColumn<?> column, final CompareType compare, final String text) {
		this.logic = logic;
		this.column = column;
		this.compare = compare;
		this.text = text;
	}

	public EnumTableColumn<?> getColumn() {
		return column;
	}

	public CompareType getCompareType() {
		return compare;
	}

	public boolean isAnd() {
		return logic == LogicType.AND;
	}

	public LogicType getLogic() {
		return logic;
	}

	public boolean isEmpty() {
		return text.isEmpty();
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Filter other = (Filter) obj;
		if (this.logic != other.logic) {
			return false;
		}
		if (!this.column.equals(other.column)) {
			return false;
		}
		if (this.compare != other.compare) {
			return false;
		}
		return !((this.text == null) ? (other.text != null) : !this.text.equals(other.text));
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 43 * hash + (this.logic != null ? this.logic.hashCode() : 0);
		hash = 43 * hash + (this.column != null ? this.column.hashCode() : 0);
		hash = 43 * hash + (this.compare != null ? this.compare.hashCode() : 0);
		hash = 43 * hash + (this.text != null ? this.text.hashCode() : 0);
		return hash;
	}
}
