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

package net.nikr.eve.jeveasset.gui.shared.components;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;


public class JDefaultField extends JTextField implements FocusListener {

	private String defaultValue;

	public JDefaultField(final String defaultValue) {
		this.defaultValue = defaultValue;
		this.addFocusListener(this);
	}

	@Override
	public void focusGained(final FocusEvent e) { }

	@Override
	public void focusLost(final FocusEvent e) {
		if (super.getText().length() == 0) {
			super.setText(defaultValue);
		}
	}

	@Override
	public String getText() {
		if (super.getText().length() == 0) {
			super.setText(defaultValue);
		}
		return super.getText();
	}

	@Override
	public void setText(final String t) {
		if (t.length() == 0) {
			super.setText(defaultValue);
		} else {
			super.setText(t);
		}
	}
}
