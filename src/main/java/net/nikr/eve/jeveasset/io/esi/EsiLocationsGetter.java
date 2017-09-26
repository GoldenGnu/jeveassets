/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.esi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterAssetsNamesResponse;


public class EsiLocationsGetter extends AbstractEsiGetter {

	private final Map<Long, String> eveNames = new HashMap<Long, String>();
	private UpdateTask updateTask;

	@Override
	public void load(UpdateTask updateTask, List<EsiOwner> owners) {
		this.updateTask = updateTask;
		eveNames.clear();
		super.load(updateTask, owners);
		if (!hasError()) {
			Settings.get().setEveNames(eveNames);
		}
	}
	
	@Override
	protected void get(EsiOwner owner) throws ApiException {
		Map<Long, String> itemMap = new HashMap<Long, String>();
		addItemIDs(itemMap, owner.getAssets());
		List<List<Long>> batches = splitList(itemMap.keySet(), LOCATIONS_BATCH_SIZE);
		int progress = 0;
		final int size = batches.size();
		for (List<Long> batch : batches) {
			List<CharacterAssetsNamesResponse> responses = getAssetsApiAuth().postCharactersCharacterIdAssetsNames((int) owner.getOwnerID(), batch, DATASOURCE, null, null, null);
			for (CharacterAssetsNamesResponse response : responses) {
				final long itemID = response.getItemId();
				final String eveName = response.getName();
				final String typeName = itemMap.get(itemID);
				if (!eveName.isEmpty() && !eveName.equals(typeName)) {
					eveNames.put(itemID, eveName);
				}
			}
			progress++;
			if (updateTask != null) {
				updateTask.setTaskProgress(size, progress, 0, 100);
			}
		}
	}

	@Override
	protected int getProgressStart() {
		return 80;
	}

	private void addItemIDs(Map<Long, String> itemIDs, List<MyAsset> assets) {
		for (MyAsset asset : assets) {
			if ((asset.getItem().getGroup().equals("Audit Log Secure Container")
					|| asset.getItem().getCategory().equals("Ship"))
					&& asset.isSingleton()) {
				itemIDs.put(asset.getItemID(), asset.getItem().getTypeName());
			}
			addItemIDs(itemIDs, asset.getAssets());
		}
	}

	@Override
	protected String getTaskName() {
		return "Locations";
	}

	@Override
	protected void setNextUpdate(EsiOwner owner, Date date) {
		//Do nothing
	}

	@Override
	protected Date getNextUpdate(EsiOwner owner) {
		return new Date();
	}

	@Override
	protected boolean inScope(EsiOwner owner) {
		return owner.isLocations();
	}

	@Override
	protected boolean enabled(EsiOwner owner) {
		if (owner.isCorporation()) {
			return EsiScopes.CORPORATION_ASSETS.isEnabled();
		} else {
			return EsiScopes.CHARACTER_ASSETS.isEnabled();
		}
	}
	
}
