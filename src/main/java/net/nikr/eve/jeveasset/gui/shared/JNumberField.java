/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;


public class JNumberField extends JTextField implements FocusListener{

	private String defaultValue;

	public JNumberField() {
		this("0");
	}

	public JNumberField(String defaultValue) {
		this.defaultValue = defaultValue;
		this.addFocusListener(this);
		this.setDocument( DocumentFactory.getIntegerPlainDocument() );
	}

	@Override
	public void focusGained(FocusEvent e) {

	}

	@Override
	public void focusLost(FocusEvent e) {
		if (super.getText().length() == 0){
			super.setText(defaultValue);
		}
	}

	@Override
	public String getText() {
		if (super.getText().length() == 0) super.setText(defaultValue);
		return super.getText();
	}
}
