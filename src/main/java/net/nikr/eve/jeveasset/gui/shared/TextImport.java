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

import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog.SimpleTextImport;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog.TextReturn;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class TextImport<T extends Enum<?> & SimpleTextImport> {
	private final Program program;
	private final String toolName;
	private final JTextDialog jTextDialog;

	public TextImport(Program program, String toolName) {
		this.program = program;
		this.toolName = toolName;

		jTextDialog = new JTextDialog(program.getMainWindow().getFrame());
	}

	public void importText(String text, T[] types, T selected, TextImportHandler<T> handler) {
		//Get string from clipboard
		TextReturn<T> textReturn = jTextDialog.importText(text, types, selected);
		String importText = textReturn.getText();
		T importType = textReturn.getType();
		if (importType != null) {
			Settings.lock("Import (" + toolName + ")");
			Settings.get().putImportSettings(toolName, importType);
			Settings.unlock("Import (" + toolName + ")");
			program.saveSettings("Import (" + toolName + ")");
		}
		if (importText == null || importType == null) {
			return; //Cancelled
		}

		//Validate Input
		importText = importText.trim();
		if (importText.isEmpty()) { //Empty sting
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().textEmpty(), GuiShared.get().textImport(), JOptionPane.PLAIN_MESSAGE);
			return;
		}

		handler.addItems(textReturn);
	}

	public static interface TextImportHandler<T extends SimpleTextImport> {
		public void addItems(TextReturn<T> textReturn);
	}
}
