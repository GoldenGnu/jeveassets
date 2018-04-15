/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.api.raw;

import java.util.Date;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterIndustryJobsResponse;
import net.troja.eve.esi.model.CorporationIndustryJobsResponse;

public class RawIndustryJob {

	public enum IndustryJobStatus {
		ACTIVE("active"),
		CANCELLED("cancelled"),
		DELIVERED("delivered"),
		PAUSED("paused"),
		READY("ready"),
		REVERTED("reverted");

		private final String value;

		IndustryJobStatus(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}

		public static IndustryJobStatus fromValue(String text) {
            for (IndustryJobStatus b : IndustryJobStatus.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
	}

	private Integer activityId = null;
	private Long blueprintId = null;
	private Long blueprintLocationId = null;
	private Integer blueprintTypeId = null;
	private Integer completedCharacterId = null;
	private Date completedDate = null;
	private Double cost = null;
	private Integer duration = null;
	private Date endDate = null;
	private Long facilityId = null;
	private Integer installerId = null;
	private Integer jobId = null;
	private Integer licensedRuns = null;
	private Long outputLocationId = null;
	private Date pauseDate = null;
	private Float probability = null;
	private Integer productTypeId = null;
	private Integer runs = null;
	private Date startDate = null;
	private Long stationId = null;
	private IndustryJobStatus status = null;
	private Integer successfulRuns = null;

	/**
	 * New
	 */
	private RawIndustryJob() {
	}

	public static RawIndustryJob create() {
		return new RawIndustryJob();
	}

	/**
	 * Raw
	 *
	 * @param industryJob
	 */
	protected RawIndustryJob(RawIndustryJob industryJob) {
		activityId = industryJob.activityId;
		blueprintId = industryJob.blueprintId;
		blueprintLocationId = industryJob.blueprintLocationId;
		blueprintTypeId = industryJob.blueprintTypeId;
		completedCharacterId = industryJob.completedCharacterId;
		completedDate = industryJob.completedDate;
		cost = industryJob.cost;
		duration = industryJob.duration;
		endDate = industryJob.endDate;
		facilityId = industryJob.facilityId;
		installerId = industryJob.installerId;
		jobId = industryJob.jobId;
		licensedRuns = industryJob.licensedRuns;
		outputLocationId = industryJob.outputLocationId;
		pauseDate = industryJob.pauseDate;
		probability = industryJob.probability;
		productTypeId = industryJob.productTypeId;
		runs = industryJob.runs;
		startDate = industryJob.startDate;
		stationId = industryJob.stationId;
		status = industryJob.status;
		successfulRuns = industryJob.successfulRuns;
	}

	/**
	 * ESI Character
	 *
	 * @param industryJob
	 */
	public RawIndustryJob(CharacterIndustryJobsResponse industryJob) {
		activityId = industryJob.getActivityId();
		blueprintId = industryJob.getBlueprintId();
		blueprintLocationId = industryJob.getBlueprintLocationId();
		blueprintTypeId = industryJob.getBlueprintTypeId();
		completedCharacterId = industryJob.getCompletedCharacterId();
		completedDate = RawConverter.toDate(industryJob.getCompletedDate());
		cost = industryJob.getCost();
		duration = industryJob.getDuration();
		endDate = RawConverter.toDate(industryJob.getEndDate());
		facilityId = industryJob.getFacilityId();
		installerId = industryJob.getInstallerId();
		jobId = industryJob.getJobId();
		licensedRuns = industryJob.getLicensedRuns();
		outputLocationId = industryJob.getOutputLocationId();
		pauseDate = RawConverter.toDate(industryJob.getPauseDate());
		probability = industryJob.getProbability();
		productTypeId = industryJob.getProductTypeId();
		runs = industryJob.getRuns();
		startDate = RawConverter.toDate(industryJob.getStartDate());
		stationId = industryJob.getStationId();
		status = IndustryJobStatus.valueOf(industryJob.getStatus().name());
		successfulRuns = industryJob.getSuccessfulRuns();
	}

	/**
	 * ESI Corporation
	 *
	 * @param industryJob
	 */
	public RawIndustryJob(CorporationIndustryJobsResponse industryJob) {
		activityId = industryJob.getActivityId();
		blueprintId = industryJob.getBlueprintId();
		blueprintLocationId = industryJob.getBlueprintLocationId();
		blueprintTypeId = industryJob.getBlueprintTypeId();
		completedCharacterId = industryJob.getCompletedCharacterId();
		completedDate = RawConverter.toDate(industryJob.getCompletedDate());
		cost = industryJob.getCost();
		duration = industryJob.getDuration();
		endDate = RawConverter.toDate(industryJob.getEndDate());
		facilityId = industryJob.getFacilityId();
		installerId = industryJob.getInstallerId();
		jobId = industryJob.getJobId();
		licensedRuns = industryJob.getLicensedRuns();
		outputLocationId = industryJob.getOutputLocationId();
		pauseDate = RawConverter.toDate(industryJob.getPauseDate());
		probability = industryJob.getProbability();
		productTypeId = industryJob.getProductTypeId();
		runs = industryJob.getRuns();
		startDate = RawConverter.toDate(industryJob.getStartDate());
		stationId = industryJob.getLocationId();
		status = IndustryJobStatus.valueOf(industryJob.getStatus().name());
		successfulRuns = industryJob.getSuccessfulRuns();
	}

	/**
	 * EveKit
	 *
	 * @param industryJob
	 */
	public RawIndustryJob(enterprises.orbital.evekit.client.model.IndustryJob industryJob) {
		activityId = industryJob.getActivityID();
		blueprintId = industryJob.getBlueprintID();
		blueprintLocationId = industryJob.getBlueprintLocationID();
		blueprintTypeId = industryJob.getBlueprintTypeID();
		completedCharacterId = industryJob.getCompletedCharacterID();
		completedDate = RawConverter.toDate(industryJob.getCompletedDateDate());
		cost = industryJob.getCost();
		duration = industryJob.getTimeInSeconds();
		endDate = RawConverter.toDate(industryJob.getEndDateDate());
		facilityId = industryJob.getFacilityID();
		installerId = industryJob.getInstallerID();
		jobId = industryJob.getJobID();
		licensedRuns = industryJob.getLicensedRuns();
		outputLocationId = industryJob.getOutputLocationID();
		pauseDate = RawConverter.toDate(industryJob.getPauseDateDate());
		probability = RawConverter.toFloat(industryJob.getProbability());
		productTypeId = industryJob.getProductTypeID();
		runs = industryJob.getRuns();
		startDate = RawConverter.toDate(industryJob.getStartDateDate());
		stationId = industryJob.getStationID();
		status = RawConverter.toIndustryJobStatus(industryJob.getStatus());
		successfulRuns = industryJob.getSuccessfulRuns();
	}

	/**
	 * EveAPI
	 *
	 * @param industryJob
	 */
	public RawIndustryJob(com.beimin.eveapi.model.shared.IndustryJob industryJob) {
		activityId = industryJob.getActivityID();
		blueprintId = industryJob.getBlueprintID();
		blueprintLocationId = industryJob.getBlueprintLocationID();
		blueprintTypeId = industryJob.getBlueprintTypeID();
		completedCharacterId = (int) industryJob.getCompletedCharacterID();
		completedDate = industryJob.getCompletedDate();
		cost = industryJob.getCost();
		duration = industryJob.getTimeInSeconds();
		endDate = industryJob.getEndDate();
		facilityId = industryJob.getFacilityID();
		installerId = (int) industryJob.getInstallerID();
		jobId = (int) industryJob.getJobID();
		licensedRuns = industryJob.getLicensedRuns();
		outputLocationId = industryJob.getOutputLocationID();
		pauseDate = industryJob.getPauseDate();
		probability = (float) industryJob.getProbability();
		productTypeId = industryJob.getProductTypeID();
		runs = industryJob.getRuns();
		startDate = industryJob.getStartDate();
		stationId = industryJob.getStationID();
		status = RawConverter.toIndustryJobStatus(industryJob.getStatus());
		successfulRuns = industryJob.getSuccessfulRuns();
	}

	public final Integer getActivityID() {
		return activityId;
	}

	public void setActivityID(Integer activityId) {
		this.activityId = activityId;
	}

	public Long getBlueprintID() {
		return blueprintId;
	}

	public void setBlueprintID(Long blueprintId) {
		this.blueprintId = blueprintId;
	}

	public Long getBlueprintLocationID() {
		return blueprintLocationId;
	}

	public void setBlueprintLocationID(Long blueprintLocationId) {
		this.blueprintLocationId = blueprintLocationId;
	}

	public Integer getBlueprintTypeID() {
		return blueprintTypeId;
	}

	public void setBlueprintTypeID(Integer blueprintTypeId) {
		this.blueprintTypeId = blueprintTypeId;
	}

	public Integer getCompletedCharacterID() {
		return completedCharacterId;
	}

	public void setCompletedCharacterID(Integer completedCharacterId) {
		this.completedCharacterId = completedCharacterId;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public final Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Long getFacilityID() {
		return facilityId;
	}

	public void setFacilityID(Long facilityId) {
		this.facilityId = facilityId;
	}

	public final long getInstallerID() {
		return installerId;
	}

	public void setInstallerID(Integer installerId) {
		this.installerId = installerId;
	}

	public Integer getJobID() {
		return jobId;
	}

	public void setJobID(Integer jobId) {
		this.jobId = jobId;
	}

	public Integer getLicensedRuns() {
		return licensedRuns;
	}

	public void setLicensedRuns(Integer licensedRuns) {
		this.licensedRuns = licensedRuns;
	}

	public Long getOutputLocationID() {
		return outputLocationId;
	}

	public void setOutputLocationID(Long outputLocationId) {
		this.outputLocationId = outputLocationId;
	}

	public Date getPauseDate() {
		return pauseDate;
	}

	public void setPauseDate(Date pauseDate) {
		this.pauseDate = pauseDate;
	}

	public Float getProbability() {
		return probability;
	}

	public void setProbability(Float probability) {
		this.probability = probability;
	}

	public Integer getProductTypeID() {
		return productTypeId;
	}

	public void setProductTypeID(Integer productTypeId) {
		this.productTypeId = productTypeId;
	}

	public final Integer getRuns() {
		return runs;
	}

	public void setRuns(Integer runs) {
		this.runs = runs;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Long getStationID() {
		return stationId;
	}

	public void setStationID(Long stationId) {
		this.stationId = stationId;
	}

	public final IndustryJobStatus getStatus() {
		return status;
	}

	public void setStatus(IndustryJobStatus status) {
		this.status = status;
	}

	public Integer getSuccessfulRuns() {
		return successfulRuns;
	}

	public void setSuccessfulRuns(Integer successfulRuns) {
		this.successfulRuns = successfulRuns;
	}

}
