/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EveAssetMatching {
	private static final Logger LOG = LoggerFactory.getLogger(EveAssetMatching.class);

	public EveAssetMatching() {

	}

	public boolean matches(Asset item, AssetFilter assetFilter) {
		return matches(item, assetFilter.getColumn(), assetFilter.getMode(), assetFilter.getText(), assetFilter.getColumnMatch());
	}
	public boolean matches(Asset eveAsset, String column, AssetFilter.Mode mode, String text, String columnMatch) {
			final String haystack = getString(eveAsset, column);
			final double value = getDouble(eveAsset, column);
			if (columnMatch != null){
				text = getString(eveAsset, columnMatch);
			}
			if (mode.equals(AssetFilter.Mode.MODE_GREATER_THAN) || mode.equals(AssetFilter.Mode.MODE_GREATER_THAN_COLUMN)){
				double number;
				try{
					number = Double.valueOf(text);
				} catch (NumberFormatException ex){
					return false;
				}
				return (value > number);
			}
			if (mode.equals(AssetFilter.Mode.MODE_LESS_THAN) || mode.equals(AssetFilter.Mode.MODE_LESS_THAN_COLUMN)){
				double number;
				try{
					number = Double.valueOf(text);
				} catch (NumberFormatException ex){
					return false;
				}
				return (value < number);
			}
			if (mode.equals(AssetFilter.Mode.MODE_CONTAIN)){
				return haystack.toLowerCase().contains(text.toLowerCase());
			}
			if (mode.equals(AssetFilter.Mode.MODE_CONTAIN_NOT)){
				return !haystack.toLowerCase().contains(text.toLowerCase());
			}
			if (mode.equals(AssetFilter.Mode.MODE_EQUALS)){
				if (value >= 0){
					double number;
					try{
						number = Double.valueOf(text);
						return (value == number);
					} catch (NumberFormatException ex){
						LOG.warn("Ignoring the exception: " + ex.getMessage(), ex);
					}
					return false;
				} else {
					if (column.equals("All")){
						return haystack.toLowerCase().contains("\n"+text.toLowerCase()+"\r");
					} else {
						return haystack.toLowerCase().equals(text.toLowerCase());
					}
				}
			}
			if (mode.equals(AssetFilter.Mode.MODE_EQUALS_NOT)){
				if (value >= 0){
					double number;
					try{
						number = Double.valueOf(text);
						return (value != number);
					} catch (NumberFormatException ex){
						LOG.warn("Ignoring the exception: " + ex.getMessage(), ex);
					}
					return false;
				} else {
					if (column.equals("All")){
						return !haystack.toLowerCase().contains("\n"+text.toLowerCase()+"\r");
					} else {
						return !haystack.toLowerCase().equals(text.toLowerCase());
					}
				}
			}
			return false;
	}

	public String getString(Asset eveAsset, String column){
		if (column.equals("All")){
			return "\r\n"
						+ eveAsset.getCategory() + "\r\n"
						+ eveAsset.getContainer() + "\r\n"
						+ eveAsset.getFlag() + "\r\n"
						+ eveAsset.getGroup() + "\r\n"
						+ eveAsset.getLocation() + "\r\n"
						+ eveAsset.getMeta() + "\r\n"
						+ eveAsset.getName() + "\r\n"
						+ eveAsset.getOwner() + "\r\n"
						+ eveAsset.getCount() + "\r\n"
						+ eveAsset.getItemID() + "\r\n"
						+ eveAsset.getPrice() + "\r\n"
						+ eveAsset.getPriceSellMin() + "\r\n"
						+ eveAsset.getPriceBuyMax() + "\r\n"
						+ eveAsset.getValue() + "\r\n"
						+ eveAsset.getPriceBase() + "\r\n"
						+ eveAsset.getVolume() + "\r\n"
						+ eveAsset.getTypeID() + "\r\n"
						+ eveAsset.getRegion() + "\r\n"
						+ eveAsset.getTypeCount() + "\r\n"
						+ eveAsset.getSecurity() + "\r\n"
						+ eveAsset.getPriceReprocessed() + "\r\n"
						+ eveAsset.getValueReprocessed() + "\r\n"
					;
		}
		if (column.equals("Name")) return eveAsset.getName();
		if (column.equals("Group")) return eveAsset.getGroup();
		if (column.equals("Category")) return eveAsset.getCategory();
		if (column.equals("Owner")) return eveAsset.getOwner();
		if (column.equals("Count")) return String.valueOf(eveAsset.getCount());
		if (column.equals("Location")) return eveAsset.getLocation();
		if (column.equals("Container")) return eveAsset.getContainer();
		if (column.equals("Flag")) return eveAsset.getFlag();
		if (column.equals("Price")) return String.valueOf(eveAsset.getPrice());
		if (column.equals("Meta")) return eveAsset.getMeta();
		if (column.equals("ID")) return String.valueOf(eveAsset.getItemID());
		if (column.equals("Sell Min")) return String.valueOf(eveAsset.getPriceSellMin());
		if (column.equals("Buy Max")) return String.valueOf(eveAsset.getPriceBuyMax());
		if (column.equals("Value")) return String.valueOf(eveAsset.getValue());
		if (column.equals("Base Price")) return String.valueOf(eveAsset.getPriceBase());
		if (column.equals("Volume")) return String.valueOf(eveAsset.getVolume());
		if (column.equals("Type ID")) return String.valueOf(eveAsset.getTypeID());
		if (column.equals("Region")) return eveAsset.getRegion();
		if (column.equals("Type Count")) return String.valueOf(eveAsset.getTypeCount());
		if (column.equals("Security")) return eveAsset.getSecurity();
		if (column.equals("Reprocessed")) return String.valueOf(eveAsset.getPriceReprocessed());
		if (column.equals("Reprocessed Value")) return String.valueOf(eveAsset.getValueReprocessed());
		return "";
	}
	public double getDouble(Asset eveAsset, String column){
		if (column.equals("All")) return -1;
		if (column.equals("Name")) return -1;
		if (column.equals("Group")) return -1;
		if (column.equals("Category")) return -1;
		if (column.equals("Owner")) return -1;
		if (column.equals("Count")) return eveAsset.getCount();
		if (column.equals("Location")) return -1;
		if (column.equals("Container")) return -1;
		if (column.equals("Flag")) return -1;
		if (column.equals("Price")) return eveAsset.getPrice();
		if (column.equals("Meta")){
			String meta = eveAsset.getMeta();
			if (meta.isEmpty()) return 0;
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(meta);
			if (m.find()){
				meta = meta.substring(m.start(), m.end());
				try {
					return Double.valueOf( meta );
				} catch (NumberFormatException ex){
					LOG.warn("Ignoring the exception: " + ex.getMessage(), ex);
				}
			}
			return -1;
		}
		if (column.equals("ID")) return eveAsset.getItemID();
		if (column.equals("Sell Min")) return eveAsset.getPriceSellMin();
		if (column.equals("Buy Max")) return eveAsset.getPriceBuyMax();
		if (column.equals("Value")) return eveAsset.getValue();
		if (column.equals("Base Price")) return eveAsset.getPriceBase();
		if (column.equals("Volume")) return eveAsset.getVolume();
		if (column.equals("Type ID")) return eveAsset.getTypeID();
		if (column.equals("Region")) return -1;
		if (column.equals("Type Count")) return eveAsset.getTypeCount();
		if (column.equals("Security")){
			try {
				return Double.valueOf( eveAsset.getSecurity());
			} catch (NumberFormatException ex){
				return -1;
			}
		}
		if (column.equals("Reprocessed")) return eveAsset.getPriceReprocessed();
		if (column.equals("Reprocessed Value")) return eveAsset.getValueReprocessed();

		return -1;
	}

}
