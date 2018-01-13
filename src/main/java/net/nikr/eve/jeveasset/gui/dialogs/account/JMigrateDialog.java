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
package net.nikr.eve.jeveasset.gui.dialogs.account;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAccountBalance;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.my.MyMarketOrder;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;

public class JMigrateDialog extends JDialogCentered {

	private final JButton jOK;
	private final JButton jCancel;
	private final JCheckBox jAll;
	private final JLabel jHelp;
	private final List<OwnerContainer> containers = new ArrayList<OwnerContainer>();
	private boolean returnValue = false;

	public JMigrateDialog(Program program, AccountManagerDialog accountManagerDialog) {
		super(program, DialoguesAccount.get().migrateTitle(), accountManagerDialog.getDialog(), Images.MISC_EVE.getImage());

		jOK = new JButton(DialoguesAccount.get().ok());
		jOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

		jCancel = new JButton(DialoguesAccount.get().cancel());
		jCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		jAll = new JCheckBox(DialoguesAccount.get().migrateAll());
		jAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (OwnerContainer container : containers) {
					if (container.getCheckBox().isEnabled()) {
						container.getCheckBox().setSelected(jAll.isSelected());
					}
				}
			}
		});
		jHelp = new JLabel(DialoguesAccount.get().migrateHelp());
	}

	private void validateAll() {
		boolean allSelected = true;
		boolean someEnabled = false;
		for (OwnerContainer container : containers) {
			if (container.getCheckBox().isEnabled()) {
				someEnabled = true;
				if (!container.getCheckBox().isSelected()) {
					allSelected = false;
					break;
				}
			}
		}
		jAll.setSelected(allSelected);
		jAll.setEnabled(someEnabled);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jAll;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {

	}

	@Override
	protected void save() {
		for (OwnerContainer container : containers) {
			returnValue = container.migrate() || returnValue;
		}
		setVisible(false);
	}

	public boolean show(List<EveApiOwner> owners) {
		returnValue = false;
		jPanel.removeAll();
		containers.clear();
		for (EveApiOwner owner : owners) {
			List<EsiOwner> esiOwners = new ArrayList<EsiOwner>();
			for (EsiOwner esiOwner : program.getProfileManager().getEsiOwners()) {
				if (esiOwner.getOwnerID() == owner.getOwnerID()) {
					esiOwners.add(esiOwner);
				}
			}
			containers.add(new OwnerContainer(owner, esiOwners));
		}
		validateAll();
		GroupLayout.ParallelGroup horizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
		GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();

		GroupLayout.SequentialGroup horizontalContainer = layout.createSequentialGroup();
		horizontalGroup.addGroup(horizontalContainer);
		GroupLayout.ParallelGroup horizontalCheckBox = layout.createParallelGroup();
		horizontalContainer.addGroup(horizontalCheckBox);
		GroupLayout.ParallelGroup horizontalComboBox = layout.createParallelGroup();
		GroupLayout.ParallelGroup horizontalButton = layout.createParallelGroup();
		GroupLayout.ParallelGroup horizontalLabel = layout.createParallelGroup();
		horizontalContainer.addGroup(layout.createParallelGroup()
				.addComponent(jHelp)
				.addGroup(layout.createSequentialGroup()
					.addGroup(horizontalComboBox)
					.addGroup(horizontalButton)
					.addGroup(horizontalLabel)
				)
		);
		horizontalCheckBox.addComponent(jAll);
		verticalGroup.addGroup(layout.createParallelGroup()
			.addComponent(jAll)
			.addComponent(jHelp)
		);
		for (OwnerContainer container : containers) {
			horizontalCheckBox.addComponent(container.getCheckBox());
			horizontalComboBox.addComponent(container.getComboBox());
			horizontalButton.addComponent(container.getButton());
			horizontalLabel.addComponent(container.getLabel());
			verticalGroup.addGroup(layout.createParallelGroup()
					.addComponent(container.getCheckBox(), Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(container.getComboBox(), Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(container.getButton(), Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(container.getLabel(), Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
			);
		}
		horizontalGroup.addGroup(layout.createSequentialGroup()
				.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
		);
		verticalGroup.addGroup(layout.createParallelGroup()
				.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
		layout.setHorizontalGroup(horizontalGroup);
		layout.setVerticalGroup(verticalGroup);
		setVisible(true);
		return returnValue;
	}

	private class OwnerContainer {

		private final EveApiOwner owner;
		private final JCheckBox jOwner;
		private final JLabel jInfo;
		private final JComboBox<EsiOwner> jEsiOwners;
		private final JButton jEsiInfo;

		public OwnerContainer(EveApiOwner owner, List<EsiOwner> esiOwners) {
			this.owner = owner;
			jOwner = new JCheckBox(owner.getOwnerName());
			jOwner.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					validateAll();
				}
			});
			jEsiInfo = new JButton(Images.DIALOG_ABOUT.getIcon());
			jEsiInfo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Object object = jEsiOwners.getSelectedItem();
					if (object instanceof EsiOwner) {
						JOptionPane.showMessageDialog(getDialog(), buildMsg((EsiOwner) object), DialoguesAccount.get().migrateTitle(), JOptionPane.PLAIN_MESSAGE);
					}
				}
			});
			if (!owner.canMigrate()) { //Already migrated
				jInfo = new JLabel(DialoguesAccount.get().migrateDone());

				jOwner.setSelected(false);
				jOwner.setEnabled(false);

				jEsiOwners = new JComboBox<EsiOwner>();
				jEsiOwners.setEnabled(false);

				jEsiInfo.setEnabled(false);
			} else if (esiOwners.isEmpty()) { //No Esi account
				jInfo = new JLabel(DialoguesAccount.get().migrateEsiEmpty(owner.getOwnerName()));

				jOwner.setSelected(false);
				jOwner.setEnabled(false);

				jEsiOwners = new JComboBox<EsiOwner>();
				jEsiOwners.setEnabled(false);

				jEsiInfo.setEnabled(false);
			} else if (esiOwners.size() > 1) { //Multible Esi Accounts
				jInfo = new JLabel(DialoguesAccount.get().migrateEsiSelect());

				jOwner.setSelected(true);
				jOwner.setEnabled(true);

				EsiOwner[] data = new EsiOwner[esiOwners.size()];
				jEsiOwners = new JComboBox<EsiOwner>(esiOwners.toArray(data));

				jEsiInfo.setEnabled(true);
			} else { //One Esi Account
				jInfo = new JLabel(DialoguesAccount.get().migrateOk());

				jOwner.setSelected(true);
				jOwner.setEnabled(true);

				EsiOwner[] data = new EsiOwner[esiOwners.size()];
				jEsiOwners = new JComboBox<EsiOwner>(esiOwners.toArray(data));
				jEsiOwners.setEnabled(false);

				jEsiInfo.setEnabled(true);
			}
			jEsiOwners.setRenderer(new EsiOwnerComboBoxRenderer());
		}

		public JCheckBox getCheckBox() {
			return jOwner;
		}

		public JLabel getLabel() {
			return jInfo;
		}

		public JComboBox<EsiOwner> getComboBox() {
			return jEsiOwners;
		}

		public JButton getButton() {
			return jEsiInfo;
		}

		private String buildMsg(EsiOwner esiOwner) {
			StringBuilder builder = new StringBuilder();
			builder.append("<html>Selected Account<br><br>");
			builder.append(getPre(esiOwner.isAccountBalance()));
			builder.append("Account Balance");
			builder.append(getPost());

			builder.append(getPre(esiOwner.isAssetList()));
			builder.append("Assets");
			builder.append(getPost());

			builder.append(getPre(esiOwner.isBlueprints()));
			builder.append("Blueprints");
			builder.append(getPost());

			builder.append(getPre(esiOwner.isContracts()));
			builder.append("Contracts");
			builder.append(getPost());

			builder.append(getPre(esiOwner.isIndustryJobs()));
			builder.append("Industry Jobs");
			builder.append(getPost());

			builder.append(getPre(esiOwner.isJournal()));
			builder.append("Journal");
			builder.append(getPost());

			builder.append(getPre(esiOwner.isLocations()));
			builder.append("Locations");
			builder.append(getPost());

			builder.append(getPre(esiOwner.isMarketOrders()));
			builder.append("Market Orders");
			builder.append(getPost());

			if (!esiOwner.isCorporation()) {
				builder.append(getPre(esiOwner.isStructures()));
				builder.append("Structures");
				builder.append(getPost());
			}

			builder.append(getPre(esiOwner.isTransactions()));
			builder.append("Transactions");
			builder.append(getPost());

			builder.append("</html>");
			return builder.toString();
		}

		private String getPre(boolean enabled) {
			if (enabled) {
				return "<font color=\"green\">＋";
			} else {
				return "<font color=\"red\">－";
			}
		}

		private String getPost() {
			return "</font><br>";
		}

		private boolean migrate() {
			if (!jOwner.isSelected()) { //Selected for migration
				return false;
			}
			Object object = jEsiOwners.getSelectedItem();
			if (!(object instanceof EsiOwner)) { //This should never happen, but, better safe than sorry
				return false;
			}
			EsiOwner esiOwner = (EsiOwner) object;
		//Migration data
			//Market Orders
			esiOwner.getMarketOrders().addAll(owner.getMarketOrders());
			//Journal
			esiOwner.getJournal().addAll(owner.getJournal());
			//Transactions
			esiOwner.getTransactions().addAll(owner.getTransactions());
		//Clear data
			owner.setAccountBalances(new ArrayList<MyAccountBalance>());
			owner.setAssets(new ArrayList<MyAsset>());
			owner.setBlueprints(new HashMap<Long, RawBlueprint>());
			owner.setContracts(new HashMap<MyContract, List<MyContractItem>>());
			owner.setIndustryJobs(new ArrayList<MyIndustryJob>());
			owner.setJournal(new HashSet<MyJournal>());
			owner.setMarketOrders(new HashSet<MyMarketOrder>());
			owner.setTransactions(new HashSet<MyTransaction>());
		//Set Migrated
			owner.setMigrated(true);
			return true;
		}
	}

	public static class EsiOwnerComboBoxRenderer implements ListCellRenderer<EsiOwner> {

		private final JPanel jPanel;
		private final JLabel jLabel;

		public EsiOwnerComboBoxRenderer() {
			jPanel = new JPanel();
			GroupLayout layout = new GroupLayout(jPanel);
			jPanel.setLayout(layout);
			jLabel = new JLabel();
			layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addComponent(jLabel)
			);
			layout.setVerticalGroup(
				layout.createParallelGroup()
					.addComponent(jLabel)

			);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends EsiOwner> list, EsiOwner value, int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				jPanel.setBackground(list.getSelectionBackground());
				jPanel.setForeground(list.getSelectionForeground());
			} else {
				jPanel.setBackground(list.getBackground());
				jPanel.setForeground(list.getForeground());
			}
			if (value != null) {
				int total = 0;
				int included = 0;
				total++;
				if (value.isAccountBalance()) {
					included++;
				}
				total++;
				if (value.isAssetList()) {
					included++;
				}
				total++;
				if (value.isBlueprints()) {
					included++;
				}
				total++;
				if (value.isContracts()) {
					included++;
				}
				total++;
				if (value.isIndustryJobs()) {
					included++;
				}
				total++;
				if (value.isJournal()) {
					included++;
				}
				total++;
				if (value.isLocations()) {
					included++;
				}
				total++;
				if (value.isMarketOrders()) {
					included++;
				}
				if (!value.isCorporation()) {
					total++;
					if (value.isStructures()) {
						included++;
					}
				}
				total++;
				if (value.isTransactions()) {
					included++;
				}
				jLabel.setText(DialoguesAccount.get().migrateEsiAccountName(value.getAccountName(), included, total));
			} else {
				jLabel.setText("");
			}
			return jPanel;
		}
	}
}
