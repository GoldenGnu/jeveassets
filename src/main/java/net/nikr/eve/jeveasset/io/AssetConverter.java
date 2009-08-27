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

import com.beimin.eveapi.asset.ApiAsset;
import com.beimin.eveapi.industry.ApiIndustryJob;
import com.beimin.eveapi.order.ApiMarketOrder;
import com.beimin.eveapi.utils.stationlist.ApiStation;
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Items;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.Settings;


public class AssetConverter {
	
	public static String flag(int theFlag) {
		switch (theFlag) {
			case 0:
				return "None";
			case 1:
				return "Wallet";
			case 2:
				return "Factory";
			case 4:
				return "Hangar";
			case 5:
				return "Cargo";
			case 6:
				return "Briefcase";
			case 7:
				return "Skill";
			case 8:
				return "Reward";
			case 9:
				return "Connected"; //Character in station connected
			case 10:
				return "Disconnected"; //Character in station offline
			case 11:
				return "LoSlot0"; //Low power slot 1
			case 12:
				return "LoSlot1"; //Low power slot 2
			case 13:
				return "LoSlot2"; //Low power slot 3
			case 14:
				return "LoSlot3"; //Low power slot 4
			case 15:
				return "LoSlot4"; //Low power slot 5
			case 16:
				return "LoSlot5"; //Low power slot 6
			case 17:
				return "LoSlot6"; //Low power slot 7
			case 18:
				return "LoSlot7"; //Low power slot 8
			case 19:
				return "MedSlot0"; //Medium power slot 1
			case 20:
				return "MedSlot1"; //Medium power slot 2
			case 21:
				return "MedSlot2"; //Medium power slot 3
			case 22:
				return "MedSlot3"; //Medium power slot 4
			case 23:
				return "MedSlot4"; //Medium power slot 5
			case 24:
				return "MedSlot5"; //Medium power slot 6
			case 25:
				return "MedSlot6"; //Medium power slot 7
			case 26:
				return "MedSlot7"; //Medium power slot 8
			case 27:
				return "HiSlot0"; //High power slot 1
			case 28:
				return "HiSlot1"; //High power slot 2
			case 29:
				return "HiSlot2"; //High power slot 3
			case 30:
				return "HiSlot3"; //High power slot 4
			case 31:
				return "HiSlot4"; //High power slot 5
			case 32:
				return "HiSlot5"; //High power slot 6
			case 33:
				return "HiSlot6"; //High power slot 7
			case 34:
				return "HiSlot7"; //High power slot 8
			case 35:
				return "Fixed Slot";
			case 56:
				return "Capsule";
			case 57:
				return "Pilot";
			case 58:
				return "Passenger";
			case 59:
				return "Boarding gate";
			case 60:
				return "Crew";
			case 61:
				return "Skill In Training";
			case 62:
				return "CorpMarket"; //Corporation Market Deliveries
			case 63:
				return "Locked"; //Locked item, can not be moved unless unlocked
			case 64:
				return "Unlocked"; //Unlocked item, can be moved
			case 70:
				return "Office Slot 1 ";
			case 71:
				return "Office Slot 2";
			case 72:
				return "Office Slot 3";
			case 73:
				return "Office Slot 4";
			case 74:
				return "Office Slot 5";
			case 75:
				return "Office Slot 6";
			case 76:
				return "Office Slot 7";
			case 77:
				return "Office Slot 8";
			case 78:
				return "Office Slot 9";
			case 79:
				return "Office Slot 10";
			case 80:
				return "Office Slot 11";
			case 81:
				return "Office Slot 12";
			case 82:
				return "Office Slot 13";
			case 83:
				return "Office Slot 14";
			case 84:
				return "Office Slot 15";
			case 85:
				return "Office Slot 16";
			case 86:
				return "Bonus";
			case 87:
				return "DroneBay";
			case 88:
				return "Booster";
			case 89:
				return "Implant";
			case 90:
				return "ShipHangar";
			case 91:
				return "ShipOffline";
			case 92:
				return "RigSlot0"; //Rig power slot 1
			case 93:
				return "RigSlot1"; //Rig power slot 2
			case 94:
				return "RigSlot2"; //Rig power slot 3
			case 95:
				return "RigSlot3"; //Rig power slot 4
			case 96:
				return "RigSlot4"; //Rig power slot 5
			case 97:
				return "RigSlot5"; //Rig power slot 6
			case 98:
				return "RigSlot6"; //Rig power slot 7
			case 99:
				return "RigSlot7"; //Rig power slot 8
			case 100:
				return "Factory Operation"; //Factory Background Operation
			case 116:
				return "CorpSAG2"; //Corp Security Access Group 2
			case 117:
				return "CorpSAG3"; //Corp Security Access Group 3
			case 118:
				return "CorpSAG4"; //Corp Security Access Group 4
			case 119:
				return "CorpSAG5"; //Corp Security Access Group 5
			case 120:
				return "CorpSAG6"; //Corp Security Access Group 6
			case 121:
				return "CorpSAG7"; //Corp Security Access Group 7
			case 122:
				return "SecondaryStorage"; //Secondary Storage
			case 123:
				return "CaptainsQuarters";
			case 124:
				return "Wis Promenade";
			case 125:
				return "SubSystem0";
			case 126:
				return "SubSystem1";
			case 127:
				return "SubSystem2";
			case 128:
				return "SubSystem3";
			case 129:
				return "SubSystem4";
			case 130:
				return "SubSystem5";
			case 131:
				return "SubSystem6";
			case 132:
				return "SubSystem7";
			default:
				return String.valueOf(theFlag);
		}
	}

	public static String location(int locationID, EveAsset parentAsset, Settings settings) {
		Location location = null;
		ApiStation apiStation = null;

		//Offices
		if (locationID >= 66000000) {
			if (locationID < 66014933) {
				locationID = locationID - 6000001;
			} else {
				locationID = locationID - 6000000;
			}
		}

		//Conquerable Stations
		apiStation = settings.getConquerableStations().get(locationID);
		if (apiStation != null) {
			location = settings.getLocations().get(apiStation.getSolarSystemID());
			if (location != null) {
				return location.getName() + " - " + apiStation.getStationName();
			} else {
				return apiStation.getStationName();
			}
		}

		//locations.xml (staStations && mapDenormalize)
		location = settings.getLocations().get(locationID);
		if (location != null) {
			return location.getName();
		}

		if (parentAsset != null) {
			return parentAsset.getLocation();
		}
		return "Error !" + String.valueOf(locationID);
	}

	public static String region(int locationID, EveAsset parentAsset, Settings settings) {
		Location location = null;
		ApiStation apiStation = null;

		//Offices
		if (locationID >= 66000000) {
			if (locationID < 66014933) {
				locationID = locationID - 6000001;
			} else {
				locationID = locationID - 6000000;
			}
		}

		//Conquerable Stations
		apiStation = settings.getConquerableStations().get(locationID);
		if (apiStation != null) {
			location = settings.getLocations().get(apiStation.getSolarSystemID());
			if (location != null) {
				location = settings.getLocations().get(location.getRegion());
				if (location != null) {
					return location.getName();
				}
			}
		}

		//locations.xml (staStations && mapDenormalize)
		location = settings.getLocations().get(locationID);
		if (location != null) {
			location = settings.getLocations().get(location.getRegion());
			if (location != null) {
				return location.getName();
			}
		}
		if (parentAsset != null) {
			return parentAsset.getRegion();
		}
		return "Error !" + String.valueOf(locationID);
	}

	public static String security(int locationID, EveAsset parentAsset, Settings settings) {
		Location location = null;
		ApiStation apiStation = null;

		//Offices
		if (locationID >= 66000000) {
			if (locationID < 66014933) {
				locationID = locationID - 6000001;
			} else {
				locationID = locationID - 6000000;
			}
		}

		//Conquerable Stations
		apiStation = settings.getConquerableStations().get(locationID);
		if (apiStation != null) {
			location = settings.getLocations().get(apiStation.getSolarSystemID());
			if (location != null) {
				return location.getSecurity();
			}
		}

		//locations.xml (staStations && mapDenormalize)
		location = settings.getLocations().get(locationID);
		if (location != null) {
			return location.getSecurity();
		}

		if (parentAsset != null) {
			return parentAsset.getSecurity();
		}
		return "Error !" + String.valueOf(locationID);
	}

	public static float volume(int typeID, Settings settings) {
		Items item = settings.getItems().get(typeID);
		if (item != null) {
			return item.getVolume();
		}
		return -typeID;
	}

	public static String name(int typeID, Settings settings) {
		Items item = settings.getItems().get(typeID);
		if (item != null) {
			return item.getName();
		}
		return "!" + String.valueOf(typeID);
	}

	public static double priceBase(int typeID, Settings settings) {
		Items item = settings.getItems().get(typeID);
		if (item != null) {
			return item.getPrice();
		}
		return -typeID;
	}

	public static String category(int typeID, Settings settings) {
		Items item = settings.getItems().get(typeID);
		if (item != null) {
			return item.getCategory();
		}
		return "";
	}

	public static String owner(Human human, boolean bCorp) {
		if (bCorp) {
			return human.getCorporation();
		} else {
			return human.getName();
		}
	}

	public static String group(int typeID, Settings settings) {
		Items item = settings.getItems().get(typeID);
		if (item != null) {
			return item.getGroup();
		}
		return "";
	}

	public static String meta(int typeID, Settings settings) {
		Items item = settings.getItems().get(typeID);
		if (item != null) {
			return item.getMeta();
		}
		return "";
	}

	public static boolean marketGroup(int typeID, Settings settings) {
		Items item = settings.getItems().get(typeID);
		if (item != null) {
			return item.isMarketGroup();
		}
		return false;
	}

	public static String container(int locationID, EveAsset parentAsset) {

		String sTemp = "";
		if (locationID >= 66000000 && locationID < 68000000) {
			sTemp = "Office";
		}
		if (parentAsset != null) {
			if (!sTemp.equals("")) {
				sTemp = sTemp + ": ";
			}
			sTemp = sTemp + parentAsset.getName() + " #" + parentAsset.getId();
			String parentContainer = parentAsset.getContainer();
			if (!parentContainer.equals(sTemp) && !parentContainer.equals("") && !parentContainer.equals("Office")) {
				sTemp = parentContainer + " - " + sTemp;
			}
		}


		return sTemp;
	}

	public static List<EveAsset> apiMarketOrder(List<ApiMarketOrder> marketOrders, Settings settings, Human human, boolean bCorp){
		List<EveAsset> eveAssets = new Vector<EveAsset>();
		for (int a = 0; a < marketOrders.size(); a++){
			ApiMarketOrder apiMarketOrder = marketOrders.get(a);
			if (apiMarketOrder.getBid() == 0
					&& apiMarketOrder.getOrderState() == 0
					&& apiMarketOrder.getVolRemaining() > 0
					){
				EveAsset eveAsset = apiMarketOrderToEveAsset(apiMarketOrder, settings, human, bCorp);
				eveAssets.add(eveAsset);
			}
		}
		return eveAssets;
	}

	private static EveAsset apiMarketOrderToEveAsset(ApiMarketOrder apiMarketOrder, Settings settings, Human human, boolean bCorp){
		int typeID = (int)apiMarketOrder.getTypeID();
		int locationID = (int) apiMarketOrder.getStationID();
		long count = apiMarketOrder.getVolRemaining();
		int id = (int) apiMarketOrder.getOrderID();
		String flag = "Market Order";
		boolean corporationAsset = bCorp;
		boolean singleton  = false;

		String name = AssetConverter.name(typeID, settings);
		String group = AssetConverter.group(typeID, settings);
		String category = AssetConverter.category(typeID, settings);
		double basePrice = AssetConverter.priceBase(typeID, settings);
		boolean marketGroup = AssetConverter.marketGroup(typeID, settings);
		float volume = AssetConverter.volume(typeID, settings);
		String meta = AssetConverter.meta(typeID, settings);

		String owner = AssetConverter.owner(human, bCorp);

		String location = AssetConverter.location(locationID, null, settings);
		String container = AssetConverter.container(locationID, null);
		String region = AssetConverter.region(locationID, null, settings);
		String security = AssetConverter.security(locationID, null, settings);

		return new EveAsset(name, group, category, owner, count, location, container, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton, security);
	}

	public static List<EveAsset> apiIndustryJob(List<ApiIndustryJob> industryJobs, Settings settings, Human human, boolean bCorp){
		List<EveAsset> eveAssets = new Vector<EveAsset>();
		for (int a = 0; a < industryJobs.size(); a++){
			ApiIndustryJob apiIndustryJob = industryJobs.get(a);
			int id = (int) apiIndustryJob.getInstalledItemID();
			if (apiIndustryJob.getCompleted() == 0){
				EveAsset eveAsset = apiIndustryJobToEveAsset(apiIndustryJob, settings, human, bCorp);
				eveAssets.add(eveAsset);
			}
			//Mark original blueprints
			boolean isCopy = (apiIndustryJob.getInstalledItemCopy() > 0);
			if (settings.getBpos().contains(id)){
				settings.getBpos().remove(settings.getBpos().indexOf(id));
			}
			if (!isCopy){
				settings.getBpos().add(id);
			}
		}
		return eveAssets;
	}
	
	private static EveAsset apiIndustryJobToEveAsset(ApiIndustryJob apiIndustryJob, Settings settings, Human human, boolean bCorp){
		int typeID = (int) apiIndustryJob.getInstalledItemTypeID();
		int locationID = (int) apiIndustryJob.getInstalledItemLocationID();
		long count = apiIndustryJob.getInstalledItemQuantity();
		int id = (int) apiIndustryJob.getInstalledItemID();
		int nFlag = apiIndustryJob.getInstalledItemFlag();
		boolean corporationAsset = bCorp;
		boolean singleton  = false;

		String flag = AssetConverter.flag(nFlag);

		String name = AssetConverter.name(typeID, settings);
		String group = AssetConverter.group(typeID, settings);
		String category = AssetConverter.category(typeID, settings);
		double basePrice = AssetConverter.priceBase(typeID, settings);
		boolean marketGroup = AssetConverter.marketGroup(typeID, settings);
		float volume = AssetConverter.volume(typeID, settings);
		String meta = AssetConverter.meta(typeID, settings);

		String owner = AssetConverter.owner(human, bCorp);

		String location = AssetConverter.location(locationID, null, settings);
		String container = AssetConverter.container(locationID, null);
		String region = AssetConverter.region(locationID, null, settings);
		String security = AssetConverter.security(locationID, null, settings);

		return new EveAsset(name, group, category, owner, count, location, container, flag, basePrice, meta, id, typeID, marketGroup, corporationAsset, volume, region, locationID, singleton, security);
	}

	public static List<EveAsset> apiAsset(Settings setttings, Human human, List<ApiAsset> assets, boolean bCorp){
		List<EveAsset> eveAssets = new Vector<EveAsset>();
		apiAsset(setttings, human, assets, eveAssets, null, bCorp);
		return eveAssets;
	}
	private static void apiAsset(Settings setttings, Human human, List<ApiAsset> assets, List<EveAsset> eveAssets, EveAsset parentEveAsset, boolean bCorp){
		for (int a = 0; a < assets.size(); a++){
			ApiAsset asset = assets.get(a);
			EveAsset eveAsset = apiAssetsToEveAsset(setttings, human, asset, parentEveAsset, bCorp);
			if (parentEveAsset == null){
				eveAssets.add(eveAsset);
			} else {
				parentEveAsset.addEveAsset(eveAsset);
			}
			apiAsset(setttings, human, new Vector<ApiAsset>(asset.getAssets()), eveAssets, eveAsset, bCorp);
		}
	}
	private static EveAsset apiAssetsToEveAsset(Settings settings, Human human, ApiAsset apiAsset, EveAsset parentEveAsset, boolean bCorp){
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
