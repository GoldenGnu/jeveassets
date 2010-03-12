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

import com.beimin.eveapi.balance.ApiAccountBalance;
import com.beimin.eveapi.balance.Parser;
import com.beimin.eveapi.balance.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.shared.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import org.xml.sax.SAXException;


public class AccountBalanceGetter extends AbstractApiGetter<Response> {

	public AccountBalanceGetter() {
		super("Account Balance", true, false);
	}

	@Override
	public void load(UpdateTask updateTask, boolean forceUpdate, Human human) {
		super.load(updateTask, forceUpdate, human);
	}

	@Override
	public void load(UpdateTask updateTask, boolean forceUpdate, List<Account> accounts) {
		super.load(updateTask, forceUpdate, accounts);
	}

	@Override
	protected Response getResponse(boolean bCorp) throws IOException, SAXException {
		Parser parser = new Parser();
		return parser.getAccountBalance(Human.getApiAuthorization(getHuman()), bCorp);
	}

	@Override
	protected void setNextUpdate(Date nextUpdate) {
		getHuman().setBalanceNextUpdate(nextUpdate);
	}

	@Override
	protected Date getNextUpdate() {
		return getHuman().getBalanceNextUpdate();
	}

	@Override
	protected void setData(Response response, boolean bCorp) {
		List<ApiAccountBalance> accountBalances = new Vector<ApiAccountBalance>(response.getAccountBalances());
		if (bCorp){
			getHuman().setAccountBalancesCorporation(accountBalances);
		} else {
			getHuman().setAccountBalances(accountBalances);
		}
	}
}