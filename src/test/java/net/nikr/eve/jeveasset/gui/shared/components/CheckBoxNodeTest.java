/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.components;

import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.Test;
import static org.junit.Assert.*;


public class CheckBoxNodeTest {
	private CheckBoxNode root;
	private CheckBoxNode a;
	private CheckBoxNode a1;
	private CheckBoxNode a2;
	private CheckBoxNode a3;
	private CheckBoxNode b;
	private CheckBoxNode b1;
	private CheckBoxNode b2;
	private CheckBoxNode b3;
	private CheckBoxNode c;
	private CheckBoxNode c1;
	private CheckBoxNode c2;
	private CheckBoxNode c3;
	private List<CheckBoxNode> all = new ArrayList<>();
	

	@Test
	public void testSetSelected() {
		makeNodes(true);

		c3.setSelected(false);
		assertThat(root.isSelected(), equalTo(false));
		assertThat(a.isSelected(), equalTo(true));
		assertThat(a1.isSelected(), equalTo(true));
		assertThat(a2.isSelected(), equalTo(true));
		assertThat(a3.isSelected(), equalTo(true));
		assertThat(b.isSelected(), equalTo(true));
		assertThat(b1.isSelected(), equalTo(true));
		assertThat(b2.isSelected(), equalTo(true));
		assertThat(b3.isSelected(), equalTo(true));
		assertThat(c.isSelected(), equalTo(false));
		assertThat(c1.isSelected(), equalTo(true));
		assertThat(c2.isSelected(), equalTo(true));
		assertThat(c3.isSelected(), equalTo(false));

		c3.setSelected(true);
		assertThat(root.isSelected(), equalTo(true));
		assertThat(a.isSelected(), equalTo(true));
		assertThat(a1.isSelected(), equalTo(true));
		assertThat(a2.isSelected(), equalTo(true));
		assertThat(a3.isSelected(), equalTo(true));
		assertThat(b.isSelected(), equalTo(true));
		assertThat(b1.isSelected(), equalTo(true));
		assertThat(b2.isSelected(), equalTo(true));
		assertThat(b3.isSelected(), equalTo(true));
		assertThat(c.isSelected(), equalTo(true));
		assertThat(c1.isSelected(), equalTo(true));
		assertThat(c2.isSelected(), equalTo(true));
		assertThat(c3.isSelected(), equalTo(true));

		c.setSelected(false);
		assertThat(root.isSelected(), equalTo(false));
		assertThat(a.isSelected(), equalTo(true));
		assertThat(a1.isSelected(), equalTo(true));
		assertThat(a2.isSelected(), equalTo(true));
		assertThat(a3.isSelected(), equalTo(true));
		assertThat(b.isSelected(), equalTo(true));
		assertThat(b1.isSelected(), equalTo(true));
		assertThat(b2.isSelected(), equalTo(true));
		assertThat(b3.isSelected(), equalTo(true));
		assertThat(c.isSelected(), equalTo(false));
		assertThat(c1.isSelected(), equalTo(false));
		assertThat(c2.isSelected(), equalTo(false));
		assertThat(c3.isSelected(), equalTo(false));

		c.setSelected(true);
		assertThat(root.isSelected(), equalTo(true));
		assertThat(a.isSelected(), equalTo(true));
		assertThat(a1.isSelected(), equalTo(true));
		assertThat(a2.isSelected(), equalTo(true));
		assertThat(a3.isSelected(), equalTo(true));
		assertThat(b.isSelected(), equalTo(true));
		assertThat(b1.isSelected(), equalTo(true));
		assertThat(b2.isSelected(), equalTo(true));
		assertThat(b3.isSelected(), equalTo(true));
		assertThat(c.isSelected(), equalTo(true));
		assertThat(c1.isSelected(), equalTo(true));
		assertThat(c2.isSelected(), equalTo(true));
		assertThat(c3.isSelected(), equalTo(true));

		root.setSelected(false);
		assertThat(root.isSelected(), equalTo(false));
		assertThat(a.isSelected(), equalTo(false));
		assertThat(a1.isSelected(), equalTo(false));
		assertThat(a2.isSelected(), equalTo(false));
		assertThat(a3.isSelected(), equalTo(false));
		assertThat(b.isSelected(), equalTo(false));
		assertThat(b1.isSelected(), equalTo(false));
		assertThat(b2.isSelected(), equalTo(false));
		assertThat(b3.isSelected(), equalTo(false));
		assertThat(c.isSelected(), equalTo(false));
		assertThat(c1.isSelected(), equalTo(false));
		assertThat(c2.isSelected(), equalTo(false));
		assertThat(c3.isSelected(), equalTo(false));

		root.setSelected(true);
		assertThat(root.isSelected(), equalTo(true));
		assertThat(a.isSelected(), equalTo(true));
		assertThat(a1.isSelected(), equalTo(true));
		assertThat(a2.isSelected(), equalTo(true));
		assertThat(a3.isSelected(), equalTo(true));
		assertThat(b.isSelected(), equalTo(true));
		assertThat(b1.isSelected(), equalTo(true));
		assertThat(b2.isSelected(), equalTo(true));
		assertThat(b3.isSelected(), equalTo(true));
		assertThat(c.isSelected(), equalTo(true));
		assertThat(c1.isSelected(), equalTo(true));
		assertThat(c2.isSelected(), equalTo(true));
		assertThat(c3.isSelected(), equalTo(true));

		root.setSelected(false);
		assertThat(root.isSelected(), equalTo(false));
		assertThat(a.isSelected(), equalTo(false));
		assertThat(a1.isSelected(), equalTo(false));
		assertThat(a2.isSelected(), equalTo(false));
		assertThat(a3.isSelected(), equalTo(false));
		assertThat(b.isSelected(), equalTo(false));
		assertThat(b1.isSelected(), equalTo(false));
		assertThat(b2.isSelected(), equalTo(false));
		assertThat(b3.isSelected(), equalTo(false));
		assertThat(c.isSelected(), equalTo(false));
		assertThat(c1.isSelected(), equalTo(false));
		assertThat(c2.isSelected(), equalTo(false));
		assertThat(c3.isSelected(), equalTo(false));

		c1.setSelected(true);
		c2.setSelected(true);
		c3.setSelected(true);
		assertThat(root.isSelected(), equalTo(false));
		assertThat(a.isSelected(), equalTo(false));
		assertThat(a1.isSelected(), equalTo(false));
		assertThat(a2.isSelected(), equalTo(false));
		assertThat(a3.isSelected(), equalTo(false));
		assertThat(b.isSelected(), equalTo(false));
		assertThat(b1.isSelected(), equalTo(false));
		assertThat(b2.isSelected(), equalTo(false));
		assertThat(b3.isSelected(), equalTo(false));
		assertThat(c.isSelected(), equalTo(true));
		assertThat(c1.isSelected(), equalTo(true));
		assertThat(c2.isSelected(), equalTo(true));
		assertThat(c3.isSelected(), equalTo(true));

		a.setSelected(true);
		b.setSelected(true);
		assertThat(root.isSelected(), equalTo(true));
		assertThat(a.isSelected(), equalTo(true));
		assertThat(a1.isSelected(), equalTo(true));
		assertThat(a2.isSelected(), equalTo(true));
		assertThat(a3.isSelected(), equalTo(true));
		assertThat(b.isSelected(), equalTo(true));
		assertThat(b1.isSelected(), equalTo(true));
		assertThat(b2.isSelected(), equalTo(true));
		assertThat(b3.isSelected(), equalTo(true));
		assertThat(c.isSelected(), equalTo(true));
		assertThat(c1.isSelected(), equalTo(true));
		assertThat(c2.isSelected(), equalTo(true));
		assertThat(c3.isSelected(), equalTo(true));
	}

	@Test
	public void testSetShown() {
		makeNodes(false);
		//Step 1: Select All
		root.setSelected(true);
		assertThat(root.isSelected(), equalTo(true));
		assertThat(a.isSelected(), equalTo(true));
		assertThat(a1.isSelected(), equalTo(true));
		assertThat(a2.isSelected(), equalTo(true));
		assertThat(a3.isSelected(), equalTo(true));
		assertThat(b.isSelected(), equalTo(true));
		assertThat(b1.isSelected(), equalTo(true));
		assertThat(b2.isSelected(), equalTo(true));
		assertThat(b3.isSelected(), equalTo(true));
		assertThat(c.isSelected(), equalTo(true));
		assertThat(c1.isSelected(), equalTo(true));
		assertThat(c2.isSelected(), equalTo(true));
		assertThat(c3.isSelected(), equalTo(true));
		//Step 2 Deselect B
		b.setSelected(false);
		assertThat(root.isSelected(), equalTo(false));
		assertThat(a.isSelected(), equalTo(true));
		assertThat(a1.isSelected(), equalTo(true));
		assertThat(a2.isSelected(), equalTo(true));
		assertThat(a3.isSelected(), equalTo(true));
		assertThat(b.isSelected(), equalTo(false));
		assertThat(b1.isSelected(), equalTo(false));
		assertThat(b2.isSelected(), equalTo(false));
		assertThat(b3.isSelected(), equalTo(false));
		assertThat(c.isSelected(), equalTo(true));
		assertThat(c1.isSelected(), equalTo(true));
		assertThat(c2.isSelected(), equalTo(true));
		assertThat(c3.isSelected(), equalTo(true));
		//Step 3 Hide all
		hideAll();
		assertThat(root.isSelected(), equalTo(false));
		assertThat(a.isSelected(), equalTo(true));
		assertThat(a1.isSelected(), equalTo(true));
		assertThat(a2.isSelected(), equalTo(true));
		assertThat(a3.isSelected(), equalTo(true));
		assertThat(b.isSelected(), equalTo(false));
		assertThat(b1.isSelected(), equalTo(false));
		assertThat(b2.isSelected(), equalTo(false));
		assertThat(b3.isSelected(), equalTo(false));
		assertThat(c.isSelected(), equalTo(true));
		assertThat(c1.isSelected(), equalTo(true));
		assertThat(c2.isSelected(), equalTo(true));
		assertThat(c3.isSelected(), equalTo(true));
		//Step 4 Show A and C
		matchTrue(a);
		matchTrue(c);
		assertThat(root.isSelected(), equalTo(true));
		assertThat(a.isSelected(), equalTo(true));
		assertThat(a1.isSelected(), equalTo(true));
		assertThat(a2.isSelected(), equalTo(true));
		assertThat(a3.isSelected(), equalTo(true));
		assertThat(b.isSelected(), equalTo(false));
		assertThat(b1.isSelected(), equalTo(false));
		assertThat(b2.isSelected(), equalTo(false));
		assertThat(b3.isSelected(), equalTo(false));
		assertThat(c.isSelected(), equalTo(true));
		assertThat(c1.isSelected(), equalTo(true));
		assertThat(c2.isSelected(), equalTo(true));
		assertThat(c3.isSelected(), equalTo(true));
	}

	private void hideAll() {
		for (CheckBoxNode node : all) {
			node.hide();
		}
	}

	private void matchTrue(CheckBoxNode node) {
		node.matches(node.getNodeName());
	}
	
	
	private void makeNodes(boolean selected) {
		root = new CheckBoxNode(null, "root", "root", selected);
		a = new CheckBoxNode(root, "aa", "aa", selected);
		a1 = new CheckBoxNode(a, "a1", "a1", selected);
		a2 = new CheckBoxNode(a, "a2", "a2", selected);
		a3 = new CheckBoxNode(a, "a3", "a3", selected);
		b = new CheckBoxNode(root, "bb", "bb", selected);
		b1 = new CheckBoxNode(b, "b1", "b1", selected);
		b2 = new CheckBoxNode(b, "b2", "b2", selected);
		b3 = new CheckBoxNode(b, "b3", "b3", selected);
		c = new CheckBoxNode(root, "cc", "cc", selected);
		c1 = new CheckBoxNode(c, "c1", "c1", selected);
		c2 = new CheckBoxNode(c, "c2", "c2", selected);
		c3 = new CheckBoxNode(c, "c3", "c3", selected);
		all.add(root);
		all.add(a);
		all.add(a1);
		all.add(a2);
		all.add(a3);
		all.add(b);
		all.add(b1);
		all.add(b2);
		all.add(b3);
		all.add(c);
		all.add(c1);
		all.add(c2);
		all.add(c3);
		

		assertThat(root.isSelected(), equalTo(selected));
		assertThat(root.isShown(), equalTo(true));
		assertThat(a.isSelected(), equalTo(selected));
		assertThat(a.isShown(), equalTo(true));
		assertThat(a1.isSelected(), equalTo(selected));
		assertThat(a1.isShown(), equalTo(true));
		assertThat(a2.isSelected(), equalTo(selected));
		assertThat(a2.isShown(), equalTo(true));
		assertThat(a3.isSelected(), equalTo(selected));
		assertThat(a3.isShown(), equalTo(true));
		assertThat(b.isSelected(), equalTo(selected));
		assertThat(b.isShown(), equalTo(true));
		assertThat(b1.isSelected(), equalTo(selected));
		assertThat(b1.isShown(), equalTo(true));
		assertThat(b2.isSelected(), equalTo(selected));
		assertThat(b2.isShown(), equalTo(true));
		assertThat(b3.isSelected(), equalTo(selected));
		assertThat(b3.isShown(), equalTo(true));
		assertThat(c.isSelected(), equalTo(selected));
		assertThat(c.isShown(), equalTo(true));
		assertThat(c1.isSelected(), equalTo(selected));
		assertThat(c1.isShown(), equalTo(true));
		assertThat(c2.isSelected(), equalTo(selected));
		assertThat(c2.isShown(), equalTo(true));
		assertThat(c3.isSelected(), equalTo(selected));
		assertThat(c3.isShown(), equalTo(true));
	}

	private void printTree() {
		System.out.println(nodeToString(root));
		System.out.println("  "+ nodeToString(a));
		System.out.println("    "+ nodeToString(a1));
		System.out.println("    "+ nodeToString(a2));
		System.out.println("    "+ nodeToString(a3));
		System.out.println("  "+ nodeToString(b));
		System.out.println("    "+ nodeToString(b1));
		System.out.println("    "+ nodeToString(b2));
		System.out.println("    "+ nodeToString(b3));
		System.out.println("  "+ nodeToString(c));
		System.out.println("    "+ nodeToString(c1));
		System.out.println("    "+ nodeToString(c2));
		System.out.println("    "+ nodeToString(c3));
	}

	
	private String nodeToString(CheckBoxNode root) {
		return root.getNodeName() + " - show: " + root.isShown() + " selected:" + root.isSelected();
	}
}
