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
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.table.AbstractTableModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Candle
 */
public class EnumTableFormatAdaptor<T extends Enum<T> & EnumTableColumn<Q>, Q> implements AdvancedTableFormat<Q>, WritableTableFormat<Q> {
	
	private final static Logger LOG = LoggerFactory.getLogger(EnumTableFormatAdaptor.class);
	
	public static enum ResizeMode{
		TEXT{
			@Override String getI18N() {
				return GuiShared.get().tableResizeText();
			}
		},
		WINDOW{
			@Override String getI18N() {
				return GuiShared.get().tableResizeWindow();
			}
		},
		NONE{
			@Override String getI18N() {
				return GuiShared.get().tableResizeNone();
			}
		},
		;

		abstract String getI18N();
		
		@Override
		public String toString(){
			return getI18N();
		}
	}
	
	private Class<T> enumClass;
	private List<T> shownColumns;
	private List<T> orderColumns;
	private ColumnComparator columnComparator;
	
	private JMenu jMenu;
	private EditColumnsDialog<T, Q> dialog;
	private ResizeMode resizeMode;

	public EnumTableFormatAdaptor(Class<T> enumClass) {
		this.enumClass = enumClass;
		columnComparator = new ColumnComparator();
		resizeMode = ResizeMode.TEXT;
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

	public ResizeMode getResizeMode() {
		return resizeMode;
	}

	public void setResizeMode(ResizeMode resizeMode) {
		if (resizeMode == null) return;
		this.resizeMode = resizeMode;
	}
	
	public void setColumns(List<SimpleColumn> columns){
		if (columns == null) return;
		orderColumns = new ArrayList<T>();
		shownColumns = new ArrayList<T>();
		List<T> originalColumns = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));		
		for (SimpleColumn column : columns){
			try {
				T t = Enum.valueOf(enumClass, column.getEnumName());
				orderColumns.add(t);
				if (column.isShown()) shownColumns.add(t);
			} catch (IllegalArgumentException ex) {
				LOG.info("Removing column: "+column.getEnumName());
			}
			
		}
		//Add new columns
		for (T t : originalColumns){
			if (!orderColumns.contains(t)){
				LOG.info("Adding column: "+t.getColumnName());
				int index = originalColumns.indexOf(t);
				if (index >= orderColumns.size()) index = orderColumns.size() - 1;
				orderColumns.add(index, t);
				shownColumns.add(t);
			}
		}
		updateColumns();
	}
	
	public List<SimpleColumn> getColumns(){
		List<SimpleColumn> columns = new ArrayList<SimpleColumn>(orderColumns.size());
		for (T t : orderColumns){
			String columnName = t.getColumnName();
			String enumName = t.name();
			boolean shown = shownColumns.contains(t);
			columns.add(new SimpleColumn(enumName, columnName, shown));
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
	
	public JMenuItem getMenu(Program program, final AbstractTableModel tableModel, final JAutoColumnTable jTable){
		if (dialog == null){ //Create dialog (only once)
			dialog = new EditColumnsDialog<T, Q>(program, this);
		}		
		
		if (jMenu == null){ //Create menu (only once)
			jMenu = new JMenu(GuiShared.get().tableSettings());
			jMenu.setIcon(Images.TABLE_COLUMN_SHOW.getIcon());
 			JMenuItem jMenuItem = new JMenuItem(GuiShared.get().tableColumns(), Images.DIALOG_SETTINGS.getIcon());
			jMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.setVisible(true);
					tableModel.fireTableStructureChanged();
					jTable.autoResizeColumns();
				}
			});
			jMenu.add(jMenuItem);

			jMenu.addSeparator();

			jMenuItem = new JMenuItem(GuiShared.get().tableColumnsReset(), Images.TABLE_COLUMN_SHOW.getIcon());
			jMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					reset();
					tableModel.fireTableStructureChanged();
					jTable.autoResizeColumns();
				}
			});
			jMenu.add(jMenuItem);
			
			jMenu.addSeparator();
			
			ButtonGroup buttonGroup = new ButtonGroup();
			JRadioButtonMenuItem jRadioButton;
			for (final ResizeMode mode : ResizeMode.values()){
				jRadioButton = new JRadioButtonMenuItem(mode.toString(), Images.TABLE_COLUMN_RESIZE.getIcon());
				jRadioButton.setSelected(resizeMode == mode);
				jRadioButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setResizeMode(mode);
						jTable.autoResizeColumns();
					}
				});
				buttonGroup.add(jRadioButton);
				jMenu.add(jRadioButton);
			}
			
			
		}
		
		return jMenu;
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
		private final String enumName;
		private final String columnName;
		private boolean shown;

		public SimpleColumn(String enumName, boolean shown) {
			this.enumName = enumName;
			this.columnName = "";
			this.shown = shown;
		}

		public SimpleColumn(String enumName, String columnName, boolean shown) {
			this.enumName = enumName;
			this.columnName = columnName;
			this.shown = shown;
		}

		public String getColumnName() {
			return columnName;
		}

		public String getEnumName() {
			return enumName;
		}

		public boolean isShown() {
			return shown;
		}

		public void setShown(boolean shown) {
			this.shown = shown;
		}
	}
}
