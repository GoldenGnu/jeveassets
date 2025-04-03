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
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class ProfileJournals extends ProfileTable {

	private static final String JOURNALS_TABLE = "journals";

	@Override
	protected boolean isUpdated() {
		return Settings.get().isJournalHistory();
	}

	private static void set(PreparedStatement statement, MyJournal journal, long ownerID) throws SQLException {
		int index = 0;
		setAttributeOptional(statement, ++index, ownerID);
		setAttributeOptional(statement, ++index, journal.getAmount());
		setAttributeOptional(statement, ++index, journal.getBalance());
		setAttributeOptional(statement, ++index, journal.getContextID());
		setAttributeOptional(statement, ++index, journal.getContextType());
		setAttributeOptional(statement, ++index, journal.getContextTypeString());
		setAttribute(statement, ++index, journal.getDate());
		setAttribute(statement, ++index, journal.getDescription());
		setAttributeOptional(statement, ++index, journal.getFirstPartyID());
		setAttributeOptional(statement, ++index, journal.getSecondPartyID());
		setAttributeOptional(statement, ++index, journal.getReason());
		setAttribute(statement, ++index, journal.getRefID());
		if (journal.getRefType() != null) {
			setAttributeOptional(statement, ++index, journal.getRefType().getID());
		} else {
			setAttributeNull(statement, ++index);
		}
		setAttribute(statement, ++index, journal.getRefTypeString());
		setAttributeOptional(statement, ++index, journal.getTaxAmount());
		setAttributeOptional(statement, ++index, journal.getTaxReceiverID());
		//Extra
		setAttribute(statement, ++index, journal.getAccountKey());
	}

	/**
	 * Journal entries are immutable (IGNORE)
	 * @param connection
	 * @param ownerID
	 * @param journals
	 * @return 
	 */
	public static boolean updateJournals(Connection connection, long ownerID, Collection<MyJournal> journals) {
		//Tables exist
		if (!tableExist(connection, JOURNALS_TABLE)) {
			return false;
		}

		//Insert data
		String sql = "INSERT OR IGNORE INTO " + JOURNALS_TABLE + " ("
				+ "	ownerid,"
				+ "	amount,"
				+ "	balance,"
				+ "	contextid,"
				+ "	contexttype,"
				+ "	contexttypestring,"
				+ "	date,"
				+ "	description,"
				+ "	ownerid1,"
				+ "	ownerid2,"
				+ "	reason,"
				+ "	refid,"
				+ "	reftypeid,"
				+ "	reftypestring,"
				+ "	taxamount,"
				+ "	taxreceiverid,"
				+ "	accountkey)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, journals.size());
				for (MyJournal journal : journals) {
					set(statement, journal, ownerID);
					rows.addRow();
				}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	@Override
	protected boolean insert(Connection connection, List<EsiOwner> esiOwners) {
		//Delete all data
		if (!tableDelete(connection, JOURNALS_TABLE)) {
			return true;
		}

		//Insert data
		String sql = "INSERT INTO " + JOURNALS_TABLE + " ("
				+ "	ownerid,"
				+ "	amount,"
				+ "	balance,"
				+ "	contextid,"
				+ "	contexttype,"
				+ "	contexttypestring,"
				+ "	date,"
				+ "	description,"
				+ "	ownerid1,"
				+ "	ownerid2,"
				+ "	reason,"
				+ "	refid,"
				+ "	reftypeid,"
				+ "	reftypestring,"
				+ "	taxamount,"
				+ "	taxreceiverid,"
				+ "	accountkey)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Rows rows = new Rows(statement, esiOwners, new RowSize<EsiOwner>() {
				@Override
				public int getSize(EsiOwner owner) {
					return owner.getJournal().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyJournal journal : owner.getJournal()) {
					set(statement, journal, owner.getOwnerID());
					rows.addRow();
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
		Map<EsiOwner, Set<MyJournal>> journals = new HashMap<>();
		String sql = "SELECT * FROM " + JOURNALS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				long ownerID = getLong(rs, "ownerid");
				RawJournal rawJournal = RawJournal.create();
				Double amount = getDoubleOptional(rs, "amount");
				Double balance = getDoubleOptional(rs, "balance");
				Long contextID = getLongOptional(rs, "contextid");
				String contextType = getStringOptional(rs, "contexttype");
				String contextTypeString = getStringOptional(rs, "contexttypestring");
				Date date = getDate(rs, "date");
				String description = getStringNotNull(rs, "description", "");
				Integer firstPartyID = getIntOptional(rs, "ownerid1");
				Integer secondPartyID = getIntOptional(rs, "ownerid2");
				String reason = getStringOptional(rs, "reason");
				long refID = getLong(rs, "refid");
				Integer refTypeEnum = getIntOptional(rs, "reftypeid");
				String refTypeString = getStringOptional(rs, "reftypestring");
				Double taxAmount = getDoubleOptional(rs, "taxamount");
				Integer taxReceiverID = getIntOptional(rs, "taxreceiverid");
				//Extra
				int accountKey = getInt(rs, "accountkey");

				rawJournal.setAmount(amount);
				rawJournal.setBalance(balance);
				rawJournal.setDate(date);
				rawJournal.setDescription(description);
				rawJournal.setFirstPartyID(firstPartyID);
				rawJournal.setReason(reason);
				rawJournal.setRefID(refID);
				RawJournalRefType refType = RawConverter.toJournalRefType(refTypeEnum, refTypeString);
				rawJournal.setRefType(refType);
				rawJournal.setRefTypeString(refTypeString);
				rawJournal.setSecondPartyID(secondPartyID);
				rawJournal.setTax(taxAmount);
				rawJournal.setTaxReceiverID(taxReceiverID);
				rawJournal.setContextID(contextID);
				rawJournal.setContextType(RawConverter.toJournalContextType(contextType, contextTypeString));
				rawJournal.setContextTypeString(contextTypeString);
				rawJournal.setAccountKey(accountKey);


				EsiOwner owner = owners.get(ownerID);
				if (owner == null) {
					continue;
				}
				set(owner, journals, DataConverter.toMyJournal(rawJournal, owner));
			}
			for (Map.Entry<EsiOwner, Set<MyJournal>> entry : journals.entrySet()) {
				entry.getKey().setJournal(entry.getValue());
			}
			return true;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	protected boolean create(Connection connection) {
		if (!tableExist(connection, JOURNALS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + JOURNALS_TABLE + " (\n"
					+ "	ownerid INTEGER,"
					+ "	amount REAL,"
					+ "	balance REAL,"
					+ "	contextid INTEGER,"
					+ "	contexttype TEXT,"
					+ "	contexttypestring TEXT,"
					+ "	date INTEGER,"
					+ "	description TEXT,"
					+ "	ownerid1 INTEGER,"
					+ "	ownerid2 INTEGER,"
					+ "	reason TEXT,"
					+ "	refid INTEGER,"
					+ "	reftypeid INTEGER,"
					+ "	reftypestring TEXT,"
					+ "	taxamount REAL,"
					+ "	taxreceiverid INTEGER,"
					+ "	accountkey INTEGER,"
					+ "	UNIQUE(ownerid, refid, amount)\n"
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
