/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import static net.nikr.eve.jeveasset.Program.PROGRAM_NAME;
import static net.nikr.eve.jeveasset.Program.PROGRAM_VERSION;
import net.nikr.eve.jeveasset.io.online.Updater;
import net.nikr.eve.jeveasset.io.shared.FileLock;
import net.nikr.eve.jeveasset.io.shared.FileUtil;


public final class Main {
	private static boolean debug = false;
	private static boolean portable = false;
	private static boolean forceNoUpdate = false;
	private static boolean forceUpdate = false;
	private static boolean lazySave = false;

	private static Logger log;

	private Main() {
		//Validate directory
		checkLibs();
		//install the uncaught exception handlers
		NikrUncaughtExceptionHandler.install();
		//Splash screen
		SplashUpdater splashUpdater = new SplashUpdater();
		splashUpdater.start();
		//Print program data
		log.info("Starting " + PROGRAM_NAME + " " +PROGRAM_VERSION);
		log.info("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
		log.info("Java: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version"));
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

	/**
	 * Entry point for jEveAssets.
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
		}

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
		System.setProperty("java.util.logging.SimpleFormatter.format", " %4$s: %2$s - %5$s%n");
		log = Logger.getLogger(Main.class.getName());
		//Add user agent to online requests
		System.setProperty("http.agent", Program.PROGRAM_NAME + "/" + Program.PROGRAM_VERSION.replace(" ", "_"));

		//XXX - Workaround for IPv6 fail (force IPv4)
		//eveonline.com is not IPv6 ready...
		System.setProperty("java.net.preferIPv4Stack" , "true");

		//XXX - Workaround for Java Bug
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

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
		} catch (ClassNotFoundException ex) {
			log.log(Level.SEVERE, "Failed to set LookAndFeel: " + lookAndFeel, ex);
		} catch (InstantiationException ex) {
			log.log(Level.SEVERE, "Failed to set LookAndFeel: " + lookAndFeel, ex);
		} catch (IllegalAccessException ex) {
			log.log(Level.SEVERE, "Failed to set LookAndFeel: " + lookAndFeel, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			log.log(Level.SEVERE, "Failed to set LookAndFeel: " + lookAndFeel, ex);
		}
	}

	private static void checkLibs() {
		File jar = new File(FileUtil.getPathRunJar());
		boolean temp = false;
		//Check if trying to run from inside zip file (Windows only)
		if (jar.getAbsolutePath().contains(".zip") && jar.getAbsolutePath().contains(System.getProperty("java.io.tmpdir")) && System.getProperty("os.name").startsWith("Windows")) {
			temp = true;
		}
		boolean missing = false;
		//Check if all lirbaries are pressent
		for (String filename : getLibFiles()) {
			File file = new File(FileUtil.getPathLib(filename));
			if (!file.exists()) {
				missing = true;
				break;
			}
		}
		if (temp && missing) { //Running from zip file...
			JOptionPane.showMessageDialog(null, "You need to unzip jEveAssets to run it\r\nIt will not work from inside the zip file", "Critical Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		} else if (missing) { //Missing lirbaries
			Updater updater = new Updater();
			updater.fixLibs();
		}
	}

	public static Set<String> getLibFiles() {
		Set<String> files = new HashSet<String>();
		files.add("asm-5.0.4.jar");
		files.add("dom4j-1.6.1.jar");
		files.add("eveapi-6.0.6.jar");
		files.add("glazedlists_java15-1.9.1.jar");
		files.add("graph-1.5.0.jar");
		files.add("guava-r09.jar");
		files.add("jaxen-1.1.6.jar");
		files.add("guava-r09.jar");
		files.add("LGoodDatePicker-7.6.3.jar");
		files.add("jcl-over-slf4j-1.6.1.jar");
		files.add("jcommon-1.0.16.jar");
		files.add("jfreechart-1.0.13.jar");
		files.add("log4j-1.2.16.jar");
		files.add("osxadapter-1.1.0.jar");
		files.add("pricing-1.5.1.jar");
		files.add("routing-1.5.0.jar");
		files.add("slf4j-api-1.6.1.jar");
		files.add("slf4j-log4j12-1.6.1.jar");
		files.add("supercsv-1.52.jar");
		files.add("translations-2.2.0.jar");
		files.add("jul-to-slf4j-1.6.1.jar");
		return files;
	}
}