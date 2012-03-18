/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.gui.shared.JGroupLayoutPanel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.GuiFrame;


public class StatusPanel extends JGroupLayoutPanel {
	
	//GUI
	
	private JLabel jEveTime;
	private JLabel jUpdatable;
	private JToolBar jToolBar;


	private List<JLabel> programStatus = new ArrayList<JLabel>();

	public StatusPanel(Program program) {
		super(program);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		jToolBar = new JToolBar();
		jToolBar.setFloatable(false);
		jToolBar.setRollover(false);
		
		

		jUpdatable = createIcon(Images.DIALOG_UPDATE.getIcon(), GuiFrame.get().updatable());
		programStatus.add(jUpdatable);
		
		jEveTime = createLabel(GuiFrame.get().eve(),  Images.MISC_EVE.getIcon());
		programStatus.add(jEveTime);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar, 25, 25, 25)
		);
	}

	public void tabChanged(){
		doLayout();
	}
	
	private void doLayout(){
		jToolBar.removeAll();
		addSpace(5);
		for (JLabel jLabel : programStatus){
			jToolBar.add(jLabel);
			addSpace(10);
		}
		for (JLabel jLabel : program.getMainWindow().getSelectedTab().getStatusbarLabels()){
			jToolBar.add(jLabel);
			addSpace(10);
		}
		addSpace(10);
		this.getPanel().updateUI();

	}

	public void timerTicked(boolean updatable){
		jEveTime.setText(GuiFrame.get().eveTime(Settings.getGmtNow()));
		if (updatable){
			jUpdatable.setIcon(Images.DIALOG_UPDATE.getIcon());
			jUpdatable.setToolTipText(GuiFrame.get().updatable());
		} else {
			jUpdatable.setIcon(Images.DIALOG_UPDATE_DISABLED.getIcon());
			jUpdatable.setToolTipText(GuiFrame.get().not());
		}
	}

	public static JLabel createIcon(Icon icon, String toolTip){
		JLabel jLabel = new JLabel();
		jLabel.setIcon(icon);
		jLabel.setForeground(jLabel.getBackground().darker().darker().darker());
		jLabel.setMinimumSize( new Dimension(25, 25) );
		jLabel.setPreferredSize( new Dimension(25, 25));
		jLabel.setMaximumSize( new Dimension(25, 25));
		jLabel.setHorizontalAlignment(JLabel.CENTER);
		jLabel.setToolTipText(toolTip);
		return jLabel;
	}
	public static JLabel createLabel(String toolTip, Icon icon){
		JLabel jLabel = new JLabel();
		jLabel.setIcon(icon);
		jLabel.setForeground(jLabel.getBackground().darker().darker().darker());
		jLabel.setToolTipText(toolTip);
		jLabel.setHorizontalAlignment(JLabel.LEFT);
		return jLabel;
	}
	private void addSpace(int width){
		JLabel jSpace = new JLabel();
		jSpace.setMinimumSize( new Dimension(width, 25) );
		jSpace.setPreferredSize( new Dimension(width, 25));
		jSpace.setMaximumSize( new Dimension(width, 25));
		jToolBar.add(jSpace);
	}
}
