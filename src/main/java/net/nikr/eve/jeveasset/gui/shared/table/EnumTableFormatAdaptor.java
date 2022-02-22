/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
import com.udojava.evalex.Expression;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.table.AbstractTableModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.JFormulaDialog;
import net.nikr.eve.jeveasset.gui.shared.menu.JFormulaDialog.Formula;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager.IndexColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.NumberValue;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileStock;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTableFormat;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Candle
 * @param <T>
 * @param <Q>
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

	private final static Object NULL_PLACEHOLDER = new Object();

	private final List<ColumnValueChangeListener> listeners = new ArrayList<>();

	private final Class<T> enumClass;
	private List<EnumTableColumn<Q>> shownColumns;
	private Map<String, EnumTableColumn<Q>> orderColumnsName;
	private List<EnumTableColumn<Q>> orderColumns;
	private List<EnumTableColumn<Q>> tempColumns = new ArrayList<>();
	private final ColumnComparator columnComparator;

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

	public T[] getEnumConstants() {
		return enumClass.getEnumConstants();
	}

	private void reset() {
		shownColumns = new ArrayList<>();
		orderColumnsName = new HashMap<>();
		for (T t : enumClass.getEnumConstants()) {
			if (t.isShowDefault()) {
				shownColumns.add(t);
			}
			orderColumnsName.put(t.name(), t);
		}
		orderColumns = new ArrayList<>(Arrays.asList(enumClass.getEnumConstants()));
		addTempColumns();
	}

	private void addTempColumns() {
		for (EnumTableColumn<Q> column : tempColumns) {
			addColumn(column, false);
		}
	}

	public void addColumn(EnumTableColumn<Q> column) {
		addColumn(column, true);
	}

	private void addColumn(EnumTableColumn<Q> column, boolean temp) {
		Integer index = null;
		if (column instanceof IndexColumn) {
			index = ((IndexColumn)column).getIndex();
		}
		if (!shownColumns.contains(column)) {
			orderColumnsName.put(column.name(), column);
			shownColumns.add(column);
			if (index != null && index >= 0 && index < orderColumns.size()) {
				orderColumns.add(index, column);
				updateColumns();
			} else {
				orderColumns.add(column);
			}
			if (temp) {
				tempColumns.add(column);
			}
		}
	}

	public void removeColumn(EnumTableColumn<Q> column) {
		orderColumnsName.remove(column.name());
		shownColumns.remove(column);
		orderColumns.remove(column);
		tempColumns.remove(column);
	}

	public List<EnumTableColumn<Q>> getShownColumns() {
		return shownColumns;
	}

	public List<EnumTableColumn<Q>> getOrderColumns() {
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
		orderColumns = new ArrayList<>();
		shownColumns = new ArrayList<>();
		orderColumnsName = new HashMap<>();
		List<T> originalColumns = new ArrayList<>(Arrays.asList(enumClass.getEnumConstants()));
		for (SimpleColumn column : columns) {
			try {
				T t = Enum.valueOf(enumClass, column.getEnumName());
				orderColumns.add(t);
				orderColumnsName.put(t.name(), t);
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
				if (index > orderColumns.size()) {
					index = orderColumns.size();
				}
				orderColumns.add(index, t);
				orderColumnsName.put(t.name(), t);
				if (t.isShowDefault()) {
					shownColumns.add(t);
				}
			}
		}
		addTempColumns();
		updateColumns();
	}

	public List<SimpleColumn> getColumns() {
		List<SimpleColumn> columns = new ArrayList<>(orderColumns.size());
		for (EnumTableColumn<Q> t : orderColumns) {
			if (tempColumns.contains(t)) { //Ignore temp columns
				continue;
			}
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
		EnumTableColumn<Q> fromColumn = getColumn(from);
		EnumTableColumn<Q> toColumn = getColumn(to);

		int fromIndex = orderColumns.indexOf(fromColumn);
		orderColumns.remove(fromIndex);

		int toIndex = orderColumns.indexOf(toColumn);
		if (to > from) {
			toIndex++;
		}
		orderColumns.add(toIndex, fromColumn);
		//Update IndexColumn
		for (int i = 0; i < orderColumns.size(); i++)  {
			EnumTableColumn<Q> column = orderColumns.get(i);
			if (column instanceof IndexColumn) {
				((IndexColumn)column).setIndex(i);
			}
		}
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
				editColumns = new EditColumnsDialog<>(program, this);
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
					viewSave.updateData(new ArrayList<>(views.values())); //Update views
					View view = viewSave.show();
					if (view != null ) { //Validate
						view.setColumns(getColumns()); //Set data
						if (views.containsValue(view)) { //Ovwewrite?
							int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), GuiShared.get().overwrite(), GuiShared.get().saveView(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
							if (value != JOptionPane.OK_OPTION) {
								return;
							}
						}
						Settings.lock("View (New)"); //Lock for View (New)
						views.remove(view.getName()); //Remove old
						views.put(view.getName(), view); //Add new
						Settings.unlock("View (New)"); //Unlock for View (New)
						program.saveSettings("View (New)"); //Save View (New)
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
				program.saveSettings("Columns (Reset)"); //Save Resize Mode
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
						program.saveSettings("Resize Mode"); //Save Resize Mode
					}
				});
			buttonGroup.add(jRadioButton);
			jMenu.add(jRadioButton);
		}
		return jMenu;
	}

	public void addListener(ColumnValueChangeListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ColumnValueChangeListener listener) {
		listeners.remove(listener);
	}

	private void notifyListeners() {
		for (ColumnValueChangeListener listener : listeners) {
			listener.columnValueChanged();
		}
	}

	private EnumTableColumn<Q> getColumn(final int i) throws IndexOutOfBoundsException {
		return getShownColumns().get(i);
	}

	private void updateColumns() {
		Collections.sort(shownColumns, columnComparator);
	}

	@Override public Class<?> getColumnClass(final int i) {
		try {
			return getColumn(i).getType();
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	@Override public Comparator<?> getColumnComparator(final int i) {
		try {
			return getColumn(i).getComparator();
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	@Override public int getColumnCount() {
		return getShownColumns().size();
	}

	@Override public String getColumnName(final int i) {
		try {
			return getColumn(i).getColumnName();
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	public String getColumnToolTip(final int i) {
		try {
			return getColumn(i).getColumnToolTip();
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	public EnumTableColumn<Q> valueOf(String column) {
		return orderColumnsName.get(column);
	}

	@Override public Object getColumnValue(final Q e, final int i) {
		try {
			return getColumnValue(e, getColumn(i));
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	public Object getColumnValue(final Q e, final String columnName) {
		return getColumnValue(e, orderColumnsName.get(columnName));
	}

	public Object getColumnValue(final Q e, final EnumTableColumn<Q> column) {
		if (column == null) { //Better safe than sorry
			return null;
		}
		Object object = column.getColumnValue(e);
		if (object instanceof Formula) {
			Formula formula = (Formula) object;
			Object value = formula.getValues().get(e);
			if (value == null) { //eval
				value = eval(formula, e);
				if (value == null) {
					value = NULL_PLACEHOLDER;
				}
				formula.getValues().put(e, value);
			}
			if (value.equals(NULL_PLACEHOLDER)) { //Handle NULL_PLACEHOLDER
				return null;
			}
			return value;
		} else {
			return object;
		}
	}

	private Object eval(Formula formula, Q e) {
		final Expression expression = formula.getExpression();
		//Populate variableColumns
		if (formula.getVariableColumns().isEmpty()) {
			for (T t : enumClass.getEnumConstants()) {
				if (formula.getUsedVariables().contains(JFormulaDialog.getHardName(t))) {
					formula.getVariableColumns().add(t.name());
				}
			}
		}
		//Set variables
		if (e instanceof StockpileTotal) {
			if (formula.isBoolean()) {
				return null;
			}
			StockpileTotal totalItem = (StockpileTotal) e;
			Map<Integer, StockpileItem> map = new HashMap<>();
			//Items
			for (StockpileItem item : totalItem.getStockpile().getItems()) {
				if (item.getTypeID() == 0) {
					continue;
				}
				map.put(item.getItemTypeID(), item);
			}
			//SubpileItem (Overwrites StockpileItem items)
			for (SubpileItem item : totalItem.getStockpile().getSubpileItems()) {
				if (item instanceof SubpileStock) {
					continue;
				}
				map.put(item.getItemTypeID(), item);
			}
			double total = 0.0;
			for (StockpileItem item : map.values()) {
				if (item.getItemTypeID() == 0) {
					continue;
				}
				setVariables(formula, StockpileTableFormat.values(), item);
				BigDecimal value = safeEval(expression);
				if (value != null) {
					total = total + value.doubleValue();
				}
			}
			return total;
		} else { //Default
			setVariables(formula, enumClass.getEnumConstants(), e);
			//Eval
			BigDecimal value = safeEval(expression);
			if (value == null) {
				return null;
			} else if (formula.isBoolean()) {
				return value.compareTo(BigDecimal.ZERO) > 0 ? "True" : "False";
			} else {
				return value.doubleValue();
			}
		}
	}

	public static BigDecimal safeEval(Expression expression) {
		try {
			return expression.eval();
		} catch (RuntimeException ex) {
			return null;
		}
	}

	private static <T extends Enum<T> & EnumTableColumn<Q>, Q> void setVariables(Formula formula, T[] enumColumns, Q e) {
		final Expression expression = formula.getExpression();
		for (T t : enumColumns) {
			if (!formula.getVariableColumns().contains(t.name())) {
				continue;
			}
			Number number = getValue(t, e);
			if (number != null) {
				expression.setVariable(JFormulaDialog.getHardName(t), new BigDecimal(number.toString()));
			}
		}
	}

	private static <T extends Enum<T> & EnumTableColumn<Q>, Q> Number getValue(T t, Q e) {
		if (Number.class.isAssignableFrom(t.getType())) {
			Number number = (Number) t.getColumnValue(e);
			if (number == null) { //Handle null
				return 0;
			}
			return number;
		} else if (NumberValue.class.isAssignableFrom(t.getType())) {
			NumberValue numberValue = (NumberValue) t.getColumnValue(e);
			if (numberValue == null) { //Handle null
				return 0;
			}
			Number number = numberValue.getNumber();
			if (number == null) { //Handle null
				return 0;
			}
			return number;
		}
		return null; //Not a valid numeric column
	}

	//Used by the JSeparatorTable
	@Override public boolean isEditable(final Q baseObject, final int i) {
		try {
			return getColumn(i).isColumnEditable(baseObject);
		} catch (IndexOutOfBoundsException ex) {
			return false;
		}
	}
	@Override public Q setColumnValue(final Q baseObject, final Object editedValue, final int i) {
		try {
			boolean changed = getColumn(i).setColumnValue(baseObject, editedValue);
			if (changed) {
				notifyListeners();
			}
		} catch (IndexOutOfBoundsException ex) {
			//No problem
		}
		return baseObject;
	}

	class ColumnComparator implements Comparator<EnumTableColumn<Q>> {

		@Override
		public int compare(final EnumTableColumn<Q> o1, final EnumTableColumn<Q> o2) {
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

	public static interface ColumnValueChangeListener {
		public void columnValueChanged();
	}
}
