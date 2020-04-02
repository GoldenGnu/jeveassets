/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.sde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.io.local.FlagsReader;
import static org.junit.Assert.assertFalse;
import org.junit.Test;


public class StaticDataTest extends TestUtil {

	private static final int RUNS = 1000;

	@Test
	public void testFlagsWorkaround() throws InterruptedException, ExecutionException {
		Map<Integer, ItemFlag> flags = new HashMap<>();
		FlagsReader.load(flags);
		assertFalse(flags.containsKey(179));
	}

	@Test
	public void testLocationsThreadSafty() throws InterruptedException, ExecutionException {
		StaticData.load();
		List<SwingWorker<Void, Void>> threads = new ArrayList<>();
		for (int i = 0; i < RUNS; i++) {
			threads.add(new ReadThread());
			threads.add(new WriteThread());
		}
		for (SwingWorker<Void, Void> thread : threads) {
			thread.execute();
		}
		for (SwingWorker<Void, Void> thread : threads) {
			thread.get();
		}
	}

	private static class ReadThread extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			for (MyLocation location : StaticData.get().getLocations()) {
				StaticData.get().getLocation(location.getLocationID());
			}
			return null;
		}

	}

	private static class WriteThread extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			for (MyLocation location : StaticData.get().getLocations()) {
				StaticData.get().removeLocation(location.getLocationID());
				StaticData.get().addLocation(location);
			}
			return null;
		}

	}
}
