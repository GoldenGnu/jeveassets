/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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


public enum TagColor {
	GRAY (Color.LIGHT_GRAY, Color.BLACK),
	RED (Color.RED, Color.BLACK),
	GREEN (Color.GREEN, Color.BLACK),
	BLUE (Color.BLUE, Color.WHITE),
	CYAN (Color.CYAN, Color.BLACK),
	MAGENTA (Color.MAGENTA, Color.BLACK),
	ORANGE (Color.ORANGE, Color.BLACK),
	PINK (Color.PINK, Color.BLACK),
	YELLOW (Color.YELLOW, Color.BLACK),
	;

	private Color background;
	private Color forground;
	private String backgroundHtml;
	private String forgroundHtml;

	private TagColor(Color background, Color forground) {
		this.background = background;
		this.forground = forground;
		this.backgroundHtml = colorToHex(background);
		this.forgroundHtml = colorToHex(forground);
	}

	public Color getBackground() {
		return background;
	}

	public Color getForground() {
		return forground;
	}

	public String getBackgroundHtml() {
		return backgroundHtml;
	}

	public String getForgroundHtml() {
		return forgroundHtml;
	}

	private String colorToHex(Color color) {
		String rgb = Integer.toHexString(color.getRGB());
		return rgb.substring(2, rgb.length());
	}
}
