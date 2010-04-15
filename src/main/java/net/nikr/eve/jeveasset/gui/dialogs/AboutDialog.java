/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;
import net.nikr.eve.jeveasset.gui.shared.JWait;


public class AboutDialog extends JDialogCentered implements ActionListener, HyperlinkListener, PropertyChangeListener {

	private final static String ACTION_ABOUT_CLOSE = "ACTION_ABOUT_CLOSE";
	private final static String ACTION_UPDATE = "ACTION_UPDATE";

	private JButton jClose;
	private JButton jCheckUpdates;
	private JEditorPane jInfo;
	private JEditorPane jExternal;
	private JWait jWait;
	
	public AboutDialog(Program program, Image image) {
		super(program, "About", image);

		jWait = new JWait(this.getDialog());

		JLabel jIcon = new JLabel();
		jIcon.setIcon( ImageGetter.getIcon("icon07_13.png") );

		JEditorPane jProgram = createEditorPane(
				"<div style=\"font-size: 30pt;\"><b>"+Program.PROGRAM_NAME+"</b></div>"
				+ "Copyright &copy; 2009, 2010 Contributors<br>"
				);


		jInfo = createEditorPane(
				  "<b>Version</b><br>"
				+ "&nbsp;"+Program.PROGRAM_VERSION+"<br>"
				+ "<br>"
				+ "<b>Contributors</b><br>"
				+ "&nbsp;Niklas Kyster Rasmussen<br>"
				+ "&nbsp;Flaming Candle<br>"
				+ "&nbsp;Jochen Bedersdorfer<br>"
				+ "<br>"
				+ "<b>www</b><br>"
				+ "&nbsp;<a href=\"http://eve.nikr.net/?page=jeveasset\">Homepage</a> (download and source)<br>"
				+ "&nbsp;<a href=\"http://code.google.com/p/jeveassets/\">Google Code Project</a> (developers)<br>"
				+ "&nbsp;<a href=\"http://www.eveonline.com/iNgameboard.asp?a=topic&threadID=1103224/\">Forum Thread</a> (feedback)<br>"
				+ "<br>"
				+ "<b>License</b><br>"
				+ "&nbsp;<a href=\"http://www.gnu.org/licenses/old-licenses/gpl-2.0.html\">GNU General Public License 2.0</a><br>"
				+ "<br>"
				);
		jInfo.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(jPanel.getBackground().darker(), 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)) );

		jExternal = createEditorPane(
				  "<b>Content</b><br>"
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
				+ "&nbsp;<a href=\"http://eve.nikr.net/?page=jeveasset\">OSXAdapter</a> (native mac os x support)<br>"
				+ "&nbsp;<a href=\"http://eve.nikr.net/?page=jeveasset\">Pricing</a> (parsing price data api)<br>"
				+ "&nbsp;<a href=\"http://eve.nikr.net/?page=jeveasset\">Routing</a> (routing tool)<br>"
				+ "&nbsp;<a href=\"http://eve.nikr.net/?page=jeveasset\">NiKR Log</a> (logging)<br>"
				+ "<br>"
				);
		jExternal.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(jPanel.getBackground().darker(), 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)) );
		
		jClose = new JButton("Close");
		jClose.setActionCommand(ACTION_ABOUT_CLOSE);
		jClose.addActionListener(this);

		jCheckUpdates = new JButton("Check for updates");
		jCheckUpdates.setActionCommand(ACTION_UPDATE);
		jCheckUpdates.addActionListener(this);

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
					.addGap(20)
					.addComponent(jExternal)
				)
				.addComponent(jCheckUpdates)
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
				.addGroup(layout.createParallelGroup()
					.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCheckUpdates, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	private void setEnabledAll(boolean b){
		jClose.setEnabled(b);
		jCheckUpdates.setEnabled(b);
		jInfo.setEnabled(b);
		jExternal.setEnabled(b);
	}

	private JEditorPane createEditorPane(String text){
		JEditorPane jEditorPane = new JEditorPane("text/html",
				"<html><div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 11pt;\">"
				+text
				+ "</div>"
				);
		jEditorPane.setEditable(false);
		jEditorPane.setOpaque(false);
		jEditorPane.addHyperlinkListener(this);
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
	protected void windowShown() {
		setEnabledAll(true);
	}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ABOUT_CLOSE.equals(e.getActionCommand())) {
			dialog.setVisible(false);
		}
		if (ACTION_UPDATE.equals(e.getActionCommand())){
			jWait.showWaitDialog("Checking for new program updates");
			setEnabledAll(false);
			CheckProgramUpdate checkProgramUpdate = new CheckProgramUpdate();
			checkProgramUpdate.addPropertyChangeListener(this);
			checkProgramUpdate.execute();
			
		}
	}
	
	@Override
	public void hyperlinkUpdate(HyperlinkEvent hle) {
		Object o = hle.getSource();
		if (o instanceof JEditorPane){
			JEditorPane jEditorPane = (JEditorPane) o;
			if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType()) && jEditorPane.isEnabled()) {
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object o = evt.getSource();
		if (o instanceof SwingWorker){
			SwingWorker swingWorker = (SwingWorker) o;
			if (swingWorker.isDone()){
				setEnabledAll(true);
				jWait.hideWaitDialog();
				
			}
		}

	}

	private class CheckProgramUpdate extends SwingWorker<Void, Void>{

		@Override
		protected Void doInBackground() throws Exception {
			program.checkForProgramUpdates(getDialog());
			return null;
		}

	}
}
