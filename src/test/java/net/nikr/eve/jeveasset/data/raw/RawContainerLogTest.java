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

import net.nikr.eve.jeveasset.data.api.raw.RawContainerLog;
import net.troja.eve.esi.model.CorporationContainersLogsResponse;
import org.junit.Test;


public class RawContainerLogTest {

	@Test
	public void rawContainerLogTest() {
		RawUtil.compare(RawContainerLog.class, CorporationContainersLogsResponse.class);
		RawUtil.compare(RawContainerLog.ContainerAction.values(), CorporationContainersLogsResponse.ActionEnum.values());
		RawUtil.compare(RawContainerLog.ContainerPasswordType.values(), CorporationContainersLogsResponse.PasswordTypeEnum.values());
	}
}
