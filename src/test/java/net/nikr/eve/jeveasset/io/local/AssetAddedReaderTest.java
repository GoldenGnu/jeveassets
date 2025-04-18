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
import java.net.URL;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.data.settings.AddedData;
import org.junit.Assert;
import org.junit.Test;


public class AssetAddedReaderTest extends TestUtil {

	@Test
	public void testAddedJson() {
		TestAssetAddedReader.load();
		Assert.assertTrue(!AddedData.getAssets().isEmpty());
	}

	private static class TestAssetAddedReader extends AssetAddedReader {
		public static void load() {
			try {
				TestAssetAddedReader assetAddedReader = new TestAssetAddedReader();
				URL resource = BackwardCompatibilitySettings.class.getResource("/added.json");
				String filename = new File(resource.toURI()).getAbsolutePath();
				assetAddedReader.read(filename);
			} catch (URISyntaxException ex) {
				Assert.fail(ex.getMessage());
			}
		}
	}
}
