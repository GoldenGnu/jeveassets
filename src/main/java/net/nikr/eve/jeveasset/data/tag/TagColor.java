/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.tag;

import java.awt.Color;


public class TagColor {

	private static final TagColor[] VALUES = {
		new TagColor(Color.WHITE, Color.BLACK), //White and black
		new TagColor(Color.WHITE, Color.RED.darker().darker()), //White and red
		new TagColor(Color.WHITE, Color.GREEN.darker().darker()), //White and green
		new TagColor(Color.WHITE, Color.BLUE.darker().darker()), //White and blue
		new TagColor(Color.LIGHT_GRAY, Color.BLACK), //Gray
		new TagColor(Color.BLACK, Color.WHITE), //Black
		new TagColor(Color.CYAN, Color.BLACK), //Cyan
		new TagColor(Color.BLUE, Color.WHITE), //Blue
		new TagColor(Color.MAGENTA, Color.BLACK), //Magenta
		new TagColor(Color.PINK, Color.BLACK), //Pink
		new TagColor(Color.RED, Color.BLACK), //Red
		new TagColor(Color.ORANGE, Color.BLACK), //Orange
		new TagColor(Color.YELLOW, Color.BLACK), //Yellow
		new TagColor(Color.GREEN, Color.BLACK), //Green
	};

	private Color backgroundColor;
	private Color foregroundColor;
	private String backgroundHtml;
	private String foregroundHtml;

	public TagColor(Color background, Color foreground) {
		this(colorToHex(background), colorToHex(foreground));
	}

	public TagColor(String background, String foreground) {
		this.backgroundColor = Color.decode("0x"+background);
		this.foregroundColor = Color.decode("0x"+foreground);
		this.backgroundHtml = background;
		this.foregroundHtml = foreground;
	}

	public Color getBackground() {
		return backgroundColor;
	}

	public Color getForeground() {
		return foregroundColor;
	}

	public String getBackgroundHtml() {
		return backgroundHtml;
	}

	public String getForegroundHtml() {
		return foregroundHtml;
	}

	public static TagColor[] getValues() {
		return VALUES;
	}

	private static String colorToHex(Color color) {
		String rgb = Integer.toHexString(color.getRGB());
		return rgb.substring(2, rgb.length());
	}
}
