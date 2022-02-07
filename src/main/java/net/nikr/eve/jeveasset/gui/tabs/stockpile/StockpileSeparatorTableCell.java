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
import java.awt.event.*;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.Colors;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.Formater;
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
		DELETE_STOCKPILE,
		EDIT_STOCKPILE,
		CLONE_STOCKPILE,
		HIDE_STOCKPILE,
		SHOPPING_LIST_SINGLE,
		ADD_ITEM,
		SUBPILES,
		UPDATE_MULTIPLIER
	}

	private final JLabel jStartSpace;
	private final JLabel jGroup;
	private final JLabel jColor;
	private final JLabel jColorDisabled;
	private final JDropDownButton jStockpile;
	private final JLabel jOwnerLabel;
	private final JLabel jOwner;
	private final JLabel jLocation;
	private final JLabel jLocationLabel;
	private final JLabel jMultiplierLabel;
	private final JDoubleField jMultiplier;
	private final Program program;

	private Component focusOwner;

	public StockpileSeparatorTableCell(final Program program, final JTable jTable, final SeparatorList<StockpileItem> separatorList, final ActionListener actionListener) {
		super(jTable, separatorList);
		this.program = program;

		ListenerClass listener = new ListenerClass();

		jTable.addHierarchyListener(listener);

		jStartSpace = new JLabel();

		jColor = new JLabel();
		jColor.setOpaque(true);
		jColor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		jColorDisabled = new JLabel();
		jColorDisabled.setOpaque(false);
		jColorDisabled.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		jColorDisabled.setVisible(false);

		jMultiplier = new JDoubleField("1", DocumentFactory.ValueFlag.POSITIVE_AND_NOT_ZERO);
		jMultiplier.setActionCommand(StockpileCellAction.UPDATE_MULTIPLIER.name());
		jMultiplier.addActionListener(actionListener);
		jMultiplier.setHorizontalAlignment(JTextField.RIGHT);
		jMultiplier.setOpaque(false);
		jMultiplier.setBackground(Colors.COMPONENT_TRANSPARENT.getColor());
		jMultiplier.setBorder(null);
		jMultiplier.setAutoSelectAll(true);
		
		jMultiplierLabel = new JLabel(TabsStockpile.get().multiplierSign());

		jGroup = new JLabel();
		jGroup.setBorder(null);
		jGroup.setOpaque(false);
		jGroup.setFont(new Font(jGroup.getFont().getName(), Font.BOLD, jGroup.getFont().getSize() + 1));

		//Owner
		jOwnerLabel = new JLabel(TabsStockpile.get().stockpileOwner());
		jOwnerLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		jOwnerLabel.setFont(new Font(jOwnerLabel.getFont().getName(), Font.BOLD, jOwnerLabel.getFont().getSize()));

		jOwner = new JLabel();
		jOwner.setVerticalAlignment(SwingConstants.BOTTOM);

		//Location
		jLocationLabel = new JLabel(TabsStockpile.get().stockpileLocation());
		jLocationLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		jLocationLabel.setFont(new Font(jLocationLabel.getFont().getName(), Font.BOLD, jLocationLabel.getFont().getSize()));

		jLocation = new JLabel();
		jLocation.setVerticalAlignment(SwingConstants.BOTTOM);

		//Stockpile Edit/Add/etc.
		jStockpile = new JDropDownButton(TabsStockpile.get().stockpile());
		jStockpile.setOpaque(false);

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

		JMenuItem jSubStockpile = new JMenuItem(TabsStockpile.get().subpiles(), Images.TOOL_STOCKPILE.getIcon());
		jSubStockpile.setActionCommand(StockpileCellAction.SUBPILES.name());
		jSubStockpile.addActionListener(actionListener);
		jStockpile.add(jSubStockpile);

		jStockpile.addSeparator();

		jMenuItem = new JMenuItem(TabsStockpile.get().getShoppingList(), Images.STOCKPILE_SHOPPING_LIST.getIcon());
		jMenuItem.setActionCommand(StockpileCellAction.SHOPPING_LIST_SINGLE.name());
		jMenuItem.addActionListener(actionListener);
		jStockpile.add(jMenuItem);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jStartSpace)
					.addComponent(jExpand)
					.addGap(5)
					.addComponent(jColor, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6)
					.addComponent(jColorDisabled, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6)
					.addGap(10)
					.addComponent(jStockpile, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jMultiplier, 50, 50, 50)
					.addComponent(jMultiplierLabel)
					.addGap(10)
					.addComponent(jGroup, 150, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
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
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGap(2)
				.addGroup(layout.createParallelGroup()
					.addComponent(jStartSpace, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addGroup(layout.createSequentialGroup()
						.addGap(3)
						.addComponent(jColor, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6)
						.addComponent(jColorDisabled, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6)
					)
					.addComponent(jStockpile, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMultiplier, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jMultiplierLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jGroup, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addGroup(layout.createSequentialGroup()
						.addGap(4)
						.addGroup(layout.createParallelGroup()
							.addComponent(jLocationLabel)
							.addComponent(jLocation)
							.addComponent(jOwnerLabel)
							.addComponent(jOwner)
						)
					)
				)
				.addGap(2)
		);
	}

	private void setEnabled(final boolean enabled) {
		if (!enabled) { //Save focus owner
			focusOwner = program.getMainWindow().getFrame().getFocusOwner();
		}
		jGroup.setEnabled(enabled);
		jColor.setVisible(enabled);
		jColorDisabled.setVisible(!enabled);
		jStockpile.setEnabled(enabled);
		jOwnerLabel.setEnabled(enabled);
		jMultiplierLabel.setEnabled(enabled);
		jMultiplier.setEnabled(enabled);
		jOwner.setEnabled(enabled);
		jLocation.setEnabled(enabled);
		jLocationLabel.setEnabled(enabled);
		jExpand.setEnabled(enabled);
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
		jGroup.setText(stockpileItem.getStockpile().getName());
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
		String location = stockpileItem.getStockpile().getLocationName();
		jLocation.setText(location);
		jLocation.setToolTipText(location);
		jLocationLabel.setToolTipText(location);
		String owner = stockpileItem.getStockpile().getOwnerName();
		jOwner.setText(owner);
		jOwner.setToolTipText(owner);
		jOwnerLabel.setToolTipText(owner);
		jMultiplier.setText(Formater.compareFormat(stockpileItem.getStockpile().getMultiplier()));
	}

	protected JViewport getParentViewport() {
		Container container = jTable.getParent();
		if (container instanceof JViewport) {
			return (JViewport) container;
		} else {
			return null;
		}
	}

	private class ListenerClass implements HierarchyListener, AdjustmentListener {
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
				setEnabled(true);
				jTable.repaint();
			} else {
				if (jExpand.isEnabled()) { //Only do once
					setEnabled(false);
					jTable.repaint();
				}
			}
		}
	}
}
