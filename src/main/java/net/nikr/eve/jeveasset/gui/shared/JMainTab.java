/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.Program;

/**
 *
 * @author Niklas
 */
public abstract class JMainTab {

	private String title;
	private Icon icon;
	private boolean closeable;
	protected Program program;
	protected JPanel jPanel;
	protected GroupLayout layout;

	protected JMainTab(boolean load) { }

	public JMainTab(Program program, String title, Icon icon, boolean closeable) {
		this.program = program;
		this.title = title;
		this.icon = icon;
		this.closeable = closeable;
		
		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
	}

	public abstract void updateData();

	public Icon getIcon() {
		return icon;
	}

	public JPanel getPanel() {
		return jPanel;
	}

	public String getTitle() {
		return title;
	}

	public boolean isCloseable() {
		return closeable;
	}

}
