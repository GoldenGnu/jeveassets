/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.tabs.materials;

import ca.odell.glazedlists.SeparatorList;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.SeparatorTableCell;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class MaterialsSeparatorTableCell extends SeparatorTableCell<Material> {

	private final JLabel jLocation;
	private final JLabel jGroup;

	public MaterialsSeparatorTableCell(JTable jTable, SeparatorList<Material> separatorList) {
		super(jTable, separatorList);
		
		jLocation = new JLabel();
		jLocation.setBorder(null);
		jLocation.setBackground(Color.BLACK);
		jLocation.setForeground(Color.WHITE);
		jLocation.setOpaque(true);
		Font font = jLocation.getFont();
		jLocation.setFont( new Font(font.getName(), Font.BOLD, font.getSize()+1));

		jGroup = new JLabel();
		jGroup.setBorder(null);
		jGroup.setOpaque(false);
		jGroup.setBackground(Color.BLACK);
		jGroup.setFont( new Font(font.getName(), Font.BOLD, font.getSize()+1));


		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGap(1)
					.addComponent(jLocation, 0, 0, Integer.MAX_VALUE)
					.addGap(1)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jExpand)
					.addGap(1)
					.addComponent(jGroup, 0, 0, Integer.MAX_VALUE)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGap(1)
				.addComponent(jLocation, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGap(1)
				.addGroup(layout.createParallelGroup()
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jGroup, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGap(2)
		);
	}

	@Override
	protected void configure(SeparatorList.Separator<?> separator) {
		Material material = (Material) separator.first();
		if(material == null) return; // handle 'late' rendering calls after this separator is invalid
		jLocation.setVisible(material.isFirst());
		jLocation.setText(material.getLocation());
		jGroup.setText(material.getGroup());
	}
}