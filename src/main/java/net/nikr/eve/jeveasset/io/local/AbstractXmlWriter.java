/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.NonWritableChannelException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
import net.nikr.eve.jeveasset.io.local.FileLock.SafeFileIO;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public abstract class AbstractXmlWriter extends AbstractBackup {

	private static DocumentBuilderFactory factory = null;

	protected Document getXmlDocument(final String rootname) throws XmlException {
		try {
			DocumentBuilder builder = getFactory().newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();
			return impl.createDocument(null, rootname, null);
		} catch (ParserConfigurationException ex) {
			throw new XmlException(ex.getMessage(), ex);
		}
	}

	private synchronized static DocumentBuilderFactory getFactory() {
		if (factory == null) {
			factory = DocumentBuilderFactory.newInstance();
		}
		return factory;
	}

	protected void writeXmlFileFitting(final Document doc, final String filename, final boolean createBackup) throws XmlException {
		writeXmlFile(doc, filename, "UTF-8", createBackup, true);
	}

	protected void writeXmlFile(final Document doc, final String filename, final boolean createBackup) throws XmlException {
		writeXmlFile(doc, filename, "UTF-16", createBackup, false);
	}

	private void writeXmlFile(final Document doc, final String filename, final String encoding, boolean createBackup, boolean fitting) throws XmlException {
		DOMSource source = new DOMSource(doc);
		File file;
		if (createBackup) {
			file = getNewFile(filename); //Save to .new file
		} else {
			file = new File(filename);
		}
		try (SafeFileIO io = new SafeFileIO(file)){
			//Save file
			OutputStreamWriter outputStreamWriter = io.getOutputStreamWriter(encoding);
			if (fitting) {
				outputStreamWriter.append("<?xml version=\"1.0\" ?>\r\n");
			}
			// result
			Result result = new StreamResult(outputStreamWriter);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			if (fitting) {
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			}
			transformer.transform(source, result);
			io.unlock(); //Unlock before backupFile() is called
			//Saving done - create backup and rename new file to target
			if (createBackup) {
				backupFile(filename); //Rename .xml => .bac (.new is safe) and .new => .xml (.bac is safe). That way we always have at least one safe file
			}
		} catch (FileNotFoundException ex) {
			throw new XmlException(ex.getMessage(), ex);
		} catch (TransformerConfigurationException ex) {
			throw new XmlException(ex.getMessage(), ex);
		} catch (TransformerException ex) {
			throw new XmlException(ex.getMessage(), ex);
		} catch (UnsupportedEncodingException ex) {
			throw new XmlException(ex.getMessage(), ex);
		} catch (NonWritableChannelException ex) {
			throw new XmlException(ex.getMessage(), ex);
		} catch (IOException ex) {
			throw new XmlException(ex.getMessage(), ex);
		}
	}

	protected void setAttribute(final Element node, final String qualifiedName, final Object value) {
		node.setAttribute(qualifiedName, valueOf(value));
	}

	protected void setAttributeOptional(final Element node, final String qualifiedName, final Object value) {
		if (value != null) {
			node.setAttribute(qualifiedName, valueOf(value));
		}
	}

	protected void setAttribute(final Element node, final String qualifiedName, final String value) {
		node.setAttribute(qualifiedName, value);
	}

	public static String valueOf(final Object object) {
		if (object == null) {
			throw new RuntimeException("Can't save null");
		} else if (object instanceof Collection) {
			Collection<?> collection = (Collection) object;
			List<String> list = new ArrayList<>();
			for (Object t : collection) {
				list.add(valueOf(t));
			}
			return String.join(",", list);
		} else if (object instanceof Color) {
			Color color = (Color) object;
			return String.valueOf(color.getRGB());
		} else if (object instanceof Date) {
			Date date = (Date) object;
			return String.valueOf(date.getTime());
		} else if (object instanceof Enum) {
			return ((Enum)object).name();
		} else {
			return String.valueOf(object);
		}
	}
}
