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

package net.nikr.eve.jeveasset.gui.tabs.routing;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.SolarSystem;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.TabsRouting;
import uk.me.candle.eve.graph.Node;


public class JSystemDialog extends JDialogCentered implements ActionListener {

	private static final String ACTION_OK = "ACTION_OK";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";

	private final EventList<SolarSystem> systems;
	private JComboBox jSystem;
	private JButton jOK;
	private RoutingTab routingTab;

	private SolarSystem system;

	public JSystemDialog(Program program, RoutingTab routingTab) {
		super(program, TabsRouting.get().addSystemTitle(), Images.TOOL_ROUTING.getImage());
		this.routingTab = routingTab;

		JLabel jText = new JLabel(TabsRouting.get().addSystemSelect());

		jSystem = new JComboBox();
		JCopyPopup.install((JTextComponent) jSystem.getEditor().getEditorComponent());
		systems = new BasicEventList<SolarSystem>();
		AutoCompleteSupport.install(jSystem, systems, new Filterator());
		jOK = new JButton(TabsRouting.get().addSystemOK());
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);

		JButton jCancel = new JButton(TabsRouting.get().addSystemCancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jText)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jSystem, 220, 220, 220)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
						.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jText, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jSystem, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	protected void buildData() {
		if (!systems.isEmpty()) {
			return;
		}
		systems.getReadWriteLock().writeLock().lock();
		try {
			for (Node node : routingTab.getGraph().getNodes()) {
				if (node instanceof SolarSystem) {
					systems.add((SolarSystem) node);
				}
			}
		} finally {
			systems.getReadWriteLock().writeLock().unlock();
		}
	}

	public SolarSystem show() {
		buildData();
		jSystem.getModel().setSelectedItem("");
		system = null;
		setVisible(true);
		return system;
	}

	
	@Override
	protected JComponent getDefaultFocus() {
		return jSystem;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		Object object = jSystem.getSelectedItem();
		if (object instanceof SolarSystem) {
			system = (SolarSystem) object;
		} else {
			system = null;
		}
		setVisible(false);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (ACTION_OK.equals(e.getActionCommand())) {
			save();
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())) {
			setVisible(false);
		}
	}

	private static class Filterator implements TextFilterator<SolarSystem> {
		@Override
		public void getFilterStrings(final List<String> baseList, final SolarSystem element) {
			baseList.add(element.getName());
		}
	}
	
}
