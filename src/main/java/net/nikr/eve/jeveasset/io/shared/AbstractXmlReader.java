/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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
import net.nikr.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


public abstract class AbstractXmlReader {

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
			is.close();
			return doc.getDocumentElement();
		} catch (SAXException ex) {
			if (!usingBackupFile){
				if (restoreBackupFile(filename)){
					return getDocumentElement(filename, true);
				}
			}
			throw new XmlException(ex.getMessage(), ex);
		} catch (ParserConfigurationException ex) {
			throw new XmlException(ex.getMessage(), ex);
		}
	}

	private static boolean restoreBackupFile(String filename){
		int end = filename.lastIndexOf(".");
		String backup = filename.substring(0, end)+".bac";
		File backupFile = new File(backup);
		File inputFile = new File(filename);
		if (!backupFile.exists()){
			Log.warning("No backup file found: "+backup);
			return false;
		}
		if (inputFile.exists() ){
			if (!inputFile.delete()){
				Log.warning("Was not able to delete buggy inputfile: "+filename);
				return false;
			}
		}
		if (backupFile.renameTo(inputFile)){
			Log.warning("Backup file restored: "+backup);
			return true;
		} else {
			Log.warning("Was not able to restore backup: "+backup);
		}
		return false;
	}
}
