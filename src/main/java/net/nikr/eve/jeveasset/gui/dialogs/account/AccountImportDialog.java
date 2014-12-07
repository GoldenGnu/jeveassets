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

package net.nikr.eve.jeveasset.gui.dialogs.account;

import com.beimin.eveapi.handler.ApiError;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.text.html.HTMLDocument;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.MyAccount;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.components.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JIntegerField;
import net.nikr.eve.jeveasset.gui.shared.components.JWorking;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;
import net.nikr.eve.jeveasset.io.eveapi.AccountGetter;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AccountImportDialog extends JDialogCentered {

	private static final Logger LOG = LoggerFactory.getLogger(AccountImportDialog.class);

	private enum AccountImportAction {
		ADD_KEY_CANCEL,
		NEXT,
		PREVIOUS
	}

	private enum AccountImportCard {
		ADD,
		VALIDATE,
		DONE
	}

	private final AccountManagerDialog apiManager;

	private enum Result {
		FAIL_EXIST,
		FAIL_API_EXCEPTION,
		FAIL_API_ERROR,
		FAIL_API_GENERIC,
		FAIL_NOT_VALID,
		FAIL_NOT_ENOUGH_PRIVILEGES,
		OK_LIMITED_ACCESS,
		OK_ACCOUNT_VALID
	}

	private JTextField jKeyID;
	private JTextField jVCode;
	private final JButton jNext;
	private final JButton jPrevious;
	private final JButton jCancel;
	private final CardLayout cardLayout;
	private final JPanel jContent;
	private MyAccount account;
	private MyAccount editAccount;
	private final ListenerClass listener = new ListenerClass();

	private final DonePanel donePanel;

	private int nTabIndex;

	public AccountImportDialog(final AccountManagerDialog apiManager, final Program program) {
		super(program, DialoguesAccount.get().dialogueNameAccountImport(), apiManager.getDialog());
		this.apiManager = apiManager;

		donePanel = new DonePanel();

		this.getDialog().addWindowFocusListener(listener);

		cardLayout = new CardLayout();
		jContent = new JPanel(cardLayout);
		jContent.add(new InputPanel(), AccountImportCard.ADD.name());
		jContent.add(new ValidatePanel(), AccountImportCard.VALIDATE.name());
		jContent.add(donePanel, AccountImportCard.DONE.name());

		jPrevious = new JButton(DialoguesAccount.get().previousArrow());
		jPrevious.setActionCommand(AccountImportAction.PREVIOUS.name());
		jPrevious.addActionListener(listener);

		jNext = new JButton(DialoguesAccount.get().nextArrow());
		jNext.setActionCommand(AccountImportAction.NEXT.name());
		jNext.addActionListener(listener);

		jCancel = new JButton(DialoguesAccount.get().cancel());
		jCancel.setActionCommand(AccountImportAction.ADD_KEY_CANCEL.name());
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

	private int getKeyID() {
		try {
			return Integer.valueOf(jKeyID.getText());
		} catch (NumberFormatException ex) {
			return 0;
		}
	}
	private String getVCode() {
		return jVCode.getText();
	}
	private void focus() {
		if (jKeyID.getText().isEmpty() && nTabIndex == 0) {
			jKeyID.requestFocusInWindow();
		} else if (jVCode.getText().isEmpty() && nTabIndex == 0) {
			jVCode.requestFocusInWindow();
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
	protected void windowShown() { }

	@Override
	protected void save() { }

	public void show(final MyAccount editAccount) {
		this.editAccount = editAccount;
		if (editAccount != null) { //Edit
			jKeyID.setText(String.valueOf(editAccount.getKeyID()));
			jVCode.setText(editAccount.getVCode());
		} else {
			jKeyID.setText("");
			jVCode.setText("");
		}
		nTabIndex = 0;
		updateTab();
		super.setVisible(true);
	}

	public void show() {
		show(null);
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			show();
		} else {
			super.setVisible(false);
		}
	}

	private void showAddTap() {
		cardLayout.show(jContent, AccountImportCard.ADD.name());
		jPrevious.setEnabled(false);
		jNext.setEnabled(true);
		jNext.setText(DialoguesAccount.get().nextArrow());
		focus();
	}

	private void showValidateTab() {
		cardLayout.show(jContent, AccountImportCard.VALIDATE.name());
		jPrevious.setEnabled(true);
		jNext.setEnabled(false);
		jNext.setText(DialoguesAccount.get().nextArrow());
		if (editAccount == null) { //Add
			account = new MyAccount(getKeyID(), getVCode());
		} else { //Edit
			account = new MyAccount(editAccount);
			account.setKeyID(getKeyID());
			account.setvCode(getVCode());
		}
		ValidateApiKeyTask validateApiKeyTask = new ValidateApiKeyTask();
		validateApiKeyTask.addPropertyChangeListener(listener);
		validateApiKeyTask.execute();
	}

	private void showDoneTab() {
		jPrevious.setEnabled(true);
		jNext.setText(DialoguesAccount.get().ok());
		cardLayout.show(jContent, AccountImportCard.DONE.name());
	}

	private void done() {
		if (editAccount != null) { //Edit
			program.getAccounts().remove(editAccount);
		}
		apiManager.forceUpdate();
		program.getAccounts().add(account);
		apiManager.updateTable();
		program.saveProfile();
		this.setVisible(false);
	}

	private void updateTab() {
		switch (nTabIndex) {
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
				done();
				break;
		}
	}

	private class ListenerClass implements ActionListener, PropertyChangeListener,
											WindowFocusListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (AccountImportAction.ADD_KEY_CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
			if (AccountImportAction.PREVIOUS.name().equals(e.getActionCommand())) {
				nTabIndex = 0;
				updateTab();
			}

			if (AccountImportAction.NEXT.name().equals(e.getActionCommand())) {
				nTabIndex++;
				updateTab();
			}
		}

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			Object o = evt.getSource();
			if (o instanceof ValidateApiKeyTask) {
				ValidateApiKeyTask validateApiKeyTask = (ValidateApiKeyTask) o;
				if (validateApiKeyTask.done) {
					validateApiKeyTask.done = false;
					switch (validateApiKeyTask.result) {
						case FAIL_EXIST:
							jNext.setEnabled(false);
							donePanel.setResult(DialoguesAccount.get().failExist());
							donePanel.setText(DialoguesAccount.get().failExistText());
							break;
						case FAIL_API_EXCEPTION:
							jNext.setEnabled(false);
							donePanel.setResult(DialoguesAccount.get().failApiException());
							donePanel.setText(DialoguesAccount.get().failApiExceptionText());
							break;
						case FAIL_API_ERROR:
							jNext.setEnabled(false);
							donePanel.setResult(DialoguesAccount.get().failApiError());
							donePanel.setText(DialoguesAccount.get().failApiErrorText(validateApiKeyTask.error));
							break;
						case FAIL_API_GENERIC:
							jNext.setEnabled(false);
							donePanel.setResult(DialoguesAccount.get().failGeneric());
							donePanel.setText(DialoguesAccount.get().failGenericText(validateApiKeyTask.error));
							break;
						case FAIL_NOT_VALID:
							jNext.setEnabled(false);
							donePanel.setResult(DialoguesAccount.get().failNotValid());
							donePanel.setText(DialoguesAccount.get().failNotValidText());
							break;
						case FAIL_NOT_ENOUGH_PRIVILEGES:
							jNext.setEnabled(false);
							donePanel.setResult(DialoguesAccount.get().failNotEnoughPrivileges());
							donePanel.setText(DialoguesAccount.get().failNotEnoughPrivilegesText());
							break;
						case OK_LIMITED_ACCESS:
							jNext.setEnabled(true);
							donePanel.setResult(DialoguesAccount.get().okLimited());
							donePanel.setText(DialoguesAccount.get().okLimitedText());
							break;
						case OK_ACCOUNT_VALID:
							jNext.setEnabled(true);
							donePanel.setResult(DialoguesAccount.get().okValid());
							donePanel.setText(DialoguesAccount.get().okValidText());
							break;
					}
					nTabIndex = 2;
					updateTab();
				}
			}
		}

		@Override
		public void windowGainedFocus(final WindowEvent e) {
			focus();
		}

		@Override
		public void windowLostFocus(final WindowEvent e) { }
	}

	private class InputPanel extends JCardPanel {

		public InputPanel() {
			JLabel jUserIdLabel = new JLabel(DialoguesAccount.get().keyId());
			jUserIdLabel.setHorizontalAlignment(JLabel.RIGHT);

			jKeyID = new JIntegerField("", DocumentFactory.ValueFlag.POSITIVE_AND_ZERO);
			JCopyPopup.install(jKeyID);

			JLabel jApiKeyLabel = new JLabel(DialoguesAccount.get().vCode());

			jVCode = new JTextField();
			JCopyPopup.install(jVCode);
			JEditorPane jHelp = new JEditorPane(
					"text/html", "<html><body style=\"font-family: " + jUserIdLabel.getFont().getName() + "; font-size: " + jUserIdLabel.getFont().getSize() + "pt\">"
				+ DialoguesAccount.get().helpText() + "</body></html>");
			((HTMLDocument) jHelp.getDocument()).getStyleSheet().addRule("body { font-family: " + this.getFont().getFamily() + "; " + "font-size: " + this.getFont().getSize() + "pt; }");
			jHelp.setFont(this.getFont());
			jHelp.setEditable(false);
			jHelp.setFocusable(false);
			jHelp.setOpaque(false);
			jHelp.addHyperlinkListener(DesktopUtil.getHyperlinkListener(program));

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

	private class ValidatePanel extends JCardPanel {

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

		private final JLabel jResult;

		private final JEditorPane jHelp;

		public DonePanel() {
			jResult = new JLabel();
			jResult.setFont(new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize()));

			jHelp = new JEditorPane("text/html", "");
			//jHelp.setLineWrap(true);
			//jHelp.setWrapStyleWord(true);
			jHelp.addHyperlinkListener(DesktopUtil.getHyperlinkListener(program));
			jHelp.setFont(this.getFont());
			jHelp.setEditable(false);
			jHelp.setOpaque(false);
			jHelp.setFocusable(false);
			jHelp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			JScrollPane jScroll = new JScrollPane(jHelp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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

		public void setResult(final String text) {
			jResult.setText(text);
		}

		public void setText(final String text) {
			jHelp.setText("<html><body style=\"font-family: " + jResult.getFont().getName() + "; font-size: " + jResult.getFont().getSize() + "pt\">"
				+ text
				+ "</body></html>");
		}

	}

	private abstract class JCardPanel extends JPanel {

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
		private String error = "";
		private final AccountGetter accountGetter = new AccountGetter();

		@Override
		public Void doInBackground() {
			setProgress(0);
			if (program.getAccounts().contains(account)) { //account already exist
				result = Result.FAIL_EXIST;
				return null;
			}
			accountGetter.load(null, true, account); //Update account
			if (accountGetter.hasError() || accountGetter.isFail()) { //Failed to add new account
				Object object = accountGetter.getError();
				if (accountGetter.isInvalid()) { //invalid account
					result = Result.FAIL_NOT_VALID;
				} else if (object instanceof Exception) { //Real error
					result = Result.FAIL_API_EXCEPTION;
				} else if (object instanceof ApiError) { //API error
					ApiError apiError = (ApiError) object;
					result = Result.FAIL_API_ERROR;
					error = apiError.getError() + " (Code: " + apiError.getCode() + ")";
				} else if (object instanceof String) { //String error
					String string = (String) object;
					error = string;
					result = Result.FAIL_API_GENERIC;
				} else if (accountGetter.isFail()) { // Not enough privileges
					result = Result.FAIL_NOT_ENOUGH_PRIVILEGES;
				} else { //Fallback - should never happen
					result = Result.FAIL_API_GENERIC;
					error = "Unknown Error";
				}
			} else { //Successfully added new account
				if (accountGetter.isLimited()) {
					result = Result.OK_LIMITED_ACCESS;
				} else {
					result = Result.OK_ACCOUNT_VALID;
				}
			}
			return null;
		}

		@Override
		public void done() {
			try {
				get();
			} catch (InterruptedException ex) {
				LOG.error(ex.getMessage(), ex);
				throw new RuntimeException(ex);
			} catch (ExecutionException ex) {
				LOG.error(ex.getMessage(), ex);
				throw new RuntimeException(ex);
			}
			done = true;
			setProgress(100);
		}
	}
}
