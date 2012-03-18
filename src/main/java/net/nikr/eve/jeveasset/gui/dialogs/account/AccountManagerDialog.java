/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

import net.nikr.eve.jeveasset.gui.shared.JSeparatorTable;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import java.awt.Dimension;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
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
import javax.swing.SwingConstants;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.dialogs.account.HumanTableFormat.ExpirerDate;
import net.nikr.eve.jeveasset.gui.dialogs.account.HumanTableFormat.YesNo;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.TableCellRenderers.ToStringCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;


public class AccountManagerDialog extends JDialogCentered implements ActionListener  {

	// TODO action enum - more string enum pattern, to be converted to an enum
	private final static String ACTION_ADD = "ACTION_ADD";
	private final static String ACTION_CLOSE = "ACTION_CLOSE";
	private final static String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private final static String ACTION_EXPAND = "ACTION_EXPAND";
	private final static String ACTION_ASSETS_CHECK_ALL = "ACTION_ASSETS_CHECK_ALL";
	private final static String ACTION_ASSETS_UNCHECK_ALL = "ACTION_ASSETS_UNCHECK_ALL";
	private final static String ACTION_ASSETS_CHECK_SELECTED = "ACTION_ASSETS_CHECK_SELECTED";
	private final static String ACTION_ASSETS_UNCHECK_SELECTED = "ACTION_ASSETS_UNCHECK_SELECTED";
	
	//GUI
	private AccountImportDialog accountImportDialog;
	private JSeparatorTable jTable;
	private JButton jAdd;
	private JButton jExpand;
	private JButton jCollapse;
	private JDropDownButton jAssets;
	private JButton jClose;
	private EventList<Human> eventList;
	private EventTableModel<Human> tableModel;
	private SeparatorList<Human> separatorList;
	private EventSelectionModel<Human> selectionModel;

	private Map<Human, Boolean> shownAssets;
	private boolean forceUpdate = false;

	public AccountManagerDialog(Program program) {
		super(program, DialoguesAccount.get().dialogueNameAccountManagement(), Images.DIALOG_ACCOUNTS.getImage());

		accountImportDialog = new AccountImportDialog(this, program);

		eventList = new BasicEventList<Human>();

		separatorList = new SeparatorList<Human>(eventList, new SeparatorListComparator(), 1, 3);
		EnumTableFormatAdaptor<HumanTableFormat, Human> humanTableFormat = new EnumTableFormatAdaptor<HumanTableFormat, Human>(HumanTableFormat.class);
		tableModel = new EventTableModel<Human>(separatorList, humanTableFormat);
		jTable = new JSeparatorTable(tableModel);
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.setSeparatorRenderer(new HumanSeparatorTableCell(this, jTable, separatorList));
		jTable.setSeparatorEditor(new HumanSeparatorTableCell(this, jTable, separatorList));
		jTable.setDefaultRenderer(YesNo.class, new ToStringCellRenderer(SwingConstants.CENTER));
		jTable.setDefaultRenderer(ExpirerDate.class, new ToStringCellRenderer(SwingConstants.CENTER));

		JScrollPane jTableScroll = new JScrollPane(jTable);
		
		selectionModel = new EventSelectionModel<Human>(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);

		//Add Button
		jAdd = new JButton(DialoguesAccount.get().add());
		jAdd.setActionCommand(ACTION_ADD);
		jAdd.addActionListener(this);

		jCollapse = new JButton(DialoguesAccount.get().collapse());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(this);

		jExpand = new JButton(DialoguesAccount.get().expand());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(this);

		jAssets = new JDropDownButton(DialoguesAccount.get().showAssets());
		//jAssets.setIcon( ImageGetter.getIcon( "database_edit.png"));
		JMenuItem menuItem;

		menuItem = new JMenuItem(DialoguesAccount.get().checkAll());
		menuItem.setActionCommand(ACTION_ASSETS_CHECK_ALL);
		menuItem.addActionListener(this);
		jAssets.add(menuItem);
		
		menuItem = new JMenuItem(DialoguesAccount.get().uncheckAll());
		menuItem.setActionCommand(ACTION_ASSETS_UNCHECK_ALL);
		menuItem.addActionListener(this);
		jAssets.add(menuItem);
		
		jAssets.addSeparator();

		menuItem = new JMenuItem(DialoguesAccount.get().checkSelected());
		menuItem.setActionCommand(ACTION_ASSETS_CHECK_SELECTED);
		menuItem.addActionListener(this);
		jAssets.add(menuItem);

		menuItem = new JMenuItem(DialoguesAccount.get().uncheckSelected());
		menuItem.setActionCommand(ACTION_ASSETS_UNCHECK_SELECTED);
		menuItem.addActionListener(this);
		jAssets.add(menuItem);

		//Done Button
		jClose = new JButton(DialoguesAccount.get().close());
		jClose.setActionCommand(ACTION_CLOSE);
		jClose.addActionListener(this);

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
					.addComponent(jAssets, Program.BUTTONS_WIDTH+20, Program.BUTTONS_WIDTH+20, Program.BUTTONS_WIDTH+20)
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

	public void forceUpdate(){
		forceUpdate = true;
	}

	public void updateTable(){
		//Update rows (Add all rows)
		
		eventList.getReadWriteLock().writeLock().lock();
		eventList.clear();
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				eventList.add(human);
			}
		}
		eventList.getReadWriteLock().writeLock().unlock();
		if (!eventList.isEmpty()){
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

	private void checkAssets(boolean selected, boolean check){
		if (selected){ //Set selected to check value
			int[] selectedRows = jTable.getSelectedRows();
			for (int a = 0; a < selectedRows.length; a++){
				Object o = tableModel.getElementAt(selectedRows[a]);
				if (o instanceof Human){
					Human human = (Human) o;
					human.setShowAssets(check);
				}
			}
		} else { //Set all the check value
			for (Account account : program.getSettings().getAccounts()){
				for (Human human : account.getHumans()){
					human.setShowAssets(check);
				}
			}
		}
		for (int row = 0; row < jTable.getRowCount(); row++){
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
		if (program.getSettings().getAccounts().isEmpty()){
			accountImportDialog.show();
		}
	}

	@Override
	protected void save() {
		boolean changed = false;
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				if (!shownAssets.containsKey(human)){ //New account
					if (human.isShowAssets())  changed = true; //if shown: Updated
				} else if (human.isShowAssets() != shownAssets.get(human)){ //Old account changed: Update
					changed = true;
				}
			}
		}
		if (changed || forceUpdate){
			program.updateEventList();
		}
		this.setVisible(false);
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			forceUpdate = false;
			updateTable();
			shownAssets = new HashMap<Human, Boolean>();
			for (Account account : program.getSettings().getAccounts()){
				for (Human human : account.getHumans()){
					shownAssets.put(human, human.isShowAssets());
				}
			}
		}
		super.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD.equals(e.getActionCommand())) {
			accountImportDialog.show();
		}
		if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
			jTable.expandSeparators(false, separatorList);
		}
		if (ACTION_EXPAND.equals(e.getActionCommand())) {
			jTable.expandSeparators(true, separatorList);
		}
		if (ACTION_CLOSE.equals(e.getActionCommand())) {
			save();
		}
		if (HumanSeparatorTableCell.ACTION_EDIT.equals(e.getActionCommand())) {
			int index = jTable.getSelectedRow();
			Object o = tableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>){
				SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
				Human human = (Human) separator.first();
				Account account = human.getParentAccount();
				accountImportDialog.show(account);
			}
		}
		if (HumanSeparatorTableCell.ACTION_DELETE.equals(e.getActionCommand())) {
			int index = jTable.getSelectedRow();
			Object o = tableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>){
				int nReturn = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame()
						, DialoguesAccount.get().deleteAccountQuestion()
						, DialoguesAccount.get().deleteAccount()
						, JOptionPane.YES_NO_OPTION
						, JOptionPane.PLAIN_MESSAGE);
				if (nReturn == JOptionPane.YES_OPTION){
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
					Human human = (Human) separator.first();
					Account account = human.getParentAccount();
					program.getSettings().getAccounts().remove( account );
					forceUpdate();
					updateTable();
				}
			}
		}
		if (ACTION_ASSETS_CHECK_ALL.equals(e.getActionCommand())){
			checkAssets(false, true);
		}

		if (ACTION_ASSETS_UNCHECK_ALL.equals(e.getActionCommand())){
			checkAssets(false, false);
		}

		if (ACTION_ASSETS_CHECK_SELECTED.equals(e.getActionCommand())){
			checkAssets(true, true);
		}

		if (ACTION_ASSETS_UNCHECK_SELECTED.equals(e.getActionCommand())){
			checkAssets(true, false);
		}
	}
}