/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.IOException;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.JNumberField;
import net.nikr.eve.jeveasset.gui.shared.JWorking;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;
import net.nikr.eve.jeveasset.io.online.Online;
import net.nikr.eve.jeveasset.io.eveapi.HumansGetter;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AccountImportDialog extends JDialogCentered {

	private final static Logger LOG = LoggerFactory.getLogger(AccountImportDialog.class);

	// TODO (Candle, 2010-09-13) more string enum pattern, to be converted to an enum
	public final static String ACTION_ADD_KEY_CANCEL = "ACTION_ADD_KEY_CANCEL";
	public final static String ACTION_NEXT = "ACTION_NEXT";
	public final static String ACTION_PREVIOUS = "ACTION_PREVIOUS";

	// TODO (Candle, 2010-09-13) more string enum pattern, to be converted to an enum
	public final static String TAB_ADD = "TAB_ADD";
	public final static String TAB_VALIDATE = "TAB_VALIDATE";
	public final static String TAB_DONE = "TAB_DONE";

	private AccountManagerDialog apiManager;
	
	private enum Result{
		FAIL_ALREADY_IMPORTED,
		FAIL_NO_INTERNET,
		FAIL_NOT_VALID,
		FAIL_NO_ACCESS,
		OK_LIMITED_ACCESS,
		OK_ACCOUNT_VALID
	}

	private JTextField jKeyID;
	private JTextField jVCode;
	private JButton jNext;
	private JButton jPrevious;
	private JButton jCancel;
	private CardLayout cardLayout;
	private JPanel jContent;
	private Account account;
	private boolean bEditAccount;
	private ListenerClass listener;

	private DonePanel donePanel;

	private Pattern pattern = Pattern.compile("[\\w\\d]*");

	private int nTabIndex;
	
	public AccountImportDialog(AccountManagerDialog apiManager, Program program) {
		super(program, DialoguesAccount.get().dialogueNameAccountImport(), apiManager.getDialog());
		this.apiManager = apiManager;

		listener = new ListenerClass();

		//layout.setAutoCreateGaps(false);

		donePanel = new DonePanel();

		cardLayout = new CardLayout();
		jContent = new JPanel(cardLayout);
		jContent.add(new InputPanel(), TAB_ADD);
		jContent.add(new ValidatePanel(), TAB_VALIDATE);
		jContent.add(donePanel, TAB_DONE);

		jPrevious = new JButton(DialoguesAccount.get().previousArrow());
		jPrevious.setActionCommand(ACTION_PREVIOUS);
		jPrevious.addActionListener(listener);

		jNext = new JButton(DialoguesAccount.get().nextArrow());
		jNext.setActionCommand(ACTION_NEXT);
		jNext.addActionListener(listener);

		jCancel = new JButton(DialoguesAccount.get().cancel());
		jCancel.setActionCommand(ACTION_ADD_KEY_CANCEL);
		jCancel.addActionListener(listener);

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

	private int getKeyID(){
		int keyID = 0;
		try {
			keyID = Integer.valueOf(jKeyID.getText());
		} catch (NumberFormatException ex){
			return 0;
		}
		return keyID;
	}
	private String getVCode(){
		return jVCode.getText();
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
				if (s.length() >= 3 && s.length() <= 10 && jKeyID.isEnabled()){
					jKeyID.setText(s);
				}
				return;
			} catch (NumberFormatException ex){
				Matcher matcher = pattern.matcher(s);
				if (matcher.matches()){
					jVCode.setText(s);
				}
			}
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jKeyID;
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
		jKeyID.setText(userId);
		jVCode.setText(apiKey);
		bEditAccount = (!userId.isEmpty() && !apiKey.isEmpty());
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

	
	private void showAddTap(){
		cardLayout.show(jContent, TAB_ADD);
		jPrevious.setEnabled(false);
		jNext.setEnabled(true);
		jNext.setText(DialoguesAccount.get().nextArrow());
	}
	
	private void showValidateTab(){
		cardLayout.show(jContent, TAB_VALIDATE);
		jPrevious.setEnabled(true);
		jNext.setEnabled(false);
		jNext.setText(DialoguesAccount.get().nextArrow());
		account = new Account(getKeyID(), getVCode());
		ValidateApiKeyTask validateApiKeyTask = new ValidateApiKeyTask();
		validateApiKeyTask.addPropertyChangeListener(listener);
		validateApiKeyTask.execute();
	}
	
	private void showDoneTab(){
		jPrevious.setEnabled(true);
		jNext.setText(DialoguesAccount.get().ok());
		cardLayout.show(jContent, TAB_DONE);
	}
	
	private void doDone(){
		if (account != null){
			if (bEditAccount){ //update account
				List<Account> accounts = program.getSettings().getAccounts();
				for (int a = 0; a < accounts.size(); a++){
					if (accounts.get(a).getKeyID() == account.getKeyID()){
						accounts.get(a).setVCode(account.getVCode());
						break;
					}
				}
			} else { //add new account
				apiManager.forceUpdate();
				program.getSettings().getAccounts().add(account);
			}
			apiManager.updateTable();
			this.setVisible(false);
		}
	}

	private void updateTab(){
		switch (nTabIndex){
			case 0:
				showAddTap();
				break;
			case 1:
				showValidateTab();
				break;
			case 2:
				showDoneTab();
				break;
			case 3:
				doDone();
				break;
		}
	}

	private class ListenerClass implements ActionListener, PropertyChangeListener, HyperlinkListener{

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

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Object o = evt.getSource();
			if (o instanceof ValidateApiKeyTask){
				ValidateApiKeyTask validateApiKeyTask = (ValidateApiKeyTask) o;
				if (validateApiKeyTask.done){
					validateApiKeyTask.done = false;
					switch (validateApiKeyTask.result){
						case FAIL_ALREADY_IMPORTED:
							jNext.setEnabled(false);
							account = null;
							donePanel.setResult(DialoguesAccount.get().accountAlreadyImported());
							donePanel.setText(DialoguesAccount.get().accountAlreadyImportedText());
							break;
						case FAIL_NO_INTERNET:
							jNext.setEnabled(false);
							account = null;
							donePanel.setResult(DialoguesAccount.get().noInternetConnection());
							donePanel.setText(DialoguesAccount.get().noInternetConnectionText());
							break;
						case FAIL_NOT_VALID:
							jNext.setEnabled(false);
							account = null;
							donePanel.setResult(DialoguesAccount.get().accountNotValid());
							donePanel.setText(DialoguesAccount.get().accountNotValidText());
							break;
						case FAIL_NO_ACCESS:
							donePanel.setResult(DialoguesAccount.get().noAccess());
							donePanel.setText(DialoguesAccount.get().noAccessText());
							break;
						case OK_LIMITED_ACCESS:
							jNext.setEnabled(true);
							donePanel.setResult(DialoguesAccount.get().notEnoughAccess());
							donePanel.setText(DialoguesAccount.get().notEnoughAccessText());
							break;
						case OK_ACCOUNT_VALID:
							jNext.setEnabled(true);
							donePanel.setResult(DialoguesAccount.get().accountValid());
							donePanel.setText(DialoguesAccount.get().accountValidText());
							break;
					}
					nTabIndex = 2;
					updateTab();
				}
				
			}
		}

		@Override
		public void hyperlinkUpdate(HyperlinkEvent hle) {
			if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
				DesktopUtil.browse(hle.getURL().toString(), program);
			}
		}
	}

	private class InputPanel extends JCardPanel {

		public InputPanel() {
			JLabel jUserIdLabel = new JLabel(DialoguesAccount.get().keyId());
			jUserIdLabel.setHorizontalAlignment(JLabel.RIGHT);

			jKeyID = new JNumberField("");
			JCopyPopup.install(jKeyID);

			JLabel jApiKeyLabel = new JLabel(DialoguesAccount.get().vCode());

			jVCode = new JTextField();
			JCopyPopup.install(jVCode);
			JEditorPane jHelp = new JEditorPane("text/html", DialoguesAccount.get().helpText());
			((HTMLDocument)jHelp.getDocument()).getStyleSheet().addRule("body { font-family: "+this.getFont().getFamily() +"; "+"font-size: " + this.getFont().getSize() + "pt; }");
			jHelp.setFont( this.getFont() );
			jHelp.setEditable(false);
			jHelp.setFocusable(false);
			jHelp.setOpaque(false);
			jHelp.addHyperlinkListener(listener);

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
							.addComponent(jKeyID, 100, 100, 100)
							.addComponent(jVCode, 150, 150, Integer.MAX_VALUE)
						)
					)
				)
			);
			cardLayout.setVerticalGroup(
				cardLayout.createSequentialGroup()
				.addComponent(jHelp)
				.addGroup(cardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jUserIdLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jKeyID, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(cardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jApiKeyLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jVCode, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
			);
		}
	}

	private class ValidatePanel extends JCardPanel{

		public ValidatePanel() {
			JLabel jHelp = new JLabel(DialoguesAccount.get().validatingMessage());
			
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

		private JTextArea jHelp;

		public DonePanel() {
			jResult = new JLabel();
			jResult.setFont( new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize()));

			jHelp = new JTextArea();
			jHelp.setLineWrap(true);
			jHelp.setWrapStyleWord(true);
			jHelp.setFont(this.getFont());
			jHelp.setEditable(false);
			jHelp.setOpaque(false);
			jHelp.setFocusable(false);
			jHelp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			JScrollPane jScroll = new JScrollPane(jHelp,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScroll.setBorder(BorderFactory.createLineBorder(this.getBackground().darker(), 1));

			cardLayout.setHorizontalGroup(
				cardLayout.createParallelGroup()
					.addGroup(cardLayout.createSequentialGroup()
						.addGap(5)
						.addComponent(jResult)
					)
					.addComponent(jScroll, 350, 350, 350)
			);
			cardLayout.setVerticalGroup(
				cardLayout.createSequentialGroup()
					.addComponent(jResult, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jScroll, 78, 78, 78)
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

		private Result result = null;
		private boolean done = false;
		private HumansGetter humansGetter = new HumansGetter();

		@Override
		public Void doInBackground() {
			setProgress(0);
			if (program.getSettings().getAccounts().contains( account )){ //account already exist
				result = Result.FAIL_ALREADY_IMPORTED;
				return null;
			}
			humansGetter.load(null, true, account); //Update account
			if (humansGetter.hasError() || humansGetter.getFails() > 0){ //Failed to add new account
				if (humansGetter.getFails() > 0 && humansGetter.getFails() < 4){ //Not enough access
					result = Result.OK_LIMITED_ACCESS;
				} else if (humansGetter.getFails() >= 4){ //Offline
					result = Result.FAIL_NO_ACCESS;
				} else if (!Online.isOnline(program.getSettings())){ //Offline
					result = Result.FAIL_NO_INTERNET;
				} else {
					result = Result.FAIL_NOT_VALID; //Not valid
				}
			} else { //Successfully added new account
				result = Result.OK_ACCOUNT_VALID;
			}
			return null;
        }

		@Override
		public void done() {
			try {
				get();
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
				throw new RuntimeException(ex);
			}
			done = true;
			setProgress(100);
		}
	}
}