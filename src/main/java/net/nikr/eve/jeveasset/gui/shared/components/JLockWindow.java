/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingWorker;


public class JLockWindow {

	private final JWindow jWindow;
	private final JLabel jLabel;
	private final Window parent;
	private final JProgressBar jProgress;

	public JLockWindow(final Window parent) {
		this.parent = parent;
		jWindow = new JWindow(parent);

		jProgress = new JProgressBar(0, 100);

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
				.addComponent(jProgress)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jLabel)
				.addComponent(jProgress)
		);
	}

	public void show(final String text, final LockWorker lockWorker) {
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
		jProgress.setIndeterminate(false);
		jProgress.setIndeterminate(true);
		jWindow.setVisible(true); //Does not block!
		Wait wait = new Wait(lockWorker);
		wait.execute();
	}

	private void hide() {
		parent.setEnabled(true);
		jWindow.setVisible(false);
	}

	class Wait extends SwingWorker<Void, Void>{

		private final LockWorker worker;

		public Wait(LockWorker worker) {
			this.worker = worker;
		}

		@Override
		protected Void doInBackground() throws Exception {
			worker.task();
			return null;
		}

		@Override
		protected void done() {
			worker.gui();
			hide();
			if (worker instanceof LockWorkerAdvanced) {
				((LockWorkerAdvanced) worker).hidden();
			}
			try {
				get();
			} catch (InterruptedException | ExecutionException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public static interface LockWorker {
		public void task();
		public void gui();
	}

	public static interface LockWorkerAdvanced extends LockWorker {
		public void hidden();
	}
}
