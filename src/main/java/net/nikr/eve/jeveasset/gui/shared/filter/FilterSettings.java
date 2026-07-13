/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
import java.util.List;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;


public class FilterSettings  {

	public static final FilterSettings EMPTY = new FilterSettings(new ArrayList<>(), null, null);
	
	private final List<Filter> filters;
	private final String sort;
	private final List<SimpleColumn> columns;

	public FilterSettings(List<Filter> filters, String sort, List<SimpleColumn> columns) {
		this.filters = filters;
		this.sort = sort;
		this.columns = columns;
	}

	public static FilterSettings get(List<Filter> filters) {
		return new FilterSettings(filters, null, null);
	}

	
	public List<Filter> getFilters() {
		return filters;
	}

	public String getSort() {
		return sort;
	}

	public List<SimpleColumn> getColumns() {
		return columns;
	}
}
