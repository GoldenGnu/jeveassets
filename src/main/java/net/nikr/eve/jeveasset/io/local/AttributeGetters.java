/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.settings.Settings;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class AttributeGetters {

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

	protected AttributeGetters() { }

	protected static Element getNodeOptional(final Element parent, final String nodeName) throws XmlException {
		NodeList nodes = parent.getElementsByTagName(nodeName);
		if (nodes.getLength() != 1) {
			return null;
		}
		return (Element) nodes.item(0);
	}

	protected static Element getNode(final Element parent, final String nodeName) throws XmlException {
		NodeList nodes = parent.getElementsByTagName(nodeName);
		if (nodes.getLength() != 1) {
			throw new XmlException(nodeName + " is " + nodes.getLength()+ " (should be 1)");
		}
		return (Element) nodes.item(0);
	}

	protected static boolean haveAttribute(final Node node, final String attributeName) {
		Node attributeNode = node.getAttributes().getNamedItem(attributeName);
		return attributeNode != null;
	}

	protected static Color getColorOptional(final Node node, final String attributeName) throws XmlException {
		Integer i = getIntOptional(node, attributeName);
		if (i == null) {
			return null;
		} else {
			return new Color(i);
		}
	}

	protected static Color getColor(final Node node, final String attributeName) throws XmlException {
		int i = getInt(node, attributeName);
		return new Color(i);
	}

	protected static List<String> getStringListOptional(final Node node, final String attributeName) throws XmlException {
		String nodeValue = getNodeValueOptional(node, attributeName);
		if (nodeValue == null) {
			return null;
		} else {
			return stringToList(nodeValue);
		}
	}

	protected static void addIntToList(final Node node, final String attributeName, final Collection<Integer> addTo) throws XmlException {
		String nodeValue = getNodeValueOptional(node, attributeName);
		if (nodeValue == null) {
			return;
		}
		for (String s : nodeValue.split(",")) {
			try {
				addTo.add(Integer.valueOf(s));
			} catch (NumberFormatException ex) {
				//Ignore...
			}
		}
	}

	protected static List<String> getStringList(final Node node, final String attributeName) throws XmlException {
		String nodeValue = getNodeValue(node, attributeName);
		return stringToList(nodeValue);
	}

	private static List<String> stringToList(String nodeValue) {
		String[] arr = nodeValue.split(",");
		return new ArrayList<>(Arrays.asList(arr));
	}

	protected static String getString(final Node node, final String attributeName) throws XmlException {
		return getNodeValue(node, attributeName);
	}

	protected static String getStringOptional(final Node node, final String attributeName) throws XmlException {
		return getNodeValueOptional(node, attributeName);
	}

	protected static String getStringNotNull(final Node node, final String attributeName, final String defaultValue) throws XmlException {
		String value = getNodeValueOptional(node, attributeName);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	protected static Date getDate(final Node node, final String attributeName) throws XmlException {
		String value = getNodeValue(node, attributeName);
		return toDate(value, node, attributeName);
	}

	protected static Date getDateNotNull(final Node node, final String attributeName) {
		String value = getNodeValueOptional(node, attributeName);
		if (value == null) {
			return Settings.getNow();
		}
		try {
			return toDate(value, node, attributeName);
		} catch (XmlException ex) {
			return Settings.getNow();
		}
	}

	protected static Date getDateOptional(final Node node, final String attributeName) throws XmlException {
		String value = getNodeValueOptional(node, attributeName);
		if (value == null) {
			return null;
		}
		return toDate(value, node, attributeName);
	}

	private static Date toDate(final String value, final Node node, final String attributeName) throws XmlException {
		try {
			return FORMAT.parse(value);
		} catch (ParseException ex) {
			//Lets try one more thing
		}
		try {
			return new Date(Long.parseLong(value));
		} catch (NumberFormatException ex) {
			throw new XmlException("Failed to convert value: " +value+ " to Date form node: " + node.getNodeName() + " > " + attributeName);
		}
	}

	protected static int getInt(final Node node, final String attributeName) throws XmlException {
		String value = getNodeValue(node, attributeName);
		return toInt(value, node, attributeName);
	}

	protected static Integer getIntOptional(final Node node, final String attributeName) throws XmlException {
		String value = getNodeValueOptional(node, attributeName);
		if (value == null) {
			return null;
		}
		return toInt(value, node, attributeName);
	}

	protected static int getIntNotNull(final Node node, final String attributeName, final int defaultValue) throws XmlException {
		String value = getNodeValueOptional(node, attributeName);
		if (value == null) {
			return defaultValue;
		}
		return toInt(value, node, attributeName);
	}

	protected static Integer toInt(String value, final Node node, final String attributeName) throws XmlException {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			throw new XmlException("Failed to convert value: " +value+ " to Integer form node: " + node.getNodeName() + " > " + attributeName);
		}
	}

	protected static long getLong(final Node node, final String attributeName) throws XmlException {
		String value = getNodeValue(node, attributeName);
		return toLong(value, node, attributeName);
	}

	protected static Long getLongOptional(final Node node, final String attributeName) throws XmlException {
		String value = getNodeValueOptional(node, attributeName);
		if (value == null) {
			return null;
		}
		return toLong(value, node, attributeName);
	}

	protected static long getLongNotNull(final Node node, final String attributeName, final long defaultValue) throws XmlException {
		String value = getNodeValueOptional(node, attributeName);
		if (value == null) {
			return defaultValue;
		}
		return toLong(value, node, attributeName);
	}

	private static Long toLong(final String value, final Node node, final String attributeName) throws XmlException {
		try {
			return safeStringToLong(value);
		} catch (NumberFormatException ex) {
			throw new XmlException("Failed to convert value: " +value+ " to Long form node: " + node.getNodeName() + " > " + attributeName);
		}
	}

	protected static double getDouble(final Node node, final String attributeName) throws XmlException {
		String value = getNodeValue(node, attributeName);
		return toDouble(value, node, attributeName);
	}

	protected static Double getDoubleOptional(final Node node, final String attributeName) throws XmlException {
		String value = getNodeValueOptional(node, attributeName);
		if (value == null) {
			return null;
		}
		return toDouble(value, node, attributeName);
	}

	protected static double getDoubleNotNull(final Node node, final String attributeName, final double defaultValue) throws XmlException {
		String value = getNodeValueOptional(node, attributeName);
		if (value == null) {
			return defaultValue;
		}
		return toDouble(value, node, attributeName);
	}

	private static Double toDouble(final String value, final Node node, final String attributeName) throws XmlException {
		try {
			return Double.valueOf(value);
		} catch (NumberFormatException ex) {
			throw new XmlException("Failed to convert value: " +value+ " to Double form node: " + node.getNodeName() + " > " + attributeName);
		}
	}

	protected static float getFloat(final Node node, final String attributeName) throws XmlException {
		String value = getNodeValue(node, attributeName);
		return toFloat(value, node, attributeName);
	}

	protected static Float getFloatOptional(final Node node, final String attributeName) throws XmlException {
		String value = getNodeValueOptional(node, attributeName);
		if (value == null) {
			return null;
		}
		return toFloat(value, node, attributeName);
	}

	protected static float getFloatNotNull(final Node node, final String attributeName, final float defaultValue) throws XmlException {
		String value = getNodeValueOptional(node, attributeName);
		if (value == null) {
			return defaultValue;
		}
		return toFloat(value, node, attributeName);
	}

	private static Float toFloat(String value, final Node node, final String attributeName) throws XmlException {
		try {
			return Float.valueOf(value);
		} catch (NumberFormatException ex) {
			throw new XmlException("Failed to convert value: " +value+ " to Float form node: " + node.getNodeName() + " > " + attributeName);
		}
	}

	protected static boolean getBoolean(final Node node, final String attributeName) throws XmlException {
		String value = getNodeValue(node, attributeName);
		return (value.equals("true") || value.equals("1"));
	}

	protected static Boolean getBooleanOptional(final Node node, final String attributeName) throws XmlException {
		String value = getNodeValueOptional(node, attributeName);
		if (value == null) {
			return null;
		}
		return (value.equals("true") || value.equals("1"));
	}

	protected static boolean getBooleanNotNull(final Node node, final String attributeName, final boolean defaultValue) throws XmlException {
		String value = getNodeValueOptional(node, attributeName);
		if (value == null) {
			return defaultValue;
		}
		return (value.equals("true") || value.equals("1"));
	}

	private static Long safeStringToLong(final String s) {
		int nE = s.indexOf("E");
		if (nE == -1) {
			nE = s.indexOf("e");
		}
		if (nE == -1) {
			return Long.parseLong(s);
		}
		String sFirstNumber = s.substring(0, nE);
		String sLastNumber = s.substring(nE + 2);
		double nFirstNumber = Double.parseDouble(sFirstNumber);
		double nLastNumber = Double.parseDouble(sLastNumber);

		long nOutput = 10;
		for (int a = 1; a < nLastNumber; a++) {
			nOutput = nOutput * 10;
		}
		nOutput = (long) Math.ceil(nFirstNumber * nOutput);
		return nOutput;
	}

	private static String getNodeValue(final Node node, final String attributeName) throws XmlException {
		Node attributeNode = node.getAttributes().getNamedItem(attributeName);
		if (attributeNode == null) {
			throw new XmlException("Failed to parse attribute from node: " + node.getNodeName() + " > " + attributeName);
		}
		return attributeNode.getNodeValue();
	}

	private static String getNodeValueOptional(final Node node, final String attributeName) {
		Node attributeNode = node.getAttributes().getNamedItem(attributeName);
		if (attributeNode == null) {
			return null;
		} else {
			return attributeNode.getNodeValue();
		}
	}
}
