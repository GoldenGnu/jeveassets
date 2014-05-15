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

package net.nikr.eve.jeveasset.gui.frame;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.Timer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JGroupLayoutPanel;
import net.nikr.eve.jeveasset.i18n.GuiFrame;


public class StatusPanel extends JGroupLayoutPanel {

	//GUI
	private JLabel jEveTime;
	private JLabel jUpdatable;
	private JToolBar jToolBar;
	private Timer eveTimer;


	private List<JLabel> programStatus = new ArrayList<JLabel>();

	public StatusPanel(final Program program) {
		super(program);

		ListenerClass listener = new ListenerClass();

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		jToolBar = new JToolBar();
		jToolBar.setFloatable(false);
		jToolBar.setRollover(false);

		jUpdatable = createIcon(Images.DIALOG_UPDATE.getIcon(), GuiFrame.get().updatable());
		programStatus.add(jUpdatable);

		jEveTime = createLabel(GuiFrame.get().eve(),  Images.MISC_EVE.getIcon());
		programStatus.add(jEveTime);

		eveTimer = new Timer(1000, listener);
		eveTimer.start();

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar, 25, 25, 25)
		);
	}

	public void tabChanged() {
		doLayout();
	}

	private void doLayout() {
		jToolBar.removeAll();
		addSpace(5);
		for (JLabel jLabel : programStatus) {
			jToolBar.add(jLabel);
			addSpace(10);
		}
		for (JLabel jLabel : program.getMainWindow().getSelectedTab().getStatusbarLabels()) {
			jToolBar.add(jLabel);
			addSpace(10);
		}
		addSpace(10);
		this.getPanel().updateUI();

	}

	public void timerTicked(final boolean updatable) {
		if (updatable) {
			jUpdatable.setIcon(Images.DIALOG_UPDATE.getIcon());
			jUpdatable.setToolTipText(GuiFrame.get().updatable());
		} else {
			jUpdatable.setIcon(Images.DIALOG_UPDATE_DISABLED.getIcon());
			jUpdatable.setToolTipText(GuiFrame.get().not());
		}
	}

	public static JLabel createIcon(final Icon icon, final String toolTip) {
		JLabel jLabel = new JLabel();
		jLabel.setIcon(icon);
		jLabel.setForeground(jLabel.getBackground().darker().darker().darker());
		jLabel.setMinimumSize(new Dimension(25, 25));
		jLabel.setPreferredSize(new Dimension(25, 25));
		jLabel.setMaximumSize(new Dimension(25, 25));
		jLabel.setHorizontalAlignment(JLabel.CENTER);
		jLabel.setToolTipText(toolTip);
		return jLabel;
	}
	public static JLabel createLabel(final String toolTip, final Icon icon) {
		JLabel jLabel = new JLabel();
		jLabel.setIcon(icon);
		jLabel.setForeground(jLabel.getBackground().darker().darker().darker());
		jLabel.setToolTipText(toolTip);
		jLabel.setHorizontalAlignment(JLabel.LEFT);
		return jLabel;
	}
	private void addSpace(final int width) {
		JLabel jSpace = new JLabel();
		jSpace.setMinimumSize(new Dimension(width, 25));
		jSpace.setPreferredSize(new Dimension(width, 25));
		jSpace.setMaximumSize(new Dimension(width, 25));
		jToolBar.add(jSpace);
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			jEveTime.setText(Formater.eveTime(Settings.getNow()));
		}
	}
}
