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
package net.nikr.eve.jeveasset.data.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;


public class TrackerDataTest extends TestUtil {

	@Test
	public void addAll() {
		Map<String, List<Value>> values;
		List<Value> list;
		String owner = "owner";
		Date date = new Date();
		Value oldValue = new Value("Old", date);
		Value newValue = new Value("New", date);
		TrackerData.set(Collections.singletonMap(owner, new ArrayList<>(Collections.singletonList(oldValue))));
		TrackerData.addAll(Collections.singletonMap(owner, new ArrayList<>(Collections.singletonList(newValue))), true);
		values = TrackerData.get();
		list = values.get(owner);
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(newValue.getName(), list.get(0).getName());
		TrackerData.set(Collections.emptyMap());

		TrackerData.set(Collections.singletonMap(owner, new ArrayList<>(Collections.singletonList(oldValue))));
		TrackerData.addAll(Collections.singletonMap(owner, new ArrayList<>(Collections.singletonList(newValue))), false);
		values = TrackerData.get();
		list = values.get(owner);
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(oldValue.getName(), list.get(0).getName());
		TrackerData.set(Collections.emptyMap());
	}
	
}
