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

package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JWindow;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.tag.Tag;
import net.nikr.eve.jeveasset.data.tag.TagColor;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.GuiShared;

/**
 *
 * @author Niklas
 */
public class JTagsDialog extends JDialogCentered {

	private enum TagsDialogAction {
		OK, CANCEL, SHOW_COLOR
	}

	private ColorPicker colorPicker;
	private JTextField jTextField;
	private JButton jColor;
	private JButton jOK;
	private Tag tag;

	public JTagsDialog(Program program) {
		super(program, GuiShared.get().tagsNewTitle(), Images.TAG_GRAY.getImage());

		ListenerClass listener = new ListenerClass();

		JLabel jLabel = new JLabel(GuiShared.get().tagsNewMsg());

		jTextField = new JTextField();
		jTextField.addFocusListener(listener);


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

	public Tag show() {
		tag = null;
		jColor.setIcon(new ColorIcon(TagColor.values()[0]));
		jTextField.setText("");
		this.setVisible(true);
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
		ColorIcon icon = (ColorIcon) jColor.getIcon();
		TagColor color = icon.getTagColor();
		tag = new Tag(name, color);
		setVisible(false);
	}

	private class ListenerClass implements ActionListener, FocusListener {

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
	}

	private static class ColorIcon implements Icon {

		private TagColor tagColor;

		public ColorIcon(TagColor tagColor) {
			this.tagColor = tagColor;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(tagColor.getBackground());
			g.fillRect(x, y, getIconWidth(), getIconHeight());
			g.setColor(Color.BLACK);
			g.drawRect(x, y, getIconWidth(), getIconHeight());
			g.setColor(tagColor.getForground());
			g.drawString("a", x + 5, y + 11);
		}

		@Override
		public int getIconWidth() {
			return 14;
		}

		@Override
		public int getIconHeight() {
			return 14;
		}

		public TagColor getTagColor() {
			return tagColor;
		}
	}

	private static class ColorPicker {

		private JWindow jWindow;
		private JButton jButton;

		public ColorPicker(final Window owner, final JButton jButton) {
			this.jButton = jButton;

			jWindow = new JWindow(owner);

			JPanel jPanel = new JPanel();
			jPanel.setBorder(BorderFactory.createCompoundBorder(
					new JPopupMenu().getBorder(),
					BorderFactory.createEmptyBorder(10, 10, 10, 10)));
			GridLayout gridLayout = new GridLayout(0, 4, 5, 5);
			jPanel.setLayout(gridLayout);
			jWindow.add(jPanel);

			for (TagColor tagColor : TagColor.values()) {
				final ColorIcon colorIcon = new ColorIcon(tagColor);
				JButton button = new JButton(colorIcon);
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						jButton.setIcon(colorIcon);
						jWindow.setVisible(false);
					}
				});
				button.setBorder(null);
				jPanel.add(button);
			}
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
