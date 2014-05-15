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

package net.nikr.eve.jeveasset.gui.tabs.routing;

import java.util.Arrays;
import java.util.Comparator;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Candle
 */
public class TestMoveJList {
	private MoveJList<Something> a;
	private MoveJList<Something> b;
	private Something[] somethings;

	@Before
	public void setup() {
		Comparator<Something> comp = new Comparator<Something>() {
			@Override
			public int compare(final Something o1, final Something o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		};
		somethings = new Something[] {new Something("foo"), new Something("foobar"), new Something("zap") };


		EditableListModel<Something> editableListModel = new EditableListModel<Something>(Arrays.asList(somethings));
		a = new MoveJList<Something>(editableListModel);
		a.getEditableModel().setSortComparator(comp);
		b = new MoveJList<Something>();
		b.getEditableModel().setSortComparator(comp);
	}

	@Test
	public void testMove() {
		a.setSelectedIndices(new int[] {1});
		a.move(b, 10);
		assertEquals(2, a.getEditableModel().getSize());

		assertEquals(somethings[1], b.getEditableModel().getElementAt(0));
	}

	@Test
	public void testMoveAndMoveAgain() {
		a.setSelectedIndices(new int[] {1});
		a.move(b, 10);
		assertEquals(2, a.getEditableModel().getSize());
		assertEquals(somethings[1], b.getEditableModel().getElementAt(0));

		b.setSelectedIndices(new int[] {0});
		b.move(a, 10);
		assertEquals(3, a.getEditableModel().getSize());
		assertEquals(0, b.getEditableModel().getSize());
	}

	@Test
	public void testMoveAllAndMoveBack() {
		a.setSelectedIndices(new int[] {0, 1, 2});
		a.move(b, 10);
		assertEquals(0, a.getEditableModel().getSize());
		assertEquals(3, b.getEditableModel().getSize());

		b.setSelectedIndices(new int[] {0, 1, 2});
		b.move(a, 10);
		assertEquals(3, a.getEditableModel().getSize());
		assertEquals(0, b.getEditableModel().getSize());
	}

	class Something {
		String name;

		public Something(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}
