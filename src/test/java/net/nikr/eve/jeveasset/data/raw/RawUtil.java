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
package net.nikr.eve.jeveasset.data.raw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.api.raw.RawContainerLog;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalExtraInfo;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderState;
import net.nikr.eve.jeveasset.data.api.raw.RawTransaction;
import net.troja.eve.esi.model.CharacterWalletJournalExtraInfoResponse;
import net.troja.eve.esi.model.CorporationWalletJournalExtraInfoResponse;


public class RawUtil {

	public static void compare(Class<?> raw, Class<?> esi) {
		compare(getNames(raw, raw.getDeclaredFields()), getNames(raw, esi.getDeclaredFields()));
		compareTypes(getTypes(raw, raw.getDeclaredFields()), getTypes(raw, esi.getDeclaredFields()));
	}

	public static void compare(Enum<?>[] raw, Enum<?>[] esi) {
		compare(getNames(raw), getNames(esi));
	}

	public static void compare(Enum<?>[] raw, Enum<?>[] ... esi) {
		compare(getNames(raw), getNames(esi));
	}

	private static Map<String, String> getTypes(Class<?> raw, Field[] values) {
		Map<String, String> names = new HashMap<String, String>();
		for (Field value : values) {
			Class<?> type = value.getType();
			
			if (value.getName().equals("serialVersionUID")) { //serialVersionUID
				continue;
			}
			if (value.getName().equals("itemFlag") && (raw.equals(RawAsset.class) || raw.equals(RawBlueprint.class) || raw.equals(RawContainerLog.class))) {
				continue;
			}
			if (value.getName().equals("locationFlag") && (raw.equals(RawAsset.class) || raw.equals(RawBlueprint.class) || raw.equals(RawContainerLog.class))) {
				continue;
			}
			if (value.getName().equals("accountKey") && (raw.equals(RawTransaction.class) || raw.equals(RawJournal.class))) {
				continue;
			}
			if (value.getName().equals("isPersonal") && raw.equals(RawTransaction.class)) { //Only in character endpoint
				continue;
			}
			if (value.getName().equals("stationId") && raw.equals(RawIndustryJob.class)) { //stationId in character endpoint / locationId in corporation endpoint
				continue;
			}
			if (value.getName().equals("locationId") && raw.equals(RawIndustryJob.class)) { //stationId in character endpoint / locationId in corporation endpoint
				continue;
			}
			if (value.getName().equals("accountId") && raw.equals(RawMarketOrder.class)) { //accountId in character endpoint / walletDivision in corporation endpoint
				continue;
			}
			if (value.getName().equals("walletDivision") && raw.equals(RawMarketOrder.class)) { //accountId in character endpoint / walletDivision in corporation endpoint
				continue;
			}
			if (value.getName().equals("isCorp") && raw.equals(RawMarketOrder.class)) { //Only in character endpoint
				continue;
			}
			if (value.getName().equals("isCorporation") && raw.equals(RawMarketOrder.class)) { //Only in character endpoint
				continue;
			}
			if (type.isEnum()) { //Ignore enums
				continue;
			}
			if (type.equals(Date.class)) { //Ignore date formats
				continue;
			}
			if (type.equals(OffsetDateTime.class)) { //Ignore date formats
				continue;
			}
			if (type.equals(RawJournalExtraInfo.class)) { //Ignore RawJournalExtraInfo
				continue;
			}
			if (type.equals(CharacterWalletJournalExtraInfoResponse.class)) { //Ignore CharacterWalletJournalExtraInfoResponse
				continue;
			}
			if (type.equals(CorporationWalletJournalExtraInfoResponse.class)) { //Ignore CorporationWalletJournalExtraInfoResponse
				continue;
			}
			names.put(value.getName(), type.getName());
		}
		return names;
	}

	private static Set<String> getNames(Class<?> raw, Field[] values) {
		Set<String> names = new HashSet<String>();
		for (Field value : values) {
			if (value.getName().equals("serialVersionUID")) { //serialVersionUID
				continue;
			}
			if (value.getName().equals("itemFlag") && (raw.equals(RawAsset.class) || raw.equals(RawBlueprint.class) || raw.equals(RawContainerLog.class))) {
				continue;
			}
			if (value.getName().equals("locationFlag") && (raw.equals(RawAsset.class) || raw.equals(RawBlueprint.class) || raw.equals(RawContainerLog.class))) {
				continue;
			}
			if (value.getName().equals("accountKey") && (raw.equals(RawTransaction.class) || raw.equals(RawJournal.class))) {
				continue;
			}
			if (value.getName().equals("isPersonal") && raw.equals(RawTransaction.class)) { //Only in character endpoint
				continue;
			}
			if (value.getName().equals("stationId") && raw.equals(RawIndustryJob.class)) { //stationId in character endpoint / locationId in corporation endpoint
				continue;
			}
			if (value.getName().equals("locationId") && raw.equals(RawIndustryJob.class)) { //stationId in character endpoint / locationId in corporation endpoint
				continue;
			}
			if (value.getName().equals("accountId") && raw.equals(RawMarketOrder.class)) { //accountId in character endpoint / walletDivision in corporation endpoint
				continue;
			}
			if (value.getName().equals("walletDivision") && raw.equals(RawMarketOrder.class)) { //accountId in character endpoint / walletDivision in corporation endpoint
				continue;
			}
			if (value.getName().equals("isCorp") && raw.equals(RawMarketOrder.class)) { //Only in character endpoint
				continue;
			}
			if (value.getName().equals("isCorporation") && raw.equals(RawMarketOrder.class)) { //Only in character endpoint
				continue;
			}
			names.add(value.getName());
		}
		return names;
	}

	private static Set<String> getNames(Enum<?>[] ... values) {
		Set<String> names = new HashSet<String>();
		for (Enum<?>[] value : values) {
			for (Enum<?> e : value) {
				if (e.equals(MarketOrderState.UNKNOWN)) { //jEveAssets value
					continue;
				}
				names.add(e.name());
			}
		}
		return names;
	}

	private static void compare(Set<String> raw, Set<String> esi) {
		for (String name : raw) {
			if (!esi.contains(name)) {
				fail(name+ " removed from esi");
			}
		}
		for (String name : esi) {
			if (!raw.contains(name)) {
				fail(name+ " missing from raw");
			}
		}
	}

	private static void compareTypes(Map<String, String> raw, Map<String, String> esi) {
		assertEquals(esi.size(), raw.size());
		for (Map.Entry<String, String> entry : raw.entrySet()) {
			assertEquals(entry.getValue(), esi.get(entry.getKey()));
		}
		for (Map.Entry<String, String> entry : esi.entrySet()) {
			assertEquals(entry.getValue(), esi.get(entry.getKey()));
		}
	}
}
