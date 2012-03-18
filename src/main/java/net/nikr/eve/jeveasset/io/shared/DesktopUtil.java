/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.shared;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DesktopUtil {

	private final static Logger LOG = LoggerFactory.getLogger(DesktopUtil.class);

	private DesktopUtil() {
	}

	private static boolean isSupported(Desktop.Action action){
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(action)) {
				return true;
			}
		}
		return false;
	}

	public static void open(String filename, Program program){
		File file = new File(filename);
		LOG.info("Opening: {}", file.getName());
		if (isSupported(Desktop.Action.OPEN)) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.open(file);
				return;
			} catch (IOException ex) {
				LOG.warn("	Opening Failed: {}", ex.getMessage());
			}
		} else {
			LOG.warn("	Opening failed");
		}
		JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Could not open "+file.getName(), "Open File", JOptionPane.PLAIN_MESSAGE);
	}


	public static void browse(String url, Program program){
		LOG.info("Browsing: {}", url);
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException ex) {
			uri = null;
		}
		if (isSupported(Desktop.Action.BROWSE) && uri != null) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(uri);
				return;
			} catch (IOException ex) {
				LOG.warn("	Browsing Failed: {}", ex.getMessage());
			}
		} else {
			LOG.warn("	Browsing failed");
		}
		JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Could not browse to:\n"+url, "Browse", JOptionPane.PLAIN_MESSAGE);
	}
}
