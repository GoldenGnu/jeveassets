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
	private final JLabel jMarketOrdersUpdate;
	private final JLabel jMarketOrdersLeft;
	private final JCheckBox jJournal;
	private final JLabel jJournalUpdate;
	private final JLabel jJournalLeft;
	private final JCheckBox jTransactions;
	private final JLabel jTransactionsUpdate;
	private final JLabel jTransactionsLeft;
	private final JCheckBox jIndustryJobs;
	private final JLabel jIndustryJobsUpdate;
	private final JLabel jIndustryJobsLeft;
	private final JCheckBox jAccountBalance;
	private final JLabel jAccountBalanceUpdate;
	private final JLabel jAccountBalanceLeft;
	private final JCheckBox jContracts;
	private final JLabel jContractsUpdate;
	private final JLabel jContractsLeft;
	private final JCheckBox jAssets;
	private final JLabel jAssetsUpdate;
	private final JLabel jAssetsLeft;
	private final JCheckBox jBlueprints;
	private final JLabel jBlueprintsUpdate;
	private final JLabel jBlueprintsLeft;
	private final JRadioButton jPriceDataAll;
	private final JRadioButton jPriceDataNew;
	private final JRadioButton jPriceDataNone;
	private final JLabel jPriceDataUpdate;
	private final JLabel jPriceDataLeft;
	private final JButton jUpdate;
	private final JButton jCancel;
	private final List<JCheckBox> jCheckBoxes = new ArrayList<JCheckBox>();

	public UpdateDialog(final Program program) {
		super(program, DialoguesUpdate.get().update(), Images.DIALOG_UPDATE.getImage());

		ListenerClass listener = new ListenerClass();

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
		JLabel jNextUpdateLabel = new JLabel(DialoguesUpdate.get().nextUpdate());
		jMarketOrdersUpdate = new JLabel();
		jJournalUpdate = new JLabel();
		jTransactionsUpdate = new JLabel();
		jIndustryJobsUpdate = new JLabel();
		jAccountBalanceUpdate = new JLabel();
		jContractsUpdate = new JLabel();
		jAssetsUpdate = new JLabel();
		jBlueprintsUpdate = new JLabel();
		jPriceDataUpdate = new JLabel();

		jMarketOrdersLeft = new JLabel();
		jJournalLeft = new JLabel();
		jTransactionsLeft = new JLabel();
		jIndustryJobsLeft = new JLabel();
		jAccountBalanceLeft = new JLabel();
		jContractsLeft = new JLabel();
		jAssetsLeft = new JLabel();
		jBlueprintsLeft = new JLabel();
		jPriceDataLeft = new JLabel();

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
					.addGroup(layout.createParallelGroup()
						.addComponent(jNextUpdateLabel)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(Alignment.TRAILING)
								.addComponent(jMarketOrdersLeft)
								.addComponent(jJournalLeft)
								.addComponent(jTransactionsLeft)
								.addComponent(jIndustryJobsLeft)
								.addComponent(jAccountBalanceLeft)
								.addComponent(jContractsLeft)
								.addComponent(jAssetsLeft)
								.addComponent(jBlueprintsLeft)
								.addComponent(jPriceDataLeft)
							)
							.addGap(20)
							.addGroup(layout.createParallelGroup()
								.addComponent(jMarketOrdersUpdate)
								.addComponent(jJournalUpdate)
								.addComponent(jTransactionsUpdate)
								.addComponent(jIndustryJobsUpdate)
								.addComponent(jAccountBalanceUpdate)
								.addComponent(jContractsUpdate)
								.addComponent(jAssetsUpdate)
								.addComponent(jBlueprintsUpdate)
								.addComponent(jPriceDataUpdate)
							)
						)
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
					.addComponent(jNextUpdateLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jMarketOrders, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMarketOrdersLeft, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMarketOrdersUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jJournal, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jJournalLeft, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jJournalUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jTransactions, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTransactionsLeft, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTransactionsUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jIndustryJobs, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jIndustryJobsLeft, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jIndustryJobsUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAccountBalance, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAccountBalanceLeft, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAccountBalanceUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jContracts, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContractsLeft, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContractsUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAssets, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAssetsLeft, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAssetsUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jBlueprints, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBlueprintsLeft, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBlueprintsUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jPriceDataLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPriceDataLeft, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPriceDataUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jPriceDataAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPriceDataNew, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jPriceDataNone, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGap(30)
				.addGroup(layout.createParallelGroup()
					//.addComponent(jCheckAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	private void changed() {
		boolean allChecked = true;
		boolean someChecked = false;
		boolean allDiabled = true;
		for (JCheckBox jCheckBox : jCheckBoxes) {
			if (jCheckBox.isEnabled()) {
				if (jCheckBox.isSelected()) {
					someChecked = true;
				} else {
					allChecked = false;
				}
				allDiabled = false;
			}
		}
		if (jPriceDataAll.isEnabled()) {
			if (jPriceDataAll.isSelected()) {
				someChecked = true;
			} else { //Not selected
				allChecked = false;
			}
			allDiabled = false;
		} else if (jPriceDataNew.isEnabled()) {
			if (!jPriceDataNew.isSelected()) {
				allChecked = false;
			}
			allDiabled = false;
		}
		jUpdate.setEnabled(someChecked);
		jCheckAll.setSelected(allChecked && !allDiabled);
	}

	private void update() {
		Date industryJobsNextUpdate = null;
		Date marketOrdersNextUpdate = null;
		Date journalNextUpdate = null;
		Date transactionsNextUpdate = null;
		Date contractsNextUpdate = null;
		Date assetsNextUpdate = null;
		Date blueprintsNextUpdate = null;
		Date accountBalanceNextUpdate = null;

		boolean industryJobsUpdateAll = true;
		boolean marketOrdersUpdateAll = true;
		boolean journalUpdateAll = true;
		boolean transactionsUpdateAll = true;
		boolean contractsUpdateAll = true;
		boolean assetsUpdateAll = true;
		boolean blueprintsUpdateAll = true;
		boolean accountBalanceUpdateAll = true;

		Date priceDataNextUpdate = program.getPriceDataGetter().getNextUpdate();
		for (OwnerType owner : program.getOwnerTypes()) {
			if (owner.isShowOwner()) {
				industryJobsNextUpdate = nextUpdate(industryJobsNextUpdate, owner.getIndustryJobsNextUpdate());
				marketOrdersNextUpdate = nextUpdate(marketOrdersNextUpdate, owner.getMarketOrdersNextUpdate());
				journalNextUpdate = nextUpdate(journalNextUpdate, owner.getJournalNextUpdate());
				transactionsNextUpdate = nextUpdate(transactionsNextUpdate, owner.getTransactionsNextUpdate());
				contractsNextUpdate = nextUpdate(contractsNextUpdate, owner.getContractsNextUpdate());
				assetsNextUpdate = nextUpdate(assetsNextUpdate, owner.getAssetNextUpdate());
				blueprintsNextUpdate = nextUpdate(blueprintsNextUpdate, owner.getBlueprintsNextUpdate());
				accountBalanceNextUpdate = nextUpdate(accountBalanceNextUpdate, owner.getBalanceNextUpdate());

				industryJobsUpdateAll = updateAll(industryJobsUpdateAll, owner.getIndustryJobsNextUpdate());
				marketOrdersUpdateAll = updateAll(marketOrdersUpdateAll, owner.getMarketOrdersNextUpdate());
				journalUpdateAll = updateAll(journalUpdateAll, owner.getJournalNextUpdate());
				transactionsUpdateAll = updateAll(transactionsUpdateAll, owner.getTransactionsNextUpdate());
				contractsUpdateAll = updateAll(contractsUpdateAll, owner.getContractsNextUpdate());
				assetsUpdateAll = updateAll(assetsUpdateAll, owner.getAssetNextUpdate());
				blueprintsUpdateAll = updateAll(blueprintsUpdateAll, owner.getBlueprintsNextUpdate());
				accountBalanceUpdateAll = updateAll(accountBalanceUpdateAll, owner.getBalanceNextUpdate());
			}
		}
		setUpdateLabel(jMarketOrdersUpdate, jMarketOrdersLeft, jMarketOrders, marketOrdersNextUpdate, marketOrdersUpdateAll);
		setUpdateLabel(jJournalUpdate, jJournalLeft, jJournal, journalNextUpdate, journalUpdateAll);
		setUpdateLabel(jTransactionsUpdate, jTransactionsLeft, jTransactions, transactionsNextUpdate, transactionsUpdateAll);
		setUpdateLabel(jIndustryJobsUpdate, jIndustryJobsLeft, jIndustryJobs, industryJobsNextUpdate, industryJobsUpdateAll);
		setUpdateLabel(jAccountBalanceUpdate, jAccountBalanceLeft, jAccountBalance, accountBalanceNextUpdate, accountBalanceUpdateAll);
		setUpdateLabel(jContractsUpdate, jContractsLeft, jContracts, contractsNextUpdate, contractsUpdateAll);
		setUpdateLabel(jAssetsUpdate, jAssetsLeft, jAssets, assetsNextUpdate, assetsUpdateAll);
		setUpdateLabel(jBlueprintsUpdate, jBlueprintsLeft, jBlueprints, blueprintsNextUpdate, blueprintsUpdateAll);
		setUpdateLabel(jPriceDataUpdate, jPriceDataLeft, jPriceDataAll, priceDataNextUpdate, true, false);
		changed();
		if (!jPriceDataAll.isEnabled()) {
			//jUpdate.setEnabled(true);
			//jCheckAll.setEnabled(true);
			jPriceDataNew.setSelected(true);
			changed();
		}
	}

	private void setUpdateLabel(final JLabel jUpdate, final JLabel jLeft, final JToggleButton jCheckBox, final Date nextUpdate, final boolean updateAll) {
		this.setUpdateLabel(jUpdate, jLeft, jCheckBox, nextUpdate, updateAll, true);
	}

	private void setUpdateLabel(final JLabel jUpdate, final JLabel jLeft, final JToggleButton jCheckBox, Date nextUpdate, final boolean updateAll, final boolean ignoreOnProxy) {
		if (nextUpdate == null) {
			nextUpdate = Settings.getNow();
		}
		if (Settings.get().isUpdatable(nextUpdate, ignoreOnProxy)) {
			jLeft.setText("");
			if (updateAll) {
				jUpdate.setText(DialoguesUpdate.get().nowAll());
			} else {
				jUpdate.setText(DialoguesUpdate.get().nowSome());
			}
			jCheckBox.setSelected(true);
			jCheckBox.setEnabled(true);
		} else {
			String timeLeft;
			long time = nextUpdate.getTime() - Settings.getNow().getTime();
			if (time < (60 * 1000)) { //less than 1 minute
				timeLeft = "<1m";
			} else {
				timeLeft = Formater.milliseconds(time);
			}
			jUpdate.setText(Formater.weekdayAndTime(nextUpdate));
			jLeft.setText(timeLeft);
			jCheckBox.setSelected(false);
			jCheckBox.setEnabled(false);
		}
	}

	private Date nextUpdate(Date nextUpdate, Date thisUpdate) {
		if (nextUpdate == null) {
				nextUpdate = thisUpdate;
		}
		if (thisUpdate.before(nextUpdate)) {
			nextUpdate = thisUpdate;
		}
		return nextUpdate;
	}

	private boolean updateAll(final boolean updateAll, final Date nextUpdate) {
		return updateAll && Settings.get().isUpdatable(nextUpdate, true);
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
			update();
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
					TaskDialog taskDialog = new TaskDialog(program, updateTasks);
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
			AccountGetter accountGetter = new AccountGetter();
			accountGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			//EveKit
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
			AssetsGetter assetsGetter = new AssetsGetter();
			assetsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			LocationsGetter locationsGetter = new LocationsGetter();
			locationsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			//EveKit
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
			AccountBalanceGetter accountBalanceGetter = new AccountBalanceGetter();
			accountBalanceGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			//EveKit
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
			IndustryJobsGetter industryJobsGetter = new IndustryJobsGetter();
			industryJobsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			//EveKit
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
			MarketOrdersGetter marketOrdersGetter = new MarketOrdersGetter();
			marketOrdersGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts(), Settings.get().isMarketOrderHistory());
			//EveKit
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
			JournalGetter journalGetter = new JournalGetter();
			journalGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts(), Settings.get().isJournalHistory());
			//EveKit
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
			TransactionsGetter transactionsGetter = new TransactionsGetter();
			transactionsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts(), Settings.get().isTransactionHistory());
			//EveKit
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
			ContractsGetter contractsGetter = new ContractsGetter();
			contractsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			ContractItemsGetter itemsGetter = new ContractItemsGetter();
			itemsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			//EveKit
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
			BlueprintsGetter blueprintsGetter = new BlueprintsGetter();
			blueprintsGetter.load(this, Settings.get().isForceUpdate(), program.getProfileManager().getAccounts());
			//EveKit
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
			if (update) {
				program.getPriceDataGetter().updateAll(this);
			} else {
				program.getPriceDataGetter().updateNew(this);
			}
		}
	}
}
