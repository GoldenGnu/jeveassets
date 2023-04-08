/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
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
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Citadel;
import net.nikr.eve.jeveasset.data.settings.Citadel.CitadelSource;
import net.nikr.eve.jeveasset.data.settings.PriceData;
import net.nikr.eve.jeveasset.data.settings.ReprocessSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.esi.EsiItemsGetter;
import net.nikr.eve.jeveasset.io.local.ItemsWriter;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.troja.eve.esi.model.CharacterBookmarkItem;
import net.troja.eve.esi.model.CharacterBookmarksResponse;
import net.troja.eve.esi.model.CorporationBookmarkItem;
import net.troja.eve.esi.model.CorporationBookmarksResponse;
import net.troja.eve.esi.model.PlanetResponse;
import net.troja.eve.esi.model.StructureResponse;

public final class ApiIdConverter {

	private ApiIdConverter() { }

	private static final String EMPTY_STRING = "";
	private static final String UNKNOWN_FLAG = "Unknown";

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

	public static String getFlagName(ItemFlag itemFlag, OwnerType ownerType) {
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

	private static String getDivisionName(OwnerType ownerType, int i) {
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
		return getPriceType(typeID, isBlueprintCopy, false);
	}

	private static double getPriceReprocessed(final Integer typeID) {
		return getPriceType(typeID, false, true);
	}

	private static double getPriceType(final Integer typeID, final boolean isBlueprintCopy, boolean reprocessed) {
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

		//Price data
		PriceData priceData = Settings.get().getPriceData().get(typeID);
		if (priceData != null && priceData.isEmpty()) {
			priceData = null;
		}
		if (reprocessed) {
			return Settings.get().getPriceDataSettings().getDefaultPriceReprocessed(priceData);
		} else {
			return Settings.get().getPriceDataSettings().getDefaultPrice(priceData);
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

	public static Item getItemUpdate(final Integer typeID) {
		if (typeID == null) {
			return new Item(0);
		}
		Item item = StaticData.get().getItems().get(typeID);
		if (item == null || (item.getVersion() != null && !item.getVersion().equals(EsiItemsGetter.ESI_ITEM_VERSION))) { //New ESI item version
			if (item != null && item.getVersion().startsWith(EsiItemsGetter.ESI_ITEM_EMPTY)) {
				String lastUpdated = item.getVersion().replace(EsiItemsGetter.ESI_ITEM_EMPTY, "");
				String today = Formatter.dateOnly(Settings.getNow());
				if (lastUpdated.equals(today)) {
					return item;
				}
			}
			item = downloadItem(typeID);
		}
		return item;
	}

	private synchronized static Item downloadItem(final Integer typeID) { //Only download one item at the time
		Item item = StaticData.get().getItems().get(typeID); //May have been downloaded while waiting for sync
		if (item == null) {
			EsiItemsGetter esiItemsGetter = new EsiItemsGetter(typeID);
			esiItemsGetter.run();
			item = esiItemsGetter.getItem();
			if (item == null) { //Empty Item
				item = new Item(typeID, EsiItemsGetter.ESI_ITEM_EMPTY + Formatter.dateOnly(Settings.getNow()));
			}
			StaticData.get().getItems().put(typeID, item);
			ItemsWriter.save();
		}
		return item;
	}

	public static String getOwnerName(final Integer ownerID) {
		if (ownerID == null) {
			return EMPTY_STRING;
		} else {
			return getOwnerName(Long.valueOf(ownerID));
		}
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

	public static Citadel getCitadel(final CorporationBookmarksResponse response) {
		CorporationBookmarkItem item = response.getItem(); //Can be null
		if (item != null && item.getItemId() > 1000000000000L) {
			return getCitadel(response.getLocationId(), item.getItemId(), response.getLabel(), CitadelSource.ESI_BOOKMARKS);
		} else {
			return null;
		}
	}

	public static Citadel getCitadel(final CharacterBookmarksResponse response) {
		CharacterBookmarkItem item = response.getItem(); //Can be null
		if (item != null && item.getItemId() > 1000000000000L) {
			return getCitadel(response.getLocationId(), item.getItemId(), response.getLabel(), CitadelSource.ESI_BOOKMARKS);
		} else {
			return null;
		}
	}

	private static Citadel getCitadel(Integer systemID, Long locationID, String label, CitadelSource source) {
		MyLocation system = getLocation(systemID);
		return new Citadel(locationID, General.get().bookmarkLocation(system.getSystem(), label.trim(), String.valueOf(locationID)), systemID, false, true, source);
	}

	public static Citadel getCitadel(PlanetResponse planet) {
		return new Citadel(planet.getPlanetId(), planet.getName(), planet.getSystemId(), false, false, CitadelSource.ESI_PLANET);
	}

}
