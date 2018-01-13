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
package net.nikr.eve.jeveasset.gui.shared.components;

import ca.odell.glazedlists.EventList;
import java.util.Set;
import javax.swing.Icon;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.settings.types.EditableLocationType;
import net.nikr.eve.jeveasset.data.settings.types.EditablePriceType;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;


public abstract class JMainTabSecondary extends JMainTab {

	public JMainTabSecondary(Program program, String title, Icon icon, boolean closeable) {
		super(program, title, icon, closeable);
	}

	public JMainTabSecondary(final boolean load) {
		super(false);
	}

	@Override
	public void updateNames(Set<Long> itemIDs) {
		if (this instanceof NamesUpdater) {
			NamesUpdater<?> namesUpdater = (NamesUpdater) this;
			ProfileData.updateNames(namesUpdater.getEventList(), itemIDs);
		}
	}

	@Override
	public void updateLocations(Set<Long> locationIDs) {
		if (this instanceof LocationsUpdater) {
			LocationsUpdater<?> locationUpdater = (LocationsUpdater) this;
			ProfileData.updateLocation(locationUpdater.getEventList(), locationIDs);
		} else {
			updateData();
		}
	}

	@Override
	public void updatePrices(Set<Integer> typeIDs) {
		if (this instanceof PricesUpdater) {
			PricesUpdater<?> locationUpdater = (PricesUpdater) this;
			ProfileData.updatePrices(locationUpdater.getEventList(), typeIDs);
		} else {
			updateData();
		}
	}

	public interface LocationsUpdater<T extends EditableLocationType> {
		public EventList<T> getEventList();
	}

	public interface NamesUpdater<T extends MyAsset> {
		public EventList<T> getEventList();
	}

	public interface PricesUpdater<T extends ItemType & EditablePriceType> {
		public EventList<T> getEventList();
	}

}