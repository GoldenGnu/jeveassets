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

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.table.AbstractTableModel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JAutoColumnTable;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Candle
 */
public class EnumTableFormatAdaptor<T extends Enum<T> & EnumTableColumn<Q>, Q> implements AdvancedTableFormat<Q>, WritableTableFormat<Q> {

	private final static Logger LOG = LoggerFactory.getLogger(EnumTableFormatAdaptor.class);
	
	private Class<T> enumClass;
	private List<T> shownColumns;
	private List<T> orderColumns;
	private ColumnComparator columnComparator;

	public EnumTableFormatAdaptor(Class<T> enumClass) {
		this.enumClass = enumClass;
		columnComparator = new ColumnComparator();
		reset();
	}
	
	private void reset(){
		shownColumns = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));
		orderColumns = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));
	}

	public List<T> getShownColumns(){
		return shownColumns;
	}

	public List<T> getOrderColumns(){
		return orderColumns;
	}
	
	public void setColumns(List<SimpleColumn> columns){
		if (columns == null) return;
		orderColumns = new ArrayList<T>();
		shownColumns = new ArrayList<T>();
		List<T> originalColumns = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));		
		for (SimpleColumn column : columns){
			try {
				T t = Enum.valueOf(enumClass, column.getName());
				orderColumns.add(t);
				if (column.isShown()) shownColumns.add(t);
			} catch (IllegalArgumentException ex) {
				LOG.info("Removing column: "+column.getName());
			}
			
		}
		//Add new columns
		for (T t : originalColumns){
			if (!orderColumns.contains(t)){
				LOG.info("Adding column: "+t.getColumnName());
				orderColumns.add(t);
				shownColumns.add(t);
			}
		}
	}
	
	public List<SimpleColumn> getColumns(){
		List<SimpleColumn> columns = new ArrayList<SimpleColumn>(orderColumns.size());
		for (T t : orderColumns){
			String name = t.name();
			boolean shown = shownColumns.contains(t);
			columns.add(new SimpleColumn(name, shown));
		}
		return columns;
	}

	public void moveColumn(int from, int to){
		if (from == to) return;
		T fromColumn = getColumn(from);
		T toColumn = getColumn(to);

		int fromIndex = orderColumns.indexOf(fromColumn);
		orderColumns.remove(fromIndex);

		int toIndex = orderColumns.indexOf(toColumn);
		if (to > from) toIndex++;
		orderColumns.add(toIndex, fromColumn);

		updateColumns();
	}

	public void hideColumn(T column){
		if (!shownColumns.contains(column)) return;
		shownColumns.remove(column);
		updateColumns();
	}

	public void showColumn(T column){
		if (shownColumns.contains(column)) return;
		shownColumns.add(column);
		updateColumns();
	}
	
	public JMenu getMenu(AbstractTableModel tableModel, JAutoColumnTable jTable){
		return new JColumnsMenu(tableModel, jTable);
	}

	private T getColumn(int i){
		return shownColumns.get(i);
	}

	private void updateColumns(){
		Collections.sort(shownColumns, columnComparator);
	}

	@Override public Class getColumnClass(int i) {
		return getColumn(i).getType();
	}

	@Override public Comparator getColumnComparator(int i) {
		return getColumn(i).getComparator();
	}

	@Override public int getColumnCount() {
		return getShownColumns().size();
	}

	@Override public String getColumnName(int i) {
		return getColumn(i).getColumnName();
	}

	@Override public Object getColumnValue(Q e, int i) {
		return getColumn(i).getColumnValue(e);
	}


	//Used by the JSeparatorTable
	@Override public boolean isEditable(Q baseObject, int i) {
		return getColumn(i).isColumnEditable(baseObject);
	}
	@Override public Q setColumnValue(Q baseObject, Object editedValue, int i) {
		return getColumn(i).setColumnValue(baseObject, editedValue);
	}

	class ColumnComparator implements Comparator<T>{

		@Override
		public int compare(T o1, T o2) {
			return orderColumns.indexOf(o1) - orderColumns.indexOf(o2);
		}

	}
	
	public static class SimpleColumn{
		private final String name;
		private final boolean shown;

		public SimpleColumn(String name, boolean shown) {
			this.name = name;
			this.shown = shown;
		}

		public String getName() {
			return name;
		}

		public boolean isShown() {
			return shown;
		}
	}
	
	private class JColumnsMenu extends JMenu implements ActionListener{
	
		private AbstractTableModel tableModel;
		private JAutoColumnTable jTable;
		
		private JColumnsMenu(AbstractTableModel tableModel, JAutoColumnTable jTable) {
			super(GuiShared.get().columns());
			this.tableModel = tableModel;
			this.jTable = jTable;
			setIcon(Images.TABLE_COLUMN_SHOW.getIcon());

			JCheckBoxMenuItem jCheckBoxMenuItem;
			JMenuItem  jMenuItem;

			jMenuItem = new JMenuItem(GuiShared.get().reset());
			jMenuItem.setActionCommand("");
			jMenuItem.addActionListener(this);
			add(jMenuItem);
			
			addSeparator();

			for (T enumColumn : getOrderColumns()){
				jCheckBoxMenuItem = new JCheckBoxMenuItem(enumColumn.toString());
				jCheckBoxMenuItem.setActionCommand(enumColumn.name());
				jCheckBoxMenuItem.addActionListener(this);
				jCheckBoxMenuItem.setIcon(Images.TABLE_COLUMN_SHOW.getIcon());
				jCheckBoxMenuItem.setSelected(getShownColumns().contains(enumColumn));
				add(jCheckBoxMenuItem);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().isEmpty()){
				reset();
				tableModel.fireTableStructureChanged();
				jTable.autoResizeColumns();
			} else {
				for (final T enumColumn : getOrderColumns()){
					if (enumColumn.name().equals(e.getActionCommand())){
						final boolean shown = getShownColumns().contains(enumColumn);
						if (shown){
							hideColumn(enumColumn);
						} else {
							showColumn(enumColumn);
						}
						tableModel.fireTableStructureChanged();
						jTable.autoResizeColumns();
					}
				}
			}
		}
	}
}
