/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.dialogs.account;

import ca.odell.glazedlists.SeparatorList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class SeparatorTableCell extends AbstractCellEditor 
		implements TableCellRenderer, TableCellEditor, FocusListener, ActionListener{

	private final static String ACTION_EXPAND = "ACTION_EXPAND";
	private final static String ACTION_ACCOUNT_NAME = "ACTION_ACCOUNT_NAME";
	public final static String ACTION_EDIT = "ACTION_EDIT";
	public final static String ACTION_DELETE = "ACTION_DELETE";

	private static final Icon EXPANDED_ICON =  ImageGetter.getIcon("expanded.png"); //Icons.triangle(9, SwingConstants.EAST, Color.WHITE);
	private static final Icon COLLAPSED_ICON = ImageGetter.getIcon("collapsed.png");//  Icons.triangle(9, SwingConstants.SOUTH, Color.WHITE);
	private static final Border EMPTY_TWO_PIXEL_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);

	/** the separator list to lock */
	private final SeparatorList<Human> separatorList;
	private int row;

	private final JPanel jPanel;
	private final JTextField jAccountName;
	private final JButton jExpand;
	private final JButton jEdit;
	private final JButton jDelete;
	private final GroupLayout layout;
	private final JTable jTable;

	private SeparatorList.Separator<?> separator;

	public SeparatorTableCell(ActionListener actionListener, JTable jTable, SeparatorList<Human> separatorList) {
		this.jTable = jTable;
		this.separatorList = separatorList;

		jPanel = new JPanel(new BorderLayout());
		jPanel.setBackground(Color.LIGHT_GRAY);

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(false);
		layout.setAutoCreateContainerGaps(false);

		jExpand = new JButton(EXPANDED_ICON);
		jExpand.setOpaque(false);
		jExpand.setContentAreaFilled(false);
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.setBorder(EMPTY_TWO_PIXEL_BORDER);
		jExpand.setIcon(EXPANDED_ICON);
		jExpand.addActionListener(this);


		jAccountName = new JTextField();
		jAccountName.addFocusListener(this);
		jAccountName.setBorder(null);
		jAccountName.setOpaque(false);
		jAccountName.setActionCommand(ACTION_ACCOUNT_NAME);
		jAccountName.addActionListener(this);

		jEdit = new JButton("Edit");
		jEdit.setOpaque(false);
		jEdit.setActionCommand(ACTION_EDIT);
		jEdit.addActionListener(actionListener);

		jDelete = new JButton("Delete");
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
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		configure(value, row);
		return jPanel;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		configure(value, row);
		return jPanel;
	}

	@Override
	public Object getCellEditorValue() {
		return this.separator;
	}

	private void configure(Object value, int row) {
		this.row = row;
		if (value instanceof SeparatorList.Separator<?>){
			this.separator = (SeparatorList.Separator<?>)value;
			Human human = (Human) separator.first();
			if(human == null) return; // handle 'late' rendering calls after this separator is invalid
			Account account = human.getParentAccount();
			jTable.setRowHeight(row, jPanel.getPreferredSize().height);
			jExpand.setIcon(separator.getLimit() == 0 ? EXPANDED_ICON : COLLAPSED_ICON);
			if (account.getName().isEmpty()){
				jAccountName.setText(String.valueOf(account.getUserID()));
			} else {
				jAccountName.setText(account.getName());
			}
		}
	}

	private void expandSeparator(boolean expand){
		separatorList.getReadWriteLock().writeLock().lock();
		try {
			separator.setLimit(expand ? Integer.MAX_VALUE : 0);
		} finally {
			separatorList.getReadWriteLock().writeLock().unlock();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_EXPAND.equals(e.getActionCommand())){
			expandSeparator(separator.getLimit() == 0);
		}
		if (ACTION_ACCOUNT_NAME.equals(e.getActionCommand())){
			Human human = (Human) separator.first();
			Account account = human.getParentAccount();
			if (jAccountName.getText().isEmpty()){
				jAccountName.setText(String.valueOf(account.getUserID()));
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