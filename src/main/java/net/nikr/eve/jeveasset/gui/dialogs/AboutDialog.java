/*
 * Copyright 2009, 2010
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
import javax.swing.JLabel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;


public class AboutDialog extends JDialogCentered implements ActionListener, HyperlinkListener {

	public final static String ACTION_ABOUT_CLOSE = "ACTION_ABOUT_CLOSE";
	private JButton jClose;
	
	public AboutDialog(Program program, Image image) {
		super(program, "About", image);
		JLabel jIcon = new JLabel();
		jIcon.setIcon( ImageGetter.getIcon("icon07_13.png") );

		JEditorPane jProgram = new JEditorPane("text/html",
				"<html><div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 11pt;\">"
				+ "<div style=\"font-size: 14pt;\"><b>"+Program.PROGRAM_NAME+"</b></div>"
				+ "Version "+Program.PROGRAM_VERSION+"<br>"
				+ "Copyright &copy; 2009, 2010 Contributors<br>"
				+ "<a href=\"http://eve.nikr.net/?page=jeveasset\">http://eve.nikr.net/?page=jeveasset</a><br>"
				+ "</div>"
			);
		jProgram.addHyperlinkListener(this);
		jProgram.setEditable(false);
		jProgram.setOpaque(false);

		JEditorPane jCredits = new JEditorPane("text/html",
				"<html><div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 11pt;\">"
				+ "<b>Contributors</b><br>"
				+ "&nbsp;Niklas Kyster Rasmussen (Original creator)<br>"
				+ "&nbsp;Flaming Candle (Developer)<br>"
				+ "<br>"
				+ "<b>License</b><br>"
				+ "&nbsp;<a href=\"http://www.gnu.org/licenses/old-licenses/gpl-2.0.html\">GNU General Public License 2.0</a><br>"
				+ "<br>"
				+ "<b>Content</b><br>"
				+ "&nbsp;<a href=\"http://www.famfamfam.com/lab/icons/silk/\">Silk icons</a> (icons)<br>"
				+ "&nbsp;<a href=\"http://www.eveonline.com/\">EVE-Online</a> (api and toolkit)<br> "
				+ "&nbsp;<a href=\"http://eve-central.com/\">EVE-Central.com</a> (price data api)<br>"
				+ "&nbsp;<a href=\"http://www.eve-metrics.com\">EVE-Metrics.com</a> (price data api)<br>"
				+ "<br>"
				+ "<b>Libraries</b><br>"
				+ "&nbsp;<a href=\"http://publicobject.com/glazedlists/\">Glazed Lists</a> (table sorting and filtering)<br> "
				+ "&nbsp;<a href=\"http://supercsv.sourceforge.net/\">Super CSV</a> (csv export)<br> "
				+ "&nbsp;<a href=\"http://code.google.com/p/eveapi/\">eveapi</a> (parsing eve-online api)<br> "
				+ "&nbsp;<a href=\"http://junit.sourceforge.net/\">JUnit</a> (unit tesing)<br>"
				+ "&nbsp;<a href=\"http://eve.nikr.net/?page=jeveasset\">Pricing</a> (parsing price data api)<br>"
				+ "&nbsp;<a href=\"http://eve.nikr.net/?page=jeveasset\">Routing</a> (routing tool)<br>"
				+ "&nbsp;<a href=\"http://eve.nikr.net/?page=jeveasset\">NiKR Log</a> (logging)<br>"
				+ "</div>"
				
			);
		jCredits.setEditable(false);
		jCredits.setOpaque(false);
		jCredits.addHyperlinkListener(this);

		jClose = new JButton("Close");
		jClose.setActionCommand(ACTION_ABOUT_CLOSE);
		jClose.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jIcon)
					.addComponent(jProgram)
				)
				.addComponent(jCredits)
				.addComponent(jClose, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
			)

		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jIcon)
					.addComponent(jProgram)
				)
				.addComponent(jCredits)
				.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}

	@Override
	public JComponent getDefaultFocus() {
		return jClose;
	}

	@Override
	protected JButton getDefaultButton() {
		return jClose;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {}

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
