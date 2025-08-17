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
package net.nikr.eve.jeveasset.gui.tabs.skills;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MySkill;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.components.JAutoCompleteDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.i18n.TabsSkills;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class SkillsOverviewTab extends JMainTabSecondary {

	private enum SkillsOverviewAction {
		ADD_SKILL_PLAN,
		MANAGE_SKILL_PLANS,
	}

	private static final double[] RANK_1_LEVELS = new double[]{250, 1415, 8000, 45255, 256000};
	private static final String SKILL_PLAN_EXAMPLE =
			"Power Grid Management 1\n" +
			"Science 1\n" +
			"Gunnery 1\n" +
			"Navigation 1\n" +
			"Spaceship Command 1\n" +
			"CPU Management 1\n" +
			"Drones 1\n" +
			"Small Hybrid Turret 1\n" +
			"Afterburner 1\n" +
			"Drone Avionics 1\n" +
			"Gallente Frigate 1\n" +
			"Power Grid Management 2\n" +
			"CPU Management 2\n" +
			"Drones 2\n" +
			"Gunnery 2\n" +
			"Drone Avionics 2\n" +
			"Weapon Upgrades 1\n" +
			"Shield Upgrades 1\n" +
			"Gallente Frigate 2\n" +
			"Electronics Upgrades 1\n" +
			"Shield Upgrades 2\n" +
			"Power Grid Management 3\n" +
			"Drones 3\n" +
			"Drone Avionics 3\n" +
			"Tactical Shield Manipulation 1\n" +
			"Gallente Frigate 3\n";

	private final JFixedToolBar jToolBar;
	private final JAutoColumnTable jTable;

	//Dialog
	private final JSkillPlansManageDialog jSkillPlansManageDialog;
	private final JTextDialog jTextDialog;
	private final JAutoCompleteDialog<String> jSaveDialog;

	private final SkillsOverviewFilterControl filterControl;
	private final DefaultEventTableModel<SkillsOverview> tableModel;
	private final EventList<SkillsOverview> eventList;
	private final FilterList<SkillsOverview> filterList;
	private final EnumTableFormatAdaptor<SkillsOverviewTableFormat, SkillsOverview> tableFormat;
	private final List<EnumTableColumn<SkillsOverview>> dynamicColumns = new ArrayList<>();
	private final DefaultEventSelectionModel<SkillsOverview> selectionModel;

	public static final String NAME = "skillsoverview";

	public SkillsOverviewTab(Program program) {
		super(program, NAME, TabsSkills.get().skillsOverview(), Images.TOOL_SKILLS.getIcon(), true);

		jSkillPlansManageDialog = new JSkillPlansManageDialog(program, this);
		jTextDialog = new JTextDialog(program.getMainWindow().getFrame());
		jSaveDialog = new JAutoCompleteDialog<>(program, TabsSkills.get().add(), Images.TOOL_SKILLS.getImage(), TabsSkills.get().enterName(), false, true, JAutoCompleteDialog.STRING_OPTIONS);
		final ListenerClass listener = new ListenerClass();

		jToolBar = new JFixedToolBar();

		JDropDownButton jSkills = new JDropDownButton(TabsSkills.get().skillPlans(), Images.TOOL_SKILLS.getIcon());
		jToolBar.addButton(jSkills);

		JMenuItem jManage = new JMenuItem(TabsSkills.get().manage(), Images.DIALOG_SETTINGS.getIcon());
		jManage.setActionCommand(SkillsOverviewAction.MANAGE_SKILL_PLANS.name());
		jManage.addActionListener(listener);
		jSkills.add(jManage);

		jSkills.addSeparator();

		JMenuItem jAdd = new JMenuItem(TabsSkills.get().add(), Images.EDIT_ADD.getIcon());
		jAdd.setActionCommand(SkillsOverviewAction.ADD_SKILL_PLAN.name());
		jAdd.addActionListener(listener);
		jSkills.add(jAdd);

		//Table Format
		tableFormat = TableFormatFactory.skillsOverviewTableFormat();

		//Backend
		eventList = EventListManager.create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<SkillsOverview> sortedColumns = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Sorting (total)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<SkillsOverview> sorted = new SortedList<>(sortedColumns, new TotalComparator());
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(sorted);
		eventList.getReadWriteLock().readLock().unlock();

		//Table Model
		tableModel = EventModels.createTableModel(filterList, tableFormat);
		//Table
		UIDefaults uiDefaults = UIManager.getLookAndFeelDefaults();
		Font font = uiDefaults.getFont("ToolTip.font");
		if (font == null) {
			font = new JTable().getFont(); //Better safe, than sorry...
		}
		FontMetrics fontMetrics = new Canvas().getFontMetrics(font);
		double maxHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		int lineHeight = fontMetrics.getHeight();
		jTable = new JAutoColumnTable(program, tableModel) {
			@Override
			public String getToolTipText(java.awt.event.MouseEvent e) {
				int row = rowAtPoint(e.getPoint());
				int column = columnAtPoint(e.getPoint());
				if (row < 0 || column < 0) {
					return null;
				}
				int modelColumn = convertColumnIndexToModel(column);
				String columnName = tableModel.getColumnName(modelColumn);
				Map<String, Map<Integer, Integer>> plans = Settings.get().getSkillPlans();
				if (!plans.containsKey(columnName)) {
					return null;
				}
				try {
					SkillsOverview skillsOverview = filterList.get(row);
					Map<Integer, Long> have = skillsOverview.getOwnerSkillPoints();
					Map<Integer, Integer> plan = plans.get(columnName);
					StringBuilder builder = new StringBuilder();
					int missing = 0;
					for (Map.Entry<Integer, Integer> required : plan.entrySet()) {
						int typeID = required.getKey();
						int targetLevel = Math.max(1, Math.min(5, required.getValue()));
						double targetSkillPoints = skillPointsForLevel(typeID, targetLevel);
						long currentSkillPoints = have.getOrDefault(typeID, 0L);
						if (currentSkillPoints < targetSkillPoints) {
							missing++;
							String name;
							name = ApiIdConverter.getItem(typeID).getTypeName();
							int approximateLevel = approximateLevelFromSkillPoints(currentSkillPoints);
							String percent = Formatter.percentFormat(Math.min(1.0, currentSkillPoints / Math.max(1.0, targetSkillPoints)));
							builder.append(TabsSkills.get().tableToolTipSkill(name, approximateLevel, targetLevel, percent));
							if (lineHeight * (missing + 3) > maxHeight) {
								builder.append(TabsSkills.get().tableToolTipTruncated());
								break;
							}
						}
					}
					if (missing == 0) {
						return TabsSkills.get().tableToolTipCompleted();
					}
					return TabsSkills.get().tableToolTipMissing(missing) + builder.toString().trim();
				} catch (Throwable t) {
					return null;
				}
			}
		};
		jTable.setToolTipText("");
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		//Sorting
		TableComparatorChooser<SkillsOverview> comparatorChooser = TableComparatorChooser.install(jTable, sortedColumns, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(filterList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);

		//Listeners
		installTable(jTable);
		//Scroll
		JScrollPane jScrollPane = new JScrollPane(jTable);
		//Table Filter
		filterControl = new SkillsOverviewFilterControl(sorted);
		//Menu
		installTableTool(new SkillsOverviewTableMenu(), tableFormat, comparatorChooser, tableModel, jTable, eventList, SkillsOverview.class);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar, jToolBar.getMinimumSize().width, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(jScrollPane, 0, 0, Short.MAX_VALUE));
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jScrollPane, 0, 0, Short.MAX_VALUE));
	}

	@Override
	public void updateData() {
		List<SkillsOverview> skillsOverviews = new ArrayList<>();
		for (EnumTableColumn<SkillsOverview> column : new ArrayList<>(dynamicColumns)) {
			tableFormat.removeColumn(column);
		}
		dynamicColumns.clear();
		for (String planName : Settings.get().getSkillPlans().keySet()) {
			PlanColumn column = new PlanColumn(planName);
			tableFormat.addColumn(column);
			dynamicColumns.add(column);
		}
		tableModel.fireTableStructureChanged();
		filterControl.updateColumns(true);
		Map<String, Map<Integer, Integer>> plans = Settings.get().getSkillPlans();
		Map<String, Map<Integer, Long>> ownerSkillPoints = new LinkedHashMap<>();
		try {
			program.getProfileData().getSkillsEventList().getReadWriteLock().readLock().lock();
			for (MySkill skill : program.getProfileData().getSkillsEventList()) {
				Map<Integer, Long> map = ownerSkillPoints.computeIfAbsent(skill.getOwnerName(), k -> new LinkedHashMap<>());
				map.put(skill.getTypeID(), skill.getSkillpoints());
			}
		} finally {
			program.getProfileData().getSkillsEventList().getReadWriteLock().readLock().unlock();
		}
		for (Map.Entry<String, Map<Integer, Long>> entry : ownerSkillPoints.entrySet()) {
			SkillsOverview skillsOverview = new SkillsOverview(entry.getKey());
			skillsOverview.setOwnerSkillPoints(entry.getValue());
			// Populate Total SP and Unallocated SP for this character
			OwnerType ownerType = null;
			for (OwnerType owner : program.getOwnerTypes()) {
				if (owner.getOwnerName().equals(entry.getKey())) {
					ownerType = owner;
					break;
				}
			}
			if (ownerType != null) {
				skillsOverview.setTotalSkillPoints(ownerType.getTotalSkillPoints());
				skillsOverview.setUnallocatedSkillPoints(ownerType.getUnallocatedSkillPoints());
			}
			for (Map.Entry<String, Map<Integer, Integer>> plan : plans.entrySet()) {
				double planPercent = computePlanPercent(entry.getValue(), plan.getValue());
				skillsOverview.planToPercent.put(plan.getKey(), Percent.create(planPercent));
			}
			skillsOverviews.add(skillsOverview);
		}
		SkillsOverview total = new SkillsOverview(TabsSkills.get().total());
		for (String plan : plans.keySet()) {
			double sum = 0;
			int count = 0;
			for (SkillsOverview skillsOverview : skillsOverviews) {
				Percent p = skillsOverview.planToPercent.get(plan);
				if (p != null) {
					sum += p.getDouble();
					count++;
				}
			}
			total.planToPercent.put(plan, Percent.create(count == 0 ? 0 : (sum / 100.0 / count)));
		}
		skillsOverviews.add(total);
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(skillsOverviews);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	private static class TotalComparator implements Comparator<SkillsOverview> {

		@Override
		public int compare(SkillsOverview a, SkillsOverview b) {
			if (a.isTotal() && b.isTotal()) {
				return 0;
			} else if (a.isTotal()) {
				return 1;
			} else if (b.isTotal()) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	private double computePlanPercent(Map<Integer, Long> ownerSkillSp, Map<Integer, Integer> plan) {
		double have = 0;
		double need = 0;
		for (Map.Entry<Integer, Integer> required : plan.entrySet()) {
			int typeID = required.getKey();
			int level = Math.max(1, Math.min(5, required.getValue()));
			double targetSkillPoints = skillPointsForLevel(typeID, level);
			long currentSkillPoints = ownerSkillSp.getOrDefault(typeID, 0L);
			have += Math.min(currentSkillPoints, targetSkillPoints);
			need += targetSkillPoints;
		}
		if (need <= 0) {
			return 0;
		}
		return have / need;
	}

	private static int approximateLevelFromSkillPoints(long currentSkillPoints) {
		for (int i = 5; i >= 1; i--) {
			if (currentSkillPoints >= RANK_1_LEVELS[i - 1]) {
				return i;
			}
		}
		return 0;
	}

	private double skillPointsForLevel(int typeID, int level) {
		double rank = 1.0;
		ApiIdConverter.getItem(typeID);

		return RANK_1_LEVELS[level - 1] * rank;
	}

	public Map<Integer, Integer> importSkillPlan(String text) {
		String importText = jTextDialog.importText(text, SKILL_PLAN_EXAMPLE);
		if (importText == null) {
			return null;
		}
		//Lookup Table
		Map<String, Integer> names = new HashMap<>();
		for (Item item : StaticData.get().getItems().values()) {
			names.put(item.getTypeName().toLowerCase(), item.getTypeID());
		}

		String[] lines = importText.split("[\r\n]+");
		Map<Integer, Integer> skills = new LinkedHashMap<>();
		for (String line : lines) {
			String trimmed = line.trim();
			if (trimmed.isEmpty()) {
				continue;
			}
			int space = lastSpaceIndex(trimmed);
			if (space <= 0 || space == trimmed.length() - 1) {
				continue;
			}
			String skillName = trimmed.substring(0, space).trim();
			String levelStr = trimmed.substring(space + 1).trim();
			int level;
			try {
				level = Integer.parseInt(levelStr);
			} catch (NumberFormatException ex) {
				continue;
			}
			level = Math.max(1, Math.min(5, level));
			Integer typeID = names.get(skillName.toLowerCase());
			if (typeID == null || typeID == 0) {
				continue;
			}
			skills.put(typeID, level);
		}
		if (skills.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsSkills.get().noValidSkills (), TabsSkills.get().add(), JOptionPane.WARNING_MESSAGE);
			return importSkillPlan(text);
		}
		return skills;
	}

	public void newSkillPlan(Map<Integer, Integer> skills) {
		if (skills == null || skills.isEmpty()) {
			return;
		}
		jSaveDialog.updateData(Settings.get().getSkillPlans().keySet());
		String name = jSaveDialog.show();
		if (name == null) {
			return; //Cancel
		}
		Settings.lock("Skills Overview (New Plan)");
		Settings.get().getSkillPlans().put(name, skills);
		Settings.unlock("Skills Overview (New Plan)");
		program.saveSettings("Skills Overview (New Plan)");
		updateData();
	}

	private static int lastSpaceIndex(String s) {
		for (int i = s.length() - 1; i >= 0; i--) {
			if (Character.isWhitespace(s.charAt(i)))
				return i;
		}
		return -1;
	}

	@Override
	public void clearData() {
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		filterControl.clearCache();
	}

	@Override
	public void updateCache() {
		filterControl.createCache();
	}

	@Override
	public Collection<LocationType> getLocations() {
		return new ArrayList<>();
	}

	public class SkillsOverview implements Comparable<SkillsOverview> {

		private final String owner;
		private final Map<String, Percent> planToPercent = new LinkedHashMap<>();
		private Long totalSkillsPoints;
		private Integer unallocatedSkillsPoints;
		private Map<Integer, Long> ownerSkillPoints;

		public SkillsOverview(String owner) {
			this.owner = owner;
		}

		public String getOwner() {
			return owner;
		}

		public Percent getPercent(String plan) {
			return planToPercent.getOrDefault(plan, Percent.create(0));
		}

		public void setOwnerSkillPoints(Map<Integer, Long> ownerSkillPoints) {
			this.ownerSkillPoints = ownerSkillPoints;
		}

		public Map<Integer, Long> getOwnerSkillPoints() {
			return ownerSkillPoints != null ? ownerSkillPoints : new LinkedHashMap<>();
		}

		public void setTotalSkillPoints(Long totalSkillsPoints) {
			this.totalSkillsPoints = totalSkillsPoints;
		}

		public Long getTotalSkillPoints() {
			return totalSkillsPoints;
		}

		public void setUnallocatedSkillPoints(Integer unallocatedSkillsPoints) {
			this.unallocatedSkillsPoints = unallocatedSkillsPoints;
		}

		public Integer getUnallocatedSkillPoints() {
			return unallocatedSkillsPoints;
		}

		public boolean isTotal() {
			return owner.equals(TabsSkills.get().total());
		}

		@Override
		public int compareTo(SkillsOverview o) {
			return 0;
		}
	}

	private static class PlanColumn implements EnumTableColumn<SkillsOverview> {

		private final String plan;

		PlanColumn(String plan) {
			this.plan = plan;
		}

		@Override
		public Class<?> getType() {
			return Percent.class;
		}

		@Override
		public Comparator<?> getComparator() {
			return EnumTableColumn.getComparator(Percent.class);
		}

		@Override
		public String getColumnName() {
			return plan;
		}

		@Override
		public Object getColumnValue(SkillsOverview from) {
			return from.getPercent(plan);
		}

		@Override
		public String toString() {
			return getColumnName();
		}

		@Override
		public String name() {
			return plan;
		}

		@Override
		public boolean isShowDefault() {
			return true;
		}
	}

	private class SkillsOverviewTableMenu implements TableMenu<SkillsOverview> {

		@Override
		public MenuData<SkillsOverview> getMenuData() {
			return new MenuData<>(selectionModel.getSelected());
		}

		@Override
		public JMenu getFilterMenu() {
			return filterControl.getMenu(jTable, selectionModel.getSelected());
		}

		@Override
		public JMenu getColumnMenu() {
			return new JMenuColumns<>(program, tableFormat, tableModel, jTable, NAME);
		}

		@Override
		public void addInfoMenu(JPopupMenu jPopupMenu) { }

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class SkillsOverviewFilterControl extends FilterControl<SkillsOverview> {

		public SkillsOverviewFilterControl(EventList<SkillsOverview> exportEventList) {
			super(program.getMainWindow().getFrame(), NAME, tableFormat, eventList, exportEventList, filterList);
		}

		@Override
		public void saveSettings(final String msg) {
			program.saveSettings("Skills Overview Table: " + msg);
		}
	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (SkillsOverviewAction.ADD_SKILL_PLAN.name().equals(e.getActionCommand())) {
				Map<Integer, Integer> skills = importSkillPlan("");
				newSkillPlan(skills);
			} else if (SkillsOverviewAction.MANAGE_SKILL_PLANS.name().equals(e.getActionCommand())) {
				jSkillPlansManageDialog.setVisible(true);
			}
		}
	}
}
