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

package net.nikr.eve.jeveasset.gui.shared.table;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.table.AbstractTableModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Candle
 */
public class EnumTableFormatAdaptor<T extends Enum<T> & EnumTableColumn<Q>, Q> implements AdvancedTableFormat<Q>, WritableTableFormat<Q> {

	private static final  Logger LOG = LoggerFactory.getLogger(EnumTableFormatAdaptor.class);

	public static enum ResizeMode {
		TEXT {
			@Override String getI18N() {
				return GuiShared.get().tableResizeText();
			}
		},
		WINDOW {
			@Override String getI18N() {
				return GuiShared.get().tableResizeWindow();
			}
		},
		NONE {
			@Override String getI18N() {
				return GuiShared.get().tableResizeNone();
			}
		};

		abstract String getI18N();

		@Override
		public String toString() {
			return getI18N();
		}
	}

	private Class<T> enumClass;
	private List<T> shownColumns;
	private List<T> orderColumns;
	private ColumnComparator columnComparator;

	private EditColumnsDialog<T, Q> editColumns;
	private ViewSave viewSave;
	private ViewManager viewManager;
	private ResizeMode resizeMode;

	public EnumTableFormatAdaptor(final Class<T> enumClass) {
		this.enumClass = enumClass;
		columnComparator = new ColumnComparator();
		resizeMode = ResizeMode.TEXT;
		reset();
	}

	private void reset() {
		shownColumns = new ArrayList<T>();
		for (T t : enumClass.getEnumConstants()) {
			if (t.isShowDefault()) {
				shownColumns.add(t);
			}
		}
		orderColumns = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));
	}

	public List<T> getShownColumns() {
		return shownColumns;
	}

	public List<T> getOrderColumns() {
		return orderColumns;
	}

	public ResizeMode getResizeMode() {
		return resizeMode;
	}

	public void setResizeMode(final ResizeMode resizeMode) {
		if (resizeMode == null) {
			return;
		}
		this.resizeMode = resizeMode;
	}

	public void setColumns(final List<SimpleColumn> columns) {
		if (columns == null) {
			return;
		}
		orderColumns = new ArrayList<T>();
		shownColumns = new ArrayList<T>();
		List<T> originalColumns = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));
		for (SimpleColumn column : columns) {
			try {
				T t = Enum.valueOf(enumClass, column.getEnumName());
				orderColumns.add(t);
				if (column.isShown()) {
					shownColumns.add(t);
				}
			} catch (IllegalArgumentException ex) {
				LOG.info("Removing column: " + column.getEnumName());
			}
		}
		//Add new columns
		for (T t : originalColumns) {
			if (!orderColumns.contains(t)) {
				LOG.info("Adding column: " + t.getColumnName());
				int index = originalColumns.indexOf(t);
				if (index >= orderColumns.size()) {
					index = orderColumns.size() - 1;
				}
				orderColumns.add(index, t);
				if (t.isShowDefault()) {
					shownColumns.add(t);
				}
			}
		}
		updateColumns();
	}

	public List<SimpleColumn> getColumns() {
		List<SimpleColumn> columns = new ArrayList<SimpleColumn>(orderColumns.size());
		for (T t : orderColumns) {
			String columnName = t.getColumnName();
			String enumName = t.name();
			boolean shown = shownColumns.contains(t);
			columns.add(new SimpleColumn(enumName, columnName, shown));
		}
		return columns;
	}

	public void moveColumn(final int from, final int to) {
		if (from == to) {
			return;
		}
		T fromColumn = getColumn(from);
		T toColumn = getColumn(to);

		int fromIndex = orderColumns.indexOf(fromColumn);
		orderColumns.remove(fromIndex);

		int toIndex = orderColumns.indexOf(toColumn);
		if (to > from) {
			toIndex++;
		}
		orderColumns.add(toIndex, fromColumn);

		updateColumns();
	}

	public void hideColumn(final T column) {
		if (!shownColumns.contains(column)) {
			return;
		}
		shownColumns.remove(column);
		updateColumns();
	}

	public void showColumn(final T column) {
		if (shownColumns.contains(column)) {
			return;
		}
		shownColumns.add(column);
		updateColumns();
	}

	public JMenu getMenu(final Program program, final AbstractTableModel tableModel, final JAutoColumnTable jTable, final String name) {
		return getMenu(program, tableModel, jTable, name, true);
	}

	public JMenu getMenu(final Program program, final AbstractTableModel tableModel, final JAutoColumnTable jTable, final String name, final boolean editable) {
		JMenu jMenu;
		JMenuItem jMenuItem;
		jMenu = new JMenu(GuiShared.get().tableSettings());
		jMenu.setIcon(Images.TABLE_COLUMN_SHOW.getIcon());

		if (editable) {
			if (editColumns == null) { //Create dialog (only once)
				editColumns = new EditColumnsDialog<T, Q>(program, this);
			}
			if (viewSave == null) { //Create dialog (only once)
				viewSave = new ViewSave(program);
			}
			if (viewManager == null) { //Create dialog (only once)
				viewManager = new ViewManager(program, this, tableModel, jTable);
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
			jMenu.add(jMenuItem);

			jMenu.addSeparator();

			jMenuItem = new JMenuItem(GuiShared.get().saveView(), Images.FILTER_SAVE.getIcon());
			jMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					//Get views...
					Map<String, View> views = Settings.get().getTableViews(name);
					viewSave.updateData(new ArrayList<View>(views.values())); //Update views
					View view = viewSave.show();
					if (view != null ) { //Validate
						view.setColumns(getColumns()); //Set data
						if (views.containsValue(view)) { //Ovwewrite?
							int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), GuiShared.get().overwrite(), GuiShared.get().saveView(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
							if (value != JOptionPane.OK_OPTION) {
								return;
							}
						}
						views.remove(view.getName()); //Remove old
						views.put(view.getName(), view); //Add new
					}
				}
			});
			jMenu.add(jMenuItem);

			JMenu jLoad = new JMenu(GuiShared.get().loadView());
			jLoad.setIcon(Images.FILTER_LOAD.getIcon());
			jMenu.add(jLoad);

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

			jMenu.addSeparator();
		}

		jMenuItem = new JMenuItem(GuiShared.get().tableColumnsReset(), Images.TABLE_COLUMN_SHOW.getIcon());
		jMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				reset();
				tableModel.fireTableStructureChanged();
				jTable.autoResizeColumns();
			}
		});
		jMenu.add(jMenuItem);

		jMenu.addSeparator();

		ButtonGroup buttonGroup = new ButtonGroup();
		JRadioButtonMenuItem jRadioButton;
		for (final ResizeMode mode : ResizeMode.values()) {
			jRadioButton = new JRadioButtonMenuItem(mode.toString(), Images.TABLE_COLUMN_RESIZE.getIcon());
			jRadioButton.setSelected(resizeMode == mode);
				jRadioButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						setResizeMode(mode);
						jTable.saveColumnsWidth();
						jTable.autoResizeColumns();
						program.updateTableMenu();
					}
				});
			buttonGroup.add(jRadioButton);
			jMenu.add(jRadioButton);
		}
		return jMenu;
	}

	private T getColumn(final int i) {
		return shownColumns.get(i);
	}

	private void updateColumns() {
		Collections.sort(shownColumns, columnComparator);
	}

	@Override public Class<?> getColumnClass(final int i) {
		return getColumn(i).getType();
	}

	@Override public Comparator<?> getColumnComparator(final int i) {
		return getColumn(i).getComparator();
	}

	@Override public int getColumnCount() {
		return getShownColumns().size();
	}

	@Override public String getColumnName(final int i) {
		return getColumn(i).getColumnName();
	}

	@Override public Object getColumnValue(final Q e, final int i) {
		return getColumn(i).getColumnValue(e);
	}


	//Used by the JSeparatorTable
	@Override public boolean isEditable(final Q baseObject, final int i) {
		return getColumn(i).isColumnEditable(baseObject);
	}
	@Override public Q setColumnValue(final Q baseObject, final Object editedValue, final int i) {
		return getColumn(i).setColumnValue(baseObject, editedValue);
	}

	class ColumnComparator implements Comparator<T> {

		@Override
		public int compare(final T o1, final T o2) {
			return orderColumns.indexOf(o1) - orderColumns.indexOf(o2);
		}

	}

	public static class SimpleColumn {
		private final String enumName;
		private final String columnName;
		private boolean shown;

		public SimpleColumn(final String enumName, final boolean shown) {
			this.enumName = enumName;
			this.columnName = "";
			this.shown = shown;
		}

		public SimpleColumn(final String enumName, final String columnName, final boolean shown) {
			this.enumName = enumName;
			this.columnName = columnName;
			this.shown = shown;
		}

		public String getColumnName() {
			return columnName;
		}

		public String getEnumName() {
			return enumName;
		}

		public boolean isShown() {
			return shown;
		}

		public void setShown(final boolean shown) {
			this.shown = shown;
		}
	}
}
