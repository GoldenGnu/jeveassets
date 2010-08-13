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

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddSystemDialog extends JDialogCentered {
	
	private final static Logger LOG = LoggerFactory.getLogger(AddSystemDialog.class);

	public final static String ACTION_SAVE = "ACTION_SAVE";
	public final static String ACTION_CANCEL = "ACTION_CANCEL";
	public final static String ACTION_SELECTED = "ACTION_SELECTED";

	private EventList<String> systems;
	private JLabel jPrompt;
	private JComboBox jSystem;
	private JButton jAdd;
	private JButton jCancel;
	
	public AddSystemDialog(Program program) {
		super(program, "Add System");

		this.getDialog().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		jPrompt = new JLabel("Enter System Name:");
		jSystem = new JComboBox();
		jAdd = new JButton("Add");
		jCancel = new JButton("Cancel");

		jAdd.setEnabled(false);
		jCancel.setEnabled(false);

		Set<String> systemsTemp = new TreeSet<String>();
		for (Location l : program.getSettings().getLocations().values()) {
			// Check first char of location ID to extract solar systems only.
			if (Integer.toString(l.getId()).charAt(0) == '3' ) {
				String regionName = program.getSettings().getLocations().get(l.getRegion()).getName();
				if (!"Unknown".equals(regionName))
					systemsTemp.add(l.getName() + " (" + regionName + ")");
			}
		systems = new BasicEventList<String>();
		systems.addAll(systemsTemp);
		}
		AutoCompleteSupport.install(jSystem, systems);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jPrompt)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jSystem, 200, 200, 200)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jAdd, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
				)
		);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jPrompt, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jSystem, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAdd, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);

	}

	@Override
	protected JComponent getDefaultFocus() {
		return jSystem;
	}

	@Override
	protected JButton getDefaultButton() {
		return jAdd;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {}

}
