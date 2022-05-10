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

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JFormulaDialog.Formula;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuJumps.Jump;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class ColumnManager<T extends Enum<T> & EnumTableColumn<Q>, Q> {

	private static final Map<String, ColumnManager<?, ?>> COLUMN_MANAGERS = new HashMap<>();
	private static final Set<String> loaded = new HashSet<>();
	private final Program program;
	private final String toolName;
	private final EnumTableFormatAdaptor<T, Q> tableFormat;
	private final DefaultEventTableModel<Q> tableModel;
	private final JAutoColumnTable jTable;
	private final FilterControl<Q> filterControl;

	private final Map<Formula, FormulaColumn<Q>> formulaColumns = new HashMap<>();
	private final Map<Jump, JumpColumn<Q>> jumpColumns = new HashMap<>();

	private boolean locked = false;

	public ColumnManager(Program program, String toolName, EnumTableFormatAdaptor<T, Q> tableFormat, DefaultEventTableModel<Q> tableModel, JAutoColumnTable jTable, EventList<Q> eventList, FilterControl<Q> filterControl) {
		this.program = program;
		this.toolName = toolName;
		this.tableFormat = tableFormat;
		this.tableModel = tableModel;
		this.jTable = jTable;
		this.filterControl = filterControl;
		//Load columns
		if (!loaded.contains(toolName)) {
			loaded.add(toolName);
			load();
		}
		//Reset cached Formula values on list changes
		eventList.addListEventListener(new ListEventListener<Q>() {
			@Override @SuppressWarnings("deprecation")
			public void listChanged(ListEvent<Q> listChanges) {
				if (locked) {
					return;
				}
				try {
					eventList.getReadWriteLock().readLock().lock();
					List<Q> reset = new ArrayList<>();
					//For each list event
					while(listChanges.next()) {
						switch (listChanges.getType()) {
							case ListEvent.DELETE:
								Q q = listChanges.getOldValue();
								if (q != null) {
									reset.add(q);
								}
								break;
							case ListEvent.UPDATE:
								int index = listChanges.getIndex();
								if (index >= 0 && index < eventList.size()) {
									reset.add(eventList.get(index));
								}
								break;
						}
					}
					//Remove changed values
					if (!reset.isEmpty()) {
						for (Formula formula : formulaColumns.keySet()) {
							formula.getValues().keySet().removeAll(reset);
						}
					}
				} finally {
					eventList.getReadWriteLock().readLock().unlock();
				}
			}
		});
		if (filterControl != null) {
			jTable.getColumnModel().addColumnModelListener( new TableColumnModelListener() {
				@Override
				public void columnAdded(TableColumnModelEvent e) {}

				@Override
				public void columnRemoved(TableColumnModelEvent e) {}

				@Override
				public void columnMoved(TableColumnModelEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							filterControl.updateColumns(false);
						}
					});
				}

				@Override
				public void columnMarginChanged(ChangeEvent e) { }

				@Override
				public void columnSelectionChanged(ListSelectionEvent e) { }
			});
		}
		COLUMN_MANAGERS.put(toolName, this);
	}

	public static ColumnManager<?, ?> getColumnManager(String toolName) {
		return COLUMN_MANAGERS.get(toolName);
	}

	public Set<Formula> getFormulas() {
		return formulaColumns.keySet();
	}

	public Set<Jump> getJumps() {
		return jumpColumns.keySet();
	}

	public T[] getEnumConstants() {
		return tableFormat.getEnumConstants();
	}

	private FormulaColumn<Q> get(Formula formula) {
		FormulaColumn<Q> column = formulaColumns.get(formula);
		if (column == null) { //Better save than sorry
			column = new FormulaColumn<>(formula);
		}
		return column;
	}

	private JumpColumn<Q> get(Jump jump) {
		JumpColumn<Q> column = jumpColumns.get(jump);
		if (column == null) { //Better save than sorry
			column = new JumpColumn<>(jump);
		}
		return column;
	}

	private void updateGUI() {
		tableModel.fireTableStructureChanged();
		jTable.autoResizeColumns();
		if (filterControl != null) {
			filterControl.updateColumns(true);
		}
	}

	private void remove(Formula remove) {
		tableFormat.removeColumn(get(remove));
		formulaColumns.remove(remove);
	}

	private void remove(Jump remove) {
		tableFormat.removeColumn(get(remove));
		jumpColumns.remove(remove);
	}

	private FormulaColumn<Q> add(Formula add) {
		FormulaColumn<Q> toColumn = get(add);
		tableFormat.addColumn(toColumn);
		formulaColumns.put(add, toColumn);
		return toColumn;
	}

	private JumpColumn<Q> add(Jump add) {
		JumpColumn<Q> toColumn = get(add);
		tableFormat.addColumn(toColumn);
		jumpColumns.put(add, toColumn);
		//Update Data
		updateJumpsData(add);
		return toColumn;
	}

	private boolean contains(Jump add) {
		return jumpColumns.containsKey(add);
	}

	public void editColumn(Formula remove, Formula add) {
		//Remove old
		remove(remove);
		//Add new
		add(add);
		//Update GUI
		updateGUI();
		//Settings
		Settings.lock("Formulas (Edit)");
		Settings.get().getTableFormulas(toolName).remove(remove);
		Settings.get().getTableFormulas(toolName).add(add);
		Settings.unlock("Formulas (Edit)");
		program.saveSettings("Formulas (Edit)");
	}

	public void removeColumn(Formula remove) {
		//Remove
		remove(remove);
		//Update GUI
		updateGUI();
		//Settings
		Settings.lock("Formulas (Remove)");
		Settings.get().getTableFormulas(toolName).remove(remove);
		Settings.unlock("Formulas (Remove)");
		program.saveSettings("Formulas (Remove)");
	}

	public void removeColumn(Jump remove) {
		//Remove old
		remove(remove);
		//Update GUI
		updateGUI(); //ToDo
		//Settings
		Settings.lock("Jumps (remove)");
		Settings.get().getTableJumps(toolName).remove(remove);
		Settings.unlock("Jumps (remove)");
		program.saveSettings("Jumps (remove)");
	}

	private void load() {
		//Add
		for (Formula add : Settings.get().getTableFormulas(toolName)) {
			add(add);
		}
		for (Jump add : Settings.get().getTableJumps(toolName)) {
			add(add);
		}
		//Update GUI
		updateGUI();
	}

	public FormulaColumn<Q> addColumn(Formula add) {
		//Add
		FormulaColumn<Q> column = add(add);
		//Update GUI
		updateGUI();
		//Settings
		Settings.lock("Formulas (add)");
		Settings.get().getTableFormulas(toolName).add(add);
		Settings.unlock("Formulas (add)");
		program.saveSettings("Formulas (add)");
		return column;
	}

	public JumpColumn<Q> addColumn(Jump add) {
		if (contains(add)) {
			return null; //Skip existing
		}
		//Add
		JumpColumn<Q> column = add(add);
		//Update GUI
		updateGUI();
		//Settings
		Settings.lock("Jumps (add)");
		Settings.get().getTableJumps(toolName).add(add);
		Settings.unlock("Jumps (add)");
		program.saveSettings("Jumps (add)");
		return column;
	}

	public void addColumns(Collection<MyLocation> locations) {
		//Convert
		List<Jump> jumps = new ArrayList<>();
		for (MyLocation location : locations) {
			Jump add = new Jump(location);
			if (contains(add)) {
				continue; //Skip existing
			}
			jumps.add(add);
		}
		//Add
		for (Jump add : jumps) {
			
			add(add);
		}
		//Update GUI
		updateGUI();
		//Settings
		Settings.lock("Jumps (add)");
		for (Jump add : jumps) {
			Settings.get().getTableJumps(toolName).add(add);
		}
		Settings.unlock("Jumps (add)");
		program.saveSettings("Jumps (add)");
	}

	public void clearJumpColumns() {
		//Remove
		for (Jump remove : Settings.get().getTableJumps(toolName)) {
			remove(remove);
		}
		//Update GUI
		updateGUI();
		//Settings
		Settings.lock("Jumps (clear)");
		Settings.get().getTableJumps(toolName).clear();
		Settings.unlock("Jumps (clear)");
		program.saveSettings("Jumps (clear)");
	}

	public void resetFormulaData() {
		for (Formula formula : formulaColumns.keySet()) {
			formula.getValues().clear(); //Reset calculations
		}
	}

	public void lock() {
		locked = true;
	}

	public void unlock() {
		locked = false;
	}

	public void updateJumpsData(Jump jump) {
		for (LocationType locationType : program.getMainTabs().get(toolName).getLocations()) {
			MyLocation location = locationType.getLocation();
			if (location == null) {
				continue;
			}
			long systemID = location.getSystemID();
			if (systemID <= 0) {
				continue;
			}
			jump.addJump(locationType);
		}
	}

	public void updateJumpsData() {
		for (LocationType locationType : program.getMainTabs().get(toolName).getLocations()) {
			MyLocation location = locationType.getLocation();
			if (location == null) {
				continue;
			}
			long systemID = location.getSystemID();
			if (systemID <= 0) {
				continue;
			}
			for (Jump jump : Settings.get().getTableJumps(toolName)) {
				jump.addJump(locationType);
			}
		}
	}

	public static interface IndexColumn<Q> extends EnumTableColumn<Q> {
		public Integer getIndex();
		public void setIndex(Integer index);
	}

	public static class FormulaColumn<Q> implements IndexColumn<Q> {

		private final Formula formula;

		public FormulaColumn(Formula formula) {
			this.formula = formula;
		}

		public Formula getFormula() {
			return formula;
		}

		@Override
		public Integer getIndex() {
			return formula.getIndex();
		}

		@Override
		public void setIndex(Integer index) {
			formula.setIndex(index);
		}

		@Override
		public Class<?> getType() {
			if (formula.isBoolean()) {
				return String.class;
			} else {
				return Double.class;
			}
		}

		@Override
		public Comparator<?> getComparator() {
			return GlazedLists.comparableComparator();
		}

		@Override
		public String getColumnName() {
			return formula.getColumnName();
		}

		@Override
		public Object getColumnValue(Q from) {
			return formula;
		}

		@Override
		public String name() {
			return formula.getColumnName();
		}

		@Override
		public boolean isColumnEditable(Object baseObject) {
			return false;
		}

		@Override
		public boolean isShowDefault() {
			return true;
		}

		@Override
		public boolean setColumnValue(Object baseObject, Object editedValue) {
			return false;
		}

		@Override
		public String toString() {
			return getColumnName();
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 37 * hash + Objects.hashCode(this.formula.getColumnName());
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
			final FormulaColumn<?> other = (FormulaColumn<?>) obj;
			if (!Objects.equals(this.formula.getColumnName(), other.formula.getColumnName())) {
				return false;
			}
			return true;
		}
	}

	public static class JumpColumn<Q> implements IndexColumn<Q> {

		private final Jump jump;

		public JumpColumn(Jump jump) {
			this.jump = jump;
		}

		public Jump getJump() {
			return jump;
		}

		@Override
		public Integer getIndex() {
			return jump.getIndex();
		}

		@Override
		public void setIndex(Integer index) {
			jump.setIndex(index);
		}

		@Override
		public Class<?> getType() {
			return Integer.class;
		}

		@Override
		public Comparator<?> getComparator() {
			return GlazedLists.comparableComparator();
		}

		@Override
		public String getColumnName() {
			return jump.getName();
		}

		@Override
		public String getColumnToolTip() {
			return GuiShared.get().jumpsColumnToolTip(jump.getName());
		}

		@Override
		public Object getColumnValue(Q from) {
			return jump.getJumps(from);
		}

		@Override
		public String name() {
			return jump.getName();
		}

		@Override
		public boolean isColumnEditable(Object baseObject) {
			return false;
		}

		@Override
		public boolean isShowDefault() {
			return true;
		}

		@Override
		public boolean setColumnValue(Object baseObject, Object editedValue) {
			return false;
		}

		@Override
		public String toString() {
			return getColumnName();
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 59 * hash + (int) (this.jump.getSystemID() ^ (this.jump.getSystemID() >>> 32));
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
			final JumpColumn<?> other = (JumpColumn<?>) obj;
			return this.jump.getSystemID() == other.jump.getSystemID();
		}
	}
}
