/*
 * Copyright 2009-2019 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.ApiType;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.settings.Settings;


public class Updatable {

	private final Program program;

	public Updatable(final Program program) {
		this.program = program;
	}

	public boolean isUpdatable() {
		if (isUpdatable(program.getPriceDataGetter().getNextUpdate())) {
			return true;
		}
		for (OwnerType owner : program.getOwnerTypes()) {
			if (!owner.isShowOwner() || owner.isInvalid() || owner.isExpired() || owner.getAccountAPI() == ApiType.EVE_ONLINE) {
				continue;
			}
			if (owner.isAssetList() && isUpdatable(owner.getAssetNextUpdate())){
				return true;
			}
			if (owner.isAccountBalance() && isUpdatable(owner.getBalanceNextUpdate())){
				return true;
			}
			if (owner.isIndustryJobs() && isUpdatable(owner.getIndustryJobsNextUpdate())){
				return true;
			}
			if (owner.isMarketOrders() && isUpdatable(owner.getMarketOrdersNextUpdate())){
				return true;
			}
			if (owner.isJournal() && isUpdatable(owner.getJournalNextUpdate())){
				return true;
			}
			if (owner.isTransactions() && isUpdatable(owner.getTransactionsNextUpdate())){
				return true;
			}
			if (owner.isContracts() && isUpdatable(owner.getContractsNextUpdate())){
				return true;
			}
			if (owner.isBlueprints() && isUpdatable(owner.getBlueprintsNextUpdate())){
				return true;
			}
			if (owner.isBookmarks() && isUpdatable(owner.getBookmarksNextUpdate())){
				return true;
			}
		}
		return false;
	}

	private boolean isUpdatable(Date nextUpdate) {
		if (nextUpdate == null) {
			return false;
		}
		return Settings.get().isUpdatable(nextUpdate);
	}
}
