package net.nikr.eve.jeveassets.tests;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.nikr.eve.jeveasset.gui.dialogs.EditableListModel;
import net.nikr.eve.jeveasset.gui.dialogs.MoveJList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andrew
 */
public class TestMoveJList {
	MoveJList<Something> a;
	MoveJList<Something> b;
	Something[] somethings;

	@Before
	public void setup() {
		Comparator<Something> comp = new Comparator<Something>() {
			@Override
			public int compare(Something o1, Something o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};
		a = new MoveJList<Something>();
		a.getEditableModel().setSortComparator(comp);
		b = new MoveJList<Something>();
		b.getEditableModel().setSortComparator(comp);

		somethings = new Something[] {
							new Something("foo")
						, new Something("foobar")
						, new Something("zap")
		};

		for (int i = 0; i < somethings.length; ++i) {
			a.getEditableModel().add(somethings[i]);
		}
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
		a.setSelectedIndices(new int[] {0,1, 2});
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
