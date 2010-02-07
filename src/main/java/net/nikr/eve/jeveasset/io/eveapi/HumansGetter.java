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
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import net.nikr.log.Log;
import org.xml.sax.SAXException;


public class HumansGetter extends AbstractApiGetter<Response> {

	private AccountBalanceGetter accountBalanceGetter = new AccountBalanceGetter();

	private Account account;
	private boolean forceUpdate;

	public void load(List<Account> accounts, boolean forceUpdate){
		Log.info("Characters updating:");
		boolean updated = false;
		boolean updateFailed = false;
		for (int a = 0; a < accounts.size(); a++){
			load( accounts.get(a), forceUpdate);
			if (isCharacterUpdated()){
				updated = true;
			} else {
				updateFailed = true;
			}
		}
		if (updated && !updateFailed){
			Log.info("	Characters updated (ALL)");
		} else if(updated && updateFailed) {
			Log.info("	Characters updated (SOME)");
		} else {
			Log.info("	Characters not updated (NONE)");
		}
	}

	public void load(Account account, boolean forceUpdate){
		this.account = account;
		this.forceUpdate = forceUpdate;
		load(account.getCharactersNextUpdate(), forceUpdate, false, "Characters", "account "+account.getUserID());
	}

	@Override
	protected Response getResponse(boolean bCorp) throws IOException, SAXException {
		Parser parser = new Parser();
		Response response = parser.getEveCharacters(Human.getApiAuthorization(account));
		account.setCharactersNextUpdate(response.getCachedUntil());
		return response;
	}

	@Override
	protected void ok(Response response, boolean bCorp) {
		List<ApiCharacter> characters = new Vector<ApiCharacter>(response.getEveCharacters());
		for (int a = 0; a < characters.size(); a++){
			ApiCharacter apiCharacter = characters.get(a);
			Human human = new Human(
									account
									,apiCharacter.getName()
									,apiCharacter.getCharacterID()
									,apiCharacter.getCorporationName()
									);

			if (!account.getHumans().contains(human)){ //Add new account
				if (forceUpdate){
					accountBalanceGetter.load(human, forceUpdate);
					if (!accountBalanceGetter.isCharacterUpdated()){
						return;
					}
				}
				account.getHumans().add(human);
			} else { //Update existing account
				List<Human> humans = account.getHumans();
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

	/*
	private static String error;

	public static void load(SettingsInterface settings, List<Account> accounts){
		Log.info("Characters updating:");
		boolean updated = false;
		boolean updateFailed = false;
		for (int a = 0; a < accounts.size(); a++){
			Account account = accounts.get(a);
			boolean returned = load(settings, account, false);
			if (returned){
				updated = true;
			} else {
				updateFailed = true;
			}
		}
		if (updated && !updateFailed){
			Log.info("	Characters updated (ALL)");
		} else if(updated && updateFailed) {
			Log.info("	Characters updated (SOME)");
		} else {
			Log.info("	Characters not updated (NONE)");
		}
	}

	public static boolean load(SettingsInterface settings, Account account, boolean apiKeyCheck){
		if (settings.isUpdatable(account.getCharactersNextUpdate()) || apiKeyCheck){
			Parser characterListParser = new Parser();
			characterListParser.setCachingEnabled(true);
			Response characterListResponse = null;
			try {
				characterListResponse = characterListParser.getEveCharacters(Human.getApiAuthorization(account));
				account.setCharactersNextUpdate(characterListResponse.getCachedUntil());
				if (!characterListResponse.hasError()){
					List<ApiCharacter> characters = new Vector<ApiCharacter>(characterListResponse.getEveCharacters());
					for (int a = 0; a < characters.size(); a++){
						ApiCharacter eveCharacter = characters.get(a);
						Log.info("	Character updated for: "+eveCharacter.getName()+" ("+eveCharacter.getCorporationName()+")");
						Human human = new Human(
												account
												,eveCharacter.getName()
												,eveCharacter.getCharacterID()
												,eveCharacter.getCorporationName()
												);
						
						if (!account.getHumans().contains(human)){ //Add new account
							AccountBalanceGetter.load(human, apiKeyCheck);
							if (apiKeyCheck){
								if (!){
									return false;
								}
							}
							account.getHumans().add(human);
						} else { //Update existing account
							List<Human> humans = account.getHumans();
							for (int b = 0; b < humans.size(); b++){
								Human currentHuman = humans.get(b);
								if (currentHuman.getCharacterID() == human.getCharacterID()){
									currentHuman.setName(human.getName());
									currentHuman.setCorporation(human.getCorporation());
								}
							}
						}
					}
				} else {
					ApiError apiError = characterListResponse.getError();
					error = apiError.getError();
					if (apiError.getCode() == 516 || apiError.getCode() == 203){
						Log.info("Characters update failed (Not a valid API Key)");
						return false;
					}
					Log.info("	Characters update failed (API ERROR: code: "+apiError.getCode()+" :: "+apiError.getError()+")");
					return false;
				}
			} catch (IOException ex) {
				Log.info("Characters update failed");
				return false;
			} catch (SAXException ex) {
				Log.error("Characters update failed (PARSER ERROR)", ex);
			}
			return true;
		} else {
			return false;
		}
		
	}

	public static String getError() {
		return error;
	}
	 *
	 */
}
