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

package net.nikr.eve.jeveasset.gui.shared.table;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import java.util.Comparator;
import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JAutoCompleteDialog;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class ViewSave extends JAutoCompleteDialog<View> {

	public ViewSave(Program program) {
		super(program, GuiShared.get().saveView(), Images.FILTER_SAVE.getImage(), GuiShared.get().saveViewMsg());
		this.setStrict(false);
	}

	@Override
	protected Comparator<View> getComparator() {
		return GlazedLists.comparableComparator();
	}

	@Override
	protected TextFilterator<View> getFilterator() {
		return new Filterator();
	}

	@Override
	protected View getValue(Object object) {
		if (object instanceof View) {
			return (View) object;
		} else if (object instanceof String) {
			return new View((String) object);
		} else {
			return null;
		}
	}

	private static class Filterator implements TextFilterator<View> {
		@Override
		public void getFilterStrings(final List<String> baseList, final View element) {
			baseList.add(element.getName());
		}
	}
	
}
