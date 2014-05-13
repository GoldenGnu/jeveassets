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

import ca.odell.glazedlists.BasicEventList;
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
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.MyAccount;
import net.nikr.eve.jeveasset.data.Owner;
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
		ADD,
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
	private final JButton jExpand;
	private final JButton jCollapse;
	private final JDropDownButton jAssets;
	private final JButton jClose;
	private final EventList<Owner> eventList;
	private final DefaultEventTableModel<Owner> tableModel;
	private final SeparatorList<Owner> separatorList;
	private final DefaultEventSelectionModel<Owner> selectionModel;

	private Map<Owner, Boolean> shownAssets;
	private boolean forceUpdate = false;

	public AccountManagerDialog(final Program program) {
		super(program, DialoguesAccount.get().dialogueNameAccountManagement(), Images.DIALOG_ACCOUNTS.getImage());

		accountImportDialog = new AccountImportDialog(this, program);
		ListenerClass listener = new ListenerClass();

		eventList = new BasicEventList<Owner>();

		separatorList = new SeparatorList<Owner>(eventList, new SeparatorListComparator(), 1, 3);
		EnumTableFormatAdaptor<AccountTableFormat, Owner> tableFormat = new EnumTableFormatAdaptor<AccountTableFormat, Owner>(AccountTableFormat.class);
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
		jAdd = new JButton(DialoguesAccount.get().add());
		jAdd.setActionCommand(AccountManagerAction.ADD.name());
		jAdd.addActionListener(listener);

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
					.addComponent(jClose, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jAdd, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCollapse, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExpand, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addGap(0, 0, Short.MAX_VALUE)
					.addComponent(jAssets, Program.BUTTONS_WIDTH + 20, Program.BUTTONS_WIDTH + 20, Program.BUTTONS_WIDTH + 20)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jAdd, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCollapse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAssets, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTableScroll, 300, 300, Short.MAX_VALUE)
				.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
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
		eventList.getReadWriteLock().writeLock().lock();
		eventList.clear();
		for (MyAccount account : program.getAccounts()) {
			if (account.getOwners().isEmpty()) {
				eventList.add(new Owner(account, DialoguesAccount.get().noOwners(), 0));
			} else {
				for (Owner owner : account.getOwners()) {
					eventList.add(owner);
				}
			}
		}
		eventList.getReadWriteLock().writeLock().unlock();
		if (!eventList.isEmpty()) {
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
				if (o instanceof Owner) {
					Owner owner = (Owner) o;
					if (!owner.getName().equals(DialoguesAccount.get().noOwners())) {
						owner.setShowOwner(check);
					}
				}
			}
		} else { //Set all the check value
			for (MyAccount account : program.getAccounts()) {
				for (Owner owner : account.getOwners()) {
					if (!owner.getName().equals(DialoguesAccount.get().noOwners())) {
						owner.setShowOwner(check);
					}
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
		if (program.getAccounts().isEmpty()) {
			accountImportDialog.show();
		}
	}

	@Override
	protected void save() {
		boolean changed = false;
		for (MyAccount account : program.getAccounts()) {
			for (Owner owner : account.getOwners()) {
				if (!shownAssets.containsKey(owner)) { //New account
					if (owner.isShowOwner()) { //if shown: Updated
						changed = true;
					}
				} else if (owner.isShowOwner() != shownAssets.get(owner)) { //Old account changed: Update
					changed = true;
				}
			}
		}
		if (changed || forceUpdate) {
			program.updateEventLists();
		}
		this.setVisible(false);
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			forceUpdate = false;
			updateTable();
			shownAssets = new HashMap<Owner, Boolean>();
			for (MyAccount account : program.getAccounts()) {
				for (Owner owner : account.getOwners()) {
					shownAssets.put(owner, owner.isShowOwner());
				}
			}
		}
		super.setVisible(b);
	}
	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (AccountManagerAction.ADD.name().equals(e.getActionCommand())) {
				accountImportDialog.show();
			}
			if (AccountManagerAction.COLLAPSE.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(false);
			}
			if (AccountManagerAction.EXPAND.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(true);
			}
			if (AccountManagerAction.CLOSE.name().equals(e.getActionCommand())) {
				save();
			}
			if (AccountCellAction.EDIT.name().equals(e.getActionCommand())) {
				int index = jTable.getSelectedRow();
				Object o = tableModel.getElementAt(index);
				if (o instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
					Owner owner = (Owner) separator.first();
					MyAccount account = owner.getParentAccount();
					accountImportDialog.show(account);
				}
			}
			if (AccountCellAction.DELETE.name().equals(e.getActionCommand())) {
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
						Owner owner = (Owner) separator.first();
						MyAccount account = owner.getParentAccount();
						program.getAccounts().remove(account);
						forceUpdate();
						updateTable();
					}
				}
			}
			if (AccountManagerAction.CHECK_ALL.name().equals(e.getActionCommand())) {
				checkAssets(false, true);
			}

			if (AccountManagerAction.UNCHECK_ALL.name().equals(e.getActionCommand())) {
				checkAssets(false, false);
			}

			if (AccountManagerAction.CHECK_SELECTED.name().equals(e.getActionCommand())) {
				checkAssets(true, true);
			}

			if (AccountManagerAction.UNCHECK_SELECTED.name().equals(e.getActionCommand())) {
				checkAssets(true, false);
			}
		}
	}
}
