/*
 * Copyright 2009, 2010
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.gui.dialogs;

import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.shared.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.JUneditableTableModel;
import net.nikr.eve.jeveasset.io.eveapi.HumansGetter;
import net.nikr.eve.jeveasset.io.Online;
import net.nikr.log.Log;


public class ApiManagerDialog extends JDialogCentered implements ActionListener, TableModelListener  {

	public final static String ACTION_ADD = "ACTION_ADD";
	public final static String ACTION_DONE = "ACTION_DONE";
	public final static String ACTION_REMOVE = "ACTION_REMOVE";
	public final static String ACTION_EDIT = "ACTION_EDIT";
	public final static String ACTION_ASSETS_CHECK_ALL = "ACTION_ASSETS_CHECK_ALL";
	public final static String ACTION_ASSETS_UNCHECK_ALL = "ACTION_ASSETS_UNCHECK_ALL";
	public final static String ACTION_ASSETS_CHECK_SELECTED = "ACTION_ASSETS_CHECK_SELECTED";
	public final static String ACTION_ASSETS_UNCHECK_SELECTED = "ACTION_ASSETS_UNCHECK_SELECTED";
	public final static String ACTION_CORPORATION_CHECK_ALL = "ACTION_CORPORATION_CHECK_ALL";
	public final static String ACTION_CORPORATION_UNCHECK_ALL = "ACTION_CORPORATION_UNCHECK_ALL";
	public final static String ACTION_CORPORATION_CHECK_SELECTED = "ACTION_CORPORATION_CHECK_SELECTED";
	public final static String ACTION_CORPORATION_UNCHECK_SELECTED = "ACTION_CORPORATION_UNCHECK_SELECTED";

	//GUI
	private ApiAddDialog apiAddDialog;
	private DefaultTableModel accountTableModel;
	private JScrollPane jAccountScrollPanel;
	private DefaultTableModel humanTableModel;
	private JTable jHumanTable;
	private JTable jAccountTable;
	private JButton jRemove;
	private JButton jEdit;
	private JButton jAdd;
	private JDropDownButton jAssets;
	private JDropDownButton jCorporation;
	private JButton jDone;

	private CheckHumanTask checkHumanTask;

	private Map<Long, Boolean> shownAssets;
	private Map<Long, Boolean> shownAssetsCopy;
	private Map<Long, Boolean> corpAssets;
	private Map<Long, Boolean> corpAssetsCopy;

	public ApiManagerDialog(Program program, Image image) {
		super(program, "Api Key Manager", image);

		apiAddDialog = new ApiAddDialog(this, program);

		//Api Table
		String[] columnNames = {"User", "Full API Key"};
		accountTableModel = new JUneditableTableModel(columnNames);
		jAccountTable = new JTable( accountTableModel );
		jAccountTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jAccountTable.getTableHeader().setReorderingAllowed(false);
		jAccountTable.getTableHeader().setResizingAllowed(false);
		jAccountScrollPanel = new JScrollPane(jAccountTable);
		jPanel.add(jAccountScrollPanel);

		//Add Button
		jAdd = new JButton("Add");
		jAdd.setActionCommand(ACTION_ADD);
		jAdd.addActionListener(this);
		jPanel.add(jAdd);

		//Edit Button
		jEdit = new JButton("Edit");
		jEdit.setActionCommand(ACTION_EDIT);
		jEdit.addActionListener(this);
		jPanel.add(jEdit);

		//Remove Button
		jRemove = new JButton("Remove");
		jRemove.setActionCommand(ACTION_REMOVE);
		jRemove.addActionListener(this);
		jPanel.add(jRemove);

		jAssets = new JDropDownButton("Personal");
		//jAssets.setIcon( ImageGetter.getIcon( "database_edit.png"));
		jPanel.add(jAssets);

		JMenuItem menuItem;

		menuItem = new JMenuItem("Check All Asssets");
		menuItem.setActionCommand(ACTION_ASSETS_CHECK_ALL);
		menuItem.addActionListener(this);
		jAssets.add(menuItem);
		
		menuItem = new JMenuItem("Uncheck All Asssets");
		menuItem.setActionCommand(ACTION_ASSETS_UNCHECK_ALL);
		menuItem.addActionListener(this);
		jAssets.add(menuItem);
		
		jAssets.addSeparator();

		menuItem = new JMenuItem("Check Selected Asssets");
		menuItem.setActionCommand(ACTION_ASSETS_CHECK_SELECTED);
		menuItem.addActionListener(this);
		jAssets.add(menuItem);

		menuItem = new JMenuItem("Uncheck Selected Asssets");
		menuItem.setActionCommand(ACTION_ASSETS_UNCHECK_SELECTED);
		menuItem.addActionListener(this);
		jAssets.add(menuItem);
		
		jCorporation = new JDropDownButton("Corp.");
		//jCorporation.setIcon( ImageGetter.getIcon( "building_edit.png"));

		menuItem = new JMenuItem("Check All Corporations");
		menuItem.setActionCommand(ACTION_CORPORATION_CHECK_ALL);
		menuItem.addActionListener(this);
		jCorporation.add(menuItem);

		menuItem = new JMenuItem("Uncheck All Corporations");
		menuItem.setActionCommand(ACTION_CORPORATION_UNCHECK_ALL);
		menuItem.addActionListener(this);
		jCorporation.add(menuItem);

		jCorporation.addSeparator();

		menuItem = new JMenuItem("Check Selected Corporations");
		menuItem.setActionCommand(ACTION_CORPORATION_CHECK_SELECTED);
		menuItem.addActionListener(this);
		jCorporation.add(menuItem);

		menuItem = new JMenuItem("Uncheck Selected Corporations");
		menuItem.setActionCommand(ACTION_CORPORATION_UNCHECK_SELECTED);
		menuItem.addActionListener(this);
		jCorporation.add(menuItem);

		jPanel.add(jCorporation);

		//Human/Characters Table
		String[] characterColumnNames = {"User", "Name", "Corporation", "Corporation Assets", "Show Assets"};
		humanTableModel = new JUneditableTableModel(characterColumnNames);
		humanTableModel.addTableModelListener(this);
		jHumanTable = new JTable( humanTableModel );
		jHumanTable.getTableHeader().setReorderingAllowed(false);
		JScrollPane jCharacterScrollPanel = new JScrollPane(jHumanTable);
		jPanel.add(jCharacterScrollPanel);

		//Done Button
		jDone = new JButton("Done");
		jDone.setActionCommand(ACTION_DONE);
		jDone.addActionListener(this);
		jPanel.add(jDone);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup()
				.addComponent(jAccountScrollPanel, 550, 550, 550)
				.addComponent(jCharacterScrollPanel, 550, 550, 550)
			)
			.addGroup(layout.createParallelGroup()
				.addComponent(jAdd, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				.addComponent(jEdit, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				.addComponent(jRemove, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				.addComponent(jAssets, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				.addComponent(jCorporation, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				.addComponent(jDone, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
			)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jAccountScrollPanel, 142, 142, 142)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jAdd, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jEdit, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jCharacterScrollPanel, 142, 142, 142)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jAssets, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jCorporation, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					)
				)
				.addComponent(jDone, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
		updateTable();
	}

	public void updateTable(){
		

		//Save HumanTable selected rows
		List<Long> humanSelectedIds = new Vector<Long>();
		for (int a = 0; a < humanTableModel.getRowCount(); a++){
			if (jHumanTable.getSelectionModel().isSelectedIndex(a)){
				humanSelectedIds.add( Long.valueOf( (String) jHumanTable.getValueAt(a, 0) ) );
			}
		}
		//Clear rows
		accountTableModel.setRowCount(0);
		humanTableModel.setRowCount(0);

		//Update rows (Add all rows)
		List<Account> accounts = program.getSettings().getAccounts();
		for (int a = 0; a < accounts.size(); a++){
			Account account = accounts.get(a);
			accountTableModel.addRow( new String[] {String.valueOf(account.getUserID() ), account.getApiKey()});
			List<Human> humans = account.getHumans();
			for (int b = 0; b < humans.size(); b++){
				Human human = humans.get(b);
				humanTableModel.addRow( new Object[] {String.valueOf(human.getCharacterID()), human.getName(), human.getCorporation(), human.isUpdateCorporationAssets(), human.isShowAssets()} );
				if (humanSelectedIds.contains(human.getCharacterID())){ //Restore selection
					jHumanTable.getSelectionModel().addSelectionInterval(humanTableModel.getRowCount()-1, humanTableModel.getRowCount()-1);
				}

			}
		}
		if (jAccountTable.getRowCount() > 0){
			jAccountTable.setRowSelectionInterval(0, 0);
			jRemove.setEnabled(true);
			jEdit.setEnabled(true);
			jAssets.setEnabled(true);
			jCorporation.setEnabled(true);
		} else {
			jRemove.setEnabled(false);
			jEdit.setEnabled(false);
			jAssets.setEnabled(false);
			jCorporation.setEnabled(false);
		}
	}

	public void saveApiKey(){
		checkHumanTask = new CheckHumanTask(apiAddDialog.getUserId(), apiAddDialog.getApiKey());
		checkHumanTask.addPropertyChangeListener( new addApiKey());
		checkHumanTask.execute();
	}

	private void checkAssets(boolean selected, boolean check, boolean assets){
		List<Long> ids = new Vector<Long>();
		if (selected){
			int[] selectedRows = jHumanTable.getSelectedRows();
			for (int a = 0; a < selectedRows.length; a++){
				String s = (String) jHumanTable.getValueAt(selectedRows[a], 0);
				ids.add( Long.valueOf(s) );
			}
		}
		List<Account> accounts = program.getSettings().getAccounts();
		for (int b = 0; b < accounts.size(); b++){
			Account account = accounts.get(b);
			List<Human> humans = account.getHumans();
			for (int c = 0; c < humans.size(); c++){
				Human human = humans.get(c);
				if (ids.contains(human.getCharacterID()) || !selected){
					if (assets){
						shownAssetsCopy.put(human.getCharacterID(), check);
						human.setShowAssets(check);
					} else {
						corpAssetsCopy.put(human.getCharacterID(), check);
						human.setUpdateCorporationAssets(check);
					}
				}
			}
		}
		updateTable();
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jAdd;
	}

	@Override
	protected JButton getDefaultButton() {
		return jDone;
	}

	@Override
	protected void windowShown() {
		jAccountTable.getColumnModel().getColumn(0).setPreferredWidth(65);
		jAccountTable.getColumnModel().getColumn(1).setPreferredWidth(jAccountScrollPanel.getViewportBorderBounds().width - 65); //455
	}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {
		if (!shownAssetsCopy.equals(shownAssets)){
			program.updateEventList();
		}
		if (!corpAssetsCopy.equals(corpAssets)){
			program.updateEventList();
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Corporation asset settings changed.\r\nYou need to update asset before the new settings take effect\r\nTo update assets select:\r\nOptions > Update Assets", "Corporation Asset Settings", JOptionPane.PLAIN_MESSAGE);
		}
		this.setVisible(false);
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			shownAssets = new HashMap<Long, Boolean>();
			corpAssets = new HashMap<Long, Boolean>();
			List<Account> accounts = program.getSettings().getAccounts();
			for (int a = 0; a < accounts.size(); a++){
				Account account = accounts.get(a);
				List<Human> humans = account.getHumans();
				for (int c = 0; c < humans.size(); c++){
					Human human = humans.get(c);
					shownAssets.put(human.getCharacterID(), human.isShowAssets());
					corpAssets.put(human.getCharacterID(), human.isUpdateCorporationAssets());
				}
			}
			shownAssetsCopy = new HashMap<Long, Boolean>(shownAssets);
			corpAssetsCopy = new HashMap<Long, Boolean>(corpAssets);
			updateTable();
		}
		super.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD.equals(e.getActionCommand())) {
			apiAddDialog.setVisible(true);
		}
		if (ACTION_DONE.equals(e.getActionCommand())) {
			save();
		}
		if (ACTION_EDIT.equals(e.getActionCommand())) {
			int row = jAccountTable.getSelectedRow();
			int userID = Integer.valueOf( (String) jAccountTable.getValueAt(row, 0));
			String oldApiKey = (String) jAccountTable.getValueAt(row, 1);
			String newApiKey = (String) JOptionPane.showInputDialog(this.getDialog(), "Enter new api key", "Edit API Key", JOptionPane.PLAIN_MESSAGE, null, null, oldApiKey);
			if (newApiKey == null){ //Cancel - do nothing
				return;
			}
			this.getDialog().setEnabled(false);
			checkHumanTask = new CheckHumanTask(userID, newApiKey);
			checkHumanTask.addPropertyChangeListener( new updateApiKey() );
			checkHumanTask.execute();
			
		}
		if (ACTION_REMOVE.equals(e.getActionCommand())) {
			int selectedRow = jAccountTable.getSelectedRow();
			if (selectedRow == -1){
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Nothing to remove...", "Remove Api Key", JOptionPane.PLAIN_MESSAGE);
				return;
			}
			int userID = Integer.parseInt((String)accountTableModel.getValueAt(selectedRow, 0));
			String apiKey = (String) accountTableModel.getValueAt(selectedRow, 1);

			List<Account> accounts =  program.getSettings().getAccounts();

			Account tempAccount = new Account(userID, apiKey);
			String users = "";
			for (int a = 0; a < accounts.size(); a++){
				Account account = accounts.get(a);
				if (account.equals(tempAccount)){
					List<Human> humans = account.getHumans();
					if (humans.size() > 1){
						users = "Characters Removed:\r\n";
						for (int b = 0; b < humans.size(); b++){
							Human human = humans.get(b);
							users = users + "  " + human.getName()+"\r\n";
						}
					} else {
						users = "Character removed: ";
						for (int b = 0; b < humans.size(); b++){
							Human human = humans.get(b);
							users = users + human.getName()+"\r\n";
						}
					}
				}
			}

			int nReturn = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), "Remove API Key: "+userID+"?\r\n"+users, "Remove Api Key", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.YES_OPTION){
				accountTableModel.removeRow(selectedRow);
				program.getSettings().getAccounts().remove( tempAccount );
				updateTable();
				program.updateEventList();
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
	
	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE){
			int rowStart = e.getFirstRow();
			int rowEnd = e.getLastRow();
			int column = e.getColumn();
			for (int a = rowStart; a <= rowEnd; a++){
				boolean booleanColumn = (Boolean) humanTableModel.getValueAt(a, column);
				String characterID = (String) humanTableModel.getValueAt(a, 0);
				List<Account> accounts = program.getSettings().getAccounts();
				for (int b = 0; b < accounts.size(); b++){
					Account account = accounts.get(b);
					List<Human> humans = account.getHumans();
					for (int c = 0; c < humans.size(); c++){
						Human human = humans.get(c);
						if (Integer.valueOf(characterID) == human.getCharacterID()){
							if (column == 3){
								corpAssetsCopy.put(human.getCharacterID(), (Boolean) booleanColumn);
								human.setUpdateCorporationAssets((Boolean) booleanColumn);
							}
							if (column == 4){
								shownAssetsCopy.put(human.getCharacterID(), (Boolean) booleanColumn);
								human.setShowAssets((Boolean) booleanColumn);
							}
						}
					}
				}
			}
		}
	}

	class updateApiKey implements PropertyChangeListener{

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			apiAddDialog.setEnabledAll(false);

			if (checkHumanTask.throwable != null){
				Log.error("Uncaught Exception (SwingWorker): Please email the latest error.txt in the logs directory to niklaskr@gmail.com", checkHumanTask.throwable);
			}
			if (checkHumanTask.result == 10 && checkHumanTask.done){
				checkHumanTask.done = false;
				getDialog().setEnabled(true);
				JOptionPane.showMessageDialog(dialog, "Could not update API Key.\r\nThe API Key already exist...", "Update API Key", JOptionPane.PLAIN_MESSAGE);
			}
			if (checkHumanTask.result == 20 && checkHumanTask.done){
				checkHumanTask.done = false;
				getDialog().setEnabled(true);
				JOptionPane.showMessageDialog(dialog, "Could not update API Key.\r\nPlease connect to the internet and try again...", "Update API Key", JOptionPane.PLAIN_MESSAGE);
			}
			if (checkHumanTask.result == 30 && checkHumanTask.done){
				checkHumanTask.done = false;
				getDialog().setEnabled(true);
				JOptionPane.showMessageDialog(dialog, "Could not update API Key.\r\nThe entered API Key is not a valid Full Access API Key", "Update API Key", JOptionPane.PLAIN_MESSAGE);
			}
			if (checkHumanTask.result == 100 && checkHumanTask.done){
				checkHumanTask.done = false;
				List<Account> accounts = program.getSettings().getAccounts();
				for (int a = 0; a < accounts.size(); a++){
					Account account = accounts.get(a);
					if (account.getUserID() == checkHumanTask.userID){
						account.setApiKey(checkHumanTask.apiKey);
						break;
					}
				}
				getDialog().setEnabled(true);
				updateTable();
				JOptionPane.showMessageDialog(dialog, "API Key updated", "Update API Key", JOptionPane.PLAIN_MESSAGE);
			}
		}

	}

	class addApiKey implements PropertyChangeListener{

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			apiAddDialog.setEnabledAll(false);
			if (checkHumanTask.throwable != null){
				Log.error("Uncaught Exception (SwingWorker): Please email the latest error.txt in the logs directory to niklaskr@gmail.com", checkHumanTask.throwable);
			}
			if (checkHumanTask.result == 10 && checkHumanTask.done){
				checkHumanTask.done = false;
				apiAddDialog.setVisible(false);
				JOptionPane.showMessageDialog(dialog, "Could not add API Key.\r\nThe API Key already exist...", "Add API Key", JOptionPane.PLAIN_MESSAGE);
			}
			if (checkHumanTask.result == 20 && checkHumanTask.done){
				checkHumanTask.done = false;
				apiAddDialog.setVisible(false);
				JOptionPane.showMessageDialog(dialog, "Could not add API Key.\r\nPlease connect to the internet and try again...", "Add API Key", JOptionPane.PLAIN_MESSAGE);
			}
			if (checkHumanTask.result == 30 && checkHumanTask.hasError &&checkHumanTask.done){
				checkHumanTask.done = false;
				apiAddDialog.setVisible(false);
				JOptionPane.showMessageDialog(dialog, "Could not add API Key.\r\nThe entered API Key is not a valid Full Access API Key", "Add API Key", JOptionPane.PLAIN_MESSAGE);
			}
			if (checkHumanTask.result == 100 && checkHumanTask.done){
				checkHumanTask.done = false;
				program.getSettings().getAccounts().add(checkHumanTask.account);
				updateTable();
				apiAddDialog.setVisible(false);
				JOptionPane.showMessageDialog(dialog, "API Key added\r\nTo update assets select:\r\nOptions > Update Assets", "Add API Key", JOptionPane.PLAIN_MESSAGE);
			}
		}

	}

	class CheckHumanTask extends SwingWorker<Void, Void> {

		private int result = 0;
		private boolean done = false;
		private Account account;
		private Throwable throwable = null;
		private int userID;
		private String apiKey;
		//private String error = null;
		private boolean hasError = false;
		private HumansGetter humansGetter = new HumansGetter();

		public CheckHumanTask(int userID, String apiKey) {
			this.userID = userID;
			this.apiKey = apiKey;
		}

		@Override
		public Void doInBackground() {
			setProgress(0);
			try {
				account = new Account(userID, apiKey);
				boolean ok = !program.getSettings().getAccounts().contains( account );
				if (!ok){
					result = 10;
					return null;
				}
				ok = Online.isOnline(program.getSettings());
				if (!ok){
					result = 20;
					return null;
				}
				humansGetter.load(null, true, account);
				if (humansGetter.hasError()){
					hasError = true;
					result = 30;
					return null;
				}
				result = 100;
			} catch (Throwable ex) {
				throwable = ex;
				done = false;
			}
			return null;
        }

		@Override
		public void done() {
			done = true;
			setProgress(100);
		}
	}
}