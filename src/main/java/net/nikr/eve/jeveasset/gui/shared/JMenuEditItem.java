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

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserNameSettingsPanel.UserName;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel.UserPrice;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuEditItem extends JMenuTool implements ActionListener {

	public final static String ACTION_USER_PRICE_EDIT = "ACTION_USER_PRICE_EDIT";
	public final static String ACTION_USER_NAME_EDIT = "ACTION_SET_ITEM_NAME";
	
	private Asset asset = null;
	
	public JMenuEditItem(Program program, Object object) {
		super(GuiShared.get().editItem(), program, object); //
		this.setIcon(Images.EDIT_EDIT.getIcon());

		JMenuItem jMenuItem;
		
		jMenuItem = new JMenuItem(GuiShared.get().editPrice());
		jMenuItem.setIcon(Images.SETTINGS_USER_PRICE.getIcon());
		jMenuItem.setEnabled(typeId != 0);
		jMenuItem.setActionCommand(ACTION_USER_PRICE_EDIT);
		jMenuItem.addActionListener(this);
		add(jMenuItem);

		if (object instanceof Asset){
			asset = (Asset) object;
			jMenuItem = new JMenuItem(GuiShared.get().editName());
			jMenuItem.setIcon(Images.SETTINGS_USER_NAME.getIcon());
			jMenuItem.setEnabled(typeId != 0);
			jMenuItem.setActionCommand(ACTION_USER_NAME_EDIT);
			jMenuItem.addActionListener(this);
			add(jMenuItem);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_USER_PRICE_EDIT.equals(e.getActionCommand())) {
			UserPrice userPrice;
			if (asset != null){
				userPrice = new UserPrice(asset);
			} else {
				userPrice = new UserPrice(price, typeId, typeName);
			}
			program.getUserPriceSettingsPanel().edit(userPrice);
		}
		if (ACTION_USER_NAME_EDIT.equals(e.getActionCommand())){
			program.getUserNameSettingsPanel().edit(new UserName(asset));
		}
	}
	
}
