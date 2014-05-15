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

package net.nikr.eve.jeveasset.gui.tabs.routing;

import ca.odell.glazedlists.TextFilterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.SolarSystem;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JAutoCompleteDialog;
import net.nikr.eve.jeveasset.i18n.TabsRouting;
import uk.me.candle.eve.graph.Node;


public class JSystemDialog extends JAutoCompleteDialog<SolarSystem> {

	public JSystemDialog(Program program) {
		super(program, TabsRouting.get().addSystemTitle(), Images.TOOL_ROUTING.getImage(), TabsRouting.get().addSystemSelect(), true, true);
	}

	public void updateData(Set<Node> nodes) {
		List<SolarSystem> systems = new ArrayList<SolarSystem>();
		for (Node node : nodes) {
			if (node instanceof SolarSystem) {
				systems.add((SolarSystem) node);
			}
		}
		super.updateData(systems);
	}

	@Override
	protected SolarSystem getValue(Object object) {
		if (object instanceof SolarSystem) {
			return (SolarSystem) object;
		} else {
			return null;
		}
	}

	@Override
	protected Comparator<SolarSystem> getComparator() {
		return new SystemComparator();
	}

	@Override
	protected TextFilterator<SolarSystem> getFilterator() {
		return new Filterator();
	}

	private static class Filterator implements TextFilterator<SolarSystem> {
		@Override
		public void getFilterStrings(final List<String> baseList, final SolarSystem element) {
			baseList.add(element.getName());
		}
	}

	private static class SystemComparator implements Comparator<SolarSystem> {
		@Override
		public int compare(SolarSystem o1, SolarSystem o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	}
}
