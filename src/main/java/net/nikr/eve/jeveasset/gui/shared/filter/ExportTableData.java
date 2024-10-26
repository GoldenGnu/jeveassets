/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.settings.ExportSettings.DecimalSeparator;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.menu.JFormulaDialog.Formula;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuJumps.Jump;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager.FormulaColumn;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager.JumpColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.shared.table.containers.HierarchyColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.NumberValue;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
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

	private static final DecimalFormat INTEGER_DOT_FORMAT = new DecimalFormat("###0", new DecimalFormatSymbols(new Locale("en")));
	private static final DecimalFormat INTEGER_COMMA_FORMAT = new DecimalFormat("###0", new DecimalFormatSymbols(new Locale("da")));
	private static final DecimalFormat DECIMAL_HTML_DOT_FORMAT = new DecimalFormat("#,##0.00##", new DecimalFormatSymbols(new Locale("en")));
	private static final DecimalFormat DECIMAL_HTML_COMMA_FORMAT = new DecimalFormat("#,##0.00##", new DecimalFormatSymbols(new Locale("da")));
	private static final DecimalFormat DECIMAL_CSV_DOT_FORMAT = new DecimalFormat("###0.00##", new DecimalFormatSymbols(new Locale("en")));
	private static final DecimalFormat DECIMAL_CSV_COMMA_FORMAT = new DecimalFormat("###0.00##", new DecimalFormatSymbols(new Locale("da")));

	/**
	 * Get Filters and Views from Settings (No Column Cache).
	 * @param <Q>
	 * @param eventList
	 * @param tableFormat
	 * @param toolName
	 * @param exportSettings
	 * @return
	 */
	public static <Q> boolean exportAutoNoCache(EventList<Q> eventList, final SimpleTableFormat<Q> tableFormat, String toolName, ExportSettings exportSettings) {
		return exportAuto(eventList, null, tableFormat, toolName, exportSettings);
	}

	/**
	 * Get Filters and Views from Settings (With Column Cache)
	 * @param <Q>
	 * @param eventList
	 * @param columnCache
	 * @param tableFormat
	 * @param toolName
	 * @param exportSettings
	 * @return
	 */
	public static <Q> boolean exportAuto(EventList<Q> eventList, ColumnCache<Q> columnCache, final SimpleTableFormat<Q> tableFormat, String toolName, ExportSettings exportSettings) {
		return export(eventList, columnCache, tableFormat, toolName, Settings.get().getTableViews(toolName), Settings.get().getTableFilters(toolName), Settings.get().getDefaultTableFilters(toolName), Settings.get().getCurrentTableFilters(toolName), exportSettings);
	}
	/**
	 * No Filters and Views from Settings
	 * @param <Q>
	 * @param eventList
	 * @param tableFormat
	 * @param toolName
	 * @param exportSettings
	 * @return
	 */
	public static <Q> boolean exportEmpty(EventList<Q> eventList, final SimpleTableFormat<Q> tableFormat, String toolName, ExportSettings exportSettings) {
		return export(eventList, null, tableFormat, toolName, new HashMap<>(), new HashMap<>(), new HashMap<>(), new ArrayList<>(), exportSettings);
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
	private static <Q> boolean export(EventList<Q> eventList, ColumnCache<Q> columnCache, final SimpleTableFormat<Q> tableFormat, String toolName, Map<String, View> views, Map<String, List<Filter>> filters, Map<String, List<Filter>> defaultFilters, List<Filter> currentFilters, ExportSettings exportSettings) {
		//Filter
		final List<Filter> filter;
		switch (exportSettings.getFilterSelection()) {
			case NONE:
				filter = new ArrayList<>();
				break;
			case CURRENT:
				filter = currentFilters;
				break;
			case SAVED:
				String filterName = exportSettings.getFilterName();
				if (filterName == null) {
					LOG.error(toolName + " -> Filter name is null");
					return false;
				}
				List<Filter> f = filters.get(filterName);
				if (f != null) {
					filter = f;
				} else {
					filter = defaultFilters.get(filterName);
					if (filter == null) {
						LOG.error(toolName + " -> No such filter: " + filterName);
						return false;
					}
				}
				break;
			default:
				LOG.error(toolName + " -> Unknown FilterSelection: " + exportSettings.getFilterSelection());
				return false;
		}
		//Columns + Header
		Map<String, EnumTableColumn<Q>> columns = new HashMap<>(); //Column lookup
		for (EnumTableColumn<Q> column : tableFormat.getAllColumns()) {
			columns.put(column.name(), column);
		}
		final List<EnumTableColumn<Q>> header;
		switch (exportSettings.getColumnSelection()) {
			case SELECTED: //Selected/All columns
				List<String> selectedColumns = exportSettings.getTableExportColumns();
				if (!selectedColumns.isEmpty()) { //Selected columns
					header = new ArrayList<>();
					for (String selectedColumn : selectedColumns) {
						EnumTableColumn<Q> column = columns.get(selectedColumn);
						if (column != null) {
							header.add(column);
						}
					}
				} else { //All columns
					header = new ArrayList<>(tableFormat.getAllColumns()); //Copy (Otherwise added columns to the tableformat will also add it to the header)
				}
				break;
			case SHOWN: //Current shown columns in order
				header = new ArrayList<>();
				for (SimpleColumn simpleColumn : Settings.get().getTableColumns().get(toolName)) {
					if (simpleColumn.isShown()) {
						EnumTableColumn<Q> column = columns.get(simpleColumn.getEnumName());
						if (column != null) {
							header.add(column);
						}
					}
				}
				break;
			case SAVED: //Saved View
				String viewName = exportSettings.getViewName();
				if (viewName == null) {
					LOG.error(toolName + " -> View name is null");
					return false;
				}
				View view = views.get(viewName);
				if (view == null) {
					LOG.error(toolName + " -> No such view: " + viewName);
					return false;
				}
				header = new ArrayList<>();
				for (SimpleColumn simpleColumn : view.getColumns()) {
					if (simpleColumn.isShown()) {
						EnumTableColumn<Q> column = columns.get(simpleColumn.getEnumName());
						if (column != null) {
							header.add(column);
						}
					}
				}
				break;
			default:
				LOG.error(toolName + " -> Unknown ColumnSelection: " + exportSettings.getColumnSelection());
				return false;
		}
		if (header.isEmpty()) {
			LOG.error(toolName + " -> No columns selected for ColumnSelection: " + exportSettings.getColumnSelection());
			return false;
		}
		//Formula
		for (Formula formula : Settings.get().getTableFormulas(toolName)) {
			FormulaColumn<Q> formulaColumn = new FormulaColumn<>(formula);
			tableFormat.addColumn(formulaColumn); //Add the column for filters
			if (exportSettings.isFormulas()) {
				header.add(formulaColumn); //Export
			}
		}
		//Jump
		for (Jump jump : Settings.get().getTableJumps(toolName)) {
			JumpColumn<Q> jumpColumn = new JumpColumn<>(jump);
			tableFormat.addColumn(jumpColumn); //Add the column for filters
			if (exportSettings.isJumps()) {
				header.add(jumpColumn); //Export
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
					row.put(column.name(), format(tableFormat.getColumnValue(e, column.name()), exportSettings.getDecimalSeparator(), false));
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
					row.put(column, format(tableFormat.getColumnValue(e, column.name()), exportSettings.getDecimalSeparator(), true));
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

	private static String format(Object object, final DecimalSeparator decimalSeparator, final boolean html) {
		if (object != null && object instanceof NumberValue && !(object instanceof Percent)) { //Unpack NumberValue
			object = ((NumberValue)object).getNumber();
		}
		if (object == null) {
			return "";
		} else if (object instanceof HierarchyColumn) {
			HierarchyColumn column = (HierarchyColumn) object;
			return column.getExport();
		} else if (object instanceof Percent) {
			Percent percent = (Percent) object;
			return percent.toString();
		} else if (object instanceof Number) {
			Number number = (Number) object;
			if (object instanceof Integer || object instanceof Long) {
				if (decimalSeparator == DecimalSeparator.DOT) {
					return INTEGER_DOT_FORMAT.format(number);
				} else {
					return INTEGER_COMMA_FORMAT.format(number);
				}
			} else {
				if (html) {
					if (decimalSeparator == DecimalSeparator.DOT) {
						return DECIMAL_HTML_DOT_FORMAT.format(number);
					} else {
						return DECIMAL_HTML_COMMA_FORMAT.format(number);
					}
				} else {
					if (decimalSeparator == DecimalSeparator.DOT) {
						return DECIMAL_CSV_DOT_FORMAT.format(number);
					} else {
						return DECIMAL_CSV_COMMA_FORMAT.format(number);
					}
				}
			}
		} else if (object instanceof Tags && html) {
			Tags tags = (Tags) object;
			return tags.getHtml();
		} else if (object instanceof Date) {
			return Formatter.columnDate(object);
		} else {
			return object.toString();
		}
	}
}
