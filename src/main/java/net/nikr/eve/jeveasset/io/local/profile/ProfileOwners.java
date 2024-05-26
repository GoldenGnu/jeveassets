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
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.io.esi.EsiCallbackURL;
import net.troja.eve.esi.model.CharacterRolesResponse;


public class ProfileOwners extends ProfileTable {

	private static final String OWNERS_TABLE = "owners";

	@Override
	protected boolean insert(Connection connection, List<EsiOwner> esiOwners) {
		if (esiOwners == null || esiOwners.isEmpty()) {
			return false;
		}
		//Delete all data
		if (!tableDelete(connection, OWNERS_TABLE)) {
			return false;
		}
		String sql = "INSERT INTO " + OWNERS_TABLE + " ("
				+ "	ownerid,"
				+ "	name,"
				+ "	corp,"
				+ "	show,"
				+ "	invalid,"
				+ "	assetslastupdate,"
				+ "	assetsnextupdate,"
				+ "	balancelastupdate,"
				+ "	balancenextupdate,"
				+ "	marketordersnextupdate,"
				+ "	journalnextupdate,"
				+ "	transactionsnextupdate,"
				+ "	industryjobsnextupdate,"
				+ "	contractsnextupdate,"
				+ "	locationsnextupdate,"
				+ "	blueprintsnextupdate,"
				+ "	bookmarksnextupdate,"
				+ "	skillsnextupdate,"
				+ "	miningnextupdate,"
				+ "	accountname,"
				+ "	refreshtoken,"
				+ "	scopes,"
				+ "	structuresnextupdate,"
				+ "	accountnextupdate,"
				+ "	callbackurl,"
				+ "	characterroles)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Row row = new Row(esiOwners.size());
			for (EsiOwner owner : esiOwners) {
				int index = 0;
				setAttribute(statement, ++index, owner.getOwnerID());
				setAttribute(statement, ++index, owner.getOwnerName());
				setAttributeOptional(statement, ++index, owner.getCorporationName());
				setAttribute(statement, ++index, owner.isShowOwner());
				setAttribute(statement, ++index, owner.isInvalid());
				setAttributeOptional(statement, ++index, owner.getAssetLastUpdate());
				setAttribute(statement, ++index, owner.getAssetNextUpdate());
				setAttributeOptional(statement, ++index, owner.getBalanceLastUpdate());
				setAttribute(statement, ++index, owner.getBalanceNextUpdate());
				setAttribute(statement, ++index, owner.getMarketOrdersNextUpdate());
				setAttribute(statement, ++index, owner.getJournalNextUpdate());
				setAttribute(statement, ++index, owner.getTransactionsNextUpdate());
				setAttribute(statement, ++index, owner.getIndustryJobsNextUpdate());
				setAttribute(statement, ++index, owner.getContractsNextUpdate());
				setAttribute(statement, ++index, owner.getLocationsNextUpdate());
				setAttribute(statement, ++index, owner.getBlueprintsNextUpdate());
				setAttribute(statement, ++index, owner.getBookmarksNextUpdate());
				setAttribute(statement, ++index, owner.getSkillsNextUpdate());
				setAttribute(statement, ++index, owner.getMiningNextUpdate());
				setAttribute(statement, ++index, owner.getAccountName());
				setAttribute(statement, ++index, owner.getRefreshToken());
				setAttribute(statement, ++index, owner.getScopes());
				setAttribute(statement, ++index, owner.getStructuresNextUpdate());
				setAttribute(statement, ++index, owner.getAccountNextUpdate());
				setAttribute(statement, ++index, owner.getCallbackURL());
				setAttribute(statement, ++index, owner.getRoles());
				row.addRow(statement);
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
		String sql = "SELECT * FROM " + OWNERS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				String ownerName = getString(rs, "name");
				String corporationName = getStringOptional(rs, "corp");
				long ownerID = getLong(rs, "ownerid");
				Date assetsNextUpdate = getDateNotNull(rs, "assetsnextupdate");
				Date assetsLastUpdate = getDateOptional(rs, "assetslastupdate");
				Date balanceNextUpdate = getDateNotNull(rs, "balancenextupdate");
				Date balanceLastUpdate = getDateOptional(rs, "balancelastupdate");
				boolean showOwner = getBooleanNotNull(rs, "show", true) ;
				boolean invalid = getBooleanNotNull(rs, "invalid", false);
				Date marketOrdersNextUpdate = getDateNotNull(rs, "marketordersnextupdate");
				Date journalNextUpdate = getDateNotNull(rs, "journalnextupdate");
				Date transactionsNextUpdate = getDateNotNull(rs, "transactionsnextupdate");
				Date industryJobsNextUpdate = getDateNotNull(rs, "industryjobsnextupdate");
				Date contractsNextUpdate = getDateNotNull(rs, "contractsnextupdate");
				Date locationsNextUpdate = getDateNotNull(rs, "locationsnextupdate");
				Date blueprintsNextUpdate = getDateNotNull(rs, "blueprintsnextupdate");
				Date bookmarksNextUpdate = getDateNotNull(rs, "bookmarksnextupdate");
				Date skillsNextUpdate = getDateNotNull(rs, "skillsnextupdate");
				Date miningNextUpdate = getDateNotNull(rs, "miningnextupdate");
				String accountName = getString(rs, "accountname");
				String refreshToken = getString(rs, "refreshtoken");
				String scopes = getString(rs, "scopes");
				Date structuresNextUpdate = getDate(rs, "structuresnextupdate");
				Date accountNextUpdate = getDate(rs, "accountnextupdate");
				EsiCallbackURL callbackURL;
				try {
					callbackURL = EsiCallbackURL.valueOf(getString(rs, "callbackurl"));
				} catch (IllegalArgumentException ex) {
					continue;
				}
				Set<CharacterRolesResponse.RolesEnum> roles = EnumSet.noneOf(CharacterRolesResponse.RolesEnum.class);
				for (String role : getString(rs, "characterroles").split(",")) {
					try {
						roles.add(CharacterRolesResponse.RolesEnum.valueOf(role));
					} catch (IllegalArgumentException ex) {

					}
				}
				EsiOwner owner = new EsiOwner();
				owner.setRoles(roles);
				owner.setAccountName(accountName);
				owner.setScopes(new HashSet<>(Arrays.asList(scopes.split(","))));
				owner.setStructuresNextUpdate(structuresNextUpdate);
				owner.setAccountNextUpdate(accountNextUpdate);
				owner.setAuth(callbackURL, refreshToken, null);
				owner.setOwnerName(ownerName);
				owner.setCorporationName(corporationName);
				owner.setOwnerID(ownerID);
				owner.setAssetNextUpdate(assetsNextUpdate);
				owner.setAssetLastUpdate(assetsLastUpdate);
				owner.setBalanceNextUpdate(balanceNextUpdate);
				owner.setBalanceLastUpdate(balanceLastUpdate);
				owner.setShowOwner(showOwner);
				owner.setInvalid(invalid);
				owner.setMarketOrdersNextUpdate(marketOrdersNextUpdate);
				owner.setJournalNextUpdate(journalNextUpdate);
				owner.setTransactionsNextUpdate(transactionsNextUpdate);
				owner.setIndustryJobsNextUpdate(industryJobsNextUpdate);
				owner.setContractsNextUpdate(contractsNextUpdate);
				owner.setLocationsNextUpdate(locationsNextUpdate);
				owner.setBlueprintsNextUpdate(blueprintsNextUpdate);
				owner.setBookmarksNextUpdate(bookmarksNextUpdate);
				owner.setSkillsNextUpdate(skillsNextUpdate);
				owner.setMiningNextUpdate(miningNextUpdate);
				esiOwners.add(owner);
			}
			return true;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	protected boolean create(Connection connection) {
		if (!tableExist(connection, OWNERS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + OWNERS_TABLE + " (\n"
					+ "	ownerid INTEGER,\n"
					+ "	name TEXT,\n"
					+ "	corp TEXT,\n"
					+ "	show NUMERIC,\n"
					+ "	invalid NUMERIC,\n"
					+ "	assetslastupdate INTEGER,\n"
					+ "	assetsnextupdate INTEGER,\n"
					+ "	balancelastupdate INTEGER,\n"
					+ "	balancenextupdate INTEGER,\n"
					+ "	marketordersnextupdate INTEGER,\n"
					+ "	journalnextupdate INTEGER,\n"
					+ "	transactionsnextupdate INTEGER,\n"
					+ "	industryjobsnextupdate INTEGER,\n"
					+ "	contractsnextupdate INTEGER,\n"
					+ "	locationsnextupdate INTEGER,\n"
					+ "	blueprintsnextupdate INTEGER,\n"
					+ "	bookmarksnextupdate INTEGER,\n"
					+ "	skillsnextupdate INTEGER,\n"
					+ "	miningnextupdate INTEGER,\n"
					+ "	accountname TEXT,\n"
					+ "	refreshtoken TEXT,\n"
					+ "	scopes TEXT,\n"
					+ "	structuresnextupdate INTEGER,\n"
					+ "	accountnextupdate INTEGER,\n"
					+ "	callbackurl TEXT,\n"
					+ "	characterroles TEXT,\n"
					+ "	UNIQUE(ownerid)\n"
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

	protected boolean update(Connection connection) { return true; }
	
}
