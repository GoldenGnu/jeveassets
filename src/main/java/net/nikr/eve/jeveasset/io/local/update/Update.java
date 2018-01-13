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
package net.nikr.eve.jeveasset.io.local.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.nikr.eve.jeveasset.io.local.AbstractXmlReader;
import net.nikr.eve.jeveasset.io.local.AttributeGetters;
import net.nikr.eve.jeveasset.io.local.SettingsReader;
import net.nikr.eve.jeveasset.io.local.XmlException;
import net.nikr.eve.jeveasset.io.local.update.updates.Update1To2;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/**
 *
 * @author Candle
 */
public class Update extends AbstractXmlReader<Integer> {
	private static final Logger LOG = LoggerFactory.getLogger(Update.class);

	@Override
	protected Integer parse(org.w3c.dom.Element element) throws XmlException {
		if (element.getNodeName().equals("settings")) {
			if (AttributeGetters.haveAttribute((Node) element, "version")) {
				return AttributeGetters.getInt((Node) element, "version");
			}
		} else {
			throw new XmlException("Wrong root element name.");
		}
		return 1;
	}

	@Override
	protected Integer failValue() {
		return 1;
	}

	@Override
	protected Integer doNotExistValue() {
		return SettingsReader.SETTINGS_VERSION;
	}

	void setVersion(final File xml, final int newVersion) throws DocumentException {
		SAXReader xmlReader = new SAXReader();
		Document doc = xmlReader.read(xml);

		XPath xpathSelector = DocumentHelper.createXPath("/settings");
		List<?> results = xpathSelector.selectNodes(doc);
		for (Iterator<?> iter = results.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			Attribute attr = element.attribute("version");
			if (attr == null) {
				element.add(new DefaultAttribute("version", String.valueOf(newVersion)));
			} else {
				attr.setText(String.valueOf(newVersion));
			}
		}

		try {
			FileOutputStream fos = new FileOutputStream(xml);
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			outformat.setEncoding("UTF-16");
			XMLWriter writer = new XMLWriter(fos, outformat);
			writer.write(doc);
			writer.flush();
		} catch (IOException ioe) {
			LOG.error("Failed to update the serttings.xml version number", ioe);
			throw new RuntimeException(ioe);
		}
	}

	/**
	 * TODO When more updates are added convert this to a graph-based
	 * search for a suitable list of updates and then perform the updates
	 * in the correct order. - Candle 2010-09-19
	 * @param requiredVersion current version
	 * @param path settings path
	 * @throws net.nikr.eve.jeveasset.io.local.XmlException on any error
	 */
	public void performUpdates(final int requiredVersion, final String path) throws XmlException {
		File xml = new File(path);
		if (!xml.exists()) {
			LOG.info("No settings.xml file found - nothing to update");
			return;
		}
		try {
			int currentVersion = read("update settings", path, AbstractXmlReader.XmlType.DYNAMIC);
			lock(path);
			if (requiredVersion > currentVersion) {
				LOG.info("settings.xml are out of date, updating.");
				Update1To2 update = new Update1To2();
				update.performUpdate(path);
				setVersion(new File(path), requiredVersion);
			} else {
				LOG.info("settings.xml are up to date.");
			}
		} catch (DocumentException ex) {
			LOG.warn("Failed to update settings", ex);
			throw new XmlException(ex);
		} finally {
			unlock(path);
		}
	}
}
