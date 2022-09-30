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

package net.nikr.eve.jeveasset.gui.shared.table;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import net.nikr.eve.jeveasset.data.settings.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JButtonComparable;


public class TableCellRenderers {

	public static class LongCellRenderer extends DefaultTableCellRenderer {
		public LongCellRenderer() {
			this.setHorizontalTextPosition(DefaultTableCellRenderer.RIGHT);
			this.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		}
		@Override
		public void setValue(final Object value) {
			if (value == null) {
				setText("");
			} else if (value instanceof Number) {
				setText(Formater.longFormat(value));
			} else {
				setText(value.toString());
			}
		}
	}

	public static class DoubleCellRenderer extends DefaultTableCellRenderer {
		public DoubleCellRenderer() {
			this.setHorizontalTextPosition(DefaultTableCellRenderer.RIGHT);
			this.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		}
		@Override
		public void setValue(final Object value) {
			if (value == null) {
				setText("");
			} else if (value instanceof Number) {
				setText(Formater.doubleFormat(value));
			} else {
				setText(value.toString());
			}
		}
	}

	public static class IntegerCellRenderer extends DefaultTableCellRenderer {
		public IntegerCellRenderer() {
			this.setHorizontalTextPosition(DefaultTableCellRenderer.RIGHT);
			this.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		}

		@Override
		public void setValue(final Object value) {
			if (value == null) {
				setText("");
			} else if (value instanceof Number) {
				setText(Formater.integerFormat(value));
			} else {
				setText(value.toString());
			}
		}
	}

	public static class FloatCellRenderer extends DefaultTableCellRenderer {
		public FloatCellRenderer() {
			this.setHorizontalTextPosition(DefaultTableCellRenderer.RIGHT);
			this.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		}
		@Override
		public void setValue(final Object value) {
			if (value == null) {
				setText("");
			} else if (value instanceof Number) {
				setText(Formater.floatFormat(value));
			} else {
				setText(value.toString());
			}
		}
	}

	public static class DateCellRenderer extends DefaultTableCellRenderer {
		public DateCellRenderer() {
			this.setHorizontalTextPosition(DefaultTableCellRenderer.RIGHT);
			this.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		}
		@Override
		public void setValue(final Object value) {
			if (value == null) {
				setText("");
			} else if (value instanceof Date) {
				setText(Formater.columnDate(value));
			} else {
				setText(value.toString());
			}
		}
	}

	public static class ToStringCellRenderer extends DefaultTableCellRenderer {

		public ToStringCellRenderer() {	this(SwingConstants.RIGHT); }
		public ToStringCellRenderer(final int alignment) {
			this.setHorizontalTextPosition(alignment);
			this.setHorizontalAlignment(alignment);
		}
		@Override
		public void setValue(final Object value) {
			if (value == null) {
				setText("");
			} else {
				setText(value.toString());
			}
		}
	}

	public static class TagsCellRenderer extends DefaultTableCellRenderer {

		public TagsCellRenderer() {
			this.setHorizontalTextPosition(SwingConstants.CENTER);
			this.setHorizontalAlignment(SwingConstants.CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel jLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); //To change body of generated methods, choose Tools | Templates.
			if (value instanceof Tags) {
				Tags tags = (Tags) value;
				JPanel jPanel = tags.getPanel();
				jPanel.setBackground(jLabel.getBackground());
				jPanel.setForeground(jLabel.getForeground());
				jPanel.setBorder(jLabel.getBorder());
				return jPanel;
			}
			return jLabel;
		}
	}

	public static class ComponentRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (value instanceof JButtonComparable) {
				JButtonComparable jButton = (JButtonComparable) value;
				if (isSelected && table.getSelectedColumns().length == 1 && table.getSelectedRows().length == 1) {
					jButton.getModel().setRollover(true);
					jButton.getModel().setSelected(true);
				} else {
					jButton.getModel().setRollover(false);
					jButton.getModel().setSelected(false);
				}
				return jButton;
			} else if (value instanceof Component) {
				return (Component) value;
			} else {
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		}
	}

	public static class ComponentEditor extends AbstractCellEditor implements TableCellEditor {

		private final TableCellEditor defaultTableCellEditor;

		public ComponentEditor(TableCellEditor defaultTableCellEditor) {
			this.defaultTableCellEditor = defaultTableCellEditor;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			if (value instanceof Component) {
				return (Component) value;
			} else {
				return defaultTableCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
			}
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}
	}

	/**
	 * NumberEditor that handle space and comma thousand separator
	 * Original code from JTable.GenericEditor and JTable.NumberEditor
	 */
	static class BetterNumberEditor extends DefaultCellEditor {

		Class<?>[] argTypes = new Class<?>[]{String.class};
		java.lang.reflect.Constructor<?> constructor;
		Object value;

		public BetterNumberEditor() {
			super(new JTextField());
			getComponent().setName("Table.editor");
			((JTextField)getComponent()).setHorizontalAlignment(JTextField.RIGHT);
		}

		@Override
		public boolean stopCellEditing() {
			String s = (String)super.getCellEditorValue();

			//Workaround start
			s = s.replace(",", ""); //Ignore thousands separator
			s = s.replace(" ", ""); //Ignore thousands separator
			//workaround end (All other code is directly from Java core)

			// Here we are dealing with the case where a user
			// has deleted the string value in a cell, possibly
			// after a failed validation. Return null, so that
			// they have the option to replace the value with
			// null or use escape to restore the original.
			// For Strings, return "" for backward compatibility.
			try {
				if ("".equals(s)) {
					if (constructor.getDeclaringClass() == String.class) {
						value = s;
					}
					return super.stopCellEditing();
				}
				value = constructor.newInstance(new Object[]{s});
			}
			catch (Exception e) {
				((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
				return false;
			}
			return super.stopCellEditing();
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value,
												 boolean isSelected,
												 int row, int column) {
			this.value = null;
			((JComponent)getComponent()).setBorder(new LineBorder(Color.black));
			try {
				Class<?> type = table.getColumnClass(column);
				// Since our obligation is to produce a value which is
				// assignable for the required type it is OK to use the
				// String constructor for columns which are declared
				// to contain Objects. A String is an Object.
				if (type == Object.class) {
					type = String.class;
				}
				constructor = type.getConstructor(argTypes);
			}
			catch (Exception e) {
				return null;
			}
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}

		@Override
		public Object getCellEditorValue() {
			return value;
		}
	}
}
