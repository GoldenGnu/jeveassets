/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.table.containers;

import com.google.common.primitives.Longs;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class Duration implements Comparable<Duration> {

	public static final Duration NEVER = new Duration(GuiShared.get().durationNever());
	public static final Duration DONE = new Duration(GuiShared.get().durationDone());

	private final long duration;
	private final String formatted;
	
	public Duration(String formatted) {
		this.duration = 0;
		this.formatted = formatted;
	}

	public Duration(long time) {
		this(time, false, false, true, true, true, true);
	}

	public Duration(long time, boolean first, boolean verbose) {
		this(time, first, verbose, true, true, true, true);
	}

	public Duration(long time, boolean showDays, boolean showHours, boolean showMinutes, boolean showSeconds) {
		this(time, false, false, showDays, showHours, showMinutes, showSeconds);
	}

	public Duration(long time, boolean first, boolean verbose, boolean showDays, boolean showHours, boolean showMinutes, boolean showSeconds) {
		duration = time;
		formatted = Formatter.milliseconds(time, showDays, showHours, showMinutes, showSeconds);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + (int) (this.duration ^ (this.duration >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Duration other = (Duration) obj;
		return this.duration == other.duration;
	}

	@Override
	public String toString() {
		return formatted;
	}

	@Override
	public int compareTo(Duration o) {
		return Longs.compare(duration, o.duration);
	}
}
