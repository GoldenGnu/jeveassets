/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
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
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.Updatable;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.tabs.values.AssetValue;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.io.esi.EsiAccountBalanceGetter;
import net.nikr.eve.jeveasset.io.esi.EsiAssetsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiBlueprintsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiClonesGetter;
import net.nikr.eve.jeveasset.io.esi.EsiContractItemsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiContractsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiDivisionsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiFactionWarfareGetter;
import net.nikr.eve.jeveasset.io.esi.EsiIndustryJobsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiJournalGetter;
import net.nikr.eve.jeveasset.io.esi.EsiLocationsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiLoyaltyPointsGetter;
import net.nikr.eve.jeveasset.io.esi.EsiManufacturingPrices;
import net.nikr.eve.jeveasset.io.esi.EsiMarketOrdersGetter;
import net.nikr.eve.jeveasset.io.esi.EsiMiningGetter;
import net.nikr.eve.jeveasset.io.esi.EsiNameGetter;
import net.nikr.eve.jeveasset.io.esi.EsiOwnerGetter;
import net.nikr.eve.jeveasset.io.esi.EsiPlanetaryInteractionGetter;
import net.nikr.eve.jeveasset.io.esi.EsiShipGetter;
import net.nikr.eve.jeveasset.io.esi.EsiSkillGetter;
import net.nikr.eve.jeveasset.io.esi.EsiNpcStandingGetter;
import net.nikr.eve.jeveasset.io.esi.EsiTransactionsGetter;
import net.nikr.eve.jeveasset.io.online.EveImageGetter;
import net.nikr.eve.jeveasset.io.online.PriceDataGetter;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker;


public class UpdateDialog extends JDialogCentered {

	private enum UpdateDialogAction {
		CANCEL, UPDATE, CHANGED, CHECK_ALL
	}

	private static enum Updates {
		MARKET_ORDERS(DialoguesUpdate.get().marketOrders()){
			@Override
			public boolean is(OwnerType owner) {
				return owner.isMarketOrders();
			}

			@Override
			public Date nextUpdate(OwnerType owner) {
				return owner.getMarketOrdersNextUpdate();
			}
		},
		JOURNAL(DialoguesUpdate.get().journal()){
			@Override
			public boolean is(OwnerType owner) {
				return owner.isJournal();
			}

			@Override
			public Date nextUpdate(OwnerType owner) {
				return owner.getJournalNextUpdate();
			}
		},
		TRANSACTIONS(DialoguesUpdate.get().transactions()){
			@Override
			public boolean is(OwnerType owner) {
				return owner.isTransactions();
			}

			@Override
			public Date nextUpdate(OwnerType owner) {
				return owner.getTransactionsNextUpdate();
			}
		},
		INDUSTRY_JOBS(DialoguesUpdate.get().industryJobs()){
			@Override
			public boolean is(OwnerType owner) {
				return owner.isIndustryJobs();
			}

			@Override
			public Date nextUpdate(OwnerType owner) {
				return owner.getIndustryJobsNextUpdate();
			}
		},
		ACCOUNT_BALANCE(DialoguesUpdate.get().accountBalance()){
			@Override
			public boolean is(OwnerType owner) {
				return owner.isAccountBalance();
			}

			@Override
			public Date nextUpdate(OwnerType owner) {
				return owner.getBalanceNextUpdate();
			}
		},
		CONTRACTS(DialoguesUpdate.get().contracts()){
			@Override
			public boolean is(OwnerType owner) {
				return owner.isContracts();
			}

			@Override
			public Date nextUpdate(OwnerType owner) {
				return owner.getContractsNextUpdate();
			}
		},
		ASSETS(DialoguesUpdate.get().assets()) {
			@Override
			public boolean is(OwnerType owner) {
				return owner.isAssetList();
			}

			@Override
			public Date nextUpdate(OwnerType owner) {
				return owner.getAssetNextUpdate();
			}
		},
		BLUEPRINTS(DialoguesUpdate.get().blueprints()){
			@Override
			public boolean is(OwnerType owner) {
				return owner.isBlueprints();
			}

			@Override
			public Date nextUpdate(OwnerType owner) {
				return owner.getBlueprintsNextUpdate();
			}
		},
		SKILLS(DialoguesUpdate.get().skills()){
			@Override
			public boolean is(OwnerType owner) {
				return owner.isSkills();
			}

			@Override
			public Date nextUpdate(OwnerType owner) {
				return owner.getSkillsNextUpdate();
			}
		},
		LOYALTY_POINTS(DialoguesUpdate.get().loyaltyPoints()){
			@Override
			public boolean is(OwnerType owner) {
				return owner.isLoyaltyPoints();
			}

			@Override
			public Date nextUpdate(OwnerType owner) {
				return owner.getLoyaltyPointsNextUpdate();
			}
		},
		NPC_STANDING(DialoguesUpdate.get().npcStanding()) {
			@Override
			public boolean is(OwnerType owner) {
				return owner.isNpcStanding();
			}

			@Override
			public Date nextUpdate(OwnerType owner) {
				return owner.getNpcStandingNextUpdate();
			}
		},
		MINING(DialoguesUpdate.get().mining()){
			@Override
			public boolean is(OwnerType owner) {
				return owner.isMining();
			}

			@Override
			public Date nextUpdate(OwnerType owner) {
				return owner.getMiningNextUpdate();
			}
		};

		private final String title;
		private UpdateUI updateUI = null;
		private Date first = null;
		private Date last = null;
		private Updates(String title) {
			this.title = title;
		}

		public abstract boolean is(OwnerType owner);
		public abstract Date nextUpdate(OwnerType owner);

		public Date getFirst() {
			return first;
		}

		public Date getLast() {
			return last;
		}

		public JCheckBox getCheckBox() {
			return getUI().getCheckBox();
		}

		public JLabel getLeftFirst() {
			return getUI().getLeftFirst();
		}

		public JLabel getLeftLast() {
			return getUI().getLeftLast();
		}

		public boolean isSelected() {
			return getCheckBox().isSelected();
		}

		public void setSelected(boolean b) {
			getCheckBox().setSelected(b);
		}

		public void reset() {
			first = null;
			last = null;
		}

		public void updateDates(OwnerType owner) {
			if (is(owner)) {
				first = updateFirst(first, nextUpdate(owner));
				last = updateLast(last, nextUpdate(owner));
			}
		}

		private UpdateUI getUI() {
			if (updateUI == null) {
				updateUI = new UpdateUI(title);
			}
			return updateUI;
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
	}

	private final JCheckBox jCheckAll;
	private final JRadioButton jPriceDataAll;
	private final JRadioButton jPriceDataNew;
	private final JRadioButton jPriceDataNone;
	private final JLabel jPriceDataLeft;
	private final JButton jUpdate;
	private final JButton jCancel;
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
		JLabel jLeftFirst = new JLabel(DialoguesUpdate.get().firstAccount());
		JLabel jLeftLast = new JLabel(DialoguesUpdate.get().allAccounts());

		jPriceDataAll = new JRadioButton(DialoguesUpdate.get().priceData());
		jPriceDataAll.setActionCommand(UpdateDialogAction.CHANGED.name());
		jPriceDataAll.addActionListener(listener);
		jPriceDataNew = new JRadioButton(DialoguesUpdate.get().priceDataNew());
		jPriceDataNew.setActionCommand(UpdateDialogAction.CHANGED.name());
		jPriceDataNew.addActionListener(listener);
		jPriceDataNone = new JRadioButton(DialoguesUpdate.get().priceDataNone());
		jPriceDataNone.setActionCommand(UpdateDialogAction.CHANGED.name());
		jPriceDataNone.addActionListener(listener);
		jPriceDataLeft = new JLabel();
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(jPriceDataAll);
		buttonGroup.add(jPriceDataNew);
		buttonGroup.add(jPriceDataNone);

		jUpdate = new JButton(DialoguesUpdate.get().update());
		jUpdate.setActionCommand(UpdateDialogAction.UPDATE.name());
		jUpdate.addActionListener(listener);

		jCancel = new JButton(DialoguesUpdate.get().cancel());
		jCancel.setActionCommand(UpdateDialogAction.CANCEL.name());
		jCancel.addActionListener(listener);

		GroupLayout.ParallelGroup horizontalCheckBox = layout.createParallelGroup();
		GroupLayout.ParallelGroup horizontalLeftFirst = layout.createParallelGroup(Alignment.TRAILING);
		GroupLayout.ParallelGroup horizontalLeftLast = layout.createParallelGroup(Alignment.TRAILING);
		horizontalCheckBox.addComponent(jCheckAll);
		horizontalLeftFirst.addComponent(jLeftFirst);
		horizontalLeftLast.addComponent(jLeftLast);

		GroupLayout.SequentialGroup vertical = layout.createSequentialGroup();
		vertical.addGroup(layout.createParallelGroup()
				.addComponent(jCheckAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jLeftFirst, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jLeftLast, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
			);
		
		for (Updates updates : Updates.values()) {
			JCheckBox jCheckBox = updates.getCheckBox();
			horizontalCheckBox.addComponent(jCheckBox);
			horizontalLeftFirst.addComponent(updates.getLeftFirst());
			horizontalLeftLast.addComponent(updates.getLeftLast());
			vertical.addGroup(layout.createParallelGroup()
					.addComponent(updates.getCheckBox(), Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(updates.getLeftFirst(), Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(updates.getLeftLast(), Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				);
			jCheckBox.setActionCommand(UpdateDialogAction.CHANGED.name());
			jCheckBox.addActionListener(listener);
		}
		horizontalLeftLast.addComponent(jPriceDataLeft);

		vertical.addGroup(layout.createParallelGroup()
				.addComponent(jPriceDataAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jPriceDataNew, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jPriceDataNone, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jPriceDataLeft, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
			)
			.addGap(30)
			.addGroup(layout.createParallelGroup()
				.addComponent(jUpdate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
			);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
							.addGroup(horizontalCheckBox)
							.addGap(20)
							.addGroup(horizontalLeftFirst)
							.addGap(20)
						)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jPriceDataAll)
							.addGap(10)
							.addComponent(jPriceDataNew)
							.addGap(10)
							.addComponent(jPriceDataNone)
						)
					)
					.addGroup(horizontalLeftLast)
				)
				.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
					.addComponent(jUpdate, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(vertical);
	}

	private void changed() {
		boolean allChecked = true;
		boolean someChecked = false;
		boolean allDisabled = true;
		for (Updates updates : Updates.values()) {
			JCheckBox jCheckBox = updates.getCheckBox();
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

	private void update(boolean check) {
		for (Updates updates : Updates.values()) {
			updates.reset();
		}
		Date priceData = program.getPriceDataGetter().getNextUpdate();
		for (OwnerType owner : program.getOwnerTypes()) {
			if (!owner.isShowOwner() || owner.isInvalid() || owner.isExpired()) {
				continue;
			}
			for (Updates updates : Updates.values()) {
				updates.updateDates(owner);
			}
		}
		if (program.getOwnerTypes().isEmpty()) {
			jPriceDataNone.setSelected(true);
			jPriceDataNone.setEnabled(false);
			jPriceDataNew.setEnabled(false);
			jPriceDataAll.setEnabled(false);
			setUpdateLabel(null, jPriceDataLeft, jPriceDataAll, null, null, check);
		} else {
			jPriceDataNone.setEnabled(true);
			jPriceDataNew.setEnabled(true);
			jPriceDataAll.setEnabled(true);
			setUpdateLabel(null, jPriceDataLeft, jPriceDataAll, null, priceData, check);
			if (!jPriceDataAll.isEnabled() && jPriceDataNew.isEnabled() && !jPriceDataNone.isSelected()) {
				jPriceDataNew.setSelected(true);
			}
		}
		for (Updates updates : Updates.values()) {
			setUpdateLabel(updates.getLeftFirst(), updates.getLeftLast(), updates.getCheckBox(), updates.getFirst(), updates.getLast(), check);
		}
		changed();

	}

	private void setUpdateLabel(final JLabel jFirst, final JLabel jLast, final JToggleButton jCheckBox, final Date first, final Date last, boolean check) {
		if (jFirst != null) {
			if (Updatable.isUpdatable(last)) {
				jFirst.setText("");
			} else {
				jFirst.setText(getFormattedDuration(first));
			}
			jFirst.setEnabled(first != null);
		} else {
		}
		if (jLast != null) {
			jLast.setText(getFormattedDuration(last));
			jLast.setEnabled(last != null);
		}
		if (jCheckBox != null) {
			if ((Updatable.isUpdatable(first) || Updatable.isUpdatable(last))) {
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

	private String getFormattedDuration(Date date) {
		if (date == null) { //less than 1 second
			return DialoguesUpdate.get().noAccounts();
		} else if (Updatable.isUpdatable(date)) {
			return DialoguesUpdate.get().now();
		} else {
			long time = date.getTime() - Settings.getNow().getTime();
			if (time <= 1000) { //less than 1 second
				return "...";
			} else if (time < (60 * 1000)) { //less than 1 minute
				return Formatter.milliseconds(time, false, false, false, true);
			} else {
				return Formatter.milliseconds(time, true, true, true, false);
			}
		}
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
			for (Updates updates : Updates.values()) {
				updates.setSelected(true);
			}
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
				List<UpdateTask> updateTasks = new ArrayList<>();
				if (Updates.ASSETS.isSelected()
						|| Updates.ACCOUNT_BALANCE.isSelected()
						|| Updates.BLUEPRINTS.isSelected()
						|| Updates.CONTRACTS.isSelected()
						|| Updates.INDUSTRY_JOBS.isSelected()
						|| Updates.JOURNAL.isSelected()
						|| Updates.MARKET_ORDERS.isSelected()
						|| Updates.MINING.isSelected()
						|| Updates.TRANSACTIONS.isSelected()
						|| Updates.SKILLS.isSelected()
						|| Updates.LOYALTY_POINTS.isSelected()
						|| Updates.NPC_STANDING.isSelected()
						) {
					updateTasks.add(new Step1Task(program.getProfileManager()));
					updateTasks.add(new Step2Task(program.getProfileManager(),
							Updates.ASSETS.isSelected(),
							Updates.ACCOUNT_BALANCE.isSelected(),
							Updates.BLUEPRINTS.isSelected(),
							Updates.CONTRACTS.isSelected(),
							Updates.INDUSTRY_JOBS.isSelected(),
							Updates.JOURNAL.isSelected(),
							Updates.MARKET_ORDERS.isSelected(),
							Updates.MINING.isSelected(),
							Updates.TRANSACTIONS.isSelected(),
							Updates.SKILLS.isSelected(),
							Updates.LOYALTY_POINTS.isSelected(),
							Updates.NPC_STANDING.isSelected()));
					updateTasks.add(new Step3Task(program.getProfileManager(), Updates.ASSETS.isSelected()));
				}
				if (Updates.CONTRACTS.isSelected()) {
					updateTasks.add(new Step4Task(program.getProfileManager()));
				}
				if (jPriceDataAll.isSelected() || jPriceDataNew.isSelected()) {
					updateTasks.add(new PriceDataTask(program.getPriceDataGetter(), program.getProfileData(), jPriceDataAll.isSelected()));
				}
				if (!updateTasks.isEmpty()) {
					//Pause structure update
					program.getStatusPanel().setPauseUpdates(true);
					TaskDialog taskDialog = new TaskDialog(program, updateTasks, false, false, false, null, new TaskDialog.TasksCompleted() {
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
				for (Updates updates : Updates.values()) {
					JCheckBox jCheckBox = updates.getCheckBox();
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

	public static class Step1Task extends UpdateTask {

		private final ProfileManager profileManager;

		public Step1Task(final ProfileManager profileManager) {
			super(DialoguesUpdate.get().step1());
			this.profileManager = profileManager;
		}

		@Override
		public void update() {
			setIcon(null);
			//Esi
			List<Runnable> updates = new ArrayList<>();
			for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
				updates.add(new EsiOwnerGetter(this, esiOwner));
			}
			ThreadWoker.start(this, updates);
		}
	}

	public static class Step2Task extends UpdateTask {

		private final ProfileManager profileManager;
		private final boolean assets;
		private final boolean accountBalance;
		private final boolean blueprints;
		private final boolean contracts;
		private final boolean industryJobs;
		private final boolean journal;
		private final boolean marketOrders;
		private final boolean mining;
		private final boolean transactions;
		private final boolean skills;
		private final boolean loyaltyPoints;
		private final boolean npcStanding;

		public Step2Task(final ProfileManager profileManager,
								final boolean assets,
								final boolean accountBalance,
								final boolean blueprints,
								final boolean contracts,
								final boolean industryJobs,
								final boolean journal,
								final boolean marketOrders,
								final boolean mining,
								final boolean transactions,
								final boolean skills,
								final boolean loyaltyPoints,
								final boolean npcStanding) {
			super(DialoguesUpdate.get().step2());
			this.profileManager = profileManager;
			this.assets = assets;
			this.accountBalance = accountBalance;
			this.blueprints = blueprints;
			this.contracts = contracts;
			this.industryJobs = industryJobs;
			this.journal = journal;
			this.marketOrders = marketOrders;
			this.mining = mining;
			this.transactions = transactions;
			this.skills = skills;
			this.loyaltyPoints = loyaltyPoints;
			this.npcStanding = npcStanding;
		}

		@Override
		public void update() {
			setIcon(null);
			//Esi
			List<Runnable> updates = new ArrayList<>();
			for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
				if (accountBalance) {
					updates.add(new EsiAccountBalanceGetter(this, esiOwner));
				}
				if (assets) {
					updates.add(new EsiAssetsGetter(this, esiOwner));
					if (esiOwner.isCorporation()) {
						updates.add(new EsiDivisionsGetter(this, esiOwner));
					}
				}
				if (industryJobs) {
					updates.add(new EsiIndustryJobsGetter(this, esiOwner, Settings.get().isIndustryJobsHistory()));
				}
				if (mining) {
					updates.add(new EsiMiningGetter(this, esiOwner, Settings.get().isMiningHistory()));
				}
				if (marketOrders) {
					updates.add(new EsiMarketOrdersGetter(this, esiOwner, Settings.get().isMarketOrderHistory()));
				}
				if (journal) {
					updates.add(new EsiJournalGetter(this, esiOwner, Settings.get().isJournalHistory()));
				}
				if (transactions) {
					updates.add(new EsiTransactionsGetter(this, esiOwner, Settings.get().isTransactionHistory()));
				}
				if (contracts) {
					updates.add(new EsiContractsGetter(this, esiOwner, Settings.get().isContractHistory()));
				}
				if (blueprints) {
					updates.add(new EsiBlueprintsGetter(this, esiOwner));
				}
				if (skills) {
					updates.add(new EsiSkillGetter(this, esiOwner));
				}
				if (loyaltyPoints) {
					updates.add(new EsiLoyaltyPointsGetter(this, esiOwner));
				}
				if (npcStanding) {
					updates.add(new EsiNpcStandingGetter(this, esiOwner));
				}
			}
			ThreadWoker.start(this, updates);
		}
	}

	public static class Step3Task extends UpdateTask {

		private final ProfileManager profileManager;
		private final boolean assets;
		private final Map<EsiOwner, Date> assetNextUpdate = new HashMap<>();

		public Step3Task(final ProfileManager profileManager, final boolean assets) {
			super(DialoguesUpdate.get().step3());
			this.profileManager = profileManager;
			this.assets = assets;
			for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
				assetNextUpdate.put(esiOwner, esiOwner.getAssetNextUpdate());
			}
		}

		@Override
		public void update() {
			setIcon(null);
			List<Runnable> updates = new ArrayList<>();
			//Locations
			if (assets) {
				//Esi
				for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
					updates.add(new EsiLocationsGetter(this, esiOwner));
					updates.add(new EsiShipGetter(this, esiOwner, assetNextUpdate.getOrDefault(esiOwner, Settings.getNow())));
					updates.add(new EsiPlanetaryInteractionGetter(this, esiOwner, assetNextUpdate.getOrDefault(esiOwner, Settings.getNow())));
					updates.add(new EsiClonesGetter(this, esiOwner, assetNextUpdate.getOrDefault(esiOwner, Settings.getNow())));
				}
			}
			updates.add(new EsiFactionWarfareGetter(this));
			//char/corp/alliance IDs to names (ESI)
			updates.add(new EsiNameGetter(this, profileManager.getOwnerTypes()));
			//char/corp/alliance images
			updates.add(new EveImageGetter(this, profileManager.getOwnerTypes()));
			ThreadWoker.start(this, updates);
		}
	}

	public static class Step4Task extends UpdateTask {

		private final ProfileManager profileManager;

		public Step4Task(final ProfileManager profileManager) {
			super(DialoguesUpdate.get().step4());
			this.profileManager = profileManager;
		}

		@Override
		public void update() {
			setIcon(null);
			//Contract Items
			//Esi
			List<Runnable> updates = new ArrayList<>();
			EsiContractItemsGetter.reset();
			for (EsiOwner esiOwner : profileManager.getEsiOwners()) {
				updates.add(new EsiContractItemsGetter(this, esiOwner, profileManager.getEsiOwners(), Settings.get().isContractHistory()));
			}
			ThreadWoker.start(this, updates, false);
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
			ThreadWoker.start(this, Collections.singleton(new EsiManufacturingPrices(this)), false);
			if (update) {
				priceDataGetter.updateAll(profileData, this);
			} else {
				priceDataGetter.updateNew(profileData, this);
			}
		}
	}

	private static class UpdateUI {
		private final JCheckBox jCheckBox;
		private final JLabel jLeftFirst;
		private final JLabel jLeftLast;

		public UpdateUI(String title) {
			jCheckBox = new JCheckBox(title);
			jLeftFirst = new JLabel();
			jLeftLast = new JLabel();
		}

		public JCheckBox getCheckBox() {
			return jCheckBox;
		}

		public JLabel getLeftFirst() {
			return jLeftFirst;
		}

		public JLabel getLeftLast() {
			return jLeftLast;
		}
	}
}
