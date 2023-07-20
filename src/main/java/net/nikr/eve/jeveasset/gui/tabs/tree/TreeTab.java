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

package net.nikr.eve.jeveasset.gui.tabs.tree;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TreeList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TreeNodeData;
import ca.odell.glazedlists.swing.TreeTableCellEditor;
import ca.odell.glazedlists.swing.TreeTableCellRenderer;
import ca.odell.glazedlists.swing.TreeTableSupport;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.tag.TagUpdate;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.JStatusLabel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData.AssetMenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.gui.shared.table.containers.HierarchyColumn;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab.AssetTreeExpansionModel.ExpandedState;
import net.nikr.eve.jeveasset.i18n.TabsAssets;
import net.nikr.eve.jeveasset.i18n.TabsTree;


public class TreeTab extends JMainTabSecondary implements TagUpdate {

	private enum TreeAction {
		TYPE,
		COLLAPSE,
		EXPAND,
		REPROCESS_COLORS
	}

	private final int INDENT = 10;

	//GUI
	private final JTreeTable jTable;
	private final JStatusLabel jValue;
	private final JStatusLabel jReprocessed;
	private final JStatusLabel jCount;
	private final JStatusLabel jAverage;
	private final JStatusLabel jVolume;
	private final JToggleButton jCategories;
	private final JToggleButton jReprocessColors;

	//Table
	private final DefaultEventTableModel<TreeAsset> tableModel;
	private final EventList<TreeAsset> eventList;
	private final EventList<TreeAsset> exportEventList;
	private final SortedList<TreeAsset> sortedList;
	private final SortedList<TreeAsset> emptySortedList;
	private final FilterList<TreeAsset> filterList;
	private final TreeList<TreeAsset> treeList;
	private final TreeFilterControl filterControl;
	private final EnumTableFormatAdaptor<TreeTableFormat, TreeAsset> tableFormat;
	private final DefaultEventSelectionModel<TreeAsset> selectionModel;
	private final AssetTreeExpansionModel expansionModel;

	public static final String NAME = "treeassets"; //Not to be changed!

	private final TreeData treeData;

	public TreeTab(final Program program) {
		super(program, NAME, TabsTree.get().title(), Images.TOOL_TREE.getIcon(), true);

		treeData = new TreeData(program);

		layout.setAutoCreateGaps(true);

		ListenerClass listener = new ListenerClass();

		JFixedToolBar jToolBar = new JFixedToolBar();

		ButtonGroup buttonGroup = new ButtonGroup();

		jCategories = new JToggleButton(TabsTree.get().categories(), Images.LOC_GROUPS.getIcon());
		jCategories.setActionCommand(TreeAction.TYPE.name());
		jCategories.addActionListener(listener);
		buttonGroup.add(jCategories);
		jToolBar.addButton(jCategories);

		JToggleButton jLocation = new JToggleButton(TabsTree.get().locations(), Images.LOC_LOCATIONS.getIcon());
		jLocation.setActionCommand(TreeAction.TYPE.name());
		jLocation.addActionListener(listener);
		jLocation.setSelected(true);
		buttonGroup.add(jLocation);
		jToolBar.addButton(jLocation);

		jToolBar.addSeparator();

		jReprocessColors = new JToggleButton(TabsAssets.get().reprocessColors(), Images.TOOL_REPROCESSED.getIcon());
		jReprocessColors.setToolTipText(TabsAssets.get().reprocessColorsToolTip());
		jReprocessColors.setSelected(Settings.get().isReprocessColors());
		jReprocessColors.setActionCommand(TreeAction.REPROCESS_COLORS.name());
		jReprocessColors.addActionListener(listener);
		jToolBar.addButton(jReprocessColors);

		jToolBar.addGlue();

		JButton jCollapse = new JButton(TabsTree.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(TreeAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);
		jToolBar.addButton(jCollapse);

		JButton jExpand = new JButton(TabsTree.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(TreeAction.EXPAND.name());
		jExpand.addActionListener(listener);
		jToolBar.addButton(jExpand);

		jVolume = StatusPanel.createLabel(TabsAssets.get().totalVolume(), Images.ASSETS_VOLUME.getIcon(), AutoNumberFormat.DOUBLE);
		this.addStatusbarLabel(jVolume);

		jCount = StatusPanel.createLabel(TabsAssets.get().totalCount(), Images.EDIT_ADD.getIcon(), AutoNumberFormat.ITEMS);
		this.addStatusbarLabel(jCount);

		jAverage = StatusPanel.createLabel(TabsAssets.get().average(), Images.ASSETS_AVERAGE.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jAverage);

		jReprocessed = StatusPanel.createLabel(TabsAssets.get().totalReprocessed(), Images.SETTINGS_REPROCESSING.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jReprocessed);

		jValue = StatusPanel.createLabel(TabsAssets.get().totalValue(), Images.TOOL_VALUES.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jValue);

		//Table Format
		tableFormat = TableFormatFactory.treeTableFormat();
		//Backend
		eventList = EventListManager.create();
		exportEventList = EventListManager.create();
		//Filter (must be done before sorting for totals to match up - for reason beyond my comprehension)
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Sorting
		EventList<TreeAsset> emptyEventList = EventListManager.create();
		emptyEventList.getReadWriteLock().readLock().lock();
		emptySortedList = new SortedList<>(emptyEventList);
		emptyEventList.getReadWriteLock().readLock().unlock();
		eventList.getReadWriteLock().readLock().lock();
		sortedList = new SortedList<>(filterList);
		eventList.getReadWriteLock().readLock().unlock();
		//Tree
		expansionModel = new AssetTreeExpansionModel();
		treeList = new TreeList<>(sortedList, new AssetTreeFormat(sortedList), expansionModel);
		//Table Model
		tableModel = EventModels.createTableModel(treeList, tableFormat);
		//Table
		jTable = new JTreeTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		jTable.disableColumnResizeCache(HierarchyColumn.class);
		jTable.setRowHeight(22);
		jTable.addMouseListener(listener);
		//Sorting
		TableComparatorChooser<TreeAsset> tableComparatorChooser = TableComparatorChooser.install(jTable, emptySortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		tableComparatorChooser.addSortActionListener(new ListenerSorter());
		//Tree
		TreeTableSupport install = TreeTableSupport.install(jTable, treeList, 0);
		TreeTableCellEditor editor = new AssetTreeTableCellEditor(install.getDelegateEditor(), treeList, tableModel, INDENT, 6);
		TreeTableCellRenderer renderer = new AssetTreeTableCellRenderer(install.getDelegateRenderer(), treeList, tableModel, INDENT, 6);
		install.setEditor(editor);
		install.setRenderer(renderer);
		jTable.setDefaultRenderer(HierarchyColumn.class, renderer);
		jTable.setDefaultEditor(HierarchyColumn.class, editor);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(treeList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new TreeFilterControl();
		//Menu
		installTableTool(new TreeTableMenu(), tableFormat, tableModel, jTable, filterControl, TreeAsset.class);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar, jToolBar.getMinimumSize().width, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateTags() {
		beforeUpdateData();
		tableModel.fireTableDataChanged();
		filterControl.refilter();
		afterUpdateData();
	}

	@Override
	public void updateData() {
		//Update Data
		treeData.updateData();
		//Update GUI
		updateTableFull();
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
		expansionModel.clearCache();
	}

	@Override
	public void updateCache() {
		filterControl.createCache();
	}

	@Override
	public Collection<LocationType> getLocations() {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return new ArrayList<>(eventList);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}

	public EventList<TreeAsset> getEventList() {
		return eventList;
	}

	public void updateTableFull() {
		beforeUpdateData();
		updateTable(true);
		updateTotals();
		updateStatusbar();
		afterUpdateData();
	}

	public void resetTable() {
		beforeUpdateDataKeepCache();
		updateTable(false);
		afterUpdateData();
	}

	public void updateTable(boolean export) {
		final Set<TreeAsset> treeAssets;
		final Set<TreeAsset> treeAssetsExport;
		if (jCategories.isSelected()) {
			treeAssets = treeData.getCategories();
			treeAssetsExport = treeData.getCategoriesExport();
		} else {
			treeAssets = treeData.getLocations();
			treeAssetsExport = treeData.getLocationsExport();
		}
		//Update Jumps
		eventList.getReadWriteLock().writeLock().lock();
		try {
			eventList.clear();
			eventList.addAll(treeAssets);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		if (export) {
			exportEventList.getReadWriteLock().writeLock().lock();
			try {
				exportEventList.clear();
				exportEventList.addAll(treeAssetsExport);
			} finally {
				exportEventList.getReadWriteLock().writeLock().unlock();
			}
		}
	}

	public void updateReprocessColors() {
		jReprocessColors.setSelected(Settings.get().isReprocessColors());
	}

	private void updateTotals() {
		//Reset
		if (jCategories.isSelected()) {
			for (TreeAsset treeAsset : treeData.getCategoriesExport()) {
				treeAsset.resetValues();
			}
		} else {
			for (TreeAsset treeAsset : treeData.getLocationsExport()) {
				treeAsset.resetValues();
			}
		}
		//Calculate
		try {
			filterList.getReadWriteLock().readLock().lock();
			for (TreeAsset treeAsset : filterList) {
				if (treeAsset.isItem()) {
					treeAsset.updateParents();
				}
			}
		} finally {
			filterList.getReadWriteLock().readLock().unlock();
		}
	}

	private void updateStatusbar() {
		double averageValue = 0;
		double totalValue = 0;
		long totalCount = 0;
		double totalVolume = 0;
		double totalReprocessed = 0;
		try {
			filterList.getReadWriteLock().readLock().lock();
			for (TreeAsset asset : filterList) {
				if (!asset.isItem()) {
					continue;
				}
				totalValue = totalValue + asset.getValue();
				totalCount = totalCount + asset.getCount();
				totalVolume = totalVolume + asset.getVolumeTotal();
				totalReprocessed = totalReprocessed + asset.getValueReprocessed();
			}
		} finally {
			filterList.getReadWriteLock().readLock().unlock();
		}
		if (totalCount > 0 && totalValue > 0) {
			averageValue = totalValue / totalCount;
		}
		jVolume.setNumber(totalVolume);
		jCount.setNumber(totalCount);
		jAverage.setNumber(averageValue);
		jReprocessed.setNumber(totalReprocessed);
		jValue.setNumber(totalValue);
	}

	private class TreeTableMenu implements TableMenu<TreeAsset> {
		@Override
		public MenuData<TreeAsset> getMenuData() {
			return new AssetMenuData<>(selectionModel.getSelected());
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
			JMenuInfo.treeAsset(jPopupMenu, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class ListenerClass implements ActionListener, MouseListener {
		private final int WIDTH = UIManager.getIcon("Tree.expandedIcon").getIconWidth();

		@Override
		public void actionPerformed(ActionEvent e) {
			if (TreeAction.TYPE.name().equals(e.getActionCommand())) {
				expansionModel.setState(ExpandedState.LOAD);
				updateTableFull();
			} else if (TreeAction.COLLAPSE.name().equals(e.getActionCommand())) {
				expansionModel.setState(ExpandedState.COLLAPSE);
				resetTable();
				expansionModel.setState(ExpandedState.LOAD);
			} else if (TreeAction.EXPAND.name().equals(e.getActionCommand())) {
				expansionModel.setState(ExpandedState.EXPAND);
				resetTable();
				expansionModel.setState(ExpandedState.LOAD);
			} else if (TreeAction.REPROCESS_COLORS.name().equals(e.getActionCommand())) {
				boolean oldValue = Settings.get().isReprocessColors();
				boolean newValue = jReprocessColors.isSelected();
				if (oldValue != newValue) {
					Settings.lock("Reprocess Colors");
					Settings.get().setReprocessColors(newValue);
					Settings.unlock("Reprocess Colors");
					program.saveSettings("Reprocess Colors");
					jTable.repaint();
					program.getAssetsTab().updateReprocessColors();
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() % 2 == 0) {
				int row = jTable.rowAtPoint(e.getPoint());
				int depth = treeList.depth(row);
				final int min = INDENT + (depth * WIDTH);
				final int max = min + WIDTH;
				if (e.getPoint().x < min || e.getPoint().x > max) {
					treeList.setExpanded(row, !treeList.isExpanded(row));
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) { }

		@Override
		public void mouseReleased(MouseEvent e) { }

		@Override
		public void mouseEntered(MouseEvent e) { }

		@Override
		public void mouseExited(MouseEvent e) { }
	}

	private class ListenerSorter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					expansionModel.setState(ExpandedState.LOAD);
					try {
						jTable.lock();
						emptySortedList.getReadWriteLock().readLock().lock();
						sortedList.getReadWriteLock().writeLock().lock();
						sortedList.setComparator(emptySortedList.getComparator());
					} finally {
						emptySortedList.getReadWriteLock().readLock().unlock();
						sortedList.getReadWriteLock().writeLock().unlock();
						jTable.unlock();
					}
				}
			});
		}
	}

	public static class AssetTreeExpansionModel implements TreeList.ExpansionModel<TreeAsset> {

		public enum ExpandedState {
			EXPAND,
			COLLAPSE,
			LOAD
		}

		private final Map<String, Boolean> cache = new HashMap<>();
		private ExpandedState state = ExpandedState.LOAD;

		@Override
		public boolean isExpanded(TreeAsset element, List<TreeAsset> path) {
			if (state == ExpandedState.EXPAND) {
				return saveExpanded(element, true);
			} else if (state == ExpandedState.COLLAPSE) {
				return saveExpanded(element, false);
			} else {
				return loadExpanded(element);
			}
		}

		@Override
		public void setExpanded(TreeAsset element, List<TreeAsset> path, boolean expanded) {
			saveExpanded(element, expanded);
		}

		private boolean loadExpanded(final TreeAsset element) {
			Boolean expanded = cache.get(getElementKey(element));
			if (expanded != null) {
				return expanded;
			} else {
				return false; // default to collapsed
			}
		}

		private boolean saveExpanded(final TreeAsset element, final boolean expanded) {
			cache.put(getElementKey(element), expanded);
			return expanded;
		}

		private String getElementKey(TreeAsset element) {
			return element.getCompare();
		}

		public void clearCache() {
			cache.clear();
		}

		public void setState(ExpandedState expandeState) {
			this.state = expandeState;
		}
	}

	public static class AssetTreeComparator implements Comparator<TreeAsset> {

		@Override
		public int compare(TreeAsset o1, TreeAsset o2) {
			return o1.getCompare().compareTo(o2.getCompare());
		}
	}

	public static class AssetTreeSortedComparator implements Comparator<TreeAsset> {

		private final SortedList<TreeAsset> sortedList;

		public AssetTreeSortedComparator(SortedList<TreeAsset> sortedList) {
			this.sortedList = sortedList;
		}

		@Override
		public int compare(TreeAsset o1, TreeAsset o2) {
			if (o1.getCompare().equals(o2.getCompare())) {
				return 0; //Equal item
			} else {
				if (o1.getTree().size() == o2.getTree().size()) { //Compare equal depth
					for (int i = 0; i < o1.getTree().size(); i++) {
						TreeAsset tree1 = o1.getTree().get(i);
						TreeAsset tree2 = o2.getTree().get(i);
						int result = tree1.getCompare().compareTo(tree2.getCompare());
						if (result != 0) {
							return result; //Parent not equal
						}
					}
					//Parents equal - compare items

					//Use sorted comparator
					Comparator<? super TreeAsset> comparator = sortedList.getComparator();
					if (comparator != null) {
						int result = comparator.compare(o1, o2);
						if (result != 0) { //Not equal
							return result;
						}
					}
					//Fallback (Sorted comparator equal or null)
					return o1.getCompare().compareTo(o2.getCompare());
				} else { //Should never happen (depth not equal)
					return -1;
				}
			}
		}
	}

	public static class AssetTreeFormat implements TreeList.Format<TreeAsset> {

		private final AssetTreeSortedComparator comparator;

		public AssetTreeFormat(SortedList<TreeAsset> sortedList) {
			this.comparator = new AssetTreeSortedComparator(sortedList);
		}

		@Override
		public void getPath(List<TreeAsset> path, TreeAsset element) {
			path.addAll(element.getTree());
			path.add(element);
		}

		@Override
		public boolean allowsChildren(TreeAsset element) {
			return true;
		}

		@Override
		public Comparator<? super TreeAsset> getComparator(int depth) {
			return comparator;
		}
	}

	private class TreeFilterControl extends FilterControl<TreeAsset> {

		public TreeFilterControl() {
			super(program.getMainWindow().getFrame(),
					NAME,
					tableFormat,
					eventList,
					exportEventList,
					filterList
					);
		}

		@Override
		protected void updateFilters() {
			if (program != null && program.getOverviewTab() != null) {
				program.getOverviewTab().updateFilters();
			}
		}

		@Override
		protected void beforeFilter() {
			beforeUpdateData();
		}

		@Override
		protected void afterFilter() {
			updateTotals();
			updateStatusbar();
			afterUpdateData();
		}

		@Override
		public void saveSettings(final String msg) {
			program.saveSettings("Tree Table: " + msg); //Save Tree Filters and Export Settings
		}
	}

	public static class AssetTreeTableCellEditor extends TreeTableCellEditor {

		private int indent;
		private int spacer;
		private DefaultEventTableModel<TreeAsset> tableModel;

		public AssetTreeTableCellEditor(TableCellEditor delegate, TreeList<TreeAsset> treeList, DefaultEventTableModel<TreeAsset> tableModel, int indent, int spacer) {
			super(delegate, treeList);
			if (indent == spacer) {
				throw new IllegalArgumentException("indent and spacer may not be equal - that invalidates indent");
			}
			this.tableModel = tableModel;
			this.indent = indent;
			this.spacer = spacer;
		}

		@Override
		protected int getIndent(TreeNodeData treeNodeData, boolean showExpanderForEmptyParent) {
			return super.getIndent(treeNodeData, showExpanderForEmptyParent) + indent;
		}

		@Override
		protected int getSpacer(TreeNodeData treeNodeData, boolean showExpanderForEmptyParent) {
			return spacer;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			JPanel jPanel = (JPanel) super.getTableCellEditorComponent(table, value, isSelected, row, column); //To change body of generated methods, choose Tools | Templates.
			TreeAsset treeAsset = tableModel.getElementAt(row);
			JLabel jLabel = (JLabel) jPanel.getComponent(3);
			jLabel.setIcon(treeAsset.getIcon());
			return jPanel;
		}

	}

	public static class AssetTreeTableCellRenderer extends TreeTableCellRenderer {

		private int indent;
		private int spacer;
		private DefaultEventTableModel<TreeAsset> tableModel;

		public AssetTreeTableCellRenderer(TableCellRenderer delegate, TreeList<TreeAsset> treeList, DefaultEventTableModel<TreeAsset> tableModel, int indent, int spacer) {
			super(delegate, treeList);
			if (indent == spacer) {
				throw new IllegalArgumentException("indent and spacer may not be equal - that invalidates indent");
			}
			this.tableModel = tableModel;
			this.indent = indent;
			this.spacer = spacer;
		}

		@Override
		protected int getIndent(TreeNodeData treeNodeData, boolean showExpanderForEmptyParent) {
			return super.getIndent(treeNodeData, showExpanderForEmptyParent) + indent;
		}

		@Override
		protected int getSpacer(TreeNodeData treeNodeData, boolean showExpanderForEmptyParent) {
			return spacer;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JPanel jPanel = (JPanel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			TreeAsset treeAsset = tableModel.getElementAt(row);
			JLabel jLabel = (JLabel) jPanel.getComponent(3);
			jLabel.setIcon(treeAsset.getIcon());
			return jPanel;
		}
	}
}
