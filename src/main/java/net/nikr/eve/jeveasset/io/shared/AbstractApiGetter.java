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

package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.ApiError;
import com.beimin.eveapi.ApiResponse;
import java.io.IOException;
import java.util.Date;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.log.Log;
import org.xml.sax.SAXException;


abstract public class AbstractApiGetter<T> {
	
	private String error;
	private boolean characterUpdated;
	private boolean corporationUpdated;

	protected void load(Date nextUpdate, boolean forceUpdate, boolean bCorp, String updateName, String characterName){
		error = null;
		if (bCorp){
			corporationUpdated = false;
		} else {
			characterUpdated = false;
		}
		if (isUpdatable(nextUpdate) || forceUpdate){
			try {
				T response = getResponse(bCorp);
				if (response instanceof ApiResponse){
					ApiResponse apiResponse = (ApiResponse)response;
					if (!apiResponse.hasError()){
						ok(response, bCorp);
						if (bCorp){
							corporationUpdated = true;
						} else {
							characterUpdated = true;
						}
						Log.info("	"+updateName+" update for: "+characterName);
					} else {
						ApiError apiError = apiResponse.getError();
						error = apiError.getError();
						Log.info("	"+updateName+" failed to update for: "+characterName+" (API ERROR: code: "+apiError.getCode()+" :: "+apiError.getError()+")");
					}
				}
			} catch (IOException ex) {
				Log.info("	"+updateName+" failed to update for: "+characterName+" (NOT FOUND)");
			} catch (SAXException ex) {
				Log.error("	"+updateName+" failed to update for: "+characterName+" (PARSER ERROR)", ex);
			}
		}

	}

	abstract protected T getResponse(boolean bCorp) throws IOException, SAXException;
	abstract protected void ok(T response, boolean bCorp);

	public boolean isUpdatable(Date date){
		return ( (
				Settings.getGmtNow().after(date)
				|| Settings.getGmtNow().equals(date)
				|| Program.FORCE_UPDATE
				//|| getApiProxy() != null
				)
				&& !Program.FORCE_NO_UPDATE);
	}

	public boolean isCharacterUpdated() {
		return characterUpdated;
	}

	public boolean isCorporationUpdated() {
		return corporationUpdated;
	}

	public String getError() {
		return error;
	}
	
}
