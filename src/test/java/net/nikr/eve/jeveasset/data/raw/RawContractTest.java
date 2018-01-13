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
package net.nikr.eve.jeveasset.data.raw;

import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractAvailability;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractStatus;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractType;
import net.troja.eve.esi.model.CharacterContractsResponse;
import net.troja.eve.esi.model.CorporationContractsResponse;
import org.junit.Test;


public class RawContractTest extends TestUtil {

	@Test
	public void rawContractTest() {
		RawUtil.compare(RawContract.class, CharacterContractsResponse.class);
		RawUtil.compare(ContractAvailability.values(), CharacterContractsResponse.AvailabilityEnum.values());
		RawUtil.compare(ContractStatus.values(), CharacterContractsResponse.StatusEnum.values());
		RawUtil.compare(ContractType.values(), CharacterContractsResponse.TypeEnum.values());
		RawUtil.compare(RawContract.class, CorporationContractsResponse.class);
		RawUtil.compare(ContractAvailability.values(), CorporationContractsResponse.AvailabilityEnum.values());
		RawUtil.compare(ContractStatus.values(), CorporationContractsResponse.StatusEnum.values());
		RawUtil.compare(ContractType.values(), CorporationContractsResponse.TypeEnum.values());
	}
}
