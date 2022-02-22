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
package net.nikr.eve.jeveasset.data.settings;

import java.awt.Color;
import javax.swing.UIDefaults;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;


public class DarkNimbus extends NimbusLookAndFeel {

	private UIDefaults defaults;

	@Override
	public UIDefaults getDefaults() {
		if (defaults == null) {
			defaults = super.getDefaults();
			defaults.put("control", new Color(128, 128, 128));
			defaults.put("info", new Color(128, 128, 128));
			defaults.put("nimbusBase", new Color(18, 30, 49));
			defaults.put("nimbusAlertYellow", new Color(248, 187, 0));
			defaults.put("nimbusDisabledText", new Color(128, 128, 128));
			defaults.put("nimbusFocus", new Color(115, 164, 209));
			defaults.put("nimbusGreen", new Color(176, 179, 50));
			defaults.put("nimbusInfoBlue", new Color(66, 139, 221));
			defaults.put("nimbusLightBackground", new Color(18, 30, 49));
			defaults.put("nimbusOrange", new Color(191, 98, 4));
			defaults.put("nimbusRed", new Color(169, 46, 34));
			defaults.put("nimbusSelectedText", new Color(255, 255, 255));
			defaults.put("nimbusSelectionBackground", new Color(104, 93, 156));
			defaults.put("text", new Color(230, 230, 230));
		}
		return defaults;
	}
}
