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

package net.nikr.eve.jeveasset.gui.shared.components;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;


public abstract class JMainTab {

	private String title;
	private Icon icon;
	private boolean closeable;
	private List<JLabel> statusbarLabels = new ArrayList<JLabel>();
	protected Program program;
	protected JPanel jPanel;
	protected GroupLayout layout;

	protected JMainTab(final boolean load) { }

	public JMainTab(final Program program, final String title, final Icon icon, final boolean closeable) {
		this.program = program;
		this.title = title;
		this.icon = icon;
		this.closeable = closeable;

		program.addMainTab(this);

		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
	}

	public abstract void updateTableMenu(JComponent jComponent);
	/** Must be called after setting SelectionModel.
	 * @param e mouse event
	 */
	protected void showTablePopupMenu(final MouseEvent e) {
		JPopupMenu jTablePopupMenu = new JPopupMenu();

		selectClickedCell(e);

		updateTableMenu(jTablePopupMenu);

		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	/**
	 * Overwrite to update settings before saving...
	 */
	public void updateSettings() { }

	public void addStatusbarLabel(final JLabel jLabel) {
		statusbarLabels.add(jLabel);
	}

	public List<JLabel> getStatusbarLabels() {
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

	protected void addSeparator(final JComponent jComponent) {
		if (jComponent instanceof JMenu) {
			JMenu jMenu = (JMenu) jComponent;
			jMenu.addSeparator();
		}
		if (jComponent instanceof JPopupMenu) {
			JPopupMenu jPopupMenu = (JPopupMenu) jComponent;
			jPopupMenu.addSeparator();
		}
		if (jComponent instanceof JDropDownButton) {
			JDropDownButton jDropDownButton = (JDropDownButton) jComponent;
			jDropDownButton.addSeparator();
		}
	}

	protected void installTableMenu(final JTable jTable) {
		TableMenuListener listener = new TableMenuListener(jTable);
		jTable.addMouseListener(listener);
		jTable.getSelectionModel().addListSelectionListener(listener);
		jTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
	}

	protected void selectClickedCell(final MouseEvent e) {
		Object source = e.getSource();
		if (source instanceof JTable) {
			JTable jTable = (JTable) source;

			//Rows
			boolean clickInRowsSelection = false;
			int[] selectedRows = jTable.getSelectedRows();
			for (int a = 0; a < selectedRows.length; a++) {
				if (selectedRows[a] == jTable.rowAtPoint(e.getPoint())) {
					clickInRowsSelection = true;
					break;
				}
			}

			//Column
			boolean clickInColumnsSelection = false;
			int[] selectedColumns = jTable.getSelectedColumns();
			for (int a = 0; a < selectedColumns.length; a++) {
				if (selectedColumns[a] == jTable.columnAtPoint(e.getPoint())) {
					clickInColumnsSelection = true;
					break;
				}
			}

			//Clicked outside selection, select clicked cell
			if (!clickInRowsSelection || !clickInColumnsSelection) {
				jTable.setRowSelectionInterval(jTable.rowAtPoint(e.getPoint()), jTable.rowAtPoint(e.getPoint()));
				jTable.setColumnSelectionInterval(jTable.columnAtPoint(e.getPoint()), jTable.columnAtPoint(e.getPoint()));
			}
		}
	}

	private class TableMenuListener implements MouseListener, ListSelectionListener {

		private JTable jTable;

		public TableMenuListener(final JTable jTable) {
			this.jTable = jTable;
		}

		@Override
		public void mouseClicked(final MouseEvent e) { }

		@Override
		public void mousePressed(final MouseEvent e) {
			if (e.getSource().equals(jTable) && e.isPopupTrigger()) {
				showTablePopupMenu(e);
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			if (e.getSource().equals(jTable) && e.isPopupTrigger()) {
				showTablePopupMenu(e);
			}
		}

		@Override
		public void mouseEntered(final MouseEvent e) { }

		@Override
		public void mouseExited(final MouseEvent e) { }

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				program.updateTableMenu();
			}
		}
	}
}
