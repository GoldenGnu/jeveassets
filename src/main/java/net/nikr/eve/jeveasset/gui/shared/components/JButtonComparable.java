/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

import com.formdev.flatlaf.ui.FlatButtonBorder;
import java.awt.Color;
import java.awt.Component;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.Border;


public class JButtonComparable extends JButton implements Comparable<Component> {

	private boolean lock = false;

	public JButtonComparable() {
		this(null, null);
	}

	public JButtonComparable(Icon icon) {
		this(null, icon);
	}

	public JButtonComparable(String text) {
		this(text, null);
	}

	public JButtonComparable(String text, Icon icon) {
		super(text, icon);
		updateBorder();
		lock();
	}

	@Override
	public void setBackground(Color bg) {
		if (lock) {
			return;
		}
		super.setBackground(bg);
	}

	@Override
	public void setForeground(Color fg) {
		if (lock) {
			return;
		}
		super.setForeground(fg);
	}

	@Override
	public void setBorder(Border border) {
		if (lock) {
			return;
		}
		super.setBorder(border);
	}

	/**
	 * Workaround for: https://github.com/JFormDesigner/FlatLaf/issues/331
	 */
	private void updateBorder() {
		if (getBorder() instanceof FlatButtonBorder) {
			super.setBorder(new FlatButtonBorder() {
				@Override
				protected boolean isCellEditor(Component c) {
					return false;
				}
			});
		}
	}

	private void lock() {
		this.lock = true;
	}

	@Override
	public int compareTo(Component o) {
		return 0;
	}

	@Override
	public String toString() {
		return getText();
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + Objects.hashCode(this.getText());
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
		final JButtonComparable other = (JButtonComparable) obj;
		if (!Objects.equals(this.getText(), other.getText())) {
			return false;
		}
		return true;
	}

}
