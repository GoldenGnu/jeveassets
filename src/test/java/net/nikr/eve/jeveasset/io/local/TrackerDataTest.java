/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.local;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.gui.tabs.values.AssetValue;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;


public class TrackerDataTest extends TestUtil {

	private final String filename = "tracker.json";
	private final Date date = new Date(1552492124589L);

	@Test
	public void testEmpty() throws URISyntaxException {
		Map<String, List<Value>> out = new TreeMap<>();
		testWriteRead(out);
		testRead(out, "tracker_empty.json");
	}

	@Test
	public void testFilters() throws URISyntaxException {
		Map<String, List<Value>> out = new TreeMap<>();
		List<Value> values = new ArrayList<>();
		out.put("TEST-NAME", values);
		Value value = new Value(date);
		value.setContractCollateral(3);
		value.setContractValue(4);
		value.setEscrows(5);
		value.setEscrowsToCover(6);
		value.setManufacturing(7);
		value.setSellOrders(8);
		value.addAssets(AssetValue.create("location", "flag", 1000L), 9.0);
		value.addBalance("balence-id", 10);
		value.setSkillPoints(11);
		values.add(value);
		testWriteRead(out);
		testRead(out, "tracker_filters.json");
	}

	@Test
	public void testTotal() throws URISyntaxException {
		Map<String, List<Value>> out = new TreeMap<>();
		List<Value> values = new ArrayList<>();
		out.put("TEST-NAME", values);
		Value value = new Value(date);
		value.setAssetsTotal(1);
		value.setBalanceTotal(2);
		value.setContractCollateral(3);
		value.setContractValue(4);
		value.setEscrows(5);
		value.setEscrowsToCover(6);
		value.setManufacturing(7);
		value.setSellOrders(8);
		value.setSkillPoints(11);
		values.add(value);
		testWriteRead(out);
		testRead(out, "tracker_total.json");
	}

	private void testRead(final Map<String, List<Value>> out, String filename) {
		try {
			read(out, new File(TrackerDataTest.class.getResource("/" + filename).toURI()).getAbsolutePath());
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void testWriteRead(final Map<String, List<Value>> out) {
		TrackerDataWriter.save(filename, out, false);
		read(out, filename);
		File file = new File(filename);
		file.delete();
	}

	private void read(final Map<String, List<Value>> out, String filename) {
		final Map<String, List<Value>> in = TrackerDataReader.load(filename, false);
		assertThat(in.keySet(), equalTo(out.keySet()));
		for (String key : in.keySet()) {
			List<Value> outValues = out.get(key);
			List<Value> inValues = in.get(key);
			assertThat(inValues.getClass(), equalTo(outValues.getClass()));
			assertThat(inValues.size(), equalTo(outValues.size()));
			for (int i = 0; i < inValues.size(); i++) {
				Value outValue = outValues.get(i);
				Value inValue = inValues.get(i);
				//assertThat(inValue.hashCode(), equalTo(outValue.hashCode()));
				assertThat(inValue.getAssetsFilter(), equalTo(outValue.getAssetsFilter()));
				assertThat(inValue.getAssetsTotal(), equalTo(outValue.getAssetsTotal()));
				assertThat(inValue.getBalanceFilter(), equalTo(outValue.getBalanceFilter()));
				assertThat(inValue.getBalanceTotal(), equalTo(outValue.getBalanceTotal()));
				assertThat(inValue.getBestAssetValue(), equalTo(outValue.getBestAssetValue()));
				assertThat(inValue.getBestModuleValue(), equalTo(outValue.getBestModuleValue()));
				assertThat(inValue.getBestShipFittedValue(), equalTo(outValue.getBestShipFittedValue()));
				assertThat(inValue.getBestShipValue(), equalTo(outValue.getBestShipValue()));
				assertThat(inValue.getContractCollateral(), equalTo(outValue.getContractCollateral()));
				assertThat(inValue.getContractValue(), equalTo(outValue.getContractValue()));
				assertThat(inValue.getSkillPointValue(), equalTo(outValue.getSkillPointValue()));
				assertThat(inValue.getDate(), equalTo(outValue.getDate()));
				assertThat(inValue.getBestAssetName(), equalTo(outValue.getBestAssetName()));
				assertThat(inValue.getBestModuleName(), equalTo(outValue.getBestModuleName()));
				assertThat(inValue.getBestShipFittedName(), equalTo(outValue.getBestShipFittedName()));
				assertThat(inValue.getBestShipName(), equalTo(outValue.getBestShipName()));
				assertThat(inValue.getName(), equalTo(outValue.getName()));
				assertThat(inValue, equalTo(outValue));
			}
		}
		assertThat(in, equalTo(out));
	}
}
