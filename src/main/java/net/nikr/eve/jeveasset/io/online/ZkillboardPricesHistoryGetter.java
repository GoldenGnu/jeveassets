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
package net.nikr.eve.jeveasset.io.online;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.nikr.eve.jeveasset.Program;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ZkillboardPricesHistoryGetter {
	private static final Logger LOG = LoggerFactory.getLogger(ZkillboardPricesHistoryGetter.class);

	private static final long RATE_LIMIT_MS = 1010;
	private static final ExecutorService THREAD_POOL = Executors.newSingleThreadExecutor();
	private static final Gson GSON = new GsonBuilder().create();
	private static Long wait = null;
	private static Long ended = null;
	/**
	* HttpLoggingInterceptor
	*/
	private static final HttpLoggingInterceptor HTTP_LOGGING_INTERCEPTOR = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
									@Override
									public void log(String string) {
										LOG.debug(string);
									}
								});
	/**
	 * HTTP Client
	 */
	private final OkHttpClient client;

	private static ZkillboardPricesHistoryGetter getter;

	private ZkillboardPricesHistoryGetter() {
		if (LOG.isDebugEnabled()) {
			client = new OkHttpClient().newBuilder()
				.addNetworkInterceptor(HTTP_LOGGING_INTERCEPTOR)
				.build();
			HTTP_LOGGING_INTERCEPTOR.setLevel(HttpLoggingInterceptor.Level.BASIC);
		} else {
			 client = new OkHttpClient().newBuilder().build();
		}
	}

	public static Map<String, Double> getPriceHistory(int TypeID) {
		if (getter == null) {
			getter = new ZkillboardPricesHistoryGetter();
		}
		return getter.update(TypeID);
	}

	private Map<String, Double> update(int TypeID) {
		Future<Map<String, Double>> future = THREAD_POOL.submit(new PriceHistoryUpdate(TypeID));
		try {
			return future.get();
		} catch (InterruptedException ex) {
			LOG.error(ex.getMessage(), ex);
		} catch (ExecutionException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return null; //Failed
	}

	private class PriceHistoryUpdate implements Callable<Map<String, Double>> {

		private final int typeID;

		public PriceHistoryUpdate(int typeID) {
			this.typeID = typeID;
		}

		@Override
		public Map<String, Double> call() throws Exception {
			if (wait != null && ended != null && System.currentTimeMillis() < (ended + wait)) {
				LOG.info("Waiting: " + (wait) + "ms");
				Thread.sleep(wait);
			}
			long start = System.currentTimeMillis();
			Map<String, Double> results = null;
			try {
				results = GSON.fromJson(getCall(typeID).execute().body().string(), new TypeToken<Map<String, Double>>() {}.getType());
				if (results == null) {
					LOG.error("Error fetching price", new Exception("results is null"));
				}
			} catch (IllegalArgumentException | IOException | JsonParseException ex) {
				LOG.error("Error fetching price", ex);
			}
			long duration = System.currentTimeMillis() - start;
			LOG.info("Completed in " + duration + "ms");
			if (duration < RATE_LIMIT_MS) {
				wait = (RATE_LIMIT_MS - duration);
				ended = System.currentTimeMillis();
			} else {
				wait = null;
				ended = null;
			}
			if (results != null) {
				results.remove("typeID");
				results.remove("currentPrice");
			}
			return results;
		}

		public Call getCall(Integer typeID) {
			Request.Builder request = new Request.Builder()
					.url("https://zkillboard.com/api/prices/" + typeID+ "/")
					.addHeader("User-Agent", Program.PROGRAM_USER_AGENT);
			return client.newCall(request.build());
		}
	}
}
