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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.Change;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class ProfileMarketOrders extends ProfileTable {

	private static final String MARKET_ORDERS_TABLE = "marketorders";
	private static final String MARKET_ORDER_CHANGES_TABLE = "marketorderchanges";

	@Override
	protected boolean isUpdated() {
		return Settings.get().isMarketOrderHistory();
	}

	private static void set(PreparedStatement statement, MyMarketOrder marketOrder, RawMarketOrder.Change change) throws SQLException {
		int index = 0;
		setAttribute(statement, ++index, marketOrder.getOrderID());
		setAttribute(statement, ++index, change.getDate());
		setAttributeOptional(statement, ++index, change.getPrice());
		setAttributeOptional(statement, ++index, change.getVolumeRemaining());
	}

	private static void set(PreparedStatement statement, MyMarketOrder marketOrder, String accountID) throws SQLException {
		int index = 0;
		setAttribute(statement, ++index, accountID);
		setAttribute(statement, ++index, marketOrder.getOrderID());
		setAttribute(statement, ++index, marketOrder.getLocationID());
		setAttribute(statement, ++index, marketOrder.getRegionID());
		setAttribute(statement, ++index, marketOrder.getVolumeTotal());
		setAttribute(statement, ++index, marketOrder.getVolumeRemain());
		setAttribute(statement, ++index, marketOrder.getMinVolume());
		setAttributeOptional(statement, ++index, marketOrder.getState());
		setAttributeOptional(statement, ++index, marketOrder.getStateString());
		setAttribute(statement, ++index, marketOrder.getTypeID());
		setAttributeOptional(statement, ++index, marketOrder.getRange());
		setAttributeOptional(statement, ++index, marketOrder.getRangeString());
		setAttribute(statement, ++index, marketOrder.getWalletDivision());
		setAttribute(statement, ++index, marketOrder.getDuration());
		setAttribute(statement, ++index, marketOrder.getEscrow());
		setAttribute(statement, ++index, marketOrder.getPrice());
		setAttribute(statement, ++index, RawConverter.fromMarketOrderIsBuyOrder(marketOrder.isBuyOrder()));
		setAttribute(statement, ++index, marketOrder.getIssued());
		setAttributeOptional(statement, ++index, marketOrder.getIssuedBy());
		setAttribute(statement, ++index, marketOrder.isCorp());
		setAttribute(statement, ++index, marketOrder.isESI());
	}

	/**
	 * Market orders are mutable (REPLACE).Market order changes are immutable (IGNORE)
	 * @param connection
	 * @param accountID
	 * @param marketOrders 
	 * @throws java.sql.SQLException 
	 */
	public static void updateMarketOrders(Connection connection, String accountID, Collection<MyMarketOrder> marketOrders) throws SQLException {
		//Tables exist
		if (!tableExist(connection, MARKET_ORDERS_TABLE, MARKET_ORDER_CHANGES_TABLE)) {
			return;
		}

		//Insert data
		String ordersSQL = "INSERT OR REPLACE INTO " + MARKET_ORDERS_TABLE + " ("
				+ "	accountid,"
				+ "	orderid,"
				+ "	stationid,"
				+ "	regionid,"
				+ "	volentered,"
				+ "	volremaining,"
				+ "	minvolume,"
				+ "	orderstateenum,"
				+ "	orderstatestring,"
				+ "	typeid,"
				+ "	rangeenum,"
				+ "	rangestring,"
				+ "	accountkey,"
				+ "	duration,"
				+ "	escrow,"
				+ "	price,"
				+ "	bid,"
				+ "	issued,"
				+ "	issuedby,"
				+ "	corp,"
				+ "	esi)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
				;
		try (PreparedStatement statement = connection.prepareStatement(ordersSQL)) {
			Rows rows = new Rows(statement, marketOrders.size());
			for (MyMarketOrder marketOrder : marketOrders) {
				set(statement, marketOrder, accountID);
				rows.addRow();
			}
		}

		String changesSQL = "INSERT OR IGNORE INTO " + MARKET_ORDER_CHANGES_TABLE + " ("
				+ "	orderid,"
				+ "	date,"
				+ "	price,"
				+ "	volremaining)"
				+ " VALUES (?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(changesSQL)) {
			Rows rows = new Rows(statement, marketOrders.size());
			for (MyMarketOrder marketOrder : marketOrders) {
				for (RawMarketOrder.Change change : marketOrder.getChanges()) {
					set(statement, marketOrder, change);
					rows.addRow();
				}
			}
		}
	}

	@Override
	protected void insert(Connection connection, List<EsiOwner> esiOwners) throws SQLException {
		//Delete all data
		tableDelete(connection, MARKET_ORDERS_TABLE, MARKET_ORDER_CHANGES_TABLE);

		//Insert data
		String ordersSQL = "INSERT INTO " + MARKET_ORDERS_TABLE + " ("
				+ "	accountid,"
				+ "	orderid,"
				+ "	stationid,"
				+ "	regionid,"
				+ "	volentered,"
				+ "	volremaining,"
				+ "	minvolume,"
				+ "	orderstateenum,"
				+ "	orderstatestring,"
				+ "	typeid,"
				+ "	rangeenum,"
				+ "	rangestring,"
				+ "	accountkey,"
				+ "	duration,"
				+ "	escrow,"
				+ "	price,"
				+ "	bid,"
				+ "	issued,"
				+ "	issuedby,"
				+ "	corp,"
				+ "	esi)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
				;
		try (PreparedStatement statement = connection.prepareStatement(ordersSQL)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner owner) {
					return owner.getMarketOrders().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyMarketOrder marketOrder : owner.getMarketOrders()) {
					set(statement, marketOrder, owner.getAccountID());
					rows.addRow();
				}
			}
		}

		String changesSQL = "INSERT OR IGNORE INTO " + MARKET_ORDER_CHANGES_TABLE + " ("
				+ "	orderid,"
				+ "	date,"
				+ "	price,"
				+ "	volremaining)"
				+ " VALUES (?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(changesSQL)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner owner) {
					return owner.getMarketOrders().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyMarketOrder marketOrder : owner.getMarketOrders()) {
					for (RawMarketOrder.Change change : marketOrder.getChanges()) {
						set(statement, marketOrder, change);
						rows.addRow();
					}
				}
			}
		}
	}

	@Override
	protected void select(Connection connection, List<EsiOwner> esiOwners, Map<String, EsiOwner> owners) throws SQLException {
		Map<EsiOwner, Set<MyMarketOrder>> marketOrders = new HashMap<>();
		Map<Long, Set<Change>> changes = new HashMap<>();
		String changesSQL = "SELECT * FROM " + MARKET_ORDER_CHANGES_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(changesSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				long orderID = getLong(rs, "orderid");
				Date date = getDate(rs, "date");
				Double changePrice = getDoubleOptional(rs, "price");
				Integer changeVolRemaining = getIntOptional(rs, "volremaining");
				Set<Change> set = changes.get(orderID);
				if (set == null) {
					set = new HashSet<>();
					changes.put(orderID, set);
				}
				set.add(new Change(date, changePrice, changeVolRemaining));
			}
			for (Map.Entry<EsiOwner, Set<MyMarketOrder>> entry : marketOrders.entrySet()) {
				entry.getKey().setMarketOrders(entry.getValue());
			}
		}
		String ordersSQL = "SELECT * FROM " + MARKET_ORDERS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(ordersSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				String accountID = getString(rs, "accountid");
				EsiOwner owner = owners.get(accountID);
				if (owner == null) {
					continue;
				}
				RawMarketOrder rawMarketOrder = RawMarketOrder.create();
				long orderID = getLong(rs, "orderid");
				long locationID = getLong(rs, "stationid");
				Integer regionID = getIntOptional(rs, "regionid");
				int volEntered = getInt(rs, "volentered");
				int volRemaining = getInt(rs, "volremaining");
				int minVolume = getInt(rs, "minvolume");
				String stateEnum = getStringOptional(rs, "orderstateenum");
				String stateString = getStringOptional(rs, "orderstatestring");
				int typeID = getInt(rs, "typeid");
				String rangeEnum = getStringOptional(rs, "rangeenum");
				String rangeString = getStringOptional(rs, "rangestring");
				int accountkey = getInt(rs, "accountkey");
				int duration = getInt(rs, "duration");
				double escrow = getDouble(rs, "escrow");
				double price = getDouble(rs, "price");
				int bid = getInt(rs, "bid");
				Date issued = getDate(rs, "issued");
				Integer issuedBy = getIntOptional(rs, "issuedby");
				boolean corp = getBooleanNotNull(rs, "corp", owner.isCorporation());
				boolean esi = getBooleanNotNull(rs, "esi", true);


				rawMarketOrder.setWalletDivision(accountkey);
				rawMarketOrder.setDuration(duration);
				rawMarketOrder.setEscrow(escrow);
				rawMarketOrder.setBuyOrder(bid > 0);
				rawMarketOrder.setCorp(corp);
				rawMarketOrder.setIssued(issued);
				rawMarketOrder.addChanges(changes.get(orderID));
				rawMarketOrder.setIssuedBy(issuedBy);
				rawMarketOrder.setLocationID(locationID);
				rawMarketOrder.setMinVolume(minVolume);
				rawMarketOrder.setOrderID(orderID);
				rawMarketOrder.setPrice(price);
				rawMarketOrder.setRange(RawConverter.toMarketOrderRange(null, rangeEnum, rangeString));
				rawMarketOrder.setRangeString(rangeString);
				rawMarketOrder.setRegionID(RawConverter.toMarketOrderRegionID(locationID, typeID, regionID));
				rawMarketOrder.setState(RawConverter.toMarketOrderState(null, stateEnum, stateString));
				rawMarketOrder.setStateString(stateString);
				rawMarketOrder.setTypeID(typeID);
				rawMarketOrder.setVolumeRemain(volRemaining);
				rawMarketOrder.setVolumeTotal(volEntered);

				MyMarketOrder marketOrder = DataConverter.toMyMarketOrder(rawMarketOrder, owner);
				marketOrder.setESI(esi);

				set(owner, marketOrders, marketOrder);
			}
			for (Map.Entry<EsiOwner, Set<MyMarketOrder>> entry : marketOrders.entrySet()) {
				entry.getKey().setMarketOrders(entry.getValue());
			}
		}
	}

	@Override
	protected void updateTable(Connection connection) throws SQLException {
		addColumn(connection, MARKET_ORDERS_TABLE, "regionid", "INTEGER");
	}

	@Override
	protected boolean isEmpty(Connection connection) throws SQLException {
		return !tableExist(connection, MARKET_ORDERS_TABLE, MARKET_ORDER_CHANGES_TABLE);
	}

	@Override
	protected void create(Connection connection) throws SQLException {
		if (!tableExist(connection, MARKET_ORDERS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + MARKET_ORDERS_TABLE + " (\n"
					+ "	accountid TEXT,"
					+ "	orderid INTEGER,"
					+ "	stationid INTEGER,"
					+ "	regionid INTEGER,"
					+ "	volentered INTEGER,"
					+ "	volremaining INTEGER,"
					+ "	minvolume INTEGER,"
					+ "	orderstateenum TEXT,"
					+ "	orderstatestring TEXT,"
					+ "	typeid INTEGER,"
					+ "	rangeenum TEXT,"
					+ "	rangestring TEXT,"
					+ "	accountkey INTEGER,"
					+ "	duration INTEGER,"
					+ "	escrow REAL,"
					+ "	price REAL,"
					+ "	bid NUMERIC,"
					+ "	issued INTEGER,"
					+ "	issuedby INTEGER,"
					+ "	corp NUMERIC,"
					+ "	esi NUMERIC,"
					+ "	UNIQUE(accountid, orderid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
		if (!tableExist(connection, MARKET_ORDER_CHANGES_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + MARKET_ORDER_CHANGES_TABLE + " (\n"
					+ "	orderid INTEGER,"
					+ "	date INTEGER,"
					+ "	price REAL,"
					+ "	volremaining INTEGER, "
					+ " UNIQUE(orderid, date)"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
	}
}
