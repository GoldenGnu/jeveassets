/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import ca.odell.glazedlists.SeparatorList;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.components.JDoubleField;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.table.SeparatorTableCell;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class StockpileSeparatorTableCell extends SeparatorTableCell<StockpileItem> {

	public enum StockpileCellAction {
		GROUP_NEW,
		GROUP_CHANGE_ADD,
		GROUP_TOGGLE_COLLAPSE,
		GROUP_EXPAND,
		GROUP_RENAME,
		GROUP_COLLAPSE,
		DELETE_STOCKPILE,
		EDIT_STOCKPILE,
		CLONE_STOCKPILE,
		HIDE_STOCKPILE,
		SHOPPING_LIST_SINGLE,
		ADD_ITEM,
		SUBPILES,
		UPDATE_MULTIPLIER
	}

	private final static List<JCheckBoxMenuItem> jGroupMenuItems = new ArrayList<>();
	private final JPanel jGroupPanel;
	private final JLabel jGroup;
	private final JMenu jGroupMenu;
	private final JMenuItem jGroupNew;
	private final JButton jExpandGroup;
	private final JButton jCollapseGroupStockpiles;
	private final JButton jExpandGroupStockpiles;
	private final JButton jRenameGroup;
	private final JPanel jInfoPanel;
	private final JLabel jStartSpaceGroup;
	private final JLabel jStartSpace;
	private final JLabel jColor;
	private final JLabel jColorDisabled;
	private final JDropDownButton jStockpile;
	private final JDoubleField jMultiplier;
	private final JLabel jMultiplierLabel;
	private final JLabel jName;
	private final JLabel jAvailableLabel;
	private final JLabel jAvailable;
	private final JLabel jOwnerLabel;
	private final JLabel jOwner;
	private final JLabel jLocation;
	private final JLabel jLocationLabel;
	private final Program program;

	private Component focusOwner;

	public StockpileSeparatorTableCell(final Program program, final JTable jTable, final SeparatorList<StockpileItem> separatorList, final ActionListener actionListener) {
		super(jTable, separatorList);
		this.program = program;

		ListenerClass listener = new ListenerClass();
		addCellEditorListener(listener);

		jTable.addHierarchyListener(listener);

		jGroupPanel = new JPanel();
		jGroupPanel.setBackground(Color.BLACK);
		GroupLayout groupLayout = new GroupLayout(jGroupPanel);
		jGroupPanel.setLayout(groupLayout);
		groupLayout.setAutoCreateGaps(false);
		groupLayout.setAutoCreateContainerGaps(false);

		jStartSpaceGroup = new JLabel();

		jExpandGroup = new JButton(Images.MISC_COLLAPSED_WHITE.getIcon());
		jExpandGroup.setOpaque(true);
		jExpandGroup.setContentAreaFilled(false);
		jExpandGroup.setBorder(EMPTY_TWO_PIXEL_BORDER);
		jExpandGroup.setBackground(Color.BLACK);
		jExpandGroup.setActionCommand(StockpileCellAction.GROUP_TOGGLE_COLLAPSE.name());
		jExpandGroup.addActionListener(actionListener);

		jCollapseGroupStockpiles = new JButton(Images.MISC_COLLAPSED.getIcon());
		jCollapseGroupStockpiles.setOpaque(false);
		jCollapseGroupStockpiles.setActionCommand(StockpileCellAction.GROUP_COLLAPSE.name());
		jCollapseGroupStockpiles.addActionListener(actionListener);

		jExpandGroupStockpiles = new JButton(Images.MISC_EXPANDED.getIcon());
		jExpandGroupStockpiles.setOpaque(false);
		jExpandGroupStockpiles.setActionCommand(StockpileCellAction.GROUP_EXPAND.name());
		jExpandGroupStockpiles.addActionListener(actionListener);

		jRenameGroup = new JButton(Images.EDIT_EDIT_WHITE.getIcon());
		jRenameGroup.setOpaque(false);
		jRenameGroup.setActionCommand(StockpileCellAction.GROUP_RENAME.name());
		jRenameGroup.addActionListener(actionListener);

		jGroup = new JLabel();
		jGroup.setBorder(null);
		jGroup.setForeground(Color.WHITE);
		Font font = jGroup.getFont();
		jGroup.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 1));
		jGroup.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() >= 2) {
					actionListener.actionPerformed(new ActionEvent(jGroup, MouseEvent.MOUSE_RELEASED, StockpileCellAction.GROUP_TOGGLE_COLLAPSE.name()));
				}
			}
		});

		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup()
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(jStartSpaceGroup)
					.addComponent(jExpandGroup)
					.addGap(5)
					.addComponent(jCollapseGroupStockpiles, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth())
					.addGap(5)
					.addComponent(jExpandGroupStockpiles, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth())
					.addGap(5)
					.addComponent(jRenameGroup, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth())
					.addGap(10)
					.addComponent(jGroup, 0, 0, Integer.MAX_VALUE)
				)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createSequentialGroup()
				.addGap(2)
				.addGroup(groupLayout.createParallelGroup()
					.addComponent(jStartSpaceGroup, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jExpandGroup, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCollapseGroupStockpiles, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jExpandGroupStockpiles, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRenameGroup, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jGroup, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGap(2)
		);

		jInfoPanel = new JPanel();
		jInfoPanel.setOpaque(false);
		GroupLayout infoLayout = new GroupLayout(jInfoPanel);
		jInfoPanel.setLayout(infoLayout);
		infoLayout.setAutoCreateGaps(false);
		infoLayout.setAutoCreateContainerGaps(false);

		jStartSpace = new JLabel();

		jColor = new JLabel();
		jColor.setOpaque(true);
		jColor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		jColorDisabled = new JLabel();
		jColorDisabled.setOpaque(false);
		jColorDisabled.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		jColorDisabled.setVisible(false);

		jMultiplier = new JDoubleField("1", DocumentFactory.ValueFlag.POSITIVE_AND_NOT_ZERO);
		jMultiplier.setAutoSelectAll(true);
		jMultiplier.setHorizontalAlignment(JTextField.RIGHT);
		jMultiplier.setActionCommand(StockpileCellAction.UPDATE_MULTIPLIER.name());
		jMultiplier.addActionListener(listener);
		jMultiplier.addKeyListener(listener);

		jMultiplierLabel = new JLabel(TabsStockpile.get().multiplierSign());

		jName = createLabel("");

		//Available
		jAvailableLabel = createLabel(TabsStockpile.get().stockpileAvailable());
		jAvailable = createLabel();

		//Owner
		jOwnerLabel = createLabel(TabsStockpile.get().stockpileOwner());
		jOwner = createLabel();

		//Location
		jLocationLabel = createLabel(TabsStockpile.get().stockpileLocation());
		jLocation = createLabel();

		//Stockpile Edit/Add/etc.
		jStockpile = new JDropDownButton(TabsStockpile.get().stockpile());
		jStockpile.setOpaque(false);
		jStockpile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { //Update when shown
				//Group Menu
				jGroupMenu.removeAll();
				jGroupMenu.add(jGroupNew);

				if (!jGroupMenuItems.isEmpty()) {
					jGroupMenu.addSeparator();
				}
				StockpileItem stockpileItem = (StockpileItem) currentSeparator.first();
				if (stockpileItem == null) { // handle 'late' rendering calls after this separator is invalid
					return;
				}
				String group = stockpileItem.getGroup();
				for (JCheckBoxMenuItem jMenuItem : jGroupMenuItems) {
					jMenuItem.setSelected(group.equals(jMenuItem.getText()));
					jGroupMenu.add(jMenuItem);
				}
			}
		});

		JMenuItem jMenuItem;

		JMenuItem jAdd = new JMenuItem(TabsStockpile.get().addItem(), Images.EDIT_ADD.getIcon());
		jAdd.setActionCommand(StockpileCellAction.ADD_ITEM.name());
		jAdd.addActionListener(actionListener);
		jStockpile.add(jAdd);

		jStockpile.addSeparator();

		jMenuItem = new JMenuItem(TabsStockpile.get().editStockpile(), Images.EDIT_EDIT.getIcon());
		jMenuItem.setActionCommand(StockpileCellAction.EDIT_STOCKPILE.name());
		jMenuItem.addActionListener(actionListener);
		jStockpile.add(jMenuItem);

		jMenuItem = new JMenuItem(TabsStockpile.get().cloneStockpile(), Images.EDIT_COPY.getIcon());
		jMenuItem.setActionCommand(StockpileCellAction.CLONE_STOCKPILE.name());
		jMenuItem.addActionListener(actionListener);
		jStockpile.add(jMenuItem);

		jMenuItem = new JMenuItem(TabsStockpile.get().hideStockpile(), Images.EDIT_SHOW.getIcon());
		jMenuItem.setActionCommand(StockpileCellAction.HIDE_STOCKPILE.name());
		jMenuItem.addActionListener(actionListener);
		jStockpile.add(jMenuItem);

		jMenuItem = new JMenuItem(TabsStockpile.get().deleteStockpile(), Images.EDIT_DELETE.getIcon());
		jMenuItem.setActionCommand(StockpileCellAction.DELETE_STOCKPILE.name());
		jMenuItem.addActionListener(actionListener);
		jStockpile.add(jMenuItem);

		jStockpile.addSeparator();

		jGroupMenu = new JMenu(TabsStockpile.get().groupMenu());
		jGroupMenu.setIcon(Images.FILTER_LOAD.getIcon());
		jStockpile.add(jGroupMenu);

		jGroupNew = new JMenuItem(TabsStockpile.get().groupAddNew(), Images.EDIT_ADD.getIcon());
		jGroupNew.setActionCommand(StockpileCellAction.GROUP_NEW.name());
		jGroupNew.addActionListener(actionListener);
		jGroupMenu.add(jGroupNew);

		jStockpile.addSeparator();

		JMenuItem jSubStockpile = new JMenuItem(TabsStockpile.get().subpiles(), Images.TOOL_STOCKPILE.getIcon());
		jSubStockpile.setActionCommand(StockpileCellAction.SUBPILES.name());
		jSubStockpile.addActionListener(actionListener);
		jStockpile.add(jSubStockpile);

		jStockpile.addSeparator();

		jMenuItem = new JMenuItem(TabsStockpile.get().getShoppingList(), Images.STOCKPILE_SHOPPING_LIST.getIcon());
		jMenuItem.setActionCommand(StockpileCellAction.SHOPPING_LIST_SINGLE.name());
		jMenuItem.addActionListener(actionListener);
		jStockpile.add(jMenuItem);

		infoLayout.setHorizontalGroup(
			infoLayout.createParallelGroup()
				.addGroup(infoLayout.createSequentialGroup()
					.addComponent(jStartSpace)
					.addComponent(jExpand)
					.addGap(5)
					.addComponent(jColor, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6)
					.addComponent(jColorDisabled, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6)
					.addGap(10)
					.addComponent(jStockpile, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addGap(5)
					.addComponent(jMultiplier, 50, 50, 50)
					.addComponent(jMultiplierLabel)
					.addGap(10)
					.addComponent(jName, 150, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(jAvailableLabel)
					.addGap(5)
					.addComponent(jAvailable, 30, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(jOwnerLabel)
					.addGap(5)
					.addComponent(jOwner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(jLocationLabel)
					.addGap(5)
					.addComponent(jLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
		);
		infoLayout.setVerticalGroup(
			infoLayout.createSequentialGroup()
				.addGap(2)
				.addGroup(infoLayout.createParallelGroup()
					.addComponent(jStartSpace, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addGroup(infoLayout.createSequentialGroup()
						.addGap(3)
						.addComponent(jColor, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6)
						.addComponent(jColorDisabled, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6)
					)
					.addComponent(jStockpile, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMultiplier, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMultiplierLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jName, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addGroup(infoLayout.createSequentialGroup()
						.addGap(4)
						.addGroup(infoLayout.createParallelGroup()
							.addComponent(jAvailableLabel)
							.addComponent(jAvailable)
							.addComponent(jOwnerLabel)
							.addComponent(jOwner)
							.addComponent(jLocationLabel)
							.addComponent(jLocation)
						)
					)
				)
				.addGap(2)
		);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(jGroupPanel)
				.addComponent(jInfoPanel)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jGroupPanel)
				.addComponent(jInfoPanel)
		);
		updateGroups(actionListener);
	}

	public static void updateGroups(ActionListener actionListener) {
		Set<String> groups = Settings.get().getStockpileGroupSettings().getGroups();
		jGroupMenuItems.clear();

		for (String g : groups) {
			if (g.isEmpty()) {
				continue;
			}
			JCheckBoxMenuItem jMenuItem = new JCheckBoxMenuItem(g);
			jMenuItem.setActionCommand(StockpileCellAction.GROUP_CHANGE_ADD.name());
			jMenuItem.addActionListener(actionListener);
			jGroupMenuItems.add(jMenuItem);
		}
	}

	private JLabel createLabel() {
		return createLabel(null);
	}

	private JLabel createLabel(String text) {
		JLabel jLabel = new JLabel();
		jLabel.setBorder(null);
		jLabel.setOpaque(false);
		//jLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		if (text != null) {
			jLabel.setText(text);
			jLabel.setFont(new Font(jLabel.getFont().getName(), Font.BOLD, jLabel.getFont().getSize() + 1));
		}
		return jLabel;
	}

	private void setEnabled(final boolean enabled) {
		if (!enabled) { //Save focus owner
			focusOwner = program.getMainWindow().getFrame().getFocusOwner();
		}
		jExpandGroup.setEnabled(enabled);
		jCollapseGroupStockpiles.setEnabled(enabled);
		jExpandGroupStockpiles.setEnabled(enabled);
		jExpand.setEnabled(enabled);
		jColor.setVisible(enabled);
		jColorDisabled.setVisible(!enabled);
		jStockpile.setEnabled(enabled);
		jMultiplier.setEnabled(enabled);
		jMultiplierLabel.setEnabled(enabled);
		jName.setEnabled(enabled);
		jAvailableLabel.setEnabled(enabled);
		jAvailable.setEnabled(enabled);
		jOwnerLabel.setEnabled(enabled);
		jOwner.setEnabled(enabled);
		jLocation.setEnabled(enabled);
		jLocationLabel.setEnabled(enabled);
		if (enabled && focusOwner != null) { //Load focus owner
			focusOwner.requestFocusInWindow();
		}
	}

	@Override
	protected void configure(final SeparatorList.Separator<?> separator) {
		StockpileItem stockpileItem = (StockpileItem) separator.first();
		if (stockpileItem == null) { // handle 'late' rendering calls after this separator is invalid
			return;
		}
		//Color
		if (Settings.get().isStockpileHalfColors()) {
			if (stockpileItem.getStockpile().getPercentFull() >= (Settings.get().getStockpileColorGroup3() / 100.0) ) {
				ColorSettings.config(jColor, ColorEntry.STOCKPILE_ICON_OVER_THRESHOLD);
			} else if (stockpileItem.getStockpile().getPercentFull() >= (Settings.get().getStockpileColorGroup2() / 100.0)) {
				ColorSettings.config(jColor, ColorEntry.STOCKPILE_ICON_BELOW_THRESHOLD_2ND);
			} else {
				ColorSettings.config(jColor, ColorEntry.STOCKPILE_ICON_BELOW_THRESHOLD);
			}
		} else {
			if (stockpileItem.getStockpile().getPercentFull() >= (Settings.get().getStockpileColorGroup2() / 100.0)) {
				ColorSettings.config(jColor, ColorEntry.STOCKPILE_ICON_OVER_THRESHOLD);
			} else {
				ColorSettings.config(jColor, ColorEntry.STOCKPILE_ICON_BELOW_THRESHOLD);
			}
		}
		//Group
		String group = stockpileItem.getGroup();
		jGroup.setText(group);
		if (Settings.get().getStockpileGroupSettings().isGroupExpanded(group)) {
			jInfoPanel.setVisible(true);
			jExpandGroup.setIcon(Images.MISC_COLLAPSED_WHITE.getIcon());
		} else {
			jInfoPanel.setVisible(false);
			jExpandGroup.setIcon(Images.MISC_EXPANDED_WHITE.getIcon());
		}
		if (Settings.get().getStockpileGroupSettings().isGroupFirst(stockpileItem.getStockpile())) {
			jGroupPanel.setVisible(true);
		} else {
			jGroupPanel.setVisible(false);
		}

		//Multiplier
		jMultiplier.setText(Formatter.compareFormat(stockpileItem.getStockpile().getMultiplier()));
		//Name
		jName.setText(stockpileItem.getStockpile().getName());
		//Available
		String available = Formatter.doubleFormat(stockpileItem.getStockpile().getPercentFull());
		jAvailable.setText(available);
		//Owner
		String owner = stockpileItem.getStockpile().getOwnerName();
		jOwner.setText(owner);
		//Location
		String location = stockpileItem.getStockpile().getLocationName();
		jLocation.setText(location);
	}

	protected JViewport getParentViewport() {
		Container container = jTable.getParent();
		if (container instanceof JViewport) {
			return (JViewport) container;
		} else {
			return null;
		}
	}

	private class ListenerClass implements HierarchyListener, AdjustmentListener, ActionListener, CellEditorListener, KeyListener {

		private boolean update = true;

		@Override
		public void hierarchyChanged(final HierarchyEvent e) {
			if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) == HierarchyEvent.PARENT_CHANGED) {
				JViewport jViewport = getParentViewport();
				if (jViewport != null) {
					Container container = getParentViewport().getParent();
					if (container instanceof JScrollPane) {
						JScrollPane jScroll = (JScrollPane) container;
						//jScroll.getVerticalScrollBar().removeAdjustmentListener(this);
						jScroll.getHorizontalScrollBar().removeAdjustmentListener(this);
						//jScroll.getVerticalScrollBar().addAdjustmentListener(this);
						jScroll.getHorizontalScrollBar().addAdjustmentListener(this);
					}
				}
			}
		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent e) {
			if (!e.getValueIsAdjusting()) {
				int position = getParentViewport().getViewPosition().x;
				jStartSpace.setMinimumSize(new Dimension(position, Program.getButtonsHeight()));
				jStartSpaceGroup.setMinimumSize(new Dimension(position, Program.getButtonsHeight()));
				setEnabled(true);
				jTable.repaint();
			} else {
				if (jExpand.isEnabled()) { //Only do once
					setEnabled(false);
					jTable.repaint();
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (StockpileCellAction.UPDATE_MULTIPLIER.name().equals(e.getActionCommand())) { //Multiplier
				stopCellEditing();
			}
		}

		@Override
		public void editingStopped(ChangeEvent e) {
			saveCount();
		}

		@Override
		public void editingCanceled(ChangeEvent e) {
			saveCount();
		}

		@Override
		public void keyTyped(KeyEvent e) { }

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				update = false;
				stopCellEditing();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) { }

		private void saveCount() {
			if (!update) {
				update = true;
				return;
			}
			StockpileItem stockpileItem = (StockpileItem) currentSeparator.first();
			if (stockpileItem == null) { // handle 'late' rendering calls after this separator is invalid
				return;
			}
			program.getStockpileTab().setMultiplyer(stockpileItem.getStockpile(), jMultiplier);
		}

	}
}
