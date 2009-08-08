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

package net.nikr.eve.jeveasset.io;


import net.nikr.log.Log;
import org.w3c.dom.Node;


public class AttributeGetters {
	public static boolean haveAttribute(Node node, String attributeName){
		Node attributeNode = node.getAttributes().getNamedItem(attributeName);
		if (attributeNode == null){
			return false;
		}
		return true;
	}
	public static String getAttributeString(Node node, String attributeName){
		Node attributeNode = node.getAttributes().getNamedItem(attributeName);
		if (attributeNode == null){
			Log.warning("Failed to parse attribute from node: "+node.getNodeName()+" > "+attributeName);
			return "";
		}
		return attributeNode.getNodeValue();
	}
	public static int getAttributeInteger(Node node, String attributeName){
		Node attributeNode = node.getAttributes().getNamedItem(attributeName);
		if (attributeNode == null){
			Log.warning("Failed to parse attribute from node: "+node.getNodeName()+" > "+attributeName);
			return -1;
		}
		String sTemp = attributeNode.getNodeValue();
		int nTemp;
		try {
			nTemp = Integer.parseInt(sTemp);
			return nTemp;
		} catch(NumberFormatException ex){
			Log.warning("Failed to convert string to int: "+sTemp+" from node: "+node.getNodeName()+" > "+attributeName);
			return -1;
		}
	}
	public static long getAttributeLong(Node node, String attributeName){
		Node attributeNode = node.getAttributes().getNamedItem(attributeName);
		if (attributeNode == null){
			Log.warning("Failed to parse attribute from node: "+node.getNodeName()+" > "+attributeName);
			return -1;
		}
		String sTemp = attributeNode.getNodeValue();
		long nTemp;
		try {
			nTemp = safeStringToLong(sTemp);//Long.parseLong(sTemp);
			return nTemp;
		} catch (NumberFormatException ex){
			Log.warning("Failed to convert string to long: "+sTemp+" from node: "+node.getNodeName()+" > "+attributeName);
			return -1;
		}
	}
	public static double getAttributeDouble(Node node, String attributeName){
		Node attributeNode = node.getAttributes().getNamedItem(attributeName);
		if (attributeNode == null){
			Log.warning("Failed to parse attribute from node: "+node.getNodeName()+" > "+attributeName);
			return -1;
		}
		String sTemp = attributeNode.getNodeValue();
		double nTemp;
		try {
			nTemp = Double.valueOf(sTemp);//Long.parseLong(sTemp);
			return nTemp;
		} catch (NumberFormatException ex){
			Log.warning("Failed to convert string to long: "+sTemp+" from node: "+node.getNodeName()+" > "+attributeName);
			return -1;
		}
	}
	public static float getAttributeFloat(Node node, String attributeName){
		Node attributeNode = node.getAttributes().getNamedItem(attributeName);
		if (attributeNode == null){
			Log.warning("Failed to parse attribute from node: "+node.getNodeName()+" > "+attributeName);
			return -1;
		}
		String sTemp = attributeNode.getNodeValue();
		float nTemp;
		try {
			nTemp = Float.valueOf(sTemp);//Long.parseLong(sTemp);
			return nTemp;
		} catch (NumberFormatException ex){
			Log.warning("Failed to convert string to long: "+sTemp+" from node: "+node.getNodeName()+" > "+attributeName);
			return -1;
		}
	}
	public static boolean getAttributeBoolean(Node node, String attributeName){
		Node attributeNode = node.getAttributes().getNamedItem(attributeName);
		if (attributeNode == null){
			Log.warning("Failed to parse attribute from node: "+node.getNodeName()+" > "+attributeName);
			return false;
		}
		String sTemp = attributeNode.getNodeValue();
		return (sTemp.equals("true"));
	}


	private static Long safeStringToLong(String s) throws NumberFormatException {
		int nE = s.indexOf("E");
		if (nE == -1){
			nE = s.indexOf("e");
		}
		if (nE == -1){
			return Long.parseLong(s);
		}
		String sFirstNumber = s.substring(0, nE);
		String sLastNumber = s.substring(nE+2);
		double nFirstNumber = Double.parseDouble(sFirstNumber);
		double nLastNumber = Double.parseDouble(sLastNumber);
		
		long nOutput = 10;
		for (int a = 1; a < nLastNumber; a++){
			nOutput = nOutput * 10;
		}
		nOutput = (long) Math.ceil(nFirstNumber * nOutput);
		return nOutput;
	}
}
