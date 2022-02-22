/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.MarketOrdersSettings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JIntegerField;
import net.nikr.eve.jeveasset.gui.shared.components.JLabelMultiline;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory.ValueFlag;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class MarketOrdersToolSettingsPanel extends JSettingsPanel {

	private final JCheckBox jSaveHistory;
	private final JIntegerField jExpireWarnDays;
	private final JIntegerField jRemainingWarnPercent;

	public MarketOrdersToolSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().marketOrders(), Images.TOOL_MARKET_ORDERS.getIcon());

		JLabelMultiline jSaveHistoryWarning = new JLabelMultiline(DialoguesSettings.get().saveHistoryWarning(), 2);
		JLabel jExpireWarnDaysLabel = new JLabel(DialoguesSettings.get().expireWarnDays());
		JLabel jRemainingWarnPercentsLabel = new JLabel(DialoguesSettings.get().remainingWarnPercent());

		jSaveHistory = new JCheckBox(DialoguesSettings.get().marketOrdersSaveHistory());
		jExpireWarnDays = new JIntegerField("0", ValueFlag.POSITIVE_AND_ZERO);
		jExpireWarnDays.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {}

			@Override
			public void focusLost(FocusEvent e) {
				try {
					//Max time of market orders is 90 days so anything over that doesn't make sense
					if(Integer.parseInt(jExpireWarnDays.getText()) > 90) {
						jExpireWarnDays.setText("90");
					}
				} catch (NumberFormatException ex) {
					//No problem
				}
			}
		});

		jRemainingWarnPercent = new JIntegerField("10", ValueFlag.POSITIVE_AND_ZERO);
		jRemainingWarnPercent.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {}

			@Override
			public void focusLost(FocusEvent e) {
				try {
					//Max percent that makes sense is 100 so anything over that should be lowered to 100
					if(Integer.parseInt(jRemainingWarnPercent.getText()) > 100) {
						jRemainingWarnPercent.setText("100");
					}
				} catch (NumberFormatException ex) {
					//No problem
				}
			}
		});

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jSaveHistory)
				.addComponent(jSaveHistoryWarning, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jExpireWarnDaysLabel)
						.addComponent(jRemainingWarnPercentsLabel)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jExpireWarnDays)
						.addComponent(jRemainingWarnPercent)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jSaveHistory, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSaveHistoryWarning, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jExpireWarnDaysLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jExpireWarnDays, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jRemainingWarnPercentsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRemainingWarnPercent, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}
		
	@Override
	public boolean save() {
		int oldExpireWarnDays = Settings.get().getMarketOrdersSettings().getExpireWarnDays();
		int oldRemainingWarnPercent = Settings.get().getMarketOrdersSettings().getRemainingWarnPercent();

		boolean marketOrderHistory = jSaveHistory.isSelected();

		int expireWarnDays;
		try {
			expireWarnDays = Integer.parseInt(jExpireWarnDays.getText());
		} catch (NumberFormatException ex) {
			expireWarnDays = 0;
		}

		int remainingWarnPercent;
		try {
			remainingWarnPercent = Integer.parseInt(jRemainingWarnPercent.getText());
		} catch (NumberFormatException ex) {
			remainingWarnPercent = 0;
		}

		Settings.get().setMarketOrderHistory(marketOrderHistory);
		Settings.get().getMarketOrdersSettings().setExpireWarnDays(expireWarnDays);
		Settings.get().getMarketOrdersSettings().setRemainingWarnPercent(remainingWarnPercent);

		//This may get awkward with more checks
		return (oldExpireWarnDays != expireWarnDays) || (oldRemainingWarnPercent != remainingWarnPercent);
	}

	@Override
	public void load() {
		final MarketOrdersSettings marketOrdersSettings = Settings.get().getMarketOrdersSettings();

		jSaveHistory.setSelected(Settings.get().isMarketOrderHistory());
		jExpireWarnDays.setText(String.valueOf(marketOrdersSettings.getExpireWarnDays()));
		jRemainingWarnPercent.setText(String.valueOf(marketOrdersSettings.getRemainingWarnPercent()));
	}
}
