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

package net.nikr.eve.jeveasset;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NikrUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(NikrUncaughtExceptionHandler.class);
	private static final String SUBMIT = "http://eve.nikr.net/jeveassets/bugs/submit.php";

	private static boolean error = false;

	public static void install() {
		System.setProperty("sun.awt.exception.handler", NikrUncaughtExceptionHandler.class.getName());
		Thread.setDefaultUncaughtExceptionHandler(new NikrUncaughtExceptionHandler());
	}

	private NikrUncaughtExceptionHandler() {
	}

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

			if (t instanceof OutOfMemoryError) {
				JOptionPane.showMessageDialog(null,
						"Java have run out of memory\r\n"
						+ "\r\n"
						+ "To avoid this error:\r\n"
						+ "Use jmemory.jar to run.\r\n"
						+ "\r\n"
						,
						"Critical Error", JOptionPane.ERROR_MESSAGE);
			} else {
				int value = JOptionPane.showConfirmDialog(null,
						"Send bug report?\r\n"
						+ "\r\n"
						+ "Data send and saved:\r\n"
						+ "-OS (name and version)\r\n"
						+ "-Java (vendor and version)\r\n"
						+ "-Program (name and version)\r\n"
						+ "-Date (current)\r\n"
						+ "-Java stack trace (bug)\r\n"
						+ "\r\n"
						+ "\r\n",
						"Critical Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
				if (value == JOptionPane.OK_OPTION) {
					String result = send(t);
					JOptionPane.showMessageDialog(null, result, "Bug Report", JOptionPane.PLAIN_MESSAGE);
				}
			}
			System.exit(-1);
		}
	}

	private String send(Throwable t) {
		HttpURLConnection connection = null;
		try {
			String urlParameters
					= "os=" + URLEncoder.encode(System.getProperty("os.name") + " " + System.getProperty("os.version"), "UTF-8")
					+ "&java=" + URLEncoder.encode(System.getProperty("java.vendor") + " " + System.getProperty("java.version"), "UTF-8")
					+ "&version=" + URLEncoder.encode(Program.PROGRAM_NAME + " " + Program.PROGRAM_VERSION, "UTF-8")
					+ "&log=" + URLEncoder.encode(getStackTrace(t), "UTF-8");
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
			if (!bugID.trim().equals("0") && !bugID.trim().isEmpty() ) {
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
