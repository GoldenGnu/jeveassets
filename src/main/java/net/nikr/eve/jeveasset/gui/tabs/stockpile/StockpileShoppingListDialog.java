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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JNumberField;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


class StockpileShoppingListDialog extends JDialogCentered implements ActionListener, CaretListener {

	private static final String ACTION_CLIPBOARD_STOCKPILE = "ACTION_CLIPBOARD_STOCKPILE";
	private static final String ACTION_CLOSE = "ACTION_CLOSE";

	private JTextArea jText;
	private JButton jClose;
	private JTextField jPercent;

	private Stockpile stockpile;

	StockpileShoppingListDialog(final Program program) {
		super(program,  TabsStockpile.get().shoppingList(), Images.TOOL_STOCKPILE.getImage());

		JButton jCopyToClipboard = new JButton(TabsStockpile.get().clipboardStockpile(), Images.EDIT_COPY.getIcon());
		jCopyToClipboard.setActionCommand(ACTION_CLIPBOARD_STOCKPILE);
		jCopyToClipboard.addActionListener(this);

		JLabel jPercentFullLabel = new JLabel(TabsStockpile.get().percentFull());
		JLabel jPercentLabel = new JLabel(TabsStockpile.get().percent());

		jPercent = new JNumberField("");
		jPercent.addCaretListener(this);

		jClose = new JButton(TabsStockpile.get().close());
		jClose.setActionCommand(ACTION_CLOSE);
		jClose.addActionListener(this);

		jText = new JTextArea();
		jText.setEditable(false);
		jText.setFont(jPanel.getFont());
		jText.setBackground(jPanel.getBackground());
		JCopyPopup.install(jText);

		JSeparator jSeparator = new JSeparator(SwingConstants.VERTICAL);

		JScrollPane jTextScroll = new JScrollPane(jText);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jCopyToClipboard)
					.addGap(10)
					.addComponent(jSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(jPercentFullLabel)
					.addComponent(jPercent, 100, 100, 100)
					.addComponent(jPercentLabel)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(jTextScroll, 500, 500, 500)
					.addComponent(jClose)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCopyToClipboard, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSeparator, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPercentFullLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPercent, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPercentLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTextScroll, 400, 400, 400)
				.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}

	void show(final Stockpile showStockpile) {
		this.stockpile = showStockpile;
		jPercent.setText("100");
		updateList();
		super.setVisible(true);
	}

	private void updateList() {
		long percent;
		try {
			percent = Long.valueOf(jPercent.getText());
			if (percent <= 0) {
				percent = 100;
			}
		} catch (NumberFormatException e) {
			percent = 100;
		}
		String s = "";
		double volume = 0;
		double value = 0;
		for (Stockpile.StockpileItem stockpileItem : stockpile.getItems()) {
			if (stockpileItem.getItemTypeID() != 0) { //Ignore Total
				final double minimumCount = (stockpileItem.getCountMinimum() * percent / 100.0);
				final double countNeeded = Math.ceil(minimumCount - stockpileItem.getCountNow());
				if (countNeeded > 0) {
					volume = volume + (countNeeded * stockpileItem.getVolume());
					value = value + (countNeeded * stockpileItem.getPrice());
					s = s + Formater.longFormat(countNeeded) + "x " + stockpileItem.getName() + "\r\n";
				}
			}
		}
		if (s.isEmpty()) {
			s = TabsStockpile.get().nothingNeeded();
		} else {
			s = s + "\r\n";
			s = s + TabsStockpile.get().totalToHaul() + Formater.doubleFormat(Math.abs(volume)) + "\r\n";
			s = s + TabsStockpile.get().estimatedMarketValue() + Formater.iskFormat(Math.abs(value)) + "\r\n";
		}
		if (percent != 100) {
			s = stockpile.getName() + " (" + percent + TabsStockpile.get().percent() + ")\r\n\r\n" + s;
		} else {
			s = stockpile.getName() + "\r\n\r\n" + s;
		}
		jText.setText(s);
	}

	private void copyToClipboard() {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			try {
				sm.checkSystemClipboardAccess();
			} catch (Exception ex) {
				return;
			}
		}
		Toolkit tk = Toolkit.getDefaultToolkit();
		StringSelection data = new StringSelection(jText.getText());
		Clipboard cp = tk.getSystemClipboard();
		cp.setContents(data, null);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jClose;
	}

	@Override
	protected JButton getDefaultButton() {
		return null;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() { }

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_CLIPBOARD_STOCKPILE.equals(e.getActionCommand())) {
			copyToClipboard();
		}
		if (ACTION_CLOSE.equals(e.getActionCommand())) {
			super.setVisible(false);
		}
	}

	@Override
	public void caretUpdate(final CaretEvent e) {
		updateList();
	}
}
