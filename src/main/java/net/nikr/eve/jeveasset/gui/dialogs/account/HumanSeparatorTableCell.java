/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.dialogs.account;

import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SeparatorList.Separator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.shared.SeparatorTableCell;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class HumanSeparatorTableCell extends SeparatorTableCell<Human>
		implements FocusListener, ActionListener{

	// TODO action enum - more string enum pattern, to be converted to an enum
	private final static String ACTION_ACCOUNT_NAME = "ACTION_ACCOUNT_NAME";
	public final static String ACTION_EDIT = "ACTION_EDIT";
	public final static String ACTION_DELETE = "ACTION_DELETE";

	/** the separator list to lock */
	private final JTextField jAccountName;
	private final JButton jEdit;
	private final JButton jDelete;

	public HumanSeparatorTableCell(ActionListener actionListener, JTable jTable, SeparatorList<Human> separatorList) {
		super(jTable, separatorList);
		
		jAccountName = new JTextField();
		jAccountName.addFocusListener(this);
		jAccountName.setBorder(null);
		jAccountName.setOpaque(false);
		jAccountName.setActionCommand(ACTION_ACCOUNT_NAME);
		jAccountName.addActionListener(this);

		jEdit = new JButton(DialoguesAccount.get().edit());
		jEdit.setOpaque(false);
		jEdit.setActionCommand(ACTION_EDIT);
		jEdit.addActionListener(actionListener);

		jDelete = new JButton(DialoguesAccount.get().delete());
		jDelete.setOpaque(false);
		jDelete.setActionCommand(ACTION_DELETE);
		jDelete.addActionListener(actionListener);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jExpand)
				.addGap(1)
				.addComponent(jAccountName)
				.addGap(1)
				.addComponent(jEdit)
				.addComponent(jDelete)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGap(1)
				.addGroup(layout.createParallelGroup()
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAccountName, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jEdit, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDelete, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGap(2)
		);
	}

	@Override
	protected void configure(Separator<?> separator) {
		Human human = (Human) separator.first();
		if(human == null) return; // handle 'late' rendering calls after this separator is invalid
		Account account = human.getParentAccount();
		if (account.getName().isEmpty()){
			jAccountName.setText(String.valueOf(account.getKeyID()));
		} else {
			jAccountName.setText(account.getName());
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ACCOUNT_NAME.equals(e.getActionCommand())){
			Human human = (Human) separator.first();
			Account account = human.getParentAccount();
			if (jAccountName.getText().isEmpty()){
				jAccountName.setText(String.valueOf(account.getKeyID()));
			}
			account.setName(jAccountName.getText());
			jAccountName.transferFocus();
			expandSeparator(true);
			int index = jTable.getSelectedRow()+1;
			if (jTable.getRowCount() >= index){
				jTable.setRowSelectionInterval(index, index);
			}
			
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() instanceof JTextField){
			jTable.setRowSelectionInterval(row, row);
			jAccountName.selectAll();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {

	}


}