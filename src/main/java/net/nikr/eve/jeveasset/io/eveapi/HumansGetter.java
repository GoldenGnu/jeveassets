/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

import com.beimin.eveapi.account.characters.ApiCharacter;
import com.beimin.eveapi.account.characters.CharactersResponse;
import com.beimin.eveapi.core.ApiException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;


public class HumansGetter extends AbstractApiGetter<CharactersResponse> {

	private AccountBalanceGetter accountBalanceGetter;

	public HumansGetter() {
		super("Accounts", false, true);
		accountBalanceGetter = new AccountBalanceGetter();
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
	protected CharactersResponse getResponse(boolean bCorp) throws ApiException {
		return com.beimin.eveapi.account.characters
				.CharactersParser.getInstance()
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
	protected void setData(CharactersResponse response, boolean bCorp) {
		List<ApiCharacter> characters = new ArrayList<ApiCharacter>(response.getEveCharacters());
		List<Human> humans = new ArrayList<Human>();
		if (characters.isEmpty()){ //No characters on account
			this.error(); //it's impossible to check if it's a limited or full api key
			return;
		}
		for (int a = 0; a < characters.size(); a++){
			ApiCharacter apiCharacter = characters.get(a);
			Human human = new Human(getAccount(), apiCharacter.getName(), apiCharacter.getCharacterID(), apiCharacter.getCorporationName());

			if (!getAccount().getHumans().contains(human)){ //Add new account
				if (isForceUpdate()){ //New account
					accountBalanceGetter.load(null, true, human);
					if (accountBalanceGetter.hasCharacterError()){
						this.error();
						return;
					}
				}
				humans.add(human);
			} else { //Update existing account
				for (int b = 0; b < getAccount().getHumans().size(); b++){
					Human currentHuman = getAccount().getHumans().get(b);
					if (currentHuman.equals(human)){
						currentHuman.setName(human.getName());
						currentHuman.setCorporation(human.getCorporation());
						humans.add(currentHuman);
						break;
					}
				}
			}
		}
		getAccount().setHumans(humans);
	}

	@Override
	protected void clearData(boolean bCorp){}
}
