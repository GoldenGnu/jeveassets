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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.components.JManagerDialog;
import net.nikr.eve.jeveasset.i18n.TabsSkills;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;


public class JSkillPlansManageDialog extends JManagerDialog {

	private final SkillsOverviewTab skillsOverviewTab;

	public JSkillPlansManageDialog(Program program, SkillsOverviewTab skillsOverviewTab) {
		super(program, program.getMainWindow().getFrame(), TabsSkills.get().manageTitle(), false, true, true, true, false);
		this.skillsOverviewTab = skillsOverviewTab;
	}

	@Override
	protected void load(String name) { }

	@Override
	protected void edit(String name) {
		Map<Integer, Integer> oldSkills = Settings.get().getSkillPlans().getOrDefault(name, Collections.emptyMap());
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<Integer, Integer> e : oldSkills.entrySet()) {
			Item item = ApiIdConverter.getItem(e.getKey());
			if (!item.isEmpty()) {
				builder.append(item.getTypeName()).append(' ').append(e.getValue()).append('\n');
			}
		}
		Map<Integer, Integer> newSkills = skillsOverviewTab.importSkillPlan(builder.toString());
		if (newSkills == null || newSkills.isEmpty()) {
			return;
		}
		Settings.lock("Skills Overview (Edit Plan)");
		Settings.get().getSkillPlans().put(name, newSkills);
		Settings.unlock("Skills Overview (Edit Plan)");
		program.saveSettings("Skills Overview (Edit Plan)");
		skillsOverviewTab.updateData();
	}

	@Override
	protected void merge(String name, List<String> list) {
		Map<Integer, Integer> output = new HashMap<>();
		for (String s : list) {
			for (Map.Entry<Integer, Integer> entry : Settings.get().getSkillPlans().getOrDefault(s, Collections.emptyMap()).entrySet()) {
				Integer key = entry.getKey();
				Integer newValue = entry.getValue();
				Integer oldValue = output.get(key);
				if (oldValue == null) {
					output.put(key, newValue);
				} else {
					output.put(key, Math.max(newValue, oldValue));
				}
			}
		}
		Settings.lock("Skills Overview (Merge Plans)");
		Settings.get().getSkillPlans().put(name, output);
		Settings.unlock("Skills Overview (Merge Plans)");
		program.saveSettings("Skills Overview (Merge Plans)");
		updateData();
		skillsOverviewTab.updateData();
	}

	@Override
	protected void copy(String fromName, String toName) {
		Map<Integer, Integer> skills = Settings.get().getSkillPlans().getOrDefault(fromName, Collections.emptyMap());
		Settings.lock("Skills Overview (Copy Plan)");
		Settings.get().getSkillPlans().put(toName, skills);
		Settings.unlock("Skills Overview (Copy Plan)");
		program.saveSettings("Skills Overview (Copy Plan)");
		updateData();
		skillsOverviewTab.updateData();
	}

	@Override
	protected void rename(String name, String oldName) {
		Settings.lock("Skills Overview (Rename Plan)");
		Map<Integer, Integer> remove = Settings.get().getSkillPlans().remove(oldName);
		Settings.get().getSkillPlans().put(name, remove);
		Settings.unlock("Skills Overview (Rename Plan)");
		program.saveSettings("Skills Overview (Rename Plan)");
		updateData();
		skillsOverviewTab.updateData();

	}

	@Override
	protected void delete(List<String> list) {
		Settings.lock("Skills Overview (Delete Plans)");
		Settings.get().getSkillPlans().keySet().removeAll(list);
		Settings.unlock("Skills Overview (Delete Plans)");
		program.saveSettings("Skills Overview (Delete Plans)");
		updateData();
		skillsOverviewTab.updateData();
	}

	@Override
	protected void export(List<String> list) {
		//Export is not supported
	}

	@Override
	protected void importData() {
		//Import is not supported
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			updateData();
		}
		super.setVisible(b);
	}

	private void updateData() {
		update(Settings.get().getSkillPlans().keySet());
	}

	@Override protected String textDeleteMultipleMsg(int size) { return TabsSkills.get().deleteSkillPlans(size); }
	@Override protected String textDelete() { return TabsSkills.get().deleteSkillPlan(); }
	@Override protected String textEnterName() { return TabsSkills.get().enterName(); }
	@Override protected String textMerge() { return TabsSkills.get().merge(); }
	@Override protected String textRename() { return TabsSkills.get().rename(); }
	@Override protected String textOverwrite() { return TabsSkills.get().overwrite(); }

}
