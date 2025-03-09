/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.profile.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProfileDatabase {
	private static final Logger LOG = LoggerFactory.getLogger(ProfileDatabase.class);

	private static final Collection<Update> UPDATES = Collections.synchronizedCollection(new ArrayList<>());

	public static enum Table {
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

		public boolean insert(Connection connection, List<EsiOwner> esiOwners, boolean full) {
			if (esiOwners == null) {
				esiOwners = new ArrayList<>(); //Ensure never null
			}
			if (!full && profileTable.isUpdated()) {
				return true; //Ignore updated
			}
			return profileTable.insert(connection, esiOwners);
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

	private static String updateConnectionUrl = null;

	public static void setUpdateConnectionUrl(Profile profile) {
		if (profile == null) {
			ProfileDatabase.updateConnectionUrl = null;
		} else {
			ProfileDatabase.updateConnectionUrl = getConnectionUrl(profile.getSQLiteFilename());
		}
	}

	private static String getConnectionUrl(String filename) {
		return "jdbc:sqlite:" + filename;
	}

	public static boolean load(Profile profile) {
		backup(profile);
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

	public static void update(ProfileConnection profileConnection) {
		Update update = new Update(profileConnection);
		UPDATES.add(update);
		update.start();
	}

	public static boolean save(Profile profile, boolean full) {
		return save(profile, Table.values(), full);
	}

	public static boolean save(Profile profile, Table table, boolean full) {
		return save(profile, Collections.singleton(table), full);
	}

	private static boolean save(Profile profile, Table[] tables, boolean full) {
		return save(profile, Arrays.asList(tables), full);
	}

	private static boolean save(Profile profile, Collection<Table> tables, boolean full) {
		boolean ok = true;
		String connectionUrl = getConnectionUrl(profile.getSQLiteFilename());
		if (!full) {
			synchronized (UPDATES) {
				for (Update update : UPDATES) {
					try {
						update.join();
						ok = update.ok && ok;
					} catch (InterruptedException ex) {
						//No problem
					}
				}
			}
			UPDATES.clear();
		}
		try (Connection connection = DriverManager.getConnection(connectionUrl)) {
			connection.setAutoCommit(false);
			for (Table profileTable : tables) {
				ok = profileTable.create(connection) && ok;
				ok = profileTable.insert(connection, profile.getEsiOwners(), full) && ok;
			}
			if (ok) {
				//Only commit once, when everything is done - so we can rollback on any errors 
				connection.commit();
			} else {
				connection.rollback();
			}
			connection.setAutoCommit(true);
			return ok;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
	}

	private static void backup(final Profile profile) {
		String filename = profile.getSQLiteFilename();
		if (!exists(filename)) {
			return;
		}
		if (exists(profile.getSQLiteBackupFilename())) {
			return;
		}
		String connectionUrl = getConnectionUrl(filename);
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				final Statement statement = connection.createStatement()
				) {
			statement.executeUpdate("BACKUP TO " + profile.getSQLiteBackupFilename()) ;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			
		}
    }

	private static boolean exists(final String filename) {
		File backup = new File(filename);
		return backup.exists();
    }

	private static class Update extends Thread {

		boolean ok = false;

		private final ProfileConnection profileConnection;

		public Update(ProfileConnection profileConnection) {
			this.profileConnection = profileConnection;
		}

		@Override
		public void run() {
			boolean ok = false;
			if (updateConnectionUrl == null) {
				return;
			}
			try (Connection connection = DriverManager.getConnection(updateConnectionUrl)) {
				connection.setAutoCommit(false);
				ok = profileConnection.update(connection);
				if (ok) {
					//Only commit once, when everything is done - so we can rollback on any errors 
					connection.commit();
				} else {
					connection.rollback();
				}
				connection.setAutoCommit(true);
			} catch (SQLException ex) {
				LOG.error(ex.getMessage(), ex);
			}
			this.ok = ok;
		}
		
	}
}
