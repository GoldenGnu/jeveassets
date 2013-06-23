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
package net.nikr.eve.jeveasset.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.i18n.DialoguesExport;


public class ExportSettings {

	public enum FieldDelimiter {
		COMMA(',') {
			@Override
			String getI18N() {
				return DialoguesExport.get().comma();
			}
		},
		SEMICOLON(';') {
			@Override
			String getI18N() {
				return DialoguesExport.get().semicolon();
			}
		};
		private char character;
		private FieldDelimiter(final char character) {
			this.character = character;
		}
		public char getValue() {
			return character;
		}
		@Override
		public String toString() {
			return getI18N();
		}
		abstract String getI18N();
	}
	public enum LineDelimiter {
		DOS("\r\n") {
			@Override
			String getI18N() {
				return DialoguesExport.get().lineEndingsWindows();
			}
		},
		MAC("\r") {
			@Override
			String getI18N() {
				return DialoguesExport.get().lineEndingsMac();
			}
		},
		UNIX("\n") {
			@Override
			String getI18N() {
				return DialoguesExport.get().lineEndingsUnix();
			}
		};
		private String string;
		private LineDelimiter(final String string) {
			this.string = string;
		}
		public String getValue() {
			return string;
		}
		@Override
		public String toString() {
			return getI18N();
		}
		abstract String getI18N();
	}
	public  enum DecimalSeparator {
		DOT() {
			@Override
			String getI18N() {
				return DialoguesExport.get().dot();
			}
		},
		COMMA() {
			@Override
			String getI18N() {
				return DialoguesExport.get().comma();
			}
		};
		@Override
		public String toString() {
			return getI18N();
		}
		abstract String getI18N();
	}

	private static final String PATH = Settings.getUserDirectory();
	//CSV
	private FieldDelimiter fieldDelimiter;
	private LineDelimiter lineDelimiter;
	private DecimalSeparator decimalSeparator;
	//SQL
	private final Map<String, String> tableNames = new HashMap<String, String>(); // Per tool option
	private boolean createTable;
	private boolean dropTable;
	private boolean extendedInserts;
	private boolean htmlStyled;
	private int htmlRepeatHeader;
	//Common - Per tool options
	private final Map<String, List<String>> tableExportColumns = new HashMap<String, List<String>>();
	private final Map<String, String> filenames = new HashMap<String, String>();
	//private String filename = FILENAME;

	public ExportSettings() {
		fieldDelimiter = FieldDelimiter.COMMA;
		lineDelimiter = LineDelimiter.DOS;
		decimalSeparator = DecimalSeparator.DOT;
		//filename = FILENAME;
		createTable = true;
		dropTable = true;
		extendedInserts = true;
		htmlStyled = true;
	}

	public DecimalSeparator getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(final DecimalSeparator decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}

	public FieldDelimiter getFieldDelimiter() {
		return fieldDelimiter;
	}

	public void setFieldDelimiter(final FieldDelimiter fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}

	public LineDelimiter getLineDelimiter() {
		return lineDelimiter;
	}

	public void setLineDelimiter(final LineDelimiter lineDelimiter) {
		this.lineDelimiter = lineDelimiter;
	}

	public boolean isCreateTable() {
		return createTable;
	}

	public void setCreateTable(final boolean createTable) {
		this.createTable = createTable;
	}

	public boolean isDropTable() {
		return dropTable;
	}

	public void setDropTable(final boolean dropTable) {
		this.dropTable = dropTable;
	}

	public boolean isExtendedInserts() {
		return extendedInserts;
	}

	public void setExtendedInserts(final boolean extendedInserts) {
		this.extendedInserts = extendedInserts;
	}

	public boolean isHtmlStyled() {
		return htmlStyled;
	}

	public void setHtmlStyled(boolean htmlStyled) {
		this.htmlStyled = htmlStyled;
	}

	public int getHtmlRepeatHeader() {
		return htmlRepeatHeader;
	}

	public void setHtmlRepeatHeader(int htmlRepeatHeader) {
		this.htmlRepeatHeader = htmlRepeatHeader;
	}

	public Map<String, String> getTableNames() {
		return tableNames;
	}

	public String getTableName(final String tool) {
		if (tableNames.containsKey(tool)) {
			return tableNames.get(tool);
		} else {
			return "";
		}
	}

	public void putTableName(final String tool, final String tableName) {
		tableNames.put(tool, tableName);
	}

	public String getFilename(final String tool) {
		if (filenames.containsKey(tool)) {
			return filenames.get(tool);
		} else {
			return getDefaultFilename(tool);
		}
	}

	public void putFilename(final String tool, final String filename) {
		filenames.put(tool, filename);
	}

	public Map<String, String> getFilenames() {
		return filenames;
	}

	public List<String> getTableExportColumns(final String key) {
		return tableExportColumns.get(key);
	}
	public Set<Map.Entry<String, List<String>>> getTableExportColumns() {
		return tableExportColumns.entrySet();
	}
	public void putTableExportColumns(final String key, final List<String> list) {
		if (list == null) {
			tableExportColumns.remove(key);
		} else {
			tableExportColumns.put(key, list);
		}
	}

	public static String getDefaultPath() {
		return PATH;
	}

	public static String getDefaultFilename(final String tool) {
		return Settings.getUserDirectory() + tool + "_export.csv";
	}
}
