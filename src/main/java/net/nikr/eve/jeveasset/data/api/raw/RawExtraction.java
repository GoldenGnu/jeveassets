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
package net.nikr.eve.jeveasset.data.api.raw;

import java.util.Date;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CorporationMiningExtractionsResponse;


public class RawExtraction {

	private Date chunkArrivalTime;
	private Date extractionStartTime;
	private Integer moonId;
	private Date naturalDecayTime;
	private Long structureId;

	private RawExtraction() { }

	public static RawExtraction create() {
		return new RawExtraction();
	}

	public RawExtraction(RawExtraction extraction) {
		this.chunkArrivalTime = extraction.chunkArrivalTime;
		this.extractionStartTime = extraction.extractionStartTime;
		this.moonId = extraction.moonId;
		this.naturalDecayTime = extraction.naturalDecayTime;
		this.structureId = extraction.structureId;
	}

	public RawExtraction(CorporationMiningExtractionsResponse response) {
		this.chunkArrivalTime = RawConverter.toDate(response.getChunkArrivalTime());
		this.extractionStartTime = RawConverter.toDate(response.getExtractionStartTime());
		this.moonId = response.getMoonId();
		this.naturalDecayTime = RawConverter.toDate(response.getNaturalDecayTime());
		this.structureId = response.getStructureId();
	}

	public Date getChunkArrivalTime() {
		return chunkArrivalTime;
	}

	public void setChunkArrivalTime(Date chunkArrivalTime) {
		this.chunkArrivalTime = chunkArrivalTime;
	}

	public Date getExtractionStartTime() {
		return extractionStartTime;
	}

	public void setExtractionStartTime(Date extractionStartTime) {
		this.extractionStartTime = extractionStartTime;
	}

	public Integer getMoonID() {
		return moonId;
	}

	public void setMoonID(Integer moonId) {
		this.moonId = moonId;
	}

	public Date getNaturalDecayTime() {
		return naturalDecayTime;
	}

	public void setNaturalDecayTime(Date naturalDecayTime) {
		this.naturalDecayTime = naturalDecayTime;
	}

	public Long getStructureID() {
		return structureId;
	}

	public void setStructureID(Long structureId) {
		this.structureId = structureId;
	}

}
