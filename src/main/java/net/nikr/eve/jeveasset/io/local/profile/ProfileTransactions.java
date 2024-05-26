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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawTransaction;
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class ProfileTransactions  extends ProfileTable {

	private static final String TRANSACTIONS_TABLE = "transactions";

	@Override
	protected boolean insert(Connection connection, List<EsiOwner> esiOwners) {
		if (esiOwners == null || esiOwners.isEmpty()) {
			return false;
		}
		//Delete all data
		if (!tableDelete(connection, TRANSACTIONS_TABLE)) {
			return false;
		}
		String sql = "INSERT INTO " + TRANSACTIONS_TABLE + " ("
				+ "	ownerid,"
				+ "	transactiondatetime,"
				+ "	transactionid,"
				+ "	quantity,"
				+ "	typeid,"
				+ "	price,"
				+ "	clientid,"
				+ "	stationid,"
				+ "	transactiontype,"
				+ "	transactionfor,"
				+ "	journaltransactionid,"
				+ "	clienttypeid,"
				+ "	accountkey)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Row row = new Row(esiOwners, new RowSize() {
				@Override
				public int getSize(EsiOwner owner) {
					return owner.getTransactions().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyTransaction transaction : owner.getTransactions()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getOwnerID());
					setAttribute(statement, ++index, transaction.getDate());
					setAttribute(statement, ++index, transaction.getTransactionID());
					setAttribute(statement, ++index, transaction.getQuantity());
					setAttribute(statement, ++index, transaction.getTypeID());
					setAttribute(statement, ++index, transaction.getPrice());
					setAttribute(statement, ++index, transaction.getClientID());
					setAttribute(statement, ++index, transaction.getLocationID());
					setAttribute(statement, ++index, RawConverter.fromTransactionIsBuy(transaction.isBuy()));
					setAttribute(statement, ++index, RawConverter.fromTransactionIsPersonal(transaction.isPersonal()));
					//New
					setAttribute(statement, ++index, transaction.getTransactionID());
					setAttribute(statement, ++index, transaction.getClientID());
					//Extra
					setAttribute(statement, ++index, transaction.getAccountKey());
					row.addRow(statement);
				}
			}
			row.commit(connection);
			return true;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	protected boolean select(Connection connection, List<EsiOwner> esiOwners, Map<Long, EsiOwner> owners) {
		Map<EsiOwner, Set<MyTransaction>> transactions = new HashMap<>();
		String sql = "SELECT * FROM " + TRANSACTIONS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				long ownerID = getLong(rs, "ownerid");
				RawTransaction rawTransaction = RawTransaction.create();
				Date date = getDate(rs, "transactiondatetime");
				long transactionID = getLong(rs, "transactionid");
				int quantity = getInt(rs, "quantity");
				int typeID = getInt(rs, "typeid");
				double price = getDouble(rs, "price");
				int clientID = getInt(rs, "clientid");
				long locationID = getLong(rs, "stationid");
				String transactionType = getString(rs, "transactiontype");
				String transactionFor = getString(rs, "transactionfor");

				//New
				long journalRefID = getLongNotNull(rs, "journaltransactionid", 0L);

				//Extra
				int accountKey = getIntNotNull(rs, "accountkey", 1000);
				rawTransaction.setClientID(clientID);
				rawTransaction.setDate(date);
				rawTransaction.setBuy(RawConverter.toTransactionIsBuy(transactionType));
				rawTransaction.setPersonal(RawConverter.toTransactionIsPersonal(transactionFor));
				rawTransaction.setJournalRefID(journalRefID);
				rawTransaction.setLocationID(locationID);
				rawTransaction.setQuantity(quantity);
				rawTransaction.setTransactionID(transactionID);
				rawTransaction.setTypeID(typeID);
				rawTransaction.setUnitPrice(price);
				rawTransaction.setAccountKey(accountKey);

				
				EsiOwner owner = owners.get(ownerID);
				if (owner == null) {
					continue;
				}
				set(owner, transactions, DataConverter.toMyTransaction(rawTransaction, owner));
			
			}
			for (Map.Entry<EsiOwner, Set<MyTransaction>> entry : transactions.entrySet()) {
				entry.getKey().setTransactions(entry.getValue());
			}
			return true;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	protected boolean create(Connection connection) {
		if (!tableExist(connection, TRANSACTIONS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + TRANSACTIONS_TABLE + " (\n"
					+ "	ownerid INTEGER,\n"
					+ "	transactiondatetime INTEGER,"
					+ "	transactionid INTEGER,"
					+ "	quantity INTEGER,"
					+ "	typeid INTEGER,"
					+ "	price REAL,"
					+ "	clientid INTEGER,"
					+ "	stationid INTEGER,"
					+ "	transactiontype TEXT,"
					+ "	transactionfor TEXT,"
					+ "	journaltransactionid INTEGER,"
					+ "	clienttypeid INTEGER,"
					+ "	accountkey INTEGER,"
					+ "	UNIQUE(ownerid, transactionid, price)\n"
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

	@Override
	protected boolean update(Connection connection) { return true; }

}
