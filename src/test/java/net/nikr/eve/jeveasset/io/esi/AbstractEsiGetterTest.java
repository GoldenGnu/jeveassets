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

import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.TestUtil;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.api.UniverseApi;
import static org.junit.Assert.*;
import org.junit.Test;


public class AbstractEsiGetterTest extends TestUtil {

	private final String DATASOURCE = "tranquility";

	@Test
	public void universeApiStructures() {
		UniverseApi api = new UniverseApi();
		try {
			api.getUniverseStructuresStructureId(1L, DATASOURCE, null, null, null);
		} catch (ApiException ex) {
			
		}
		validate(api.getApiClient());
	}

	private void validate(ApiClient client) {
		Map<String, List<String>> responseHeaders = client.getResponseHeaders();
		if (responseHeaders != null) {
			for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
				if (entry.getKey().toLowerCase().equals("warning")) {
					fail(entry.getValue().get(0));
				}
			}
		} else {
			fail("No headers");
		}
	}
	
}
