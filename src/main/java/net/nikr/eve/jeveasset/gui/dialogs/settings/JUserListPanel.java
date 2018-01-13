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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Map.Entry;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.gui.shared.components.JLabelMultiline;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public abstract class JUserListPanel<K, V extends Comparable<V>> extends JSettingsPanel {

	private enum UserListAction {
		DELETE, EDIT
	}

	private final JComboBox<UserItem<K, V>> jItems;
	private final JButton jEdit;
	private final JButton jDelete;

	private Map<K, UserItem<K, V>> items;
	private List<UserItem<K, V>> listItems;
	private final String type;

	public JUserListPanel(final Program program, final SettingsDialog optionsDialog, final Icon icon, final String title, final String type, final String help) {
		super(program, optionsDialog, title, icon);
		this.type = type;

		ListenerClass listener = new ListenerClass();

		jItems = new JComboBox<UserItem<K, V>>();

		jEdit = new JButton(DialoguesSettings.get().editItem());
		jEdit.setActionCommand(UserListAction.EDIT.name());
		jEdit.addActionListener(listener);

		jDelete = new JButton(DialoguesSettings.get().deleteItem());
		jDelete.setActionCommand(UserListAction.DELETE.name());
		jDelete.addActionListener(listener);

		JLabelMultiline jHelp = new JLabelMultiline(help);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jItems)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jEdit, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jDelete, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
				.addComponent(jHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jItems, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jEdit, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jDelete, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
		);
	}

	protected abstract Map<K, UserItem<K, V>> getItems();
	protected abstract void setItems(Map<K, UserItem<K, V>> items);
	protected abstract V valueOf(String value);
	protected abstract UserItem<K, V> newUserItem(UserItem<K, V> userItem);
	protected abstract void updateEventList(List<K> keys);

	private void setEnabledAll(final boolean b) {
		jItems.setEnabled(b);
		jEdit.setEnabled(b);
		jDelete.setEnabled(b);
	}

	//Work on live data AKA getItems() - no need to re-cache (never cancel) or update GUI (never shown)
	public boolean contains(final List<UserItem<K, V>> userItems) {
		for (UserItem<K, V> userItem : userItems) {
			if (getItems().containsKey(userItem.getKey())) {
				return true;
			}
		}
		return false;
	}

	//Work on live data AKA getItems() - no need to re-cache (never cancel) or update GUI (never shown)
	public boolean containsKey(final Set<K> key) {
		for (K k : key) {
			if (getItems().containsKey(k)) {
				return true;
			}
		}
		return false;
	}

	public void edit(final UserItem<K, V> userItem) {
		edit(Collections.singletonList(userItem), true, null);
	}

	public void edit(final List<UserItem<K, V>> userItems) {
		edit(userItems, true, null);
	}

	private void edit(final UserItem<K, V> userItem, final boolean save) {
		edit(Collections.singletonList(userItem), save, null);
	}

	private void edit(final List<UserItem<K, V>> userItems, final boolean save, final String oldValue) {
		if (save) {
			load();
		}
		String name;
		String formatedValue = oldValue;
		if (userItems.size() == 1) {
			name = userItems.get(0).getName();
			if (oldValue == null) {
				formatedValue = userItems.get(0).getValueFormated();
			}
		} else {
			name = DialoguesSettings.get().items(userItems.size());
		}
		if (formatedValue == null) {
			formatedValue = "0";
		}
		String value = (String) JOptionPane.showInputDialog(program.getMainWindow().getFrame(), name, DialoguesSettings.get().editTypeTitle(type), JOptionPane.PLAIN_MESSAGE, null, null, formatedValue);
		if (value != null) {
			V v = valueOf(value);
			if (v != null) { //Update value
				List<K> containedKeys = new ArrayList<K>();
				for (UserItem<K, V> userItem : userItems) {
					UserItem<K, V> userItemExisting = items.get(userItem.getKey());;
					if (userItemExisting == null) { //Add new
						userItemExisting = userItem;
						items.put(userItem.getKey(), userItem);
					}
					containedKeys.add(userItem.getKey());
					//Update Value
					userItemExisting.setValue(v);
				}
				//Update GUI
				updateGUI();
				if (save) { //Save (if not in setttings dialog)
					Settings.lock("Custom Price/Name (Edit)"); //Lock for Custom Price/Name (Edit)
					boolean update = save();
					Settings.unlock("Custom Price/Name (Edit)"); //Unlock for Custom Price/Name (Edit)
					if (update) {
						//FIXME - - - > Price/Name: Update Price/Name (no need to update all date - just need to update the data in tags column)
						updateEventList(containedKeys);
					}
					program.saveSettings("Custom Price/Name (Edit)"); //Save Custom Price/Name (Edit)
				}
			} else {
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), DialoguesSettings.get().inputNotValid(), DialoguesSettings.get().badInput(), JOptionPane.PLAIN_MESSAGE);
				edit(userItems, save, value);
			}
		}
	}

	public void delete(final List<UserItem<K, V>> userItems) {
		delete(userItems, true);
	}

	private void delete(final UserItem<K, V> userItem, final boolean save) {
		delete(Collections.singletonList(userItem), save);
	}

	private void delete(final List<UserItem<K, V>> userItems, final boolean save) {
		if (save) {
			load();
		}
		int count = 0;
		String name = ""; //Never used
		List<K> containedKeys = new ArrayList<K>();
		for (UserItem<K, V> userItem : userItems) {
			if (items.containsKey(userItem.getKey())) {
				count++;
				name = userItem.getName();
				containedKeys.add(userItem.getKey());
			}
		}
		if (count > 1) {
			name = DialoguesSettings.get().items(count);
		}
		if (name.isEmpty()) { //this should never happen!
			return;
		}
		int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), name, DialoguesSettings.get().deleteTypeTitle(type), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (value == JOptionPane.OK_OPTION) {
			items.keySet().removeAll(containedKeys);
			updateGUI();
			if (save) { //Save (if not in setttings dialog)
				Settings.lock("Custom Price/Name (Delete)"); //Lock for Custom Price/Name (Delete)
				boolean update = save();
				Settings.unlock("Custom Price/Name (Delete)"); //Unlock for Custom Price/Name (Delete)
				if (update) {
					updateEventList(containedKeys);
				}
				program.saveSettings("Custom Price/Name (Delete)"); //Save Custom Price/Name (Delete)
			}
		}
	}

	private void updateGUI() {
		if (items.isEmpty()) {
			setEnabledAll(false);
			jItems.setModel(new ListComboBoxModel<UserItem<K, V>>());
			jItems.getModel().setSelectedItem(DialoguesSettings.get().itemEmpty());
			listItems = new ArrayList<UserItem<K, V>>(); //Clear list
		} else {
			setEnabledAll(true);
			listItems = new ArrayList<UserItem<K, V>>(new TreeSet<UserItem<K, V>>(items.values()));
			jItems.setModel(new ListComboBoxModel<UserItem<K, V>>(listItems));
		}
	}

	@Override
	public boolean save() {
		boolean update = !getItems().equals(items);
		//Update Settings
		setItems(items);
		//Update this
		int selected = jItems.getSelectedIndex();
		if (update) {
			load();
		}
		if (selected >= 0) {
			jItems.setSelectedIndex(selected);
		}
		//Update table if needed
		return update;
	}

	@Override
	public void load() {
		items = new HashMap<K, UserItem<K, V>>();
		for (Entry<K, UserItem<K, V>> entry : getItems().entrySet()) {
			UserItem<K, V> userItem = newUserItem(entry.getValue());
			items.put(entry.getKey(), userItem);
		}
		updateGUI();
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (UserListAction.DELETE.name().equals(e.getActionCommand())) {
				int index = jItems.getSelectedIndex();
				if (index >= 0) {
					delete(listItems.get(index), false);
				}
			}
			if (UserListAction.EDIT.name().equals(e.getActionCommand())) {
				int index = jItems.getSelectedIndex();
				if (index >= 0) {
					edit(listItems.get(index), false);
				}
			}
		}
	}
}
