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

	private static final Icon EXPANDED_ICON = Images.MISC_EXPANDED_WHITE.getIcon();
	private static final Icon COLLAPSED_ICON = Images.MISC_COLLAPSED_WHITE.getIcon();

	public MaterialsSeparatorTableCell(final JTable jTable, final SeparatorList<Material> separatorList) {
		super(jTable, separatorList);

		jLocation = new JLabel();
		jLocation.setBorder(null);
		jLocation.setBackground(Color.BLACK);
		jLocation.setForeground(Color.WHITE);
		jLocation.setOpaque(true);
		Font font = jLocation.getFont();
		jLocation.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 1));
		jLocation.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() >= 2) {
					expandHeader();
				}
			}
		});

		jGroup = new JLabel();
		jGroup.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 1));

		jExpandLocation = new JButton(COLLAPSED_ICON);
		jExpandLocation.setContentAreaFilled(false);
		jExpandLocation.setOpaque(true);
		jExpandLocation.setBorder(EMPTY_TWO_PIXEL_BORDER);
		jExpandLocation.setBackground(Color.BLACK);
		jExpandLocation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				expandHeader();
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
					.addComponent(jExpandLocation, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jLocation, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jGroup, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGap(2)
		);
	}

	@Override
	protected void configure(final SeparatorList.Separator<?> separator) {
		Material material = (Material) separator.first();
		if (material == null) {
			return;
		} // handle 'late' rendering calls after this separator is invalid
		jLocation.setVisible(material.isFirst());
		jExpandLocation.setVisible(material.isFirst());
		jLocation.setText(material.getHeader());
		jGroup.setText(material.getGroup());
		if (material.isFirst()) {
			jExpandLocation.setIcon(isHeaderCollapsed() ? EXPANDED_ICON : COLLAPSED_ICON);
		}
	}

	private void expandHeader() {
		Material material = (Material) currentSeparator.first();
		boolean expand = isHeaderCollapsed();
		try {
			separatorList.getReadWriteLock().readLock().lock();
			for (int i = 0; i < separatorList.size(); i++) {
				Object object = separatorList.get(i);
				if (object instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
					Material currentMaterial = (Material) separator.first();
					if (currentMaterial.getHeader().equals(material.getHeader())) {
						try {
							separatorList.getReadWriteLock().readLock().unlock();
							separatorList.getReadWriteLock().writeLock().lock();
							separator.setLimit(expand ? Integer.MAX_VALUE : 0);
						} finally {
							separatorList.getReadWriteLock().writeLock().unlock();
							separatorList.getReadWriteLock().readLock().lock();
						}
					}
				}
			}
		} finally {
			separatorList.getReadWriteLock().readLock().unlock();
		}
	}

	private boolean isHeaderCollapsed() {
		Material material = (Material) currentSeparator.first();
		try {
			separatorList.getReadWriteLock().readLock().lock();
			for (int i = 0; i < separatorList.size(); i++) {
				Object object = separatorList.get(i);
				if (object instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
					Material currentMaterial = (Material) separator.first();
					if (currentMaterial.getHeader().equals(material.getHeader())) {
						if (separator.getLimit() != 0) {
							return false;
						}
					}
				}
			}
		} finally {
			separatorList.getReadWriteLock().readLock().unlock();
		}
		return true;
	}
}
