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

package net.nikr.eve.jeveasset;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SplashUpdater {
	private static final Logger LOG = LoggerFactory.getLogger(SplashUpdater.class);

	private static int progress = 0;
	private static int subProgress = 0;
	private static String text = "";
	private static int currentLoadingImage = 0;
	private static BufferedImage[] loadingImages;
	private static SplashScreen splashScreen;
	private static JWindow splashWindow;
	private static Canvas splashWindowCanvas;
	private static final int UPDATE_DELAY = 200;
	private static final Object PAINT_LOCK = new Object();
	private static BufferedImage splashImage;
	private static boolean showSplashWindow = true;
	private static long delta = 0;
	private static Long lastPaint = null;

	/** Creates a new instance of SplashUpdater. */
	public SplashUpdater() {
		splashScreen = SplashScreen.getSplashScreen();
		loadingImages = new BufferedImage[8];
		for (int i = 0; i < 8; i++) {
			try {
				loadingImages[i] = ImageIO.read(getClass().getResource("gui/images/loading0" + (i + 1) + ".png"));
			} catch (IOException ex) {
				LOG.warn("SplashScreen: loading0{}.png (NOT FOUND)", (i + 1));
			}
		}
		try {
			splashImage = ImageIO.read(getClass().getResource("/splash.jpg"));
		} catch (IOException | IllegalArgumentException ex) {
			splashImage = null;
		}
		splashWindow = new JWindow();
		splashWindowCanvas = new Canvas();
		splashWindowCanvas.setIgnoreRepaint(true);
		splashWindowCanvas.setFocusable(false);
		splashWindowCanvas.setFont(new Font("Dialog.plain", 0, 12));
		splashWindow.add(splashWindowCanvas);
		if (splashScreen != null) {
			splashWindow.setBounds(splashScreen.getBounds());
		} else if (splashImage != null) {
			splashWindow.setSize(splashImage.getWidth(), splashImage.getHeight());
		}
		splashWindow.setLocationRelativeTo(null);
	}

	public static void hide() {
		showSplashWindow = false;
		splashWindow.setVisible(false);
	}

	public void start() {
		Animator animator = new Animator();
		animator.start();
		Paineter paineter = new Paineter();
		paineter.start();
	}

	public synchronized static void nextLoadingImage() {
		currentLoadingImage++;
		if (currentLoadingImage >= 8) {
			currentLoadingImage = 0;
		}
	}

	/**
	 * Set splash screen text.
	 * @param s	 String to show on splash screen
	 */
	public synchronized static void setText(final String s) {
		text = s;
	}

	/**
	 * Set subprogress of splash screen progressbar in the range 0-100.
	 * @param n	 Set progress in the range 0-100
	 */
	public synchronized static void setSubProgress(final int n) {
		int number = n;
		if (number >= 100) {
			number = 0;
		}
		if (number < 0) {
			number = 0;
		}
		if (subProgress != number) {
			if ((number > subProgress || number == 0)) {
				subProgress = number;
				update(true);
			}
		}
	}

	/**
	 * Set progress of splash screen progressbar in the range 0-100.
	 * @param n	 Set progress in the range 0-100
	 */
	public synchronized static void setProgress(final int n) {
		int number = n;
		if (number > 100) {
			number = 100;
		}
		if (number < 0) {
			number = 0;
		}
		if (progress != number) {
			if (number > progress) {
				progress = number;
				update(false);
			}
		}
	}

	private static void update(boolean sub) {
		if (isVisible()) {
			synchronized (PAINT_LOCK) {
				PAINT_LOCK.notify();
			}
		} else if (SwingUtilities.isEventDispatchThread()) {
			showSplashWindow();
			paintSplashWindow(sub);
		}
	}

	private static void showSplashWindow() {
		if (showSplashWindow && splashWindow != null && !splashWindow.isVisible()) {
			splashWindow.setVisible(true);
			splashWindow.toFront();
		}
	}

	private static void paintSplashWindow(boolean sub) {
		synchronized (PAINT_LOCK) {
			if (splashWindow != null && splashWindow.isVisible()) {
				if (lastPaint != null) {
					delta = delta + (System.currentTimeMillis() - lastPaint);
				}
				boolean paint = sub || (lastPaint == null || (System.currentTimeMillis() - lastPaint) > 16);
				if (delta > UPDATE_DELAY) {
					nextLoadingImage();
					delta = 0;
					paint = true;
				}
				if (paint) {
					lastPaint = System.currentTimeMillis();
					nextLoadingImage();
					BufferStrategy bufferStrategy = splashWindowCanvas.getBufferStrategy();
					if (bufferStrategy == null) {
						splashWindowCanvas.createBufferStrategy(2);
						bufferStrategy = splashWindowCanvas.getBufferStrategy();
					}
					Graphics g = bufferStrategy.getDrawGraphics();
					g.drawImage(splashImage, 0, 0, null);
					paint((Graphics2D) g);
					g.dispose();
					splashWindowCanvas.getBufferStrategy().show();
				}
			}
		}
	}

	private static void paintSplashScreen() {
		if (isVisible()) {
			try {
				if (splashScreen != null) {
					Graphics2D g = splashScreen.createGraphics();
					if (g != null) {
						g.setComposite(AlphaComposite.Clear);
						Dimension size = splashScreen.getSize();
						g.fillRect(0, 0, size.width, size.height);
						g.setPaintMode();
						paint(g);
						splashScreen.update();
					}
				}
			} catch (IllegalStateException ex) {
				LOG.info("SplashScreen: Closed before painting ended (NO PROBLEM)");
			}
		}
	}

	private static void paint(final Graphics2D g) throws IllegalStateException {
		//Clear Screen
		if (CliOptions.get().isDebug()) {
			g.setColor(Color.DARK_GRAY);
			g.drawString("DEBUG", 344, 232);
			g.setColor(Color.WHITE);
			g.drawString("DEBUG", 343, 231);
		}
		if (!text.isEmpty()) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 235, 90, 24);
			g.setColor(Color.WHITE);
			g.drawString(text, 5, 252);
		}
		g.setColor(Color.WHITE);
		g.fillRect(106, 242, (int) (progress * 2.6), 12);
		if (subProgress > 0) {
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(106, 248, (int) (subProgress * 2.6), 6);
		}
		if (loadingImages[currentLoadingImage] != null) {
			g.drawImage(loadingImages[currentLoadingImage], 368, 238, null);
		}
	}

	private static boolean isVisible() {
		return (splashScreen != null && splashScreen.isVisible());
	}

	private class Paineter extends Thread {

		@Override
		public void run() {
			while (isVisible()) {
				synchronized (PAINT_LOCK) {
					try {
						PAINT_LOCK.wait();
					} catch (InterruptedException ex) {

					}
					paintSplashScreen();
				}
			}
		}
	}

	private class Animator extends Thread {

		@Override
		public void run() {
			while (isVisible()) {
				nextLoadingImage();
				update(false);
				synchronized (this) {
					try {
						wait(UPDATE_DELAY);
					} catch (InterruptedException ex) {
						break;
					}
				}
			}
		}
	}
}
