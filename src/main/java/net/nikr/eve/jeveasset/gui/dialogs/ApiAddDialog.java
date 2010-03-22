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

import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import java.awt.Desktop;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.JNumberField;


public class ApiAddDialog extends JDialogCentered implements ActionListener, HyperlinkListener {

	public final static String ACTION_ADD_KEY_CANCEL = "ACTION_ADD_KEY_CANCEL";
	public final static String ACTION_ADD_KEY_OK = "ACTION_ADD_KEY_OK";

	private ApiManagerDialog apiManager;

	private JTextField jUserId;
	private JTextField jApiKey;
	private JButton jOk;
	private JButton jCancel;

	private Pattern pattern = Pattern.compile("[\\w\\d]*");
	
	public ApiAddDialog(ApiManagerDialog apiManager, Program program) {
		super(program, "Add Api Key", apiManager);
		this.apiManager = apiManager;

		JLabel jUserIdLabel = new JLabel("User ID");
		jUserIdLabel.setHorizontalAlignment(JLabel.RIGHT);
		jPanel.add(jUserIdLabel);

		jUserId = new JNumberField("");
		JCopyPopup.install(jUserId);
		jPanel.add(jUserId);

		JLabel jApiKeyLabel = new JLabel("API Key");
		jPanel.add(jApiKeyLabel);

		jApiKey = new JTextField();
		JCopyPopup.install(jApiKey);
		jPanel.add(jApiKey);
		JEditorPane jHelp = new JEditorPane(
				"text/html", "<html>"
				+ "<div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 11pt;\">"
				+ "Enter Full Access API Key.<br>"
				+ "You can find your api key at: <a href=\"http://www.eveonline.com/api/default.asp\">http://www.eveonline.com/api/default.asp</a><br>"
				+ "Note: jEveAssets will not work with a Limited Access API Key.<br>"
				+ "</div>"
				);
		jHelp.setFont( this.getDialog().getFont() );
		jHelp.setEditable(false);
		jHelp.setOpaque(false);
		jHelp.addHyperlinkListener(this);
		jPanel.add(jHelp);

		jOk = new JButton("OK");
		jOk.setActionCommand(ACTION_ADD_KEY_OK);
		jOk.addActionListener(this);
		jPanel.add(jOk);

		jCancel = new JButton("Cancel");
		jCancel.setActionCommand(ACTION_ADD_KEY_CANCEL);
		jCancel.addActionListener(this);
		jPanel.add(jCancel);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup()
				.addComponent(jHelp)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jUserIdLabel)
						.addComponent(jApiKeyLabel)
					)
					.addGroup(layout.createParallelGroup()
							.addComponent(jUserId, 100, 100, 100)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(jApiKey, 150, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addGroup(layout.createSequentialGroup()
										.addComponent(jOk, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
										.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
									)
								)
							)
					)
				)
			)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
			.addComponent(jHelp)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jUserIdLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jUserId, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
			)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jApiKeyLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jApiKey, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
			)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jOk, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
			)
		);
	}
	
	public void setEnabledAll(boolean b){
		jUserId.setEnabled(b);
		jApiKey.setEnabled(b);
		jOk.setEnabled(b);
		jCancel.setEnabled(b);
	}

	public Integer getUserId(){
		int nUserID = 0;
		try {
			nUserID = Integer.valueOf(jUserId.getText());
		} catch (NumberFormatException ex){
			return 0;
		}
		return nUserID;
	}
	public String getApiKey(){
		return jApiKey.getText();
	}

	private void getClipboardData(){
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			try {
				sm.checkSystemClipboardAccess();
			} catch (Exception ex) {
				return;
			}
		}
		String s = null;
		Transferable transferable = dialog.getToolkit().getSystemClipboard ().getContents(this);
		try {
			s = (String) transferable.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException ex) {
			return;
		} catch (IOException ex) {
			return;
		}
		if (s != null){
			s = s.trim();
			try{
				int number = Integer.valueOf(s);
				if (s.length() >= 3 && s.length() <= 10){
					jUserId.setText(s);
				}
				return;
			} catch (NumberFormatException ex){
				Matcher matcher = pattern.matcher(s);
				if (s.length() == 64 && matcher.matches()){
					jApiKey.setText(s);
				}
			}
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jUserId;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOk;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {
		getClipboardData();
	}

	@Override
	protected void save() {
		setEnabledAll(false);
		apiManager.saveApiKey();
		//setVisible(false);
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			jUserId.setText("");
			jApiKey.setText("");
			setEnabledAll(true);
		}
		super.setVisible(b);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD_KEY_CANCEL.equals(e.getActionCommand())) {
			setVisible(false);
		}
		if (ACTION_ADD_KEY_OK.equals(e.getActionCommand())) {
			save();
		}

	}
}