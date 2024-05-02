/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.table.containers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.i18n.General;


public class AssetContainer implements Comparable<AssetContainer>{

	private final String container;
	private final String containerSimple; //No ItemID
	private final List<Long> parentIDs = new ArrayList<>();

	public AssetContainer() {
		this.container = "";
		this.containerSimple = "";
	}

	public AssetContainer(MyAsset asset) {
		StringBuilder builder = new StringBuilder();
		StringBuilder simpleBuilder = new StringBuilder();
		if (asset.getParents().isEmpty()) {
			builder.append(General.get().none());
			simpleBuilder.append(General.get().none());
		} else {
			boolean first = true;
			for (MyAsset parentAsset : asset.getParents()) {
				if (first) {
					first = false;
				} else {
					builder.append(" > ");
					simpleBuilder.append(" > ");
				}
				simpleBuilder.append(parentAsset.getName());
				builder.append(parentAsset.getName());
				if (!parentAsset.isUserName()) {
					builder.append(" #");
					builder.append(parentAsset.getItemID());
				}				
				parentIDs.add(parentAsset.getItemID());
			}
		}
		this.container = builder.toString().intern();
		this.containerSimple = simpleBuilder.toString().intern();
	}

	public String getContainer() {
		return container;
	}

	public List<Long> getParentIDs() {
		return parentIDs;
	}

	@Override
	public int compareTo(AssetContainer o) {
		return this.container.compareTo(o.container);
	}

	@Override
	public String toString() {
		if (Settings.get().isContainersShowItemID()) {
			return container;
		} else {
			return containerSimple;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		if (Settings.get().isContainersShowItemID()) {
			hash = 37 * hash + Objects.hashCode(this.container);
		} else {
			hash = 37 * hash + Objects.hashCode(this.containerSimple);
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AssetContainer other = (AssetContainer) obj;
		if (Settings.get().isContainersShowItemID()) {
			return Objects.equals(this.container, other.container);
		} else {
			return Objects.equals(this.containerSimple, other.containerSimple);
		}
	}
}
