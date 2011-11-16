/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared;

import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;


public class Updatable {

	private boolean updatable;
	private Settings settings;

	public Updatable(Settings settings) {
		this.settings = settings;
	}

	public boolean isUpdatable(){
		List<Account> accounts = settings.getAccounts();
		Date accountsNextUpdate = null;
		Date industryJobsNextUpdate = null;
		Date marketOrdersNextUpdate = null;
		Date assetsNextUpdate = null;
		Date accountBalanceNextUpdate = null;
		Date priceDataNextUpdate = settings.getPriceDataNextUpdate();
		for (int a = 0; a < accounts.size(); a++){
			Account account = accounts.get(a);
			//Account
			accountsNextUpdate = nextUpdate(accountsNextUpdate, account.getCharactersNextUpdate());
			List<Human> humans = account.getHumans();
			for (int b = 0; b < humans.size(); b++){
				Human human = humans.get(b);
				if (human.isShowAssets()){
					industryJobsNextUpdate = nextUpdate(industryJobsNextUpdate, human.getIndustryJobsNextUpdate());
					marketOrdersNextUpdate = nextUpdate(marketOrdersNextUpdate, human.getMarketOrdersNextUpdate());
					assetsNextUpdate = nextUpdate(assetsNextUpdate, human.getAssetNextUpdate());
					accountBalanceNextUpdate = nextUpdate(accountBalanceNextUpdate, human.getBalanceNextUpdate());
				}
			}
		}
		updatable = false;
		isUpdatable(marketOrdersNextUpdate);
		isUpdatable(industryJobsNextUpdate);
		isUpdatable(accountsNextUpdate);
		isUpdatable(accountBalanceNextUpdate);
		isUpdatable(assetsNextUpdate);
		isUpdatable(priceDataNextUpdate, false);
		return updatable;
	}

	private void isUpdatable(Date nextUpdate){
		isUpdatable(nextUpdate, true);
	}

	private void isUpdatable(Date nextUpdate, boolean ignoreOnProxy){
		if (nextUpdate == null) nextUpdate = Settings.getGmtNow();
		if (settings.isUpdatable(nextUpdate, ignoreOnProxy)){
			updatable = true;
		}
	}

	private Date nextUpdate(Date nextUpdate, Date thisUpdate){
		if (nextUpdate == null){
				nextUpdate = thisUpdate;
		}
		if (thisUpdate.before(nextUpdate)){
			nextUpdate = thisUpdate;
		}
		return nextUpdate;
	}
}
