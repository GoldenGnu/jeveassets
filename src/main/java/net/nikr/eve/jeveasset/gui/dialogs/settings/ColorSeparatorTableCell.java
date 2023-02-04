/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import ca.odell.glazedlists.SeparatorList;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ColorSettings.ColorRow;
import net.nikr.eve.jeveasset.gui.shared.table.SeparatorTableCell;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class ColorSeparatorTableCell extends SeparatorTableCell<ColorRow> {

	private final JLabel jGroup;

	public ColorSeparatorTableCell(final JTable jTable, final SeparatorList<ColorRow> separatorList) {
		super(jTable, separatorList);

		jGroup = new JLabel();

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jExpand)
				.addComponent(jGroup)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jGroup, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}

	@Override
	protected void configure(final SeparatorList.Separator<?> separator) {
		ColorRow colorRow = (ColorRow) separator.first();
		if (colorRow == null) { // handle 'late' rendering calls after this separator is invalid
			return;
		}
		jGroup.setText(colorRow.getColorEntry().getGroup().getName());
	}
}
