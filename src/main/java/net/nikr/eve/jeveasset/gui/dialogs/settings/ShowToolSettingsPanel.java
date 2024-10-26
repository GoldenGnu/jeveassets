/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.MOVE;
import javax.swing.TransferHandler.TransferSupport;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.ToolLoader;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShowToolSettingsPanel extends JSettingsPanel {

	private static final Logger LOG = LoggerFactory.getLogger(ShowToolSettingsPanel.class);

	private final static double COLUMN_COUNT = 4.0;

	private final JRadioButton jSelected;
	private final JRadioButton jSave;
	private final JLabel jToolsLabel;
	private final DefaultListModel<Tool> model = new DefaultListModel<>();
	private final JList<Tool> jTools;
	TreeSet<Tool> tools = new TreeSet<>();

	public ShowToolSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().show(), Images.EDIT_SHOW.getIcon());

		for (String toolTitle : ToolLoader.getToolTitles(program)) {
			tools.add(new Tool(toolTitle));
		}

		ButtonGroup buttonGroup = new ButtonGroup();
		jSave = new JRadioButton(DialoguesSettings.get().saveTools());
		jSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setEnabled(false);
			}
		});
		buttonGroup.add(jSave);

		jSelected = new JRadioButton(DialoguesSettings.get().selectTools());
		jSelected.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setEnabled(true);
			}
		});

		buttonGroup.add(jSelected);

		GroupLayout.SequentialGroup horizontal = layout.createSequentialGroup();
		List<GroupLayout.ParallelGroup> columns = new ArrayList<>();
		for (int i = 0; i < COLUMN_COUNT; i++) {
			GroupLayout.ParallelGroup column = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
			horizontal.addGroup(column);
			columns.add(column);
		}
		int rowCount = (int) Math.ceil(tools.size() / COLUMN_COUNT);
		GroupLayout.SequentialGroup vertical = layout.createSequentialGroup();
		List<GroupLayout.ParallelGroup> rows = new ArrayList<>();
		for (int i = 0; i < rowCount; i++) {
			GroupLayout.ParallelGroup row = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
			vertical.addGroup(row);
			rows.add(row);
		}
		int row = 0;
		int column = 0;
		for (Tool tool : tools) {
			if (row >= rowCount) {
				row = 0;
				column++;
			}
			JCheckBox jCheckBox = tool.getCheckBox();
			jCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (jCheckBox.isSelected()) {
						if (!model.contains(tool)) {
							model.addElement(tool);
						}
					} else {
						model.removeElement(tool);
					}
				}
			});
			rows.get(row).addComponent(jCheckBox);
			columns.get(column).addComponent(jCheckBox);
			row++;
		}

		jToolsLabel = new JLabel(DialoguesSettings.get().toolsOrderHelp());

		jTools = new JList<>(model);
		jTools.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTools.setTransferHandler(new ListItemTransferHandler());
		jTools.setDropMode(DropMode.INSERT);
		jTools.setDragEnabled(true);

		JScrollPane jToolsOrderScroll = new JScrollPane(jTools);
		jToolsOrderScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jSave)
				.addComponent(jSelected)
				.addGroup(horizontal)
				.addComponent(jToolsLabel)
				.addComponent(jToolsOrderScroll)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jSave, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSelected, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGroup(vertical)
				.addComponent(jToolsLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addGap(0)
				.addComponent(jToolsOrderScroll, 140, 140, Integer.MAX_VALUE)
		);
	}

	@Override
	public UpdateType save() {
		Settings.get().setSaveToolsOnExit(jSave.isSelected()); //must be before: program.getMainWindow().saveShown() (or it won't save)
		if (jSave.isSelected()) {
			Settings.get().getShowTools().clear();
			for (JMainTab tab : program.getMainWindow().getTabs()) {
				Settings.get().getShowTools().add(tab.getTitle());
			}
		} else {
			Settings.get().getShowTools().clear();
			for (int i = 0; i < model.size(); i++) {
				Settings.get().getShowTools().add(model.get(i).getTitle());
			}
		}
		return UpdateType.NONE;
	}

	@Override
	public void load() {
		//Reset
		model.removeAllElements();
		for (Tool tool : tools) {
			tool.setSelected(false);
		}
		if (Settings.get().isSaveToolsOnExit()) {
			jSave.setSelected(true);
		} else {
			jSelected.setSelected(true);
			for (String toolTitle :Settings.get().getShowTools()) {
				for (Tool tool : tools) {
					if (tool.getTitle().equals(toolTitle)) {
						tool.setSelected(true);
						model.addElement(tool);
					}
				}
			}
		}
		setEnabled(!Settings.get().isSaveToolsOnExit());
	}

	private void setEnabled(boolean b) {
		jTools.setEnabled(b);
		jToolsLabel.setEnabled(b);
		for (Tool tool : tools) {
			tool.setEnabled(b);
		}
	}

	private static class Tool implements Comparable<Tool>, Serializable {
		private final String title;
		private final JCheckBox jCheckBox;

		public Tool(String title) {
			this.title = title;
			this.jCheckBox = new JCheckBox(title);
		}

		public String getTitle() {
			return title;
		}

		public void setSelected(boolean b) {
			jCheckBox.setSelected(b);
		}

		public void setEnabled(boolean b) {
			jCheckBox.setEnabled(b);
		}

		public boolean isSelected() {
			return jCheckBox.isSelected();
		}

		public JCheckBox getCheckBox() {
			return jCheckBox;
		}

		@Override
		public int compareTo(Tool o) {
			return title.compareTo(o.title);
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 83 * hash + Objects.hashCode(this.title);
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
			final Tool other = (Tool) obj;
			if (!Objects.equals(this.title, other.title)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return title;
		}
	}

	// @camickr already suggested above.
	// https://docs.oracle.com/javase/tutorial/uiswing/dnd/dropmodedemo.html
	@SuppressWarnings("serial")
	public static class ListItemTransferHandler extends TransferHandler {

		protected final DataFlavor localObjectFlavor;
		protected int[] indices;
		protected int addIndex = -1; // Location where items were added
		protected int addCount; // Number of items added.

		public ListItemTransferHandler() {
			super();
			// localObjectFlavor = new ActivationDataFlavor(
			// Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
			localObjectFlavor = new DataFlavor(Object[].class, "Array of items");
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			JList<?> source = (JList<?>) c;
			c.getRootPane().getGlassPane().setVisible(true);

			indices = source.getSelectedIndices();
			Object[] transferedObjects = source.getSelectedValuesList().toArray(new Object[0]);
			// return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
			return new Transferable() {
				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[]{localObjectFlavor};
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor) {
					return Objects.equals(localObjectFlavor, flavor);
				}

				@Override
				public Object getTransferData(DataFlavor flavor)
						throws UnsupportedFlavorException, IOException {
					if (isDataFlavorSupported(flavor)) {
						return transferedObjects;
					} else {
						throw new UnsupportedFlavorException(flavor);
					}
				}
			};
		}

		@Override
		public boolean canImport(TransferSupport info) {
			return info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
		}

		@Override
		public int getSourceActions(JComponent c) {
			Component glassPane = c.getRootPane().getGlassPane();
			glassPane.setCursor(DragSource.DefaultMoveDrop);
			return MOVE; // COPY_OR_MOVE;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean importData(TransferSupport info) {
			TransferHandler.DropLocation tdl = info.getDropLocation();
			if (!canImport(info) || !(tdl instanceof JList.DropLocation)) {
				return false;
			}

			JList.DropLocation dl = (JList.DropLocation) tdl;
			JList<?> target = (JList) info.getComponent();
			DefaultListModel<Object> listModel = (DefaultListModel) target.getModel();
			int max = listModel.getSize();
			int index = dl.getIndex();
			index = index < 0 ? max : index; // If it is out of range, it is appended to the end
			index = Math.min(index, max);

			addIndex = index;

			try {
				Object[] values = (Object[]) info.getTransferable().getTransferData(localObjectFlavor);
				for (Object value : values) {
					int idx = index++;
					listModel.add(idx, value);
					target.addSelectionInterval(idx, idx);
				}
				addCount = values.length;
				return true;
			} catch (UnsupportedFlavorException | IOException ex) {
				LOG.error(ex.getMessage(), ex);
				return false;
			}
		}

		@Override
		protected void exportDone(JComponent c, Transferable data, int action) {
			c.getRootPane().getGlassPane().setVisible(false);
			cleanup(c, action == MOVE);
		}

		private void cleanup(JComponent c, boolean remove) {
			if (remove && Objects.nonNull(indices)) {
				if (addCount > 0) {
					// https://github.com/aterai/java-swing-tips/blob/master/DragSelectDropReordering/src/java/example/MainPanel.java
					for (int i = 0; i < indices.length; i++) {
						if (indices[i] >= addIndex) {
							indices[i] += addCount;
						}
					}
				}
				JList<?> source = (JList) c;
				DefaultListModel<?> model = (DefaultListModel) source.getModel();
				for (int i = indices.length - 1; i >= 0; i--) {
					model.remove(indices[i]);
				}
			}

			indices = null;
			addCount = 0;
			addIndex = -1;
		}
	}
}

