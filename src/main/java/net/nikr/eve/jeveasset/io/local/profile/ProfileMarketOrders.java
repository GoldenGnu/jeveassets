/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.Change;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class ProfileMarketOrders extends ProfileTable {

	private static final String MARKET_ORDERS_TABLE = "marketorders";
	private static final String MARKET_ORDER_CHANGES_TABLE = "marketorderchanges";

	@Override
	protected boolean insert(Connection connection, List<EsiOwner> esiOwners) {
		if (esiOwners == null || esiOwners.isEmpty()) {
			return false;
		}
		//Delete all data
		if (!tableDelete(connection, MARKET_ORDERS_TABLE, MARKET_ORDER_CHANGES_TABLE)) {
			return false;
		}
		String ordersSQL = "INSERT INTO " + MARKET_ORDERS_TABLE + " ("
				+ "	ownerid,"
				+ "	orderid,"
				+ "	stationid,"
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
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
				;
		try (PreparedStatement statement = connection.prepareStatement(ordersSQL)) {
			Row row = new Row(esiOwners, new RowSize() {
				@Override
				public int getSize(EsiOwner owner) {
					return owner.getMarketOrders().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyMarketOrder marketOrder : owner.getMarketOrders()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getOwnerID());
					setAttribute(statement, ++index, marketOrder.getOrderID());
					setAttribute(statement, ++index, marketOrder.getLocationID());
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
					row.addRow(statement);
				}
			}
			row.commit(connection);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		String changesSQL = "INSERT INTO " + MARKET_ORDER_CHANGES_TABLE + " ("
				+ "	orderid,"
				+ "	date,"
				+ "	price,"
				+ "	volremaining)"
				+ " VALUES (?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(changesSQL)) {
			Row row = new Row(esiOwners, new RowSize() {
				@Override
				public int getSize(EsiOwner owner) {
					return owner.getMarketOrders().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyMarketOrder marketOrder : owner.getMarketOrders()) {
					for (RawMarketOrder.Change change : marketOrder.getChanges()) {
						int index = 0;
						setAttribute(statement, ++index, marketOrder.getOrderID());
						setAttribute(statement, ++index, change.getDate());
						setAttributeOptional(statement, ++index, change.getPrice());
						setAttributeOptional(statement, ++index, change.getVolumeRemaining());
						row.addRow(statement);
					}
				}
			}
			row.commit(connection);
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	@Override
	protected boolean select(Connection connection, List<EsiOwner> esiOwners, Map<Long, EsiOwner> owners) {
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
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		String ordersSQL = "SELECT * FROM " + MARKET_ORDERS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(ordersSQL);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				long ownerID = getLong(rs, "ownerid");
				EsiOwner owner = owners.get(ownerID);
				if (owner == null) {
					continue;
				}
				RawMarketOrder rawMarketOrder = RawMarketOrder.create();
				long orderID = getLong(rs, "orderid");
				long locationID = getLong(rs, "stationid");
				int volEntered = getInt(rs, "volentered");
				int volRemaining = getInt(rs, "volremaining");
				int minVolume = getInt(rs, "minvolume");
				String stateEnum = getStringOptional(rs, "orderstateenum");
				String stateString = getStringOptional(rs, "orderstatestring");
				int typeID = getInt(rs, "typeid");
				String rangeEnum = getStringOptional(rs, "rangeenum");
				String rangeString = getStringOptional(rs, "rangestring");
				int accountID = getInt(rs, "accountkey");
				int duration = getInt(rs, "duration");
				double escrow = getDouble(rs, "escrow");
				double price = getDouble(rs, "price");
				int bid = getInt(rs, "bid");
				Date issued = getDate(rs, "issued");
				Integer issuedBy = getIntOptional(rs, "issuedby");
				boolean corp = getBooleanNotNull(rs, "corp", owner.isCorporation());
				boolean esi = getBooleanNotNull(rs, "esi", true);


				rawMarketOrder.setWalletDivision(accountID);
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
				rawMarketOrder.setRegionID((int) ApiIdConverter.getLocation(locationID).getRegionID());
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
			return true;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	protected boolean create(Connection connection) {
		if (!tableExist(connection, MARKET_ORDERS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + MARKET_ORDERS_TABLE + " (\n"
					+ "	ownerid INTEGER,"
					+ "	orderid INTEGER,"
					+ "	stationid INTEGER,"
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
					+ "	UNIQUE(ownerid, orderid)\n"
					+ ");";
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			} catch (SQLException ex) {
				LOG.error(ex.getMessage(), ex);
				return false;
			}
		}
		if (!tableExist(connection, MARKET_ORDER_CHANGES_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + MARKET_ORDER_CHANGES_TABLE + " (\n"
					+ "	orderid INTEGER,"
					+ "	date INTEGER,"
					+ "	price REAL,"
					+ "	volremaining INTEGER"
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
