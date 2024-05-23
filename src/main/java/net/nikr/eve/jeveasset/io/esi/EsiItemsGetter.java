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
package net.nikr.eve.jeveasset.io.esi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.Settings;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.getMarketApiOpen;
import net.nikr.eve.jeveasset.io.local.ItemsReader;
import net.nikr.eve.jeveasset.io.online.EveRefGetter;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CategoryResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.GroupResponse;
import net.troja.eve.esi.model.MarketGroupResponse;
import net.troja.eve.esi.model.TypeDogmaAttribute;
import net.troja.eve.esi.model.TypeDogmaEffect;
import net.troja.eve.esi.model.TypeResponse;


public class EsiItemsGetter extends AbstractEsiGetter {

	/**
	 * Change ESI_ITEM_VERSION to force items_updates.xml items to be updated again.
	 * ChangeLog
	 * 1.2.0:
	 * Updated with EveRef type and blueprint data
	 */
	public final static String ESI_ITEM_VERSION = "1.2.0";
	public final static String ESI_ITEM_EMPTY = "EMPTY";

	private final static Map<Integer, GroupResponse> GROUPS_CACHE = new HashMap<>();
	private final static Map<Integer, CategoryResponse> CATEGORY_CACHE = new HashMap<>();
	private final static Map<Integer, MarketGroupResponse> MARKET_GROUP_CACHE = new HashMap<>();

	public final static long BASE_PRICE_DEFAULT = -1;
	public final static int PRODUCT_TYPE_ID_DEFAULT = 0;
	public final static int PRODUCT_QUANTITY_DEFAULT = 1;

	private final int typeID;
	private Item item = null;

	public EsiItemsGetter(int typeID) {
		super(null, null, true, Settings.getNow(), TaskType.ITEM_TYPES);
		this.typeID = typeID;
	}

	@Override
	protected void update() throws ApiException {
		//Types
		TypeResponse typeResponse = update(DEFAULT_RETRIES, new EsiHandler<TypeResponse>() {
			@Override
			public ApiResponse<TypeResponse> get() throws ApiException {
				return getUniverseApiOpen().getUniverseTypesTypeIdWithHttpInfo(typeID, null, DATASOURCE, null, null);
			}
		});
		//Groups
		final int groupID = typeResponse.getGroupId();
		GroupResponse groupResponse = GROUPS_CACHE.get(groupID);
		if (groupResponse == null) {
			groupResponse = update(DEFAULT_RETRIES, new EsiHandler<GroupResponse>() {
				@Override
				public ApiResponse<GroupResponse> get() throws ApiException {
					return getUniverseApiOpen().getUniverseGroupsGroupIdWithHttpInfo(groupID, null, DATASOURCE, null, null);
				}
			});
			GROUPS_CACHE.put(groupID, groupResponse);
		}
		final int categoryID = groupResponse.getCategoryId();
		//Categories
		CategoryResponse categoryResponse = CATEGORY_CACHE.get(categoryID);
		if (categoryResponse == null) {
			categoryResponse = update(DEFAULT_RETRIES, new EsiHandler<CategoryResponse>() {
				@Override
				public ApiResponse<CategoryResponse> get() throws ApiException {
					return getUniverseApiOpen().getUniverseCategoriesCategoryIdWithHttpInfo(categoryID, null, DATASOURCE, null, null);
				}
			});
			CATEGORY_CACHE.put(categoryID, categoryResponse);
		}
		//Market Groups
		MarketGroupResponse marketGroupResponse = null;
		if (typeResponse.getMarketGroupId() != null) {
			Integer marketGroupID = typeResponse.getMarketGroupId();
			marketGroupResponse  = MARKET_GROUP_CACHE.get(marketGroupID);
			if (marketGroupResponse == null) {
				marketGroupResponse = update(DEFAULT_RETRIES, new EsiHandler<MarketGroupResponse>() {
					@Override
					public ApiResponse<MarketGroupResponse> get() throws ApiException {
						return getMarketApiOpen().getMarketsGroupsMarketGroupIdWithHttpInfo(marketGroupID, null, DATASOURCE, null, null);
					}
				});
				MARKET_GROUP_CACHE.put(marketGroupID, marketGroupResponse);
			}
		}
		String name = typeResponse.getName()
							.replaceAll(" +", " ") //Replace 2 or more spaces
							.replace("\t", " ") //Tab
							.replace("„", "\"") //Index
							.replace("“", "\"") //Set transmit state
							.replace("”", "\"") //Cancel character
							.replace("‘", "'") //Private use one
							.replace("’", "'") //Private use two
							.replace("`", "'") //Grave accent
							.replace("´", "'") //Acute accent
							.replace("–", "-") //En dash
							.replace("‐", "-") //Hyphen
							.replace("‑", "-") //Non-breaking hyphen
							.replace("‒", "-") //Figure dash
							.replace("—", "-") //Em dash
							.trim();

		String group = groupResponse.getName();
		String category = categoryResponse.getName();
		long basePrice = BASE_PRICE_DEFAULT; //Base Price
		float volume = getNotNull(typeResponse.getVolume());
		float packagedVolume = getNotNull(typeResponse.getPackagedVolume());
		float capacity = getNotNull(typeResponse.getCapacity());
	//Meta / Charge Sire
		Integer metaGroupID = null;
		int metaLevel = 0;
		List<TypeDogmaAttribute> dogmaAttributes = typeResponse.getDogmaAttributes();
		Integer charge = null;
		if (dogmaAttributes != null) {
			for (TypeDogmaAttribute attribute : dogmaAttributes) {
				if (attribute.getAttributeId() == 1692) { //1692 = meta group
					metaGroupID = attribute.getValue().intValue();
				}
				if (attribute.getAttributeId() == 633) { //633 = meta level
					metaLevel = attribute.getValue().intValue();
				}
				if (attribute.getAttributeId() == 128) { //128 = The size of the charges that can fit in the turret/whatever.
					charge = attribute.getValue().intValue();
				}
			}
		}
	//Tech Level
		final String techLevel = getTechLevel(metaGroupID);
		
		boolean marketGroup;
		if (marketGroupResponse != null) {
			marketGroup = marketGroupResponse.getTypes().contains(typeID);
		} else {
			marketGroup = false;
		}
		int portion = typeResponse.getPortionSize();
		int productTypeID = PRODUCT_TYPE_ID_DEFAULT; //Product
		int productQuantity = PRODUCT_QUANTITY_DEFAULT; //Product Quantity
		//Slot
		String slot = null;
		List<TypeDogmaEffect> dogmaEffects = typeResponse.getDogmaEffects();
		if (dogmaEffects != null) {
			for (TypeDogmaEffect attribute : dogmaEffects) {
				if (attribute.getIsDefault()) {
					continue;
				}
				if (attribute.getEffectId() == 11) { //11 = Requires a low power slot
					slot = "Low";
					break;
				}
				if (attribute.getEffectId() == 12) { //12 = Requires a high power slot
					slot = "High";
					break;
				}
				if (attribute.getEffectId() == 13) { //13 = Requires a medium power slot
					slot = "Medium";
					break;
				}
				if (attribute.getEffectId() == 2663) { //2663 = Must be installed into an open rig slot
					slot = "Rig";
					break;
				}
				if (attribute.getEffectId() == 3772) { //3772 = Must be installed into an available subsystem slot on a Tech III ship.
					slot = "Subsystem";
					break;
				}
			}
		}
		//Charge Size
		String chargeSize = ItemsReader.getChargeSize(charge);
		//Item
		item = new Item(typeID, name, group, category, basePrice, volume, packagedVolume, capacity, metaLevel, techLevel, marketGroup, portion, productTypeID, productQuantity, slot, chargeSize, ESI_ITEM_VERSION);

		//EveRef Update
		item = EveRefGetter.getItem(item); //Update from EveRef
	}

	private float getNotNull(Float f) {
		if (f != null) {
			return f;
		} else {
			return 0f;
		}
	}

	public static String getTechLevel(Integer metaGroupID) {
		if (metaGroupID == null) {
			return "Tech I"; 
		}

		switch (metaGroupID) {
			case 1: return "Tech I";
			case 2: return "Tech II";
			case 3: return "Storyline";
			case 4: return "Faction";
			case 5: return "Officer";
			case 6: return "Deadspace";
			/*
			//No longer in use
			case 7: return "Frigates";
			case 8: return "Elite Frigates";
			case 9: return "Commander Frigates";
			case 10: return "Destroyer";
			case 11: return "Cruiser";
			case 12: return "Elite Cruiser";
			case 13: return "Commander Cruiser";
			*/
			case 14: return "Tech III";
			case 15: return "Abyssal";
			case 17: return "Premium";
			case 19: return "Limited Time";
			case 52: return "Faction"; //Structure Faction
			case 53: return "Tech II"; //Structure Tech II
			case 54: return "Tech I"; //Structure Tech I
			default: return "Tech I";
		}
	}

	public Item getItem() {
		return item;
	}


	@Override
	protected void setNextUpdate(Date date) {
		//Do Nothing
	}

	@Override
	protected boolean haveAccess() {
		return true; //Only use public endpoints
	}

	@Override
	protected RolesEnum[] getRequiredRoles() {
		return null;
	}

}
