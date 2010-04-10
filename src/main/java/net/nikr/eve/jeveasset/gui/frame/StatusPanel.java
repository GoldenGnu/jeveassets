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

package net.nikr.eve.jeveasset.gui.frame;

import java.awt.Dimension;
import net.nikr.eve.jeveasset.gui.shared.JProgramPanel;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;
import net.nikr.eve.jeveasset.gui.shared.Formater;


public class StatusPanel extends JProgramPanel {
	
	//GUI
	private JLabel jTotalValue;
	private JLabel jCount;
	private JLabel jAverage;
	private JLabel jVolume;
	private JLabel jEveTime;
	private JLabel jUpdatable;
	private JToolBar jToolBar;


	public StatusPanel(Program program) {
		super(program);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		jToolBar = new JToolBar();
		jToolBar.setFloatable(false);
		jToolBar.setRollover(false);

		jToolBar.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, this.getPanel().getBackground().darker()),
				BorderFactory.createMatteBorder(1, 0, 0, 0, this.getPanel().getBackground().brighter())
				));
		addSpace(5);

		jUpdatable = addIcon(ImageGetter.getIcon("update.png"), "Updatable");

		jEveTime = createLabel("Eve Server Time", ImageGetter.getIcon("eve.png"));

		jVolume = createLabel("Total volume of shown assets", ImageGetter.getIcon("volume.png"));

		jCount = createLabel("Total number of shown assets", ImageGetter.getIcon("add.png")); //Add

		jAverage = createLabel("Average value of shown assets", ImageGetter.getIcon("shape_align_middle.png"));

		jTotalValue = createLabel("Total value of shown assets", ImageGetter.getIcon("icon07_02.png"));

		addSpace(10);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar, 25, 25, 25)
		);
		setAverage(0);
		setTotalValue(0);
		setCount(0);
		timerTicked(true);
	}

	public void timerTicked(boolean updatable){
		jEveTime.setText( Formater.timeOnly(Settings.getGmtNow())+" GMT" );
		jUpdatable.setEnabled(updatable);
		if (updatable){
			jUpdatable.setToolTipText("Updatable");
		} else {
			jUpdatable.setToolTipText("Not Updatable");
		}
	}
	

	public void setAverage(double n){
		jAverage.setText(Formater.isk(n));
	}
	public void setTotalValue(double n){
		jTotalValue.setText(Formater.isk(n));
	}
	public void setCount(long n){
		jCount.setText(Formater.count(n));
	}
	public void setVolume(float n){
		jVolume.setText(Formater.number(n));
	}
	private JLabel addIcon(Icon icon, String toolTip){
		JLabel jLabel = new JLabel();
		jLabel.setIcon(icon);
		jLabel.setForeground(jToolBar.getBackground().darker().darker().darker());
		jLabel.setMinimumSize( new Dimension(25, 25) );
		jLabel.setPreferredSize( new Dimension(25, 25));
		jLabel.setMaximumSize( new Dimension(25, 25));
		jLabel.setHorizontalAlignment(JLabel.CENTER);
		jLabel.setToolTipText(toolTip);
		jToolBar.add(jLabel);
		return jLabel;
	}
	private JLabel createLabel(String toolTip, Icon icon){
		addIcon(icon, toolTip);
		JLabel jLabel = new JLabel();
		jLabel.setForeground(jToolBar.getBackground().darker().darker().darker());
		jLabel.setToolTipText(toolTip);
		jLabel.setHorizontalAlignment(JLabel.LEFT);
		jToolBar.add(jLabel);
		addSpace(10);
		return jLabel;
	}
	private void addSpace(int width){
		JLabel jSpace = new JLabel();
		jSpace.setMinimumSize( new Dimension(width, 25) );
		jSpace.setPreferredSize( new Dimension(width, 25));
		jSpace.setMaximumSize( new Dimension(width, 25));
		jToolBar.add(jSpace);
	}

	@Override
	protected JProgramPanel getThis(){
		return this;
	}
}
