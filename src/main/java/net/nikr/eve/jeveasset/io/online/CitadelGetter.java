/*
 * Copyright 2009-2016 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
  * Original code from jWarframe (https://github.com/GoldenGnu/jwarframe)
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
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Citadel;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.io.local.CitadelReader;
import net.nikr.eve.jeveasset.io.local.CitadelWriter;
import net.nikr.eve.jeveasset.io.shared.AbstractXmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CitadelGetter extends AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(CitadelGetter.class);

	private final String URL = "https://stop.hammerti.me.uk/api/citadel/";

	private static CitadelGetter citadelGetter;
	private final Map<Long, Citadel> cache = new HashMap<>();

	private CitadelGetter() { }

	public static MyLocation load(long locationID) {
		return getCitadelGetter().parse(locationID);
	}

	private static CitadelGetter getCitadelGetter() {
		if (citadelGetter == null) {
			citadelGetter = new CitadelGetter();
			citadelGetter.loadXml();
		}
		return citadelGetter;
	}

	private void saveXml() {
		CitadelWriter.save(cache);
	}

	private void loadXml() {
		cache.putAll(CitadelReader.load());
	}

	private MyLocation parse(long locationID) {
		Citadel citadel = cache.get(locationID);
		if (citadel == null || citadel.getNextUpdate().before(new Date())) { //null or time to update
			try { //Update from API
				LOG.info("Updateing: " + URL+locationID);
				ObjectMapper mapper = new ObjectMapper(); //create once, reuse
				Map<Long, Citadel> results = mapper.readValue(new URL(URL+locationID), new TypeReference<Map<Long, Citadel>>() { } );
				if (!results.isEmpty() && results.get(locationID) != null) {
					citadel = results.get(locationID);
					cache.put(locationID, citadel);
					saveXml();
					return citadel.getLocation(locationID);
				}
			} catch (IOException ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (citadel == null) {
			citadel = new Citadel();
			cache.put(locationID, citadel);
			saveXml();
		}
		if (!citadel.isEmpty()) {
			return citadel.getLocation(locationID);
		} else { //Fallback 
			return new MyLocation(locationID, "[A citadel somewhere in space]", locationID, "[Unknown System]", locationID, "[Unknown Region]", "0.0");
		}
	}
}
