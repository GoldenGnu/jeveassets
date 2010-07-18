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

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.gui.images.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JMenuLookup extends JMenuTools implements ActionListener{

	private final static Logger LOG = LoggerFactory.getLogger(JMenuLookup.class);

	private static final String ACTION_BROWSE_EVE_CENTRAL = "ACTION_BROWSE_EVE_CENTRAL";
	private static final String ACTION_BROWSE_EVE_METRICS = "ACTION_BROWSE_EVE_METRICS";
	private static final String ACTION_BROWSE_EVE_MARKETS = "ACTION_BROWSE_EVE_MARKETS";
	private static final String ACTION_BROWSE_GAMES_CHRUKER = "ACTION_BROWSE_GAMES_CHRUKER";
	private static final String ACTION_BROWSE_EVEMAPS_DOTLAN_STATION = "ACTION_BROWSE_EVEMAPS_DOTLAN_STATION";
	private static final String ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM = "ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM";
	private static final String ACTION_BROWSE_EVEMAPS_DOTLAN_REGION = "ACTION_BROWSE_EVEMAPS_DOTLAN_REGION";

	protected JMenuLookup(Arguments arguments) {
		super("Lookup", Images.ICON_EXTERNAL_LINK, arguments);
	}

	private void browse(String s){
		boolean opened = false;
		LOG.info("Opening: {}", s);
		URI uri;
		try {
			uri = new URI(s);
		} catch (URISyntaxException ex) {
			uri = null;
		}
		if (Desktop.isDesktopSupported() && uri != null) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(uri);
					opened = true;
				} catch (IOException ex) {
					LOG.warn("	Opening Failed: "+ex.getMessage());
				}
			}
		}
		if (!opened){
			LOG.warn("	Opening File Failed");
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Could not open "+s, "Open File", JOptionPane.PLAIN_MESSAGE);
		}
	}

	@Override
	protected void createMenu() {
		JMenuItem menuItem;
		JMenu menu;

		if (station != null || system != null || region != null){
			menu = new JMenu("Dotlan EveMaps");
			menu.setIcon(Images.ICON_DOTLAN_EVEMAPS);
			this.add(menu);

			if (station != null){
				menuItem = new JMenuItem("Station");
				menuItem.setIcon(Images.ICON_STATION);
				menuItem.setActionCommand(ACTION_BROWSE_EVEMAPS_DOTLAN_STATION);
				menuItem.addActionListener(this);
				menu.add(menuItem);
			}
			if (system != null){
				menuItem = new JMenuItem("System");
				menuItem.setIcon(Images.ICON_SYSTEM);
				menuItem.setActionCommand(ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM);
				menuItem.addActionListener(this);
				menu.add(menuItem);
			}
			if (region != null){
				menuItem = new JMenuItem("Region");
				menuItem.setIcon(Images.ICON_REGION);
				menuItem.setActionCommand(ACTION_BROWSE_EVEMAPS_DOTLAN_REGION);
				menuItem.addActionListener(this);
				menu.add(menuItem);
			}
		}

		if ((station != null || system != null || region != null) && typeId != 0) this.addSeparator();

		if (typeId != 0){
			if (isMarketGroup){
				menuItem = new JMenuItem("Eve-Central");
				menuItem.setIcon(Images.ICON_EVE_CENTRAL);
				menuItem.setActionCommand(ACTION_BROWSE_EVE_CENTRAL);
				menuItem.addActionListener(this);
				this.add(menuItem);

				menuItem = new JMenuItem("Eve-Metrics");
				menuItem.setIcon(Images.ICON_EVE_METRICS);
				menuItem.setActionCommand(ACTION_BROWSE_EVE_METRICS);
				menuItem.addActionListener(this);
				this.add(menuItem);

				menuItem = new JMenuItem("Eve-Markets");
				menuItem.setIcon(Images.ICON_EVE_MARKETS);
				menuItem.setActionCommand(ACTION_BROWSE_EVE_MARKETS);
				menuItem.addActionListener(this);
				this.add(menuItem);
			}
			menuItem = new JMenuItem("Chruker Item Database");
			menuItem.setIcon(Images.ICON_CHRUKER);
			menuItem.setActionCommand(ACTION_BROWSE_GAMES_CHRUKER);
			menuItem.addActionListener(this);
			this.add(menuItem);


		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_BROWSE_EVE_CENTRAL.equals(e.getActionCommand())){
			browse("http://www.eve-central.com/home/quicklook.html?typeid="+typeId);
		}
		if (ACTION_BROWSE_EVE_METRICS.equals(e.getActionCommand())){
			browse("http://eve-metrics.com/q/"+typeId);
		}
		if (ACTION_BROWSE_EVE_MARKETS.equals(e.getActionCommand())){
			browse("http://www.eve-markets.net/detail.php?typeid="+typeId);
		}
		if (ACTION_BROWSE_GAMES_CHRUKER.equals(e.getActionCommand())){
			browse("http://games.chruker.dk/eve_online/item.php?type_id="+typeId);
		}
		if (ACTION_BROWSE_EVEMAPS_DOTLAN_STATION.equals(e.getActionCommand())){
			browse("http://evemaps.dotlan.net/outpost/"+station.replace(" ", "_"));
		}
		if (ACTION_BROWSE_EVEMAPS_DOTLAN_SYSTEM.equals(e.getActionCommand())){
			browse("http://evemaps.dotlan.net/system/"+system.replace(" ", "_"));
		}
		if (ACTION_BROWSE_EVEMAPS_DOTLAN_REGION.equals(e.getActionCommand())){
			browse("http://evemaps.dotlan.net/map/"+region.replace(" ", "_"));
		}
	}
}
