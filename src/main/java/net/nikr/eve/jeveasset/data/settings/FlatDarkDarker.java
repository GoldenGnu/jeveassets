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
package net.nikr.eve.jeveasset.data.settings;

import com.formdev.flatlaf.FlatDarkLaf;
import java.util.Properties;


public class FlatDarkDarker extends FlatDarkLaf {

	@Override
	public String getName() {
		return "Flat Dark Darker";
	}

	@Override
	protected Properties getAdditionalDefaults() {
		Properties properties = new Properties();
		Properties defaults = super.getAdditionalDefaults();
		if (defaults != null) {
			properties.putAll(defaults);
		}

		// Keep Flat Dark behavior, but shift the base palette darker.
		properties.put("@background", "#1A1A1A");
		properties.put("@foreground", "#D0D0D0");
		properties.put("@disabledForeground", "shade(@foreground,30%)");
		properties.put("@buttonBackground", "tint(@background,7%)");
		properties.put("@componentBackground", "tint(@background,4%)");
		properties.put("@menuBackground", "darken(@background,4%)");
		properties.put("@selectionBackground", "#304867");
		properties.put("@accentBaseColor", "#3D5F97");

		// Improve panel contrast in desktop areas when using very dark backgrounds.
		properties.put("Desktop.background", "#161616");
		properties.put("Component.borderColor", "tint(@background,16%)");
		return properties;
	}
}
