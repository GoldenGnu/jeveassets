/*
 * Copyright 2009, Niklas Kyster Rasmussen
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
import com.beimin.eveapi.corpsheet.Parser;
import com.beimin.eveapi.corpsheet.Response;
import java.io.IOException;
import java.util.Date;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.log.Log;
import org.xml.sax.SAXException;


public class EveApiCorporationReader {
	public static String load(Settings settings, Account account, int characterID, long corporationID){
		if (settings.getCorporations().containsKey(corporationID)){
			Log.info("		Corporation not updated (ALREADY DONE)");
			return settings.getCorporations().get(corporationID);
		}

		Date nextUpdate = Settings.getGmtNow();

		if (settings.getCorporationsNextUpdate().containsKey(corporationID)){
			nextUpdate = settings.getCorporationsNextUpdate().get(corporationID);
		}

		if (Settings.getGmtNow().after(nextUpdate) || Settings.getGmtNow().equals(nextUpdate)){
			Parser corporationParser = new Parser();
			corporationParser.setCachingEnabled(true);
			Response corporationResponse = null;
			try {
				corporationResponse = corporationParser.getCorporationSheet(Human.getApiAuthorization(account, characterID));
				settings.getCorporationsNextUpdate().put(corporationID, corporationResponse.getCachedUntil());
				if (!corporationResponse.hasError()){
					settings.getCorporations().put(corporationID, corporationResponse.getCorporationName());
					Log.info("		Corporation updated");
					return corporationResponse.getCorporationName();
				} else {
					ApiError error = corporationResponse.getError();
					Log.error("Corporation update failed (API ERROR: code: "+error.getCode()+" :: "+error.getError()+")");
				}
			} catch (IOException ex) {
				Log.info("Corporation update failed");
				return "";
			} catch (SAXException ex) {
				Log.error("Corporation update failed (PARSER ERROR)", ex);
			}
		} else {
			Log.info("		Corporation not updated (NOT ALLOWED YET)");
		}
		return "";
		
	}
}
