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

package net.nikr.eve.jeveasset.gui.tabs.tree;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TreeList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.tag.TagUpdate;
import net.nikr.eve.jeveasset.data.settings.types.JumpType;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary.NamesUpdater;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuJumps;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset.TreeType;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab.AssetTreeExpansionModel.ExpandeState;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTableFormat.HierarchyColumn;
import net.nikr.eve.jeveasset.i18n.TabsAssets;
import net.nikr.eve.jeveasset.i18n.TabsTree;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class TreeTab extends JMainTabSecondary implements TagUpdate, NamesUpdater<TreeAsset> {

	private enum TreeAction {
		UPDATE,
		COLLAPSE,
		EXPAND
	}

	private final int INDENT = 10;

	//GUI
	private final JTreeTable jTable;
	private final JLabel jValue;
	private final JLabel jReprocessed;
	private final JLabel jCount;
	private final JLabel jAverage;
	private final JLabel jVolume;
	private final JToggleButton jCategories;
	private final JToggleButton jLocation;

	//Table
	private final DefaultEventTableModel<TreeAsset> tableModel;
	private final EventList<TreeAsset> eventList;
	private final EventList<TreeAsset> exportEventList;
	private final FilterList<TreeAsset> filterList;
	private final TreeList<TreeAsset> treeList;
	private final AssetFilterControl filterControl;
	private final EnumTableFormatAdaptor<TreeTableFormat, TreeAsset> tableFormat;
	private final DefaultEventSelectionModel<TreeAsset> selectionModel;
	private final AssetTreeExpansionModel expansionModel;
	private final SortedList<TreeAsset> sortedList;
	private final Set<TreeAsset> locationsExport = new TreeSet<TreeAsset>(new AssetTreeComparator());
	private final Set<TreeAsset> locations = new TreeSet<TreeAsset>(new AssetTreeComparator());
	private final Set<TreeAsset> categoriesExport = new TreeSet<TreeAsset>(new AssetTreeComparator());
	private final Set<TreeAsset> categories = new TreeSet<TreeAsset>(new AssetTreeComparator());

	public static final String NAME = "treeassets"; //Not to be changed!

	//FIXME - - - - > Sorted export not tested/working
	public TreeTab(final Program program) {
		super(program, TabsTree.get().title(), Images.TOOL_TREE.getIcon(), true);
		layout.setAutoCreateGaps(true);

		ListenerClass listener = new ListenerClass();
		
		JFixedToolBar jToolBarLeft = new JFixedToolBar();

		ButtonGroup buttonGroup = new ButtonGroup();

		jCategories = new JToggleButton(TabsTree.get().categories(), Images.LOC_GROUPS.getIcon());
		jCategories.setActionCommand(TreeAction.UPDATE.name());
		jCategories.addActionListener(listener);
		buttonGroup.add(jCategories);
		jToolBarLeft.addButton(jCategories);

		jLocation = new JToggleButton(TabsTree.get().locations(), Images.LOC_LOCATIONS.getIcon());
		jLocation.setActionCommand(TreeAction.UPDATE.name());
		jLocation.addActionListener(listener);
		jLocation.setSelected(true);
		buttonGroup.add(jLocation);
		jToolBarLeft.addButton(jLocation);

		JFixedToolBar jToolBarRight = new JFixedToolBar();

		JButton jCollapse = new JButton(TabsTree.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(TreeAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);
		jToolBarRight.addButton(jCollapse);

		JButton jExpand = new JButton(TabsTree.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(TreeAction.EXPAND.name());
		jExpand.addActionListener(listener);
		jToolBarRight.addButton(jExpand);


		//Table Format
		tableFormat = new EnumTableFormatAdaptor<TreeTableFormat, TreeAsset>(TreeTableFormat.class);
		//Backend
		eventList = new EventListManager<TreeAsset>().create();
		exportEventList = new EventListManager<TreeAsset>().create();
		//Sorting (per column)
		
		EventList<TreeAsset> myEventList = new EventListManager<TreeAsset>().create();
		myEventList.getReadWriteLock().readLock().lock();
		sortedList = new SortedList<TreeAsset>(myEventList);
		myEventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<TreeAsset>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		filterList.addListEventListener(listener);
		//Tree
		expansionModel = new AssetTreeExpansionModel();
		treeList = new TreeList<TreeAsset>(filterList, new AssetTreeFormat(sortedList), expansionModel);
		//Table Model
		tableModel = EventModels.createTableModel(treeList, tableFormat);
		//Table
		jTable = new JTreeTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		jTable.disableColumnResizeCache(HierarchyColumn.class);
		jTable.setRowHeight(22);
		jTable.addMouseListener(listener);
		//Sorting
		TableComparatorChooser<TreeAsset> tableComparatorChooser = TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
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
		installTable(jTable, NAME);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		filterControl = new AssetFilterControl(
				program.getMainWindow().getFrame(),
				eventList,
				exportEventList,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		//Menu
		installMenu(program, new TreeTableMenu(), jTable, TreeAsset.class);

		jVolume = StatusPanel.createLabel(TabsAssets.get().totalVolume(), Images.ASSETS_VOLUME.getIcon());
		this.addStatusbarLabel(jVolume);

		jCount = StatusPanel.createLabel(TabsAssets.get().totalCount(), Images.EDIT_ADD.getIcon()); //Add
		this.addStatusbarLabel(jCount);

		jAverage = StatusPanel.createLabel(TabsAssets.get().average(), Images.ASSETS_AVERAGE.getIcon());
		this.addStatusbarLabel(jAverage);

		jReprocessed = StatusPanel.createLabel(TabsAssets.get().totalReprocessed(), Images.SETTINGS_REPROCESSING.getIcon());
		this.addStatusbarLabel(jReprocessed);

		jValue = StatusPanel.createLabel(TabsAssets.get().totalValue(), Images.TOOL_VALUES.getIcon());
		this.addStatusbarLabel(jValue);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createSequentialGroup()
					.addComponent(jToolBarLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					.addGap(0)
					.addComponent(jToolBarRight)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createParallelGroup()
					.addComponent(jToolBarLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jToolBarRight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
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
		locations.clear();
		categories.clear();
		locationsExport.clear();
		categoriesExport.clear();
		Map<String, TreeAsset> categoryCache = new HashMap<String, TreeAsset>();
		Map<String, TreeAsset> locationCache = new HashMap<String, TreeAsset>();
		MyLocation emptyLocation = new MyLocation(0, "", 0, "", 0, "", "");
		for (MyAsset asset : program.getAssetList()) {
		//LOCATION
			List<TreeAsset> locationTree = new ArrayList<TreeAsset>();
			MyLocation location = asset.getLocation();

			//Region
			String regionKey = location.getRegion();
			TreeAsset regionAsset = locationCache.get(location.getRegion());
			if (regionAsset == null) {
				regionAsset = new TreeAsset(ApiIdConverter.getLocation(location.getRegionID()), location.getRegion(), regionKey, Images.LOC_REGION.getIcon(), locationTree);
				locationCache.put(regionKey, regionAsset);
			}
			locationTree.add(regionAsset);
			locationsExport.add(regionAsset);

			//System
			String systemKey = location.getRegion() + location.getSystem();
			TreeAsset systemAsset = locationCache.get(systemKey);
			if (systemAsset == null) {
				systemAsset = new TreeAsset(ApiIdConverter.getLocation(location.getSystemID()), location.getSystem(), systemKey, Images.LOC_SYSTEM.getIcon(), locationTree);
				locationCache.put(systemKey, systemAsset);
			}
			locationTree.add(systemAsset);
			locationsExport.add(systemAsset);

			String fullLocation = location.getRegion()+location.getSystem();
			//Station
			if (location.isStation()) {
				String stationKey = location.getRegion() + location.getSystem() + location.getLocation();
				TreeAsset stationAsset = locationCache.get(stationKey);
				if (stationAsset == null) {
					stationAsset = new TreeAsset(asset.getLocation(), location.getLocation(), stationKey, Images.LOC_STATION.getIcon(), locationTree);
					locationCache.put(stationKey, stationAsset);
				}
				locationTree.add(stationAsset);
				locationsExport.add(stationAsset);
				fullLocation = location.getRegion()+location.getSystem()+location.getLocation();
			}

			//Add parent item(s)
			String parentKey = fullLocation;
			if (!asset.getParents().isEmpty()) {
				for (MyAsset parentAsset : asset.getParents()) {
					String cacheKey = parentAsset.getName() + " #" + parentAsset.getItemID();
					TreeAsset parentTreeAsset = locationCache.get(cacheKey);
					if (parentTreeAsset == null) {
						parentTreeAsset = new TreeAsset(parentAsset, TreeType.LOCATION, locationTree, parentKey, !parentAsset.getAssets().isEmpty());
						locationCache.put(cacheKey, parentTreeAsset);
						locations.add(parentTreeAsset);
					}
					parentKey = parentKey + parentAsset.getName() + " #" + parentAsset.getItemID();
					locationTree.add(parentTreeAsset);
					locationsExport.add(parentTreeAsset);
				}
			}
			//Add item
			if (asset.getAssets().isEmpty()) {
				TreeAsset locationAsset = new TreeAsset(asset, TreeType.LOCATION, locationTree, parentKey, !asset.getAssets().isEmpty());
				locations.add(locationAsset);
				locationsExport.add(locationAsset);
			}
			
		//CATEGORY
			List<TreeAsset> categoryTree = new ArrayList<TreeAsset>();

			//Category
			String categoryKey = asset.getItem().getCategory();
			TreeAsset categoryAsset = categoryCache.get(categoryKey);
			if (categoryAsset == null) {
				categoryAsset = new TreeAsset(emptyLocation, asset.getItem().getCategory(), categoryKey, null, categoryTree, 1);
				categoryCache.put(categoryKey, categoryAsset);
			}
			categoryTree.add(categoryAsset);
			categoriesExport.add(categoryAsset);

			//Group
			String groupKey = categoryKey + asset.getItem().getGroup();
			TreeAsset groupAsset = categoryCache.get(groupKey);
			if (groupAsset == null) {
				groupAsset = new TreeAsset(emptyLocation, asset.getItem().getGroup(), groupKey, null, categoryTree, 1);
				categoryCache.put(groupKey, groupAsset);
			}
			categoryTree.add(groupAsset);
			categoriesExport.add(groupAsset);

			//Item
			TreeAsset category = new TreeAsset(asset, TreeType.CATEGORY, categoryTree, groupKey, false);
			categories.add(category);
			categoriesExport.add(category);
		}
		updateTable();
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

	public void addColumn(MyLocation location) {
		tableFormat.addColumn(new JMenuJumps.Column<TreeAsset>(location.getSystem(), location.getSystemID()));
		filterControl.setColumns(tableFormat.getOrderColumns());
	}

	public void removeColumn(MyLocation location) {
		tableFormat.removeColumn(new JMenuJumps.Column<TreeAsset>(location.getSystem(), location.getSystemID()));
		filterControl.setColumns(tableFormat.getOrderColumns());
	}

	@Override
	public EventList<TreeAsset> getEventList() {
		return eventList;
	}

	public void updateTable() {
		jTable.lock();
		Set<TreeAsset> treeAssets = locations;
		Set<TreeAsset> treeAssetsExport = locationsExport;
		if (jCategories.isSelected()) {
			treeAssets = categories;
			treeAssetsExport = categoriesExport;
		}
		//Update Jumps
		program.getProfileData().updateJumps(new ArrayList<JumpType>(treeAssets), TreeAsset.class);
		eventList.getReadWriteLock().writeLock().lock();
		try {
			eventList.clear();
			eventList.addAll(treeAssets);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		exportEventList.getReadWriteLock().writeLock().lock();
		try {
			exportEventList.clear();
			exportEventList.addAll(treeAssetsExport);
		} finally {
			exportEventList.getReadWriteLock().writeLock().unlock();
		}
		updateTotals();
	}

	private void updateTotals() {
		jTable.lock();
		if (jCategories.isSelected()) {
			for (TreeAsset treeAsset : categoriesExport) {
				treeAsset.resetValues();
			}
			try {
				filterList.getReadWriteLock().readLock().lock();
				for (TreeAsset treeAsset : filterList) {
					treeAsset.updateParents();
				}
			} finally {
				filterList.getReadWriteLock().readLock().unlock();
			}
		} else {
			for (TreeAsset treeAsset : locationsExport) {
				treeAsset.resetValues();
			}
			Set<TreeAsset> parentItems = new TreeSet<TreeAsset>(new AssetTreeComparator());
			try {
				filterList.getReadWriteLock().readLock().lock();
				for (TreeAsset treeAsset : filterList) {
					if (treeAsset.isItem()) {
						treeAsset.updateParents();
					}
					//Add containers
					for (TreeAsset treeParent : treeAsset.getTree()) {
						if (treeParent.isItem()) {
							parentItems.add(treeParent);
						}
					}
				}
			} finally {
				filterList.getReadWriteLock().readLock().unlock();
			}
		}
		jTable.unlock();
	}

	private void updateStatusbar() {
		double averageValue = 0;
		double totalValue = 0;
		long totalCount = 0;
		double totalVolume = 0;
		double totalReprocessed = 0;
		for (TreeAsset asset : filterList) {
			if (!asset.isItem()) {
				continue;
			}
			totalValue = totalValue + (asset.getDynamicPrice() * asset.getCount()) ;
			totalCount = totalCount + asset.getCount();
			totalVolume = totalVolume + asset.getVolumeTotal();
			totalReprocessed = totalReprocessed + asset.getValueReprocessed();
		}
		if (totalCount > 0 && totalValue > 0) {
			averageValue = totalValue / totalCount;
		}
		jVolume.setText(Formater.doubleFormat(totalVolume));
		jCount.setText(Formater.itemsFormat(totalCount));
		jAverage.setText(Formater.iskFormat(averageValue));
		jReprocessed.setText(Formater.iskFormat(totalReprocessed));
		jValue.setText(Formater.iskFormat(totalValue));
	}

	private class TreeTableMenu implements TableMenu<TreeAsset> {
		@Override
		public MenuData<TreeAsset> getMenuData() {
			return new MenuData<TreeAsset>(selectionModel.getSelected());
		}

		@Override
		public JMenu getFilterMenu() {
			return filterControl.getMenu(jTable, selectionModel.getSelected());
		}

		@Override
		public JMenu getColumnMenu() {
			return tableFormat.getMenu(program, tableModel, jTable, NAME);
		}

		@Override
		public void addInfoMenu(JComponent jComponent) {
			JMenuInfo.treeAsset(jComponent, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class ListenerClass implements ActionListener, MouseListener, ListEventListener<TreeAsset> {
		private final int WIDTH = UIManager.getIcon("Tree.expandedIcon").getIconWidth();
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (TreeAction.UPDATE.name().equals(e.getActionCommand())) {
				expansionModel.setState(ExpandeState.LOAD);
				updateTable();
			} else if (TreeAction.COLLAPSE.name().equals(e.getActionCommand())) {
				expansionModel.setState(ExpandeState.COLLAPSE);
				updateTable();
				expansionModel.setState(ExpandeState.LOAD);
			} else if (TreeAction.EXPAND.name().equals(e.getActionCommand())) {
				expansionModel.setState(ExpandeState.EXPANDE);
				updateTable();
				expansionModel.setState(ExpandeState.LOAD);
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
		
		@Override
		public void listChanged(final ListEvent<TreeAsset> listChanges) {
			updateStatusbar();
			program.getOverviewTab().updateTable();
		}
	}

	private class ListenerSorter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					//Full list update
					expansionModel.setState(ExpandeState.LOAD);
					List<TreeAsset> treeAssets = new ArrayList<TreeAsset>(eventList);
					eventList.getReadWriteLock().writeLock().lock();
					try {
						eventList.clear();
						eventList.addAll(treeAssets);
					} finally {
						eventList.getReadWriteLock().writeLock().unlock();
					}
				}
			});
		}
	}

	public static class AssetTreeExpansionModel implements TreeList.ExpansionModel<TreeAsset> {

		public enum ExpandeState {
			EXPANDE,
			COLLAPSE,
			LOAD
		}

		private ExpandeState expandeState = ExpandeState.COLLAPSE;

		@Override
		public boolean isExpanded(TreeAsset element, List<TreeAsset> path) {
			if (expandeState == ExpandeState.EXPANDE) {
				element.setExpanded(true);  //Save changes made by ExpandeState
				return true;
			} else if (expandeState == ExpandeState.COLLAPSE) {
				element.setExpanded(false);  //Save changes made by ExpandeState
				return false;
			} else {
				return element.isExpanded();
			}
		}

		@Override
		public void setExpanded(TreeAsset element, List<TreeAsset> path, boolean expanded) {
			element.setExpanded(expanded); //Save GUI changes
		}

		public ExpandeState getState() {
			return expandeState;
		}

		public void setState(ExpandeState expandeState) {
			this.expandeState = expandeState;
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

	private class AssetFilterControl extends FilterControl<TreeAsset> {

		public AssetFilterControl(JFrame jFrame, EventList<TreeAsset> eventList, EventList<TreeAsset> exportEventList, FilterList<TreeAsset> filterList, Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, exportEventList, filterList, filters);
		}

		@Override
		protected Object getColumnValue(final TreeAsset item, final String column) {
			return tableFormat.getColumnValue(item, column);
		}

		@Override
		protected EnumTableColumn<?> valueOf(final String column) {
			return TreeTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<TreeAsset>> getColumns() {
			return new ArrayList<EnumTableColumn<TreeAsset>>(tableFormat.getOrderColumns());
		}

		@Override
		protected List<EnumTableColumn<TreeAsset>> getShownColumns() {
			return new ArrayList<EnumTableColumn<TreeAsset>>(tableFormat.getShownColumns());
		}

		@Override
		protected void updateFilters() {
			if (program != null && program.getOverviewTab() != null) {
				program.getOverviewTab().updateFilters();
			}
		}

		@Override
		protected void beforeFilter() {
			jTable.lock();
		}

		@Override
		protected void afterFilter() {
			updateTotals();
		}

		@Override
		protected void saveSettings(final String msg) {
			program.saveSettings("Tree Talbe: " + msg); //Save Tree Filters and Export Setttings
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
