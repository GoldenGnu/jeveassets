/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.shared.locations.ApiLocation;
import com.beimin.eveapi.shared.locations.LocationsResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Account.AccessMask;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;

/**
 *
 * @author Niklas
 */
public class LocationsGetter extends AbstractApiGetter<LocationsResponse> {

	private final int MAX_SIZE = 50;
	private Map<Long, String> itemMap = new HashMap<Long, String>();
	private Map<Long, String> eveNames = new HashMap<Long, String>();
	private List<Long> itemIDs = new ArrayList<Long>();
	private Date nextUpdate;

	public LocationsGetter() {
		super("Locations", true, false);
	}

	public void load(UpdateTask updateTask, boolean forceUpdate, List<Account> accounts) {
		eveNames = new HashMap<Long, String>();
		super.loadAccounts(updateTask, forceUpdate, accounts);
		if (!hasError()) {
			Settings.get().setEveNames(eveNames);
		}
	}

	@Override
	protected int getProgressStart() {
		return 80;
	}

	private List<Long> getItems() throws ApiException {
		return itemIDs;
	}

	private void getItemID(Map<Long, String> itemIDs, List<Asset> assets) {
		for (Asset asset : assets) {
			if ((asset.getItem().getGroup().equals("Audit Log Secure Container")
					|| asset.getItem().getCategory().equals("Ship"))
					&& asset.isSingleton()) {
				itemIDs.put(asset.getItemID(), asset.getItem().getTypeName());
			}
			getItemID(itemIDs, asset.getAssets());
		}
	}

	@Override
	protected boolean load(Date nextUpdate, boolean updateCorporation, String updateName) {
		nextUpdate = getOwner().getLocationsNextUpdate();
		itemMap = new HashMap<Long, String>();
		getItemID(itemMap, getOwner().getAssets());
		List<Long> keys = new ArrayList<Long>(itemMap.keySet());
		for (int start = 0; start < keys.size(); start = Math.min(start + MAX_SIZE, keys.size())) {
			int end = Math.min(start + MAX_SIZE, keys.size());
			itemIDs = keys.subList(start, end);
			super.load(nextUpdate, updateCorporation, updateName + " (Request " + ((long)Math.ceil(start / MAX_SIZE) + 1) + " of " + ((long)Math.ceil((double)keys.size() / (double)MAX_SIZE)) + ")");
		}
		return true;
	}

	

	@Override
	protected LocationsResponse getResponse(boolean bCorp) throws ApiException {
		if (bCorp) {
			return com.beimin.eveapi.corporation.locations.LocationsParser.getInstance().getResponse(Owner.getApiAuthorization(getOwner()), getItems());
		} else {
			return com.beimin.eveapi.character.locations.LocationsParser.getInstance().getResponse(Owner.getApiAuthorization(getOwner()), getItems());
		}
	}

	@Override
	protected Date getNextUpdate() {
		return nextUpdate;
	}

	@Override
	protected void setNextUpdate(Date nextUpdate) {
		getOwner().setLocationsNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(LocationsResponse response) {
		Set<ApiLocation> all = response.getAll();
		for (ApiLocation apiLocation : all) {
			final long itemID = apiLocation.getItemID();
			final String eveName = apiLocation.getItemName();
			final String typeName = itemMap.get(itemID);
			if (!eveName.equals(typeName)) {
				eveNames.put(itemID, eveName);
			}
		}
	}

	@Override
	protected void updateFailed(Owner ownerFrom, Owner ownerTo) {
		
	}

	@Override
	protected long requestMask(boolean bCorp) {
		if (bCorp) {
			return AccessMask.LOCATIONS_CORP.getAccessMask();
		} else {
			return AccessMask.LOCATIONS_CHAR.getAccessMask();
		}
	}
}
