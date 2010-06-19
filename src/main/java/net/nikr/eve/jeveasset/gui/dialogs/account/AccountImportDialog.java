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

package net.nikr.eve.jeveasset.gui.dialogs.account;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.JNumberField;
import net.nikr.eve.jeveasset.gui.shared.JWorking;
import net.nikr.eve.jeveasset.io.online.Online;
import net.nikr.eve.jeveasset.io.eveapi.HumansGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AccountImportDialog extends JDialogCentered implements ActionListener, PropertyChangeListener {

	private final static Logger LOG = LoggerFactory.getLogger(AccountImportDialog.class);

	public final static String ACTION_ADD_KEY_CANCEL = "ACTION_ADD_KEY_CANCEL";
	public final static String ACTION_NEXT = "ACTION_NEXT";
	public final static String ACTION_PREVIOUS = "ACTION_PREVIOUS";

	public final static String TAB_ADD = "TAB_ADD";
	public final static String TAB_VALIDATE = "TAB_VALIDATE";
	public final static String TAB_DONE = "TAB_DONE";

	private AccountManagerDialog apiManager;

	private JTextField jUserId;
	private JTextField jApiKey;
	private JButton jNext;
	private JButton jPrevious;
	private JButton jCancel;
	private CardLayout cardLayout;
	private JPanel jContent;
	private Account account;
	private boolean bEditAccount;

	private DonePanel donePanel;

	private Pattern pattern = Pattern.compile("[\\w\\d]*");

	private int nTabIndex;
	
	public AccountImportDialog(AccountManagerDialog apiManager, Program program) {
		super(program, "Account Import", apiManager.getDialog());
		this.apiManager = apiManager;

		//layout.setAutoCreateGaps(false);

		donePanel = new DonePanel();

		cardLayout = new CardLayout();
		jContent = new JPanel(cardLayout);
		jContent.add(new InputPanel(), TAB_ADD);
		jContent.add(new ValidatePanel(), TAB_VALIDATE);
		jContent.add(donePanel, TAB_DONE);

		jPrevious = new JButton("< Previous");
		jPrevious.setActionCommand(ACTION_PREVIOUS);
		jPrevious.addActionListener(this);

		jNext = new JButton("Next >");
		jNext.setActionCommand(ACTION_NEXT);
		jNext.addActionListener(this);

		jCancel = new JButton("Cancel");
		jCancel.setActionCommand(ACTION_ADD_KEY_CANCEL);
		jCancel.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jContent)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jPrevious, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jNext, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addGap(20)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jContent)
				.addGroup(layout.createParallelGroup()
					.addComponent(jPrevious, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jNext, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	private Integer getUserId(){
		int nUserID = 0;
		try {
			nUserID = Integer.valueOf(jUserId.getText());
		} catch (NumberFormatException ex){
			return 0;
		}
		return nUserID;
	}
	private String getApiKey(){
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
		Transferable transferable = getDialog().getToolkit().getSystemClipboard ().getContents(this);
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
				Integer.valueOf(s);
				if (s.length() >= 3 && s.length() <= 10 && jUserId.isEnabled()){
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
		return jNext;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {
		getClipboardData();
	}

	@Override
	protected void save() {}

	public void show(String userId, String apiKey) {
		jUserId.setText(userId);
		jApiKey.setText(apiKey);
		if (userId.isEmpty() || apiKey.isEmpty()){
			jUserId.setEnabled(true);
			bEditAccount = false;
		} else {
			jUserId.setEnabled(false);
			bEditAccount = true;
		}
		nTabIndex = 0;
		updateTab();
		super.setVisible(true);
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			show("", "");
		} else {
			super.setVisible(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD_KEY_CANCEL.equals(e.getActionCommand())) {
			setVisible(false);
		}
		if (ACTION_PREVIOUS.equals(e.getActionCommand())) {
			nTabIndex = 0;
			updateTab();
		}


		if (ACTION_NEXT.equals(e.getActionCommand())) {
			nTabIndex++;
			updateTab();
		}
	}

	private void updateTab(){
		switch (nTabIndex){
			case 0:
				cardLayout.show(jContent, TAB_ADD);
				jPrevious.setEnabled(false);
				jNext.setEnabled(true);
				jNext.setText("Next >");
				break;
			case 1:
				cardLayout.show(jContent, TAB_VALIDATE);
				jPrevious.setEnabled(true);
				jNext.setEnabled(false);
				jNext.setText("Next >");
				account = new Account(getUserId(), getApiKey());
				ValidateApiKeyTask validateApiKeyTask = new ValidateApiKeyTask();
				validateApiKeyTask.addPropertyChangeListener(this);
				validateApiKeyTask.execute();
				break;
			case 2:
				jPrevious.setEnabled(true);
				jNext.setText("OK");
				cardLayout.show(jContent, TAB_DONE);
				break;
			case 3:
				if (account != null){
					if (bEditAccount){
						List<Account> accounts = program.getSettings().getAccounts();
						for (int a = 0; a < accounts.size(); a++){
							if (accounts.get(a).getUserID() == account.getUserID()){
								accounts.get(a).setApiKey(account.getApiKey());
								break;
							}
						}
					} else {
						apiManager.forceUpdate();
						program.getSettings().getAccounts().add(account);
					}
					apiManager.updateTable();
					this.setVisible(false);
				}
				break;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object o = evt.getSource();
		if (o instanceof ValidateApiKeyTask){
			ValidateApiKeyTask validateApiKeyTask = (ValidateApiKeyTask) o;
			if (validateApiKeyTask.throwable != null){
				LOG.error("Uncaught Exception (SwingWorker): Please email the latest error.txt in the logs directory to niklaskr@gmail.com", validateApiKeyTask.throwable);
			}
			if (validateApiKeyTask.done){
				validateApiKeyTask.done = false;
				if (validateApiKeyTask.result == 10){
					donePanel.setResult("Account already imported");
					donePanel.setText("An existing account can not be imported again\r\n"
							+ "\r\n"
							+ "\r\n"
							+"Press \"Previous\" to retry.");
				}
				if (validateApiKeyTask.result == 20){
					donePanel.setResult("No internet connection");
					donePanel.setText("Please connect to the internet and retry.\r\n"
							+ "\r\n"
							+ "\r\n"
							+ "Press \"Previous\" to retry."
							);
				}
				if (validateApiKeyTask.result == 30){
					donePanel.setResult("Account not valid");
					donePanel.setText("The entered User ID and/or API Key was wrong.\r\n"
							+ "Note: You must enter a full access api key.\r\n"
							+ "\r\n"
							+ "Press \"Previous\" to retry."
							);
				}
				if (validateApiKeyTask.result == 100){
					jNext.setEnabled(true);
					donePanel.setResult("Account Valid");
					donePanel.setText("Tip: To get the new account data select: Menu > Update > Update\r\n"
							+ "	\r\n"
							+ "\r\n"
							+ "Press \"OK\" to import."
							);
				} else {
					jNext.setEnabled(false);
					account = null;
				}
				nTabIndex = 2;
				updateTab();
			}
		}
		
	}

	private class InputPanel extends JCardPanel implements HyperlinkListener{

		public InputPanel() {
			JLabel jUserIdLabel = new JLabel("User ID");
			jUserIdLabel.setHorizontalAlignment(JLabel.RIGHT);

			jUserId = new JNumberField("");
			JCopyPopup.install(jUserId);

			JLabel jApiKeyLabel = new JLabel("API Key");

			jApiKey = new JTextField();
			JCopyPopup.install(jApiKey);
			JEditorPane jHelp = new JEditorPane(
					"text/html", "<html>"
					+ "<div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 11pt;\">"
					+ "Enter a full access api key.<br>"
					+ "You can find your api key at: <a href=\"http://www.eveonline.com/api/default.asp\">http://www.eveonline.com/api/default.asp</a><br>"
					+ "Note: jEveAssets will not work with a limited access api key.<br>"
					+ "</div>"
					);
			jHelp.setFont( this.getFont() );
			jHelp.setEditable(false);
			jHelp.setOpaque(false);
			jHelp.addHyperlinkListener(this);

			cardLayout.setHorizontalGroup(
				cardLayout.createSequentialGroup()
				.addGroup(cardLayout.createParallelGroup()
					.addComponent(jHelp)
					.addGroup(cardLayout.createSequentialGroup()
						.addGroup(cardLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(jUserIdLabel)
							.addComponent(jApiKeyLabel)
						)
						.addGroup(cardLayout.createParallelGroup()
							.addComponent(jUserId, 100, 100, 100)
							.addComponent(jApiKey, 150, 150, Integer.MAX_VALUE)
						)
					)
				)
			);
			cardLayout.setVerticalGroup(
				cardLayout.createSequentialGroup()
				.addComponent(jHelp)
				.addGroup(cardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jUserIdLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jUserId, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(cardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jApiKeyLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jApiKey, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
			);
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

	private class ValidatePanel extends JCardPanel{

		public ValidatePanel() {
			JLabel jHelp = new JLabel("Validating API Key");
			
			JWorking jWorking = new JWorking();

			cardLayout.setHorizontalGroup(
				cardLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addGroup(cardLayout.createSequentialGroup()
						.addGap(10, 10, Integer.MAX_VALUE)
						.addComponent(jWorking)
						.addGap(10, 10, Integer.MAX_VALUE)
					)
					.addGroup(cardLayout.createSequentialGroup()
						.addGap(10, 10, Integer.MAX_VALUE)
						.addComponent(jHelp)
						.addGap(10, 10, Integer.MAX_VALUE)
					)
			);
			cardLayout.setVerticalGroup(
				cardLayout.createSequentialGroup()
					.addGap(10, 10, Integer.MAX_VALUE)
					.addComponent(jWorking)
					.addComponent(jHelp)
					.addGap(10, 10, Integer.MAX_VALUE)
			);
		}
	}

	private class DonePanel extends JCardPanel {

		private JLabel jResult;

		private JEditorPane jHelp;

		public DonePanel() {
			jResult = new JLabel();
			jResult.setFont( new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize()));

			jHelp = new JEditorPane();
			jHelp.setFont( this.getFont() );
			jHelp.setEditable(false);
			jHelp.setOpaque(false);
			jHelp.setFocusable(false);
			jHelp.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(this.getBackground().darker(), 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)) );

			cardLayout.setHorizontalGroup(
				cardLayout.createParallelGroup()
					.addGroup(cardLayout.createSequentialGroup()
						.addGap(5)
						.addComponent(jResult)
					)
					.addComponent(jHelp)
			);
			cardLayout.setVerticalGroup(
				cardLayout.createSequentialGroup()
					.addComponent(jResult)
					.addComponent(jHelp)
			);
		}

		public void setResult(String text){
			jResult.setText(text);
		}

		public void setText(String text){
			jHelp.setText(text);
		}

	}

	private abstract class JCardPanel extends JPanel{

		protected GroupLayout cardLayout;

		public JCardPanel() {
			cardLayout = new GroupLayout(this);
			this.setLayout(cardLayout);
			cardLayout.setAutoCreateGaps(true);
			cardLayout.setAutoCreateContainerGaps(false);
		}


	}


	class ValidateApiKeyTask extends SwingWorker<Void, Void> {

		private int result = 0;
		private boolean done = false;
		private Throwable throwable = null;
		private HumansGetter humansGetter = new HumansGetter();

		@Override
		public Void doInBackground() {
			setProgress(0);
			try {
				boolean ok = !program.getSettings().getAccounts().contains( account );
				if (!ok){
					result = 10;
					return null;
				}
				ok = Online.isOnline(program.getSettings());
				if (!ok){
					result = 20;
					return null;
				}
				humansGetter.load(null, true, account);
				if (humansGetter.hasError()){
					result = 30;
					return null;
				}
				result = 100;
			} catch (Throwable ex) {
				throwable = ex;
				done = false;
			}
			return null;
        }

		@Override
		public void done() {
			done = true;
			setProgress(100);
		}
	}
}