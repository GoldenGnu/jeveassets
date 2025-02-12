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

package net.nikr.eve.jeveasset.gui.shared;

import java.util.Map;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog.TextReturn;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.local.text.TextImportType;


public class TextImport {
	private final Program program;
	private final JTextDialog jTextDialog;

	public TextImport(Program program) {
		this.program = program;

		jTextDialog = new JTextDialog(program.getMainWindow().getFrame());
	}

	public void importText(TextImportType[] types, TextImportHandler handler) {
		importText("", types, null, handler);
	}

	private void importText(String text, TextImportType[] types, TextImportType selected, TextImportHandler handler) {
		//Get string from clipboard
		TextReturn<TextImportType> textReturn = jTextDialog.importText(text, types, selected);
		String importText = textReturn.getText();
		TextImportType importType = textReturn.getType();
		if (importText == null || importType == null) {
			return; //Cancelled
		}

		//Validate Input
		importText = importText.trim();
		if (importText.isEmpty()) { //Empty sting
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().textEmpty(), GuiShared.get().textImport(), JOptionPane.PLAIN_MESSAGE);
			return;
		}

		Map<Integer, Double> data = importType.importText(importText);
		//Validate Output
		if (data == null || data.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().textInvalid(), GuiShared.get().textImport(), JOptionPane.PLAIN_MESSAGE);
			importText(importText, types, importType, handler); //Again!
			return;
		}
		//Add items
		handler.addItems(data, importType);
	}

	public static interface TextImportHandler {
		public void addItems(Map<Integer, Double> data, TextImportType type);
	}
}
