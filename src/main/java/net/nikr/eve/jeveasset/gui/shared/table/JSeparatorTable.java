/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package net.nikr.eve.jeveasset.gui.shared.table;

import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.*;
import net.nikr.eve.jeveasset.Program;

/**
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class JSeparatorTable extends JAutoColumnTable {

	/** working with separator cells. */
	private TableCellRenderer separatorRenderer;
	private TableCellEditor separatorEditor;
	private final Map<Integer, Integer> rowsHeight = new HashMap<Integer, Integer>();
	private final SeparatorList<?> separatorList;
	private final Map<Integer, Boolean> expandedSate = new HashMap<Integer, Boolean>();
	private boolean defaultState = true;

	public JSeparatorTable(final Program program, final DefaultEventTableModel<?> tableModel, SeparatorList<?> separatorList) {
		super(program, tableModel);
		setUI(new SpanTableUI());
		this.separatorList = separatorList;

		// use a toString() renderer for the separator
		this.separatorRenderer = getDefaultRenderer(Object.class);
	}

	public void expandSeparators(final boolean expand) {
		clearExpandedState(); //Reset
		defaultState = expand;
		lock();
		final DefaultEventSelectionModel<?> selectModel = getEventSelectionModel();
		if (selectModel != null) {
			selectModel.setEnabled(false);
		}
		try {
			separatorList.getReadWriteLock().readLock().lock();
			for (int i = 0; i < separatorList.size(); i++) {
				Object object = separatorList.get(i);
				if (object instanceof SeparatorList.Separator) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
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
		} finally {
			separatorList.getReadWriteLock().readLock().unlock();
		}
		if (selectModel != null) {
			selectModel.setEnabled(true);
		}
		unlock();
	}

	public void clearExpandedState() {
		expandedSate.clear();
		defaultState = true;
	}

	public void saveExpandedState() {
		try {
			separatorList.getReadWriteLock().readLock().lock();
			for (int i = 0; i < separatorList.size(); i++) {
				Object object = separatorList.get(i);
				if (object instanceof SeparatorList.Separator) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator) object;
					for (Object item : separator.getGroup()) {
						expandedSate.put(item.hashCode(), separator.getLimit() != 0);
					}
				}
			}
		} finally {
			separatorList.getReadWriteLock().readLock().unlock();
		}
	}

	public void loadExpandedState() {
		try {
			separatorList.getReadWriteLock().readLock().lock();
			for (int i = 0; i < separatorList.size(); i++) {
				Object object = separatorList.get(i);
				if (object instanceof SeparatorList.Separator) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator) object;
					Boolean expanded = null;
					for (Object item : separator.getGroup()) {
						expanded = expandedSate.get(item.hashCode());
						if (expanded != null) {
							break;
						}
					}
					if (expanded == null) {
						expanded = defaultState;
					}
					try {
						separatorList.getReadWriteLock().readLock().unlock();
						separatorList.getReadWriteLock().writeLock().lock();
						separator.setLimit(expanded ? Integer.MAX_VALUE : 0);
					} finally {
						separatorList.getReadWriteLock().writeLock().unlock();
						separatorList.getReadWriteLock().readLock().lock();
					}
				}
			}
		} finally {
			separatorList.getReadWriteLock().readLock().unlock();
		}
	}

	private DefaultEventSelectionModel<?> getEventSelectionModel() {
		if (selectionModel instanceof DefaultEventSelectionModel<?>) {
			return (DefaultEventSelectionModel) selectionModel;
		} else {
			return null;
		}
	}

	/**
	 * A convenience method to cast the TableModel to the expected
	 * EventTableModel implementation.
	 *
	 * @return the EventTableModel that backs this table
	 */
	private DefaultEventTableModel<?> getEventTableModel() {
		return (DefaultEventTableModel) getModel();
	}


	/** {@inheritDoc} */
	@Override
	public Rectangle getCellRect(final int row, final int column, final boolean includeSpacing) {
		final DefaultEventTableModel<?> eventTableModel = getEventTableModel();

		// sometimes JTable asks for a cellrect that doesn't exist anymore, due
		// to an editor being installed before a bunch of rows were removed.
		// In this case, just return an empty rectangle, since it's going to
		// be discarded anyway
		if (row >= eventTableModel.getRowCount() || row < 0) {
			return new Rectangle();
		}

		// if it's the separator row, return the entire row as one big rectangle
		Object rowValue = eventTableModel.getElementAt(row);
		if (rowValue instanceof SeparatorList.Separator) {
			Rectangle firstColumn = super.getCellRect(row, 0, includeSpacing);
			Rectangle lastColumn = super.getCellRect(row, getColumnCount() - 1, includeSpacing);
			return firstColumn.union(lastColumn);

		// otherwise it's business as usual
		} else {
			return super.getCellRect(row, column, includeSpacing);
		}
	}

	public Rectangle getCellRectWithoutSpanning(final int row, final int column, final boolean includeSpacing) {
		return super.getCellRect(row, column, includeSpacing);
	}

	/** {@inheritDoc} */
	@Override
	public Object getValueAt(final int row, final int column) {
		final Object rowValue = getEventTableModel().getElementAt(row);

		// if it's the separator row, return the value directly
		if (rowValue instanceof SeparatorList.Separator) {
			return rowValue;
		}

		// otherwise it's business as usual
		return super.getValueAt(row, column);
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		final Object rowValue = getEventTableModel().getElementAt(row);

		// if it's the separator row, ignore the call
		if (rowValue instanceof SeparatorList.Separator) {
			return;
		}

		// otherwise it's business as usual
		super.setValueAt(aValue, row, column);
	}

	/** {@inheritDoc} */
	@Override
	public TableCellRenderer getCellRenderer(final int row, final int column) {
		// if it's the separator row, use the separator renderer
		if (getEventTableModel().getElementAt(row) instanceof SeparatorList.Separator) {
			return separatorRenderer;
		}
		// otherwise it's business as usual
		return super.getCellRenderer(row, column);
	}

	/** {@inheritDoc} */
	@Override
	public TableCellEditor getCellEditor(final int row, final int column) {
		// if it's the separator row, use the separator editor
		if (getEventTableModel().getElementAt(row) instanceof SeparatorList.Separator) {
			return separatorEditor;
		}
		// otherwise it's business as usual
		return super.getCellEditor(row, column);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCellEditable(final int row, final int column) {
		// if it's the separator row, it is always editable (so that the separator can be collapsed/expanded)
		if (getEventTableModel().getElementAt(row) instanceof SeparatorList.Separator) {
			return true;
		}
		// otherwise it's business as usual
		return super.isCellEditable(row, column);
	}

	/**
	 * Get the renderer for separator rows.
	 */
	public TableCellRenderer getSeparatorRenderer() { return separatorRenderer; }
	public void setSeparatorRenderer(final TableCellRenderer separatorRenderer) { this.separatorRenderer = separatorRenderer; }

	/**
	 * Get the editor for separator rows.
	 */
	public TableCellEditor getSeparatorEditor() { return separatorEditor; }
	public void setSeparatorEditor(final TableCellEditor separatorEditor) { this.separatorEditor = separatorEditor; }

	//XXX - Workaround for Autoscroller less then optimal behavior on SeparatorList.Separator
	private List<Integer> selectedRows = new ArrayList<Integer>();
	/** {@inheritDoc} */
	@Override
	public void valueChanged(final ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			for (int row = e.getFirstIndex(); row <= e.getLastIndex(); row++) {
				if (this.isRowSelected(row)) {
					if (!selectedRows.contains(row)) {
						selectedRows.add(row);
					}
				} else {
					if (selectedRows.contains(row)) {
						selectedRows.remove(selectedRows.indexOf(row));
					}
				}
			}
		}
		if (!selectedRows.isEmpty()
				&& selectedRows.get(selectedRows.size() - 1) < getEventTableModel().getRowCount()
				&& (getEventTableModel().getElementAt(selectedRows.get(selectedRows.size() - 1)) instanceof SeparatorList.Separator)) {
			setAutoscrolls(false);
		} else {
			setAutoscrolls(true);
		}
		super.valueChanged(e);
	}

	@Override
	public void unlock() {
		if (isLocked()) { //only if locked
			super.unlock(); //Unlock JAutoColumnTable
			autoResizeRows(); //Update after unlock
		}
	}

	private void autoResizeRows() {
		if (isLocked()) {
			return;
		}
		for (int row = 0; row < getEventTableModel().getRowCount(); row++) {
			autoResizeRow(row);
		}
	}

	private void autoResizeRow(final int row) {
		if (row < 0 || row > getEventTableModel().getRowCount()) {
			return;
		}
		int height = 0;
		final Object rowValue = getEventTableModel().getElementAt(row);
		final int key = rowValue.hashCode();
		if (rowsHeight.containsKey(key)) { //Load row height
			height = rowsHeight.get(key);
		} else if (rowValue instanceof SeparatorList.Separator) {
				//Calculate the Separator row height
				//This is done every time, because Separator can never be identified 100%
				//Because elements is changed by filters and sorting
				//If saved: the list keep growing with useless hash keys
				TableCellRenderer renderer = this.getCellRenderer(row, 0);
				Component component = super.prepareRenderer(renderer, row, 0);
				height = component.getPreferredSize().height;
			} else { //Calculate the row height
				for (int i = 0; i < this.getColumnCount(); i++) {
					TableCellRenderer renderer = this.getCellRenderer(row, i);
					Component component = super.prepareRenderer(renderer, row, i);
					height = Math.max(height, component.getPreferredSize().height);
				}
				//Save row height so we don't have to calculate it all the time
				rowsHeight.put(key, height);
		}

		//Set row height, if needed (is expensive because repaint is needed)
		if (this.getRowHeight(row) != height) {
			this.setRowHeight(row, height);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void tableChanged(final TableModelEvent e) {
		// stop edits when the table changes, or else we might
		// get a relocated edit in the wrong cell!
		if (isEditing()) {
			super.getCellEditor().cancelCellEditing();
		}

		// handle the change event
		super.tableChanged(e);

		//set row heigh
		autoResizeRows();
	}
}
/**
 * Modified from BasicTableUI to allow for spanning cells.
 */
class SpanTableUI extends BasicTableUI {

	private JSeparatorTable separatorTable;

	@Override
	public void installUI(final JComponent c) {
		this.separatorTable = (JSeparatorTable) c;
		super.installUI(c);
	}

	/** Paint a representation of the <code>table</code> instance
	 * that was set in installUI().
	 */
	@Override
	public void paint(final Graphics g, final JComponent c) {
		Rectangle clip = g.getClipBounds();

		Rectangle bounds = table.getBounds();
		// account for the fact that the graphics has already been translated
		// into the table's bounds
		bounds.x = bounds.y = 0;

		if (table.getRowCount() <= 0 || table.getColumnCount() <= 0
			// this check prevents us from painting the entire table
			// when the clip doesn't intersect our bounds at all
			|| !bounds.intersects(clip)) {

			return;
		}

		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width - 1, clip.y + clip.height - 1);
		int rMin = table.rowAtPoint(upperLeft);
		int rMax = table.rowAtPoint(lowerRight);
		// This should never happen (as long as our bounds intersect the clip,
		// which is why we bail above if that is the case).
		if (rMin == -1) {
			rMin = 0;
		}
		// If the table does not have enough rows to fill the view we'll get -1.
		// (We could also get -1 if our bounds don't intersect the clip,
		// which is why we bail above if that is the case).
		// Replace this with the index of the last row.
		if (rMax == -1) {
			rMax = table.getRowCount() - 1;
		}

		boolean ltr = table.getComponentOrientation().isLeftToRight();
		int cMin = table.columnAtPoint(ltr ? upperLeft : lowerRight);
		int cMax = table.columnAtPoint(ltr ? lowerRight : upperLeft);
		// This should never happen.
		if (cMin == -1) {
			cMin = 0;
		}
		// If the table does not have enough columns to fill the view we'll get -1.
		// Replace this with the index of the last column.
		if (cMax == -1) {
			cMax = table.getColumnCount() - 1;
		}

		// Paint the grid.
		paintGrid(g, rMin, rMax, cMin, cMax);

		// Paint the cells.
		paintCells(g, rMin, rMax, cMin, cMax);
	}
	private void paintCell(final Graphics g, final Rectangle cellRect, final int row, final int column) {
		if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) {
			Component component = table.getEditorComponent();
			component.setBounds(cellRect);
			component.validate();
		} else {
			TableCellRenderer renderer = table.getCellRenderer(row, column);
			Component component = table.prepareRenderer(renderer, row, column);
			rendererPane.paintComponent(g, component, table, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
		}
	}
	private void paintCells(final Graphics g, final int rMin, final int rMax, final int cMin, final int cMax) {
		JTableHeader header = table.getTableHeader();
		TableColumn draggedColumn = (header == null) ? null : header.getDraggedColumn();

		TableColumnModel cm = table.getColumnModel();
		int columnMargin = cm.getColumnMargin();

		Rectangle cellRect;
		TableColumn aColumn;
		int columnWidth;
		if (table.getComponentOrientation().isLeftToRight()) {
			for (int row = rMin; row <= rMax; row++) {
				for (int column = 0; column <= cMax; column++) {
					aColumn = cm.getColumn(column);
					cellRect = table.getCellRect(row, column, false);
					if (aColumn != draggedColumn) {
					   paintCell(g, cellRect, row, column);
					}
				}
			}
		} else {
			for (int row = rMin; row <= rMax; row++) {
				cellRect = table.getCellRect(row, cMin, false);
				aColumn = cm.getColumn(cMin);
				if (aColumn != draggedColumn) {
					columnWidth = aColumn.getWidth();
					cellRect.width = columnWidth - columnMargin;
					paintCell(g, cellRect, row, cMin);
				}
				for (int column = cMin + 1; column <= cMax; column++) {
					aColumn = cm.getColumn(column);
					columnWidth = aColumn.getWidth();
					cellRect.width = columnWidth - columnMargin;
					cellRect.x -= columnWidth;
					if (aColumn != draggedColumn) {
						paintCell(g, cellRect, row, column);
					}
				}
			}
		}

		// Paint the dragged column if we are dragging.
		if (draggedColumn != null) {
			paintDraggedArea(g, rMin, rMax, draggedColumn, header.getDraggedDistance());
		}

		// Remove any renderers that may be left in the rendererPane.
		rendererPane.removeAll();
	}

	/*
	 * Paints the grid lines within <I>aRect</I>, using the grid
	 * color set with <I>setGridColor</I>. Paints vertical lines
	 * if <code>getShowVerticalLines()</code> returns true and paints
	 * horizontal lines if <code>getShowHorizontalLines()</code>
	 * returns true.
	 */
	private void paintGrid(final Graphics g, final int rMin, final int rMax, final int cMin, final int cMax) {
		g.setColor(table.getGridColor());

		Rectangle minCell = table.getCellRect(rMin, cMin, true);
		Rectangle maxCell = table.getCellRect(rMax, cMax, true);
		Rectangle damagedArea = minCell.union(maxCell);

		if (table.getShowHorizontalLines()) {
			int tableWidth = damagedArea.x + damagedArea.width;
			int y = damagedArea.y;
			for (int row = rMin; row <= rMax; row++) {
				y += table.getRowHeight(row);
				g.drawLine(damagedArea.x, y - 1, tableWidth - 1, y - 1);
			}
		}
		if (table.getShowVerticalLines()) {
			TableColumnModel cm = table.getColumnModel();
			int tableHeight = damagedArea.y + damagedArea.height;
			int x;
			if (table.getComponentOrientation().isLeftToRight()) {
				x = 0; //damagedArea.x;
				for (int column = 0; column <= cMax; column++) {
					x += cm.getColumn(column).getWidth();
					// redraw the grid lines for this column if it is damaged
					if (column >= cMin) {
						g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
					}
				}
			} else {
				x = damagedArea.x + damagedArea.width;
				for (int column = cMin; column < cMax; column++) {
					x -= cm.getColumn(column).getWidth();
					g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
				}
				x -= cm.getColumn(cMax).getWidth();
				g.drawLine(x, 0, x, tableHeight - 1);
			}
		}
	}

	private int viewIndexForColumn(final TableColumn aColumn) {
		TableColumnModel cm = table.getColumnModel();
		for (int column = 0; column < cm.getColumnCount(); column++) {
			if (cm.getColumn(column) == aColumn) {
				return column;
			}
		}
		return -1;
	}

	private void paintDraggedArea(final Graphics g, final int rMin, final int rMax, final TableColumn draggedColumn, final int distance) {
		int draggedColumnIndex = viewIndexForColumn(draggedColumn);

		for (int row = rMin; row <= rMax; row++) {
			// skip separator rows
			Object rowValue = ((DefaultEventTableModel) separatorTable.getModel()).getElementAt(row);

			// only paint the cell on non-separator rows
			if (!(rowValue instanceof SeparatorList.Separator)) {

				Rectangle cellRect = table.getCellRect(row, draggedColumnIndex, false);

				// Paint a gray well in place of the moving column.
				g.setColor(table.getParent().getBackground());
				g.fillRect(cellRect.x, cellRect.y, cellRect.width, cellRect.height);

				// Move to the where the cell has been dragged.
				cellRect.x += distance;

				// Fill the background.
				g.setColor(table.getBackground());
				g.fillRect(cellRect.x, cellRect.y, cellRect.width, cellRect.height);

				// Paint the vertical grid lines if necessary.
				if (table.getShowVerticalLines()) {
					g.setColor(table.getGridColor());
					int x1 = cellRect.x;
					int y1 = cellRect.y;
					int x2 = x1 + cellRect.width - 1;
					int y2 = y1 + cellRect.height - 1;
					// Left
					g.drawLine(x1 - 1, y1, x1 - 1, y2);
					// Right
					g.drawLine(x2, y1, x2, y2);
				}

				// Render the cell value
				paintCell(g, cellRect, row, draggedColumnIndex);
			}

			// Paint the (lower) horizontal grid line if necessary.
			if (table.getShowHorizontalLines()) {
				g.setColor(table.getGridColor());
				Rectangle rcr = table.getCellRect(row, draggedColumnIndex, true);
				rcr.x += distance;
				int x1 = rcr.x;
				int y1 = rcr.y;
				int x2 = x1 + rcr.width - 1;
				int y2 = y1 + rcr.height - 1;
				g.drawLine(x1, y2, x2, y2);
			}
		}
	}
}
