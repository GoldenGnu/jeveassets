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

import java.util.Comparator;
import net.nikr.eve.jeveasset.i18n.TabsNpcStanding;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterStandingsResponse;
import net.troja.eve.esi.model.CorporationStandingsResponse;


public class RawNpcStanding {

	public static final TypeComparator FROM_TYPE_COMPARATOR = new TypeComparator();

	public enum FromType {
		FACTION("faction"){
			@Override
			String getI18N() {
				return TabsNpcStanding.get().typeFaction();
			}
		},
		NPC_CORP("npc_corp"){
			@Override
			String getI18N() {
				return TabsNpcStanding.get().typeCorporation();
			}
		},
		AGENT("agent"){
			@Override
			String getI18N() {
				return TabsNpcStanding.get().typeAgent();
			}
		};

		private final String value;

		FromType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		
		abstract String getI18N();

		@Override
		public String toString() {
			return getI18N();
		}
	}

	private String fromType;
    private FromType fromTypeEnum;
    private Integer fromId;
    private Float standing;

	
	public static RawNpcStanding create() {
		return new RawNpcStanding();
	}

	private RawNpcStanding() { }

	public RawNpcStanding(CharacterStandingsResponse response) {
		fromType = response.getFromTypeString();
		fromTypeEnum = RawConverter.toNpcStandingFromType(response.getFromType());
		fromId = response.getFromId();
		standing = response.getStanding();
	}

	public RawNpcStanding(CorporationStandingsResponse response) {
		fromType = response.getFromTypeString();
		fromTypeEnum = RawConverter.toNpcStandingFromType(response.getFromType());
		fromId = response.getFromId();
		standing = response.getStanding();
	}

	public RawNpcStanding(RawNpcStanding response) {
		fromType = response.getFromTypeString();
		fromTypeEnum = response.getFromType();
		fromId = response.getFromID();
		standing = response.getStanding();
	}

	public String getFromTypeString() {
		return fromType;
	}

	public void setFromTypeString(String fromType) {
		this.fromType = fromType;
	}

	public FromType getFromType() {
		return fromTypeEnum;
	}

	public void setFromType(FromType fromTypeEnum) {
		this.fromTypeEnum = fromTypeEnum;
	}

	public Integer getFromID() {
		return fromId;
	}

	public void setFromID(Integer fromId) {
		this.fromId = fromId;
	}

	public Float getStanding() {
		return standing;
	}

	public void setStanding(Float standing) {
		this.standing = standing;
	}

	public static class TypeComparator implements Comparator<FromType> {

		@Override
		public int compare(FromType o1, FromType o2) {
			return Integer.compare(o1.ordinal(), o2.ordinal());
		}
		
	}

}
