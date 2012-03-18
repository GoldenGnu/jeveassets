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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.gui.images.Images;


public class JWorking extends JPanel {

	private static final int IMG_FRAMES = 24;
	private static final int IMG_WIDTH = 32;
	private static final int IMG_HEIGHT = 32;

	private static BufferedImage[] loadingImages;
	private int currentLoadingImage = 0;

	public JWorking() {
		this.setMinimumSize( new Dimension(IMG_WIDTH, IMG_HEIGHT) );
		this.setPreferredSize( new Dimension(IMG_WIDTH, IMG_HEIGHT) );
		this.setMaximumSize( new Dimension(IMG_WIDTH, IMG_HEIGHT) );
		this.setDoubleBuffered(true);
		loadingImages = new BufferedImage[IMG_FRAMES];
		for (int a = 0; a < IMG_FRAMES; a++){
			String number;
			if ((a+1)<10){
				number = "0"+(a+1);
			} else {
				number = ""+(a+1);
			}
			loadingImages[a] = Images.getBufferedImage("working"+number+".png");
		}
		Worker worker = new Worker(this);
		worker.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		if (loadingImages[currentLoadingImage] != null){
			g2.drawImage(loadingImages[currentLoadingImage], 0, 0, null);
		}
	}

	public class Worker extends Thread{
		
		private static final int updateDelay = 100;
		private JWorking working;

		public Worker(JWorking working) {
			this.working = working;
		}

		@Override
		public void run(){
			while (true){
				currentLoadingImage++;
				if (currentLoadingImage >= IMG_FRAMES) currentLoadingImage = 0;
				working.repaint();
				try {
					Thread.sleep(updateDelay);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

}
