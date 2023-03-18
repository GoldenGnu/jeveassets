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
import java.awt.Component;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import net.nikr.eve.jeveasset.data.settings.ColorTheme.ColorThemeTypes;
import net.nikr.eve.jeveasset.gui.shared.ColorUtil;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;

public class ColorSettings {

	public static enum PredefinedLookAndFeel {
		DEFAULT(null, UIManager.getSystemLookAndFeelClassName(), true),
		FLAT_LIGHT(DialoguesSettings.get().lookAndFeelFlatLight(), "com.formdev.flatlaf.FlatLightLaf"),
		FLAT_INTELLIJ(DialoguesSettings.get().lookAndFeelFlatIntelliJ(), "com.formdev.flatlaf.FlatIntelliJLaf"),
		FLAT_DARK(DialoguesSettings.get().lookAndFeelFlatDark(), "com.formdev.flatlaf.FlatDarkLaf"),
		FLAT_DARCULA(DialoguesSettings.get().lookAndFeelFlatDarcula(), "com.formdev.flatlaf.FlatDarculaLaf"),
		DARK_NIMBUS(DialoguesSettings.get().lookAndFeelNimbusDark(), DarkNimbus.class.getName()),
		;

		private final String name;
		private final String className;
		private final boolean selected;

		private PredefinedLookAndFeel(String name, String className) {
			this(name, className, false);
		}

		private PredefinedLookAndFeel(String name, String className, boolean selected) {
			if (name == null) {
				for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
					if (laf.getClassName().equals(UIManager.getSystemLookAndFeelClassName())) {
						name = DialoguesSettings.get().lookAndFeelDefaultName(laf.getName());
						break;
					}
				}
				if (name == null) {
					name = DialoguesSettings.get().lookAndFeelDefault();
				}
			}
			this.name = name;
			this.className = className;
			this.selected = selected;
		}

		public boolean isSelected() {
			return selected;
		}

		public LookAndFeelInfo getLookAndFeelInfo() {
			return new LookAndFeelInfo(name, className);
		}
	}

	private String lookAndFeelClass = UIManager.getSystemLookAndFeelClassName();
	private ColorTheme colorTheme = ColorThemeTypes.DEFAULT.getInstance();
	private final Map<ColorEntry, Color> backgrounds = new EnumMap<>(ColorEntry.class);
	private final Map<ColorEntry, Color> foregrounds = new EnumMap<>(ColorEntry.class);
	private static final Map<JTextField, BorderWrap> BORDERS = new HashMap<>();

	public ColorSettings() {
		for (ColorEntry colorEntry : ColorEntry.values()) {
			backgrounds.put(colorEntry, colorTheme.getDefaultBackground(colorEntry));
			foregrounds.put(colorEntry, colorTheme.getDefaultForeground(colorEntry));
		}
	}

	public String getLookAndFeelClass() {
		return lookAndFeelClass;
	}

	public boolean isFlatLAF() {
		return lookAndFeelClass.startsWith("com.formdev.flatlaf");
	}

	public void setLookAndFeelClass(String lookAndFeelClass) {
		this.lookAndFeelClass = lookAndFeelClass;
	}

	public ColorTheme getColorTheme() {
		return colorTheme;
	}

	public void setColorTheme(ColorTheme colorTheme, boolean overwrite) {
		//Setting new colors
		for (ColorEntry colorEntry : ColorEntry.values()) {
			boolean defaultBackground = Objects.equals(getBackgrounds().get(colorEntry), getColorTheme().getDefaultBackground(colorEntry));
			if (defaultBackground || overwrite) {
				getBackgrounds().put(colorEntry, colorTheme.getDefaultBackground(colorEntry));
			}
			boolean defaultForeground = Objects.equals(getForegrounds().get(colorEntry), getColorTheme().getDefaultForeground(colorEntry));
			if (defaultForeground || overwrite) {
				getForegrounds().put(colorEntry, colorTheme.getDefaultForeground(colorEntry));
			}
		}
		//Setting ColorTheme (must be done last)
		this.colorTheme = colorTheme;
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
			rows.add(new ColorRow(colorEntry, colorTheme.getDefaultBackground(colorEntry), colorTheme.getDefaultForeground(colorEntry), getBackground(colorEntry), getForeground(colorEntry)));
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
		if (component == null) {
			return;
		}
		if (background != null) {
			/**
			 * Nimbus JTextField opaque workaround.
			 * Making the JTextField opaque on Nimbus makes it look very ugly
			 */
			if ("Nimbus".equalsIgnoreCase(UIManager.getLookAndFeel().getID()) && component instanceof JTextField) {
				component.setOpaque(false);
			} else {
				component.setOpaque(true);
			}
			component.setBackground(background);
			/**
			 * GTK JTextField background workaround.
			 * Use Borders instead of background color on GTK, as setting background color have no effect
			 * Save the original border and set new border with the background color
			 */
			if ("GTK".equalsIgnoreCase(UIManager.getLookAndFeel().getID()) && component instanceof JTextField) {
				JTextField jTextField = (JTextField) component;
				BorderWrap borderWrap = BORDERS.get(jTextField);
				if (borderWrap == null) { //Original border
					//Save original border
					borderWrap = new BorderWrap(jTextField.getBorder());
					BORDERS.put(jTextField, borderWrap);
				}
				//Set background color border
				jTextField.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(4, 4, 4, 4, background),
						BorderFactory.createEmptyBorder(0, 7, 0, 7)));
			}
		} else {
			component.setOpaque(false);
		}
		if (foreground != null) {
			component.setForeground(foreground);
		} else {
			component.setForeground(Colors.COMPONENT_FOREGROUND.getColor());
		}
	}

	public static void configReset(JTextField jTextField) {
		jTextField.setBackground(Colors.TEXTFIELD_BACKGROUND.getColor());
		jTextField.setForeground(Colors.TEXTFIELD_FOREGROUND.getColor());
		/**
		 * GTK JTextField background workaround.
		 * Use Borders instead of background color on GTK, as setting background color have no effect
		 * Load the original border
		 */
		if ("GTK".equals(UIManager.getLookAndFeel().getID())) {
			BorderWrap borderWrap = BORDERS.get(jTextField);
			if (borderWrap != null) {
				//Restore original border (if saved)
				jTextField.setBorder(borderWrap.getBorder());
			}
		}
	}

	public static void configReset(JButton jButton) {
		jButton.setBackground(Colors.BUTTON_BACKGROUND.getColor());
		jButton.setForeground(Colors.BUTTON_FOREGROUND.getColor());
		if ("Nimbus".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) {
			jButton.setOpaque(false);
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
					double luminance = ColorUtil.luminance(foreground) - 0.2;
					component.setForeground(ColorUtil.brighter(foreground, luminance));
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
					double luminance = ColorUtil.luminance(foreground) - 0.2;
					component.setForeground(ColorUtil.brighter(foreground, luminance));
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
		private final Color defaultBackground;
		private final Color defaultForeground;
		private Color background;
		private Color foreground;

		public ColorRow(ColorEntry colorEntry, Color defaultBackground, Color defaultForeground, Color background, Color foreground) {
			this.colorEntry = colorEntry;
			this.background = background;
			this.foreground = foreground;
			this.defaultBackground = defaultBackground;
			this.defaultForeground = defaultForeground;
		}

		public ColorEntry getColorEntry() {
			return colorEntry;
		}

		public Color getBackground() {
			return background;
		}

		public Color getDefaultBackground() {
			return defaultBackground;
		}

		public void setBackground(Color background) {
			this.background = background;
		}

		public Color getDefaultForeground() {
			return defaultForeground;
		}

		public Color getForeground() {
			return foreground;
		}

		public void setForeground(Color foreground) {
			this.foreground = foreground;
		}

		public boolean isForegroundDefault() {
			return Objects.equals(foreground, defaultForeground);
		}

		public boolean isBackgroundDefault() {
			return Objects.equals(background, defaultBackground);
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 37 * hash + java.util.Objects.hashCode(this.colorEntry);
			hash = 37 * hash + java.util.Objects.hashCode(this.defaultBackground);
			hash = 37 * hash + java.util.Objects.hashCode(this.defaultForeground);
			hash = 37 * hash + java.util.Objects.hashCode(this.background);
			hash = 37 * hash + java.util.Objects.hashCode(this.foreground);
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
			final ColorRow other = (ColorRow) obj;
			if (this.colorEntry != other.colorEntry) {
				return false;
			}
			if (!java.util.Objects.equals(this.defaultBackground, other.defaultBackground)) {
				return false;
			}
			if (!java.util.Objects.equals(this.defaultForeground, other.defaultForeground)) {
				return false;
			}
			if (!java.util.Objects.equals(this.background, other.background)) {
				return false;
			}
			if (!java.util.Objects.equals(this.foreground, other.foreground)) {
				return false;
			}
			return true;
		}
	}

	/**
	 * Wrap border.
	 * Differentiate null border from null map value (unset)
	 */
	private static class BorderWrap {
		private final Border border;

		public BorderWrap(Border border) {
			this.border = border;
		}

		public Border getBorder() {
			return border;
		}
	}
}
