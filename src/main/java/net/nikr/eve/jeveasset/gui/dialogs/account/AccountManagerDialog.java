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

package net.nikr.eve.jeveasset.gui.dialogs.account;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.DeprecatedOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.gui.dialogs.account.AccountSeparatorTableCell.AccountCellAction;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow.LockWorkerAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.troja.eve.esi.auth.OAuth;


public class AccountManagerDialog extends JDialogCentered {

	private enum AccountManagerAction {
		ADD,
		REVALIDATE,
		SHARE_EXPORT,
		SHARE_IMPORT,
		CLOSE,
		COLLAPSE,
		EXPAND,
		CHECK_ALL,
		UNCHECK_ALL,
		CHECK_SELECTED,
		UNCHECK_SELECTED
	}

	//GUI
	private final AccountImportDialog accountImportDialog;
	private final JSeparatorTable jTable;
	private final JButton jAdd;
	private final JButton jRevalidate;
	private final JButton jExpand;
	private final JButton jCollapse;
	private final JDropDownButton jAssets;
	private final JButton jClose;
	private final EventList<OwnerType> eventList;
	private final DefaultEventTableModel<OwnerType> tableModel;
	private final SeparatorList<OwnerType> separatorList;
	private final DefaultEventSelectionModel<OwnerType> selectionModel;
	private final JMigrateDialog jMigrateDialog;
	private final JLockWindow jLockWindow;
	private final JLockWindow jRevalidateLock;
	private final Map<OwnerType, Boolean> ownersShownCache = new HashMap<>();

	private boolean updated = false;

	public AccountManagerDialog(final Program program) {
		super(program, DialoguesAccount.get().dialogueNameAccountManagement(), Images.DIALOG_ACCOUNTS.getImage());

		accountImportDialog = new AccountImportDialog(this, program);

		jMigrateDialog = new JMigrateDialog(program, this);

		jLockWindow = new JLockWindow(program.getMainWindow().getFrame());
		jRevalidateLock = new JLockWindow(getDialog());

		ListenerClass listener = new ListenerClass();

		eventList = EventListManager.create();

		eventList.getReadWriteLock().readLock().lock();
		separatorList = new SeparatorList<>(eventList, new SeparatorListComparator(), 1, 3);
		eventList.getReadWriteLock().readLock().unlock();

		tableModel = EventModels.createTableModel(separatorList, TableFormatFactory.accountTableFormat());
		jTable = new JAccountTable(program, tableModel, separatorList);
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.setSeparatorRenderer(new AccountSeparatorTableCell(this, listener, jTable, separatorList));
		jTable.setSeparatorEditor(new AccountSeparatorTableCell(this, listener, jTable, separatorList));

		JScrollPane jTableScroll = new JScrollPane(jTable);
		jTableScroll.getVerticalScrollBar().setUnitIncrement(19);

		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);

		//Buttons
		jAdd = new JButton(DialoguesAccount.get().add());
		jAdd.setActionCommand(AccountManagerAction.ADD.name());
		jAdd.addActionListener(listener);

		jRevalidate = new JButton(DialoguesAccount.get().revalidate());
		jRevalidate.setActionCommand(AccountManagerAction.REVALIDATE.name());
		jRevalidate.addActionListener(listener);

		JDropDownButton jShare = new JDropDownButton(DialoguesAccount.get().share());

		JMenuItem jExport = new JMenuItem(DialoguesAccount.get().shareExport(), Images.MISC_ESI.getIcon());
		jExport.setActionCommand(AccountManagerAction.SHARE_EXPORT.name());
		jExport.addActionListener(listener);
		jShare.add(jExport);

		JMenuItem jImport = new JMenuItem(DialoguesAccount.get().shareImport(), Images.TOOL_ASSETS.getIcon());
		jImport.setActionCommand(AccountManagerAction.SHARE_IMPORT.name());
		jImport.addActionListener(listener);
		jShare.add(jImport);

		jCollapse = new JButton(DialoguesAccount.get().collapse());
		jCollapse.setActionCommand(AccountManagerAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);

		jExpand = new JButton(DialoguesAccount.get().expand());
		jExpand.setActionCommand(AccountManagerAction.EXPAND.name());
		jExpand.addActionListener(listener);

		jAssets = new JDropDownButton(DialoguesAccount.get().showAssets());
		JMenuItem menuItem;

		menuItem = new JMenuItem(DialoguesAccount.get().checkAll());
		menuItem.setActionCommand(AccountManagerAction.CHECK_ALL.name());
		menuItem.addActionListener(listener);
		jAssets.add(menuItem);

		menuItem = new JMenuItem(DialoguesAccount.get().uncheckAll());
		menuItem.setActionCommand(AccountManagerAction.UNCHECK_ALL.name());
		menuItem.addActionListener(listener);
		jAssets.add(menuItem);

		jAssets.addSeparator();

		menuItem = new JMenuItem(DialoguesAccount.get().checkSelected());
		menuItem.setActionCommand(AccountManagerAction.CHECK_SELECTED.name());
		menuItem.addActionListener(listener);
		jAssets.add(menuItem);

		menuItem = new JMenuItem(DialoguesAccount.get().uncheckSelected());
		menuItem.setActionCommand(AccountManagerAction.UNCHECK_SELECTED.name());
		menuItem.addActionListener(listener);
		jAssets.add(menuItem);

		//Done Button
		jClose = new JButton(DialoguesAccount.get().close());
		jClose.setActionCommand(AccountManagerAction.CLOSE.name());
		jClose.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createParallelGroup(Alignment.TRAILING)
					.addComponent(jTableScroll, 750, 750, Short.MAX_VALUE)
					.addComponent(jClose, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jAdd, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jRevalidate, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jShare, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCollapse, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jExpand, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addGap(0, 0, Short.MAX_VALUE)
					.addComponent(jAssets, Program.getButtonsWidth() + 20, Program.getButtonsWidth() + 20, Program.getButtonsWidth() + 20)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jAdd, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRevalidate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jShare, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCollapse, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAssets, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jTableScroll, 450, 450, Short.MAX_VALUE)
				.addComponent(jClose, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
		// Pack then take the dialog dimensions to use as the minimum dimension.
		getDialog().pack();
		Dimension d = new Dimension(getDialog().getWidth() + 10, getDialog().getHeight() + 10); // Use 10 pixel buffer to offset the resize 'bug'.
		getDialog().setMinimumSize(d);
		getDialog().setResizable(true);
	}

	public void forceUpdate() {
		updated = true;
	}

	public void updateTable() {
		//Collect owners
		List<OwnerType> ownerTypes = new ArrayList<>();
		//Eve Online API
		for (EveApiAccount account : program.getProfileManager().getAccounts()) {
			if (account.getOwners().isEmpty()) {
				ownerTypes.add(new EveApiOwner(account, DialoguesAccount.get().noOwners(), 0));
			} else {
				ownerTypes.addAll(account.getOwners());
			}
		}
		//EveKit API
		ownerTypes.addAll(program.getProfileManager().getEveKitOwners());
		//ESI
		ownerTypes.addAll(program.getProfileManager().getEsiOwners());
		//Update rows (Add all rows)
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(ownerTypes);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Revalidate
		boolean invalid = false;
		for (EsiOwner owner : program.getProfileManager().getEsiOwners()) {
			if (owner.isInvalid()) {
				invalid = true;
				break; //search done
			}
		}
		jRevalidate.setEnabled(invalid);

		if (!ownerTypes.isEmpty()) {
			jTable.setRowSelectionInterval(1, 1);
			jAssets.setEnabled(true);
			jCollapse.setEnabled(true);
			jExpand.setEnabled(true);
		} else {
			jAssets.setEnabled(false);
			jCollapse.setEnabled(false);
			jExpand.setEnabled(false);
		}
	}

	private void checkAssets(final boolean selected, final boolean check) {
		if (selected) { //Set selected to check value
			int[] selectedRows = jTable.getSelectedRows();
			for (int i = 0; i < selectedRows.length; i++) {
				Object o = tableModel.getElementAt(selectedRows[i]);
				if (o instanceof OwnerType) {
					OwnerType owner = (OwnerType) o;
					if (!owner.getOwnerName().equals(DialoguesAccount.get().noOwners())) {
						owner.setShowOwner(check);
					}
				}
			}
		} else { //Set all the check value
			for (OwnerType owner : program.getOwnerTypes()) {
				if (!owner.getOwnerName().equals(DialoguesAccount.get().noOwners())) {
					owner.setShowOwner(check);
				}
			}
		}
		for (int row = 0; row < jTable.getRowCount(); row++) {
			tableModel.fireTableCellUpdated(row, 0);
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jAdd;
	}

	@Override
	protected JButton getDefaultButton() {
		return jClose;
	}

	@Override
	protected void windowShown() {
		if (program.getOwnerTypes().isEmpty()) {
			accountImportDialog.add();
		}
	}

	@Override
	protected void save() {
		super.setVisible(false);
		if (!updated) {
			Map<OwnerType, Boolean> ownersShownNow = new HashMap<>();
			for (OwnerType ownerType : program.getProfileManager().getOwnerTypes()) {
				ownersShownNow.put(ownerType, ownerType.isShowOwner());
			}
			updated = !ownersShownNow.equals(ownersShownCache);
		}
		if (updated) {
			jLockWindow.show(GuiShared.get().updating(), new LockWorkerAdaptor() {
				@Override
				public void task() {
					program.saveProfile();
					program.updateEventLists();
				}
			});
		}
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			updated = false;
			ownersShownCache.clear();
			for (OwnerType ownerType : program.getProfileManager().getOwnerTypes()) {
				ownersShownCache.put(ownerType, ownerType.isShowOwner());
			}
			updateTable();
			super.setVisible(b);
		} else {
			save();
		}
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (AccountManagerAction.ADD.name().equals(e.getActionCommand())) {
				accountImportDialog.add();
			} else if (AccountManagerAction.SHARE_EXPORT.name().equals(e.getActionCommand())) {
				accountImportDialog.shareExport();
			} else if (AccountManagerAction.SHARE_IMPORT.name().equals(e.getActionCommand())) {
				accountImportDialog.shareImport();
			} else if (AccountManagerAction.COLLAPSE.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(false);
			} else if (AccountManagerAction.EXPAND.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(true);
			} else if (AccountManagerAction.CLOSE.name().equals(e.getActionCommand())) {
				save();
			} else if (AccountCellAction.EDIT.name().equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Object o = tableModel.getElementAt(index);
				if (o instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
					Object object = separator.first();
					if (object instanceof EsiOwner) {
						EsiOwner esiOwner = (EsiOwner) object;
						accountImportDialog.editEsi(esiOwner);
					}
				}
			} else if (AccountCellAction.DELETE.name().equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Object o = tableModel.getElementAt(index);
				if (o instanceof SeparatorList.Separator<?>) {
					int nReturn = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame()
							, DialoguesAccount.get().deleteAccountQuestion()
							, DialoguesAccount.get().deleteAccount()
							, JOptionPane.YES_NO_OPTION
							, JOptionPane.PLAIN_MESSAGE);
					if (nReturn == JOptionPane.YES_OPTION) {
						SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
						Object object = separator.first();
						if (object instanceof EveApiOwner) { //Eve Api
							EveApiOwner owner = (EveApiOwner) object;
							EveApiAccount account = owner.getParentAccount();
							program.getProfileManager().getAccounts().remove(account);
							forceUpdate();
							updateTable();
						}
						if (object instanceof EveKitOwner) {
							EveKitOwner eveKitOwner = (EveKitOwner) object;
							program.getProfileManager().getEveKitOwners().remove(eveKitOwner);
							forceUpdate();
							updateTable();
						}
						if (object instanceof EsiOwner) {
							EsiOwner esiOwner = (EsiOwner) object;
							program.getProfileManager().getEsiOwners().remove(esiOwner);
							forceUpdate();
							updateTable();
						}
					}
				}
			} else if (AccountCellAction.MIGRATE.name().equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Object o = tableModel.getElementAt(index);
				if (o instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
					List<DeprecatedOwner> owners = new ArrayList<>();
					try {
						separatorList.getReadWriteLock().readLock().lock();
						for (Object object : separator.getGroup()) {
							if (object instanceof DeprecatedOwner) { //Eve Api
								owners.add((DeprecatedOwner) object);
							}
						}
					} finally {
						separatorList.getReadWriteLock().readLock().unlock();
					}
					boolean updated = jMigrateDialog.show(owners);
					if (updated) {
						boolean allMigrated = true;
						for (DeprecatedOwner owner : owners) {
							if (!owner.isMigrated()) {
								allMigrated = false;
								break;
							}
						}
						if (allMigrated) {
							JOptionPane.showMessageDialog(getDialog(), DialoguesAccount.get().accountMigratedDoneMsg(), DialoguesAccount.get().accountMigratedDoneTitle(), JOptionPane.PLAIN_MESSAGE);
						}
						forceUpdate();
						updateTable();
					}
				}
			} else if (AccountManagerAction.REVALIDATE.name().equals(e.getActionCommand())) {
				jRevalidateLock.show(GuiShared.get().updating(), new LockWorkerAdaptor() {
					private int done = 0;
					private int total = 0;

					@Override
					public void task() {
						for (EsiOwner owner : program.getProfileManager().getEsiOwners()) {
							if (owner.isInvalid()) {
								total++;
								OAuth oAuth = (OAuth) owner.getApiClient().getAuthentication("evesso");
								String accessToken = oAuth.getAccessToken();
								if (accessToken != null) {
									owner.setInvalid(false);
									done++;
								}
							}
						}
					}

					@Override
					public void gui() {
						if (done > 0) {
							forceUpdate();
							updateTable();
						}
					}

					@Override
					public void hidden() {
						if (total > 0) {
							if (done == total) { //All
								JOptionPane.showMessageDialog(getDialog(), DialoguesAccount.get().revalidateMsgAll(total), DialoguesAccount.get().revalidate(), JOptionPane.PLAIN_MESSAGE);
							} else if (done == 0) { //None
								JOptionPane.showMessageDialog(getDialog(), DialoguesAccount.get().revalidateMsgNone(total), DialoguesAccount.get().revalidate(), JOptionPane.PLAIN_MESSAGE);
							} else { //Some
								JOptionPane.showMessageDialog(getDialog(), DialoguesAccount.get().revalidateMsgSome(total, done, total-done), DialoguesAccount.get().revalidate(), JOptionPane.PLAIN_MESSAGE);
							}
						}
					}

				});
			} else if (AccountManagerAction.CHECK_ALL.name().equals(e.getActionCommand())) {
				checkAssets(false, true);
			} else if (AccountManagerAction.UNCHECK_ALL.name().equals(e.getActionCommand())) {
				checkAssets(false, false);
			} else if (AccountManagerAction.CHECK_SELECTED.name().equals(e.getActionCommand())) {
				checkAssets(true, true);
			} else if (AccountManagerAction.UNCHECK_SELECTED.name().equals(e.getActionCommand())) {
				checkAssets(true, false);
			}
		}
	}
}