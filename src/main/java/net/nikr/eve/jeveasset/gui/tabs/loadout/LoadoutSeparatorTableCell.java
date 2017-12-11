/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.tabs.loadout;

import ca.odell.glazedlists.SeparatorList;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.table.SeparatorTableCell;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class LoadoutSeparatorTableCell extends SeparatorTableCell<Loadout> {

	private final JLabel jOwner;
	private final JLabel jLocation;
	private final JLabel jFlag;

	public LoadoutSeparatorTableCell(final JTable jTable, final SeparatorList<Loadout> separatorList) {
		super(jTable, separatorList);

		jOwner = new JLabel();
		Font largeFont = new Font(jOwner.getFont().getName(), Font.BOLD, jOwner.getFont().getSize() + 1);
		jOwner.setBorder(null);
		jOwner.setBackground(Color.BLACK);
		jOwner.setForeground(Color.WHITE);
		jOwner.setOpaque(true);
		jOwner.setFont(largeFont);

		jLocation = new JLabel();
		jLocation.setBorder(null);
		jLocation.setBackground(Color.BLACK);
		jLocation.setForeground(Color.WHITE);
		jLocation.setOpaque(true);
		jLocation.setFont(largeFont);

		jFlag = new JLabel();
		jFlag.setBorder(null);
		jFlag.setOpaque(false);
		jFlag.setBackground(Color.BLACK);
		jFlag.setFont(largeFont);


		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGap(1)
					.addComponent(jOwner, 0, 0, Integer.MAX_VALUE)
					.addGap(1)
				)
				.addGroup(layout.createSequentialGroup()
					.addGap(1)
					.addComponent(jLocation, 0, 0, Integer.MAX_VALUE)
					.addGap(1)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jExpand)
					.addGap(1)
					.addComponent(jFlag, 0, 0, Integer.MAX_VALUE)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGap(1)
				.addComponent(jOwner, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jLocation, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGap(1)
				.addGroup(layout.createParallelGroup()
					.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFlag, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGap(2)
		);
	}

	@Override
	protected void configure(final SeparatorList.Separator<?> separator) {
		Loadout module = (Loadout) separator.first();
		if (module == null) { // handle 'late' rendering calls after this separator is invalid
			return;
		}
		jLocation.setVisible(module.isFirst());
		jLocation.setText(TabsLoadout.get().whitespace10(module.getLocation().getLocation()));
		jOwner.setVisible(module.isFirst());
		jOwner.setText(TabsLoadout.get().whitespace10(module.getOwnerName()));
		jFlag.setText(module.getFlag());
	}

}
