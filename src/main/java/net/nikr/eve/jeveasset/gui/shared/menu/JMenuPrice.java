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

package net.nikr.eve.jeveasset.gui.shared.menu;

import eve.nikr.net.client.ApiException;
import eve.nikr.net.client.api.FeedbackApi;
import eve.nikr.net.client.model.Feedback;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager.ContractPriceItem;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.UserItem;
import net.nikr.eve.jeveasset.gui.dialogs.settings.UserPriceSettingsPanel.UserPrice;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JMenuPrice<T> extends JAutoMenu<T> {

	private static final Logger LOG = LoggerFactory.getLogger(JMenuPrice.class);

	private enum MenuPriceAction {
		EDIT, DELETE
	}

	private final JMenuItem jEdit;
	private final JMenuItem jReset;

	public JMenuPrice(final Program program) {
		super(GuiShared.get().itemPriceTitle(), program); //
		this.setIcon(Images.SETTINGS_USER_PRICE.getIcon());

		ListenerClass listener =new ListenerClass();

		jEdit = new JMenuItem(GuiShared.get().itemEdit());
		jEdit.setIcon(Images.EDIT_EDIT.getIcon());
		jEdit.setActionCommand(MenuPriceAction.EDIT.name());
		jEdit.addActionListener(listener);
		add(jEdit);

		jReset = new JMenuItem(GuiShared.get().itemDelete());
		jReset.setIcon(Images.EDIT_DELETE.getIcon());
		jReset.setActionCommand(MenuPriceAction.DELETE.name());
		jReset.addActionListener(listener);
		add(jReset);
	}

	
	@Override
	public void updateMenuData() {
		jEdit.setEnabled(!menuData.getPrices().isEmpty());
		jReset.setEnabled(!menuData.getPrices().isEmpty() && program.getUserPriceSettingsPanel().containsKey(menuData.getPrices().keySet()));
	}

	private List<UserItem<Integer, Double>> createList() {
		List<UserItem<Integer, Double>> itemPrices = new ArrayList<>();
		for (Map.Entry<Integer, Double> entry : menuData.getPrices().entrySet()) {
			Item item = ApiIdConverter.getItem(Math.abs(entry.getKey()));
			String name = "";
			if (!item.isEmpty()) {
				if (item.isBlueprint()) {
					//Blueprint
					if (entry.getKey() < 0) {
						//Copy
						name = item.getTypeName() + " (BPC)";
					} else {
						//Original
						name = item.getTypeName() + " (BPO)";
					}
				} else {
					//Not blueprint
					name = item.getTypeName();
				}
			}
			itemPrices.add(new UserPrice(entry.getValue(), entry.getKey(), name));
		}
		return itemPrices;
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuPriceAction.EDIT.name().equals(e.getActionCommand())) {
				if (!menuData.getBpcTypeIDs().isEmpty() && !menuData.getPrices().isEmpty() && !menuData.getTypeNames().isEmpty()) {
					boolean updated = program.getUserPriceSettingsPanel().edit(createList());
					if (!updated) {
						return;
					}
					if (menuData.getContractPriceItems().isEmpty()) {
						return;
					}
					if (!Settings.get().getContractPriceSettings().isFeedbackAsked()) {
						Settings.get().getContractPriceSettings().setFeedbackAsked(true);
						int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame()
								, GuiShared.get().contractPriceReportMsg()
								, GuiShared.get().contractPriceReportTitle()
								, JOptionPane.OK_CANCEL_OPTION);
						Settings.get().getContractPriceSettings().setFeedback(value == JOptionPane.OK_OPTION);
						program.saveSettings("Contract Price (Send Feedback)");
					} 
					if (!Settings.get().getContractPriceSettings().isFeedback()) {
						return;
					}
					for (ContractPriceItem contractPriceItem : menuData.getContractPriceItems()) {
						sendFeedback(contractPriceItem);
					}
					ContractPriceManager.get().save();
				}
			} else if (MenuPriceAction.DELETE.name().equals(e.getActionCommand())) {
				if (!menuData.getBpcTypeIDs().isEmpty() && !menuData.getPrices().isEmpty() && !menuData.getTypeNames().isEmpty()) {
					program.getUserPriceSettingsPanel().delete(createList());
				}
			}
		}
	}

	private static void sendFeedback(final ContractPriceItem contractPriceItem) {
		FeedbackApi feedbackApi = new FeedbackApi();
		Feedback feedback = new Feedback();
		if (contractPriceItem.isBpc()) {
			feedback.setBpcRuns((long)contractPriceItem.getRuns());
			feedback.setIsBpc(true);
		}
		if (contractPriceItem.isBpo()) {
			feedback.setIsBpc(false);
		}
		if (contractPriceItem.isBpc() || contractPriceItem.isBpo()) {
			feedback.setMaterialEfficiency(contractPriceItem.getMe());
			feedback.setTimeEfficiency(contractPriceItem.getTe());
		}
		UserItem<Integer, Double> userPrice = getUserPrice(contractPriceItem);
		if (userPrice != null) {
			if (contractPriceItem.isBpc() && userPrice.getValue() > 0 && contractPriceItem.getRuns() > 0) {
				feedback.setSuggestedPricePerUnit(userPrice.getValue() / contractPriceItem.getRuns());
			} else {
				feedback.setSuggestedPricePerUnit(userPrice.getValue());
			}
		}
		feedback.setOldPricePerUnit(ContractPriceManager.get().getContractPrice(contractPriceItem, true));
		feedback.setOldPriceType(Settings.get().getContractPriceSettings().getContractPriceMode().name());
		feedback.setTypeId(contractPriceItem.getTypeID());
		feedback.setSecurity(Settings.get().getContractPriceSettings().getSecurityEnums());
		try {
			feedbackApi.giveFeedback(feedback);
			LOG.info("contract price feedback send");
		} catch (ApiException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	private static UserItem<Integer, Double> getUserPrice(ContractPriceManager.ContractPriceItem contractPriceItem) {
		if (contractPriceItem.isBpc()) {
			return Settings.get().getUserPrices().get(-contractPriceItem.getTypeID());
		} else {
			return Settings.get().getUserPrices().get(contractPriceItem.getTypeID());
		}
	}
}
