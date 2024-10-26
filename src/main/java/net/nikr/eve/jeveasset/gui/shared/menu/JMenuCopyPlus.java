/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Map;
import javax.swing.JMenuItem;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.i18n.GuiShared;

public class JMenuCopyPlus<T> extends JAutoMenu<T> {

	private final DecimalFormat number = new DecimalFormat("0");
	private final JTextDialog jTextDialog;
	private final JMenuItem jEveMultiBuy;


	public JMenuCopyPlus(Program program) {
		super(GuiShared.get().copyPlus(), program);
		setIcon(Images.EDIT_COPY.getIcon());

		jTextDialog = new JTextDialog(program.getMainWindow().getFrame());

		jEveMultiBuy = new JMenuItem(GuiShared.get().copyEveMultiBuy());
		jEveMultiBuy.setIcon(Images.MISC_EVE.getIcon());
		jEveMultiBuy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder builder = new StringBuilder();
				for (Map.Entry<Item, Long> entry : menuData.getItemCounts().entrySet()) {
					builder.append(entry.getKey().getTypeName());
					builder.append(" ");
					builder.append(number.format(entry.getValue()));
					builder.append("\r\n");
				}
				jTextDialog.exportText(builder.toString());
			}
		});
		add(jEveMultiBuy);
	}

	@Override
	protected void updateMenuData() {
		jEveMultiBuy.setEnabled(!menuData.getItemCounts().isEmpty());
	}
}
