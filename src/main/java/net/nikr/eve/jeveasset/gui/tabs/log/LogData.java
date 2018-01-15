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
package net.nikr.eve.jeveasset.gui.tabs.log;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class LogData {
	private final Map<Date, Boolean> canBeLot = new TreeMap<>();
	private Map<Date, Map<Integer, List<AssetLog>>> addedClaims = new TreeMap<>();
	private Map<Integer, Set<LogSource>> addedSources = new HashMap<>();
	private Map<Date, Map<Integer, List<AssetLog>>> removedClaims = new TreeMap<>();
	private Map<Integer, Set<LogSource>> removedSources = new HashMap<>();

	public LogData() { }

	public Map<Date, Map<Integer, List<AssetLog>>> getAddedClaims() {
		return addedClaims;
	}

	public Map<Integer, Set<LogSource>> getAddedSources() {
		return addedSources;
	}

	public Map<Date, Map<Integer, List<AssetLog>>> getRemovedClaims() {
		return removedClaims;
	}

	public Map<Integer, Set<LogSource>> getRemovedSources() {
		return removedSources;
	}

	public boolean isEmpty() {
		return addedClaims.isEmpty() && addedSources.isEmpty() && removedClaims.isEmpty() && removedSources.isEmpty();
	}
}
