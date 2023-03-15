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
package net.nikr.eve.jeveasset.data.settings;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;

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
	 * 190, 190, 190
	 */
	COLORBLIND_GRAY(190, 190, 190),
	/**
	 * 81, 81, 81
	 */
	DARK_GRAY(81, 81, 81),
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
	FOREGROUND_RED(Color.RED.darker()),
	/**
	 * Dark Red
	 */
	DARK_FOREGROUND_RED(239, 143, 143),
	/**
	 * 120, 45, 30
	 */
	DARK_RED(120, 45, 30),
	/**
	 * 213, 94, 0
	 */
	COLORBLIND_FOREGROUND_RED(213, 94, 0),
	/**
	 * 200, 255, 200
	 */
	LIGHT_GREEN(200, 255, 200),
	/**
	 * 160, 255, 160
	 */
	STRONG_GREEN(160, 255, 160),
	/**
	 * 0, 158, 115
	 */
	COLORBLIND_GREEN(0, 158, 115),
	/**
	 * Dark Green
	 */
	FOREGROUND_GREEN(Color.GREEN.darker()),
	/**
	 * 22, 153, 0
	 */
	DARK_FOREGROUND_GREEN(152, 229, 152),
	/**
	 * 45, 67, 34
	 */
	DARK_GREEN(45, 67, 34),
	/**
	 * 0, 114, 178 (not really green, it's blue)
	 */
	COLORBLIND_FOREGROUND_GREEN(0, 114, 178),
	/**
	 * 255, 255, 200
	 */
	LIGHT_YELLOW(255, 255, 200),
	/**
	 * 255, 255, 160
	 */
	STRONG_YELLOW(255, 255, 160),
	/**
	 * 255, 176, 0
	 */
	COLORBLIND_YELLOW(240, 228, 66),
	/**
	 * 132, 103, 14
	 */
	DARK_YELLOW(132, 103, 14),
	/**
	 * 220, 240, 255
	 */
	LIGHT_BLUE(220, 240, 255),
	/**
	 * 180, 220, 255
	 */
	STRONG_BLUE(180, 220, 255),
	/**
	 * 86, 106, 143
	 */
	DARK_BLUE(86, 106, 143),
	/**
	 * 100, 143, 255
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
	 * 254, 97, 0
	 */
	COLORBLIND_ORANGE(230, 159, 0),
	/**
	 * 114, 77, 38
	 */
	DARK_ORANGE(114, 77, 38),
	/**
	 * 255, 220, 255
	 */
	LIGHT_MAGENTA(255, 220, 255),
	/**
	 * 255, 180, 255
	 */
	STRONG_MAGENTA(255, 180, 255),
	/**
	 * 220, 38, 127
	 */
	COLORBLIND_MAGENTA(204, 121, 167),
	/**
	 * 77, 50, 64
	 */
	DARK_MAGENTA(77, 50, 64),
	/**
	 * Table selection background
	 */
	TABLE_SELECTION_BACKGROUND("Table.selectionBackground") {
		@Override
		public Color getComponentColor() {
			return new JTable().getSelectionBackground();
		}
	},
	/**
	 * Table selection foreground
	 */
	TABLE_SELECTION_FOREGROUND("Table.selectionForeground") {
		@Override
		public Color getComponentColor() {
			return new JTable().getSelectionForeground();
		}
	},
	/**
	 * TextField background
	 */
	TEXTFIELD_BACKGROUND("TextField.background") {
		@Override
		public Color getComponentColor() {
			return new JTextField().getBackground();
		}
	},
	/**
	 * TextField foreground
	 */
	TEXTFIELD_FOREGROUND("TextField.foreground") {
		@Override
		public Color getComponentColor() {
			return new JTextField().getForeground();
		}
	},
	/**
	 * Component background
	 */
	COMPONENT_BACKGROUND("Panel.background") {
		@Override
		public Color getComponentColor() {
			return new JPanel().getBackground();
		}
	},
	/**
	 * Transparent component background
	 */
	COMPONENT_TRANSPARENT("Panel.background", 0) {
		@Override
		public Color getComponentColor() {
			return new JPanel().getBackground();
		}
	},
	/**
	 * Component foreground
	 */
	COMPONENT_FOREGROUND("Panel.foreground") {
		@Override
		public Color getComponentColor() {
			return new JPanel().getForeground();
		}
	},
	/**
	 * Component background
	 */
	BUTTON_BACKGROUND("Button.background") {
		@Override
		public Color getComponentColor() {
			return new JButton().getBackground();
		}
	},
	/**
	 * Component foreground
	 */
	BUTTON_FOREGROUND("Button.foreground") {
		@Override
		public Color getComponentColor() {
			return new JButton().getForeground();
		}
	},
	;

	private final Color color;
	private final String key;
	private final Integer alpha;

	private Colors(String key) {
		this(key, null);
	}

	private Colors(String key, Integer alpha) {
		this.color = null;
		this.key = key;
		this.alpha = alpha;
	}

	private Colors(Color color) {
		this.color = color;
		this.key = null;
		this.alpha = null;
	}

	private Colors(int r, int g, int b) {
		this.color = new Color(r, g, b);
		this.key = null;
		this.alpha = null;
	}

	public Color getColor() {
		if (alpha != null) {
			Color c = getColorWithoutAlpha();
			return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
		} else {
			return getColorWithoutAlpha();
		}
	}

	private Color getColorWithoutAlpha() {
		if (color != null) {
			return color;
		}
		if (key != null) {
			Color ui = UIManager.getColor(key);
			if (ui != null) {
				return ui;
			}
		}
		Color component = getComponentColor();
		if (component != null) {
			return component;
		} else {
			throw new RuntimeException(name() + " have no valid color");
		}
	}

	public Color getComponentColor() {
		return null;
	}

}
