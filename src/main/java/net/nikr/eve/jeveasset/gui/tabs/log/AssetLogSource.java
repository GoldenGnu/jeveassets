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


public class AssetLogSource extends AssetLogData implements Comparable<AssetLogSource> {
	private final AssetLog parent;
	private final LogChangeType changeType;
	private final int percent;
	private final long count;

	public AssetLogSource(AssetLogData data, AssetLog parent, LogChangeType changeType, int percent, long count) {
		super(data);
		this.parent = parent;
		this.changeType = changeType;
		this.percent = percent;
		this.count = count;
		
	}

	public AssetLog getParent() {
		return parent;
	}
	
	public LogChangeType getChangeType() {
		return changeType;
	}

	public int getPercent() {
		return percent;
	}

	public long getCount() {
		return count;
	}

	public String getChangeTypeName() {
		return getChangeType().toString();
	}

	@Override
	public int compareTo(AssetLogSource o) {
		return 0;
	}
}
