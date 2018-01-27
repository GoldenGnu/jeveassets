/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccount;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.esi.EsiAccountBalanceGetter;
import net.nikr.eve.jeveasset.io.esi.EsiAssetsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiBlueprintsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiConquerableStationsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiContainerLogsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiContractItemsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiContractsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiIndustryJobsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiJournalGetter;
import net.nikr.eve.jeveasset.io.esi.EsiLocationsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiMarketOrdersGetter;
import net.nikr.eve.jeveasset.io.esi.EsiNameGetter;
import net.nikr.eve.jeveasset.io.esi.EsiOwnerGetter;
import net.nikr.eve.jeveasset.io.esi.EsiShipGetter;
import net.nikr.eve.jeveasset.io.esi.EsiTransactionsGetter;
import net.nikr.eve.jeveasset.io.eveapi.AccountBalanceGetter;
import net.nikr.eve.jeveasset.io.eveapi.AccountGetter;
import net.nikr.eve.jeveasset.io.eveapi.AssetsGetter;
import net.nikr.eve.jeveasset.io.eveapi.BlueprintsGetter;
import net.nikr.eve.jeveasset.io.eveapi.ContractItemsGetter;
import net.nikr.eve.jeveasset.io.eveapi.ContractsGetter;
import net.nikr.eve.jeveasset.io.eveapi.IndustryJobsGetter;
import net.nikr.eve.jeveasset.io.eveapi.JournalGetter;
import net.nikr.eve.jeveasset.io.eveapi.LocationsGetter;
import net.nikr.eve.jeveasset.io.eveapi.MarketOrdersGetter;
import net.nikr.eve.jeveasset.io.eveapi.TransactionsGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitAccountBalanceGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitAssetGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitBlueprintsGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitContractItemsGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitContractsGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitIndustryJobsGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitJournalGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitLocationsGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitMarketOrdersGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitOwnerGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitTransactionsGetter;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
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
	private final JCheckBox jContainerLogs;
	private final JLabel jContainerLogsLeftFirst;
	private final JLabel jContainerLogsLeftLast;
	private final JRadioButton jPriceDataAll;
	private final JRadioButton jPriceDataNew;
	private final JRadioButton jPriceDataNone;
	private final JLabel jPriceDataLeftLast;
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
				update();
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
		jContainerLogs = new JCheckBox(DialoguesUpdate.get().containerLogs());
		JLabel jPriceDataLabel = new JLabel(DialoguesUpdate.get().priceData());
		jPriceDataAll = new JRadioButton(DialoguesUpdate.get().priceDataAll());
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
		jCheckBoxes.add(jContainerLogs);
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
		jContainerLogsLeftFirst = new JLabel();
		jPriceDataLeftLast = new JLabel();

		JLabel jLeftLast = new JLabel(DialoguesUpdate.get().allAccounts());
		jMarketOrdersLeftLast = new JLabel();
		jJournalLeftLast = new JLabel();
		jTransactionsLeftLast = new JLabel();
		jIndustryJobsLeftLast = new JLabel();
		jAccountBalanceLeftLast = new JLabel();
		jContractsLeftLast = new JLabel();
		jAssetsLeftLast = new JLabel();
		jBlueprintsLeftLast = new JLabel();
		jContainerLogsLeftLast = new JLabel();

		jUpdate = new JButton(DialoguesUpdate.get().update());
		jUpdate.setActionCommand(UpdateDialogAction.UPDATE.name());
		jUpdate.addActionListener(listener);

		jCancel = new JButton(DialoguesUpdate.get().cancel());
		jCancel.setActionCommand(UpdateDialogAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jPriceDataAll, 65, 65, 65)
					.addComponent(jPriceDataNew, 65, 65, 65)
					.addComponent(jPriceDataNone, 65, 65, 65)
				)
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
						.addComponent(jContainerLogs)
						.addComponent(jPriceDataLabel)
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
						.addComponent(jContainerLogsLeftFirst)
					)
					.addGap(20)
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
						.addComponent(jContainerLogsLeftLast)
						.addComponent(jPriceDataLeftLast)
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
					.addComponent(jContainerLogs, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContainerLogsLeftFirst, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContainerLogsLeftLast, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jPriceDataLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPriceDataLeftLast, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jPriceDataAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPriceDataNew, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPriceDataNone, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
		jUpdate.setEnabled(someChecked);
		jCheckAll.setSelected(allChecked && !allDisabled);
		jCheckAll.setEnabled(!allDisabled);
	}

	private void update() {
		Date industryJobsFirst = null;
		Date marketOrdersFirst = null;
		Date journalFirst = null;
		Date transactionsFirst = null;
		Date contractsFirst = null;
		Date assetsFirst = null;
		Date blueprintsFirst = null;
		Date containerLogsFirst  = null;
		Date accountBalanceFirst = null;

		Date industryJobsLast = null;
		Date marketOrdersLast = null;
		Date journalLast = null;
		Date transactionsLast = null;
		Date contractsLast = null;
		Date assetsLast = null;
		Date blueprintsLast = null;
		Date containerLogsLast  = null;
		Date accountBalanceLast = null;

		Date priceData = program.getPriceDataGetter().getNextUpdate();
		for (OwnerType owner : program.getOwnerTypes()) {
			if (owner.isShowOwner() && !owner.isInvalid() && !owner.isExpired()) {
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
				if (owner.isContainerLogs()) {
					containerLogsFirst = updateFirst(containerLogsFirst, owner.getContainerLogsNextUpdate());
					containerLogsLast = updateLast(containerLogsLast, owner.getContainerLogsNextUpdate());
				}
				
			}
		}
		if (program.getOwnerTypes().isEmpty()) {
			jPriceDataNone.setSelected(true);
			jPriceDataNone.setEnabled(false);
			jPriceDataNew.setEnabled(false);
			jPriceDataAll.setEnabled(false);
		} else {
			jPriceDataNone.setEnabled(true);
			jPriceDataNew.setEnabled(true);
			jPriceDataAll.setEnabled(true);
			setUpdateLabel(null, jPriceDataLeftLast, jPriceDataAll, priceData, priceData, false);
			if (!jPriceDataAll.isEnabled() && jPriceDataNew.isEnabled() && !jPriceDataNone.isSelected()) {
				jPriceDataNew.setSelected(true);
			}
		}
		setUpdateLabel(jMarketOrdersLeftFirst, jMarketOrdersLeftLast, jMarketOrders, marketOrdersFirst, marketOrdersLast);
		setUpdateLabel(jJournalLeftFirst, jJournalLeftLast, jJournal, journalFirst, journalLast);
		setUpdateLabel(jTransactionsLeftFirst, jTransactionsLeftLast, jTransactions, transactionsFirst, transactionsLast);
		setUpdateLabel(jIndustryJobsLeftFirst, jIndustryJobsLeftLast, jIndustryJobs, industryJobsFirst, industryJobsLast);
		setUpdateLabel(jAccountBalanceLeftFirst, jAccountBalanceLeftLast, jAccountBalance, accountBalanceFirst, accountBalanceLast);
		setUpdateLabel(jContractsLeftFirst, jContractsLeftLast, jContracts, contractsFirst, contractsLast);
		setUpdateLabel(jAssetsLeftFirst, jAssetsLeftLast, jAssets, assetsFirst, assetsLast);
		setUpdateLabel(jBlueprintsLeftFirst, jBlueprintsLeftLast, jBlueprints, blueprintsFirst, blueprintsLast);
		setUpdateLabel(jContainerLogsLeftFirst, jContainerLogsLeftLast, jContainerLogs, containerLogsFirst, containerLogsLast);
		changed();

	}

	private void setUpdateLabel(final JLabel jFirst, final JLabel jAll, final JToggleButton jCheckBox, final Date first, final Date last) {
		this.setUpdateLabel(jFirst, jAll, jCheckBox, first, last, true);
	}

	private void setUpdateLabel(final JLabel jFirst, final JLabel jAll, final JToggleButton jCheckBox, final Date first, final Date last, final boolean ignoreOnProxy) {
		if (first != null && Settings.get().isUpdatable(first, ignoreOnProxy)) {
			if (last != null && Settings.get().isUpdatable(last, ignoreOnProxy)) {
				if (jFirst != null) {
					jFirst.setText("");
				}
				jAll.setText(DialoguesUpdate.get().now());
			} else {
				if (jFirst != null) {
					jFirst.setText(DialoguesUpdate.get().now());
				}
				jAll.setText(getFormatedDuration(last));
			}
			if (!jCheckBox.isEnabled()) {
				jCheckBox.setSelected(true);
				jCheckBox.setEnabled(true);
			}
		} else {
			if (jFirst != null) {
				jFirst.setText(getFormatedDuration(first));
			}
			jAll.setText(getFormatedDuration(last));
			jCheckBox.setSelected(false);
			jCheckBox.setEnabled(false);
		}
		if (first == null) {
			if (jFirst != null) {
				jFirst.setEnabled(false);
			}
		} else {
			if (jFirst != null) {
				jFirst.setEnabled(true);
			}
		}
		if (last == null) {
			jAll.setEnabled(false);
		} else {
			jAll.setEnabled(true);
		}
	}

	private String getFormatedDuration(Date date) {
		if (date == null) { //less than 1 second
			return DialoguesUpdate.get().noAccounts();
		} else {
			long time = date.getTime() - Settings.getNow().getTime();
			if (time <= 1000) { //less than 1 second
				return "...";
			} else if (time < (60 * 1000)) { //less than 1 minute
				return Formater.milliseconds(time, false, false, false, true);
			} else {
				return Formater.milliseconds(time, false, true, true, false);
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
			jContainerLogs.setSelected(true);
			jPriceDataAll.setSelected(true);
			update();
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
				Date lastUpdate = null;
				for (OwnerType ownerType : program.getProfileManager().getOwnerTypes()) {
					Date update = ownerType.getAssetLastUpdate();
					if (lastUpdate == null) {
						lastUpdate = update;
					} if (update != null) {
						if (lastUpdate.after(update)) {
							lastUpdate = update;
						}
					}
				}
				final Date start = lastUpdate;
				List<UpdateTask> updateTasks = new ArrayList<UpdateTask>();
				if (jMarketOrders.isSelected()
						|| jJournal.isSelected()
						|| jTransactions.isSelected()
						|| jIndustryJobs.isSelected()
						|| jAccountBalance.isSelected()
						|| jContracts.isSelected()
						|| jAssets.isSelected()
						|| jBlueprints.isSelected()
						|| jContainerLogs.isSelected()
						) {
					updateTasks.add(new CitadelTask());
					updateTasks.add(new Step1Task());
					updateTasks.add(new Step2Task());
					updateTasks.add(new Step3Task());
				}
				if (jContracts.isSelected()) {
					updateTasks.add(new Step4Task());
				}
				if (jPriceDataAll.isSelected() || jPriceDataNew.isSelected()) {
					updateTasks.add(new PriceDataTask(jPriceDataAll.isSelected()));
				}
				if (!updateTasks.isEmpty()) {
					//Pause structure update
					program.getStatusPanel().setPauseUpdates(true);
					TaskDialog taskDialog = new TaskDialog(program, updateTasks, false, null, new TaskDialog.TasksCompleted() {
						@Override
						public void tasksCompleted(TaskDialog taskDialog) {
							//Update tracker locations
							Value.update();
							//Update eventlists
							program.updateEventLists(start);
							//Create value tracker point
							program.createTrackerDataPoint();
							//Save settings after updating (if we crash later)
							program.saveSettingsAndProfile();
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
				changed();
			}
		}
	}

	public class Step1Task extends UpdateTask {

		private final List<Runnable> updates = new ArrayList<Runnable>();

		public Step1Task() {
			super(DialoguesUpdate.get().step1());
			//EveKit
			for (EveKitOwner eveKitOwner : program.getProfileManager().getEveKitOwners()) {
				updates.add(new EveKitOwnerGetter(this, eveKitOwner));
			}
			//Esi
			for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
				updates.add(new EsiOwnerGetter(this, esiOwner));
			}
			for (EveApiAccount account : program.getProfileManager().getAccounts()) {
				updates.add(new AccountGetter(this, account));
			}
		}

		@Override
		public void update() {
			setIcon(null);
			ThreadWoker.start(this, updates);
		}
	}

	public class Step2Task extends UpdateTask {

		private final List<Runnable> updates = new ArrayList<Runnable>();

		public Step2Task() {
			super(DialoguesUpdate.get().step2());
			if (jAccountBalance.isSelected()) {
				//EveApi
				for (EveApiAccount account : program.getProfileManager().getAccounts()) {
					for (EveApiOwner owner : account.getOwners()) {
						updates.add(new AccountBalanceGetter(this, owner));
					}
				}
				//EveKit
				for (EveKitOwner eveKitOwner : program.getProfileManager().getEveKitOwners()) {
					updates.add(new EveKitAccountBalanceGetter(this, eveKitOwner));
				}
				//Esi
				for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
					updates.add(new EsiAccountBalanceGetter(this, esiOwner));
				}
			}
			if (jAssets.isSelected()) {
				//EveApi
				for (EveApiAccount account : program.getProfileManager().getAccounts()) {
					for (EveApiOwner owner : account.getOwners()) {
						updates.add(new AssetsGetter(this, owner));
					}
				}
				//EveKit
				for (EveKitOwner eveKitOwner : program.getProfileManager().getEveKitOwners()) {
					updates.add(new EveKitAssetGetter(this, eveKitOwner));
				}
				//Esi
				for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
					updates.add(new EsiAssetsGetter(this, esiOwner));
				}
			}
			if (jIndustryJobs.isSelected()) {
				//EveApi
				for (EveApiAccount account : program.getProfileManager().getAccounts()) {
					for (EveApiOwner owner : account.getOwners()) {
						updates.add(new IndustryJobsGetter(this, owner));
					}
				}
				//EveKit
				for (EveKitOwner eveKitOwner : program.getProfileManager().getEveKitOwners()) {
					updates.add(new EveKitIndustryJobsGetter(this, eveKitOwner));
				}
				//Esi
				for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
					updates.add(new EsiIndustryJobsGetter(this, esiOwner));
				}
			}
			if (jMarketOrders.isSelected()) {
				//EveApi
				for (EveApiAccount account : program.getProfileManager().getAccounts()) {
					for (EveApiOwner owner : account.getOwners()) {
						updates.add(new MarketOrdersGetter(this, owner, Settings.get().isMarketOrderHistory()));
					}
				}
				//EveKit
				for (EveKitOwner eveKitOwner : program.getProfileManager().getEveKitOwners()) {
					updates.add(new EveKitMarketOrdersGetter(this, eveKitOwner));
				}
				//Esi
				for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
					updates.add(new EsiMarketOrdersGetter(this, esiOwner, Settings.get().isMarketOrderHistory()));
				}
			}
			if (jJournal.isSelected()) {
				//EveApi
				for (EveApiAccount account : program.getProfileManager().getAccounts()) {
					for (EveApiOwner owner : account.getOwners()) {
						updates.add(new JournalGetter(this, owner, Settings.get().isJournalHistory()));
					}
				}
				//EveKit
				for (EveKitOwner eveKitOwner : program.getProfileManager().getEveKitOwners()) {
					updates.add(new EveKitJournalGetter(this, eveKitOwner));
				}
				//Esi
				for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
					updates.add(new EsiJournalGetter(this, esiOwner, Settings.get().isJournalHistory()));
				}
			}
			if (jTransactions.isSelected()) {
				//EveApi
				for (EveApiAccount account : program.getProfileManager().getAccounts()) {
					for (EveApiOwner owner : account.getOwners()) {
						updates.add(new TransactionsGetter(this, owner, Settings.get().isTransactionHistory()));
					}
				}
				//EveKit
				for (EveKitOwner eveKitOwner : program.getProfileManager().getEveKitOwners()) {
					updates.add(new EveKitTransactionsGetter(this, eveKitOwner));
				}
				//Esi
				for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
					updates.add(new EsiTransactionsGetter(this, esiOwner, Settings.get().isTransactionHistory()));
				}
			}
			if (jContracts.isSelected()) {
				//EveApi
				for (EveApiAccount account : program.getProfileManager().getAccounts()) {
					for (EveApiOwner owner : account.getOwners()) {
						updates.add(new ContractsGetter(this, owner));
					}
				}
				//EveKit
				for (EveKitOwner eveKitOwner : program.getProfileManager().getEveKitOwners()) {
					updates.add(new EveKitContractsGetter(this, eveKitOwner));
				}
				////Esi
				for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
					updates.add(new EsiContractsGetter(this, esiOwner));
				}
			}
			if (jBlueprints.isSelected()) {
				//EveApi
				for (EveApiAccount account : program.getProfileManager().getAccounts()) {
					for (EveApiOwner owner : account.getOwners()) {
						updates.add(new BlueprintsGetter(this, owner));
					}
				}
				//EveKit
				for (EveKitOwner eveKitOwner : program.getProfileManager().getEveKitOwners()) {
					updates.add(new EveKitBlueprintsGetter(this, eveKitOwner));
				}
				//Esi
				for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
					updates.add(new EsiBlueprintsGetter(this, esiOwner));
				}
			}
			if (jContainerLogs.isSelected()) {
				//Esi
				for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
					updates.add(new EsiContainerLogsGetter(this, esiOwner));
				}
			}
		}

		@Override
		public void update() {
			setIcon(null);
			ThreadWoker.start(this, updates);
		}
	}

	public class Step3Task extends UpdateTask {

		private final List<Runnable> updates = new ArrayList<Runnable>();

		public Step3Task() {
			super(DialoguesUpdate.get().step3());
			//Conquerable Stations (ESI)
			updates.add(new EsiConquerableStationsGetter(this));
			//Contract Items
			if (jContracts.isSelected()) {
				//EveApi
				for (EveApiAccount account : program.getProfileManager().getAccounts()) {
					for (EveApiOwner owner : account.getOwners()) {
						updates.add(new ContractItemsGetter(this, owner));
					}
				}
				//EveKit
				for (EveKitOwner eveKitOwner : program.getProfileManager().getEveKitOwners()) {
					updates.add(new EveKitContractItemsGetter(this, eveKitOwner));
				}
			}
			//Locations
			if (jAssets.isSelected()) {
				//EveApi
				for (EveApiAccount account : program.getProfileManager().getAccounts()) {
					for (EveApiOwner owner : account.getOwners()) {
						updates.add(new LocationsGetter(this, owner));
					}
				}
				//EveKit
				for (EveKitOwner eveKitOwner : program.getProfileManager().getEveKitOwners()) {
					updates.add(new EveKitLocationsGetter(this, eveKitOwner));
				}
				//Esi
				for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
					updates.add(new EsiLocationsGetter(this, esiOwner));
					updates.add(new EsiShipGetter(this, esiOwner));
				}
			}
			//char/corp/alliance IDs to names (ESI)
			updates.add(new EsiNameGetter(this, program.getOwnerTypes()));
		}

		@Override
		public void update() {
			setIcon(null);
			ThreadWoker.start(this, updates);
		}
	}
	
	public class Step4Task extends UpdateTask {

		private final List<Runnable> updates = new ArrayList<Runnable>();

		public Step4Task() {
			super(DialoguesUpdate.get().step4());
			//Contract Items
			if (jContracts.isSelected()) {
				//Esi
				EsiContractItemsGetter.reset();
				for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
					updates.add(new EsiContractItemsGetter(this, esiOwner, program.getProfileManager().getEsiOwners()));
				}
			}
		}

		@Override
		public void update() {
			setIcon(null);
			ThreadWoker.start(this, false, updates);
		}
	}

	public class CitadelTask extends UpdateTask {

		public CitadelTask() {
			super(DialoguesUpdate.get().citadel());
		}

		@Override
		public void update() {
			CitadelGetter.update(this);
		}
	}

	public class PriceDataTask extends UpdateTask {

		private final boolean update;

		public PriceDataTask(final boolean update) {
			super(DialoguesUpdate.get().priceData() + " (" + (Settings.get().getPriceDataSettings().getSource().toString()) + ")");
			this.update = update;
		}

		@Override
		public void update() {
			setIcon(Settings.get().getPriceDataSettings().getSource().getIcon());
			if (update) {
				program.getPriceDataGetter().updateAll(program.getProfileData(), this);
			} else {
				program.getPriceDataGetter().updateNew(program.getProfileData(), this);
			}
		}
	}
}
