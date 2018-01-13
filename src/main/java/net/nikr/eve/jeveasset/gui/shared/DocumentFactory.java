/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

import java.awt.Toolkit;
import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.PlainDocument;


public final class DocumentFactory {

	public enum ValueFlag {
		POSITIVE_AND_NOT_ZERO {
			@Override
			public boolean match(int i) {
				return i > 0;
			}
			@Override
			public boolean match(double i) {
				return i > 0;
			}
		},
		POSITIVE_AND_ZERO {
			@Override
			public boolean match(int i) {
				return i >= 0;
			}
			@Override
			public boolean match(double i) {
				return i >= 0;
			}
		},
		NEGATIVE_AND_NOT_ZERO {
			@Override
			public boolean match(int i) {
				return i < 0;
			}
			@Override
			public boolean match(double i) {
				return i < 0;
			}
		},
		NEGATIVE_AND_ZERO {
			@Override
			public boolean match(int i) {
				return i <= 0;
			}
			@Override
			public boolean match(double i) {
				return i <= 0;
			}
		},
		ANY_NUMBER {
			@Override
			public boolean match(int i) {
				return true;
			}
			@Override
			public boolean match(double i) {
				return true;
			}
		},
		NOT_ZERO {
			@Override
			public boolean match(int i) {
				return i != 0;
			}
			@Override
			public boolean match(double i) {
				return i != 0;
			}
		};
		public abstract boolean match(int i);
		public abstract boolean match(double d);
	}

	public static IntegerPlainDocument getIntegerPlainDocument() {
		return new IntegerPlainDocument(ValueFlag.ANY_NUMBER);
	}
	public static IntegerPlainDocument getIntegerPlainDocument(ValueFlag flag) {
		return new IntegerPlainDocument(flag);
	}
	public static WordPlainDocument getWordPlainDocument() {
		return new WordPlainDocument();
	}
	public static DoublePlainDocument getDoublePlainDocument() {
		return new DoublePlainDocument(ValueFlag.ANY_NUMBER);
	}
	public static DoublePlainDocument getDoublePlainDocument(ValueFlag flag) {
		return new DoublePlainDocument(flag);
	}
	public static MaxLengthPlainDocument getMaxLengthPlainDocument(final int maxLength) {
		return new MaxLengthPlainDocument(maxLength);
	}
	public static MaxLengthStyledDocument getMaxLengthStyledDocument(final int maxLength) {
		return new MaxLengthStyledDocument(maxLength);
	}

	private DocumentFactory() {	}

	public static class IntegerPlainDocument extends PlainDocument {

		ValueFlag flag;

		public IntegerPlainDocument(ValueFlag flag) {
			this.flag = flag;
		}

		@Override
		public void insertString(final int offset, final String string, final AttributeSet attributes) throws BadLocationException {
			int length = getLength();
			if (string == null) {
				return;
			}
			String newValue;
			if (length == 0) {
				newValue = string;
			} else {
				String currentContent = getText(0, length);
				StringBuilder currentBuffer = new StringBuilder(currentContent);
				currentBuffer.insert(offset, string);
				newValue = currentBuffer.toString();
			}
			try {
				if (flag.match(Integer.parseInt(newValue))) {
					super.insertString(offset, string, attributes);
				} else {
					Toolkit.getDefaultToolkit().beep();
				}
			} catch (NumberFormatException exception) {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}

	public static class DoublePlainDocument extends PlainDocument {

		ValueFlag flag;

		public DoublePlainDocument(ValueFlag flag) {
			this.flag = flag;
		}

		@Override
		public void insertString(final int offset, final String string, final AttributeSet attributes) throws BadLocationException {
			int length = getLength();
			if (string == null) {
				return;
			}
			String newValue;
			if (length == 0) {
				newValue = string;
			} else {
				String currentContent = getText(0, length);
				StringBuilder currentBuffer = new StringBuilder(currentContent);
				currentBuffer.insert(offset, string);
				newValue = currentBuffer.toString();
			}
			try {
				if (flag.match(Double.parseDouble(newValue))) {
					super.insertString(offset, string, attributes);
				} else {
					Toolkit.getDefaultToolkit().beep();
				}
			} catch (NumberFormatException exception) {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}

	public static class WordPlainDocument extends PlainDocument {

		@Override
		public void insertString(final int offset, final String string, final AttributeSet attributes) throws BadLocationException {
			int length = getLength();
			if (string == null) {
				return;
			}
			String newValue;
			if (length == 0) {
				newValue = string;
			} else {
				String currentContent = getText(0, length);
				StringBuilder currentBuffer = new StringBuilder(currentContent);
				currentBuffer.insert(offset, string);
				newValue = currentBuffer.toString();
			}
			boolean b = Pattern.matches("[\\w\\s]*", newValue);
			if (b && !newValue.isEmpty()) {
				super.insertString(offset, string, attributes);
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}

	public static class MaxLengthPlainDocument extends PlainDocument {

		private int maxLength;

		public MaxLengthPlainDocument(final int maxLength) {
			this.maxLength = maxLength;
		}

		@Override
		public void insertString(final int offset, final String string, final AttributeSet attributes) throws BadLocationException {
			int length = getLength();
			if (string == null) {
				return;
			}
			if (length + string.length() > maxLength) {
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			super.insertString(offset, string, attributes);
		}

	}

	public static class MaxLengthStyledDocument extends DefaultStyledDocument {

		private int maxLength;

		public MaxLengthStyledDocument(final int maxLength) {
			this.maxLength = maxLength;
		}

		@Override
		public void insertString(final int offset, final String string, final AttributeSet attributes) throws BadLocationException {
			int length = getLength();
			if (string == null) {
				return;
			}
			if (length + string.length() > maxLength) {
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			super.insertString(offset, string, attributes);
		}
	}

}
