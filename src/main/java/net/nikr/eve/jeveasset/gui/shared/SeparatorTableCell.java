/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.shared;

import ca.odell.glazedlists.SeparatorList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.gui.images.Images;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public abstract class SeparatorTableCell<E>  extends AbstractCellEditor
		implements TableCellRenderer, TableCellEditor{

	protected static final Border EMPTY_TWO_PIXEL_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);

	/** the separator list to lock */
	protected final SeparatorList<E> separatorList;
	protected SeparatorList.Separator<?> separator;
	protected int row;

	protected final JPanel jPanel;
	protected final JButton jExpand;
	protected final GroupLayout layout;
	protected final JTable jTable;
	
	private final Icon EXPANDED_ICON = Images.MISC_EXPANDED.getIcon();
	private final Icon COLLAPSED_ICON = Images.MISC_COLLAPSED.getIcon();
	

	public SeparatorTableCell(JTable jTable, SeparatorList<E> separatorList) {
		this.jTable = jTable;
		this.separatorList = separatorList;

		jPanel = new JPanel(new BorderLayout());
		jPanel.setBackground(Color.LIGHT_GRAY);
		jPanel.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2){
					expandSeparator(separator.getLimit() == 0);
				}
			}
		});

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(false);
		layout.setAutoCreateContainerGaps(false);

		jExpand = new JButton(EXPANDED_ICON);
		jExpand.setOpaque(false);
		jExpand.setContentAreaFilled(false);
		jExpand.setBorder(EMPTY_TWO_PIXEL_BORDER);
		jExpand.setIcon(EXPANDED_ICON);
		jExpand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				expandSeparator(separator.getLimit() == 0);
			}
		});
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

	private void configure(Object value, int row){
		if (value instanceof SeparatorList.Separator<?>){
			this.row = row;
			this.separator = (SeparatorList.Separator<?>)value;
			jExpand.setIcon(separator.getLimit() == 0 ? EXPANDED_ICON : COLLAPSED_ICON);
			configure(separator);
		}
		
	}

	protected abstract void configure(SeparatorList.Separator<?> separator);

	protected void expandSeparator(boolean expand){
		separatorList.getReadWriteLock().writeLock().lock();
		try {
			separator.setLimit(expand ? Integer.MAX_VALUE : 0);
		} finally {
			separatorList.getReadWriteLock().writeLock().unlock();
		}
		if (expand) scrollToRow(row+separator.size());
	}

	private void scrollToRow(int row) {
		if (!(jTable.getParent() instanceof JViewport)) return;

		JViewport viewport = (JViewport)jTable.getParent();

		// This rectangle is relative to the table where the
		// northwest corner of cell (0,0) is always (0,0).
		Rectangle rect = jTable.getCellRect(row, 0, true);

		// The location of the viewport relative to the table
		Point pt = viewport.getViewPosition();

		// Translate the cell location so that it is relative
		// to the view, assuming the northwest corner of the
		// view is (0,0)
		rect.setLocation(rect.x-pt.x, rect.y-pt.y);

		// Scroll the area into view
		viewport.scrollRectToVisible(rect);
	}
}