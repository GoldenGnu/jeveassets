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

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterNamesResponse;
import net.troja.eve.esi.model.UniverseNamesResponse;


public class EsiNameGetter extends AbstractEsiGetter {

	private Set<Integer> ids;
	private UpdateTask updateTask;

	public void load(UpdateTask updateTask, Set<Integer> ids) {
		this.ids = new HashSet<Integer>(ids);
		this.updateTask = updateTask;
		super.load(updateTask);
	}

	@Override
	protected void get(EsiOwner owner) throws ApiException {
		//XXX - Workaround for universe/names being a bitch
		Set<Integer> blocked = new HashSet<Integer>();
		for (Integer id : ids) {
			if (oneByOneNameRange(id)) {
				blocked.add(id);
			}
		}
		ids.removeAll(blocked);
		List<List<Integer>> batches = splitList(ids, UNIVERSE_BATCH_SIZE);
		int progress = 0;
		final int size = batches.size() + blocked.size();
		for (List<Integer> batch : batches) {
			List<UniverseNamesResponse> names = getUniverseApiOpen().postUniverseNames(batch, DATASOURCE, System.getProperty("http.agent"), null);
			for (UniverseNamesResponse lookup : names) {
				Settings.get().getOwners().put((long)lookup.getId(), lookup.getName());
			}
			progress++;
			if (updateTask != null) {
				updateTask.setTaskProgress(size, progress, 0, 100);
			}
		}
		for (Integer id : blocked) { //One by one we go
			List<CharacterNamesResponse> names = getCharacterApiOpen().getCharactersNames(Collections.singletonList((long)id), DATASOURCE, System.getProperty("http.agent"), null);
			for (CharacterNamesResponse lookup : names) {
				Settings.get().getOwners().put(lookup.getCharacterId(), lookup.getCharacterName());
				progress++;
				if (updateTask != null) {
					updateTask.setTaskProgress(size, progress, 0, 100);
				}
			}
		}
		//Original code to return when universe/names is un-bitched
		/*
		List<List<Integer>> batches = splitList(ids, UNIVERSE_BATCH_SIZE);
		int progress = 0;
		for (List<Integer> batch : batches) {
			List<UniverseNamesResponse> names = getUniverseApiOpen().postUniverseNames(batch, DATASOURCE, System.getProperty("http.agent"), null);
			for (UniverseNamesResponse lookup : names) {
				Settings.get().getOwners().put((long)lookup.getId(), lookup.getName());
			}
			progress++;
			if (updateTask != null) {
				updateTask.setTaskProgress(batches.size(), progress, 0, 100);
			}
		}
		*/
	}

	@Override
	protected String getTaskName() {
		return "OwnerID to Name";
	}

	@Override
	protected void setNextUpdate(EsiOwner owner, Date date) { }

	@Override
	protected Date getNextUpdate(EsiOwner owner) {
		return new Date();
	}

	@Override
	protected boolean inScope(EsiOwner owner) {
		return true;
	}

	@Override
	protected boolean enabled(EsiOwner owner) {
		return EsiScopes.NAMES.isEnabled();
	}

}
