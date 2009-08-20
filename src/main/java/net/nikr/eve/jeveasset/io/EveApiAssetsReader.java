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
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.log.Log;
import org.xml.sax.SAXException;


public class EveApiAssetsReader {

	private EveApiAssetsReader() {}

	public static boolean load(Program program, Human human){
		return load(program, human, false);
	}
	
	private static boolean load(Program program, Human human, boolean bCorp){
		if (human.isAssetsUpdatable() || bCorp){
			if (human.isUpdateCorporationAssets() && !bCorp){
				load(program, human, true);
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
						human.setAssets( assetsToEveAssets(program.getSettings(), human, assets, bCorp) );
					} else { //Add to assets (if we just parsed the corp asset, so they are not overwriten)
						human.getAssets().addAll( assetsToEveAssets(program.getSettings(), human, assets, bCorp) );
					}
					if (bCorp) {
						Log.info("Updated corporation assets for: "+human.getCorporation()+" by "+human.getName());
					} else {
						Log.info("Updated assets for: "+human.getName());
					}
				} else {
					ApiError error = assetResponse.getError();
					//Not Director or CEO
					if (error.getCode() == 209){ 
						human.setUpdateCorporationAssets(false);
						if (bCorp) {
							Log.info("Failed to update corporation assets for: "+human.getCorporation()+" by "+human.getName()+" (API ERROR: code: "+error.getCode()+" :: "+error.getError()+")");
						} else {
							Log.info("Failed to update assets for: "+human.getName()+" (API ERROR: code: "+error.getCode()+" :: "+error.getError()+")");
						}
						return false;
					}
					if (bCorp) {
						Log.warning("Failed to update corporation assets for: "+human.getCorporation()+" by "+human.getName()+" (API ERROR: code: "+error.getCode()+" :: "+error.getError()+")");
					} else {
						Log.warning("Failed to update assets for: "+human.getName()+" (API ERROR: code: "+error.getCode()+" :: "+error.getError()+")");
					}
					return false;
				}
			} catch (IOException ex) {
				if (bCorp) {
					Log.info("Failed to update corporation assets for: "+human.getCorporation()+" by "+human.getName()+" (NOT FOUND)");
				} else {
					Log.info("Failed to update assets for: "+human.getName()+" (NOT FOUND)");
				}
				return false;
			} catch (SAXException ex) {
				if (bCorp) {
					Log.error("Failed to update corporation assets for: "+human.getCorporation()+" by "+human.getName()+" (PARSER ERROR)", ex);
				} else {
					Log.error("Failed to update assets for: "+human.getName()+" (PARSER ERROR)", ex);
				}
				return false;
			}
			return true;
		} else {
			if (bCorp) {
				Log.info("Failed to update corporation assets for: "+human.getCorporation()+" by "+human.getName()+" (NOT ALLOWED YET)");
			} else {
				Log.info("Failed to update assets for: "+human.getName()+" (NOT ALLOWED YET)");
			}
			return false;
		}
	}

	private static List<EveAsset> assetsToEveAssets(Settings setttings, Human human, List<ApiAsset> assets, boolean bCorp){
		List<EveAsset> eveAssets = new Vector<EveAsset>();
		convertAll(setttings, human, assets, eveAssets, null, bCorp);
		return eveAssets;
	}
	private static void convertAll(Settings setttings, Human human, List<ApiAsset> assets, List<EveAsset> eveAssets, EveAsset parentEveAsset, boolean bCorp){
		for (int a = 0; a < assets.size(); a++){
			ApiAsset asset = assets.get(a);
			EveAsset eveAsset = assetToEveAsset(setttings, human, asset, parentEveAsset, bCorp);
			if (parentEveAsset == null){
				eveAssets.add(eveAsset);
			} else {
				parentEveAsset.addEveAsset(eveAsset);
			}
			convertAll(setttings, human, new Vector<ApiAsset>(asset.getAssets()), eveAssets, eveAsset, bCorp);
		}
	}
	private static EveAsset assetToEveAsset(Settings settings, Human human, ApiAsset apiAsset, EveAsset parentEveAsset, boolean bCorp){
		String name = AssetConverter.name(apiAsset.getTypeID(), settings); //OK
		String group = AssetConverter.group(apiAsset.getTypeID(), settings); //OK
		String category = AssetConverter.category(apiAsset.getTypeID(), settings); //OK
		String owner = AssetConverter.owner(human, bCorp); //Semi-OK (Fix not confirmed)
		long count = apiAsset.getQuantity(); //OK
		String location = AssetConverter.location(apiAsset.getLocationID(), parentEveAsset, settings); //NOT OKAY!
		String container = AssetConverter.container(apiAsset.getLocationID(), parentEveAsset); //Should be okay
		String flag = AssetConverter.flag(apiAsset.getFlag()); //should be okay
		double basePrice = AssetConverter.priceBase(apiAsset.getTypeID(), settings); //OK
		String meta = AssetConverter.meta(apiAsset.getTypeID(), settings); //OK - but some is missiong from data export
		boolean marketGroup = AssetConverter.marketGroup(apiAsset.getTypeID(), settings); //OK
		float volume = AssetConverter.volume(apiAsset.getTypeID(), settings);
		String region = AssetConverter.region(apiAsset.getLocationID(), parentEveAsset, settings);
		int id = apiAsset.getItemID(); //OK
		int typeID = apiAsset.getTypeID(); //OK
		boolean corporationAsset = bCorp; //Semi-OK - OLD: (owner.equals(human.getCorporation()));
		boolean singleton  = (apiAsset.getSingleton() > 0);
		String security = AssetConverter.security(apiAsset.getLocationID(), parentEveAsset, settings); //NOT OKAY!
		
		return new EveAsset(name, group, category, owner, count, location, container, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, apiAsset.getLocationID(), singleton, security);
	}

	

}
