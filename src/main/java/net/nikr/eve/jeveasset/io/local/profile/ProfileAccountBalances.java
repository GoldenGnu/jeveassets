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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.raw.RawAccountBalance;
import net.nikr.eve.jeveasset.io.shared.DataConverter;


public class ProfileAccountBalances extends ProfileTable {

	private static final String ACCOUNT_BALANCES_TABLE = "accountbalances";

	@Override
	protected void insert(Connection connection, List<EsiOwner> esiOwners) throws SQLException {
		//Delete all data
		tableDelete(connection, ACCOUNT_BALANCES_TABLE);

		//Insert Data
		String sql = "INSERT OR REPLACE INTO " + ACCOUNT_BALANCES_TABLE + " ("
				+ "	accountid,"
				+ "	accountkey,"
				+ "	balance)"
				+ " VALUES (?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner esiOwner) {
					return esiOwner.getAccountBalances().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyAccountBalance accountBalance : owner.getAccountBalances()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getAccountID());
					setAttribute(statement, ++index, accountBalance.getAccountKey());
					setAttribute(statement, ++index, accountBalance.getBalance());
					rows.addRow();
				}
			}
		}
	}

	@Override
	protected void select(Connection connection, List<EsiOwner> esiOwners, Map<String, EsiOwner> owners)  throws SQLException {
		Map<EsiOwner, List<MyAccountBalance>> accountBalances = new HashMap<>();
		String sql = "SELECT * FROM " + ACCOUNT_BALANCES_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				RawAccountBalance accountBalance = RawAccountBalance.create();
				String accountID = getString(rs, "accountid");
				int accountKey = getInt(rs, "accountkey");
				double balance = getDouble(rs, "balance");
				accountBalance.setAccountKey(accountKey);
				accountBalance.setBalance(balance);

				EsiOwner owner = owners.get(accountID);
				if (owner == null) {
					continue;
				}
				list(owner, accountBalances, DataConverter.toMyAccountBalance(accountBalance, owner));
			}
			for (Map.Entry<EsiOwner, List<MyAccountBalance>> entry : accountBalances.entrySet()) {
				entry.getKey().setAccountBalances(entry.getValue());
			}
		}
	}

	@Override
	protected boolean isEmpty(Connection connection) throws SQLException {
		return !tableExist(connection, ACCOUNT_BALANCES_TABLE);
	}

	@Override
	protected void create(Connection connection)  throws SQLException {
		if (!tableExist(connection, ACCOUNT_BALANCES_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + ACCOUNT_BALANCES_TABLE + " (\n"
					+ "	accountid TEXT,\n"
					+ "	accountkey INTEGER,\n"
					+ "	balance REAL,\n"
					+ "	UNIQUE(accountid, accountkey)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
	}
}
