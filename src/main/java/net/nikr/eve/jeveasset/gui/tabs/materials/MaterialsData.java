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
package net.nikr.eve.jeveasset.gui.tabs.materials;

import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.profile.TableData;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;


public class MaterialsData extends TableData {

	public MaterialsData(Program program) {
		super(program);
	}

	public MaterialsData(ProfileManager profileManager, ProfileData profileData) {
		super(profileManager, profileData);
	}

	public EventList<Material> getData(String owner, boolean ore, boolean pi) {
		Set<String> groups = new HashSet<>();
		for (Item item : StaticData.get().getItems().values()) {
			if (item.getCategory().equals(Item.CATEGORY_MATERIAL)) {
				groups.add(item.getGroup());
			}
			if (pi && item.isPiMaterial()) {
				groups.add(item.getGroup());
			}
			if (ore && item.isMined()) {
				groups.add(item.getGroup());
			}
		}
		return getData(owner, groups);
	}

	public EventList<Material> getData(String owner, Set<String> groups) {
		EventList<Material> eventList = EventListManager.create();
		updateData(eventList, owner, groups);
		return eventList;
	}

	protected boolean updateData(EventList<Material> eventList, String owner, Set<String> groups) {
		if (owner == null || owner.isEmpty()) {
			owner = General.get().all();
		}
		boolean includeAll = owner.equals(General.get().all());
		List<Material> materials = new ArrayList<>();
		Map<String, Material> uniqueMaterials = new HashMap<>();
		Map<String, Material> totalMaterials = new HashMap<>();
		Map<String, Material> totalAllMaterials = new HashMap<>();
		Map<String, Material> summary = new HashMap<>();
		Map<String, Material> total = new HashMap<>();
		//Summary Total All
		Material summaryTotalAllMaterial = new Material(Material.MaterialType.SUMMARY_ALL, null, TabsMaterials.get().summary(), TabsMaterials.get().grandTotal(), General.get().all());
		for (MyAsset asset : profileData.getAssetsList()) {
			//Skip
			if (!groups.contains(asset.getItem().getGroup())) {
				continue;
			}
			//Skip not selected owners
			if (!owner.equals(asset.getOwnerName()) && !includeAll) {
				continue;
			}

			//Locations
			Material material = uniqueMaterials.get(asset.getLocation().getLocation() + asset.getName());
			if (material == null) { //New
				material = new Material(Material.MaterialType.LOCATIONS, asset, asset.getLocation().getLocation(), asset.getItem().getGroup(), asset.getName());
				uniqueMaterials.put(asset.getLocation().getLocation() + asset.getName(), material);
				materials.add(material);
			}

			//Locations Total
			Material totalMaterial = totalMaterials.get(asset.getLocation().getLocation() + asset.getItem().getGroup());
			if (totalMaterial == null) { //New
				totalMaterial = new Material(Material.MaterialType.LOCATIONS_TOTAL, asset, asset.getLocation().getLocation(), TabsMaterials.get().total(), asset.getItem().getGroup());
				totalMaterials.put(asset.getLocation().getLocation() + asset.getItem().getGroup(), totalMaterial);
				materials.add(totalMaterial);
			}

			//Locations Total All
			Material totalAllMaterial = totalAllMaterials.get(asset.getLocation().getLocation());
			if (totalAllMaterial == null) { //New
				totalAllMaterial = new Material(Material.MaterialType.LOCATIONS_ALL, asset, asset.getLocation().getLocation(), TabsMaterials.get().total(), General.get().all());
				totalAllMaterials.put(asset.getLocation().getLocation(), totalAllMaterial);
				materials.add(totalAllMaterial);
			}

			//Summary
			Material summaryMaterial = summary.get(asset.getName());
			if (summaryMaterial == null) { //New
				summaryMaterial = new Material(Material.MaterialType.SUMMARY, asset, TabsMaterials.get().summary(), asset.getItem().getGroup(), asset.getName());
				summary.put(asset.getName(), summaryMaterial);
				materials.add(summaryMaterial);
			}

			//Summary Total
			Material summaryTotalMaterial = total.get(asset.getItem().getGroup());
			if (summaryTotalMaterial == null) { //New
				summaryTotalMaterial = new Material(Material.MaterialType.SUMMARY_TOTAL, null, TabsMaterials.get().summary(), TabsMaterials.get().grandTotal(), asset.getItem().getGroup());
				total.put(asset.getItem().getGroup(), summaryTotalMaterial);
				materials.add(summaryTotalMaterial);
			}

			//Update values
			material.updateValue(asset.getCount(), asset.getDynamicPrice());
			totalMaterial.updateValue(asset.getCount(), asset.getDynamicPrice());
			totalAllMaterial.updateValue(asset.getCount(), asset.getDynamicPrice());
			summaryMaterial.updateValue(asset.getCount(), asset.getDynamicPrice());
			summaryTotalMaterial.updateValue(asset.getCount(), asset.getDynamicPrice());
			summaryTotalAllMaterial.updateValue(asset.getCount(), asset.getDynamicPrice());
		}
		if (!materials.isEmpty()) {
			materials.add(summaryTotalAllMaterial);
		}
		Collections.sort(materials);
		String location = "";
		for (Material material : materials) {
			if (!location.equals(material.getHeader())) {
				material.first();
				location = material.getHeader();
			}
		}
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(materials);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		return materials.isEmpty();
	}
}
