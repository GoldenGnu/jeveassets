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

package net.nikr.eve.jeveasset.gui.shared.components;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JCustomFileChooser extends JFileChooser {

	private JFrame jFrame;
	private String extension;

	public JCustomFileChooser(final JFrame jFrame, final String extension) {
		this.jFrame = jFrame;
		setExtension(extension);
		this.setAcceptAllFileFilterUsed(false);
	}

	@Override
	public void setSelectedFile(File file) {
		if (file != null) {
			String filename = file.getAbsolutePath();
			if (filename.matches("(?i).*\\.\\w{0,4}$")) { //Already got a extension - remove it
				int end = filename.lastIndexOf(".");
				filename = filename.substring(0, end);
			}
			filename = filename + "." +  getExtension();
			super.setSelectedFile(new File(filename));
		} else {
			super.setSelectedFile(file);
		}
		
	}

	public final void setExtension(final String extension) {
		this.extension = extension.toLowerCase();
		this.resetChoosableFileFilters();
		this.addChoosableFileFilter(new CustomFileFilter(extension));
	}

	@Override
	public void approveSelection() {
		File selectedFile = this.getSelectedFile();
		//Confirm Overwrite file
		if (selectedFile != null && selectedFile.exists()) {
			int nReturn = JOptionPane.showConfirmDialog(
					jFrame,
					GuiShared.get().overwrite(),
					GuiShared.get().overwriteFile(),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE
					);
			if (nReturn == JOptionPane.NO_OPTION) {
				return;
			}
		}
		super.approveSelection();
	}

	public String getExtension() {
		return extension;
	}

	public static class CustomFileFilter extends FileFilter {

		private String extension;

		public CustomFileFilter(final String extension) {
			this.extension = extension;
		}

		@Override
		public boolean accept(final File f) {
			if (f.isDirectory()) {
				return true;
			}
			String currentExtension = Utils.getExtension(f);
			if (currentExtension != null && currentExtension.equals(extension)) {
				return true;
			} else {
				return false;
			}
		}

		//The description of this filter
		@Override
		public String getDescription() {
			return GuiShared.get().files(extension.toUpperCase());
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 23 * hash + (this.extension != null ? this.extension.hashCode() : 0);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final CustomFileFilter other = (CustomFileFilter) obj;
			if ((this.extension == null) ? (other.extension != null) : !this.extension.equals(other.extension)) {
				return false;
			}
			return true;
		}
	}

	private static class Utils {
		//public final static String XML = "xml";
		public static String getExtension(final File file) {
			String extension = null;
			if (file == null) {
				return null;
			}
			String filename = file.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 &&  i < filename.length() - 1) {
				extension = filename.substring(i + 1).toLowerCase();
			}
			return extension;
		}
	}
}
