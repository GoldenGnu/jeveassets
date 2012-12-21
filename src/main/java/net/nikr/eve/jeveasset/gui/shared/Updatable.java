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

package net.nikr.eve.jeveasset.gui.shared;

import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Settings;


public class Updatable {

	private boolean updatable;
	private Settings settings;

	public Updatable(final Settings settings) {
		this.settings = settings;
	}

	public boolean isUpdatable() {
		Date accountsNextUpdate = null;
		Date industryJobsNextUpdate = null;
		Date marketOrdersNextUpdate = null;
		Date assetsNextUpdate = null;
		Date accountBalanceNextUpdate = null;
		Date priceDataNextUpdate = settings.getPriceDataNextUpdate();
		for (Account account : settings.getAccounts()) {
			//Account
			accountsNextUpdate = nextUpdate(accountsNextUpdate, account.getAccountNextUpdate());
			for (Owner owner : account.getOwners()) {
				if (owner.isShowAssets()) {
					industryJobsNextUpdate = nextUpdate(industryJobsNextUpdate, owner.getIndustryJobsNextUpdate());
					marketOrdersNextUpdate = nextUpdate(marketOrdersNextUpdate, owner.getMarketOrdersNextUpdate());
					assetsNextUpdate = nextUpdate(assetsNextUpdate, owner.getAssetNextUpdate());
					accountBalanceNextUpdate = nextUpdate(accountBalanceNextUpdate, owner.getBalanceNextUpdate());
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

	private void isUpdatable(final Date nextUpdate) {
		isUpdatable(nextUpdate, true);
	}

	private void isUpdatable(Date nextUpdate, final boolean ignoreOnProxy) {
		if (nextUpdate == null) {
			nextUpdate = Settings.getNow();
		}
		if (settings.isUpdatable(nextUpdate, ignoreOnProxy)) {
			updatable = true;
		}
	}

	private Date nextUpdate(Date nextUpdate, final Date thisUpdate) {
		if (nextUpdate == null) {
				nextUpdate = thisUpdate;
		}
		if (thisUpdate.before(nextUpdate)) {
			nextUpdate = thisUpdate;
		}
		return nextUpdate;
	}
}
