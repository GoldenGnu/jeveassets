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
import net.nikr.eve.jeveasset.data.sde.IndustryMaterial;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.DATASOURCE;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.getIndustryApiOpen;
import static net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter.getMarketApiOpen;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.model.CharacterRolesResponse;
import net.troja.eve.esi.model.IndustrySystemsResponse;
import net.troja.eve.esi.model.MarketPricesResponse;
import net.troja.eve.esi.model.SystemCostIndice;


public class EsiManufacturingPrices extends AbstractEsiGetter {

	public EsiManufacturingPrices(UpdateTask updateTask) {
		super(updateTask, null, false,  Settings.get().getManufacturingSettings().getNextUpdate(), TaskType.MANUFACTURING_PRICES);
	}

	@Override
	protected void update() throws ApiException {
		//Manufacturing Base Cost
		List<MarketPricesResponse> priceResponses = update(DEFAULT_RETRIES, new EsiHandler<List<MarketPricesResponse>>() {
			@Override
			public ApiResponse<List<MarketPricesResponse>> get() throws ApiException {
				return getMarketApiOpen().getMarketsPricesWithHttpInfo(DATASOURCE, null);
			}
		});
		Map<Integer, MarketPricesResponse> prices = new HashMap<>();
		for (MarketPricesResponse price : priceResponses) {
			prices.put(price.getTypeId(), price);
		}
		Map<Integer, Double> manufacturingPrices = new HashMap<>();
		for (Item item : StaticData.get().getItems().values()) {
			int productTypeID = item.getProductTypeID();
			if (item.getManufacturingMaterials().isEmpty() || productTypeID == 0) {
				continue;
			}
			double price = getPrice(prices, item);
			manufacturingPrices.put(productTypeID, price);
			manufacturingPrices.put(item.getTypeID(), price);
		}
		Settings.get().getManufacturingSettings().setPrices(manufacturingPrices);
		//System Indexes
		List<IndustrySystemsResponse> systemResponses = update(DEFAULT_RETRIES, new EsiHandler<List<IndustrySystemsResponse>>() {
			@Override
			public ApiResponse<List<IndustrySystemsResponse>> get() throws ApiException {
				return getIndustryApiOpen().getIndustrySystemsWithHttpInfo(DATASOURCE, null);
			}
		});
		Map<Integer, Float> manufacturingSystems = new HashMap<>();
		for (IndustrySystemsResponse system : systemResponses) {
			for (SystemCostIndice costIndice : system.getCostIndices()) {
				switch (costIndice.getActivity()) {
					case MANUFACTURING:
						manufacturingSystems.put(system.getSolarSystemId(), costIndice.getCostIndex());
						break;
				}
			}
		}
		Settings.get().getManufacturingSettings().setSystems(manufacturingSystems);
	}

	@Override
	protected void setNextUpdate(Date date) {
		Settings.get().getManufacturingSettings().setNextUpdate(date);
	}

	@Override
	protected boolean haveAccess() {
		return true; //Public
	}

	@Override
	protected CharacterRolesResponse.RolesEnum[] getRequiredRoles() {
		return null; //Public
	}

	private double getPrice(Map<Integer, MarketPricesResponse> prices, Item item) {
		double price = 0;
		
		for (IndustryMaterial material : item.getManufacturingMaterials()) {
			MarketPricesResponse response = prices.get(material.getTypeID());
			if (response == null) {
				Item deeper = ApiIdConverter.getItem(material.getTypeID());
				System.out.println(item.getTypeName() + " does not have a price for: " + deeper.getTypeName());
				//price = price + getPrice(prices, deeper);
				continue;
			}
			double adjustedPrice = response.getAdjustedPrice();
			int quantity = material.getQuantity();
			price = price + (adjustedPrice * quantity);
		}
		return price;
	}

}
