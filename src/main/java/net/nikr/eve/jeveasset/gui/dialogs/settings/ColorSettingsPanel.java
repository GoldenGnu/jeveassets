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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ColorSettings.ColorRow;
import net.nikr.eve.jeveasset.data.settings.ColorSettings.PredefinedLookAndFeel;
import net.nikr.eve.jeveasset.data.settings.ColorTheme.ColorThemeTypes;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JSimpleColorPicker;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class ColorSettingsPanel extends JSettingsPanel {

	private final JCheckBoxMenuItem  jChartColor;
	private final JTextField jFilter;
	//Table
	private final JColorTable jTable;
	private final DefaultEventTableModel<ColorRow> tableModel;
	private final EventList<ColorRow> eventList;
	private final DefaultEventSelectionModel<ColorRow> selectionModel;
	private ColorThemeTypes colorThemeTypes;
	private List<ColorRow> colors;
	private String lookAndFeelClass;
	private boolean updateLock = false;
	private final List<JThemeMenuItem> jThemeMenuItems = new ArrayList<>();
	private final List<JLookAndFeelMenuItem> jLafMenuItems = new ArrayList<>();

	public ColorSettingsPanel(final Program program, final SettingsDialog settingsDialog) {
		super(program, settingsDialog, DialoguesSettings.get().colors(), Images.SETTINGS_COLORS.getIcon());

		JSimpleColorPicker jSimpleColorPicker = new JSimpleColorPicker(settingsDialog.getDialog());

		//Backend
		eventList = EventListManager.create();

		//Filter
		jFilter = new JTextField();
		MatcherEditor<ColorRow> matcherEditor = new TextComponentMatcherEditor<>(jFilter, new ColorRowTextFilterator());
		eventList.getReadWriteLock().readLock().lock();
		FilterList<ColorRow> filterList = new FilterList<>(eventList, matcherEditor);
		eventList.getReadWriteLock().readLock().unlock();

		//Separator
		SeparatorList<ColorRow> separatorList = new SeparatorList<>(filterList, new ColorRowSeparatorComparator(), 1, Integer.MAX_VALUE);

		//Table Model
		tableModel = EventModels.createTableModel(separatorList, TableFormatFactory.colorsTableFormat());
		//Table
		jTable = new JColorTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new ColorSeparatorTableCell(jTable, separatorList));
		jTable.setSeparatorEditor(new ColorSeparatorTableCell(jTable, separatorList));
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		jTable.addMouseListener(new MouseAdapter() {
			int lastShownRow = -1;
			int lastShownColumn = -1;
			boolean ignore = false;

			@Override
			public void mousePressed(MouseEvent e) {
				int row = jTable.rowAtPoint(e.getPoint());
				int column = jTable.columnAtPoint(e.getPoint());
				if (column == lastShownColumn && row == lastShownRow) {
					ignore = true;
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (ignore) {
					ignore = false;
					lastShownRow = -1;
					lastShownColumn = -1;
					return; //Ignore same cell click
				}
				int row = jTable.rowAtPoint(e.getPoint());
				int column = jTable.columnAtPoint(e.getPoint());

				Object object = tableModel.getElementAt(row);
				if (!(object instanceof ColorRow)) {
					return; //Ignore Separator
				}
				ColorRow colorRow = (ColorRow) object;

				String columnName = (String) jTable.getTableHeader().getColumnModel().getColumn(column).getHeaderValue();

				Rectangle cellRect = jTable.getCellRect(row, column, false);
				Point table = jTable.getLocationOnScreen();
				Point point = new Point(table.x + cellRect.x + cellRect.width, table.y + cellRect.y + cellRect.height);
				if (columnName.equals(ColorsTableFormat.BACKGROUND.getColumnName())) {
					if (!colorRow.getColorEntry().isBackgroundEditable()) {
						return;
					}
					jTable.startEditCell(row, column);
					jSimpleColorPicker.show(colorRow.getBackground(), colorRow.getDefaultBackground(), colorRow.getColorEntry().isBackgroundNullable(), point, new JSimpleColorPicker.ColorListenere() {
						@Override
						public void colorChanged(Color color) {
							colorRow.setBackground(color);
							jTable.stopEditCell();
						}

						@Override
						public void cancelled() {
							update(row, column);
							jTable.stopEditCell();
						}
					});
				}
				if (columnName.equals(ColorsTableFormat.FOREGROUND.getColumnName())) {
					if (!colorRow.getColorEntry().isForegroundEditable()) {
						return;
					}
					jTable.startEditCell(row, column);
					jSimpleColorPicker.show(colorRow.getForeground(), colorRow.getDefaultForeground(), colorRow.getColorEntry().isForegroundNullable(), point, new JSimpleColorPicker.ColorListenere() {
						@Override
						public void colorChanged(Color color) {
							colorRow.setForeground(color);
							jTable.stopEditCell();
						}

						@Override
						public void cancelled() {
							update(row, column);
							jTable.stopEditCell();
						}
					});
				}
			}

			private void update(int row, int column) {
				lastShownRow = row;
				lastShownColumn = column;
				tableModel.fireTableCellUpdated(row, 3);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						//In case we click outside the table
						lastShownRow = -1;
						lastShownColumn = -1;
					}
				});
			}
		});
		//Selection Model
		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);

		JScrollPane jTableScroll = new JScrollPane(jTable);

		JFixedToolBar jToolBar = new JFixedToolBar();

		JDropDownButton jSetting = new JDropDownButton(Images.DIALOG_SETTINGS.getIcon());
		jToolBar.addButtonIcon(jSetting);

		jChartColor = new JCheckBoxMenuItem (DialoguesSettings.get().chartColors());
		jSetting.add(jChartColor);

		jToolBar.addSeparator();

		JDropDownButton jLookAndFeel = new JDropDownButton(DialoguesSettings.get().lookAndFeel(), Images.FILTER_LOAD.getIcon());
		jToolBar.addButton(jLookAndFeel);

		ButtonGroup lafButtonGroup = new ButtonGroup();
		//Predefined LookAndFeels
		for (PredefinedLookAndFeel predefinedLookAndFeel : PredefinedLookAndFeel.values()) {
			JLookAndFeelMenuItem jMenuItem = new JLookAndFeelMenuItem(predefinedLookAndFeel.getLookAndFeelInfo(), jLookAndFeel, settingsDialog);
			jMenuItem.setSelected(predefinedLookAndFeel.isSelected());
			jLookAndFeel.add(jMenuItem);
			lafButtonGroup.add(jMenuItem);
			jLafMenuItems.add(jMenuItem);
		}
		//Installed LookAndFeels
		for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
			if (laf.getClassName().equals(UIManager.getSystemLookAndFeelClassName())) {
				continue; //Skip system LaF
			}
			JLookAndFeelMenuItem jMenuItem = new JLookAndFeelMenuItem(laf, jLookAndFeel, settingsDialog);
			jLookAndFeel.add(jMenuItem);
			lafButtonGroup.add(jMenuItem);
			jLafMenuItems.add(jMenuItem);
		}

		JDropDownButton jTheme = new JDropDownButton(DialoguesSettings.get().theme(), Images.FILTER_LOAD.getIcon());
		jToolBar.addButton(jTheme);

		ButtonGroup buttonGroup = new ButtonGroup();
		for (ColorThemeTypes theme : ColorThemeTypes.values()) {
			JThemeMenuItem jMenuItem = new JThemeMenuItem(theme);
			jTheme.add(jMenuItem);
			buttonGroup.add(jMenuItem);
			jThemeMenuItems.add(jMenuItem);
		}

		JButton jCollapse = new JButton(DialoguesSettings.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jTable.expandSeparators(false);
			}
		});
		jToolBar.addButton(jCollapse);

		JButton jExpand = new JButton(DialoguesSettings.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jTable.expandSeparators(true);
			}
		});
		jToolBar.addButton(jExpand);

		JFixedToolBar jToolBarSearch = new JFixedToolBar();

		jToolBarSearch.add(jFilter);

		JButton jClear = new JButton(Images.TAB_CLOSE.getIcon());
		jClear.setContentAreaFilled(false);
		jClear.setFocusPainted(false);
		jClear.setPressedIcon(Images.TAB_CLOSE_ACTIVE.getIcon());
		jClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jFilter.setText("");
			}
		});
		jToolBarSearch.add(jClear);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jToolBar, jToolBar.getMinimumSize().width, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(jToolBarSearch, jToolBarSearch.getMinimumSize().width, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(jTableScroll, 375, 375, Integer.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar ,GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jToolBarSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jTableScroll, 250, 250, Integer.MAX_VALUE)
		);
	}

	@Override
	public UpdateType save() {
		boolean lookAndfeelChanged = !Settings.get().getColorSettings().getLookAndFeelClass().equals(lookAndFeelClass);
		Settings.get().getColorSettings().setColorTheme(colorThemeTypes.getInstance(), true); //Later overwritten by table values, but, set uneditable values
		Settings.get().getColorSettings().setLookAndFeelClass(lookAndFeelClass);
		try {
			eventList.getReadWriteLock().readLock().lock();
			for (ColorRow row : eventList) {
				Settings.get().getColorSettings().set(row);
			}
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
		boolean easyChartColors = jChartColor.isSelected();
		boolean repaint = !Settings.get().getColorSettings().get().equals(colors)
						|| Settings.get().isEasyChartColors() != easyChartColors;
		Settings.get().setEasyChartColors(easyChartColors);
		colors = Settings.get().getColorSettings().get(); //Copy to check for changes on save
		if (lookAndfeelChanged && !UIManager.getLookAndFeel().getClass().getName().equals(lookAndFeelClass)) {
			JOptionPane.showMessageDialog(parent, DialoguesSettings.get().lookAndFeelMsg(), DialoguesSettings.get().lookAndFeelTitle(), JOptionPane.PLAIN_MESSAGE);
		}
		return repaint ? UpdateType.FULL_REPAINT : UpdateType.NONE;
	}

	@Override
	public void load() {
		updateLock = true;
		colors = Settings.get().getColorSettings().get(); //Copy to check for changes on save
		updateTable(Settings.get().getColorSettings().get()); //This copy will be edited
		colorThemeTypes = Settings.get().getColorSettings().getColorTheme().getType();
		jChartColor.setSelected(Settings.get().isEasyChartColors());
		select(colorThemeTypes);
		lookAndFeelClass = Settings.get().getColorSettings().getLookAndFeelClass();
		select(lookAndFeelClass);
		updateLock = false;
	}

	private void updateLookAndFeelClass(String lookAndFeelClass) {
		this.lookAndFeelClass = lookAndFeelClass;
	}

	private void updateTheme(ColorThemeTypes colorTheme) {
		List<ColorRow> rows = EventListManager.safeList(eventList);
		boolean overwrite;
		if (changed(rows)) {
			int value = JOptionPane.showConfirmDialog(parent, DialoguesSettings.get().overwriteMsg(), DialoguesSettings.get().overwriteTitle(), JOptionPane.YES_NO_CANCEL_OPTION);
			if (value == JOptionPane.CANCEL_OPTION) {
				updateLock = true;
				select(colorThemeTypes);
				updateLock = false;
				return;
			}
			overwrite = value == JOptionPane.YES_OPTION;
		} else {
			overwrite = true;
		}
		colorThemeTypes = colorTheme;
		updateTable(colorThemeTypes.getInstance().get(overwrite, rows));
	}

	public boolean changed(List<ColorRow> old) {
		for (ColorRow colorRow : old) {
			if (!colorRow.isBackgroundDefault() || !colorRow.isForegroundDefault()) {
				return true;
			}
		}
		return false;
	}

	private void select(ColorThemeTypes colorTheme) {
		for (JThemeMenuItem jThemeMenuItem : jThemeMenuItems) {
			if (jThemeMenuItem.getTheme().equals(colorTheme)) {
				jThemeMenuItem.setSelected(true);
				break;
			}
		}
	}

	private void select(String lookAndFeelClass) {
		for (JLookAndFeelMenuItem jLookAndFeelMenuItem : jLafMenuItems) {
			if (jLookAndFeelMenuItem.getLookAndFeelClass().equals(lookAndFeelClass)) {
				jLookAndFeelMenuItem.setSelected(true);
				break;
			}
		}
	}

	private void updateTable(List<ColorRow> rows) {
		jTable.lock();
		jTable.saveExpandedState();
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(rows);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		jTable.loadExpandedState();
		jTable.unlock();
	}

	public static class ColorRowSeparatorComparator implements Comparator<ColorRow> {
		@Override
		public int compare(final ColorRow o1, final ColorRow o2) {
			return o1.getColorEntry().getGroup().compareTo(o2.getColorEntry().getGroup());
		}
	}

	class ColorRowTextFilterator implements TextFilterator<ColorRow> {

		@Override
		public void getFilterStrings(List<String> baseList, ColorRow element) {
			baseList.add(element.getColorEntry().getDescription());
			baseList.add(element.getColorEntry().getGroup().getName());
		}
	}

	private final class JThemeMenuItem extends JRadioButtonMenuItem {
		private final ColorThemeTypes theme;

		public JThemeMenuItem(ColorThemeTypes theme) {
			super(theme.toString());
			this.theme = theme;
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (updateLock) {
						return;
					}
					updateTheme(theme);
				}
			});
		}

		public ColorThemeTypes getTheme() {
			return theme;
		}

	}

	private final class JLookAndFeelMenuItem extends JRadioButtonMenuItem {
		private final String lookAndFeelClass;

		public JLookAndFeelMenuItem(LookAndFeelInfo lookAndFeel, JDropDownButton jDropDownButton, final SettingsDialog settingsDialog) {
			super(lookAndFeel.getName());
			this.lookAndFeelClass = lookAndFeel.getClassName();
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (updateLock) {
						return;
					}
					updateLookAndFeelClass(lookAndFeelClass);
				}
			});
			LookAndFeelPreview.install(lookAndFeelClass, jDropDownButton, this, settingsDialog);
		}

		public String getLookAndFeelClass() {
			return lookAndFeelClass;
		}

	}
}
