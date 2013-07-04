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

package net.nikr.eve.jeveasset.io.local;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.NumberValue;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTableFormat.HierarchyColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class HtmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(HtmlWriter.class);

	private HtmlWriter() { }

	public static boolean save(final String filename, final List<Map<EnumTableColumn<?>, String>> data, final List<EnumTableColumn<?>> header, final boolean htmlStyled, final int htmlRepeatHeader, final boolean treetable) {
		HtmlWriter writer = new HtmlWriter();
		return writer.write(filename, data, header, htmlStyled, htmlRepeatHeader, treetable);
	}

	private boolean write(final String filename, final List<Map<EnumTableColumn<?>, String>> data, final List<EnumTableColumn<?>> header, final boolean htmlStyled, final int htmlRepeatHeader, final boolean treetable) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			if (htmlStyled) {
				writeHeader(writer);
			} else {
				writeComment(writer);
			}
			writer.write("<table>\r\n");
			writeTableHeader(writer, header);
			writeTableRows(writer, data, header, htmlStyled, htmlRepeatHeader, treetable);
			writer.write("</table>\r\n");
			if (htmlStyled) {
				writeFooter(writer);
			}
			writer.close();
		} catch (IOException ex) {
			LOG.warn("Html file not saved");
			return false;
		}
		LOG.info("Html file saved");
		return true;
	}

	private void writeHeader(final BufferedWriter writer) throws IOException {
		writer.write("<!DOCTYPE html>\r\n");
		writer.write("<html>\r\n");
		writer.write("<header>\r\n");
		writeComment(writer);
		writer.write("<style type=\"text/css\">\r\n");
		writer.write(
					//Table
					"table {\r\n" +
					"	border: solid #ccc;\r\n" +
					"	border-width: 1px 0px 1px 1px;\r\n" +
					"	border-spacing: 0;\r\n" +
					"	font-family: Arial, Helvetica, sans-serif;\n" +
					"	font-size: 13px;\r\n" +
					"}\r\n" +
					//Cells
					"table td, table th {\r\n" +
					"	border: solid #ccc;\r\n" +
					"	border-width: 0px 1px 0px 0px;\r\n" +
					"	margin: 0;\r\n" +
					"	padding: 2px 3px 2px 3px;\r\n" +
					"	white-space: nowrap;\r\n" +
					"}\r\n" +
					//Header
					"table th {\r\n" +
					"	background-color: #ddd;\r\n" +
					"	font-size: 15px;\r\n" +
					"}\r\n" +
					//Even rows (dark)
					".even {\r\n" +
					"	background-color: #f3f3f3;\r\n" +
					"}\r\n" +
					//TreeTable
					".level0 {\r\n" +
					"	background-color: #777;\r\n" +
					"}\r\n" +
					".level1 {\r\n" +
					"	background-color: #999;\r\n" +
					"}\r\n" +
					".level2 {\r\n" +
					"	background-color: #bbb;\r\n" +
					"}\r\n" +
					".number {\r\n" +
					"	text-align: right;\r\n" +
					"}\r\n"
					);
		writer.write("</style>\r\n");
		writer.write("</header>\r\n");
		writer.write("<body>\r\n");
	}

	private void writeFooter(final BufferedWriter writer) throws IOException {
		writer.write("</body>\r\n");
		writer.write("</html>\r\n");
	}

	private void writeComment(final BufferedWriter writer) throws IOException {
		writer.write("<!-- " + Program.PROGRAM_NAME + " Html Export -->\r\n");
		writer.write("<!-- version " + Program.PROGRAM_VERSION + " -->\r\n");
		writer.write("<!-- " + Program.PROGRAM_HOMEPAGE + " -->\r\n");
	}

	private void writeTableHeader(final BufferedWriter writer, List<EnumTableColumn<?>> header) throws IOException {
		writer.write("<tr>\r\n");
		for (EnumTableColumn<?> column : header) {
			writer.write("\t<th>");
			writer.write(column.getColumnName());
			writer.write("</th>\r\n");
		}
		writer.write("</tr>\r\n");
	}

	private void writeTableRows(final BufferedWriter writer, final List<Map<EnumTableColumn<?>, String>> data, final List<EnumTableColumn<?>> header, final boolean htmlStyled, final int htmlRepeatHeader, final boolean treetable) throws IOException {
		boolean even = false;
		boolean wait = true;
		int count = 0;
		for (Map<EnumTableColumn<?>, String> map : data) {
			boolean level0 = false;
			boolean level1 = false;
			boolean level2 = false;
			if (treetable && htmlStyled) {
				for (EnumTableColumn<?> column : header) {
					if (HierarchyColumn.class.isAssignableFrom(column.getType())) {
						if (map.get(column).contains(TreeAsset.SPACE + TreeAsset.SPACE + "+") && treetable) { //Level 2
							level2 = true;
							break;
						} else if (map.get(column).startsWith(TreeAsset.SPACE + "+") && treetable) { //Level 1
							level1 = true;
							break;
						} else if (map.get(column).startsWith("+") && treetable) { //Level 0
							level0 = true;
							break;
						}
					}
				}
			}
			if (level0 || level1 || level2) { //Parent
				if (!wait) {
					writeTableHeader(writer, header);
					wait = true;
					count = 0;
				}
			} else if (htmlRepeatHeader != 0 && htmlRepeatHeader == count && !wait) { //Repeat
				writeTableHeader(writer, header);
				count = 0;
			} else { //item row
				wait = false;
			}
			if (level0 && htmlStyled) {
				writer.write("\t<tr class=\"level0\">");
			} else if (level1 && htmlStyled) {
				writer.write("\t<tr class=\"level1\">");
			} else if (level2 && htmlStyled) {
				writer.write("\t<tr class=\"level2\">");
			} else if (even && htmlStyled) {
				writer.write("\t<tr class=\"even\">");
			} else {
				writer.write("\t<tr>");
			}
			for (EnumTableColumn<?> column : header) {
				if ((Number.class.isAssignableFrom(column.getType())
				 || NumberValue.class.isAssignableFrom(column.getType()))) {
					writer.write("\t<td class=\"number\">");
				} else {
					writer.write("\t<td>");
				}
				writer.write(map.get(column).replace(" ", "&nbsp;").replace("+", "")); //.replace("-", "&#8209;")
				writer.write("</td>\r\n");
			}
			writer.write("</tr>\r\n");
			even = !even;
			if (!level0 && !level1 && !level2) {
				count++;
			}
		}
	}
}
