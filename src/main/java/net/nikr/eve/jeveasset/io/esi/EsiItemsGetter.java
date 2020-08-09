/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.Settings;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.getMarketApiOpen;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CategoryResponse;
import net.troja.eve.esi.model.CharacterRolesResponse.RolesEnum;
import net.troja.eve.esi.model.GroupResponse;
import net.troja.eve.esi.model.MarketGroupResponse;
import net.troja.eve.esi.model.TypeDogmaAttribute;
import net.troja.eve.esi.model.TypeResponse;


public class EsiItemsGetter extends AbstractEsiGetter {

	public final static String ESI_ITEM_VERSION = "1.0.0";
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
		TypeDogmaAttribute techLevel = null;
		TypeDogmaAttribute metaLevel = null;
		TypeDogmaAttribute metaGroup = null;
		if (typeResponse.getDogmaAttributes() != null) {
			for (TypeDogmaAttribute attribute : typeResponse.getDogmaAttributes()) {
				if (attribute.getAttributeId() == 422) { //422 = tech level
					techLevel = attribute;
				}
				if (attribute.getAttributeId() == 633) { //633 = meta level
					metaLevel = attribute;
				}
				if (attribute.getAttributeId() == 1692) { //1692 = meta group
					metaGroup = attribute;
				}
			}
		}
		//Tech Level
		String tech;
		if (metaGroup != null) {
			switch (metaGroup.getValue().intValue()) {
				case 1:  tech = "Tech I"; break;
				case 2:  tech = "Tech II"; break;
				case 3:  tech = "Storyline"; break;
				case 4:  tech = "Faction"; break;
				case 5:  tech = "Officer"; break;
				case 6:  tech = "Deadspace"; break;
				case 7:  tech = "Frigates"; break;
				case 8:  tech = "Elite Frigates"; break;
				case 9:  tech = "Commander Frigates"; break;
				case 10: tech = "Destroyer"; break;
				case 11: tech = "Cruiser"; break;
				case 12: tech = "Elite Cruiser"; break;
				case 13: tech = "Commander Cruiser"; break;
				case 14: tech = "Tech III"; break;
				case 15: tech = "Abyssal"; break;
				case 17: tech = "Premium"; break;
				case 19: tech = "Limited Time"; break;
				default: tech = "Tech I"; break;
			}
		} else if (techLevel != null) {
			switch (techLevel.getValue().intValue()) {
				case 1:  tech = "Tech I";   break;
				case 2:  tech = "Tech II";  break;
				case 3:  tech = "Tech III"; break;
				default: tech = "Tech I";   break;
			}
		} else {
			tech = "Tech 1"; 
		}
		//Meta Level
		int meta;
		if (metaLevel != null) {
			meta = metaLevel.getValue().intValue(); //Meta Level
		} else {
			meta = 0;
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
		item = new Item(typeID, name, group, category, price, volume, packagedVolume, capacity, meta, tech, marketGroup, portion, productTypeID, productQuantity, ESI_ITEM_VERSION);
		
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
