/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.core.ApiListResponse;
import com.beimin.eveapi.exception.ApiException;
import java.util.Date;
import java.util.Map;


public abstract class AbstractApiAccountKeyGetter<T extends ApiListResponse<?>, V> extends AbstractApiGetter<T> {

	private static final int ROW_COUNT = 1000;

	//Date
	private Map<Long, V> values;
	private Date nextUpdate;

	//Request controllers
	private int accountKey;
	private int rowCount;
	private long fromID;

	public AbstractApiAccountKeyGetter(final String taskName) {
		super(taskName, true, false);
	}

	protected abstract Map<Long, V> get();
	protected abstract void set(Map<Long, V> values, Date nextUpdate);
	protected abstract T getResponse(final boolean bCorp, final int accountKey, final long fromID, final int rowCount) throws ApiException;
	protected abstract Map<Long, V> convertData(final T response, final int accountKey);

	//Called for each owner by AbstractApiGetter
	@Override
	protected final boolean load(final Date nextUpdate, final boolean updateCorporation, final String updateName) {
		values = get();
		if (updateCorporation) {
			boolean ok = false;
			for (int i = 1000; i <= 1006; i++) { //For each wallet devision
				accountKey = i;
				boolean updated = loadAll(nextUpdate, updateCorporation, updateName);
				if (!updated) {
					return false;
				} else {
					ok = true;
				}
			}
			if (ok) {
				set();
			}
			return ok;
		} else {
			accountKey = 1000;
			boolean updated = loadAll(nextUpdate, updateCorporation, updateName);
			if (updated) {
				set();
			}
			return updated;
		}
	}

	//Called for each owner (by AbstractApiAccountKeyGetter.load)
	private void set() {
		set(values, nextUpdate);
	}

	// Called for each owner > accountKey (by AbstractApiAccountKeyGetter.load)
	private boolean loadAll(Date nextUpdate, boolean updateCorporation, String updateName) {
		boolean ok = false;
		int page = 1;
		rowCount = ROW_COUNT;
		fromID = 0;
		while (rowCount >= ROW_COUNT) {
			boolean updated = super.load(nextUpdate, updateCorporation, updateName + " (accountKey: " + accountKey + " - page: " + page + ")");
			if (!updated) {
				return false;
			} else {
				ok = true;
			}
			page++;
		}
		return ok;
	}

	//Called for each owner > accountKey > page (by AbstractApiAccountKeyGetter.loadAll via AbstractApiGetter)
	@Override
	protected final T getResponse(boolean bCorp) throws ApiException {
		return getResponse(bCorp, accountKey, fromID, ROW_COUNT);
	}

	//Called for each owner > accountKey > page (by AbstractApiAccountKeyGetter.loadAll via AbstractApiGetter)
	@Override
	protected final void setNextUpdate(final Date nextUpdate) {
		this.nextUpdate = nextUpdate;
	}

	//Called for each owner > accountKey > page (by AbstractApiAccountKeyGetter.loadAll via AbstractApiGetter)
	@Override
	protected final void setData(final T response) {
		rowCount = response.getAll().size();
		fromID = 0;
		Map<Long, V> data = convertData(response, accountKey);
		for (Map.Entry<Long, V> entry : data.entrySet()) {
			Long id = entry.getKey();
			if (fromID == 0) {
				fromID = id;
			} else {
				fromID = Math.min(fromID, id);
			}
			values.put(id, entry.getValue());
		}
	}
}
