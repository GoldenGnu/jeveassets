/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.io;

import java.net.HttpURLConnection;
import java.net.URL;
import net.nikr.eve.jeveasset.data.Settings;


public class Online {

	public static boolean isOnline(Settings settings){
		try {
			URL ourURL = new URL("http://www.google.com"); //Coding Forums RSS Feed
			HttpURLConnection huc = (HttpURLConnection)ourURL.openConnection(settings.getProxy());
			huc.setRequestMethod("GET");
			huc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; JVM)");
			huc.setRequestProperty("Pragma", "no-cache");
			huc.connect();
			huc.disconnect();
		} catch(Exception e) {
			return false;
		}
		return true;
	}
}
