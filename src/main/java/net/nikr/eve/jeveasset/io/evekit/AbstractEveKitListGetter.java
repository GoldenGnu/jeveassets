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


public abstract class AbstractEveKitListGetter<T> extends AbstractEveKitValidatedGetter<T> {
	
	@Override
	protected final void get(EveKitOwner owner) throws ApiException {
		List<T> results = new ArrayList<T>();
		List<T> batch = null;
		boolean more = true;
		while (batch == null || (!batch.isEmpty() && more)) {
			batch = get(owner, getCid(owner, batch));
			more = false;
			for (T t : batch) {
				if ((isUpdateFullHistory() || isNow(t))) { //Ignore old objects, unless we're getting everything
					if (isValid(t)) {
						results.add(t);
					}
					more = true; //At least one item was not old (or we're getting everything): run another batch
				}
			}
		}
		set(owner, results);
		saveCID(owner, getCid(owner, results));
	}

	private Long getCid(EveKitOwner owner, List<T> batch) {
		if (batch == null || batch.isEmpty()) {
			return loadCID(owner);
		} else {
			return getCID(batch.get(batch.size() - 1));
		}
	}

	protected abstract List<T> get(EveKitOwner owner, Long cid) throws ApiException;
	protected abstract long getCID(T obj);
	protected abstract void saveCID(EveKitOwner owner, Long cid);
	protected abstract Long loadCID(EveKitOwner owner);
}
