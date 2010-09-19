package net.nikr.eve.jeveasset.io.local.update.updates;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.io.local.update.LocalUpdate;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
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
			// XPath: /settings/filters/filter/row[@mode]
			// changed from (e.g.) "Contains" to the enum value name in AssetFilter.Mode
			// settings/marketstat[@defaultprice] --> another enum: EveAsset.PriceMode
			String settingPath = Settings.getPathSettings();
			SAXReader xmlReader = new SAXReader();
			Document doc = xmlReader.read(settingPath);
			convertDefaultPriceModes(doc);
			convertModes(doc);

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
			String currentValue = attr.getText();
			attr.setText(convertDefaultPriceMode(currentValue));
		}
	}

	private String convertDefaultPriceMode(String oldVal) {
		if (oldVal.startsWith("PRICE_")) return oldVal;
		String convert = oldVal.toLowerCase();
		String out = "PRICE";
		if (convert.contains("sell")) return out + "_SELL";
		if (convert.contains("buy")) return out + "_BUY";
		if (convert.contains("midpoint")) return out + "_MIDPOINT";
		if (convert.contains("maximum")) return out + "_MAX";
		if (convert.contains("average")) return out + "_AVG";
		if (convert.contains("median")) return out + "_MEDIAN";
		if (convert.contains("minimum")) return out + "_MIN";
		throw new IllegalArgumentException("Failed to convert the price type " + oldVal);
	}

	private String convertMode(String oldVal) {
		if (oldVal.startsWith("MODE_")) return oldVal;
		String convert = oldVal.toLowerCase();
		convert = convert.toLowerCase();
		String out = "MODE";
		if (convert.contains("equal")) return out + "_EQUALS";
		if (convert.contains("contain")) return out + "_CONTAIN";
		if (convert.contains("not")) return out + "_NOT";
		if (convert.contains("greater")) return out + "_GREATER_THAN";
		if (convert.contains("less")) return out + "_LESS_THAN";
		if (convert.contains("column")) return out + "_COLUMN";
		throw new IllegalArgumentException("Failed to convert the mode type " + oldVal);
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
