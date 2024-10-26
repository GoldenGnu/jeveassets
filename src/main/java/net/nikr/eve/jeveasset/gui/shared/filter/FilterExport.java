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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.menu.JFormulaDialog.Formula;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuJumps.Jump;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager.FormulaColumn;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager.JumpColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.SimpleColumnManager;
import net.nikr.eve.jeveasset.io.local.SettingsReader;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class FilterExport {
	//Constants
	private final String ENABLED = "enabled";
	private final String DISABLED = "disabled";
	private final String FORMULA = "FORMULA";
	private final String JUMP = "JUMP";

	private final String toolName;

	private SimpleColumnManager<?> columnManager;

	public FilterExport(String toolName) {
		this(toolName, null);
	}

	/**
	 * For testing
	 * @param toolName
	 * @param columnManager
	 */
	protected FilterExport(String toolName, SimpleColumnManager<?> columnManager) {
		this.toolName = toolName;
		this.columnManager = columnManager;
	}

	protected String exportFilter(String filterName, List<Filter> filterList) {
		StringBuilder builder = new StringBuilder();
		exportFilter(builder, filterName, filterList);
		return builder.toString();
	}

	protected void exportFilter(StringBuilder builder, String filterName, List<Filter> filterList) {
		//Header
		addHeader(builder, filterName);
		//Each filter
		for (Filter filter : filterList) {
			addFilter(builder, filter);
		}
		builder.append("\r\n");
	}

	private void addHeader(StringBuilder builder, String filterName) {
		builder.append("[");
		builder.append(toolName.toUpperCase()); //Never used, but, usefull to identify where the filters fit
		builder.append("] [");
		builder.append(wrap(filterName));
		builder.append("]\r\n");
	}

	private void addFilter(StringBuilder builder, Filter filter) {
		builder.append("[");
		builder.append(filter.getGroup());
		builder.append("] [");
		builder.append(filter.getLogic().name());
		builder.append("] [");
		EnumTableColumn<?> column = filter.getColumn();
		builder.append(column.name());
		builder.append("] [");
		builder.append(filter.getCompareType().name());
		builder.append("] [");
		builder.append(wrap(filter.getText()));
		builder.append("] [");
		builder.append(wrapEnabled(filter.isEnabled()));
		builder.append("]");
		if (column instanceof FormulaColumn) {
			FormulaColumn<?> formulaColumn = (FormulaColumn) column;
			builder.append(" [");
			builder.append(FORMULA);
			builder.append(wrap(formulaColumn.getFormula().getOriginalExpression()).replace(" ", ""));
			builder.append("]");
		}
		if (column instanceof JumpColumn) {
			JumpColumn<?> jumpColumn = (JumpColumn) column;
			builder.append(" [");
			builder.append(JUMP);
			builder.append(jumpColumn.getJump().getSystemID());
			builder.append("]");
		}
		builder.append("\r\n");
	}

	protected Map<String, List<Filter>> importFilter(String importText) {
		List<Filter> filterList = new ArrayList<>();
		Map<String, List<Filter>> filters = new HashMap<>();
		boolean headerLoaded = false;
		if (importText == null) {
			return filters;
		}
		List<String> groups = new ArrayList<>();
		for (String line : importText.split("[\r\n]+")) {
			groups.clear(); //Clear old data

			//For each [*]
			Pattern pattern = Pattern.compile("\\[([^\\]]|\\]\\])*\\]"); //	\[([^\]]|\]\])*\]	A([^B]|BB)*B
			Matcher m = pattern.matcher(line);
			while (m.find()) {
				groups.add(m.group());
			}
			//Header
			if (groups.size() == 2) {
				filterList = new ArrayList<>(); //New list (as the list is passed to "filters")
				String filterName = unwrap(groups.get(1));
				filters.put(filterName, filterList);
				headerLoaded = true;
			}
			//Filter
			if (headerLoaded) {
				if (groups.size() == 4) { //backward compatibility (5.7.X and bellow)
					//Logic
					Filter.LogicType logic = null;
					try {
						logic = Filter.LogicType.valueOf(unwrap(groups.get(0)));
					} catch (IllegalArgumentException ex) {
						//Already null;
					}
					//Column
					EnumTableColumn<?> column = SettingsReader.getColumn(unwrap(groups.get(1)), toolName, Settings.get());

					//Compare
					Filter.CompareType compare = null;
					try {
						compare = Filter.CompareType.valueOf(unwrap(groups.get(2)));
					} catch (IllegalArgumentException ex) {
						//Already null;
					}
					String text = null;
					EnumTableColumn<?> compareColumn = null;
					if (Filter.CompareType.isColumnCompare(compare)) {
						compareColumn = SettingsReader.getColumn(unwrap(groups.get(3)), toolName, Settings.get());
						if (compareColumn != null) { //Valid
							text = unwrap(groups.get(3));
						}
					} else {
						text = unwrap(groups.get(3));
					}
					if (logic != null && column != null && compare != null && (text != null || compareColumn != null)) {
						Filter filter = new Filter(logic, column, compare, text);
						filterList.add(filter);
					}
				}
				if (groups.size() >= 5 && headerLoaded) {
					//Group
					Integer group = null;
					try {
						group = Integer.valueOf(unwrap(groups.get(0)));
					} catch (IllegalArgumentException ex) {
						//Already null;
					}
					//Logic
					Filter.LogicType logic = null;
					try {
						logic = Filter.LogicType.valueOf(unwrap(groups.get(1)));
					} catch (IllegalArgumentException ex) {
						//Already null;
					}
					//Column
					String columnName = unwrap(groups.get(2));
					EnumTableColumn<?> column = SettingsReader.getColumn(columnName, toolName, Settings.get());

					//Compare
					Filter.CompareType compare = null;
					try {
						compare = Filter.CompareType.valueOf(unwrap(groups.get(3)));
					} catch (IllegalArgumentException ex) {
						//Already null;
					}
					String text = null;
					EnumTableColumn<?> compareColumn = null;
					if (Filter.CompareType.isColumnCompare(compare)) {
						compareColumn = SettingsReader.getColumn(unwrap(groups.get(4)), toolName, Settings.get());
						if (compareColumn != null) { //Valid
							text = unwrap(groups.get(4));
						}
					} else {
						text = unwrap(groups.get(4));
					}
					//Enabled
					boolean enabled = true;
					if (groups.size() >= 6) {
						enabled = unwrapEnabled(groups.get(5));
					}
					//Special Columns
					if (groups.size() == 7 && column == null) { //Only if the column doesn't already exist
						String columnData = unwrap(groups.get(6));
						if (columnData.startsWith(FORMULA)) {
							String expresion = columnData.replaceFirst(FORMULA, "");
							Formula formula = new Formula(columnName, expresion, null);
							if (columnManager == null) { //Lazy load (as it's initialized after this class)
								columnManager = ColumnManager.getColumnManager(toolName);
							}
							if (columnManager != null) {
								column = columnManager.addColumn(formula);
							}
						} else if (columnData.startsWith(JUMP)) {
							String system = columnData.replaceFirst(JUMP, "");
							try {
								long systemID = Long.valueOf(system);
								MyLocation from = ApiIdConverter.getLocation(systemID);
								Jump jump = new Jump(from);
								if (columnManager == null) { //Lazy load (as it's initialized after this class)
									columnManager = ColumnManager.getColumnManager(toolName);
								}
								if (columnManager != null) {
									column = columnManager.addColumn(jump);
								}
							} catch (NumberFormatException ex) {
								//No nothing
							}
						}
					}
					if (group != null && logic != null && column != null && compare != null && (text != null || compareColumn != null)) {
						Filter filter = new Filter(group, logic, column, compare, text, enabled);
						filterList.add(filter);
					}
				}
			}
			//Ignore everything that does not match the syntax
		}
		return filters;
	}

	protected <E> String createExample(List<EnumTableColumn<E>> columns) {
		StringBuilder builder = new StringBuilder();
		addHeader(builder, "Example Filter");
		boolean s = true;
		boolean d = true;
		for (EnumTableColumn<E> column : columns) {
			if (s && column.getType().equals(String.class)) {
				addFilter(builder, new Filter(Filter.LogicType.AND, column, Filter.CompareType.CONTAINS, "text"));
				builder.append("\r\n");
				s = false;
			}
			if (d && column.getType().equals(Double.class)) {
				addFilter(builder, new Filter(Filter.LogicType.AND, column, Filter.CompareType.GREATER_THAN, "0"));
				builder.append("\r\n");
				d = false;
			}
			if (!d && !s) {
				break; //both found
			}
		}
		return builder.toString();
	}

	private String wrap(String text) {
		return text.replace("]", "]]");
	}

	/**
	 * *
	 * Convert the enabled disable flag into a readable format for export.
	 *
	 * @param enabled The state of the flag.
	 * @return A string that contains an export element in human readable form,
	 * either "enabled" or "disabled".
	 */
	private String wrapEnabled(boolean enabled) {
		if (enabled) {
			return wrap(ENABLED);
		}
		return wrap(DISABLED);
	}

	private String unwrap(String text) {
		text = text.substring(1, text.length() - 1);
		text = text.replace("]]", "]");
		return text;
	}

	/**
	 * *
	 * Unwrap and convert the human readable enable disable flag to a boolean,
	 * case insensitive.
	 *
	 * @param text The text of the element to be unwrapped and converted.
	 * @return "disabled" returns false, all others (including invalid data)
	 * return true.
	 */
	private boolean unwrapEnabled(String text) {
		String unwrapped = unwrap(text);
		if (DISABLED.equalsIgnoreCase(unwrapped)) {
			return false;
		} else if (ENABLED.equalsIgnoreCase(unwrapped)) {
			return true;
		}
		//Assume true if no match.
		return true;
	}
}
