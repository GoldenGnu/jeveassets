/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.Program;


public abstract class JGroupLayoutPanel {

	protected Program program;
	protected GroupLayout layout;
	private JPanel jPanel;

	public JGroupLayoutPanel(final Program program) {
		this.program = program;

		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
	}

	/**
	 *
	 * @param load does nothing except change the signature.
	 */
	protected JGroupLayoutPanel(final boolean load) { }

	public JPanel getPanel() {
		return jPanel;
	}
}
