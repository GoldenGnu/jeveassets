/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import net.nikr.eve.jeveasset.data.TableSettings;
import net.nikr.eve.jeveasset.data.TableSettings.ResizeMode;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JColumnTable extends JTable {

	private final static String ACTION_AUTO_RESIZING_COLUMNS_TEXT = "ACTION_AUTO_RESIZING_COLUMNS_TEXT";
	private final static String ACTION_AUTO_RESIZING_COLUMNS_WINDOW = "ACTION_AUTO_RESIZING_COLUMNS_WINDOW";
	private final static String ACTION_DISABLE_AUTO_RESIZING_COLUMNS = "ACTION_DISABLE_AUTO_RESIZING_COLUMNS";
	private final static String ACTION_RESET_COLUMNS_TO_DEFAULT = "ACTION_SHOW_ALL_COLUMNS";

	private JScrollPane jTableScroll;
	private JDropDownButton jColumnsSelection;
	private JMenu jColumnMenu;

	private List<ColumnTableListener> listeners = new ArrayList<ColumnTableListener>();
	private ListenerClass listenerClass = new ListenerClass();

	private TableSettings columnTableSettings;

	public JColumnTable(AbstractTableModel abstractTableModel, TableSettings columnTableSettings) {
		super(abstractTableModel);
		this.columnTableSettings = columnTableSettings;

		//Listeners
		this.getTableHeader().addMouseListener(listenerClass);
		this.addPropertyChangeListener("tableHeader", listenerClass);

		this.getColumnModel().addColumnModelListener(listenerClass);
		this.addPropertyChangeListener("columnModel", listenerClass);

		abstractTableModel.addTableModelListener(listenerClass);
		this.addPropertyChangeListener("model", listenerClass);

		//Table Button
		jColumnsSelection = new JDropDownButton(JDropDownButton.RIGHT);
		jColumnsSelection.setIcon(Images.ICON_ARROW_DOWN);
		jColumnsSelection.setHorizontalAlignment(SwingConstants.RIGHT);
		jColumnsSelection.setBorder(null);
		jColumnsSelection.addMouseListener(listenerClass);

		//Table Menu
		jColumnMenu = new JMenu(GuiShared.get().columns());
		jColumnMenu.setIcon(Images.ICON_TABLE_SHOW);

		//Table Scrollpanel
		jTableScroll = new JScrollPane(this);
		jTableScroll.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, jColumnsSelection);
		jTableScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jTableScroll.setAutoscrolls(true);
		jTableScroll.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,0,0,0), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)) );

		tableUpdateColumnMenus();
		tableUpdateCoulmnsSize();
	}

	public AbstractTableModel getAbstractTableModel(){
		TableModel tableModel = getModel();
		if (tableModel instanceof AbstractTableModel){
			return (AbstractTableModel) tableModel;
		}
		return null;
	}

	public TableSettings getColumnTableSettings() {
		return columnTableSettings;
	}

	public void addColumnTableListener(ColumnTableListener listener){
		listeners.add(listener);
	}

	public void removeColumnTableListener(ColumnTableListener listener){
		listeners.remove(listener);
	}

	protected void tableUpdate(){
		for (ColumnTableListener listener : listeners){
			listener.tableUpdate();
		}
	}

	public JMenu getMenu() {
		return jColumnMenu;
	}

	public JScrollPane getScroll() {
		return jTableScroll;
	}

	private void tableUpdateCoulmnsSize(){
		if (columnTableSettings.getMode().equals(ResizeMode.TEXT)){
			TableColumnUtil.resizeColumnsText(this, jTableScroll);
		}
		if (columnTableSettings.getMode().equals(ResizeMode.WINDOW)){
			TableColumnUtil.resizeColumnsWindow(this);
		}
		if (!columnTableSettings.getMode().equals(ResizeMode.TEXT) && !columnTableSettings.getMode().equals(ResizeMode.WINDOW)){
			this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}
	}

	private void tableUpdateTableStructure(){
		if (columnTableSettings.getMode().equals(ResizeMode.TEXT)){
			getAbstractTableModel().fireTableStructureChanged();
			tableUpdateCoulmnsSize();
		} else {
			Map<String, Integer> widths = new HashMap<String, Integer>();
			for (int a = 0; a < this.getColumnCount(); a++){
				int width = this.getColumnModel().getColumn(a).getPreferredWidth();
				String name = (String)this.getColumnModel().getColumn(a).getHeaderValue();
				widths.put(name, width);
			}
			getAbstractTableModel().fireTableStructureChanged();
			for (int a = 0; a < this.getColumnCount(); a++){
				String name = (String)this.getColumnModel().getColumn(a).getHeaderValue();
				if (widths.containsKey(name)){
					int width = widths.get(name);
					this.getColumnModel().getColumn(a).setPreferredWidth(width);
				} else {
					TableColumnUtil.resizeColumn(this, this.getColumnModel().getColumn(a));
				}
			}
		}
	}
	
	private void tableUpdateColumnMenus(){
		tableUpdateColumnMenu(jColumnsSelection);
		tableUpdateColumnMenu(jColumnMenu);
	}

	private void tableUpdateColumnMenu(JComponent jComponent){
		jComponent.removeAll();

		JCheckBoxMenuItem jCheckBoxMenuItem;
		JRadioButtonMenuItem jRadioButtonMenuItem;
		JMenuItem  jMenuItem;

		jMenuItem = new JMenuItem(GuiShared.get().reset());
		jMenuItem.setActionCommand(ACTION_RESET_COLUMNS_TO_DEFAULT);
		jMenuItem.addActionListener(listenerClass);
		jComponent.add(jMenuItem);

		addSeparator(jComponent);

		ButtonGroup group = new ButtonGroup();

		jRadioButtonMenuItem = new JRadioButtonMenuItem(GuiShared.get().autoText());
		jRadioButtonMenuItem.setIcon(Images.ICON_TABLE_RESIZE);
		jRadioButtonMenuItem.setActionCommand(ACTION_AUTO_RESIZING_COLUMNS_TEXT);
		jRadioButtonMenuItem.addActionListener(listenerClass);
		jRadioButtonMenuItem.setSelected(columnTableSettings.getMode().equals(ResizeMode.TEXT));
		group.add(jRadioButtonMenuItem);
		jComponent.add(jRadioButtonMenuItem);

		jRadioButtonMenuItem = new JRadioButtonMenuItem(GuiShared.get().autoWindow());
		jRadioButtonMenuItem.setIcon(Images.ICON_TABLE_RESIZE);
		jRadioButtonMenuItem.setActionCommand(ACTION_AUTO_RESIZING_COLUMNS_WINDOW);
		jRadioButtonMenuItem.addActionListener(listenerClass);
		jRadioButtonMenuItem.setSelected(columnTableSettings.getMode().equals(ResizeMode.WINDOW));
		group.add(jRadioButtonMenuItem);
		jComponent.add(jRadioButtonMenuItem);

		jRadioButtonMenuItem = new JRadioButtonMenuItem(GuiShared.get().disable());
		jRadioButtonMenuItem.setIcon(Images.ICON_TABLE_RESIZE);
		jRadioButtonMenuItem.setActionCommand(ACTION_DISABLE_AUTO_RESIZING_COLUMNS);
		jRadioButtonMenuItem.addActionListener(listenerClass);
		jRadioButtonMenuItem.setSelected(!columnTableSettings.getMode().equals(ResizeMode.TEXT) && !columnTableSettings.getMode().equals(ResizeMode.WINDOW));
		group.add(jRadioButtonMenuItem);
		jComponent.add(jRadioButtonMenuItem);

		addSeparator(jComponent);

		for (String columnName : columnTableSettings.getTableColumnNames()){
			jCheckBoxMenuItem = new JCheckBoxMenuItem(columnName);
			jCheckBoxMenuItem.setActionCommand(columnName);
			jCheckBoxMenuItem.addActionListener(listenerClass);
			jCheckBoxMenuItem.setIcon(Images.ICON_TABLE_SHOW);
			jCheckBoxMenuItem.setSelected(columnTableSettings.getTableColumnVisible().contains(columnName));
			jComponent.add(jCheckBoxMenuItem);
		}
	}

	private void addSeparator(JComponent jComponent){
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

	class ListenerClass implements ActionListener, MouseListener, 
			TableColumnModelListener, TableModelListener, PropertyChangeListener{

		//Data
		private boolean columnMoved = false;
		private List<String> tempMainTableColumnNames;
		private List<String> tempMainTableColumnVisible;
		private int rowsLastTime = 0;
		private int rowsCount = 0;


		@Override
		public void actionPerformed(ActionEvent e) {
			if (ACTION_RESET_COLUMNS_TO_DEFAULT.equals(e.getActionCommand())){
				columnTableSettings.resetColumns();
				tableUpdateTableStructure();
				tableUpdateCoulmnsSize();
				tableUpdateColumnMenus();
			}
			if (ACTION_AUTO_RESIZING_COLUMNS_TEXT.equals(e.getActionCommand())){
				columnTableSettings.setMode(ResizeMode.TEXT);
				tableUpdateCoulmnsSize();
				tableUpdateColumnMenus();
			}
			if (ACTION_AUTO_RESIZING_COLUMNS_WINDOW.equals(e.getActionCommand())){
				columnTableSettings.setMode(ResizeMode.WINDOW);
				tableUpdateCoulmnsSize();
				tableUpdateColumnMenus();
			}
			if (ACTION_DISABLE_AUTO_RESIZING_COLUMNS.equals(e.getActionCommand())){
				columnTableSettings.setMode(ResizeMode.NONE);
				for (int a = 0; a < getColumnCount(); a++){
					int width = getColumnModel().getColumn(a).getWidth();
					getColumnModel().getColumn(a).setPreferredWidth(width);
				}
				tableUpdateCoulmnsSize();
				tableUpdateColumnMenus();
			}
			//Hide/show column
			if (e.getSource() instanceof JCheckBoxMenuItem){
				if (columnTableSettings.getTableColumnVisible().contains(e.getActionCommand())){
					columnTableSettings.getTableColumnVisible().remove(e.getActionCommand());
				} else {
					columnTableSettings.getTableColumnVisible().add(e.getActionCommand());
					//Fix column order
					List<String> mainTableColumnVisible = new ArrayList<String>();
					for (String columnName : columnTableSettings.getTableColumnNames()){
						if (columnTableSettings.getTableColumnVisible().contains(columnName)){
							mainTableColumnVisible.add(columnName);
						}
					}
					columnTableSettings.setTableColumnVisible(mainTableColumnVisible);
				}
				tableUpdateTableStructure();
				tableUpdateColumnMenus();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getSource().equals(getTableHeader())){
				tempMainTableColumnNames = new ArrayList<String>(columnTableSettings.getTableColumnNames());
				tempMainTableColumnVisible = new ArrayList<String>(columnTableSettings.getTableColumnVisible());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getSource().equals(getTableHeader()) && columnMoved){
				columnMoved = false;
				columnTableSettings.setTableColumnNames(tempMainTableColumnNames);
				columnTableSettings.setTableColumnVisible(tempMainTableColumnVisible);
				tableUpdateTableStructure();
				tableUpdateColumnMenus();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}


		@Override
		public void columnAdded(TableColumnModelEvent e) {}

		@Override
		public void columnRemoved(TableColumnModelEvent e) {}

		@Override
		public void columnMoved(TableColumnModelEvent e) {
			if (e.getFromIndex() != e.getToIndex()){
				columnMoved = true;

				String movingColumnName = tempMainTableColumnVisible.get(e.getFromIndex());
				String movingToColumnName = tempMainTableColumnVisible.get(e.getToIndex());

				int movingIndex = tempMainTableColumnNames.indexOf(movingColumnName);
				tempMainTableColumnNames.remove(movingIndex);

				int movingToIndex = tempMainTableColumnNames.indexOf(movingToColumnName);
				if (e.getToIndex() > e.getFromIndex()) movingToIndex = movingToIndex + 1;
				tempMainTableColumnNames.add(movingToIndex, movingColumnName);

				List<String> mainTableColumnVisible = new ArrayList<String>();
				String columnOrder = "";
				String columnVisible = "";
				for (int a = 0; a < tempMainTableColumnNames.size(); a++){
					columnOrder = GuiShared.get().whitespace37(columnOrder,
							tempMainTableColumnNames.get(a));
					if (columnTableSettings.getTableColumnVisible().contains(tempMainTableColumnNames.get(a))){
						columnVisible = GuiShared.get().whitespace37(columnVisible,
								tempMainTableColumnNames.get(a));
						mainTableColumnVisible.add(tempMainTableColumnNames.get(a));
					}
				}
				tempMainTableColumnVisible = mainTableColumnVisible;
			}
		}


		@Override
		public void columnMarginChanged(ChangeEvent e) {}

		@Override
		public void columnSelectionChanged(ListSelectionEvent e) {}

		@Override
		public void tableChanged(TableModelEvent e) { //Filter
			if (e.getType() == TableModelEvent.DELETE) rowsCount = rowsCount - (Math.abs(e.getFirstRow()-e.getLastRow())+1);
			if (e.getType() == TableModelEvent.INSERT) rowsCount = rowsCount + (Math.abs(e.getFirstRow()-e.getLastRow())+1);
			if (Math.abs(rowsLastTime + rowsCount) == getRowCount()
					&& e.getType() != TableModelEvent.UPDATE){ //Last Table Update
				rowsLastTime = getRowCount();
				rowsCount = 0;
				tableUpdateCoulmnsSize();
				tableUpdate();
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("tableHeader")){
				Object o = evt.getNewValue();
				if (o instanceof JTableHeader){
					JTableHeader header = (JTableHeader) o;
					header.addMouseListener(listenerClass);
				}
			}
			if (evt.getPropertyName().equals("columnModel")){
				Object o = evt.getNewValue();
				if (o instanceof TableColumnModel){
					TableColumnModel model = (TableColumnModel) o;
					model.addColumnModelListener(listenerClass);
				}
			}
			if (evt.getPropertyName().equals("model")){
				Object o = evt.getNewValue();
				if (o instanceof AbstractTableModel){
					TableModel model = (TableModel) o;
					model.addTableModelListener(listenerClass);
				} else {
					throw new IllegalArgumentException("Cannot set a TableModel that does not extend AbstractTableModel");
				}
			}
		}
	}

	public interface ColumnTableListener {
		public void tableUpdate();
	}

	public static class TableColumnUtil{
		
		private TableColumnUtil() {}

		public static void resizeColumnsText(JTable jTable, JScrollPane jScroll) {
			if (jTable.getRowCount() > 0){
				int size = 0;
				jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				for (int i = 0; i < jTable.getColumnCount(); i++) {
					 size = size+resizeColumn(jTable, jTable.getColumnModel().getColumn(i));
				}
				if (size < jScroll.getSize().width){
					jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				}
			} else {
				for (int i = 0; i < jTable.getColumnCount(); i++) {
					jTable.getColumnModel().getColumn(i).setPreferredWidth(75);
				}
				jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			}
		}

		public static void resizeColumnsWindow(JTable jTable) {
			for (int a = 0; a < jTable.getColumnCount(); a++){
				jTable.getColumnModel().getColumn(a).setPreferredWidth(75);
			}
			jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}

		public static int resizeColumn(JTable jTable, TableColumn column) {
			int maxWidth = 0;
			TableCellRenderer renderer = column.getHeaderRenderer();
			if (renderer == null) {
				renderer = jTable.getTableHeader().getDefaultRenderer();
			}
			Component component = renderer.getTableCellRendererComponent(jTable, column.getHeaderValue(), false, false, 0, 0);
			maxWidth = component.getPreferredSize().width;
			for (int a = 0; a < jTable.getRowCount(); a++){
				renderer = jTable.getCellRenderer(a, column.getModelIndex());
				if (renderer instanceof SeparatorTableCell) continue;
				component = renderer.getTableCellRendererComponent(jTable, jTable.getValueAt(a, column.getModelIndex()), false, false, a, column.getModelIndex());
				maxWidth = Math.max(maxWidth, component.getPreferredSize().width);
			}
			column.setPreferredWidth(maxWidth+4);
			return maxWidth+4;
		}
	}
}
