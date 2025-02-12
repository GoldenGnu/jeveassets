/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import org.junit.Test;


public class ApiIdConverterOnlineTest extends TestUtil {

	private static final long MAX_IDS = 500;
	private static final long MAX_RUNS = 100;

	@Test
	public void testDownloadItemsThreads() throws InterruptedException {
		testDownloadItemsThreads(false);
	}

	@Test
	public void testUpdateItem() throws InterruptedException {
		testDownloadItemsThreads(true);
	}

	public void testDownloadItemsThreads(boolean updateItems) throws InterruptedException {
		ApiIdConverter.setUpdateItem(true);
		Set<Integer> typeIDs = new HashSet<>();
		List<Integer> all = new ArrayList<>(StaticData.get().getItems().keySet());
		if (MAX_IDS > 0) {
			Collections.shuffle(all); //Randomize
			typeIDs.addAll(all.subList(0, (int)Math.min(MAX_RUNS, all.size())));
		} else {
			typeIDs.addAll(all);
		}
		List<Thread> updates = new ArrayList<>();
		Map<Integer, Item> removed = new HashMap<>();
		for (Integer typeID : typeIDs) {
			removed.put(typeID, StaticData.get().getItems().remove(typeID));
		}
		if (updateItems) {
			for (Integer typeID : typeIDs) {
				updates.add(new UpdateItem(typeID));
			}
		}
		for (int i= 0; i < MAX_RUNS; i++ ) {
			for (Integer typeID : typeIDs) {
				updates.add(new Download(typeID));
			}
		}
		for (Thread thread : updates) {
			thread.start();
		}
		for (Thread thread : updates) {
			thread.join();
		}
		for (Map.Entry<Integer, Item> entry : removed.entrySet()) {
			StaticData.get().getItems().put(entry.getKey(), entry.getValue());
		}
		ApiIdConverter.setUpdateItem(false);
	}

	private static class Download extends Thread {

		private final int typeID;

		public Download(int typeID) {
			this.typeID = typeID;
		}

		@Override
		public void run() {
			ApiIdConverter.synchronizedDownloadItem(typeID);
		}
	}

	private static class UpdateItem extends Thread {

		private final int typeID;

		public UpdateItem(int typeID) {
			this.typeID = typeID;
		}

		@Override
		public void run() {
			ApiIdConverter.updateItem(typeID);
		}
	}
}
