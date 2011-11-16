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

package net.nikr.eve.jeveasset.gui.shared.table;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Candle
 */
public class EnumTableFormatAdaptor<T extends Enum<T> & EnumTableColumn<Q>, Q> implements AdvancedTableFormat<Q>, WritableTableFormat<Q> {

	List<T> shownColumns;
	List<T> orderColumns;
	ColumnComparator columnComparator;

	public EnumTableFormatAdaptor(Class<T> enumClass) {
		shownColumns = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));
		orderColumns = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));
		columnComparator = new ColumnComparator();
	}

	public List<T> getShownColumns(){
		return shownColumns;
	}

	public List<T> getOrderColumns(){
		return orderColumns;
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

	private T getColumn(int i){
		return shownColumns.get(i);
	}

	private void updateColumns(){
		Collections.sort(shownColumns, columnComparator);
	}

	private List<T> getColumns() {
		return shownColumns;
	}

	@Override public Class getColumnClass(int i) {
		return getColumn(i).getType();
	}

	@Override public Comparator getColumnComparator(int i) {
		return getColumn(i).getComparator();
	}

	@Override public int getColumnCount() {
		return getColumns().size();
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
}
