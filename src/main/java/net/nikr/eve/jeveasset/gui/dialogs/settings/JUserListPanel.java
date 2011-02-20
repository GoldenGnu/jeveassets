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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.UserListItem;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;


public abstract class JUserListPanel<K, I extends UserListItem<K>> extends JSettingsPanel implements ActionListener, FocusListener {

	public final static String ACTION_DEFAULT = "ACTION_DEFAULT";
	public final static String ACTION_DELETE = "ACTION_DELETE";
	public final static String ACTION_SELECTED = "ACTION_SELECTED";

	public final static int FILTER_NO_RESTRICTIONS = 0;
	public final static int FILTER_WORDS_ONLY = 1;
	public final static int FILTER_INTEGERS_ONLY = 2;
	public final static int FILTER_NUMBERS_ONLY = 3;


	private JComboBox jItems;
	private JTextField jItem;
	private JButton jDefault;
	private JButton jDelete;

	private Map<K, I> items;
	private I lastItem = null;
	private I newItem = null;



	public JUserListPanel(Program program, SettingsDialog optionsDialog, Icon icon, DefaultMutableTreeNode parentNode, int filter, String title, String list, String item, String help) {
		super(program, optionsDialog, title, icon, parentNode);

		JLabel jSetPriceLable = new JLabel(list+":");
		jItems = new JComboBox();
		jItems.setActionCommand(ACTION_SELECTED);
		jItems.addActionListener(this);

		JLabel jPriceLable = new JLabel(item+":");
		jItem = new JTextField();
		switch (filter){
			case FILTER_WORDS_ONLY:
				jItem.setDocument( DocumentFactory.getWordPlainDocument() );
				break;
			case FILTER_INTEGERS_ONLY:
				jItem.setDocument( DocumentFactory.getIntegerPlainDocument() );
				break;
			case FILTER_NUMBERS_ONLY:
				jItem.setDocument( DocumentFactory.getDoublePlainDocument() );
				break;
			case FILTER_NO_RESTRICTIONS:
			default:
				jItem.setDocument( new PlainDocument() );
		}
		JCopyPopup.install(jItem);
		jItem.addFocusListener(this);
		//jPanel.add(jItem);

		jDelete = new JButton("Delete");
		jDelete.setActionCommand(ACTION_DELETE);
		jDelete.addActionListener(this);
		//jPanel.add(jDelete);

		jDefault = new JButton("Default");
		jDefault.setActionCommand(ACTION_DEFAULT);
		jDefault.addActionListener(this);
		//jPanel.add(jDefault);

		JTextArea jHelp = new JTextArea(help);
		jHelp.setEditable(false);
		jHelp.setFocusable(false);
		jHelp.setOpaque(false);
		jHelp.setFont(jSetPriceLable.getFont() );

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jPriceLable)
						.addComponent(jSetPriceLable)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jItems, 200, 200, 200)
						.addComponent(jItem, 200, 200, 200)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jDelete, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jDefault, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)

					)
				)
				.addComponent(jHelp)

		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jSetPriceLable, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jItems, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDelete, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jPriceLable, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jItem, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDefault, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jHelp)
		);
	}

	protected abstract Map<K, I> getItems();
	protected abstract void setItems(Map<K, I> items);
	protected abstract I newItem(I item);
	protected abstract I valueOf(Object o);
	protected abstract String getDefault(I item);
	
	private void setEnabledAll(boolean b){
		jItems.setEnabled(b);
		jItem.setEnabled(b);
		jDefault.setEnabled(b);
		jDelete.setEnabled(b);
	}

	private void updateItem(){
		if (lastItem != null){
			lastItem.setValue(jItem.getText());
			//updating last UserPrice
			if (items.containsKey(lastItem.getKey())){
				items.remove(lastItem.getKey());
			}
			items.put(lastItem.getKey(), lastItem);
		}
	}

	private void update(){
		Vector<I> names = new Vector<I>();
		lastItem = null;
		for (Map.Entry<K, I> entry : items.entrySet()){
			I item = entry.getValue();
			names.add(item);
		}
		Collections.sort(names);
		if (!items.isEmpty()){
			lastItem = names.get(0);
			jItems.setModel( new DefaultComboBoxModel(names) );
			jItem.setText(lastItem.getValue());
			setEnabledAll(true);
		} else {
			jItems.setModel( new DefaultComboBoxModel() );
			jItems.addItem("No values found");
			jItem.setText("");
			setEnabledAll(false);
		}
	}

	public void setNewItem(I newItem){
		this.newItem = newItem;
	}

	@Override
	public boolean save() {
		updateItem();
		boolean update = !getItems().equals(items);
		//Update Settings
		setItems(items);
		//Update this
		int selected = jItems.getSelectedIndex();
		load();
		if (selected >= 0) jItems.setSelectedIndex(selected);
		//Update table if needed
		return update;
	}

	@Override
	public void load(){
		//Clear temp settings
		items = new HashMap<K, I>();
		if (newItem != null
						&& !getItems().containsKey(newItem.getKey())){
			items.put(newItem.getKey(), newItem);
		}
		for (Map.Entry<K, I> entry : getItems().entrySet()){
			I item = newItem(entry.getValue());
			items.put(item.getKey(), item);
		}
		update();
		if (newItem != null){
			jItems.setSelectedItem(newItem);
		}
		this.newItem = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_DEFAULT.equals(e.getActionCommand())){
			I item = valueOf(jItems.getSelectedItem());
			jItem.setText( getDefault(item) );
		}
		if (ACTION_DELETE.equals(e.getActionCommand())){
			I item = valueOf(jItems.getSelectedItem());
			if (items.containsKey(item.getKey())){
				items.remove(item.getKey());
			}
			update();
		}
		if (ACTION_SELECTED.equals(e.getActionCommand())){
			I item = valueOf(jItems.getSelectedItem());
			if (item == null) return;
			if (items.containsKey(item.getKey())){
				item = items.get(item.getKey());
			}
			if (!lastItem.equals(item)){
				updateItem();
				lastItem = item;
			}
			jItem.setText(item.getValue());
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (jItem.equals(e.getSource())){
			jItem.selectAll();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {

	}


}
