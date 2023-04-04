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
import java.util.List;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.Settings;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.getMarketApiOpen;
import net.nikr.eve.jeveasset.io.local.ItemsReader;
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
	 */
	public final static String ESI_ITEM_VERSION = "1.0.2";
	public final static String ESI_ITEM_EMPTY = "EMPTY";

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
		GroupResponse groupResponse = update(DEFAULT_RETRIES, new EsiHandler<GroupResponse>() {
			@Override
			public ApiResponse<GroupResponse> get() throws ApiException {
				return getUniverseApiOpen().getUniverseGroupsGroupIdWithHttpInfo(typeResponse.getGroupId(), null, DATASOURCE, null, null);
			}
		});
		//Categories
		CategoryResponse categoryResponse = update(DEFAULT_RETRIES, new EsiHandler<CategoryResponse>() {
			@Override
			public ApiResponse<CategoryResponse> get() throws ApiException {
				return getUniverseApiOpen().getUniverseCategoriesCategoryIdWithHttpInfo(groupResponse.getCategoryId(), null, DATASOURCE, null, null);
			}
		});
		//Market Groups
		MarketGroupResponse marketGroupResponse = null;
		if (typeResponse.getMarketGroupId() != null) {
			marketGroupResponse = update(DEFAULT_RETRIES, new EsiHandler<MarketGroupResponse>() {
				@Override
				public ApiResponse<MarketGroupResponse> get() throws ApiException {
					return getMarketApiOpen().getMarketsGroupsMarketGroupIdWithHttpInfo(typeResponse.getMarketGroupId(), null, DATASOURCE, null, null);
				}
			});
		}
		String name = typeResponse.getName();
		String group = groupResponse.getName();
		String category = categoryResponse.getName();
		long price = -1; //Base Price
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
		final String techLevel;
		if (metaGroupID != null) {
			switch (metaGroupID) {
				case 1: techLevel = "Tech I"; break;
				case 2: techLevel = "Tech II"; break;
				case 3: techLevel = "Storyline"; break;
				case 4: techLevel = "Faction"; break;
				case 5: techLevel = "Officer"; break;
				case 6: techLevel = "Deadspace"; break;
				/*
				//No longer in use
				case 7: tech = "Frigates"; break;
				case 8: tech = "Elite Frigates"; break;
				case 9: tech = "Commander Frigates"; break;
				case 10: tech = "Destroyer"; break;
				case 11: tech = "Cruiser"; break;
				case 12: tech = "Elite Cruiser"; break;
				case 13: tech = "Commander Cruiser"; break;
				*/
				case 14: techLevel = "Tech III"; break;
				case 15: techLevel = "Abyssal"; break;
				case 17: techLevel = "Premium"; break;
				case 19: techLevel = "Limited Time"; break;
				case 52: techLevel = "Faction"; break; //Structure Faction
				case 53: techLevel = "Tech II"; break; //Structure Tech II
				case 54: techLevel = "Tech I"; break; //Structure Tech I
				default: techLevel = "Tech I"; break;
			}
		} else {
			techLevel = "Tech 1";
		}
		boolean marketGroup;
		if (marketGroupResponse != null) {
			marketGroup = marketGroupResponse.getTypes().contains(typeID);
		} else {
			marketGroup = false;
		}
		int portion = typeResponse.getPortionSize();
		int productTypeID = 0; //Product
		int productQuantity = 1; //Product Quantity
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
		item = new Item(typeID, name, group, category, price, volume, packagedVolume, capacity, metaLevel, techLevel, marketGroup, portion, productTypeID, productQuantity, slot, chargeSize, ESI_ITEM_VERSION);
	}

	private float getNotNull(Float f) {
		if (f != null) {
			return f;
		} else {
			return 0f;
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
