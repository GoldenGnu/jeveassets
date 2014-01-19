/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import ca.odell.glazedlists.GlazedLists;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.tag.Tag;
import net.nikr.eve.jeveasset.data.tag.TagID;
import net.nikr.eve.jeveasset.data.tag.Tags;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.JTagsDialog;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class TagsSettingsPanel extends JSettingsPanel {

	private enum TagsSettingsAction {
		EDIT, DELETE, ADD
	}

	//GUI
	private JList jTags;
	private JButton jAdd;
	private JButton jEdit;
	private JButton jDelete;
	private DefaultListModel listModel;
	private JTagsDialog jTagsDialog;

	//Date
	private List<TagTask> tasks = new ArrayList<TagTask>();
	private Set<String> currentTags = new HashSet<String>();

	public TagsSettingsPanel(Program program, SettingsDialog settingsDialog) {
		super(program, settingsDialog, GuiShared.get().tags(), Images.TAG_GRAY.getIcon());

		ListenerClass listener = new ListenerClass();

		jTagsDialog = new JTagsDialog(program);

		listModel = new DefaultListModel();

		jTags = new JList(listModel);
		jTags.setCellRenderer(new TagListCellRenderer(jTags.getCellRenderer()));
		jTags.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTags.addListSelectionListener(listener);

		jAdd = new JButton(GuiShared.get().add());
		jAdd.setActionCommand(TagsSettingsAction.ADD.name());
		jAdd.addActionListener(listener);

		jEdit = new JButton(GuiShared.get().edit());
		jEdit.setActionCommand(TagsSettingsAction.EDIT.name());
		jEdit.addActionListener(listener);

		jDelete = new JButton(GuiShared.get().delete());
		jDelete.setActionCommand(TagsSettingsAction.DELETE.name());
		jDelete.addActionListener(listener);

		JScrollPane jTagsScroll = new JScrollPane(jTags);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jTagsScroll, 150, 150, 150)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(jAdd, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jEdit, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jDelete, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jTagsScroll, 200, 200, 200)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jAdd, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jEdit, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDelete, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	@Override
	public boolean save() {
		for (TagTask task : tasks) {
			task.runTask();
		}
		if (!tasks.isEmpty()) {
			program.updateTags();
		}
		return false;
	}

	@Override
	public void load() {
		tasks.clear();
		
		Set<Tag> allTags = new TreeSet<Tag>(GlazedLists.comparableComparator());
		allTags.addAll(Settings.get().getTags().values());

		currentTags.clear();
		currentTags.addAll(Settings.get().getTags().keySet());

		jTags.clearSelection();

		listModel.clear();
		for (Tag tag : allTags) {
			listModel.addElement(tag);
			
		}

		jEdit.setEnabled(false);
		jDelete.setEnabled(false);
	}

	private int addToList(Tag tag, int index) {
		boolean ok = false;
		for (int i = 0; i < listModel.size(); i++) {
			Tag listTag = (Tag) listModel.get(i);
			int compareTo = listTag.compareTo(tag);
			if (compareTo >= 0) {
				listModel.insertElementAt(tag, i);
				index = i; //Update selected index
				ok = true;
				break;
			}
		}
		if (!ok) { //Handle insert if last in list
			listModel.addElement(tag);
			index = listModel.size() - 1; //Update selected index
		}
		return index;
	}

	private class ListenerClass implements ListSelectionListener, ActionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			int index = jTags.getSelectedIndex();
			boolean valid = (index >= 0);
			jEdit.setEnabled(valid);
			jDelete.setEnabled(valid);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (TagsSettingsAction.ADD.name().equals(e.getActionCommand())) {
				Tag tag = jTagsDialog.show(currentTags);
				if (tag != null && !tag.getName().isEmpty()) {
					//Save for update (only executed if saved AKA ignored on cancel)
					tasks.add(new AddTask(tag));
					//Update List
					addToList(tag, -1);
					//Update current tags
					currentTags.add(tag.getName());
				}
			} else if (TagsSettingsAction.EDIT.name().equals(e.getActionCommand())) {
				Tag tag = (Tag) jTags.getSelectedValue();
				Tag editedTag = jTagsDialog.show(tag, currentTags);
				if (editedTag != null && !editedTag.getName().isEmpty()) {
					//Update count
					editedTag.getIDs().addAll(tag.getIDs());
					//Update current tags
					currentTags.remove(tag.getName());
					currentTags.add(editedTag.getName());
					//Remove from List
					listModel.removeElement(tag);
					//Get original tag
					if (tag instanceof EditTag) {
						EditTag editTag = (EditTag) tag;
						tag = editTag.getTag();
					}
					//Save for update (only executed if saved AKA ignored on cancel)
					tasks.add(new EditTask(tag, editedTag));
					//Save selected index
					int index = jTags.getSelectedIndex();
					//Add to list
					index = addToList(new EditTag(tag, editedTag), index);
					//Load selected index
					jTags.setSelectedIndex(index);
				}
			} else if (TagsSettingsAction.DELETE.name().equals(e.getActionCommand())) {
				Tag tag = (Tag) jTags.getSelectedValue();
				//Save for update (only executed if saved AKA ignored on cancel)
				tasks.add(new DeleteTask(tag));
				//Update List
				listModel.removeElement(tag);
				//Update current tags
				currentTags.remove(tag.getName());
			}
		}
	}

	private abstract static class TagTask {
		public abstract void runTask();
	}

	private static class EditTask extends TagTask {
		private Tag tag;
		private Tag editedTag;

		public EditTask(Tag tag, Tag editedTag) {
			this.tag = tag;
			this.editedTag = editedTag;
		}

		@Override
		public void runTask() {
			//Remove old
			Settings.get().getTags().remove(tag.getName());
			//Update tag
			tag.update(editedTag);
			//Add updated
			Settings.get().getTags().put(tag.getName(), tag);
			//Update tags
			for (TagID ids : tag.getIDs()) {
				Tags tags = Settings.get().getTags(ids);
				tags.updateTags();
			}
		}
	}

	private static class AddTask extends TagTask {
		private Tag tag;

		public AddTask(Tag tag) {
			this.tag = tag;
		}

		@Override
		public void runTask() {
			Settings.get().getTags().put(tag.getName(), tag);
		}
	}

	private static class DeleteTask extends TagTask {
		private Tag tag;

		public DeleteTask(Tag tag) {
			this.tag = tag;
		}

		@Override
		public void runTask() {
			for (TagID tagID : tag.getIDs()) { //Remove from all items
				Tags tags = Settings.get().getTags(tagID);
				tags.remove(tag);
			}
			//Remove from settings
			Settings.get().getTags().remove(tag.getName());
		}
	}

	private static class EditTag extends Tag {
		private Tag tag;

		public EditTag(Tag tag, Tag editedTag) {
			super(editedTag.getName(), editedTag.getColor());
			this.tag = tag;
			this.getIDs().addAll(editedTag.getIDs());
		}

		public Tag getTag() {
			return tag;
		}
	}

	private static class TagListCellRenderer implements ListCellRenderer {

		private ListCellRenderer renderer;

		public TagListCellRenderer(ListCellRenderer renderer) {
			this.renderer = renderer;
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean expanded) {
			JLabel jLabel = (JLabel) renderer.getListCellRendererComponent(list, value, index, selected, expanded);
			if (value instanceof Tag) {
				Tag tag = (Tag) value;
				jLabel.setText(GuiShared.get().tagsName(tag.getName(), tag.getIDs().size()));
				jLabel.setIcon(new JTagsDialog.TagIcon(tag.getColor()));
			}
			return jLabel;
		}
	}
	
}
