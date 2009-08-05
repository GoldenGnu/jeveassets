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

package net.nikr.eve.jeveasset.data;

import com.beimin.eveapi.utils.stationlist.ApiStation;
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.gui.shared.Formater;


public class EveAsset implements Comparable<EveAsset> {

	private List<EveAsset> assets = new Vector<EveAsset>();
	private EveAsset parentEveAsset;
	private String name;
	private String group;
	private String category;
	private String owner;
	private long count;
	private String location;
	private int locationID;
	private String container;
	private String flag;
	private double basePrice;
	private String meta;
	private int id;
	private int typeId;
	private boolean marketGroup;
	private Marketstat marketstat;
	private UserPrice userPrice;
	private boolean corporationAsset;
	private float volume ;
	private String region;
	private long typeCount = 0;
	private boolean bpo;

	public EveAsset(EveAsset parentEveAsset, String name, String group, String category, String owner, long count, String location, String container, String flag, double basePrice, String meta, int id, int typeId, boolean marketGroup, boolean corporationAsset, float volume, String region, int locationID) {
		this.parentEveAsset = parentEveAsset;
		this.name = name;
		this.group = group;
		this.category = category;
		this.owner = owner;
		this.count = count;
		this.location = location;
		this.container = container;
		this.flag = flag;
		this.basePrice = basePrice;
		this.meta = meta;
		this.id = id;
		this.typeId = typeId;
		this.marketGroup = marketGroup;
		this.corporationAsset = corporationAsset;
		this.volume = volume;
		this.region = region;
		this.locationID = locationID;
		this.bpo = false;
	}

	public void setMarketstat(Marketstat marketstat) {
		this.marketstat = marketstat;
	}

	public void setUserPrice(UserPrice userPrice) {
		this.userPrice = userPrice;
	}

	public boolean isBpo() {
		return bpo;
	}

	public void setBpo(boolean bpo) {
		this.bpo = bpo;
	}

	public boolean isBlueprint() {
		return (name.toLowerCase().contains("blueprint"));
	}
	
	public void addEveAsset(EveAsset eveAsset){
		assets.add(eveAsset);
	}

	public List<EveAsset> getAssets() {
		return assets;
	}

	public String getCategory() {
		return category;
	}

	public String getContainer() {
		return container;
	}

	public boolean isCorporationAsset(){
		return corporationAsset;
	}

	public long getCount() {
		return count;
	}

	public String getFlag() {
		return flag;
	}

	public String getGroup() {
		return group;
	}

	public int getId() {
		return id;
	}

	public String getLocation() {
		return location;
	}

	public int getLocationID() {
		return locationID;
	}

	public boolean isMarketGroup() {
		return marketGroup;
	}

	public Marketstat getMarketstat() {
		return marketstat;
	}

	public UserPrice getUserPrice() {
		return userPrice;
	}

	public String getMeta() {
		return meta;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public EveAsset getParentEveAsset() {
		return parentEveAsset;
	}

	public double getPrice(){
		if (isBlueprint() && !isBpo()){
			return 0;
		}
		if (this.getUserPrice() != null){
			double d = this.getUserPrice().getPrice();
			if (d != 0) return d;
		}
		if (this.isMarketGroup() && this.getMarketstat() != null){
			double d = this.getMarketstat().getSellMedian();
			if (d != 0) return d;
		}
		return 0;
	}
	public boolean isUserPrice(){
		return (this.getUserPrice() != null);
	}
	public double getPriceSellMedian(){
		if (this.isMarketGroup() && this.getMarketstat() != null){
			double d = this.getMarketstat().getSellMedian();
			if (d != 0) return d;
		}
		return 0;
	}
	public double getPriceSellMin(){
		if (isBlueprint() && !isBpo()){
			return 0;
		}
		if (this.isMarketGroup() && this.getMarketstat() != null){
			double d = this.getMarketstat().getSellMin();
			if (d != 0) return d;
		}
		//return basePrice;
		return 0;
	}
	public double getPriceBuyMax(){
		if (isBlueprint() && !isBpo()){
			return 0;
		}
		if (this.isMarketGroup() && this.getMarketstat() != null){
			double d = this.getMarketstat().getBuyMax();
			if (d != 0) return d;
		}
		//return basePrice;
		return 0;
	}

	public double getValue(){
		return Formater.numberDouble(this.getPrice() * this.getCount());
	}

	public double getPriceBase() {
		return basePrice;
	}

	public int getTypeId() {
		return typeId;
	}

	public String getRegion() {
		return region;
	}

	public float getVolume() {
		return volume;
	}

	public long getTypeCount() {
		return typeCount;
	}

	public void setTypeCount(long typeCount) {
		this.typeCount = typeCount;
	}

	@Override
	public String toString(){
		return this.getName();
	}

	@Override
	public int compareTo(EveAsset o) {
		return this.getName().compareTo(o.getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EveAsset){
			return equals((EveAsset) obj);
		}
		return false;
	}

	public boolean equals(EveAsset eveAsset) {
		return this.getName().equals(eveAsset.getName());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}
	
	public static String calcFlag(int theFlag){
		switch(theFlag){
			case 0: return "None";
			case 1: return "Wallet";
			case 2: return "Factory";
			case 4: return "Hangar";
			case 5: return "Cargo";
			case 6: return "Briefcase";
			case 7: return "Skill";
			case 8: return "Reward";
			case 9: return "Connected"; //Character in station connected
			case 10: return "Disconnected"; //Character in station offline
			case 11: return "LoSlot0"; //Low power slot 1
			case 12: return "LoSlot1"; //Low power slot 2
			case 13: return "LoSlot2"; //Low power slot 3
			case 14: return "LoSlot3"; //Low power slot 4
			case 15: return "LoSlot4"; //Low power slot 5
			case 16: return "LoSlot5"; //Low power slot 6
			case 17: return "LoSlot6"; //Low power slot 7
			case 18: return "LoSlot7"; //Low power slot 8
			case 19: return "MedSlot0"; //Medium power slot 1
			case 20: return "MedSlot1"; //Medium power slot 2
			case 21: return "MedSlot2"; //Medium power slot 3
			case 22: return "MedSlot3"; //Medium power slot 4
			case 23: return "MedSlot4"; //Medium power slot 5
			case 24: return "MedSlot5"; //Medium power slot 6
			case 25: return "MedSlot6"; //Medium power slot 7
			case 26: return "MedSlot7"; //Medium power slot 8
			case 27: return "HiSlot0"; //High power slot 1
			case 28: return "HiSlot1"; //High power slot 2
			case 29: return "HiSlot2"; //High power slot 3
			case 30: return "HiSlot3"; //High power slot 4
			case 31: return "HiSlot4"; //High power slot 5
			case 32: return "HiSlot5"; //High power slot 6
			case 33: return "HiSlot6"; //High power slot 7
			case 34: return "HiSlot7"; //High power slot 8
			case 35: return "Fixed Slot";
			case 56: return "Capsule";
			case 57: return "Pilot";
			case 58: return "Passenger";
			case 59: return "Boarding gate";
			case 60: return "Crew";
			case 61: return "Skill In Training";
			case 62: return "CorpMarket"; //Corporation Market Deliveries
			case 63: return "Locked"; //Locked item, can not be moved unless unlocked
			case 64: return "Unlocked"; //Unlocked item, can be moved
			case 70: return "Office Slot 1 ";
			case 71: return "Office Slot 2";
			case 72: return "Office Slot 3";
			case 73: return "Office Slot 4";
			case 74: return "Office Slot 5";
			case 75: return "Office Slot 6";
			case 76: return "Office Slot 7";
			case 77: return "Office Slot 8";
			case 78: return "Office Slot 9";
			case 79: return "Office Slot 10";
			case 80: return "Office Slot 11";
			case 81: return "Office Slot 12";
			case 82: return "Office Slot 13";
			case 83: return "Office Slot 14";
			case 84: return "Office Slot 15";
			case 85: return "Office Slot 16";
			case 86: return "Bonus";
			case 87: return "DroneBay";
			case 88: return "Booster";
			case 89: return "Implant";
			case 90: return "ShipHangar";
			case 91: return "ShipOffline";
			case 92: return "RigSlot0"; //Rig power slot 1
			case 93: return "RigSlot1"; //Rig power slot 2
			case 94: return "RigSlot2"; //Rig power slot 3
			case 95: return "RigSlot3"; //Rig power slot 4
			case 96: return "RigSlot4"; //Rig power slot 5
			case 97: return "RigSlot5"; //Rig power slot 6
			case 98: return "RigSlot6"; //Rig power slot 7
			case 99: return "RigSlot7"; //Rig power slot 8
			case 100: return "Factory Operation"; //Factory Background Operation
			case 116: return "CorpSAG2"; //Corp Security Access Group 2
			case 117: return "CorpSAG3"; //Corp Security Access Group 3
			case 118: return "CorpSAG4"; //Corp Security Access Group 4
			case 119: return "CorpSAG5"; //Corp Security Access Group 5
			case 120: return "CorpSAG6"; //Corp Security Access Group 6
			case 121: return "CorpSAG7"; //Corp Security Access Group 7
			case 122: return "SecondaryStorage"; //Secondary Storage
			case 123: return "CaptainsQuarters";
			case 124: return "Wis Promenade";
			case 125: return "SubSystem0";
			case 126: return "SubSystem1";
			case 127: return "SubSystem2";
			case 128: return "SubSystem3";
			case 129: return "SubSystem4";
			case 130: return "SubSystem5";
			case 131: return "SubSystem6";
			case 132: return "SubSystem7";
			default: return String.valueOf(theFlag);
		}
	}
	public static String calcLocation(int locationID, EveAsset parentAsset, Settings settings){
		Location location = null;
		ApiStation apiStation = null;

		//Offices
		//[FIXME] Fix not confirmed...
		if (locationID >= 66000000){
			if (locationID < 66014933){
				locationID = locationID - 6000001;
			} else {
				locationID = locationID - 6000000;
			}
		}

		//Conquerable Stations
		apiStation = settings.getConquerableStations().get(locationID);
		if (apiStation != null){
			location = settings.getLocations().get(apiStation.getSolarSystemID());
			if (location != null){
				return location.getName() + " - " + apiStation.getStationName();
			} else {
				return apiStation.getStationName();
			}
		}

		//locations.xml (staStations && mapDenormalize)
		location = settings.getLocations().get( locationID);
		if (location != null){
			return location.getName();
		}

		if (parentAsset != null){
			return parentAsset.getLocation();
		}
		return "Error !"+String.valueOf(locationID);
	}
	
	public static String calcRegion(int locationID, EveAsset parentAsset, Settings settings){
		Location location = null;
		ApiStation apiStation = null;

		//Offices
		//[FIXME] Fix not confirmed...
		if (locationID >= 66000000){
			if (locationID < 66014933){
				locationID = locationID - 6000001;
			} else {
				locationID = locationID - 6000000;
			}
		}

		//Conquerable Stations
		apiStation = settings.getConquerableStations().get(locationID);
		if (apiStation != null){
			location = settings.getLocations().get(apiStation.getSolarSystemID());
			if (location != null){
				location = settings.getLocations().get(location.getRegion());
				if (location != null){
					return location.getName();
				}
			}
		}

		//locations.xml (staStations && mapDenormalize)
		location = settings.getLocations().get( locationID);
		if (location != null){
			location = settings.getLocations().get(location.getRegion());
			if (location != null){
				return location.getName();
			}
		}
		if (parentAsset != null){
			return parentAsset.getRegion();
		}
		return "Error !"+String.valueOf(locationID);
	}

	public static float calcVolume(int typeID, Settings settings){
		Items item = settings.getItems().get(typeID);
		if (item != null) return item.getVolume();
		return -typeID;
	}

	public static String calcName(int typeID, Settings settings){
		Items item = settings.getItems().get(typeID);
		if (item != null) return item.getName();
		return "!"+String.valueOf(typeID);
	}
	public static double calcPrice(int typeID, Settings settings){
		Items item = settings.getItems().get(typeID);
		if (item != null) return item.getPrice();
		return -typeID;
	}
	public static String calcCategory(int typeID, Settings settings){
		Items item = settings.getItems().get(typeID);
		if (item != null) return item.getCategory();
		return "";
	}
	public static String calcOwner(Human human, boolean bCorp){
		if (bCorp){
			return human.getCorporation();
		} else {
			return human.getName();
		}
	}
	public static String calcGroup(int typeID, Settings settings){
		Items item = settings.getItems().get(typeID);
		if (item != null) return item.getGroup();
		return "";
	}
	public static String calcMeta(int typeID, Settings settings){
		Items item = settings.getItems().get(typeID);
		if (item != null) return item.getMeta();
		return "";
	}
	public static boolean calcMarketGroup(int typeID, Settings settings){
		Items item = settings.getItems().get(typeID);
		if (item != null) return item.isMarketGroup();
		return false;
	}

	public static String calcContainer(int locationID, EveAsset parentAsset){

		String sTemp = "";
		if (locationID >= 66000000 && locationID < 68000000){
			sTemp = "Office";
		}
		if (parentAsset != null){
			if (!sTemp.equals("")) sTemp = sTemp + ": ";
			sTemp = sTemp+parentAsset.getName()+" #"+parentAsset.getId();
			String parentContainer = parentAsset.getContainer();
			if (!parentContainer.equals(sTemp)
					&& !parentContainer.equals("")
					&& !parentContainer.equals("Office")){
				sTemp = parentContainer+" - "+sTemp;
			}
		}

		
		return sTemp;
	}
}
