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
import com.beimin.eveapi.asset.ApiAsset;
import com.beimin.eveapi.asset.Parser;
import com.beimin.eveapi.asset.Response;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.log.Log;
import org.xml.sax.SAXException;


public class EveApiAssetsReader {

	private static String error;

	private EveApiAssetsReader() {}

	public static boolean load(Settings settings, Human human){
		return load(settings, human, false);
	}
	
	private static boolean load(Settings settings, Human human, boolean bCorp){
		error = null;
		if (settings.isUpdatable(human.getAssetNextUpdate()) || bCorp){
			if (human.isUpdateCorporationAssets() && !bCorp){
				load(settings, human, true);
			}
			Parser assetParser = new Parser();
			Response assetResponse = null;
			try {
				assetResponse = assetParser.getAssets(Human.getApiAuthorization(human), bCorp);
				human.setAssetNextUpdate( assetResponse.getCachedUntil() );
				if (!assetResponse.hasError()){
					List<ApiAsset> assets = new Vector<ApiAsset>(assetResponse.getAssets());
					//overwrite assets (if we are parsing the corp asset or will not parse the corp assets)
					if (bCorp || !human.isUpdateCorporationAssets()){
						human.setAssets( AssetConverter.apiAsset(settings, human, assets, bCorp) );
					} else { //Add to assets (if we just parsed the corp asset, so they are not overwriten)
						human.getAssets().addAll( AssetConverter.apiAsset(settings, human, assets, bCorp) );
					}
					if (bCorp) {
						Log.info("	Updated corporation assets for: "+human.getCorporation()+" by "+human.getName());
					} else {
						Log.info("	Updated assets for: "+human.getName());
					}
					return true;
				} else {
					ApiError apiError = assetResponse.getError();
					error = apiError.getError();
					//Not Director or CEO
					if (apiError.getCode() == 209){
						human.setUpdateCorporationAssets(false);
						if (bCorp) {
							Log.info("	Failed to update corporation assets for: "+human.getCorporation()+" by "+human.getName()+" (API ERROR: code: "+apiError.getCode()+" :: "+apiError.getError()+")");
						} else {
							Log.info("	Failed to update assets for: "+human.getName()+" (API ERROR: code: "+apiError.getCode()+" :: "+apiError.getError()+")");
						}
					}
					if (bCorp) {
						Log.warning("	Failed to update corporation assets for: "+human.getCorporation()+" by "+human.getName()+" (API ERROR: code: "+apiError.getCode()+" :: "+apiError.getError()+")");
					} else {
						Log.warning("	Failed to update assets for: "+human.getName()+" (API ERROR: code: "+apiError.getCode()+" :: "+apiError.getError()+")");
					}
				}
			} catch (IOException ex) {
				if (bCorp) {
					Log.info("	Failed to update corporation assets for: "+human.getCorporation()+" by "+human.getName()+" (NOT FOUND)");
				} else {
					Log.info("	Failed to update assets for: "+human.getName()+" (NOT FOUND)");
				}
			} catch (SAXException ex) {
				if (bCorp) {
					Log.error("Failed to update corporation assets for: "+human.getCorporation()+" by "+human.getName()+" (PARSER ERROR)", ex);
				} else {
					Log.error("Failed to update assets for: "+human.getName()+" (PARSER ERROR)", ex);
				}
			}
		} else {
			if (bCorp) {
				Log.info("	Failed to update corporation assets for: "+human.getCorporation()+" by "+human.getName()+" (NOT ALLOWED YET)");
			} else {
				Log.info("	Failed to update assets for: "+human.getName()+" (NOT ALLOWED YET)");
			}
		}
		return false;
	}

	public static String getError() {
		return error;
	}
}
