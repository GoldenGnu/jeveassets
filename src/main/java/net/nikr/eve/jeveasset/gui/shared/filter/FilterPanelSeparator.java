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
package net.nikr.eve.jeveasset.gui.shared.filter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.util.Arrays;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import net.nikr.eve.jeveasset.gui.images.Images;


public class FilterPanelSeparator {

	private final JPanel jPanel;
	private final int group;

	public FilterPanelSeparator(int group) {
		this.group = group;

		jPanel = new JPanel();

		GroupLayout layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		BufferedImageOp lookup = new LookupOp(new ColorMapper(Color.WHITE, getColor("nimbusBlueGrey", "Separator.foreground")), null);
		BufferedImage convertedImage = lookup.filter((BufferedImage)Images.MISC_AND.getImage(), null);
		JLabel jIcon = new JLabel(new ImageIcon(convertedImage));

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGap(30)
				.addComponent(jIcon, 12, 12, 12)
		);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGap(1)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(jIcon, 4, 4, 4)
				)
				.addGap(1)
		);
	}

	public JPanel getPanel() {
		return jPanel;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 47 * hash + this.group;
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
		final FilterPanelSeparator other = (FilterPanelSeparator) obj;
		if (this.group != other.group) {
			return false;
		}
		return true;
	}

	private static Color getColor(String ... values) {
		for (String s : values) {
			Color color = UIManager.getColor(s);
			if (color != null) {
				return color;
			}
		}
		return Color.BLACK;
	}

	private static class ColorMapper extends LookupTable {

		private final int[] from;
		private final int[] to;

		public ColorMapper(Color from,
				Color to) {
			super(0, 4);

			this.from = new int[]{
				from.getRed(),
				from.getGreen(),
				from.getBlue(),
				from.getAlpha(),};
			this.to = new int[]{
				to.getRed(),
				to.getGreen(),
				to.getBlue(),
				to.getAlpha(),};
		}

		@Override
		public int[] lookupPixel(int[] src, int[] dest) {
			if (dest == null) {
				dest = new int[src.length];
			}

			int[] newColor = (Arrays.equals(src, from) ? to : src);
			System.arraycopy(newColor, 0, dest, 0, newColor.length);

			return dest;
		}
	}
}
