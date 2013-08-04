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

package net.nikr.eve.jeveasset.data.types;

import java.util.Set;
import net.nikr.eve.jeveasset.i18n.General;

public interface TagsType {
	public Set<String> getTags();
	public String getTagsString();
	public void setTags(Set<String> tags);
	public void setTagsString(String tagsString);
	public long getTagsID();
	public String getTagsTool();

	public static class Util {
		public static String getTagString(Set<String> tags) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String s : tags) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(s);
			}
			if (sb.toString().isEmpty()) {
				return General.get().none();
			} else {
				return sb.toString();
			}
		}
	}
}
