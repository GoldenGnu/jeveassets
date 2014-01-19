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

package net.nikr.eve.jeveasset.io.shared;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;


public abstract class AbstractXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractXmlWriter.class);

	protected Document getXmlDocument(final String rootname) throws XmlException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();
			return impl.createDocument(null, rootname, null);
		} catch (ParserConfigurationException ex) {
			throw new XmlException(ex.getMessage(), ex);
		}
	}

	protected void writeXmlFile(final Document doc, final String filename, final boolean createBackup) throws XmlException {
		writeXmlFile(doc, filename, "UTF-16", createBackup);
	}

	private void writeXmlFile(final Document doc, final String filename, final String encoding, boolean createBackup) throws XmlException {
		DOMSource source = new DOMSource(doc);
		FileOutputStream outputStream = null;
		File file = new File(filename);
		try {
			FileLock.lock(file);
			if (createBackup) {
				backupFile(filename);
			}
			outputStream = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, encoding);
			// result
			Result result = new StreamResult(outputStreamWriter);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			transformer.transform(source, result);
		} catch (FileNotFoundException ex) {
			throw new XmlException(ex.getMessage(), ex);
		} catch (TransformerConfigurationException ex) {
			throw new XmlException(ex.getMessage(), ex);
		} catch (TransformerException ex) {
			throw new XmlException(ex.getMessage(), ex);
		} catch (UnsupportedEncodingException ex) {
			throw new XmlException(ex.getMessage(), ex);
		} finally {
			FileLock.unlock(file);
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException ex) {
					throw new XmlException(ex.getMessage(), ex);
				}
			}
		}
	}

	private void backupFile(final String filename) {
		File outputFile = new File(filename);
		int end = filename.lastIndexOf(".");
		String backup = filename.substring(0, end) + ".bac";
		File backupFile = new File(backup);
		if (!outputFile.exists()) {
			LOG.info("No file to backup: {}", outputFile.getName());
			return;
		}
		if (backupFile.exists() && !backupFile.delete()) {
			LOG.warn("Was not able to delete previous backup file: {}", backupFile.getName());
			return;
		}
		if (outputFile.exists() && outputFile.renameTo(backupFile)) {
			LOG.info("Backup created: {}", backupFile.getName());
		} else {
			LOG.warn("Was not able to make backup of: {}", outputFile.getName());
		}
		
	}
}
