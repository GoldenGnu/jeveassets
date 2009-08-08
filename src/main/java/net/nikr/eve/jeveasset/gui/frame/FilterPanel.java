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

package net.nikr.eve.jeveasset.gui.frame;

import net.nikr.eve.jeveasset.gui.shared.JProgramPanel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;
import net.nikr.eve.jeveasset.gui.table.EveAssetMatcherEditor;
import net.nikr.eve.jeveasset.gui.table.MatcherEditorManager;


public class FilterPanel extends JProgramPanel {

	public final static String ACTION_REMOVE_FILTER = "ACTION_REMOVE_FILTER";

	private JComboBox jAnd;
	private JComboBox jColumn;
	private JComboBox jMode;
	private JTextField jText;
	private JButton jRemove;

	private EveAssetMatcherEditor eveAssetMatcherEditor;
	
	public FilterPanel(Program program, MatcherEditorManager matcherEditorManager, ToolPanel filtersPanel) {
		super(program);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		eveAssetMatcherEditor = new EveAssetMatcherEditor(program);
		matcherEditorManager.add(eveAssetMatcherEditor);

		jAnd = eveAssetMatcherEditor.getAnd();
		this.getPanel().add(jAnd);

		jColumn = eveAssetMatcherEditor.getColumn();
		this.getPanel().add(jColumn);

		jMode = eveAssetMatcherEditor.getMode();
		this.getPanel().add(jMode);

		jText = eveAssetMatcherEditor.getText();
		this.getPanel().add(jText);
		
		jRemove = new JButton();
		jRemove.setIcon( ImageGetter.getIcon("delete.png") );
		jRemove.setActionCommand(ACTION_REMOVE_FILTER);
		jRemove.addActionListener(filtersPanel);
		this.getPanel().add(jRemove);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jAnd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jColumn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jText, 150, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(jRemove, 30, 30, 30)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jAnd, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jColumn, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jMode, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jText, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
			)
		);
	}

	public void hideButton(){
		jRemove.setEnabled(false);
	}
	public void showButton(){
		jRemove.setEnabled(true);
	}
	public AssetFilter getAssetFilter(){
		String column = (String)jColumn.getSelectedItem();
		String text = jText.getText();
		String mode = (String) jMode.getSelectedItem();
		String sAnd = (String)jAnd.getSelectedItem();
		boolean and = (sAnd.equals(AssetFilter.AND));
		
		return new AssetFilter(column, text, mode, and);
	}
	public void setAssetFilter(AssetFilter assetFilter){
		jText.setText(assetFilter.getText());
		jColumn.setSelectedItem(assetFilter.getColumn());
		jMode.setSelectedItem(assetFilter.getMode());
		if (assetFilter.isAnd()){
			jAnd.setSelectedItem(AssetFilter.AND);
		} else {
			jAnd.setSelectedItem(AssetFilter.OR);
		}
	}

	public EveAssetMatcherEditor getEveAssetMatcherEditor() {
		return eveAssetMatcherEditor;
	}

	@Override
	protected JProgramPanel getThis(){
		return this;
	}
}
