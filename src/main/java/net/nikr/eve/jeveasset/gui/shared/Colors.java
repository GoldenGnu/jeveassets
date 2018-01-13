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
package net.nikr.eve.jeveasset.gui.shared;

import java.awt.Color;
import net.nikr.eve.jeveasset.data.settings.Settings;


public enum Colors {
	/**
	 * 230, 230, 230
	 */
	LIGHT_GRAY(230, 230, 230, 190, 190, 190),
	/**
	 * 255, 200, 200
	 */
	LIGHT_RED(255, 200, 200, 255, 160, 160),
	/**
	 * 255, 160, 160
	 */
	RED(255, 160, 160, 255, 120, 120),
	/**
	 * 200, 255, 200
	 */
	LIGHT_GREEN(200, 255, 200, 160, 255, 160),
	/**
	 * 160, 255, 160
	 */
	GREEN(160, 255, 160, 120, 255, 120),
	/**
	 * 255, 255, 200
	 */
	LIGHT_YELLOW(255, 255, 200, 255, 255, 160),
	/**
	 * 255, 255, 160
	 */
	YELLOW(255, 255, 160, 255, 255, 120),
	/**
	 * 220, 240, 255
	 */
	LIGHT_BLUE(220, 240, 255, 180, 220, 255),
	;

	private final Color color;
	private final Color colorStrong;

	private Colors(int r, int g, int b) {
		this(r, g, b, r, g, b);
	}
	

	private Colors(int r, int g, int b, int r_strong, int g_strong, int b_strong) {
		color = new Color(r, g, b);
		colorStrong = new Color(r_strong, g_strong, b_strong);
	}

	public Color getColor() {
		if (Settings.get().isStrongColors()) {
			return colorStrong;
		} else {
			return color;
		}
	}
}
