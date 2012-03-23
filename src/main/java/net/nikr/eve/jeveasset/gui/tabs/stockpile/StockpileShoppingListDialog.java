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
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class StockpileShoppingListDialog extends JDialogCentered implements ActionListener{

	private final static String ACTION_CLIPBOARD_STOCKPILE = "ACTION_CLIPBOARD_STOCKPILE";
	private final static String ACTION_CLOSE = "ACTION_CLOSE";
	
	private JTextArea jText;
	private JButton jClose;
	
	public StockpileShoppingListDialog(Program program) {
		super(program,  TabsStockpile.get().shoppingList(), Images.TOOL_STOCKPILE.getImage());
		
		JButton jCopyToClipboard = new JButton(TabsStockpile.get().clipboardStockpile());
		jCopyToClipboard.setActionCommand(ACTION_CLIPBOARD_STOCKPILE);
		jCopyToClipboard.addActionListener(this);
		
		jClose = new JButton(TabsStockpile.get().close());
		jClose.setActionCommand(ACTION_CLOSE);
		jClose.addActionListener(this);
		
		jText = new JTextArea();
		jText.setEditable(false);
		jText.setFont(jPanel.getFont());
		jText.setBackground(jPanel.getBackground());
		JCopyPopup.install(jText);
		
		JScrollPane jTextScroll = new JScrollPane(jText);
		
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jCopyToClipboard)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(jTextScroll, 500, 500, 500)
					.addComponent(jClose)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jCopyToClipboard, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jTextScroll, 400, 400, 400)
				.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}
	
	void show(Stockpile stockpile){
		String s = "";
		double volume = 0;
		double value = 0;
		for (Stockpile.StockpileItem stockpileItem : stockpile.getItems()){
			if (stockpileItem.getTypeID() > 0 && stockpileItem.getCountNeeded() < 0){
				s = s + Formater.longFormat(Math.abs(stockpileItem.getCountNeeded()))+"x " +stockpileItem.getName()+"\r\n";
				volume = volume + stockpileItem.getVolumeNeeded();
				value = value + stockpileItem.getValueNeeded();
			}
		}
		s = s + "\r\n";
		s = s + TabsStockpile.get().totalToHaul()+Formater.doubleFormat(Math.abs(volume))+ "\r\n";
		s = s + TabsStockpile.get().estimatedMarketValue()+Formater.iskFormat(Math.abs(value))+ "\r\n";
		jText.setText(s);
		super.setVisible(true);
	}
	
	private void copyToClipboard(){
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
		return jClose;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void save() {}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_CLIPBOARD_STOCKPILE.equals(e.getActionCommand())){
			copyToClipboard();
		}
		if (ACTION_CLOSE.equals(e.getActionCommand())){
			super.setVisible(false);
		}
	}
	
}
