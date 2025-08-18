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
package net.nikr.eve.jeveasset.io.local;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileContainer;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileFilter.StockpileFlag;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


public class StockpileDataReadWriteTest extends TestUtil {

	@Test
	public void testText() {
		System.out.println("testText");
		testIO(new SaveLoad() {
			@Override
			public List<Stockpile> saveAndLoad(List<Stockpile> stockpiles) {
				String data = StockpileWriter.save(stockpiles);
				System.out.println(data);
				return StockpileReader.load(data);
			}
		});
	}

	@Test
	public void testXml() {
		System.out.println("testXml");
		testIO(new SaveLoad() {
			int i = 0;
			@Override
			public List<Stockpile> saveAndLoad(List<Stockpile> stockpiles) {
				String filename = "stockpile_test_" + i + ".xml";
				i++;
				SettingsWriter.saveStockpiles(stockpiles, filename);
				List<Stockpile> rStockpile = SettingsReader.loadStockpile(filename);
				File file = new File(filename);
				assertTrue(file.exists());
				assertTrue(file.delete());
				return rStockpile;
			}
		});
	}

	@Test
	public void testText740() {
		System.out.println("testText740");
		final String[] data = new String[12];
		data[0] = "eNpNj0EOgyAQRe_y16SxiiZwiG66JC4EMcGKNEVXxrs72lZkM483ZPijFoyQeE7BvN5usI_GWzB4yOqWMRjfQE6f2TLEDlItoHvXDJGE1iZxwnjBC-uQdEI9JZvQ_v_sT9W3AyTfK0Ur6fXZcS1kkdG585wCHyENFcRZR1rFme-0lf38GC6dY8haM-zb5awgonRKlLwquRD13nIkCDYsblqG";
		data[1] = "eNpNj7sOgzAMRf_lzlFFISAlH9GlI2IgIUhpeVRNmBD_jkMfJotPji3rul4xQeMeZ_t8-cHd2tFBYISuLpmAHVvo-F6cQOih6xX079shkDDGMjOGE57YzKwZTWTL6P70YOoGaJkqZStp_JfLd9BFRu8qcwp8hLRUEBYT6BRvP5Ob-PppPnWO7VsjkK7LRUFE6WpVyqqUSjWp5UkQ7CyBWoY=";
		data[2] = "eNpNj0EOgyAQRe_y16SxiibOIbrp0rgQxIQWtCm6Mt7dwTYim3n8T8ibZsUIwnOe9PtjnXl03kDAg6pbJqB9B5q_ixEIA6hZcd6V0qChcyFywnDBC6spxQnVnNKE5qRXot6BZJysVvLzs7E9qMj43GXOwoek5oGwqMCrWP0z3sQ_H6dLc3yytQJxu1wUTKzX1KWsSlnXbawsBww7L19ahg==";
		data[3] = "eNpNj8EOgyAQRP9lzqSxiibwEb30aDwAYkIr0hQ8Gf-9q21FLvt2huzOtgsmSNxTMM-XG-1NeQsGD9lcCgbjFWR6z5YhDpDtAuoHNUYStDaZM8YTnliHLIf_TJ2ymNEe9MjUj5B8qxStpu-H43rIqqB35SUF3kMaKoizjnSKM99tK_vpUzg5-5C1Y9iuK1lFROlaUfOm5kJ0m-VIIPgALU9ahg==";
		data[4] = "eNpNj8EOgyAQRP9lzqSxCibwEb30aDwIYkKL0hQ9Gf_dxTYil33MbHZnmxUTFJ5zMO-P8_bRjRYMI1R9KxjM2EHN38UyxAGqWUH_ofORBK1N5ozxghfW4RwUsjjn3oz2pFem3kPxVCmaoPbTcT1UVdC785ICHyENFcRFRzrFmd_ijf31KVycY8jWMqTrSlYRUbxGCl4LLmWbLEcCwQ4trVqG";
		data[5] = "eNpNjzEOgzAMRe_y56iiEJCSQ3TpGDGQEKRQQqoGJsTdMbQlePHzt2V_qwUjJJ5TMK-3G-yj8RYMHrK6ZQzGN5DTZ7YMsYNUC6jumiGSoLVJnDBe8MI6JDmhnpKa0J7U_6_37QDJ90zWSpo-R1wLWWQUd56T4cOkoYQ460ivOPPdsbKfPoZL51iy1gz7dzkriMidEiWvSi5EvbccCQQbLD1ahg==";
		data[6] = "eNpNj8EOgyAQRP9lzqSxiibwEb30aDwAYkKL2hQ8Gf_d1TYgl307Q3Zn2xUTJJ5xNu-P8_ahRguGEbK5FQxmVJDxu1iGMEC2K6gflA8kaG0yZwwXvLCes5xRxzQ-JtEmemXqPSQ_KkWr6XtyXA9ZFfTuvKTAZ0hDBWHRgU5x5rdiY399mi_OOWTrGI7rSlYRUbpW1LypuRDdYTkSCHYs_VqG";
		data[7] = "eNpNj0EOgyAQRe_y16SxCiZwiG66NC4AMaFVaQqujHfvaFuRzTzekOFPs2CCwj0F-3z5wd306MAwQtWXgsGOGiq9Z8cQe6hmAd17PUQSxtjMGeMJT2xC1hlNyjb9f3KHe2TqBii-VYom6PXR8R1UVdC58pIC7yEtFcTZRFrF2-_clf38FE6dfcjaMmzblawionSNFLwWXMp2a3kSBB8smlqG";
		data[8] = "eNpNj80OgyAQhN9lzqSxiibyEL30aDwIYkLrT1PwZHx3F2NYuezHzEBmmw0zFN5hMd-fG-2rmywEJqjqkQmYqYMK_9UK-AGq2UD3oRs9CVobZkZv0gt_CywcYNSBVUab6MPUj1AyTqpWUjw5rocqMjpPmVPhs6ShAb9qT6u4q88uLn1ebs75yd4KxO1yURBRvaYuZVXKum6j5UggOAAudFqG";
		data[9] = "eNpNj7EOgzAMRP_l5qiiEJCSj-jSETGQEKS0BKoGJsS_16EI48Uvd3Z0rleM0HjOk31__OAebXAQCNDVLROwoYWev4sTiD10vYLefTtEEoyxzIzxgon_22ZildHMrDK6k15M3QAtU6doJY2fju-gi4zqLnMKvIe01BAXE-kUf2TYxKGP08XZP9kagXRdLgoiilerUlalVKpJlieB4Act-lqG";
		data[10] = "eNpNj7sOgzAMRf_lzlFFISCRj-jSETGQEKS0PKomTIh_x0EoJotPji3rutkwQ-EdFvP9udG-uslCYIKqHpmAmTqo8F-tgB-gmg30H7rRk9DaMJs0xtLfWC-sGXVgy2gTfZj6EUrGStFKGk8d10MVGb2nzCnwGdJQgV-1p1PcFW0Xl5-XW-dcsrcC8bpcFEQUr6lLWZWyrtvYciQIDi7GWoY=";
		data[11] = "eNpNj8sOgyAQRf_lrkljFU2cj-imS-MCEBNaH03BlfHfOxgrspnDuYTcaVZMIDzDbN4fN9iHGi0ERlB1ywTMqEDhu1gB34OaFXzv1eBZaG3-WaTD-gteWM9JJ9Qh2YT2pFeibgDJOLlayc_PxHWgIuNzlzkX3ksaHvCL9ryKO1pu4vDTfEn2T7ZWIG6Xi4KJ6zV1KatS1nUbI8eC4QcvB1qG";
		boolean[] b = new boolean[12];
		for (int i = 0; i < b.length; i++) {
			b[i] = false;
		}
		int last = -1;
		for (int i = 0; i < b.length; i++) {
			b[i] = true;
			System.out.println("Testing index: " + i);
			if (last > -1) {
				b[last] = false;
			}
			last = i;
			final int index = i;
			testIO(true, new SaveLoad() {
				@Override
				public List<Stockpile> saveAndLoad(List<Stockpile> stockpiles) {
					return StockpileReader.load(data[index]);
				}
			}, b);
		}
	}

	@Test
	public void testXml740() {
		System.out.println("testXml740");
		boolean[] b = new boolean[12];
		for (int i = 0; i < b.length; i++) {
			b[i] = false;
		}
		int last = -1;
		for (int i = 0; i < b.length; i++) {
			b[i] = true;
			System.out.println("Testing index: " + i);
			if (last > -1) {
				b[last] = false;
			}
			last = i;
			final int index = i;
			testIO(true, new SaveLoad() {
				@Override
				public List<Stockpile> saveAndLoad(List<Stockpile> stockpiles) {
					String filename;
					try {
						filename = new File(StockpileDataReadWriteTest.class.getResource("/740/" + "stockpile_test_" + index + ".xml").toURI()).getAbsolutePath();
					} catch (URISyntaxException ex) {
						throw new RuntimeException(ex);
					}
					return SettingsReader.loadStockpile(filename);
				}
			}, b);
		}
	}

	@Test
	public void testXml750() {
		System.out.println("testXml750");
		boolean[] b = new boolean[12];
		for (int i = 0; i < b.length; i++) {
			b[i] = false;
		}
		int last = -1;
		for (int i = 0; i < b.length; i++) {
			b[i] = true;
			System.out.println("Testing index: " + i);
			if (last > -1) {
				b[last] = false;
			}
			last = i;
			final int index = i;
			testIO(false, new SaveLoad() {
				@Override
				public List<Stockpile> saveAndLoad(List<Stockpile> stockpiles) {
					String filename;
					try {
						filename = new File(StockpileDataReadWriteTest.class.getResource("/750/" + "stockpile_test_" + index + ".xml").toURI()).getAbsolutePath();
					} catch (URISyntaxException ex) {
						throw new RuntimeException(ex);
					}
					return SettingsReader.loadStockpile(filename);
				}
			}, b);
		}
	}

	@Test
	public void testText750() {
		System.out.println("testText750");
		final String[] data = new String[12];
		data[0] = "eNpdj8EOgyAMht_lP5PFKZrIQ-yyo_EgiAlOdBl4Mr77qm7CxqUfX0tpqwUjBO5-Uo-nGfStsRoMFqK4JAzKNhD-NWsG10FUC-jeNYMjIaUKHNBFGLGcgg4ofbAB9ffP_lR9O0DwLdJoOVWfGdNCZAmdK09p4H1IRQFulo5WMerotrKPH6coszdZa4Zju45C-vtmU9lfMS1QlTkvcl6W9SYMCYI3gotlwQ==";
		data[1] = "eNpdj8sOgjAQRf_lrhuDUEjaj3DjkrCgpU2qPIyFFeHfHUAdtJs5PZ1p7pQzemhcx8HeH6F1l7pzEOigi1MiYLsaenxOTiB66HIG3X3dRhLGWGbGeMADm4E1oxnZMrov3ZiaFlqulbLl1P7JFRroLKFzlikF3kJaKoiTibRKsHvnIt6-Hw4v2-9LJbBv56mkvzOryv6aaYFS5bLIpVLVKgIJgheCnmXB";
		data[2] = "eNpdj80OgyAQhN9lzqSxiibuQ_TSo_EgiAmtP03Bk_Hdu2gjtlzmY3YhM9WCEYS7n_TzZXtzawYDgQFUXBIBPTQg_56NgOtA1YLjrpQGdU3vAkd0JzyxmqIdUfnoRjQHPSK1PUgG5Wg5rx8T24KyhM9Vphx4C6lZ4GbluIrVe-JVfP1xOk22T9ZaYG_XsaS_b4KV_S1zg6rMZZHLsqyDYdlg-ACFfGXB";
		data[3] = "eNpdj8EOgyAMht_lP5PFKZrAQ-yyo_EgCAmbyjL0ZHx3q27ixqUfX0tpywk9JO6D18-Xa82t7gwYOsjikjDoroYc3qNhCBaynEB3W7eBhFI6csRwwhMrH7X_9lRDlBHNQY9ITQvJ10ij5VR-ZFwDmSV0rjylgbchNQWEUQVaxen9t5l9fO9Pma3JXDHs21kK6e-bVWV_xbRAKXJe5FyIahWOBMECg2xlwQ==";
		data[4] = "eNpdj8EOgyAQRP9lzqSxiibyEb30aDwIYkIL0hQ8Gf-9qzZiy2Ufs8Oy08wYIXCPXj1fxupb5zQYHER1yRiU6yDie9IMYYBoZtB96GwgQUqVOGE44YmlPwb5JMbkTagPeiTqLQRfK61Wkv3omB6iyOhceU4Lb0sqKgiTDBTFqP3jhX310Z8625ClZdjTDVTy3zerVPyZKUFTl7wqeV23q2BIIPgAg8plwQ==";
		data[5] = "eNpdj8EOgyAMht_lP5PFKZrIQ-yyo_EgiAlOdBl4Mr77qm7CxqUfH21pqwUjBO5-Uo-nGfStsRoMFqK4JAzKNhD-NWsG10FUC-jeNYMjIaUKHNBFGLGcgg4ofbAB9Un99_e-HSD4Fmm0nLLPFNNCZAmdK09p4H1IRQFulo5WMerosbKPH6foZW-y1gzHdh2F9LdmU9lfMi1QlTkvcl6W9SYMCYI3glplwQ==";
		data[6] = "eNpdj00OgyAQRu_yrUljFU3kEN10aVwIQkLrT1NwZbx7B23Als083gzDTLNigsDdz-r5soO-daMGwwhRXTIGNXYQ_r1oBmcgmhV0N93gSEipEid0JzyxnJNOKH1s76PUkR6J-gGCh0ijlVQeM7aHKDI6V57TwPuQigLcIh2tYtXxxca-fppPmb3J1jIc2xkK-e-boIq_YlqgqUtelbyu2yAsCYIPgxplwQ==";
		data[7] = "eNpdj8EOgyAMht_lP5PFKZrIQ-yyo_EgiAkb6DLwZHx3q27ixqUfX0tpqwk9BO5hUM-XsfrWOA0GB1FcEgblGojwHjWD7yCqCXTvGutJSKkiR_QnPLEcoo4oQ7Th-5M-3CNSayH4Gmm0nKqPjGkhsoTOlac08DakogA_Sk-rGLX3ndnH98MpszWZa4Z9u45C-vtmVdlfMS1QlTkvcl6W9SoMCYIFgrdlwQ==";
		data[8] = "eNpdj70OgzAMhN_l5qiiEJCSh-jSETGQEKS0_FQNTIh3r6Eops3iL-ezdS4XDNC4T6N9vnznbnXvINBDF5dEwPY19PSenUBoocsF9G_rLpBgjGVmDDZOhJNhZAOjmVhldJEeTE0HLbdK0XKyx45voLOE3lWmFHgPaakgzCbQKf7Is4pDH8ZTZ1-yVgLf61oq6e_MJmV_ZrqgVLkscqlUtQmeBIIPhJFlwQ==";
		data[9] = "eNpdj8sOgyAQRf_lrkljFU3kI7rp0rgQxITWR1N0Zfx3BzWOLZs5nGHInWJGD4XnOJj3x7X2UXUWAh1UdosETFdBjd_JCvgGqphB96ZqPQmtDTOjv2DgfVoPbBn1yJbRnvRiqlsoGSpFS-n52XE1VBLRucuYAm8hDRX4SXtaxR0ZFnH4frh0tk-WUmDfrqES_84Elfw9pg2KPJVZKvO8DMKRIFgBhBdlwQ==";
		data[10] = "eNpdj8sOgyAQRf_lrkljFU3gI7rp0rgQxITWR1N0Zfz3jtYwtmzmcGYgd8oFAzTu02ifL9-5W907CPTQxSURsH0NPb1nJxBa6HIB3du6CySMscw2jrEMJzYja0YzsWV0kR5MTQctt0rRchqPHd9AZwmdq0wp8B7SUkGYTaBV_BFtFYcfxlNn_2StBL7btVTS3zebyv6GaYNS5bLIpVLVJjwJgg-E42XB";
		data[11] = "eNpdj8sOgyAQRf_lrkljFU3gI7rp0rgQhITWR1N0Zfx3B2vFls0czgzkTjmjh8R9HPTz5VpzqzsDhg6yuCQMuqshx_dkGLyFLGfQ3datJ6GU_vYC7daf8MRqiDqiGqONaA56RGpaSB4qRctp_Oi4BjJL6Fx5SoG3kJoK_KQ8reL2lAvbfT-cOtsnS8Xw2c5SSX_fBJX9DdMGpch5kXMhqiAcCYIVhSRlwQ==";
		boolean[] b = new boolean[12];
		for (int i = 0; i < b.length; i++) {
			b[i] = false;
		}
		int last = -1;
		for (int i = 0; i < b.length; i++) {
			b[i] = true;
			System.out.println("Testing index: " + i);
			if (last > -1) {
				b[last] = false;
			}
			last = i;
			final int index = i;
			testIO(false, new SaveLoad() {
				@Override
				public List<Stockpile> saveAndLoad(List<Stockpile> stockpiles) {
					return StockpileReader.load(data[index]);
				}
			}, b);
		}
	}

	private void testIO(SaveLoad saveLoad) {
		testIO(false, saveLoad);
	}

	private void testIO(boolean flagNoSubs, SaveLoad saveLoad) {
		boolean[] b = new boolean[12];
		for (int i = 0; i < b.length; i++) {
			b[i] = false;
		}
		int last = -1;
		for (int i = 0; i < b.length; i++) {
			b[i] = true;
			System.out.println("Testing index: " + i);
			if (last > -1) {
				b[last] = false;
			}
			last = i;
			testIO(flagNoSubs, saveLoad, b);
		}
	}

	private void testIO(boolean flagNoSubs, SaveLoad saveLoad, boolean[] b) {
		//Boolean
		boolean exclude = b[0];
		Boolean singleton = b[1];
		boolean assets = b[2];
		boolean sellOrders = b[3];
		boolean buyOrders = b[4];
		boolean jobs = b[5];
		boolean buyTransactions = b[6];
		boolean sellTransactions = b[7];
		boolean sellingContracts = b[8];
		boolean soldContracts = b[9];
		boolean buyingContracts = b[10];
		boolean boughtContracts = b[11];
		//Int
		Integer jobsDaysLess = 4;
		Integer jobsDaysMore = 5;
		//Location (Jita)
		MyLocation location = ApiIdConverter.getLocation(30000142);
		//Owners
		Long ownerID = 95465499L;
		List<Long> ownerIDs = new ArrayList<>();
		ownerIDs.add(ownerID); //CCP Bartender
		//Flags
		List<StockpileFlag> flags = new ArrayList<>();
		flags.add(new StockpileFlag(2, true)); //Office +subs
		flags.add(new StockpileFlag(3, flagNoSubs)); //Wardrobe !subs
		//Containers
		List<StockpileContainer> containers = new ArrayList<>();
		String containerSubs = "subs";
		String containerNoSubs = "nosubs";
		containers.add(new StockpileContainer(containerSubs, true));
		containers.add(new StockpileContainer(containerNoSubs, false));


		List<StockpileFilter> filters = new ArrayList<>();

		filters.add(new StockpileFilter(location, exclude, flags, containers, ownerIDs, jobsDaysLess, jobsDaysMore, singleton, assets, sellOrders, buyOrders, jobs, buyTransactions, sellTransactions, sellingContracts, soldContracts, buyingContracts, boughtContracts));
		List<Stockpile> stockpiles = new ArrayList<>();
		String stockpileName = "StockpileName";
		double multiplier = 6.0;
		boolean matchAll = true;
		stockpiles.add(new Stockpile(stockpileName, 1L, filters, multiplier, matchAll));

		//Save and Load
		List<Stockpile> rStockpiles = saveLoad.saveAndLoad(stockpiles);

		//Stockpiles
		assertEquals(1, rStockpiles.size());
		//Stockpile
		Stockpile rStockpile = rStockpiles.get(0);
		assertEquals(stockpileName, rStockpile.getName());
		assertEquals(multiplier, rStockpile.getMultiplier(), 0.001);
		assertEquals(matchAll, rStockpile.isMatchAll());
		//Filters
		assertEquals(1, rStockpile.getFilters().size());
		//Filter
		StockpileFilter rFilter = rStockpile.getFilters().get(0);
			//Boolean
		assertEquals(exclude, rFilter.isExclude());
		assertEquals(singleton, rFilter.isSingleton());
		assertEquals(assets, rFilter.isAssets());
		assertEquals(sellOrders, rFilter.isSellOrders());
		assertEquals(buyOrders, rFilter.isBuyOrders());
		assertEquals(jobs, rFilter.isJobs());
		assertEquals(buyTransactions, rFilter.isBuyTransactions());
		assertEquals(sellTransactions, rFilter.isSellTransactions());
		assertEquals(sellingContracts, rFilter.isSellingContracts());
		assertEquals(soldContracts, rFilter.isSoldContracts());
		assertEquals(buyingContracts, rFilter.isBuyingContracts());
		assertEquals(boughtContracts, rFilter.isBoughtContracts());
			//Int
		assertEquals(jobsDaysLess, rFilter.getJobsDaysLess());
		assertEquals(jobsDaysMore, rFilter.getJobsDaysMore());
			//Location (Jita)
		assertEquals(location, rFilter.getLocation());
			//Owners
		assertEquals(1, rFilter.getOwnerIDs().size());
		assertEquals(ownerID, rFilter.getOwnerIDs().get(0));
			//Flags
		assertEquals(2, rFilter.getFlags().size());
		for (StockpileFlag flag : rFilter.getFlags()) {
			if (flag.getFlagID() == 2) {
				assertEquals(true, flag.isIncludeSubs());
			} else {
				assertEquals(flagNoSubs, flag.isIncludeSubs());
			}
		}
			//Containers
		assertEquals(2, rFilter.getContainers().size());
		for (StockpileContainer rContainer : rFilter.getContainers()) {
			if (rContainer.getContainer().equals(containerSubs)) {
				assertEquals(true, rContainer.isIncludeSubs());
			} else {
				assertEquals(false, rContainer.isIncludeSubs());
			}
		}
	}

	private static interface SaveLoad {
		public List<Stockpile> saveAndLoad(List<Stockpile> stockpiles);
	}
}
