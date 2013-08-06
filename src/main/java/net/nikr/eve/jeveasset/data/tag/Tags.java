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
package net.nikr.eve.jeveasset.data.tag;

import ca.odell.glazedlists.GlazedLists;
import java.util.Collection;
import java.util.TreeSet;
import net.nikr.eve.jeveasset.i18n.General;


public class Tags extends TreeSet<Tag> implements Comparable<Tags>{

	private String tags;
	private String tagsHtml;

	public Tags() {
		super(GlazedLists.comparableComparator());
		updateTags();
	}

	@Override
	public boolean add(Tag e) {
		boolean add = super.add(e);
		updateTags();
		return add;
	}

	@Override
	public boolean addAll(Collection<? extends Tag> c) {
		boolean addAll = super.addAll(c);
		updateTags();
		return addAll;
	}

	@Override
	public boolean remove(Object o) {
		boolean remove = super.remove(o);
		updateTags();
		return remove;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean removeAll = super.removeAll(c);
		updateTags();
		return removeAll;
	}

	@Override
	public void clear() {
		super.clear();
		updateTags();
	}

	private void updateTags() {
		updateString();
		updateHtml();
	}

	private void updateString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Tag tag : this) {
			if (first) {
				first = false;
			} else {
				sb.append(" ,");
			}
			sb.append(tag.getName());
		}
		if (isEmpty()) {
			tags = General.get().none();
		} else {
			tags = sb.toString();
		}
	}

	private void updateHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		boolean first = true;
		for (Tag tag : this) {
			if (first) {
				first = false;
			} else {
				sb.append("&nbsp;");
			}
			sb.append("<span style=\"background-color: #");
			sb.append(tag.getColor().getBackgroundHtml());
			sb.append("; color: ");
			sb.append(tag.getColor().getForegroundHtml());
			sb.append("\">&nbsp;");
			sb.append(tag.getName());
			sb.append("&nbsp;</span>");
		}
		if (this.isEmpty()) {
			sb.append("<span style=\"color: #999999; font-style: italic;\">");
			sb.append(General.get().none());
			sb.append("</span>");
		}
		tagsHtml = sb.toString();
	}

	public String getHtml() {
		return tagsHtml;
	}

	@Override
	public String toString() {
		return tags;
	}

	@Override
	public int compareTo(Tags o) {
		if (isEmpty() && o.isEmpty()) {
			return 0;
		} else if (isEmpty() && !o.isEmpty()) {
			return 1;
		} else if (!isEmpty() && o.isEmpty()) {
			return -1;
		} else {
			return toString().compareTo(o.toString());
		}
	}

	

}