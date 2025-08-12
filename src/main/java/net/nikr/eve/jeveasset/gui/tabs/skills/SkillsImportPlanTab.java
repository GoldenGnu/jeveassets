package net.nikr.eve.jeveasset.gui.tabs.skills;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabPrimary;
import net.nikr.eve.jeveasset.i18n.TabsSkills;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;

public class SkillsImportPlanTab extends JMainTabPrimary {

	public static final String NAME = "skillplanimport";

	private final JTextField jName;
	private final JTextArea jText;
	private final JButton jPaste;
	private final JButton jNew;
	private final JButton jSave;
	private final JButton jDelete;
	private final JButton jRename;
	private final JList<String> jPlans;
	private final DefaultListModel<String> planListModel;

	public SkillsImportPlanTab(Program program) {
		super(program, NAME, TabsSkills.get().skills() + " - Import Plan", Images.TOOL_SKILLS.getIcon(), true);

		JLabel nameLabel = new JLabel("Plan name");
		jName = new JTextField();
		jText = new JTextArea(18, 80);
		JScrollPane scroll = new JScrollPane(jText);
		jPaste = new JButton("Paste from Clipboard", Images.EDIT_PASTE.getIcon());
		jNew = new JButton("New", Images.EDIT_ADD.getIcon());
		jSave = new JButton("Save Plan", Images.FILTER_SAVE.getIcon());
		jDelete = new JButton("Delete Plan", Images.EDIT_DELETE.getIcon());
		jRename = new JButton("Rename Plan", Images.EDIT_EDIT.getIcon());

		planListModel = new DefaultListModel<>();
		jPlans = new JList<>(planListModel);
		JScrollPane plansScroll = new JScrollPane(jPlans);
		plansScroll.setPreferredSize(new java.awt.Dimension(220, 300));

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
						.addComponent(plansScroll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addGroup(layout.createParallelGroup()
								.addGroup(layout.createSequentialGroup()
										.addComponent(nameLabel)
										.addComponent(jName, 200, 300, Integer.MAX_VALUE))
								.addComponent(scroll)
								.addGroup(layout.createSequentialGroup()
										.addComponent(jNew, Program.getButtonsWidth(), Program.getButtonsWidth(),
												Integer.MAX_VALUE)
										.addComponent(jPaste, Program.getButtonsWidth(), Program.getButtonsWidth(),
												Integer.MAX_VALUE)
										.addComponent(jSave, Program.getButtonsWidth(), Program.getButtonsWidth(),
												Integer.MAX_VALUE)
										.addComponent(jRename, Program.getButtonsWidth(), Program.getButtonsWidth(),
												Integer.MAX_VALUE)
										.addComponent(jDelete, Program.getButtonsWidth(), Program.getButtonsWidth(),
												Integer.MAX_VALUE))));
		layout.setVerticalGroup(
				layout.createParallelGroup()
						.addComponent(plansScroll)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
										.addComponent(nameLabel, Program.getButtonsHeight(), Program.getButtonsHeight(),
												Program.getButtonsHeight())
										.addComponent(jName, Program.getButtonsHeight(), Program.getButtonsHeight(),
												Program.getButtonsHeight()))
								.addComponent(scroll)
								.addGroup(layout.createParallelGroup()
										.addComponent(jNew, Program.getButtonsHeight(), Program.getButtonsHeight(),
												Program.getButtonsHeight())
										.addComponent(jPaste, Program.getButtonsHeight(), Program.getButtonsHeight(),
												Program.getButtonsHeight())
										.addComponent(jSave, Program.getButtonsHeight(), Program.getButtonsHeight(),
												Program.getButtonsHeight())
										.addComponent(jRename, Program.getButtonsHeight(), Program.getButtonsHeight(),
												Program.getButtonsHeight())
										.addComponent(jDelete, Program.getButtonsHeight(), Program.getButtonsHeight(),
												Program.getButtonsHeight()))));

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
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), ex.getMessage(), "Clipboard",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void savePlan() {
		String name = jName.getText().trim();
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Enter a plan name", "Skill Plan",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		String[] lines = jText.getText().split("\r?\n");
		Map<Integer, Integer> map = new LinkedHashMap<>();
		List<String> errors = new ArrayList<>();
		for (String line : lines) {
			String trimmed = line.trim();
			if (trimmed.isEmpty())
				continue;
			int space = lastSpaceIndex(trimmed);
			if (space <= 0 || space == trimmed.length() - 1) {
				errors.add(trimmed);
				continue;
			}
			String skillName = trimmed.substring(0, space).trim();
			String levelStr = trimmed.substring(space + 1).trim();
			int level;
			try {
				level = Integer.parseInt(levelStr);
			} catch (NumberFormatException ex) {
				errors.add(trimmed);
				continue;
			}
			level = Math.max(1, Math.min(5, level));
			Integer typeId = findTypeIdByName(skillName);
			if (typeId == null || typeId == 0) {
				errors.add(trimmed);
				continue;
			}
			map.put(typeId, level);
		}
		if (map.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "No valid skills found", "Skill Plan",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		Settings.lock("Save Skill Plan");
		try {
			Settings.get().getSkillPlans().put(name, map);
		} finally {
			Settings.unlock("Save Skill Plan");
		}
		program.saveSettings("Skill Plan: " + name);
		if (program.getMainWindow().isOpen(program.getSkillPlansTab())) {
			program.getSkillPlansTab().updateData();
		}
		refreshPlanList();
		JOptionPane.showMessageDialog(program.getMainWindow().getFrame(),
				"Saved plan '" + name + "' (" + map.size() + ")", "Skill Plan", JOptionPane.INFORMATION_MESSAGE);
	}

	private void deletePlan() {
		String name = jName.getText().trim();
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Enter a plan name to delete",
					"Skill Plan", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (!Settings.get().getSkillPlans().containsKey(name)) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Plan not found: " + name, "Skill Plan",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		int confirm = javax.swing.JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(),
				"Delete plan '" + name + "'?", "Skill Plan", javax.swing.JOptionPane.OK_CANCEL_OPTION,
				javax.swing.JOptionPane.WARNING_MESSAGE);
		if (confirm != javax.swing.JOptionPane.OK_OPTION)
			return;
		Settings.lock("Delete Skill Plan");
		try {
			Settings.get().getSkillPlans().remove(name);
		} finally {
			Settings.unlock("Delete Skill Plan");
		}
		program.saveSettings("Skill Plan (Delete): " + name);
		if (program.getMainWindow().isOpen(program.getSkillPlansTab())) {
			program.getSkillPlansTab().updateData();
		}
		refreshPlanList();
		JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Deleted plan '" + name + "'", "Skill Plan",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void renamePlan() {
		String name = jName.getText().trim();
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Enter the current plan name",
					"Skill Plan", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (!Settings.get().getSkillPlans().containsKey(name)) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Plan not found: " + name, "Skill Plan",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		String newName = javax.swing.JOptionPane.showInputDialog(program.getMainWindow().getFrame(),
				"New name for '" + name + "':", name);
		if (newName == null)
			return;
		newName = newName.trim();
		if (newName.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "New name cannot be empty", "Skill Plan",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (name.equals(newName))
			return;
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
		if (program.getMainWindow().isOpen(program.getSkillPlansTab())) {
			program.getSkillPlansTab().updateData();
		}
		refreshPlanList();
		JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), "Renamed plan to '" + newName + "'",
				"Skill Plan", JOptionPane.INFORMATION_MESSAGE);
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
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Integer, Integer> e : map.entrySet()) {
			Item item = net.nikr.eve.jeveasset.io.shared.ApiIdConverter.getItem(e.getKey());
			if (item != null && item.getTypeID() != 0) {
				sb.append(item.getTypeName()).append(' ').append(e.getValue()).append('\n');
			}
		}
		jText.setText(sb.toString());
	}

	private Integer findTypeIdByName(String name) {
		String normalized = name.toLowerCase(Locale.ROOT);
		for (Item item : net.nikr.eve.jeveasset.data.sde.StaticData.get().getItems().values()) {
			if (item.getTypeName().equalsIgnoreCase(name)) {
				return item.getTypeID();
			}
		}
		ApiIdConverter.getItem(0);
		for (Item item : net.nikr.eve.jeveasset.data.sde.StaticData.get().getItems().values()) {
			if (item.getTypeName().toLowerCase(Locale.ROOT).equals(normalized)) {
				return item.getTypeID();
			}
		}
		return null;
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

