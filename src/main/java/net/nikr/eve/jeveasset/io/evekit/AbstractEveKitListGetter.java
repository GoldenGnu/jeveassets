/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.evekit;

import enterprises.orbital.evekit.client.invoker.ApiException;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;


public abstract class AbstractEveKitListGetter<T> extends AbstractEveKitGetter {
	protected static final boolean REVERSE = false;
	protected static final Integer MAX_RESULTS = Integer.MAX_VALUE;
	private boolean updateFullHistory = false;

	public void setUpdateFullHistory(boolean updateFullHistory) {
		this.updateFullHistory = updateFullHistory;
	}

	@Override
	protected final void get(EveKitOwner owner) throws ApiException {
		List<T> results = new ArrayList<>();
		List<T> batch = null;
		boolean more = true;
		while (batch == null || (!batch.isEmpty() && more)) {
			batch = get(owner, getCid(batch));
			more = false;
			for (T t : batch) {
				if ((updateFullHistory || isNow(t))) { //Ignore old objects, unless we're getting everything
					if (isValid(t)) {
						results.add(t);
					}
					more = true; //At least one item was not old (or we're getting everything): run another batch
				}
			}
		}
		set(owner, results);
	}

	private long getCid(List<T> batch) {
		if (batch == null) {
			return 0;
		} else {
			return getCid(batch.get(batch.size() - 1));
		}
	}

	protected boolean isValid(T obj) {
		return true;
	}
	protected abstract List<T> get(EveKitOwner owner, long contid) throws ApiException;
	protected abstract void set(EveKitOwner owner, List<T> data) throws ApiException;
	protected abstract long getCid(T obj);
	protected abstract boolean isNow(T obj);
}
