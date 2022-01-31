/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.loadout;

import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.profile.TableData;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;


public class LoadoutData extends TableData {

	public LoadoutData(Program program) {
		super(program);
	}

	public LoadoutData(ProfileManager profileManager, ProfileData profileData) {
		super(profileManager, profileData);
	}

	public EventList<Loadout> getData() {
		EventList<Loadout> eventList = EventListManager.create();
		updateData(eventList);
		return eventList;
	}

	public void updateData(EventList<Loadout> eventList) {
		List<Loadout> ship = new ArrayList<>();
		for (MyAsset asset : profileData.getAssetsList()) {
			if (!asset.getItem().isShip() || !asset.isSingleton()) {
				continue;
			}
			Loadout moduleShip = new Loadout(asset.getItem(), asset.getLocation(), asset.getOwner(), TabsLoadout.get().totalShip(), asset, TabsLoadout.get().flagTotalValue(), null, asset.getDynamicPrice(), 1, true);
			Loadout moduleModules = new Loadout(new Item(0), asset.getLocation(), asset.getOwner(), TabsLoadout.get().totalModules(), asset, TabsLoadout.get().flagTotalValue(), null, 0, 0, false);
			Loadout moduleTotal = new Loadout(new Item(0), asset.getLocation(), asset.getOwner(), TabsLoadout.get().totalAll(), asset, TabsLoadout.get().flagTotalValue(), null, asset.getDynamicPrice(), 1, false);
			ship.add(moduleShip);
			ship.add(moduleModules);
			ship.add(moduleTotal);
			Map<Integer, Loadout> modules = new HashMap<>();
			for (MyAsset assetModule : asset.getAssets()) {
				Loadout module = modules.get(assetModule.getTypeID());
				if (module == null //New
						|| assetModule.getFlag().contains(Loadout.FlagType.HIGH_SLOT.getFlag())
						|| assetModule.getFlag().contains(Loadout.FlagType.MEDIUM_SLOT.getFlag())
						|| assetModule.getFlag().contains(Loadout.FlagType.LOW_SLOT.getFlag())
						|| assetModule.getFlag().contains(Loadout.FlagType.RIG_SLOTS.getFlag())
						|| assetModule.getFlag().contains(Loadout.FlagType.SUB_SYSTEMS.getFlag())
						) {
					module = new Loadout(assetModule.getItem(), assetModule.getLocation(), assetModule.getOwner(), assetModule.getName(), asset, assetModule.getFlag(), assetModule.getDynamicPrice(), (assetModule.getDynamicPrice() * assetModule.getCount()), assetModule.getCount(), false);
					modules.put(assetModule.getTypeID(), module);
					ship.add(module);
				} else { //Add count
					module.addCount(assetModule.getCount());
					module.addValue(assetModule.getDynamicPrice() * assetModule.getCount());
				}
				moduleModules.addValue(assetModule.getDynamicPrice() * assetModule.getCount());
				moduleModules.addCount(assetModule.getCount());
				moduleTotal.addValue(assetModule.getDynamicPrice() * assetModule.getCount());
				moduleTotal.addCount(assetModule.getCount());
			}
		}
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(ship);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}
	
}
