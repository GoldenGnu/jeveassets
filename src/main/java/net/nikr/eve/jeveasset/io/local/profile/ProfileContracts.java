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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.raw.RawContract;
import net.nikr.eve.jeveasset.data.api.raw.RawContractItem;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class ProfileContracts extends ProfileTable {

	private static final String CONTRACTS_OWNERS_TABLE = "contractsowners";
	private static final String CONTRACTS_TABLE = "contracts";
	private static final String CONTRACT_ITEMS_TABLE = "contractitems";

	@Override
	protected boolean isUpdated() {
		return Settings.get().isContractHistory();
	}

	private static void set(PreparedStatement statement, MyContract contract) throws SQLException {
		int index = 0;
		setAttribute(statement, ++index, contract.getAcceptorID());
		setAttribute(statement, ++index, contract.getAssigneeID());
		setAttributeOptional(statement, ++index, contract.getAvailability());
		setAttributeOptional(statement, ++index, contract.getAvailabilityString());
		setAttributeOptional(statement, ++index, contract.getBuyout());
		setAttributeOptional(statement, ++index, contract.getCollateral());
		setAttribute(statement, ++index, contract.getContractID());
		setAttributeOptional(statement, ++index, contract.getDateAccepted());
		setAttributeOptional(statement, ++index, contract.getDateCompleted());
		setAttribute(statement, ++index, contract.getDateExpired());
		setAttribute(statement, ++index, contract.getDateIssued());
		setAttributeOptional(statement, ++index, contract.getEndLocationID());
		setAttribute(statement, ++index, contract.getIssuerCorpID());
		setAttribute(statement, ++index, contract.getIssuerID());
		setAttributeOptional(statement, ++index, contract.getDaysToComplete());
		setAttributeOptional(statement, ++index, contract.getPrice());
		setAttributeOptional(statement, ++index, contract.getReward());
		setAttributeOptional(statement, ++index, contract.getStartLocationID());
		setAttributeOptional(statement, ++index, contract.getStatus());
		setAttributeOptional(statement, ++index, contract.getStatusString());
		setAttributeOptional(statement, ++index, contract.getTitle());
		setAttributeOptional(statement, ++index, contract.getTypeString());
		setAttributeOptional(statement, ++index, contract.getType());
		setAttributeOptional(statement, ++index, contract.getVolume());
		setAttribute(statement, ++index, contract.isForCorp());
		setAttribute(statement, ++index, contract.isESI());
	}

	private static void set(PreparedStatement statement, MyContractItem contractItem) throws SQLException {
		int index = 0;
		setAttribute(statement, ++index, contractItem.getContract().getContractID());
		setAttribute(statement, ++index, contractItem.isIncluded());
		setAttribute(statement, ++index, contractItem.getQuantity());
		setAttribute(statement, ++index, contractItem.getRecordID());
		setAttribute(statement, ++index, contractItem.isSingleton());
		setAttribute(statement, ++index, contractItem.getTypeID());
		setAttributeOptional(statement, ++index, contractItem.getRawQuantity());
		setAttributeOptional(statement, ++index, contractItem.getItemID());
		setAttributeOptional(statement, ++index, contractItem.getLicensedRuns());
		setAttributeOptional(statement, ++index, contractItem.getME());
		setAttributeOptional(statement, ++index, contractItem.getTE());
	}

	/**
	 * Contract items are immutable (IGNORE)
	 * @param connection
	 * @param contractItemsLists 
	 * @throws java.sql.SQLException 
	 */
	public static void updateContractItems(Connection connection, Collection<List<MyContractItem>> contractItemsLists) throws SQLException {
		//Tables exist
		if (!tableExist(connection, CONTRACT_ITEMS_TABLE)) {
			return;
		}

		//Insert data
		String sqlContractItems = "INSERT OR IGNORE INTO " + CONTRACT_ITEMS_TABLE + " ("
				+ "	contractid,"
				+ "	included,"
				+ "	quantity,"
				+ "	recordid,"
				+ "	singleton,"
				+ "	typeid,"
				+ "	rawquantity,"
				+ "	itemid,"
				+ "	runs,"
				+ "	me,"
				+ "	te)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sqlContractItems)) {
			Rows rows = new Rows(statement, contractItemsLists, new RowSize<List<MyContractItem>>() {
				@Override
				public int getSize(List<MyContractItem> contractItems) {
					return contractItems.size();
				}
			});
			for (Collection<MyContractItem> contractItems : contractItemsLists) {
				for (MyContractItem contractItem : contractItems) {
					set(statement, contractItem);
					rows.addRow();
				}
			}
		}
	}

	/**
	 * Contracts are mutable (REPLACE).Owners are immutable (IGNORE)
	 * @param connection
	 * @param accountID
	 * @param contracts 
	 * @throws java.sql.SQLException 
	 */
	public static void updateContracts(Connection connection, String accountID, Collection<MyContract> contracts) throws SQLException {
		//Tables exist
		if (!tableExist(connection, CONTRACTS_OWNERS_TABLE, CONTRACTS_TABLE)) {
			return;
		}

		//Insert data
		String sqlOwners = "INSERT OR IGNORE INTO " + CONTRACTS_OWNERS_TABLE + " ("
				+ "	accountid,"
				+ "	contractid)"
				+ " VALUES (?,?)"
				;
		try (PreparedStatement statement = connection.prepareStatement(sqlOwners)) {
			Rows rows = new Rows(statement, contracts.size());
			for (MyContract contract : contracts) {
				int index = 0;
				setAttribute(statement, ++index, accountID);
				setAttribute(statement, ++index, contract.getContractID());
				rows.addRow();
			}
		}

		String sqlContracts = "INSERT OR REPLACE INTO " + CONTRACTS_TABLE + " ("
				+ "	acceptorid,"
				+ "	assigneeid,"
				+ "	availability,"
				+ "	availabilitystring,"
				+ "	buyout,"
				+ "	collateral,"
				+ "	contractid,"
				+ "	dateaccepted,"
				+ "	datecompleted,"
				+ "	dateexpired,"
				+ "	dateissued,"
				+ "	endstationid,"
				+ "	issuercorpid,"
				+ "	issuerid,"
				+ "	numdays,"
				+ "	price,"
				+ "	reward,"
				+ "	startstationid,"
				+ "	status,"
				+ "	statusstring,"
				+ "	title,"
				+ "	typestring,"
				+ "	type,"
				+ "	volume,"
				+ "	forcorp,"
				+ "	esi)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sqlContracts)) {
			Rows rows = new Rows(statement, contracts.size());
			for (MyContract contract : contracts) {
				set(statement, contract);
				rows.addRow();
			}
		}
	}

	@Override
	protected void insert(Connection connection, List<EsiOwner> esiOwners) throws SQLException {
		//Delete all data
		tableDelete(connection, CONTRACTS_OWNERS_TABLE, CONTRACTS_TABLE, CONTRACT_ITEMS_TABLE);

		//Insert data
		String sqlOwners = "INSERT OR IGNORE INTO " + CONTRACTS_OWNERS_TABLE + " ("
				+ "	accountid,"
				+ "	contractid)"
				+ " VALUES (?,?)"
				;
		try (PreparedStatement statement = connection.prepareStatement(sqlOwners)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner owner) {
					return owner.getContracts().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyContract contract : owner.getContracts().keySet()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getAccountID());
					setAttribute(statement, ++index, contract.getContractID());
					rows.addRow();
				}
			}
		}

		String sqlContracts = "INSERT OR REPLACE INTO " + CONTRACTS_TABLE + " ("
				+ "	acceptorid,"
				+ "	assigneeid,"
				+ "	availability,"
				+ "	availabilitystring,"
				+ "	buyout,"
				+ "	collateral,"
				+ "	contractid,"
				+ "	dateaccepted,"
				+ "	datecompleted,"
				+ "	dateexpired,"
				+ "	dateissued,"
				+ "	endstationid,"
				+ "	issuercorpid,"
				+ "	issuerid,"
				+ "	numdays,"
				+ "	price,"
				+ "	reward,"
				+ "	startstationid,"
				+ "	status,"
				+ "	statusstring,"
				+ "	title,"
				+ "	typestring,"
				+ "	type,"
				+ "	volume,"
				+ "	forcorp,"
				+ "	esi)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sqlContracts)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner owner) {
					return owner.getContracts().keySet().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyContract contract : owner.getContracts().keySet()) {
					set(statement, contract);
					rows.addRow();
				}
			}
		}

		String sqlContractItems = "INSERT OR IGNORE INTO " + CONTRACT_ITEMS_TABLE + " ("
				+ "	contractid,"
				+ "	included,"
				+ "	quantity,"
				+ "	recordid,"
				+ "	singleton,"
				+ "	typeid,"
				+ "	rawquantity,"
				+ "	itemid,"
				+ "	runs,"
				+ "	me,"
				+ "	te)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sqlContractItems)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner owner) {
					int size = 0;
					for (List<MyContractItem> contractItems : owner.getContracts().values()) {
						size += contractItems.size();
				}
					return size;
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (List<MyContractItem> contractItems : owner.getContracts().values()) {
					for (MyContractItem contractItem : contractItems) {
						set(statement, contractItem);
						rows.addRow();
					}
				}
			}
		}
	}

	@Override
	protected void select(Connection connection, List<EsiOwner> esiOwners, Map<String, EsiOwner> owners) throws SQLException {
		String ownerSQL = "SELECT * FROM " + CONTRACTS_OWNERS_TABLE;
		Map<Integer, Set<EsiOwner>> contractOwners = new HashMap<>();
		Map<EsiOwner, Map<MyContract, List<MyContractItem>>> contracts = new HashMap<>();
		try (PreparedStatement statement = connection.prepareStatement(ownerSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				String accountID = getString(rs, "accountid");
				int contractID = getInt(rs, "contractid");
				Set<EsiOwner> set = contractOwners.get(contractID);
				if (set == null) {
					set = new HashSet<>();
					contractOwners.put(contractID, set);
				}
				EsiOwner owner = owners.get(accountID);
				if (owner != null) {
					set.add(owner);
					contracts.put(owner, new HashMap<>());
				}
			}
		}
		String contractsSQL = "SELECT * FROM " + CONTRACTS_TABLE;
		Map<Integer, MyContract> contractIDs = new HashMap<>();
		try (PreparedStatement statement = connection.prepareStatement(contractsSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				RawContract rawContract = RawContract.create();
				int acceptorID = getInt(rs, "acceptorid");
				int assigneeID = getInt(rs, "assigneeid");
				String availabilityString = getStringOptional(rs, "availabilitystring");
				String availabilityEnum = getStringOptional(rs, "availability");
				Double buyout = getDoubleOptional(rs, "buyout");
				Double collateral = getDoubleOptional(rs, "collateral");
				int contractID = getInt(rs, "contractid");
				Date dateAccepted = getDateOptional(rs, "dateaccepted");
				Date dateCompleted = getDateOptional(rs, "datecompleted");
				Date dateExpired = getDate(rs, "dateexpired");
				Date dateIssued = getDate(rs, "dateissued");
				Long endLocationID = getLongOptional(rs, "endstationid");
				int issuerCorporationID = getInt(rs, "issuercorpid");
				int issuerID = getInt(rs, "issuerid");
				Integer daysToComplete = getIntOptional(rs, "numdays");
				Double price = getDoubleOptional(rs, "price");
				Double reward = getDoubleOptional(rs, "reward");
				Long startLocationID = getLongOptional(rs, "startstationid");
				String statusString = getStringOptional(rs, "statusstring");
				String statusEnum = getStringOptional(rs, "status");
				String title = getStringOptional(rs, "title");
				String typeString = getStringOptional(rs, "typestring");
				String typeEnum = getStringOptional(rs, "type");
				Double volume = getDoubleOptional(rs, "volume");
				boolean forCorporation = getBoolean(rs, "forcorp");
				boolean esi = getBooleanNotNull(rs, "esi", true);
				rawContract.setAcceptorID(acceptorID);
				rawContract.setAssigneeID(assigneeID);
				rawContract.setAvailability(RawConverter.toContractAvailability(availabilityEnum, availabilityString));
				rawContract.setAvailabilityString(availabilityString);
				rawContract.setBuyout(buyout);
				rawContract.setCollateral(collateral);
				rawContract.setContractID(contractID);
				rawContract.setDateAccepted(dateAccepted);
				rawContract.setDateCompleted(dateCompleted);
				rawContract.setDateExpired(dateExpired);
				rawContract.setDateIssued(dateIssued);
				rawContract.setDaysToComplete(daysToComplete);
				rawContract.setEndLocationID(endLocationID);
				rawContract.setForCorporation(forCorporation);
				rawContract.setIssuerCorporationID(issuerCorporationID);
				rawContract.setIssuerID(issuerID);
				rawContract.setPrice(price);
				rawContract.setReward(reward);
				rawContract.setStartLocationID(startLocationID);
				rawContract.setStatus(RawConverter.toContractStatus(statusEnum, statusString));
				rawContract.setStatusString(statusString);
				rawContract.setTitle(title);
				rawContract.setTypeString(typeString);
				rawContract.setType(RawConverter.toContractType(typeEnum, typeString));
				rawContract.setVolume(volume);

				MyContract contract = DataConverter.toMyContract(rawContract);
				contract.setESI(esi);

				contractIDs.put(contractID, contract);

				Set<EsiOwner> set = contractOwners.get(contractID);
				if (set != null) {
					for (EsiOwner esiOwner : set) {
						Map<MyContract, List<MyContractItem>> map = contracts.get(esiOwner);
						map.put(contract, new ArrayList<>());
					}
				}
			}
		}
		String contractItemsSQL = "SELECT * FROM " + CONTRACT_ITEMS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(contractItemsSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				RawContractItem contractItem = RawContractItem.create();
				int contractID = getInt(rs, "contractid");
				boolean included = getBoolean(rs, "included");
				int quantity = getInt(rs, "quantity");
				long recordID = getLong(rs, "recordid");
				boolean singleton = getBoolean(rs, "singleton");
				int typeID = getInt(rs, "typeid");
				Integer rawQuantity = getIntOptional(rs, "rawquantity");
				Long itemID = getLongOptional(rs, "itemid");
				Integer runs = getIntOptional(rs, "runs");
				Integer materialEfficiency = getIntOptional(rs, "me");
				Integer timeEfficiency = getIntOptional(rs, "te");
				contractItem.setIncluded(included);
				contractItem.setQuantity(quantity);
				contractItem.setRecordID(recordID);
				contractItem.setSingleton(singleton);
				contractItem.setTypeID(typeID);
				contractItem.setRawQuantity(rawQuantity);
				contractItem.setItemID(itemID);
				contractItem.setLicensedRuns(runs);
				contractItem.setME(materialEfficiency);
				contractItem.setTE(timeEfficiency);
				MyContract contract = contractIDs.get(contractID);
				if (contract == null) {
					continue;
				}
				Set<EsiOwner> set = contractOwners.get(contractID);
				if (set != null) {
					for (EsiOwner esiOwner : set) {
						Map<MyContract, List<MyContractItem>> map = contracts.get(esiOwner);
						List<MyContractItem> contractItems = map.get(contract);
						if (contractItems == null) {
							continue;
						}
						contractItems.add(DataConverter.toMyContractItem(contractItem, contract));
					}
				}
			}
		}
		for (Map.Entry<EsiOwner, Map<MyContract, List<MyContractItem>>> entry : contracts.entrySet()) {
			EsiOwner owner = entry.getKey();
			owner.setContracts(entry.getValue());
		}
	}

	@Override
	protected boolean isEmpty(Connection connection) throws SQLException {
		return !tableExist(connection, CONTRACTS_OWNERS_TABLE, CONTRACTS_TABLE, CONTRACT_ITEMS_TABLE);
	}

	@Override
	protected void create(Connection connection) throws SQLException {
		if (!tableExist(connection, CONTRACTS_OWNERS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + CONTRACTS_OWNERS_TABLE + " ("
					+ "	accountid TEXT,\n"
					+ "	contractid INTEGER,"
					+ "	UNIQUE(accountid, contractid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
		if (!tableExist(connection, CONTRACTS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + CONTRACTS_TABLE + " ("
					+ "	acceptorid INTEGER,"
					+ "	assigneeid INTEGER,"
					+ "	availability TEXT,"
					+ "	availabilitystring TEXT,"
					+ "	buyout REAL,"
					+ "	collateral REAL,"
					+ "	contractid INTEGER,"
					+ "	dateaccepted INTEGER,"
					+ "	datecompleted INTEGER,"
					+ "	dateexpired INTEGER,"
					+ "	dateissued INTEGER,"
					+ "	endstationid INTEGER,"
					+ "	issuercorpid INTEGER,"
					+ "	issuerid INTEGER,"
					+ "	numdays INTEGER,"
					+ "	price REAL,"
					+ "	reward REAL,"
					+ "	startstationid INTEGER,"
					+ "	status TEXT,"
					+ "	statusstring TEXT,"
					+ "	title TEXT,"
					+ "	typestring TEXT,"
					+ "	type TEXT,"
					+ "	volume REAL,"
					+ "	forcorp NUMERIC,"
					+ "	esi NUMERIC,"
					+ "	UNIQUE(contractid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
		if (!tableExist(connection, CONTRACT_ITEMS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS  " + CONTRACT_ITEMS_TABLE + " ("
					+ "	contractid INTEGER,"
					+ "	included NUMERIC,"
					+ "	quantity INTEGER,"
					+ "	recordid INTEGER,"
					+ "	singleton NUMERIC,"
					+ "	typeid INTEGER,"
					+ "	rawquantity INTEGER,"
					+ "	itemid INTEGER,"
					+ "	runs INTEGER,"
					+ "	me INTEGER,"
					+ "	te INTEGER,"
					+ "	UNIQUE(recordid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
	}
}
