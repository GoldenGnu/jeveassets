package net.nikr.eve.jeveasset.io.local.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.io.local.update.updates.Update1To2;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Candle
 */
public class Update {
	private static final Logger LOG = LoggerFactory.getLogger(Update.class);

	int getVersion(File xml) throws DocumentException {
		SAXReader xmlReader = new SAXReader();
		Document doc = xmlReader.read(xml);

		XPath xpathSelector = DocumentHelper.createXPath("/settings");
		List results = xpathSelector.selectNodes(doc);
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			Attribute attr = element.attribute("version");
			if (attr == null) {
				return 1;
			} else {
				return Integer.parseInt(attr.getText());
			}
		}
		return 1;
	}

	void setVersion(File xml, int newVersion) throws DocumentException {
		SAXReader xmlReader = new SAXReader();
		Document doc = xmlReader.read(xml);

		XPath xpathSelector = DocumentHelper.createXPath("/settings");
		List results = xpathSelector.selectNodes(doc);
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			Attribute attr = element.attribute("version");
			if (attr == null) {
				element.add(new DefaultAttribute("version", String.valueOf(newVersion)));
			} else {
				attr.setText(String.valueOf(newVersion));
			}
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(xml);
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			outformat.setEncoding("UTF-16");
			XMLWriter writer = new XMLWriter(fos, outformat);
			writer.write(doc);
			writer.flush();
		} catch (IOException ioe) {
			LOG.error("Failed to update the serttings.xml version number", ioe);
			throw new RuntimeException(ioe);
		}
	}

	/**
	 * TODO When more updates are added convert this to a graph-based
	 * search for a suitable list of updates and then perform the updates
	 * in the correct order. - Candle 2010-09-19
	 * @param requiredVersion
	 */
	public void performUpdates(int requiredVersion) {
		File xml = new File(Settings.getPathSettings());
		if (!xml.exists()){
			LOG.info("No settings.xml file found - nothing to update");
			return;
		}
		try {
			int currentVersion = getVersion(xml);
			if (requiredVersion > currentVersion) {
				LOG.info("Data files are out of date, updating.");
				Update1To2 update = new Update1To2();
				update.performUpdate();
				setVersion(new File(Settings.getPathSettings()), requiredVersion);
			}
		} catch (DocumentException ex) {
			LOG.error("", ex);
			throw new RuntimeException(ex);
		}
	}
}
