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

package net.nikr.eve.jeveasset.data;

import java.util.ArrayList;
import java.util.List;


public class TableSettings{

	public enum ResizeMode {
		TEXT,
		WINDOW,
		NONE
	}

	private final List<String> tableColumnOriginal;
	private final List<String> tableColumnNames = new ArrayList<String>();
	private final List<String> tableColumnVisible = new ArrayList<String>();
	private ResizeMode mode;

	public TableSettings(List<String> tableColumnOriginal) {
		this(tableColumnOriginal, ResizeMode.TEXT);
	}

	public TableSettings() {
		this(new ArrayList<String>(), ResizeMode.TEXT);
	}

	public TableSettings(List<String> tableColumnOriginal, ResizeMode mode) {
		if (tableColumnOriginal == null || mode == null){
			throw new IllegalArgumentException("Arguments can not be null");
		}
		this.tableColumnOriginal = tableColumnOriginal;
		this.mode = mode;
		resetColumns();
	}

	public final void resetColumns(){
		tableColumnNames.clear();
		tableColumnNames.addAll(tableColumnOriginal);
		tableColumnVisible.clear();
		tableColumnVisible.addAll(tableColumnOriginal);
	}

	public ResizeMode getMode() {
		return mode;
	}

	public void setMode(ResizeMode mode) {
		this.mode = mode;
	}

	public List<String> getTableColumnVisible() {
		return tableColumnVisible;
	}

	public void setTableColumnVisible(List<String> tableColumnVisible) {
		this.tableColumnVisible.clear();
		this.tableColumnVisible.addAll(tableColumnVisible);
	}

	public List<String> getTableColumnNames() {
		return tableColumnNames;
	}

	public void setTableColumnNames(List<String> tableColumnNames) {
		this.tableColumnNames.clear();
		this.tableColumnNames.addAll(tableColumnNames);
	}

	public List<String> getTableColumnOriginal() {
		return tableColumnOriginal;
	}
}
