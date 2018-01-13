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

package net.nikr.eve.jeveasset.io.online;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.MalformedInputException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DataGetter {

	private static final Logger LOG = Logger.getLogger(DataGetter.class.getName());

	public boolean get(String link, File out, String checksum) {
		return get(link, out, checksum, 0);
	}

	private boolean get(String link, File out, String checksum, int tries) {
		LOG.info("Downloading: " + link + " to: " + out.getAbsolutePath());
		DigestInputStream input = null;
		FileOutputStream output = null;
		int n;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			URL url = new URL(link);
			URLConnection con = url.openConnection();
			
			byte[] buffer = new byte[4096];
			input = new DigestInputStream(con.getInputStream(), md);
			output = new FileOutputStream(out);
			while ((n = input.read(buffer)) != -1) {
				output.write(buffer, 0, n);
			}
			output.flush();
			String sum = getToHex(md.digest());
			if (sum.equals(checksum)) {
				return true; //OK
			}
		} catch (MalformedInputException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		} catch (NoSuchAlgorithmException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException ex) {
					LOG.log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException ex) {
					LOG.log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
		}
		if (tries < 10){ //Retry 10 times
			out.delete();
			tries++;
			return get(link, out, checksum, tries);
		} else { //Failed 10 times, I give up...
			return false;
		}
	}

	private String getToHex(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
}
