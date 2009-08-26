/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.gui.shared.JProgramPanel;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;
import net.nikr.eve.jeveasset.gui.shared.Formater;


public class StatusPanel extends JProgramPanel {

	//GUI
	private JLabel jTask;
	private JLabel jTotalValue;
	private JLabel jCount;
	private JLabel jAverage;
	private JLabel jVolume;
	private JLabel jAssetUpdate;
	private JLabel jEveCentralUpdate;
	private JLabel jMarketOrdersUpdate;
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

		jTask = new JLabel();
		jTask.setForeground(jTask.getBackground().darker().darker().darker());
		jTask.setMinimumSize( new Dimension(0, 25) );
		jTask.setPreferredSize( new Dimension(Short.MAX_VALUE, 25));
		jTask.setMaximumSize( new Dimension(Short.MAX_VALUE, 25));
		jToolBar.add(jTask);

		jEveCentralUpdate = createLabel(120, "Price data next update", ImageGetter.getIcon("price_data_update.png"));

		jAssetUpdate = createLabel(120, "Assets next update", ImageGetter.getIcon("assets_update.png"));

		jMarketOrdersUpdate = new JLabel();// = createLabel(120, "Market orders next update", ImageGetter.getIcon("marketorders_update.png"));

		jVolume = createLabel(100, "Total volume of shown assets", ImageGetter.getIcon("volume.png"));

		jCount = createLabel(100, "Total number of shown assets", ImageGetter.getIcon("add.png")); //Add

		jAverage = createLabel(100, "Average value of shown assets", ImageGetter.getIcon("shape_align_middle.png"));

		jTotalValue = createLabel(120, "Total value of shown assets", ImageGetter.getIcon("icon07_02.png"));

		addSpace(10);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar, 25, 25, 25)
		);
		update();
	}


	public void updateEveCentralDate(){
		Date d = program.getSettings().getMarketstatsNextUpdate();
		if (Settings.isUpdatable(d)){
			jEveCentralUpdate.setText("Now");
		} else {
			jEveCentralUpdate.setText(Formater.weekdayAndTime(d)+" GMT");
		}
	}
	public void updateAssetDate(){
		List<Account> accounts = program.getSettings().getAccounts();
		Date nextUpdate = null;
		for (int a = 0; a < accounts.size(); a++){
			Account account = accounts.get(a);
			List<Human> humans = account.getHumans();
			for (int b = 0; b < humans.size(); b++){
				Human human = humans.get(b);
				if (human.isShowAssets()){
					if (nextUpdate == null){
						nextUpdate = human.getAssetNextUpdate();
					}
					if (human.getAssetNextUpdate().before(nextUpdate)){
						nextUpdate = human.getAssetNextUpdate();
					}
				}
			}
		}
		if (nextUpdate == null) nextUpdate = Settings.getGmtNow();
		if (Settings.isUpdatable(nextUpdate)){
			jAssetUpdate.setText("Now");
		} else {
			jAssetUpdate.setText(Formater.weekdayAndTime(nextUpdate)+" GMT");
		}
	}
	public void updateMarketOrdersDate(){
		List<Account> accounts = program.getSettings().getAccounts();
		Date nextUpdate = null;
		for (int a = 0; a < accounts.size(); a++){
			Account account = accounts.get(a);
			List<Human> humans = account.getHumans();
			for (int b = 0; b < humans.size(); b++){
				Human human = humans.get(b);
				if (human.isShowAssets()){
					if (nextUpdate == null){
						nextUpdate = human.getMarketOrdersNextUpdate();
					}
					if (human.getMarketOrdersNextUpdate().before(nextUpdate)){
						nextUpdate = human.getMarketOrdersNextUpdate();
					}
				}
			}
		}
		if (nextUpdate == null) nextUpdate = Settings.getGmtNow();
		if (Settings.isUpdatable(nextUpdate)){
			jMarketOrdersUpdate.setText("Now");
		} else {
			jMarketOrdersUpdate.setText(Formater.weekdayAndTime(nextUpdate)+" GMT");
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
	private void update(){
		updateEveCentralDate();
		updateAssetDate();
		updateMarketOrdersDate();
		setAverage(0);
		setTotalValue(0);
		setCount(0);
		

	}
	private void addIcon(Icon icon){
		JLabel jLabel = new JLabel();
		jLabel.setIcon(icon);
		jLabel.setForeground(jTask.getBackground().darker().darker().darker());
		jLabel.setMinimumSize( new Dimension(25, 25) );
		jLabel.setPreferredSize( new Dimension(25, 25));
		jLabel.setMaximumSize( new Dimension(25, 25));
		jLabel.setHorizontalAlignment(JLabel.CENTER);
		jToolBar.add(jLabel);
	}
	private JLabel createLabel(int width, String toolTip, Icon icon){
		addIcon(icon);
		JLabel jLabel = new JLabel();
		jLabel.setForeground(jTask.getBackground().darker().darker().darker());
		//jLabel.setMinimumSize( new Dimension(width, 25) );
		//jLabel.setPreferredSize( new Dimension(width, 25));
		jLabel.setMaximumSize( new Dimension(width, 25));
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
