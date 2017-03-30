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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;


public abstract class AbstractEveKitValidatedGetter<T> extends AbstractEveKitGetter {

	private static final int MONTHS = 3;

	protected static final boolean REVERSE = false;
	protected static final Integer MAX_RESULTS = Integer.MAX_VALUE;
	private boolean updateFullHistory = false;

	public void setUpdateFullHistory(boolean updateFullHistory) {
		this.updateFullHistory = updateFullHistory;
	}

	public boolean isUpdateFullHistory() {
		return updateFullHistory;
	}

	protected boolean isValid(T obj) {
		return true;
	}
	protected abstract boolean isNow(T obj);
	protected abstract void set(EveKitOwner owner, List<T> data) throws ApiException;

	protected final String industryJobsFilter() {
		return encode("{ values: [\"1\", \"2\", \"3\"] }");
	}

	protected final String contractsFilter() {
		return encode("{ values: [\"InProgress\"] }");
	}

	protected final String valuesFilter(long id) {
		return encode("{ values: [\"" + id + "\"] }");
	}

	protected final String dateFilter() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		calendar.add(Calendar.DAY_OF_MONTH, (-MONTHS * 28));
		return encode("{ start: \"" + String.valueOf(calendar.getTime().getTime()) + "\", end: \"" + String.valueOf(Long.MAX_VALUE) + "\" }");
	}

	private String encode(String plane) {
		try {
			return URLEncoder.encode(plane, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}
}
