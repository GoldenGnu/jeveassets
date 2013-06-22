/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.TreeList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TreeNodeData;
import ca.odell.glazedlists.swing.TreeTableCellEditor;
import ca.odell.glazedlists.swing.TreeTableCellRenderer;
import ca.odell.glazedlists.swing.TreeTableSupport;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset.TreeType;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab.AssetTreeExpansionModel.ExpandeState;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTableFormat.HierarchyColumn;
import net.nikr.eve.jeveasset.i18n.TabsAssets;
import net.nikr.eve.jeveasset.i18n.TabsTree;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class TreeTab extends JMainTab implements TableMenu<TreeAsset> {

	private static final String ACTION_UPDATE = "ACTION_UPDATE";
	private static final String ACTION_COLLAPSE = "ACTION_COLLAPSE";
	private static final String ACTION_EXPAND = "ACTION_EXPAND";

	private final int INDENT = 10;

	//GUI
	private JTreeTable jTable;
	private JLabel jValue;
	private JLabel jReprocessed;
	private JLabel jCount;
	private JLabel jAverage;
	private JLabel jVolume;
	private JToggleButton jCategories;
	private JToggleButton jLocation;

	//Table
	private DefaultEventTableModel<TreeAsset> tableModel;
	private EventList<TreeAsset> eventList;
	private FilterList<TreeAsset> filterList;
	private TreeList<TreeAsset> treeList;
	private AssetFilterControl filterControl;
	private EnumTableFormatAdaptor<TreeTableFormat, TreeAsset> tableFormat;
	private DefaultEventSelectionModel<TreeAsset> selectionModel;
	private AssetTreeExpansionModel expansionModel;
	private Set<TreeAsset> locations = new TreeSet<TreeAsset>(new AssetTreeComparator());
	private Set<TreeAsset> categories = new TreeSet<TreeAsset>(new AssetTreeComparator());

	public static final String NAME = "treeassets"; //Not to be changed!

	public TreeTab(final Program program) {
		super(program, TabsTree.get().title(), Images.TOOL_TREE.getIcon(), true);
		layout.setAutoCreateGaps(true);

		ListenerClass listener = new ListenerClass();
		
		JToolBar jToolBarLeft = new JToolBar();
		jToolBarLeft.setFloatable(false);
		jToolBarLeft.setRollover(true);

		ButtonGroup buttonGroup = new ButtonGroup();

		jCategories = new JToggleButton(TabsTree.get().categories(), Images.LOC_GROUPS.getIcon());
		jCategories.setActionCommand(ACTION_UPDATE);
		jCategories.addActionListener(listener);
		buttonGroup.add(jCategories);
		addToolButton(jToolBarLeft, jCategories);

		jLocation = new JToggleButton(TabsTree.get().locations(), Images.LOC_LOCATIONS.getIcon());
		jLocation.setActionCommand(ACTION_UPDATE);
		jLocation.addActionListener(listener);
		jLocation.setSelected(true);
		buttonGroup.add(jLocation);
		addToolButton(jToolBarLeft, jLocation);

		JToolBar jToolBarRight = new JToolBar();
		jToolBarRight.setFloatable(false);
		jToolBarRight.setRollover(true);

		JButton jCollapse = new JButton(TabsTree.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(ACTION_COLLAPSE);
		jCollapse.addActionListener(listener);
		addToolButton(jToolBarRight, jCollapse);

		JButton jExpand = new JButton(TabsTree.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(ACTION_EXPAND);
		jExpand.addActionListener(listener);
		addToolButton(jToolBarRight, jExpand);


		//Table Format
		tableFormat = new EnumTableFormatAdaptor<TreeTableFormat, TreeAsset>(TreeTableFormat.class);
		//Backend
		eventList = new BasicEventList<TreeAsset>();
		//Filter
		filterList = new FilterList<TreeAsset>(eventList);
		filterList.addListEventListener(listener);
		//Tree
		expansionModel = new AssetTreeExpansionModel();
		treeList = new TreeList<TreeAsset>(filterList, new AssetTreeFormat(), expansionModel);
		//Table Model
		tableModel = EventModels.createTableModel(treeList, tableFormat);
		//Table
		jTable = new JTreeTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		jTable.disableColumnResizeCache(HierarchyColumn.class);
		jTable.setRowHeight(22);
		jTable.addMouseListener(listener);
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
				program,
				program.getMainWindow().getFrame(),
				tableFormat,
				eventList,
				filterList,
				Settings.get().getTableFilters(NAME)
				);

		//Menu
		installMenu(program, this, jTable, TreeAsset.class);

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

		final int TOOLBAR_HEIGHT = jToolBarLeft.getInsets().top + jToolBarLeft.getInsets().bottom + Program.BUTTONS_HEIGHT;
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createSequentialGroup()
					.addComponent(jToolBarLeft)
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jToolBarRight)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addGroup(layout.createParallelGroup()
					.addComponent(jToolBarLeft, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT)
					.addComponent(jToolBarRight, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	private void addToolButton(final JToolBar jToolBar, final AbstractButton jButton) {
		addToolButton(jToolBar, jButton, 90);
	}

	private void addToolButton(final JToolBar jToolBar, final AbstractButton jButton, final int width) {
		if (width > 0) {
			jButton.setMinimumSize(new Dimension(width, Program.BUTTONS_HEIGHT));
			jButton.setMaximumSize(new Dimension(width, Program.BUTTONS_HEIGHT));
		}
		jButton.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBar.add(jButton);
	}

	@Override
	public MenuData<TreeAsset> getMenuData() {
		return new MenuData<TreeAsset>(selectionModel.getSelected());
	}

	@Override
	public JMenu getFilterMenu() {
		return filterControl.getMenu(jTable, tableFormat, selectionModel.getSelected());
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

	@Override
	public void updateData() {
		locations.clear();
		categories.clear();
		Map<String, TreeAsset> categoryCache = new HashMap<String, TreeAsset>();
		Map<String, TreeAsset> locationCache = new HashMap<String, TreeAsset>();
		for (Asset asset : program.getAssetEventList()) {
		//LOCATION
			List<TreeAsset> locationTree = new ArrayList<TreeAsset>();
			Location location = asset.getLocation();

			//Region
			TreeAsset regionAsset = locationCache.get(location.getRegion());
			if (regionAsset == null) {
				regionAsset = new TreeAsset(ApiIdConverter.getLocation(location.getRegionID()), location.getRegion(), location.getRegion(), Images.LOC_REGION.getIcon(), locationTree);
				locationCache.put(location.getRegion(), regionAsset);
			}
			locationTree.add(regionAsset);
			locations.add(regionAsset);
			//Update region total
			regionAsset.add(asset);

			//System
			String systemKey = location.getRegion() + location.getSystem();
			TreeAsset systemAsset = locationCache.get(systemKey);
			if (systemAsset == null) {
				systemAsset = new TreeAsset(ApiIdConverter.getLocation(location.getSystemID()), location.getSystem(), systemKey, Images.LOC_SYSTEM.getIcon(), locationTree);
				locationCache.put(systemKey, systemAsset);
			}
			locationTree.add(systemAsset);
			locations.add(systemAsset);
			//Update system total
			systemAsset.add(asset);
			
			String fullLocation = location.getRegion()+location.getSystem();
			//Station
			if (location.isStation()) {
				String stationKey = location.getRegion() + location.getSystem() + location.getLocation();
				TreeAsset stationAsset = locationCache.get(stationKey);
				if (stationAsset == null) {
					stationAsset = new TreeAsset(asset.getLocation(), location.getLocation(), stationKey, Images.LOC_SYSTEM.getIcon(), locationTree);
					locationCache.put(stationKey, stationAsset);
				}
				locationTree.add(stationAsset);
				locations.add(stationAsset);
				//Update station total
				stationAsset.add(asset);
				fullLocation = location.getRegion()+location.getSystem()+location.getLocation();
			}

			//Parent
			String parentKey = fullLocation;
			if (!asset.getParents().isEmpty()) {
				for (Asset parentAsset : asset.getParents()) {
					parentKey = parentKey + parentAsset.getName() + " #" + parentAsset.getItemID();
					TreeAsset parentTreeAsset = locationCache.get(parentKey);
					if (parentTreeAsset == null) {
						parentTreeAsset = new TreeAsset(asset.getLocation(), parentAsset.getName(), parentKey, Images.LOC_SYSTEM.getIcon(), locationTree);
						locationCache.put(parentKey, parentTreeAsset);
					}
					locationTree.add(parentTreeAsset);
					locations.add(parentTreeAsset);
					//Update parent total
					parentTreeAsset.add(asset);
				}
			}
			TreeAsset locationAsset = new TreeAsset(asset, TreeType.LOCATION, locationTree, parentKey, !asset.getAssets().isEmpty());
			locations.add(locationAsset);
			
		//CATEGORY
			List<TreeAsset> categoryTree = new ArrayList<TreeAsset>();

			//Category
			String categoryKey = asset.getItem().getCategory();
			TreeAsset categoryAsset = categoryCache.get(categoryKey);
			if (categoryAsset == null) {
				categoryAsset = new TreeAsset(new Location(0), asset.getItem().getCategory(), categoryKey, null, categoryTree, 1);
				categoryCache.put(categoryKey, categoryAsset);
			}
			categoryTree.add(categoryAsset);
			categories.add(categoryAsset);
			//Update category total
			categoryAsset.add(asset);

			//Group
			String groupKey = categoryKey + asset.getItem().getGroup();
			TreeAsset groupAsset = categoryCache.get(groupKey);
			if (groupAsset == null) {
				groupAsset = new TreeAsset(new Location(0), asset.getItem().getGroup(), groupKey, null, categoryTree, 1);
				categoryCache.put(groupKey, groupAsset);
			}
			categoryTree.add(groupAsset);
			categories.add(groupAsset);
			//Update group total
			groupAsset.add(asset);

			TreeAsset category = new TreeAsset(asset, TreeType.CATEGORY, categoryTree, groupKey, false);
			categories.add(category);
		}
		updateTable();
	}

	public void updateTable() {
		Set<TreeAsset> treeAssets = locations;
		if (jCategories.isSelected()) {
			treeAssets = categories;
		}
		eventList.getReadWriteLock().writeLock().lock();
		try {
			eventList.clear();
			eventList.addAll(treeAssets);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
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

	public class ListenerClass implements ActionListener, MouseListener, ListEventListener<TreeAsset> {

		private final int WIDTH = UIManager.getIcon("Tree.expandedIcon").getIconWidth();
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ACTION_UPDATE.equals(e.getActionCommand())) {
				expansionModel.setState(ExpandeState.LOAD);
				updateTable();
			} else if (ACTION_COLLAPSE.equals(e.getActionCommand())) {
				expansionModel.setState(ExpandeState.COLLAPSE);
				updateTable();
				expansionModel.setState(ExpandeState.LOAD);
			} else if (ACTION_EXPAND.equals(e.getActionCommand())) {
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

	public static class AssetTreeFormat implements TreeList.Format<TreeAsset> {

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
			return new AssetTreeComparator();
		}
	}

	public static class AssetFilterControl extends FilterControl<TreeAsset> {

		private EnumTableFormatAdaptor<TreeTableFormat, TreeAsset> tableFormat;
		private Program program;

		public AssetFilterControl(final Program program, final JFrame jFrame, final EnumTableFormatAdaptor<TreeTableFormat, TreeAsset> tableFormat, final EventList<TreeAsset> eventList, final FilterList<TreeAsset> filterList, final Map<String, List<Filter>> filters) {
			super(jFrame, NAME, eventList, filterList, filters);
			this.tableFormat = tableFormat;
			this.program = program;
		}

		@Override
		protected Object getColumnValue(final TreeAsset item, final String column) {
			TreeTableFormat format = TreeTableFormat.valueOf(column);
			return format.getColumnValue(item);
		}

		@Override
		protected boolean isNumericColumn(final Enum<?> column) {
			TreeTableFormat format = (TreeTableFormat) column;
			if (Number.class.isAssignableFrom(format.getType())) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected boolean isDateColumn(final Enum<?> column) {
			TreeTableFormat format = (TreeTableFormat) column;
			if (format.getType().getName().equals(Date.class.getName())) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Enum[] getColumns() {
			return TreeTableFormat.values();
		}

		@Override
		protected Enum<?> valueOf(final String column) {
			return TreeTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<TreeAsset>> getEnumColumns() {
			return columnsAsList(TreeTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<TreeAsset>> getEnumShownColumns() {
			return new ArrayList<EnumTableColumn<TreeAsset>>(tableFormat.getShownColumns());
		}

		@Override
		protected void updateFilters() {
			if (program != null && program.getOverviewTab() != null) {
				program.getOverviewTab().updateFilters();
			}
		}
	}

	public static class AssetTreeTableCellEditor extends TreeTableCellEditor {

		private int indent;
		private int spacer;
		private DefaultEventTableModel<TreeAsset> tableModel;

		public AssetTreeTableCellEditor(TableCellEditor delegate, TreeList treeList, DefaultEventTableModel<TreeAsset> tableModel, int indent, int spacer) {
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

		public AssetTreeTableCellRenderer(TableCellRenderer delegate, TreeList treeList, DefaultEventTableModel<TreeAsset> tableModel, int indent, int spacer) {
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
			if (value instanceof HierarchyColumn) {
				jLabel.setText(jLabel.getText().trim());
			}
			return jPanel;
		}
	}
}
