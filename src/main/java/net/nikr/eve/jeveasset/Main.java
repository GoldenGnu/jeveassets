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

package net.nikr.eve.jeveasset;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import com.beimin.eveapi.ApiAuth;
import com.beimin.eveapi.ApiAuthorization;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import net.nikr.log.Log;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.log4j.LogManager;
import org.supercsv.cellprocessor.constraint.Unique;


public class Main {
	static String[] args;
	Program program;
	//DataLoader dataLoader;
	/** Creates a new instance of Main */
	public Main(String[] argumentsString) {
		//Force error here, if any libraries are missing
		try {
			Digester digester = new Digester();
			BeanUtils beanUtils = new BeanUtils();
			org.apache.commons.logging.Log apacheLog = new SimpleLog("");
			LogManager logManager = new LogManager();
			ApiAuth apiAuth = new ApiAuthorization(1, 1, "");
			EventList<String> eventList = new BasicEventList<String>();
			Unique Unique = new Unique();
		} catch (NoClassDefFoundError e){
			if (e.toString().contains("supercsv")){
				Log.error("The Super CSV library is missing (lib\\SuperCSV-1.52.jar)\nPlease see readme.txt for more information.", e);
			}
			if (e.toString().contains("glazedlists")){
				Log.error("The Glazed Lists library is missing (lib\\glazedlists-1.8.0_java15.jar)\nPlease see readme.txt for more information.", e);
			}
			if (e.toString().contains("eveapi")){
				Log.error("The EVEAPI library is missing (lib\\EVEAPI.jar)\nPlease see readme.txt for more information.", e);
			}
			if (e.toString().contains("log4j")){
				Log.error("The log4j library is missing (lib\\log4j-1.2.15.jar)\nPlease see readme.txt for more information.", e);
			}
			if (e.toString().contains("logging")){
				Log.error("The Commons Logging library is missing (lib\\commons-logging-1.1.1.jar)\nPlease see readme.txt for more information.", e);
			}
			if (e.toString().contains("beanutils") || e.toString().contains("collections")){
				Log.error("The Commons BeanUtils library is missing (lib\\commons-beanutils-1.8.0.jar)\nPlease see readme.txt for more information.", e);
			}
			if (e.toString().contains("digester")){
				Log.error("The Commons Digester library is missing (lib\\lcommons-digester-2.0.jar)\nPlease see readme.txt for more information.", e);
			}
		}
		program = new Program(args);
		
	}
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
			Main.args = args;
			javax.swing.SwingUtilities.invokeLater(
				new Runnable() {
					@Override
					public void run() {
						createAndShowGUI();
							}
					}
			);
	}
	
	private static void createAndShowGUI() {
		try {
			//Force error here, if the log library is missing
			Log log = new Log();
		} catch (NoClassDefFoundError e){
			String s = "The NiKR Log library is missing (lib\\nikr_log.jar)\nPlease see readme.txt for more information.";
			System.err.println(s);
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, s, "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		Log.init(Main.class, "Please email the latest error.txt in the logs directory to niklaskr@gmail.com");

		SplashUpdater splashUpdater = new SplashUpdater();
		splashUpdater.start();

		initLookAndFeel();

		//Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		Main main = new Main(args);
	}
	
	private static void initLookAndFeel() {
		String lookAndFeel = null;
		//lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		lookAndFeel = UIManager.getSystemLookAndFeelClassName(); //System
		//lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName(); //Java
		//lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
		//lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"; //GTK
		//lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			Log.error("failed to set LookAndFeel: "+lookAndFeel, e);
			e.printStackTrace();
		}
	}


}
