package net.nikr.eve.jeveassets.tests;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.nikr.eve.jeveasset.gui.dialogs.EditableListModel;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author Andrew
 */
public class TestEditableModel {
	Comparator<ListContents> comp;
	List<ListContents> contents;

	@Before
	public void setup() {
		Comparator<ListContents> comp = new Comparator<ListContents>() {
			@Override
			public int compare(ListContents o1, ListContents o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		};
		contents = new ArrayList<ListContents>();
		contents.add(new ListContents("abc"));
		contents.add(new ListContents("abcd"));
		contents.add(new ListContents("tgv"));
		contents.add(new ListContents("123"));
		contents.add(new ListContents("gt"));
		contents.add(new ListContents("lt"));
	}

	@Test
	public void testCreate() {
		EditableListModel<ListContents> elm = new EditableListModel<ListContents>(
						contents
						, comp
						);
		assertNotSame("contents should be a different object (unmodifiable list)", contents, elm.getAll());
		assertSame("The comparator should be the same", comp, elm.getSortComparator());
	}

	class ListContents {
		String name;

		public ListContents(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}
