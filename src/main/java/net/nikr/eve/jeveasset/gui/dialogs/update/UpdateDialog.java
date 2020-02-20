/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.dialogs.update;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.ApiType;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.tabs.values.AssetValue;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.esi.EsiAccountBalanceGetter;
import net.nikr.eve.jeveasset.io.esi.EsiAssetsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiBlueprintsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiBookmarksGetter;
import net.nikr.eve.jeveasset.io.esi.EsiContractItemsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiContractsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiDivisionsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiIndustryJobsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiJournalGetter;
import net.nikr.eve.jeveasset.io.esi.EsiLocationsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiMarketOrdersGetter;
import net.nikr.eve.jeveasset.io.esi.EsiNameGetter;
import net.nikr.eve.jeveasset.io.esi.EsiOwnerGetter;
import net.nikr.eve.jeveasset.io.esi.EsiPlanetaryInteractionGetter;
import net.nikr.eve.jeveasset.io.esi.EsiShipGetter;
import net.nikr.eve.jeveasset.io.esi.EsiTransactionsGetter;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.online.ContractPriceGetter;
import net.nikr.eve.jeveasset.data.settings.ContractPriceManager;
import net.nikr.eve.jeveasset.io.online.PriceDataGetter;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker;


public class UpdateDialog extends JDialogCentered {

	private enum UpdateDialogAction {
		CANCEL, UPDATE, CHANGED, CHECK_ALL
	}

	private final JCheckBox jCheckAll;
	private final JCheckBox jMarketOrders;
	private final JLabel jMarketOrdersLeftFirst;
	private final JLabel jMarketOrdersLeftLast;
	private final JCheckBox jJournal;
	private final JLabel jJournalLeftFirst;
	private final JLabel jJournalLeftLast;
	private final JCheckBox jTransactions;
	private final JLabel jTransactionsLeftFirst;
	private final JLabel jTransactionsLeftLast;
	private final JCheckBox jIndustryJobs;
	private final JLabel jIndustryJobsLeftFirst;
	private final JLabel jIndustryJobsLeftLast;
	private final JCheckBox jAccountBalance;
	private final JLabel jAccountBalanceLeftFirst;
	private final JLabel jAccountBalanceLeftLast;
	private final JCheckBox jContracts;
	private final JLabel jContractsLeftFirst;
	private final JLabel jContractsLeftLast;
	private final JCheckBox jAssets;
	private final JLabel jAssetsLeftFirst;
	private final JLabel jAssetsLeftLast;
	private final JCheckBox jBlueprints;
	private final JLabel jBlueprintsLeftFirst;
	private final JLabel jBlueprintsLeftLast;
	private final JCheckBox jBookmarks;
	private final JLabel jBookmarksLeftFirst;
	private final JLabel jBookmarksLeftLast;
	private final JRadioButton jContractPricesAll;
	private final JRadioButton jContractPricesNew;
	private final JRadioButton jContractPricesNone;
	private final JLabel jContractPricesLeft;
	private final JRadioButton jPriceDataAll;
	private final JRadioButton jPriceDataNew;
	private final JRadioButton jPriceDataNone;
	private final JLabel jPriceDataLeft;
	private final JButton jUpdate;
	private final JButton jCancel;
	private final List<JCheckBox> jCheckBoxes = new ArrayList<JCheckBox>();
	private final Timer timer;

	public UpdateDialog(final Program program) {
		super(program, DialoguesUpdate.get().update(), Images.DIALOG_UPDATE.getImage());

		ListenerClass listener = new ListenerClass();

		timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update(false);
			}
		});

		jCheckAll = new JCheckBox(General.get().all());
		jCheckAll.setActionCommand(UpdateDialogAction.CHECK_ALL.name());
		jCheckAll.addActionListener(listener);

		jMarketOrders = new JCheckBox(DialoguesUpdate.get().marketOrders());
		jJournal = new JCheckBox(DialoguesUpdate.get().journal());
		jTransactions = new JCheckBox(DialoguesUpdate.get().transactions());
		jIndustryJobs = new JCheckBox(DialoguesUpdate.get().industryJobs());
		jAccountBalance = new JCheckBox(DialoguesUpdate.get().accountBlances());
		jContracts = new JCheckBox(DialoguesUpdate.get().contracts());
		jAssets = new JCheckBox(DialoguesUpdate.get().assets());
		jBlueprints = new JCheckBox(DialoguesUpdate.get().blueprints());
		jBookmarks = new JCheckBox(DialoguesUpdate.get().bookmarks());
		jContractPricesAll = new JRadioButton(DialoguesUpdate.get().contractPrices());
		jContractPricesAll.setActionCommand(UpdateDialogAction.CHANGED.name());
		jContractPricesAll.addActionListener(listener);
		jContractPricesNew = new JRadioButton(DialoguesUpdate.get().priceDataNew());
		jContractPricesNew.setActionCommand(UpdateDialogAction.CHANGED.name());
		jContractPricesNew.addActionListener(listener);
		jContractPricesNone = new JRadioButton(DialoguesUpdate.get().priceDataNone());
		jContractPricesNone.setActionCommand(UpdateDialogAction.CHANGED.name());
		jContractPricesNone.addActionListener(listener);
		ButtonGroup jContractPricesGroup = new ButtonGroup();
		jContractPricesGroup.add(jContractPricesAll);
		jContractPricesGroup.add(jContractPricesNew);
		jContractPricesGroup.add(jContractPricesNone);
		jPriceDataAll = new JRadioButton(DialoguesUpdate.get().priceData());
		jPriceDataAll.setActionCommand(UpdateDialogAction.CHANGED.name());
		jPriceDataAll.addActionListener(listener);
		jPriceDataNew = new JRadioButton(DialoguesUpdate.get().priceDataNew());
		jPriceDataNew.setActionCommand(UpdateDialogAction.CHANGED.name());
		jPriceDataNew.addActionListener(listener);
		jPriceDataNone = new JRadioButton(DialoguesUpdate.get().priceDataNone());
		jPriceDataNone.setActionCommand(UpdateDialogAction.CHANGED.name());
		jPriceDataNone.addActionListener(listener);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(jPriceDataAll);
		buttonGroup.add(jPriceDataNew);
		buttonGroup.add(jPriceDataNone);

		jCheckBoxes.add(jMarketOrders);
		jCheckBoxes.add(jJournal);
		jCheckBoxes.add(jTransactions);
		jCheckBoxes.add(jIndustryJobs);
		jCheckBoxes.add(jAccountBalance);
		jCheckBoxes.add(jContracts);
		jCheckBoxes.add(jAssets);
		jCheckBoxes.add(jBlueprints);
		jCheckBoxes.add(jBookmarks);
		for (JCheckBox jCheckBox : jCheckBoxes) {
			jCheckBox.setActionCommand(UpdateDialogAction.CHANGED.name());
			jCheckBox.addActionListener(listener);
		}

		JLabel jLeftFirst = new JLabel(DialoguesUpdate.get().firstAccount());
		jMarketOrdersLeftFirst = new JLabel();
		jJournalLeftFirst = new JLabel();
		jTransactionsLeftFirst = new JLabel();
		jIndustryJobsLeftFirst = new JLabel();
		jAccountBalanceLeftFirst = new JLabel();
		jContractsLeftFirst = new JLabel();
		jAssetsLeftFirst = new JLabel();
		jBlueprintsLeftFirst = new JLabel();
		jBookmarksLeftFirst = new JLabel();
		jPriceDataLeft = new JLabel();

		JLabel jLeftLast = new JLabel(DialoguesUpdate.get().allAccounts());
		jMarketOrdersLeftLast = new JLabel();
		jJournalLeftLast = new JLabel();
		jTransactionsLeftLast = new JLabel();
		jIndustryJobsLeftLast = new JLabel();
		jAccountBalanceLeftLast = new JLabel();
		jContractsLeftLast = new JLabel();
		jAssetsLeftLast = new JLabel();
		jBlueprintsLeftLast = new JLabel();
		jBookmarksLeftLast = new JLabel();
		jContractPricesLeft = new JLabel();

		jUpdate = new JButton(DialoguesUpdate.get().update());
		jUpdate.setActionCommand(UpdateDialogAction.UPDATE.name());
		jUpdate.addActionListener(listener);

		jCancel = new JButton(DialoguesUpdate.get().cancel());
		jCancel.setActionCommand(UpdateDialogAction.CANCEL.name());
		jCancel.addActionListener(listener);
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup()
								.addComponent(jCheckAll)
								.addComponent(jMarketOrders)
								.addComponent(jJournal)
								.addComponent(jTransactions)
								.addComponent(jIndustryJobs)
								.addComponent(jAccountBalance)
								.addComponent(jContracts)
								.addComponent(jAssets)
								.addComponent(jBlueprints)
								.addComponent(jBookmarks)
							)
							.addGap(20)
							.addGroup(layout.createParallelGroup(Alignment.TRAILING)
								.addComponent(jLeftFirst)
								.addComponent(jMarketOrdersLeftFirst)
								.addComponent(jJournalLeftFirst)
								.addComponent(jTransactionsLeftFirst)
								.addComponent(jIndustryJobsLeftFirst)
								.addComponent(jAccountBalanceLeftFirst)
								.addComponent(jContractsLeftFirst)
								.addComponent(jAssetsLeftFirst)
								.addComponent(jBlueprintsLeftFirst)
								.addComponent(jBookmarksLeftFirst)
							)
							.addGap(20)
						)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup()
								.addComponent(jContractPricesAll)
								.addComponent(jPriceDataAll)
							)
							.addGap(10)
							.addGroup(layout.createParallelGroup()
								.addComponent(jContractPricesNew)
								.addComponent(jPriceDataNew)
							)
							.addGap(10)
							.addGroup(layout.createParallelGroup()
								.addComponent(jContractPricesNone)
								.addComponent(jPriceDataNone)
							)
						)
					)
					.addGroup(layout.createParallelGroup(Alignment.TRAILING)
						.addComponent(jLeftLast)
						.addComponent(jMarketOrdersLeftLast)
						.addComponent(jJournalLeftLast)
						.addComponent(jTransactionsLeftLast)
						.addComponent(jIndustryJobsLeftLast)
						.addComponent(jAccountBalanceLeftLast)
						.addComponent(jContractsLeftLast)
						.addComponent(jAssetsLeftLast)
						.addComponent(jBlueprintsLeftLast)
						.addComponent(jBookmarksLeftLast)
						.addComponent(jContractPricesLeft)
						.addComponent(jPriceDataLeft)
					)
				)
				.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
					.addComponent(jUpdate, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCheckAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jLeftFirst, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jLeftLast, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jMarketOrders, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMarketOrdersLeftFirst, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMarketOrdersLeftLast, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jJournal, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jJournalLeftFirst, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jJournalLeftLast, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jTransactions, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTransactionsLeftFirst, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTransactionsLeftLast, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jIndustryJobs, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jIndustryJobsLeftFirst, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jIndustryJobsLeftLast, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAccountBalance, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAccountBalanceLeftFirst, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAccountBalanceLeftLast, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jContracts, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContractsLeftFirst, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContractsLeftLast, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAssets, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAssetsLeftFirst, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAssetsLeftLast, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jBlueprints, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBlueprintsLeftFirst, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBlueprintsLeftLast, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jBookmarks, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBookmarksLeftFirst, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBookmarksLeftLast, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jContractPricesAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContractPricesNew, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContractPricesNone, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContractPricesLeft, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jPriceDataAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPriceDataNew, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPriceDataNone, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPriceDataLeft, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGap(30)
				.addGroup(layout.createParallelGroup()
					.addComponent(jUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	private void changed() {
		boolean allChecked = true;
		boolean someChecked = false;
		boolean allDisabled = true;
		for (JCheckBox jCheckBox : jCheckBoxes) {
			if (jCheckBox.isEnabled()) {
				if (jCheckBox.isSelected()) {
					someChecked = true;
				} else {
					allChecked = false;
				}
				allDisabled = false;
			}
		}
		if (jPriceDataAll.isEnabled()) {
			if (jPriceDataAll.isSelected()) {
				someChecked = true;
			} else { //Not selected
				allChecked = false;
			}
			allDisabled = false;
		} else if (jPriceDataNew.isEnabled()) {
			if (!jPriceDataNew.isSelected()) {
				allChecked = false;
			}
			allDisabled = false;
		}
		if (jContractPricesAll.isEnabled()) {
			if (jContractPricesAll.isSelected()) {
				someChecked = true;
			} else { //Not selected
				allChecked = false;
			}
			allDisabled = false;
		} else if (jContractPricesNew.isEnabled()) {
			if (!jContractPricesNew.isSelected()) {
				allChecked = false;
			}
			allDisabled = false;
		}
		jUpdate.setEnabled(someChecked);
		jCheckAll.setSelected(allChecked && !allDisabled);
		jCheckAll.setEnabled(!allDisabled);
	}

	private void update(boolean check) {
		Date industryJobsFirst = null;
		Date marketOrdersFirst = null;
		Date journalFirst = null;
		Date transactionsFirst = null;
		Date contractsFirst = null;
		Date assetsFirst = null;
		Date blueprintsFirst = null;
		Date bookmarksFirst = null;
		Date accountBalanceFirst = null;

		Date industryJobsLast = null;
		Date marketOrdersLast = null;
		Date journalLast = null;
		Date transactionsLast = null;
		Date contractsLast = null;
		Date assetsLast = null;
		Date blueprintsLast = null;
		Date bookmarksLast = null;
		Date accountBalanceLast = null;

		Date priceData = program.getPriceDataGetter().getNextUpdate();
		Date contracePrices = ContractPriceManager.get().getNextUpdate();
		for (OwnerType owner : program.getOwnerTypes()) {
			if (!owner.isShowOwner() || owner.isInvalid() || owner.isExpired() || owner.getAccountAPI() == ApiType.EVE_ONLINE || owner.getAccountAPI() == ApiType.EVEKIT) {
				continue;
			}
			if (owner.isIndustryJobs()) {
				industryJobsFirst = updateFirst(industryJobsFirst, owner.getIndustryJobsNextUpdate());
				industryJobsLast = updateLast(industryJobsLast, owner.getIndustryJobsNextUpdate());
			}
			if (owner.isMarketOrders()) {
				marketOrdersFirst = updateFirst(marketOrdersFirst, owner.getMarketOrdersNextUpdate());
				marketOrdersLast = updateLast(marketOrdersLast, owner.getMarketOrdersNextUpdate());
			}
			if (owner.isJournal()) {
				journalFirst = updateFirst(journalFirst, owner.getJournalNextUpdate());
				journalLast = updateLast(journalLast, owner.getJournalNextUpdate());
			}
			if (owner.isTransactions()) {
				transactionsFirst = updateFirst(transactionsFirst, owner.getTransactionsNextUpdate());
				transactionsLast = updateLast(transactionsLast, owner.getTransactionsNextUpdate());
			}
			if (owner.isContracts()) {
				contractsFirst = updateFirst(contractsFirst, owner.getContractsNextUpdate());
				contractsLast = updateLast(contractsLast, owner.getContractsNextUpdate());
			}
			if (owner.isAssetList()) {
				assetsFirst = updateFirst(assetsFirst, owner.getAssetNextUpdate());
				assetsLast = updateLast(assetsLast, owner.getAssetNextUpdate());
			}
			if (owner.isBlueprints()) {
				blueprintsFirst = updateFirst(blueprintsFirst, owner.getBlueprintsNextUpdate());
				blueprintsLast = updateLast(blueprintsLast, owner.getBlueprintsNextUpdate());
			}
			if (owner.isAccountBalance()) {
				accountBalanceFirst = updateFirst(accountBalanceFirst, owner.getBalanceNextUpdate());
				accountBalanceLast = updateLast(accountBalanceLast, owner.getBalanceNextUpdate());
			}
			if (owner.isBookmarks()) {
				bookmarksFirst = updateFirst(bookmarksFirst, owner.getBookmarksNextUpdate());
				bookmarksLast = updateLast(bookmarksLast, owner.getBookmarksNextUpdate());
			}
		}
		if (program.getOwnerTypes().isEmpty()) {
			jPriceDataNone.setSelected(true);
			jPriceDataNone.setEnabled(false);
			jPriceDataNew.setEnabled(false);
			jPriceDataAll.setEnabled(false);
			setUpdateLabel(null, jPriceDataLeft, jPriceDataAll, null, null, check);
			jContractPricesNone.setSelected(true);
			jContractPricesNone.setEnabled(false);
			jContractPricesNew.setEnabled(false);
			jContractPricesAll.setEnabled(false);
			setUpdateLabel(null, jContractPricesLeft, jContractPricesAll, null, null, check);
		} else {
			jPriceDataNone.setEnabled(true);
			jPriceDataNew.setEnabled(true);
			jPriceDataAll.setEnabled(true);
			setUpdateLabel(null, jPriceDataLeft, jPriceDataAll, null, priceData, check);
			if (!jPriceDataAll.isEnabled() && jPriceDataNew.isEnabled() && !jPriceDataNone.isSelected()) {
				jPriceDataNew.setSelected(true);
			}
			jContractPricesNone.setEnabled(true);
			jContractPricesNew.setEnabled(true);
			jContractPricesAll.setEnabled(true);
			setUpdateLabel(null, jContractPricesLeft, jContractPricesAll, null, contracePrices, check);
			if (!jContractPricesAll.isEnabled() && jContractPricesNew.isEnabled() && !jContractPricesNone.isSelected()) {
				jContractPricesNew.setSelected(true);
			}
		}
		setUpdateLabel(jMarketOrdersLeftFirst, jMarketOrdersLeftLast, jMarketOrders, marketOrdersFirst, marketOrdersLast, check);
		setUpdateLabel(jJournalLeftFirst, jJournalLeftLast, jJournal, journalFirst, journalLast, check);
		setUpdateLabel(jTransactionsLeftFirst, jTransactionsLeftLast, jTransactions, transactionsFirst, transactionsLast, check);
		setUpdateLabel(jIndustryJobsLeftFirst, jIndustryJobsLeftLast, jIndustryJobs, industryJobsFirst, industryJobsLast, check);
		setUpdateLabel(jAccountBalanceLeftFirst, jAccountBalanceLeftLast, jAccountBalance, accountBalanceFirst, accountBalanceLast, check);
		setUpdateLabel(jContractsLeftFirst, jContractsLeftLast, jContracts, contractsFirst, contractsLast, check);
		setUpdateLabel(jAssetsLeftFirst, jAssetsLeftLast, jAssets, assetsFirst, assetsLast, check);
		setUpdateLabel(jBlueprintsLeftFirst, jBlueprintsLeftLast, jBlueprints, blueprintsFirst, blueprintsLast, check);
		setUpdateLabel(jBookmarksLeftFirst, jBookmarksLeftLast, jBookmarks, bookmarksFirst, bookmarksLast, check);
		changed();

	}

	private void setUpdateLabel(final JLabel jFirst, final JLabel jAll, final JToggleButton jCheckBox, final Date first, final Date last, boolean check) {
		if (jFirst != null) {
			if (Settings.get().isUpdatable(last)) {
				jFirst.setText("");
			} else {
				jFirst.setText(getFormatedDuration(first));
			}
			jFirst.setEnabled(first != null);
		} else {
		}
		if (jAll != null) {
			jAll.setText(getFormatedDuration(last));
			jAll.setEnabled(last != null);
		}
		if (jCheckBox != null) {
			if ((Settings.get().isUpdatable(first) || Settings.get().isUpdatable(last))) {
				if (!jCheckBox.isEnabled()) {
					if (check) {
						jCheckBox.setSelected(true);
					}
					jCheckBox.setEnabled(true);
				}
			} else {
				jCheckBox.setEnabled(false);
				jCheckBox.setSelected(false);
			}
		}
	}

	private String getFormatedDuration(Date date) {
		if (date == null) { //less than 1 second
			return DialoguesUpdate.get().noAccounts();
		} else if (Settings.get().isUpdatable(date)){
			return DialoguesUpdate.get().now();
		} else {
			long time = date.getTime() - Settings.getNow().getTime();
			if (time <= 1000) { //less than 1 second
				return "...";
			} else if (time < (60 * 1000)) { //less than 1 minute
				return Formater.milliseconds(time, false, false, false, true);
			} else {
				return Formater.milliseconds(time, true, true, true, false);
			}
		}
	}

	private Date updateFirst(Date nextUpdate, Date thisUpdate) {
		if (nextUpdate == null) { //First
			nextUpdate = thisUpdate;
		} else if (thisUpdate.before(nextUpdate)) {
			nextUpdate = thisUpdate;
		}
		return nextUpdate;
	}

	private Date updateLast(Date lastUpdate, Date thisUpdate) {
		if (lastUpdate == null) { //First
			lastUpdate = thisUpdate;
		} else if (thisUpdate.after(lastUpdate)) {
			lastUpdate = thisUpdate;
		}
		return lastUpdate;
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jUpdate;
	}

	@Override
	protected JButton getDefaultButton() {
		return jUpdate;
	}

	@Override
	protected void windowShown() {
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			jMarketOrders.setSelected(true);
			jJournal.setSelected(true);
			jTransactions.setSelected(true);
			jIndustryJobs.setSelected(true);
			jAccountBalance.setSelected(true);
			jContracts.setSelected(true);
			jAssets.setSelected(true);
			jBlueprints.setSelected(true);
			jBookmarks.setSelected(true);
			jContractPricesAll.setSelected(true);
			jPriceDataAll.setSelected(true);
			update(true);
			timer.start();
		} else {
			timer.stop();
		}
		super.setVisible(b);
	}

	@Override
	protected void save() {

	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (UpdateDialogAction.UPDATE.name().equals(e.getActionCommand())) {
				setVisible(false);
				List<UpdateTask> updateTasks = new ArrayList<UpdateTask>();
				if (jMarketOrders.isSelected()
						|| jJournal.isSelected()
						|| jTransactions.isSelected()
						|| jIndustryJobs.isSelected()
						|| jAccountBalance.isSelected()
						|| jContracts.isSelected()
						|| jAssets.isSelected()
						|| jBlueprints.isSelected()
						|| jBookmarks.isSelected()
						) {
					updateTasks.add(new CitadelTask());
					updateTasks.add(new Step1Task(program.getProfileManager()));
					updateTasks.add(new Step2Task(program.getProfileManager(),
							jAssets.isSelected(),
							jAccountBalance.isSelected(),
							jBlueprints.isSelected(),
							jBookmarks.isSelected(),
							jContracts.isSelected(),
							jIndustryJobs.isSelected(),
							jJournal.isSelected(),
							jMarketOrders.isSelected(),
							jTransactions.isSelected()));
					updateTasks.add(new Step3Task(program.getProfileManager(),
							jAssets.isSelected(),
							jContracts.isSelected()));
				}
				if (jContracts.isSelected()) {
					updateTasks.add(new Step4Task(program.getProfileManager(), jContracts.isSelected()));
				}
				if (jContractPricesAll.isSelected() || jContractPricesNew.isSelected())  {
					updateTasks.add(new ContractPricesTask(program.getProfileData(), jContractPricesAll.isSelected()));
				}
				if (jPriceDataAll.isSelected() || jPriceDataNew.isSelected()) {
					updateTasks.add(new PriceDataTask(program.getPriceDataGetter(), program.getProfileData(), jPriceDataAll.isSelected()));
				}
				if (!updateTasks.isEmpty()) {
					//Pause structure update
					program.getStatusPanel().setPauseUpdates(true);
					TaskDialog taskDialog = new TaskDialog(program, updateTasks, false, null, new TaskDialog.TasksCompleted() {
						@Override
						public void tasksCompleted(TaskDialog taskDialog) {
							//Update tracker locations
							AssetValue.updateData();
							//Update eventlists
							program.updateEventLists();
							//Create value tracker point
							program.createTrackerDataPoint();
							//Save settings after updating (if we crash later)
							program.saveSettingsAndProfile(); //Save updated id<->name data
							//Resume structure update
							program.getStatusPanel().setPauseUpdates(false);
						}
					});
				}
			}
			if (UpdateDialogAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
			if (UpdateDialogAction.CHANGED.name().equals(e.getActionCommand())) {
				changed();
			}
			if (UpdateDialogAction.CHECK_ALL.name().equals(e.getActionCommand())) {
				boolean checked = jCheckAll.isSelected();
				for (JCheckBox jCheckBox : jCheckBoxes) {
					if (jCheckBox.isEnabled()) {
						jCheckBox.setSelected(checked);
					}
				}
				if (checked) {
					if (jPriceDataAll.isEnabled()) {
						jPriceDataAll.setSelected(true);
					} else {
						jPriceDataNew.setSelected(true);
					}
				} else {
					jPriceDataNone.setSelected(true);
				}
				if (checked) {
					if (jContractPricesAll.isEnabled()) {
						jContractPricesAll.setSelected(true);
					} else {
						jContractPricesNew.setSelected(true);
					}
				} else {
					jContractPricesNone.setSelected(true);
				}
				changed();
			}
		}
	}

	public static class Step1Task extends UpdateTask {

		private final List<Runnable> updates = new ArrayList<Runnable>();
		private final ProfileManager profileManager;

		public Step1Task(final ProfileManager profileManager) {
			super(DialoguesUpdate.get().step1());
			this.profileManager = profileManager;
			//Esi
			for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
				updates.add(new EsiOwnerGetter(this, esiOwner));
			}
		}

		@Override
		public void update() {
			setIcon(null);
			for (EveApiAccount account : profileManager.getAccounts()) {
				for (EveApiOwner eveApiOwner : account.getOwners()) {
					if (eveApiOwner.canMigrate()) {
						addError("EveApi accounts must be migrated to ESI", "Add ESI accounts in the account manager:\r\nOptions > Accounts... > Add > ESI");
						break;
					} else {
						addError("Migrated EveApi accounts can safely be deleted", "Delete EveApi accounts in the account manager:\r\nOptions > Accounts... > Edit");
					}
				}
			}
			for (EveKitOwner eveKitOwner : profileManager.getEveKitOwners()) {
				if (eveKitOwner.canMigrate()) {
					addError("EveKit accounts must be migrated to ESI", "Add ESI accounts in the account manager:\r\nOptions > Accounts... > Add > ESI");
					break;
				} else {
					addError("Migrated EveKit accounts can safely be deleted", "Delete EveApi accounts in the account manager:\r\nOptions > Accounts... > Edit");
				}
			}
			ThreadWoker.start(this, updates);
		}
	}

	public static class Step2Task extends UpdateTask {

		private final List<Runnable> updates = new ArrayList<Runnable>();

		public Step2Task(final ProfileManager profileManager, final boolean assets, final boolean balance, final boolean blueprints, final boolean bookmarks, final boolean contracts, final boolean industry, final boolean journal, final boolean orders, final boolean transactions) {
			super(DialoguesUpdate.get().step2());
			if (balance) {
				//Esi
				for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
					updates.add(new EsiAccountBalanceGetter(this, esiOwner));
				}
			}
			if (assets) {
				//Esi
				for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
					updates.add(new EsiAssetsGetter(this, esiOwner));
					if (esiOwner.isCorporation()) {
						updates.add(new EsiDivisionsGetter(this, esiOwner));
					}
				}
			}
			if (industry) {
				//Esi
				for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
					updates.add(new EsiIndustryJobsGetter(this, esiOwner));
				}
			}
			if (orders) {
				//Esi
				for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
					updates.add(new EsiMarketOrdersGetter(this, esiOwner, Settings.get().isMarketOrderHistory()));
				}
			}
			if (journal) {
				//Esi
				for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
					updates.add(new EsiJournalGetter(this, esiOwner, Settings.get().isJournalHistory()));
				}
			}
			if (transactions) {
				//Esi
				for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
					updates.add(new EsiTransactionsGetter(this, esiOwner, Settings.get().isTransactionHistory()));
				}
			}
			if (contracts) {
				//Esi
				for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
					updates.add(new EsiContractsGetter(this, esiOwner));
				}
			}
			if (blueprints) {
				//Esi
				for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
					updates.add(new EsiBlueprintsGetter(this, esiOwner));
				}
			}
			if (bookmarks) {
				//Esi
				for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
					updates.add(new EsiBookmarksGetter(this, esiOwner));
				}
			}
		}

		@Override
		public void update() {
			setIcon(null);
			ThreadWoker.start(this, updates);
		}
	}

	public static class Step3Task extends UpdateTask {

		private final List<Runnable> updates = new ArrayList<Runnable>();

		public Step3Task(final ProfileManager profileManager, final boolean assets, final boolean contracts) {
			super(DialoguesUpdate.get().step3());
			//Locations
			if (assets) {
				//Esi
				for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
					updates.add(new EsiLocationsGetter(this, esiOwner));
					updates.add(new EsiShipGetter(this, esiOwner));
					updates.add(new EsiPlanetaryInteractionGetter(this, esiOwner));
				}
			}
			//char/corp/alliance IDs to names (ESI)
			updates.add(new EsiNameGetter(this, profileManager.getOwnerTypes()));
		}

		@Override
		public void update() {
			setIcon(null);
			ThreadWoker.start(this, updates);
		}
	}
	
	public static class Step4Task extends UpdateTask {

		private final List<Runnable> updates = new ArrayList<Runnable>();

		public Step4Task(final ProfileManager profileManager, final boolean contracts) {
			super(DialoguesUpdate.get().step4());
			//Contract Items
			if (contracts) {
				//Esi
				EsiContractItemsGetter.reset();
				for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
					updates.add(new EsiContractItemsGetter(this, esiOwner, profileManager.getEsiOwners()));
				}
			}
		}

		@Override
		public void update() {
			setIcon(null);
			ThreadWoker.start(this, updates, false);
		}
	}

	public static class CitadelTask extends UpdateTask {

		public CitadelTask() {
			super(DialoguesUpdate.get().citadel());
		}

		@Override
		public void update() {
			CitadelGetter.update(this);
		}
	}

	public static class ContractPricesTask extends UpdateTask {

		private final ProfileData profileData;
		private final boolean all;

		public ContractPricesTask(ProfileData profileData, boolean all) {
			super(DialoguesUpdate.get().contractPrices() + " (Contracts Appraisal)");
			this.profileData = profileData;
			this.all = all;
			setIcon(Images.MISC_CONTRACTS_APPRAISAL.getIcon());
		}

		@Override
		public void update() {
			ThreadWoker.start(this, Collections.singletonList(new ContractPriceGetter(this, profileData, all)), false);
		}
	}

	public static class PriceDataTask extends UpdateTask {

		private final PriceDataGetter priceDataGetter;
		private final ProfileData profileData;
		private final boolean update;

		public PriceDataTask(final PriceDataGetter priceDataGetter, final ProfileData profileData, final boolean update) {
			super(DialoguesUpdate.get().priceData() + " (" + (Settings.get().getPriceDataSettings().getSource().toString()) + ")");
			this.priceDataGetter = priceDataGetter;
			this.profileData = profileData;
			this.update = update;
		}

		@Override
		public void update() {
			setIcon(Settings.get().getPriceDataSettings().getSource().getIcon());
			if (update) {
				priceDataGetter.updateAll(profileData, this);
			} else {
				priceDataGetter.updateNew(profileData, this);
			}
		}
	}
}
