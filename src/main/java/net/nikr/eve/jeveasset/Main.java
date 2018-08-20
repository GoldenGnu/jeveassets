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
package net.nikr.eve.jeveasset;

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.nikr.eve.jeveasset.io.local.FileLock;
import org.slf4j.bridge.SLF4JBridgeHandler;

public final class Main {

	private static boolean debug = false;
	private static boolean portable = false;
	private static boolean forceNoUpdate = false;
	private static boolean forceUpdate = false;
	private static boolean lazySave = false;
	private static boolean jmemory = false;

	private static Logger log;

	private Main() {
		//Validate directory
		LibraryManager.checkLibraries();
		//install the uncaught exception handlers
		NikrUncaughtExceptionHandler.install();
		//Splash screen
		SplashUpdater splashUpdater = new SplashUpdater();
		splashUpdater.start();
		//Print program data
		if (jmemory) {
			log.info("jmemory ok");
		}
		log.info("Starting " + Program.PROGRAM_NAME + " " + Program.PROGRAM_VERSION);
		log.log(Level.INFO, "OS: {0} {1}", new Object[]{System.getProperty("os.name"), System.getProperty("os.version")});
		log.log(Level.INFO, "Java: {0} {1}", new Object[]{System.getProperty("java.vendor"), System.getProperty("java.version")});
		// variables to the main program and settings.
		Program.setDebug(debug);
		Program.setPortable(portable);
		Program.setForceNoUpdate(forceNoUpdate && debug);
		Program.setForceUpdate(forceUpdate && debug);
		Program.setLazySave(lazySave);
		//Ensure only one instance is running...
		SingleInstance instance = new SingleInstance();
		if (instance.isSingleInstance()) {
			FileLock.unlockAll();
		}
		//Lets go!
		Program program = new Program();
	}

	public static boolean isJmemory() {
		return jmemory;
	}

	/**
	 * Entry point for jEveAssets.
	 *
	 * @param args the command line arguments
	 */
	public static void main(final String[] args) {
		for (String arg : args) {
			if (arg.toLowerCase().equals("-debug")) {
				debug = true;
			}
			if (arg.toLowerCase().equals("-portable")) {
				portable = true;
			}
			if (arg.toLowerCase().equals("-noupdate")) {
				forceNoUpdate = true;
			}
			if (arg.toLowerCase().equals("-update")) {
				forceUpdate = true;
			}
			if (arg.toLowerCase().equals("-lazysave")) {
				lazySave = true;
			}
			if (arg.toLowerCase().equals("-jmemory")) {
				jmemory = true;
			}
		}
		//Force UTF-8 File system
		System.setProperty("sun.jnu.encoding", "UTF-8"); 
		System.setProperty("file.encoding", "UTF-8");

		// the tests for null indicate that the property is not set
		// It is possible to set properties using the -Dlog.home=foo/bar
		// and thus we want to allow this to take priority over the
		// configuration options here.
		if (System.getProperty("log.home") == null) {
			if (portable) {
				try {
					//jeveassets.jar directory
					File file = new File(net.nikr.eve.jeveasset.Program.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
					System.setProperty("log.home", file.getAbsolutePath() + File.separator);
				} catch (URISyntaxException ex) {
					//Working directory
					System.setProperty("log.home", System.getProperty("user.dir") + File.separator); //Working directory
				}
			} else {
				//Note: We can not use Program.onMac() as that will initialize the Program LOG
				if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) { //Mac
					System.setProperty("log.home", System.getProperty("user.home") + File.separator + "Library" + File.separator + "Preferences" + File.separator + "JEveAssets" + File.separator);
				} else { //Windows/Linux
					System.setProperty("log.home", System.getProperty("user.home") + File.separator + ".jeveassets" + File.separator);
				}
			}
		}
		// ditto here.
		if (System.getProperty("log.level") == null) {
			if (debug) {
				System.setProperty("log.level", "DEBUG");
			} else {
				System.setProperty("log.level", "INFO");
			}
		}
		//Set format
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %2$s - %5$s%n");
		try {
			SLF4JBridgeHandler.install();
		} catch (Throwable t) {
			//This is ignored
		}
		log = Logger.getLogger(Main.class.getName());
		//Add user agent to online requests
		System.setProperty("http.agent", Program.PROGRAM_NAME + "/" + Program.PROGRAM_VERSION.replace(" ", "_"));

		//XXX - Workaround for IPv6 fail (force IPv4)
		//eveonline.com is not IPv6 ready...
		System.setProperty("java.net.preferIPv4Stack", "true");

		//XXX - Workaround for Java Bug
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

		//XXX - Workaround: Allow basic proxy authorization
		System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
		System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");

		javax.swing.SwingUtilities.invokeLater(
			new Runnable() {
				@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		initLookAndFeel();

		//Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		Main main = new Main();
	}

	private static void initLookAndFeel() {
		//Allow users to overwrite LaF
		if (System.getProperty("swing.defaultlaf") != null) {
			return;
		}
		String lookAndFeel;
		//lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		lookAndFeel = UIManager.getSystemLookAndFeelClassName(); //System
		//lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName(); //Java
		//lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"; //Nimbus
		//lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
		//lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"; //GTK
		//lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			log.log(Level.SEVERE, "Failed to set LookAndFeel: " + lookAndFeel, ex);
		}
	}
}
