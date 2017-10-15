/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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

import net.nikr.eve.jeveasset.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;


public class EsiScopesTest extends TestUtil {
	
	public EsiScopesTest() {
	}

	@Test
	public void testLocalhost() {
		String s = 
		"    esi-wallet.read_character_wallet.v1\n" +
		"    esi-universe.read_structures.v1\n" +
		"    esi-assets.read_assets.v1\n" +
		"    esi-industry.read_character_jobs.v1\n" +
		"    esi-markets.read_character_orders.v1\n" +
		"    esi-characters.read_blueprints.v1\n" +
		"    esi-characters.read_corporation_roles.v1\n" +
		"    esi-contracts.read_character_contracts.v1\n" +
		"    esi-wallet.read_corporation_wallets.v1\n" +
		"    esi-corporations.read_divisions.v1\n" +
		"    esi-assets.read_corporation_assets.v1\n" +
		"    esi-corporations.read_blueprints.v1\n" +
		"    esi-contracts.read_corporation_contracts.v1\n" +
		"    esi-industry.read_corporation_jobs.v1\n" +
		"    esi-markets.read_corporation_orders.v1";
		for (EsiScopes scope : EsiScopes.values()) {
			assertTrue(scope.getScope() + " not included", s.contains(scope.getScope()));
		}
	}

	@Test
	public void testNiKR() {
		String s = 
		"    esi-wallet.read_character_wallet.v1\n" +
		"    esi-universe.read_structures.v1\n" +
		"    esi-assets.read_assets.v1\n" +
		"    esi-industry.read_character_jobs.v1\n" +
		"    esi-markets.read_character_orders.v1\n" +
		"    esi-characters.read_blueprints.v1\n" +
		"    esi-characters.read_corporation_roles.v1\n" +
		"    esi-contracts.read_character_contracts.v1\n" +
		"    esi-wallet.read_corporation_wallets.v1\n" +
		"    esi-corporations.read_divisions.v1\n" +
		"    esi-assets.read_corporation_assets.v1\n" +
		"    esi-corporations.read_blueprints.v1\n" +
		"    esi-contracts.read_corporation_contracts.v1\n" +
		"    esi-industry.read_corporation_jobs.v1\n" +
		"    esi-markets.read_corporation_orders.v1";
		for (EsiScopes scope : EsiScopes.values()) {
			assertTrue(scope.getScope() + " not included", s.contains(scope.getScope()));
		}
	}
	
}
