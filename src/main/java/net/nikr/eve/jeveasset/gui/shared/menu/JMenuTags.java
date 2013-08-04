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
package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.types.TagsType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.JAutoMenu;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JMenuTags<T> extends JAutoMenu<T> {

	private enum TagsAction {
		ACTION_NEW_TAG,
	}

	private JMenuItem jNew;
	private List<TagsType> tags = new ArrayList<TagsType>();
	private ListenerClass listener = new ListenerClass();

	public JMenuTags(Program program) {
		super(GuiShared.get().tags(), program);

		setIcon(Images.TAG_GRAY.getIcon());

		jNew = new JMenuItem(GuiShared.get().tagsNew(), Images.EDIT_ADD.getIcon());
		jNew.setActionCommand(TagsAction.ACTION_NEW_TAG.name());
		jNew.addActionListener(listener);
	}

	@Override
	public void setMenuData(MenuData<T> menuData) {
		tags = menuData.getTags();

		removeAll();

		add(jNew);

		Set<String> allTags = new HashSet<String>();
		for (Set<String> tagList : getMap().values()) {
			allTags.addAll(tagList);
		}

		if (!allTags.isEmpty()) {
			addSeparator();
		}

		JCheckBoxMenuItem jMenuItem;
		for (String tag : allTags) {
			Integer count = menuData.getTagCount().get(tag);
			if (count == null) {
				count = 0;
			}
			boolean selected = count == tags.size();
			if (selected) {
				count = 0;
			}
			jMenuItem = new JCheckBoxMenuItem(GuiShared.get().tagsName(tag, count));
			jMenuItem.setActionCommand(tag);
			jMenuItem.addActionListener(listener);
			jMenuItem.setSelected(selected);
			add(jMenuItem);
		}
	}

	private void addTag(String tag) {
		if (tag != null && !tag.isEmpty()) {
			for (TagsType tagsType : tags) {
				//Add tag
				tagsType.getTags().add(tag);
				//Update TagString
				String tagString = TagsType.Util.getTagString(tagsType.getTags());
				tagsType.setTagsString(tagString);
				//Update settings
				Map<Long, Set<String>> map = getMap();
				map.put(tagsType.getTagsID(), tagsType.getTags());
			}
			//FIXME - - - > TAGS: Update Tags (no need to update all date - just need to update the data in tags column)
			program.updateEventLists();
		}
	}

	private void removeTag(String tag) {
		if (tag != null) {
			for (TagsType tagsType : tags) {
				//Remove tag
				tagsType.getTags().remove(tag);
				//Update TagString
				String tagString = TagsType.Util.getTagString(tagsType.getTags());
				tagsType.setTagsString(tagString);
				//Update settings
				Map<Long, Set<String>> map = getMap();
				if (tagsType.getTags().isEmpty()) { //Remove empty
					map.remove(tagsType.getTagsID());
				} else { //Update
					map.put(tagsType.getTagsID(), tagsType.getTags());
				}
			}
			//FIXME - - - > TAGS: Update Tags (no need to update all date - just need to update the data in tags column)
			program.updateEventLists();
		}
	}

	private Map<Long, Set<String>> getMap() {
		
		if (tags.isEmpty()) {
			return new HashMap<Long, Set<String>>(); //this should never happen
		}
		String key = tags.get(0).getTagsTool();
		Map<Long, Set<String>> map = Settings.get().getTags().get(key);
		if (map == null) {
			map = new HashMap<Long, Set<String>>();
			Settings.get().getTags().put(key, map);
		}
		return map;
	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object object = e.getSource();
			if (object instanceof JCheckBoxMenuItem){
				JCheckBoxMenuItem jCheckBoxMenuItem = (JCheckBoxMenuItem) object;
				String tag = e.getActionCommand();
				if (!jCheckBoxMenuItem.isSelected()) { //State change before this is called
					removeTag(tag);
				} else {
					addTag(tag);
				}
			} else if (TagsAction.ACTION_NEW_TAG.name().equals(e.getActionCommand())) {
				String tag = JOptionPane.showInputDialog(program.getMainWindow().getFrame(), GuiShared.get().tagsNewMsg(), GuiShared.get().tagsNewTitle(), JOptionPane.PLAIN_MESSAGE);
				addTag(tag);
			}
		}
		
	}
}
