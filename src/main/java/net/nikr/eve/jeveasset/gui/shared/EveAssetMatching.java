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

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EveAssetMatching {
	private static final Logger LOG = LoggerFactory.getLogger(EveAssetMatching.class);

	//TODO i18n Use localized input
	//public static final Locale LOCALE = Locale.getDefault();
	
	public static final Locale LOCALE = Locale.ENGLISH; //Use english AKA US_EN
	
	public EveAssetMatching() {

	}

	public boolean matches(Asset item, AssetFilter assetFilter) {
		return matches(item, assetFilter.getColumn(), assetFilter.getMode(), assetFilter.getText(), assetFilter.getColumnMatch());
	}
	public boolean matches(final Asset eveAsset, final String column, final AssetFilter.Mode mode, String filterValue, String columnMatch) {
		//Compare numbers (Greater/Less then [column])
		boolean isNumericComparison = mode.equals(AssetFilter.Mode.MODE_GREATER_THAN) || mode.equals(AssetFilter.Mode.MODE_GREATER_THAN_COLUMN) || mode.equals(AssetFilter.Mode.MODE_LESS_THAN) || mode.equals(AssetFilter.Mode.MODE_LESS_THAN_COLUMN);
		
		//Compare columns - Set filterValue to column value
		if (columnMatch != null) filterValue = getString(eveAsset, columnMatch, isNumericComparison);
		
		//If a column is meta and is numeric comparison - get meta number
		if (isNumericComparison && columnMatch == null && column.equals("Meta")){
			filterValue = getMetaNumber(filterValue);
		}
		
		//Get filter number or null if NaN	
		Double filterNumber = getNumber(filterValue);
		
		//if filter is numeric: update text for string comparison
		if (filterNumber != null) filterValue = Formater.compareFormat(filterNumber);
		
		//Get column value
		String columnValue = getString(eveAsset, column, isNumericComparison);

		if (isNumericComparison){
			//Column number
			final Double columnNumber = getNumber(columnValue);
			
			//null = false
			if (columnNumber == null || filterNumber == null) return false;
			
			//Greater then [column]
			if (mode.equals(AssetFilter.Mode.MODE_GREATER_THAN) || mode.equals(AssetFilter.Mode.MODE_GREATER_THAN_COLUMN)){
				return filterNumber < columnNumber;
			}
			
			//Less then [column]
			if (mode.equals(AssetFilter.Mode.MODE_LESS_THAN) || mode.equals(AssetFilter.Mode.MODE_LESS_THAN_COLUMN)){
				return filterNumber > columnNumber;
			}
		}
		//Contain
		if (mode.equals(AssetFilter.Mode.MODE_CONTAIN)){
			return columnValue.toLowerCase().contains(filterValue.toLowerCase());
		}
		//Does not contain
		if (mode.equals(AssetFilter.Mode.MODE_CONTAIN_NOT)){
			return !columnValue.toLowerCase().contains(filterValue.toLowerCase());
		}
		//Equals
		if (mode.equals(AssetFilter.Mode.MODE_EQUALS)){
			if (column.equals("All")){
				return columnValue.toLowerCase().contains("\n"+filterValue.toLowerCase()+"\r");
			} else {
				return columnValue.toLowerCase().equals(filterValue.toLowerCase());
			}
		}
		//Does not equals
		if (mode.equals(AssetFilter.Mode.MODE_EQUALS_NOT)){
			if (column.equals("All")){
				return !columnValue.toLowerCase().contains("\n"+filterValue.toLowerCase()+"\r");
			} else {
				return !columnValue.toLowerCase().equals(filterValue.toLowerCase());
			}
		}
		return false;
			
	}

	private String getString(Asset eveAsset, String column, boolean isNumericComparison){
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
						+ Formater.compareFormat(eveAsset.getCount()) + "\r\n"
						+ Formater.compareFormat(eveAsset.getItemID()) + "\r\n"
						+ Formater.compareFormat(eveAsset.getPrice()) + "\r\n"
						+ Formater.compareFormat(eveAsset.getPriceSellMin()) + "\r\n"
						+ Formater.compareFormat(eveAsset.getPriceBuyMax()) + "\r\n"
						+ Formater.compareFormat(eveAsset.getValue()) + "\r\n"
						+ Formater.compareFormat(eveAsset.getPriceBase()) + "\r\n"
						+ Formater.compareFormat(eveAsset.getVolume()) + "\r\n"
						+ Formater.compareFormat(eveAsset.getTypeID()) + "\r\n"
						+ eveAsset.getRegion() + "\r\n"
						+ Formater.compareFormat(eveAsset.getTypeCount()) + "\r\n"
						+ Formater.compareFormat(getSecurityNumber(eveAsset.getSecurity())) + "\r\n"
						+ Formater.compareFormat(eveAsset.getPriceReprocessed()) + "\r\n"
						+ Formater.compareFormat(eveAsset.getValueReprocessed()) + "\r\n"
						+ eveAsset.getSingleton() + "\r\n"
					;
		}
		if (column.equals("Name")) return eveAsset.getName();
		if (column.equals("Group")) return eveAsset.getGroup();
		if (column.equals("Category")) return eveAsset.getCategory();
		if (column.equals("Owner")) return eveAsset.getOwner();
		if (column.equals("Count")) return Formater.compareFormat(eveAsset.getCount());
		if (column.equals("Location")) return eveAsset.getLocation();
		if (column.equals("Container")) return eveAsset.getContainer();
		if (column.equals("Flag")) return eveAsset.getFlag();
		if (column.equals("Price")) return Formater.compareFormat(eveAsset.getPrice());
		if (column.equals("Meta")){
			if (eveAsset.getMeta().isEmpty()){
				return "0";
			} else if (isNumericComparison){
				return getMetaNumber(eveAsset.getMeta());
			} else {
				return eveAsset.getMeta();
			}
		}
		if (column.equals("Item ID")) return Formater.compareFormat(eveAsset.getItemID());
		if (column.equals("Sell Min")) return Formater.compareFormat(eveAsset.getPriceSellMin());
		if (column.equals("Buy Max")) return Formater.compareFormat(eveAsset.getPriceBuyMax());
		if (column.equals("Value")) return Formater.compareFormat(eveAsset.getValue());
		if (column.equals("Base Price")) return Formater.compareFormat(eveAsset.getPriceBase());
		if (column.equals("Volume")) return Formater.compareFormat(eveAsset.getVolume());
		if (column.equals("Type ID")) return Formater.compareFormat(eveAsset.getTypeID());
		if (column.equals("Region")) return eveAsset.getRegion();
		if (column.equals("Type Count")) return Formater.compareFormat(eveAsset.getTypeCount());
		if (column.equals("Security")) return Formater.compareFormat(getSecurityNumber(eveAsset.getSecurity()));
		if (column.equals("Reprocessed")) return Formater.compareFormat(eveAsset.getPriceReprocessed());
		if (column.equals("Reprocessed Value")) return Formater.compareFormat(eveAsset.getValueReprocessed());
		if (column.equals("Singleton")) return eveAsset.getSingleton();
		if (column.equals("Total Volume")) return Formater.compareFormat(eveAsset.getVolumeTotal());
		return "";
	}

	private double getSecurityNumber(String security){
		try {
			return Double.valueOf(security);
		} catch (NumberFormatException ex){
			LOG.info("Ignoring the exception: " + ex.getMessage());
			return -1;
		}
	}
	
	private String getMetaNumber(String meta){
		Pattern p = Pattern.compile("[\\+-\\.,\\d]+");
		Matcher m = p.matcher(meta);
		if (m.find()){
			return meta.substring(m.start(), m.end());
		} else {
			return "";
		}
	}
	
	private Double getNumber(String filterValue){
		//Used to check if parsing was successful
		ParsePosition position = new ParsePosition(0);
		//Parse number using the Locale
		Number n = NumberFormat.getInstance(LOCALE).parse(filterValue, position);
		if (n != null && position.getIndex() == filterValue.length()){ //Numeric
			return n.doubleValue();
		} else { //String
			return null;
		}
	}
}
