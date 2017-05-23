/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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

import com.beimin.eveapi.model.shared.AccountBalance;
import com.beimin.eveapi.model.shared.Asset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.ItemFlag;
import net.nikr.eve.jeveasset.data.MyAccountBalance;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.data.esi.EsiOwner;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;
import net.troja.eve.esi.model.CharacterAssetsResponse;
import net.troja.eve.esi.model.CharacterWalletsResponse;


public class EsiConverter {

	private EsiConverter() { }

	public static List<MyAsset> convertAssets(EsiOwner owner, List<CharacterAssetsResponse> responses) {
		List<Asset> assets = new ArrayList<Asset>();
		Map<Long, CharacterAssetsResponse> parents = new HashMap<Long, CharacterAssetsResponse>();
		Map<Long, Asset> cache = new HashMap<Long, Asset>();
		for (CharacterAssetsResponse response : responses) {
			parents.put(response.getItemId(), response);
		}
		for (CharacterAssetsResponse response : responses) {
			deepAsset(assets, parents, cache, owner, response);
		}
		return ApiConverter.convertAsset(assets, owner);
	}

	private static Asset deepAsset(List<Asset> assets, Map<Long, CharacterAssetsResponse> parents, Map<Long, Asset> cache, EsiOwner owner, CharacterAssetsResponse response) {
		Asset asset = cache.get(response.getItemId());
		if (asset != null) {
			return asset; //Asset already converted
		}
		CharacterAssetsResponse parent = parents.get(response.getLocationId());
		Asset parentEveAsset = null;
		if (parent != null) {
			parentEveAsset = cache.get(response.getLocationId());
			if (parentEveAsset == null) {
				parentEveAsset = deepAsset(assets, parents, cache, owner, parent);
				cache.put(parentEveAsset.getItemID(), parentEveAsset);
			}
		}

		asset = toAsset(response, parentEveAsset);
		cache.put(asset.getItemID(), asset);
		if (parent == null) {
			assets.add(asset);
		}
		return asset;
	}

	private static com.beimin.eveapi.model.shared.Asset toAsset(final CharacterAssetsResponse asset, final com.beimin.eveapi.model.shared.Asset parentAsset) {
		com.beimin.eveapi.model.shared.Asset eveApiAsset = new com.beimin.eveapi.model.shared.Asset();
		int count;
		int rawQuantity;
		boolean singleton = asset.getIsSingleton();
		if (null == asset.getQuantity()) { //Packaged
			count = 1;
			if (asset.getIsSingleton()) { //Unpackaged > Quantity should tell us if it's a BPC or BPO, but, it's null!!!
				rawQuantity = 0;
			} else { //Stacked > Packaged > BPO
				rawQuantity = -1; //BP0
			}
		} else {
			if (asset.getQuantity() < 0) { //rawQuantity: Quantity should tell us if it's a BPC or BPO
				count = 1;
				rawQuantity = asset.getQuantity();
				if (asset.getQuantity() == -2) { //BPC does not return the correct singleton value
					singleton = true;
				}
			} else if (asset.getQuantity() > 1) { //In stack > Packaged > BPO
				count = asset.getQuantity();
				rawQuantity = -1;
			} else if (!asset.getIsSingleton()) { //Not singleton > Packaged > BPO
				count = asset.getQuantity();
				rawQuantity = -1;
			} else { //Quantity = 0 or 1 and item is not packed
				count = 1;
				rawQuantity = 0;
			}
		}
		int flagID = 0;
		for (ItemFlag flag : StaticData.get().getItemFlags().values()) {
			if (flag.getFlagName().equals(asset.getLocationFlag().toString())) {
				flagID = flag.getFlagID();
				break;
			}
		}
		eveApiAsset.setItemID(asset.getItemId());
		eveApiAsset.setLocationID(asset.getLocationId());
		eveApiAsset.setTypeID(asset.getTypeId());
		eveApiAsset.setQuantity(count); //Long to Int
		eveApiAsset.setRawQuantity(rawQuantity); //Long to Int
		eveApiAsset.setFlag(flagID);
		eveApiAsset.setSingleton(singleton);
		if (parentAsset != null) {
			parentAsset.getAssets().add(eveApiAsset);
		}
		return eveApiAsset;
	}

	public static List<MyAccountBalance> convertAccountBallances(EsiOwner owner, List<CharacterWalletsResponse> responses) {
		List<MyAccountBalance> accountBalances = new ArrayList<MyAccountBalance>();
		for (CharacterWalletsResponse response : responses) {
			AccountBalance accountBalance = new AccountBalance();
			accountBalance.setAccountID(0);
			accountBalance.setAccountKey(response.getWalletId());
			accountBalance.setBalance(response.getBalance());
			MyAccountBalance myAccountBalance = new MyAccountBalance(accountBalance, owner);
			accountBalances.add(myAccountBalance);
		}
		return accountBalances;
	}
}
