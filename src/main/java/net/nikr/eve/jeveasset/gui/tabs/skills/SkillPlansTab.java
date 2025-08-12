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
 */

package net.nikr.eve.jeveasset.gui.tabs.skills;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MySkill;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillPlansTab extends JMainTabSecondary {

    public static final String NAME = "skillplans";
    private static final Logger LOG = LoggerFactory.getLogger(SkillPlansTab.class);

    // GUI
    private final JAutoColumnTable jTable;

    // Table
    private final SkillPlansFilterControl filterControl;
    private final DefaultEventTableModel<Row> tableModel;
    private final EventList<Row> eventList;
    private final FilterList<Row> filterList;
    private final EnumTableFormatAdaptor<SkillPlansTableFormat, Row> tableFormat;
    private final List<EnumTableColumn<Row>> dynamicColumns = new ArrayList<>();
    private final DefaultEventSelectionModel<Row> selectionModel;

    public SkillPlansTab(Program program) {
        super(program, NAME, TabsSkills.get().skills() + " - Plans", Images.TOOL_SKILLS.getIcon(), true);

        tableFormat = TableFormatFactory.create(SkillPlansTableFormat.class);
        eventList = EventListManager.create();
        eventList.getReadWriteLock().readLock().lock();
        // Use a non-comparing comparator initially to avoid ClassCastException for
        // rows that are not Comparable. Actual column sorting is managed by
        // TableComparatorChooser after installation.
        SortedList<Row> sortedColumns = new SortedList<>(eventList, new Comparator<Row>() {
            @Override
            public int compare(Row o1, Row o2) {
                return 0;
            }
        });
        eventList.getReadWriteLock().readLock().unlock();
        eventList.getReadWriteLock().readLock().lock();
        SortedList<Row> sorted = new SortedList<>(sortedColumns, new TotalComparator());
        eventList.getReadWriteLock().readLock().unlock();
        filterList = new FilterList<>(sorted);
        tableModel = EventModels.createTableModel(filterList, tableFormat);
        jTable = new JAutoColumnTable(program, tableModel) {
            @Override
            public String getToolTipText(java.awt.event.MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                if (row < 0 || col < 0)
                    return null;
                int modelCol = convertColumnIndexToModel(col);
                String columnName = tableModel.getColumnName(modelCol);
                Map<String, Map<Integer, Integer>> plans = Settings.get().getSkillPlans();
                if (!plans.containsKey(columnName)) {
                    return null; // Only show tooltip for plan columns
                }
                try {
                    Row r = filterList.get(row);
                    Map<Integer, Long> have = r.getOwnerSkillSp();
                    Map<Integer, Integer> plan = plans.get(columnName);
                    StringBuilder sb = new StringBuilder();
                    int missing = 0;
                    for (Map.Entry<Integer, Integer> req : plan.entrySet()) {
                        int typeId = req.getKey();
                        int targetLevel = Math.max(1, Math.min(5, req.getValue()));
                        double targetSp = spForLevel(typeId, targetLevel);
                        long currentSp = have.getOrDefault(typeId, 0L);
                        if (currentSp < targetSp) {
                            missing++;
                            String name;
                            try {
                                name = net.nikr.eve.jeveasset.io.shared.ApiIdConverter.getItem(typeId).getTypeName();
                            } catch (Exception ex) {
                                name = String.valueOf(typeId);
                            }
                            int approxLevel = approximateLevelFromSp(currentSp);
                            sb.append(name).append(": ")
                                    .append("L").append(approxLevel).append(" → L").append(targetLevel)
                                    .append(" (")
                                    .append(net.nikr.eve.jeveasset.gui.shared.Formatter
                                            .percentFormat(Math.min(1.0, currentSp / Math.max(1.0, targetSp))))
                                    .append(")")
                                    .append("\n");
                            if (sb.length() > 800) { // keep tooltip short
                                sb.append("…");
                                break;
                            }
                        }
                    }
                    if (missing == 0) {
                        return "All skills complete for this plan.";
                    }
                    return "Missing skills (" + missing + "):\n" + sb.toString().trim();
                } catch (Throwable t) {
                    return null;
                }
            }
        };
        jTable.setToolTipText("");
        jTable.setCellSelectionEnabled(true);
        jTable.setRowSelectionAllowed(true);
        jTable.setColumnSelectionAllowed(true);
        TableComparatorChooser.install(jTable, sortedColumns, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE,
                tableFormat);
        selectionModel = EventModels.createSelectionModel(filterList);
        selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
        jTable.setSelectionModel(selectionModel);
        installTable(jTable);
        JScrollPane scrollPane = new JScrollPane(jTable);
        filterControl = new SkillPlansFilterControl(sorted);
        installTableTool(new SkillPlansTableMenu(), tableFormat, tableModel, jTable, filterControl, Row.class);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(filterControl.getPanel())
                        .addComponent(scrollPane, 0, 0, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(filterControl.getPanel())
                        .addComponent(scrollPane, 0, 0, Short.MAX_VALUE));
    }

    @Override
    public void updateData() {
        try {
            eventList.getReadWriteLock().writeLock().lock();
            eventList.clear();
            // Rebuild dynamic plan columns from settings to avoid duplicates
            for (EnumTableColumn<Row> col : new ArrayList<>(dynamicColumns)) {
                tableFormat.removeColumn(col);
            }
            dynamicColumns.clear();
            for (String planName : Settings.get().getSkillPlans().keySet()) {
                PlanColumn col = new PlanColumn(planName);
                tableFormat.addColumn(col);
                dynamicColumns.add(col);
            }
            tableModel.fireTableStructureChanged();
            filterControl.updateColumns(true);
            Map<String, Map<Integer, Integer>> plans = Settings.get().getSkillPlans();
            // Build per owner rows
            Map<String, Map<Integer, Long>> ownerSp = new LinkedHashMap<>();
            program.getProfileData().getSkillsEventList().getReadWriteLock().readLock().lock();
            try {
                for (MySkill s : program.getProfileData().getSkillsEventList()) {
                    Map<Integer, Long> map = ownerSp.computeIfAbsent(s.getOwnerName(), k -> new LinkedHashMap<>());
                    map.put(s.getTypeID(), s.getSkillpoints());
                }
            } finally {
                program.getProfileData().getSkillsEventList().getReadWriteLock().readLock().unlock();
            }
            for (Map.Entry<String, Map<Integer, Long>> entry : ownerSp.entrySet()) {
                Row row = new Row(entry.getKey());
                row.setOwnerSkillSp(entry.getValue());
                for (Map.Entry<String, Map<Integer, Integer>> plan : plans.entrySet()) {
                    double pct = computePlanPercent(entry.getValue(), plan.getValue());
                    row.planToPercent.put(plan.getKey(), Percent.create(pct));
                }
                eventList.add(row);
            }
            // Optional grand total row
            Row total = new Row("Total");
            for (String plan : plans.keySet()) {
                double sum = 0;
                int count = 0;
                for (Row r : eventList) {
                    Percent p = r.planToPercent.get(plan);
                    if (p != null) {
                        sum += p.getDouble();
                        count++;
                    }
                }
                total.planToPercent.put(plan, Percent.create(count == 0 ? 0 : (sum / 100.0 / count)));
            }
            eventList.add(total);
        } catch (Throwable t) {
            LOG.error("SkillPlansTab.updateData failed: {}", t.getMessage(), t);
            javax.swing.JOptionPane.showMessageDialog(program.getMainWindow().getFrame(),
                    "Failed to build Skill Plans table. See log for details.",
                    "Skill Plans", javax.swing.JOptionPane.ERROR_MESSAGE);
        } finally {
            eventList.getReadWriteLock().writeLock().unlock();
        }
    }

    // (no dynamic adaptor rebuild; columns dialog will only show static enum
    // columns)

    private static class TotalComparator implements Comparator<Row> {
        @Override
        public int compare(Row a, Row b) {
            // Only enforce that the "Total" row is last. Do not impose any
            // ordering for normal rows so column sorting can take effect.
            boolean aTotal = "Total".equals(a.owner);
            boolean bTotal = "Total".equals(b.owner);
            if (aTotal && bTotal)
                return 0;
            if (aTotal)
                return 1; // after others
            if (bTotal)
                return -1; // before total
            return 0; // leave ordering to TableComparatorChooser
        }
    }

    private double computePlanPercent(Map<Integer, Long> ownerSkillSp, Map<Integer, Integer> plan) {
        double have = 0;
        double need = 0;
        for (Map.Entry<Integer, Integer> req : plan.entrySet()) {
            int typeId = req.getKey();
            int level = Math.max(1, Math.min(5, req.getValue()));
            double targetSp = spForLevel(typeId, level);
            long current = ownerSkillSp.getOrDefault(typeId, 0L);
            have += Math.min(current, targetSp);
            need += targetSp;
        }
        if (need <= 0)
            return 0;
        return have / need; // decimal percent for Percent.create
    }

    private static int approximateLevelFromSp(long currentSp) {
        double[] rank1Levels = new double[] { 250, 1415, 8000, 45255, 256000 };
        for (int i = 5; i >= 1; i--) {
            if (currentSp >= rank1Levels[i - 1])
                return i;
        }
        return 0;
    }

    // Approximate SP thresholds by rank: SP to each level = multiplier * base
    // table.
    // If rank is unknown, assume rank 1.
    private double spForLevel(int typeId, int level) {
        double rank = 1.0;
        ApiIdConverter.getItem(typeId); // ensure item cached; rank not available here
        // Item metadata does not expose rank in current model; use 1.0 as reasonable
        // default.
        double[] rank1Levels = new double[] { 250, 1415, 8000, 45255, 256000 };
        return rank1Levels[level - 1] * rank;
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

    public static class Row {
        private final String owner;
        private final Map<String, Percent> planToPercent = new LinkedHashMap<>();

        public Row(String owner) {
            this.owner = owner;
        }

        public String getOwner() {
            return owner;
        }

        public Percent getPercent(String plan) {
            return planToPercent.get(plan);
        }

        private Map<Integer, Long> ownerSkillSp;

        public void setOwnerSkillSp(Map<Integer, Long> ownerSkillSp) {
            this.ownerSkillSp = ownerSkillSp;
        }

        public Map<Integer, Long> getOwnerSkillSp() {
            return ownerSkillSp != null ? ownerSkillSp : new LinkedHashMap<Integer, Long>();
        }
    }

    public enum SkillPlansTableFormat implements EnumTableColumn<Row> {
        OWNER(String.class) {
            @Override
            public String getColumnName() {
                return TabsSkills.get().columnCharacter();
            }

            @Override
            public Object getColumnValue(Row from) {
                return from.getOwner();
            }
        };

        private final Class<?> type;

        private SkillPlansTableFormat(Class<?> type) {
            this.type = type;
        }

        @Override
        public Class<?> getType() {
            return type;
        }

        @Override
        public Comparator<?> getComparator() {
            return EnumTableColumn.getComparator(type);
        }
    }

    // Removed unused initial dynamic column builder; columns are rebuilt in
    // updateData()

    private static class PlanColumn implements EnumTableColumn<Row> {
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
        public Object getColumnValue(Row from) {
            Percent value = from.getPercent(plan);
            // GlazedLists' default comparator does not handle nulls; return 0% instead
            return value != null ? value : Percent.create(0);
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

    private class SkillPlansTableMenu implements TableMenu<Row> {
        @Override
        public MenuData<Row> getMenuData() {
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
        public void addInfoMenu(JPopupMenu jPopupMenu) {
        }

        @Override
        public void addToolMenu(JComponent jComponent) {
        }
    }

    private class SkillPlansFilterControl extends FilterControl<Row> {
        public SkillPlansFilterControl(EventList<Row> exportEventList) {
            super(program.getMainWindow().getFrame(), NAME, tableFormat, eventList, exportEventList, filterList);
        }

        @Override
        public void saveSettings(final String msg) {
            program.saveSettings("Skill Plans Table: " + msg);
        }
    }
}
