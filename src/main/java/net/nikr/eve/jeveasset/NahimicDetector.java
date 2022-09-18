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
package net.nikr.eve.jeveasset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class NahimicDetector {

	private NahimicDetector() { }

	public static boolean isNahimicRunning() {
		try {
			StringBuilder builder = new StringBuilder();
			String line;

			//Process p = Runtime.getRuntime().exec(System.getenv("windir") + File.separator + "system32" + File.separator + "tasklist.exe");
			Process p = Runtime.getRuntime().exec("tasklist");

			try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
				while ((line = input.readLine()) != null) {
					builder.append(line.toLowerCase());
				}
			}
			String string = builder.toString();
			return string.contains("nahimicservice") //exact
					|| string.contains("nahimicsvc64") //exact
					|| string.contains("nahimicsvc32") //exact
					/*
					|| string.contains("nahimicsvc64run") //Other?
					|| string.contains("nahimicsvc32run") //Other?
					|| string.contains("nahimctask32") //Other?
					|| string.contains("nahimic2uilauncher")  //Other?
					|| string.contains("nahimic") //Match all
					*/
					;
		} catch (IOException ex) {
			return false;
		}
	}
}
