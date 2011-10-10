/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.assets;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor.Event;
import ca.odell.glazedlists.matchers.MatcherEditor.Listener;
import net.nikr.eve.jeveasset.data.Asset;


public class EveAssetLogicalMatcherEditor extends AbstractMatcherEditor<Asset> implements Listener<Asset> {

	private EventList<EveAssetMatcherEditor> matcherEditors;

	public EveAssetLogicalMatcherEditor(EventList<EveAssetMatcherEditor> matcherEditors) {
		this.matcherEditors = matcherEditors;
		for (int a = 0; a < matcherEditors.size(); a++){
			matcherEditors.get(a).addMatcherEditorListener(this);
		}
		this.fireChanged(new EveAssetLogicalMatcher(matcherEditors));
	}

	@Override
	public void changedMatcher(Event<Asset> matcherEvent) {
		this.fireChanged(new EveAssetLogicalMatcher(matcherEditors));
	}

	private static class EveAssetLogicalMatcher implements Matcher<Asset> {

		private EventList<EveAssetMatcherEditor> matcherEditors;

		public EveAssetLogicalMatcher(EventList<EveAssetMatcherEditor> matcherEditors) {
			this.matcherEditors = matcherEditors;
		}

		@Override
		public boolean matches(Asset item) {
			boolean bOr = false;
			boolean bAnyOrs = false;
			int nOrs = 0;
			for (int a = 0; a < matcherEditors.size(); a++){
				EveAssetMatcherEditor eame = matcherEditors.get(a);
				if (!eame.isEmpty()){
					if (eame.isAnd()){ //And
						if (!eame.getMatcher().matches(item)){ //if just one don't match, none match
							return false;
						}
					} else { //Or
						nOrs++;
						bAnyOrs = true;
						if (eame.getMatcher().matches(item)){ //if just one is true all is true
							bOr = true;
						}
					}
				}
			}
			
			//if any "Or" is true, if no "Or" is included, if just one "Or" it's considered as "And"
			return (bOr || !bAnyOrs);

		}
	}
}
