/*
 * Copyright 2009-2022 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.ColorSettings.ColorRow;
import net.nikr.eve.jeveasset.data.settings.Colors;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.ColorUtil;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class JColorTable extends JSeparatorTable {

	private final DefaultEventTableModel<ColorSettings.ColorRow> tableModel;
	private final MyComboBox jForeground;
	private final MyComboBox jBackground;
	private final JLabel jPreview;
	private final JLabel jSelected;
	private final JNullableLabel jNotEditable;
	private int mouseRow = -1;
	private int mouseColumn = -1;
	private int editRow = -1;
	private int editColumn = -1;

	public JColorTable(final Program program, final DefaultEventTableModel<ColorRow> tableModel, SeparatorList<?> separatorList) {
		super(program, tableModel, separatorList);

		this.tableModel = tableModel;
		this.getTableHeader().setDefaultRenderer(new IconTableCellRenderer(this));

		this.setDefaultRenderer(Color.class, new ColorCellRenderer());

		this.getTableHeader().setReorderingAllowed(false);
		this.getTableHeader().setResizingAllowed(false);

		this.addMouseListener(new MouseAdapter() {
		@Override
			public void mouseExited(MouseEvent e) {
				//Repaint
				Rectangle oldCell = getCellRect(mouseRow, mouseColumn, true);
				repaint(oldCell);
				//Reset
				mouseRow = -1;
				mouseColumn = -1;
			}
		});

		this.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				boolean update = false;
				int oldRow = mouseRow;
				int newRow = rowAtPoint(e.getPoint());
				if (newRow != mouseRow) {
					mouseRow = newRow;
					update = true;
				}
				int oldColumn = mouseColumn;
				int newColumn = columnAtPoint(e.getPoint());
				if (newColumn != mouseColumn) {
					mouseColumn = newColumn;
					update = true;
				}
				if (update) {
					Rectangle oldCell = getCellRect(oldRow, oldColumn, true);
					Rectangle newCell = getCellRect(newRow, newColumn, true);
					repaint(oldCell);
					repaint(newCell);
				}
			}
		});

		jForeground = new MyComboBox();
		jBackground = new MyComboBox();

		jNotEditable = new JNullableLabel();
		jNotEditable.setBackground(null);

		jPreview = new JLabel(DialoguesSettings.get().testText());
		jPreview.setOpaque(true);
		jPreview.setHorizontalAlignment(JLabel.CENTER);

		jSelected = new JLabel(DialoguesSettings.get().testSelectedText());
		jSelected.setOpaque(true);
		jSelected.setHorizontalAlignment(JLabel.CENTER);
	}

	public void startEditCell(int editRow, int editColumn) {
		this.editRow = editRow;
		this.editColumn = editColumn;
	}

	public void stopEditCell() {
		//Repaint
		Rectangle oldCell = getCellRect(editRow, editColumn, true);
		repaint(oldCell);
		//Reset
		editRow = -1;
		editColumn = -1;
	}

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		//boolean isSelected = isCellSelected(row, column);
		Object object = tableModel.getElementAt(row);
		if (!(object instanceof ColorRow)) {
			return component;
		}
		ColorRow colorRow = (ColorRow) object;
		String columnName = (String) this.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

		boolean mouseCell = row == mouseRow && column == mouseColumn;
		boolean editCell = row == editRow && column == editColumn;
		if (columnName.equals(ColorsTableFormat.BACKGROUND.getColumnName())) {
			if (colorRow.getColorEntry().isBackgroundEditable()) {
				jBackground.config(colorRow.getBackground(), colorRow.isBackgroundDefault(), mouseCell, editCell);
				return jBackground.getjPanel();
			} else {
				return jNotEditable;
			}
		}
		if (columnName.equals(ColorsTableFormat.FOREGROUND.getColumnName())) {
			if (colorRow.getColorEntry().isForegroundEditable()) {
				jForeground.config(colorRow.getForeground(), colorRow.isForegroundDefault(), mouseCell, editCell);
				return jForeground.getjPanel();
			} else {
				return jNotEditable;
			}
		}
		Color background = colorRow.getBackground();
		Color foreground = colorRow.getForeground();
		if (columnName.equals(ColorsTableFormat.PREVIEW.getColumnName())) {
			jPreview.setBackground(this.getBackground());
			jPreview.setForeground(this.getForeground());
			ColorSettings.configCell(jPreview, foreground, background, false);
			return jPreview;
		}
		if (columnName.equals(ColorsTableFormat.SELECTED.getColumnName())) {
			ColorSettings.configCell(jSelected, foreground, background, true);
			return jSelected;
		}
		return component;
	}

	public static class ColorCellRenderer extends DefaultTableCellRenderer {

		@Override
		public void setValue(final Object value) {
			setText("");
		}
	}

	private static class IconTableCellRenderer implements TableCellRenderer {

		private final JTable jTable;
		private TableCellRenderer tableCellRenderer;

		public IconTableCellRenderer(JTable jTable) {
			this.jTable = jTable;
			this.tableCellRenderer = jTable.getTableHeader().getDefaultRenderer();
			if (tableCellRenderer.getClass().getName().contains("XPDefaultRenderer")) {
				installWorkaround();
			}
		}

		private void installWorkaround() {
			jTable.getTableHeader().addPropertyChangeListener("UI", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					/**
					* Workaround for: https://github.com/GoldenGnu/jeveassets/issues/279 (JDK-6429812)
					* On TableHeader UI update:
					* Create decoupled DefaultTableCellHeaderRenderer to replace the now defunct wrapped renderer
					*/
					IconTableCellRenderer.this.tableCellRenderer = new JTableHeader() {
						@Override
						public TableCellRenderer createDefaultRenderer() {
							return super.createDefaultRenderer();
						}
					}.createDefaultRenderer();
				}
			});
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component component = tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			String columnName = (String) jTable.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();
			if (component instanceof JLabel) {
				JLabel jLabel = (JLabel) component;
				if (columnName.equals(ColorsTableFormat.NAME.getColumnName())) {
					jLabel.setHorizontalAlignment(JLabel.LEFT);
				}
				if (columnName.equals(ColorsTableFormat.BACKGROUND.getColumnName())) {
					jLabel.setText("");
					jLabel.setIcon(Images.SETTINGS_COLOR_BACKGROUND.getIcon());
					jLabel.setHorizontalAlignment(JLabel.CENTER);
				}
				if (columnName.equals(ColorsTableFormat.FOREGROUND.getColumnName())) {
					jLabel.setText("");
					jLabel.setIcon(Images.SETTINGS_COLOR_FOREGROUND.getIcon());
					jLabel.setHorizontalAlignment(JLabel.CENTER);
				}
				if (columnName.equals(ColorsTableFormat.PREVIEW.getColumnName())) {
					jLabel.setText("");
					jLabel.setIcon(Images.EDIT_SHOW.getIcon());
					jLabel.setHorizontalAlignment(JLabel.CENTER);
				}
				if (columnName.equals(ColorsTableFormat.SELECTED.getColumnName())) {
					jLabel.setText("");
					jLabel.setIcon(Images.EDIT_SHOW.getIcon());
					jLabel.setHorizontalAlignment(JLabel.CENTER);
				}
			}
			return component;
		}
	}

	private class MyComboBox {

		private final static double FACTOR = 0.9;

		private final JPanel jPanel;
		private final JNullableLabel jDefault;
		private final JLabel jPickerIcon;

		public MyComboBox() {

			jPanel = new JPanel();

			GroupLayout layout = new GroupLayout(jPanel);
			jPanel.setLayout(layout);
			layout.setAutoCreateGaps(false);
			layout.setAutoCreateContainerGaps(false);

			jDefault = new JNullableLabel();
			jDefault.setHorizontalAlignment(JLabel.CENTER);
			jDefault.setVerticalAlignment(JLabel.CENTER);
			jDefault.setOpaque(true);

			jPickerIcon = new JLabel(Images.SETTINGS_COLOR_PICKER.getIcon());
			jPickerIcon.setHorizontalTextPosition(JLabel.CENTER);
			jPickerIcon.setHorizontalAlignment(JLabel.CENTER);
			jPickerIcon.setVerticalAlignment(JLabel.CENTER);
			jPickerIcon.setOpaque(true);

			layout.setHorizontalGroup(
			layout.createSequentialGroup()
					.addComponent(jDefault, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					.addComponent(jPickerIcon, 16, 16, 16)
			);
			layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(jDefault, 0, 0, Integer.MAX_VALUE)
					.addComponent(jPickerIcon, 0, 0, Integer.MAX_VALUE)
			);
		}

		public JComponent getjPanel() {
			return jPanel;
		}

		public void config(Color color, boolean isDefault, boolean mouseCell, boolean editCell) {
			jDefault.setBackground(color);
			if (color == null) {
				color = Colors.TABLE_SELECTION_FOREGROUND.getColor();
			}
			boolean isBrightColor = ColorUtil.isBrightColor(color);
			if (editCell) {
				jPanel.setBorder(BorderFactory.createLineBorder(getSelectionBackground().darker(), 1));
				jDefault.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ColorUtil.brighter(getSelectionBackground().darker(), FACTOR)));
			} else if (mouseCell) {
				jPanel.setBorder(BorderFactory.createLineBorder(getSelectionBackground(), 1));
				jDefault.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ColorUtil.brighter(getSelectionBackground(), FACTOR)));
			} else {
				jPanel.setBorder(BorderFactory.createLineBorder(jPickerIcon.getBackground(), 1));
				if (isBrightColor) {
					jDefault.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, color.darker()));
				} else {
					jDefault.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ColorUtil.brighter(color, FACTOR)));
				}
			}
			if (isDefault) {
				jDefault.setIcon(null);
			} else {
				if (isBrightColor) {
					jDefault.setIcon(Images.SETTINGS_COLOR_CHECK_BLACK_ALPHA.getIcon());
				} else {
					jDefault.setIcon(Images.SETTINGS_COLOR_CHECK_WHITE_ALPHA.getIcon());
				}
			}
		}
	}

	private static class JNullableLabel extends JLabel {

		boolean paintBackground = false;

		@Override
		public void setBackground(Color bg) {
			super.setBackground(bg);
			if (bg == null) {
				paintBackground = true;
				this.setOpaque(false);
			} else {
				paintBackground = false;
				this.setOpaque(true);
			}
		}

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			if (paintBackground) {
				boolean firstDark = true;
				boolean dark;
				for (int y = 0; y < getSize().getHeight() + 4; y = y + 4) {
					if (firstDark) {
						firstDark = false;
						dark = true;
					} else {
						firstDark = true;
						dark = false;
					}
					for (int x = 0; x < getSize().getWidth() + 4; x = x + 4) {
						if (dark) {
							g2d.setColor(Color.GRAY);
							dark = false;
						} else {
							g2d.setColor(Color.LIGHT_GRAY);
							dark = true;
						}
						g2d.fillRect(x, y, 4, 4);
					}
				}
			}
			super.paintComponent(g);
		}
	}
}
