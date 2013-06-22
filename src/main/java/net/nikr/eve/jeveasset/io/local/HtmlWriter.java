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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class HtmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(HtmlWriter.class);

	private HtmlWriter() { }

	public static boolean save(final String filename, final List<Map<String, String>> data, final List<String> header) {
		HtmlWriter writer = new HtmlWriter();
		return writer.write(filename, data, header);
	}

	private boolean write(final String filename, final List<Map<String, String>> data, final List<String> header) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writeComment(writer);
			writer.write("<table>\r\n");
			writeHeader(writer, header);
			writeRows(writer, data, header);
			writer.write("</table>\r\n");
			writer.close();
		} catch (IOException ex) {
			LOG.warn("Html file not saved");
			return false;
		}
		LOG.info("Html file saved");
		return true;
	}

	private void writeComment(final BufferedWriter writer) throws IOException {
		writer.write("<!-- " + Program.PROGRAM_NAME + " Html Export -->\r\n");
		writer.write("<!-- version " + Program.PROGRAM_VERSION + " -->\r\n");
		writer.write("<!-- " + Program.PROGRAM_HOMEPAGE + " -->\r\n");
	}

	private void writeHeader(final BufferedWriter writer, final List<String> header) throws IOException {
		writer.write("<tr>\r\n");
		for (String column : header) {
			writer.write("\t<th>");
			writer.write(column);
			writer.write("</th>\r\n");
		}
		writer.write("</tr>\r\n");
	}

	private void writeRows(final BufferedWriter writer, final List<Map<String, String>> data, final List<String> header) throws IOException {
		for (Map<String, String> map : data) {
			writer.write("<tr>\r\n");
			for (String s : header) {
				writer.write("\t<td>");
				writer.write(map.get(s).replace(" ", "&nbsp;"));
				writer.write("</td>\r\n");
			}
			writer.write("</tr>\r\n");
		}
	}
}
