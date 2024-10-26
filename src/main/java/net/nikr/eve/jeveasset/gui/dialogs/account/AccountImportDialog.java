/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.ApiType;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.settings.Colors;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CopyHandler;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JLabelMultiline;
import net.nikr.eve.jeveasset.gui.shared.components.JLabelMultilineHtml;
import net.nikr.eve.jeveasset.gui.shared.components.JWorking;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.esi.EsiAuth;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.nikr.eve.jeveasset.io.esi.EsiOwnerGetter;
import net.nikr.eve.jeveasset.io.esi.EsiScopes;
import net.nikr.eve.jeveasset.io.shared.AccountAdder;
import net.nikr.eve.jeveasset.io.shared.AccountAdderAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AccountImportDialog extends JDialogCentered {

	private static final Logger LOG = LoggerFactory.getLogger(AccountImportDialog.class);

	private enum AccountImportAction {
		ADD_KEY_CANCEL,
		SHARE_TO_CLIPBOARD,
		SHARE_TO_FILE,
		SHARE_FROM_CLIPBOARD,
		SHARE_FROM_FILE,
		NEXT,
		PREVIOUS
	}

	private enum AccountImportCard {
		ADD_ESI,
		SHARE_EXPORT,
		SHARE_IMPORT,
		AUTHCODE,
		BROWSER,
		VALIDATE,
		DONE,
		EXIT
	}

	private final AccountManagerDialog apiManager;

	private enum Result {
		FAIL_API_FAIL,
		FAIL_INVALID,
		FAIL_NOT_ENOUGH_PRIVILEGES,
		FAIL_WRONG_ENTRY,
		FAIL_CANCEL,
		OK_EXIST, //OK
		OK_EXIST_LIMITED_ACCESS, //OK
		OK_LIMITED_ACCESS, //OK
		OK_ACCOUNT_VALID //OK
	}

	private enum Share {
		IMPORT, EXPORT
	}

	//EsiPanel
	private JDropDownButton jScopes;
	private JRadioButton jCharacter;
	private JRadioButton jCorporation;
	private JLabel jCharacterLabel;
	private JLabel jCorporationLabel;
	private JCheckBox jWorkaround;
	//AuthCodePanel
	private JTextField jAuthCode;
	//ExportPanel
	private JTextArea jExport;
	private JButton jExportClipboard;
	private JButton jExportFile;
	//ImportPanel
	private JTextArea jImport;
	private JButton jImportClipboard;
	private JButton jImportFile;
	//Done
	private JLabel jResultHeader;
	private JLabelMultilineHtml jResultText;
	//Main
	private final JButton jNext;
	private final JButton jPrevious;
	private final CardLayout cardLayout;
	private final JPanel jContent;
	private final ListenerClass listener = new ListenerClass();
	private final EsiAuth esiAuth = new EsiAuth();
	private final JCustomFileChooser jFileChooser;

	private EsiOwner esiOwner;
	private EsiOwner editEsiOwner;
	private AccountImportCard currentCard;
	private ApiType apiType;
	private Share share;
	private AddTask addTask;
	private final Map<EsiScopes, JCheckBoxMenuItem> scopesMap = new EnumMap<>(EsiScopes.class);

	public AccountImportDialog(final AccountManagerDialog apiManager, final Program program) {
		super(program, DialoguesAccount.get().dialogueNameAccountImport(), apiManager.getDialog());
		this.apiManager = apiManager;

		this.getDialog().addWindowFocusListener(listener);

		jFileChooser = JCustomFileChooser.createFileChooser(getDialog(), "txt");
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		cardLayout = new CardLayout();
		jContent = new JPanel(cardLayout);
		jContent.add(new ImportPanel(), AccountImportCard.SHARE_IMPORT.name());
		jContent.add(new EsiPanel(), AccountImportCard.ADD_ESI.name());
		jContent.add(new AuthCodePanel(), AccountImportCard.AUTHCODE.name());
		jContent.add(new BrowserPanel(), AccountImportCard.BROWSER.name());
		jContent.add(new ValidatePanel(), AccountImportCard.VALIDATE.name());
		jContent.add(new ExportPanel(), AccountImportCard.SHARE_EXPORT.name());
		jContent.add(new DonePanel(), AccountImportCard.DONE.name());

		jPrevious = new JButton(DialoguesAccount.get().previousArrow());
		jPrevious.setActionCommand(AccountImportAction.PREVIOUS.name());
		jPrevious.addActionListener(listener);

		jNext = new JButton(DialoguesAccount.get().nextArrow());
		jNext.setActionCommand(AccountImportAction.NEXT.name());
		jNext.addActionListener(listener);

		JButton jCancel = new JButton(DialoguesAccount.get().cancel());
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

	private String getAuthCode() {
		return jAuthCode.getText().trim();
	}

	private void focus() {
		if (currentCard == AccountImportCard.AUTHCODE) {
			jAuthCode.requestFocusInWindow();
		} else if (jNext.isEnabled()) {
			jNext.requestFocusInWindow();
		} else if (jPrevious.isEnabled()) {
			jNext.requestFocusInWindow();
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jNext;
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
		show(null, AccountImportCard.ADD_ESI, null);
	}

	public void shareExport() {
		show(Share.EXPORT, AccountImportCard.ADD_ESI, null);
	}

	public void shareImport() {
		show(Share.IMPORT, AccountImportCard.SHARE_IMPORT, null);
	}

	public void editEsi(final EsiOwner editEsiOwner) {
		show(null, AccountImportCard.ADD_ESI, editEsiOwner);
	}

	private void show(Share share, AccountImportCard accountImportCard, final EsiOwner editEsiOwner) {
		currentCard = accountImportCard;
		this.share = share;
		this.editEsiOwner = editEsiOwner;
		if (editEsiOwner != null) { //Edit ESI
			jCharacter.setVisible(false);
			jCorporation.setVisible(false);
			if (editEsiOwner.isCorporation()) {
				jCorporationLabel.setVisible(true);
				jCharacterLabel.setVisible(false);
				jCorporation.setSelected(true);
			} else {
				jCharacterLabel.setVisible(true);
				jCorporationLabel.setVisible(false);
				jCharacter.setSelected(true);
			}
		} else { //Add
			jImport.setText("");
			jCharacter.setVisible(true);
			jCorporation.setVisible(true);
			jCharacterLabel.setVisible(false);
			jCorporationLabel.setVisible(false);
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

	private Set<String> getScopes() {
		Set<String> scopes = new HashSet<>();
		for (Map.Entry<EsiScopes, JCheckBoxMenuItem> entry : scopesMap.entrySet()) {
			if (entry.getValue().isSelected()) {
				scopes.add(entry.getKey().getScope());
			}
		}
		return scopes;
	}

	private void startImport() {
		if (share == Share.IMPORT) {
			esiOwner = new EsiOwner();
			try {
				String code = new String(Base64.getUrlDecoder().decode(jImport.getText().trim()), "UTF-8");
				String[] codes = code.split(" ");
				esiOwner.setAuth(EsiCallbackURL.valueOf(codes[0]), codes[1], null);
			} catch (UnsupportedEncodingException | IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
				LOG.error("Failed to import jEveAssets ESI Key", ex); //Will fail the update, so, we just ignore it here
			}
			addTask = new ImportTask();
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

	private void showEsiTap() {
		cardLayout.show(jContent, AccountImportCard.ADD_ESI.name());
		jPrevious.setEnabled(false);
		jWorkaround.setSelected(false);
		jNext.setEnabled(true);
		jNext.setText(DialoguesAccount.get().nextArrow());
		getDialog().setIconImage(Images.MISC_ESI.getImage());
		if (share == Share.EXPORT) {
			getDialog().setTitle(DialoguesAccount.get().dialogueNameAccountExport());
		} else {
			getDialog().setTitle(DialoguesAccount.get().dialogueNameAccountImport());
		}
		esiAuth.cancelImport();
		updateScopes();
		focus();
	}

	private void showImportTap() {
		cardLayout.show(jContent, AccountImportCard.SHARE_IMPORT.name());
		jPrevious.setEnabled(false);
		jNext.setEnabled(true);
		jNext.setText(DialoguesAccount.get().nextArrow());
		getDialog().setIconImage(Images.MISC_ESI.getImage());
		getDialog().setTitle(DialoguesAccount.get().dialogueNameAccountImport());
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

	private void showAuthCodeTab() {
		cardLayout.show(jContent, AccountImportCard.AUTHCODE.name());
		jAuthCode.setText("");
		jPrevious.setEnabled(true);
		jNext.setEnabled(true);
		jNext.setText(DialoguesAccount.get().nextArrow());
	}

	private void showBrowserTab() {
		cardLayout.show(jContent, AccountImportCard.BROWSER.name());
		jPrevious.setEnabled(true);
		jNext.setEnabled(false);
		jNext.setText(DialoguesAccount.get().nextArrow());
		startImport();
	}

	private void showValidateTab() {
		cardLayout.show(jContent, AccountImportCard.VALIDATE.name());
		jPrevious.setEnabled(true);
		jNext.setEnabled(false);
		jNext.setText(DialoguesAccount.get().nextArrow());
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
		if (apiType == ApiType.ESI && share != Share.EXPORT) {
			List<EsiOwner> existingEsiOwners = getExistingEsiOwners();
			if (!existingEsiOwners.isEmpty()) { //Update
				for (EsiOwner owner : existingEsiOwners) {
					owner.updateAuth(esiOwner);
				}
			} else {
				if (editEsiOwner != null) { //Add & Edit
					program.getProfileManager().getEsiOwners().remove(editEsiOwner);
				}
				program.getProfileManager().getEsiOwners().add(esiOwner);
			}
			apiManager.forceUpdate();
			apiManager.updateTable();
		}
		this.setVisible(false);
	}

	private void updateTab() {
		switch (currentCard) {
			case ADD_ESI:
				apiType = ApiType.ESI;
				showEsiTap();
				break;
			case AUTHCODE:
				showAuthCodeTab();
				break;
			case BROWSER:
				showBrowserTab();
				break;
			case VALIDATE:
				if (apiType == ApiType.ESI) {
					//Move to front
					getDialog().setAlwaysOnTop(true);
					getDialog().setAlwaysOnTop(false);
				}
				showValidateTab();
				break;
			case DONE:
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
					jNext.setEnabled(enabled);
				}
			});
			jScopes.add(jCheckBoxMenuItem, true);
			scopesMap.put(scope, jCheckBoxMenuItem);
		}
	}

	private List<EsiOwner> getExistingEsiOwners() {
		List<EsiOwner> esiOwners = new ArrayList<>();
		for (EsiOwner owner : program.getProfileManager().getEsiOwners()) {
			if (Objects.equals(owner.getOwnerID(), esiOwner.getOwnerID())) {
				esiOwners.add(owner);
			}
		}
		return esiOwners;
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
					case ADD_ESI: //Previous: None
						currentCard = AccountImportCard.ADD_ESI;
						break;
					case AUTHCODE: //Previous: Add
						if (share == Share.IMPORT) {
							currentCard = AccountImportCard.SHARE_IMPORT;
						} else if (apiType == ApiType.ESI) {
							currentCard = AccountImportCard.ADD_ESI;
						}
						if (addTask != null) {
							addTask.cancel(true);
						}
						break;
					case BROWSER: //Previous: Add
						if (share == Share.IMPORT) {
							currentCard = AccountImportCard.SHARE_IMPORT;
						} else if (apiType == ApiType.ESI) {
							currentCard = AccountImportCard.ADD_ESI;
						}
						if (addTask != null) {
							addTask.cancel(true);
						}
						break;
					case VALIDATE: //Previous: Add
						if (share == Share.IMPORT) {
							currentCard = AccountImportCard.SHARE_IMPORT;
						} else if (apiType == ApiType.ESI) {
							currentCard = AccountImportCard.ADD_ESI;
						}
						if (addTask != null) {
							addTask.cancel(true);
						}
						break;
					case DONE: //Previous: Add
						if (share == Share.IMPORT) {
							currentCard = AccountImportCard.SHARE_IMPORT;
						} else if (apiType == ApiType.ESI) {
							currentCard = AccountImportCard.ADD_ESI;
						}
						break;
					case SHARE_EXPORT:
						if (jResultHeader.getText().isEmpty()) {
							currentCard = AccountImportCard.ADD_ESI;
						} else {
							currentCard = AccountImportCard.DONE;
						}
						break;
					case EXIT: //Previous: Exit
						currentCard = AccountImportCard.EXIT;
						break;
				}
				updateTab();
			} else if (AccountImportAction.NEXT.name().equals(e.getActionCommand())) {
				switch (currentCard) {
					case ADD_ESI: //Next: Validate
						Set<String> scopes = getScopes();
						if (esiAuth.isServerStarted() && !jWorkaround.isSelected()) { //Localhost
							esiAuth.openWebpage(EsiCallbackURL.LOCALHOST, scopes, getDialog());
							currentCard = AccountImportCard.BROWSER;
						} else {
							esiAuth.openWebpage(EsiCallbackURL.EVE_NIKR_NET, scopes, getDialog());
							currentCard = AccountImportCard.AUTHCODE;
						}
						break;
					case SHARE_IMPORT: //Next: Browser
						currentCard = AccountImportCard.VALIDATE;
						startImport();
						break;
					case AUTHCODE: //Next Validate
						currentCard = AccountImportCard.VALIDATE;
						startImport();
						break;
					case BROWSER: //Next Validate
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
			} else if (AccountImportAction.SHARE_TO_CLIPBOARD.name().equals(e.getActionCommand())) {
				CopyHandler.toClipboard(jExport.getText());
			} else if (AccountImportAction.SHARE_TO_FILE.name().equals(e.getActionCommand())) {
				File file = jFileChooser.getSelectedFile();
				if (file != null) {
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
				String s = CopyHandler.fromClipboard();
				if (s != null) {
					jImport.setText(s);
				}
			} else if (AccountImportAction.SHARE_FROM_FILE.name().equals(e.getActionCommand())) {
				File file = jFileChooser.getSelectedFile();
				if (file != null) {
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
						JOptionPane.showMessageDialog(getDialog(), GuiShared.get().textLoadFailMsg(), GuiShared.get().textLoadFailTitle(), JOptionPane.WARNING_MESSAGE);
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
						case FAIL_API_FAIL:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(false);
							jResultHeader.setText(DialoguesAccount.get().failApiError());
							jResultText.setText(DialoguesAccount.get().failApiErrorText(addTask.error));
							break;
						case FAIL_INVALID:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(false);
							jResultHeader.setText(DialoguesAccount.get().failNotValid());
							jResultText.setText(DialoguesAccount.get().failNotValidText());
							break;
						case FAIL_NOT_ENOUGH_PRIVILEGES:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(false);
							jResultHeader.setText(DialoguesAccount.get().failNotEnoughPrivileges());
							jResultText.setText(DialoguesAccount.get().failNotEnoughPrivilegesText());
							break;
						case FAIL_WRONG_ENTRY:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(false);
							jResultHeader.setText(DialoguesAccount.get().failWrongEntry());
							jResultText.setText(DialoguesAccount.get().failWrongEntryText());
							break;
						case FAIL_CANCEL:
							switch(apiType) {
								case ESI:
									currentCard = AccountImportCard.ADD_ESI;
									break;
							}
							break;
						case OK_EXIST:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(true);
							jResultHeader.setText(DialoguesAccount.get().okUpdate());
							jResultText.setText(DialoguesAccount.get().okUpdateText());
							break;
						case OK_EXIST_LIMITED_ACCESS:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(true);
							jResultHeader.setText(DialoguesAccount.get().okUpdate());
							jResultText.setText(DialoguesAccount.get().okUpdateLimitedText());
							break;
						case OK_LIMITED_ACCESS:
							currentCard = AccountImportCard.DONE;
							jNext.setEnabled(true);
							jResultHeader.setText(DialoguesAccount.get().okLimited());
							if (share == Share.EXPORT) {
								jResultText.setText(DialoguesAccount.get().okLimitedExportText());
							} else {
								jResultText.setText(DialoguesAccount.get().okLimitedText());
							}
							break;
						case OK_ACCOUNT_VALID:
							jNext.setEnabled(true);
							if (share == Share.EXPORT) {
								currentCard = AccountImportCard.SHARE_EXPORT;
								jResultHeader.setText("");
								jResultText.setText("");
							} else {
								currentCard = AccountImportCard.DONE;
								jResultHeader.setText(DialoguesAccount.get().okValid());
								jResultText.setText(DialoguesAccount.get().okValidText());
							}
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

	private class EsiPanel extends JCardPanel {

		public EsiPanel() {
			JLabel jAuthLabel = new JLabel(DialoguesAccount.get().authorize());
			JLabel jType = new JLabel(DialoguesAccount.get().accountType());
			ButtonGroup type = new ButtonGroup();
			jCharacterLabel = new JLabel(DialoguesAccount.get().character());
			jCharacter = new JRadioButton(DialoguesAccount.get().character());
			jCharacter.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateScopes();
				}
			});
			type.add(jCharacter);
			jCorporationLabel = new JLabel(DialoguesAccount.get().corporation());
			jCorporation = new JRadioButton(DialoguesAccount.get().corporation());
			jCorporation.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateScopes();
				}
			});
			type.add(jCorporation);

			jScopes = new JDropDownButton(DialoguesAccount.get().scopes(), Images.MISC_ESI.getIcon(), JDropDownButton.LEFT, JDropDownButton.TOP);

			JLabel jWorkaroundLabel = new JLabel(DialoguesAccount.get().workaroundLabel());
			jWorkaround = new JCheckBox(DialoguesAccount.get().workaroundCheckbox());

			JLabelMultiline jHelp = new JLabelMultiline(DialoguesAccount.get().esiHelpText());

			layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addComponent(jHelp)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jAuthLabel)
							.addComponent(jType)
							.addComponent(jWorkaroundLabel)

						)
						.addGroup(layout.createParallelGroup()
							.addGroup(layout.createSequentialGroup()
								.addComponent(jCharacter)
								.addComponent(jCorporation)
								.addComponent(jCharacterLabel)
								.addComponent(jCorporationLabel)
							)
							.addComponent(jScopes, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
							.addComponent(jWorkaround)
						)
					)
			);
			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(jHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(jType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jCharacter, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jCorporation, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jCharacterLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jCorporationLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(jAuthLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jScopes, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(jWorkaroundLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jWorkaround, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())	
					)
			);
		}
	}

	private class AuthCodePanel extends JCardPanel {

		public AuthCodePanel() {
			JLabel jAuthCodeLabel = new JLabel(DialoguesAccount.get().authCode());
			jAuthCode = new JTextField();

			JLabelMultiline jHelp = new JLabelMultiline(DialoguesAccount.get().authCodeHelpText());

			layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addComponent(jHelp)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jAuthCodeLabel)
						.addComponent(jAuthCode, 150, 150, Integer.MAX_VALUE)
					)
			);
			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(jHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(jAuthCodeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jAuthCode, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
					.addGap(10, 10, Integer.MAX_VALUE)
			);
		}
	}

	private class BrowserPanel extends JCardPanel {

		public BrowserPanel() {
			JLabelMultiline jHelp = new JLabelMultiline(DialoguesAccount.get().browseHelpText());

			layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addGap(10, 10, Integer.MAX_VALUE)
					.addComponent(jHelp)
					.addGap(10, 10, Integer.MAX_VALUE)
			);
			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addGap(10, 10, Integer.MAX_VALUE)
					.addComponent(jHelp)
					.addGap(10, 10, Integer.MAX_VALUE)
			);
		}
	}

	private class ValidatePanel extends JCardPanel {

		public ValidatePanel() {
			JLabel jHelp = new JLabel(DialoguesAccount.get().validatingMessage());

			JWorking jWorking = new JWorking();

			layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addGroup(layout.createSequentialGroup()
						.addGap(10, 10, Integer.MAX_VALUE)
						.addComponent(jWorking)
						.addGap(10, 10, Integer.MAX_VALUE)
					)
					.addGroup(layout.createSequentialGroup()
						.addGap(10, 10, Integer.MAX_VALUE)
						.addComponent(jHelp)
						.addGap(10, 10, Integer.MAX_VALUE)
					)
			);
			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addGap(10, 10, Integer.MAX_VALUE)
					.addComponent(jWorking)
					.addComponent(jHelp)
					.addGap(10, 10, Integer.MAX_VALUE)
			);
		}
	}

	private class DonePanel extends JCardPanel {

		public DonePanel() {
			jResultHeader = new JLabel();
			jResultHeader.setFont(new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize()));

			jResultText = new JLabelMultilineHtml(getDialog());
			jResultText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			JScrollPane jScroll = new JScrollPane(jResultText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScroll.setBorder(BorderFactory.createLineBorder(this.getBackground().darker(), 1));

			layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
						.addGap(5)
						.addComponent(jResultHeader)
					)
					.addComponent(jScroll, 400, 400, 400)
			);
			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(jResultHeader, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jScroll, 98, 98, 98)
			);
		}

	}

	private class ImportPanel extends JCardPanel {

		public ImportPanel() {
			JLabelMultiline jHelp = new JLabelMultiline(DialoguesAccount.get().shareImportHelpText());

			jImportClipboard = new JButton(DialoguesAccount.get().shareImportClipboard() , Images.EDIT_PASTE.getIcon());
			jImportClipboard.setActionCommand(AccountImportAction.SHARE_FROM_CLIPBOARD.name());
			jImportClipboard.addActionListener(listener);

			jImportFile = new JButton(DialoguesAccount.get().shareImportFile(), Images.FILTER_LOAD.getIcon());
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

			layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addComponent(jHelp)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jScroll)
						.addGroup(layout.createParallelGroup()
							.addComponent(jImportClipboard, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
							.addComponent(jImportFile, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						)
					)
			);
			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(jHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup()
						.addComponent(jScroll)
						.addGroup(layout.createSequentialGroup()
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
			jExport.setBackground(Colors.COMPONENT_TRANSPARENT.getColor());
			jExport.setLineWrap(true);
			jExport.setWrapStyleWord(false);

			JScrollPane jScroll = new JScrollPane(jExport, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScroll.setBorder(BorderFactory.createLineBorder(this.getBackground().darker(), 1));

			layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addComponent(jHelp)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jScroll)
						.addGroup(layout.createParallelGroup()
							.addComponent(jExportClipboard, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
							.addComponent(jExportFile, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						)
					)
			);
			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(jHelp)
					.addGroup(layout.createParallelGroup()
						.addComponent(jScroll)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jExportClipboard, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
							.addComponent(jExportFile, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						)
					)
			);
		}
	}

	private abstract class JCardPanel extends JPanel {

		protected GroupLayout layout;

		public JCardPanel() {
			layout = new GroupLayout(this);
			setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(false);
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

	class ImportTask extends AddTask {

		private final EsiOwnerGetter esiOwnerGetter = new EsiOwnerGetter(esiOwner, true);

		@Override
		public boolean exist() {
			if (editEsiOwner != null) {
				return false;
			}
			for (EsiOwner owner : program.getProfileManager().getEsiOwners()) {
				if (Objects.equals(owner.getOwnerID(), esiOwner.getOwnerID())) {
					return true;
				}
			}
			return false;
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
			if (editEsiOwner != null) {
				return false;
			}
			if (share == Share.EXPORT) {
				return false;
			}
			for (EsiOwner owner : program.getProfileManager().getEsiOwners()) {
				if (Objects.equals(owner.getOwnerID(), esiOwner.getOwnerID())) {
					return true;
				}
			}
			return false;
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
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					currentCard = AccountImportCard.VALIDATE;
					updateTab();
				}
				
			});
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
			load();
			if (getAccountAdder().hasError() || getAccountAdder().isPrivilegesInvalid()) { //Failed to add new account
				String s = getAccountAdder().getError();
				if (getAccountAdder().isInvalid()) { //invalid account
					result = Result.FAIL_INVALID;
				} else if (getAccountAdder().isPrivilegesInvalid()) { // Not enough privileges
					result = Result.FAIL_NOT_ENOUGH_PRIVILEGES;
				} else if (getAccountAdder().isWrongEntry()) { // Editing account to a different character/corporation
					result = Result.FAIL_WRONG_ENTRY;
				} else { //String error
					error = s;
					result = Result.FAIL_API_FAIL;
				}
			} else { //Successfully added new account
				if (exist()) { //account already exist
					if (getAccountAdder().isPrivilegesLimited()) {
						result = Result.OK_EXIST_LIMITED_ACCESS; //limited privileges
					} else {
						result = Result.OK_EXIST;
					}
				} else if (getAccountAdder().isPrivilegesLimited()) { //limited privileges
					result = Result.OK_LIMITED_ACCESS;
				} else { //All okay
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
