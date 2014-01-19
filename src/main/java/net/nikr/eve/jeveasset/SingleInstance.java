/*
 * Copyright 2009-2014 Contributors (see credits.txt)
 * 
 * Original code from: http://ganeshtiwaridotcomdotnp.blogspot.dk/2012/01/java-single-instance-of-application.html
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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.i18n.General;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SingleInstance {
	private static final Logger LOG = LoggerFactory.getLogger(SingleInstance.class);

	private final String HOST = "127.0.0.1";
	private final int PORT = 2222;
	private boolean msgShown = false;
	private DetectForNew thread;

	public SingleInstance() {
		// try to connect to server
		test();
		// start detecting server thread
		thread = new DetectForNew();
		thread.start();
	}

	public boolean isSingleInstance() {
		return thread.isSingleInstance() && !msgShown;
	}

	private void test() {
		if (!msgShown && findExisting()) {
			msgShown = true;
			int value = JOptionPane.showConfirmDialog(null, General.get().singleInstanceMsg(), General.get().singleInstanceTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (value != JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}

	private boolean findExisting() {
		LOG.info("Check for existing instances");
		try {
			Socket client = new Socket(HOST, PORT);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	class DetectForNew extends Thread {

		private ServerSocket serverSocket;
		private boolean singleInstance = true;

		public DetectForNew() {
			createServerSocket();
		}

		public boolean isSingleInstance() {
			return singleInstance && serverSocket != null;
		}

		@Override
		public void run() {
			while (true) {
				if (serverSocket == null && !findExisting()) {
					createServerSocket();
				}
				try {
					if (serverSocket != null) {
						serverSocket.accept();
						singleInstance = false;
					} else {
						Thread.sleep(2000); //Wait a bit and try again...
					}
				} catch (IOException ex) {
					//Ignore IO error
				} catch (InterruptedException ex) {
					//Ignore interrupt error
				}
			}
		}

		private void createServerSocket() {
			LOG.info("Creating instance blocker");
			try {
				serverSocket = new ServerSocket(PORT);
			} catch (IOException ex) {
				test();
			}
		}
	}
}