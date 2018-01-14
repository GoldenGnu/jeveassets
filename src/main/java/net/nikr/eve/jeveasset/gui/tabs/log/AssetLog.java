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

	public AssetLog(AssetLogData data, long itemID, long need) {
		super(data);
		this.itemID = itemID;
		this.need = need;
	}

	public AssetLog(AssetLog assetLog, Date date, long need) {
		super(assetLog, date);
		this.need = need;
		this.itemID = assetLog.getItemID();
	}

	public AssetLog(MyAsset asset, Date date) {
		super(asset, date);
		this.need = asset.getCount();
		this.itemID = asset.getItemID();
	}

	public List<AssetLogSource> getSources() {
		return Collections.unmodifiableList(sources);
	}

	public long getItemID() {
		return itemID;
	}

	public long getNeed() {
		return need;
	}

	public void add(AssetLogSource assetLogSource, boolean claim) {
		sources.add(assetLogSource);
		if (claim) {
			need = need - assetLogSource.getCount();
		}
	}

	void add(AssetLog assetLog, int percent, long remove) {
		sources.add(new AssetLogSource(assetLog, this, LogChangeType.MOVED_FROM, percent, remove));
		assetLog.add(new AssetLogSource(this, assetLog, LogChangeType.MOVED_TO, percent, remove), true);
		need = need - remove;
	}

	@Override
	public int compareTo(AssetLog o) {
		return Long.compare(itemID, o.itemID);
	}
}
