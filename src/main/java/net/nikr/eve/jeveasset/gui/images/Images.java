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

package net.nikr.eve.jeveasset.gui.images;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import net.nikr.eve.jeveasset.SplashUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Images {
	ASSETS_CLEAR_FIELDS ("assets_clear_fields.png"),
	ASSETS_SAVE_FILTERS ("assets_save_filters.png"),
	ASSETS_LOAD_FILTER ("assets_load_filter.png"),
	ASSETS_AVERAGE ("assets_average.png"),
	ASSETS_VOLUME ("assets_volume.png"),

	DIALOG_UPDATE ("dialog_update.png"),
	DIALOG_UPDATE_DISABLED ("dialog_update_disabled.png"),
	DIALOG_ACCOUNTS ("dialog_accounts.png"),
	DIALOG_PROFILES ("dialog_profiles.png"),
	DIALOG_SETTINGS ("dialog_settings.png"),
	DIALOG_ABOUT ("dialog_about.png"),
	DIALOG_CSV_EXPORT ("dialog_csv_export.png"),

	EDIT_COPY ("edit_copy.png"),
	EDIT_CUT ("edit_cut.png"),
	EDIT_PASTE ("edit_paste.png"),
	EDIT_EDIT ("edit_edit.png"),
	EDIT_RENAME ("edit_rename.png"),
	EDIT_DELETE ("edit_delete.png"),
	EDIT_ADD ("edit_add.png"),

	FILTER_NOT_CONTAIN ("filter_not_contain.png"),
	FILTER_CONTAIN ("filter_contain.png"),
	FILTER_NOT_EQUAL ("filter_not_equal.png"),
	FILTER_EQUAL ("filter_equal.png"),
	FILTER_EQUAL_DATE ("filter_equal_date.png"),
	FILTER_NOT_EQUAL_DATE ("filter_not_equal_date.png"),
	FILTER_GREATER_THAN ("filter_greater_than.png"),
	FILTER_LESS_THAN ("filter_less_than.png"),
	FILTER_AFTER ("filter_after.png"),
	FILTER_BEFORE ("filter_before.png"),
	FILTER_CONTAIN_COLUMN("filter_contain_column.png"),
	FILTER_EQUAL_COLUMN ("filter_equal_column.png"),
	FILTER_NOT_CONTAIN_COLUMN ("filter_not_contain_column.png"),
	FILTER_NOT_EQUAL_COLUMN ("filter_not_equal_column.png"),
	FILTER_GREATER_THAN_COLUMN ("filter_greater_than_column.png"),
	FILTER_LESS_THAN_COLUMN ("filter_less_than_column.png"),
	FILTER_AFTER_COLUMN ("filter_after_column.png"),
	FILTER_BEFORE_COLUMN ("filter_before_column.png"),

	LINK_LOOKUP ("link_lookup.png"),
	LINK_EVE_MARKETS ("link_eve_markets.png"),
	LINK_DOTLAN_EVEMAPS ("link_dotlan_evemaps.png"),
	LINK_EVE_CENTRAL ("link_eve_central.png"),
	LINK_EVE_MARKETDATA ("link_eve_marketdata.png"),
	LINK_CHRUKER ("link_chruker.png"),

	LOC_GROUPS ("loc_groups.png"),
	LOC_STATION ("loc_station.png"),
	LOC_SYSTEM ("loc_system.png"),
	LOC_REGION ("loc_region.png"),
	LOC_LOCATIONS ("loc_locations.png"),

	MISC_EVE ("misc_eve.png"),
	MISC_EXIT ("misc_exit.png"),
	MISC_HELP ("misc_help.png"),
	MISC_EXPANDED ("misc_expanded.png"),
	MISC_COLLAPSED ("misc_collapsed.png"),
	MISC_ASSETS_64 ("misc_assets_64.png"),

	SETTINGS_TOOLS ("settings_tools.png"),
	SETTINGS_PRICE_DATA ("settings_price_data.png"),
	SETTINGS_USER_PRICE ("settings_user_price.png"),
	SETTINGS_USER_NAME ("settings_user_name.png"),
	SETTINGS_REPROCESSING ("settings_reprocessing.png"),
	SETTINGS_PROXY ("settings_proxy.png"),
	SETTINGS_WINDOW ("settings_window.png"),

	TAB_CLOSE ("tab_close.png"),
	TAB_CLOSE_ACTIVE ("tab_close_active.png"),

	TABLE_COLUMN_RESIZE ("table_column_resize.png"),
	TABLE_COLUMN_SHOW ("table_column_show.png"),
	TABLE_COLUMN_SETTINGS ("table_column_settings.png"),

	TOOL_ASSETS ("tool_assets.png"),
	TOOL_OVERVIEW ("tool_overview.png"),
	TOOL_MARKET_ORDERS ("tool_market_orders.png"),
	TOOL_VALUES ("tool_values.png"),
	TOOL_INDUSTRY_JOBS ("tool_industry_jobs.png"),
	TOOL_ROUTING ("tool_routing.png"),
	TOOL_MATERIALS ("tool_materials.png"),
	TOOL_SHIP_LOADOUTS ("tool_ship_loadouts.png"),
	TOOL_STOCKPILE ("tool_stockpile.png"),

	UPDATE_NOT_STARTED ("update_not_started.png"),
	UPDATE_WORKING ("update_working.png"),
	UPDATE_CANCELLED ("update_cancelled.png"),
	UPDATE_DONE_OK ("update_done_ok.png"),
	UPDATE_DONE_SOME ("update_done_some.png"),
	UPDATE_DONE_ERROR ("update_done_error.png"),
	;

	private static Logger LOG = LoggerFactory.getLogger(Images.class);
	private final String filename;   // in kilograms
	private BufferedImage image = null;

	Images(String filename) {
		this.filename = filename;
	}

	public Icon getIcon(){
		load();
		return new ImageIcon(image);
	}

	public Image getImage() {
		load();
		return image;
	}

	public String getFilename() {
		return filename;
	}

	private boolean load(){
		if (image == null){
			image = getBufferedImage(filename);
		}
		return (image != null);
	}

	public static boolean preload(){
		int count = 0;
		boolean ok = true;
		for (Images i : Images.values()){
			if (!i.load()){
				ok = false;
			}
			count++;
			SplashUpdater.setSubProgress((int)(count * 100.0 / Images.values().length));
		}
		return ok;
	}

	public static BufferedImage getBufferedImage(String s) {
		try {
			java.net.URL imgURL = Images.class.getResource(s);
			if (imgURL != null){
				return ImageIO.read(imgURL);
			} else {
				LOG.warn("image: "+s+" not found (URL == null)");
			}
		} catch (IOException ex) {
			LOG.warn("image: "+s+" not found (IOException)");
		}
		return null;
	}
}
