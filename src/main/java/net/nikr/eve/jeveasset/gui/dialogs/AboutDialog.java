/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Colors;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.DialoguesAbout;
import net.nikr.eve.jeveasset.io.online.Updater;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;


public class AboutDialog extends JDialogCentered {

	private enum AboutAction {
		CLOSE
	}

	private final JButton jClose;

	public AboutDialog(final Program program) {
		super(program, DialoguesAbout.get().about(), Images.DIALOG_ABOUT.getImage());

		ListenerClass listener = new ListenerClass();

		JLabel jIcon = new JLabel();
		jIcon.setIcon(Images.MISC_ASSETS_64.getIcon());

		JEditorPane jProgram = createEditorPane(false,
				"<div style=\"font-size: 30pt;\"><b>" + Program.PROGRAM_NAME + "</b></div>"
				+ "Copyright &copy; 2009-2023 Contributors<br>"
				);

		String s = Updater.getPackageMaintainers();
		StringBuilder packageManager = new StringBuilder();
		if (s != null) {
			String[] names = s.split(",");
			packageManager.append("<b>Package Maintainers</b><br>");
			for (String name : names) {
				packageManager.append("&nbsp;");
				packageManager.append(name.trim());
				packageManager.append("<br>");
			}
			packageManager.append("<br>");
		}

		JEditorPane jInfo = createEditorPane(
				"<b>Version</b><br>"
				+ "&nbsp;" + Program.PROGRAM_VERSION + "<br>"
				+ "<br>"
				+ "<b>Data</b><br>"
				+ "&nbsp;" + program.getProgramDataVersion() + "<br>"
				+ "<br>"
				+ "<b>License</b><br>"
				+ "&nbsp;<a href=\"http://www.gnu.org/licenses/old-licenses/gpl-2.0.html\">GNU General Public License 2.0</a><br>"
				+ "<br>"
				+ "<b>www</b><br>"
				+ "&nbsp;<a href=\"" + Program.PROGRAM_HOMEPAGE + "\">Homepage</a> (download and source)<br>"
				+ "&nbsp;<a href=\"https://github.com/GoldenGnu/jeveassets\">GitHub Project</a> (developers)<br>"
				+ "&nbsp;<a href=\"https://forums.eveonline.com/t/13255\">Forum Thread</a> (feedback)<br>"
				+ "&nbsp;<a href=\"https://wiki.jeveassets.org\">Wiki</a> (user documentation)<br>"
				+ "<br>"
				+ "<b>Developers</b><br>"
				+ "&nbsp;Niklas Kyster Rasmussen (Golden Gnu)<br>"
				+ "&nbsp;Dultas<br>"
				+ "<br>"
				+ "<b>Testers</b><br>"
				+ "&nbsp;Huzid<br>"	
				+ "<br>"
				+ "<b>Contributors</b><br>"
				+ "&nbsp;Flaming Candle<br>"
				+ "&nbsp;Jochen Bedersdorfer<br>"
				+ "&nbsp;TryfanMan<br>"
				+ "&nbsp;Jan<br>"
				+ "&nbsp;Ima Sohmbadi<br>"
				+ "&nbsp;Saulvin<br>"
				+ "&nbsp;AnrDaemon<br>"
				+ "&nbsp;Madetara (Ray Kavier)<br>"
				+ "&nbsp;Kaylee Syntax<br>"
				+ "&nbsp;Inoruuk<br>"
				+ "&nbsp;Burberius<br>"
				+ "&nbsp;Lazaren<br>"
				+ "&nbsp;Boran Lordsworth<br>"
				+ "<br>"
				+ "<b>Retired Testers</b><br>"
				+ "&nbsp;Varo Jan<br>"
				+ "&nbsp;Scrapyard Bob<br>"
				+ "&nbsp;Johann Hemphill<br>"
				+ "&nbsp;Tomasz (kitsibas) Wiktorski<br>"
				+ "<br>"
				+ packageManager.toString()
				+ "<b>Special Thanks</b><br>"
				+ "&nbsp;jEveAssets is heavily based on the user interface in EVE Asset Manager by William J. Rogers<br>"
				+ "<br>"
				+ "<b>Content</b><br>"
				+ "&nbsp;<a href=\"https://www.eveonline.com/\">EVE-Online</a> (api and sde)<br> "
				+ "&nbsp;<a href=\"https://market.fuzzwork.co.uk/api/\">fuzzwork.co.uk</a> (price data api)<br>"
				+ "&nbsp;<a href=\"https://evemarketer.com/\">EveMarketer.com</a> (price data api)<br>"
				+ "&nbsp;<a href=\"https://janice.e-351.com/api/rest/docs/index.html\">Janice</a> (price data api)<br>"
				+ "&nbsp;<a href=\"https://github.com/zKillboard/zKillboard/wiki/API-(Prices)\">zKillboard</a> (price history api)<br>"
				+ "&nbsp;<a href=\"http://www.famfamfam.com/lab/icons/silk/\">Silk icons</a> (icons)<br>"
				+ "<br>"
				+ "<b>Libraries</b><br>"
				+ "&nbsp;<a href=\"http://publicobject.com/glazedlists/\">Glazed Lists</a> (table sorting and filtering)<br> "
				+ "&nbsp;<a href=\"http://super-csv.github.io/super-csv/\">Super CSV</a> (csv export)<br> "
				+ "&nbsp;<a href=\"https://github.com/burberius/eve-esi\">eve-esi</a> (esi endpoints)<br> "
				+ "&nbsp;<a href=\"https://github.com/LGoodDatePicker/LGoodDatePicker\">LGoodDatePicker</a> (date input)<br> "
				+ "&nbsp;<a href=\"https://www.jfree.org/jfreechart/\">JFreeChart</a> (charts)<br> "
				+ "&nbsp;<a href=\"https://junit.org\">JUnit</a> (unit testing)<br>"
				+ "&nbsp;<a href=\"https://www.slf4j.org/\">slf4J</a> (logging)<br>"
				+ "&nbsp;<a href=\"https://logback.qos.ch/\">logback</a> (logging)<br>"
				+ "&nbsp;<a href=\"https://github.com/xerial/sqlite-jdbc\">SQLite</a> (database)<br>"
				+ "&nbsp;<a href=\"https://www.formdev.com/flatlaf\">FlatLaf</a> (look and feel and native mac os x support)<br>"
				+ "&nbsp;<a href=\"https://github.com/GoldenGnu/price\">Pricing</a> (parsing price data api)<br>"
				+ "&nbsp;<a href=\"https://github.com/GoldenGnu/routing\">Routing</a> (routing tool)<br>"
				+ "&nbsp;<a href=\"https://github.com/GoldenGnu/graph\">Graph</a> (routing tool)<br>"
				+ "&nbsp;<a href=\"https://github.com/GoldenGnu/translations\">Translations</a> (i18n)<br>"
				+ "&nbsp;<a href=\"https://github.com/uklimaschewski/EvalEx\">EvalEx</a> (formula columns)<br>"
				+ "&nbsp;<a href=\"https://picocli.info\">Picocli</a> (CLI)<br>"
				+ "<br>"
				+ "<center><a href=\"https://cultofthepartyparrot.com/\"><img border=\"0\" src=\"" + getPartyParrot() +"\" /></a><br></center>"
				);

		JScrollPane jInfoScroll = new JScrollPane(jInfo, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JLabel jPartner = new JLabel();
		jPartner.setIcon(Images.MISC_PARTNER.getIcon());

		jClose = new JButton(DialoguesAbout.get().close());
		jClose.setActionCommand(AboutAction.CLOSE.name());
		jClose.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jIcon)
					.addComponent(jProgram)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(jInfoScroll)
					.addComponent(jPartner, 300, 300, 300)
					.addComponent(jClose, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jIcon)
					.addComponent(jProgram)
				)
				.addComponent(jInfoScroll, 300, 300, 300)
				.addComponent(jPartner, 50, 50, 50)
				.addComponent(jClose, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}

	private String getPartyParrot() {
		URL url = Images.class.getResource(Images.MISC_PARTYPARROT.getFilename());
		if (url != null) {
			return url.toString();
		}
		return "";
	}

	private JEditorPane createEditorPane(final String text) {
		return createEditorPane(true, text);
	}

	private JEditorPane createEditorPane(final boolean addBorder, final String text) {
		JEditorPane jEditorPane = new JEditorPane("text/html",
				"<html><div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 11pt; white-space: nowrap;\">"
				+ text
				+ "</div>"
				);
		jEditorPane.setEditable(false);
		jEditorPane.setFocusable(false);
		jEditorPane.setOpaque(false);
		jEditorPane.setBackground(Colors.COMPONENT_TRANSPARENT.getColor());
		jEditorPane.setFont(jPanel.getFont());
		jEditorPane.addHyperlinkListener(DesktopUtil.getHyperlinkListener(getDialog()));
		if (addBorder) {
			jEditorPane.setBorder(BorderFactory.createEmptyBorder(11, 11, 11, 11));
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
