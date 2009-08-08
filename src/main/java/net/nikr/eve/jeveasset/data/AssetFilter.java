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

package net.nikr.eve.jeveasset.data;


public class AssetFilter {
	/*
	public static final int COLUMN_ALL = 1;
	public static final int COLUMN_NAME = 2;
	public static final int COLUMN_GROUP = 3;
	public static final int COLUMN_CATEGORY = 4;
	public static final int COLUMN_OWNER = 5;
	public static final int COLUMN_COUNT = 6;
	public static final int COLUMN_LOCATION = 7;
	public static final int COLUMN_CONTAINER = 8;
	public static final int COLUMN_FLAG = 9;
	public static final int COLUMN_PRICE = 10;
	public static final int COLUMN_META = 11;
	public static final int COLUMN_ID = 12;
	public static final int COLUMN_SELL_MIN = 13;
	public static final int COLUMN_BUY_MAX = 14;
	public static final int COLUMN_VALUE = 15;
	public static final int COLUMN_BASE_PRICE = 16;
	public static final int COLUMN_VOLUME = 17;
	public static final int COLUMN_TYPE_ID = 18;
	 */


	public static final String MODE_CONTAIN = "Contains";
	public static final String MODE_CONTAIN_NOT = "Does not contain";
	public static final String MODE_EQUALS = "Equals";
	public static final String MODE_EQUALS_NOT = "Does not equal";
	public static final String MODE_GREATER_THAN = "Greater than";
	public static final String MODE_LESS_THAN = "Less than";
	
	public static final String AND = "And";
	public static final String OR = "Or";

	private String column;
	private String text;
	private String mode;
	private boolean and;

	public AssetFilter(String column, String text, String mode, boolean and) {
		this.column = column;
		this.text = text;
		this.mode = mode;
		this.and = and;
	}

	public boolean isAnd() {
		return and;
	}

	public String getColumn() {
		return column;
	}

	public String getMode() {
		return mode;
	}

	public String getText() {
		return text;
	}

	/*
	public static int columnNameToInt(String sColumn){
		int column = AssetFilter.COLUMN_ALL;
		if (sColumn.equals("Name")) column = AssetFilter.COLUMN_NAME;
		if (sColumn.equals("Group")) column = AssetFilter.COLUMN_GROUP;
		if (sColumn.equals("Category")) column = AssetFilter.COLUMN_CATEGORY;
		if (sColumn.equals("Owner")) column = AssetFilter.COLUMN_OWNER;
		if (sColumn.equals("Count")) column = AssetFilter.COLUMN_COUNT;
		if (sColumn.equals("Location")) column = AssetFilter.COLUMN_LOCATION;
		if (sColumn.equals("Container")) column = AssetFilter.COLUMN_CONTAINER;
		if (sColumn.equals("Flag")) column = AssetFilter.COLUMN_FLAG;
		if (sColumn.equals("Price")) column = AssetFilter.COLUMN_PRICE;
		if (sColumn.equals("Meta")) column = AssetFilter.COLUMN_META;
		if (sColumn.equals("ID")) column = AssetFilter.COLUMN_ID;
		if (sColumn.equals("Sell Min")) column = AssetFilter.COLUMN_SELL_MIN;
		if (sColumn.equals("Buy Max")) column = AssetFilter.COLUMN_BUY_MAX;
		if (sColumn.equals("Value")) column = AssetFilter.COLUMN_VALUE;
		if (sColumn.equals("Base Price")) column = AssetFilter.COLUMN_BASE_PRICE;
		if (sColumn.equals("Volume")) column = AssetFilter.COLUMN_VOLUME;
		if (sColumn.equals("Type ID")) column = AssetFilter.COLUMN_TYPE_ID;
		return column;
	}
	public static String intToColumnName(int column){
		switch (column){
			case AssetFilter.COLUMN_ALL:
				return "All";
			case AssetFilter.COLUMN_NAME:
				return "Name";
			case AssetFilter.COLUMN_GROUP:
				return "Group";
			case AssetFilter.COLUMN_CATEGORY:
				return "Category";
			case AssetFilter.COLUMN_OWNER:
				return "Owner";
			case AssetFilter.COLUMN_COUNT:
				return "Count";
			case AssetFilter.COLUMN_LOCATION:
				return "Location";
			case AssetFilter.COLUMN_CONTAINER:
				return "Container";
			case AssetFilter.COLUMN_FLAG:
				return "Flag";
			case AssetFilter.COLUMN_PRICE:
				return "Price";
			case AssetFilter.COLUMN_META:
				return "Meta";
			case AssetFilter.COLUMN_ID:
				return "ID";
			case AssetFilter.COLUMN_SELL_MIN:
				return "Sell Min";
			case AssetFilter.COLUMN_BUY_MAX:
				return "Buy Max";
			case AssetFilter.COLUMN_VALUE:
				return "Value";
			case AssetFilter.COLUMN_BASE_PRICE:
				return "Base Price";
			case AssetFilter.COLUMN_VOLUME:
				return "Volume";
			case AssetFilter.COLUMN_TYPE_ID:
				return "Type ID";
		}
		return "";
	}
	 */
}
