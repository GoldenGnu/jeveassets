/* Glazed Lists													(c) 2003-2006 */
/* http://publicobject.com/glazedlists/						 publicobject.com,*/
/*													   O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.dialogs.account;

import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SeparatorList.Separator;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.OwnerType;
import net.nikr.eve.jeveasset.gui.shared.Colors;
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
		DELETE
	}

	private final JTextField jAccountName;
	private final JLabel jAccountType;
	private final JButton jEdit;
	private final JButton jDelete;
	private final JLabel jInvalidLabel;
	private final JLabel jExpiredLabel;
	private final JLabel jSpaceLabel;

	private final Color defaultColor;

	public AccountSeparatorTableCell(final ActionListener actionListener, final JTable jTable, final SeparatorList<OwnerType> separatorList) {
		super(jTable, separatorList);

		defaultColor = jPanel.getBackground();

		ListenerClass listener = new ListenerClass();

		jAccountType = new JLabel();

		jEdit = new JButton(DialoguesAccount.get().edit());
		jEdit.setOpaque(false);
		jEdit.setActionCommand(AccountCellAction.EDIT.name());
		jEdit.addActionListener(actionListener);

		jDelete = new JButton(DialoguesAccount.get().delete());
		jDelete.setOpaque(false);
		jDelete.setActionCommand(AccountCellAction.DELETE.name());
		jDelete.addActionListener(actionListener);

		jAccountName = new JTextField();
		jAccountName.addFocusListener(listener);
		jAccountName.setBorder(null);
		jAccountName.setOpaque(false);
		jAccountName.setActionCommand(AccountCellAction.ACCOUNT_NAME.name());
		jAccountName.addActionListener(listener);

		jInvalidLabel = new JLabel(DialoguesAccount.get().accountInvalid());

		jExpiredLabel = new JLabel(DialoguesAccount.get().accountExpired());

		jSpaceLabel = new JLabel();

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jExpand)
				.addGap(1)
				.addComponent(jEdit, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				.addComponent(jDelete, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				.addGap(5)
				.addComponent(jAccountType)
				.addGap(5)
				.addComponent(jAccountName, 20, 20, Integer.MAX_VALUE)
				.addGap(10)
				.addComponent(jExpiredLabel)
				.addComponent(jInvalidLabel)
				.addComponent(jSpaceLabel, 20, 20, Integer.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGap(1)
				.addGroup(layout.createParallelGroup()
					.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAccountType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jEdit, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jDelete, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAccountName, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jInvalidLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jExpiredLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSpaceLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
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
		switch (owner.getAccountAPI()) {
			case EVE_ONLINE:
				jAccountType.setIcon(Images.MISC_EVE.getIcon());
				break;
			case EVEKIT:
				jAccountType.setIcon(Images.MISC_EVEKIT.getIcon());
				break;
		}
		jAccountName.setText(owner.getAccountName());
		//Expired
		jExpiredLabel.setVisible(owner.isExpired());

		//Invalid
		jInvalidLabel.setVisible(owner.isInvalid());

		//Invalid / Expired
		jSpaceLabel.setVisible(owner.isInvalid() || owner.isExpired());
		jPanel.setBackground(owner.isInvalid() || owner.isExpired() ? Colors.LIGHT_RED.getColor() : defaultColor);
	}

	private class ListenerClass implements FocusListener, ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (AccountCellAction.ACCOUNT_NAME.name().equals(e.getActionCommand())) {
				OwnerType owner = (OwnerType) currentSeparator.first();
				if (jAccountName.getText().isEmpty()) {
					owner.setResetAccountName();
				} else {
					owner.setAccountName(jAccountName.getText());
				}
				jAccountName.transferFocus();
				expandSeparator(true);
				int index = jTable.getSelectedRow() + 1;
				if (jTable.getRowCount() >= index) {
					jTable.setRowSelectionInterval(index, index);
				}
			}
		}

		@Override
		public void focusGained(final FocusEvent e) {
			if (e.getSource() instanceof JTextField) {
				jTable.setRowSelectionInterval(currentRow, currentRow);
				jAccountName.selectAll();
			}
		}

		@Override
		public void focusLost(final FocusEvent e) { }
	}

}
