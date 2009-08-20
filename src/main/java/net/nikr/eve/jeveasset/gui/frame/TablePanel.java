/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.gui.frame;

import java.awt.datatransfer.Transferable;
import javax.swing.event.TableModelEvent;
import net.nikr.eve.jeveasset.gui.shared.JProgramPanel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import net.nikr.eve.jeveasset.gui.table.EveAssetTableFormat;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.JDropDownButton;
import net.nikr.eve.jeveasset.gui.table.JAssetTable;
import net.nikr.eve.jeveasset.gui.table.DoubleCellRenderer;
import net.nikr.eve.jeveasset.gui.table.EveAssetTableHeader;
import net.nikr.eve.jeveasset.gui.table.LongCellRenderer;
import net.nikr.eve.jeveasset.gui.table.MatcherEditorManager;


public class TablePanel extends JProgramPanel implements MouseListener, ActionListener, TableColumnModelListener, ClipboardOwner, TableModelListener {

	public final static String ACTION_AUTO_RESIZING_COLUMNS_TEXT = "ACTION_AUTO_RESIZING_COLUMNS_TEXT";
	public final static String ACTION_AUTO_RESIZING_COLUMNS_WINDOW = "ACTION_AUTO_RESIZING_COLUMNS_WINDOW";
	public final static String ACTION_DISABLE_AUTO_RESIZING_COLUMNS = "ACTION_DISABLE_AUTO_RESIZING_COLUMNS";
	public final static String ACTION_RESET_COLUMNS_TO_DEFAULT = "ACTION_SHOW_ALL_COLUMNS";
	public final static String ACTION_COPY_TABLE_SELECTED_CELLS = "ACTION_COPY_TABLE_SELECTED_CELLS";
	public final static String ACTION_BLUEPRINT_ORIGINAL = "ACTION_BLUEPRINT_ORIGINAL";
	public final static String ACTION_SET_USER_PRICE = "ACTION_SET_USER_PRICE";
	public final static String ACTION_ADD_FILTER_CONTAIN = "ACTION_ADD_FILTER_CONTAIN";
	public final static String ACTION_ADD_FILTER_CONTAIN_NOT = "ACTION_ADD_FILTER_CONTAIN_NOT";
	public final static String ACTION_ADD_FILTER_EQUALS = "ACTION_ADD_FILTER_EQUALS";
	public final static String ACTION_ADD_FILTER_EQUALS_NOT = "ACTION_ADD_FILTER_EQUALS_NOT";
	public final static String ACTION_ADD_FILTER_ABOVE = "ACTION_ADD_FILTER_ABOVE";
	public final static String ACTION_ADD_FILTER_BELOW = "ACTION_ADD_FILTER_BELOW";

	//GUI
	private ToolPanel toolPanel;
	private StatusPanel statusPanel;
	private JTable jTable;
	private JDropDownButton jColumnsSelection;
	private JPopupMenu jTablePopupMenu;


	//Table Data
	private EveAssetTableFormat eveAssetTableFormat;
	private EventTableModel<EveAsset> eveAssetTableModel;
	private FilterList<EveAsset> eveAssetTextFiltered;
	private EventList<EveAsset> eveAssetEventList;
	
	//Data
	private boolean columnMoved = false;
	private List<String> tempMainTableColumnNames;
	private List<String> tempMainTableColumnVisible;
	
	public TablePanel(Program program) {
		super(program);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		eveAssetEventList = program.getEveAssetEventList();
		//For soring the table
		SortedList<EveAsset> eveAssetSortedList = new SortedList<EveAsset>(eveAssetEventList);
		eveAssetTableFormat = new EveAssetTableFormat(program.getSettings());
		//For filtering the table
		eveAssetTextFiltered = new FilterList<EveAsset>(eveAssetSortedList);
		MatcherEditorManager matcherEditorManager = new MatcherEditorManager(program, eveAssetTextFiltered);

		//Table
		eveAssetTableModel = new EventTableModel<EveAsset>(eveAssetTextFiltered, eveAssetTableFormat);
		eveAssetTableModel.addTableModelListener(this);

		jTable = new JAssetTable(eveAssetTableModel);
		jTable.setTableHeader( new EveAssetTableHeader(program, jTable.getColumnModel()) );
		jTable.getTableHeader().setReorderingAllowed(true);
		jTable.getTableHeader().setResizingAllowed(true);
		jTable.setCellSelectionEnabled(true);
		jTable.setRowSelectionAllowed(true);
		jTable.setColumnSelectionAllowed(true);
		jTable.getTableHeader().addMouseListener(this);
		jTable.getColumnModel().addColumnModelListener(this);
		jTable.addMouseListener(this);
		jTable.setDefaultRenderer(Double.class, new DoubleCellRenderer());
		jTable.setDefaultRenderer(Long.class, new LongCellRenderer());
		//install the sorting/filtering
		TableComparatorChooser<EveAsset> eveAssetSorter =
				TableComparatorChooser.install(jTable, eveAssetSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, eveAssetTableFormat);

		//Table Scrollpanel

		jColumnsSelection = new JDropDownButton(JDropDownButton.RIGHT);
		jColumnsSelection.setIcon( ImageGetter.getIcon("bullet_arrow_down.png") );
		jColumnsSelection.setHorizontalAlignment(SwingConstants.RIGHT);
		jColumnsSelection.setBorder(null);
		jColumnsSelection.addMouseListener(this);

		JScrollPane jTableSPanel = new JScrollPane(jTable);
		jTableSPanel.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, jColumnsSelection);
		jTableSPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jTableSPanel.setAutoscrolls(true);
		jTableSPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,0,0,0), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)) );
		this.getPanel().add(jTableSPanel);

		//Filter panel(s)
		toolPanel = new ToolPanel(program, matcherEditorManager);
		eveAssetTableModel.addTableModelListener(toolPanel);
		this.getPanel().add(toolPanel.getPanel());

		statusPanel = new StatusPanel(program);

		updateColumnSelectionPopup();
		
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGap(15)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(toolPanel.getPanel())
						.addComponent(jTableSPanel)
					)
					.addGap(15)
				)
				.addComponent(statusPanel.getPanel())
			)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGap(5)
				.addComponent(toolPanel.getPanel())
				.addComponent(jTableSPanel)
				.addComponent(statusPanel.getPanel(), 25, 25, 25)
		);
		
	}

	public EveAsset getSelectedAsset(){
		return eveAssetTableModel.getElementAt(jTable.getSelectedRow());
	}

	public void shownAssetsChanged(){
		updateAutoCoulmnsSize();
	}

	private void updateAutoCoulmnsSize(){
		if (program.getSettings().isAutoResizeColumnsText()){
			autoResizeColumns();
		}
		if (program.getSettings().isAutoResizeColumnsWindow()){
			jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
		if (!program.getSettings().isAutoResizeColumnsText() && !program.getSettings().isAutoResizeColumnsWindow()){
			jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}
	}

	private void resetSelection(){
		jTable.clearSelection();
		if (jTable.getModel().getRowCount() >  0){
			jTable.setRowSelectionInterval(0, 0);
			jTable.setColumnSelectionInterval(0, 0);
			
		}
	}

	private void updateTableStructure(){
		if (program.getSettings().isAutoResizeColumnsText()){
			eveAssetTableModel.fireTableStructureChanged();
			updateAutoCoulmnsSize();
		} else {
			Map<String, Integer> widths = new HashMap<String, Integer>();
			for (int a = 0; a < jTable.getColumnCount(); a++){
				//int width = jTable.getColumnModel().getColumn(a).getWidth();
				int width = jTable.getColumnModel().getColumn(a).getPreferredWidth();
				String name = (String)jTable.getColumnModel().getColumn(a).getHeaderValue();
				widths.put(name, width);
			}
			eveAssetTableModel.fireTableStructureChanged();
			for (int a = 0; a < jTable.getColumnCount(); a++){
				String name = (String)jTable.getColumnModel().getColumn(a).getHeaderValue();
				if (widths.containsKey(name)){
					int width = widths.get(name);
					jTable.getColumnModel().getColumn(a).setPreferredWidth(width);
				} else {
					autoResizeColumn(jTable.getColumnModel().getColumn(a));
				}
			}
		}
	}

	private void autoResizeColumns() {
		if (!eveAssetEventList.isEmpty()){
			jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			for (int i = 0; i < eveAssetTableModel.getColumnCount(); i++) {
				autoResizeColumn(jTable.getColumnModel().getColumn(i));
			}
			if(eveAssetTableModel.getRowCount() == 0){
				jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			}
		} else {
			jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
	}

	private void autoResizeColumn(TableColumn column) {
		int maxWidth = 0;
		TableCellRenderer renderer = column.getHeaderRenderer();
		if (renderer == null) {
			renderer = jTable.getTableHeader().getDefaultRenderer();
		}
		Component component = renderer.getTableCellRendererComponent(jTable, column.getHeaderValue(), false, false, 0, 0);
		maxWidth = component.getPreferredSize().width;
		for (int a = 0; a < jTable.getRowCount(); a++){
			renderer = jTable.getCellRenderer(a, column.getModelIndex());
			component = renderer.getTableCellRendererComponent(jTable, jTable.getValueAt(a, column.getModelIndex()), false, false, a, column.getModelIndex());
			maxWidth = Math.max(maxWidth, component.getPreferredSize().width);
		}
		column.setPreferredWidth(maxWidth+4);
	}
	private void updateColumnSelectionPopup(){
		jColumnsSelection.clearMenu();
		JCheckBoxMenuItem jCheckBoxMenuItem;
		JRadioButtonMenuItem jRadioButtonMenuItem;
		JMenuItem  jMenuItem;

		jMenuItem = new JMenuItem("Reset columns to default");
		jMenuItem.setActionCommand(ACTION_RESET_COLUMNS_TO_DEFAULT);
		jMenuItem.addActionListener(this);
		jColumnsSelection.add(jMenuItem);

		jColumnsSelection.addSeparator();
		
		ButtonGroup group = new ButtonGroup();

		jRadioButtonMenuItem = new JRadioButtonMenuItem("Auto resize columns to fit text");
		jRadioButtonMenuItem.setIcon( ImageGetter.getIcon("application_view_detail.png") );
		jRadioButtonMenuItem.setActionCommand(ACTION_AUTO_RESIZING_COLUMNS_TEXT);
		jRadioButtonMenuItem.addActionListener(this);
		jRadioButtonMenuItem.setSelected(program.getSettings().isAutoResizeColumnsText());
		group.add(jRadioButtonMenuItem);
		jColumnsSelection.add(jRadioButtonMenuItem);

		jRadioButtonMenuItem = new JRadioButtonMenuItem("Auto resize columns to fit in window");
		jRadioButtonMenuItem.setIcon( ImageGetter.getIcon("application_view_detail.png") );
		jRadioButtonMenuItem.setActionCommand(ACTION_AUTO_RESIZING_COLUMNS_WINDOW);
		jRadioButtonMenuItem.addActionListener(this);
		jRadioButtonMenuItem.setSelected(program.getSettings().isAutoResizeColumnsWindow());
		group.add(jRadioButtonMenuItem);
		jColumnsSelection.add(jRadioButtonMenuItem);

		jRadioButtonMenuItem = new JRadioButtonMenuItem("Disable columns auto resizing");
		jRadioButtonMenuItem.setIcon( ImageGetter.getIcon("application_view_detail.png") );
		jRadioButtonMenuItem.setActionCommand(ACTION_DISABLE_AUTO_RESIZING_COLUMNS);
		jRadioButtonMenuItem.addActionListener(this);
		jRadioButtonMenuItem.setSelected(!program.getSettings().isAutoResizeColumnsText() && !program.getSettings().isAutoResizeColumnsWindow());
		group.add(jRadioButtonMenuItem);
		jColumnsSelection.add(jRadioButtonMenuItem);

		jColumnsSelection.addSeparator();

		List<String> columns = program.getSettings().getTableColumnNames();
		for (int a = 0; a < columns.size(); a++){
			jCheckBoxMenuItem = new JCheckBoxMenuItem(columns.get(a));
			jCheckBoxMenuItem.setActionCommand(columns.get(a));
			jCheckBoxMenuItem.addActionListener(this);
			jCheckBoxMenuItem.setIcon( ImageGetter.getIcon("application_view_columns.png") );
			jCheckBoxMenuItem.setSelected(program.getSettings().getTableColumnVisible().contains(columns.get(a)));
			jColumnsSelection.add(jCheckBoxMenuItem);
		}
	}

	private void showTablePopup(MouseEvent e){
		jTablePopupMenu = new JPopupMenu();
		JMenuItem  jMenuItem;
		JCheckBoxMenuItem jCheckBoxMenuItem;
		JMenu jSubMenu;

		boolean clickInRowsSelection = false;
		int[] selectedRows = jTable.getSelectedRows();
		//int[] selectedRows = jTable.getSelectedRows();
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



		jMenuItem = new JMenuItem("Copy");
		jMenuItem.setIcon(  ImageGetter.getIcon("page_copy.png") );
		jMenuItem.setActionCommand(ACTION_COPY_TABLE_SELECTED_CELLS);
		jMenuItem.addActionListener(this);
		jTablePopupMenu.add(jMenuItem);

		boolean isBlueprints = false;
		boolean isBPOs = true;
		for (int a = 0; a < selectedRows.length; a++){
			EveAsset eveAsset = eveAssetTableModel.getElementAt(selectedRows[a]);
			if (eveAsset == null){
				isBlueprints = false;
				isBPOs = false;
				break;
			}
			if (eveAssetTableModel.getElementAt(selectedRows[0]).isBlueprint()){
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

		if (selectedRows.length == 1 && selectedColumns.length == 1){
			jMenuItem = new JMenuItem("Set price...");
			jMenuItem.setIcon(  ImageGetter.getIcon("money.png") );
			jMenuItem.setActionCommand(ACTION_SET_USER_PRICE);
			jMenuItem.addActionListener(program);
			jTablePopupMenu.add(jMenuItem);
		}
		if (isBlueprints){
			jCheckBoxMenuItem = new JCheckBoxMenuItem("Blueprint Original");
			jCheckBoxMenuItem.setIcon(  ImageGetter.getIcon("icon33_02.png") );
			jCheckBoxMenuItem.setActionCommand(ACTION_BLUEPRINT_ORIGINAL);
			jCheckBoxMenuItem.addActionListener(this);
			jCheckBoxMenuItem.setSelected(isBPOs);
			jTablePopupMenu.add(jCheckBoxMenuItem);
		}
		if (selectedRows.length == 1 && selectedColumns.length == 1){

			jSubMenu = new JMenu("Add Filter");
			//jSubMenu.setMnemonic(KeyEvent.VK_S);
			jTablePopupMenu.add(jSubMenu);

			jMenuItem = new JMenuItem(AssetFilter.MODE_CONTAIN);
			//jMenuItem.setIcon(  IconGettet.getIcon("page_copy.png") );
			jMenuItem.setActionCommand(ACTION_ADD_FILTER_CONTAIN);
			jMenuItem.addActionListener(this);
			jSubMenu.add(jMenuItem);

			jMenuItem = new JMenuItem(AssetFilter.MODE_CONTAIN_NOT);
			//jMenuItem.setIcon(  IconGettet.getIcon("page_copy.png") );
			jMenuItem.setActionCommand(ACTION_ADD_FILTER_CONTAIN_NOT);
			jMenuItem.addActionListener(this);
			jSubMenu.add(jMenuItem);

			jMenuItem = new JMenuItem(AssetFilter.MODE_EQUALS);
			//jMenuItem.setIcon(  IconGettet.getIcon("page_copy.png") );
			jMenuItem.setActionCommand(ACTION_ADD_FILTER_EQUALS);
			jMenuItem.addActionListener(this);
			jSubMenu.add(jMenuItem);

			jMenuItem = new JMenuItem(AssetFilter.MODE_EQUALS_NOT);
			//jMenuItem.setIcon(  IconGettet.getIcon("page_copy.png") );
			jMenuItem.setActionCommand(ACTION_ADD_FILTER_EQUALS_NOT);
			jMenuItem.addActionListener(this);
			jSubMenu.add(jMenuItem);

			String column = (String) jTable.getColumnModel().getColumn(selectedColumns[0]).getHeaderValue();
			if (program.getSettings().getTableNumberColumns().contains(column)){
				jMenuItem = new JMenuItem(AssetFilter.MODE_GREATER_THAN);
				//jMenuItem.setIcon(  IconGettet.getIcon("page_copy.png") );
				jMenuItem.setActionCommand(ACTION_ADD_FILTER_ABOVE);
				jMenuItem.addActionListener(this);
				jSubMenu.add(jMenuItem);

				jMenuItem = new JMenuItem(AssetFilter.MODE_LESS_THAN);
				//jMenuItem.setIcon(  IconGettet.getIcon("page_copy.png") );
				jMenuItem.setActionCommand(ACTION_ADD_FILTER_BELOW);
				jMenuItem.addActionListener(this);
				jSubMenu.add(jMenuItem);
			}
		}
		
		jTablePopupMenu.addSeparator();

		jMenuItem = new JMenuItem("Selection Information");
		jMenuItem.setDisabledIcon( ImageGetter.getIcon("information.png") );
		jMenuItem.setEnabled(false);
		jTablePopupMenu.add(jMenuItem);

		JPanel jPanel = new JPanel();
		jPanel.setMinimumSize( new Dimension(50, 5) );
		jPanel.setPreferredSize( new Dimension(50, 5) );
		jPanel.setMaximumSize( new Dimension(50, 5) );
		jTablePopupMenu.add(jPanel);

		double total = 0;
		long count = 0;
		for (int a = 0; a < selectedRows.length; a++){
			EveAsset eveAsset = eveAssetTableModel.getElementAt(selectedRows[a]);
			total = total + (eveAsset.getPrice() * eveAsset.getCount());
			count = count + eveAsset.getCount();
		}

		jMenuItem = new JMenuItem(Formater.isk(total));
		jMenuItem.setDisabledIcon( ImageGetter.getIcon("icon07_02.png") );
		jMenuItem.setEnabled(false);
		jMenuItem.setToolTipText("Value of selected assets");
		jTablePopupMenu.add(jMenuItem);

		jMenuItem = new JMenuItem( Formater.isk(total/count) );
		jMenuItem.setDisabledIcon( ImageGetter.getIcon("shape_align_middle.png") );
		jMenuItem.setEnabled(false);
		jMenuItem.setToolTipText("Average value of selected assets");
		jTablePopupMenu.add(jMenuItem);

		jMenuItem = new JMenuItem( Formater.count(count));
		jMenuItem.setDisabledIcon( ImageGetter.getIcon("add.png") );
		jMenuItem.setEnabled(false);
		jMenuItem.setToolTipText("Count of selected assets");
		jTablePopupMenu.add(jMenuItem);

		jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	private void copyToClipboard(Object o){
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			try {
				sm.checkSystemClipboardAccess();
			} catch (Exception ex) {
				return;
			}
		}
		Toolkit tk = Toolkit.getDefaultToolkit();
		StringSelection st =
		new StringSelection(String.valueOf(o));
		Clipboard cp = tk.getSystemClipboard();
		cp.setContents(st, this);
	}

	@Override
	protected JProgramPanel getThis(){
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_RESET_COLUMNS_TO_DEFAULT.equals(e.getActionCommand())){
			program.getSettings().resetMainTableColumns();
			updateTableStructure();
			updateColumnSelectionPopup();
			if (program.getSettings().isAutoResizeColumnsWindow()){
				for (int i = 0; i < eveAssetTableModel.getColumnCount(); i++) {
					jTable.getColumnModel().getColumn(i).setPreferredWidth(75);
				}
			}
			updateAutoCoulmnsSize();
		}
		if (ACTION_AUTO_RESIZING_COLUMNS_TEXT.equals(e.getActionCommand())){
			program.getSettings().setAutoResizeColumnsText(true);
			program.getSettings().setAutoResizeColumnsWindow(false);
			updateAutoCoulmnsSize();
		}
		if (ACTION_AUTO_RESIZING_COLUMNS_WINDOW.equals(e.getActionCommand())){
			program.getSettings().setAutoResizeColumnsText(false);
			program.getSettings().setAutoResizeColumnsWindow(true);
			for (int i = 0; i < eveAssetTableModel.getColumnCount(); i++) {
				jTable.getColumnModel().getColumn(i).setPreferredWidth(75);
			}
			updateAutoCoulmnsSize();
		}
		if (ACTION_DISABLE_AUTO_RESIZING_COLUMNS.equals(e.getActionCommand())){
			program.getSettings().setAutoResizeColumnsText(false);
			program.getSettings().setAutoResizeColumnsWindow(false);
			for (int a = 0; a < jTable.getColumnCount(); a++){
				int width = jTable.getColumnModel().getColumn(a).getWidth();
				jTable.getColumnModel().getColumn(a).setPreferredWidth(width);
			}
			updateAutoCoulmnsSize();
		}
		if (ACTION_COPY_TABLE_SELECTED_CELLS.equals(e.getActionCommand())){
			String s = "";
			int[] selectedRows = jTable.getSelectedRows();
			int[] selectedColumns = jTable.getSelectedColumns();
			for (int a = 0; a < selectedRows.length; a++){
				for (int b = 0; b < selectedColumns.length; b++){
					if (b != 0) s = s + "	";
					s = s + jTable.getValueAt(selectedRows[a], selectedColumns[b]);
				}
				if ( (a + 1) < selectedRows.length ) s = s + "\r\n";
			}
			copyToClipboard(s);
		}
		if (ACTION_ADD_FILTER_CONTAIN.equals(e.getActionCommand())){
			String text = String.valueOf(jTable.getValueAt(jTable.getSelectedRows()[0], jTable.getSelectedColumns()[0]));
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			toolPanel.addFilter( new AssetFilter(column, text, AssetFilter.MODE_CONTAIN, true));
		}
		if (ACTION_ADD_FILTER_CONTAIN_NOT.equals(e.getActionCommand())){
			String text = String.valueOf(jTable.getValueAt(jTable.getSelectedRows()[0], jTable.getSelectedColumns()[0]));
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			toolPanel.addFilter( new AssetFilter(column, text, AssetFilter.MODE_CONTAIN_NOT, true));
		}
		if (ACTION_ADD_FILTER_EQUALS.equals(e.getActionCommand())){
			String text = String.valueOf(jTable.getValueAt(jTable.getSelectedRows()[0], jTable.getSelectedColumns()[0]));
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			toolPanel.addFilter( new AssetFilter(column, text, AssetFilter.MODE_EQUALS, true));
		}
		if (ACTION_ADD_FILTER_EQUALS_NOT.equals(e.getActionCommand())){
			String text = String.valueOf(jTable.getValueAt(jTable.getSelectedRows()[0], jTable.getSelectedColumns()[0]));
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			toolPanel.addFilter( new AssetFilter(column, text, AssetFilter.MODE_EQUALS_NOT, true));
		}
		if (ACTION_ADD_FILTER_ABOVE.equals(e.getActionCommand())){
			String text = String.valueOf(jTable.getValueAt(jTable.getSelectedRows()[0], jTable.getSelectedColumns()[0]));
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			toolPanel.addFilter( new AssetFilter(column, text, AssetFilter.MODE_GREATER_THAN, true));
		}
		if (ACTION_ADD_FILTER_BELOW.equals(e.getActionCommand())){
			String text = String.valueOf(jTable.getValueAt(jTable.getSelectedRows()[0], jTable.getSelectedColumns()[0]));
			String column = (String) jTable.getTableHeader().getColumnModel().getColumn(jTable.getSelectedColumns()[0]).getHeaderValue();
			toolPanel.addFilter( new AssetFilter(column, text, AssetFilter.MODE_LESS_THAN, true));
		}

		if (ACTION_BLUEPRINT_ORIGINAL.equals(e.getActionCommand())){
			JCheckBoxMenuItem jCheckBoxMenuItem = (JCheckBoxMenuItem) e.getSource();
			boolean bpo = jCheckBoxMenuItem.isSelected();
			int[] selectedRows = jTable.getSelectedRows();
			for (int a = 0; a < selectedRows.length; a++){
				EveAsset eveAsset = eveAssetTableModel.getElementAt(selectedRows[a]);
				if (bpo){
					if (!program.getSettings().getBpos().contains(eveAsset.getId())){
						program.getSettings().getBpos().add(eveAsset.getId());
					}
				} else {
					if (program.getSettings().getBpos().contains(eveAsset.getId())){
						int index = program.getSettings().getBpos().indexOf(eveAsset.getId());
						program.getSettings().getBpos().remove(index);
					}
				}
			}


			EveAsset eveAsset = eveAssetTableModel.getElementAt(jTable.getSelectedRows()[0]);
			
			program.assetsChanged();
			return;
		}

		//Hide/show column
		if (e.getSource() instanceof JCheckBoxMenuItem){
			if (program.getSettings().getTableColumnVisible().contains(e.getActionCommand())){
				program.getSettings().getTableColumnVisible().remove(e.getActionCommand());
			} else {
				program.getSettings().getTableColumnVisible().add(e.getActionCommand());
				List<String> mainTableColumnNames = program.getSettings().getTableColumnNames();
				List<String> mainTableColumnVisible = new Vector<String>();
				for (int a = 0; a < mainTableColumnNames.size(); a++){
					if (program.getSettings().getTableColumnVisible().contains(mainTableColumnNames.get(a))){
						mainTableColumnVisible.add(mainTableColumnNames.get(a));
					}
				}
				program.getSettings().setTableColumnVisible(mainTableColumnVisible);
			}
			updateTableStructure();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource().equals(jTable.getTableHeader())){
			tempMainTableColumnNames = new Vector<String>(program.getSettings().getTableColumnNames());
			tempMainTableColumnVisible = new Vector<String>(program.getSettings().getTableColumnVisible());
		}
		if (e.getSource().equals(jTable) && e.isPopupTrigger()){
				showTablePopup(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource().equals(jTable.getTableHeader()) && columnMoved){
			columnMoved = false;
			program.getSettings().setTableColumnNames(tempMainTableColumnNames);
			program.getSettings().setTableColumnVisible(tempMainTableColumnVisible);
			updateTableStructure();
			updateColumnSelectionPopup();
		}
		if (e.getSource().equals(jTable) && e.isPopupTrigger()){
			showTablePopup(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void columnAdded(TableColumnModelEvent e) {}

	@Override
	public void columnRemoved(TableColumnModelEvent e) {}

	@Override
	public void columnMoved(TableColumnModelEvent e) {
		if (e.getFromIndex() != e.getToIndex()){
			columnMoved = true;
			
			String movingColumnName = tempMainTableColumnVisible.get(e.getFromIndex());
			String movingToColumnName = tempMainTableColumnVisible.get(e.getToIndex());

			int movingIndex = tempMainTableColumnNames.indexOf(movingColumnName);
			tempMainTableColumnNames.remove(movingIndex);
			
			int movingToIndex = tempMainTableColumnNames.indexOf(movingToColumnName);
			if (e.getToIndex() > e.getFromIndex()) movingToIndex = movingToIndex + 1;
			tempMainTableColumnNames.add(movingToIndex, movingColumnName);

			List<String> mainTableColumnVisible = new Vector<String>();
			String columnOrder = "";
			String columnVisible = "";
			for (int a = 0; a < tempMainTableColumnNames.size(); a++){
				columnOrder = columnOrder+tempMainTableColumnNames.get(a)+" ";
				if (program.getSettings().getTableColumnVisible().contains(tempMainTableColumnNames.get(a))){
					columnVisible = columnVisible+tempMainTableColumnNames.get(a)+" ";
					mainTableColumnVisible.add(tempMainTableColumnNames.get(a));
				}
			}
			tempMainTableColumnVisible = mainTableColumnVisible;
		}
	}


	@Override
	public void columnMarginChanged(ChangeEvent e) {}


	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}

	@Override
	public void tableChanged(TableModelEvent e) {
		resetSelection();
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
		program.getStatusPanel().setTotalValue(total);
		program.getStatusPanel().setCount(count);
		program.getStatusPanel().setAverage(average);
		program.getStatusPanel().setVolume(volume);
	}
}
