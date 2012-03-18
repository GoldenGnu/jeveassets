/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import java.util.List;


public class FilterLogicalMatcher<E> implements Matcher<E> {

	private final List<FilterMatcher<E>> matchers;

	public FilterLogicalMatcher(List<FilterMatcher<E>> matchers) {
		this.matchers = matchers;
	}

	
	public FilterLogicalMatcher(FilterControl<E> matcherControl, List<Filter> filters) {
		this.matchers = new ArrayList<FilterMatcher<E>>();
		for (Filter filter : filters){
			this.matchers.add(new FilterMatcher<E>(matcherControl, filter));
		}
	}
	
	@Override
	public boolean matches(E item) {
		boolean bOr = false;
		boolean bAnyOrs = false;
		for (FilterMatcher<E> matcher : matchers){
			if (!matcher.isEmpty()){
				boolean matches = matcher.matches(item);
				if (matcher.isAnd()){ //And
					if (!matches){ //if just one don't match, none match
						return false;
					}
				} else { //Or
					bAnyOrs = true;
					if (matches){ //if just one is true all is true
						bOr = true;
					}
				}
			}
		}
		return (bOr || !bAnyOrs);
	}
}
