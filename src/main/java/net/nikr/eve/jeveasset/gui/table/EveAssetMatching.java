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

package net.nikr.eve.jeveasset.gui.table;

import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.EveAsset;


public class EveAssetMatching {

	public EveAssetMatching() {

	}

	public boolean matches(EveAsset item, AssetFilter assetFilter) {
		return matches(item, assetFilter.getColumn(), assetFilter.getMode(), assetFilter.getText());

	}
	public boolean matches(EveAsset item, String column, String mode, String text) {
			final String haystack;
			final double value;
			if (column.equals("All")){
				haystack =  "\r\n"
							+ item.getCategory() + "\r\n"
							+ item.getContainer() + "\r\n"
							+ item.getFlag() + "\r\n"
							+ item.getGroup() + "\r\n"
							+ item.getLocation() + "\r\n"
							+ item.getMeta() + "\r\n"
							+ item.getName() + "\r\n"
							+ item.getOwner() + "\r\n"
							+ item.getCount() + "\r\n"
							+ item.getId() + "\r\n"
							+ item.getPrice() + "\r\n"
							+ item.getPriceSellMin() + "\r\n"
							+ item.getPriceBuyMax() + "\r\n"
							+ item.getValue() + "\r\n"
							+ item.getPriceBase() + "\r\n"
							+ item.getVolume() + "\r\n"
							+ item.getTypeId() + "\r\n"
							+ item.getRegion() + "\r\n"
							+ item.getTypeCount() + "\r\n"
						;
				value = -1;
			} else if (column.equals("Name")){
				haystack = item.getName();
				value = -1;
			} else if (column.equals("Group")){
				haystack = item.getGroup();
				value = -1;
			} else if (column.equals("Category")){
				haystack = item.getCategory();
				value = -1;
			} else if (column.equals("Owner")){
				haystack = item.getOwner();
				value = -1;
			} else if (column.equals("Count")){
				haystack = String.valueOf(item.getCount());
				value = item.getCount();
			} else if (column.equals("Location")){
				haystack = item.getLocation();
				value = -1;
			} else if (column.equals("Container")){
				haystack = item.getContainer();
				value = -1;
			} else if (column.equals("Flag")){
				haystack = item.getFlag();
				value = -1;
			} else if (column.equals("Price")){
				haystack = String.valueOf(item.getPrice());
				value = item.getPrice();
			} else if (column.equals("Meta")){
				haystack = item.getMeta();
				value = -1;
			} else if (column.equals("ID")){
				haystack = String.valueOf(item.getId());
				value = item.getId();
			} else if (column.equals("Sell Min")){
				haystack = String.valueOf(item.getPriceSellMin());
				value = item.getPriceSellMin();
			} else if (column.equals("Buy Max")){
				haystack = String.valueOf(item.getPriceBuyMax());
				value = item.getPriceBuyMax();
			} else if (column.equals("Value")){
				haystack = String.valueOf(item.getValue());
				value = item.getValue();
			} else if (column.equals("Base Price")){
				haystack = String.valueOf(item.getPriceBase());
				value = item.getPriceBase();
			} else if (column.equals("Volume")){
				haystack = String.valueOf(item.getVolume());
				value = item.getVolume();
			}  else if (column.equals("Type ID")){
				haystack = String.valueOf(item.getTypeId());
				value = item.getTypeId();
			}  else if (column.equals("Region")){
				haystack = item.getRegion();
				value = -1;
			}  else if (column.equals("Type Count")){
				haystack = String.valueOf(item.getTypeCount());
				value = item.getTypeCount();
			}    else {
				haystack = "";
				value = -1;
			}

			if (mode.equals(AssetFilter.MODE_GREATER_THAN)){
				double number;
				try{
					number = Double.valueOf(text);
				} catch (NumberFormatException ex){
					return false;
				}
				return (value > number);
			}
			if (mode.equals(AssetFilter.MODE_LESS_THAN)){
				double number;
				try{
					number = Double.valueOf(text);
				} catch (NumberFormatException ex){
					return false;
				}
				return (value < number);
			}
			if (mode.equals(AssetFilter.MODE_CONTAIN)){
				return haystack.toLowerCase().contains(text.toLowerCase());
			}
			if (mode.equals(AssetFilter.MODE_CONTAIN_NOT)){
				return !haystack.toLowerCase().contains(text.toLowerCase());
			}
			if (mode.equals(AssetFilter.MODE_EQUALS)){
				if (value >= 0){
					double number;
					try{
						number = Double.valueOf(text);
						return (value == number);
					} catch (NumberFormatException ex){
						
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
			if (mode.equals(AssetFilter.MODE_EQUALS_NOT)){
				if (value >= 0){
					double number;
					try{
						number = Double.valueOf(text);
						return (value != number);
					} catch (NumberFormatException ex){

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

}
