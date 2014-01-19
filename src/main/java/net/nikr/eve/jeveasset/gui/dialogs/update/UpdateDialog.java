/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.tabs.contracts.Contract;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.eveapi.*;


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
	private final JCheckBox jPriceData;
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
		jPriceData = new JCheckBox(DialoguesUpdate.get().priceData());

		jCheckBoxes.add(jMarketOrders);
		jCheckBoxes.add(jJournal);
		jCheckBoxes.add(jTransactions);
		jCheckBoxes.add(jIndustryJobs);
		jCheckBoxes.add(jAccountBalance);
		jCheckBoxes.add(jContracts);
		jCheckBoxes.add(jAssets);
		jCheckBoxes.add(jPriceData);
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
		jPriceDataUpdate = new JLabel();

		jMarketOrdersLeft = new JLabel();
		jJournalLeft = new JLabel();
		jTransactionsLeft = new JLabel();
		jIndustryJobsLeft = new JLabel();
		jAccountBalanceLeft = new JLabel();
		jContractsLeft = new JLabel();
		jAssetsLeft = new JLabel();
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
					.addGroup(layout.createParallelGroup()
						.addComponent(jCheckAll)
						.addComponent(jMarketOrders)
						.addComponent(jJournal)
						.addComponent(jTransactions)
						.addComponent(jIndustryJobs)
						.addComponent(jAccountBalance)
						.addComponent(jContracts)
						.addComponent(jAssets)
						.addComponent(jPriceData)
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
								.addComponent(jPriceDataUpdate)
							)
						)
					)
				)
				.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
					.addComponent(jUpdate, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)

		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCheckAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jNextUpdateLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jMarketOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jMarketOrdersLeft, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jMarketOrdersUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jJournal, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jJournalLeft, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jJournalUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jTransactions, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jTransactionsLeft, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jTransactionsUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jIndustryJobs, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jIndustryJobsLeft, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jIndustryJobsUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAccountBalance, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAccountBalanceLeft, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAccountBalanceUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jContracts, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jContractsLeft, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jContractsUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAssets, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAssetsLeft, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAssetsUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jPriceData, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPriceDataLeft, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPriceDataUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGap(30)
				.addGroup(layout.createParallelGroup()
					//.addComponent(jCheckAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	private void changed() {
		boolean allChecked = true;
		boolean noneChecked = false;
		boolean allDiabled = true;
		for (JCheckBox jCheckBox : jCheckBoxes) {
			if (jCheckBox.isEnabled() && !jCheckBox.isSelected()) {
				allChecked = false;
			}
			if (jCheckBox.isEnabled() && jCheckBox.isSelected()) {
				noneChecked = true;
			}
			if (jCheckBox.isEnabled()) {
				allDiabled = false;
			}

		}
		jUpdate.setEnabled(noneChecked);
		jCheckAll.setSelected(allChecked && !allDiabled);
	}

	private void update() {
		Date industryJobsNextUpdate = null;
		Date marketOrdersNextUpdate = null;
		Date journalNextUpdate = null;
		Date transactionsNextUpdate = null;
		Date contractsNextUpdate = null;
		Date assetsNextUpdate = null;
		Date accountBalanceNextUpdate = null;

		boolean industryJobsUpdateAll = true;
		boolean marketOrdersUpdateAll = true;
		boolean journalUpdateAll = true;
		boolean transactionsUpdateAll = true;
		boolean contractsUpdateAll = true;
		boolean assetsUpdateAll = true;
		boolean accountBalanceUpdateAll = true;

		Date priceDataNextUpdate = program.getPriceDataGetter().getNextUpdate();
		for (Account account : program.getAccounts()) {
			for (Owner owner : account.getOwners()) {
				if (owner.isShowOwner()) {
					industryJobsNextUpdate = nextUpdate(industryJobsNextUpdate, owner.getIndustryJobsNextUpdate());
					marketOrdersNextUpdate = nextUpdate(marketOrdersNextUpdate, owner.getMarketOrdersNextUpdate());
					journalNextUpdate = nextUpdate(journalNextUpdate, owner.getJournalNextUpdate());
					transactionsNextUpdate = nextUpdate(transactionsNextUpdate, owner.getTransactionsNextUpdate());
					contractsNextUpdate = nextUpdate(contractsNextUpdate, owner.getContractsNextUpdate());
					assetsNextUpdate = nextUpdate(assetsNextUpdate, owner.getAssetNextUpdate());
					accountBalanceNextUpdate = nextUpdate(accountBalanceNextUpdate, owner.getBalanceNextUpdate());

					industryJobsUpdateAll = updateAll(industryJobsUpdateAll, owner.getIndustryJobsNextUpdate());
					marketOrdersUpdateAll = updateAll(marketOrdersUpdateAll, owner.getMarketOrdersNextUpdate());
					journalUpdateAll = updateAll(journalUpdateAll, owner.getJournalNextUpdate());
					transactionsUpdateAll = updateAll(transactionsUpdateAll, owner.getTransactionsNextUpdate());
					contractsUpdateAll = updateAll(contractsUpdateAll, owner.getContractsNextUpdate());
					assetsUpdateAll = updateAll(assetsUpdateAll, owner.getAssetNextUpdate());
					accountBalanceUpdateAll = updateAll(accountBalanceUpdateAll, owner.getBalanceNextUpdate());

				}
			}
		}
		setUpdateLabel(jMarketOrdersUpdate, jMarketOrdersLeft, jMarketOrders, marketOrdersNextUpdate, marketOrdersUpdateAll);
		setUpdateLabel(jJournalUpdate, jJournalLeft, jJournal, journalNextUpdate, journalUpdateAll);
		setUpdateLabel(jTransactionsUpdate, jTransactionsLeft, jTransactions, transactionsNextUpdate, transactionsUpdateAll);
		setUpdateLabel(jIndustryJobsUpdate, jIndustryJobsLeft, jIndustryJobs, industryJobsNextUpdate, industryJobsUpdateAll);
		setUpdateLabel(jAccountBalanceUpdate, jAccountBalanceLeft, jAccountBalance, accountBalanceNextUpdate, accountBalanceUpdateAll);
		setUpdateLabel(jContractsUpdate, jContractsLeft, jContracts, contractsNextUpdate, contractsUpdateAll);
		setUpdateLabel(jAssetsUpdate, jAssetsLeft, jAssets, assetsNextUpdate, assetsUpdateAll);
		setUpdateLabel(jPriceDataUpdate, jPriceDataLeft, jPriceData, priceDataNextUpdate, true, false);
		changed();
		jUpdate.setEnabled(false);
		jCheckAll.setEnabled(false);
		setUpdatableButton(marketOrdersNextUpdate);
		setUpdatableButton(transactionsNextUpdate);
		setUpdatableButton(transactionsNextUpdate);
		setUpdatableButton(industryJobsNextUpdate);
		setUpdatableButton(accountBalanceNextUpdate);
		setUpdatableButton(contractsNextUpdate);
		setUpdatableButton(assetsNextUpdate);
		setUpdatableButton(priceDataNextUpdate, false);
	}

	private void setUpdateLabel(final JLabel jUpdate, final JLabel jLeft, final JCheckBox jCheckBox, final Date nextUpdate, final boolean updateAll) {
		this.setUpdateLabel(jUpdate, jLeft, jCheckBox, nextUpdate, updateAll, true);
	}

	private void setUpdateLabel(final JLabel jUpdate, final JLabel jLeft, final JCheckBox jCheckBox, Date nextUpdate, final boolean updateAll, final boolean ignoreOnProxy) {
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
			long time = nextUpdate.getTime() - Settings.getNow().getTime();
			long minutes = time / (60 * 1000) % 60;
			long hours = time / (60 * 60 * 1000) % 24;
			long days = time / (24 * 60 * 60 * 1000);
			if (days == 0 && hours == 0 && minutes == 0) {
				minutes = -1;
			}
			jUpdate.setText(Formater.weekdayAndTime(nextUpdate));
			jLeft.setText(DialoguesUpdate.get().timeLeft(days, hours, minutes));
			jCheckBox.setSelected(false);
			jCheckBox.setEnabled(false);
		}
	}

	private void setUpdatableButton(final Date nextUpdate) {
		setUpdatableButton(nextUpdate, true);
	}

	private void setUpdatableButton(Date nextUpdate, final boolean ignoreOnProxy) {
		if (nextUpdate == null) {
			nextUpdate = Settings.getNow();
		}
		if (Settings.get().isUpdatable(nextUpdate, ignoreOnProxy)) {
			jUpdate.setEnabled(true);
			jCheckAll.setEnabled(true);
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
				if (jPriceData.isSelected() //May need prices for new items
						|| jMarketOrders.isSelected()
						|| jIndustryJobs.isSelected()
						|| jAssets.isSelected()
						) {
					updateTasks.add(new PriceDataTask(jPriceData.isSelected()));
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
		}
	}

	public class AccountsTask extends UpdateTask {

		public AccountsTask() {
			super(DialoguesUpdate.get().accounts());
		}

		@Override
		public void update() {
			AccountGetter accountGetter = new AccountGetter();
			accountGetter.load(this, Settings.get().isForceUpdate(), program.getAccounts());
		}
	}

	public class AssetsTask extends UpdateTask {

		public AssetsTask() {
			super(DialoguesUpdate.get().assets());
		}

		@Override
		public void update() {
			AssetsGetter assetsGetter = new AssetsGetter();
			assetsGetter.load(this, Settings.get().isForceUpdate(), program.getAccounts());
			LocationsGetter locationsGetter = new LocationsGetter();
			locationsGetter.load(this, Settings.get().isForceUpdate(), program.getAccounts());
		}
	}

	public class BalanceTask extends UpdateTask {

		public BalanceTask() {
			super(DialoguesUpdate.get().balance());
		}

		@Override
		public void update() {
			AccountBalanceGetter accountBalanceGetter = new AccountBalanceGetter();
			accountBalanceGetter.load(this, Settings.get().isForceUpdate(), program.getAccounts());
		}
	}

	public class IndustryJobsTask extends UpdateTask {

		public IndustryJobsTask() {
			super(DialoguesUpdate.get().industryJobs());
		}

		@Override
		public void update() {
			IndustryJobsGetter industryJobsGetter = new IndustryJobsGetter();
			industryJobsGetter.load(this, Settings.get().isForceUpdate(), program.getAccounts());
		}
	}

	public class MarketOrdersTask extends UpdateTask {

		public MarketOrdersTask() {
			super(DialoguesUpdate.get().marketOrders());
		}

		@Override
		public void update() {
			MarketOrdersGetter marketOrdersGetter = new MarketOrdersGetter();
			marketOrdersGetter.load(this, Settings.get().isForceUpdate(), program.getAccounts());
		}
	}

	public class JournalTask extends UpdateTask {

		public JournalTask() {
			super(DialoguesUpdate.get().journal());
		}

		@Override
		public void update() {
			JournalGetter journalGetter = new JournalGetter();
			journalGetter.load(this, Settings.get().isForceUpdate(), program.getAccounts(), Settings.get().isJournalHistory());
		}
	}

	public class TransactionsTask extends UpdateTask {

		public TransactionsTask() {
			super(DialoguesUpdate.get().transactions());
		}

		@Override
		public void update() {
			TransactionsGetter transactionsGetter = new TransactionsGetter();
			transactionsGetter.load(this, Settings.get().isForceUpdate(), program.getAccounts(), Settings.get().isTransactionHistory());
		}
	}

	public class ContractsTask extends UpdateTask {

		public ContractsTask() {
			super(DialoguesUpdate.get().contracts());
		}

		@Override
		public void update() {
			//Get Contracts
			ContractsGetter contractsGetter = new ContractsGetter();
			contractsGetter.load(this, Settings.get().isForceUpdate(), program.getAccounts());
			//Get Contract Items
			ContractItemsGetter itemsGetter = new ContractItemsGetter();
			itemsGetter.load(this, Settings.get().isForceUpdate(), program.getAccounts());
			Set<Long> list = new HashSet<Long>();
			for (Account account : program.getAccounts()) {
				for (Owner owner : account.getOwners()) {
					list.add(owner.getOwnerID()); //Just to be sure
					for (Contract contract : owner.getContracts().keySet()) {
						list.add(contract.getAcceptorID());
						list.add(contract.getAssigneeID());
						list.add(contract.getIssuerCorpID());
						list.add(contract.getIssuerID());
					}
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
				program.getPriceDataGetter().update(this);
			} else {
				program.getPriceDataGetter().load(this);
			}
		}
	}
}
