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
package net.nikr.eve.jeveasset.io.esi;

import net.nikr.eve.jeveasset.TestUtil;
import static org.junit.Assert.*;
import org.junit.Test;


public class EsiScopesTest extends TestUtil {

	public EsiScopesTest() {
	}

	@Test
	public void testLocalhost() {
		String s = "esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-skills.read_skills.v1 esi-wallet.read_character_wallet.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-bookmarks.read_character_bookmarks.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-ui.open_window.v1 esi-ui.write_waypoint.v1 esi-markets.structure_markets.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-characters.read_corporation_roles.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-bookmarks.read_corporation_bookmarks.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-corporations.read_container_logs.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1 esi-planets.read_customs_offices.v1";
		for (EsiScopes scope : EsiScopes.values()) {
			assertTrue(scope.getScope() + " not included", s.contains(scope.getScope()));
		}
	}

	@Test
	public void testNiKR() {
		String s = "esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-skills.read_skills.v1 esi-wallet.read_character_wallet.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-bookmarks.read_character_bookmarks.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-ui.open_window.v1 esi-ui.write_waypoint.v1 esi-markets.structure_markets.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-characters.read_corporation_roles.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-bookmarks.read_corporation_bookmarks.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-corporations.read_container_logs.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1 esi-planets.read_customs_offices.v1";
		for (EsiScopes scope : EsiScopes.values()) {
			assertTrue(scope.getScope() + " not included", s.contains(scope.getScope()));
		}
	}

}
