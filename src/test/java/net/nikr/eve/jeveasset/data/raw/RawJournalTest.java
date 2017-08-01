/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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

import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.raw.RawJournal.JournalPartyType;
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.troja.eve.esi.model.CharacterWalletJournalResponse;
import net.troja.eve.esi.model.CharacterWalletJournalResponse.FirstPartyTypeEnum;
import net.troja.eve.esi.model.CharacterWalletJournalResponse.RefTypeEnum;
import net.troja.eve.esi.model.CharacterWalletsJournalResponse.SecondPartyTypeEnum;
import org.junit.Test;


public class RawJournalTest extends TestUtil {


	@Test
	public void rawJournalTest() {
		RawUtil.compare(RawJournal.class, CharacterWalletJournalResponse.class);
		RawUtil.compare(JournalPartyType.values(), FirstPartyTypeEnum.values());
		RawUtil.compare(JournalPartyType.values(), SecondPartyTypeEnum.values());
		RefTypeEnum[] refTypes = RefTypeEnum.values();
		assertEquals(29, refTypes.length);
		int undefined = 0;
		for (RefTypeEnum refType : refTypes) {
			RawJournalRefType rawJournalRefType = RawConverter.toJournalRefType(refType);
			if (rawJournalRefType == RawJournalRefType.UNDEFINED) {
				undefined++;
			}
		}
		assertEquals(1, undefined);
	}
	
}
