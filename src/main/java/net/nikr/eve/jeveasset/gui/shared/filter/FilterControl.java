/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.AllColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.NumberValue;


public abstract class FilterControl<E> extends ExportFilterControl<E> {

	private final String name;
	private final EventList<E> eventList;
	private final EventList<E> exportEventList;
	private final FilterList<E> filterList;
	private final Map<String, List<Filter>> filters;
	private final Map<String, List<Filter>> defaultFilters;
	private final FilterGui<E> gui;
	private final Map<E, String> cache;

	/** Do not use this constructor - it's here only for test purposes. */
	protected FilterControl() {
		name = null;
		eventList = null;
		exportEventList = null;
		filterList = null;
		filters = null;
		defaultFilters = null;
		gui = null;
		cache = new HashMap<E, String>();
	}

	protected FilterControl(final JFrame jFrame, final String name, final EventList<E> eventList, final EventList<E> exportEventList, final FilterList<E> filterList, final Map<String, List<Filter>> filters) {
		this(jFrame, name, eventList, exportEventList, filterList, filters, new HashMap<String, List<Filter>>());
	}
	protected FilterControl(final JFrame jFrame, final String name, final EventList<E> eventList, final EventList<E> exportEventList, final FilterList<E> filterList, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
		this.name = name;
		this.eventList = eventList;
		this.exportEventList = exportEventList;
		this.filterList = filterList;
		eventList.addListEventListener(new ListEventListener<E>() {
			@Override @SuppressWarnings("deprecation")
			public void listChanged(ListEvent<E> listChanges) {
				try {
					eventList.getReadWriteLock().readLock().lock();
					List<E> delete = new ArrayList<E>();
					List<E> update = new ArrayList<E>();
					while(listChanges.next()) {
						switch (listChanges.getType()) {
							case ListEvent.DELETE:
								addSafe(delete, listChanges.getOldValue());
								break;
							case ListEvent.UPDATE:
								addSafe(eventList, update, listChanges.getIndex());
								break;
						}
					}
					cacheDelete(delete);
					cacheUpdate(update);
				} finally {
					eventList.getReadWriteLock().readLock().unlock();
				}
			}
		});
		this.filters = filters;
		this.defaultFilters = defaultFilters;
		ListenerClass listener = new ListenerClass();
		filterList.addListEventListener(listener);
		gui = new FilterGui<E>(jFrame, this);
		cache = new HashMap<E, String>();
	}

	public void clearCache() {
		cache.clear();
	}

	public void createCache() {
		cacheRebuild();
	}

	public void refilter() {
		gui.refilter();
	}

	Map<E, String> getCache() {
		return cache;
	}

	void addCache(E e, String haystack) {
		cache.put(e, haystack);
	}

	private void cacheDelete(List<E> update) {
		if (update.isEmpty()) {
			return;
		}
		for (E e : update) {
			cache.remove(e); //Remove deleted cache
		}
	}

	private void cacheUpdate(List<E> update) {
		if (update.isEmpty()) {
			return;
		}
		for (E e : update) {
			cache.put(e, FilterMatcher.buildItemCache(this, e)); //Update outdated cache
		}
	}

	private void cacheRebuild() {
		cache.clear();
		try {
			getEventList().getReadWriteLock().readLock().lock();
			for (E e : getEventList()) {
				String s = FilterMatcher.buildItemCache(this, e);
				cache.put(e, s);
			}
		} finally {
			getEventList().getReadWriteLock().readLock().unlock();
		}
	}

	private void addSafe(final EventList<E> eventList, List<E> list, int index) {
		if (index >= 0 && index < eventList.size()) {
			list.add(eventList.get(index));
		}
	}

	private void addSafe(List<E> list, E e) {
		if (e != null) {
			list.add(e);
		}
	}

	public void setColumns(final List<EnumTableColumn<E>> enumColumns) {
		gui.setColumns(enumColumns);
		cacheRebuild(); //Add or Remove column means everything have to be rebuild...
	}

	@Override
	public List<Filter> getCurrentFilters() {
		return gui.getFilters(false);
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

	public void addExportOption(final JMenuItem jMenuItem) {
		gui.addExportOption(jMenuItem);
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
			text = FilterMatcher.format(getColumnValue(items.get(0), column.name()), false);
		}
		return new FilterMenu<E>(gui, column, text, isNumeric, isDate);
	}

	String getName() {
		return name;
	}

	public EventList<E> getEventList() {
		return eventList;
	}

	public EventList<E> getExportEventList() {
		return exportEventList;
	}

	public FilterList<E> getFilterList() {
		return filterList;
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
