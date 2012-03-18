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

package net.nikr.eve.jeveasset.gui.tabs.routing.mocks;

import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.tests.mocks.FakeProgram;
import net.nikr.eve.jeveasset.tests.mocks.FakeSettings;

/**
 *
 * @author Candle
 */
public class RoutingMockProgram extends FakeProgram {

	FakeSettings fakeSettings;

	public RoutingMockProgram(RoutingMockSettings routingMockSettings) {
		this.fakeSettings = routingMockSettings;
	}

	@Override
	public Settings getSettings() {
		if (fakeSettings == null) {
			fakeSettings = new RoutingMockSettings();
		}
		return fakeSettings;
	}
}
