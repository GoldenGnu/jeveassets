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

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;


public abstract class JMainTab{

	private String title;
	private Icon icon;
	private boolean closeable;
	private List<JLabel> statusbarLabels = new ArrayList<JLabel>();
	protected Program program;
	protected JPanel jPanel;
	protected GroupLayout layout;

	protected JMainTab(boolean load) { }

	public JMainTab(Program program, String title, Icon icon, boolean closeable) {
		this.program = program;
		this.title = title;
		this.icon = icon;
		this.closeable = closeable;

		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
	}

	public abstract void updateTableMenu(JComponent jComponent);
	/**
	 * Must be called after setting SelectionModel
	 * @param e mouse event
	 */
	protected abstract void showTablePopupMenu(MouseEvent e);

	public void addStatusbarLabel(JLabel jLabel){
		statusbarLabels.add(jLabel);
	}

	public List<JLabel> getStatusbarLabels(){
		return statusbarLabels;
	}

	public abstract void updateData();

	public Icon getIcon() {
		return icon;
	}

	public JPanel getPanel() {
		return jPanel;
	}

	public String getTitle() {
		return title;
	}

	public boolean isCloseable() {
		return closeable;
	}

	protected void addSeparator(JComponent jComponent){
		if (jComponent instanceof JMenu){
			JMenu jMenu = (JMenu) jComponent;
			jMenu.addSeparator();
		}
		if (jComponent instanceof JPopupMenu){
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;
			jPopupMenu.addSeparator();
		}
		if (jComponent instanceof JDropDownButton){
			JDropDownButton jDropDownButton = (JDropDownButton) jComponent;
			jDropDownButton.addSeparator();
		}
	}

	protected void installTableMenu(JTable jTable){
		TableMenuListener listener = new TableMenuListener(jTable);
		jTable.addMouseListener(listener);
		jTable.getSelectionModel().addListSelectionListener(listener);
		jTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
	}

	private class TableMenuListener implements MouseListener, ListSelectionListener{

		private JTable jTable;

		public TableMenuListener(JTable jTable) {
			this.jTable = jTable;
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getSource().equals(jTable) && e.isPopupTrigger()){
				showTablePopupMenu(e);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getSource().equals(jTable) && e.isPopupTrigger()){
				showTablePopupMenu(e);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()){
				program.updateTableMenu();
			}
		}
	}
}
