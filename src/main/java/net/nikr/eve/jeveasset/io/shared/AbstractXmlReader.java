/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


public abstract class AbstractXmlReader {
	private final static Logger LOG = LoggerFactory.getLogger(AbstractXmlReader.class);

	protected static Element getDocumentElement(String filename) throws XmlException, IOException {
		return getDocumentElement(filename, false);
	}
	
	private static Element getDocumentElement(String filename, boolean usingBackupFile) throws XmlException, IOException {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		
		Document doc = null;
		FileInputStream is = null;
		try {
			is = new FileInputStream(new File(filename));
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
			return doc.getDocumentElement();
		} catch (SAXException ex) {
			if (is != null) is.close(); //Close file - so we can delete it...
			if (!usingBackupFile && restoreBackupFile(filename)){
				return getDocumentElement(filename, true);
			}
			throw new XmlException(ex.getMessage(), ex);
		} catch (ParserConfigurationException ex) {
			throw new XmlException(ex.getMessage(), ex);
		} finally {
			if (is != null) is.close();
		}
	}

	private static boolean restoreBackupFile(String filename){
		int end = filename.lastIndexOf(".");
		String backup = filename.substring(0, end)+".bac";
		File backupFile = new File(backup);
		File inputFile = new File(filename);
		if (!backupFile.exists()){
			LOG.warn("No backup file found: {}", backup);
			return false;
		}
		if (inputFile.exists() && !inputFile.delete()){
			LOG.warn("Was not able to delete buggy inputfile: {}", filename);
			return false;
		}
		if (backupFile.renameTo(inputFile)){
			LOG.warn("Backup file restored: {}", backup);
			return true;
		} else {
			LOG.warn("Was not able to restore backup: {}", backup);
		}
		return false;
	}
}
