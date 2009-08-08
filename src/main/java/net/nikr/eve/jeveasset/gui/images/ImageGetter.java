/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.gui.images;

import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class ImageGetter {

	public ImageGetter() {
	}

	public static ImageIcon getIcon(String s){
		java.net.URL imgURL = ImageGetter.class.getResource(s);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			return null;
		}
	}

	public static Image getImage(String s) {
		try {
			java.net.URL imgURL = ImageGetter.class.getResource(s);
			if (imgURL != null) {
				return ImageIO.read(imgURL);
			}
		} catch (IOException ex) {

		}
		return null;
	}

	public static java.net.URL getURL(String s) {
		java.net.URL imgURL = ImageGetter.class.getResource(s);
		return imgURL;
	}
}
