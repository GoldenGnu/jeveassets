/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.text.html.HTMLDocument;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.ApiType;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.components.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JIntegerField;
import net.nikr.eve.jeveasset.gui.shared.components.JWorking;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;
import net.nikr.eve.jeveasset.i18n.GuiShared;
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
		SHARE_TO_CLIPBOARD,
		SHARE_TO_FILE,
		SHARE_FROM_CLIPBOARD,
		SHARE_FROM_FILE,
		NEXT,
		PREVIOUS
	}

	private enum AccountImportCard {
		TYPE,
		ADD_ESI,
		SHARE_EXPORT,
		SHARE_IMPORT,
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

	private enum Share {
		IMPORT, EXPORT
	}

	private JDropDownButton jScopes;
	private JTextField jAuthCode;
	private JRadioButtonMenuItem jCorporation;
	private JRadioButtonMenuItem jCharacter;
	private JDropDownButton jType;
	private JButton jBrowse;
	private JTextField jKeyID;
	private JTextField jVCode;
	private JTextField jAccessKey;
	private JTextField jAccessCred;
	private JTextArea jExport;
	private JButton jExportClipboard;
	private JButton jExportFile;
	private JTextArea jImport;
	private JButton jImportClipboard;
	private JButton jImportFile;
	private final JButton jNext;
	private final JButton jPrevious;
	private final JButton jCancel;
	private final CardLayout cardLayout;
	private final JPanel jContent;
	private final ListenerClass listener = new ListenerClass();
	private final EsiAuth esiAuth = new EsiAuth();
	private final JCustomFileChooser jFileChooser;

	private final DonePanel donePanel;

	private EveApiAccount account;
	private EveApiAccount editAccount;
	private EveKitOwner eveKitOwner;
	private EveKitOwner editEveKitOwner;
	private EsiOwner esiOwner;
	private EsiOwner editEsiOwner;
	private AccountImportCard currentCard;
	private ApiType apiType;
	private boolean enableTypeCard;
	private Share share;
	private AddTask addTask;
	private final Map<EsiScopes, JCheckBoxMenuItem> scopesMap = new EnumMap<EsiScopes, JCheckBoxMenuItem>(EsiScopes.class);

	public AccountImportDialog(final AccountManagerDialog apiManager, final Program program) {
		super(program, DialoguesAccount.get().dialogueNameAccountImport(), apiManager.getDialog());
		this.apiManager = apiManager;

		donePanel = new DonePanel();

		this.getDialog().addWindowFocusListener(listener);

		jFileChooser = JCustomFileChooser.createFileChooser(getDialog(), "txt");
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		cardLayout = new CardLayout();
		jContent = new JPanel(cardLayout);
		jContent.add(new TypePanel(), AccountImportCard.TYPE.name());
		jContent.add(new ImportPanel(), AccountImportCard.SHARE_IMPORT.name());
		jContent.add(new EveApiPanel(), AccountImportCard.ADD_EVEAPI.name());
		jContent.add(new EveKitPanel(), AccountImportCard.ADD_EVEKIT.name());
		jContent.add(new EsiPanel(), AccountImportCard.ADD_ESI.name());
		jContent.add(new ValidatePanel(), AccountImportCard.VALIDATE.name());
		jContent.add(new ExportPanel(), AccountImportCard.SHARE_EXPORT.name());
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
		show(true, null, AccountImportCard.TYPE, null, null, null);
	}

	public void shareExport() {
		show(false, Share.EXPORT, AccountImportCard.ADD_ESI, null, null, null);
	}

	public void shareImport() {
		show(false, Share.IMPORT, AccountImportCard.SHARE_IMPORT, null, null, null);
	}

	public void editEveKit(final EveKitOwner editEveKitOwner) {
		show(false, null, AccountImportCard.ADD_EVEKIT, null, editEveKitOwner, null);
	}

	public void editEveApi(final EveApiAccount editAccount) {
		show(false, null, AccountImportCard.ADD_EVEAPI, editAccount, null, null);
	}

	public void editEsi(final EsiOwner editEsiOwner) {
		show(false, null, AccountImportCard.ADD_ESI, null, null, editEsiOwner);
	}

	private void show(boolean enableTypeCard, Share share, AccountImportCard accountImportCard, final EveApiAccount editAccount, final EveKitOwner editEveKitOwner, final EsiOwner editEsiOwner) {
		currentCard = accountImportCard;
		this.enableTypeCard = enableTypeCard;
		this.share = share;
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
		if (editEsiOwner != null) { //Edit ESI
			jType.setVisible(false);
			if (editEsiOwner.isCorporation()) {
				jCharacter.setSelected(false);
				jCorporation.setSelected(true);
			} else {
				jCorporation.setSelected(false);
				jCharacter.setSelected(true);
			}
		} else { //Add
			jImport.setText("");
			jType.setVisible(true);
			jCorporation.setSelected(false);
			jCharacter.setSelected(true);
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
		this.getDialog().setTitle(DialoguesAccount.get().dialogueNameAccountImport());
		jPrevious.setEnabled(false);
		jNext.setEnabled(false);
		jNext.setText(DialoguesAccount.get().nextArrow());
		focus();
	}

	private void showEveKitTap() {
		cardLayout.show(jContent, AccountImportCard.ADD_EVEKIT.name());
		this.getDialog().setIconImage(Images.MISC_EVEKIT.getImage());
		this.getDialog().setTitle(DialoguesAccount.get().dialogueNameAccountImport());
		jPrevious.setEnabled(enableTypeCard);
		jNext.setEnabled(true);
		jNext.setText(DialoguesAccount.get().nextArrow());
		focus();
	}

	private void showEveApiTap() {
		cardLayout.show(jContent, AccountImportCard.ADD_EVEAPI.name());
		this.getDialog().setIconImage(Images.MISC_EVE.getImage());
		this.getDialog().setTitle(DialoguesAccount.get().dialogueNameAccountImport());
		jPrevious.setEnabled(enableTypeCard);
		jNext.setEnabled(true);
		jNext.setText(DialoguesAccount.get().nextArrow());
		focus();
	}

	private void showEsiTap() {
		cardLayout.show(jContent, AccountImportCard.ADD_ESI.name());
		this.getDialog().setIconImage(Images.MISC_ESI.getImage());
		if (share == Share.EXPORT) {
			this.getDialog().setTitle(DialoguesAccount.get().dialogueNameAccountExport());
		} else {
			this.getDialog().setTitle(DialoguesAccount.get().dialogueNameAccountImport());
		}
		jPrevious.setEnabled(enableTypeCard);
		jAuthCode.setEnabled(false);
		jAuthCode.setText("");
		jNext.setEnabled(false);
		esiAuth.cancelImport();
		updateScopes();
		jNext.setText(DialoguesAccount.get().nextArrow());
		focus();
	}

	private void showImportTap() {
		cardLayout.show(jContent, AccountImportCard.SHARE_IMPORT.name());
		this.getDialog().setIconImage(Images.MISC_ESI.getImage());
		this.getDialog().setTitle(DialoguesAccount.get().dialogueNameAccountImport());
		jPrevious.setEnabled(false);
		jNext.setEnabled(true);
		jNext.setText(DialoguesAccount.get().nextArrow());
	}

	private void showExportTap() {
		cardLayout.show(jContent, AccountImportCard.SHARE_EXPORT.name());
		jPrevious.setEnabled(true);
		jNext.setEnabled(true);
		jNext.setText(DialoguesAccount.get().ok());
		try {
			String value = esiOwner.getCallbackURL().name() + " " + esiOwner.getRefreshToken();
			String code = new String(Base64.getUrlEncoder().encode(value.getBytes(StandardCharsets.UTF_8)), "UTF-8").replace("=", "");
			jExport.setText(code);
			jExport.setFocusable(true);
			jExportClipboard.setEnabled(true);
			jExportFile.setEnabled(true);
		} catch (UnsupportedEncodingException ex) {
			jExport.setText(DialoguesAccount.get().shareExportFail());
			jExport.setFocusable(false);
			jExportClipboard.setEnabled(false);
			jExportFile.setEnabled(false);
		}
	}

	private void showValidateTab() {
		cardLayout.show(jContent, AccountImportCard.VALIDATE.name());
		jPrevious.setEnabled(true);
		jNext.setEnabled(false);
		jNext.setText(DialoguesAccount.get().nextArrow());
		if (share == Share.IMPORT) {
			esiOwner = new EsiOwner();
			try {
				String code = new String(Base64.getUrlDecoder().decode(jImport.getText().trim()), "UTF-8");
				System.out.println("code: " + code);
				String[] codes = code.split(" ");
				esiOwner.setCallbackURL(EsiCallbackURL.valueOf(codes[0]));
				esiOwner.setRefreshToken(codes[1]);
			} catch (UnsupportedEncodingException | IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
				LOG.error("Failed to import jEveAssets ESI Key", ex); //Will fail the update, so, we just ignore it here
			}
			addTask = new ImportTask();
			addTask.addPropertyChangeListener(listener);
			addTask.execute();
		} else if (apiType == ApiType.EVE_ONLINE) {
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
		} else if (apiType == ApiType.EVEKIT) {
			if (editEveKitOwner == null) { //Add
				eveKitOwner = new EveKitOwner(getAccessKey(), getAccessCred());
			} else { //Edit
				eveKitOwner = new EveKitOwner(getAccessKey(), getAccessCred(), editEveKitOwner);
			}
			addTask = new EveKitTask();
			addTask.addPropertyChangeListener(listener);
			addTask.execute();
		} else if (apiType == ApiType.ESI) {
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
		if (share == Share.EXPORT) {
			jNext.setText(DialoguesAccount.get().nextArrow());
		} else {
			jNext.setText(DialoguesAccount.get().ok());
		}
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
		if (apiType == ApiType.ESI && share != Share.EXPORT) {
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
				if (apiType == ApiType.ESI) {
					//Move to front
					getDialog().setAlwaysOnTop(true);
					getDialog().setAlwaysOnTop(false);
				}
				showDoneTab();
				break;
			case SHARE_IMPORT:
				apiType = ApiType.ESI;
				showImportTap();
				break;
			case SHARE_EXPORT:
				showExportTap();
				break;
			case EXIT:
				done();
				break;
		}
	}

	private void updateScopes() {
		scopesMap.clear();
		jScopes.removeAll();
		if (jCharacter.isSelected()) {
			jType.setText(DialoguesAccount.get().character());
		} else {
			jType.setText(DialoguesAccount.get().corporation());
		}
		for (EsiScopes scope : EsiScopes.values()) {
			if (scope.isPublicScope()) {
				continue;
			}
			if (jCharacter.isSelected() && !scope.isCharacterScope()) {
				continue;
			}
			if (jCorporation.isSelected() && !scope.isCorporationScope()) {
				continue;
			}
			JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(scope.toString());
			jCheckBoxMenuItem.setSelected(true);
			jCheckBoxMenuItem.setEnabled(!scope.isForced());
			jCheckBoxMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean enabled = false;
					for (Map.Entry<EsiScopes, JCheckBoxMenuItem> entry : scopesMap.entrySet()) {
						if (entry.getValue().isSelected() && !entry.getKey().isForced()) {
							enabled = true;
							break;
						}
					}
					jBrowse.setEnabled(enabled);
				}
			});
			jScopes.add(jCheckBoxMenuItem, true);
			scopesMap.put(scope, jCheckBoxMenuItem);
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
					case ADD_ESI: //Previous: Type
						currentCard = AccountImportCard.TYPE;
						break;
					case VALIDATE: //Previous: Add
						if (share == Share.IMPORT) {
							currentCard = AccountImportCard.SHARE_IMPORT;
						} else if (apiType == ApiType.EVEKIT) {
							currentCard = AccountImportCard.ADD_EVEKIT;
						} else if (apiType == ApiType.EVE_ONLINE) {
							currentCard = AccountImportCard.ADD_EVEAPI;
						} else if (apiType == ApiType.ESI) {
							currentCard = AccountImportCard.ADD_ESI;
						}
						addTask.cancel(true);
						break;
					case DONE: //Previous: Add
						if (share == Share.IMPORT) {
							currentCard = AccountImportCard.SHARE_IMPORT;
						} else if (apiType == ApiType.EVEKIT) {
							currentCard = AccountImportCard.ADD_EVEKIT;
						} else if (apiType == ApiType.EVE_ONLINE) {
							currentCard = AccountImportCard.ADD_EVEAPI;
						} else if (apiType == ApiType.ESI) {
							currentCard = AccountImportCard.ADD_ESI;
						}
						break;
					case SHARE_EXPORT:
						currentCard = AccountImportCard.DONE;
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
					case SHARE_IMPORT: //Next: Validate
						currentCard = AccountImportCard.VALIDATE;
						break;
					case VALIDATE: //Next Done
						currentCard = AccountImportCard.DONE;
						break;
					case DONE: //Next Exit
						if (share == Share.EXPORT) {
							currentCard = AccountImportCard.SHARE_EXPORT;
						} else {
							currentCard = AccountImportCard.EXIT;
						}						
						break;
					case SHARE_EXPORT:
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
			} else if (AccountImportAction.SHARE_TO_CLIPBOARD.name().equals(e.getActionCommand())) {
				Toolkit tk = Toolkit.getDefaultToolkit();
				StringSelection data = new StringSelection(jExport.getText());
				Clipboard cp = tk.getSystemClipboard();
				cp.setContents(data, null);
			} else if (AccountImportAction.SHARE_TO_FILE.name().equals(e.getActionCommand())) {
				File file = jFileChooser.getSelectedFile();
				if (file != null)  {
					jFileChooser.setSelectedFile(new File(""));
					jFileChooser.setCurrentDirectory(file.getParentFile());
				}
				int showSaveDialog = jFileChooser.showSaveDialog(getDialog());
				if (showSaveDialog == JCustomFileChooser.APPROVE_OPTION) {
					BufferedWriter writer = null;
					try {
						writer = new BufferedWriter(new FileWriter(jFileChooser.getSelectedFile()));
						writer.write(jExport.getText());
						writer.close();
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(getDialog(), GuiShared.get().textSaveFailMsg(), GuiShared.get().textSaveFailTitle(), JOptionPane.WARNING_MESSAGE);
					} finally {
						if (writer != null) {
							try {
								writer.close();
							} catch (IOException ex) {
								//Ohh well we tried our best
							}
						}
					}
				}
			} else if (AccountImportAction.SHARE_FROM_CLIPBOARD.name().equals(e.getActionCommand())) {
				Toolkit tk = Toolkit.getDefaultToolkit();
				Clipboard clipboard = tk.getSystemClipboard();
				Transferable transferable = clipboard.getContents(this);
				try {
					String s = (String) transferable.getTransferData(DataFlavor.stringFlavor);
					jImport.setText(s);
				} catch (UnsupportedFlavorException | IOException ex) {

				}
			} else if (AccountImportAction.SHARE_FROM_FILE.name().equals(e.getActionCommand())) {
				File file = jFileChooser.getSelectedFile();
				if (file != null)  {
					jFileChooser.setSelectedFile(new File(""));
					jFileChooser.setCurrentDirectory(file.getParentFile());
				}
				int showSaveDialog = jFileChooser.showOpenDialog(getDialog());
				if (showSaveDialog == JCustomFileChooser.APPROVE_OPTION) {
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(jFileChooser.getSelectedFile()));
						String line;
						StringBuilder builder = new StringBuilder();
						while ((line = reader.readLine()) != null) {
							builder.append(line);
							builder.append("\r\n");
						}
						jImport.setText(builder.toString());
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(getDialog(), GuiShared.get().textLoadFailMsg(),  GuiShared.get().textLoadFailTitle(), JOptionPane.WARNING_MESSAGE);
					} finally {
						try {
							if (reader != null) {
								reader.close();
							}
						} catch (IOException ex) {
							//Ohh well we tried our best
						}
					}
				}
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
			JLabel jEsiLabel = new JLabel(DialoguesAccount.get().esiDescription());
			JButton jEsi = new JButton(DialoguesAccount.get().esi(), Images.MISC_ESI.getIcon());
			Font font = new Font(jEsi.getFont().getName(), Font.BOLD, jEsi.getFont().getSize() + 5);
			jEsi.setActionCommand(AccountImportAction.ADD_ESI.name());
			jEsi.addActionListener(listener);
			jEsi.setIconTextGap(20);
			jEsi.setFont(font);
			jEsi.setHorizontalAlignment(JButton.LEADING);

			JLabel jEveKitLabel = new JLabel(DialoguesAccount.get().evekitDescription());
			JButton jEveKit = new JButton(DialoguesAccount.get().evekit(), Images.MISC_EVEKIT.getIcon());
			jEveKit.setActionCommand(AccountImportAction.ADD_EVEKIT.name());
			jEveKit.addActionListener(listener);
			jEveKit.setIconTextGap(20);
			jEveKit.setFont(font);
			jEveKit.setHorizontalAlignment(JButton.LEADING);

			JLabel jEveApiLabel = new JLabel(DialoguesAccount.get().eveapiDescription());
			JButton jEveApi = new JButton(DialoguesAccount.get().eveapi(), Images.MISC_EVE.getIcon());
			jEveApi.setActionCommand(AccountImportAction.ADD_EVEAPI.name());
			jEveApi.addActionListener(listener);
			jEveApi.setIconTextGap(20);
			jEveApi.setFont(font);
			jEveApi.setHorizontalAlignment(JButton.LEADING);

			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(jEveApi);
			buttonGroup.add(jEveKit);
			buttonGroup.add(jEsi);

			cardLayout.setHorizontalGroup(
				cardLayout.createParallelGroup()
					.addGroup(cardLayout.createSequentialGroup()
						.addGroup(cardLayout.createParallelGroup()
							.addComponent(jEsi, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
							.addComponent(jEveKit, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
							.addComponent(jEveApi, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
						)
						.addGap(10)
						.addGroup(cardLayout.createParallelGroup()
							.addComponent(jEsiLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
							.addComponent(jEveKitLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
							.addComponent(jEveApiLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
						)
					)
			);
			cardLayout.setVerticalGroup(
				cardLayout.createSequentialGroup()
					.addGroup(cardLayout.createParallelGroup()
						.addComponent(jEsi, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
						.addComponent(jEsiLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					)
					.addGroup(cardLayout.createParallelGroup()
						.addComponent(jEveKit, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
						.addComponent(jEveKitLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					)
					.addGroup(cardLayout.createParallelGroup()
						.addComponent(jEveApi, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
						.addComponent(jEveApiLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					)
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
			jHelp.addHyperlinkListener(DesktopUtil.getHyperlinkListener(getDialog()));

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
			jHelp.addHyperlinkListener(DesktopUtil.getHyperlinkListener(getDialog()));

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
						boolean ok = esiAuth.openWebpage(EsiCallbackURL.LOCALHOST, scopes, getDialog());
						if (ok) { //Wait for response
							currentCard = AccountImportCard.VALIDATE;
							updateTab();
						}
					} else {
						boolean ok = esiAuth.openWebpage(EsiCallbackURL.EVE_NIKR_NET, scopes, getDialog());
						if (ok) {
							jAuthCode.setEnabled(true);
							jNext.setEnabled(true);
						}
					}
				}
			});

			jType = new JDropDownButton(JDropDownButton.LEFT, JDropDownButton.TOP);
			jType.setText(DialoguesAccount.get().character());

			jCorporation = new JRadioButtonMenuItem(DialoguesAccount.get().corporation());
			jCorporation.setSelected(false);
			jCorporation.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateScopes();
				}
			});
			jType.add(jCorporation);

			jCharacter = new JRadioButtonMenuItem(DialoguesAccount.get().character());
			jCharacter.setSelected(true);
			jCharacter.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateScopes();
				}
			});
			jType.add(jCharacter);

			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(jCorporation);
			buttonGroup.add(jCharacter);

			jScopes = new JDropDownButton(DialoguesAccount.get().scopes(), Images.MISC_ESI.getIcon(), JDropDownButton.LEFT, JDropDownButton.TOP);

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
			jHelp.addHyperlinkListener(DesktopUtil.getHyperlinkListener(getDialog()));

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
								.addComponent(jType, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
								.addComponent(jScopes, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
								.addComponent(jBrowse, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
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
					.addComponent(jType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScopes, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBrowse, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(cardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jAuthCodeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAuthCode, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
			jHelp.addHyperlinkListener(DesktopUtil.getHyperlinkListener(getDialog()));
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

	private class ImportPanel extends JCardPanel {

		public ImportPanel() {
			JEditorPane jHelp = new JEditorPane("text/html", "<html><body style=\"font-family: " + getFont().getName() + "; font-size: " + getFont().getSize() + "pt\">"
				+ DialoguesAccount.get().shareImportHelp() + "</body></html>");
			((HTMLDocument) jHelp.getDocument()).getStyleSheet().addRule("body { font-family: " + getFont().getFamily() + "; " + "font-size: " + this.getFont().getSize() + "pt; }");
			jHelp.setFont(getFont());
			jHelp.setEditable(false);
			jHelp.setFocusable(false);
			jHelp.setOpaque(false);

			jImportClipboard = new JButton(DialoguesAccount.get().shareImportClipboard() , Images.EDIT_PASTE.getIcon());
			jImportClipboard.setActionCommand(AccountImportAction.SHARE_FROM_CLIPBOARD.name());
			jImportClipboard.addActionListener(listener);

			jImportFile= new JButton(DialoguesAccount.get().shareImportFile(), Images.FILTER_LOAD.getIcon());
			jImportFile.setActionCommand(AccountImportAction.SHARE_FROM_FILE.name());
			jImportFile.addActionListener(listener);

			jImport = new JTextArea();
			jImport.setFont(getFont());
			jImport.setEditable(true);
			jImport.setFocusable(true);
			jImport.setOpaque(true);
			jImport.setLineWrap(true);
			jImport.setWrapStyleWord(false);

			JScrollPane jScroll = new JScrollPane(jImport, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScroll.setBorder(BorderFactory.createLineBorder(this.getBackground().darker(), 1));

			cardLayout.setHorizontalGroup(
				cardLayout.createParallelGroup()
					.addComponent(jHelp)
					.addGroup(cardLayout.createSequentialGroup()
						.addComponent(jScroll)
						.addGroup(cardLayout.createParallelGroup()
							.addComponent(jImportClipboard, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
							.addComponent(jImportFile, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						)
					)
			);
			cardLayout.setVerticalGroup(
				cardLayout.createSequentialGroup()
					.addComponent(jHelp)
					.addGroup(cardLayout.createParallelGroup()
						.addComponent(jScroll)
						.addGroup(cardLayout.createSequentialGroup()
							.addComponent(jImportClipboard, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
							.addComponent(jImportFile, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						)
					)
			);
		}
		
	}

	private class ExportPanel extends JCardPanel {

		public ExportPanel() {
			JLabel jHelp = new JLabel(DialoguesAccount.get().shareExportHelp() );

			jExportClipboard = new JButton(DialoguesAccount.get().shareExportClipboard(), Images.EDIT_COPY.getIcon());
			jExportClipboard.setActionCommand(AccountImportAction.SHARE_TO_CLIPBOARD.name());
			jExportClipboard.addActionListener(listener);

			jExportFile = new JButton(DialoguesAccount.get().shareExportFile(), Images.FILTER_SAVE.getIcon());
			jExportFile.setActionCommand(AccountImportAction.SHARE_TO_FILE.name());
			jExportFile.addActionListener(listener);

			jExport = new JTextArea();
			jExport.setFont(getFont());
			jExport.setEditable(false);
			jExport.setFocusable(true);
			jExport.setOpaque(false);
			jExport.setLineWrap(true);
			jExport.setWrapStyleWord(false);

			JScrollPane jScroll = new JScrollPane(jExport, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScroll.setBorder(BorderFactory.createLineBorder(this.getBackground().darker(), 1));

			cardLayout.setHorizontalGroup(
				cardLayout.createParallelGroup()
					.addComponent(jHelp)
					.addGroup(cardLayout.createSequentialGroup()
						.addComponent(jScroll)
						.addGroup(cardLayout.createParallelGroup()
							.addComponent(jExportClipboard, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
							.addComponent(jExportFile, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						)
					)
			);
			cardLayout.setVerticalGroup(
				cardLayout.createSequentialGroup()
					.addComponent(jHelp)
					.addGroup(cardLayout.createParallelGroup()
						.addComponent(jScroll)
						.addGroup(cardLayout.createSequentialGroup()
							.addComponent(jExportClipboard, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
							.addComponent(jExportFile, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						)
					)
			);
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

		private final AccountGetter accountGetter = new AccountGetter(account, true);

		@Override
		public boolean exist() {
			return program.getProfileManager().getAccounts().contains(account) && !account.isExpired();
		}

		@Override
		public void load() {
			accountGetter.start(); //Update account
		}

		@Override
		public AccountAdder getAccountAdder() {
			return accountGetter;
		}
	}

	class EveKitTask extends AddTask {

		private final EveKitOwnerGetter eveKitOwnerGetter = new EveKitOwnerGetter(eveKitOwner, true);

		@Override
		public boolean exist() {
			return program.getProfileManager().getEveKitOwners().contains(eveKitOwner);
		}

		@Override
		public void load() {
			eveKitOwnerGetter.start();
		}

		@Override
		public AccountAdder getAccountAdder() {
			return eveKitOwnerGetter;
		}
	}

	class ImportTask extends AddTask {

		private final EsiOwnerGetter esiOwnerGetter = new EsiOwnerGetter(esiOwner, true);

		@Override
		public boolean exist() {
			return false; //Each ESI account are unique
		}

		@Override
		public void load() {
			esiOwnerGetter.start();
		}

		@Override
		public AccountAdder getAccountAdder() {
			return esiOwnerGetter;
		}
	}

	class EsiTask extends AddTask {

		private final EsiOwnerGetter esiOwnerGetter = new EsiOwnerGetter(esiOwner, true);
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
			esiOwnerGetter.start();
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
			} catch (InterruptedException | ExecutionException ex) {
				LOG.error(ex.getMessage(), ex);
				throw new RuntimeException(ex);
			}
			done = true;
			setProgress(100);
		}
	}
}
