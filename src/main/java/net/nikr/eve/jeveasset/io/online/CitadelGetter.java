/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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
import javax.swing.SwingUtilities;
import net.nikr.eve.jeveasset.data.Citadel;
import net.nikr.eve.jeveasset.data.CitadelSettings;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.io.local.CitadelReader;
import net.nikr.eve.jeveasset.io.local.CitadelWriter;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlWriter;
import net.nikr.eve.jeveasset.io.shared.UpdateTaskInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CitadelGetter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(CitadelGetter.class);

	private final String URL = "https://stop.hammerti.me.uk/api/citadel/all";

	private static CitadelGetter citadelGetter;
	private CitadelSettings citadelSettings = new CitadelSettings();

	private CitadelGetter() {
	}

	public static MyLocation get(long locationID) {
		return getCitadelGetter().load(locationID);
	}

	public static void update(UpdateTask updateTask) {
		getCitadelGetter().updateCache(updateTask);
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

	private void updateCache(UpdateTask updateTask) {
		LOG.info("Citadels updating:");
		if (citadelSettings.getNextUpdate().after(new Date())) { //Check if we can update now
			if (updateTask != null) {
				updateTask.addError(DialoguesUpdate.get().citadel(), "Not allowed yet.\r\n(Fix: Just wait a bit)");
			}
			LOG.info("	Citadels failed to update (NOT ALLOWED YET)");
			return;
		}
		//Update citadel
		InputStream in = null;
		try { //Update from API
			ObjectMapper mapper = new ObjectMapper(); //create once, reuse
			URL url = new URL(URL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Accept-Encoding", "gzip");

			int contentLength = con.getContentLength();
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
					citadelSettings.put(entry.getKey(), entry.getValue());
				}
			}
			citadelSettings.setNextUpdate();
			saveXml();
			LOG.info("	Updated citadels for jEveAssets");
		} catch (IOException ex) {
			if (updateTask != null) {
				updateTask.addError(DialoguesUpdate.get().citadel(), ex.getMessage());
			}
			LOG.error("	Citadels failed to update", ex);
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

	private MyLocation load(long locationID) {
		Citadel citadel = citadelSettings.get(locationID);
		if (citadel == null) { //Location not found in cache -> add placeholder for future updates
			citadel = new Citadel();
			citadelSettings.put(locationID, citadel);
			saveXml();
		}
		return citadel.getLocation(locationID);
	}
}
