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

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.nikr.eve.jeveasset.data.settings.ColorSettings.ColorRow;


public abstract class ColorTheme {

	public static enum ColorThemeTypes {
		DEFAULT(new ColorThemeDefault()),
		STRONG(new ColorThemeStrong()),
		COLORBLIND(new ColorThemeColorblind()),
		DARK(new ColorThemeDark()),
		;

		private final ColorTheme colorTheme;

		private ColorThemeTypes(ColorTheme colorTheme) {
			this.colorTheme = colorTheme;
		}

		public ColorTheme getInstance() {
			return colorTheme;
		}

		@Override
		public String toString() {
			return colorTheme.getName();
		}
	}

	private Map<ColorEntry, ColorThemeEntry> colors = null;

	protected ColorTheme() { }

	public Map<ColorEntry, ColorThemeEntry> getColors() {
		if (colors == null) {
			colors = new HashMap<>();
			createColors(colors);
		}
		return colors;
	}

	public boolean isValid() {
		for (ColorEntry entry : ColorEntry.values()) {
			ColorThemeEntry colorThemeEntry = getColors().get(entry);
			if (colorThemeEntry == null) {
				return false;
			}
		}
		return true;
	}

	public List<ColorRow> get(boolean overwrite, List<ColorRow> old) {
		Map<ColorEntry, ColorRow> loopup = new HashMap<>();
		for (ColorRow colorRow : old) {
			loopup.put(colorRow.getColorEntry(), colorRow);
		}
		List<ColorRow> rows = new ArrayList<>();
		for (ColorEntry colorEntry : ColorEntry.values()) {
			if (!colorEntry.isBackgroundEditable() && !colorEntry.isForegroundEditable()) {
				continue;
			}
			ColorRow current = loopup.get(colorEntry);
			Color defaultBackground = getDefaultBackground(colorEntry);
			Color defaultForeground = getDefaultForeground(colorEntry);
			Color currentBackground;
			Color currentForeground;
			if (overwrite || current.isBackgroundDefault()) {
				currentBackground = defaultBackground;
			} else {
				currentBackground = current.getBackground();
			}
			if (overwrite || current.isForegroundDefault()) {
				currentForeground = defaultForeground;
			} else {
				currentForeground = current.getForeground();
			}
			rows.add(new ColorRow(colorEntry, defaultBackground, defaultForeground, currentBackground, currentForeground));
		}
		return rows;
	}

	public Color getDefaultBackground(ColorEntry colorEntry) {
		ColorThemeEntry entry = getColors().get(colorEntry);
		if (entry == null) {
			return null;
		} else {
			return entry.getBackground();
		}
	}

	public Color getDefaultForeground(ColorEntry colorEntry) {
		ColorThemeEntry entry = getColors().get(colorEntry);
		if (entry == null) {
			return null;
		} else {
			return entry.getForeground();
		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 43 * hash + Objects.hashCode(this.colors);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ColorTheme other = (ColorTheme) obj;
		if (!Objects.equals(this.colors, other.colors)) {
			return false;
		}
		return true;
	}

	public boolean isDefault() {
		return getType() != null;
	}

	public abstract String getName();
	public abstract ColorThemeTypes getType();
	protected abstract void createColors(Map<ColorEntry, ColorThemeEntry> colors);

	public class ColorThemeEntry {
		private final Color background;
		private final Color foreground;

		public ColorThemeEntry(Colors background) {
			this.background = toColor(background);
			this.foreground = null;
		}

		public ColorThemeEntry(Colors background, Colors foreground) {
			this.background = toColor(background);
			this.foreground = toColor(foreground);
		}

		public Color getBackground() {
			return background;
		}

		public Color getForeground() {
			return foreground;
		}

		private Color toColor(Colors colors) {
			if (colors != null) {
				return colors.getColor();
			} else {
				return null;
			}
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 17 * hash + Objects.hashCode(this.background);
			hash = 17 * hash + Objects.hashCode(this.foreground);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final ColorThemeEntry other = (ColorThemeEntry) obj;
			if (!Objects.equals(this.background, other.background)) {
				return false;
			}
			if (!Objects.equals(this.foreground, other.foreground)) {
				return false;
			}
			return true;
		}
	}
}
