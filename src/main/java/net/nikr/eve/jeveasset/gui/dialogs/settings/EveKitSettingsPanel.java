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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Date;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.components.JIntegerField;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class EveKitSettingsPanel extends JSettingsPanel {

	private final JRadioButton jTransactionsAll;
	private final JRadioButton jTransactionsMonths;
	private final JTextField jTransactions;
	private final JRadioButton jJournalAll;
	private final JRadioButton jJournalMonths;
	private final JTextField jJournal;
	private final JRadioButton jMarketOrdersAll;
	private final JRadioButton jMarketOrdersMonths;
	private final JTextField jMarketOrders;
	private final JRadioButton jIndustryJobsAll;
	private final JRadioButton jIndustryJobsMonths;
	private final JTextField jIndustryJobs;
	private final JRadioButton jContractsAll;
	private final JRadioButton jContractsMonths;
	private final JTextField jContracts;
	
	public EveKitSettingsPanel(final Program program, final SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().eveKit(), Images.MISC_EVEKIT.getIcon());

		//Transactions
		JLabel jTransactionsLabel = new JLabel(DialoguesSettings.get().transactionsHistory());
		jTransactionsLabel.setIcon(Images.TOOL_TRANSACTION.getIcon());
		ButtonGroup transactionsButtons = new ButtonGroup();
		jTransactionsAll = new JRadioButton(DialoguesSettings.get().allHistory());
		jTransactionsAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jTransactions.setEnabled(false);
			}
		});
		transactionsButtons.add(jTransactionsAll);
		jTransactionsMonths = new JRadioButton(DialoguesSettings.get().monthsHistory());
		jTransactionsMonths.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jTransactions.setEnabled(true);
				jTransactions.setText("1");
			}
		});
		transactionsButtons.add(jTransactionsMonths);
		jTransactions = new JIntegerField(DocumentFactory.ValueFlag.POSITIVE_AND_NOT_ZERO);
		jTransactions.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jTransactions.selectAll();
			}
		});

		//Journal
		JLabel jJournalLabel = new JLabel(DialoguesSettings.get().journalHistory());
		jJournalLabel.setIcon(Images.TOOL_JOURNAL.getIcon());
		ButtonGroup journalButtons = new ButtonGroup();
		jJournalAll = new JRadioButton(DialoguesSettings.get().allHistory());
		jJournalAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jJournal.setEnabled(false);
			}
		});
		journalButtons.add(jJournalAll);
		jJournalMonths = new JRadioButton(DialoguesSettings.get().monthsHistory());
		jJournalMonths.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jJournal.setEnabled(true);
				jJournal.setText("1");
			}
		});
		journalButtons.add(jJournalMonths);
		jJournal = new JIntegerField(DocumentFactory.ValueFlag.POSITIVE_AND_NOT_ZERO);
		jJournal.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jJournal.selectAll();
			}
		});

		//Market Orders
		JLabel jMarketOrdersLabel = new JLabel(DialoguesSettings.get().marketOrdersHistory());
		jMarketOrdersLabel.setIcon(Images.TOOL_MARKET_ORDERS.getIcon());
		ButtonGroup marketOrdersButtons = new ButtonGroup();
		jMarketOrdersAll = new JRadioButton(DialoguesSettings.get().allHistory());
		jMarketOrdersAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jMarketOrders.setEnabled(false);
			}
		});
		marketOrdersButtons.add(jMarketOrdersAll);
		jMarketOrdersMonths = new JRadioButton(DialoguesSettings.get().monthsHistory());
		jMarketOrdersMonths.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jMarketOrders.setEnabled(true);
				jMarketOrders.setText("1");
			}
		});
		marketOrdersButtons.add(jMarketOrdersMonths);
		jMarketOrders = new JIntegerField(DocumentFactory.ValueFlag.POSITIVE_AND_NOT_ZERO);
		jMarketOrders.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jMarketOrders.selectAll();
			}
		});

		//Industry Jobs
		JLabel jIndustryJobsLabel = new JLabel(DialoguesSettings.get().industryJobsHistory());
		jIndustryJobsLabel.setIcon(Images.TOOL_INDUSTRY_JOBS.getIcon());
		ButtonGroup industryJobsButtons = new ButtonGroup();
		jIndustryJobsAll = new JRadioButton(DialoguesSettings.get().allHistory());
		jIndustryJobsAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jIndustryJobs.setEnabled(false);
			}
		});
		industryJobsButtons.add(jIndustryJobsAll);
		jIndustryJobsMonths = new JRadioButton(DialoguesSettings.get().monthsHistory());
		jIndustryJobsMonths.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jIndustryJobs.setEnabled(true);
				jIndustryJobs.setText("1");
			}
		});
		industryJobsButtons.add(jIndustryJobsMonths);
		jIndustryJobs = new JIntegerField(DocumentFactory.ValueFlag.POSITIVE_AND_NOT_ZERO);
		jIndustryJobs.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jIndustryJobs.selectAll();
			}
		});

		//Contracts
		JLabel jContractsLabel = new JLabel(DialoguesSettings.get().contractsHistory());
		jContractsLabel.setIcon(Images.TOOL_CONTRACTS.getIcon());
		ButtonGroup contractsButtons = new ButtonGroup();
		jContractsAll = new JRadioButton(DialoguesSettings.get().allHistory());
		jContractsAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jContracts.setEnabled(false);
			}
		});
		contractsButtons.add(jContractsAll);
		jContractsMonths = new JRadioButton(DialoguesSettings.get().monthsHistory());
		jContractsMonths.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jContracts.setEnabled(true);
				jContracts.setText("1");
			}
		});
		contractsButtons.add(jContractsMonths);
		jContracts = new JIntegerField(DocumentFactory.ValueFlag.POSITIVE_AND_NOT_ZERO);
		jContracts.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jContracts.selectAll();
			}
		});

		
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(jTransactionsLabel)
				.addComponent(jJournalLabel)
				.addComponent(jMarketOrdersLabel)
				.addComponent(jMarketOrdersLabel)
				.addComponent(jIndustryJobsLabel)
				.addComponent(jContractsLabel)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jTransactionsAll)
						.addComponent(jJournalAll)
						.addComponent(jMarketOrdersAll)
						.addComponent(jIndustryJobsAll)
						.addComponent(jContractsAll)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jTransactionsMonths)
						.addComponent(jJournalMonths)
						.addComponent(jMarketOrdersMonths)
						.addComponent(jIndustryJobsMonths)
						.addComponent(jContractsMonths)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jTransactions, 75, 75, 75)
						.addComponent(jJournal, 75, 75, 75)
						.addComponent(jMarketOrders, 75, 75, 75)
						.addComponent(jIndustryJobs, 75, 75, 75)
						.addComponent(jContracts, 75, 75, 75)
					)
				)
				
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jTransactionsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jTransactionsAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTransactionsMonths, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTransactions, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jJournalLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jJournalAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jJournalMonths, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jJournal, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jMarketOrdersLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jMarketOrdersAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMarketOrdersMonths, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMarketOrders, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jIndustryJobsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jIndustryJobsAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jIndustryJobsMonths, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jIndustryJobs, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jContractsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createParallelGroup()
					.addComponent(jContractsAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContractsMonths, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContracts, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	@Override
	public boolean save() {
		//Transactions
		int transactions;
		if (jTransactionsAll.isSelected()) {
			transactions = 0;
		} else {
			transactions = Integer.valueOf(jTransactions.getText()); //Can not be null
			
		}
		boolean transactionsChanged = transactions != Settings.get().getEveKitTransactionsHistory();
		Settings.get().setEveKitTransactionsHistory(transactions);
		//Journal
		int journal;
		if (jJournalAll.isSelected()) {
			journal = 0;
		} else {
			journal = Integer.valueOf(jJournal.getText()); //Can not be null
		}
		boolean journalChanged = journal != Settings.get().getEveKitJournalHistory();
		Settings.get().setEveKitJournalHistory(journal);

		//Market Orders
		int marketOrders;
		if (jMarketOrdersAll.isSelected()) {
			marketOrders = 0;
		} else {
			marketOrders = Integer.valueOf(jMarketOrders.getText()); //Can not be null
		}
		boolean marketOrdersChanged = marketOrders != Settings.get().getEveKitMarketOrdersHistory();
		Settings.get().setEveKitMarketOrdersHistory(marketOrders);

		//Industry Jobs
		int industryJobs;
		if (jIndustryJobsAll.isSelected()) {
			industryJobs = 0;
		} else {
			industryJobs = Integer.valueOf(jIndustryJobs.getText()); //Can not be null
		}
		boolean industryJobsChanged = industryJobs != Settings.get().getEveKitIndustryJobsHistory();
		Settings.get().setEveKitIndustryJobsHistory(industryJobs);

		//Industry Jobs
		int contracts;
		if (jContractsAll.isSelected()) {
			contracts = 0;
		} else {
			contracts = Integer.valueOf(jContracts.getText()); //Can not be null
		}
		boolean contractsChanged = contracts != Settings.get().getEveKitContractsHistory();
		Settings.get().setEveKitContractsHistory(contracts);
		boolean saveProfile = false;
		//Reset EveKitOwners item cache and update time
		if (transactionsChanged || journalChanged || marketOrdersChanged || industryJobsChanged || contractsChanged) {
			for (OwnerType ownerType : program.getOwnerTypes()) {
				if (ownerType instanceof EveKitOwner) {
					EveKitOwner eveKitOwner = (EveKitOwner) ownerType;
					if (transactionsChanged) {
						eveKitOwner.setTransactionsCID(null); //Reset item cache
						eveKitOwner.setTransactionsNextUpdate(new Date(0)); //Reset update time
						saveProfile = true;
					}
					if (journalChanged) {
						eveKitOwner.setJournalCID(null); //Reset item cache
						eveKitOwner.setJournalNextUpdate(new Date(0)); //Reset update time
						saveProfile = true;
					}
					if (marketOrdersChanged) {
						eveKitOwner.setMarketOrdersCID(null); //Reset item cache
						eveKitOwner.setMarketOrdersNextUpdate(new Date(0)); //Reset update time
						saveProfile = true;
					}
					if (industryJobsChanged) {
						eveKitOwner.setIndustryJobsCID(null); //Reset item cache
						eveKitOwner.setIndustryJobsNextUpdate(new Date(0)); //Reset update time
						saveProfile = true;
					}
					if (contractsChanged) {
						eveKitOwner.setContractsCID(null); //Reset item cache
						eveKitOwner.setContractsNextUpdate(new Date(0)); //Reset update time
						saveProfile = true;
					}
				}
			}
		}
		if (saveProfile) {
			program.saveProfile();
		}
		return false; //Do not need to update the EventLists
	}

	@Override
	public void load() {
		Integer eveKitTransactionsHistory = Settings.get().getEveKitTransactionsHistory();
		if (eveKitTransactionsHistory == 0) {
			jTransactionsAll.setSelected(true);
			jTransactions.setEnabled(false);
			jTransactions.setText("1");
		} else {
			jTransactionsMonths.setSelected(true);
			jTransactions.setEnabled(true);
			jTransactions.setText("" + eveKitTransactionsHistory);
		}
		Integer eveKitJournalHistory = Settings.get().getEveKitJournalHistory();
		if (eveKitJournalHistory == 0) {
			jJournalAll.setSelected(true);
			jJournal.setEnabled(false);
			jJournal.setText("1");
		} else {
			jJournalMonths.setSelected(true);
			jJournal.setEnabled(true);
			jJournal.setText("" + eveKitJournalHistory);
		}
		Integer eveKitMarketOrdersHistory = Settings.get().getEveKitMarketOrdersHistory();
		if (eveKitMarketOrdersHistory == 0) {
			jMarketOrdersAll.setSelected(true);
			jMarketOrders.setEnabled(false);
			jMarketOrders.setText("1");
		} else {
			jMarketOrdersMonths.setSelected(true);
			jMarketOrders.setEnabled(true);
			jMarketOrders.setText("" + eveKitMarketOrdersHistory);
		}
		int eveKitIndustryJobsHistory = Settings.get().getEveKitIndustryJobsHistory();
		if (eveKitIndustryJobsHistory == 0) {
			jIndustryJobsAll.setSelected(true);
			jIndustryJobs.setEnabled(false);
			jIndustryJobs.setText("1");
		} else {
			jIndustryJobsMonths.setSelected(true);
			jIndustryJobs.setEnabled(true);
			jIndustryJobs.setText("" + eveKitIndustryJobsHistory);
		}
		int eveKitContractsHistory = Settings.get().getEveKitContractsHistory();
		if (eveKitContractsHistory == 0) {
			jContractsAll.setSelected(true);
			jContracts.setEnabled(false);
			jContracts.setText("1");
		} else {
			jContractsMonths.setSelected(true);
			jContracts.setEnabled(true);
			jContracts.setText("" + eveKitContractsHistory);
		}
	}
}
