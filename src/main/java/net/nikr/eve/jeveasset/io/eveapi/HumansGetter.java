/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.account.apikeyinfo.ApiKeyInfoResponse;
import com.beimin.eveapi.account.characters.EveCharacter;
import com.beimin.eveapi.core.ApiException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;


public class HumansGetter extends AbstractApiGetter<ApiKeyInfoResponse> {

	private int fails = 0;
	
	public HumansGetter() {
		super("Accounts", 0, false, true);
	}

	@Override
	public void load(UpdateTask updateTask, boolean forceUpdate, Account account) {
		super.load(updateTask, forceUpdate, account);
	}

	@Override
	public void load(UpdateTask updateTask, boolean forceUpdate, List<Account> accounts) {
		super.load(updateTask, forceUpdate, accounts);
	}

	@Override
	protected ApiKeyInfoResponse getResponse(boolean bCorp) throws ApiException {
		return com.beimin.eveapi.account.apikeyinfo
				.ApiKeyInfoParser.getInstance()
				.getResponse(Human.getApiAuthorization(getAccount()));
	}

	@Override
	protected Date getNextUpdate() {
		return getAccount().getCharactersNextUpdate();
	}

	@Override
	protected void setNextUpdate(Date nextUpdate) {
		getAccount().setCharactersNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(ApiKeyInfoResponse response) {
		//Changed between Char and Corp AKA should be treated as a new api
		boolean typeChanged = !getAccount().compareTypes(response.getType());
		
		//Update account
		getAccount().setAccessMask(response.getAccessMask());
		getAccount().setExpires(response.getExpires());
		getAccount().setType(response.getType());
		
		List<EveCharacter> characters = new ArrayList<EveCharacter>(response.getEveCharacters());
		List<Human> humans = new ArrayList<Human>();
		
		fails = 0;
		if (isForceUpdate()){
			if (!getAccount().isAccountBalance()) fails++;
			if (!getAccount().isIndustryJobs()) fails++;
			if (!getAccount().isMarketOrders()) fails++;
			if (!getAccount().isAssetList()) fails = 4; //Can not work without it...
		}
		
		for (EveCharacter apiCharacter : characters){
			boolean found = false;
			for (Human human : getAccount().getHumans()){
				if ( (human.getOwnerID() == apiCharacter.getCharacterID() || human.getOwnerID() == apiCharacter.getCorporationID()) && !typeChanged){
					human.setName(getName(apiCharacter));
					human.setOwnerID(getID(apiCharacter));
					humans.add(human);
					found = true;
					break;
				}
			}
			if (!found){ //Add New
				humans.add(new Human(getAccount(), getName(apiCharacter), getID(apiCharacter)));
			}
		}
		getAccount().setHumans(humans);
	}
	
	@Override
	protected void updateFailed(Human humanFrom, Human humanTo){}
	
	private String getName(EveCharacter apiCharacter){
		if (getAccount().isCharacter()){
			return apiCharacter.getName();
		} else {
			return apiCharacter.getCorporationName();
		}
	}
	private long getID(EveCharacter apiCharacter){
		if (getAccount().isCharacter()){
			return apiCharacter.getCharacterID();
		} else {
			return apiCharacter.getCorporationID();
		}
	}
	
	public int getFails() {
		return fails;
	}
}
