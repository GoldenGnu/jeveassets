/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.dialogs.export;

import ca.odell.glazedlists.matchers.Matcher;
import java.util.List;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.gui.shared.EveAssetMatching;


public class AssetFilterLogicalMatcher implements Matcher<Asset> {

	private List<AssetFilter> assetFilters;
	private EveAssetMatching eveAssetMatching = new EveAssetMatching();

	public AssetFilterLogicalMatcher(List<AssetFilter> assetFilters) {
		this.assetFilters = assetFilters;
	}
	
	@Override
	public boolean matches(Asset item) {
		boolean bOr = false;
		boolean bAnyOrs = false;
		for (int a = 0; a < assetFilters.size(); a++){
			AssetFilter assetFilter = assetFilters.get(a);
			boolean matches = eveAssetMatching.matches(item, assetFilter);
			if (assetFilter.isAnd()){ //And
				if (!matches){ //if just one don't match, none match
					return false;
				}
			} else { //Or
				bAnyOrs = true;
				if (matches){ //if just one is true all is true
					bOr = true;
				}
			}
		}
		return (bOr || !bAnyOrs);
	}
}
