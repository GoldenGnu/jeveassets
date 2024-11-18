/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import java.util.Comparator;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JAutoCompleteDialog;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels.ItemFilterator;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;


public class JReprocessedAddItemDialog extends JAutoCompleteDialog<Item> {

	public JReprocessedAddItemDialog(final Program program) {
		super(program, TabsReprocessed.get().addItem(), Images.TOOL_REPROCESSED.getImage(), TabsReprocessed.get().selectItem(), true);
	}

	@Override
	protected Comparator<Item> getComparator() {
		return GlazedLists.comparableComparator();
	}

	@Override
	protected TextFilterator<Item> getFilterator() {
		return new ItemFilterator();
	}

	@Override
	protected Item getValue(Object object) {
		if(object instanceof Item) {
			return (Item)object;
		}
		return null;
	}

	@Override
	protected boolean isEmpty(Item t) {
		return false;
	}

}
