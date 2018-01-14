/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.tabs.log;

import ca.odell.glazedlists.SeparatorList;
import javax.swing.JLabel;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.table.SeparatorTableCell;
import net.nikr.eve.jeveasset.gui.tabs.materials.Material;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class LogSeparatorTableCell extends SeparatorTableCell<AssetLogSource> {

	private final JLabel jType;
	private final JLabel jLocation;
	
	public LogSeparatorTableCell(JTable jTable, SeparatorList<AssetLogSource> separatorList) {
		super(jTable, separatorList);

		jType = new JLabel();
		jLocation = new JLabel();
		
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jExpand)
				.addGap(5)
				.addComponent(jType)
				.addGap(5)
				.addComponent(jLocation)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jLocation, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}

	@Override
	protected void configure(SeparatorList.Separator<?> separator) {
		AssetLogSource source = (AssetLogSource) separator.first();
		if (source == null) {
			return;
		}
		AssetLog log = source.getParent();
		jType.setText(log.getItem().getTypeName());
		jLocation.setText(log.getLocation());
	}
	
}
