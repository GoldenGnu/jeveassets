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

package net.nikr.eve.jeveasset.io.shared;

import java.awt.Desktop;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import net.nikr.eve.jeveasset.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class DesktopUtil {

	private static final Logger LOG = LoggerFactory.getLogger(DesktopUtil.class);

	private DesktopUtil() { }

	public static HyperlinkListener getHyperlinkListener(Program program) {
		return new LinkListener(program);
	}

	private static boolean isSupported(final Desktop.Action action) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(action)) {
				return true;
			}
		}
		return false;
	}

	public static void open(final String filename, final Program program) {
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
		JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Could not open " + file.getName(), "Open File", JOptionPane.PLAIN_MESSAGE);
	}

	public static void browse(final String url) {
		browse(url, (Window)null);
	}

	public static void browse(final String url, Program program) {
		if (program != null) {
			browse(url, program.getMainWindow().getFrame());
		} else {
			browse(url, (Window)null);
		}
	}

	public static void browse(final String url, final Window window) {
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
		JOptionPane.showMessageDialog(window, "Could not browse to:\n" + url, "Browse", JOptionPane.PLAIN_MESSAGE);
	}

	private static class LinkListener implements HyperlinkListener {

		private Program program;

		public LinkListener(Program program) {
			this.program = program;
		}

		@Override
		public void hyperlinkUpdate(final HyperlinkEvent hle) {
			Object o = hle.getSource();
			if (o instanceof JEditorPane) {
				JEditorPane jEditorPane = (JEditorPane) o;
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType()) && jEditorPane.isEnabled()) {
					browse(hle.getURL().toString(), program);
				}
			}
		}
	}
}
