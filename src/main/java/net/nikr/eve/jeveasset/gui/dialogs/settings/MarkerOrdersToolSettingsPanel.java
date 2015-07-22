/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.nikr.eve.jeveasset.gui.dialogs.settings;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;

/**
 *
 * @author Niklas
 */
public class MarkerOrdersToolSettingsPanel extends JSettingsPanel {

	private final JCheckBox jSaveHistory;

	public MarkerOrdersToolSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().markerOrders(), Images.TOOL_MARKET_ORDERS.getIcon());

		jSaveHistory = new JCheckBox(DialoguesSettings.get().markerOrdersSaveHistory());

		JTextArea jSaveHistoryWarning = new JTextArea(DialoguesSettings.get().saveHistoryWarning());
		jSaveHistoryWarning.setFont(this.getPanel().getFont());
		jSaveHistoryWarning.setBackground(this.getPanel().getBackground());
		jSaveHistoryWarning.setLineWrap(true);
		jSaveHistoryWarning.setWrapStyleWord(true);
		jSaveHistoryWarning.setFocusable(false);
		jSaveHistoryWarning.setEditable(false);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jSaveHistory)
				.addComponent(jSaveHistoryWarning)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jSaveHistory, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jSaveHistoryWarning)
		);
	}
		
	@Override
	public boolean save() {
		Settings.get().setMarketOrderHistory(jSaveHistory.isSelected());
		return false;
	}

	@Override
	public void load() {
		jSaveHistory.setSelected(Settings.get().isMarketOrderHistory());
	}
}
