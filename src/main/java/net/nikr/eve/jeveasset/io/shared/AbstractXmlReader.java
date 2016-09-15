/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.shared;

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


public abstract class AbstractXmlReader extends AbstractXmlBackup {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractXmlReader.class);

	protected void staticDataFix() {
		Updater updater = new Updater();
		updater.fixData();
	}

	protected Element getDocumentElement(final String filename, final boolean fileLock) throws XmlException, IOException {
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document doc;
		FileInputStream is = null;
		File file = new File(filename);
		try {
			if (fileLock) {
				lock(filename);
			}
			is = new FileInputStream(file);
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
			return doc.getDocumentElement();
		} catch (SAXException ex) {
			if (is != null) { //Close file - so we can delete it...
				is.close();
			}
			if (restoreNewFile(filename)) { //If possible restore from .new (Should be the newest)
				return getDocumentElement(filename, false); //File already locked
			} else if (restoreBackupFile(filename)) { //If possible restore from .bac (Should be the oldes, but, still worth trying)
				return getDocumentElement(filename, false); //File already locked
			} else { //Nothing left to try - throw error
				restoreFailed(filename); //Backup error file
				throw new XmlException(ex.getMessage(), ex);
			}
		} catch (ParserConfigurationException ex) {
			throw new XmlException(ex.getMessage(), ex);
		} finally {
			if (is != null) {
				is.close();
			}
			if (fileLock) {
				unlock(filename); //Last thing to do
			}
		}
	}

	
}
