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
package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
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
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class SettingsDialog extends JDialogCentered implements ActionListener, TreeSelectionListener {

	public final static String ACTION_OK = "ACTION_OK";
	public final static String ACTION_CANCEL = "ACTION_CANCEL";
	public final static String ACTION_APPLY = "ACTION_APPLY";

	private JTree jTree;
	private JPanel jContent;
	private JButton jOK;
	private Map<String, JSettingsPanel> settingsPanels;
	private Map<Object, Icon> icons;
	private DefaultMutableTreeNode rootNode;
	private DefaultTreeModel treeModel;
	private CardLayout cardLayout;

	private boolean tabSelected = false;

	public SettingsDialog(Program program) {
		super(program, DialoguesSettings.get().settings(Program.PROGRAM_NAME), Images.DIALOG_SETTINGS.getImage());

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
		jTree.addTreeSelectionListener(this);
		
		JScrollPane jTreeScroller = new JScrollPane(jTree);

		cardLayout = new CardLayout();

		jContent = new JPanel(cardLayout);

		JSeparator jSeparator = new JSeparator();

		jOK = new JButton(DialoguesSettings.get().ok());
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);

		JButton jApply = new JButton(DialoguesSettings.get().apply());
		jApply.setActionCommand(ACTION_APPLY);
		jApply.addActionListener(this);

		JButton jCancel = new JButton(DialoguesSettings.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);

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
					.addComponent(jTreeScroller, 180, 180, 200)
					.addComponent(jContent)
				)
				.addComponent(jSeparator, 5, 5, 5)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jApply, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	public DefaultMutableTreeNode addGroup(String name, Icon icon){
		SettingsGroup group = new SettingsGroup(program, this, name, icon);
		return group.getTreeNode();
	}

	public DefaultMutableTreeNode add(JSettingsPanel jSettingsPanel, Icon icon, DefaultMutableTreeNode parentNode){
		settingsPanels.put(jSettingsPanel.getTitle(), jSettingsPanel);
		icons.put(jSettingsPanel.getTitle(), icon);
		jContent.add(jSettingsPanel.getPanel(), jSettingsPanel.getTitle());
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(jSettingsPanel.getTitle());
		if (parentNode == null){
			treeModel.insertNodeInto(node, rootNode, rootNode.getChildCount());
		} else {
			treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
		}
		return node;
	}

	private void expandAll(TreePath parent, boolean expand) {
		TreeNode node = (TreeNode)parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e=node.children(); e.hasMoreElements(); ) {
				TreeNode n = (TreeNode)e.nextElement();
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
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {
		boolean update = false;
		for (Map.Entry<String, JSettingsPanel> entry : settingsPanels.entrySet()){
			if (entry.getValue().save()){
				update = true;
			}
		}
		if (update){
			program.updateEventList();
		}
	}

	public void setVisible(JSettingsPanel c) {
		jTree.setSelectionPath(new TreePath(c.getTreeNode()));
		tabSelected = true;
		setVisible(true);
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			for (Map.Entry<String, JSettingsPanel> entry : settingsPanels.entrySet()){
				entry.getValue().load();
			}
			expandAll(new TreePath((rootNode)), true);
			if (!tabSelected){
				jTree.setSelectionRow(0);
			}
		}
		tabSelected = false;
		super.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_OK.equals(e.getActionCommand())){
			save();
			setVisible(false);
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			setVisible(false);
		}
		if (ACTION_APPLY.equals(e.getActionCommand())){
			save();
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath[] paths = jTree.getSelectionPaths();
		if (paths != null && paths.length == 1){
			cardLayout.show(jContent, paths[0].getLastPathComponent().toString());
		}
	}

	public class IconTreeCellRenderer implements TreeCellRenderer{

		private Map<Object, Icon> icons = null;
		private DefaultTreeCellRenderer cellRenderer = new DefaultTreeCellRenderer();

		public IconTreeCellRenderer(Map<Object, Icon> icons) {
			this.icons = icons;
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			JLabel label = (JLabel) cellRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			label.setIcon(icons.get(value.toString()));
			return label;
		}

	}

	private static class SettingsGroup extends JSettingsPanel{

		public SettingsGroup(Program program, SettingsDialog settingsDialog, String sTitle, Icon icon) {
			super(program, settingsDialog, sTitle, icon);
		}

		@Override
		public boolean save() { return false; }

		@Override
		public void load() {}

	}
}
