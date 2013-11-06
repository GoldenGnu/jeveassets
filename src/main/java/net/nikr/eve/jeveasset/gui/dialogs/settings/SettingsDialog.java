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
package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class SettingsDialog extends JDialogCentered {

	private enum SettingsDialogAction {
		OK, CANCEL, APPLY
	}

	private JTree jTree;
	private JPanel jContent;
	private JButton jOK;
	private Map<String, JSettingsPanel> settingsPanels;
	private Map<Object, Icon> icons;
	private DefaultMutableTreeNode rootNode;
	private DefaultTreeModel treeModel;
	private CardLayout cardLayout;

	private UserPriceSettingsPanel userPriceSettingsPanel;
	private UserNameSettingsPanel userNameSettingsPanel;

	private boolean tabSelected = false;

	public SettingsDialog(final Program program) {
		super(program, DialoguesSettings.get().settings(Program.PROGRAM_NAME), Images.DIALOG_SETTINGS.getImage());

		ListenerClass listener = new ListenerClass();

		settingsPanels = new HashMap<String, JSettingsPanel>();
		icons = new HashMap<Object, Icon>();

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

		JScrollPane jTreeScroller = new JScrollPane(jTree);

		cardLayout = new CardLayout();

		jContent = new JPanel(cardLayout);

		JSeparator jSeparator = new JSeparator();

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
					.addComponent(jTreeScroller)
					.addComponent(jContent)
				)
				.addComponent(jSeparator)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jApply, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jTreeScroller, 250, 250, 250)
					.addComponent(jContent)
				)
				.addComponent(jSeparator, 5, 5, 5)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jApply, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);

		GeneralSettingsPanel generalSettingsPanel = new GeneralSettingsPanel(program, this);
		add(generalSettingsPanel);

		DefaultMutableTreeNode toolNode = addGroup("Tools", Images.SETTINGS_TOOLS.getIcon());

		AssetsToolSettingsPanel assetsToolSettingsPanel = new AssetsToolSettingsPanel(program, this);
		add(assetsToolSettingsPanel, toolNode);

		OverviewToolSettingsPanel overviewToolSettingsPanel = new OverviewToolSettingsPanel(program, this);
		add(overviewToolSettingsPanel, toolNode);

		StockpileToolSettingsPanel stockpileToolSettingsPanel = new StockpileToolSettingsPanel(program, this);
		add(stockpileToolSettingsPanel, toolNode);

		TransactionsToolSettingsPanel transactionsToolSettingsPanel = new TransactionsToolSettingsPanel(program, this);
		add(transactionsToolSettingsPanel, toolNode);

		JournalToolSettingsPanel journalToolSettingsPanel = new JournalToolSettingsPanel(program, this);
		add(journalToolSettingsPanel, toolNode);
		
		DefaultMutableTreeNode valuesNode = addGroup("Values", Images.EDIT_RENAME.getIcon());
		userPriceSettingsPanel = new UserPriceSettingsPanel(program, this);
		add(userPriceSettingsPanel, valuesNode);
		userNameSettingsPanel = new UserNameSettingsPanel(program, this);
		add(userNameSettingsPanel, valuesNode);
		TagsSettingsPanel tagsSettingsPanel = new TagsSettingsPanel(program, this);
		add(tagsSettingsPanel, valuesNode);

		PriceDataSettingsPanel priceDataSettingsPanel = new PriceDataSettingsPanel(program, this);
		add(priceDataSettingsPanel);

		ReprocessingSettingsPanel reprocessingSettingsPanel = new ReprocessingSettingsPanel(program, this);
		add(reprocessingSettingsPanel);

		ProxySettingsPanel proxySettingsPanel = new ProxySettingsPanel(program, this);
		add(proxySettingsPanel);

		WindowSettingsPanel windowSettingsPanel = new WindowSettingsPanel(program, this);
		add(windowSettingsPanel);
	}

	private DefaultMutableTreeNode addGroup(final String name, final Icon icon) {
		SettingsGroup group = new SettingsGroup(program, this, name, icon);
		add(group);
		return group.getTreeNode();
	}

	private void add(final JSettingsPanel jSettingsPanel) {
		add(jSettingsPanel, null);
	}

	private void add(final JSettingsPanel jSettingsPanel, final DefaultMutableTreeNode parentNode) {
		settingsPanels.put(jSettingsPanel.getTitle(), jSettingsPanel);
		icons.put(jSettingsPanel.getTitle(), jSettingsPanel.getIcon());
		jContent.add(jSettingsPanel.getPanel(), jSettingsPanel.getTitle());
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
		boolean update = false;
		for (Map.Entry<String, JSettingsPanel> entry : settingsPanels.entrySet()) {
			if (entry.getValue().save()) {
				update = true;
			}
		}
		if (update) {
			program.updateEventLists();
		}
	}

	public void setVisible(final JSettingsPanel c) {
		jTree.setSelectionPath(new TreePath(c.getTreeNode()));
		tabSelected = true;
		setVisible(true);
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
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
				save();
				setVisible(false);
			}
			if (SettingsDialogAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
			if (SettingsDialogAction.APPLY.name().equals(e.getActionCommand())) {
				save();
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
		private DefaultTreeCellRenderer cellRenderer = new DefaultTreeCellRenderer();

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
		public boolean save() { return false; }

		@Override
		public void load() { }

	}
}
