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
package net.nikr.eve.jeveasset.gui.tabs.tracker;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.TreeList;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.swing.EventTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.CheckBoxNode;
import net.nikr.eve.jeveasset.gui.shared.components.CheckBoxNodeEditor;
import net.nikr.eve.jeveasset.gui.shared.components.CheckBoxNodeRenderer;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.i18n.TabsTracker;


public class TrackerAssetFilterDialog extends JDialogCentered {

	private final JTree jTree;
	private final JTextField jFilter;
	private final JCheckBox jNewSelected;
	private final EventList<CheckBoxNode> eventList;
	private final Timer timer;
	private final FilterList<CheckBoxNode> filterList;

	private boolean save = false;

	public TrackerAssetFilterDialog(Program program) {
		super(program, TabsTracker.get().filterTitle(), Images.TOOL_TRACKER.getImage());
		//Backend
		eventList = new EventListManager<CheckBoxNode>().create();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Tree
		eventList.getReadWriteLock().readLock().lock();
		TreeList<CheckBoxNode> treeList = new TreeList<>(EventModels.createSwingThreadProxyList(filterList), new CheckBoxNodeFormat(), TreeList.nodesStartExpanded());
		eventList.getReadWriteLock().readLock().unlock();

		EventTreeModel<CheckBoxNode> eventTreeModel = new EventTreeModel<>(treeList);
		jTree = new JTree(eventTreeModel);
		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		jTree.putClientProperty("JTree.lineStyle", "None");
		jTree.setExpandsSelectedPaths(true);
		jTree.setRootVisible(false);
		jTree.setShowsRootHandles(true);
		jTree.setVisibleRowCount(0);
		jTree.setCellRenderer(new CheckBoxNodeRenderer());
		jTree.setCellEditor(new CheckBoxNodeEditor(jTree));
		jTree.setEditable(true);
		jTree.setLargeModel(true);
		jTree.setRowHeight(16);

		timer = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				filter();
			}
		});

		JLabel jSearch = new JLabel(TabsTracker.get().search());

		jFilter = new JTextField();
		jFilter.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				timer.stop();
				timer.start();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				timer.stop();
				timer.start();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				timer.stop();
				timer.start();
			}
		});
		JButton jClear = new JButton(Images.TAB_CLOSE.getIcon());
		jClear.setContentAreaFilled(false);
		jClear.setFocusPainted(false);
		jClear.setPressedIcon(Images.TAB_CLOSE_ACTIVE.getIcon());
		jClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jFilter.setText("");
				timer.stop();
				filter();
			}
		});
		
		
		jNewSelected = new JCheckBox(TabsTracker.get().newSelected());
		jNewSelected.setSelected(Settings.get().isTrackerSelectNew());

		JButton jOK = new JButton(TabsTracker.get().ok());
		jOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save = true;
				setVisible(false);
			}
		});
		JButton jCancel = new JButton(TabsTracker.get().cancel());
		jCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		JScrollPane jTreeScroll = new JScrollPane(jTree);
		jTreeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jSearch)
					.addComponent(jFilter, 0, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					.addComponent(jClear, 16, 16, 16)
				)
				.addComponent(jTreeScroll, 0, GroupLayout.PREFERRED_SIZE, 600)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jNewSelected)
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jSearch, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFilter, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jClear, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jTreeScroll, 0, GroupLayout.PREFERRED_SIZE, 500)
				.addGroup(layout.createParallelGroup()
					.addComponent(jNewSelected)
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jTree;
	}

	@Override
	protected JButton getDefaultButton() {
		return null;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() { }

	public boolean isSelectNew() {
		return jNewSelected.isSelected();
	}

	public boolean showLocations(Map<String, CheckBoxNode> nodes) {
		//Reset
		save = false;
		jFilter.setText("");
		timer.stop();
		filterList.setMatcher(null);

		//Copy list
		Map<String, CheckBoxNode> cloneList = cloneList(nodes);

		//Set data
		try { 
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
			eventList.addAll(cloneList.values());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		if (cloneList.size() > 35) {
			jTree.setVisibleRowCount(35);
		} else {
			jTree.setVisibleRowCount(cloneList.size());
		}
		expandAll(); //Expand all

		//Show
		setVisible(true);

		boolean changed = changed(cloneList, nodes); //Only need to update if something have been changed...
		if (save && changed) { //If changed
			nodes.clear();
			nodes.putAll(cloneList);
		}
		return save && changed;
	}

	private void filter() {
		if (jFilter.getText().isEmpty()) {
			filterList.setMatcher(null);
			try { 
				eventList.getReadWriteLock().readLock().lock();
				for (CheckBoxNode node : eventList) {
					node.setShown(true);
				}
			} finally {
				eventList.getReadWriteLock().readLock().unlock();
			}
		} else {
			filterList.setMatcher(new CheckBoxNodeMatcher(jFilter.getText()));
		}
		expandAll();
	}

	private Map<String, CheckBoxNode> cloneList(Map<String, CheckBoxNode> nodes) {
		Map<String, CheckBoxNode> clonesCache = new HashMap<>();
		Map<String, CheckBoxNode> clonedNodes = new TreeMap<>();
		for (Map.Entry<String, CheckBoxNode> entry : nodes.entrySet()) {
			clonedNodes.put(entry.getKey(), cloneTree(clonesCache, entry.getValue()));
		}
		return clonedNodes;
	}

	private CheckBoxNode cloneTree(Map<String, CheckBoxNode> clonesCache, CheckBoxNode oldNode) {
		CheckBoxNode oldParent = oldNode.getParent();
		CheckBoxNode cloneParent = null;
		if (oldParent != null) {
			cloneParent = cloneTree(clonesCache, oldParent);
		}
		CheckBoxNode cloneNode = clonesCache.get(oldNode.getNodeId());
		if (cloneNode == null) {
			cloneNode = new CheckBoxNode(cloneParent, oldNode);
			clonesCache.put(cloneNode.getNodeId(), cloneNode);
		}
		return cloneNode;
	}

	/**
	 * Check if the clone tree have been changed compared to the source tree
	 * @param clone Clone Tree
	 * @param source Source Tree
	 * @return true if the maps are not equal. false if the maps are equal
	 */
	private boolean changed(Map<String, CheckBoxNode> clone, Map<String, CheckBoxNode> source) {
		for (Map.Entry<String, CheckBoxNode> entry : clone.entrySet()) {
			CheckBoxNode sourceNode = source.get(entry.getKey());
			CheckBoxNode cloneNode = entry.getValue();
			if (cloneNode.isSelected() != sourceNode.isSelected()) {
				return true;
			}
		}
		return false;
	}

	private void expandAll() {
		if (jTree.getRowCount() > 0) { //Always expand root
			jTree.expandRow(0);
		}
		for (int i = 0; i < jTree.getRowCount(); i++) { //Expand everything
			TreePath path = jTree.getPathForRow(i);
			Object object = path.getLastPathComponent();
			if (object instanceof TreeList.Node) {
				TreeList.Node<?> node = (TreeList.Node) object;
				if (node.getChildren().size() > 5000) { //Ignore nodes with more that 5000 children
					continue;
				}
			}
			jTree.expandPath(path);
		}
	}

	private static class CheckBoxNodeFormat implements TreeList.Format<CheckBoxNode> {

		private final CheckBoxNodeComparator comparator = new CheckBoxNodeComparator();
		
		@Override
		public void getPath(List<CheckBoxNode> path, CheckBoxNode element) {
			addParents(path, element);
			path.add(element);
		}

		@Override
		public boolean allowsChildren(CheckBoxNode element) {
			return true;
		}

		@Override
		public Comparator<? super CheckBoxNode> getComparator(int depth) {
			return comparator;
		}

		private void addParents(List<CheckBoxNode> path, CheckBoxNode element) {
			CheckBoxNode parent = element.getParent();
			if (parent != null) {
				path.add(0, parent);
				addParents(path, parent);
			}
		}

		private static class CheckBoxNodeComparator implements Comparator<CheckBoxNode> {

			@Override
			public int compare(CheckBoxNode o1, CheckBoxNode o2) {
				return o1.compareTo(o2);
			}
		}
	}
	

	private static class CheckBoxNodeMatcher implements Matcher<CheckBoxNode> {

		private final String match;

		public CheckBoxNodeMatcher(String match) {
			this.match = match.toLowerCase();
		}

		@Override
		public boolean matches(CheckBoxNode item) {
			boolean shown = matchTree(item);
			item.setShown(shown);
			return shown;
		}

		private boolean matchTree(CheckBoxNode item) {
			if (item == null) {
				return false;
			}
			if (item.getNodeName().toLowerCase().contains(match)) {
				return true;
			} else {
				return matchTree(item.getParent());
			}
		}

	}

}
