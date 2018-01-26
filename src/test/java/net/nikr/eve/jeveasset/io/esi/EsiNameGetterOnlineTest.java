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
package net.nikr.eve.jeveasset.io.esi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.settings.Settings;
import org.junit.Assert;
import org.junit.Test;


public class EsiNameGetterOnlineTest extends TestUtil {

	@Test
	public void testEsi() {
		Set<Long> ids = new HashSet<Long>();
		ids.add(1232111352L);
		ids.add(2112730710L);
		//ids.add(500016L);
		ids.add(93678202L);
		ids.add(96503035L);

		List<OwnerType> owners = new ArrayList<OwnerType>();
		for (Long id : ids) {
			EsiOwner esiOwner = new EsiOwner();
			esiOwner.setOwnerID(id);
			owners.add(esiOwner);
		}

		Settings.get().getOwners().clear();

		EsiNameGetter esiNameGetter = new EsiNameGetter(null, owners);
		esiNameGetter.run();

		for (Long id : ids) {
			Assert.assertNotNull(Settings.get().getOwners().get(id));
			Assert.assertFalse(Settings.get().getOwners().get(id).isEmpty());
		}
	}

}
