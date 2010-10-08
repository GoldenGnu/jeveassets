/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

/**
 *
 * @author Niklas
 */
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
