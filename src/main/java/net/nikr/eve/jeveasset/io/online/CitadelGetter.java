/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.CitadelSettings;
import net.nikr.eve.jeveasset.data.settings.ZKillStructure;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.io.local.CitadelReader;
import net.nikr.eve.jeveasset.io.local.CitadelWriter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CitadelGetter {

	private static final Logger LOG = LoggerFactory.getLogger(CitadelGetter.class);

	protected static enum StructureHost {
		HAMMERTIME("https://stop.hammerti.me.uk/api/citadel/all"),
		NIKR("https://eve.nikr.net/jeveassets/citadel/"),
		ZKILL("https://zkillboard.com/api/structures.json"),;

		private final String url;

		private StructureHost(String url) {
			this.url = url;
		}

		public String getUrl() {
			return url;
		}
	}

	private static CitadelGetter citadelGetter;
	private CitadelSettings citadelSettings = new CitadelSettings();

	protected CitadelGetter() { }

	public synchronized static Citadel get(long locationID) {
		return getCitadelGetter().getCitadel(locationID);
	}

	public synchronized static Iterable<Map.Entry<Long, Citadel>> getAll() {
		return getCitadelGetter().getCitadelAll();
	}

	public synchronized static void update(UpdateTask updateTask) {
		if (!getCitadelGetter().canUpdate(updateTask)) {
			return;
		}
		List<Exception> exceptions = new ArrayList<>();
		try {
			getCitadelGetter().updateCache(updateTask, StructureHost.ZKILL, getCitadelGetter().citadelSettings.getZKillETag(), 0, 50, new TypeToken<Map<Long, ZKillStructure>>() {}, new Setter<ZKillStructure>() {
				@Override
				public void setETag(CitadelSettings citadelSettings, String eTag) {
					citadelSettings.setZKillETag(eTag);
				}
				@Override
				public void setData(CitadelSettings citadelSettings, Map<Long, ZKillStructure> results) {
					for (Map.Entry<Long, ZKillStructure> entry : results.entrySet()) {
						MyLocation system = ApiIdConverter.getLocation(entry.getValue().getSystemID());
						citadelSettings.put(entry.getKey(), new Citadel(entry.getKey(), entry.getValue(), system));
					}
				}
			});
		} catch (IOException | JsonParseException ex) {
			LOG.error("	ZKill Structures failed to update", ex);
			if (updateTask != null) {
				updateTask.addError("zKillboard > " + DialoguesUpdate.get().structures(), ex.getMessage());
			}
		}
		try {
			//Get the cached version
			boolean updated = getCitadelGetter().updateCache(updateTask, StructureHost.NIKR, null, 50, 100, new TypeToken<Map<Long, Citadel>>() {}, new Setter<Citadel>() {
				@Override
				public void setETag(CitadelSettings citadelSettings, String eTag) { }
				@Override
				public void setData(CitadelSettings citadelSettings, Map<Long, Citadel> results) {
					//Updated OK
					for (Map.Entry<Long, Citadel> entry : results.entrySet()) {
						entry.getValue().setID(entry.getKey()); //Update locationID
						citadelSettings.put(entry.getKey(), entry.getValue());
					}
				}
			});
			if (updated) {
				return;
			}
		} catch (IOException | JsonParseException ex) {
			LOG.error("	Citadels failed to update", ex);
			exceptions.add(ex);
		}
		try {
			//Get it from the source
			boolean updated = getCitadelGetter().updateCache(updateTask, StructureHost.HAMMERTIME, null, 50, 100, new TypeToken<Map<Long, Citadel>>() {}, new Setter<Citadel>() {
				@Override
				public void setETag(CitadelSettings citadelSettings, String eTag) { }
				@Override
				public void setData(CitadelSettings citadelSettings, Map<Long, Citadel> results) {
					//Updated OK
					for (Map.Entry<Long, Citadel> entry : results.entrySet()) {
						entry.getValue().setID(entry.getKey()); //Update locationID
						citadelSettings.put(entry.getKey(), entry.getValue());
					}
				}
			});
			if (updated) {
				return;
			}
		} catch (IOException | JsonParseException ex) {
			LOG.error("	Citadels failed to update", ex);
			exceptions.add(ex);
		}
		for (Exception ex : exceptions) {
			if (updateTask != null) {
				updateTask.addError("HammerTime > " + DialoguesUpdate.get().structures(), ex.getMessage());
			}
		}
	}

	public synchronized static void remove(long locationID) {
		getCitadelGetter().removeCitadel(locationID);
	}

	public synchronized static void remove(Set<Long> locationIDs) {
		getCitadelGetter().removeCitadels(locationIDs);
	}

	public synchronized static void set(Citadel citadel) {
		getCitadelGetter().setCitadel(citadel);
	}

	public synchronized static void set(Collection<Citadel> citadels) {
		getCitadelGetter().setCitadels(citadels);
	}

	private static CitadelGetter getCitadelGetter() {
		if (citadelGetter == null) {
			citadelGetter = new CitadelGetter();
			citadelGetter.loadXml();
		}
		return citadelGetter;
	}

	private void saveXml() {
		CitadelWriter.save(citadelSettings);
	}

	private void loadXml() {
		citadelSettings = CitadelReader.load();
	}

	protected boolean canUpdate(UpdateTask updateTask) {
		if (citadelSettings.getNextUpdate().after(new Date()) && !Program.isForceUpdate()) { //Check if we can update now
			if (updateTask != null) {
				updateTask.addWarning(DialoguesUpdate.get().structures(), "Update skipped: Waiting for cache to expire.\r\n(Updatable again after cache expires)");
			}
			LOG.info("	Citadels failed to update (NOT ALLOWED YET)");
			return false;
		}
		return true;
	}

	protected <T> boolean  updateCache(UpdateTask updateTask, StructureHost structureHost, String eTag, int progressStart, int progressEnd, TypeToken<Map<Long, T>> type, Setter<T> setter) throws IOException, JsonParseException {
		LOG.info("Citadels updating from: " + structureHost.getUrl());
		//Update citadel
		InputStream in = null;
		GZIPInputStream gZipIn = null;
		UpdateTaskInputStream updateTaskIn = null;
		InputStreamReader reader = null;
		try { //Update from API
			URL url = new URL(structureHost.getUrl());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Accept-Encoding", "gzip");
			if (eTag != null) {
				con.setRequestProperty("If-None-Match", eTag);
			}
			long contentLength = con.getContentLengthLong();
			String contentEncoding = con.getContentEncoding();
			in = con.getInputStream();
			InputStream temp;
			if ("gzip".equals(contentEncoding)) {
				gZipIn = new GZIPInputStream(in);
				temp = gZipIn;
			} else {
				temp = in;
			}
			updateTaskIn = new UpdateTaskInputStream(temp, contentLength, updateTask, progressStart, progressEnd);
			reader = new InputStreamReader(updateTaskIn);
			Gson gson = new GsonBuilder().create();
			Map<Long, T> results = gson.fromJson(reader, type.getType());
			if (results == null) { 
				return false;
			}
			//Updated OK
			eTag = con.getHeaderField("etag");
			if (eTag != null) {
				setter.setETag(citadelSettings, eTag);
			}
			setter.setData(citadelSettings, results);
			citadelSettings.setNextUpdate();
			saveXml();
			LOG.info("	Updated citadels for jEveAssets");
			return true;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					//No problem...
				}
			}
			if (gZipIn != null) {
				try {
					gZipIn.close();
				} catch (IOException ex) {
					//No problem...
				}
			}
			if (updateTaskIn != null) {
				try {
					updateTaskIn.close();
				} catch (IOException ex) {
					//No problem...
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					//No problem...
				}
			}
		}
	}

	protected static interface Setter<T> {
		public void setETag(CitadelSettings citadelSettings, String eTag);
		public void setData(CitadelSettings citadelSettings, Map<Long, T> results);
	}

	private void removeCitadel(long locationID) {
		citadelSettings.remove(locationID);
		saveXml();
	}

	private void removeCitadels(Set<Long> locationIDs) {
		for (long locationID : locationIDs) {
			citadelSettings.remove(locationID);
		}
		if (!locationIDs.isEmpty()) {
			saveXml();
		}
	}

	private void setCitadel(Citadel citadel) {
		citadelSettings.put(citadel.id, citadel);
		saveXml();
	}

	private void setCitadels(Collection<Citadel> citadels) {
		for (Citadel citadel : citadels) {
			citadelSettings.put(citadel.id, citadel);
		}
		if (!citadels.isEmpty()) {
			saveXml();
		}
	}

	private Citadel getCitadel(long locationID) {
		Citadel citadel = citadelSettings.get(locationID);
		if (citadel == null) { //Location not found in cache -> add placeholder for future updates
			citadel = new Citadel(locationID);
		}
		return citadel;
	}

	private Iterable<Map.Entry<Long, Citadel>> getCitadelAll() {
		return citadelSettings.getCache();
	}
}
