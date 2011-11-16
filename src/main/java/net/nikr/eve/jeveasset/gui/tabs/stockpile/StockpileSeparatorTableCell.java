/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import ca.odell.glazedlists.SeparatorList;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.SeparatorTableCell;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class StockpileSeparatorTableCell extends SeparatorTableCell<StockpileItem> implements HierarchyListener, AdjustmentListener{

	public final static String ACTION_DELETE_STOCKPILE = "ACTION_DELETE_STOCKPILE";
	public final static String ACTION_EDIT_STOCKPILE = "ACTION_EDIT_STOCKPILE";
	public final static String ACTION_CLONE_STOCKPILE = "ACTION_CLONE_STOCKPILE";
	public final static String ACTION_CLIPBOARD_STOCKPILE = "ACTION_CLIPBOARD_STOCKPILE";
	public final static String ACTION_ADD_ITEM = "ACTION_ADD_ITEM";
	public final static String ACTION_EDIT_ITEM = "ACTION_EDIT_ITEM";
	public final static String ACTION_DELETE_ITEM = "ACTION_DELETE_ITEM";
	
	private final JLabel jGroup;
	private final JLabel jColor;
	private final JDropDownButton jStockpile;
	private final JDropDownButton jItem;
	private final ActionListener actionListener;

	public StockpileSeparatorTableCell(JTable jTable, SeparatorList<StockpileItem> separatorList, ActionListener actionListener) {
		super(jTable, separatorList);
		this.actionListener = actionListener;
		
		jTable.addHierarchyListener(this);
		
		
		jColor = new JLabel();
		jColor.setOpaque(true);
		jColor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		
		jItem = new JDropDownButton(TabsStockpile.get().items());
		jItem.setOpaque(false);
		
		jStockpile = new JDropDownButton(TabsStockpile.get().stockpile());
		jStockpile.setOpaque(false);
		
		JMenuItem jMenuItem;
		
		jMenuItem = new JMenuItem(TabsStockpile.get().editStockpile());
		jMenuItem.setActionCommand(ACTION_EDIT_STOCKPILE);
		jMenuItem.addActionListener(actionListener);
		jStockpile.add(jMenuItem);
		
		jMenuItem = new JMenuItem(TabsStockpile.get().cloneStockpile());
		jMenuItem.setActionCommand(ACTION_CLONE_STOCKPILE);
		jMenuItem.addActionListener(actionListener);
		jStockpile.add(jMenuItem);
		
		jMenuItem = new JMenuItem(TabsStockpile.get().deleteStockpile());
		jMenuItem.setActionCommand(ACTION_DELETE_STOCKPILE);
		jMenuItem.addActionListener(actionListener);
		jStockpile.add(jMenuItem);
		
		jStockpile.addSeparator();	
		
		jMenuItem = new JMenuItem(TabsStockpile.get().clipboardStockpile());
		jMenuItem.setActionCommand(ACTION_CLIPBOARD_STOCKPILE);
		jMenuItem.addActionListener(actionListener);
		jStockpile.add(jMenuItem);
		
		jGroup = new JLabel();
		jGroup.setBorder(null);
		jGroup.setOpaque(false);
		jGroup.setBackground(Color.BLACK);
		jGroup.setFont(new Font(jGroup.getFont().getName(), Font.BOLD, jGroup.getFont().getSize()+1));
		
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jExpand)
					.addGap(1)
					.addGap(10)
					.addComponent(jColor, Program.BUTTONS_HEIGHT-6, Program.BUTTONS_HEIGHT-6, Program.BUTTONS_HEIGHT-6)
					.addGap(10)
					.addComponent(jGroup)
					.addGap(10)
					.addComponent(jItem, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jStockpile, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGap(2)
				.addGroup(layout.createParallelGroup()
					.addComponent(jExpand, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jGroup, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jItem, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStockpile, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addGroup(layout.createSequentialGroup()
						.addGap(3)
						.addComponent(jColor, Program.BUTTONS_HEIGHT-6, Program.BUTTONS_HEIGHT-6, Program.BUTTONS_HEIGHT-6)
					)
				)
				.addGap(2)
		);
	}
	
	@Override
	protected void configure(SeparatorList.Separator<?> separator) {
		StockpileItem stockpileItem = (StockpileItem) separator.first();
		if(stockpileItem == null) return; // handle 'late' rendering calls after this separator is invalid
		
		jGroup.setText(stockpileItem.getStockpile().getName());	
		
		JMenuItem jMenuItem;
		jItem.removeAll();
		
		jColor.setBackground(stockpileItem.getStockpile().isOK() ? new Color(200,255,200) : new Color(255,200,200));
		
		JMenuItem jAdd = new JMenuItem(TabsStockpile.get().addItem());
		jAdd.setActionCommand(ACTION_ADD_ITEM);
		jAdd.addActionListener(actionListener);
		jItem.add(jAdd);
		
		JMenu jEdit = new JMenu(TabsStockpile.get().editItem());
		jEdit.setEnabled(!stockpileItem.getStockpile().isEmpty());
		jItem.add(jEdit);
		
		JMenu jDelete = new JMenu(TabsStockpile.get().deleteItem());
		jDelete.setEnabled(!stockpileItem.getStockpile().isEmpty());
		jItem.add(jDelete);
		
		for (StockpileItem item : stockpileItem.getStockpile().getItems()){
			if (item.getTypeID() > 0){ //Ignore Total
				jMenuItem = new JStockpileMenuItem(item);
				jMenuItem.setActionCommand(ACTION_EDIT_ITEM);
				jMenuItem.addActionListener(actionListener);
				jEdit.add(jMenuItem);

				jMenuItem = new JStockpileMenuItem(item);
				jMenuItem.setActionCommand(ACTION_DELETE_ITEM);
				jMenuItem.addActionListener(actionListener);
				jDelete.add(jMenuItem);
			}
		}
	}
	
	protected JViewport getParentViewport(){
		Container container = jTable.getParent();
		if (container instanceof JViewport){
			return (JViewport) container;
		} else {
			return null;
		}
	}

	@Override
	public void hierarchyChanged(HierarchyEvent e) {
		if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) == HierarchyEvent.PARENT_CHANGED){
			JViewport jViewport = getParentViewport();
			if (jViewport != null){
				Container container = getParentViewport().getParent();
				if (container instanceof JScrollPane){
					JScrollPane jScroll = (JScrollPane) container;
					jScroll.getVerticalScrollBar().removeAdjustmentListener(this);
					jScroll.getHorizontalScrollBar().removeAdjustmentListener(this);
					jScroll.getVerticalScrollBar().addAdjustmentListener(this);
					jScroll.getHorizontalScrollBar().addAdjustmentListener(this);
				}
			}
		}
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (!e.getValueIsAdjusting()){
			int position = getParentViewport().getViewPosition().x;
			int width = getParentViewport().getSize().width;
			int offset = width + position - 250;
			jGroup.setMaximumSize(new Dimension(offset, jGroup.getMaximumSize().height) );
			if (jTable.isEditing() && (jTable.getCellEditor().getCellEditorValue() instanceof SeparatorList.Separator<?>)){
				int selectedRow = jTable.getSelectedRow();
				jTable.getCellEditor().cancelCellEditing();
				jTable.editCellAt(selectedRow, 0);
			}
			jTable.repaint();
		}
	}
	
	public static class JStockpileMenuItem extends JMenuItem{

		private StockpileItem item;
		
		public JStockpileMenuItem(StockpileItem item) {
			super(item.toString());
			this.item = item;
		}

		public StockpileItem getItem() {
			return item;
		}
	} 
	
}
