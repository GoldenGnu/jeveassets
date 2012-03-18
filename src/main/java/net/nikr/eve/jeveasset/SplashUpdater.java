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

package net.nikr.eve.jeveasset;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SplashUpdater extends Thread{
	private final static Logger LOG = LoggerFactory.getLogger(SplashUpdater.class);

	private static int nProgress = 0;
	private static int nSubProgress = 0;
	private static String sText = "";
	private static int currentLoadingImage = 0;
	private static BufferedImage[] loadingImages;
	private static SplashScreen splash;
	private static final int updateDelay = 200;

	/** Creates a new instance of SplashUpdater (Should never be called, as all functions are static) */
	public SplashUpdater() {
		splash = SplashScreen.getSplashScreen();
		loadingImages = new BufferedImage[8];
		for (int a = 0; a < 8; a++){
			try {
				loadingImages[a] = ImageIO.read(getClass().getResource("gui/images/loading0"+(a+1)+".png"));
			} catch (IOException ex) {
				LOG.warn("SplashScreen: loading0{}.png (NOT FOUND)", (a+1));
			}
		}
		
	}



	@Override
	public void run(){
		while (splash != null && splash.isVisible()){
			currentLoadingImage++;
			if (currentLoadingImage >= 8) currentLoadingImage = 0;
			update();
			try {
				Thread.sleep(updateDelay);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	/**
	 * Set splash screen text
	 * @param s	 String to show on splash screen
	 */
	public static void setText(String s){
		sText = s;
	}

	public static void setSubProgress(int n) {
		if (n >= 100) n = 0;
		nSubProgress = n;
	}

	/**
	 * Set progress of splash screen progressbar in the range 0-100
	 * @param n	 Set progress in the range 0-100
	 */
	public static void setProgress(int n){
		if (n > 100) n = 100;
		nProgress = n;
	}

	private static void update(){
		if (splash != null){
			try {
				Graphics2D g = splash.createGraphics();
				//Clear Screen
				g.setComposite(AlphaComposite.Clear);
				Dimension size = splash.getSize();
				g.fillRect(0, 0, size.width, size.height);
				g.setPaintMode();
				if (Program.isDebug()){
					g.setColor(Color.GRAY);
					g.drawString("DEBUG", 344, 232);
					g.setColor(Color.WHITE);
					g.drawString("DEBUG", 343, 231);
				}
				if (!sText.equals("")){
					g.setColor(Color.BLACK);
					g.fillRect(0, 235, 90, 24);
					g.setColor(Color.WHITE);
					g.drawString(sText, 5, 252);
				}
				g.setColor(Color.WHITE);
				g.fillRect(106, 242, (int) (nProgress*2.6), 12);
				if (nSubProgress > 0){
					g.setColor(Color.LIGHT_GRAY);
					g.fillRect(106, 248, (int) (nSubProgress*2.6), 6);
				}
				if (loadingImages[currentLoadingImage] != null) g.drawImage(loadingImages[currentLoadingImage], 368, 238, null);
				splash.update();
			} catch (IllegalStateException ex) {
				LOG.info("SplashScreen: Closed before painting ended (NO PROBLEM)");
			}
		}
	}
}
