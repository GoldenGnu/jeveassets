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

package net.nikr.eve.jeveasset.gui.shared.components;

import java.awt.Dimension;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;


public class JLockWindow {

	private JWindow jWindow;
	private JLabel jLabel;
	private Window parent;

	public JLockWindow(final Window parent) {
		this.parent = parent;
		jWindow = new JWindow(parent);

		JPanel jPanel = new JPanel();
		jPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		GroupLayout layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		jWindow.add(jPanel);

		jLabel = new JLabel();


		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(jLabel)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jLabel)
		);
	}

	public void show(final Runnable runnable, final String text) {
		jLabel.setText(text);
		jWindow.pack();
		//Get the parent size
		Dimension parentSize = parent.getSize();

		//Calculate the frame location
		int x = (parentSize.width - jWindow.getWidth()) / 2;
		int y = (parentSize.height - jWindow.getHeight()) / 2;

		//Set the new frame location
		jWindow.setLocation(x, y);
		jWindow.setLocationRelativeTo(parent);
		parent.setEnabled(false);
		jWindow.setVisible(true);
		Thread thread = new Thread(new Wait(runnable));
		thread.start();
	}

	private void hide() {
		parent.setEnabled(true);
		jWindow.setVisible(false);
	}

	class Wait implements Runnable {

		private Runnable runnable;

		public Wait(Runnable runnable) {
			this.runnable = runnable;
		}

		@Override
		public void run() {
			try {
			//SwingUtilities.invokeLater(runnable);
				SwingUtilities.invokeAndWait(runnable);
			} catch (InterruptedException ex) {

			} catch (InvocationTargetException ex) {

			}
			hide();
		}
		
	}
}
