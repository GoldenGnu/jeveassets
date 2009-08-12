/*
 * Copyright 2009
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
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import net.nikr.eve.jeveasset.gui.shared.JUneditableTableModel;
import net.nikr.eve.jeveasset.io.EveApiHumansReader;
import net.nikr.eve.jeveasset.io.Online;
import net.nikr.log.Log;


public class ApiManagerDialog extends JDialogCentered implements ActionListener, TableModelListener, PropertyChangeListener  {

	public final static String ACTION_ADD = "ACTION_ADD";
	public final static String ACTION_DONE = "ACTION_DONE";
	public final static String ACTION_REMOVE = "ACTION_REMOVE";

	//GUI
	private ApiAddDialog apiAddDialog;
	private DefaultTableModel usersTableModel;
	private JScrollPane jApiScrollPanel;
	private DefaultTableModel humansTableModel;
	private JTable jApiTable;
	private JButton jRemove;
	private JButton jAdd;

	private AddHumanTask addHumanTask;

	private Map<Long, Boolean> shownAssets;
	private Map<Long, Boolean> shownAssetsCopy;
	private Map<Long, Boolean> corpAssets;
	private Map<Long, Boolean> corpAssetsCopy;

	public ApiManagerDialog(Program program, Image image) {
		super(program, "Api Key Manager", image);

		apiAddDialog = new ApiAddDialog(this, program);

		//Api Table
		String[] columnNames = {"User", "Full API Key"};
		usersTableModel = new JUneditableTableModel(columnNames);
		jApiTable = new JTable( usersTableModel );
		jApiTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jApiTable.getTableHeader().setReorderingAllowed(false);
		jApiTable.getTableHeader().setResizingAllowed(false);
		jApiScrollPanel = new JScrollPane(jApiTable);
		jPanel.add(jApiScrollPanel);

		//Add Button
		jAdd = new JButton("Add");
		jAdd.setActionCommand(ACTION_ADD);
		jAdd.addActionListener(this);
		jPanel.add(jAdd);

		//Remove Button
		jRemove = new JButton("Remove");
		jRemove.setActionCommand(ACTION_REMOVE);
		jRemove.addActionListener(this);
		jPanel.add(jRemove);


		//Human/Characters Table
		String[] characterColumnNames = {"User", "Name", "Corporation", "Corporation Assets", "Show Assets"};
		humansTableModel = new JUneditableTableModel(characterColumnNames);
		humansTableModel.addTableModelListener(this);
		JTable jCharacterTable = new JTable( humansTableModel );
		jCharacterTable.getTableHeader().setReorderingAllowed(false);
		JScrollPane jCharacterScrollPanel = new JScrollPane(jCharacterTable);
		jPanel.add(jCharacterScrollPanel);

		//Done Button
		JButton jDone = new JButton("Done");
		jDone.setActionCommand(ACTION_DONE);
		jDone.addActionListener(this);
		jPanel.add(jDone);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jApiScrollPanel, 550, 550, 550)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jAdd, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jRemove, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jCharacterScrollPanel, 550, 550, 550)
					.addComponent(jDone, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
			)

		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jApiScrollPanel, 142, 142, 142)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jAdd, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jCharacterScrollPanel, 142, 142, 142)
				.addComponent(jDone, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
		updateTable();
	}

	public void updateTable(){
		List<Account> accounts = program.getSettings().getAccounts();
		usersTableModel.setRowCount(0);
		humansTableModel.setRowCount(0);
		for (int a = 0; a < accounts.size(); a++){
			Account account = accounts.get(a);
			usersTableModel.addRow( new String[] {String.valueOf(account.getUserID() ), account.getApiKey()});
			List<Human> humans = account.getHumans();
			for (int b = 0; b < humans.size(); b++){
				Human human = humans.get(b);
				humansTableModel.addRow( new Object[] {String.valueOf(human.getCharacterID()), human.getName(), human.getCorporation(), human.isUpdateCorporationAssets(), human.isShowAssets()} );
			}
		}
		if (jApiTable.getRowCount() > 0){
			jApiTable.setRowSelectionInterval(0, 0);
			jRemove.setEnabled(true);
		} else {
			jRemove.setEnabled(false);
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jAdd;
	}

	@Override
	protected void windowShown() {
		jApiTable.getColumnModel().getColumn(0).setPreferredWidth(65);
		jApiTable.getColumnModel().getColumn(1).setPreferredWidth(jApiScrollPanel.getViewportBorderBounds().width - 65); //455
	}

	@Override
	protected void windowActivated() {}

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
			if (!shownAssetsCopy.equals(shownAssets)){
				program.assetsChanged();
				program.charactersChanged();
			}
			if (!corpAssetsCopy.equals(corpAssets)){
				program.assetsChanged();
				program.charactersChanged();
				JOptionPane.showMessageDialog(program.getFrame(), "Corporation asset settings changed.\r\nYou need to update asset before the new settings take effect\r\nTo update assets select:\r\nOptions > Update Assets", "Corporation Asset Settings", JOptionPane.PLAIN_MESSAGE);
			}
			this.setVisible(false);
		}
		if (ACTION_REMOVE.equals(e.getActionCommand())) {
			int selectedRow = jApiTable.getSelectedRow();
			if (selectedRow == -1){
				JOptionPane.showMessageDialog(program.getFrame(), "Nothing to remove...", "Remove Api Key", JOptionPane.PLAIN_MESSAGE);
				return;
			}
			int userID = Integer.parseInt((String)usersTableModel.getValueAt(selectedRow, 0));
			String apiKey = (String) usersTableModel.getValueAt(selectedRow, 1);

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

			int nReturn = JOptionPane.showConfirmDialog(program.getFrame(), "Remove API Key: "+userID+"?\r\n"+users, "Remove Api Key", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.YES_OPTION){
				usersTableModel.removeRow(selectedRow);
				program.getSettings().getAccounts().remove( tempAccount );
				updateTable();
				program.assetsChanged();
			}
		}
		if (ApiAddDialog.ACTION_ADD_KEY_OK.equals(e.getActionCommand())) {
			addHumanTask = new AddHumanTask();
			addHumanTask.addPropertyChangeListener(this);
			addHumanTask.execute();
		}

	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE){
			int rowStart = e.getFirstRow();
			int rowEnd = e.getLastRow();
			int column = e.getColumn();
			for (int a = rowStart; a <= rowEnd; a++){
				boolean booleanColumn = (Boolean) humansTableModel.getValueAt(a, column);
				String characterID = (String) humansTableModel.getValueAt(a, 0);
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		apiAddDialog.setEnabledAll(false);
		if (addHumanTask.throwable != null){
			Log.error("Uncaught Exception (SwingWorker): Please email the latest error.txt in the logs directory to niklaskr@gmail.com", addHumanTask.throwable);
		}
		if (addHumanTask.result == 10 && addHumanTask.done){
			addHumanTask.done = false;
			apiAddDialog.setVisible(false);
			JOptionPane.showMessageDialog(dialog, "Could not add API Key.\r\nThe API Key has already been added...", "Add API Key", JOptionPane.PLAIN_MESSAGE);
		}
		if (addHumanTask.result == 20 && addHumanTask.done){
			addHumanTask.done = false;
			apiAddDialog.setVisible(false);
			JOptionPane.showMessageDialog(dialog, "Could not add API Key.\r\nPlease connect to the internet and try again...", "Add API Key", JOptionPane.PLAIN_MESSAGE);
		}
		if (addHumanTask.result == 30 && addHumanTask.done){
			addHumanTask.done = false;
			apiAddDialog.setVisible(false);
			JOptionPane.showMessageDialog(dialog, "The entered API Key is not a valid Full Access API Key", "Add API Key", JOptionPane.PLAIN_MESSAGE);
		}
		if (addHumanTask.result == 100 && addHumanTask.done){
			addHumanTask.done = false;
			program.getSettings().getAccounts().add(addHumanTask.account);
			updateTable();
			program.charactersChanged();
			apiAddDialog.setVisible(false);
			JOptionPane.showMessageDialog(dialog, "API Key added\r\nTo update assets select:\r\nOptions > Update Assets", "Add API Key", JOptionPane.PLAIN_MESSAGE);
		}
	}

	class AddHumanTask extends SwingWorker<Void, Void> {

		private int result = 0;
		private boolean done = false;
		private Account account;
		private Throwable throwable = null;

		public AddHumanTask() {

		}

		@Override
		public Void doInBackground() {
			setProgress(0);
			try {
				int userID = apiAddDialog.getUserId();
				String apiKey = apiAddDialog.getApiKey();
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
				ok = EveApiHumansReader.load(program.getSettings(), account, true);
				if (!ok){
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