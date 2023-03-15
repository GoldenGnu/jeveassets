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
package net.nikr.eve.jeveasset.gui.shared;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ToolTipManager;


public class InstantToolTip {

	private final MouseListener listener;
	private final Component component;

	private InstantToolTip(Component component) {
		this.component = component;
		this.listener = new MouseAdapter() { //Instant ToolTips
			private int defaultDismissTimeout;
			private int defaultInitialDelay;

			@Override
			public void mouseEntered(MouseEvent me) {
				defaultDismissTimeout = ToolTipManager.sharedInstance().getDismissDelay();
				defaultInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();
				ToolTipManager.sharedInstance().setDismissDelay(60000);
				ToolTipManager.sharedInstance().setInitialDelay(0);
			}

			@Override
			public void mouseExited(MouseEvent me) {
				ToolTipManager.sharedInstance().setDismissDelay(defaultDismissTimeout);
				ToolTipManager.sharedInstance().setInitialDelay(defaultInitialDelay);
			}
		};
		component.addMouseListener(listener);
	}

	public static InstantToolTip install(Component component) {
		return new InstantToolTip(component);
	}

	public void uninstall() {
		component.removeMouseListener(listener);
	}
}
