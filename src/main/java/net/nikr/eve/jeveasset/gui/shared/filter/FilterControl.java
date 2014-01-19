/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared.filter;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.AllColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.NumberValue;

public abstract class FilterControl<E> extends ExportFilterControl<E> {

	private final String name;
	private final List<EventList<E>> eventLists;
	private final List<FilterList<E>> filterLists;
	private final Map<String, List<Filter>> filters;
	private final Map<String, List<Filter>> defaultFilters;
	private final FilterGui<E> gui;

	/** Do not use this constructor - it's here only for test purposes. */
	protected FilterControl() {
		name = null;
		eventLists = null;
		filterLists = null;
		filters = null;
		defaultFilters = null;
		gui = null;
	}

	protected FilterControl(final JFrame jFrame, final String name, final EventList<E> eventList, final FilterList<E> filterList, final Map<String, List<Filter>> filters) {
		this(jFrame, name, Collections.singletonList(eventList), Collections.singletonList(filterList), filters);
	}

	protected FilterControl(final JFrame jFrame, final String name, final List<EventList<E>> eventLists, final List<FilterList<E>> filterLists, final Map<String, List<Filter>> filters) {
		this(jFrame, name, eventLists, filterLists, filters, new HashMap<String, List<Filter>>());
	}

	protected FilterControl(final JFrame jFrame, final String name, final EventList<E> eventList, final FilterList<E> filterList, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
		this(jFrame, name, Collections.singletonList(eventList), Collections.singletonList(filterList), filters, defaultFilters);
	}

	protected FilterControl(final JFrame jFrame, final String name, final List<EventList<E>> eventLists, final List<FilterList<E>> filterLists, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
		this.name = name;
		this.eventLists = eventLists;
		this.filterLists = filterLists;
		this.filters = filters;
		this.defaultFilters = defaultFilters;
		ListenerClass listener = new ListenerClass();
		for (FilterList<E> filterList : filterLists) {
			filterList.addListEventListener(listener);
		}
		gui = new FilterGui<E>(jFrame, this);
	}

	@Override
	public List<Filter> getCurrentFilters() {
		return gui.getFilters();
	}

	public void clearCurrentFilters() {
		gui.clear();
	}

	public void addFilter(final Filter filter) {
		gui.addFilter(filter);
	}

	public void addFilters(final List<Filter> filters) {
		gui.addFilters(filters);
	}

	public String getCurrentFilterName() {
		return gui.getCurrentFilterName();
	}

	public JPanel getPanel() {
		return gui.getPanel();
	}

	public void addToolButton(final AbstractButton jButton) {
		gui.addToolButton(jButton);
	}
	public void addToolButton(final AbstractButton jButton, final int width) {
		gui.addToolButton(jButton, width);
	}
	public void addToolSeparator() {
		gui.addToolSeparator();
	}

	public JMenu getMenu(final JTable jTable, final List<E> items) {
		String text = null;
		EnumTableColumn<?> column = null;
		boolean isNumeric = false;
		boolean isDate = false;
		int columnIndex = jTable.getSelectedColumn();
		if (jTable.getSelectedColumnCount() == 1 //Single cell (column)
				&& jTable.getSelectedRowCount() == 1 //Single cell (row)
				&& items.size() == 1 //Single element
				&& !(items.get(0) instanceof SeparatorList.Separator) //Not Separator
				&& columnIndex >= 0 //Shown column
				&& columnIndex < getShownColumns().size()) { //Shown column
			column = getShownColumns().get(columnIndex);
			isNumeric = isNumeric(column);
			isDate = isDate(column);
			text = FilterMatcher.format(getColumnValue(items.get(0), column.name()), false, false);
		}
		return new FilterMenu<E>(gui, column, text, isNumeric, isDate);
	}

	String getName() {
		return name;
	}

	List<EventList<E>> getEventLists() {
		return eventLists;
	}

	List<FilterList<E>> getFilterLists() {
		return filterLists;
	}

	Map<String, List<Filter>> getFilters() {
		return filters;
	}

	@Override
	public Map<String, List<Filter>> getAllFilters() {
		//Need to be updated each time something has changed....
		Map<String, List<Filter>> allFilters = new HashMap<String, List<Filter>>();
		allFilters.putAll(defaultFilters);
		allFilters.putAll(filters);
		return allFilters;
	}

	Map<String, List<Filter>> getDefaultFilters() {
		return defaultFilters;
	}

	int getTotalSize() {
		int totalSize = 0;
		for (EventList<E> eventList : eventLists) {
			totalSize = totalSize + eventList.size();
		}
		return totalSize;
	}

	protected abstract List<EnumTableColumn<E>> getColumns();

	protected abstract Object getColumnValue(E item, String column);

	/**
	 * Overwrite to do stuff before filtering.
	 */
	protected void beforeFilter() { }

	/**
	 * Overwrite to do stuff after filtering.
	 */
	protected void afterFilter() { }

	/**
	 * Overwrite to do stuff after saved filters have been changed.
	 */
	protected void updateFilters() { }

	protected List<EnumTableColumn<E>> columnsAsList(final EnumTableColumn<E>[] fixme) {
		List<EnumTableColumn<E>> columns = new ArrayList<EnumTableColumn<E>>();
		columns.addAll(Arrays.asList(fixme));
		return columns;
	}

	boolean isNumeric(final EnumTableColumn<?> column) {
		if (column instanceof AllColumn) {
			return false;
		} else if (Number.class.isAssignableFrom(column.getType())) {
			return true;
		} else if (NumberValue.class.isAssignableFrom(column.getType())) {
			return true;
		} else {
			return false;
		}
	}

	boolean isDate(final EnumTableColumn<?> column) {
		if (column instanceof AllColumn) {
			return false;
		} else if (column.getType().getName().equals(Date.class.getName())) {
			return true;
		} else {
			return false;
		}
	}

	boolean isAll(final EnumTableColumn<?> column) {
		return (column instanceof AllColumn);
	}

	private class ListenerClass implements ListEventListener<E> {
		@Override
		public void listChanged(final ListEvent<E> listChanges) {
			gui.updateShowing();
		}
	}
}
