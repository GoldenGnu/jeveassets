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

package net.nikr.eve.jeveasset.gui.shared.components;

import java.awt.Dimension;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import net.nikr.eve.jeveasset.Program;


public class JFixedToolBar extends JToolBar {

	private Integer height = null;
	private final Orientation orientation;

	public static enum Orientation {
		HORIZONTAL,
		VERTICAL
	}

	public JFixedToolBar() {
		this(Orientation.HORIZONTAL);
	}

	public JFixedToolBar(Orientation orientation) {
		super(); //We don't need orientation as it's only used by DefaultToolBarLayout
		this.orientation = orientation;
		setLayout(new BoxLayout(this, orientation == Orientation.HORIZONTAL ? BoxLayout.LINE_AXIS : BoxLayout.PAGE_AXIS));
		setFloatable(false);
		setRollover(true);
		getSize();
	}

	public void addGlue() {
		add(Box.createGlue());
	}

	public void addGlue(int width) {
		add(new Box.Filler(new Dimension(width, 0), new Dimension(width, 0), new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)));
	}

	public void add(JCheckBox jCheckBox) {
		addSpace(5);
		jCheckBox.setBorder(null);
        super.add(jCheckBox);
		addSpace(5);
    }

	public void add(final JComponent jComponent, int width) {
		if (width > 0) {
			setSize(jComponent, width);
		}
		super.add(jComponent);
	}

	public void addComboBox(final JComboBox<?> jComboBox, int width) {
		if (width > 0) {
			setSize(jComboBox, width);
		}
		super.add(jComboBox);
	}

	public void addLabelIcon(final JLabel jButton) {
		Dimension dimension = new Dimension(Program.getIconButtonsWidth(), Program.getButtonsHeight());
		jButton.setPreferredSize(dimension);
		jButton.setMaximumSize(dimension);

		jButton.setHorizontalAlignment(SwingConstants.CENTER);
		super.add(jButton);
	}

	public void addButtonIcon(final AbstractButton jButton) {
		Dimension dimension = new Dimension(Program.getIconButtonsWidth(), Program.getButtonsHeight());
		jButton.setPreferredSize(dimension);
		jButton.setMaximumSize(dimension);

		jButton.setHorizontalAlignment(SwingConstants.CENTER);
		super.add(jButton);
	}

	public void addButton(final AbstractButton jButton) {
		addButton(jButton, 100, SwingConstants.LEFT);
	}

	public void addButton(final AbstractButton jButton, final int width, final int alignment) {
		if (width > 0) {
			setSize(jButton, width);
		}
		jButton.setHorizontalAlignment(alignment);
		super.add(jButton);
	}

	public void addSpace(final int width) {
		super.add(Box.createRigidArea(new Dimension(width,0)));
	}

	private void setSize(JComponent jComponent, int width) {
		setSize(jComponent, width, Program.getButtonsHeight());
	}

	private void setSize(final JComponent jComponent, final int width, final int height) {
		int preferredWidth = jComponent.getPreferredSize().width;
		Dimension dimension;
		if (preferredWidth > width) {
			dimension = new Dimension(preferredWidth, height);
		} else {
			dimension = new Dimension(width, height);
		}
		jComponent.setPreferredSize(dimension);
		jComponent.setMaximumSize(dimension);
	}

	@Override
	public Dimension getPreferredSize() {
		if (orientation == Orientation.VERTICAL) {
			return super.getPreferredSize();
		} else {
			if (height == null) {
				height = getInsets().top + getInsets().bottom + Program.getButtonsHeight();
			}
			Dimension preferredSize = super.getPreferredSize();
			return new Dimension(preferredSize.width, height);
		}
	}
}
