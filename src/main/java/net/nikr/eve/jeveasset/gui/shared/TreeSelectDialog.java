/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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


package net.nikr.eve.jeveasset.gui.shared;

import java.awt.Dimension;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import net.nikr.eve.jeveasset.Program;

public class TreeSelectDialog extends JDialogCentered {

	//Form components
	private JDialog dialog;
	private JLabel leafFilterLabel;
	private JTextField leafFilterTextField;
	private JLabel filterInfoLabel;
	private JLabel filterInfoResultLabel;
	private JLabel selectedLeafLabel;
	private JLabel selectedLeafValueLabel;
	private JButton cancelButton;
	private JButton addButton;
	private JTree tree;
	private JScrollPane treeScrollPane;

	public TreeSelectDialog(Program program, String title) {
		super(program, title);
		dialog = getDialog();
		dialog.setResizable(true);
		dialog.setMinimumSize(new Dimension(300,400));
		leafFilterLabel = new JLabel();
		leafFilterTextField = new JTextField();
		filterInfoLabel = new JLabel();
		filterInfoResultLabel = new JLabel();
		selectedLeafLabel = new JLabel();
		selectedLeafValueLabel = new JLabel();
		cancelButton = new JButton();
		addButton = new JButton();
		tree = new JTree();
		treeScrollPane = new JScrollPane(tree);
		layoutComponents();
	}

	// <editor-fold defaultstate="collapsed" desc="Component Accessors">

	/**
	 * @return the leafFilterLabel
	 */
	public JLabel getLeafFilterLabel() {
		return leafFilterLabel;
	}

	/**
	 * @return the leafFilterTextField
	 */
	public JTextField getLeafFilterTextField() {
		return leafFilterTextField;
	}

	/**
	 * @return the filterInfoLabel
	 */
	public JLabel getFilterInfoLabel() {
		return filterInfoLabel;
	}

	/**
	 * @return the filterInfoResultLabel
	 */
	public JLabel getFilterInfoResultLabel() {
		return filterInfoResultLabel;
	}

	/**
	 * @return the selectedLeafLabel
	 */
	public JLabel getSelectedLeafLabel() {
		return selectedLeafLabel;
	}

	/**
	 * @return the selectedLeafValueLabel
	 */
	public JLabel getSelectedLeafValueLabel() {
		return selectedLeafValueLabel;
	}

	/**
	 * @return the cancelButton
	 */
	public JButton getCancelButton() {
		return cancelButton;
	}

	/**
	 * @return the addButton
	 */
	public JButton getAddButton() {
		return addButton;
	}

	/**
	 * @return the tree
	 */
	public JTree getTree() {
		return tree;
	}

	// </editor-fold>

	private void layoutComponents() {

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(getLeafFilterLabel())
					.addComponent(getLeafFilterTextField())
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(getFilterInfoLabel())
					.addComponent(getFilterInfoResultLabel())
				)
				.addComponent(treeScrollPane)
				.addGroup(layout.createSequentialGroup()
					.addComponent(getSelectedLeafLabel())
					.addComponent(getSelectedLeafValueLabel())
				)
				.addGroup(layout.createSequentialGroup()
					.addGap(0, 0, Short.MAX_VALUE)
					.addComponent(getAddButton())
					.addComponent(getCancelButton())
				)
		);
		layout.linkSize(SwingConstants.HORIZONTAL, getAddButton(), getCancelButton());

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(getLeafFilterLabel())
					.addComponent(getLeafFilterTextField())
				)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(getFilterInfoLabel())
					.addComponent(getFilterInfoResultLabel())
				)
				.addComponent(treeScrollPane)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(getSelectedLeafLabel())
					.addComponent(getSelectedLeafValueLabel()))
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(getAddButton())
					.addComponent(getCancelButton())
			)
		);

	}


	@Override
	protected JComponent getDefaultFocus() {
		return null;
	}

	@Override
	protected JButton getDefaultButton() {
		return null;
	}

	@Override
	protected void windowShown() {	}

	@Override
	protected void save() {	}	
	
}
