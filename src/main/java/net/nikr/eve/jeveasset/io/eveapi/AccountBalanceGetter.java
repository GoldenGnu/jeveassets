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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.balance.ApiAccountBalance;
import com.beimin.eveapi.balance.Parser;
import com.beimin.eveapi.balance.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import net.nikr.log.Log;
import org.xml.sax.SAXException;


public class AccountBalanceGetter extends AbstractApiGetter<Response> {

	private Human human;

	public void load(List<Account> accounts, boolean forceUpdate){
		Log.info("Account balance updating:");
		boolean updated = false;
		boolean updateFailed = false;
		for (int a = 0; a < accounts.size(); a++){
			Account account = accounts.get(a);
			List<Human> humans = account.getHumans();
			for (int b = 0; b < humans.size(); b++){
				if (!Program.FORCE_NO_UPDATE) {
					load(humans.get(b), forceUpdate);
					if (isCharacterUpdated() || isCorporationUpdated()){
						updated = true;
					} else {
						updateFailed = true;
					}
				}
			}
		}
		if (updated && !updateFailed){
			Log.info("	Account balance updated (ALL)");
		} else if(updated && updateFailed) {
			Log.info("	Account balance updated (SOME)");
		} else {
			Log.info("	Account balance not updated (NONE)");
		}
	}

	public void load(Human human, boolean forceUpdate){
		this.human = human;
		Date nextUpdate = human.getBalanceNextUpdate();
		load(nextUpdate, forceUpdate, false, "Account balance", human.getName());
		if (human.isUpdateCorporationAssets()){
			load(nextUpdate, forceUpdate, true, "Corporation account balance", human.getCorporation()+" by "+human.getName());
		}
	}

	@Override
	protected Response getResponse(boolean bCorp) throws IOException, SAXException {
		Parser parser = new Parser();
		Response response = parser.getAccountBalance(Human.getApiAuthorization(human), bCorp);
		human.setBalanceNextUpdate( response.getCachedUntil() );
		return response;
	}

	@Override
	protected void ok(Response response, boolean bCorp) {
		List<ApiAccountBalance> accountBalances = new Vector<ApiAccountBalance>(response.getAccountBalances());
		if (bCorp){
			human.setAccountBalancesCorporation(accountBalances);
		} else {
			human.setAccountBalances(accountBalances);
		}
	}


	/*
	

	public static boolean load(SettingsInterface settings, Human human){
		return load(settings, human, false);
	}

	public static boolean load(SettingsInterface settings, Human human, boolean forceUpdate){
		return load(settings, human, forceUpdate, false);
	}

	private static boolean load(SettingsInterface settings, Human human, boolean forceUpdate, boolean bCorp){
		if (settings.isUpdatable(human.getBalanceNextUpdate()) || bCorp || forceUpdate){
			if (human.isUpdateCorporationAssets() && !bCorp){
				load(settings, human, false, true);
			}
			Parser balanceParser = new Parser();
			Response balanceResponse = null;
			try {
				balanceResponse = balanceParser.getAccountBalance(Human.getApiAuthorization(human), bCorp);
				human.setBalanceNextUpdate( balanceResponse.getCachedUntil() );
				if (!balanceResponse.hasError()){
					List<ApiAccountBalance> accountBalances = new Vector<ApiAccountBalance>(balanceResponse.getAccountBalances());
					if (bCorp){
						human.setAccountBalancesCorporation(accountBalances);
					} else {
						human.setAccountBalances(accountBalances);
					}
					if (bCorp) {
						Log.info("	Corporation account balances updated for: "+human.getCorporation()+" by "+human.getName());
					} else {
						Log.info("	Account balance updated for: "+human.getName());
					}
					return true;
				} else {
					ApiError error = balanceResponse.getError();
					if (error.getCode() == 200){
						if (bCorp) {
							Log.info("	Corporation account balances update failed (Not a full API Key)");
						} else {
							Log.info("	Account balance update failed (Not a full API Key)");
						}
					}
					if (bCorp) {
						Log.warning("	Corporation account balances update failed (API ERROR: code: "+error.getCode()+" :: "+error.getError()+")");
					} else {
						Log.warning("	Account balance update failed (API ERROR: code: "+error.getCode()+" :: "+error.getError()+")");
					}
				}
			} catch (IOException ex) {
				if (bCorp) {
					Log.info("	Corporation account balances update failed for: "+human.getCorporation()+" by "+human.getName()+" (NOT FOUND)");
				} else {
					Log.info("	Account balance update failed for: "+human.getName()+" (NOT FOUND)");
				}
			} catch (SAXException ex) {
				if (bCorp) {
					Log.error("Corporation account balances update failed for: "+human.getCorporation()+" by "+human.getName()+" (PARSER ERROR)", ex);
				} else {
					Log.error("Account balance update failed  for: "+human.getName()+" (PARSER ERROR)", ex);
				}
			}
		}
		return false;
	}
	 *
	 */
}