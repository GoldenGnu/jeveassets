/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageGetter {

	private static Logger LOG = LoggerFactory.getLogger(ImageGetter.class);

	public ImageGetter() {
	}

	public static ImageIcon getIcon(String s){
		return new ImageIcon(getBufferedImage(s));
	}

	public static Image getImage(String s) {
		return getBufferedImage(s);
		
	}

	public static BufferedImage getBufferedImage(String s) {
		try {
			java.net.URL imgURL = ImageGetter.class.getResource(s);
			return ImageIO.read(imgURL);
		} catch (IOException ex) {
			LOG.warn("icon: "+s+" not found");
		}
		return null;
	}

	public static java.net.URL getURL(String s) {
		java.net.URL imgURL = ImageGetter.class.getResource(s);
		return imgURL;
	}
}
