/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.data;

import net.nikr.eve.jeveasset.i18n.DataModelAssetFilter;


public class AssetFilter {
	public enum Mode {
		MODE_CONTAIN() {
			String getI18N() {
				return DataModelAssetFilter.get().modeContain();
			}
		},
		MODE_CONTAIN_NOT {
			String getI18N() {
				return DataModelAssetFilter.get().modeContainNot();
			}
		},
		MODE_EQUALS {
			String getI18N() {
				return DataModelAssetFilter.get().modeEqual();
			}
		},
		MODE_EQUALS_NOT {
			String getI18N() {
				return DataModelAssetFilter.get().modeEqualNot();
			}
		},
		MODE_GREATER_THAN {
			String getI18N() {
				return DataModelAssetFilter.get().modeGreaterThan();
			}
		},
		MODE_LESS_THAN {
			String getI18N() {
				return DataModelAssetFilter.get().modeLessThan();
			}
		},
		MODE_GREATER_THAN_COLUMN {
			String getI18N() {
				return DataModelAssetFilter.get().modeGreaterThanColumn();
			}
		},
		MODE_LESS_THAN_COLUMN {
			String getI18N() {
				return DataModelAssetFilter.get().modeLessThanColumn();
			}
		},
		;
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}
	}

	public enum Junction {
		AND() {
			String getI18N() {
				return DataModelAssetFilter.get().and();
			}
		},
		OR() {
			String getI18N() {
				return DataModelAssetFilter.get().or();
			}
		}
		;
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}
	}

	private String column;
	private String text;
	private Mode mode;
	private Junction junction;
	private String columnMatch;

	public AssetFilter(String column, String text, Mode mode, Junction junction, String columnMatch) {
		this.column = column;
		this.text = text;
		this.mode = mode;
		this.junction = junction;
		this.columnMatch = columnMatch;
	}

	public boolean isAnd() {
		return junction == Junction.AND;
	}

	public String getColumn() {
		return column;
	}

	public String getColumnMatch() {
		return columnMatch;
	}

	public Mode getMode() {
		return mode;
	}

	public String getText() {
		return text;
	}

	public boolean isEmpty(){
		return (text.equals("") && columnMatch == null);
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + (this.column != null ? this.column.hashCode() : 0);
		hash = 59 * hash + (this.text != null ? this.text.hashCode() : 0);
		hash = 59 * hash + (this.mode != null ? this.mode.hashCode() : 0);
		hash = 59 * hash + (this.isAnd() ? 1 : 0);
		hash = 59 * hash + (this.columnMatch != null ? this.columnMatch.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AssetFilter other = (AssetFilter) obj;
		if ((this.column == null) ? (other.column != null) : !this.column.equals(other.column)) {
			return false;
		}
		if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
			return false;
		}
		if ((this.mode == null) ? (other.mode != null) : !this.mode.equals(other.mode)) {
			return false;
		}
		if (this.isAnd() != other.isAnd()) {
			return false;
		}
		if ((this.columnMatch == null) ? (other.columnMatch != null) : !this.columnMatch.equals(other.columnMatch)) {
			return false;
		}
		return true;
	}


}
