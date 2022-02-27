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
package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import ca.odell.glazedlists.TextFilterator;
import java.util.Comparator;
import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JAutoCompleteDialog;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;


public class JReprocessedAddItemDialog extends JAutoCompleteDialog<Item> {

	public JReprocessedAddItemDialog(final Program program) {
		super(program, TabsReprocessed.get().addItem(), Images.TOOL_REPROCESSED.getImage(), 
				TabsReprocessed.get().selectItem(), false, true);
	}

	@Override
	protected Comparator<Item> getComparator() {
		return new ItemComparator();
	}

	@Override
	protected TextFilterator<Item> getFilterator() {
		return new Filterator();
	}

	@Override
	protected Item getValue(Object object) {
		if(object instanceof Item){
			return (Item)object;
		}
		return null;
	}

	private static class Filterator implements TextFilterator<Item> {
		@Override
		public void getFilterStrings(List<String> baseList, Item element) {
			baseList.add(element.getTypeName());
		}
	}

	private static class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item o1, Item o2) {
			return o1.compareTo(o2);
		}
	}
}
