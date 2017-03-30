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
import java.util.Set;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;


public abstract class AbstractEveKitIdGetter<T> extends AbstractEveKitValidatedGetter<T> {

	@Override
	protected final void get(EveKitOwner owner) throws ApiException {
		List<T> results = new ArrayList<T>();
		Set<Long> ids = getIDs(owner);
		for (long id : ids) {
			List<T> batch = get(owner, id);
			for (T t : batch) {
				if ((isUpdateFullHistory() || isNow(t)) || isValid(t)) { //Ignore old objects, unless we're getting everything
					results.add(t);
				}
			}
		}
		set(owner, results);
	}

	protected abstract List<T> get(EveKitOwner owner, long id) throws ApiException;
	protected abstract Set<Long> getIDs(EveKitOwner owner) throws ApiException;
}
