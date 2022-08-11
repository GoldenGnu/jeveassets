/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import java.util.Comparator;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JAutoCompleteDialog;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels.StringFilterator;
import net.nikr.eve.jeveasset.i18n.TabsRouting;


public class JRouteSaveDialog extends JAutoCompleteDialog<String> {

	public JRouteSaveDialog(Program program) {
		super(program, TabsRouting.get().routeSaveTitle(), Images.TOOL_ROUTING.getImage(), TabsRouting.get().routeSaveMsg(), false);
	}

	@Override
	protected Comparator<String> getComparator() {
		return GlazedLists.comparableComparator();
	}

	@Override
	protected TextFilterator<String> getFilterator() {
		return new StringFilterator();
	}

	@Override
	protected String getValue(Object object) {
		if (object instanceof String) {
			return (String) object;
		} else {
			return null;
		}
	}

	@Override
	protected boolean isEmpty(String t) {
		return t.isEmpty();
	}

}
