/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.gui.dialogs;

import javax.swing.JComponent;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import net.nikr.eve.jeveasset.Program;


public class AboutDialog extends JDialogCentered implements ActionListener, HyperlinkListener {

	public final static String ACTION_ABOUT_CLOSE = "ACTION_ABOUT_CLOSE";
	private JButton jClose;
	
	public AboutDialog(Program program, Image image) {
		super(program, "About", image);

		JEditorPane jAbout = new JEditorPane("text/html",
				"<html><div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 11pt;\">"
				+ "<font size=\"5\">"+Program.PROGRAM_NAME+" </font>"+Program.PROGRAM_VERSION+"<br>"
				+ "<b>Web:</b> <a href=\"http://eve.nikr.net/?page=jeveasset\">http://eve.nikr.net/?page=jeveasset</a><br>"
				+ "<b>License:</b> <a href=\"http://www.gnu.org/copyleft/gpl.html\">GNU General Public License</a><br>"
				+ "<b>Copyright:</b> &copy; 2009 Niklas Kyster Rasmussen, Flaming Candle<br>"
				+ "<br>"
				+ "<b>Credits:</b><br>"
				+ "<a href=\"http://www.famfamfam.com/lab/icons/silk/\">Silk icons</a> (icons)<br>"
				+ "<a href=\"http://eve-central.com/\">EVE-Central.com</a> (API)<br>"
				+ "<a href=\"http://www.eveonline.com/\">EVE-Online</a> (API and Toolkit)<br> "
				+ "<a href=\"http://publicobject.com/glazedlists/\">Glazed Lists</a> (Table sorting and filtering)<br> "
				+ "<a href=\"http://supercsv.sourceforge.net/\">Super CSV</a> (CSV Export)<br> "
				+ "<a href=\"http://code.google.com/p/eveapi/\">eveapi</a> (Parsing EVE-Online API)<br> "
				+ "<a href=\"http://logging.apache.org/log4j/\">log4j</a> (Used by eveapi)<br> "
				+ "<a href=\"http://commons.apache.org/digester/\">Apache Commons Digester</a> (Used by eveapi)<br> "
				+ "<a href=\"http://commons.apache.org/beanutils/\">Apache Commons BeanUtils</a> (Used by eveapi)<br> "
				+ "<a href=\"http://commons.apache.org/logging/\">Apache Commons Logging</a> (Used by eveapi)<br> "
				+ "<a href=\"http://eve.nikr.net/?page=jeveasset\">NiKR Log</a> (Logging)<br>"
			);
		jAbout.setEditable(false);
		jAbout.setOpaque(false);
		jAbout.addHyperlinkListener(this);
		jPanel.add(jAbout);

		jClose = new JButton("Close");
		jClose.setActionCommand(ACTION_ABOUT_CLOSE);
		jClose.addActionListener(this);
		jPanel.add(jClose);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(jAbout)
				.addComponent(jClose, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
			)

		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jAbout)
				.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}

	@Override
	public JComponent getDefaultFocus() {
		return jClose;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ABOUT_CLOSE.equals(e.getActionCommand())) {
			dialog.setVisible(false);
		}
	}
	
	@Override
	public void hyperlinkUpdate(HyperlinkEvent hle) {
		if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
			try {
				Desktop.getDesktop().browse(new URI(hle.getURL().toString()));
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}
}
