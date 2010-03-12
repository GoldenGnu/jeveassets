/*
 * Copyright 2009, 2010
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

import com.beimin.eveapi.character.list.ApiCharacter;
import com.beimin.eveapi.character.list.Parser;
import com.beimin.eveapi.character.list.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.shared.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import org.xml.sax.SAXException;


public class HumansGetter extends AbstractApiGetter<Response> {

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
	protected Response getResponse(boolean bCorp) throws IOException, SAXException {
		Parser parser = new Parser();
		return parser.getEveCharacters(Human.getApiAuthorization(getAccount()));
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
	protected void setData(Response response, boolean bCorp) {
		List<ApiCharacter> characters = new Vector<ApiCharacter>(response.getEveCharacters());
		for (int a = 0; a < characters.size(); a++){
			ApiCharacter apiCharacter = characters.get(a);
			Human human = new Human(getAccount()
									,apiCharacter.getName()
									,apiCharacter.getCharacterID()
									,apiCharacter.getCorporationName()
									);

			if (!getAccount().getHumans().contains(human)){ //Add new account
				if (isForceUpdate()){
					accountBalanceGetter.load(null, true, human);
					if (accountBalanceGetter.hasError()){
						return;
					}
				}
				getAccount().getHumans().add(human);
			} else { //Update existing account
				List<Human> humans = getAccount().getHumans();
				for (int b = 0; b < humans.size(); b++){
					Human currentHuman = humans.get(b);
					if (currentHuman.getCharacterID() == human.getCharacterID()){
						currentHuman.setName(human.getName());
						currentHuman.setCorporation(human.getCorporation());
					}
				}
			}
		}
	}
}
