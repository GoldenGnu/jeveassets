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

package net.nikr.eve.jeveasset.gui.shared.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class EditColumnsDialog<T extends Enum<T> & EnumTableColumn<Q>, Q> extends JDialogCentered implements ActionListener{

	private final static String ACTION_OK = "ACTION_OK";
	private final static String ACTION_CANCEL = "ACTION_CANCEL";
	private final static String ACTION_CHECK_ALL = "ACTION_CHECK_ALL";
	
	private DefaultListModel listModel = new DefaultListModel();
	private JList jList;
	private JCheckBox jAll;
	private JButton jOk;
	private JButton jCancel;
	
	private JTextArea jInfo;
	private EnumTableFormatAdaptor<T, Q> adaptor;
	
	public EditColumnsDialog(Program program, EnumTableFormatAdaptor<T, Q> adaptor) {
		super(program, GuiShared.get().columnsTitle(), Images.TABLE_COLUMN_SHOW.getImage());
		this.adaptor = adaptor;
		
		jInfo = new JTextArea();
		jInfo.setFont(jPanel.getFont());
		jInfo.setOpaque(false);
		jInfo.setFocusable(false);
		jInfo.setEditable(false);
		jInfo.setLineWrap(true);
		jInfo.setWrapStyleWord(true);
		jInfo.setText(GuiShared.get().columnsTip());
		
		jList = new JList(listModel);
		jList.setCellRenderer(new JCheckBoxListRenderer());
		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//jList.setBorder(BorderFactory.createEtchedBorder());
		jList.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

		// Add a mouse listener to handle changing selection
		jList.addMouseListener(new MouseAdapter(){
			@Override
				public void mouseClicked(MouseEvent event){
					JList list = (JList) event.getSource();

					// Get index of item clicked
					int index = list.locationToIndex(event.getPoint());
					SimpleColumn column = (SimpleColumn) list.getModel().getElementAt(index);

					// Toggle selected state
					column.setShown(!column.isShown());
					
					updateAll();
					
					// Repaint cell
					list.repaint(list.getCellBounds(index, index));
		}
		});
		
		jCancel = new JButton(GuiShared.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);
		
		jAll = new JCheckBox(GuiShared.get().checkAll());
		jAll.setActionCommand(ACTION_CHECK_ALL);
		jAll.addActionListener(this);
		
		jOk = new JButton(GuiShared.get().ok());
		jOk.setActionCommand(ACTION_OK);
		jOk.addActionListener(this);
		
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jAll)
				.addGroup(layout.createSequentialGroup()
					.addGap(2)
					.addComponent(jList, 300, 300, 300)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jInfo, 300, 300, 300)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jOk, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jList, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jInfo, Program.BUTTONS_HEIGHT*2, Program.BUTTONS_HEIGHT*2, Program.BUTTONS_HEIGHT*2)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOk, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}
	
	@Override
	protected JComponent getDefaultFocus() {
		return jOk;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOk;
	}

	@Override
	protected void windowShown() {
		
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			load();
		}
		super.setVisible(b);
	}
	
	
	
	private void load(){
		listModel.clear();
		for (SimpleColumn column : adaptor.getColumns()){
			listModel.addElement(column);
		}
		updateAll();
	}
	
	private void updateAll(){
		boolean allCheck = true;
		for (int i = 0; i < listModel.size(); i++){
			SimpleColumn column = (SimpleColumn) listModel.getElementAt(i);
			if (!column.isShown()){
				allCheck = false;
				break;
			}
		}
		jAll.setSelected(allCheck);
	}

	@Override
	protected void save() {
		List<SimpleColumn> columns = new ArrayList<SimpleColumn>();
		for (int i = 0; i < listModel.size(); i++){
			columns.add( (SimpleColumn) listModel.getElementAt(i) );
		}
		adaptor.setColumns(columns);
		setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_OK.equals(e.getActionCommand())){
			save();
		}
		if (ACTION_CHECK_ALL.equals(e.getActionCommand())){
			boolean check = jAll.isSelected();
			for (int i = 0; i < listModel.size(); i++){
				SimpleColumn column = (SimpleColumn) listModel.getElementAt(i);
				column.setShown(check);
			}
			jList.repaint();
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			setVisible(false);
		}
	}

	private class JCheckBoxListRenderer extends DefaultListCellRenderer{
		
		private JCheckBox checkBox = new JCheckBox();
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
			JLabel jLabel = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
			SimpleColumn column = (SimpleColumn) value;
			
			//Formating
			checkBox.setEnabled(jLabel.isEnabled());
			checkBox.setFont(jLabel.getFont());
			checkBox.setBackground(jLabel.getBackground());
			checkBox.setForeground(jLabel.getForeground());
			checkBox.setBorder(jLabel.getBorder());
			
			//Values			
			checkBox.setSelected(column.isShown());
			checkBox.setText(column.getColumnName());
			return checkBox;
		}
	}
}
