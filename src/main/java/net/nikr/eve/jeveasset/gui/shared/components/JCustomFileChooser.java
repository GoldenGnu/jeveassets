/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.shared.FileUtil;


public final class JCustomFileChooser {

	public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
	private static OverwriteFileChooser FILE_CHOOSER = null;

	private List<String> extensions;
	private File file = null;
	private boolean multiSelection = false;
	private int fileSelectionMode = JFileChooser.FILES_ONLY;
	private File currentDirectory = null;

	public JCustomFileChooser(String extension) {
		this(Collections.singletonList(extension));
	}

	public JCustomFileChooser(List<String> extensions) {
		this.extensions = extensions;
		if (FILE_CHOOSER == null) {
			//XXX - Workaround for https://bugs.openjdk.java.net/browse/JDK-8179014
			UIManager.put("FileChooser.useSystemExtensionHiding", false);
			FILE_CHOOSER = new OverwriteFileChooser();
		}
	}

	public void setSelectedFile(File file) {
		if (FILE_CHOOSER.getDialogType() != JFileChooser.OPEN_DIALOG && file != null) {
			String filename = file.getAbsolutePath();
			if (filename.matches("(?i).*\\.\\w{0,4}$")) { //Already got a extension - remove it
				int end = filename.lastIndexOf(".");
				filename = filename.substring(0, end);
			}
			filename = filename + "." + extensions.get(0);
			file = new File(filename);
		}
		this.file = file;
	}

	private void loadSettings() {
		FILE_CHOOSER.setSelectedFile(file);
		FILE_CHOOSER.resetChoosableFileFilters();
		FILE_CHOOSER.addChoosableFileFilter(new CustomFileFilter(extensions));
		FILE_CHOOSER.setMultiSelectionEnabled(multiSelection);
		FILE_CHOOSER.setFileSelectionMode(fileSelectionMode);
		FILE_CHOOSER.setCurrentDirectory(currentDirectory);
	}

	public File getSelectedFile() {
		file = FILE_CHOOSER.getSelectedFile();
		return file;
	}

	public int showDialog(Component parent, String approveButtonText) throws HeadlessException {
		loadSettings();
		return FILE_CHOOSER.showDialog(parent, approveButtonText);
	}

	public int showSaveDialog(Component parent) throws HeadlessException {
		loadSettings();
		return FILE_CHOOSER.showSaveDialog(parent);
	}

	public int showOpenDialog(Component parent) throws HeadlessException {
		loadSettings();
		return FILE_CHOOSER.showOpenDialog(parent);
	}

	public void setExtension(String extension) {
		extensions = Collections.singletonList(extension);
	}

	public void setMultiSelectionEnabled(boolean b) {
		this.multiSelection = b;
	}

	public void setFileSelectionMode(int mode) {
		this.fileSelectionMode = mode;
	}

	public void setCurrentDirectory(File dir) {
		this.currentDirectory = dir;
		
	}

	public static class CustomFileFilter extends FileFilter {

		private final Set<String> extensions = new HashSet<>();
		private final String description;

		public CustomFileFilter(final List<String> extensions) {
			StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (String extension : extensions) {
				if (first) {
					first = false;
				} else {
					builder.append("/");
				}
				builder.append(extension.toUpperCase());
				this.extensions.add(extension.toLowerCase());
			}
			description = GuiShared.get().files(builder.toString());
		}

		@Override
		public boolean accept(final File f) {
			if (f.isDirectory()) {
				return true;
			}
			String currentExtension = FileUtil.getExtension(f);
			return currentExtension != null && extensions.contains(currentExtension);
		}

		//The description of this filter
		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 53 * hash + Objects.hashCode(this.extensions);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final CustomFileFilter other = (CustomFileFilter) obj;
			if (!Objects.equals(this.extensions, other.extensions)) {
				return false;
			}
			return true;
		}
	}

	private static class OverwriteFileChooser extends JFileChooser {

		private Component parent;

		public OverwriteFileChooser() {
			setAcceptAllFileFilterUsed(false);
		}

		@Override
		public int showDialog(Component parent, String approveButtonText) throws HeadlessException {
			this.parent = parent;
			return super.showDialog(parent, approveButtonText);
		}

		@Override
		public int showSaveDialog(Component parent) throws HeadlessException {
			this.parent = parent;
			return super.showSaveDialog(parent);
		}

		@Override
		public int showOpenDialog(Component parent) throws HeadlessException {
			this.parent = parent;
			return super.showOpenDialog(parent);
		}

		@Override
		public void approveSelection() {
			File selectedFile = this.getSelectedFile();
			//Confirm Overwrite file
			if (getDialogType() != JFileChooser.OPEN_DIALOG && selectedFile != null && selectedFile.exists()) {
				int nReturn = JOptionPane.showConfirmDialog(
						parent,
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
	}
}
