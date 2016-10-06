/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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
import java.util.HashMap;
import java.util.Map;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EventListManager;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccount;
import net.nikr.eve.jeveasset.data.eveapi.EveApiOwner;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.OwnerType;
import net.nikr.eve.jeveasset.gui.dialogs.account.AccountSeparatorTableCell.AccountCellAction;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;


public class AccountManagerDialog extends JDialogCentered {

	private enum AccountManagerAction {
		ADD_EVEAPI,
		ADD_EVEKIT,
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
	private final JDropDownButton jAdd;
	private final JButton jExpand;
	private final JButton jCollapse;
	private final JDropDownButton jAssets;
	private final JButton jClose;
	private final EventList<OwnerType> eventList;
	private final DefaultEventTableModel<OwnerType> tableModel;
	private final SeparatorList<OwnerType> separatorList;
	private final DefaultEventSelectionModel<OwnerType> selectionModel;

	private Map<OwnerType, Boolean> ownerShows;
	private Map<String, String> accountNames;
	private boolean forceUpdate = false;

	public AccountManagerDialog(final Program program) {
		super(program, DialoguesAccount.get().dialogueNameAccountManagement(), Images.DIALOG_ACCOUNTS.getImage());

		accountImportDialog = new AccountImportDialog(this, program);
		ListenerClass listener = new ListenerClass();

		eventList = new EventListManager<OwnerType>().create();

		eventList.getReadWriteLock().readLock().lock();
		separatorList = new SeparatorList<OwnerType>(eventList, new SeparatorListComparator(), 1, 3);
		eventList.getReadWriteLock().readLock().unlock();

		EnumTableFormatAdaptor<AccountTableFormat, OwnerType> tableFormat = new EnumTableFormatAdaptor<AccountTableFormat, OwnerType>(AccountTableFormat.class);
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		jTable = new JAccountTable(program, tableModel, separatorList);
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.setSeparatorRenderer(new AccountSeparatorTableCell(listener, jTable, separatorList));
		jTable.setSeparatorEditor(new AccountSeparatorTableCell(listener, jTable, separatorList));

		JScrollPane jTableScroll = new JScrollPane(jTable);

		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);

		//Add Button
		jAdd = new JDropDownButton(DialoguesAccount.get().add());
		
		JMenuItem jEveApi = new JMenuItem(DialoguesAccount.get().eveapi(), Images.MISC_EVE.getIcon());
		jEveApi.setActionCommand(AccountManagerAction.ADD_EVEAPI.name());
		jEveApi.addActionListener(listener);
		jAdd.add(jEveApi);

		JMenuItem jEveKit = new JMenuItem(DialoguesAccount.get().evekit(), Images.MISC_EVEKIT.getIcon());
		jEveKit.setActionCommand(AccountManagerAction.ADD_EVEKIT.name());
		jEveKit.addActionListener(listener);
		jAdd.add(jEveKit);
		
		jCollapse = new JButton(DialoguesAccount.get().collapse());
		jCollapse.setActionCommand(AccountManagerAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);

		jExpand = new JButton(DialoguesAccount.get().expand());
		jExpand.setActionCommand(AccountManagerAction.EXPAND.name());
		jExpand.addActionListener(listener);

		jAssets = new JDropDownButton(DialoguesAccount.get().showAssets());
		//jAssets.setIcon( ImageGetter.getIcon( "database_edit.png"));
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
					.addComponent(jTableScroll, 700, 700, Short.MAX_VALUE)
					.addComponent(jClose, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jAdd, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
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
					.addComponent(jCollapse, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAssets, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jTableScroll, 300, 300, Short.MAX_VALUE)
				.addComponent(jClose, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
		// Pack then take the dialog dimensions to use as the minimun dimension.
		getDialog().pack();
		Dimension d = new Dimension(getDialog().getWidth() + 10, getDialog().getHeight() + 10); // Use 10 pixel buffer to offset the resize 'bug'.
		getDialog().setMinimumSize(d);
		getDialog().setResizable(true);
	}

	public void forceUpdate() {
		forceUpdate = true;
	}

	public void updateTable() {
		//Update rows (Add all rows)
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			//Eve Online API
			for (EveApiAccount account : program.getProfileManager().getAccounts()) {
				if (account.getOwners().isEmpty()) {
					eventList.add(new EveApiOwner(account, DialoguesAccount.get().noOwners(), 0));
				} else {
					eventList.addAll(account.getOwners());
				}
			}
			//EveKit API
			eventList.addAll(program.getProfileManager().getEveKitOwners());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		if (!EventListManager.isEmpty(eventList)) {
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
		if (forceUpdate || isChanged()) {
			program.updateEventLists();
			program.saveProfile();
		}
	}

	private boolean isChanged() {
		for (OwnerType owner  : program.getOwnerTypes()) {
			if (!owner.getAccountName().equals(accountNames.get(owner.getComparator()))) {
				return true;
			}
			if (owner.isShowOwner() != ownerShows.get(owner)) { //Owner show changed
				return true;
			}
		}
		return false;
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			forceUpdate = false;
			updateTable();
			ownerShows = new HashMap<OwnerType, Boolean>();
			accountNames = new HashMap<String, String>();
			for (OwnerType owner  : program.getOwnerTypes()) {
				accountNames.put(owner.getComparator(), owner.getAccountName());
				ownerShows.put(owner, owner.isShowOwner());
			}
		} else {
			save();
		}
		super.setVisible(b);
	}
	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (AccountManagerAction.ADD_EVEAPI.name().equals(e.getActionCommand())) {
				accountImportDialog.addEveApi();
			} else if (AccountManagerAction.ADD_EVEKIT.name().equals(e.getActionCommand())) {
				accountImportDialog.addEveKit();
			} else if (AccountManagerAction.COLLAPSE.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(false);
			} else if (AccountManagerAction.EXPAND.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(true);
			} else if (AccountManagerAction.CLOSE.name().equals(e.getActionCommand())) {
				setVisible(false);
			} else if (AccountCellAction.EDIT.name().equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Object o = tableModel.getElementAt(index);
				if (o instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
					Object object = separator.first();
					if (object instanceof EveApiOwner) { //Eve Api
						EveApiOwner owner = (EveApiOwner) object;
						EveApiAccount account = owner.getParentAccount();
						accountImportDialog.editEveApi(account);
					}
					if (object instanceof EveKitOwner) {
						EveKitOwner eveKitOwner = (EveKitOwner) object;
						accountImportDialog.editEveKit(eveKitOwner);
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
					}
				}
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
