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
package net.nikr.eve.jeveasset.data.tag;

import ca.odell.glazedlists.GlazedLists;
import java.awt.Color;
import java.awt.Font;
import java.util.Collection;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.i18n.General;


public class Tags extends TreeSet<Tag> implements Comparable<Tags>{

	private String tags;
	private String html;
	private final JPanel jPanel;

	public Tags() {
		super(GlazedLists.comparableComparator());
		jPanel = new JPanel();
		BoxLayout layout = new BoxLayout(jPanel, BoxLayout.X_AXIS);
		jPanel.setLayout(layout);

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

	public final void updateTags() {
		updateString();
		updatePanel();
		updateHTML();
	}

	private void updateString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Tag tag : this) {
			if (first) {
				first = false;
			} else {
				sb.append(" ");
			}
			sb.append(tag.getName());
		}
		if (isEmpty()) {
			tags = General.get().none();
		} else {
			tags = sb.toString();
		}
	}

	private void updatePanel() {
		jPanel.removeAll();
		boolean first = true;
		for (Tag tag : this) {
			if (first) {
				first = false;
			} else {
				jPanel.add(Box.createHorizontalStrut(3));
			}
			JLabel jLabel = new JLabel(tag.getName());
			jLabel.setOpaque(true);
			jLabel.setBackground(tag.getColor().getBackground());
			jLabel.setForeground(tag.getColor().getForeground());
			jLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 2));
			jPanel.add(jLabel);
		}
		if (isEmpty()) {
			JLabel jLabel = new JLabel(General.get().none());
			Font font = jLabel.getFont();
			jLabel.setFont(new Font(font.getName(), Font.ITALIC, font.getSize()));
			jLabel.setForeground(Color.DARK_GRAY);
			jPanel.add(jLabel);
		}
	}

	private void updateHTML() {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Tag tag : this) {
			if (first) {
				first = false;
			} else {
				builder.append("&nbsp;");
			}
			builder.append("<span_style=\"");
			builder.append("background-color:_#");
			builder.append(tag.getColor().getBackgroundHtml());
			builder.append(";_");
			builder.append("color:_#");
			builder.append(tag.getColor().getForegroundHtml());
			builder.append(";_");
			builder.append("\">");
			builder.append(" ");
			builder.append(tag.getName());
			builder.append(" ");
			builder.append("</span>");
			
		}
		html = builder.toString();
	}

	public JPanel getPanel() {
		return jPanel;
	}

	public String getHtml() {
		return html;
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