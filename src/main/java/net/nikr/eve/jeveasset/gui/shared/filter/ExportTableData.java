/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
import ca.odell.glazedlists.matchers.Matcher;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.nikr.eve.jeveasset.data.settings.ExportSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.shared.table.containers.HierarchyColumn;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.io.local.CsvWriter;
import net.nikr.eve.jeveasset.io.local.HtmlWriter;
import net.nikr.eve.jeveasset.io.local.SqlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.prefs.CsvPreference;


public class ExportTableData {

	private static final Logger LOG = LoggerFactory.getLogger(ExportTableData.class);

	private static final DecimalFormat HTML_EN_NUMBER_FORMAT  = new DecimalFormat("#,##0.####", new DecimalFormatSymbols(new Locale("en")));
	private static final DecimalFormat HTML_EU_NUMBER_FORMAT  = new DecimalFormat("#,##0.####", new DecimalFormatSymbols(new Locale("da")));

	/**
	 * No Column Cache
	 * @param <Q>
	 * @param eventList
	 * @param tableFormat
	 * @param toolName
	 * @param exportSettings
	 * @return 
	 */
	public static <Q> boolean exportNoCache(EventList<Q> eventList, final SimpleTableFormat<Q> tableFormat, String toolName, ExportSettings exportSettings) {
		return exportAutoFill(eventList, null, tableFormat, toolName, exportSettings);
	}

	/**
	 * Get Filters and Views from Settings
	 * @param <Q>
	 * @param eventList
	 * @param columnCache
	 * @param tableFormat
	 * @param toolName
	 * @param exportSettings
	 * @return 
	 */
	public static <Q> boolean exportAutoFill(EventList<Q> eventList, ColumnCache<Q> columnCache, final SimpleTableFormat<Q> tableFormat, String toolName, ExportSettings exportSettings) {
		return export(eventList, columnCache, tableFormat, toolName, Settings.get().getTableViews(toolName), Settings.get().getTableFilters(toolName), Settings.get().getCurrentTableFilters(toolName), exportSettings);
	}
	/**
	 * No Filters and Views from Settings
	 * @param <Q>
	 * @param eventList
	 * @param columnCache
	 * @param tableFormat
	 * @param toolName
	 * @param exportSettings
	 * @return 
	 */
	public static <Q> boolean exportEmpty(EventList<Q> eventList, ColumnCache<Q> columnCache, final SimpleTableFormat<Q> tableFormat, String toolName, ExportSettings exportSettings) {
		return export(eventList, columnCache, tableFormat, toolName, new HashMap<>(), new HashMap<>(), new ArrayList<>(), exportSettings);
	}

	/**
	 * 
	 * @param <Q>
	 * @param eventList
	 * @param columnCache
	 * @param tableFormat
	 * @param toolName
	 * @param views
	 * @param filters
	 * @param currentFilters
	 * @param exportSettings
	 * @return 
	 */
	public static <Q> boolean export(EventList<Q> eventList, ColumnCache<Q> columnCache, final SimpleTableFormat<Q> tableFormat, String toolName, Map<String, View> views,  Map<String, List<Filter>> filters, List<Filter> currentFilters, ExportSettings exportSettings) {
		//Filter
		String filterName = exportSettings.getFilterName();
		List<Filter> filter;
		if (filterName == null) {
			filter = new ArrayList<>();
		} else if (filterName.isEmpty()) {
			filter = currentFilters;
		} else {
			filter = filters.get(filterName);
			if (filter == null) {
				LOG.error(toolName + ": No such filter (" + filterName + " )");
				return false;
			}
		}
		//Columns + Header
		String viewName = exportSettings.getViewName();
		List<EnumTableColumn<Q>> header = new ArrayList<>();
		if (viewName == null) { //All columns
			header = tableFormat.getAllColumns();
		} else if (viewName.isEmpty()) { //Current shown columns in order
			header = tableFormat.getShownColumns();
		} else { //Saved View
			View view = views.get(viewName);
			if (view == null) {
				LOG.error(toolName + ": No such view (" + viewName + " )");
				return false;
			}
			Map<String, EnumTableColumn<Q>> columns = new HashMap<>();
			for (EnumTableColumn<Q> column : tableFormat.getAllColumns()) {
				columns.put(column.name(), column);
			}
			for (EnumTableFormatAdaptor.SimpleColumn simpleColumn : view.getColumns()) {
				if (simpleColumn.isShown()) {
					EnumTableColumn<Q> column = columns.get(simpleColumn.getEnumName());
					header.add(column);
				}
			}
		}
		//Apply Filters
		List<Q> items = new ArrayList<>();
		try {
			eventList.getReadWriteLock().readLock().lock();
			FilterList<Q> filterList = new FilterList<>(eventList, new FilterLogicalMatcher<>(tableFormat, columnCache, filter));
			if (!eventList.isEmpty() && eventList.get(0) instanceof TreeAsset) {
				FilterList<Q> treeFilterList = new FilterList<>(eventList, new TreeMatcher<>(filterList));
				items.addAll(treeFilterList);
			} else {
				items.addAll(filterList);
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		//File
		String filename = exportSettings.getFilename();
		File dir = new File(filename).getParentFile();
		if (dir.isFile()) { //If parent dir is a file, cancel
			return false;
		} else if (!dir.exists()) { //If parent dir dosn't exsit, create it
			dir.mkdirs();
		}
		if (exportSettings.isCsv()) {
			//CSV
			//Create data
			List<String> headerStrings = new ArrayList<>(header.size());
			List<String> headerKeys = new ArrayList<>(header.size());
			for (EnumTableColumn<Q> column : header) {
				headerStrings.add(column.getColumnName());
				headerKeys.add(column.name());
			}
			List<Map<String, String>> rows = new ArrayList<>();
			for (Q e : items) {
				Map<String, String> row = new HashMap<>();
				for (EnumTableColumn<Q> column : header) {
					row.put(column.name(), format(tableFormat.getColumnValue(e, column.name()), exportSettings.getCsvDecimalSeparator(), false));
				}
				rows.add(row);
			}
			//Save data
			return CsvWriter.save(exportSettings.getFilename(),
					rows,
					headerStrings.toArray(new String[headerStrings.size()]),
					headerKeys.toArray(new String[headerKeys.size()]),
					new CsvPreference.Builder('\"', exportSettings.getCsvFieldDelimiter().getValue(), exportSettings.getCsvLineDelimiter().getValue()).build());
		} else if (exportSettings.isHtml()) {
			//HTML
			//Create data
			List<Map<EnumTableColumn<?>, String>> rows = new ArrayList<>();
			for (Q e : items) {
				Map<EnumTableColumn<?>, String> row = new HashMap<>();
				for (EnumTableColumn<Q> column : header) {
					row.put(column, format(tableFormat.getColumnValue(e, column.name()), exportSettings.getCsvDecimalSeparator(), exportSettings.isHtmlStyled()));
				}
				rows.add(row);
			}
			//Save data
			return HtmlWriter.save(exportSettings.getFilename(),
					rows,
					new ArrayList<>(header),
					exportSettings.isHtmlIGB() ? new ArrayList<>(items) : null,
					exportSettings.isHtmlStyled(),
					exportSettings.getHtmlRepeatHeader(),
					toolName.equals(TreeTab.NAME));
		} else if (exportSettings.isSql()) {
			//SQL
			//Create data
			List<Map<EnumTableColumn<?>, Object>> rows = new ArrayList<>();
			for (Q e : items) {
				Map<EnumTableColumn<?>, Object> row = new HashMap<>();
				for (EnumTableColumn<Q> column : header) {
					row.put(column, tableFormat.getColumnValue(e, column.name()));
				}
				rows.add(row);
			}
			//Save data
			return SqlWriter.save(exportSettings.getFilename(),
					rows,
					new ArrayList<>(header),
					exportSettings.getSqlTableName(),
					exportSettings.isSqlDropTable(),
					exportSettings.isSqlCreateTable(),
					exportSettings.isSqlExtendedInserts());
		} else {
			return false;
		}
	}

	private static class TreeMatcher<E> implements Matcher<E> {

		private final EventList<E> eventList;
		private final Set<TreeAsset> parentTree = new HashSet<>();

		public TreeMatcher(EventList<E> eventList) {
			this.eventList = eventList;
			Set<TreeAsset> items = new TreeSet<>(new TreeTab.AssetTreeComparator());
			for (E e : eventList) {
				if (e instanceof TreeAsset) {
					TreeAsset tree = (TreeAsset) e;
					items.add(tree);
					parentTree.addAll(tree.getTree());
				}
			}
			for (TreeAsset treeAsset : parentTree) {
				treeAsset.resetValues();
				if (treeAsset.isItem()) {
					items.add(treeAsset);
				}
			}
			for (TreeAsset treeAsset : items) {
				treeAsset.updateParents();
			}
		}

		@Override
		public boolean matches(E item) { //XXX - Expensive
			if (item instanceof TreeAsset) {
				TreeAsset treeAsset = (TreeAsset) item;
				if (treeAsset.isParent()) {
					return parentTree.contains(treeAsset);
				}
			}
			return eventList.contains(item);
		}

	}

	private static String format(final Object object, final ExportSettings.DecimalSeparator decimalSeparator, final boolean html) {
		if (object == null) {
			return "";
		} else if (object instanceof HierarchyColumn) {
			HierarchyColumn column = (HierarchyColumn) object;
			return column.getExport();
		} else if (object instanceof Number) {
			Number number = (Number) object;
			if (decimalSeparator == ExportSettings.DecimalSeparator.DOT) {
				return HTML_EN_NUMBER_FORMAT.format(number);
			} else {
				return HTML_EU_NUMBER_FORMAT.format(number);
			}
		} else if (object instanceof Tags && html) {
			Tags tags = (Tags) object;
			return tags.getHtml();
		} else if (object instanceof Date) {
			return Formater.columnDate(object);
		} else {
			return object.toString();
		}
	}
}
