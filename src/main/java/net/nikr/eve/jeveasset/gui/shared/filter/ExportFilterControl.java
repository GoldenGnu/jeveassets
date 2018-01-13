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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;


public abstract class ExportFilterControl<E> {

	protected abstract EnumTableColumn<?> valueOf(String column);
	protected abstract List<EnumTableColumn<E>> getShownColumns();
	protected abstract void saveSettings(final String msg);

	public Map<String, List<Filter>> getAllFilters() {
		return new HashMap<String, List<Filter>>();
	}
	public List<Filter> getCurrentFilters() {
		return new ArrayList<Filter>();
	}
}
