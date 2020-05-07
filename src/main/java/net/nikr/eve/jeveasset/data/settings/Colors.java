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
	LIGHT_GRAY_STRONG(190, 190, 190),
	/**
	 * 255, 200, 200
	 */
	LIGHT_RED(255, 200, 200),
	/**
	 * 255, 160, 160
	 */
	LIGHT_RED_STRONG(255, 160, 160),
	/**
	 * Dark Red
	 */
	DARK_RED(Color.RED.darker()),
	/**
	 * 200, 255, 200
	 */
	LIGHT_GREEN(200, 255, 200),
	/**
	 * 160, 255, 160
	 */
	LIGHT_GREEN_STRONG(160, 255, 160),
	/**
	 * 120, 255, 120
	 */
	GREEN_STRONG(120, 255, 120),
	/**
	 * Dark Green
	 */
	DARK_GREEN(Color.GREEN.darker()),
	/**
	 * 255, 255, 200
	 */
	LIGHT_YELLOW(255, 255, 200),
	/**
	 * 255, 255, 160
	 */
	LIGHT_YELLOW_STRONG(255, 255, 160),
	/**
	 * 255, 255, 120
	 */
	YELLOW_STRONG(255, 255, 120),
	/**
	 * 220, 240, 255
	 */
	LIGHT_BLUE(220, 240, 255),
	/**
	 * 180, 220, 255
	 */
	LIGHT_BLUE_STRONG(180, 220, 255),
	/**
	 * 255, 220, 200
	 */
	LIGHT_ORANGE(255, 220, 200),
	/**
	 * 255, 180, 120
	 */
	LIGHT_ORANGE_STRONG(255, 180, 120),
	/**
	 * 255, 220, 255
	 */
	LIGHT_MAGENTA(255, 220, 255),
	/**
	 * 255, 180, 255
	 */
	LIGHT_MAGENTA_STRONG(255, 180, 255),
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
