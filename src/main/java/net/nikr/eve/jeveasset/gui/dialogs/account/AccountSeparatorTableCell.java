/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.dialogs.account;

import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SeparatorList.Separator;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.ApiType;
import net.nikr.eve.jeveasset.data.api.accounts.DeprecatedOwner;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.Colors;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.table.SeparatorTableCell;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class AccountSeparatorTableCell extends SeparatorTableCell<OwnerType> {

	public enum AccountCellAction {
		ACCOUNT_NAME,
		EDIT,
		DELETE,
		MIGRATE
	}

	private final JTextField jAccountName;
	private final JLabel jAccountType;
	private final JButton jEdit;
	private final JButton jDelete;
	private final JButton jMigrate;
	private final JLabel jInvalidLabel;
	private final JLabel jExpiredLabel;
	private final JLabel jMigratedLabel;
	private final JLabel jCanMigrateLabel;
	private final JLabel jSeparatorLabel;

	private final Color defaultColor;
	private final AccountManagerDialog accountManagerDialog;

	public AccountSeparatorTableCell(final AccountManagerDialog accountManagerDialog, final ActionListener actionListener, final JTable jTable, final SeparatorList<OwnerType> separatorList) {
		super(jTable, separatorList);
		this.accountManagerDialog = accountManagerDialog;

		defaultColor = jPanel.getBackground();

		ListenerClass listener = new ListenerClass();
		addCellEditorListener(listener);

		jSeparatorLabel = new JLabel();
		jSeparatorLabel.setBackground(jTable.getBackground());
		jSeparatorLabel.setOpaque(true);
		jSeparatorLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, jTable.getGridColor()));
		jAccountType = new JLabel();

		jEdit = new JButton(DialoguesAccount.get().edit());
		jEdit.setOpaque(false);
		jEdit.setActionCommand(AccountCellAction.EDIT.name());
		jEdit.addActionListener(actionListener);

		jMigrate = new JButton(DialoguesAccount.get().migrate());
		jMigrate.setOpaque(false);
		jMigrate.setActionCommand(AccountCellAction.MIGRATE.name());
		jMigrate.addActionListener(actionListener);

		jDelete = new JButton(DialoguesAccount.get().delete());
		jDelete.setOpaque(false);
		jDelete.setActionCommand(AccountCellAction.DELETE.name());
		jDelete.addActionListener(actionListener);

		jAccountName = new JTextField();
		jAccountName.setBorder(null);
		jAccountName.setOpaque(false);
		jAccountName.setBackground(Colors.COMPONENT_TRANSPARENT.getColor());
		jAccountName.setActionCommand(AccountCellAction.ACCOUNT_NAME.name());
		jAccountName.addActionListener(listener);
		jAccountName.addKeyListener(listener);
		jAccountName.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				jAccountName.selectAll();
			}
			@Override
			public void focusLost(FocusEvent e) { }
		});

		jInvalidLabel = new JLabel(DialoguesAccount.get().accountInvalid());

		jExpiredLabel = new JLabel(DialoguesAccount.get().accountExpired());

		jMigratedLabel = new JLabel(DialoguesAccount.get().accountMigrated());

		jCanMigrateLabel = new JLabel(DialoguesAccount.get().accountCanMigrate());

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(jSeparatorLabel, 0, 0, Integer.MAX_VALUE)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jExpand)
					.addGap(1)
					.addComponent(jEdit, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jMigrate, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jDelete, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addGap(5)
					.addComponent(jAccountType)
					.addGap(5)
					.addComponent(jAccountName, 120, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(jExpiredLabel)
					.addComponent(jInvalidLabel)
					.addComponent(jMigratedLabel)
					.addComponent(jCanMigrateLabel)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jSeparatorLabel, jTable.getRowHeight(), jTable.getRowHeight(), jTable.getRowHeight())
				.addGap(1)
				.addGroup(layout.createParallelGroup()
					.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAccountType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jEdit, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMigrate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jDelete, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAccountName, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jInvalidLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jExpiredLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMigratedLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCanMigrateLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGap(2)
		);
	}

	@Override
	protected void configure(final Separator<?> separator) {
		OwnerType owner = (OwnerType) separator.first();
		if (owner == null) { // handle 'late' rendering calls after this separator is invalid
			return;
		}
		jSeparatorLabel.setVisible(currentRow != 0);
		boolean allMigrated = false;
		boolean canMigrate = false;
		if (owner.getAccountAPI() == ApiType.EVE_ONLINE || owner.getAccountAPI() == ApiType.EVEKIT) {
			try {
				separatorList.getReadWriteLock().readLock().lock();
				for (Object object : separator.getGroup()) {
					if (object instanceof DeprecatedOwner) {
						DeprecatedOwner deprecatedOwner = (DeprecatedOwner) object;
						if (deprecatedOwner.canMigrate()) {
							canMigrate = true;
							break;
						}
					}
				}
			} finally {
				separatorList.getReadWriteLock().readLock().unlock();
			}
			allMigrated = !canMigrate;
			jMigrate.setVisible(true);
			jMigrate.setEnabled(canMigrate);
			jEdit.setVisible(false);
		} else {
			jMigrate.setVisible(false);
			jEdit.setVisible(true);
		}
		switch (owner.getAccountAPI()) {
			case EVE_ONLINE:
				jAccountType.setIcon(Images.MISC_EVE.getIcon());
				break;
			case EVEKIT:
				jAccountType.setIcon(Images.MISC_EVEKIT.getIcon());
				break;
			case ESI:
				jAccountType.setIcon(Images.MISC_ESI.getIcon());
				break;
		}
		jAccountName.setText(owner.getAccountName());
		//Expired
		jExpiredLabel.setVisible(owner.isExpired());

		//Invalid
		jInvalidLabel.setVisible(owner.isInvalid());

		//All Migrated
		jMigratedLabel.setVisible(allMigrated);

		//Can Migrate
		jCanMigrateLabel.setVisible(canMigrate);

		//Invalid / Expired
		if (owner.isInvalid() || owner.isExpired()) {
			ColorSettings.config(jPanel, ColorEntry.GLOBAL_ENTRY_INVALID);
		} else if (allMigrated) {
			ColorSettings.config(jPanel, ColorEntry.GLOBAL_ENTRY_VALID);
		} else if (canMigrate) {
			ColorSettings.config(jPanel, ColorEntry.GLOBAL_ENTRY_WARNING);
		} else {
			jPanel.setBackground(defaultColor);
		}
	}

	private void setAccountName(OwnerType owner) {
		if (owner == null) {
			return;
		}
		if (jAccountName.getText().isEmpty()) {
			owner.setResetAccountName();
		} else {
			owner.setAccountName(jAccountName.getText());
		}
		accountManagerDialog.forceUpdate();
	}

	private class ListenerClass implements ActionListener, CellEditorListener, KeyListener {

		private boolean update = true;

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (AccountCellAction.ACCOUNT_NAME.name().equals(e.getActionCommand())) {
				stopCellEditing();
				expandSeparator(true);
				int index = jTable.getSelectedRow() + 1;
				if (jTable.getRowCount() >= index) {
					jTable.setRowSelectionInterval(index, index);
				}
			}
		}

		@Override
		public void editingStopped(ChangeEvent e) {
			saveAccountName();
		}

		@Override
		public void editingCanceled(ChangeEvent e) {
			saveAccountName();
		}

		@Override
		public void keyTyped(KeyEvent e) { }

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				update = false;
				stopCellEditing();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) { }

		private void saveAccountName() {
			if (!update) {
				update = true;
				return;
			}
			OwnerType owner = (OwnerType) currentSeparator.first();
			if (owner == null) { // handle 'late' rendering calls after this separator is invalid
				return;
			}
			setAccountName(owner);
		}

	}
}
