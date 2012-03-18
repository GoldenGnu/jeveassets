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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;


public abstract class AbstractXmlWriter {

	private final static Logger LOG = LoggerFactory.getLogger(AbstractXmlWriter.class);

	protected static Document getXmlDocument(String rootname) throws XmlException  {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();
			return impl.createDocument(null, rootname, null);
		} catch (ParserConfigurationException ex) {
			throw new XmlException(ex.getMessage(), ex);
		}
	}
	protected static void writeXmlFile(Document doc, String filename) throws XmlException  {
		writeXmlFile(doc, filename, "UTF-16");
	}

	protected static void writeXmlFile(Document doc, String filename, String encoding) throws XmlException  {
		DOMSource source = new DOMSource(doc);
		try {
			backupFile(filename);
			File outputFile = new File(filename);
			FileOutputStream outputStream = new FileOutputStream(outputFile);
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
		}
	}

	private static void backupFile(String filename){
		File outputFile = new File(filename);
		int end = filename.lastIndexOf(".");
		String backup = filename.substring(0, end)+".bac";
		File backupFile = new File(backup);
		if (backupFile.exists() && !backupFile.delete()){
			LOG.warn("Was not able to delete previous backup file: {}", backup);
		}
		if (outputFile.exists() && !outputFile.renameTo(backupFile)){
			LOG.warn("Was not able to make backup of: {}", filename);
		}
	}
}
