/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.local.profile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.profile.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProfileDatabase {
	private static final Logger LOG = LoggerFactory.getLogger(ProfileDatabase.class);

	private static enum Table {
		OWNERS(new ProfileOwners()),
		ASSETS(new ProfileAssets()),
		CONTRACTS(new ProfileContracts()),
		ACTIVE_SHIP(new ProfileActiveShip()),
		ACCOUNT_BALANCES(new ProfileAccountBalances()),
		MARKET_ORDERS(new ProfileMarketOrders()),
		JOURNALS(new ProfileJournals()),
		TRANSACTIONS(new ProfileTransactions()),
		INDUSTRY_JOBS(new ProfileIndustryJobs()),
		BLUEPRINTS(new ProfileBlueprints()),
		ASSET_DIVISIONS(new ProfileAssetDivisions()),
		WALLET_DIVISIONS(new ProfileWalletDivisions()),
		SKILLS(new ProfileSkills()),
		MINING(new ProfileMining())
		;

		private final ProfileTable profileTable;

		private Table(ProfileTable databaseTable) {
			this.profileTable = databaseTable;
		}

		public boolean insert(Connection connection, final List<EsiOwner> esiOwners) {
			InsertReturn insert = profileTable.insert(connection, esiOwners);
			switch (insert) {
				case MISSING_DATA:
					return false;
				case ROLLBACK:
					profileTable.rollback(connection);
					return false;
				case OK:
					//Only commit once, when everything is done - so we can rollback on any errors
					profileTable.commit(connection);
					return true;
				default:
					return false; //?
			}
		}
		public boolean select(Connection connection, List<EsiOwner> esiOwners) {
			Map<Long, EsiOwner> owners = new HashMap<>();
			for (EsiOwner esiOwner : esiOwners) {
				owners.put(esiOwner.getOwnerID(), esiOwner);
			}
			return profileTable.select(connection, esiOwners, owners);
		}
		public boolean create(Connection connection) {
			return profileTable.create(connection);
		}
	}

	public static enum InsertReturn {
		OK, ROLLBACK, MISSING_DATA
	}
	

	private static String getConnectionUrl(String filename) {
		return "jdbc:sqlite:" + filename;
	}

	public static boolean load(Profile profile) {
		boolean ok = true;
		String connectionUrl = getConnectionUrl(profile.getSQLiteFilename());
		
		try (Connection connection = DriverManager.getConnection(connectionUrl)) {
			for (Table table : Table.values()) {
				ok = table.select(connection, profile.getEsiOwners()) && ok;
			}
			return ok;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
	}

	public static boolean save(Profile profile) {
		return save(profile, profile.getSQLiteFilename());
	}

	public static boolean save(Profile profile, String filename) {
		boolean ok = true;
		String connectionUrl = getConnectionUrl(filename);
		try (Connection connection = DriverManager.getConnection(connectionUrl)) {
			connection.setAutoCommit(false);
			for (Table profileTable : Table.values()) {
				ok = profileTable.create(connection) && ok;
				ok = profileTable.insert(connection, profile.getEsiOwners()) && ok;
			}
			connection.setAutoCommit(true);
			return ok;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
	}
}
