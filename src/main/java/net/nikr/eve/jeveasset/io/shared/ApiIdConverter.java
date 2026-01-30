/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.nikr.eve.jeveasset.data.api.accounts.SimpleOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType;
import static net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType.ALLIANCE_ID;
import static net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType.CHARACTER_ID;
import static net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType.CONTRACT_ID;
import static net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType.CORPORATION_ID;
import static net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType.EVE_SYSTEM;
import static net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType.INDUSTRY_JOB_ID;
import static net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType.MARKET_TRANSACTION_ID;
import static net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType.PLANET_ID;
import static net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType.STATION_ID;
import static net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType.STRUCTURE_ID;
import static net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType.SYSTEM_ID;
import static net.nikr.eve.jeveasset.data.api.raw.RawJournal.ContextType.TYPE_ID;
import net.nikr.eve.jeveasset.data.sde.Agent;
import net.nikr.eve.jeveasset.data.sde.IndustryMaterial;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.NpcCorporation;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.Citadel.CitadelSource;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingFacility;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ReactionRigs;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ManufacturingSecurity;
import net.nikr.eve.jeveasset.data.settings.ManufacturingSettings.ReactionSecurity;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.ReprocessSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.esi.EsiItemsGetter;
import net.nikr.eve.jeveasset.io.local.ItemsWriter;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.troja.eve.esi.model.MoonResponse;
import net.troja.eve.esi.model.PlanetResponse;
import net.troja.eve.esi.model.StructureResponse;

public final class ApiIdConverter {

	private ApiIdConverter() { }

	private static final String EMPTY_STRING = "";
	private static final String UNKNOWN_FLAG = "Unknown";
	private static final ConcurrentMap<Integer, Object> ITEM_DOWNLOAD_LOCKS = new ConcurrentHashMap<>();
	private static final ExecutorService ITEM_DOWNLOAD_THREAD_POOL = Executors.newFixedThreadPool(ThreadWoker.MAIN_THREADS);
	private static boolean update = false;

	private enum PriceType {
		ITEM, REPROCESSED, MANUFACTURING
	}

	/*
	public static String flag(final int flag, final MyAsset parentAsset) {
		ItemFlag itemFlag = StaticData.get().getItemFlags().get(flag);
		if (itemFlag != null) {
			if (parentAsset != null && !parentAsset.getFlag().isEmpty()) {
				return parentAsset.getFlag() + " > " + itemFlag.getFlagName();
			} else {
				return itemFlag.getFlagName();
			}
		}
		return "!" + flag;
	}
	*/

	public static ItemFlag getFlag(final int flag) {
		ItemFlag itemFlag = StaticData.get().getItemFlags().get(flag);
		if (itemFlag != null) {
			return itemFlag;
		} else {
			return new ItemFlag(flag, UNKNOWN_FLAG, UNKNOWN_FLAG);
		}
	}

	public static String getFlagName(String flagName) {
		switch (flagName) {
			case "CorpSAG1": return "1st Division";
			case "CorpSAG2": return "2nd Division";
			case "CorpSAG3": return "3rd Division";
			case "CorpSAG4": return "4th Division";
			case "CorpSAG5": return "5th Division";
			case "CorpSAG6": return "6th Division";
			case "CorpSAG7": return "7th Division";
			default: return flagName;
		}
	}

	public static String getFlagName(ItemFlag itemFlag) {
		return getFlagName(itemFlag, null);
	}

	public static String getFlagName(ItemFlag itemFlag, SimpleOwner ownerType) {
		switch (itemFlag.getFlagID()) {
			case 62: return "Corporation Deliveries";
			case 63: return itemFlag.getFlagName();
			case 64: return itemFlag.getFlagName();

			case 115: return getDivisionName(ownerType, 1);
			case 116: return getDivisionName(ownerType, 2);
			case 117: return getDivisionName(ownerType, 3);
			case 118: return getDivisionName(ownerType, 4);
			case 119: return getDivisionName(ownerType, 5);
			case 120: return getDivisionName(ownerType, 6);
			case 121: return getDivisionName(ownerType, 7);
			case 146: return "Junkyard Reprocessed";
			case 147: return "Junkyard Trashed";
		}
		if (itemFlag.getFlagText().toLowerCase().contains("slot")) {
			return getSlotName(itemFlag);
		}
		return itemFlag.getFlagText();
	}

	private static String getDivisionName(SimpleOwner ownerType, int i) {
		final String division;
		switch (i) {
			case 1: division = "1st Division"; break;
			case 2: division = "2nd Division"; break;
			case 3: division = "3rd Division"; break;
			case 4: division = "4th Division"; break;
			case 5: division = "5th Division"; break;
			case 6: division = "6th Division"; break;
			case 7: division = "7th Division"; break;
			default: division = "Division " + 1; break;
		}
		String divisionName = null;
		if (ownerType != null) {
			divisionName = ownerType.getAssetDivisions().get(i);
		}
		if (divisionName == null || divisionName.isEmpty()) {
			return division;
		} else {
			return divisionName + " (" + division + ")";
		}
	}

	private static String getSlotName(ItemFlag itemFlag) {
		String name = itemFlag.getFlagText();
		name = name.replace(" power", "");
		name = name.replace(" s", " S");
		return name;
	}

	public static String getContext(MyJournal journal) {
		return getContext(journal.getContextType(), journal.getContextID());
	}

	public static String getContext(final ContextType contextType, final Long contextID) {
		if (contextType == null || contextID == null) {
			return null;
		}
		switch (contextType) {
			case ALLIANCE_ID:
			case CHARACTER_ID:
			case CORPORATION_ID:
				return getOwnerName(contextID);
			case CONTRACT_ID:
				return General.get().journalContract();
			case INDUSTRY_JOB_ID:
				return General.get().journalIndustryJob();
			case MARKET_TRANSACTION_ID:
				if (contextID != 1) {
					return General.get().journalMarketTransaction();
				} else {
					return General.get().journalSystemTransaction();
				}
			case EVE_SYSTEM:
			case TYPE_ID:
				Item item = ApiIdConverter.getItem(contextID.intValue());
				if (!item.isEmpty()) {
					return item.getTypeName();
				}
				return null;
			case PLANET_ID:
			case STATION_ID:
			case SYSTEM_ID:
			case STRUCTURE_ID:
				MyLocation location = ApiIdConverter.getLocation(contextID);
				if (!location.isEmpty()) {
					return location.getLocation();
				}
				return null;
			default:
				return null;
		}
	}

	/**
	 *
	 * @param typeID
	 * @param isBlueprintCopy
	 * @return PriceData for the type or PriceData.EMPTY (all zeros)
	 */
	public static PriceData getPriceData(final Integer typeID, final boolean isBlueprintCopy) {
		if (typeID == null) {
			return PriceData.EMPTY;
		}
		if (isBlueprintCopy) {
			return PriceData.EMPTY;
		}
		PriceData priceData = Settings.get().getPriceData().get(typeID);
		if (priceData == null) {
			return PriceData.EMPTY;
		}
		if (priceData.isEmpty()) { //No Price :(
			return PriceData.EMPTY;
		}
		return priceData;
	}

	public static double getPrice(final Integer typeID, final boolean isBlueprintCopy) {
		return getPriceType(typeID, isBlueprintCopy, PriceType.ITEM);
	}

	private static double getPriceReprocessed(final Integer typeID) {
		return getPriceType(typeID, false, PriceType.REPROCESSED);
	}

	private static double getPriceManufacturing(final Integer typeID) {
		return getPriceType(typeID, false, PriceType.MANUFACTURING);
	}

	private static double getPriceType(final Integer typeID, final boolean isBlueprintCopy, PriceType type) {
		if (typeID == null) {
			return 0;
		}
		UserItem<Integer, Double> userPrice;
		if (isBlueprintCopy) { //Blueprint Copy
			userPrice = Settings.get().getUserPrices().get(-typeID);
		} else { //All other
			userPrice = Settings.get().getUserPrices().get(typeID);
		}
		if (userPrice != null) {
			return userPrice.getValue();
		}

		//Blueprint Copy (Default Zero)
		if (isBlueprintCopy) {
			return 0;
		}

		//Blueprints Base Price
		Item item = getItem(typeID);
		//Tech 1
		if (item.isBlueprint()) {
			if (Settings.get().isBlueprintBasePriceTech1() && !item.getTypeName().toLowerCase().contains("ii")) {
				return item.getPriceBase();
			}
			//Tech 2
			if (Settings.get().isBlueprintBasePriceTech2() && item.getTypeName().toLowerCase().contains("ii")) {
				return item.getPriceBase();
			}
		}
		//Manufacturing Price for non-market items
		if (!item.isMarketGroup() && Settings.get().isManufacturingDefault()) {
			return item.getPriceManufacturing();
		}

		//Price data
		PriceData priceData = Settings.get().getPriceData().get(typeID);
		if (priceData != null && priceData.isEmpty()) {
			priceData = null;
		}
		if (type == PriceType.REPROCESSED) {
			return Settings.get().getPriceDataSettings().getDefaultPriceReprocessed(priceData);
		} else if (type == PriceType.MANUFACTURING) {
			return Settings.get().getPriceDataSettings().getDefaultPriceManufacturing(priceData);
		} else {
			return Settings.get().getPriceDataSettings().getDefaultPrice(priceData);
		}
	}

	/**
	 * Calculate Manufacturing Price - This is expensive!
	 * Use Item.getPriceManufacturing() to get the manufacturing price
	 * @param item
	 * @return 
	 */
	public static double getPriceManufacturing(Item item) {
		return getPriceManufacturing(Settings.get().getManufacturingSettings(), item);
	}

	protected static double getPriceManufacturing(ManufacturingSettings manufacturingSettings, Item item) {
		//Installation Fee
		Double baseCost = manufacturingSettings.getPrices().get(item.getTypeID());
		if (baseCost == null) {
			return 0;
		}
		int systemID = manufacturingSettings.getSystemID();
		Float systemIndex = manufacturingSettings.getSystems().get(systemID);
		if (systemIndex == null) {
			return 0.1;
		}
		double installationFee = getManufacturingInstallationFee(manufacturingSettings, systemIndex, baseCost, item);
		//Materials Cost
		double materialCost = 0;
		Item blueprint = getItem(item.getBlueprintTypeID());
		for (IndustryMaterial material : blueprint.getManufacturingMaterials()) {
			double quantity = getManufacturingQuantity(manufacturingSettings, material.getQuantity());
			double price = getPriceManufacturing(material.getTypeID());
			materialCost = materialCost + (price * quantity);
		}
		return installationFee + materialCost;
	}

	protected static double getManufacturingInstallationFee(ManufacturingSettings manufacturingSettings, Float systemIndex, Double baseCost, Item item) {
		//Installation Fee
		ManufacturingFacility facility = manufacturingSettings.getFacility();
		double bonuses = percentToBonus(facility.getFeeBonus());
		double tax = manufacturingSettings.getTax() / 100;
		double scc = 0.25 / 100;

		//TIF = EIV * ((SCI * bonuses) + FacilityTax + SCC + AlphaClone) 
		//EIV: ME 0 quantity of inputs * adjusted price of inputs => baseCost
		//SCI: System Cost Index 
		//Facility Tax: Fixed tax for NPC stations set to 0.25% or tax rate set by facility owner.
		//SCC: SCC surcharge, this is a fixed value and cannot be affected by anything 
		//Bonuses: Any bonuses that are applicable 
		//AlphaClone: Tax applicable to alpha clones, set at 0.25% (Just add to tax)
		return baseCost * ((systemIndex * bonuses) + tax + scc); 
	}

	private static double getManufacturingQuantity(ManufacturingSettings manufacturingSettings, int quantity) {
		int me = manufacturingSettings.getMaterialEfficiency();
		ManufacturingFacility facility = manufacturingSettings.getFacility();
		ManufacturingRigs rigs = manufacturingSettings.getRigs();
		ManufacturingSecurity security = manufacturingSettings.getSecurity();
		return getManufacturingQuantity(quantity, me, facility, rigs, security, 1, true);
	}

	/**
	 * 
	 * @param quantity
	 * @param me
	 * @param facility
	 * @param rigs
	 * @param security
	 * @param runs
	 * @param round
	 * @return Can return less than 1 (one)
	 */
	public static double getManufacturingQuantity(int quantity, int me, ManufacturingFacility facility, ManufacturingRigs rigs, ManufacturingSecurity security, double runs, boolean round) {
		//base * ((100-ME)/100) * (EC modifier) * (EC Rig modifier))
		return roundManufacturingQuantity(quantity * percentToBonus(me) * percentToBonus(facility.getMaterialBonus()) * rigToBonus(rigs, security), runs, round);
	}

	public static double getReactionQuantity(int quantity, ManufacturingSettings.ReactionRigs rigs, ManufacturingSettings.ReactionSecurity security, double runs, boolean round) {
		return roundManufacturingQuantity(quantity * rigToBonus(rigs, security), runs, round);
	}

	private static double roundManufacturingQuantity(double manufacturingQuantity, double runs, boolean round) {
		double quantity = Math.max(runs, manufacturingQuantity * runs);
		if (round) {
			return Math.ceil(quantity);
		} else {
			return quantity;
		}
	}

	private static double percentToBonus(double value) {
		return ((100.0 - value) / 100.0);
	}

	private static double rigToBonus(ManufacturingRigs rigs, ManufacturingSecurity security) {
		if (rigs == ManufacturingRigs.NONE) {
			return 1;
		} else {
			return percentToBonus(rigs.getMaterialBonus() * security.getRigBonus());
		}
	}

	private static double rigToBonus(ReactionRigs rigs, ReactionSecurity security) {
		if (rigs == ReactionRigs.NONE) {
			return 1;
		} else {
			return percentToBonus(rigs.getMaterialBonus() * security.getRigBonus());
		}
	}

	public static double getPriceReprocessed(Item item) {
		return getPriceReprocessed(item, false);
	}

	public static double getPriceReprocessedMax(Item item) {
		return getPriceReprocessed(item, true);
	}

	private static double getPriceReprocessed(Item item, boolean max) {
		double priceReprocessed = 0;
		int portionSize = 0;
		for (ReprocessedMaterial material : item.getReprocessedMaterial()) {
			//Calculate reprocessed price
			portionSize = material.getPortionSize();
			double price = ApiIdConverter.getPriceReprocessed(material.getTypeID());
			int count;
			if (max) {
				count = ReprocessSettings.getMax(material.getQuantity(), item.isOre());
			} else {
				count = Settings.get().getReprocessSettings().getLeft(material.getQuantity(), item.isOre());
			}
			priceReprocessed = priceReprocessed + (price * count);
		}
		if (priceReprocessed > 0 && portionSize > 0) {
			priceReprocessed = priceReprocessed / portionSize;
		}
		return priceReprocessed;
	}

	public static float getVolume(final Item item, final boolean packaged) {
		if (item != null) {
			if (packaged) {
				return item.getVolumePackaged();
			} else {
				return item.getVolume();
			}
		}
		return 0;
	}

	public static Item getItem(final Integer typeID) {
		if (typeID == null) {
			return new Item(0);
		}
		Item item = StaticData.get().getItems().get(typeID);
		if (item == null) {
			item = new Item(typeID);
		}
		return item;
	}

	public static void setUpdateItem(boolean update) {
		ApiIdConverter.update = update;
	}

	public static Item getItemUpdate(final Integer typeID) {
		return getItemUpdate(typeID, update);
	}

	public static Item getItemUpdate(final Integer typeID, boolean update) {
		if (!update) {
			return getItem(typeID);
		}
		if (typeID == null) {
			return new Item(0);
		}
		Item item = getUpdateItem(typeID);
		if (item != null) {
			return item;
		} else {
			return synchronizedDownloadItem(typeID);
		}
	}

	public static void updateItem(Integer typeID) {
		if (!update || typeID == null) {
			return;
		}
		Item item = getUpdateItem(typeID);
		if (item == null) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					downloadItem(typeID);
				}
			});
			thread.start();
		}
	}

	private static Item getUpdateItem(final Integer typeID) {
		Item item = StaticData.get().getItems().get(typeID);
		if (item == null || (item.getVersion() != null && !item.getVersion().equals(EsiItemsGetter.ESI_ITEM_VERSION))) { //New ESI item version
			if (item != null && item.getVersion().startsWith(EsiItemsGetter.ESI_ITEM_EMPTY)) {
				String lastUpdated = item.getVersion().replace(EsiItemsGetter.ESI_ITEM_EMPTY, "");
				String today = Formatter.dateOnly(Settings.getNow());
				if (lastUpdated.equals(today)) {
					return item;
				}
			}
			return null;
		}
		return item;
	}

	protected static Item synchronizedDownloadItem(final Integer typeID) { 
		ITEM_DOWNLOAD_LOCKS.putIfAbsent(typeID, new Object()); //Only download one item type at the time (Locks on typeID)
		synchronized(ITEM_DOWNLOAD_LOCKS.get(typeID)) {
			Item item = getUpdateItem(typeID);
			if (item != null) { //May have been downloaded while waiting for sync
				return item;
			} else {
				return downloadItem(typeID);
			}
		}
	}

	private static Item downloadItem(final Integer typeID) { //Only download one item at the time
		Item item = ThreadWoker.startReturn(ITEM_DOWNLOAD_THREAD_POOL, null, new DownloadItem(typeID));
		if (item == null) { //Empty Item
			item = new Item(typeID, EsiItemsGetter.ESI_ITEM_EMPTY + Formatter.dateOnly(Settings.getNow()));
		}
		synchronized(StaticData.get().getItems()) {
			StaticData.get().getItems().put(typeID, item); //Add Item
		}
		ITEM_DOWNLOAD_LOCKS.remove(typeID); //Remove lock after download is completed
		if (ITEM_DOWNLOAD_LOCKS.isEmpty()) { //Save when the download queue is empty
			synchronized(StaticData.get().getItems()) {
				ItemsWriter.save(); //Save XML
			}
			
		}
		return item;
	}

	private static class DownloadItem implements Callable<Item> {

		private final Integer typeID;

		public DownloadItem(Integer typeID) {
			this.typeID = typeID;
		}

		@Override
		public Item call() throws Exception {
			EsiItemsGetter esiItemsGetter = new EsiItemsGetter(typeID);
			esiItemsGetter.run();
			return esiItemsGetter.getItem();
		}
	}

	public static String getOwnerName(final Integer ownerID) {
		if (ownerID == null) {
			return EMPTY_STRING;
		} else {
			return getOwnerName(Long.valueOf(ownerID));
		}
	}

	public static Agent getAgent(final Integer agentID) {
		Agent agent = StaticData.get().getAgents().get(agentID);
		if (agent == null) {
			agent = new Agent(agentID);
		}
		return agent;
	}

	public static NpcCorporation getNpcCorporation(final Integer corporationID) {
		NpcCorporation npcCorporation = StaticData.get().getNpcCorporations().get(corporationID);
		if (npcCorporation == null) {
			npcCorporation = new NpcCorporation(corporationID);
		}
		return npcCorporation;
	}

	public static String getOwnerName(final Long ownerID) {
		if (ownerID == null || ownerID == 0) { //0 (zero) is valid, but, should return empty string
			return EMPTY_STRING;
		}
		String owner = Settings.get().getOwners().get(ownerID);
		if (owner != null) {
			return owner;
		} else { // OwnerIDs from the journal can be a system ID
			MyLocation location = getLocation(ownerID);
			if (!location.isEmpty()) {
				return location.getLocation();
			}
		}
		return "!" + String.valueOf(ownerID);
	}

	public static boolean isLocationOK(final Long locationID) {
		return !getLocation(locationID, null).isEmpty();
	}

	public static MyLocation getLocation(Integer locationID) {
		if (locationID == null) {
			return MyLocation.create(0);
		} else {
			return getLocation(Long.valueOf(locationID), null);
		}
	}

	public static MyLocation getLocation(Long locationID) {
		return getLocation(locationID, null);
	}

	public static MyLocation getLocation(final Long locationID, final MyAsset parentAsset) {
		if (locationID == null) {
			return MyLocation.create(0);
		}
		MyLocation location = StaticData.get().getLocation(locationID);
		if (location != null) {
			return location;
		}
		if (parentAsset != null) {
			location = parentAsset.getLocation();
			if (location != null) {
				return location;
			}
		}
		location = CitadelGetter.get(locationID).toLocation();
		if (location != null) {
			return location;
		}
		return MyLocation.create(locationID);
	}

	public static void addLocation(final Citadel citadel) {
		MyLocation location = citadel.toLocation();
		if (location != null) {
			StaticData.get().addLocation(location);
		}
	}

	public static Citadel getCitadel(final StructureResponse response, final long locationID) {
		return new Citadel(locationID, response.getName(), response.getSolarSystemId(), false, true, CitadelSource.ESI_STRUCTURES);
	}

	public static Citadel getCitadel(PlanetResponse planet) {
		return new Citadel(planet.getPlanetId(), planet.getName(), planet.getSystemId(), false, false, CitadelSource.ESI_PLANET);
	}

	public static Citadel getCitadel(MoonResponse planet) {
		return new Citadel(planet.getMoonId(), planet.getName(), planet.getSystemId(), false, false, CitadelSource.ESI_MOON);
	}

}
