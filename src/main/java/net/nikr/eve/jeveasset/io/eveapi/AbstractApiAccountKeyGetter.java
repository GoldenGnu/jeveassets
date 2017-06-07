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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.response.ApiListResponse;
import java.util.Date;
import java.util.Set;


public abstract class AbstractApiAccountKeyGetter<T extends ApiListResponse<?>, V> extends AbstractApiGetter<T> {

	private static final int ROW_COUNT = 1000;

	//Date
	private Set<V> values;
	private Date nextUpdate;

	//Request controllers
	private int accountKey;
	private int rowCount;
	private long fromID;

	public AbstractApiAccountKeyGetter(final String taskName) {
		super(taskName, true, false);
	}

	protected abstract Set<V> get();
	protected abstract long getId(V v);
	protected abstract void set(Set<V> values, Date nextUpdate);
	protected abstract T getResponse(final boolean bCorp, final int accountKey, final long fromID, final int rowCount) throws ApiException;
	protected abstract Set<V> convertData(final T response, final int accountKey);

	//Called for each owner by AbstractApiGetter
	@Override
	protected final boolean load(final Date nextUpdate, final boolean updateCorporation, final String updateName) {
		values = get();
		if (updateCorporation) {
			boolean ok = false;
			for (int i = 1000; i <= 1006; i++) { //For each wallet division
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
			if (updateCorporation) {
				updateName = updateName + " (division " + accountKey;
			}
			if (page > 1) {
				if (!updateCorporation) {
					updateName = updateName + " (";
				} else {
					updateName = updateName + " - ";
				}
				updateName = updateName + "page " + page;
			}
			if (page > 1 || updateCorporation) {
				updateName = updateName + ")";
			}
			boolean updated = super.load(nextUpdate, updateCorporation, updateName);
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
		Set<V> data = convertData(response, accountKey);
		for (V v : data) {
			Long id = getId(v);
			if (fromID == 0) {
				fromID = id;
			} else {
				fromID = Math.min(fromID, id);
			}
			values.add(v);
		}
	}
}
