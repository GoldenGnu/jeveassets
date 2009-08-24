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
import com.beimin.eveapi.balance.ApiAccountBalance;
import com.beimin.eveapi.balance.Parser;
import com.beimin.eveapi.balance.Response;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.log.Log;
import org.xml.sax.SAXException;


public class EveApiAccountBalanceReader {

	public static boolean load(Human human){
		return load(human, false);
	}

	public static boolean load(Human human, boolean apiKeyCheck){
		return load(human, apiKeyCheck, false);
	}

	private static boolean load(Human human, boolean apiKeyCheck, boolean bCorp){
		if (human.isBalanceUpdatable() || bCorp || apiKeyCheck){
			if (human.isUpdateCorporationAssets() && !bCorp){
				load(human, false, true);
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
				} else {
					ApiError error = balanceResponse.getError();
					if (error.getCode() == 200){
						if (bCorp) {
							Log.info("Corporation account balances update failed (Not a full API Key)");
						} else {
							Log.info("Account balance update failed (Not a full API Key)");
						}
						return false;
					}
					if (bCorp) {
						Log.warning("Corporation account balances update failed (API ERROR: code: "+error.getCode()+" :: "+error.getError()+")");
					} else {
						Log.warning("Account balance update failed (API ERROR: code: "+error.getCode()+" :: "+error.getError()+")");
					}
					return false;
				}
			} catch (IOException ex) {
				if (bCorp) {
					Log.info("Corporation account balances update for: "+human.getCorporation()+" by "+human.getName()+" failed");
				} else {
					Log.info("Account balance update for: "+human.getName()+" failed");
				}
				return false;
			} catch (SAXException ex) {
				if (bCorp) {
					Log.error("Corporation account balances update failed (PARSER ERROR)", ex);
				} else {
					Log.error("Account balance update failed (PARSER ERROR)", ex);
				}
				return false;
			}
			if (bCorp) {
				Log.info("		Corporation account balances updated");
			} else {
				Log.info("		Account balance updated");
			}
			return true;
		} else {
			if (bCorp) {
				Log.info("		Corporation account balances not updated (NOT ALLOWED YET)");
			} else {
				Log.info("		Account balances not updated (NOT ALLOWED YET)");
			}
			return false;
		}
	}
}