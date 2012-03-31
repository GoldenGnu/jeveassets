/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data;

import net.nikr.eve.jeveasset.i18n.DialoguesCsvExport;


public class CsvSettings {

	public enum FieldDelimiter {
		COMMA(',') {
			@Override
			String getI18N() {
				return DialoguesCsvExport.get().comma();
			}
		},
		SEMICOLON(';') {
			@Override
			String getI18N() {
				return DialoguesCsvExport.get().semicolon();
			}
		}
		;
		char character;
		private FieldDelimiter(char character) {
			this.character = character;
		}
		public char getValue() {
			return character;
		}
		@Override
		public String toString() {
			return getI18N();
		}
		abstract String getI18N();
	}
	public enum LineDelimiter {
		DOS("\r\n") {
			@Override
			String getI18N() {
				return DialoguesCsvExport.get().lineEndingsWindows();
			}
		},
		MAC("\r") {
			@Override
			String getI18N() {
				return DialoguesCsvExport.get().lineEndingsMac();
			}
		},
		UNIX("\n") {
			@Override
			String getI18N() {
				return DialoguesCsvExport.get().lineEndingsUnix();
			}
		}
		;

		String string;
		private LineDelimiter(String string) {
			this.string = string;
		}
		public String getValue() {
			return string;
		}
		@Override
		public String toString() {
			return getI18N();
		}
		abstract String getI18N();
	}
	public  enum DecimalSeperator {
		DOT() {
			@Override
			String getI18N() {
				return DialoguesCsvExport.get().dot();
			}
		},
		COMMA() {
			@Override
			String getI18N() {
				return DialoguesCsvExport.get().comma();
			}
		}
		;
		@Override
		public String toString() {
			return getI18N();
		}
		abstract String getI18N();
	}
	
	
	private static final String PATH = Settings.getUserDirectory();
	private static final String FILENAME = Settings.getUserDirectory()+"export.csv";
	
	private FieldDelimiter fieldDelimiter;
	private LineDelimiter lineDelimiter;
	private DecimalSeperator decimalSeperator;
	private String filename = FILENAME;

	public CsvSettings() {
		clear();
	}
	
	public final void clear(){
		fieldDelimiter = FieldDelimiter.COMMA;
		lineDelimiter = LineDelimiter.DOS;
		decimalSeperator = DecimalSeperator.DOT;
		filename = FILENAME;
	}
	
	public DecimalSeperator getDecimalSeperator() {
		return decimalSeperator;
	}

	public void setDecimalSeperator(DecimalSeperator decimalSeperator) {
		this.decimalSeperator = decimalSeperator;
	}

	public FieldDelimiter getFieldDelimiter() {
		return fieldDelimiter;
	}

	public void setFieldDelimiter(FieldDelimiter fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}

	public LineDelimiter getLineDelimiter() {
		return lineDelimiter;
	}

	public void setLineDelimiter(LineDelimiter lineDelimiter) {
		this.lineDelimiter = lineDelimiter;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public static String getDefaultPath() {
		return PATH;
	}
	public static String getDefaultFilename() {
		return FILENAME;
	}
}
