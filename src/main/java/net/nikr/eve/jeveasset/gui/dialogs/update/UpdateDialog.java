/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.api.OwnerType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.tabs.contracts.MyContract;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.eveapi.AccountBalanceGetter;
import net.nikr.eve.jeveasset.io.eveapi.AccountGetter;
import net.nikr.eve.jeveasset.io.eveapi.AssetsGetter;
import net.nikr.eve.jeveasset.io.eveapi.BlueprintsGetter;
import net.nikr.eve.jeveasset.io.eveapi.ConquerableStationsGetter;
import net.nikr.eve.jeveasset.io.eveapi.ContractItemsGetter;
import net.nikr.eve.jeveasset.io.eveapi.ContractsGetter;
import net.nikr.eve.jeveasset.io.eveapi.IndustryJobsGetter;
import net.nikr.eve.jeveasset.io.eveapi.JournalGetter;
import net.nikr.eve.jeveasset.io.eveapi.LocationsGetter;
import net.nikr.eve.jeveasset.io.eveapi.MarketOrdersGetter;
import net.nikr.eve.jeveasset.io.eveapi.NameGetter;
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
import net.nikr.eve.jeveasset.io.local.ConquerableStationsWriter;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;


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
		Date accountBalanceFirst = null;

		Date industryJobsLast = null;
		Date marketOrdersLast = null;
		Date journalLast = null;
		Date transactionsLast = null;
		Date contractsLast = null;
		Date assetsLast = null;
		Date blueprintsLast = null;
		Date accountBalanceLast = null;

		Date priceData = program.getPriceDataGetter().getNextUpdate();
		for (OwnerType owner : program.getOwnerTypes()) {
			if (owner.isShowOwner()) {
				industryJobsFirst = updateFirst(industryJobsFirst, owner.getIndustryJobsNextUpdate());
				marketOrdersFirst = updateFirst(marketOrdersFirst, owner.getMarketOrdersNextUpdate());
				journalFirst = updateFirst(journalFirst, owner.getJournalNextUpdate());
				transactionsFirst = updateFirst(transactionsFirst, owner.getTransactionsNextUpdate());
				contractsFirst = updateFirst(contractsFirst, owner.getContractsNextUpdate());
				assetsFirst = updateFirst(assetsFirst, owner.getAssetNextUpdate());
				blueprintsFirst = updateFirst(blueprintsFirst, owner.getBlueprintsNextUpdate());
				accountBalanceFirst = updateFirst(accountBalanceFirst, owner.getBalanceNextUpdate());

				industryJobsLast = updateLast(industryJobsLast, owner.getIndustryJobsNextUpdate());
				marketOrdersLast = updateLast(marketOrdersLast, owner.getMarketOrdersNextUpdate());
				journalLast = updateLast(journalLast, owner.getJournalNextUpdate());
				transactionsLast = updateLast(transactionsLast, owner.getTransactionsNextUpdate());
				contractsLast = updateLast(contractsLast, owner.getContractsNextUpdate());
				assetsLast = updateLast(assetsLast, owner.getAssetNextUpdate());
				blueprintsLast = updateLast(blueprintsLast, owner.getBlueprintsNextUpdate());
				accountBalanceLast = updateLast(accountBalanceLast, owner.getBalanceNextUpdate());
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
				List<UpdateTask> updateTasks = new ArrayList<UpdateTask>();
				if (jMarketOrders.isSelected()
						|| jJournal.isSelected()
						|| jTransactions.isSelected()
						|| jIndustryJobs.isSelected()
						|| jAccountBalance.isSelected()
						|| jContracts.isSelected()
						|| jAssets.isSelected()
						) { //Updating from EVE API
					updateTasks.add(new ConquerableStationsTask()); //Should properly always be first
					updateTasks.add(new CitadelTask()); //Should properly always be first
					updateTasks.add(new AccountsTask());
				}
				if (jMarketOrders.isSelected()) {
					updateTasks.add(new MarketOrdersTask());
				}
				if (jJournal.isSelected()) {
					updateTasks.add(new JournalTask());
				}
				if (jTransactions.isSelected()) {
					updateTasks.add(new TransactionsTask());
				}
				if (jIndustryJobs.isSelected()) {
					updateTasks.add(new IndustryJobsTask());
				}
				if (jAccountBalance.isSelected()) {
					updateTasks.add(new BalanceTask());
				}
				if (jContracts.isSelected()) {
					updateTasks.add(new ContractsTask());
				}
				if (jAssets.isSelected()) {
					updateTasks.add(new AssetsTask());
				}
				if (jBlueprints.isSelected()) {
					updateTasks.add(new BlueprintsTask());
				}
				if (jContracts.isSelected() || jIndustryJobs.isSelected()) {
					updateTasks.add(new NameTask());
				}
				if (jPriceDataAll.isSelected() || jPriceDataNew.isSelected()) {
					updateTasks.add(new PriceDataTask(jPriceDataAll.isSelected()));
				}
				if (!updateTasks.isEmpty()) {
					TaskDialog taskDialog = new TaskDialog(program, updateTasks, false, new TaskDialog.TasksCompleted() {
						@Override
						public void tasksCompleted(TaskDialog taskDialog) {
							program.updateEventLists();
							//Create value tracker point
							program.createTrackerDataPoint();
							//Save settings after updating (if we crash later)
							program.saveSettingsAndProfile();
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

	public class ConquerableStationsTask extends UpdateTask {

		public ConquerableStationsTask() {
			super(DialoguesUpdate.get().conqStations());
		}

		@Override
		public void update() {
			setIcon(Images.MISC_EVE.getIcon());
			ConquerableStationsGetter conquerableStationsGetter = new ConquerableStationsGetter();
			conquerableStationsGetter.load(this);
			ConquerableStationsWriter.save();
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

	public class AccountsTask extends UpdateTask {

		public AccountsTask() {
			super(DialoguesUpdate.get().accounts());
		}

		@Override
		public void update() {
			//EveAPI
			if (!program.getProfileManager().getAccounts().isEmpty()) {
				setIcon(Images.MISC_EVE.getIcon());
			}
			AccountGetter accountGetter = new AccountGetter();
			accountGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			//EveKit
			if (!program.getProfileManager().getEveKitOwners().isEmpty()) {
				setIcon(Images.MISC_EVEKIT.getIcon());
			}
			EveKitOwnerGetter ownerGetter = new EveKitOwnerGetter();
			ownerGetter.load(this, program.getProfileManager().getEveKitOwners());
		}
	}

	public class AssetsTask extends UpdateTask {

		public AssetsTask() {
			super(DialoguesUpdate.get().assets());
		}

		@Override
		public void update() {
			//EveApi
			if (!program.getProfileManager().getAccounts().isEmpty()) {
				setIcon(Images.MISC_EVE.getIcon());
			}
			AssetsGetter assetsGetter = new AssetsGetter();
			assetsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			LocationsGetter locationsGetter = new LocationsGetter();
			locationsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			//EveKit
			if (!program.getProfileManager().getEveKitOwners().isEmpty()) {
				setIcon(Images.MISC_EVEKIT.getIcon());
			}
			EveKitAssetGetter eveKitAssetGetter = new EveKitAssetGetter();
			eveKitAssetGetter.load(this, program.getProfileManager().getEveKitOwners());
			EveKitLocationsGetter eveKitLocationsGetter = new EveKitLocationsGetter();
			eveKitLocationsGetter.load(this, program.getProfileManager().getEveKitOwners());
		}
	}

	public class BalanceTask extends UpdateTask {

		public BalanceTask() {
			super(DialoguesUpdate.get().balance());
		}

		@Override
		public void update() {
			//EveApi
			if (!program.getProfileManager().getAccounts().isEmpty()) {
				setIcon(Images.MISC_EVE.getIcon());
			}
			AccountBalanceGetter accountBalanceGetter = new AccountBalanceGetter();
			accountBalanceGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			//EveKit
			if (!program.getProfileManager().getEveKitOwners().isEmpty()) {
				setIcon(Images.MISC_EVEKIT.getIcon());
			}
			EveKitAccountBalanceGetter eveKitAccountBalanceGetter = new EveKitAccountBalanceGetter();
			eveKitAccountBalanceGetter.load(this, program.getProfileManager().getEveKitOwners());
		}
	}

	public class IndustryJobsTask extends UpdateTask {

		public IndustryJobsTask() {
			super(DialoguesUpdate.get().industryJobs());
		}

		@Override
		public void update() {
			//EveApi
			if (!program.getProfileManager().getAccounts().isEmpty()) {
				setIcon(Images.MISC_EVE.getIcon());
			}
			IndustryJobsGetter industryJobsGetter = new IndustryJobsGetter();
			industryJobsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			//EveKit
			if (!program.getProfileManager().getEveKitOwners().isEmpty()) {
				setIcon(Images.MISC_EVEKIT.getIcon());
			}
			EveKitIndustryJobsGetter eveKitIndustryJobsGetter = new EveKitIndustryJobsGetter();
			eveKitIndustryJobsGetter.load(this, program.getProfileManager().getEveKitOwners());
		}
	}

	public class MarketOrdersTask extends UpdateTask {

		public MarketOrdersTask() {
			super(DialoguesUpdate.get().marketOrders());
		}

		@Override
		public void update() {
			//EveApi
			if (!program.getProfileManager().getAccounts().isEmpty()) {
				setIcon(Images.MISC_EVE.getIcon());
			}
			MarketOrdersGetter marketOrdersGetter = new MarketOrdersGetter();
			marketOrdersGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts(), Settings.get().isMarketOrderHistory());
			//EveKit
			if (!program.getProfileManager().getEveKitOwners().isEmpty()) {
				setIcon(Images.MISC_EVEKIT.getIcon());
			}
			EveKitMarketOrdersGetter eveKitMarketOrdersGetter = new EveKitMarketOrdersGetter();
			eveKitMarketOrdersGetter.load(this, program.getProfileManager().getEveKitOwners());
		}
	}

	public class JournalTask extends UpdateTask {

		public JournalTask() {
			super(DialoguesUpdate.get().journal());
		}

		@Override
		public void update() {
			//EveApi
			if (!program.getProfileManager().getAccounts().isEmpty()) {
				setIcon(Images.MISC_EVE.getIcon());
			}
			JournalGetter journalGetter = new JournalGetter();
			journalGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts(), Settings.get().isJournalHistory());
			//EveKit
			if (!program.getProfileManager().getEveKitOwners().isEmpty()) {
				setIcon(Images.MISC_EVEKIT.getIcon());
			}
			EveKitJournalGetter eveKitJournalGetter = new EveKitJournalGetter();
			eveKitJournalGetter.load(this, program.getProfileManager().getEveKitOwners());
		}
	}

	public class TransactionsTask extends UpdateTask {

		public TransactionsTask() {
			super(DialoguesUpdate.get().transactions());
		}

		@Override
		public void update() {
			//EveApi
			if (!program.getProfileManager().getAccounts().isEmpty()) {
				setIcon(Images.MISC_EVE.getIcon());
			}
			TransactionsGetter transactionsGetter = new TransactionsGetter();
			transactionsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts(), Settings.get().isTransactionHistory());
			//EveKit
			if (!program.getProfileManager().getEveKitOwners().isEmpty()) {
				setIcon(Images.MISC_EVEKIT.getIcon());
			}
			EveKitTransactionsGetter eveKitTransactionsGetter = new EveKitTransactionsGetter();
			eveKitTransactionsGetter.load(this, program.getProfileManager().getEveKitOwners());
		}
	}

	public class ContractsTask extends UpdateTask {

		public ContractsTask() {
			super(DialoguesUpdate.get().contracts());
		}

		@Override
		public void update() {
			//EveApi
			if (!program.getProfileManager().getAccounts().isEmpty()) {
				setIcon(Images.MISC_EVE.getIcon());
			}
			ContractsGetter contractsGetter = new ContractsGetter();
			contractsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			ContractItemsGetter itemsGetter = new ContractItemsGetter();
			itemsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			//EveKit
			if (!program.getProfileManager().getEveKitOwners().isEmpty()) {
				setIcon(Images.MISC_EVEKIT.getIcon());
			}
			EveKitContractsGetter eveKitContractsGetter = new EveKitContractsGetter();
			eveKitContractsGetter.load(this, program.getProfileManager().getEveKitOwners());
			EveKitContractItemsGetter eveKitContractItemsGetter = new EveKitContractItemsGetter();
			eveKitContractItemsGetter.load(this, program.getProfileManager().getEveKitOwners());
		}
	}

	public class BlueprintsTask extends UpdateTask {

		public BlueprintsTask() {
			super(DialoguesUpdate.get().blueprints());
		}

		@Override
		public void update() {
			//EveApi
			if (!program.getProfileManager().getAccounts().isEmpty()) {
				setIcon(Images.MISC_EVE.getIcon());
			}
			BlueprintsGetter blueprintsGetter = new BlueprintsGetter();
			blueprintsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			//EveKit
			if (!program.getProfileManager().getEveKitOwners().isEmpty()) {
				setIcon(Images.MISC_EVEKIT.getIcon());
			}
			EveKitBlueprintsGetter eveKitBlueprintsGetter = new EveKitBlueprintsGetter();
			eveKitBlueprintsGetter.load(this, program.getProfileManager().getEveKitOwners());
		}
	}

	public class NameTask extends UpdateTask {

		public NameTask() {
			super(DialoguesUpdate.get().names());
		}

		@Override
		public void update() {
			setIcon(Images.MISC_EVE.getIcon());
			Set<Long> list = new HashSet<Long>();
			for (OwnerType owner : program.getOwnerTypes()) {
				list.add(owner.getOwnerID()); //Just to be sure
				for (MyIndustryJob myIndustryJob : owner.getIndustryJobs()) {
					list.add(myIndustryJob.getInstallerID());
				}
				for (MyContract contract : owner.getContracts().keySet()) {
					list.add(contract.getAcceptorID());
					list.add(contract.getAssigneeID());
					list.add(contract.getIssuerCorpID());
					list.add(contract.getIssuerID());
				}
			}
			//Get Name
			NameGetter nameGetter = new NameGetter();
			nameGetter.load(this, list);
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
			switch (Settings.get().getPriceDataSettings().getSource()) {
				case EVE_CENTRAL: setIcon(Images.LINK_EVE_CENTRAL.getIcon()); break;
				case EVE_MARKETDATA: setIcon(Images.LINK_EVE_MARKETDATA.getIcon()); break;
			}
			if (update) {
				program.getPriceDataGetter().updateAll(this);
			} else {
				program.getPriceDataGetter().updateNew(this);
			}
		}
	}
}
