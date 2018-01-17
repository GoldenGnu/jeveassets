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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;


public class AssetLog extends AssetLogData implements Comparable<AssetLog> {

	private final long itemID;
	private final List<AssetLogSource> sources = new ArrayList<>();
	private long need;
	private boolean added = false;
	private boolean removed = false;
	private boolean moved = false;

	/**
	 * Load
	 * @param data
	 * @param itemID 
	 */
	public AssetLog(AssetLogData data, long itemID) {
		super(data, data.getCount());
		this.itemID = itemID;
		this.need = data.getCount();
	}

	/**
	 * Removed
	 * @param assetLog
	 * @param date
	 * @param count 
	 */
	public AssetLog(AssetLog assetLog, Date date, long count) {
		super(assetLog, date, count);
		this.need = count;
		this.itemID = assetLog.getItemID();
	}

	/**
	 * New
	 * @param asset
	 * @param date 
	 */
	public AssetLog(MyAsset asset, Date date) {
		super(asset, date);
		this.need = asset.getCount();
		this.itemID = asset.getItemID();
	}

	public List<AssetLogSource> getSources() {
		return Collections.unmodifiableList(sources);
	}

	public void reset() {
		need = getCount();
		sources.clear();
	}

	public long getItemID() {
		return itemID;
	}

	public long getNeed() {
		return need;
	}

	public boolean isAdded() {
		return added;
	}

	public boolean isRemoved() {
		return removed;
	}

	public boolean isMoved() {
		return moved;
	}

	private void add(AssetLogSource assetLogSource) {
		sources.add(assetLogSource);
		added = added || assetLogSource.getSourceType().isAdded();
		removed = removed || assetLogSource.getSourceType().isRemoved();
		moved = moved || assetLogSource.getSourceType().isMoved();
	}

	public void add(AssetLogSource assetLogSource, boolean claim) {
		add(assetLogSource);
		if (claim) {
			need = need - assetLogSource.getCount();
		}
	}

	void add(AssetLog assetLog, int percent, long remove) {
		add(new AssetLogSource(assetLog, this, LogSourceType.MOVED_FROM, percent, remove));
		assetLog.add(new AssetLogSource(this, assetLog, LogSourceType.MOVED_TO, percent, remove), true);
		need = need - remove;
	}

	@Override
	public int compareTo(AssetLog o) {
		return Long.compare(itemID, o.itemID);
	}
}
