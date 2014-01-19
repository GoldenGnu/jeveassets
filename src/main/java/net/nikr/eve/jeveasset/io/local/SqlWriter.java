/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.local;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTableFormat.HierarchyColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class SqlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(SqlWriter.class);

	private final DecimalFormat DOUBLE_FORMAT  = new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.ENGLISH));
	private final DecimalFormat FLOAT_FORMAT  = new DecimalFormat("0.####", new DecimalFormatSymbols(Locale.ENGLISH));
	private final DecimalFormat LONG_FORMAT  = new DecimalFormat("0", new DecimalFormatSymbols(Locale.ENGLISH));
	private final int MAX_LENGTH = 944000; //a little less than 1MB
	private final DateFormat SQL_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

	private SqlWriter() { }

	public static boolean save(final String filename, final List<Map<EnumTableColumn<?>, Object>> rows, final List<EnumTableColumn<?>> header, final String tableName, final boolean dropTable, final boolean createTable, final boolean extendedInserts) {
		SqlWriter writer = new SqlWriter();
		return writer.write(filename, rows, header, tableName, dropTable, createTable, extendedInserts);
	}

	private boolean write(final String filename, final List<Map<EnumTableColumn<?>, Object>> rows, final List<EnumTableColumn<?>> header, final String tableName, final boolean dropTable, final boolean createTable, final boolean extendedInserts) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writeComment(writer);
			writeTable(writer, rows, header, tableName, dropTable, createTable);
			writeRows(writer, rows, header, tableName, extendedInserts);
			writer.close();
		} catch (IOException ex) {
			LOG.warn("SQL file not saved");
			return false;
		}
		LOG.info("SQL file saved");
		return true;
	}

	private void writeComment(final BufferedWriter writer) throws IOException {
		writer.write("-- " + Program.PROGRAM_NAME + " Sql Export\r\n");
		writer.write("-- version " + Program.PROGRAM_VERSION + "\r\n");
		writer.write("-- " + Program.PROGRAM_HOMEPAGE + "\r\n");
	}

	private String getType(final Object object) {
		if (object instanceof Short) {
			return "smallint";
		} else if (object instanceof Integer) {
			return "int";
		} else if (object instanceof Long) {
			return "bigint";
		} else if (object instanceof Float) {
			return "float";
		} else if (object instanceof Double) {
			return "double";
		} else if (object instanceof Date) {
			return "date";
		} else {
			return "text";
		}
	}
	private void writeTable(final BufferedWriter writer, final List<Map<EnumTableColumn<?>, Object>> rows, final List<EnumTableColumn<?>> header, final String tableName, final boolean dropTable, final boolean createTable) throws IOException {
		if (dropTable) {
			writer.write("DROP TABLE IF EXISTS `" + tableName + "`;\r\n");
		}
		if (createTable && !rows.isEmpty()) {
			writer.write("CREATE TABLE IF NOT EXISTS `" + tableName + "` (\r\n");
			boolean first = true;
			for (EnumTableColumn<?> column : header) {
				if (first) {
					first = false;
				} else {
					writer.write(",\r\n");
				}
				writer.write("`" + column.name() + "` " + getType(rows.get(0).get(column)));
			}
			writer.write("\r\n");
			writer.write(") ENGINE=MyISAM  DEFAULT CHARSET=utf8 ;\r\n");
		}
	}

	private void writeRows(final BufferedWriter writer, final List<Map<EnumTableColumn<?>, Object>> rows, final List<EnumTableColumn<?>> header, final String tableName, final boolean extendedInserts) throws IOException {
		if (!rows.isEmpty()) {
			//Create INSERT statement
			String insert = "INSERT INTO `" + tableName + "` (";
			boolean firstInsert = true;
			for (EnumTableColumn<?> column : header) {
				if (firstInsert) {
					firstInsert = false;
				} else {
					insert = insert + ", ";
				}
				insert = insert + "`" + column.name() + "`";
			}
			insert = insert + ") VALUES\r\n";
			if (extendedInserts) {
				writer.write(insert);
			}
			boolean firstRow = true;
			boolean firstCell;
			//Add values
			String values;
			int length = insert.getBytes("UTF-8").length;
			for (Map<EnumTableColumn<?>, Object> map : rows) {
				values = "";
				if (extendedInserts && length > MAX_LENGTH) {
					length = insert.getBytes("UTF-8").length;
					firstRow = true;
					writer.write(";\r\n");
					writer.write(insert);
				}
				//End Line
				if (firstRow) {
					firstRow = false;
				} else {
					if (extendedInserts) {
						values = values + ",\r\n";
					} else {
						values = values + ";\r\n";
					}
				}
				//Values
				values = values + "	(";
				firstCell = true;
				for (EnumTableColumn<?> column : header) {
					if (firstCell) {
						firstCell = false;
					} else {
						values = values + ", ";
					}
					values = values + format(map.get(column));
				}
				values = values + ")";
				if (!extendedInserts) {
					writer.write(insert);
				}
				length = length + values.getBytes("UTF-8").length; //Bytes
				writer.write(values);
			}
			writer.write(";\r\n");
		}
	}

	private String format(final Object object) {
		if (object == null) {
			return "''";
		} else if (object instanceof HierarchyColumn) {
			HierarchyColumn column = (HierarchyColumn) object;
			return "'" + column.getExport().replace("'", "\\'") + "'";
		} else if (object instanceof Double) {
			//Double
			return DOUBLE_FORMAT.format(object);
		} else if (object instanceof Float) {
			//Float
			return FLOAT_FORMAT.format(object);
		} else if (object instanceof Number) {
			//Number (Short/Integer/Long)
			return LONG_FORMAT.format(object);
		} else if (object instanceof Date) {
			//Date
			return "'" + SQL_DATE_FORMATTER.format(object) + "'";
		} else { //String etc.
			return "'" + String.valueOf(object).replace("'", "\\'") + "'";
		}
	}
}
