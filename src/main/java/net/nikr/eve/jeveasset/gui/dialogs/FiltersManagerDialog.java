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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.AssetFilter;


public class FiltersManagerDialog extends JDialogCentered implements ActionListener, MouseListener {

	public final static String ACTION_DONE = "ACTION_DONE";
	public final static String ACTION_LOAD_FILTER = "ACTION_LOAD_FILTER";
	public final static String ACTION_RENAME_FILTER = "ACTION_RENAME_FILTER";
	public final static String ACTION_DELETE_FILTER = "ACTION_DELETE_FILTER";

	//GUI
	private DefaultListModel listModel;
	private JList jFilters;
	private JButton jDelete;
	private JButton jLoad;
	private JButton jRename;
	
	public FiltersManagerDialog(Program program, Image image) {
		super(program, "Filter Manager", image);

		//Load
		jLoad = new JButton("Load");
		jLoad.setActionCommand(ACTION_LOAD_FILTER);
		jLoad.addActionListener(this);
		jPanel.add(jLoad);

		//Rename
		jRename = new JButton("Rename");
		jRename.setActionCommand(ACTION_RENAME_FILTER);
		jRename.addActionListener(this);
		jPanel.add(jRename);

		//Delete
		jDelete = new JButton("Delete");
		jDelete.setActionCommand(ACTION_DELETE_FILTER);
		jDelete.addActionListener(this);
		jPanel.add(jDelete);


		//List
		listModel = new DefaultListModel();
		jFilters = new JList(listModel);
		jFilters.addMouseListener(this);
		JScrollPane jScrollPanel = new JScrollPane(jFilters);
		jPanel.add(jScrollPanel);

		//Done
		JButton jDone = new JButton("Done");
		jDone.setActionCommand(ACTION_DONE);
		jDone.addActionListener(this);
		jPanel.add(jDone);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup()
				.addComponent(jScrollPanel, 282, 282, 282)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jLoad, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jRename, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jDelete, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jDone, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
				)
			)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jLoad, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRename, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDelete, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jScrollPanel)
				.addComponent(jDone, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
		filtersChanged();
	}

	public String getSelectedString(){
		int selectedIndex =  jFilters.getSelectedIndex();
		if (selectedIndex != -1){
			return (String) listModel.get( jFilters.getSelectedIndex() );
		} else {
			return null;
		}

	}
	private void renameFilter(){
		Boolean bOverwrite = true;
		String filterName = getSelectedString();
		if (filterName == null) return;
		List<AssetFilter> assetFilters = program.getSettings().getAssetFilters().get(filterName);
		String s = (String)JOptionPane.showInputDialog(
					program.getFrame(),
					"Enter filter name:",
					"Rename Filter",
					JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					filterName
					);
		if (s == null){ //Cancel
			return;
		}
		if (s.equals("")){ //No input (needed for name)
			renameFilter();
			return;
		}
		if (program.getSettings().getAssetFilters().containsKey(s) && !s.equals(filterName)){
			int nReturn = JOptionPane.showConfirmDialog(program.getFrame(), "Overwrite?", "Overwrite Filter", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.NO_OPTION){
				bOverwrite = false;
			}
		}
		if (bOverwrite){
			program.getSettings().getAssetFilters().remove(filterName);
			program.getSettings().getAssetFilters().remove(s);
			program.getSettings().getAssetFilters().put(s, assetFilters);
		}
		program.filtersChanged();
	}

	private void deleteFilter(){
		String filterName = getSelectedString();
		if (filterName == null) return;
		int nReturn = JOptionPane.showConfirmDialog(program.getFrame(), "Delete filter:\r\n\""+filterName+"\"?", "Delete Filter", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (nReturn == JOptionPane.YES_OPTION){
			program.getSettings().getAssetFilters().remove(filterName);
			program.filtersChanged();
		}
	}

	private void loadFilter(){
		String filterName = getSelectedString();
		if (filterName == null) return;
		List<AssetFilter> assetFilters = program.getSettings().getAssetFilters().get( filterName );
		program.getToolPanel().setAssetFilters(assetFilters);
		this.setVisible(false);
	}

	public void filtersChanged() {
		listModel.clear();
		List<String> list = new Vector<String>( program.getSettings().getAssetFilters().keySet() );
		Collections.sort(list);
		for (int a = 0; a < list.size(); a++){
			listModel.addElement(list.get(a));
		}
		if (!listModel.isEmpty()){
			if (getSelectedString() == null) jFilters.setSelectedIndex(0);
			jDelete.setEnabled(true);
			jLoad.setEnabled(true);
			jRename.setEnabled(true);
		} else {
			jDelete.setEnabled(false);
			jLoad.setEnabled(false);
			jRename.setEnabled(false);
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jLoad;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_DONE.equals(e.getActionCommand())) {
			this.setVisible(false);
		}
		if (ACTION_LOAD_FILTER.equals(e.getActionCommand())) {
			loadFilter();
		}
		if (ACTION_RENAME_FILTER.equals(e.getActionCommand())) {
			renameFilter();
		}
		if (ACTION_DELETE_FILTER.equals(e.getActionCommand())) {
			deleteFilter();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Object o = e.getSource();
		if (o instanceof JList && e.getClickCount() == 2){
			loadFilter();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

}
