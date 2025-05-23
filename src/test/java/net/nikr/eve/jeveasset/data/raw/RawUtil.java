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
package net.nikr.eve.jeveasset.data.raw;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.raw.RawAsset;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.api.raw.RawClone;
import net.nikr.eve.jeveasset.data.api.raw.RawContract.ContractStatus;
import net.nikr.eve.jeveasset.data.api.raw.RawContractItem;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob.IndustryJobStatus;
import net.nikr.eve.jeveasset.data.api.raw.RawJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder.MarketOrderState;
import net.nikr.eve.jeveasset.data.api.raw.RawPublicMarketOrder;
import net.nikr.eve.jeveasset.data.api.raw.RawTransaction;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


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
		Map<String, String> names = new HashMap<>();
		for (Field value : values) {
			if (Modifier.isStatic(value.getModifiers())) { //Ignore static fields
				continue;
			}
			Class<?> type = value.getType();
			if (ignore(value, raw)) {
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
			names.put(value.getName(), type.getName());
		}
		return names;
	}

	private static Set<String> getNames(Class<?> raw, Field[] values) {
		Set<String> names = new HashSet<>();
		for (Field value : values) {
			if (Modifier.isStatic(value.getModifiers())) { //Ignore static fields
				continue;
			}
			if (ignore(value, raw)) {
				continue;
			}
			names.add(value.getName());
		}
		return names;
	}

	private static boolean ignore(Field value, Class<?> raw) {
		if (value.getName().equals("itemFlag") && (raw.equals(RawAsset.class) || raw.equals(RawBlueprint.class) || raw.equals(RawClone.class))) {
			return true;
		}
		if (value.getName().equals("locationFlagEnum") && (raw.equals(RawAsset.class) || raw.equals(RawBlueprint.class))) {
			return true;
		}
		if (value.getName().equals("locationType") && (raw.equals(RawAsset.class))) { //Not used be jEveAssets
			return true;
		}
		if (value.getName().equals("locationTypeEnum") && (raw.equals(RawAsset.class)) || raw.equals(RawClone.class)) { //Not used be jEveAssets
			return true;
		}
		if (value.getName().equals("isBlueprintCopy") && raw.equals(RawAsset.class)) { //Converted to quantity
			return true;
		}
		if (value.getName().equals("itemId") && raw.equals(RawContractItem.class)) { //Only in public endpoint
			return true;
		}
		if (value.getName().equals("runs") && raw.equals(RawContractItem.class)) { //Only in public endpoint
			return true;
		}
		if (value.getName().equals("materialEfficiency") && raw.equals(RawContractItem.class)) { //Only in public endpoint
			return true;
		}
		if (value.getName().equals("timeEfficiency") && raw.equals(RawContractItem.class)) { //Only in public endpoint
			return true;
		}
		if (value.getName().equals("isBlueprintCopy") && raw.equals(RawContractItem.class)) { //Only in public endpoint
			return true;
		}
		if (value.getName().equals("isSingleton") && raw.equals(RawContractItem.class)) { //Only in corp/char endpoints (not working, though)
			return true;
		}
		if (value.getName().equals("rawQuantity") && raw.equals(RawContractItem.class)) { //Only in corp/char endpoints (not working, though)
			return true;
		}
		if (value.getName().equals("accountKey") && (raw.equals(RawTransaction.class) || raw.equals(RawJournal.class))) {
			return true;
		}
		if (value.getName().equals("isPersonal") && raw.equals(RawTransaction.class)) { //Only in character endpoint
			return true;
		}
		if (value.getName().equals("stationId") && raw.equals(RawIndustryJob.class)) { //stationId in character endpoint / locationId in corporation endpoint
			return true;
		}
		if (value.getName().equals("locationId") && raw.equals(RawIndustryJob.class)) { //stationId in character endpoint / locationId in corporation endpoint
			return true;
		}
		if (value.getName().equals("accountId") && raw.equals(RawMarketOrder.class)) { //accountId in character endpoint / walletDivision in corporation endpoint
			return true;
		}
		if (value.getName().equals("walletDivision") && raw.equals(RawMarketOrder.class)) { //accountId in character endpoint / walletDivision in corporation endpoint
			return true;
		}
		if (value.getName().equals("issuedBy") && raw.equals(RawMarketOrder.class)) { //Only in corporation endpoint
			return true;
		}
		if (value.getName().equals("state") && raw.equals(RawMarketOrder.class)) { //Only in history endpoints
			return true;
		}
		if (value.getName().equals("stateEnum") && raw.equals(RawMarketOrder.class)) { //Only in history endpoints
			return true;
		}
		if (value.getName().equals("isCorp") && raw.equals(RawMarketOrder.class)) { //Only in character endpoint
			return true;
		}
		if (value.getName().equals("isCorporation") && raw.equals(RawMarketOrder.class)) { //Only in character endpoint
			return true;
		}
		if (value.getName().equals("changes") && raw.equals(RawMarketOrder.class)) { //jEveAssets value
			return true;
		}
		if (value.getName().equals("changed") && raw.equals(RawMarketOrder.class)) { //jEveAssets value
			return true;
		}
		if (value.getName().equals("updateChanged") && raw.equals(RawMarketOrder.class)) { //jEveAssets value
			return true;
		}
		if (value.getName().equals("systemId") && raw.equals(RawPublicMarketOrder.class)) { //Only in RawPublicMarketOrder
			return true;
		}
		return false;
	}

	private static Set<String> getNames(Enum<?>[] ... values) {
		Set<String> names = new HashSet<>();
		for (Enum<?>[] value : values) {
			for (Enum<?> e : value) {
				if (e.equals(MarketOrderState.UNKNOWN)) { //jEveAssets value
					continue;
				}
				if (e.equals(MarketOrderState.OPEN)) { //jEveAssets value
					continue;
				}
				if (e.equals(MarketOrderState.CLOSED)) { //Only in XML
					continue;
				}
				if (e.equals(MarketOrderState.CHARACTER_DELETED)) { //Only in XML
					continue;
				}
				if (e.equals(MarketOrderState.PENDING)) { //Only in XML
					continue;
				}
				if (e.equals(IndustryJobStatus.ARCHIVED)) { //jEveAssets value
					continue;
				}
				if (e.equals(ContractStatus.ARCHIVED)) { //jEveAssets value
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
