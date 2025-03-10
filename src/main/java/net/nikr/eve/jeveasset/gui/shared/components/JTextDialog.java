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

package net.nikr.eve.jeveasset.gui.shared.components;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Colors;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CopyHandler;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JTextDialog extends JDialogCentered {

	private enum TextDialogAction {
		TO_CLIPBOARD,
		TO_FILE,
		FROM_CLIPBOARD,
		FROM_FILE,
		CANCEL,
		OK
	}

	private final JTextAreaPlaceholder jText;
	private final JButton jToClipboard;
	private final JButton jFromClipboard;
	private final JButton jToFile;
	private final JButton jFromFile;
	private final JButton jOK;
	private final JButton jCancel;
	private final Color importColor;
	private final JCustomFileChooser jFileChooser;

	private String returnValue = null;

	public JTextDialog(Window window) {
		super(null, "", window, null);

		jFileChooser = new JCustomFileChooser("txt");
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		ListenerClass listener = new ListenerClass();

		jToClipboard = new JButton(GuiShared.get().textToClipboard(), Images.EDIT_COPY.getIcon());
		jToClipboard.setActionCommand(TextDialogAction.TO_CLIPBOARD.name());
		jToClipboard.addActionListener(listener);

		jToFile = new JButton(GuiShared.get().textToFile(), Images.FILTER_SAVE.getIcon());
		jToFile.setActionCommand(TextDialogAction.TO_FILE.name());
		jToFile.addActionListener(listener);

		jFromClipboard = new JButton(GuiShared.get().textFromClipboard(), Images.EDIT_PASTE.getIcon());
		jFromClipboard.setActionCommand(TextDialogAction.FROM_CLIPBOARD.name());
		jFromClipboard.addActionListener(listener);

		jFromFile = new JButton(GuiShared.get().textFromFile(), Images.FILTER_LOAD.getIcon());
		jFromFile.setActionCommand(TextDialogAction.FROM_FILE.name());
		jFromFile.addActionListener(listener);

		jOK = new JButton();
		jOK.setActionCommand(TextDialogAction.OK.name());
		jOK.addActionListener(listener);

		jCancel = new JButton(GuiShared.get().cancel());
		jCancel.setActionCommand(TextDialogAction.CANCEL.name());
		jCancel.addActionListener(listener);

		jText = new JTextAreaPlaceholder();
		jText.setTabSize(4);
		jText.setLineWrap(true);
		jText.setEditable(false);
		jText.setFont(jPanel.getFont());

		importColor = jText.getBackground();

		JScrollPane jTextScroll = new JScrollPane(jText);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jToClipboard)
					.addComponent(jToFile)
					.addComponent(jFromClipboard)
					.addComponent(jFromFile)
				)
				.addComponent(jTextScroll, 500, 500, Integer.MAX_VALUE)
				.addGroup(layout.createSequentialGroup()
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jToClipboard, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jToFile, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFromClipboard, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFromFile, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jTextScroll, 400, 400, Integer.MAX_VALUE)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jText;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() { }

	public void setLineWrap(boolean wrap) {
		jText.setLineWrap(wrap);
	}

	public String importText() {
		return importText("", "");
	}

	public String importText(String text) {
		return importText(text, "");
	}

	public String importText(String text, String example) {
		getDialog().setTitle(GuiShared.get().textImport());
		jText.setEditable(true);
		jText.setOpaque(true);
		jText.setBackground(importColor);
		jText.setText(text);
		if (example == null) {
			example = ""; //null not allowed!
		}
		jText.setPlaceholderText(example);
		jOK.setText(GuiShared.get().ok());
		jCancel.setVisible(true);
		jFromClipboard.setVisible(true);
		jFromFile.setVisible(true);
		jToClipboard.setVisible(false);
		jToFile.setVisible(false);
		returnValue = null;
		setVisible(true);
		return returnValue;
	}

	public void exportText(String text) {
		getDialog().setTitle(GuiShared.get().textExport());
		jText.setEditable(false);
		jText.setOpaque(false);
		jText.setBackground(Colors.COMPONENT_TRANSPARENT.getColor());
		jText.setText(text);
		jOK.setText(GuiShared.get().textClose());
		jCancel.setVisible(false);
		jFromClipboard.setVisible(false);
		jFromFile.setVisible(false);
		jToClipboard.setVisible(true);
		jToFile.setVisible(true);
		setVisible(true);
	}

	private void toFile() {
		int showSaveDialog = jFileChooser.showSaveDialog(getDialog());
		if (showSaveDialog == JCustomFileChooser.APPROVE_OPTION) {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(jFileChooser.getSelectedFile()));
				writer.write(jText.getText());
				writer.close();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(getDialog(), GuiShared.get().textSaveFailMsg(), GuiShared.get().textSaveFailTitle(), JOptionPane.WARNING_MESSAGE);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException ex) {
						//Ohh well we tried our best
					}
				}
			}
		}
	}

	private void fromFile() {
		int showSaveDialog = jFileChooser.showOpenDialog(getDialog());
		if (showSaveDialog == JCustomFileChooser.APPROVE_OPTION) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(jFileChooser.getSelectedFile()));
				String line;
				StringBuilder builder = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					builder.append("\r\n");
				}
				jText.setText(builder.toString());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(getDialog(), GuiShared.get().textLoadFailMsg(), GuiShared.get().textLoadFailTitle(), JOptionPane.WARNING_MESSAGE);
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException ex) {
					//Ohh well we tried our best
				}
			}
		}
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (TextDialogAction.TO_CLIPBOARD.name().equals(e.getActionCommand())) {
				CopyHandler.toClipboard(jText.getText());
			}
			if (TextDialogAction.TO_FILE.name().equals(e.getActionCommand())) {
				toFile();
			}
			if (TextDialogAction.FROM_CLIPBOARD.name().equals(e.getActionCommand())) {
				CopyHandler.paste(jText);
			}
			if (TextDialogAction.FROM_FILE.name().equals(e.getActionCommand())) {
				fromFile();
			}
			if (TextDialogAction.OK.name().equals(e.getActionCommand())) {
				returnValue = jText.getText();
				setVisible(false);
			}
			if (TextDialogAction.CANCEL.name().equals(e.getActionCommand())) {
				returnValue = null;
				setVisible(false);
			}
		}
	}
}
