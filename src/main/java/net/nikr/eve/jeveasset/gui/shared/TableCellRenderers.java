/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;


public class TableCellRenderers {

	public static class LongCellRenderer extends DefaultTableCellRenderer {
		public LongCellRenderer() {
			this.setHorizontalTextPosition(DefaultTableCellRenderer.RIGHT);
			this.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		}
		@Override
		public void setValue(Object value) {
			setText((value == null) ? "" : Formater.longFormat(value));
		}
	}

	public static class DoubleCellRenderer extends DefaultTableCellRenderer {
		public DoubleCellRenderer() {
			this.setHorizontalTextPosition(DefaultTableCellRenderer.RIGHT);
			this.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		}
		@Override
		public void setValue(Object value) {
			setText((value == null) ? "" : Formater.doubleFormat(value));
		}
	}

	public static class IntegerCellRenderer extends DefaultTableCellRenderer {
		public IntegerCellRenderer() {
			this.setHorizontalTextPosition(DefaultTableCellRenderer.RIGHT);
			this.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		}

		@Override
		public void setValue(Object value) {
			setText((value == null) ? "" : Formater.integerFormat(value));
		}
	}

	public static class FloatCellRenderer extends DefaultTableCellRenderer {
		public FloatCellRenderer() {
			this.setHorizontalTextPosition(DefaultTableCellRenderer.RIGHT);
			this.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		}
		@Override
		public void setValue(Object value) {
			setText((value == null) ? "" : Formater.floatFormat(value));
		}
	}

	public static class DateCellRenderer extends DefaultTableCellRenderer {
		public DateCellRenderer() {
			this.setHorizontalTextPosition(DefaultTableCellRenderer.RIGHT);
			this.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		}
		@Override
		public void setValue(Object value) {
			setText((value == null) ? "" : Formater.defaultDate(value));
		}
	}
	
	public static class ToStringCellRenderer extends DefaultTableCellRenderer {
		
		public ToStringCellRenderer() {	this(SwingConstants.RIGHT); }
		public ToStringCellRenderer(int alignment) {
			this.setHorizontalTextPosition(alignment);
			this.setHorizontalAlignment(alignment);
		}
		@Override
		public void setValue(Object value) {
			setText((value == null) ? "" : value.toString());
		}
	}
}
