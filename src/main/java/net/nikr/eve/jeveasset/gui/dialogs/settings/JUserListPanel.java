/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.UserItem;


public abstract class JUserListPanel<K, V extends Comparable<V>> extends JSettingsPanel {

	public final static String ACTION_DELETE = "ACTION_DELETE";
	public final static String ACTION_EDIT = "ACTION_EDIT";

	private JComboBox jItems;
	private JButton jEdit;
	private JButton jDelete;

	private Map<K, UserItem<K,V>> items;
	private List<UserItem<K,V>> listItems;
	private String type;

	public JUserListPanel(Program program, SettingsDialog optionsDialog, Icon icon, DefaultMutableTreeNode parentNode, String title, String type, String help) {
		super(program, optionsDialog, title, icon, parentNode);
		this.type = type;

		ListenerClass listener = new ListenerClass();

		jItems = new JComboBox();

		jEdit = new JButton("Edit");
		jEdit.setActionCommand(ACTION_EDIT);
		jEdit.addActionListener(listener);

		jDelete = new JButton("Delete");
		jDelete.setActionCommand(ACTION_DELETE);
		jDelete.addActionListener(listener);

		JTextArea jHelp = new JTextArea(help);
		jHelp.setEditable(false);
		jHelp.setFocusable(false);
		jHelp.setOpaque(false);
		jHelp.setFont(jPanel.getFont());

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jItems)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jEdit, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jDelete, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
				.addComponent(jHelp)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jItems, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup()
					.addComponent(jEdit, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDelete, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jHelp)
		);
	}

	abstract protected Map<K, UserItem<K,V>> getItems();
	abstract protected void setItems(Map<K, UserItem<K,V>> items);
	abstract protected V valueOf(String value) ;
	abstract protected UserItem<K,V> newUserItem(UserItem<K,V> userItem);

	private void setEnabledAll(boolean b){
		jItems.setEnabled(b);
		jEdit.setEnabled(b);
		jDelete.setEnabled(b);
	}

	public void edit(UserItem<K,V> userItem){
		edit(userItem, true);
	}

	private void edit(UserItem<K,V> userItem, boolean save){
		edit(userItem, save, null);
	}

	private void edit(UserItem<K,V> userItem, boolean save, String oldValue){
		if (save) load();
		String value = (String)JOptionPane.showInputDialog(program.getMainWindow().getFrame(), userItem.getName(), "Edit "+type, JOptionPane.PLAIN_MESSAGE, null, null, oldValue != null ? oldValue : userItem.getValueFormated());
		if (value != null){
			V v = valueOf(value);
			if (v != null){
				//Add if needed
				if (!items.containsKey(userItem.getKey())){
					items.put(userItem.getKey(), userItem);
					listItems.add(userItem);
				}
				//Update Value
				userItem.setValue(v);
				//Update GUI
				updateGUI();
				if (save){ //Save (if not in setttings dialog)
					boolean update = save();
					if (update) program.updateEventList();
				}
			} else {
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Input not valid", "Bad input", JOptionPane.PLAIN_MESSAGE);
				edit(userItem, save, value);
			}
		}
	}

	public void delete(UserItem<K,V> userItem){
		delete(userItem, true);
	}

	private void delete(UserItem<K,V> userItem, boolean save){
		if (save) load();
		int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), userItem.getName()+"\n["+userItem.getValueFormated()+"]", "Delete "+type, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (value == JOptionPane.OK_OPTION){
			items.remove(userItem.getKey());
			listItems.remove(userItem);
			updateGUI();
			if (save){
				boolean update = save();
				if (update) program.updateEventList();
			}
		}
	}

	private void updateGUI(){
		if (items.isEmpty()){
			setEnabledAll(false);
			jItems.setModel( new DefaultComboBoxModel() );
			jItems.getModel().setSelectedItem("Empty");
		} else {
			setEnabledAll(true);
			Collections.sort(listItems);
			jItems.setModel( new DefaultComboBoxModel(listItems.toArray()) );
		}
	}

	@Override
	public boolean save() {
		boolean update = !getItems().equals(items);
		//Update Settings
		setItems(items);
		//Update this
		int selected = jItems.getSelectedIndex();
		if (update) load();
		if (selected >= 0) jItems.setSelectedIndex(selected);
		//Update table if needed
		return update;
	}

	@Override
	public void load(){
		items = new HashMap<K, UserItem<K,V>>();
		listItems = new ArrayList<UserItem<K,V>>();
		for (Entry<K, UserItem<K,V>> entry : getItems().entrySet()){
			UserItem<K,V> userItem = newUserItem(entry.getValue());
			items.put(entry.getKey(), userItem);
			listItems.add(userItem);
		}
		updateGUI();
	}

	private class ListenerClass implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (ACTION_DELETE.equals(e.getActionCommand())){
				int index = jItems.getSelectedIndex();
				if (index >= 0) delete(listItems.get(index), false);
			}
			if (ACTION_EDIT.equals(e.getActionCommand())){
				int index = jItems.getSelectedIndex();
				if (index >= 0) edit(listItems.get(index), false);
			}
		}
	}
}
