/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

import java.awt.Desktop;
import java.awt.IllegalComponentStateException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.io.online.Updater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NikrUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(NikrUncaughtExceptionHandler.class);
	private static final String SUBMIT = "https://eve.nikr.net/jeveassets/bugs/submit.php";
	private static final String JAVA = "Java 8";

	private static boolean error = false;

	public static void install() {
		System.setProperty("sun.awt.exception.handler", NikrUncaughtExceptionHandler.class.getName());
		Thread.setDefaultUncaughtExceptionHandler(new NikrUncaughtExceptionHandler());
	}

	private NikrUncaughtExceptionHandler() { }

	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		reportError("Thread", e);
	}

	public void handle(final Throwable t) {
		reportError("AWT", t);
	}

	private void reportError(String s, Throwable t) {
		if (!error) {
			error = true;
			LOG.error("Uncaught Exception (" + s + "): " + t.getMessage(), t);
			//Get root cause
			Set<Class<?>> causes = new HashSet<>();
			Throwable cause = t;
			while (cause != null) {
				causes.add(cause.getClass());
				cause = cause.getCause(); //Next or null
			}
			if (causes.contains(IllegalComponentStateException.class)
					&& t.getMessage().toLowerCase().contains("component must be showing on the screen to determine its location")
					) { //XXX - Workaround for Java bug: https://bugs.openjdk.java.net/browse/JDK-8179665 (Ignore error)
				LOG.warn("Ignoring: component must be showing on the screen to determine its location");
				error = false;
				return;
			} else if (causes.contains(UnsupportedClassVersionError.class)) { //Old Java
				JOptionPane.showMessageDialog(Main.getTop(),
						"Please update Java to the latest version.\r\n"
						+ "The minimum supported version is " + JAVA + "\r\n"
						+ "\r\n"
						+ "Press OK to close jEveAssets"
						+ "\r\n"
						+ "\r\n"
						, Program.PROGRAM_NAME + " - Critical Error", JOptionPane.ERROR_MESSAGE);
			} else if (causes.contains(OutOfMemoryError.class)) { //Out of memory
				int value = JOptionPane.showConfirmDialog(Main.getTop(),
						"Java has run out of memory. jEveAssets will now close\r\n"
						+ "Do you want to browse to the wiki article explaining how to fix this?\r\n"
						+ "\r\n"
						, Program.PROGRAM_NAME + " - Critical Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
				if (value == JOptionPane.OK_OPTION) {
					try {
						Desktop.getDesktop().browse(new URI("https://jeveassets.nikr.net/jmemory"));
					} catch (Throwable ex) {
						//We tried our best, nothing more to do now...
					}
				}
			} else if (causes.contains(NoClassDefFoundError.class) || causes.contains(ClassNotFoundException.class)) { //Corrupted class files 
				try {
					Updater updater = new Updater();
					updater.fixMissingClasses();
				} catch (Throwable ex) { //Better safe than sorry...
					JOptionPane.showMessageDialog(Main.getTop(),
							"Please, re-download jEveAssets and leave the unzipped directory intact\r\n"
							+ "Press OK to close jEveAssets"
							, Program.PROGRAM_NAME + " - Critical Error", JOptionPane.ERROR_MESSAGE);
				}
			} else if (causes.contains(UnsatisfiedLinkError.class) && t.getMessage().contains("splashscreen")) { //Headless Java
				System.err.println("ERROR: Your version of java does not support a GUI");
				System.err.println("       Please, install in non-headless version of " + JAVA + " (or later) to run jEveAssets");
				System.out.println("ERROR: Your version of java does not support a GUI");
				System.out.println("       Please, install in non-headless version of " + JAVA + " (or later) to run jEveAssets");
				try {
					JOptionPane.showMessageDialog(Main.getTop(), "Your version of java does not support a GUI\r\n"
							+ "Please, install in non-headless version of " + JAVA + " (or later) to run jEveAssets",
							Program.PROGRAM_NAME + " - Critical Error",
							JOptionPane.ERROR_MESSAGE);
				} catch (Throwable ex) {
					//We tried our best, nothing more to do now...
				}
			} else { //Bug
				if (isJavaBug(t)) { //Java Bug
					JOptionPane.showMessageDialog(Main.getTop(),
						"You have encountered a bug that is most likely a java bug.\r\n"
						+ "Updating to the latest version of java may fix this problem.\r\n"
						+ "It's still very helpful to send the the bug report.\r\n"
						+ "\r\n"
						+ "Press OK to continue\r\n"
						+ "\r\n"
						+ "\r\n"
						, Program.PROGRAM_NAME + " - Critical Error", JOptionPane.ERROR_MESSAGE);
				}
				int value = JOptionPane.showConfirmDialog(Main.getTop(),
						"Send bug report?\r\n"
						+ "\r\n"
						+ "Data send and saved:\r\n"
						+ "-OS (name and version)\r\n"
						+ "-Java (vendor and version)\r\n"
						+ "-Program (name and version)\r\n"
						+ "-Date (current)\r\n"
						+ "-Java stack trace (bug)\r\n"
						+ "\r\n"
						+ "\r\n"
						, Program.PROGRAM_NAME + " - Critical Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
				if (value == JOptionPane.OK_OPTION) {
					String result = send(t);
					JOptionPane.showMessageDialog(Main.getTop(), result, "Bug Report", JOptionPane.PLAIN_MESSAGE);
				}
			}
			System.exit(-1);
		}
	}

	private static boolean isJavaBug(Throwable t) {
		for (StackTraceElement stackTraceElement : t.getStackTrace()) {
			if (stackTraceElement.getClassName().startsWith("net.nikr")) {
				return false;
			}
		}
		return true;
	}

	private String send(Throwable t) {
		return send(getStackTrace(t));
	}

	public static String send(String bug) {
		HttpURLConnection connection = null;
		try {
			String urlParameters
					= "os=" + URLEncoder.encode(System.getProperty("os.name") + " " + System.getProperty("os.version"), "UTF-8")
					+ "&java=" + URLEncoder.encode(System.getProperty("java.vendor") + " " + System.getProperty("java.version"), "UTF-8")
					+ "&version=" + URLEncoder.encode(Program.PROGRAM_NAME + " " + Program.PROGRAM_VERSION, "UTF-8")
					+ "&log=" + URLEncoder.encode(bug, "UTF-8");
			URL url = new URL(SUBMIT);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuilder response = new StringBuilder();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			String bugID = response.toString();
			if (!bugID.trim().equals("0") && !bugID.trim().isEmpty()) {
				return "Bug report send. Thank you very much!\r\n"
						+ "\r\n"
						+ "BugID: " + bugID;
			}
		} catch (MalformedURLException ex) {
			LOG.error(ex.getMessage(), ex);
		} catch (IOException ex) {
			LOG.error(ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return "Failed to submit bug report...";
	}

	private String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		if (t != null) {
			t.printStackTrace(pw);
		}
		return sw.toString(); // stack trace as a string
	}
}
