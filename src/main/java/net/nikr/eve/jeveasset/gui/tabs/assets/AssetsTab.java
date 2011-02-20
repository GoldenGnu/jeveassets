/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.assets;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.JColumnTable;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.JMenuCopy;
import net.nikr.eve.jeveasset.gui.shared.JMenuLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AssetsTab extends JMainTab
		implements ActionListener, JColumnTable.ColumnTableListener {
	
	private final static Logger LOG = LoggerFactory.getLogger(AssetsTab.class);

	public final static String ACTION_BLUEPRINT_ORIGINAL = "ACTION_BLUEPRINT_ORIGINAL";
	public final static String ACTION_SET_USER_PRICE = "ACTION_SET_USER_PRICE";
	public final static String ACTION_SET_ITEM_NAME = "ACTION_SET_ITEM_NAME";
	public final static String ACTION_ADD_FILTER_CONTAIN = "ACTION_ADD_FILTER_CONTAIN";
	public final static String ACTION_ADD_FILTER_CONTAIN_NOT = "ACTION_ADD_FILTER_CONTAIN_NOT";
	public final static String ACTION_ADD_FILTER_EQUALS = "ACTION_ADD_FILTER_EQUALS";
	public final static String ACTION_ADD_FILTER_EQUALS_NOT = "ACTION_ADD_FILTER_EQUALS_NOT";
	public final static String ACTION_ADD_FILTER_GREATER_THEN = "ACTION_ADD_FILTER_GREATER_THEN";
	public final static String ACTION_ADD_FILTER_LESS_THEN = "ACTION_ADD_FILTER_LESS_THEN";
	public final static String ACTION_ADD_FILTER_GREATER_THEN_COLUMN = "ACTION_ADD_FILTER_GREATER_THEN_COLUMN";
	public final static String ACTION_ADD_FILTER_LESS_THEN_COLUMN = "ACTION_ADD_FILTER_LESS_THEN_COLUMN";

	//GUI
	private ToolPanel toolPanel;
	private JAssetTable jTable;
	
	private JPopupMenu jTablePopupMenu;
	

	private JLabel jTotalValue;
	private JLabel jCount;
	private JLabel jAverage;
	private JLabel jVolume;

	//Table Data
	private EventTableModel<EveAsset> eveAssetTableModel;
	private EventList<EveAsset> eveAssetEventList;
	private FilterList<EveAsset> filterList;
	
	public AssetsTab(Program program) {
		super(program, "Assets", Images.ICON_TOOL_ASSETS, false);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		eveAssetEventList = program.getEveAssetEventList();
		//For soring the table
		SortedList<EveAsset> sortedList = new SortedList<EveAsset>(eveAssetEventList);
		EveAssetTableFormat eveAssetTableFormat = new EveAssetTableFormat(program.getSettings());
		//For filtering the table
		filterList = new FilterList<EveAsset>(sortedList);
		MatcherEditorManager matcherEditorManager = new MatcherEditorManager(filterList, program);
		//Table Model
		eveAssetTableModel = new EventTableModel<EveAsset>(filterList, eveAssetTableFormat);
		//Table
		jTable = new JAssetTable(program, eveAssetTableModel, program.getSettings().getAssetTableSettings());
		jTable.setTableHeader( new EveAssetTableHeader(program, jTable.getColumnModel()) );
		jTable.getTableHeader().setReorderingAllowed(true);
		jTable.getTableHeader().setResizingAllowed(true);
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		//install the sorting/filtering
		TableComparatorChooser<EveAsset> eveAssetSorter = TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, eveAssetTableFormat);
		//Table Selection
		EventSelectionModel<EveAsset> selectionModel = new EventSelectionModel<EveAsset>(filterList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		jTable.addColumnTableListener(this);
		installTableMenu(jTable);

		//Filter panel(s)
		toolPanel = new ToolPanel(program, matcherEditorManager);

		jVolume = StatusPanel.createLabel("Total volume of shown assets", Images.ICON_VOLUME);
		this.addStatusbarLabel(jVolume);

		jCount = StatusPanel.createLabel("Total number of shown assets", Images.ICON_ADD); //Add
		this.addStatusbarLabel(jCount);

		jAverage = StatusPanel.createLabel("Average value of shown assets", Images.ICON_AVERAGE);
		this.addStatusbarLabel(jAverage);

		jTotalValue = StatusPanel.createLabel("Total value of shown assets", Images.ICON_TOOL_VALUES);
		this.addStatusbarLabel(jTotalValue);

		jTablePopupMenu = new JPopupMenu();

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGap(15)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(toolPanel.getPanel())
						.addComponent(jTable.getScroll(), 0, 0, Short.MAX_VALUE)
					)
					.addGap(15)
				)
			)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGap(5)
				.addComponent(toolPanel.getPanel())
				.addComponent(jTable.getScroll(), 0, 0, Short.MAX_VALUE)
		);
	}

	private void updateStatusbar(){
		double total = 0;
		long count = 0;
		double average = 0;
		float volume = 0;
		for (int a = 0; a < eveAssetTableModel.getRowCount(); a++){
			EveAsset eveAsset = eveAssetTableModel.getElementAt(a);
			total = total + (eveAsset.getPrice() * eveAsset.getCount());
			count = count + eveAsset.getCount();
			volume = volume + (eveAsset.getVolume() * eveAsset.getCount());
		}
		if (count > 0 && total > 0) average = total / count;
		jTotalValue.setText(Formater.iskFormat(total));
		jCount.setText(Formater.itemsFormat(count));
		jAverage.setText(Formater.iskFormat(average));
		jVolume.setText(Formater.doubleFormat(volume));
	}

	public void updateToolPanel(){
		String filter = "<i>Untitled</i>";
		if (getAssetFilters().isEmpty()){
			filter = "<i>Empty</i>";
		}
		if (program.getSettings().getAssetFilters().containsValue(getAssetFilters())){
			for (Map.Entry<String, List<AssetFilter>> entry : program.getSettings().getAssetFilters().entrySet()){
				if (entry.getValue().equals(getAssetFilters())){
					filter = entry.getKey();
					break;
				}
			}
		}
		toolPanel.setToolbarText(
				"<html><div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 11pt;\">"
				+"Showing "+jTable.getRowCount()+" of "+program.getEveAssetEventList().size()+" assets"
				+" ("+filter+")");
	}

	public EveAsset getSelectedAsset(){
		return eveAssetTableModel.getElementAt(jTable.getSelectedRow());
	}

	/**
	 * returns a new list of the filtered assets, thus the list is modifiable.
	 * @return a list of the filtered assets.
	 */
	public List<EveAsset> getFilteredAssets() {
		eveAssetEventList.getReadWriteLock().writeLock().lock();
		List<EveAsset> ret = new ArrayList<EveAsset>(filterList);
		eveAssetEventList.getReadWriteLock().writeLock().unlock();
		return ret;
	}

	public List<AssetFilter> getAssetFilters(){
		return toolPanel.getAssetFilters();
	}

	public void setAssetFilters(List<AssetFilter> assetFilters){
		toolPanel.setAssetFilters(assetFilters);
	}

	public void addFilter(AssetFilter assetFilter){
		toolPanel.addFilter(assetFilter);
	}

	public void clearFilters(){
		toolPanel.clearFilters();
	}

	public void addFilter(AssetFilter assetFilter, boolean unique){
		toolPanel.addFilter(assetFilter, unique);
	}

	public void savedFiltersChanged(){
		toolPanel.savedFiltersChanged();
		updateToolPanel();
	}

	

	@Override
	protected void showTablePopupMenu(MouseEvent e){
		boolean clickInRowsSelection = false;
		int[] selectedRows = jTable.getSelectedRows();
		for (int a = 0; a < selectedRows.length; a++){
			if (selectedRows[a] == jTable.rowAtPoint(e.getPoint())){
				clickInRowsSelection = true;
				break;
			}
		}

		boolean clickInColumnsSelection = false;
		int[] selectedColumns = jTable.getSelectedColumns();
		for (int a = 0; a < selectedColumns.length; a++){
			if (selectedColumns[a] == jTable.columnAtPoint(e.getPoint())){
				clickInColumnsSelection = true;
				break;
			}
		}

		//Clicked outside selection, select clicked cell
		if (!clickInRowsSelection || !clickInColumnsSelection){
			jTable.setRowSelectionInterval(jTable.rowAtPoint(e.getPoint()), jTable.rowAtPoint(e.getPoint()));
			jTable.setColumnSelectionInterval(jTable.columnAtPoint(e.getPoint()), jTable.columnAtPoint(e.getPoint()));
			selectedRows = jTable.getSelectedRows();
			selectedColumns = jTable.getSelectedColumns();
		}

		updateTableMenu(jTablePopupMenu);

		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void updateTableMenu(JComponent jComponent){
		jComponent.removeAll();
		jComponent.setEnabled(true);

		JMenuItem  jMenuItem;
		JCheckBoxMenuItem jCheckBoxMenuItem;
		JMenu jSubMenu;

		int[] selectedRows = jTable.getSelectedRows();
		int[] selectedColumns = jTable.getSelectedColumns();
		boolean isSingleCell = (selectedRows.length == 1 && selectedColumns.length == 1);
		boolean isSingleRow = selectedRows.length == 1;
		boolean isSelected = (jTable.getSelectedRows().length > 0 && jTable.getSelectedColumns().length > 0);

		boolean isBlueprints = false;
		boolean isBPOs = true;
		for (int a = 0; a < selectedRows.length; a++){
			EveAsset eveAsset = eveAssetTableModel.getElementAt(selectedRows[a]);
			if (eveAsset == null){
				isBlueprints = false;
				isBPOs = false;
				break;
			}
			if (eveAssetTableModel.getElementAt(selectedRows[a]).isBlueprint()){
				isBlueprints = true;
				if (!eveAsset.isBpo()){
					isBPOs = false;
				}
			} else {
				isBPOs = false;
				isBlueprints = false;
				break;
			}
		}
		if (!isBlueprints) isBPOs = false;

		boolean numericColumn = false;
		if (isSingleCell){
			String column = (String) jTable.getColumnModel().getColumn(selectedColumns[0]).getHeaderValue();
			numericColumn = program.getSettings().getAssetTableNumberColumns().contains(column);
		}
	//COPY
		if (isSelected && jComponent instanceof JPopupMenu){
			jComponent.add(new JMenuCopy(jTable));
			addSeparator(jComponent);
		}
	//COLUMNS
		if (jComponent instanceof JMenu){
			jComponent.add(jTable.getMenu());
			addSeparator(jComponent);
		}
	//FILTER
		jSubMenu = new JMenu("Add Filter");
		jSubMenu.setIcon(Images.ICON_TOOL_ASSETS);
		jComponent.add(jSubMenu);

		jMenuItem = new JMenuItem(AssetFilter.Mode.MODE_CONTAIN.toString());
		jMenuItem.setIcon(Images.ICON_CONTAIN);
		jMenuItem.setEnabled(isSingleCell);
		jMenuItem.setActionCommand(ACTION_ADD_FILTER_CONTAIN);
		jMenuItem.addActionListener(this);
		jSubMenu.add(jMenuItem);

		jMenuItem = new JMenuItem(AssetFilter.Mode.MODE_CONTAIN_NOT.toString());
		jMenuItem.setIcon(Images.ICON_NOT_CONTAIN);
		jMenuItem.setEnabled(isSingleCell);
		jMenuItem.setActionCommand(ACTION_ADD_FILTER_CONTAIN_NOT);
		jMenuItem.addActionListener(this);
		jSubMenu.add(jMenuItem);

		jMenuItem = new JMenuItem(AssetFilter.Mode.MODE_EQUALS.toString());
		jMenuItem.setIcon(Images.ICON_EQUAL);
		jMenuItem.setEnabled(isSingleCell);
		jMenuItem.setActionCommand(ACTION_ADD_FILTER_EQUALS);
		jMenuItem.addActionListener(this);
		jSubMenu.add(jMenuItem);

		jMenuItem = new JMenuItem(AssetFilter.Mode.MODE_EQUALS_NOT.toString());
		jMenuItem.setIcon(Images.ICON_NOT_EQUAL);
		jMenuItem.setEnabled(isSingleCell);
		jMenuItem.setActionCommand(ACTION_ADD_FILTER_EQUALS_NOT);
		jMenuItem.addActionListener(this);
		jSubMenu.add(jMenuItem);

		jMenuItem = new JMenuItem(AssetFilter.Mode.MODE_GREATER_THAN.toString());
		jMenuItem.setIcon(Images.ICON_GREATER_THEN);
		jMenuItem.setEnabled(isSingleCell);
		jMenuItem.setActionCommand(ACTION_ADD_FILTER_GREATER_THEN);
		jMenuItem.addActionListener(this);
		if (!numericColumn && isSingleCell){
			jMenuItem.setEnabled(false);
			jMenuItem.setToolTipText("Can only be used with numeric columns");
		}
		jSubMenu.add(jMenuItem);

		jMenuItem = new JMenuItem(AssetFilter.Mode.MODE_LESS_THAN.toString());
		jMenuItem.setIcon(Images.ICON_LESS_THEN);
		jMenuItem.setEnabled(isSingleCell);
		jMenuItem.setActionCommand(ACTION_ADD_FILTER_LESS_THEN);
		jMenuItem.addActionListener(this);
		if (!numericColumn && isSingleCell){
			jMenuItem.setEnabled(false);
			jMenuItem.setToolTipText("Can only be used with numeric columns");
		}
		jSubMenu.add(jMenuItem);

		jMenuItem = new JMenuItem(AssetFilter.Mode.MODE_GREATER_THAN_COLUMN.toString());
		jMenuItem.setIcon(Images.ICON_GREATER_THEN_COLUMN);
		jMenuItem.setEnabled(isSingleCell);
		jMenuItem.setActionCommand(ACTION_ADD_FILTER_GREATER_THEN_COLUMN);
		jMenuItem.addActionListener(this);
		if (!numericColumn && isSingleCell){
			jMenuItem.setEnabled(false);
			jMenuItem.setToolTipText("Can only be used with numeric columns");
		}
		jSubMenu.add(jMenuItem);

		jMenuItem = new JMenuItem(AssetFilter.Mode.MODE_LESS_THAN_COLUMN.toString());
		jMenuItem.setIcon(Images.ICON_LESS_THEN_COLUMN);
		jMenuItem.setEnabled(isSingleCell);
		jMenuItem.setActionCommand(ACTION_ADD_FILTER_LESS_THEN_COLUMN);
		jMenuItem.addActionListener(this);
		if (!numericColumn && isSingleCell){
			jMenuItem.setEnabled(false);
			jMenuItem.setToolTipText("Can only be used with numeric columns");
		}
		jSubMenu.add(jMenuItem);

	//LOOKUP
		jComponent.add(new JMenuLookup(program, isSingleRow ? eveAssetTableModel.getElementAt(selectedRows[0]) : null));

	//EDIT
		jSubMenu = new JMenu("Edit");
		jSubMenu.setIcon(Images.ICON_EDIT);
		jComponent.add(jSubMenu);

		jMenuItem = new JMenuItem("Price...");
		jMenuItem.setIcon(Images.ICON_USER_ITEM_PRICE);
		jMenuItem.setEnabled(isSingleRow);
		jMenuItem.setActionCommand(ACTION_SET_USER_PRICE);
		jMenuItem.addActionListener(program);
		jSubMenu.add(jMenuItem);

		jMenuItem = new JMenuItem("Name...");
		jMenuItem.setIcon(Images.ICON_USER_ITEM_NAME);
		jMenuItem.setEnabled(isSingleRow);
		jMenuItem.setActionCommand(ACTION_SET_ITEM_NAME);
		jMenuItem.addActionListener(program);
		jSubMenu.add(jMenuItem);

		jCheckBoxMenuItem = new JCheckBoxMenuItem("Blueprint Original");
		jCheckBoxMenuItem.setIcon(Images.ICON_TOOL_INDUSTRY_JOBS);
		jCheckBoxMenuItem.setEnabled(isBlueprints);
		jCheckBoxMenuItem.setActionCommand(ACTION_BLUEPRINT_ORIGINAL);
		jCheckBoxMenuItem.addActionListener(this);
		jCheckBoxMenuItem.setSelected(isBPOs);
		jSubMenu.add(jCheckBoxMenuItem);

	//INFO
		if (jComponent instanceof JPopupMenu){
			addSeparator(jComponent);

			jMenuItem = new JMenuItem("Selection Information");
			jMenuItem.setDisabledIcon(Images.ICON_DIALOG_ABOUT);
			jMenuItem.setEnabled(false);
			jComponent.add(jMenuItem);

			JPanel jSpacePanel = new JPanel();
			jSpacePanel.setMinimumSize( new Dimension(50, 5) );
			jSpacePanel.setPreferredSize( new Dimension(50, 5) );
			jSpacePanel.setMaximumSize( new Dimension(50, 5) );
			jComponent.add(jSpacePanel);

			double total = 0;
			long count = 0;
			float volume = 0;
			for (int a = 0; a < selectedRows.length; a++){
				EveAsset eveAsset = eveAssetTableModel.getElementAt(selectedRows[a]);
				total = total + (eveAsset.getPrice() * eveAsset.getCount());
				count = count + eveAsset.getCount();
				volume = volume + (eveAsset.getVolume() * eveAsset.getCount());
			}

			jMenuItem = new JMenuItem(Formater.iskFormat(total));
			jMenuItem.setDisabledIcon(Images.ICON_TOOL_VALUES);
			jMenuItem.setEnabled(false);
			jMenuItem.setToolTipText("Value of selected assets");
			jComponent.add(jMenuItem);

			jMenuItem = new JMenuItem( Formater.iskFormat(total/count) );
			jMenuItem.setDisabledIcon(Images.ICON_AVERAGE);
			jMenuItem.setEnabled(false);
			jMenuItem.setToolTipText("Average value of selected assets");
			jComponent.add(jMenuItem);

			jMenuItem = new JMenuItem( Formater.itemsFormat(count));
			jMenuItem.setDisabledIcon(Images.ICON_ADD);
			jMenuItem.setEnabled(false);
			jMenuItem.setToolTipText("Count of selected assets");
			jComponent.add(jMenuItem);

			jMenuItem = new JMenuItem( Formater.doubleFormat(volume));
			jMenuItem.setDisabledIcon(Images.ICON_VOLUME);
			jMenuItem.setEnabled(false);
			jMenuItem.setToolTipText("Volume of selected assets");
			jComponent.add(jMenuItem);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_ADD_FILTER_CONTAIN.equals(e.getActionCommand())){
			String text = String.valueOf(jTable.getValueAt(jTable.getSelectedRows()[0], jTable.getSelectedColumns()[0]));
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			addFilter( new AssetFilter(column, text, AssetFilter.Mode.MODE_CONTAIN, AssetFilter.Junction.AND, null));
		}
		if (ACTION_ADD_FILTER_CONTAIN_NOT.equals(e.getActionCommand())){
			String text = String.valueOf(jTable.getValueAt(jTable.getSelectedRows()[0], jTable.getSelectedColumns()[0]));
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			addFilter( new AssetFilter(column, text, AssetFilter.Mode.MODE_CONTAIN_NOT, AssetFilter.Junction.AND, null));
		}
		if (ACTION_ADD_FILTER_EQUALS.equals(e.getActionCommand())){
			String text = String.valueOf(jTable.getValueAt(jTable.getSelectedRows()[0], jTable.getSelectedColumns()[0]));
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			addFilter( new AssetFilter(column, text, AssetFilter.Mode.MODE_EQUALS, AssetFilter.Junction.AND, null));
		}
		if (ACTION_ADD_FILTER_EQUALS_NOT.equals(e.getActionCommand())){
			String text = String.valueOf(jTable.getValueAt(jTable.getSelectedRows()[0], jTable.getSelectedColumns()[0]));
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			addFilter( new AssetFilter(column, text, AssetFilter.Mode.MODE_EQUALS_NOT, AssetFilter.Junction.AND, null));
		}
		if (ACTION_ADD_FILTER_GREATER_THEN.equals(e.getActionCommand())){
			String text = String.valueOf(jTable.getValueAt(jTable.getSelectedRows()[0], jTable.getSelectedColumns()[0]));
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			addFilter( new AssetFilter(column, text, AssetFilter.Mode.MODE_GREATER_THAN, AssetFilter.Junction.AND, null));
		}
		if (ACTION_ADD_FILTER_LESS_THEN.equals(e.getActionCommand())){
			String text = String.valueOf(jTable.getValueAt(jTable.getSelectedRows()[0], jTable.getSelectedColumns()[0]));
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			addFilter( new AssetFilter(column, text, AssetFilter.Mode.MODE_LESS_THAN, AssetFilter.Junction.AND, null));
		}
		if (ACTION_ADD_FILTER_GREATER_THEN_COLUMN.equals(e.getActionCommand())){
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			addFilter( new AssetFilter(column, "", AssetFilter.Mode.MODE_GREATER_THAN_COLUMN, AssetFilter.Junction.AND, column));
		}
		if (ACTION_ADD_FILTER_LESS_THEN_COLUMN.equals(e.getActionCommand())){
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			addFilter( new AssetFilter(column, "", AssetFilter.Mode.MODE_LESS_THAN_COLUMN, AssetFilter.Junction.AND, column));
		}
		
		if (ACTION_BLUEPRINT_ORIGINAL.equals(e.getActionCommand())){
			JCheckBoxMenuItem jCheckBoxMenuItem = (JCheckBoxMenuItem) e.getSource();
			boolean bpo = jCheckBoxMenuItem.isSelected();
			int[] selectedRows = jTable.getSelectedRows();
			for (int a = 0; a < selectedRows.length; a++){
				EveAsset eveAsset = eveAssetTableModel.getElementAt(selectedRows[a]);
				if (bpo){
					if (!program.getSettings().getBpos().contains(eveAsset.getItemID())){
						program.getSettings().getBpos().add(eveAsset.getItemID());
					}
				} else {
					if (program.getSettings().getBpos().contains(eveAsset.getItemID())){
						program.getSettings().getBpos().remove(eveAsset.getItemID());
					}
				}
			}
			program.updateEventList();
			return;
		}
	}

	@Override
	public void tableUpdate(){
		updateStatusbar();
		updateToolPanel();
	}

	@Override
	public void updateData() {}
}
