/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
	ASSETS_AVERAGE ("assets_average.png"),
	ASSETS_VOLUME ("assets_volume.png"),

	JOBS_INVENTION_SUCCESS ("jobs_invention_success.png"),

	ORDERS_SELL ("orders_sell.png"),
	ORDERS_SOLD ("orders_sold.png"),
	ORDERS_BUY ("orders_buy.png"),
	ORDERS_BOUGHT ("orders_bought.png"),
	ORDERS_ESCROW ("orders_escrow.png"),
	ORDERS_TO_COVER ("orders_to_cover.png"),

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
	EDIT_DATE ("edit_date.png"),
	EDIT_DELETE ("edit_delete.png"),
	EDIT_DELETE_WHITE ("edit_delete_white.png"),
	EDIT_ADD ("edit_add.png"),
	EDIT_ADD_WHITE ("edit_add_white.png"),
	EDIT_SET ("edit_set.png"),
	EDIT_IMPORT ("edit_import.png"),
	EDIT_EDIT_WHITE ("edit_edit_white.png"),
	EDIT_EDIT_BACKGROUND ("edit_edit_background.png"),
	EDIT_REDO ("edit_redo.png"),
	EDIT_UNDO ("edit_undo.png"),
	EDIT_SHOW ("edit_show.png"),

	FILTER_CLEAR ("filter_clear.png"),
	FILTER_SAVE ("filter_save.png"),
	FILTER_LOAD ("filter_load.png"),
	FILTER_LOAD_DEFAULT ("filter_load_default.png"),
	FILTER_NOT_CONTAIN ("filter_not_contain.png"),
	FILTER_CONTAIN ("filter_contain.png"),
	FILTER_NOT_EQUAL ("filter_not_equal.png"),
	FILTER_REGEX ("filter_regex.png"),
	FILTER_EQUAL ("filter_equal.png"),
	FILTER_EQUAL_DATE ("filter_equal_date.png"),
	FILTER_NOT_EQUAL_DATE ("filter_not_equal_date.png"),
	FILTER_GREATER_THAN ("filter_greater_than.png"),
	FILTER_LAST_DAYS ("filter_last_days.png"),
	FILTER_NEXT_DAYS ("filter_next_days.png"),
	FILTER_LAST_HOURS ("filter_last_hours.png"),
	FILTER_NEXT_HOURS ("filter_next_hours.png"),
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

	LINK_EVE_MARKETDATA ("link_eve_marketdata.png"),
	LINK_EVEINFO ("link_eveinfo.png"),
	LINK_DOTLAN_EVEMAPS ("link_dotlan_evemaps.png"),
	LINK_LOOKUP ("link_lookup.png"),
	LINK_CHRUKER ("link_chruker.png"),
	LINK_EVE_TYCOON ("link_eve_tycoon.png"),
	LINK_JANICE ("link_janice.png"),
	LINK_JANICE_32 ("link_janice_32.png"),
	LINK_FUZZWORK ("link_fuzzwork.png"),
	LINK_ZKILLBOARD ("link_zkillboard.png"),
	LINK_ZKILLBOARD_32 ("link_zkillboard_32.png"),
	LINK_ADAM4EVE ("link_adam4eve.png"),
	LINK_KHON_SPACE ("link_khon_space.png"),
	LINK_EVE_COOKBOOK ("link_eve_cookbook.png"),
	LINK_EVEMISSIONEER ("link_evemissioneer.png"),
	LINK_EVE_REF ("link_eve_ref.png"),
	LINK_EVE_MARKET_BROWSER ("link_eve_market_browser.png"),
	LINK_JITA_SPACE ("link_jita_space.png"),
	LINK_EVECONOMY ("link_eveconomy.png"),

	LOC_GROUPS ("loc_groups.png"),
	LOC_PIN_COMMAND ("loc_pin_command.png"),
	LOC_PIN_EXTRACTOR ("loc_pin_extractor.png"),
	LOC_PIN_PROCESSOR ("loc_pin_processor.png"),
	LOC_PIN_SPACEPORT ("loc_pin_spaceport.png"),
	LOC_PIN_STORAGE ("loc_pin_storage.png"),
	LOC_PLANET ("loc_planet.png"),
	LOC_STATION ("loc_station.png"),
	LOC_SYSTEM ("loc_system.png"),
	LOC_CONSTELLATION ("loc_constellation.png"),
	LOC_REGION ("loc_region.png"),
	LOC_CONTAINER ("loc_container.png"),
	LOC_CONTAINER_WHITE ("loc_container_white.png"),
	LOC_LOCATIONS ("loc_locations.png"),
	LOC_FLAG ("loc_flag.png"),
	LOC_OWNER ("loc_owner.png"),
	LOC_INCLUDE ("loc_include.png"),
	LOC_HANGAR_ITEMS ("loc_hangar_items.png"),
	LOC_HANGAR_SHIPS ("loc_hangar_ships.png"),
	LOC_DELIVERIES ("loc_deliveries.png"),
	LOC_OFFICE ("loc_office.png"),
	LOC_DIVISION ("loc_division.png"),
	LOC_INDUSTRY ("loc_industry.png"),
	LOC_MARKET ("loc_market.png"),
	LOC_CONTRACTS ("loc_contracts.png"),
	LOC_SAFTY ("loc_safty.png"),
	LOC_SHIP ("loc_ship.png"),
	LOC_CLONEBAY ("loc_clonebay.png"),

	INCLUDE_ASSETS_SELECTED ("include_assets_selected.png"),
	INCLUDE_ASSETS ("include_assets.png"),
	INCLUDE_JOBS_SELECTED ("include_jobs_selected.png"),
	INCLUDE_JOBS ("include_jobs.png"),
	INCLUDE_CONTRACTS_SELECTED ("include_contracts_selected.png"),
	INCLUDE_CONTRACTS ("include_contracts.png"),
	INCLUDE_ORDERS_SELECTED ("include_orders_selected.png"),
	INCLUDE_ORDERS ("include_orders.png"),
	INCLUDE_PACKAGED ("include_packaged.png"),
	INCLUDE_ADD_FILTER ("include_add_filter.png"),

	MISC_EVE ("misc_eve.png"),
	MISC_ESI ("misc_esi.png"),
	MISC_EXIT ("misc_exit.png"),
	MISC_HELP ("misc_help.png"),
	MISC_EXPANDED ("misc_expanded.png"),
	MISC_COLLAPSED ("misc_collapsed.png"),
	MISC_EXPANDED_WHITE ("misc_expanded_white.png"),
	MISC_COLLAPSED_WHITE ("misc_collapsed_white.png"),
	MISC_COLLAPSE ("misc_collapse.png"),
	MISC_AND ("misc_and.png"),
	MISC_ASSETS_32 ("misc_assets_32.png"),
	MISC_ASSETS_64 ("misc_assets_64.png"),
	MISC_DEBUG ("misc_debug.png"),
	MISC_UPDATE ("misc_update.png"),
	MISC_RUNS ("misc_runs.png"),
	MISC_BLUEPRINT ("misc_blueprint.png"),
	MISC_BPC ("misc_bpc.png"),
	MISC_BPO ("misc_bpo.png"),
	MISC_COPYING ("misc_copying.png"),
	MISC_INVENTION ("misc_invention.png"),
	MISC_MANUFACTURING ("misc_manufacturing.png"),
	MISC_SUM ("misc_sum.png"),
	MISC_FORMULA ("misc_formula.png"),
	MISC_REACTION ("misc_reaction.png"),
	MISC_CONTRACTS ("misc_contracts.png"),
	MISC_CONTRACTS_CORP ("misc_contracts_corp.png"),
	MISC_MARKET_ORDERS ("misc_market_orders.png"),
	MISC_AVG ("misc_avg.png"),
	MISC_COUNT ("misc_count.png"),
	MISC_INFO ("misc_info.png"),
	MISC_MAX ("misc_max.png"),
	MISC_MIN ("misc_min.png"),
	MISC_STATUS ("misc_status.png"),
	MISC_SOUNDS ("misc_sounds.png"),
	MISC_PLAY ("misc_play.png"),
	MISC_STOP ("misc_stop.png"),
	MISC_PARTNER ("misc_partner.png"),
	MISC_PARTYPARROT ("misc_partyparrot.gif"),
	MISC_MATERIALS ("misc_materials.png"),
	MISC_ORE ("misc_ore.png"),
	MISC_COMMODITY ("misc_commodity.png"),
	MISC_PI ("misc_pi.png"),
	MISC_XML ("misc_xml.png"),
	MISC_DATE ("misc_date.png"),
	MISC_NUMBER ("misc_number.png"),
	MISC_DISCORD ("misc_discord.png"),

	SETTINGS_TOOLS ("settings_tools.png"),
	SETTINGS_PRICE_DATA ("settings_price_data.png"),
	SETTINGS_USER_PRICE ("settings_user_price.png"),
	SETTINGS_USER_NAME ("settings_user_name.png"),
	SETTINGS_REPROCESSING ("settings_reprocessing.png"),
	SETTINGS_PROXY ("settings_proxy.png"),
	SETTINGS_WINDOW ("settings_window.png"),
	SETTINGS_COLORS ("settings_colors.png"),
	SETTINGS_COLOR_PICKER ("settings_color_picker.png"),
	SETTINGS_COLOR_CHECK_BLACK ("settings_color_check_black.png"),
	SETTINGS_COLOR_CHECK_BLACK_ALPHA ("settings_color_check_black_alpha.png"),
	SETTINGS_COLOR_CHECK_WHITE ("settings_color_check_white.png"),
	SETTINGS_COLOR_CHECK_WHITE_ALPHA ("settings_color_check_white_alpha.png"),
	SETTINGS_COLOR_FOREGROUND ("settings_color_foreground.png"),
	SETTINGS_COLOR_BACKGROUND ("settings_color_background.png"),
	SETTINGS_MANUFACTURING ("settings_manufacturing.png"),

	SLOTS_CONTRACTS ("slots_contracts.png"),
	SLOTS_CONTRACTS_CORP ("slots_contracts_corp.png"),
	SLOTS_MANUFACTURING ("slots_manufacturing.png"),
	SLOTS_MARKET_ORDERS ("slots_market_orders.png"),
	SLOTS_REACTIONS ("slots_reactions.png"),
	SLOTS_RESEARCH ("slots_research.png"),

	STOCKPILE_SHOPPING_LIST ("stockpile_shopping_list.png"),

	TAB_CLOSE ("tab_close.png"),
	TAB_CLOSE_ACTIVE ("tab_close_active.png"),

	TAG_GRAY ("tag_gray.png"),
	TAG_TICK ("tag_tick.png"),

	TABLE_COLUMN_RESIZE ("table_column_resize.png"),
	TABLE_COLUMN_SHOW ("table_column_show.png"),

	TOOL_ASSETS ("tool_assets.png"),
	TOOL_OVERVIEW ("tool_overview.png"),
	TOOL_MARKET_ORDERS ("tool_market_orders.png"),
	TOOL_VALUES ("tool_values.png"),
	TOOL_VALUE_TABLE ("tool_value_table.png"),
	TOOL_INDUSTRY_JOBS ("tool_industry_jobs.png"),
	TOOL_SLOTS ("tool_slots.png"),
	TOOL_ROUTING ("tool_routing.png"),
	TOOL_MATERIALS ("tool_materials.png"),
	TOOL_SHIP_LOADOUTS ("tool_ship_loadouts.png"),
	TOOL_STOCKPILE ("tool_stockpile.png"),
	TOOL_ITEMS ("tool_items.png"),
	TOOL_TRACKER ("tool_tracker.png"),
	TOOL_REPROCESSED ("tool_reprocessed.png"),
	TOOL_CONTRACTS ("tool_contracts.png"),
	TOOL_TRANSACTION ("tool_transaction.png"),
	TOOL_JOURNAL ("tool_journal.png"),
	TOOL_TREE ("tool_tree.png"),
	TOOL_PRICE_HISTORY ("tool_price_history.png"),
	TOOL_SKILLS ("tool_skills.png"),
	TOOL_MINING ("tool_mining.png"),
	TOOL_MINING_LOG ("tool_mining_log.png"),
	TOOL_MINING_GRAPH ("tool_mining_graph.png"),
	TOOL_EXTRACTIONS ("tool_extractions.png"),
	TOOL_PRICE_CHANGE ("tool_price_change.png"),

	UPDATE_NOT_STARTED ("update_not_started.png"),
	UPDATE_WORKING ("update_working.png"),
	UPDATE_CANCELLED ("update_cancelled.png"),
	UPDATE_DONE_OK ("update_done_ok.png"),
	UPDATE_DONE_INFO ("update_done_info.png"),
	UPDATE_DONE_SOME ("update_done_some.png"),
	UPDATE_DONE_ERROR ("update_done_error.png");

	private static final Logger LOG = LoggerFactory.getLogger(Images.class);
	private final String filename;
	private BufferedImage image = null;
	private Icon icon;

	Images(final String filename) {
		this.filename = filename;
	}

	public Icon getIcon() {
		load();
		return icon;
	}

	public Image getImage() {
		load();
		return image;
	}

	public String getFilename() {
		return filename;
	}

	private boolean load() {
		if (image == null) {
			image = getBufferedImage(filename);
			icon = new ImageIcon(image);
		}
		return (image != null);
	}

	public static boolean preload() {
		int count = 0;
		boolean ok = true;
		for (Images i : Images.values()) {
			if (!i.load()) {
				ok = false;
			}
			count++;
			SplashUpdater.setSubProgress((int) (count * 100.0 / Images.values().length));
		}
		SplashUpdater.setSubProgress(100);
		return ok;
	}

	public static BufferedImage getBufferedImage(final String s) {
		try {
			java.net.URL imgURL = Images.class.getResource(s);
			if (imgURL != null) {
				return ImageIO.read(imgURL);
			} else {
				LOG.warn("image: " + s + " not found (URL == null)");
			}
		} catch (IOException ex) {
			LOG.warn("image: " + s + " not found (IOException)");
		}
		return null;
	}
}
