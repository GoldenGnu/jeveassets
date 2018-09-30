/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared.filter;

import ca.odell.glazedlists.matchers.Matcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FilterLogicalMatcher<E> implements Matcher<E> {

	private final List<FilterMatcher<E>> and = new ArrayList<>();
	private final Map<Integer, List<FilterMatcher<E>>> or = new HashMap<>();

	public FilterLogicalMatcher(final List<FilterMatcher<E>> matchers) {
		for (FilterMatcher<E> matcher : matchers) {
			addMatcher(matcher);
		}
	}

	public FilterLogicalMatcher(final FilterControl<E> filterControl, final List<Filter> filters) {
		for (Filter filter : filters) {
			FilterMatcher<E> matcher = new FilterMatcher<>(filterControl, filter);
			addMatcher(matcher);
		}
	}

	private void addMatcher(FilterMatcher<E> matcher) {
		if (!matcher.isEmpty()) {
			if (matcher.isAnd()) { //And
				and.add(matcher);
			} else {
				List<FilterMatcher<E>> list = or.get(matcher.getGroup());
				if (list == null) {
					list = new ArrayList<>();
					or.put(matcher.getGroup(), list);
				}
				list.add(matcher);
			}
		}
	}

	@Override
	public boolean matches(final E item) {
		for (FilterMatcher<E> matcher : and) {
			if (!matcher.matches(item)) { //if just one don't match, none match
				return false;
			}
		}
		//All ANDs matches
		for (List<FilterMatcher<E>> list : or.values()) {
			boolean found = false;
			for (FilterMatcher<E> matcher : list) {
				if (matcher.matches(item)) { //if just one is true all is true
					found = true;
					break;
				}
			}
			if (!found) { //if none in group match (just need to match one)
				return false;
			}
		}
		return true; //Matched
	}
}
