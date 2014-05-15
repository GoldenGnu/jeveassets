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

package net.nikr.eve.jeveasset.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow;
import net.nikr.eve.jeveasset.i18n.DialoguesAbout;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;


public class AboutDialog extends JDialogCentered {

	private enum AboutAction {
		CLOSE
	}

	private JButton jClose;
	private JLockWindow jLockWindow;

	public AboutDialog(final Program program) {
		super(program, DialoguesAbout.get().about(), Images.DIALOG_ABOUT.getImage());

		jLockWindow = new JLockWindow(this.getDialog());
		ListenerClass listener = new ListenerClass();

		JLabel jIcon = new JLabel();
		jIcon.setIcon(Images.MISC_ASSETS_64.getIcon());

		JEditorPane jProgram = createEditorPane(false,
				"<div style=\"font-size: 30pt;\"><b>" + Program.PROGRAM_NAME + "</b></div>"
				+ "Copyright &copy; 2009-2014 Contributors<br>"
				);

		JEditorPane jInfo = createEditorPane(
				  "<b>Version</b><br>"
				+ "&nbsp;" + Program.PROGRAM_VERSION + " (" + program.getProgramDataVersion() + ")<br>"
				+ "<br>"
				+ "<b>Contributors</b><br>"
				+ "&nbsp;Niklas Kyster Rasmussen<br>"
				+ "&nbsp;Flaming Candle<br>"
				+ "&nbsp;Jochen Bedersdorfer<br>"
				+ "&nbsp;TryfanMan<br>"
				+ "&nbsp;Jan<br>"
				+ "&nbsp;Ima Sohmbadi<br>"
				+ "&nbsp;Saulvin<br>"
				+ "<br>"
				+ "<b>License</b><br>"
				+ "&nbsp;<a href=\"http://www.gnu.org/licenses/old-licenses/gpl-2.0.html\">GNU General Public License 2.0</a><br>"
				+ "<br>"
				+ "<b>www</b><br>"
				+ "&nbsp;<a href=\"" + Program.PROGRAM_HOMEPAGE + "\">Homepage</a> (download and source)<br>"
				+ "&nbsp;<a href=\"http://code.google.com/p/jeveassets/\">Google Code Project</a> (developers)<br>"
				+ "&nbsp;<a href=\"https://forums.eveonline.com/default.aspx?g=posts&t=6419\">Forum Thread</a> (feedback)<br>"
				+ "<br>"
				+ "<br>"
				);

		JEditorPane jExternal = createEditorPane(
				  "<b>Content</b><br>"
				+ "&nbsp;<a href=\"http://www.eveonline.com/\">EVE-Online</a> (api and toolkit)<br> "
				+ "&nbsp;<a href=\"http://eve-central.com/\">EVE-Central.com</a> (price data api)<br>"
				+ "&nbsp;<a href=\"http://eve-marketdata.com/\">EVE-Marketdata.com</a> (price data api)<br>"
				+ "&nbsp;<a href=\"http://www.evemarketeer.com/\">EveMarketeer.com</a> (price data api)<br>"
				+ "&nbsp;<a href=\"http://eve.addicts.nl/\">Eve.Addicts.nl</a> (price data api)<br>"
				+ "&nbsp;<a href=\"http://www.famfamfam.com/lab/icons/silk/\">Silk icons</a> (icons)<br>"
				+ "<br>"
				+ "<b>Libraries</b><br>"
				+ "&nbsp;<a href=\"http://publicobject.com/glazedlists/\">Glazed Lists</a> (table sorting and filtering)<br> "
				+ "&nbsp;<a href=\"http://supercsv.sourceforge.net/\">Super CSV</a> (csv export)<br> "
				+ "&nbsp;<a href=\"http://code.google.com/p/eveapi/\">eveapi</a> (parsing eve-online api)<br> "
				+ "&nbsp;<a href=\"http://www.toedter.com/en/jcalendar/\">JCalendar</a> (date input)<br> "
				+ "&nbsp;<a href=\"http://www.jfree.org/jfreechart/\">JFreeChart</a> (charts)<br> "
				+ "&nbsp;<a href=\"http://junit.sourceforge.net/\">JUnit</a> (unit testing)<br>"
				+ "&nbsp;<a href=\"http://www.slf4j.org/\">slf4J</a> (logging)<br>"
				+ "&nbsp;<a href=\"http://logging.apache.org/log4j/1.2/\">log4j</a> (logging)<br>"
				+ "&nbsp;<a href=\"" + Program.PROGRAM_HOMEPAGE + "\">OSXAdapter</a> (native mac os x support)<br>"
				+ "&nbsp;<a href=\"" + Program.PROGRAM_HOMEPAGE + "\">Pricing</a> (parsing price data api)<br>"
				+ "&nbsp;<a href=\"" + Program.PROGRAM_HOMEPAGE + "\">Routing</a> (routing tool)<br>"
				+ "&nbsp;<a href=\"" + Program.PROGRAM_HOMEPAGE + "\">Translations</a> (i18n)<br>"
				//+ "<br>"
				);

		JEditorPane jThanks =  createEditorPane(
				"<b>Special Thanks</b><br>"
				+ "&nbsp;jEveAssets is heavily based on the user interface in <a href=\"http://wiki.heavyduck.com/EveAssetManager\">EVE Asset Manager</a>");

		jClose = new JButton(DialoguesAbout.get().close());
		jClose.setActionCommand(AboutAction.CLOSE.name());
		jClose.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jIcon)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jProgram)
						.addComponent(jClose, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jInfo)
					.addGap(10)
					.addComponent(jExternal)
				)
				.addComponent(jThanks)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jIcon)
					.addComponent(jProgram)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jInfo)
					.addComponent(jExternal)
				)
				.addComponent(jThanks)
				.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}

	private JEditorPane createEditorPane(final String text) {
		return createEditorPane(true, text);
	}

	private JEditorPane createEditorPane(final boolean addBorder, final String text) {
		JEditorPane jEditorPane = new JEditorPane("text/html",
				"<html><div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 11pt;\">"
				+ text
				+ "</div>"
				);
		jEditorPane.setEditable(false);
		jEditorPane.setOpaque(false);
		jEditorPane.addHyperlinkListener(DesktopUtil.getHyperlinkListener(program));
		if (addBorder) {
			jEditorPane.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(jPanel.getBackground().darker(), 1),
					BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		}
		return jEditorPane;
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
	protected void save() { }

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (AboutAction.CLOSE.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
		}
	}

}
