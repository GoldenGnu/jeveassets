/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.text.html.HTMLDocument;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.ApiType;
import net.nikr.eve.jeveasset.data.esi.EsiOwner;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccount;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.components.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JIntegerField;
import net.nikr.eve.jeveasset.gui.shared.components.JWorking;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;
import net.nikr.eve.jeveasset.io.esi.EsiAuth;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.nikr.eve.jeveasset.io.esi.EsiOwnerGetter;
import net.nikr.eve.jeveasset.io.esi.EsiScopes;
import net.nikr.eve.jeveasset.io.eveapi.AccountGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitOwnerGetter;
import net.nikr.eve.jeveasset.io.shared.AccountAdder;
import net.nikr.eve.jeveasset.io.shared.AccountAdderAdapter;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AccountImportDialog extends JDialogCentered {

	private static final Logger LOG = LoggerFactory.getLogger(AccountImportDialog.class);

	private enum AccountImportAction {
		ADD_ESI,
		ADD_EVEAPI,
		ADD_EVEKIT,
		ADD_KEY_CANCEL,
		NEXT,
		PREVIOUS
	}

	private enum AccountImportCard {
		TYPE,
		ADD_ESI,
		ADD_EVEAPI,
		ADD_EVEKIT,
		VALIDATE,
		DONE,
		EXIT
	}

	private final AccountManagerDialog apiManager;

	private enum Result {
		FAIL_EXIST, //OK
		FAIL_API_FAIL,
		FAIL_INVALID,
		FAIL_NOT_ENOUGH_PRIVILEGES, //OK
		FAIL_WRONG_ENTRY,
		FAIL_CANCEL,
		OK_LIMITED_ACCESS, //OK
		OK_ACCOUNT_VALID //OK
	}

	private JDropDownButton jScopes;
	private JTextField jAuthCode;
	private JTextField jKeyID;
	private JTextField jVCode;
	private JTextField jAccessKey;
	private JTextField jAccessCred;
	private final JButton jNext;
	private final JButton jPrevious;
	private final JButton jCancel;
	private final CardLayout cardLayout;
	private final JPanel jContent;
	private final ListenerClass listener = new ListenerClass();
	private final EsiAuth esiAuth = new EsiAuth();

	private final DonePanel donePanel;

	private EveApiAccount account;
	private EveApiAccount editAccount;
	private EveKitOwner eveKitOwner;
	private EveKitOwner editEveKitOwner;
	private EsiOwner esiOwner;
	private EsiOwner editEsiOwner;
	private AccountImportCard currentCard;
	private ApiType apiType;
	private boolean changeType;
	private AddTask addTask;
	private final Map<EsiScopes, JCheckBoxMenuItem> scopesMap = new EnumMap<EsiScopes, JCheckBoxMenuItem>(EsiScopes.class);

	public AccountImportDialog(final AccountManagerDialog apiManager, final Program program) {
		super(program, DialoguesAccount.get().dialogueNameAccountImport(), apiManager.getDialog());
		this.apiManager = apiManager;

		donePanel = new DonePanel();

		this.getDialog().addWindowFocusListener(listener);

		cardLayout = new CardLayout();
		jContent = new JPanel(cardLayout);
		jContent.add(new TypePanel(), AccountImportCard.TYPE.name());
		jContent.add(new EveApiPanel(), AccountImportCard.ADD_EVEAPI.name());
		jContent.add(new EveKitPanel(), AccountImportCard.ADD_EVEKIT.name());
		jContent.add(new EsiPanel(), AccountImportCard.ADD_ESI.name());
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
					.addComponent(jPrevious, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jNext, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addGap(20)
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jContent)
				.addGroup(layout.createParallelGroup()
					.addComponent(jPrevious, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jNext, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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

	private String getAuthCode() {
		return jAuthCode.getText().trim();
	}

	private int getAccessKey() {
		try {
			return Integer.valueOf(jAccessKey.getText());
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	private String getAccessCred() {
		return jAccessCred.getText();
	}

	private void focus() {
		if (jKeyID.getText().isEmpty() && currentCard == AccountImportCard.ADD_EVEAPI) {
			jKeyID.requestFocusInWindow();
		} else if (jVCode.getText().isEmpty() && currentCard == AccountImportCard.ADD_EVEAPI) {
			jVCode.requestFocusInWindow();
		}
		if (jAccessKey.getText().isEmpty() && currentCard == AccountImportCard.ADD_EVEKIT) {
			jAccessKey.requestFocusInWindow();
		} else if (jAccessCred.getText().isEmpty() && currentCard == AccountImportCard.ADD_EVEKIT) {
			jAccessCred.requestFocusInWindow();
		}
		if (currentCard == AccountImportCard.ADD_ESI) {
			jScopes.requestFocusInWindow();
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

	public void add() {
		show(true, AccountImportCard.TYPE, null, null, null);
	}

	public void addEveKit() {
		show(false, AccountImportCard.ADD_EVEKIT, null, null, null);
	}

	public void addEveApi() {
		show(false, AccountImportCard.ADD_EVEAPI, null, null, null);
	}

	public void addEsi() {
		show(false, AccountImportCard.ADD_ESI, null, null, null);
	}

	public void editEveKit(final EveKitOwner editEveKitOwner) {
		show(false, AccountImportCard.ADD_EVEKIT, null, editEveKitOwner, null);
	}

	public void editEveApi(final EveApiAccount editAccount) {
		show(false, AccountImportCard.ADD_EVEAPI, editAccount, null, null);
	}

	public void editEsi(final EsiOwner editEsiOwner) {
		show(false, AccountImportCard.ADD_ESI, null, null, editEsiOwner);
	}

	private void show(boolean apiTypeEdit, AccountImportCard accountImportCard, final EveApiAccount editAccount, final EveKitOwner editEveKitOwner, final EsiOwner editEsiOwner) {
		currentCard = accountImportCard;
		this.changeType = apiTypeEdit;
		this.editAccount = editAccount;
		this.editEveKitOwner = editEveKitOwner;
		this.editEsiOwner = editEsiOwner;
		if (editAccount != null) { //Edit EveApi
			jKeyID.setText(String.valueOf(editAccount.getKeyID()));
			jVCode.setText(editAccount.getVCode());
		} else { //New
			jKeyID.setText("");
			jVCode.setText("");
		}
		if (editEveKitOwner != null) { //Edit EveKit
			jAccessKey.setText(String.valueOf(editEveKitOwner.getAccessKey()));
			jAccessCred.setText(editEveKitOwner.getAccessCred());
		} else { //New
			jAccessKey.setText("");
			jAccessCred.setText("");
		}
		updateTab();
		super.setVisible(true);
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			add();
		} else {
			super.setVisible(false);
		}
	}

	private void showTypeTap() {
		cardLayout.show(jContent, AccountImportCard.TYPE.name());
		this.getDialog().setIconImage(Images.EDIT_ADD.getImage());
		jPrevious.setEnabled(false);
		jNext.setEnabled(false);
		jNext.setText(DialoguesAccount.get().nextArrow());
		focus();
	}

	private void showEveKitTap() {
		cardLayout.show(jContent, AccountImportCard.ADD_EVEKIT.name());
		this.getDialog().setIconImage(Images.MISC_EVEKIT.getImage());
		jPrevious.setEnabled(changeType);
		jNext.setEnabled(true);
		jNext.setText(DialoguesAccount.get().nextArrow());
		focus();
	}

	private void showEveApiTap() {
		cardLayout.show(jContent, AccountImportCard.ADD_EVEAPI.name());
		this.getDialog().setIconImage(Images.MISC_EVE.getImage());
		jPrevious.setEnabled(changeType);
		jNext.setEnabled(true);
		jNext.setText(DialoguesAccount.get().nextArrow());
		focus();
	}

	private void showEsiTap() {
		cardLayout.show(jContent, AccountImportCard.ADD_ESI.name());
		this.getDialog().setIconImage(Images.MISC_ESI.getImage());
		jPrevious.setEnabled(changeType);
		jAuthCode.setEnabled(false);
		jAuthCode.setText("");
		jNext.setEnabled(false);
		esiAuth.cancelImport();
		for (JCheckBoxMenuItem menuItem : scopesMap.values()) {
			menuItem.setSelected(true);
		}
		jNext.setText(DialoguesAccount.get().nextArrow());
		focus();
	}

	private void showValidateTab() {
		cardLayout.show(jContent, AccountImportCard.VALIDATE.name());
		jPrevious.setEnabled(true);
		jNext.setEnabled(false);
		jNext.setText(DialoguesAccount.get().nextArrow());
		if (apiType == ApiType.EVE_ONLINE) {
			if (editAccount == null) { //Add
				account = new EveApiAccount(getKeyID(), getVCode());
			} else { //Edit
				account = new EveApiAccount(editAccount);
				account.setKeyID(getKeyID());
				account.setvCode(getVCode());
			}
			addTask = new EveApiTask();
			addTask.addPropertyChangeListener(listener);
			addTask.execute();
		}
		if (apiType == ApiType.EVEKIT) {
			if (editEveKitOwner == null) { //Add
				eveKitOwner = new EveKitOwner(getAccessKey(), getAccessCred());
			} else { //Edit
				eveKitOwner = new EveKitOwner(getAccessKey(), getAccessCred(), editEveKitOwner);
			}
			addTask = new EveKitTask();
			addTask.addPropertyChangeListener(listener);
			addTask.execute();
		}
		if (apiType == ApiType.ESI) {
			if (editEsiOwner == null) { //Add
				esiOwner = new EsiOwner();
			} else { //Edit
				esiOwner = new EsiOwner(editEsiOwner);
			}
			addTask = new EsiTask();
			addTask.addPropertyChangeListener(listener);
			addTask.execute();
		}
	}

	private void showDoneTab() {
		jPrevious.setEnabled(true);
		jNext.setText(DialoguesAccount.get().ok());
		cardLayout.show(jContent, AccountImportCard.DONE.name());
	}

	private void done() {
		if (apiType == ApiType.EVE_ONLINE) {
			if (editAccount != null) { //Edit
				program.getProfileManager().getAccounts().remove(editAccount);
			}
			apiManager.forceUpdate();
			program.getProfileManager().getAccounts().add(account);
			apiManager.updateTable();
		}
		if (apiType == ApiType.EVEKIT) {
			if (editEveKitOwner != null) { //Edit
				program.getProfileManager().getEveKitOwners().remove(editEveKitOwner);
			}
			apiManager.forceUpdate();
			program.getProfileManager().getEveKitOwners().add(eveKitOwner);
			apiManager.updateTable();
		}
		if (apiType == ApiType.ESI) {
			if (editEsiOwner != null) { //Edit
				program.getProfileManager().getEsiOwners().remove(editEsiOwner);
			}
			apiManager.forceUpdate();
			program.getProfileManager().getEsiOwners().add(esiOwner);
			apiManager.updateTable();
		}
		this.setVisible(false);
	}

	private void updateTab() {
		switch (currentCard) {
			case TYPE:
				showTypeTap();
				break;
			case ADD_EVEKIT:
				apiType = ApiType.EVEKIT;
				showEveKitTap();
				break;
			case ADD_EVEAPI:
				apiType = ApiType.EVE_ONLINE;
				showEveApiTap();
				break;
			case ADD_ESI:
				apiType = ApiType.ESI;
				showEsiTap();
				break;
			case VALIDATE:
				showValidateTab();
				break;
			case DONE:
				showDoneTab();
				break;
			case EXIT:
				done();
				break;
		}
	}

	private class ListenerClass implements ActionListener, PropertyChangeListener,
											WindowFocusListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (AccountImportAction.ADD_KEY_CANCEL.name().equals(e.getActionCommand())) {
				if (addTask != null) {
					addTask.cancel(true);
				}
				esiAuth.cancelImport();
				setVisible(false);
			} else if (AccountImportAction.PREVIOUS.name().equals(e.getActionCommand())) {
				switch (currentCard) {
					case TYPE: //Previous: Type
						currentCard = AccountImportCard.TYPE;
						break;
					case ADD_EVEAPI: //Previous: Type
						currentCard = AccountImportCard.TYPE;
						break;
					case ADD_EVEKIT: //Previous: Type
						currentCard = AccountImportCard.TYPE;
						break;
					case VALIDATE: //Previous: Add
						if (apiType == ApiType.EVEKIT) {
							currentCard = AccountImportCard.ADD_EVEKIT;
						}
						if (apiType == ApiType.EVE_ONLINE) {
							currentCard = AccountImportCard.ADD_EVEAPI;
						}
						if (apiType == ApiType.ESI) {
							currentCard = AccountImportCard.ADD_ESI;
						}
						addTask.cancel(true);
						break;
					case DONE: //Previous: Add
						if (apiType == ApiType.EVEKIT) {
							currentCard = AccountImportCard.ADD_EVEKIT;
						}
						if (apiType == ApiType.EVE_ONLINE) {
							currentCard = AccountImportCard.ADD_EVEAPI;
						}
						if (apiType == ApiType.ESI) {
							currentCard = AccountImportCard.ADD_ESI;
						}
						break;
					case EXIT: //Previous: Exit
						currentCard = AccountImportCard.EXIT;
						break;
				}
				updateTab();
			} else if (AccountImportAction.NEXT.name().equals(e.getActionCommand())) {
				switch (currentCard) {
					case TYPE: //Never Used
						break;
					case ADD_EVEAPI: //Next: Validate
						currentCard = AccountImportCard.VALIDATE;
						break;
					case ADD_EVEKIT: //Next: Validate
						currentCard = AccountImportCard.VALIDATE;
						break;
					case ADD_ESI: //Next: Validate
						currentCard = AccountImportCard.VALIDATE;
						break;
					case VALIDATE: //Next Done
						currentCard = AccountImportCard.DONE;
						break;
					case DONE: //Next Exit
						currentCard = AccountImportCard.EXIT;
						break;
					case EXIT: //Next Exit
						currentCard = AccountImportCard.EXIT;
						break;
				}
				updateTab();
			} else if (AccountImportAction.ADD_ESI.name().equals(e.getActionCommand())) {
				currentCard = AccountImportCard.ADD_ESI;
				updateTab();
			} else if (AccountImportAction.ADD_EVEAPI.name().equals(e.getActionCommand())) {
				currentCard = AccountImportCard.ADD_EVEAPI;
				updateTab();
			} else if (AccountImportAction.ADD_EVEKIT.name().equals(e.getActionCommand())) {
				currentCard = AccountImportCard.ADD_EVEKIT;
				updateTab();
			}
		}

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			Object o = evt.getSource();
			if (o instanceof AddTask) {
				AddTask addTask = (AddTask) o;
				if (addTask.done) {
					addTask.done = false;
					switch (addTask.result) {
						case FAIL_EXIST:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(false);
							donePanel.setResult(DialoguesAccount.get().failExist());
							donePanel.setText(DialoguesAccount.get().failExistText());
							break;
						case FAIL_API_FAIL:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(false);
							donePanel.setResult(DialoguesAccount.get().failApiError());
							donePanel.setText(DialoguesAccount.get().failApiErrorText(addTask.error));
							break;
						case FAIL_INVALID:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(false);
							donePanel.setResult(DialoguesAccount.get().failNotValid());
							donePanel.setText(DialoguesAccount.get().failNotValidText());
							break;
						case FAIL_NOT_ENOUGH_PRIVILEGES:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(false);
							donePanel.setResult(DialoguesAccount.get().failNotEnoughPrivileges());
							donePanel.setText(DialoguesAccount.get().failNotEnoughPrivilegesText());
							break;
						case FAIL_WRONG_ENTRY:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(false);
							donePanel.setResult(DialoguesAccount.get().failWrongEntry());
							donePanel.setText(DialoguesAccount.get().failWrongEntryText());
							break;
						case FAIL_CANCEL:
							switch(apiType) {
								case EVE_ONLINE:
									currentCard = AccountImportCard.ADD_EVEAPI;
									break;
								case EVEKIT:
									currentCard = AccountImportCard.ADD_EVEKIT;
									break;
								case ESI:
									currentCard = AccountImportCard.ADD_ESI;
									break;
							}
							break;
						case OK_LIMITED_ACCESS:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(true);
							donePanel.setResult(DialoguesAccount.get().okLimited());
							donePanel.setText(DialoguesAccount.get().okLimitedText());
							break;
						case OK_ACCOUNT_VALID:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(true);
							donePanel.setResult(DialoguesAccount.get().okValid());
							donePanel.setText(DialoguesAccount.get().okValidText());
							break;
					}
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

	private class TypePanel extends JCardPanel {

		public TypePanel() {
			JButton jEveApi = new JButton(DialoguesAccount.get().eveapi(), Images.MISC_EVE.getIcon());
			Font font = new Font(jEveApi.getFont().getName(), Font.BOLD, jEveApi.getFont().getSize() + 5);
			jEveApi.setActionCommand(AccountImportAction.ADD_EVEAPI.name());
			jEveApi.addActionListener(listener);
			jEveApi.setIconTextGap(20);
			jEveApi.setFont(font);
			jEveApi.setHorizontalAlignment(JButton.LEADING);

			JButton jEveKit = new JButton(DialoguesAccount.get().evekit(), Images.MISC_EVEKIT.getIcon());
			jEveKit.setActionCommand(AccountImportAction.ADD_EVEKIT.name());
			jEveKit.addActionListener(listener);
			jEveKit.setIconTextGap(20);
			jEveKit.setFont(font);
			jEveKit.setHorizontalAlignment(JButton.LEADING);

			JButton jEsi = new JButton(DialoguesAccount.get().esi(), Images.MISC_ESI.getIcon());
			jEsi.setActionCommand(AccountImportAction.ADD_ESI.name());
			jEsi.addActionListener(listener);
			jEsi.setIconTextGap(20);
			jEsi.setFont(font);
			jEsi.setHorizontalAlignment(JButton.LEADING);

			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(jEveApi);
			buttonGroup.add(jEveKit);
			buttonGroup.add(jEsi);

			cardLayout.setHorizontalGroup(
				cardLayout.createParallelGroup()
					.addComponent(jEveApi, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					.addComponent(jEveKit, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					.addComponent(jEsi, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
			);
			cardLayout.setVerticalGroup(
				cardLayout.createSequentialGroup()
				.addComponent(jEveApi, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jEveKit, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jEsi, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			);
		}
		
	}

	private class EveKitPanel extends JCardPanel {

		public EveKitPanel() {
			JLabel jUserIdLabel = new JLabel(DialoguesAccount.get().accessKey());
			jUserIdLabel.setHorizontalAlignment(JLabel.RIGHT);

			jAccessKey = new JIntegerField("", DocumentFactory.ValueFlag.POSITIVE_AND_ZERO);
			JCopyPopup.install(jAccessKey);

			JLabel jApiKeyLabel = new JLabel(DialoguesAccount.get().credential());

			jAccessCred = new JTextField();
			JCopyPopup.install(jAccessCred);
			JEditorPane jHelp = new JEditorPane(
					"text/html", "<html><body style=\"font-family: " + jUserIdLabel.getFont().getName() + "; font-size: " + jUserIdLabel.getFont().getSize() + "pt\">"
				+ DialoguesAccount.get().eveKitHelpText() + "</body></html>");
			((HTMLDocument) jHelp.getDocument()).getStyleSheet().addRule("body { font-family: " + this.getFont().getFamily() + "; " + "font-size: " + this.getFont().getSize() + "pt; }");
			jHelp.setFont(getFont());
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
							.addComponent(jAccessKey, 100, 100, 100)
							.addComponent(jAccessCred, 150, 150, Integer.MAX_VALUE)
						)
					)
				)
			);
			cardLayout.setVerticalGroup(
				cardLayout.createSequentialGroup()
				.addComponent(jHelp)
				.addGroup(cardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jUserIdLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAccessKey, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(cardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jApiKeyLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAccessCred, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
			);
		}
	}

	private class EveApiPanel extends JCardPanel {

		public EveApiPanel() {
			JLabel jUserIdLabel = new JLabel(DialoguesAccount.get().keyId());
			jUserIdLabel.setHorizontalAlignment(JLabel.RIGHT);

			jKeyID = new JIntegerField("", DocumentFactory.ValueFlag.POSITIVE_AND_ZERO);
			JCopyPopup.install(jKeyID);

			JLabel jApiKeyLabel = new JLabel(DialoguesAccount.get().vCode());

			jVCode = new JTextField();
			JCopyPopup.install(jVCode);
			JEditorPane jHelp = new JEditorPane(
					"text/html", "<html><body style=\"font-family: " + jUserIdLabel.getFont().getName() + "; font-size: " + jUserIdLabel.getFont().getSize() + "pt\">"
				+ DialoguesAccount.get().eveApiHelpText() + "</body></html>");
			((HTMLDocument) jHelp.getDocument()).getStyleSheet().addRule("body { font-family: " + this.getFont().getFamily() + "; " + "font-size: " + this.getFont().getSize() + "pt; }");
			jHelp.setFont(getFont());
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
					.addComponent(jUserIdLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jKeyID, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(cardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jApiKeyLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jVCode, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
			);
		}
	}

	private class EsiPanel extends JCardPanel {

		private final JButton jBrowse;

		public EsiPanel() {
			JLabel jAuthLabel = new JLabel(DialoguesAccount.get().authentication());
			jBrowse = new JButton(DialoguesAccount.get().authorize());
			jBrowse.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Set<String> scopes = new HashSet<String>();
					for (Map.Entry<EsiScopes, JCheckBoxMenuItem> entry : scopesMap.entrySet()) {
						if (entry.getValue().isSelected()) {
							scopes.add(entry.getKey().getScope());
						}
					}
					if (esiAuth.isServerStarted()) { //Localhost
						boolean ok = esiAuth.openWebpage(EsiCallbackURL.LOCALHOST, scopes);
						if (ok) { //Wait for response
							currentCard = AccountImportCard.VALIDATE;
							updateTab();
						}
					} else {
						boolean ok = esiAuth.openWebpage(EsiCallbackURL.EVE_NIKR_NET, scopes);
						if (ok) {
							jAuthCode.setEnabled(true);
							jNext.setEnabled(true);
						}
					}
				}
			});

			scopesMap.clear();
			jScopes = new JDropDownButton(DialoguesAccount.get().scopes(), Images.MISC_ESI.getIcon(), JDropDownButton.LEFT, JDropDownButton.TOP);
			for (EsiScopes scopes : EsiScopes.values()) {
				if (!scopes.isEnabled() || scopes.getScope().isEmpty()) {
					continue;
				}
				JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(scopes.toString());
				jCheckBoxMenuItem.setSelected(true);
				jCheckBoxMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						validateScopes();
					}
				});
				
				jScopes.add(jCheckBoxMenuItem, true);
				scopesMap.put(scopes, jCheckBoxMenuItem);
			}

			JLabel jAuthCodeLabel = new JLabel(DialoguesAccount.get().authCode());
			jAuthCode = new JTextField();
			JCopyPopup.install(jAuthCode);

			JEditorPane jHelp = new JEditorPane(
					"text/html", "<html><body style=\"font-family: " + jAuthLabel.getFont().getName() + "; font-size: " + jAuthLabel.getFont().getSize() + "pt\">"
				+ DialoguesAccount.get().esiHelpText() + "</body></html>");
			((HTMLDocument) jHelp.getDocument()).getStyleSheet().addRule("body { font-family: " + this.getFont().getFamily() + "; " + "font-size: " + this.getFont().getSize() + "pt; }");
			jHelp.setFont(getFont());
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
							.addComponent(jAuthLabel)
							.addComponent(jAuthCodeLabel)
						)
						.addGroup(cardLayout.createParallelGroup()
							.addGroup(cardLayout.createSequentialGroup()
								.addComponent(jScopes, 100, 100, 100)
								.addComponent(jBrowse, 100, 100, 100)
							)
							.addComponent(jAuthCode, 150, 150, Integer.MAX_VALUE)
						)
					)
				)
			);
			cardLayout.setVerticalGroup(
				cardLayout.createSequentialGroup()
				.addComponent(jHelp)
				.addGroup(cardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jAuthLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScopes, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBrowse, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(cardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jAuthCodeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAuthCode, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
			);
		}

		private void validateScopes() {
			boolean enabled = false;
			for (JCheckBoxMenuItem checkBoxMenuItem : scopesMap.values()) {
				if (checkBoxMenuItem.isSelected()) {
					enabled = true;
					break;
				}
			}
			jBrowse.setEnabled(enabled);
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
					.addComponent(jScroll, 400, 400, 400)
			);
			cardLayout.setVerticalGroup(
				cardLayout.createSequentialGroup()
					.addComponent(jResult, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScroll, 98, 98, 98)
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
			setLayout(cardLayout);
			cardLayout.setAutoCreateGaps(true);
			cardLayout.setAutoCreateContainerGaps(false);
		}

		@Override
		public final Font getFont() {
			return super.getFont();
		}

		@Override
		public final Color getBackground() {
			return super.getBackground();
		}

		@Override
		public final void setLayout(LayoutManager mgr) {
			super.setLayout(mgr);
		}

	}

	class EveApiTask extends AddTask {

		private final AccountGetter accountGetter = new AccountGetter();

		@Override
		public boolean exist() {
			return program.getProfileManager().getAccounts().contains(account) && !account.isExpired();
		}

		@Override
		public void load() {
			accountGetter.load(null, true, account); //Update account
		}

		@Override
		public AccountAdder getAccountAdder() {
			return accountGetter;
		}
	}

	class EveKitTask extends AddTask {

		private final EveKitOwnerGetter eveKitOwnerGetter = new EveKitOwnerGetter();

		@Override
		public boolean exist() {
			return program.getProfileManager().getEveKitOwners().contains(eveKitOwner);
		}

		@Override
		public void load() {
			eveKitOwnerGetter.load(null, eveKitOwner);
		}

		@Override
		public AccountAdder getAccountAdder() {
			return eveKitOwnerGetter;
		}
	}

	class EsiTask extends AddTask {

		private final EsiOwnerGetter esiOwnerGetter = new EsiOwnerGetter();
		private AccountAdder accountAdder;

		@Override
		public boolean exist() {
			return false; //Each ESI account are unique
		}

		@Override
		public void load() {
			boolean ok = esiAuth.finishFlow(esiOwner, getAuthCode());
			if (!ok) {
				accountAdder = new AccountAdderAdapter() {
					@Override
					public boolean hasError() {
						return true;
					}

					@Override
					public boolean isInvalid() {
						return true;
					}
				};
				return;
			}
			accountAdder = esiOwnerGetter;
			esiOwnerGetter.load(esiOwner);
		}

		@Override
		public AccountAdder getAccountAdder() {
			return accountAdder;
		}
	}

	abstract static class AddTask extends SwingWorker<Void, Void> {

		private Result result = null;
		private boolean done = false;
		private String error = "";

		public abstract boolean exist();
		public abstract void load();
		public abstract AccountAdder getAccountAdder();

		@Override
		public final Void doInBackground() {
			setProgress(0);
			if (exist()) { //account already exist
				result = Result.FAIL_EXIST;
				return null;
			}
			load();
			if (getAccountAdder().hasError() || getAccountAdder().isInvalidPrivileges()) { //Failed to add new account
				String s = getAccountAdder().getError();
				if (getAccountAdder().isInvalid()) { //invalid account
					result = Result.FAIL_INVALID;
				} else if (getAccountAdder().isInvalidPrivileges()) { // Not enough privileges
					result = Result.FAIL_NOT_ENOUGH_PRIVILEGES;
				} else if (getAccountAdder().isWrongEntry()) { // Editing account to a different character/corporation 
					result = Result.FAIL_WRONG_ENTRY;
				} else { //String error
					error = s;
					result = Result.FAIL_API_FAIL;
				}
			} else { //Successfully added new account
				if (getAccountAdder().isLimited()) {
					result = Result.OK_LIMITED_ACCESS;
				} else {
					result = Result.OK_ACCOUNT_VALID;
				}
			}
			return null;
		}

		@Override
		public final void done() {
			try {
				get();
			} catch (CancellationException ex) {
				result = Result.FAIL_CANCEL;
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
