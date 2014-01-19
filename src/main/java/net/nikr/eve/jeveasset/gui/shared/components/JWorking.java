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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import net.nikr.eve.jeveasset.gui.images.Images;


public class JWorking extends JPanel {

	private static final int IMG_FRAMES = 24;
	private static final int IMG_WIDTH = 32;
	private static final int IMG_HEIGHT = 32;

	private BufferedImage[] loadingImages;
	private int currentLoadingImage = 0;
	private Worker worker;

	public JWorking() {
		this.setMinimumSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
		this.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
		this.setMaximumSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
		this.setDoubleBuffered(true);
		loadingImages = new BufferedImage[IMG_FRAMES];
		for (int i = 0; i < IMG_FRAMES; i++) {
			String number;
			if ((i + 1) < 10) {
				number = "0" + (i + 1);
			} else {
				number = "" + (i + 1);
			}
			loadingImages[i] = Images.getBufferedImage("working" + number + ".png");
		}
		ListenerClass listenerClass = new ListenerClass();
		addAncestorListener(listenerClass);
	}

	private void auto() {
		if (isShowing()) {
			start();
		} else {
			end();
		}
	}

	private void start() {
		end();
		worker = new Worker(this);
		worker.start();
	}

	private void end() {
		if (worker != null) {
			worker.interrupt();
			worker = null;
		}
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		if (loadingImages[currentLoadingImage] != null) {
			g2.drawImage(loadingImages[currentLoadingImage], 0, 0, null);
		}
	}

	private class ListenerClass implements AncestorListener {

		@Override
		public void ancestorAdded(AncestorEvent event) {
			auto();
		}

		@Override
		public void ancestorRemoved(AncestorEvent event) {
			auto();
		}

		@Override
		public void ancestorMoved(AncestorEvent event) {
			auto();
		}
	}

	public class Worker extends Thread {

		private static final int UPDATE_DELAY = 100;
		private JWorking jWorking;

		public Worker(final JWorking jWorking) {
			this.jWorking = jWorking;
			currentLoadingImage = 0;
		}

		@Override
		public void run() {
			while (true) {
				currentLoadingImage++;
				if (currentLoadingImage >= IMG_FRAMES) {
					currentLoadingImage = 0;
				}
				jWorking.repaint();
				try {
					Thread.sleep(UPDATE_DELAY);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

}
