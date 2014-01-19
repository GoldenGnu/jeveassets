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

package net.nikr.eve.jeveasset.io.online;

import java.awt.Window;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.shared.AttributeGetters;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import net.nikr.eve.jeveasset.io.shared.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class ProgramUpdateChecker {

	private static final Logger LOG = LoggerFactory.getLogger(ProgramUpdateChecker.class);

	private Program program;
	private String programDataVersion = "";
	private Version stable = new Version();
	private Version dev = new Version();

	public ProgramUpdateChecker(final Program program) {
		this.program = program;
		parseDataVersion();
		if ((Settings.get().isAutoUpdate())) {
			parseUpdateVersion();
		}
	}

	public String getProgramDataVersion() {
		int end = programDataVersion.lastIndexOf(".");
		if (programDataVersion.length() >= end) {
			return programDataVersion.substring(0, end);
		}
		return programDataVersion;
	}

	public void showDevBuildMessage() {
		Version version = getProgram();
		if (Program.PROGRAM_SHOW_FEEDBACK_MSG && (version.isDevBuild() || version.isBeta())) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(),
					GuiShared.get().feedbackMsg(Program.PROGRAM_NAME, version.getType().toLowerCase()),
					version.getType(),
					JOptionPane.WARNING_MESSAGE);
		}
		if (Program.PROGRAM_FORCE_PORTABLE) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(),
				GuiShared.get().protableMsg(Settings.get().isSettingsImported() ? 1 : 0),
				version.getType(),
				JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void showMessages() {
		showMessages(program.getMainWindow().getFrame(), false);
	}

	public void showMessages(final Window parent, final boolean requestedUpdate) {
		if (requestedUpdate) {
			parseUpdateVersion();
		}
		if (isStableUpdateAvailable()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					int value = JOptionPane.showConfirmDialog(parent,
							GuiShared.get().newVersionMsg(Program.PROGRAM_NAME),
							GuiShared.get().newVersionTitle(),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (value == JOptionPane.YES_OPTION) {
						DesktopUtil.browse(Program.PROGRAM_HOMEPAGE, program);
					}
				}
			});
		}
		if (isDevUpdateAvailable()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					int value = JOptionPane.showConfirmDialog(parent,
							GuiShared.get().newBuildMsg(dev.getType().toLowerCase(), Program.PROGRAM_NAME),
							GuiShared.get().newBuildTitle(),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (value == JOptionPane.YES_OPTION) {
						DesktopUtil.browse(Program.PROGRAM_HOMEPAGE, program);
					}
				}
			});
		}
		if (requestedUpdate && !isStableUpdateAvailable() && !isDevUpdateAvailable()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(parent,
							GuiShared.get().noNewVersionMsg(),
							GuiShared.get().noNewVersionTitle(),
							JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}
	}

	private boolean isStableUpdateAvailable() {
		return stable.isNewerThen(getProgram()) && (stable.isNewerThen(dev)	|| !Settings.get().isUpdateDev());
	}

	private boolean isDevUpdateAvailable() {
		return dev.isNewerThen(getProgram()) && dev.isNewerThen(stable) && Settings.get().isUpdateDev();
	}

	private Version getProgram() {
		return new Version(Program.PROGRAM_VERSION, programDataVersion);
	}

	private Element parse(final InputStream is) throws XmlException {
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document doc;
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
			is.close();
			return doc.getDocumentElement();
		} catch (SAXException ex) {
			throw new XmlException(ex);
		} catch (ParserConfigurationException ex) {
			throw new XmlException(ex);
		} catch (IOException ex) {
			throw new XmlException(ex);
		}
	}

	private void parseDataVersion() {
		try {
			InputStream is = new FileInputStream(Settings.getPathDataVersion());
			parseDataVersion(parse(is));
			is.close();
		} catch (IOException ex) {
			LOG.info("Failed to get data.xml information" + ex.getMessage());
		} catch (XmlException ex) {
			LOG.info("Failed to get data.xml information" + ex.getMessage());
		}
	}
	private void parseDataVersion(final Element element) {
		if (!element.getNodeName().equals("rows")) {
			LOG.info("Failed to get update information: Wrong root element name");
			return;
		}
		//row
		NodeList nodes = element.getElementsByTagName("row");
		if (nodes.getLength() == 1) {
			Element node = (Element) nodes.item(0);
			programDataVersion = AttributeGetters.getString(node, "version");
		}
	}

	private void parseUpdateVersion() {
		try {
			URL url = new URL(Program.PROGRAM_UPDATE_URL);
			URLConnection connection = url.openConnection(Settings.get().getProxy());
			connection.setConnectTimeout(10 * 1000); //10 sec
			InputStream is = connection.getInputStream();
			parseUpdate(parse(is));
		} catch (MalformedURLException ex) {
			LOG.info("Failed to get update information: " + ex.getMessage());
		} catch (IOException ex) {
			LOG.info("Failed to get update information: " + ex.getMessage());
		} catch (XmlException ex) {
			LOG.info("Failed to get update information: " + ex.getMessage());
		}
	}


	private void parseUpdate(final Element element) {
		if (!element.getNodeName().equals("update")) {
			LOG.info("Failed to get update information: Wrong root element name");
		}
		//jEveAssets
		NodeList nodes = element.getElementsByTagName("jeveassets");
		if (nodes.getLength() == 1) {
			Element node = (Element) nodes.item(0);
			parseJeveassets(node);
		}
	}

	private void parseJeveassets(final Element element) {
		stable = update(element, "stable");
		dev = update(element, "dev");
	}

	private Version update(final Element element, final String tagName) {
		NodeList nodes = element.getElementsByTagName(tagName);
		if (nodes.getLength() == 1) {
			Element node = (Element) nodes.item(0);
			String version = AttributeGetters.getString(node, "version");
			String data = AttributeGetters.getString(node, "data");
			return new Version(version, data);
		} else {
			return new Version();
		}
	}

	private static class Version {
		private static final int DEV_BUILD = 1;
		private static final int BETA = 2;
		private static final int RELEASE_CANDIDATE = 3;
		private static final int STABLE = 4;

		private static final int VERSION_MAJOR = 0;
		private static final int VERSION_MINOR = 1;
		private static final int VERSION_BUGFIX = 2;
		private static final int VERSION_TYPE = 3;
		private static final int VERSION_TYPE_NUMBER = 4;

		private int[] arrVersion;
		private String version;
		private String data;

		public Version() {
			this.arrVersion = new int[0];
			this.version = "";
			this.data = "";
		}
		public Version(final String version, final String data) {
			this.version = version;
			this.data = data;
			this.arrVersion = getVersion(version);
		}

		public int[] getArrVersion() {
			return arrVersion;
		}

		public String getData() {
			return data;
		}

		public String getVersion() {
			return version;
		}

		public boolean isDevBuild() {
			return (arrVersion[VERSION_TYPE] == DEV_BUILD);
		}

		public boolean isBeta() {
			return (arrVersion[VERSION_TYPE] == BETA);
		}

		public boolean isReleaseCandidate() {
			return (arrVersion[VERSION_TYPE] == RELEASE_CANDIDATE);
		}

		public boolean isNewerThen(final Version updateInfo) {
			int[] compateTo = updateInfo.getArrVersion();
			for (int a = 0; a < compateTo.length && a < arrVersion.length; a++) {
				if (compateTo[a] < arrVersion[a]) {
					return true; //This is newer
				} else if (compateTo[a] > arrVersion[a]) {
					return false; //This is older
				}
			} //Versions are equal
			if (!data.toLowerCase().equals(updateInfo.getData().toLowerCase()) && !data.isEmpty() && !updateInfo.getData().isEmpty()) {
				return true; //There is a different data set available
			} //equal data sets (not newer)
			return false;
		}

		public String getType() {
			switch (arrVersion[VERSION_TYPE]) {
				case DEV_BUILD:
					return GuiShared.get().devBuild();
				case BETA:
					return GuiShared.get().beta();
				case RELEASE_CANDIDATE:
					return GuiShared.get().releaseCandidate();
				case STABLE:
				default:
					return GuiShared.get().stable();
			}
		}

		private int[] getVersion(final String version) {
			Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+");
			Matcher m = p.matcher(version);
			String[] strings;
			if (m.find()) {
				int start = m.start();
				int end = m.end();
				strings = version.substring(start, end).split("\\.");
			} else {
				LOG.info("Failed to get update information: Reg Exp didn't find X.X.X in program version");
				return new int[0];
			}
			if (strings.length != 3) {
				return new int[0];
			}
			int[] integers = new int[5];
			integers[VERSION_MAJOR] = Integer.parseInt(strings[0]);
			integers[VERSION_MINOR] = Integer.parseInt(strings[1]);
			integers[VERSION_BUGFIX] = Integer.parseInt(strings[2]);
			integers[VERSION_TYPE] = getType(version);
			integers[VERSION_TYPE_NUMBER] = getTypeNumber(version);
			return integers;
		}

		private int getType(final String version) {
			if (version.toLowerCase().contains("dev")) {
				return DEV_BUILD;
			}
			if (version.toLowerCase().contains("beta")) {
				return BETA;
			}
			if (version.toLowerCase().contains("release candidate")) {
				return RELEASE_CANDIDATE;
			}
			if (version.toLowerCase().contains("rc")) {
				return RELEASE_CANDIDATE;
			}
			return STABLE;
		}

		private int getTypeNumber(String version) {
			Pattern p = Pattern.compile("beta \\d+|release candidate \\d+|rc \\d+|build #?\\d+", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(version);
			if (!m.find()) { //Not found - return zero
				return 0;
			}
			version = version.substring(m.start(), m.end());
			p = Pattern.compile("\\d+");
			m = p.matcher(version);
			if (!m.find()) { //Not found - return zero
				return 0;
			}
			version = version.substring(m.start(), m.end());
			return Integer.parseInt(version);
		}
	}

}
