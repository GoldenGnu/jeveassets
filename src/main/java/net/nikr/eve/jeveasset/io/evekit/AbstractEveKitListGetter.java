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
package net.nikr.eve.jeveasset.io.evekit;

import enterprises.orbital.evekit.client.invoker.ApiException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;


public abstract class AbstractEveKitListGetter<T> extends AbstractEveKitGetter {

	private Date lifeStart = null;
	private final boolean reverse = false;
	private Integer maxResults = Integer.MAX_VALUE;

	@Override
	protected final void get(EveKitOwner owner, Long at, boolean first) throws ApiException {
		if (first) {
			maxResults = 1;
			List<T> results = get(owner, atAny(), null);
			updateStartDate(results);
		} else {
			maxResults = Integer.MAX_VALUE;
			List<T> results = new ArrayList<T>();
			List<T> batch = null;
			while (batch == null || !batch.isEmpty()) {
				batch = get(owner, atFilter(at), getCid(owner, batch));
				for (T t : batch) {
					if (isValid(t)) {
						results.add(t);
					}
				}
			}
			set(owner, results);
			saveCID(owner, getCid(owner, results));
		}
	}

	private Long getCid(EveKitOwner owner, List<T> batch) {
		if (batch == null || batch.isEmpty()) {
			return loadCID(owner);
		} else {
			return getCID(batch.get(batch.size() - 1));
		}
	}

	private void updateStartDate(List<T> results) {
		if (results != null && !results.isEmpty()) {
			Long l = getLifeStart(results.get(0));
			if (l != null) {
				Date date = new Date(l);
				if (lifeStart == null || date.before(lifeStart)) {
					lifeStart = date;
				}
			}
		}
	}

	public Date getLifeStart() {
		return lifeStart;
	}

	protected boolean isValid(T obj) {
		return true;
	}

	protected Integer getMaxResults() {
		return maxResults;
	}

	protected boolean getReverse() {
		return reverse;
	}

	protected final String industryJobsFilter() {
		return encode("{ values: [\"1\", \"2\", \"3\"] }");
	}

	protected final String contractsFilter() {
		return encode("{ values: [\"InProgress\"] }");
	}

	protected final String valuesFilter(Set<Long> ids) {
		StringBuilder builder = new StringBuilder();
		builder.append("{ values: [");
		if (ids.isEmpty()) {
			builder.append("\"\"");
		}
		boolean first = true;
		for (Long id : ids) {
			if (first) {
				first = false;
			} else {
				builder.append(", ");
			}
			builder.append("\"");
			builder.append(id);
			builder.append("\"");
		}
		builder.append("] }");
		return encode(builder.toString());
	}

	protected final String dateFilter(int months) {
		if (months == 0) {
			return encode("{ any: true }");
		} else {
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			calendar.add(Calendar.DAY_OF_MONTH, (-months * 30));
			return encode("{ start: \"" + String.valueOf(calendar.getTime().getTime()) + "\", end: \"" + String.valueOf(Long.MAX_VALUE) + "\" }");
		}
	}

	protected final String atFilter(Long at) {
		if (at == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("{ values: [\"");
		builder.append(at);
		builder.append("\"] }");
		return encode(builder.toString());
	}

	protected final String atAny() {
		return encode("{ any: true }");
	}

	protected String encode(String plane) {
		try {
			return URLEncoder.encode(plane, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	protected abstract List<T> get(EveKitOwner owner, String at, Long cid) throws ApiException;
	protected abstract long getCID(T obj);
	protected abstract Long getLifeStart(T obj);
	protected abstract void saveCID(EveKitOwner owner, Long cid);
	protected abstract Long loadCID(EveKitOwner owner);
	protected abstract void set(EveKitOwner owner, List<T> data) throws ApiException;
}
