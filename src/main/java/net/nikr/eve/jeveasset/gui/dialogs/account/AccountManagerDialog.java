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

import net.nikr.eve.jeveasset.gui.shared.JSeparatorTable;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.shared.JDropDownButton;


public class AccountManagerDialog extends JDialogCentered implements ActionListener  {

	private final static String ACTION_ADD = "ACTION_ADD";
	private final static String ACTION_CLOSE = "ACTION_CLOSE";
	private final static String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private final static String ACTION_EXPAND = "ACTION_EXPAND";
	private final static String ACTION_ASSETS_CHECK_ALL = "ACTION_ASSETS_CHECK_ALL";
	private final static String ACTION_ASSETS_UNCHECK_ALL = "ACTION_ASSETS_UNCHECK_ALL";
	private final static String ACTION_ASSETS_CHECK_SELECTED = "ACTION_ASSETS_CHECK_SELECTED";
	private final static String ACTION_ASSETS_UNCHECK_SELECTED = "ACTION_ASSETS_UNCHECK_SELECTED";
	private final static String ACTION_CORPORATION_CHECK_ALL = "ACTION_CORPORATION_CHECK_ALL";
	private final static String ACTION_CORPORATION_UNCHECK_ALL = "ACTION_CORPORATION_UNCHECK_ALL";
	private final static String ACTION_CORPORATION_CHECK_SELECTED = "ACTION_CORPORATION_CHECK_SELECTED";
	private final static String ACTION_CORPORATION_UNCHECK_SELECTED = "ACTION_CORPORATION_UNCHECK_SELECTED";

	//GUI
	private AccountImportDialog accountImportDialog;
	private JSeparatorTable jTable;
	private JButton jAdd;
	private JButton jExpand;
	private JButton jCollapse;
	private JDropDownButton jAssets;
	private JDropDownButton jCorporation;
	private JButton jClose;
	private EventList<Human> eventList;
	private EventTableModel<Human> tableModel;
	private SeparatorList<Human> separatorList;
	private EventSelectionModel<Human> selectionModel;

	private Map<Human, Boolean> shownAssets;
	private Map<Human, Boolean> corpAssets;
	private boolean forceUpdate = false;

	public AccountManagerDialog(Program program, Image image) {
		super(program, "Accounts Management", image);

		accountImportDialog = new AccountImportDialog(this, program);

		eventList = new BasicEventList<Human>();

		separatorList = new SeparatorList<Human>(eventList, new SeparatorListComparator(), 1, 3);
		HumanTableFormat humanTableFormat = new HumanTableFormat();
		tableModel = new EventTableModel<Human>(separatorList, humanTableFormat);
		jTable = new JSeparatorTable(tableModel, humanTableFormat.getColumnNames());
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.setSeparatorRenderer(new HumanSeparatorTableCell(this, jTable, separatorList));
		jTable.setSeparatorEditor(new HumanSeparatorTableCell(this, jTable, separatorList));

		selectionModel = new EventSelectionModel<Human>(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);

		//Add Button
		jAdd = new JButton("Add");
		jAdd.setActionCommand(ACTION_ADD);
		jAdd.addActionListener(this);

		jCollapse = new JButton("Collapse");
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(this);

		jExpand = new JButton("Expand");
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(this);

		jAssets = new JDropDownButton("Show Assets");
		//jAssets.setIcon( ImageGetter.getIcon( "database_edit.png"));
		JMenuItem menuItem;

		menuItem = new JMenuItem("Check All");
		menuItem.setActionCommand(ACTION_ASSETS_CHECK_ALL);
		menuItem.addActionListener(this);
		jAssets.add(menuItem);
		
		menuItem = new JMenuItem("Uncheck All");
		menuItem.setActionCommand(ACTION_ASSETS_UNCHECK_ALL);
		menuItem.addActionListener(this);
		jAssets.add(menuItem);
		
		jAssets.addSeparator();

		menuItem = new JMenuItem("Check Selected");
		menuItem.setActionCommand(ACTION_ASSETS_CHECK_SELECTED);
		menuItem.addActionListener(this);
		jAssets.add(menuItem);

		menuItem = new JMenuItem("Uncheck Selected");
		menuItem.setActionCommand(ACTION_ASSETS_UNCHECK_SELECTED);
		menuItem.addActionListener(this);
		jAssets.add(menuItem);
		
		jCorporation = new JDropDownButton("Show Corp.");
		//jCorporation.setIcon( ImageGetter.getIcon( "building_edit.png"));

		menuItem = new JMenuItem("Check All");
		menuItem.setActionCommand(ACTION_CORPORATION_CHECK_ALL);
		menuItem.addActionListener(this);
		jCorporation.add(menuItem);

		menuItem = new JMenuItem("Uncheck All");
		menuItem.setActionCommand(ACTION_CORPORATION_UNCHECK_ALL);
		menuItem.addActionListener(this);
		jCorporation.add(menuItem);

		jCorporation.addSeparator();

		menuItem = new JMenuItem("Check Selected");
		menuItem.setActionCommand(ACTION_CORPORATION_CHECK_SELECTED);
		menuItem.addActionListener(this);
		jCorporation.add(menuItem);

		menuItem = new JMenuItem("Uncheck Selected");
		menuItem.setActionCommand(ACTION_CORPORATION_UNCHECK_SELECTED);
		menuItem.addActionListener(this);
		jCorporation.add(menuItem);

		//Done Button
		jClose = new JButton("Close");
		jClose.setActionCommand(ACTION_CLOSE);
		jClose.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createParallelGroup(Alignment.TRAILING)
					.addComponent(jTable.getScrollPanel(), 550, 550, Short.MAX_VALUE)
					.addComponent(jClose, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jAdd, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCollapse, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jExpand, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addGap(0, 0, Short.MAX_VALUE)
					.addComponent(jAssets, Program.BUTTONS_WIDTH+20, Program.BUTTONS_WIDTH+20, Program.BUTTONS_WIDTH+20)
					.addComponent(jCorporation, Program.BUTTONS_WIDTH+20, Program.BUTTONS_WIDTH+20, Program.BUTTONS_WIDTH+20)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jAdd, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCollapse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAssets, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCorporation, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTable.getScrollPanel(), 400, 400, Short.MAX_VALUE)
				.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				
		);
                getDialog().pack(); //Pack here to allow...
                getDialog().setMinimumSize(getDialog().getSize(null)); // ...retrieval of dimensions here.
                getDialog().setResizable(true);
	}

	public void forceUpdate(){
		forceUpdate = true;
	}

	public void updateTable(){
		//Update rows (Add all rows)
		shownAssets = new HashMap<Human, Boolean>();
		corpAssets = new HashMap<Human, Boolean>();
		eventList.getReadWriteLock().writeLock().lock();
		eventList.clear();
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				eventList.add(human);
				shownAssets.put(human, human.isShowAssets());
				corpAssets.put(human, human.isUpdateCorporationAssets());
			}
		}
		eventList.getReadWriteLock().writeLock().unlock();
		if (eventList.size() > 1){
			jTable.setRowSelectionInterval(1, 1);
			jAssets.setEnabled(true);
			jCorporation.setEnabled(true);
			jCollapse.setEnabled(true);
			jExpand.setEnabled(true);
		} else {
			jAssets.setEnabled(false);
			jCorporation.setEnabled(false);
			jCollapse.setEnabled(false);
			jExpand.setEnabled(false);
		}
	}

	private void checkAssets(boolean selected, boolean check, boolean assets){
		if (selected){
			int[] selectedRows = jTable.getSelectedRows();
			for (int a = 0; a < selectedRows.length; a++){
				Object o = tableModel.getElementAt(selectedRows[a]);
				if (o instanceof Human){
					Human human = (Human) o;
					if (assets){
						human.setShowAssets(check);
					} else {
						human.setUpdateCorporationAssets(check);
					}
				}
			}
		} else {
			for (Account account : program.getSettings().getAccounts()){
				for (Human human : account.getHumans()){
					if (assets){
						human.setShowAssets(check);
					} else {
						human.setUpdateCorporationAssets(check);
					}
				}
			}
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
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {
		boolean showAssets = false;
		boolean showCorporation = false;
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				if (human.isShowAssets() != shownAssets.get(human)) showAssets = true;
				if (human.isUpdateCorporationAssets() != corpAssets.get(human)) showCorporation = true;
			}
		}
		if (showAssets || showCorporation || forceUpdate){
			program.updateEventList();
		}
		if (showCorporation){
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Corporation asset settings changed.\r\nYou need to update asset before the new settings take effect\r\nTo update assets select: Menu > Update > Update", "Corporation Asset Settings", JOptionPane.PLAIN_MESSAGE);
		}
		this.setVisible(false);
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			forceUpdate = false;
			updateTable();
		}
		super.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD.equals(e.getActionCommand())) {
			accountImportDialog.setVisible(true);
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
				accountImportDialog.show(String.valueOf(account.getUserID()), account.getApiKey());
			}
		}
		if (HumanSeparatorTableCell.ACTION_DELETE.equals(e.getActionCommand())) {
			int index = jTable.getSelectedRow();
			Object o = tableModel.getElementAt(index);
			if (o instanceof SeparatorList.Separator<?>){
				int nReturn = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), "Delete Account?", "Delete Account", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
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
			checkAssets(false, true, true);
		}

		if (ACTION_ASSETS_UNCHECK_ALL.equals(e.getActionCommand())){
			checkAssets(false, false, true);
		}

		if (ACTION_ASSETS_CHECK_SELECTED.equals(e.getActionCommand())){
			checkAssets(true, true, true);
		}

		if (ACTION_ASSETS_UNCHECK_SELECTED.equals(e.getActionCommand())){
			checkAssets(true, false, true);
		}

		if (ACTION_CORPORATION_CHECK_ALL.equals(e.getActionCommand())){
			checkAssets(false, true, false);
		}

		if (ACTION_CORPORATION_UNCHECK_ALL.equals(e.getActionCommand())){
			checkAssets(false, false, false);
		}

		if (ACTION_CORPORATION_CHECK_SELECTED.equals(e.getActionCommand())){
			checkAssets(true, true, false);
		}

		if (ACTION_CORPORATION_UNCHECK_SELECTED.equals(e.getActionCommand())){
			checkAssets(true, false, false);
		}
	}
}