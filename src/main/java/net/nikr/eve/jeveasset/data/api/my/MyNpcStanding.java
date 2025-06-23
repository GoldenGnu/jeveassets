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
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.table.containers.TextIcon;
import net.nikr.eve.jeveasset.io.online.EveImageGetter.ImageSize;


public class MyNpcStanding extends RawNpcStanding implements Comparable<MyNpcStanding> {

	public static ImageSize IMAGE_SIZE = ImageSize.SIZE_32;

	private final OwnerType owner;
	private final int factionID;
	private final int corporationID;
	private final int agentID;
	private final boolean connections;
	private final boolean criminalConnections;
	private String factionName;
	private String corporationName;
	private String agentName;
	private TextIcon factionTextIcon = null;
	private TextIcon corporationTextIcon = null;
	private TextIcon agentTextIcon = null;
	private int connectionsLevel;
	private int diplomacyLevel;
	private int criminalConnectionsLevel;

	public MyNpcStanding(RawNpcStanding rawNpcStanding, OwnerType owner) {
		super(rawNpcStanding);
		this.owner = owner;
		if (getFromType() == FromType.AGENT) {
			agentID = getFromID();
		} else {
			agentID = 0;
		}
		if (getFromType() == FromType.AGENT) {
			Agent agent = StaticData.get().getAgents().get(getFromID());
			if (agent != null) {
				corporationID = agent.getCorporationID();
			} else {
				corporationID = 0;
			}
		} else if (getFromType() == FromType.NPC_CORP) {
			corporationID = getFromID();
		} else {
			corporationID = 0;
		}
		NpcCorporation npcCorporation = StaticData.get().getNpcCorporations().get(corporationID);
		
		if (getFromType() == FromType.FACTION) {
			factionID = getFromID();
		} else if (npcCorporation != null) {
			factionID = npcCorporation.getFactionID();
		} else {
			factionID = 0;
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

	public TextIcon getFactionTextIcon() {
		if (factionTextIcon == null) {
			factionTextIcon = new TextIcon(Images.getIcon(factionID), factionName);
		}
		return factionTextIcon;
	}

	public TextIcon getCorporationTextIcon() {
		if (corporationTextIcon == null && (getFromType() == FromType.AGENT || getFromType() == FromType.NPC_CORP)) {
			corporationTextIcon = new TextIcon(Images.getIcon(corporationID), corporationName);
		}
		return corporationTextIcon;
	}

	public TextIcon getAgentTextIcon() {
		if (agentTextIcon == null && getFromType() == FromType.AGENT) {
			agentTextIcon = new TextIcon(Images.getIcon(this), agentName);
		}
		return agentTextIcon;
	}

	public void setFactionName(String factionName) {
		this.factionName = factionName;
	}

	public void setCorporationName(String corporationName) {
		this.corporationName = corporationName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
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
