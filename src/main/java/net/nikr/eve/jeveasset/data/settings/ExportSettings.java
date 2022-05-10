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

package net.nikr.eve.jeveasset.data.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.i18n.DialoguesExport;
import net.nikr.eve.jeveasset.io.shared.FileUtil;


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
		private final char character;
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
		UNIX("\n") {
			@Override
			String getI18N() {
				return DialoguesExport.get().lineEndingsUnix();
			}
		},
		MAC("\r") {
			@Override
			String getI18N() {
				return DialoguesExport.get().lineEndingsMac();
			}
		};
		private final String string;
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

	public enum DecimalSeparator {
		DOT(FieldDelimiter.COMMA) {
			@Override
			String getI18N() {
				return DialoguesExport.get().dot();
			}
		},
		COMMA(FieldDelimiter.SEMICOLON) {
			@Override
			String getI18N() {
				return DialoguesExport.get().comma();
			}
		};

		private final FieldDelimiter fieldDelimiter;

		private DecimalSeparator(FieldDelimiter fieldDelimiter) {
			this.fieldDelimiter = fieldDelimiter;
		}

		@Override
		public String toString() {
			return getI18N();
		}

		public FieldDelimiter getFieldDelimiter() {
			return fieldDelimiter;
		}

		abstract String getI18N();
	}

	public enum ExportFormat {
		CSV("csv"),
		SQL("sql"),
		HTML("html");
		
		private final String extension;

		private ExportFormat(String extension) {
			this.extension = extension;
		}

		public String getExtension() {
			return extension;
		}
	}

	/***
	 * Enum that contains the options for column selection in column jcombobox.
	 */
	public enum ColumnSelection {
		SHOWN("shown"),
		SAVED("saved"),
		SELECTED("selected");

		private final String selection;

		private ColumnSelection(String selection) {
			this.selection = selection;
		}

		public String getSelection() {
			return selection;
		}
	}

	/***
	 * Enum that contains the options for filter selection in filter jcombobox.
	 */
	public enum FilterSelection {
		NONE("none"),
		CURRENT("current"),
		SAVED("saved");

		private final String selection;

		private FilterSelection(String selection) {
			this.selection = selection;
		}

		public String getSelection() {
			return selection;
		}
	}

	private static final String PATH = FileUtil.getUserDirectory();

	//Common
	private final String toolName;
	private final List<String> tableExportColumns = new ArrayList<>();
	private String fileName;
	private ExportFormat exportFormat;
	private FilterSelection filterSelection;
	private ColumnSelection columnSelection;
	private String filterName;
	private String viewName;
	private DecimalSeparator decimalSeparator;

	//CLI
	private boolean formulas;
	private boolean jumps;

	//CSV
	private LineDelimiter csvLineDelimiter;

	//SQL
	private boolean sqlCreateTable;
	private boolean sqlDropTable;
	private boolean sqlExtendedInserts;
	private String sqlTableName;

	//HTML
	private boolean htmlStyled;
	private int htmlRepeatHeader;
	private boolean htmlIGB;

	public ExportSettings(String toolName) {
		this.toolName = toolName;
		fileName = "";
		exportFormat = ExportFormat.CSV;
		filterSelection = FilterSelection.NONE;
		columnSelection = ColumnSelection.SHOWN;
		filterName = "";
		viewName = "";
		decimalSeparator = DecimalSeparator.DOT; //CSV & HTML

		formulas = false;
		jumps = false;

		csvLineDelimiter = LineDelimiter.DOS;

		sqlCreateTable = true;
		sqlDropTable = true;
		sqlExtendedInserts = true;
		sqlTableName = "";

		htmlStyled = true;
		htmlRepeatHeader = 0;
		htmlIGB = false;
	}

	public boolean isCsv() {
		return exportFormat == ExportFormat.CSV;
	}

	public DecimalSeparator getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(final DecimalSeparator decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}

	public FieldDelimiter getCsvFieldDelimiter() {
		return decimalSeparator.getFieldDelimiter();
	}

	public LineDelimiter getCsvLineDelimiter() {
		return csvLineDelimiter;
	}

	public void setCsvLineDelimiter(final LineDelimiter csvLineDelimiter) {
		this.csvLineDelimiter = csvLineDelimiter;
	}

	public boolean isSql() {
		return exportFormat == ExportFormat.SQL;
	}

	public boolean isSqlCreateTable() {
		return sqlCreateTable;
	}

	public void setSqlCreateTable(final boolean sqlCreateTable) {
		this.sqlCreateTable = sqlCreateTable;
	}

	public boolean isSqlDropTable() {
		return sqlDropTable;
	}

	public void setSqlDropTable(final boolean sqlDropTable) {
		this.sqlDropTable = sqlDropTable;
	}

	public boolean isSqlExtendedInserts() {
		return sqlExtendedInserts;
	}

	public void setSqlExtendedInserts(final boolean sqlExtendedInserts) {
		this.sqlExtendedInserts = sqlExtendedInserts;
	}

	public String getSqlTableName() {
		if (sqlTableName.isEmpty()) { //Ensure never empty
			return getDefaultTableName(toolName);
		}
		return sqlTableName;
	}

	public void setSqlTableName(String sqlTableName) {
		this.sqlTableName = sqlTableName;
	}

	public boolean isHtml() {
		return exportFormat == ExportFormat.HTML;
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

	public boolean isHtmlIGB() {
		return htmlIGB;
	}

	public void setHtmlIGB(boolean htmlIGB) {
		this.htmlIGB = htmlIGB;
	}

	public ExportFormat getExportFormat() {
		return exportFormat;
	}

	public void setExportFormat(ExportFormat exportFormat) {
		this.exportFormat = exportFormat;
	}

	public FilterSelection getFilterSelection() {
		return filterSelection;
	}

	public void setFilterSelection(FilterSelection filterSelection) {
		this.filterSelection = filterSelection;
	}

	public ColumnSelection getColumnSelection() {
		return columnSelection;
	}

	public void setColumnSelection(ColumnSelection columnSelection) {
		this.columnSelection = columnSelection;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public boolean isFormulas() {
		return formulas;
	}

	public void setFormulas(boolean formulas) {
		this.formulas = formulas;
	}

	public boolean isJumps() {
		return jumps;
	}

	public void setJumps(boolean jumps) {
		this.jumps = jumps;
	}

	public String getFilename() {
		if(this.fileName != null && !this.fileName.isEmpty()) {
			return this.fileName;
		}
		return getDefaultFilename();
	}

	public String getPath() {
		String pathname = getFilename();
		int end = pathname.lastIndexOf(File.separator);
		if (end >= 0) {
			return pathname.substring(0, end + 1);
		} else {
			return getDefaultPath();
		}
	}

	public void setFilename(final String fileName) {
		this.fileName = fileName;
	}

	public List<String> getTableExportColumns() {
		return tableExportColumns;
	}

	public void putTableExportColumns(final List<String> selectedColumns) {
			tableExportColumns.clear();
			if (selectedColumns != null) {
				tableExportColumns.addAll(selectedColumns);
			}
	}

	public String getDefaultPath() {
		return PATH;
	}

	public String getDefaultFilename() {
		return PATH + this.toolName + "_export." + exportFormat.getExtension();
	}

	public static String getDefaultTableName(String toolName) {
		return Program.PROGRAM_NAME.toLowerCase() + "_" + toolName.toLowerCase();
	}
}
