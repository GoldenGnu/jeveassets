/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.tabs.contracts;

import ca.odell.glazedlists.SeparatorList;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.gui.shared.table.SeparatorTableCell;
import net.nikr.eve.jeveasset.i18n.TabsContracts;


public class ContractsSeparatorTableCell extends SeparatorTableCell<MyContractItem> {

	private final JLabel jName;
	private final JLabel jType;

	public ContractsSeparatorTableCell(final JTable jTable, final SeparatorList<MyContractItem> separatorList, final ActionListener actionListener) {
		super(jTable, separatorList);

		jName = new JLabel();
		Font font = jName.getFont();
		jName.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 1));

		JLabel jTypeLabel = new JLabel(TabsContracts.get().type());
		jTypeLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		jType = new JLabel();

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jExpand)
					.addGap(10)
					.addComponent(jName, 220, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(jTypeLabel)
					.addGap(4)
					.addComponent(jType)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGap(2)
				.addGroup(layout.createParallelGroup()
					.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jName, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTypeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGap(2)
		);
	}

	@Override
	protected void configure(SeparatorList.Separator<?> separator) {
		MyContractItem item = (MyContractItem) separator.first();
		if (item == null) { // handle 'late' rendering calls after this separator is invalid
			return;
		}
		jName.setText(item.getContract().getTitle());
		jType.setText(item.getContract().getTypeName());
	}
}
