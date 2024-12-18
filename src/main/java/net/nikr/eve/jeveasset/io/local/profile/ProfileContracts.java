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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class ProfileContracts extends ProfileTable {

	private static final String CONTRACTS_OWNERS_TABLE = "contractsowners";
	private static final String CONTRACTS_TABLE = "contracts";
	private static final String CONTRACT_ITEMS_TABLE = "contractitems";

	@Override
	protected boolean insert(Connection connection, List<EsiOwner> esiOwners) {
		//Delete all data
		if (!tableDelete(connection, CONTRACTS_OWNERS_TABLE, CONTRACTS_TABLE, CONTRACT_ITEMS_TABLE)) {
			return false;
		}

		//Insert data
		String sqlOwners = "INSERT INTO " + CONTRACTS_OWNERS_TABLE + " ("
				+ "	ownerid,"
				+ "	contractid)"
				+ " VALUES (?,?)"
				+ " ON CONFLICT(ownerid, contractid) DO NOTHING"
				;
		try (PreparedStatement statement = connection.prepareStatement(sqlOwners)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize() {
				@Override
				public int getSize(EsiOwner owner) {
					return owner.getContracts().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyContract contract : owner.getContracts().keySet()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getOwnerID());
					setAttribute(statement, ++index, contract.getContractID());
					rows.addRow();
				}
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}

		String sqlContracts = "INSERT OR IGNORE INTO " + CONTRACTS_TABLE + " ("
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
			Rows rows = new Rows(statement, esiOwners, new RowSize() {
				@Override
				public int getSize(EsiOwner owner) {
					return owner.getContracts().keySet().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyContract contract : owner.getContracts().keySet()) {
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
					rows.addRow();
				}
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}

		String sqlContractItems = "INSERT INTO " + CONTRACT_ITEMS_TABLE + " ("
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
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?)"
				+ " ON CONFLICT(recordid) DO NOTHING";
		try (PreparedStatement statement = connection.prepareStatement(sqlContractItems)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize() {
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
						rows.addRow();
					}
				}
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	@Override
	protected boolean select(Connection connection, List<EsiOwner> esiOwners, Map<Long, EsiOwner> owners) {
		String ownerSQL = "SELECT * FROM " + CONTRACTS_OWNERS_TABLE;
		Map<Integer, Set<EsiOwner>> contractOwners = new HashMap<>();
		try (PreparedStatement statement = connection.prepareStatement(ownerSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				long ownerID = getLong(rs, "ownerid");
				int contractID = getInt(rs, "contractid");
				Set<EsiOwner> set = contractOwners.get(contractID);
				if (set == null) {
					set = new HashSet<>();
					contractOwners.put(contractID, set);
				}
				EsiOwner owner = owners.get(ownerID);
				if (owner != null) {
					set.add(owner);
				}
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		String contractsSQL = "SELECT * FROM " + CONTRACTS_TABLE;
		List<MyContract> contracts = new ArrayList<>();
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

				contracts.add(contract);
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		String contractItemsSQL = "SELECT * FROM " + CONTRACT_ITEMS_TABLE;
		Map<Integer, List<RawContractItem>> contractItems = new HashMap<>();
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
				List<RawContractItem> list = contractItems.get(contractID);
				if (list == null) {
					list = new ArrayList<>();
					contractItems.put(contractID, list);
				}
				list.add(contractItem);
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		for (MyContract contract : contracts) {
			Integer contractID = contract.getContractID();
			List<RawContractItem> items = contractItems.get(contractID);
			Set<EsiOwner> set = contractOwners.get(contractID);
			if (items == null) {
				items = new ArrayList<>();
			}
			if (set != null) {
				for (EsiOwner esiOwner : set) {
					esiOwner.setContracts(DataConverter.convertRawContractItems(contract, items, esiOwner));
				}
			}
		}
		return true;
	}

	@Override
	protected boolean create(Connection connection) {
		if (!tableExist(connection, CONTRACTS_OWNERS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + CONTRACTS_OWNERS_TABLE + " ("
					+ "	ownerid INTEGER,\n"
					+ "	contractid INTEGER,"
					+ "	UNIQUE(ownerid, contractid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			} catch (SQLException ex) {
				LOG.error(ex.getMessage(), ex);
				return false;
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
			} catch (SQLException ex) {
				LOG.error(ex.getMessage(), ex);
				return false;
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
			} catch (SQLException ex) {
				LOG.error(ex.getMessage(), ex);
				return false;
			}
		}
		return true;
	}
}
