/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JCustomFileChooser extends JFileChooser {

	private JFrame jFrame;

	//Multiple extensions
	public JCustomFileChooser(final JFrame jFrame, final String... extension) {
		this(jFrame, Arrays.asList(extension));
	}

	//List of extensions
	public JCustomFileChooser(final JFrame jFrame, final List<String> extensions) {
		this.jFrame = jFrame;
		setExtensions(extensions);
		this.setAcceptAllFileFilterUsed(false);
	}

	public final void setExtensions(final String... extension) {
		setExtensions(Arrays.asList(extension));
	}

	public final void setExtensions(List<String> extensions) {
		this.resetChoosableFileFilters();
		for (String extension : extensions) {
			this.addChoosableFileFilter(new CustomFileFilter(extension, GuiShared.get().files(extension.toUpperCase())));
		}
	}

	@Override
	public void approveSelection() {
		File selectedFile = this.getSelectedFile();
		//No extension - Add extension from file filter
		if (Utils.getExtension(selectedFile) == null) {
			selectedFile = new File(selectedFile.getAbsolutePath() + "." + getExtension());
			this.setSelectedFile(selectedFile);
		}
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
		FileFilter fileFilter = getFileFilter();
		if (fileFilter instanceof CustomFileFilter) {
			CustomFileFilter filter = (CustomFileFilter) fileFilter;
			return filter.getExtension();
		} else {
			return null;
		}
	}

	@Override
	public void setFileFilter(final FileFilter filter) {
		super.setFileFilter(filter);

		//Add extension from FileFilter
		FileChooserUI fcui = getUI();
		if (fcui instanceof BasicFileChooserUI) {
			BasicFileChooserUI bui = (BasicFileChooserUI) fcui;

			String file = bui.getFileName();

			if (file != null) {
				int end = file.lastIndexOf(".");
				if (end > 0) {
					file = file.substring(0, end);
				}
				String extension = getExtension();
				if (extension != null) {
					bui.setFileName(file + "." + extension);
				}
			}
		}
	}

	@Override
	public void setSelectedFile(final File file) {
		super.setSelectedFile(file);

		//Set the correct FileFilter
		if (file != null) {
			for (FileFilter fileFilter : this.getChoosableFileFilters()) {
				if (fileFilter instanceof CustomFileFilter) {
					CustomFileFilter filter = (CustomFileFilter) fileFilter;
					if (filter.getExtension().equals(Utils.getExtension(file))) {
						setFileFilter(filter);
					}
				}
			}
		}
	}

	public static class CustomFileFilter extends FileFilter {

		private String filterExtension;
		private String filterDescription;

		public CustomFileFilter(final String filterExtension, final String filterDescription) {
			this.filterExtension = filterExtension;
			this.filterDescription = filterDescription;
		}

		@Override
		public boolean accept(final File f) {
			if (f.isDirectory()) {
				return true;
			}
			String extension = Utils.getExtension(f);
			if (extension != null) {
				return extension.equals(filterExtension);
			} else {
				return false;
			}
		}

		//The description of this filter
		@Override
		public String getDescription() {
			return filterDescription;
		}

		public String getExtension() {
			return filterExtension;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final CustomFileFilter other = (CustomFileFilter) obj;
			if ((this.filterExtension == null) ? (other.filterExtension != null) : !this.filterExtension.equals(other.filterExtension)) {
				return false;
			}
			if ((this.filterDescription == null) ? (other.filterDescription != null) : !this.filterDescription.equals(other.filterDescription)) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 97 * hash + (this.filterExtension != null ? this.filterExtension.hashCode() : 0);
			hash = 97 * hash + (this.filterDescription != null ? this.filterDescription.hashCode() : 0);
			return hash;
		}
	}

	public static class Utils {
		//public final static String XML = "xml";
		public static String getExtension(final File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');
			if (i > 0 &&  i < s.length() - 1) {
				ext = s.substring(i + 1).toLowerCase();
			}
			return ext;
		}

	}
}
