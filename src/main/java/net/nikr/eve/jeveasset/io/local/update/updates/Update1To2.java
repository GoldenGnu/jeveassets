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
package net.nikr.eve.jeveasset.io.local.update.updates;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.io.local.update.LocalUpdate;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple script to modify the settings, changing the
 *
 *
 * @author Candle
 */
public class Update1To2 implements LocalUpdate {
	private static final Logger LOG = LoggerFactory.getLogger(Update1To2.class);

	@Override
	public void performUpdate() {
		LOG.info("Performing update from v1 to v2");
		LOG.info("  - modifies files:");
		LOG.info("    - settings.xml");
		try {
			// We need to update the settings
			// current changes are:
			// 1. XPath: /settings/filters/filter/row[@mode]
			// changed from (e.g.) "Contains" to the enum value name in AssetFilter.Mode
			// 2. settings/marketstat[@defaultprice] --> another enum: EveAsset.PriceMode
			// 3. settings/columns/column --> settings/tables/table/column
			// settings/flags/flag --> removed two flags (now in settings/tables/table)
			String settingPath = Settings.getPathSettings();
			SAXReader xmlReader = new SAXReader();
			Document doc = xmlReader.read(settingPath);
			convertDefaultPriceModes(doc);
			convertModes(doc);
			convertTableSettings(doc);

			FileOutputStream fos = new FileOutputStream(settingPath);
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			outformat.setEncoding("UTF-16");
			XMLWriter writer = new XMLWriter(fos, outformat);
			writer.write(doc);
			writer.flush();
		} catch (IOException ex) {
			LOG.error("", ex);
			throw new RuntimeException(ex);
		} catch (DocumentException ex) {
			LOG.error("", ex);
			throw new RuntimeException(ex);
		}
	}

	private void convertModes(Document doc) {
		XPath xpathSelector = DocumentHelper.createXPath("/settings/filters/filter/row");
		List results = xpathSelector.selectNodes(doc);
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			Element elem = (Element) iter.next();
			Attribute attr = elem.attribute("mode");
			String currentValue = attr.getText();
			attr.setText(convertMode(currentValue));
		}
	}

	private void convertDefaultPriceModes(Document doc) {
		XPath xpathSelector = DocumentHelper.createXPath("/settings/marketstat");
		List results = xpathSelector.selectNodes(doc);
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			Element elem = (Element) iter.next();
			Attribute attr = elem.attribute("defaultprice");
			if (attr != null){ //May not exist (in early versions)
				String currentValue = attr.getText();
				attr.setText(convertDefaultPriceMode(currentValue));
			}
		}
	}

	private void convertTableSettings(Document doc){
		XPath xpathSelector = DocumentHelper.createXPath("/settings/columns/column");
		List results = xpathSelector.selectNodes(doc);
		List<String> tableColumnNames = new ArrayList<String>();
		List<String> tableColumnVisible = new ArrayList<String>();
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			Attribute name = element.attribute("name");
			Attribute visible = element.attribute("visible");
			tableColumnNames.add(name.getText());
			if (visible.getText().equals("true")) tableColumnVisible.add(name.getText());
		}
		String mode = convertFlag(doc);
		writeTableSettings(doc, mode, tableColumnNames, tableColumnVisible);
	}

	private String convertDefaultPriceMode(String oldVal) {
		if (oldVal.startsWith("PRICE_")) return oldVal;
		String convert = oldVal.toLowerCase();
		if (convert.contains("midpoint"))     return Asset.PriceMode.PRICE_MIDPOINT.name();
		if (convert.contains("sell average")) return Asset.PriceMode.PRICE_SELL_AVG.name();
		if (convert.contains("sell median"))  return Asset.PriceMode.PRICE_SELL_MEDIAN.name();
		if (convert.contains("sell minimum")) return Asset.PriceMode.PRICE_SELL_MIN.name();
		if (convert.contains("sell maximum")) return Asset.PriceMode.PRICE_SELL_MAX.name();
		if (convert.contains("buy maximum"))  return Asset.PriceMode.PRICE_BUY_MAX.name();
		if (convert.contains("buy average"))  return Asset.PriceMode.PRICE_BUY_AVG.name();
		if (convert.contains("buy median"))   return Asset.PriceMode.PRICE_BUY_MEDIAN.name();
		if (convert.contains("buy minimum"))  return Asset.PriceMode.PRICE_BUY_MIN.name();
		throw new IllegalArgumentException("Failed to convert the price type " + oldVal);
	}

	private String convertMode(String oldVal) {
		if (oldVal.startsWith("MODE_")) return oldVal;
		String convert = oldVal.toLowerCase();
		convert = convert.toLowerCase();
		if (convert.contains("equals"))              return "MODE_EQUALS";
		if (convert.contains("contains"))            return "MODE_CONTAIN";
		if (convert.contains("does not contain"))    return "MODE_CONTAIN_NOT";
		if (convert.contains("does not equal"))      return "MODE_EQUALS_NOT";
		if (convert.contains("greater than"))        return "MODE_GREATER_THAN";
		if (convert.contains("less than"))           return "MODE_LESS_THAN";
		if (convert.contains("greater than column")) return "MODE_GREATER_THAN_COLUMN";
		if (convert.contains("less than column"))    return "MODE_LESS_THAN_COLUMN";
		throw new IllegalArgumentException("Failed to convert the mode type " + oldVal);
	}
	
	private String convertFlag(Document doc){
		XPath flagSelector = DocumentHelper.createXPath("/settings/flags/flag");
		List flagResults = flagSelector.selectNodes(doc);
		boolean text = false;
		boolean window = false;
		for (Iterator iter = flagResults.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			Attribute key = element.attribute("key");
			Attribute visible = element.attribute("enabled");
			if (key.getText().equals("FLAG_AUTO_RESIZE_COLUMNS_TEXT")){
				text = visible.getText().equals("true");
				element.detach();
			}
			if (key.getText().equals("FLAG_AUTO_RESIZE_COLUMNS_WINDOW")){
				window = visible.getText().equals("true");
				element.detach();
			}
		}
		if (text) return "TEXT";
		if (window) return "WINDOW";
		return "NONE";
	}

	private void writeTableSettings(Document doc, String mode, List<String> tableColumnNames, List<String> tableColumnVisible){
		Element tables = doc.getRootElement().addElement("tables");
		Element table = tables.addElement("table");
		table.addAttribute("name", "COLUMN_SETTINGS_ASSETS");
		table.addAttribute("resize", mode);
		for (String columnName : tableColumnNames){
			Element column = table.addElement("column");
			column.addAttribute("name", columnName);
			column.addAttribute("visible", String.valueOf(tableColumnVisible.contains(columnName)));
		}
	}

	@Override
	public int getStart() {
		return 1;
	}
	@Override
	public int getEnd() {
		return 2;
	}

}
