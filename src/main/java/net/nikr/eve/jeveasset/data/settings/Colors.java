/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
import javax.swing.JPanel;
import javax.swing.JTable;

public enum Colors {
	/**
	 * 230, 230, 230
	 */
	LIGHT_GRAY(230, 230, 230),
	/**
	 * 190, 190, 190
	 */
	STRONG_GRAY(190, 190, 190),
	/**
	 * 190, 190, 190 Replace GRAY
	 */
	COLORBLIND_GRAY(190, 190, 190),
	/**
	 * 255, 200, 200
	 */
	LIGHT_RED(255, 200, 200),
	/**
	 * 255, 160, 160
	 */
	STRONG_RED(255, 160, 160),
	/**
	 * Dark Red
	 */
	DARK_RED(Color.RED.darker()),
	/**
	 * 213, 94, 0 Replace DARK_RED
	 */
	COLORBLIND_DARK_RED(213, 94, 0),
	/**
	 * 200, 255, 200
	 */
	LIGHT_GREEN(200, 255, 200),
	/**
	 * 160, 255, 160
	 */
	STRONG_GREEN(160, 255, 160),
	/**
	 * 0, 158, 115 Used by FILTER_OR_GROUP_3
	 */
	COLORBLIND_GREEN(0, 158, 115),
	/**
	 * Dark Green
	 */
	DARK_GREEN(Color.GREEN.darker()),
	/**
	 * 0, 114, 178 Pleace DARK_GREEN (not really green, it's blue)
	 */
	COLORBLIND_DARK_GREEN(0, 114, 178),
	/**
	 * 255, 255, 200
	 */
	LIGHT_YELLOW(255, 255, 200),
	/**
	 * 255, 255, 160
	 */
	STRONG_YELLOW(255, 255, 160),
	/**
	 * 255, 176, 0 Replace GREEN
	 */
	COLORBLIND_YELLOW(240, 228, 66),
	/**
	 * 220, 240, 255
	 */
	LIGHT_BLUE(220, 240, 255),
	/**
	 * 180, 220, 255
	 */
	STRONG_BLUE(180, 220, 255),
	/**
	 * 100, 143, 255 Replace BLUE
	 */
	COLORBLIND_BLUE(86, 180, 233),
	/**
	 * 255, 220, 200
	 */
	LIGHT_ORANGE(255, 220, 200),
	/**
	 * 255, 180, 120
	 */
	STRONG_ORANGE(255, 180, 120),
	/**
	 * 254, 97, 0 Replace YELLOW
	 */
	COLORBLIND_ORANGE(230, 159, 0),
	/**
	 * 255, 220, 255
	 */
	LIGHT_MAGENTA(255, 220, 255),
	/**
	 * 255, 180, 255
	 */
	STRONG_MAGENTA(255, 180, 255),
	/**
	 * 220, 38, 127 Replace RED
	 */
	COLORBLIND_MAGENTA(204, 121, 167),
	/**
	 * Table selection background
	 */
	TABLE_SELECTION_BACKGROUND(new JTable().getSelectionBackground()),
	/**
	 * Table selection foreground
	 */
	TABLE_SELECTION_FOREGROUND(new JTable().getSelectionForeground()),
	/**
	 * Component foreground
	 */
	COMPONENT_FOREGROUND(new JPanel().getForeground()),
	;

	private final Color color;

	private Colors(Color color) {
		this.color = color;
	}

	private Colors(int r, int g, int b) {
		this.color = new Color(r, g, b);
	}

	public Color getColor() {
		return color;
	}

}
