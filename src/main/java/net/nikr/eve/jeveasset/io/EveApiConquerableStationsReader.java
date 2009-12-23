/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.io;

import com.beimin.eveapi.ApiError;
import com.beimin.eveapi.utils.stationlist.Parser;
import com.beimin.eveapi.utils.stationlist.Response;
import java.io.IOException;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.log.Log;
import org.xml.sax.SAXException;


public class EveApiConquerableStationsReader {
	public static boolean load(Settings settings){
		Log.info("Conquerable stations updating:");
		if (settings.isUpdatable(settings.getConquerableStationsNextUpdate()) || settings.getConquerableStations().isEmpty()){
			Parser stationParser = new Parser();
			stationParser.setCachingEnabled(true);
			Response stationResponse = null;
			try {
				stationResponse = stationParser.getStationList();
				settings.setConquerableStationsNextUpdate(stationResponse.getCachedUntil());
				if (!stationResponse.hasError()){
					settings.setConquerableStations( stationResponse.getStations() );
					
				} else {
					ApiError error = stationResponse.getError();
					Log.info("	Conquerable stations update failed (API ERROR: code: "+error.getCode()+" :: "+error.getError()+")");
				}
			} catch (IOException ex) {
				Log.info("	Conquerable stations update failed (NOT FOUND)");
				return false;
			} catch (SAXException ex) {
				Log.error("	Conquerable stations update failed (PARSER ERROR)", ex);
			}
		}
		Log.info("	Conquerable stations updated");
		LocalConquerableStationsWriter.save(settings);
		return true;
	}
}
