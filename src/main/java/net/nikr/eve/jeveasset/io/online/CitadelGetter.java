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
package net.nikr.eve.jeveasset.io.online;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Citadel;
import net.nikr.eve.jeveasset.data.CitadelSettings;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.io.local.CitadelReader;
import net.nikr.eve.jeveasset.io.local.CitadelWriter;
import net.nikr.eve.jeveasset.io.local.AbstractXmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CitadelGetter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(CitadelGetter.class);

	private static final String HAMMERTI_URL = "https://stop.hammerti.me.uk/api/citadel/all";
	private static final String NIKR_URL = "https://eve.nikr.net/jeveassets/citadel/";

	private static CitadelGetter citadelGetter;
	private CitadelSettings citadelSettings = new CitadelSettings();

	private CitadelGetter() { }

	public static Citadel get(long locationID) {
		return getCitadelGetter().getCitadel(locationID);
	}

	public static void update(UpdateTask updateTask) {
		if (!getCitadelGetter().updateCache(updateTask, NIKR_URL)) { //Get the cached version
			getCitadelGetter().updateCache(updateTask, HAMMERTI_URL); //Get it from the source
		}
	}

	public static void set(Citadel citadel) {
		getCitadelGetter().setCitadel(citadel);
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

	private boolean updateCache(UpdateTask updateTask, String hostUrl) {
		LOG.info("Citadels updating from: " + hostUrl);
		if (citadelSettings.getNextUpdate().after(new Date()) && !Settings.get().isForceUpdate() && !Program.isForceUpdate()) { //Check if we can update now
			if (updateTask != null) {
				updateTask.addError(DialoguesUpdate.get().citadel(), "Not allowed yet.\r\n(Fix: Just wait a bit)");
			}
			LOG.info("	Citadels failed to update (NOT ALLOWED YET)");
			return false;
		}
		//Update citadel
		InputStream in = null;
		try { //Update from API
			ObjectMapper mapper = new ObjectMapper(); //create once, reuse
			URL url = new URL(hostUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Accept-Encoding", "gzip");

			long contentLength = con.getContentLengthLong();
			String contentEncoding = con.getContentEncoding();
			InputStream inputStream = new UpdateTaskInputStream(con.getInputStream(), contentLength, updateTask);
			if ("gzip".equals(contentEncoding)) {
				in = new GZIPInputStream(inputStream);
			} else {
				in = inputStream;
			}
			Map<Long, Citadel> results = mapper.readValue(in, new TypeReference<Map<Long, Citadel>>() {
			});
			if (results != null) { //Updated OK
				for (Map.Entry<Long, Citadel> entry : results.entrySet()) {
					entry.getValue().id = entry.getKey(); //Update locationID
					citadelSettings.put(entry.getKey(), entry.getValue());
				}
			}
			citadelSettings.setNextUpdate();
			saveXml();
			LOG.info("	Updated citadels for jEveAssets");
			return true;
		} catch (IOException ex) {
			if (updateTask != null) {
				updateTask.addError(DialoguesUpdate.get().citadel(), ex.getMessage());
			}
			LOG.error("	Citadels failed to update", ex);
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					//No problem...
				}
			}
		}
	}

	private void setCitadel(Citadel citadel) {
		citadelSettings.put(citadel.id, citadel);
		saveXml();
	}

	private Citadel getCitadel(long locationID) {
		Citadel citadel = citadelSettings.get(locationID);
		if (citadel == null) { //Location not found in cache -> add placeholder for future updates
			citadel = new Citadel();
			citadel.id = locationID; //Save locationID
			citadelSettings.put(locationID, citadel);
			saveXml();
		}
		return citadel;
	}
}
