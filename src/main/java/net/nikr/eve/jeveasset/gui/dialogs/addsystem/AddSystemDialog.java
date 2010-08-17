/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.dialogs.addsystem;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddSystemDialog  extends JDialogCentered  {

	private static final String DEFAULT_SELECTED_SYSTEM = "None";
	private static final String DEFAULT_FILTER_RESULT = "No filter applied";
	private static final Logger LOG = LoggerFactory.getLogger(AddSystemDialog.class);

	private JDialog dialog;
	private JLabel filterLabel;
	private JTextField filterTextField;
	private JLabel filterInfoLabel;
	private JLabel filterInfoResultLabel;
	private JLabel selectedSystemLabel;
	private JLabel selectedSystemValueLabel;
	private JButton cancelButton;
	private JButton addButton;
	private SystemSelectTreePanel systemTree;
	
	public AddSystemDialog(Program program) {
		super(program, "Add system");
		dialog = getDialog();
		dialog.setResizable(true);
		dialog.setMinimumSize(new Dimension(300,400));

		filterLabel = new JLabel("Systems filter text:");

		filterTextField = new JTextField("");
		filterTextField.addKeyListener(new FilterTextFieldListener());

		filterInfoLabel = new JLabel("Filter Result:");

		filterInfoResultLabel = new JLabel(DEFAULT_FILTER_RESULT);

		selectedSystemLabel = new JLabel("Selected system:");

		selectedSystemValueLabel = new JLabel(DEFAULT_SELECTED_SYSTEM);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelButtonListener());

		addButton = new JButton("Add");
		addButton.setEnabled(false);

		systemTree = new SystemSelectTreePanel(program.getSettings().getGalaxyModel());
		systemTree.addPropertyChangeListener(SystemSelectTreePanel.TREE_CHANGE_PROPERTY_NAME, new SystemPropertyChangeListener());

		layoutComponents();
	}

	private void layoutComponents() {

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(filterLabel)
					.addComponent(filterTextField)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(filterInfoLabel)
					.addComponent(filterInfoResultLabel)
				)
				.addComponent(systemTree)
				.addGroup(layout.createSequentialGroup()
					.addComponent(selectedSystemLabel)
					.addComponent(selectedSystemValueLabel)
				)
				.addGroup(layout.createSequentialGroup()
					.addGap(0, 0, Short.MAX_VALUE)
					.addComponent(addButton)
					.addComponent(cancelButton)
				)
		);
		layout.linkSize(SwingConstants.HORIZONTAL, addButton, cancelButton);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(filterLabel)
					.addComponent(filterTextField)
				)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(filterInfoLabel)
					.addComponent(filterInfoResultLabel)
				)
				.addComponent(systemTree)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(selectedSystemLabel)
					.addComponent(selectedSystemValueLabel))
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(addButton)
					.addComponent(cancelButton)
			)
		);

	}

	@Override
	protected JComponent getDefaultFocus() {
		return filterTextField;
	}

	@Override
	protected JButton getDefaultButton() {
		return null;
	}

	@Override
	protected void windowShown() {
	}

	@Override
	protected void windowActivated() {
	}

	@Override
	protected void save() {
	}

	class AddBbuttonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

		}
	}

	class CancelButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			dialog.dispose();
		}
	}

	class FilterTextFieldListener extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {
			JTextField tf = (JTextField) e.getSource();
			systemTree.setFilterText(tf.getText());
			int systemCount = systemTree.getSystemCount();
			filterInfoResultLabel.setText(generateFilterResultString(systemCount));
		}
	}

	private String generateFilterResultString(int resultCount) {
		if ("".equals(filterTextField.getText())) {
			return DEFAULT_FILTER_RESULT;
		} else if (resultCount == 1) {
			return "<html><b>" + resultCount + " system matches</b>";
		} else {
			return "<html><b>" + resultCount + " systems match</b>";
		}
	}

	class SystemPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() != null) {
				selectedSystemValueLabel.setText("<html><b>" + evt.getNewValue().toString() + "</b>");
				addButton.setEnabled(true);
			} else {
				selectedSystemValueLabel.setText(DEFAULT_SELECTED_SYSTEM);
				addButton.setEnabled(false);
			}
		}
	}
}
