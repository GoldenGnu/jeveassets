/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.eve.conquerablestationlist.StationListResponse;
import com.beimin.eveapi.exception.ApiException;
import java.util.Date;
import net.nikr.eve.jeveasset.data.Account.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.local.ConquerableStationsWriter;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;


public class ConquerableStationsGetter extends AbstractApiGetter<StationListResponse> {

	private Settings settings;

	public ConquerableStationsGetter() {
		super("Conquerable Stations");
	}

	public void load(final UpdateTask updateTask, final Settings loadSettings) {
		this.settings = loadSettings;
		load(updateTask, loadSettings.isForceUpdate(), "jEveAssets");
	}

	@Override
	protected StationListResponse getResponse(final boolean bCorp) throws ApiException {
		return com.beimin.eveapi.eve.conquerablestationlist
				.ConquerableStationListParser.getInstance().getResponse();
	}

	@Override
	protected Date getNextUpdate() {
		return settings.getConquerableStationsNextUpdate();
	}

	@Override
	protected void setNextUpdate(final Date nextUpdate) {
		settings.setConquerableStationsNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(final StationListResponse response) {
		settings.setConquerableStations(response.getStations());
		ConquerableStationsWriter.save(settings);
	}

	@Override
	protected void updateFailed(final Owner ownerFrom, final Owner ownerTo) { }

	@Override
	protected long requestMask(boolean bCorp) {
		return AccessMask.OPEN.getAccessMask();
	}
}
