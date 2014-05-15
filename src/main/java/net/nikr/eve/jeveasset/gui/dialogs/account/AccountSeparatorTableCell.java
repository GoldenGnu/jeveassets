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
import net.nikr.eve.jeveasset.data.MyAccount;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.gui.shared.table.SeparatorTableCell;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class AccountSeparatorTableCell extends SeparatorTableCell<Owner> {

	public enum AccountCellAction {
		ACCOUNT_NAME,
		EDIT,
		DELETE
	}

	private final JTextField jAccountName;
	private final JButton jEdit;
	private final JButton jDelete;
	private final JLabel jInvalidLabel;
	private final JLabel jExpiredLabel;
	private final JLabel jSpaceLabel;

	private final Color defaultColor;
	private final Color errorColor = new Color(255, 200, 200);

	public AccountSeparatorTableCell(final ActionListener actionListener, final JTable jTable, final SeparatorList<Owner> separatorList) {
		super(jTable, separatorList);

		defaultColor = jPanel.getBackground();

		ListenerClass listener = new ListenerClass();
		jAccountName = new JTextField();
		jAccountName.addFocusListener(listener);
		jAccountName.setBorder(null);
		jAccountName.setOpaque(false);
		jAccountName.setActionCommand(AccountCellAction.ACCOUNT_NAME.name());
		jAccountName.addActionListener(listener);

		jEdit = new JButton(DialoguesAccount.get().edit());
		jEdit.setOpaque(false);
		jEdit.setActionCommand(AccountCellAction.EDIT.name());
		jEdit.addActionListener(actionListener);

		jDelete = new JButton(DialoguesAccount.get().delete());
		jDelete.setOpaque(false);
		jDelete.setActionCommand(AccountCellAction.DELETE.name());
		jDelete.addActionListener(actionListener);

		jInvalidLabel = new JLabel(DialoguesAccount.get().accountInvalid());

		jExpiredLabel = new JLabel(DialoguesAccount.get().accountExpired());

		jSpaceLabel = new JLabel();

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jExpand)
				.addGap(1)
				.addComponent(jAccountName, 20, 20, Integer.MAX_VALUE)
				.addGap(10)
				.addComponent(jExpiredLabel)
				.addComponent(jInvalidLabel)
				.addComponent(jSpaceLabel, 20, 20, Integer.MAX_VALUE)
				.addComponent(jEdit, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				.addComponent(jDelete, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGap(1)
				.addGroup(layout.createParallelGroup()
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAccountName, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jInvalidLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jExpiredLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSpaceLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jEdit, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDelete, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGap(2)
		);
	}

	@Override
	protected void configure(final Separator<?> separator) {
		Owner owner = (Owner) separator.first();
		if (owner == null) { // handle 'late' rendering calls after this separator is invalid
			return;
		}
		MyAccount account = owner.getParentAccount();
		if (account.getName().isEmpty()) {
			jAccountName.setText(String.valueOf(account.getKeyID()));
		} else {
			jAccountName.setText(account.getName());
		}
		//Expired
		jExpiredLabel.setVisible(account.isExpired());

		//Invalid
		jInvalidLabel.setVisible(account.isInvalid());

		//Invalid / Expired
		jSpaceLabel.setVisible(account.isInvalid() || account.isExpired());
		jPanel.setBackground(account.isInvalid() || account.isExpired() ? errorColor : defaultColor);
	}

	private class ListenerClass implements FocusListener, ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (AccountCellAction.ACCOUNT_NAME.name().equals(e.getActionCommand())) {
				Owner owner = (Owner) currentSeparator.first();
				MyAccount account = owner.getParentAccount();
				if (jAccountName.getText().isEmpty()) {
					jAccountName.setText(String.valueOf(account.getKeyID()));
				}
				account.setName(jAccountName.getText());
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
