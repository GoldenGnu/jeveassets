/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.tabs.log;

import ca.odell.glazedlists.SeparatorList;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.Colors;
import net.nikr.eve.jeveasset.gui.shared.table.SeparatorTableCell;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class LogSeparatorTableCell extends SeparatorTableCell<AssetLogSource> {

	private final JLabel jType;
	private final JLabel jLocation;
	private final JLabel jColor;
	private final Color defaultColor;

	public LogSeparatorTableCell(JTable jTable, SeparatorList<AssetLogSource> separatorList) {
		super(jTable, separatorList);

		jType = new JLabel();

		jLocation = new JLabel();

		jColor = new JLabel();
		jColor.setOpaque(true);
		jColor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		defaultColor = jPanel.getBackground();

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jExpand)
				.addGap(5)
				.addComponent(jColor, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6)
				.addGap(5)
				.addComponent(jType, 200, 200, 200)
				.addGap(5)
				.addComponent(jLocation)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jLocation, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(layout.createSequentialGroup()
					.addGap(3)
					.addComponent(jColor, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6)
				)
		);
	}

	@Override
	protected void configure(SeparatorList.Separator<?> separator) {
		AssetLogSource source = (AssetLogSource) separator.first();
		if (source == null) {
			return;
		}
		AssetLog log = source.getParent();
		if (log.isAdded() && !log.isMoved() && !log.isRemoved()) { //Added
			jColor.setBackground(Colors.LIGHT_GREEN.getColor());
		} else if (!log.isAdded() && !log.isMoved() && log.isRemoved()) { //Removed
			jColor.setBackground(Colors.LIGHT_RED.getColor());
		} else if (!log.isAdded() && log.isMoved() && !log.isRemoved()) { //Moved
			jColor.setBackground(Colors.LIGHT_BLUE.getColor());
		} else if (!log.isAdded() && !log.isMoved() && !log.isRemoved()) { //None
			jColor.setBackground(defaultColor);
		} else { //Multiple changes
			jColor.setBackground(Colors.LIGHT_YELLOW.getColor());
		}
		jType.setText(log.getItem().getTypeName());
		jLocation.setText(log.getLocation());
	}

}
