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
package net.nikr.eve.jeveasset.gui.dialogs;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.JSettingsPanel;


public class SettingsDialog extends JDialogCentered implements ActionListener, ListSelectionListener {

	public final static String ACTION_OK = "ACTION_OK";
	public final static String ACTION_CANCEL = "ACTION_CANCEL";
	public final static String ACTION_APPLY = "ACTION_APPLY";

	private JList jTabs;
	private JPanel jContent;
	private JButton jOK;
	private List<JSettingsPanel> settingsPanels;
	private Map<Object, Icon> icons;
	private DefaultListModel listModel;
	private CardLayout cardLayout;

	private boolean tabSelected = false;

	public SettingsDialog(Program program, Image image) {
		super(program, Program.PROGRAM_NAME+" Settings", image);
		settingsPanels = new ArrayList<JSettingsPanel>();

		listModel = new DefaultListModel();

		icons = new HashMap<Object, Icon>();
		jTabs = new JList(listModel);
		jTabs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTabs.setCellRenderer(new IconListRenderer(icons));
		jTabs.addListSelectionListener(this);
		JScrollPane jTabsScroller = new JScrollPane(jTabs);

		cardLayout = new CardLayout();

		jContent = new JPanel(cardLayout);

		JSeparator jSeparator = new JSeparator();

		jOK = new JButton("OK");
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);

		JButton jApply = new JButton("Apply");
		jApply.setActionCommand(ACTION_APPLY);
		jApply.addActionListener(this);

		JButton jCancel = new JButton("Cancel");
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jTabsScroller)
					.addComponent(jContent)
				)
				.addComponent(jSeparator)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jApply, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jTabsScroller)
					.addComponent(jContent)
				)
				.addComponent(jSeparator, 5, 5, 5)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jApply, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	public void add(JSettingsPanel jSettingsPanel, Icon icon){
		settingsPanels.add(jSettingsPanel);
		icons.put(jSettingsPanel.getTitle(), icon);
		jContent.add(jSettingsPanel.getPanel(), jSettingsPanel.getTitle());
		listModel.addElement(jSettingsPanel.getTitle());
	}


	@Override
	protected JComponent getDefaultFocus() {
		return jOK;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {
		boolean update = false;
		for (int a = 0; a < settingsPanels.size(); a++){
			if (settingsPanels.get(a).save()){
				update = true;
			}
		}
		if (update){
			program.updateEventList();
		}
	}

	public void setVisible(int number) {
		jTabs.setSelectedIndex(number);
		tabSelected = true;
		setVisible(true);
	}

	public void setVisible(JSettingsPanel c) {
		jTabs.setSelectedIndex(settingsPanels.indexOf(c));
		tabSelected = true;
		setVisible(true);
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			for (int a = 0; a < settingsPanels.size(); a++){
				settingsPanels.get(a).load();
			}
			if (!tabSelected){
				jTabs.setSelectedIndex(0);
			}
		}
		tabSelected = false;
		super.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_OK.equals(e.getActionCommand())){
			save();
			setVisible(false);
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			setVisible(false);
		}
		if (ACTION_APPLY.equals(e.getActionCommand())){
			save();
		}

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		cardLayout.show(jContent, settingsPanels.get(jTabs.getSelectedIndex()).getTitle());
	}

	public class IconListRenderer extends DefaultListCellRenderer {

		private Map<Object, Icon> icons = null;

		public IconListRenderer(Map<Object, Icon> icons) {
			this.icons = icons;
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			label.setIcon( icons.get(value) );
			return label;
		}
	}
}
