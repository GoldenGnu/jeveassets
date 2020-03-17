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
package net.nikr.eve.jeveasset.gui.shared.components;

import java.util.Objects;
import javax.swing.Icon;
import javax.swing.JButton;


public class JButtonComparable extends JButton implements Comparable<JButtonComparable> {

	public JButtonComparable() {
	}

	public JButtonComparable(Icon icon) {
		super(icon);
	}

	public JButtonComparable(String string) {
		super(string);
	}

	public JButtonComparable(String string, Icon icon) {
		super(string, icon);
	}

	@Override
	public int compareTo(JButtonComparable o) {
		return getText().compareTo(o.getText());
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
