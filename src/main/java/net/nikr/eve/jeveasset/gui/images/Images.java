/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Images {

	private static Logger LOG = LoggerFactory.getLogger(Images.class);

	public final static Icon ICON_NOT_STARTED = Images.getIcon("bullet_black.png");
	public final static Icon ICON_WORKING = Images.getIcon("bullet_go.png");
	public final static Icon ICON_CANCELLED = Images.getIcon("bullet_red.png");
	public final static Icon ICON_DONE_OK = Images.getIcon("bullet_green.png");
	public final static Icon ICON_DONE_SOME = Images.getIcon("bullet_orange.png");
	public final static Icon ICON_DONE_ERROR = Images.getIcon("bullet_error.png");

	public final static Icon ICON_TABLE_RESIZE = Images.getIcon("application_view_detail.png");
	public final static Icon ICON_TABLE_SHOW = Images.getIcon("application_view_columns.png");
	public final static Icon ICON_TABLE_SAVE = Images.getIcon("table_save.png");

	public final static Icon ICON_PRICE_DATA = Images.getIcon("coins.png");
	public final static Icon ICON_USER_ITEM_PRICE = Images.getIcon("money.png");
	public final static Icon ICON_USER_ITEM_NAME = Images.getIcon("set_name.png");

	public final static Icon ICON_REPROCESSING = Images.getIcon("reprocessing.png");
	public final static Icon ICON_PROXY = Images.getIcon("server_connect.png");
	public final static Icon ICON_WINDOW = Images.getIcon("application.png");

	public final static Icon ICON_COPY = Images.getIcon("page_copy.png");
	public static final Icon ICON_CUT = Images.getIcon("cut.png");
	public static final Icon ICON_PASTE = Images.getIcon("page_paste.png");
	public static final Icon ICON_EDIT = Images.getIcon("edit.png");

	
	public final static Icon ICON_GROUPS = Images.getIcon("groups.png");
	public final static Icon ICON_RENAME = Images.getIcon("textfield_rename.png");
	public final static Icon ICON_DELETE = Images.getIcon("delete.png");
	public final static Icon ICON_ADD = Images.getIcon("add.png");
	public final static Icon ICON_STATION = Images.getIcon("station.png");
	public final static Icon ICON_SYSTEM = Images.getIcon("system.png");
	public final static Icon ICON_REGION = Images.getIcon("region.png");
	public final static Icon ICON_LOCATIONS = Images.getIcon("locations.png");
	public final static Icon ICON_UPDATE = Images.getIcon("update.png");
	public final static Icon ICON_EVE = Images.getIcon("eve.png");
	public final static Icon ICON_ARROW_DOWN = Images.getIcon("bullet_arrow_down.png");
	public final static Icon ICON_TOOLS = Images.getIcon("bricks.png");
	public final static Icon ICON_MODIFIED_ASSETS = Images.getIcon("textfield_rename.png");
	public final static Icon ICON_EXTERNAL_LINK = Images.getIcon("world_link.png");
	public final static Icon ICON_EVE_MARKETS = Images.getIcon("eve_markets.png");
	public final static Icon ICON_DOTLAN_EVEMAPS = Images.getIcon("dotlan_evemaps.png");
	public final static Icon ICON_EVE_CENTRAL = Images.getIcon("eve_central.png");
	public final static Icon ICON_EVE_METRICS = Images.getIcon("eve_metrics.png");
	public final static Icon ICON_CHRUKER = Images.getIcon("chruker.png");
	public final static Icon ICON_EVE_ONLINE = Images.getIcon("eve_online.png");


	public final static Icon ICON_NOT_CONTAIN = Images.getIcon("not_contain.png");
	public final static Icon ICON_CONTAIN = Images.getIcon("contain.png");
	public final static Icon ICON_NOT_EQUAL = Images.getIcon("not_equal.png");
	public final static Icon ICON_EQUAL = Images.getIcon("equal.png");
	public final static Icon ICON_GREATER_THEN = Images.getIcon("greater_then.png");
	public final static Icon ICON_GREATER_THEN_COLUMN = Images.getIcon("greater_then_column.png");
	public final static Icon ICON_LESS_THEN = Images.getIcon("less_then.png");
	public final static Icon ICON_LESS_THEN_COLUMN = Images.getIcon("less_then_column.png");

	public final static Icon ICON_AVERAGE =  Images.getIcon("shape_align_middle.png");
	public final static Icon ICON_VOLUME =  Images.getIcon("volume.png");

	public static final Icon ICON_EXPANDED =  Images.getIcon("expanded.png");
	public static final Icon ICON_COLLAPSED = Images.getIcon("collapsed.png");

	public final static Icon ICON_CLOSE = Images.getIcon("close.png");
	public final static Icon ICON_CLOSE_ACTIVE = Images.getIcon("close_active.png");
	public final static Icon ICON_CLOSE_CROSS = Images.getIcon("cross.png");
	public final static Icon ICON_CLEAR = Images.getIcon("page_white.png");
	public final static Icon ICON_SAVE = Images.getIcon("disk.png");
	public final static Icon ICON_FOLDER = Images.getIcon("folder.png");

	public final static Icon ICON_TOOL_ASSETS = Images.getIcon("safe16.png");
	public final static Icon ICON_TOOL_OVERVIEW = Images.getIcon("icon03_13.png");
	public final static Icon ICON_TOOL_MARKET_ORDERS = Images.getIcon("icon07_12.png");
	public final static Icon ICON_TOOL_VALUES = Images.getIcon("icon07_02.png");
	public final static Icon ICON_TOOL_INDUSTRY_JOBS = Images.getIcon("icon33_02.png");
	public final static Icon ICON_TOOL_ROUTING = Images.getIcon("routing.png");
	public final static Icon ICON_TOOL_MATERIALS = Images.getIcon("icon23_16.png");
	public final static Icon ICON_TOOL_SHIP_LOADOUTS = Images.getIcon("icon26_02.png");

	public final static Icon ICON_DIALOG_ACCOUNT_MANAGER = Images.getIcon("key.png");
	public final static Icon ICON_DIALOG_PROFILES = Images.getIcon("profile.png");
	public final static Icon ICON_DIALOG_SETTINGS = Images.getIcon("cog.png");
	public final static Icon ICON_DIALOG_ABOUT = Images.getIcon("information.png");

	public final static Icon ICON_TXT_HELP = Images.getIcon("help.png");
	
	public final static Icon ICON_JEVEASSETS64 = Images.getIcon("icon07_13.png");
	public final static Image IMAGE_JEVEASSETS16 = Images.getImage("safe16.png");

	public final static Image IMAGE_FOLDER = Images.getImage("folder.png");
	public final static Image IMAGE_DIALOG_ACCOUNT_MANAGER = Images.getImage("key.png");
	public final static Image IMAGE_DIALOG_ABOUT = Images.getImage("information.png");
	public final static Image IMAGE_DIALOG_CSV_EXPORT = Images.getImage("table_save.png");
	public final static Image IMAGE_DIALOG_PROFILES = Images.getImage("profile.png");
	public final static Image IMAGE_DIALOG_UPDATE = Images.getImage("update.png");
	public final static Image IMAGE_DIALOG_SETTINGS = Images.getImage("cog.png");
	public final static Image IMAGE_DIALOG_OVERVIEW_GROUPS = Images.getImage("groups.png");
	public final static Image IMAGE_DIALOG_LOADOUT_EXPORT = Images.getImage("icon26_02.png");


	private Images() {
	}

	private static ImageIcon getIcon(String s){
		return new ImageIcon(getBufferedImage(s));
	}

	public static Image getImage(String s) {
		return getBufferedImage(s);
		
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
