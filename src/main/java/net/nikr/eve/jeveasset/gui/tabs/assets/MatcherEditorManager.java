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
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;


public class MatcherEditorManager {

	private List<EveAssetMatcherEditor> tcmes;
	private FilterList<Asset> eveAssetTextFiltered;
	private Program program;

	public MatcherEditorManager(FilterList<Asset> eveAssetTextFiltered, Program program) {
		this.eveAssetTextFiltered = eveAssetTextFiltered;
		this.program = program;
		tcmes = new ArrayList<EveAssetMatcherEditor>();

	}
	
	public void add(EveAssetMatcherEditor eveAssetMatcherEditor){
		if (tcmes.contains(eveAssetMatcherEditor)) tcmes.remove(eveAssetMatcherEditor);
		tcmes.add(eveAssetMatcherEditor);
		update();
	}
	public void remove(EveAssetMatcherEditor eveAssetMatcherEditor){
		tcmes.remove(eveAssetMatcherEditor);
		update();
	}

	public void update(){
		EventList<EveAssetMatcherEditor> allTCMEs = GlazedLists.eventList(tcmes);
		EveAssetLogicalMatcherEditor cme = new EveAssetLogicalMatcherEditor(allTCMEs);
		cme.addMatcherEditorListener(program);
		eveAssetTextFiltered.setMatcherEditor(cme);
	}
}