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
package net.nikr.eve.jeveasset.io.esi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MicroServe implements AuthCodeListener {

	private static final Logger LOG = LoggerFactory.getLogger(MicroServe.class);

	private static final Object LOCK = new Object();
	private boolean serverStarted;
	private String authCode;
	private boolean listen = false;

	public MicroServe() { }

	public void startServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(2221);
			ConnectionListener connectionListener = new ConnectionListener(serverSocket, this);
			connectionListener.start();
			serverStarted = true;
		} catch (IOException ex) {
			LOG.error(ex.getMessage(), ex);
			serverStarted = false;
		}
	}

	public void stopListening() {
		authCode = null;
		listen = false;
	}

	public void startListening() {
		listen = true;
	}

	public boolean isServerStarted() {
		return serverStarted;
	}

	public String getAuthCode() {
		synchronized(LOCK) {
			try {
				LOCK.wait();
			} catch (InterruptedException ex) {
				//No problem
			}
		}
		return authCode;
	}

	@Override
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	@Override
	public synchronized boolean isListening() {
		return listen;
	}

	private static class ConnectionListener extends Thread {

		private final ServerSocket serverSocket;
		private final AuthCodeListener listener;

		public ConnectionListener(ServerSocket serverSocket, AuthCodeListener listener) {
			this.serverSocket = serverSocket;
			this.listener = listener;
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					Socket clientSocket = serverSocket.accept();
					ConnectionResponse response = new ConnectionResponse(clientSocket, listener);
					response.start();
				} catch (IOException ex) {
					LOG.error(ex.getMessage(), ex);
				}
			}
		}
		
	}

	private static class ConnectionResponse extends Thread {

		private final Socket clientSocket;
		private final AuthCodeListener listener;

		public ConnectionResponse(Socket clientSocket, AuthCodeListener listener) {
			this.clientSocket = clientSocket;
			this.listener = listener;
		}

		@Override
		public void run() {
			boolean found = false;
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				OutputStream out = clientSocket.getOutputStream();

				String s;
				while ((s = in.readLine()) != null) {
					if (s.startsWith("GET") && s.contains("code=")) {
						String temp = s;
						int start = temp.indexOf("code=");
						temp = temp.substring(start + 5);
						int end = temp.indexOf("&");
						temp = temp.substring(0 , end);
						listener.setAuthCode(temp);
						found = true;
					}
					if (s.isEmpty()) {
						break;
					}
				}
				String statusLine = "HTTP/1.1 303 Moved Permanently\r\n";
				out.write(statusLine.getBytes("ASCII"));

				String location;
				if (listener.isListening()) {
					if (found) {
						location = "Location: http://eve.nikr.net/jeveasset/auth-ok\r\n";
					} else {
						location  = "Location: http://eve.nikr.net/jeveasset/auth-failed\r\n";
					}
				} else {
					location  = "Location: http://eve.nikr.net/jeveasset/auth-cancel\r\n";
				}
				
				out.write(location.getBytes("ASCII"));

				// signal end of headers
				out.write("\r\n".getBytes("ASCII"));

				out.flush();
			} catch (IOException ex) {
				LOG.error(ex.getMessage(), ex);
			} finally {
				if (in != null) {
					try {
						in.close(); //Close BufferedReader
					} catch (IOException ex) {
						//That is okay
					}
				} 
				if (clientSocket != null) {
					try {
						clientSocket.close(); //Close connection
					} catch (IOException ex) {
						//That is okay
					}
				}
				synchronized(LOCK) {
					LOCK.notifyAll();
				}
			}
		}
	}
}
