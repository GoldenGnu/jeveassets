/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import net.nikr.eve.jeveasset.data.tag.Tags;
import net.nikr.eve.jeveasset.gui.shared.Formater;


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
			} else {
				setText(Formater.longFormat(value));
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
			} else {
				setText(Formater.doubleFormat(value));
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
			} else {
				setText(Formater.integerFormat(value));
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
			} else {
				setText(Formater.floatFormat(value));
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
			} else {
				setText(Formater.columnDate(value));
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
}
