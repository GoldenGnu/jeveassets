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
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.io.shared.DataConverter;
import net.nikr.eve.jeveasset.io.shared.RawConverter;


public class ProfileIndustryJobs extends ProfileTable {

	private static final String INDUSTRY_JOBS_TABLE = "industryjobs";

	@Override
	protected boolean insert(Connection connection, List<EsiOwner> esiOwners) {
		//Delete all data
		if (!tableDelete(connection, INDUSTRY_JOBS_TABLE)) {
			return false;
		}

		//Insert data
		String sql = "INSERT INTO " + INDUSTRY_JOBS_TABLE + " ("
				+ "	ownerid,"
				+ "	jobid,"
				+ "	installerid,"
				+ "	facilityid,"
				+ "	stationid,"
				+ "	activityid,"
				+ "	blueprintid,"
				+ "	blueprinttypeid,"
				+ "	blueprintlocationid,"
				+ "	outputlocationid,"
				+ "	runs,"
				+ "	cost,"
				+ "	licensedruns,"
				+ "	probability,"
				+ "	producttypeid,"
				+ "	statusenum,"
				+ "	statusstring,"
				+ "	timeinseconds,"
				+ "	startdate,"
				+ "	enddate,"
				+ "	pausedate,"
				+ "	completeddate,"
				+ "	completedcharacterid,"
				+ "	successfulruns,"
				+ "	esi)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			Row row = new Row(esiOwners, new RowSize() {
				@Override
				public int getSize(EsiOwner owner) {
					return owner.getIndustryJobs().size();
				}
			});
			for (EsiOwner owner : esiOwners) {
				for (MyIndustryJob industryJob : owner.getIndustryJobs()) {
					int index = 0;
					setAttribute(statement, ++index, owner.getOwnerID());
					setAttribute(statement, ++index, industryJob.getJobID());
					setAttribute(statement, ++index, industryJob.getInstallerID());
					setAttribute(statement, ++index, industryJob.getFacilityID());
					setAttribute(statement, ++index, industryJob.getStationID());
					setAttribute(statement, ++index, industryJob.getActivityID());
					setAttribute(statement, ++index, industryJob.getBlueprintID());
					setAttribute(statement, ++index, industryJob.getBlueprintTypeID());
					setAttribute(statement, ++index, industryJob.getBlueprintLocationID());
					setAttribute(statement, ++index, industryJob.getOutputLocationID());
					setAttribute(statement, ++index, industryJob.getRuns());
					setAttributeOptional(statement, ++index, industryJob.getCost());
					setAttributeOptional(statement, ++index, industryJob.getLicensedRuns());
					setAttributeOptional(statement, ++index, industryJob.getProbability());
					setAttributeOptional(statement, ++index, industryJob.getProductTypeID());
					setAttributeOptional(statement, ++index, industryJob.getStatus());
					setAttributeOptional(statement, ++index, industryJob.getStatusString());
					setAttribute(statement, ++index, industryJob.getDuration());
					setAttribute(statement, ++index, industryJob.getStartDate());
					setAttribute(statement, ++index, industryJob.getEndDate());
					setAttributeOptional(statement, ++index, industryJob.getPauseDate());
					setAttributeOptional(statement, ++index, industryJob.getCompletedDate());
					setAttributeOptional(statement, ++index, industryJob.getCompletedCharacterID());
					setAttributeOptional(statement, ++index, industryJob.getSuccessfulRuns());
					setAttribute(statement, ++index, industryJob.isESI());
					row.addRow(statement);
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
		Map<EsiOwner, Set<MyIndustryJob>> industryJobs = new HashMap<>();
		String sql = "SELECT * FROM " + INDUSTRY_JOBS_TABLE;
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();) {
			while (rs.next()) {
				long ownerID = getLong(rs, "ownerid");
				RawIndustryJob rawIndustryJob = RawIndustryJob.create();
				int jobID = getInt(rs, "jobid");
				int installerID = getInt(rs, "installerid");
				long facilityID = getLong(rs, "facilityid");
				long stationID = getLong(rs, "stationid");
				int activityID = getInt(rs, "activityid");
				long blueprintID = getLong(rs, "blueprintid");
				int blueprintTypeID = getInt(rs, "blueprinttypeid");
				long blueprintLocationID = getLong(rs, "blueprintlocationid");
				long outputLocationID = getLong(rs, "outputlocationid");
				int runs = getInt(rs, "runs");
				Double cost = getDoubleOptional(rs, "cost");
				Integer licensedRuns = getIntOptional(rs, "licensedruns");
				Float probability = getFloatOptional(rs, "probability");
				Integer productTypeID = getIntOptional(rs, "producttypeid");
				//Integer statusInt = getIntOptional(rs, "status");
				String statusEnum = getStringOptional(rs, "statusenum");
				String statusString = getStringOptional(rs, "statusstring");
				int duration = getInt(rs, "timeinseconds");
				Date startDate = getDate(rs, "startdate");
				Date endDate = getDate(rs, "enddate");
				Date pauseDate = getDateOptional(rs, "pausedate");
				Date completedDate = getDateOptional(rs, "completeddate");
				Integer completedCharacterID = getIntOptional(rs, "completedcharacterid");
				Integer successfulRuns = getIntOptional(rs, "successfulruns");
				boolean esi = getBooleanNotNull(rs, "esi", true);

				rawIndustryJob.setActivityID(activityID);
				rawIndustryJob.setBlueprintID(blueprintID);
				rawIndustryJob.setBlueprintLocationID(blueprintLocationID);
				rawIndustryJob.setBlueprintTypeID(blueprintTypeID);
				rawIndustryJob.setCompletedCharacterID(completedCharacterID);
				rawIndustryJob.setCompletedDate(completedDate);
				rawIndustryJob.setCost(cost);
				rawIndustryJob.setDuration(duration);
				rawIndustryJob.setEndDate(endDate);
				rawIndustryJob.setFacilityID(facilityID);
				rawIndustryJob.setInstallerID(installerID);
				rawIndustryJob.setJobID(jobID);
				rawIndustryJob.setLicensedRuns(licensedRuns);
				rawIndustryJob.setOutputLocationID(outputLocationID);
				rawIndustryJob.setPauseDate(pauseDate);
				rawIndustryJob.setProbability(probability);
				rawIndustryJob.setProductTypeID(productTypeID);
				rawIndustryJob.setRuns(runs);
				rawIndustryJob.setStartDate(startDate);
				rawIndustryJob.setStationID(stationID);
				rawIndustryJob.setStatus(RawConverter.toIndustryJobStatus(null, statusEnum, statusString));
				rawIndustryJob.setStatusString(statusString);
				rawIndustryJob.setSuccessfulRuns(successfulRuns);

				EsiOwner owner = owners.get(ownerID);
				if (owner == null) {
					continue;
				}
				MyIndustryJob industryJob = DataConverter.toMyIndustryJob(rawIndustryJob, owner);
				industryJob.setESI(esi);
				set(owner, industryJobs, industryJob);
			}
			for (Map.Entry<EsiOwner, Set<MyIndustryJob>> entry : industryJobs.entrySet()) {
				entry.getKey().setIndustryJobs(entry.getValue());
			}
			return true;
		} catch (SQLException ex) {
			LOG.error(ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	protected boolean create(Connection connection) {
		if (!tableExist(connection, INDUSTRY_JOBS_TABLE)) {
			String sql = "CREATE TABLE IF NOT EXISTS " + INDUSTRY_JOBS_TABLE + " (\n"
					+ "	ownerid INTEGER,\n"
					+ "	jobid INTEGER,"
					+ "	installerid INTEGER,"
					+ "	facilityid INTEGER,"
					+ "	stationid INTEGER,"
					+ "	activityid INTEGER,"
					+ "	blueprintid INTEGER,"
					+ "	blueprinttypeid INTEGER,"
					+ "	blueprintlocationid INTEGER,"
					+ "	outputlocationid INTEGER,"
					+ "	runs INTEGER,"
					+ "	cost REAL,"
					+ "	licensedruns INTEGER,"
					+ "	probability REAL,"
					+ "	producttypeid INTEGER,"
					+ "	statusenum TEXT,"
					+ "	statusstring TEXT,"
					+ "	timeinseconds INTEGER,"
					+ "	startdate INTEGER,"
					+ "	enddate INTEGER,"
					+ "	pausedate INTEGER,"
					+ "	completeddate INTEGER,"
					+ "	completedcharacterid INTEGER,"
					+ "	successfulruns INTEGER,"
					+ "	esi NUMERIC,"
					+ "	UNIQUE(ownerid, jobid)\n"
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
