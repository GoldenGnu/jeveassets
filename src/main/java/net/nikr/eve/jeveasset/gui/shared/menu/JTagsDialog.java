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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.util.HashSet;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.tag.Tag;
import net.nikr.eve.jeveasset.data.tag.TagColor;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JTagsDialog extends JDialogCentered {

	private enum TagsDialogAction {
		OK, CANCEL, SHOW_COLOR
	}

	//GUI
	private ColorPicker colorPicker;
	private JTextField jTextField;
	private JButton jColor;
	private JButton jOK;

	//Data
	private Tag tag;
	private Tag editTag;
	private Set<String> unique;

	public JTagsDialog(Program program) {
		super(program, "", Images.TAG_GRAY.getImage());

		ListenerClass listener = new ListenerClass();

		JLabel jLabel = new JLabel(GuiShared.get().tagsNewMsg());

		jTextField = new JTextField();
		jTextField.addFocusListener(listener);
		jTextField.addCaretListener(listener);

		jColor = new JButton();

		colorPicker = new ColorPicker(getDialog(), jColor);
		jColor.addFocusListener(listener);
		jColor.setActionCommand(TagsDialogAction.SHOW_COLOR.name());
		jColor.addActionListener(listener);

		jOK = new JButton(GuiShared.get().ok());
		jOK.setActionCommand(TagsDialogAction.OK.name());
		jOK.addActionListener(listener);
		jOK.addFocusListener(listener);

		JButton jCancel = new JButton(GuiShared.get().cancel());
		jCancel.setActionCommand(TagsDialogAction.CANCEL.name());
		jCancel.addActionListener(listener);
		jCancel.addFocusListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(jLabel, 250, 250, Integer.MAX_VALUE)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jTextField, 200, 200, 200)
					.addComponent(jColor, 30, 30, 30)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup()
					.addComponent(jTextField, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jColor, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	public Tag show(Tag editTag){
		return show(editTag, new HashSet<String>(Settings.get().getTags().keySet()));
	}

	public Tag show(Tag editTag, Set<String> unique){
		getDialog().setTitle(GuiShared.get().tagsEditTitle());
		this.editTag = editTag;
		this.unique = unique;
		tag = null;
		jColor.setIcon(new TagIcon(editTag.getColor()));
		jTextField.setText(editTag.getName());
		setVisible(true);
		return tag;
	}

	public Tag show() {
		return show(new HashSet<String>(Settings.get().getTags().keySet()));
	}

	public Tag show(Set<String> unique) {
		getDialog().setTitle(GuiShared.get().tagsNewTitle());
		this.unique = unique;
		editTag = null;
		tag = null;
		jColor.setIcon(new TagIcon(TagColor.getValues()[0]));
		jTextField.setText("");
		setVisible(true);
		return tag;
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jTextField;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {
	}

	@Override
	protected void save() {
		String name = jTextField.getText();
		TagIcon icon = (TagIcon) jColor.getIcon();
		TagColor color = icon.getTagColor();
		tag = new Tag(name, color);
		setVisible(false);
	}

	private class ListenerClass implements ActionListener, FocusListener, CaretListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (TagsDialogAction.OK.name().equals(e.getActionCommand())) {
				save();
			} else if (TagsDialogAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			} else if (TagsDialogAction.SHOW_COLOR.name().equals(e.getActionCommand())) {
				colorPicker.show();
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
			colorPicker.hide();
		}

		@Override
		public void focusLost(FocusEvent e) {
			
		}

		@Override
		public void caretUpdate(CaretEvent e) {
			String name = jTextField.getText();
			if (unique.contains(name) && (editTag == null || !editTag.getName().equals(name))) {
				jTextField.setBackground(new Color(255, 200, 200));
				jOK.setEnabled(false);
			} else {
				jOK.setEnabled(true);
				jTextField.setBackground(Color.WHITE);
			}
		}
	}

	public static class TagIcon implements Icon {

		private TagColor tagColor;
		private Image image = null;

		public TagIcon(TagColor tagColor) {
			this(tagColor, false);
		}

		public TagIcon(TagColor tagColor, boolean selected) {
			this.tagColor = tagColor;
			if (selected) {
				image = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(Images.TAG_TICK.getImage().getSource(), new ColorFilter(tagColor.getForeground())));
			}
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2d = (Graphics2D) g;
			if (image != null) { //Selected
				//Render settings
				//g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				//Border
				g2d.setColor(Color.BLACK);
				g2d.fillOval(x, y, getIconWidth() - 1, getIconHeight() - 1);

				//Background
				g2d.setColor(tagColor.getBackground());
				g2d.fillOval(x + 1, y + 1, getIconWidth() - 3, getIconHeight() - 3);

				//Image
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g.drawImage(image, x + 2, y + 2, null);
			} else { //Not selected
				//Render settings
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

				//Background
				g2d.setColor(tagColor.getBackground());
				g2d.fillRect(x, y, getIconWidth() - 1, getIconHeight() - 1);

				//Border
				g2d.setColor(Color.BLACK);
				g2d.drawRect(x, y, getIconWidth() - 1, getIconHeight() - 1);

				//Text
				g2d.setFont(new JLabel().getFont());
				g2d.setColor(tagColor.getForeground());
				g2d.drawString("a", x + 5, y + 11);
			}
		}

		@Override
		public int getIconWidth() {
			return 16;
		}

		@Override
		public int getIconHeight() {
			return 16;
		}

		public TagColor getTagColor() {
			return tagColor;
		}
	}

	private static class ColorFilter extends RGBImageFilter {
	
		private final int foreground;
		private final int white;

		public ColorFilter(Color foreground) {
			this.foreground = foreground.getRGB();
			white = Color.WHITE.getRGB();
		}
		
		@Override
		public int filterRGB(int x, int y, int rgb) {
			if ((rgb | 0xFFFFFF00) == white) {
				return (foreground & 0x00FFFFFF) | (rgb & 0xFF000000) ;
			} else {
				return rgb;
			}
		}
		
	}

	private static class ColorPicker {

		private JWindow jWindow;
		private JButton jButton;

		public ColorPicker(final Window owner, final JButton jButton) {
			this.jButton = jButton;

			jWindow = new JWindow(owner);
			
			JPanel jPanel = new JPanel();

			jPanel.setBorder(new JPopupMenu().getBorder());
			GroupLayout groupLayout = new GroupLayout(jPanel);
			groupLayout.setAutoCreateContainerGaps(true);
			groupLayout.setAutoCreateGaps(true);
			jPanel.setLayout(groupLayout);
			jWindow.add(jPanel);

			JPanel jButtonPanel = new JPanel();
			GridLayout gridLayout = new GridLayout(0, 4, 5, 5);
			jButtonPanel.setLayout(gridLayout);

			for (TagColor tagColor : TagColor.getValues()) {
				final TagIcon colorIcon = new TagIcon(tagColor);
				JButton button = new JButton(colorIcon);
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						jButton.setIcon(colorIcon);
						jWindow.setVisible(false);
					}
				});
				button.setBorder(null);
				button.setContentAreaFilled(false);
				jButtonPanel.add(button);
			}
			JButton jCustom = new JButton(GuiShared.get().custom()); //Custom...
			jCustom.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					jWindow.setVisible(false);
					TagIcon icon = (TagIcon) jButton.getIcon();
					Color background = JColorChooser.showDialog(owner, GuiShared.get().background(), icon.getTagColor().getBackground());
					if (background == null) {
						return;
					}
					Color foreground = JColorChooser.showDialog(owner, GuiShared.get().foreground(), icon.getTagColor().getForeground());
					if (foreground == null) {
						return;
					}
					icon = new TagIcon(new TagColor(background, foreground));
					jButton.setIcon(icon);
				}
			});

			groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(jButtonPanel)
					.addComponent(jCustom)
			);
			groupLayout.setVerticalGroup(
				groupLayout.createSequentialGroup()
					.addComponent(jButtonPanel)
					.addComponent(jCustom)
			);
		}

		public void show() {
			Point point = jButton.getLocationOnScreen();
			jWindow.pack();
			jWindow.setLocation(point.x + 2, point.y + jButton.getHeight() - 1);
			jWindow.setVisible(true);
		}

		public void hide() {
			jWindow.setVisible(false);
		}
		
	}
}
