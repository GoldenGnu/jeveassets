/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class DesktopUtil {

	private static final Logger LOG = LoggerFactory.getLogger(DesktopUtil.class);

	private DesktopUtil() { }

	public static HyperlinkListener getHyperlinkListener(Window window) {
		return new LinkListener(window);
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

	
	/**
	 * Open a single link
	 * @param url 
	 * @param program 
	 */
	public static void browse(final String url, Program program) {
		browse(url, getWindow(program));
	}

	/**
	 * Open a single link
	 * @param url
	 * @param window 
	 * @return  
	 */
	public static boolean browse(final String url, final Window window) {
		if (url == null) {
			return false;
		}
		if (isSupported(Desktop.Action.BROWSE)) {
			if (browse(url)) {
				return true;
			} else {
				JOptionPane.showMessageDialog(window, "Could not browse to:\r\n" + url, "Browse", JOptionPane.PLAIN_MESSAGE);
				return false;
			}
		} else {
			int value = JOptionPane.showConfirmDialog(window, "Automatic browsing is not support on this platform.\r\n"
					+ "1) Press OK to copy the URL into your clipboard.\r\n"
					+ "2) Open your browser and paste the url into the address line.", "Browse", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (value == JOptionPane.OK_OPTION) {
				StringSelection selection = new StringSelection(url);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);
				return true;
			} else {
				return false;
			}
			
		}
	}

	/**
	 * Open multiple links
	 * @param urls 
	 * @param program 
	 */
	public static void browse(final Set<String> urls, Program program) {
		browse(urls, getWindow(program));
	}

	/**
	 * Open multiple links
	 * @param urls
	 * @param window 
	 */
	private static void browse(final Set<String> urls, Window window) {
		if (urls == null || urls.isEmpty()) {
			return;
		}
		if (isSupported(Desktop.Action.BROWSE)) {
			if (urls.size() > 1) {
				int value = JOptionPane.showConfirmDialog(window, GuiShared.get().openLinks(urls.size()), GuiShared.get().openLinksTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if(value != JOptionPane.OK_OPTION) {
					return;
				}
			}
			for (String url : urls) {
				if (!browse(url)) {
					JOptionPane.showMessageDialog(window, "Could not browse to:\r\n" + url, "Browse", JOptionPane.PLAIN_MESSAGE);
					break;
				}
			}
		} else {
			StringBuilder builder = new StringBuilder();
			for (String url : urls) {
				builder.append(url);
				builder.append("\r\n");
			}
			JOptionPane.showMessageDialog(window, "Automatic browsing is not support on this platform.\r\n"
					+ "You need to copy and paste the URLs the into your browser.\r\n"
					+ "Press OK to show the URLs.", "Browse", JOptionPane.PLAIN_MESSAGE);
			JTextDialog jTextDialog = new JTextDialog(window);
			jTextDialog.exportText(builder.toString());
		}
	}

	private static Window getWindow(Program program) {
		if (program != null) {
			return program.getMainWindow().getFrame();
		} else {
			return null;
		}
	}

	private static boolean browse(final String url) {
		if (url == null) {
			return false;
		}
		LOG.info("Browsing: " + url);
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException ex) {
			LOG.warn("	Browsing Failed: " + ex.getMessage());
			return false;
		}
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.browse(uri);
			return true;
		} catch (IOException ex) {
			LOG.warn("	Browsing Failed: " + ex.getMessage());
			return false;
		}
	}

	private static class LinkListener implements HyperlinkListener {

		private final Window window;

		public LinkListener(Window window) {
			this.window = window;
		}

		@Override
		public void hyperlinkUpdate(final HyperlinkEvent hle) {
			Object o = hle.getSource();
			if (o instanceof JEditorPane) {
				JEditorPane jEditorPane = (JEditorPane) o;
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType()) && jEditorPane.isEnabled()) {
					browse(hle.getURL().toString(), window);
				}
			}
		}
	}
}
