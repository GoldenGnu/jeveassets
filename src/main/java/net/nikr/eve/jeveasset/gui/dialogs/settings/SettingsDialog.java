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
package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.settings.JSettingsPanel.UpdateType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil.HelpLink;


public class SettingsDialog extends JDialogCentered {

	private enum SettingsDialogAction {
		OK, CANCEL, APPLY, HELP
	}

	private final JTree jTree;
	private final JPanel jContent;
	private final JButton jOK;
	private final Map<String, JSettingsPanel> settingsPanels;
	private final Map<Object, Icon> icons;
	private final DefaultMutableTreeNode rootNode;
	private final DefaultTreeModel treeModel;
	private final CardLayout cardLayout;

	private final UserPriceSettingsPanel userPriceSettingsPanel;
	private final UserNameSettingsPanel userNameSettingsPanel;
	private final UserLocationSettingsPanel locationSettingsPanel;
	private boolean tabSelected = false;

	public SettingsDialog(final Program program) {
		super(program, DialoguesSettings.get().settings(Program.PROGRAM_NAME), Images.DIALOG_SETTINGS.getImage());

		ListenerClass listener = new ListenerClass();

		settingsPanels = new HashMap<>();
		icons = new HashMap<>();

		rootNode = new DefaultMutableTreeNode(DialoguesSettings.get().root());
		treeModel = new DefaultTreeModel(rootNode);
		jTree = new JTree(treeModel);
		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		jTree.putClientProperty("JTree.lineStyle", "None");
		jTree.setCellRenderer(new IconTreeCellRenderer(icons));
		jTree.setExpandsSelectedPaths(true);
		jTree.setRootVisible(false);
		jTree.setShowsRootHandles(true);
		jTree.addTreeSelectionListener(listener);
		jTree.setVisibleRowCount(0);

		cardLayout = new CardLayout();
		jContent = new JPanel(cardLayout);

		GeneralSettingsPanel generalSettingsPanel = new GeneralSettingsPanel(program, this);
		add(generalSettingsPanel);

		DefaultMutableTreeNode toolNode = addGroup(DialoguesSettings.get().tools(), Images.SETTINGS_TOOLS.getIcon());

		add(toolNode, new ShowToolSettingsPanel(program, this));

		add(toolNode, new AssetsToolSettingsPanel(program, this));

		add(toolNode, new OverviewToolSettingsPanel(program, this));

		add(toolNode, new StockpileToolSettingsPanel(program, this));

		add(toolNode, new MarketOrdersToolSettingsPanel(program, this));

		add(toolNode, new TransactionsToolSettingsPanel(program, this));

		add(toolNode, new JournalToolSettingsPanel(program, this));

		add(toolNode, new ContractToolSettingsPanel(program, this));

		add(toolNode, new TrackerToolSettingsPanel(program, this));

		add(toolNode, new PriceHistoryToolSettingsPanel(program, this));

		add(toolNode, new MiningToolSettingsPanel(program, this));

		DefaultMutableTreeNode valuesNode = addGroup(DialoguesSettings.get().values(), Images.EDIT_RENAME.getIcon());

		userPriceSettingsPanel = new UserPriceSettingsPanel(program, this);
		add(valuesNode, userPriceSettingsPanel);

		userNameSettingsPanel = new UserNameSettingsPanel(program, this);
		add(valuesNode, userNameSettingsPanel);

		locationSettingsPanel = new UserLocationSettingsPanel(program, this);
		add(valuesNode, locationSettingsPanel);

		add(valuesNode, new TagsSettingsPanel(program, this));

		add(new ColorSettingsPanel(program, this));

		add(new SoundsSettingsPanel(program, this));

		add(new PriceDataSettingsPanel(program, this));

		add(new ReprocessingSettingsPanel(program, this));

		add(new ManufacturingSettingsPanel(program, this));

		add(new ProxySettingsPanel(program, this));

		add(new WindowSettingsPanel(program, this));

		JScrollPane jTreeScroller = new JScrollPane(jTree);

		JSeparator jSeparator = new JSeparator();

		JButton jHelp = new JButton(Images.MISC_HELP.getIcon());
		jHelp.setActionCommand(SettingsDialogAction.HELP.name());
		jHelp.addActionListener(listener);

		jOK = new JButton(DialoguesSettings.get().ok());
		jOK.setActionCommand(SettingsDialogAction.OK.name());
		jOK.addActionListener(listener);

		JButton jApply = new JButton(DialoguesSettings.get().apply());
		jApply.setActionCommand(SettingsDialogAction.APPLY.name());
		jApply.addActionListener(listener);

		JButton jCancel = new JButton(DialoguesSettings.get().cancel());
		jCancel.setActionCommand(SettingsDialogAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jTreeScroller, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					.addComponent(jContent, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addComponent(jSeparator)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jHelp, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth())
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jApply, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jTreeScroller, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					.addComponent(jContent, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addComponent(jSeparator, 5, 5, 5)
				.addGroup(layout.createParallelGroup()
					.addComponent(jHelp, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jApply, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	private DefaultMutableTreeNode addGroup(final String name, final Icon icon) {
		SettingsGroup group = new SettingsGroup(program, this, name, icon);
		add(group);
		return group.getTreeNode();
	}

	private void add(final JSettingsPanel jSettingsPanel) {
		add(null, jSettingsPanel);
	}

	protected void addCard(final JPanel jPanel, final String title) {
		jContent.add(jPanel, title);
	}

	protected void removeCard(final JPanel jPanel) {
		jContent.remove(jPanel);
	}

	private void add(final DefaultMutableTreeNode parentNode, final JSettingsPanel jSettingsPanel) {
		settingsPanels.put(jSettingsPanel.getTitle(), jSettingsPanel);
		icons.put(jSettingsPanel.getTitle(), jSettingsPanel.getIcon());
		jContent.add(jSettingsPanel.getPanel(), jSettingsPanel.getTitle());
		jTree.setVisibleRowCount(jTree.getVisibleRowCount() + 1);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(jSettingsPanel.getTitle());
		if (parentNode == null) {
			treeModel.insertNodeInto(node, rootNode, rootNode.getChildCount());
		} else {
			treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
		}
		jSettingsPanel.setTreeNode(node);
	}

	public UserNameSettingsPanel getUserNameSettingsPanel() {
		return userNameSettingsPanel;
	}

	public UserPriceSettingsPanel getUserPriceSettingsPanel() {
		return userPriceSettingsPanel;
	}

	public UserLocationSettingsPanel getUserLocationSettingsPanel() {
		return locationSettingsPanel;
	}

	private void expandAll(final TreePath parent, final boolean expand) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(path, expand);
			}
		}
		if (expand) {
			jTree.expandPath(parent);
		} else {
			jTree.collapsePath(parent);
		}
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jOK;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		save(true);
	}

	private void save(boolean hideWindow) {
		Set<UpdateType> updates = new HashSet<>();
		Settings.lock("Settings Dialog"); //Lock for Settings Dialog
		for (Map.Entry<String, JSettingsPanel> entry : settingsPanels.entrySet()) {
			updates.add(entry.getValue().save());
		}
		Settings.unlock("Settings Dialog"); //Unlock for Settings Dialog
		if (hideWindow) {
			setVisible(false);
		}
		if (updates.contains(UpdateType.FULL_UPDATE)) {
			if (hideWindow) {
				program.updateEventListsWithProgress();
			} else {
				program.updateEventListsWithProgress(getDialog());
			}
		} else {
			if (updates.contains(UpdateType.FULL_REPAINT)) {
				program.repaintTables();
			} else {
				if (updates.contains(UpdateType.REPAINT_MARKET_ORDERS_TABLE)) {
					program.getMarketOrdersTab().repaintTable();
				}
				if (updates.contains(UpdateType.REPAINT_STOCKPILE_TABLE)) {
					program.getStockpileTab().repaintTable();
				}
			}
			if (updates.contains(UpdateType.UPDATE_OVERVIEW)) {
				program.getOverviewTab().updateData();
			}
			if (updates.contains(UpdateType.UPDATE_TAGS)) {
				program.updateTags();
			}
		}
		program.saveSettings("Settings Dialog"); //Save Settings Dialog
	}

	public void setVisible(final JSettingsPanel c) {
		jTree.setSelectionPath(new TreePath(c.getTreeNode()));
		tabSelected = true;
		setVisible(true);
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			getDialog().pack(); //XXX - Workaround for LookAndFeelPreview being temperamental and resizing the dialog when generating the LAF previews
			for (Map.Entry<String, JSettingsPanel> entry : settingsPanels.entrySet()) {
				entry.getValue().load();
			}
			expandAll(new TreePath((rootNode)), true);
			if (!tabSelected) {
				jTree.setSelectionRow(0);
			}
		}
		tabSelected = false;
		super.setVisible(b);
	}

	private class ListenerClass implements ActionListener, TreeSelectionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (SettingsDialogAction.OK.name().equals(e.getActionCommand())) {
				save(true);
			} else if (SettingsDialogAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			} else if (SettingsDialogAction.APPLY.name().equals(e.getActionCommand())) {
				save(false);
			} else if (SettingsDialogAction.HELP.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(new HelpLink("https://wiki.jeveassets.org/manual/options", GuiShared.get().helpSettings()), getDialog());
			}
		}

		@Override
		public void valueChanged(final TreeSelectionEvent e) {
			TreePath[] paths = jTree.getSelectionPaths();
			if (paths != null && paths.length == 1) {
				cardLayout.show(jContent, paths[0].getLastPathComponent().toString());
			}
		}
	}

	public class IconTreeCellRenderer implements TreeCellRenderer {

		private Map<Object, Icon> icons = null;
		private final DefaultTreeCellRenderer cellRenderer = new DefaultTreeCellRenderer();

		public IconTreeCellRenderer(final Map<Object, Icon> icons) {
			this.icons = icons;
		}

		@Override
		public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
			JLabel label = (JLabel) cellRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			label.setIcon(icons.get(value.toString()));
			return label;
		}

	}

	private static class SettingsGroup extends JSettingsPanel {

		public SettingsGroup(final Program program, final SettingsDialog settingsDialog, final String sTitle, final Icon icon) {
			super(program, settingsDialog, sTitle, icon);
		}

		@Override
		public UpdateType save() { return UpdateType.NONE; }

		@Override
		public void load() { }

	}
}
