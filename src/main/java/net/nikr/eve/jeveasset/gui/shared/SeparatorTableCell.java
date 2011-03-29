/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.shared;

import ca.odell.glazedlists.SeparatorList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JViewport;
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

	private static final Border EMPTY_TWO_PIXEL_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);

	/** the separator list to lock */
	private final SeparatorList<E> separatorList;
	protected SeparatorList.Separator<?> separator;
	protected int row;

	protected final JPanel jPanel;
	protected final JButton jExpand;
	protected final GroupLayout layout;
	protected final JTable jTable;

	

	public SeparatorTableCell(JTable jTable, SeparatorList<E> separatorList) {
		this.jTable = jTable;
		this.separatorList = separatorList;

		jPanel = new JPanel(new BorderLayout());
		jPanel.setBackground(Color.LIGHT_GRAY);

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(false);
		layout.setAutoCreateContainerGaps(false);

		jExpand = new JButton(Images.MISC_EXPANDED.getIcon());
		jExpand.setOpaque(false);
		jExpand.setContentAreaFilled(false);
		jExpand.setBorder(EMPTY_TWO_PIXEL_BORDER);
		jExpand.setIcon(Images.MISC_EXPANDED.getIcon());
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
			jExpand.setIcon(separator.getLimit() == 0 ? Images.MISC_EXPANDED.getIcon() : Images.MISC_COLLAPSED.getIcon());
			configure(separator);
			jTable.setRowHeight(row, jPanel.getPreferredSize().height);
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
		//Workaround
		for (int a = 0; a < jTable.getRowCount(); a++){
			if (!(jTable.getValueAt(a, 0) instanceof SeparatorList.Separator<?>)
							&& jTable.getRowHeight() != jTable.getRowHeight(a)){
				jTable.setRowHeight(a, jTable.getRowHeight());
			}
		}
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