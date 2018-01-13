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
package net.nikr.eve.jeveasset.data.settings;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProxyData {

	private final String address;
	private final Proxy.Type type;
	private final int port;
	private final String username;
	private final String password;

	public ProxyData() {
		this.address = "";
		this.type = Proxy.Type.DIRECT;
		this.port = 0;
		this.username = null;
		this.password = null;
		updateProxy();
	}

	public ProxyData(String address, Proxy.Type type, int port, String username, String password) {
		this.address = address;
		this.type = type;
		this.port = port;
		this.username = username;
		this.password = password;
		updateProxy();
	}

	public boolean isAuth() {
		return username != null && password != null;
	}

	private void updateProxy() {
		//Reset everything
		Authenticator.setDefault(null); //Auth
		ProxySelector.setDefault(null); //Proxy
		//Host
		System.clearProperty("http.proxyHost"); //http
		System.clearProperty("https.proxyHost");//https
		System.clearProperty("socksProxyHost"); //socks
		//Port
		System.clearProperty("http.proxyPort"); //http
		System.clearProperty("https.proxyPort");//https
		System.clearProperty("socksProxyPort"); //socks
		//Username
		System.clearProperty("http.proxyUser"); //http
		System.clearProperty("https.proxyUser");//https
		System.clearProperty("java.net.socks.username"); //socks
		//Password
		System.clearProperty("http.proxyPassword"); //http
		System.clearProperty("https.proxyPassword");//https
		System.clearProperty("java.net.socks.password"); //socks
		if (type == Proxy.Type.HTTP) { //HTTP/HTTPS Proxy
			//Proxy
			ProxySelector.setDefault(new ProxyHost(getProxy()));
			//Address
			System.setProperty("http.proxyHost", getAddress());
			System.setProperty("https.proxyHost", getAddress());
			//Port
			System.setProperty("http.proxyPort", String.valueOf(getPort()));
			System.setProperty("https.proxyPort", String.valueOf(getPort()));
			if (isAuth()) {
				//Username
				System.setProperty("http.proxyUser", getUsername());
				System.setProperty("https.proxyUser", getUsername());
				//Password
				System.setProperty("http.proxyPassword", getPassword());
				System.setProperty("https.proxyPassword", getPassword());
				//Auth
				Authenticator.setDefault(new ProxyAuth(getUsername(), getPassword()));
			}
		}
		if (type == Proxy.Type.SOCKS) { //SOCKS Proxy
			//Proxy
			ProxySelector.setDefault(new ProxyHost(getProxy()));
			//Address
			System.setProperty("socksProxyHost", getAddress());
			//Port
			System.setProperty("socksProxyPort", String.valueOf(getPort()));
			if (isAuth()) {
				//Username
				System.setProperty("java.net.socks.username", getUsername());
				//Password
				System.setProperty("java.net.socks.password", getPassword());
				//Auth
				Authenticator.setDefault(new ProxyAuth(getUsername(), getPassword()));
			}
		}
	}

	private Proxy getProxy() {
		if (type == Proxy.Type.DIRECT) {
			return Proxy.NO_PROXY;
		} else {
			return new Proxy(type, new InetSocketAddress(address, port));
		}
	}

	public String getAddress() {
		return address;
	}

	public Proxy.Type getType() {
		return type;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public List<String> getArgs() {
		List<String> args = new ArrayList<String>();
		if (type == Proxy.Type.HTTP) { // HTTP/HTTPS Proxy
			//Address
			args.add(format("https.proxyHost", getAddress()));
			//Port
			args.add(format("https.proxyPort", String.valueOf(getPort())));
			if (isAuth()) {
				//Username
				args.add(format("https.proxyUser", getUsername()));
				//Password
				args.add(format("https.proxyPassword", getPassword()));
			}
		}
		//SOCKS Proxy
		if (type == Proxy.Type.SOCKS) {
			//Address
			args.add(format("socksProxyHost", getAddress()));
			//Port
			args.add(format("socksProxyPort", String.valueOf(getPort())));
			if (isAuth()) {
				//Username
				args.add(format("java.net.socks.username", getUsername()));
				//Password
				args.add(format("java.net.socks.password", getPassword()));
				//Auth
				Authenticator.setDefault(new ProxyAuth(getUsername(), getPassword()));
			}
		}
		return args;
	}

	private String format(String key, String value) {
		return "-D" + key + "=" + value;
	}

	public static class ProxyAuth extends Authenticator {

		private final PasswordAuthentication auth;

		private ProxyAuth(String user, String password) {
			auth = new PasswordAuthentication(user, password == null ? new char[]{} : password.toCharArray());
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return auth;
		}
	}

	public static class ProxyHost extends ProxySelector {

		private final Proxy proxy;

		public ProxyHost(Proxy proxy) {
			this.proxy = proxy;
		}

		@Override
		public List<Proxy> select(URI uri) {
			return Collections.singletonList(proxy);
		}

		@Override
		public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

		}

	}

}
