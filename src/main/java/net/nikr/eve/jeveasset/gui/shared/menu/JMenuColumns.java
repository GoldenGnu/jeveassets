/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.table.AbstractTableModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.table.EditColumnsDialog;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ResizeMode;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.shared.table.ViewManager;
import net.nikr.eve.jeveasset.gui.shared.table.ViewSave;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuColumns<T extends Enum<T> & EnumTableColumn<Q>, Q> extends JMenu {

	private EditColumnsDialog<T, Q> editColumns;
	private ViewSave viewSave;
	private ViewManager viewManager;

	public JMenuColumns(final Program program, EnumTableFormatAdaptor<T, Q> tableFormatAdaptor, final AbstractTableModel tableModel, final JAutoColumnTable jTable, final String name) {
		this(program, tableFormatAdaptor, tableModel, jTable, name, true);
	}

	public JMenuColumns(final Program program, EnumTableFormatAdaptor<T, Q> tableFormatAdaptor, final AbstractTableModel tableModel, final JAutoColumnTable jTable, final String name, final boolean editable) {
		super(GuiShared.get().tableSettings());
		JMenuItem jMenuItem;
		setIcon(Images.TABLE_COLUMN_SHOW.getIcon());

		if (editable) {
			if (editColumns == null) { //Create dialog (only once)
				editColumns = new EditColumnsDialog<>(program, tableFormatAdaptor);
			}
			if (viewSave == null) { //Create dialog (only once)
				viewSave = new ViewSave(program);
			}
			if (viewManager == null) { //Create dialog (only once)
				viewManager = new ViewManager(program, tableFormatAdaptor, tableModel, jTable);
			}
			jMenuItem = new JMenuItem(GuiShared.get().tableColumns(), Images.DIALOG_SETTINGS.getIcon());
			jMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					editColumns.setVisible(true);
					tableModel.fireTableStructureChanged();
					jTable.autoResizeColumns();
				}
			});
			add(jMenuItem);

			addSeparator();

			jMenuItem = new JMenuItem(GuiShared.get().saveView(), Images.FILTER_SAVE.getIcon());
			jMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					//Get views...
					Map<String, View> views = Settings.get().getTableViews(name);
					viewSave.updateData(new ArrayList<>(views.values())); //Update views
					View view = viewSave.show();
					if (view != null) { //Validate
						view.setColumns(tableFormatAdaptor.getColumns()); //Set data
						Settings.lock("View (New)"); //Lock for View (New)
						views.remove(view.getName()); //Remove old
						views.put(view.getName(), view); //Add new
						Settings.unlock("View (New)"); //Unlock for View (New)
						program.saveSettings("View (New)"); //Save View (New)
					}
				}
			});
			add(jMenuItem);

			JMenu jLoad = new JMenu(GuiShared.get().loadView());
			jLoad.setIcon(Images.FILTER_LOAD.getIcon());
			add(jLoad);

			JMenuItem jManage = new JMenuItem(GuiShared.get().editViews(), Images.DIALOG_SETTINGS.getIcon());
			jManage.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					Map<String, View> views = Settings.get().getTableViews(name);
					viewManager.updateData(views);
					viewManager.setVisible(true);
				}
			});

			if (!Settings.get().getTableViews(name).isEmpty()) {
				jLoad.setEnabled(true);

				jLoad.add(jManage);

				jLoad.addSeparator();

				for (final View view : Settings.get().getTableViews(name).values()) {
					jMenuItem = new JMenuItem(view.getName(), Images.FILTER_LOAD.getIcon());
					jMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							viewManager.loadView(view);
						}
					});
					jLoad.add(jMenuItem);
				}
			} else {
				jLoad.setEnabled(false);
			}

			addSeparator();
		}

		jMenuItem = new JMenuItem(GuiShared.get().tableColumnsReset(), Images.TABLE_COLUMN_SHOW.getIcon());
		jMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				tableFormatAdaptor.reset();
				tableModel.fireTableStructureChanged();
				jTable.autoResizeColumns();
				program.saveSettings("Columns (Reset)"); //Save Resize Mode
			}
		});
		add(jMenuItem);

		addSeparator();

		ButtonGroup buttonGroup = new ButtonGroup();
		JRadioButtonMenuItem jRadioButton;
		for (final ResizeMode mode : ResizeMode.values()) {
			jRadioButton = new JRadioButtonMenuItem(mode.toString(), Images.TABLE_COLUMN_RESIZE.getIcon());
			jRadioButton.setSelected(tableFormatAdaptor.getResizeMode() == mode);
				jRadioButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						tableFormatAdaptor.setResizeMode(mode);
						jTable.saveColumnsWidth();
						jTable.autoResizeColumns();
						program.updateTableMenu();
						program.saveSettings("Resize Mode"); //Save Resize Mode
					}
				});
			buttonGroup.add(jRadioButton);
			add(jRadioButton);
		}
	}
}
