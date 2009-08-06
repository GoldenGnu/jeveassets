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
	private static EveAsset assetToEveAsset(Settings setttings, Human human, ApiAsset asset, EveAsset parentEveAsset, boolean bCorp){
		String name = EveAsset.calcName(asset.getTypeID(), setttings); //OK
		String group = EveAsset.calcGroup(asset.getTypeID(), setttings); //OK
		String category = EveAsset.calcCategory(asset.getTypeID(), setttings); //OK
		String owner = EveAsset.calcOwner(human, bCorp); //Semi-OK (Fix not confirmed)
		long count = asset.getQuantity(); //OK
		String location = EveAsset.calcLocation(asset.getLocationID(), parentEveAsset, setttings); //NOT OKAY!
		String container = EveAsset.calcContainer(asset.getLocationID(), parentEveAsset); //Should be okay
		String flag = EveAsset.calcFlag(asset.getFlag()); //should be okay
		double basePrice = EveAsset.calcPrice(asset.getTypeID(), setttings); //OK
		String meta = EveAsset.calcMeta(asset.getTypeID(), setttings); //OK - but some is missiong from data export
		boolean marketGroup = EveAsset.calcMarketGroup(asset.getTypeID(), setttings); //OK
		float volume = EveAsset.calcVolume(asset.getTypeID(), setttings);
		String region = EveAsset.calcRegion(asset.getLocationID(), parentEveAsset, setttings);
		int id = asset.getItemID(); //OK
		int typeID = asset.getTypeID(); //OK
		boolean corporationAsset = bCorp; //Semi-OK - OLD: (owner.equals(human.getCorporation()));
		boolean singleton  = (asset.getSingleton() > 0);
		return new EveAsset(parentEveAsset, name, group, category, owner, count, location, container, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, asset.getLocationID(), singleton);
	}
	

}
