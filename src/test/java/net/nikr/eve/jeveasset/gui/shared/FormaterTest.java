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
package net.nikr.eve.jeveasset.gui.shared;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;


public class FormaterTest {

	private final Date DATE = Formater.columnStringToDate("2000-01-01 12:00");
	private final int THREADS = 100;

	@Test
	public void testThreadSafe() throws InterruptedException, ExecutionException {
		List<Callable<Void>> threads = new ArrayList<Callable<Void>>();
		for (int i = 0; i < THREADS; i++) {
			threads.add(new ParseDate());
			threads.add(new FormatDate());
			threads.add(new FormatNumber());
		}
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(THREADS * 2);
		List<Future<Void>> futures = newFixedThreadPool.invokeAll(threads);
		for (Future<Void> future : futures) {
			future.get();
		}
	}

	private class ParseDate implements Callable<Void> {
		@Override
		public Void call() throws Exception {
			Formater.columnStringToDate("2000-01-01 12:00");
			return null;
		}
	}

	private class FormatDate implements Callable<Void> {
		@Override
		public Void call() throws Exception {
			Formater.columnDate(DATE);
			return null;
		}
	}

	private class FormatNumber implements Callable<Void> {
		@Override
		public Void call() throws Exception {
			Formater.iskFormat(123456789.1234);
			return null;
		}
	}

}
