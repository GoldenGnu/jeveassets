/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.tabs.materials;

import ca.odell.glazedlists.SeparatorList;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.table.SeparatorTableCell;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class MaterialsSeparatorTableCell extends SeparatorTableCell<Material> {

	private final JLabel jLocation;
	private final JLabel jGroup;
	private final JButton jExpandLocation;
	
	private final Icon EXPANDED_ICON = Images.MISC_EXPANDED_WHITE.getIcon();
	private final Icon COLLAPSED_ICON = Images.MISC_COLLAPSED_WHITE.getIcon();

	public MaterialsSeparatorTableCell(JTable jTable, SeparatorList<Material> separatorList) {
		super(jTable, separatorList);
		
		jLocation = new JLabel();
		jLocation.setBorder(null);
		jLocation.setBackground(Color.BLACK);
		jLocation.setForeground(Color.WHITE);
		jLocation.setOpaque(true);
		Font font = jLocation.getFont();
		jLocation.setFont( new Font(font.getName(), Font.BOLD, font.getSize()+1));
		jLocation.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2){
					expandLocation();
				}
			}
		});

		jGroup = new JLabel();
		jGroup.setFont( new Font(font.getName(), Font.BOLD, font.getSize()+1));

		jExpandLocation = new JButton(COLLAPSED_ICON);
		jExpandLocation.setContentAreaFilled(false);
		jExpandLocation.setOpaque(true);
		jExpandLocation.setBorder(EMPTY_TWO_PIXEL_BORDER);
		jExpandLocation.setBackground(Color.BLACK);
		jExpandLocation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				expandLocation();
			}
		});

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jExpandLocation)
					.addComponent(jLocation, 0, 0, Integer.MAX_VALUE)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jExpand)
					.addGap(1)
					.addComponent(jGroup, 0, 0, Integer.MAX_VALUE)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jExpandLocation, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jLocation, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
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
		jExpandLocation.setVisible(material.isFirst());
		jLocation.setText(material.getLocation());
		jGroup.setText(material.getGroup());
		if (material.isFirst()){
			jExpandLocation.setIcon(isLocationCollapsed() ? EXPANDED_ICON : COLLAPSED_ICON);
		}
	}
	
	private void expandLocation(){
		Material material = (Material) separator.first();
		boolean expand = isLocationCollapsed();
		for (int i = 0; i < separatorList.size(); i++){
			Object object = separatorList.get(i);
			if (object instanceof SeparatorList.Separator<?>){
				SeparatorList.Separator<?> currentSeparator = (SeparatorList.Separator<?>) object;
				Material currentMaterial = (Material) currentSeparator.first();
				if (currentMaterial.getLocation().equals(material.getLocation())){
					separatorList.getReadWriteLock().writeLock().lock();
					try {
						currentSeparator.setLimit(expand ? Integer.MAX_VALUE : 0);
					} finally {
						separatorList.getReadWriteLock().writeLock().unlock();
					}
				}
			}
		}
	}
	
	private boolean isLocationCollapsed(){
		Material material = (Material) separator.first();
		for (int i = 0; i < separatorList.size(); i++){
			Object object = separatorList.get(i);
			if (object instanceof SeparatorList.Separator<?>){
				SeparatorList.Separator<?> currentSeparator = (SeparatorList.Separator<?>) object;
				Material currentMaterial = (Material) currentSeparator.first();
				if (currentMaterial.getLocation().equals(material.getLocation())){
					if (currentSeparator.getLimit() != 0){
						return false;
					}
				}
			}
		}
		return true;
	}
}