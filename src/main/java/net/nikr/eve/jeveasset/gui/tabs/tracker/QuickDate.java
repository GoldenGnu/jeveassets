/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.tracker;

import java.util.Calendar;
import java.util.Date;
import net.nikr.eve.jeveasset.i18n.TabsTracker;


public enum QuickDate {
	EMPTY(null, null, TabsTracker.get().quickDate())
	,MONTH_ONE(Calendar.MONTH, -1, TabsTracker.get().month1())
	,MONTH_THREE(Calendar.MONTH, -3, TabsTracker.get().months3())
	,MONTH_SIX(Calendar.MONTH, -6, TabsTracker.get().months6())
	,YEAR_ONE(Calendar.YEAR, -1, TabsTracker.get().year1())
	,YEAR_TWO(Calendar.YEAR, -2, TabsTracker.get().years2());

	private Integer field;
	private Integer amount;
	private String title;

	private QuickDate(Integer field, Integer amount, String title) {
		this.field = field;
		this.amount = amount;
		this.title = title;
	}

	public Date apply(Date to) {
		if (field == null || amount == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(to);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(field, amount);
		return calendar.getTime();
	}

	public boolean isValid(Date from, Date to) {
		if (to == null) {
			to = new Date(); //now
		}
		if (from == null) {
			return false;
		}
		to = apply(to);
		return from.equals(to);
	}

	public QuickDate getSelected(Date from, Date to) {
		for (QuickDate quickDate : QuickDate.values()) {
			if (quickDate.isValid(from, to)) {
				return quickDate;
			}
		}
		return QuickDate.EMPTY;
	}

	@Override
	public String toString() {
		return title;
	}
}
