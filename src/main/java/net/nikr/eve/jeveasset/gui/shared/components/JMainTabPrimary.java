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
package net.nikr.eve.jeveasset.gui.shared.components;

import java.util.Set;
import javax.swing.Icon;
import net.nikr.eve.jeveasset.Program;


public abstract class JMainTabPrimary extends JMainTab {

	public JMainTabPrimary(Program program, String title, Icon icon, boolean closeable) {
		super(program, title, icon, closeable);
	}

	@Override
	public void updateNames(Set<Long> itemIDs) { }

	@Override
	public void updateLocations(Set<Long> locationIDs) { }

	@Override
	public void updatePrices(Set<Integer> typeIDs) { }

	@Override
	public void updateData() { }

}
