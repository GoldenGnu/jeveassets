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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.profile.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProfileDatabase {
	private static final Logger LOG = LoggerFactory.getLogger(ProfileDatabase.class);

	private static final Collection<Future<Boolean>> UPDATES = Collections.synchronizedCollection(new ArrayList<>());

	private static final ExecutorService UPDATES_THREAD_POOL = Executors.newSingleThreadExecutor();

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

		public void insert(Connection connection, List<EsiOwner> esiOwners, boolean full) throws SQLException {
			if (esiOwners == null) {
				esiOwners = new ArrayList<>(); //Ensure never null
			}
			if (!full && profileTable.isUpdated()) {
				return; //Ignore updated
			}
			profileTable.insert(connection, esiOwners);
		}

		public void select(Connection connection, List<EsiOwner> esiOwners) throws SQLException {
			Map<String, EsiOwner> owners = new HashMap<>();
			for (EsiOwner esiOwner : esiOwners) {
				owners.put(esiOwner.getAccountID(), esiOwner);
			}
			profileTable.select(connection, esiOwners, owners);
		}

		public void create(Connection connection) throws SQLException {
			profileTable.create(connection);
		}
	}

	private static Connection updateConnection = null;
	private static String updateConnectionUrl = null;

	public static synchronized void setUpdateConnectionUrl(Profile profile) {
		if (profile == null) {
			updateConnectionUrl = null;
		} else {
			updateConnectionUrl = getConnectionUrl(profile.getSQLiteFilename());
		}
		closeUpdateConnection();
	}

	private static synchronized Connection getUpdateConnection() throws SQLException {
		if (updateConnectionUrl == null) {
			return null;
		}
		if (updateConnection == null) {
			updateConnection = DriverManager.getConnection(updateConnectionUrl);
			updateConnection.setAutoCommit(false);
		}
		return updateConnection;
	}

	private static synchronized void closeUpdateConnection() {
		if (updateConnection != null) {
			try {
				updateConnection.setAutoCommit(true);
				updateConnection.close();
			} catch (SQLException ex) {
				logError(ex);
			}
			updateConnection = null;
		}
	}

	private static String getConnectionUrl(String filename) {
		return "jdbc:sqlite:" + filename;
	}

	public static boolean load(Profile profile) {
		backup(profile);
		String connectionUrl = getConnectionUrl(profile.getSQLiteFilename());
		try (Connection connection = DriverManager.getConnection(connectionUrl)) {
			for (Table table : Table.values()) {
				table.select(connection, profile.getEsiOwners());
			}
			return true;
		} catch (SQLException ex) {
			logError(ex);
			return false;
		}
	}

	public static void update(ProfileConnection profileConnection) {
		UPDATES.add(UPDATES_THREAD_POOL.submit(new Update(profileConnection)));
	}

	public static boolean waitForUpdates() {
		boolean ok = true;
		synchronized (UPDATES) {
			for (Future<Boolean> update : UPDATES) {
				try {
					ok = update.get() && ok;
				} catch (InterruptedException ex) {
					logError(ex);
					ok = false;
				} catch (ExecutionException ex) {
					logError(ex);
					ok = false;
				}
			}
		}
		UPDATES.clear();
		closeUpdateConnection();
		return ok;
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
		String connectionUrl = getConnectionUrl(profile.getSQLiteFilename());
		if (!full) {
			waitForUpdates();
		}
		try (Connection connection = DriverManager.getConnection(connectionUrl);) {
			try {
				connection.setAutoCommit(false);
				for (Table profileTable : tables) {
					profileTable.create(connection);
					profileTable.insert(connection, profile.getEsiOwners(), full);
				}
				connection.commit();
				connection.setAutoCommit(true);
			} catch (SQLException ex) {
				connection.rollback();
				throw ex;
			}
			return true;
		} catch (SQLException ex) {
			logError(ex);
			return false;
		}
	}

	private static void backup(final Profile profile) {
		String filename = profile.getSQLiteFilename();
		if (!exists(filename)) {
			return;
		}
		if (exists(profile.getBackupSQLiteFilename())) {
			return;
		}
		String connectionUrl = getConnectionUrl(filename);
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				final Statement statement = connection.createStatement()
				) {
			statement.executeUpdate("BACKUP TO " + profile.getBackupSQLiteFilename()) ;
		} catch (SQLException ex) {
			logError(ex);
		}
    }

	private static boolean exists(final String filename) {
		File backup = new File(filename);
		return backup.exists();
    }

	private static class Update implements Callable<Boolean> {

		private final ProfileConnection profileConnection;

		public Update(ProfileConnection profileConnection) {
			this.profileConnection = profileConnection;
		}

		@Override
		public Boolean call() throws Exception {
			Connection connection = getUpdateConnection();
			if (connection == null) {
				return false;
			}
			try {
				profileConnection.update(connection);
				//Only commit once, when everything is done - so we can rollback on any errors 
				connection.commit();
				return true;
			} catch (SQLException ex) {
				connection.rollback();
				logError(ex);
				return false;
			}
		}
	}

	private static void logError(Exception ex) {
		LOG.error(ex.getMessage(), ex);
		throw new RuntimeException(ex);
	}
}
