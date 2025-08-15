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
package net.nikr.eve.jeveasset.gui.tabs.skills;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabPrimary;
import net.nikr.eve.jeveasset.i18n.TabsSkills;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class SkillsPlansTab extends JMainTabPrimary {

	private final JTextField jName;
	private final JTextArea jText;
	private final JButton jPaste;
	private final JButton jNew;
	private final JButton jSave;
	private final JButton jDelete;
	private final JButton jRename;
	private final JList<String> jPlans;
	private final DefaultListModel<String> planListModel;

	public static final String NAME = "skillplans";

	public SkillsPlansTab(Program program) {
		super(program, NAME, TabsSkills.get().skillPlans(), Images.TOOL_SKILLS.getIcon(), true);

		JLabel jNameLabel = new JLabel("Plan name");
		jName = new JTextField();
		jText = new JTextArea(18, 80);
		JScrollPane jTextScroll = new JScrollPane(jText);
		jPaste = new JButton("Paste from Clipboard", Images.EDIT_PASTE.getIcon());
		jNew = new JButton("New", Images.EDIT_ADD.getIcon());
		jSave = new JButton("Save", Images.FILTER_SAVE.getIcon());
		jDelete = new JButton("Delete", Images.EDIT_DELETE.getIcon());
		jRename = new JButton("Rename", Images.EDIT_EDIT.getIcon());

		planListModel = new DefaultListModel<>();
		jPlans = new JList<>(planListModel);
		JScrollPane jPlansScroll = new JScrollPane(jPlans);
		jPlansScroll.setPreferredSize(new java.awt.Dimension(220, 300));

		jPaste.addActionListener(e -> pasteFromClipboard());
		jNew.addActionListener(e -> newPlan());
		jSave.addActionListener(e -> savePlan());
		jDelete.addActionListener(e -> deletePlan());
		jRename.addActionListener(e -> renamePlan());
		jPlans.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					String selected = jPlans.getSelectedValue();
					if (selected != null) {
						loadPlanIntoEditor(selected);
					}
				}
			}
		});

		refreshPlanList();

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jPlansScroll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
						.addComponent(jNameLabel)
						.addComponent(jName, 200, 300, Integer.MAX_VALUE)
					)
					.addComponent(jTextScroll)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jNew, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						.addComponent(jPaste, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						.addComponent(jSave, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						.addComponent(jRename, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						.addComponent(jDelete, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					)
				)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jPlansScroll)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(jNameLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jName, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
					.addComponent(jTextScroll)
					.addGroup(layout.createParallelGroup()
						.addComponent(jNew, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jPaste, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jSave, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jRename, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jDelete, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
				)
		);

	}

	private void newPlan() {
		jPlans.clearSelection();
		jName.setText("");
		jText.setText("");
		jName.requestFocusInWindow();
	}

	private void pasteFromClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		try {
			String data = (String) clipboard.getData(DataFlavor.stringFlavor);
			if (data != null) {
				jText.setText(data);
			}
		} catch (UnsupportedFlavorException | IOException ex) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), ex.getMessage(), "Clipboard", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void savePlan() {
		String name = jName.getText().trim();
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Enter a plan name", "Skill Plan", JOptionPane.WARNING_MESSAGE);
			return;
		}

		//Lookup Table
		Map<String, Integer> names = new HashMap<>();
		for (Item item : StaticData.get().getItems().values()) {
			names.put(item.getTypeName().toLowerCase(), item.getTypeID());
		}
		
		String[] lines = jText.getText().split("\r?\n");
		Map<Integer, Integer> map = new LinkedHashMap<>();
		for (String line : lines) {
			String trimmed = line.trim();
			if (trimmed.isEmpty()) {
				continue;
			}
			int space = lastSpaceIndex(trimmed);
			if (space <= 0 || space == trimmed.length() - 1) {
				continue;
			}
			String skillName = trimmed.substring(0, space).trim();
			String levelStr = trimmed.substring(space + 1).trim();
			int level;
			try {
				level = Integer.parseInt(levelStr);
			} catch (NumberFormatException ex) {
				continue;
			}
			level = Math.max(1, Math.min(5, level));
			Integer typeID = names.get(skillName.toLowerCase());
			if (typeID == null || typeID == 0) {
				continue;
			}
			map.put(typeID, level);
		}
		if (map.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "No valid skills found", "Skill Plan", JOptionPane.WARNING_MESSAGE);
			return;
		}
		Settings.lock("Save Skill Plan");
		try {
			Settings.get().getSkillPlans().put(name, map);
		} finally {
			Settings.unlock("Save Skill Plan");
		}
		program.saveSettings("Skill Plan: " + name);
		if (program.getMainWindow().isOpen(program.getSkillsOverviewTab())) {
			program.getSkillsOverviewTab().updateData();
		}
		refreshPlanList();
		JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Saved plan '" + name + "' (" + map.size() + ")", "Skill Plan", JOptionPane.INFORMATION_MESSAGE);
	}

	private void deletePlan() {
		String name = jName.getText().trim();
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Enter a plan name to delete", "Skill Plan", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (!Settings.get().getSkillPlans().containsKey(name)) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Plan not found: " + name, "Skill Plan", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int confirm = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), "Delete plan '" + name + "'?", "Skill Plan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm != JOptionPane.OK_OPTION) {
			return;
		}
		Settings.lock("Delete Skill Plan");
		try {
			Settings.get().getSkillPlans().remove(name);
		} finally {
			Settings.unlock("Delete Skill Plan");
		}
		program.saveSettings("Skill Plan (Delete): " + name);
		if (program.getMainWindow().isOpen(program.getSkillsOverviewTab())) {
			program.getSkillsOverviewTab().updateData();
		}
		refreshPlanList();
		JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Deleted plan '" + name + "'", "Skill Plan", JOptionPane.INFORMATION_MESSAGE);
	}

	private void renamePlan() {
		String name = jName.getText().trim();
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Enter the current plan name", "Skill Plan", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (!Settings.get().getSkillPlans().containsKey(name)) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Plan not found: " + name, "Skill Plan", JOptionPane.WARNING_MESSAGE);
			return;
		}
		String newName = JOptionPane.showInputDialog(program.getMainWindow().getFrame(), "New name for '" + name + "':", name);
		if (newName == null) {
			return;
		}
		newName = newName.trim();
		if (newName.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "New name cannot be empty", "Skill Plan", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (name.equals(newName)) {
			return;
		}
		Settings.lock("Rename Skill Plan");
		try {
			Map<Integer, Integer> map = Settings.get().getSkillPlans().remove(name);
			if (map != null) {
				Settings.get().getSkillPlans().put(newName, map);
			}
		} finally {
			Settings.unlock("Rename Skill Plan");
		}
		program.saveSettings("Skill Plan (Rename): " + name + " -> " + newName);
		if (program.getMainWindow().isOpen(program.getSkillsOverviewTab())) {
			program.getSkillsOverviewTab().updateData();
		}
		refreshPlanList();
		JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Renamed plan to '" + newName + "'", "Skill Plan", JOptionPane.INFORMATION_MESSAGE);
	}

	private void refreshPlanList() {
		planListModel.clear();
		for (String planName : Settings.get().getSkillPlans().keySet()) {
			planListModel.addElement(planName);
		}
	}

	private void loadPlanIntoEditor(String planName) {
		jName.setText(planName);
		Map<Integer, Integer> map = Settings.get().getSkillPlans().get(planName);
		if (map == null) {
			jText.setText("");
			return;
		}
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<Integer, Integer> e : map.entrySet()) {
			Item item = ApiIdConverter.getItem(e.getKey());
			if (item != null && item.getTypeID() != 0) {
				builder.append(item.getTypeName()).append(' ').append(e.getValue()).append('\n');
			}
		}
		jText.setText(builder.toString());
	}

	private static int lastSpaceIndex(String s) {
		for (int i = s.length() - 1; i >= 0; i--) {
			if (Character.isWhitespace(s.charAt(i)))
				return i;
		}
		return -1;
	}

	@Override
	public void updateCache() {
	}

	@Override
	public void clearData() {
	}

	@Override
	public Collection<LocationType> getLocations() {
		return new ArrayList<>();
	}
}

