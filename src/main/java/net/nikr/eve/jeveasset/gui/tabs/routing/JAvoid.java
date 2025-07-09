/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.routing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.RouteAvoidSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.i18n.TabsRouting;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class JAvoid {

	public static final SolarSystemComparator SOLAR_SYSTEM_COMPARATOR = new SolarSystemComparator();

	private enum AvoidAction {
		AVOID_ADD,
		AVOID_REMOVE,
		AVOID_CLEAR,
		AVOID_SAVE,
		AVOID_LOAD,
		AVOID_MANAGE,
		SAVE
	}
	//Filter
	private final JList<SolarSystem> jAvoid;
	private final EditableListModel<SolarSystem> avoidModel = new EditableListModel<>();
	private final JButton jAdd;
	private final JButton jRemove;
	private final JButton jClear;
	private final JButton jSave;
	private final JDropDownButton jLoad;
	private final JLabel jSecurityIcon;
	private final JComboBox<Double> jSecurityMinimum;
	private final JLabel jSecuritySeparatorLabel;
	private final JComboBox<Double> jSecurityMaximum;
	private final JPanel jAvoidPanel;
	private final JPanel jSecurityPanel;

	//Dialogs
	private final JSaveAvoidDialog jSaveSystemDialog;
	private final JManageAvoidDialog jManageAvoidDialog;
	private final JSystemDialog jSystemDialog;

	private final Program program;
	private final ListenerClass listener;
	private final RouteAvoidSettings avoidSettings;
	private final boolean saveSettings;

	public <E extends ActionListener & MouseListener> JAvoid(Program program, RouteAvoidSettings avoidSettings, boolean saveSettings) {
		this.program = program;
		this.listener = new ListenerClass();
		this.avoidSettings = avoidSettings;
		this.saveSettings = saveSettings;

		jSaveSystemDialog = new JSaveAvoidDialog(program);
		jManageAvoidDialog = new JManageAvoidDialog(this, program);
		jSystemDialog = new JSystemDialog(program);

		jAvoidPanel = new JPanel();
		jAvoidPanel.setBorder(BorderFactory.createTitledBorder(TabsRouting.get().avoid()));
		GroupLayout avoidLayout = new GroupLayout(jAvoidPanel);
		jAvoidPanel.setLayout(avoidLayout);
		avoidLayout.setAutoCreateGaps(true);
		avoidLayout.setAutoCreateContainerGaps(true);

		jSecurityPanel = new JPanel();
		jSecurityPanel.setBorder(BorderFactory.createTitledBorder(TabsRouting.get().security()));
		GroupLayout securityLayout = new GroupLayout(jSecurityPanel);
		jSecurityPanel.setLayout(securityLayout);
		securityLayout.setAutoCreateGaps(true);
		securityLayout.setAutoCreateContainerGaps(true);

		avoidModel.setSortComparator(JAvoid.SOLAR_SYSTEM_COMPARATOR);
		avoidModel.addAll(avoidSettings.getAvoid().values());

		jAvoid = new JList<>(avoidModel);
		jAvoid.addMouseListener(listener);
		jAvoid.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				jRemove.setEnabled(jAvoid.getSelectedIndices().length > 0);
			}
		});

		JFixedToolBar jToolBar = new JFixedToolBar(JFixedToolBar.Orientation.VERTICAL);

		jAdd = new JButton(TabsRouting.get().avoidAdd(), Images.EDIT_ADD.getIcon());
		jAdd.setActionCommand(AvoidAction.AVOID_ADD.name());
		jAdd.addActionListener(listener);
		jToolBar.addButton(jAdd);

		jRemove = new JButton(TabsRouting.get().avoidRemove(), Images.EDIT_DELETE.getIcon());
		jRemove.setActionCommand(AvoidAction.AVOID_REMOVE.name());
		jRemove.addActionListener(listener);
		jRemove.setEnabled(false);
		jToolBar.addButton(jRemove);

		jClear = new JButton(TabsRouting.get().avoidClear(), Images.FILTER_CLEAR.getIcon());
		jClear.setActionCommand(AvoidAction.AVOID_CLEAR.name());
		jClear.addActionListener(listener);
		jToolBar.addButton(jClear);

		jSave = new JButton(TabsRouting.get().avoidSave(), Images.FILTER_SAVE.getIcon());
		jSave.setActionCommand(AvoidAction.AVOID_SAVE.name());
		jSave.addActionListener(listener);
		jToolBar.addButton(jSave);

		jLoad = new JDropDownButton(TabsRouting.get().avoidLoad(), Images.FILTER_LOAD.getIcon());
		jToolBar.addButton(jLoad);

		JScrollPane jAvoidScroll = new JScrollPane(jAvoid);

		Double[] security = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

		jSecurityIcon = new JLabel();

		jSecurityMinimum = new JComboBox<>(security);
		jSecurityMinimum.setSelectedItem(avoidSettings.getSecMin());
		jSecurityMinimum.setActionCommand(AvoidAction.SAVE.name());
		jSecurityMinimum.addActionListener(listener);

		jSecuritySeparatorLabel = new JLabel(" - ");

		jSecurityMaximum = new JComboBox<>(security);
		jSecurityMaximum.setSelectedItem(avoidSettings.getSecMax());
		jSecurityMaximum.setActionCommand(AvoidAction.SAVE.name());
		jSecurityMaximum.addActionListener(listener);

		updateFilterLabels();
		updateSavedFilters();

		avoidLayout.setHorizontalGroup(
			avoidLayout.createSequentialGroup()
				.addComponent(jAvoidScroll, 300, 300, Integer.MAX_VALUE)
				.addComponent(jToolBar)
		);
		avoidLayout.setVerticalGroup(
			avoidLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
				.addComponent(jAvoidScroll, 160, 160, Integer.MAX_VALUE)
				.addComponent(jToolBar)
		);
		securityLayout.setHorizontalGroup(
			securityLayout.createSequentialGroup()
				.addComponent(jSecurityIcon)
				.addComponent(jSecurityMinimum, 80, 80, Short.MAX_VALUE)
				.addComponent(jSecuritySeparatorLabel)
				.addComponent(jSecurityMaximum, 80, 80, Short.MAX_VALUE)
		);
		securityLayout.setVerticalGroup(
			securityLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
				.addComponent(jSecurityIcon, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSecurityMinimum, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSecuritySeparatorLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSecurityMaximum, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}

	public void setData(RouteAvoidSettings current) {
		avoidModel.clear();
		avoidModel.addAll(current.getAvoid().values());
		jSecurityMinimum.setSelectedItem(current.getSecMin());
		jSecurityMaximum.setSelectedItem(current.getSecMax());
		avoidSettings.getPresets().clear();
		avoidSettings.getPresets().putAll(current.getPresets());
		updateFilterLabels();
		updateSavedFilters();
	}

	public void setEnabled(boolean b) {
		jAvoid.setEnabled(b);
		jAdd.setEnabled(b);
		if (b) {
			jRemove.setEnabled(jAvoid.getSelectedIndices().length > 0);
			jClear.setEnabled(!avoidModel.getAll().isEmpty());
			jSave.setEnabled(!avoidModel.getAll().isEmpty());
			jLoad.setEnabled(!avoidSettings.getPresets().isEmpty());
		} else {
			jRemove.setEnabled(b);
			jClear.setEnabled(b);
			jSave.setEnabled(b);
			jLoad.setEnabled(b);
		}
		jSecurityMinimum.setEnabled(b);
		jSecuritySeparatorLabel.setEnabled(b);
		jSecurityMaximum.setEnabled(b);
	}

	public Double getSecurityMinimum() {
		if (jSecurityMinimum != null) {
			return (Double) jSecurityMinimum.getSelectedItem();
		} else {
			return 0.0;
		}
	}

	private void setSecurityMaximum(Double d) {
		jSecurityMaximum.setSelectedItem(d);
	}

	public Double getSecurityMaximum() {
		if (jSecurityMaximum != null) {
			return (Double) jSecurityMaximum.getSelectedItem();
		} else {
			return 1.0;
		}
	}

	public void updateSystemDialog(Set<SolarSystem> nodes) {
		jSystemDialog.updateData(nodes); //Will be replaced by valid systems by processRouteInner()
	}

	public final void updateFilterLabels() {
		double secMin = avoidSettings.getSecMin();
		if (secMin == 0.0) {
			jSecurityIcon.setIcon(Images.UPDATE_CANCELLED.getIcon());
		} else if (secMin >= 0.5) {
			jSecurityIcon.setIcon(Images.UPDATE_DONE_OK.getIcon());
		} else {
			jSecurityIcon.setIcon(Images.UPDATE_DONE_SOME.getIcon());
		}
		jClear.setEnabled(!avoidModel.getAll().isEmpty());
		jSave.setEnabled(!avoidModel.getAll().isEmpty());
		jLoad.setEnabled(!avoidSettings.getPresets().isEmpty());
	}

	private void updateSavedFilters() {
		jLoad.removeAll();

		JMenuItem jManage = new JMenuItem(TabsRouting.get().avoidManage(), Images.DIALOG_SETTINGS.getIcon());
		jManage.setActionCommand(AvoidAction.AVOID_MANAGE.name());
		jManage.addActionListener(listener);
		jLoad.add(jManage);

		if (!avoidSettings.getPresets().isEmpty()) {
			jLoad.addSeparator();
		}

		ArrayList<String> presets = new ArrayList<>(avoidSettings.getPresets().keySet());
		Collections.sort(presets);
		for (String name : presets) {
			JMenuItem jMenuItem = new JLoadMenuItem(name, avoidSettings.getPresets().get(name));
			jMenuItem.setActionCommand(AvoidAction.AVOID_LOAD.name());
			jMenuItem.addActionListener(listener);
			jLoad.add(jMenuItem);
		}
		jLoad.setEnabled(!avoidSettings.getPresets().isEmpty());
		jManageAvoidDialog.updateData();
	}

	public void loadFilter(Set<Long> systemIds) {
		avoidModel.clear();
		if (saveSettings) {
			Settings.lock("Route Avoid (Load Filter)");
		}
		avoidSettings.getAvoid().clear();
		for (Long systemID : systemIds) {
			SolarSystem system = new SolarSystem(ApiIdConverter.getLocation(systemID));
			avoidSettings.getAvoid().put(system.getSystemID(), system);
			avoidModel.add(system);
		}
		if (saveSettings) {
			Settings.unlock("Route Avoid (Load Filter)");
			program.saveSettings("Route Avoid (Load Filter)");
		}
		updateFilterLabels();
	}

	public void deleteFilters(List<String> list) {
		if (saveSettings) {
			Settings.lock("Route Avoid (Delete Filters)");
		}
		for (String filter : list) {
			avoidSettings.getPresets().remove(filter);
		}
		if (saveSettings) {
			Settings.unlock("Route Avoid (Delete Filters)");
			program.saveSettings("Route Avoid (Delete Filters)");
		}
		updateSavedFilters();
	}

	public void renameFilter(String name, String oldName) {
		if (saveSettings) {
			Settings.lock("Route Avoid (Rename Filter)");
		}
		Set<Long> systemIDs = avoidSettings.getPresets().remove(oldName);
		avoidSettings.getPresets().put(name, systemIDs);
		if (saveSettings) {
			Settings.unlock("Route Avoid (Rename Filter)");
			program.saveSettings("Route Avoid (Rename Filter)");
		}
		updateSavedFilters();
	}

	public void mergeFilters(String name, List<String> list) {
		Set<Long> systemIDs = new HashSet<>();
		if (saveSettings) {
			Settings.lock("Route Avoid (Merge Filters)");
		}
		for (String mergeName : list) {
			systemIDs.addAll(avoidSettings.getPresets().get(mergeName));
		}
		avoidSettings.getPresets().put(name, systemIDs);
		if (saveSettings) {
			Settings.unlock("Route Avoid (Merge Filters)");
			program.saveSettings("Route Avoid (Merge Filters)");
		}
		updateSavedFilters();
	}

	private void removeSystems() {
		if (saveSettings) {
			Settings.lock("Route Avoid (Delete Systems)");
		}
		for (SolarSystem system : jAvoid.getSelectedValuesList()) {
			avoidModel.remove(system);
			avoidSettings.getAvoid().remove(system.getSystemID());
		}
		if (saveSettings) {
			Settings.unlock("Route Avoid (Delete Systems)");
			program.saveSettings("Route Avoid (Delete Systems)");
		}
		updateFilterLabels();
	}

	public JPanel getAvoidPanel() {
		return jAvoidPanel;
	}

	public JPanel getSecurityPanel() {
		return jSecurityPanel;
	}

	public static class SolarSystemComparator implements Comparator<SolarSystem> {

		@Override
		public int compare(SolarSystem o1, SolarSystem o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	}

	
	public static class JLoadMenuItem extends JMenuItem {

		private final Set<Long> systemIDs;

		public JLoadMenuItem(String name, Set<Long> systemIDs) {
			super(name, Images.FILTER_LOAD.getIcon());
			this.systemIDs = systemIDs;
		}

		public Set<Long> getSystemIDs() {
			return systemIDs;
		}
	}

	private class ListenerClass extends MouseAdapter implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (AvoidAction.AVOID_REMOVE.name().equals(e.getActionCommand())) {
				removeSystems();
			} else if (AvoidAction.AVOID_CLEAR.name().equals(e.getActionCommand())) {
				if (saveSettings) {
					Settings.lock("Route Avoid (Clear Systems)");
				}
				avoidSettings.getAvoid().clear();
				avoidModel.clear();
				updateFilterLabels();
				if (saveSettings) {
					Settings.unlock("Route Avoid (Clear Systems)");
					program.saveSettings("Route Avoid (Clear Systems)");
				}
			} else if (AvoidAction.AVOID_ADD.name().equals(e.getActionCommand())) {
				SolarSystem system = jSystemDialog.show();
				if (system != null) {
					if (saveSettings) {
						Settings.lock("Route Avoid (Add System)");
					}
					avoidSettings.getAvoid().put(system.getSystemID(), system);
					avoidModel.clear();
					avoidModel.addAll(avoidSettings.getAvoid().values());
					if (saveSettings) {
						Settings.unlock("Route Avoid (Add System)");
						program.saveSettings("Route Avoid (Add System)");
					}
					updateFilterLabels();
				}
			} else if (AvoidAction.AVOID_SAVE.name().equals(e.getActionCommand())) {
				jSaveSystemDialog.updateData(new ArrayList<>(avoidSettings.getPresets().keySet()));
				String name = jSaveSystemDialog.show();
				if (name != null) {
					if (saveSettings) {
						Settings.lock("Route Avoid (Save Filter)");
					}
					Set<Long> systemIDs = new HashSet<>();
					for (SolarSystem system : avoidModel.getAll()) {
						systemIDs.add(system.getSystemID());
					}
					avoidSettings.getPresets().put(name, systemIDs);
					if (saveSettings) {
						Settings.unlock("Route Avoid (Save Filter)");
						program.saveSettings("Route Avoid (Save Filter)");
					}
					updateSavedFilters();
				}
			} else if (AvoidAction.AVOID_LOAD.name().equals(e.getActionCommand())) {
				Object source = e.getSource();
				if (source instanceof JLoadMenuItem) {
					JLoadMenuItem menuItem = (JLoadMenuItem) source;
					loadFilter(menuItem.getSystemIDs());
				}
			} else if (AvoidAction.AVOID_MANAGE.name().equals(e.getActionCommand())) {
				jManageAvoidDialog.updateData();
				jManageAvoidDialog.setVisible(true);
			} else if (AvoidAction.SAVE.name().equals(e.getActionCommand())) {
				double min = getSecurityMinimum();
				double max = getSecurityMaximum();
				if (max < min) {
					max = min;
					setSecurityMaximum(min);
				}
				if (saveSettings) {
					Settings.lock("Route Avoid (Security)");
				}
				avoidSettings.setSecMin(min);
				avoidSettings.setSecMax(max);
				if (saveSettings) {
					Settings.unlock("Route Avoid (Security)");
					program.saveSettings("Route Avoid (Security)");
				}
				updateFilterLabels();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount()% 2 == 0
						&& !e.isControlDown()
						&& !e.isShiftDown()
						) {
				if (e.getSource().equals(jAvoid) && jAvoid.isEnabled()) {
					removeSystems();
				}
			}
		}
	}

}
