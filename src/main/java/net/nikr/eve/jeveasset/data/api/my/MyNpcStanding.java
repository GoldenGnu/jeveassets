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

package net.nikr.eve.jeveasset.data.api.my;

import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.raw.RawNpcStanding;
import net.nikr.eve.jeveasset.data.sde.Agent;
import net.nikr.eve.jeveasset.data.sde.NpcCorporation;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.table.containers.TextIcon;
import net.nikr.eve.jeveasset.io.online.EveImageGetter;
import net.nikr.eve.jeveasset.io.online.EveImageGetter.ImageSize;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class MyNpcStanding extends RawNpcStanding implements Comparable<MyNpcStanding> {

	public static ImageSize IMAGE_SIZE = ImageSize.SIZE_32;

	private final OwnerType owner;
	private final int factionID;
	private final int corporationID;
	private final int agentID;
	private final Agent agent;
	private final boolean connections;
	private final boolean criminalConnections;
	private String factionName;
	private String corporationName;
	private TextIcon ownerTextIcon = null;;
	private TextIcon factionTextIcon = null;
	private TextIcon corporationTextIcon = null;
	private TextIcon agentTextIcon = null;
	private int connectionsLevel;
	private int diplomacyLevel;
	private int criminalConnectionsLevel;

	public MyNpcStanding(RawNpcStanding rawNpcStanding, OwnerType owner) {
		super(rawNpcStanding);
		this.owner = owner;
		agentID = getFromType() == FromType.AGENT ? getFromID() : 0;
		agent = ApiIdConverter.getAgent(agentID);
		NpcCorporation npcCorporation;
		if (getFromType() == FromType.AGENT) {
			if (agent != null) {
				corporationID = agent.getCorporationID();
				npcCorporation = ApiIdConverter.getNpcCorporation(corporationID);
				factionID = npcCorporation.getFactionID();
			} else {
				npcCorporation = ApiIdConverter.getNpcCorporation(getCorporationID());
				corporationID = npcCorporation.getCorporationID();
				factionID = npcCorporation.getFactionID();
			}
		} else if (getFromType() == FromType.NPC_CORP) {
			corporationID = getFromID();
			npcCorporation = ApiIdConverter.getNpcCorporation(corporationID);
			factionID = npcCorporation.getFactionID();
		} else { //FromType.FACTION
			corporationID = 0;
			factionID = getFromID();
			npcCorporation = ApiIdConverter.getNpcCorporation(factionID);
		}
		if (npcCorporation != null) {
			connections = npcCorporation.isConnections();
			criminalConnections = npcCorporation.isCriminalConnections();
		} else {
			connections = false;
			criminalConnections = false;
		}
	}

	public String getOwnerName() {
		return owner.getOwnerName();
	}

	public int getFactionID() {
		return factionID;
	}

	public int getCorporationID() {
		return corporationID;
	}

	public int getAgentID() {
		return agentID;
	}

	public Agent getAgent() {
		return agent;
	}

	public String getAgentName() {
		return agent.getAgent();
	}

	public TextIcon getOwnerTextIcon() {
		if (ownerTextIcon == null ) {
			if (owner.isCharacter()) {
				ownerTextIcon = new TextIcon(Images.getIcon((int)owner.getOwnerID(), EveImageGetter.ImageCategory.CHARACTERS), owner.getOwnerName());
			} else {
				ownerTextIcon = new TextIcon(Images.getIcon((int)owner.getOwnerID(), EveImageGetter.ImageCategory.CORPORATIONS), owner.getOwnerName());
			}
		}
		return ownerTextIcon;
	}

	public TextIcon getFactionTextIcon() {
		if (factionTextIcon == null && factionID > 0) {
			factionTextIcon = new TextIcon(Images.getIcon(factionID, EveImageGetter.ImageCategory.CORPORATIONS), factionName);
		}
		return factionTextIcon;
	}

	public TextIcon getCorporationTextIcon() {
		if (corporationTextIcon == null && corporationID > 0 && (getFromType() == FromType.AGENT || getFromType() == FromType.NPC_CORP)) {
			corporationTextIcon = new TextIcon(Images.getIcon(corporationID, EveImageGetter.ImageCategory.CORPORATIONS), corporationName);
		}
		return corporationTextIcon;
	}

	public TextIcon getAgentTextIcon() {
		if (agentTextIcon == null && getFromType() == FromType.AGENT) {
			agentTextIcon = new TextIcon(Images.getIcon(this), getAgentName());
		}
		return agentTextIcon;
	}

	public void setFactionName(String factionName) {
		this.factionName = factionName;
	}

	public void setCorporationName(String corporationName) {
		this.corporationName = corporationName;
	}

	public void updateSkills() {
		for (MySkill mySkill : owner.getSkills()) {
			if (mySkill.getTypeID() == 3359) { //Connections
				connectionsLevel = mySkill.getActiveSkillLevel();
			}
			if (mySkill.getTypeID() == 3357) { //Connections
				diplomacyLevel = mySkill.getActiveSkillLevel();
			}
			if (mySkill.getTypeID() == 3361) { //Connections
				criminalConnectionsLevel = mySkill.getActiveSkillLevel();
			}
		}
	}

	public Float getStandingEffective() {
		Float standing = getStanding();
		if (standing >= 0) {
			if (connections && connectionsLevel > 0) {
				standing = calc(standing, connectionsLevel);
			}
			if (criminalConnections && criminalConnectionsLevel > 0) {
				standing = calc(standing, criminalConnectionsLevel);
			}
		} else {
			standing = calc(standing, diplomacyLevel);
		}
		return standing;
	}

	public Float getStandingMaximum() {
		return calc(getStanding(), 5);
	}

	private Float calc(Float standing, int modifier) {
		return standing + (10 - standing) * 0.04F * modifier;
	}

	@Override
	public int compareTo(MyNpcStanding o) {
		int compare = MyNpcStanding.FROM_TYPE_COMPARATOR.compare(this.getFromType(), o.getFromType());
		if (compare != 0) {
			return compare;
		}
		return Float.compare(o.getStanding(), this.getStanding());
		
	}
}
