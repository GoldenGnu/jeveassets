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
package net.nikr.eve.jeveasset.data.api.raw;

import net.troja.eve.esi.model.Skill;


public class RawSkill {
	private Integer activeSkillLevel;
	private Integer skillId;
	private Long skillpointsInSkill;
	private Integer trainedSkillLevel;

	/**
	 * New
	 */
	private RawSkill() { }

	public static RawSkill create() {
		return new RawSkill();
	}

	public RawSkill(RawSkill skill) {
		this.activeSkillLevel = skill.getActiveSkillLevel();
		this.skillId = skill.getTypeID();
		this.skillpointsInSkill = skill.getSkillpoints();
		this.trainedSkillLevel = skill.getTrainedSkillLevel();
	}

	public RawSkill(Skill skill) {
		this.activeSkillLevel = skill.getActiveSkillLevel();
		this.skillId = skill.getSkillId();
		this.skillpointsInSkill = skill.getSkillpointsInSkill();
		this.trainedSkillLevel = skill.getTrainedSkillLevel();
	}

	public Integer getActiveSkillLevel() {
		return activeSkillLevel;
	}

	public Integer getTypeID() {
		return skillId;
	}

	public Long getSkillpoints() {
		return skillpointsInSkill;
	}

	public Integer getTrainedSkillLevel() {
		return trainedSkillLevel;
	}

	public void setActiveSkillLevel(Integer activeSkillLevel) {
		this.activeSkillLevel = activeSkillLevel;
	}

	public void setTypeID(Integer typeID) {
		this.skillId = typeID;
	}

	public void setSkillpoints(Long skillpoints) {
		this.skillpointsInSkill = skillpoints;
	}

	public void setTrainedSkillLevel(Integer trainedSkillLevel) {
		this.trainedSkillLevel = trainedSkillLevel;
	}

}
