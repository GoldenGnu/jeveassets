/*
 * Copyright 2009
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

package net.nikr.eve.jeveasset.io;

import com.beimin.eveapi.ApiError;
import com.beimin.eveapi.character.list.ApiCharacter;
import com.beimin.eveapi.character.list.Parser;
import com.beimin.eveapi.character.list.Response;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.log.Log;
import org.xml.sax.SAXException;


public class EveApiHumansReader {

	private static String error;

	public static void load(Settings settings){
		Log.info("Updating characters:");
		boolean updated = false;
		boolean updateFailed = false;
		List<Account> accounts = settings.getAccounts();
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

	public static boolean load(Settings settings, Account account, boolean apiKeyCheck){
		if (Settings.isUpdatable(account.getCharactersNextUpdate()) || apiKeyCheck){
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
						Log.info("	Updating: "+eveCharacter.getName()+":");
						Human human = new Human(
												account
												,eveCharacter.getName()
												,eveCharacter.getCharacterID()
												,EveApiCorporationReader.load(settings, account,(int) eveCharacter.getCharacterID(), eveCharacter.getCorporationID())
												);
						
						if (!account.getHumans().contains(human)){
							if (!EveApiAccountBalanceReader.load(human, apiKeyCheck) && apiKeyCheck){
								return false;
							}
							account.getHumans().add(human);
						} else {
							updateHuman(account, human);
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
	private static void updateHuman(Account account, Human human){
		if (account.getHumans().contains(human)){
			List<Human> humans = account.getHumans();
			for (int a = 0; a < humans.size(); a++){
				Human currentHuman = humans.get(a);
				if (currentHuman.getCharacterID() == human.getCharacterID()){
					EveApiAccountBalanceReader.load(currentHuman);
					currentHuman.setName(human.getName());
					if (!human.getCorporation().equals("")) currentHuman.setCorporation(human.getCorporation());
				}
			}
		}
	}

	public static String getError() {
		return error;
	}
}
