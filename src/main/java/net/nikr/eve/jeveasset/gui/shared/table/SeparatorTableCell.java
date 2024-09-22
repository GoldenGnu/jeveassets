/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.shared.table;

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
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.ColorUtil;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 * @param <E>
 */
public abstract class SeparatorTableCell<E> extends AbstractCellEditor
		implements TableCellRenderer, TableCellEditor {

	protected static final Border EMPTY_TWO_PIXEL_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);

	/** the separator list to lock. */
	protected final SeparatorList<E> separatorList;
	protected SeparatorList.Separator<?> currentSeparator;
	protected int currentRow;

	private final JSeparatorPanel jEditor;
	protected final JSeparatorPanel jPanel;
	protected final JButton jExpand;
	protected final GroupLayout layout;
	protected final JTable jTable;

	public SeparatorTableCell(final JTable jTable, final SeparatorList<E> separatorList) {
		this.jTable = jTable;
		this.separatorList = separatorList;

		jPanel = new JSeparatorPanel(new BorderLayout());
		if (ColorUtil.isBrightColor(jPanel.getBackground())) { //Light background color
			jPanel.setBackground(Color.LIGHT_GRAY);
		} else { //Dark background color
			jPanel.setBackground(Color.DARK_GRAY);
		}

		jPanel.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() >= 2) {
					expandSeparator(currentSeparator.getLimit() == 0);
				}
			}
		});

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(false);
		layout.setAutoCreateContainerGaps(false);

		/*
		XXX - Workaround for square button borders in table with FlatLAF
		https://stackoverflow.com/questions/67657036/jbuttons-in-jtable-cell-change-shape-on-row-selection
		https://github.com/JFormDesigner/FlatLaf/blob/main/flatlaf-core/src/main/java/com/formdev/flatlaf/ui/FlatUIUtils.java#L215
		*/
		if (Settings.get().getColorSettings().isFlatLAF()) {
			JSeparatorPanel jLast = jPanel;
			for (int i = 0; i < 3; i++) {
				JSeparatorPanel jSeparatorPanel = new JSeparatorPanel(new BorderLayout());
				jSeparatorPanel.add(jLast);
				jLast = jSeparatorPanel;
			}
			jEditor = jLast;
		} else {
			jEditor = jPanel;
		}

		jExpand = new JButton(Images.MISC_EXPANDED.getIcon());
		jExpand.setOpaque(false);
		jExpand.setContentAreaFilled(false);
		jExpand.setBorder(EMPTY_TWO_PIXEL_BORDER);
		jExpand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				expandSeparator(currentSeparator.getLimit() == 0);
			}
		});
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
		configure(value, row);
		return jEditor;
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
		configure(value, row);
		return jEditor;
	}

	@Override
	public Object getCellEditorValue() {
		return this.currentSeparator;
	}

	private void configure(final Object value, final int row) {
		if (value instanceof SeparatorList.Separator<?>) {
			this.currentRow = row;
			this.currentSeparator = (SeparatorList.Separator<?>) value;
			jExpand.setIcon(currentSeparator.getLimit() == 0 ? Images.MISC_EXPANDED.getIcon() : Images.MISC_COLLAPSED.getIcon());
			configure(currentSeparator);
		}
	}

	protected abstract void configure(final SeparatorList.Separator<?> separator);

	protected void expandSeparator(final boolean expand) {
		try {
			separatorList.getReadWriteLock().writeLock().lock();
			currentSeparator.setLimit(expand ? Integer.MAX_VALUE : 0);
		} finally {
			separatorList.getReadWriteLock().writeLock().unlock();
		}
		if (expand) {
			scrollToRow(currentRow + currentSeparator.size());
		}
	}

	private void scrollToRow(final int row) {
		if (!(jTable.getParent() instanceof JViewport)) {
			return;
		}

		JViewport viewport = (JViewport) jTable.getParent();

		// This rectangle is relative to the table where the
		// northwest corner of cell (0,0) is always (0,0).
		Rectangle rect = jTable.getCellRect(row, 0, true);

		// The location of the viewport relative to the table
		Point pt = viewport.getViewPosition();

		// Translate the cell location so that it is relative
		// to the view, assuming the northwest corner of the
		// view is (0,0)
		rect.setLocation(rect.x - pt.x, rect.y - pt.y);

		// Scroll the area into view
		viewport.scrollRectToVisible(rect);

		//Ensure stockpile cell is visible
		rect = jTable.getCellRect(currentRow, 0, true);
		pt = viewport.getViewPosition();
		rect.setLocation(rect.x - pt.x, rect.y - pt.y);
		viewport.scrollRectToVisible(rect);
	}

	public class JSeparatorPanel extends JPanel {

		public JSeparatorPanel(LayoutManager layout, boolean isDoubleBuffered) {
			super(layout, isDoubleBuffered);
		}

		public JSeparatorPanel(LayoutManager layout) {
			super(layout);
		}

		public JSeparatorPanel(boolean isDoubleBuffered) {
			super(isDoubleBuffered);
		}

		public JSeparatorPanel() {
			super();
		}
	}
}
