/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.local;

import eve.nikr.net.client.model.Prices;
import java.io.File;
import java.util.Date;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager.ContractPriceData;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager.ContractPriceItem;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager.ReturnData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;


public class ContractPriceTest extends TestUtil{

	private static final String FILENAME = "target" + File.separator + "contract_prices_text.json";
	private static final Integer TYPE_ID = 34;
	private static final String TYPE_NAME = "Tritanium";
	private static final Double MEDIAN = 1.0;
	private static final Double AVERAGE = 2.0;
	private static final Double MINIMUM = 3.0;
	private static final Double MAXIMUM = 4.0;
	private static final Double FIVE_PERCENT = 5.0;
	private static final Integer CONTRACTS = 6;
	private static final boolean BPC = true;
	private static final boolean BPO = false;
	private static final Integer ME = 7;
	private static final Integer TE = 8;
	private static final Date DATE = new Date(System.currentTimeMillis() + (1L * 60L * 60L * 1000L));


	@Test
	public void testReadWrite() {
		ContractPriceItem contractPriceType = new ContractPriceItem(TYPE_ID, BPC, BPO, ME, TE, 100);
		Prices prices = new Prices();
		prices.setTypeId(TYPE_ID);
		prices.setTypeName(TYPE_NAME);
		prices.setMedian(MEDIAN);
		prices.setAverage(AVERAGE);
		prices.setMinimum(MINIMUM);
		prices.setMaximum(MAXIMUM);
		prices.setFivePercent(FIVE_PERCENT);
		prices.setContracts(CONTRACTS);
		ReturnData returnData = new ReturnData(contractPriceType, DATE, prices, true);
		ContractPriceData out = new ContractPriceData();
		out.add(returnData);
		//Save
		ContractPriceWriter.save(FILENAME, out, false);
		//Load
		ContractPriceData in = ContractPriceReader.load(FILENAME, false);
		//assertThat(in.getDate().getTime(), equalTo(DATE.getTime()));
		assertThat(in.getPrices(contractPriceType).getTypeId(), equalTo(TYPE_ID));
		assertThat(in.getPrices(contractPriceType).getTypeName(), equalTo(TYPE_NAME));
		assertThat(in.getPrices(contractPriceType).getMedian(), equalTo(MEDIAN));
		assertThat(in.getPrices(contractPriceType).getAverage(), equalTo(AVERAGE));
		assertThat(in.getPrices(contractPriceType).getMinimum(), equalTo(MINIMUM));
		assertThat(in.getPrices(contractPriceType).getMaximum(), equalTo(MAXIMUM));
		assertThat(in.getPrices(contractPriceType).getFivePercent(), equalTo(FIVE_PERCENT));
		assertThat(in.getPrices(contractPriceType).getContracts(), equalTo(CONTRACTS));
		File file = new File(FILENAME);
		assertThat(file.delete(), equalTo(true));
	}

}
