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

import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor.Listener;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.Asset;


public class EveAssetLogicalMatcherEditor extends AbstractMatcherEditor<Asset> 
											implements Listener<Asset>{

	private final List<EveAssetMatcherEditor> matcherEditors = new ArrayList<EveAssetMatcherEditor>();
	
	public EveAssetLogicalMatcherEditor() {}
	
	public void add(EveAssetMatcherEditor eveAssetMatcherEditor){
		if (matcherEditors.contains(eveAssetMatcherEditor)){
			matcherEditors.remove(eveAssetMatcherEditor);
			eveAssetMatcherEditor.removeMatcherEditorListener(this);
		}
		matcherEditors.add(eveAssetMatcherEditor);
		eveAssetMatcherEditor.addMatcherEditorListener(this);
		this.fireChanged(new EveAssetLogicalMatcher(matcherEditors));
	}
	public void remove(EveAssetMatcherEditor eveAssetMatcherEditor){
		matcherEditors.remove(eveAssetMatcherEditor);
		eveAssetMatcherEditor.removeMatcherEditorListener(this);
		this.fireChanged(new EveAssetLogicalMatcher(matcherEditors));
	}

	@Override
	public void changedMatcher(Event<Asset> matcherEvent) {
		this.fireChanged(new EveAssetLogicalMatcher(matcherEditors));
	}

	private static class EveAssetLogicalMatcher implements Matcher<Asset> {

		private List<EveAssetMatcherEditor> matcherEditors;

		public EveAssetLogicalMatcher(List<EveAssetMatcherEditor> matcherEditors) {
			this.matcherEditors = matcherEditors;
		}

		@Override
		public boolean matches(Asset item) {
			boolean bOR = false;
			boolean bAnyORs = false;
			for (EveAssetMatcherEditor eame : matcherEditors){
				if (!eame.isEmpty()){
					if (eame.isAnd()){ //And
						if (!eame.getMatcher().matches(item)){ //if just one don't match, none match
							return false;
						}
					} else { //Or
						bAnyORs = true;
						if (eame.getMatcher().matches(item)){ //if just one is true all is true
							bOR = true;
						}
					}
				}
			}
			
			//if any "Or" is true | if no "Or" is included | if just one "Or" it's considered as "And"
			return (bOR || !bAnyORs);
		}
	}
}
