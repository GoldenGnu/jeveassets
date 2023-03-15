/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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


public class ColorUtil {

	private ColorUtil() { }

	public static double luminance(Color color) {
		return (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
	}

	public static boolean isBrightColor(Color color) {
		return luminance(color) > 0.5;
	}

	public static Color brighter(Color c, double factor) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();

		if (factor > 1) {
			factor = 1;
		} else if (factor < 0) {
			factor = 0;
		}
		/* From 2D group:
	 * 1. black.brighter() should return grey
	 * 2. applying brighter to blue will always return blue, brighter
	 * 3. non pure color (non zero rgb) will eventually return white
		 */
		int i = (int) (1.0 / (1.0 - factor));
		if (r == 0 && g == 0 && b == 0) {
			return new Color(i, i, i);
		}
		if (r > 0 && r < i) {
			r = i;
		}
		if (g > 0 && g < i) {
			g = i;
		}
		if (b > 0 && b < i) {
			b = i;
		}

		return new Color(
				Math.min((int) (r / factor), 255),
				Math.min((int) (g / factor), 255),
				Math.min((int) (b / factor), 255));
	}
}
