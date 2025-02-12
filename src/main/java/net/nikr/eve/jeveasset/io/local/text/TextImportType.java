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
package net.nikr.eve.jeveasset.io.local.text;

import java.util.Map;
import javax.swing.Icon;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog.TextImport;


public enum TextImportType implements TextImport {
	ISK_PER_HOUR(new ImportIskPerHour()),
	STCOKPILE_SHOPPING_LIST(new ImportShoppingList()),
	EVE_MULTIBUY(new ImportEveMultibuy()),
	EFT(new ImportEft()),;

	private final AbstractTextImport textImport;

	private TextImportType(AbstractTextImport textImport) {
		this.textImport = textImport;
	}

	@Override
	public String getExample() {
		return textImport.getExample();
	}

	@Override
	public Icon getIcon() {
		return textImport.getIcon();
	}

	@Override
	public String getType() {
		return textImport.getType();
	}

	public String getName() {
		return textImport.getName();
	}

	public Map<Integer, Double> importText(String text) {
		return textImport.importText(text);
	}	
}
