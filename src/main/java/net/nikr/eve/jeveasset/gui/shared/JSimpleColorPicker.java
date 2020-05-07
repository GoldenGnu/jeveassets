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
package net.nikr.eve.jeveasset.gui.shared;

import com.google.common.base.Objects;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import net.nikr.eve.jeveasset.data.settings.Colors;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;

public class JSimpleColorPicker {

	private final JDialog jDialog;
	private final List<ColorIcon> icons = new ArrayList<>();
	private final JButton jDefault;
	private final JButton jNone;
	private Color input;
	private Color defaultColor;
	private ColorListenere colorListenere;

	public JSimpleColorPicker(Window jParent) {

		jDialog = new JDialog(jParent);
		jDialog.setUndecorated(true);
		jDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowDeactivated(WindowEvent e) {
				cancel();
			}
		});
		JPanel jPanel = new JPanel();
		jPanel.setBackground(Color.WHITE);


		jPanel.setBorder(new JPopupMenu().getBorder());
		GroupLayout groupLayout = new GroupLayout(jPanel);
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);
		jPanel.setLayout(groupLayout);
		jDialog.add(jPanel);

		jDefault = createButton(GuiShared.get().colorDefault());
		jDefault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});

		jNone = createButton(GuiShared.get().colorNone());
		jNone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				none();
			}
		});

		JPanel jColorPanel = new JPanel();
		jColorPanel.setOpaque(false);
		jColorPanel.setLayout(new GridLayout(0, 10, 0, 1));

		for (Color color : getColors()) {
			add(jColorPanel, color);
		}

		List<Colors> colors = new ArrayList<>();
		colors.add(Colors.LIGHT_GRAY);
		colors.add(Colors.LIGHT_GRAY_STRONG);
		colors.add(Colors.DARK_RED);
		colors.add(Colors.DARK_GREEN);
		colors.add(Colors.LIGHT_RED);
		colors.add(Colors.LIGHT_RED_STRONG);
		colors.add(Colors.LIGHT_ORANGE);
		colors.add(Colors.LIGHT_ORANGE_STRONG);
		colors.add(Colors.LIGHT_YELLOW);
		colors.add(Colors.LIGHT_YELLOW_STRONG);
		colors.add(Colors.LIGHT_GREEN);
		colors.add(Colors.LIGHT_GREEN_STRONG);
		colors.add(Colors.LIGHT_BLUE);
		colors.add(Colors.LIGHT_BLUE_STRONG);
		colors.add(Colors.LIGHT_MAGENTA);
		colors.add(Colors.LIGHT_MAGENTA_STRONG);

		JPanel jClassicPanel = new JPanel();
		jClassicPanel.setOpaque(false);
		jClassicPanel.setLayout(new GridLayout(0, 2, 0, 1));

		for (Colors defaultColors : colors) {
			add(jClassicPanel, defaultColors.getColor());
		}

		JButton jCustom = createButton(GuiShared.get().colorCustom());
		jCustom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color color = JColorChooser.showDialog(jParent, null, input);
				if (color == null) {
					cancel();
				} else {
					save(color);
					
				}
			}
		});

		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(jDefault, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
							.addComponent(jNone, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
						)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(jColorPanel)
							.addGap(15)
							.addComponent(jClassicPanel)
						)
						.addComponent(jCustom, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
				groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(jDefault)
							.addComponent(jNone)
						)
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(jColorPanel)
							.addComponent(jClassicPanel)
						)
						.addComponent(jCustom)
		);
	}

	private void add(JPanel jPanel, Color color) {
		if (color == null) {
			return;
		}
		ColorIcon icon = new ColorIcon(color);
		icons.add(icon);

		JButton jButton = createButton(icon);
		jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save(color);
			}
		});
		jPanel.add(jButton);
	}

	public void show(Color input, Color defaultColor, boolean nullable, Point point, ColorListenere colorListenere) {
		this.input = input;
		this.defaultColor = defaultColor;
		this.colorListenere = colorListenere;
		for (ColorIcon icon : icons) {
			icon.setSelect(input);
		}
		jDefault.setEnabled(!Objects.equal(input, defaultColor));
		jDefault.setBorder(getBorder(jDefault));
		jNone.setEnabled(input != null && nullable);
		jNone.setBorder(getBorder(jNone));
		jDialog.pack();
		jDialog.setLocation(point.x - jDialog.getSize().width, point.y);
		jDialog.setVisible(true);
	}

	private void cancel() {
		jDialog.setVisible(false);
		colorListenere.cancelled();
	}

	private void reset() {
		colorListenere.colorChanged(defaultColor);
		jDialog.setVisible(false);
	}

	private void none() {
		colorListenere.colorChanged(null);
		jDialog.setVisible(false);
	}

	private void save(Color color) {
		colorListenere.colorChanged(color);
		jDialog.setVisible(false);
	}

	private Border getBorderInner(JButton jButton) {
		if (jButton.isEnabled()) {
			return BorderFactory.createLineBorder(Color.BLACK, 1);
		} else {
			return BorderFactory.createLineBorder(Color.DARK_GRAY, 1);
		}
	}

	private Border getBorder(JButton jButton) {
		return BorderFactory.createCompoundBorder(getBorderInner(jButton), BorderFactory.createEmptyBorder(2, 2, 2, 2));
	}

	private JButton createButton(String text) {
		return config(text, null, true);
	}

	private JButton createButton(Icon icon) {
		return config(null, icon, false);
	}

	private JButton config(String text, Icon icon, boolean border) {
		JButton jButton = new JButton();
		if (text != null) {
			jButton.setText(text);
			jButton.setBackground(Colors.TABLE_SELECTION_BACKGROUND.getColor());
			jButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					if (jButton.isEnabled()) {
						jButton.setBorder(BorderFactory.createCompoundBorder(getBorderInner(jButton),
							BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Colors.TABLE_SELECTION_BACKGROUND.getColor(), 1),
							BorderFactory.createEmptyBorder(1, 1, 1, 1))));
					}
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					jButton.setBorder(getBorder(jButton));
				}
			});
		}
		if (icon != null) {
			jButton.setIcon(icon);
			if (icon instanceof ColorIcon) {
				ColorIcon colorIcon = (ColorIcon) icon;
				jButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						if (jButton.isEnabled()) {
							colorIcon.setHover(true);
							jButton.repaint();
						}
					}

					@Override
					public void mouseExited(MouseEvent e) {
						colorIcon.setHover(false);
						jButton.repaint();
					}
				});
			}
		}
		jButton.setFocusPainted(false);
		if (border) {
			jButton.setBorder(getBorder(jButton));
		} else {
			jButton.setBorder(null);
		}
		jButton.setContentAreaFilled(false);
		return jButton;
	}

	public static interface ColorListenere {
		public void colorChanged(Color color);
		public void cancelled();
	}

	public static class ColorIcon implements Icon {

		private final Color color;
		private final boolean backgroundIsBright;
		private final int size = 22;
		private boolean selected = false;
		private boolean hover = false;

		public ColorIcon(Color color) {
			this.color = color;
			backgroundIsBright = ColorTools.isBrightColor(color);
		}

		public void setSelect(Color match) {
			this.selected = color.equals(match);
		}

		public void setHover(boolean hover) {
			this.hover = hover;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2d = (Graphics2D) g;

			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (hover) {
				//Hover
				AlphaComposite newValue = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
				Composite oldValue = g2d.getComposite();
				g2d.setComposite(newValue);
				g2d.setColor(Color.BLACK);
				fillOval(g2d, 0, x, y);
				g2d.setComposite(oldValue);
			}

			//Border
			g2d.setColor(color.darker());
			fillOval(g2d, 2, x, y);

			//Background
			g2d.setColor(color);
			fillOval(g2d, 3, x, y);

			//Icon
			if (selected) {
				if (backgroundIsBright) {
					g2d.drawImage(Images.SETTINGS_COLOR_CHECK_BLACK.getImage(), x + (getIconWidth()-16)/2, y + (getIconWidth()-16)/2, null);
				} else {
					g2d.drawImage(Images.SETTINGS_COLOR_CHECK_WHITE.getImage(), x + (getIconWidth()-16)/2, y + (getIconWidth()-16)/2, null);
					
				}
			}
		}

		private void fillOval(Graphics2D g2d, int offset, int x, int y) {
			g2d.fillOval(x + (offset), y + (offset), getIconWidth() - (offset*2), getIconHeight() - (offset*2));
		}

		@Override
		public int getIconWidth() {
			return size;
		}

		@Override
		public int getIconHeight() {
			return size;
		}
	}

	private static List<Color> getColors() {
		List<Color> colors = new ArrayList<>();
		//Black Gray White
		colors.add(new Color(0, 0, 0));
		colors.add(new Color(67, 67, 67));
		colors.add(new Color(102, 102, 102));
		colors.add(new Color(153, 153, 153));
		colors.add(new Color(183, 183, 183));
		colors.add(new Color(204, 204, 204));
		colors.add(new Color(217, 217, 217));
		colors.add(new Color(239, 239, 239));
		colors.add(new Color(243, 243, 243));
		colors.add(new Color(255, 255, 255));

		//Strong
		colors.add(new Color(152, 0, 0));	//Brown
		colors.add(new Color(255, 0, 0));	//Red
		colors.add(new Color(255, 152, 0));	//Orange
		colors.add(new Color(255, 255, 0));	//Yellow
		colors.add(new Color(0, 255, 0));	//Green
		colors.add(new Color(0, 255, 255));	//Cyan
		colors.add(new Color(74, 134, 232));//Light Blue
		colors.add(new Color(0, 0, 255));	//Blue
		colors.add(new Color(152, 0, 255));	//Purple
		colors.add(new Color(255, 0, 255));	//Magenta

		colors.add(new Color(230, 184, 175));//Brown
		colors.add(new Color(244, 204, 204));//Red
		colors.add(new Color(252, 229, 205));//Orange
		colors.add(new Color(255, 242, 204));//Yellow
		colors.add(new Color(217, 234, 211));//Green
		colors.add(new Color(208, 224, 227));//Cyan
		colors.add(new Color(201, 218, 248));//Light Blue
		colors.add(new Color(207, 226, 243));//Blue
		colors.add(new Color(217, 210, 233));//Purple
		colors.add(new Color(234, 209, 220));//Magenta

		colors.add(new Color(221, 126, 107)); //Brown
		colors.add(new Color(234, 153, 153)); //Red
		colors.add(new Color(249, 203, 156)); //Orange
		colors.add(new Color(255, 229, 153)); //Yellow
		colors.add(new Color(182, 215, 168)); //Green
		colors.add(new Color(162, 196, 201)); //Cyan
		colors.add(new Color(164, 194, 244)); //Light Blue
		colors.add(new Color(159, 197, 232)); //Blue
		colors.add(new Color(180, 167, 214)); //Purple
		colors.add(new Color(213, 166, 189)); //Magenta

		colors.add(new Color(204, 65, 37)); //Brown
		colors.add(new Color(224, 102, 102)); //Red
		colors.add(new Color(246, 178, 107)); //Orange
		colors.add(new Color(255, 217, 102)); //Yellow
		colors.add(new Color(147, 196, 125)); //Green
		colors.add(new Color(118, 165, 175)); //Cyan
		colors.add(new Color(109, 158, 235)); //Light Blue
		colors.add(new Color(111, 168, 220)); //Blue
		colors.add(new Color(142, 124, 195)); //Purple
		colors.add(new Color(194, 123, 160)); //Magenta

		colors.add(new Color(166, 28, 0)); //Brown
		colors.add(new Color(204, 0, 0)); //Red
		colors.add(new Color(230, 145, 56)); //Orange
		colors.add(new Color(241, 194, 50)); //Yellow
		colors.add(new Color(106, 168, 79)); //Green
		colors.add(new Color(69, 129, 142)); //Cyan
		colors.add(new Color(60, 120, 216)); //Light Blue
		colors.add(new Color(61, 133, 198)); //Blue
		colors.add(new Color(103, 78, 167)); //Purple
		colors.add(new Color(166, 77, 121)); //Magenta

		colors.add(new Color(133, 32, 12)); //Brown
		colors.add(new Color(153, 0, 0)); //Red
		colors.add(new Color(180, 95, 6)); //Orange
		colors.add(new Color(191, 144, 0)); //Yellow
		colors.add(new Color(56, 118, 29)); //Green
		colors.add(new Color(19, 79, 92)); //Cyan
		colors.add(new Color(17, 85, 204)); //Light Blue
		colors.add(new Color(11, 83, 148)); //Blue
		colors.add(new Color(53, 28, 117)); //Purple
		colors.add(new Color(116, 27, 71)); //Magenta

		colors.add(new Color(91, 15, 0)); //Brown
		colors.add(new Color(102, 0, 0)); //Red
		colors.add(new Color(120, 63, 5)); //Orange
		colors.add(new Color(127, 96, 0)); //Yellow
		colors.add(new Color(39, 78, 19)); //Green
		colors.add(new Color(12, 52, 61)); //Cyan
		colors.add(new Color(28, 69, 135)); //Light Blue
		colors.add(new Color(7, 55, 99)); //Blue
		colors.add(new Color(32, 18, 77)); //Purple
		colors.add(new Color(76, 17, 48)); //Magenta

		return colors;
	}

}
