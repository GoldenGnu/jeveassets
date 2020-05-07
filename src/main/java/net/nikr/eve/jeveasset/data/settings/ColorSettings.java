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

import com.privatejgoodies.common.base.Objects;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import net.nikr.eve.jeveasset.gui.shared.ColorTools;

public class ColorSettings {

	private final Map<ColorEntry, Color> backgrounds = new EnumMap<>(ColorEntry.class);
	private final Map<ColorEntry, Color> foregrounds = new EnumMap<>(ColorEntry.class);

	public ColorSettings() {
		for (ColorEntry colorEntry : ColorEntry.values()) {
			backgrounds.put(colorEntry, colorEntry.getBackground());
			foregrounds.put(colorEntry, colorEntry.getForeground());
		}
	}

	private Map<ColorEntry, Color> getBackgrounds() {
		return backgrounds;
	}

	private Map<ColorEntry, Color> getForegrounds() {
		return foregrounds;
	}

	public Color getBackground(ColorEntry colorEntry) {
		return getBackgrounds().get(colorEntry);
	}

	public Color getForeground(ColorEntry colorEntry) {
		return getForegrounds().get(colorEntry);
	}

	public boolean setForeground(ColorEntry colorEntry, Color foreground) {
		return setMap(colorEntry, foreground, getForegrounds());
	}

	public boolean setBackground(ColorEntry colorEntry, Color background) {
		return setMap(colorEntry, background, getBackgrounds());
	}

	private boolean setMap(ColorEntry colorEntry, Color color, Map<ColorEntry, Color> map) {
		if (color == null) { //None
			Color removed = map.remove(colorEntry);
			return removed != null; //Removed
		} else { //Color
			Color old = map.put(colorEntry, color);
			if (old != null) {
				return !old.equals(color); //Changed
			} else {
				return true; //New
			}
		}
	}

	public List<ColorRow> get() {
		List<ColorRow> rows = new ArrayList<>();
		for (ColorEntry colorEntry : ColorEntry.values()) {
			if (!colorEntry.isBackgroundEditable() && !colorEntry.isForegroundEditable()) {
				continue;
			}
			rows.add(new ColorRow(colorEntry, getBackground(colorEntry), getForeground(colorEntry)));
		}
		return rows;
	}

	public boolean set(ColorRow colorRow) {
		boolean changed = setForeground(colorRow.getColorEntry(), colorRow.getForeground());
		return setBackground(colorRow.getColorEntry(), colorRow.getBackground()) || changed;
	}

	public static Color background(ColorEntry colorEntry) {
		return Settings.get().getColorSettings().getBackground(colorEntry);
	}

	private static Color foreground(ColorEntry colorEntry) {
		return Settings.get().getColorSettings().getForeground(colorEntry);
	}

	public static void config(Component component, ColorEntry colorEntry) {
		Color foreground = foreground(colorEntry);
		Color background = background(colorEntry);
		if (background != null) {
			component.setBackground(background);
		} else {
		}
		if (foreground != null) {
			component.setForeground(foreground);
		} else {
			component.setForeground(Colors.COMPONENT_FOREGROUND.getColor());
		}
	}
	public static void config(JComponent component, ColorEntry colorEntry) {
		Color foreground = foreground(colorEntry);
		Color background = background(colorEntry);
		if (background != null) {
			component.setOpaque(true);
			component.setBackground(background);
		} else {
			component.setOpaque(false);
		}
		if (foreground != null) {
			component.setForeground(foreground);
		} else {
			component.setForeground(Colors.COMPONENT_FOREGROUND.getColor());
		}
	}

	public static void configCell(Component component, ColorEntry colorEntry, boolean selected) {
		configCell(component, foreground(colorEntry), background(colorEntry), selected, false);
	}

	public static void configCell(Component component, ColorEntry colorEntry, boolean selected, boolean darker) {
		configCell(component, foreground(colorEntry), background(colorEntry), selected, darker);
	}

	public static void configCell(Component component, Color foreground, Color background, boolean selected) {
		configCell(component, foreground, background, selected, false);
	}
	
	public static void configCell(Component component, Color foreground, Color background, boolean selected, boolean darker) {
		if (selected) {
			if (darker) {
				if (background != null) {
					component.setBackground(background.darker());
				}
				if (foreground != null) {
					double luminance = ColorTools.luminance(foreground) - 0.2;
					component.setForeground(ColorTools.brighter(foreground, luminance));
				} else {
					component.setForeground(Color.BLACK);
				}
			} else {
				if (background != null) {
					component.setBackground(Colors.TABLE_SELECTION_BACKGROUND.getColor().darker());
				} else {
					component.setBackground(Colors.TABLE_SELECTION_BACKGROUND.getColor());
				}
				if (foreground != null) {
					double luminance = ColorTools.luminance(foreground) - 0.2;
					component.setForeground(ColorTools.brighter(foreground, luminance));
				} else {
					component.setForeground(Colors.TABLE_SELECTION_FOREGROUND.getColor());
				}
			}
		} else {
			if (background != null) {
				component.setBackground(background);
			}
			if (foreground != null) {
				component.setForeground(foreground);
			}
		}
	}

	public static class ColorRow {

		private final ColorEntry colorEntry;
		private Color background;
		private Color foreground;

		public ColorRow(ColorEntry colorEntry, Color background, Color foreground) {
			this.colorEntry = colorEntry;
			this.background = background;
			this.foreground = foreground;
		}


		public ColorEntry getColorEntry() {
			return colorEntry;
		}

		public Color getBackground() {
			return background;
		}

		public void setBackground(Color background) {
			this.background = background;
		}

		public Color getForeground() {
			return foreground;
		}

		public void setForeground(Color foreground) {
			this.foreground = foreground;
		}

		public boolean isForegroundDefault() {
			return Objects.equals(foreground, colorEntry.getForeground());
		}

		public boolean isBackgroundDefault() {
			return Objects.equals(background, colorEntry.getBackground());
		}

		public static boolean equals(Object o1, Object o2) {
			return o1 == o2 || o1 != null && o1.equals(o2) || o2 != null && o2.equals(o1) || o1 == null && o2 == null;
		}

	}

}
