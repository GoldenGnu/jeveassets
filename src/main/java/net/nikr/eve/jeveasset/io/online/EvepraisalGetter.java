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
package net.nikr.eve.jeveasset.io.online;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import net.nikr.eve.jeveasset.data.sde.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EvepraisalGetter {

	private static final Logger LOG = LoggerFactory.getLogger(EvepraisalGetter.class);

	public static String post(Map<Item, Long> itemCounts) {
		DecimalFormat number  = new DecimalFormat("0");
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<Item, Long> entry : itemCounts.entrySet()) {
			builder.append(entry.getKey().getTypeName());
			builder.append(" ");
			builder.append(number.format(entry.getValue()));
			builder.append("\r\n");
		}
		StringBuilder urlParameters = new StringBuilder();
		//market=jita&raw_textarea=avatar&persist=no
		urlParameters.append("market=");
		urlParameters.append(encode("jita"));
		urlParameters.append("&raw_textarea=");
		urlParameters.append(encode(builder.toString()));
		try {
			URL obj = new URL("https://evepraisal.com/appraisal.json");
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// add request header
			con.setRequestMethod("POST");
			con.setConnectTimeout(10000);
			con.setReadTimeout(10000);

			// Send post request
			con.setDoOutput(true);
			try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
				wr.writeBytes(urlParameters.toString());
				wr.flush();
			}

			StringBuilder response;
			try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
				String inputLine;
				response = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			}
			// read json
			Gson gson = new GsonBuilder().create();
			Result result = gson.fromJson(response.toString(), Result.class);
			return result.getID();
			// set data
		} catch (IOException | JsonParseException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return null;
	}

	private static String encode(String parameter) {
		try {
			return URLEncoder.encode(parameter, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	public static class Appraisal {
		private String id;

		public String getID() {
			return id;
		}
	}

	public static class Result {

		private Appraisal appraisal;

		public Appraisal getAppraisal() {
			return appraisal;
		}

		public String getID() {
			return appraisal.getID();
		}
	}
}
