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
package net.nikr.eve.jeveasset.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.gui.shared.Formater;
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
		public char getCharacter() {
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
		public String getString() {
			return string;
		}
		@Override
		public String toString() {
			return getI18N();
		}
		abstract String getI18N();
	}
	public  enum DecimalSeperator {
		DOT("Dot") {
			@Override
			String getI18N() {
				return DialoguesCsvExport.get().dot();
			}
		},
		COMMA("Comma") {
			@Override
			String getI18N() {
				return DialoguesCsvExport.get().comma();
			}
		}
		;

		String string;
		private DecimalSeperator(String string) {
			this.string = string;
		}
		public String getString() {
			return string;
		}
		@Override
		public String toString() {
			return getI18N();
		}
		abstract String getI18N();
	}
	
	
	FieldDelimiter fieldDelimiter = FieldDelimiter.COMMA;
	LineDelimiter lineDelimiter = LineDelimiter.DOS;
	DecimalSeperator decimalSeperator = DecimalSeperator.DOT;
	String path = getDefaultPath();
	List<String> columns = new ArrayList<String>();
	int maxColumns = 0;

	public CsvSettings() {}
	
	public boolean isDefault(){
		return this.fieldDelimiter == FieldDelimiter.COMMA
			&& this.lineDelimiter == LineDelimiter.DOS
			&& this.decimalSeperator == DecimalSeperator.DOT
			&& this.path.equals(getDefaultPath())
			&& this.columns.equals(new ArrayList<String>()); //FIXME
	}

	public int getMaxColumns() {
		return maxColumns;
	}

	public void setMaxColumns(int columnSize) {
		this.maxColumns = columnSize;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
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

	/**
	 * Return a String with the path part of the filename
	 * @return path (always ending with File.separator)
	 */
	public String getPath() {
		if (path.lastIndexOf(File.separator) != path.length() - 1) path = path + File.separator;
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getFile() {
		return getPath()+getDefaultFile();
	}
	
	private String getDefaultPath(){
		return Settings.getUserDirectory();
	}

	private String getDefaultFile(){
		return "assets"+Formater.simpleDate( new Date() )+".csv";
	}
}
