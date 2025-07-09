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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.profile.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProfileDatabase {
	private static final Logger LOG = LoggerFactory.getLogger(ProfileDatabase.class);

	private static final BlockingQueue<Update> UPDATES = new LinkedBlockingQueue<>();

	public static enum Table {
		OWNERS(new ProfileOwners()),
		ASSETS(new ProfileAssets()),
		CLONES(new ProfileClones()),
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

		public void updateTable(Connection connection) throws SQLException {
			profileTable.updateTable(connection);
		}

		public boolean isEmpty(Connection connection) throws SQLException {
			return profileTable.isEmpty(connection);
		}
	}

	private static Connection updateConnection = null;
	private static String updateConnectionUrl = null;
	private static Updater updater = null;

	public static synchronized void setUpdateConnectionUrl(Profile profile) {
		if (updater == null) {
			updater = new Updater();
			updater.start();
		}
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
			int loaded = 0;
			for (Table table : Table.values()) {
				if (!table.isEmpty(connection)) {
					table.updateTable(connection);
					table.select(connection, profile.getEsiOwners());
					loaded++;
				} else if (table == Table.OWNERS) {
					return false; //Fatal error
				} else if (table == Table.CLONES) {
					loaded++; //No problem (new talbes)
				}
			}
			if (loaded == Table.values().length) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException ex) {
			logError(ex);
			return false;
		}
	}

	public static void update(ProfileConnection profileConnection) {
		UPDATES.add(new Update(profileConnection));
	}

	public static void waitForUpdates() {
		try {
			synchronized (UPDATES) {
				if (!UPDATES.isEmpty())  {
					UPDATES.wait();
				}
			}
		} catch (InterruptedException ex) {
			//No problem
		}
		closeUpdateConnection();
	}

	public static boolean save(Profile profile) {
		return save(profile, Table.values());
	}

	public static boolean save(Profile profile, Table table) {
		return save(profile, Collections.singleton(table));
	}

	private static boolean save(Profile profile, Table[] tables) {
		return save(profile, Arrays.asList(tables));
	}

	private static boolean save(Profile profile, Collection<Table> tables) {
		String connectionUrl = getConnectionUrl(profile.getSQLiteFilename());
		waitForUpdates();
		try (Connection connection = DriverManager.getConnection(connectionUrl);) {
			try {
				connection.setAutoCommit(false);
				for (Table profileTable : tables) {
					boolean full = profileTable.isEmpty(connection);
					profileTable.create(connection);
					profileTable.updateTable(connection);
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
		backup(profile.getSQLiteFilename(), profile.getBackupSQLiteFilename());
	}

	private static void backup(final String source, String backup) {
		File backupFile = new File(backup);
		File sourceFile = new File(source);
		if (sourceFile.exists() && !backupFile.exists()) {
			try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(backupFile));
					InputStream in = new FileInputStream(sourceFile)) {
				ZipEntry e = new ZipEntry(sourceFile.getName());
				out.putNextEntry(e);
				byte[] buffer = new byte[8192];
				int len;
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				out.closeEntry();
				LOG.info("Backup Created: " + backupFile.getName());
			} catch (IOException ex) {
				LOG.error("Failed to create backup for new program version", ex);
			}
		}
	}

	private static class Update {

		private final ProfileConnection profileConnection;

		public Update(ProfileConnection profileConnection) {
			this.profileConnection = profileConnection;
		}

		public void doUpdate() throws SQLException {
			Connection connection = getUpdateConnection();
			if (connection == null) {
				return;
			}
			try {
				profileConnection.update(connection);
				//Only commit once, when everything is done - so we can rollback on any errors 
				connection.commit();
			} catch (SQLException ex) {
				connection.rollback();
				logError(ex);
			}
		}
	}

	private static class Updater extends Thread {

		@Override
		public void run() {
			while (true) {
				try {
					Update update = UPDATES.take();
					update.doUpdate();
				} catch (InterruptedException ex) {
					//No problem
				} catch (SQLException ex) {
					logError(ex);
				}
				synchronized (UPDATES) {
					if (UPDATES.isEmpty()) {
						UPDATES.notifyAll();
					}
				}
			}
		}

	}

	private static void logError(Exception ex) {
		LOG.error(ex.getMessage(), ex);
		throw new RuntimeException(ex);
	}
}
