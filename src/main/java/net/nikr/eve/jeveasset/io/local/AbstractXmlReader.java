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

package net.nikr.eve.jeveasset.io.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.nikr.eve.jeveasset.io.online.Updater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


public abstract class AbstractXmlReader<T> extends AbstractXmlBackup {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractXmlReader.class);

	public static enum XmlType {
		DYNAMIC, STATIC, DYNAMIC_BACKUP, IMPORT
	}

	protected T read(final String name, final String filename, final XmlType xmlType) {
		if (!exist(filename) && (xmlType == XmlType.DYNAMIC || xmlType == XmlType.DYNAMIC_BACKUP)) {
			return doNotExistValue();
		}
		try {
			Element element = getDocumentElement(filename, xmlType);
			T t = parse(element);
			LOG.info(name+ " loaded");
			return t;
		} catch (IOException ex) {
			if (xmlType == XmlType.STATIC) {
				staticDataFix();
			}
			LOG.info(name+ " not loaded");
			return failValue();
		} catch (XmlException ex) {
			if (xmlType == XmlType.STATIC) { //Static data
				staticDataFix();
			} else if (xmlType == XmlType.DYNAMIC || xmlType == XmlType.DYNAMIC_BACKUP) { //Dynamic data
				if (restoreNewFile(filename)) { //If possible restore from .new (Should be the newest)
					return read(name, filename, xmlType);
				} else if (restoreBackupFile(filename)) { //If possible restore from .bac (Should be the oldest, but, still worth trying)
					return read(name, filename, xmlType);
				} else { //Nothing left to try - throw error
					restoreFailed(filename); //Backup error file
				}
			}
			LOG.error(name+ " not loaded: " + ex.getMessage(), ex);
			return failValue();
		}
	}

	protected abstract T parse(Element element) throws XmlException;
	protected abstract T failValue();
	protected abstract T doNotExistValue();

	private void staticDataFix() {
		Updater updater = new Updater();
		updater.fixData();
	}

	private Element getDocumentElement(final String filename, final XmlType xmlType) throws XmlException, IOException {
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document doc;
		FileInputStream is = null;
		File file = new File(filename);
		try {
			if (xmlType == XmlType.DYNAMIC || xmlType == XmlType.DYNAMIC_BACKUP) {
				lock(filename);
			}
			is = new FileInputStream(file);
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
			Element element = doc.getDocumentElement();
			if (xmlType == XmlType.DYNAMIC_BACKUP) {
				backup(filename, element);
			}
			return element;
		} catch (SAXException ex) {
			throw new XmlException(ex.getMessage(), ex);
		} catch (ParserConfigurationException ex) {
			throw new XmlException(ex.getMessage(), ex);
		} finally {
			if (is != null) {
				is.close();
			}
			if (xmlType == XmlType.DYNAMIC || xmlType == XmlType.DYNAMIC_BACKUP) {
				unlock(filename); //Last thing to do
			}
		}
	}

	
}
