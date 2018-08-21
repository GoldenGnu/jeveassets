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

package net.nikr.eve.jeveasset.gui.shared.components;

import java.awt.Window;
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


public final class JCustomFileChooser extends JFileChooser {

	private final Window window;
	private List<String> extensions;

	public static JCustomFileChooser createFileChooser(final Window window, final String extension) {
		return createFileChooser(window, Collections.singletonList(extension));
	}

	public static JCustomFileChooser createFileChooser(final Window window, final List<String> extensions) {
		//XXX - Workaround for https://bugs.openjdk.java.net/browse/JDK-8179014
		UIManager.put("FileChooser.useSystemExtensionHiding", false);
		return new JCustomFileChooser(window, extensions);
	}

	private JCustomFileChooser(final Window window, final List<String> extensions) {
		this.window = window;
		setExtensions(extensions);
		this.setAcceptAllFileFilterUsed(false);
	}

	@Override
	public void setSelectedFile(File file) {
		if (getDialogType() != OPEN_DIALOG && file != null) {
			String filename = file.getAbsolutePath();
			if (filename.matches("(?i).*\\.\\w{0,4}$")) { //Already got a extension - remove it
				int end = filename.lastIndexOf(".");
				filename = filename.substring(0, end);
			}
			filename = filename + "." + extensions.get(0);
			super.setSelectedFile(new File(filename));
		} else {
			super.setSelectedFile(file);
		}
		
	}

	public final void setExtension(final String extension) {
		setExtensions(Collections.singletonList(extension));
	}

	public final void setExtensions(final List<String> extensions) {
		this.extensions = extensions;
		this.resetChoosableFileFilters();
		this.addChoosableFileFilter(new CustomFileFilter(extensions));
	}

	@Override
	public void approveSelection() {
		File selectedFile = this.getSelectedFile();
		//Confirm Overwrite file
		if (getDialogType() != OPEN_DIALOG && selectedFile != null && selectedFile.exists()) {
			int nReturn = JOptionPane.showConfirmDialog(
					window,
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
}
