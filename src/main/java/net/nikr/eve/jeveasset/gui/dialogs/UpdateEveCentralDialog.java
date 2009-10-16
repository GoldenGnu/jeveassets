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

package net.nikr.eve.jeveasset.gui.dialogs;

import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.JUpdateWindow;
import net.nikr.eve.jeveasset.gui.shared.UpdateTask;
import net.nikr.eve.jeveasset.io.EveCentralMarketstatReader;
import net.nikr.eve.jeveasset.io.Online;
import net.nikr.log.Log;


public class UpdateEveCentralDialog extends JUpdateWindow implements PropertyChangeListener {

	private UpdateEveCentralTask updateEveCentralTask;

	public UpdateEveCentralDialog(Program program, Window parent) {
		super(program, parent, "Updating price data from EVE-Central...");
	}

	@Override
	public void startUpdate() {
		setVisible(true);
		updateEveCentralTask = new UpdateEveCentralTask();
		updateEveCentralTask.addPropertyChangeListener(this);
		updateEveCentralTask.execute();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		int value = updateEveCentralTask.getProgress();
		if (updateEveCentralTask.getThrowable() != null){
			Log.error("Uncaught Exception (SwingWorker): Please email the latest error.txt in the logs directory to niklaskr@gmail.com", updateEveCentralTask.getThrowable());
		}
		if (value == 100 && updateEveCentralTask.isTaskDone()){
			updateEveCentralTask.setTaskDone(false);
			if (updateEveCentralTask.updated){
				program.updateEventList();
			}
			program.getStatusPanel().updateEveCentralDate();

			jProgressBar.setValue(100);
			jProgressBar.setIndeterminate(false);
			setVisible(false);


			if (updateEveCentralTask.updated){ //All assets updated
				JOptionPane.showMessageDialog(parent, "Price data updated...", "Update Price Data", JOptionPane.PLAIN_MESSAGE);
			} else if (!updateEveCentralTask.isOnline){ //No assets updated
				JOptionPane.showMessageDialog(parent, "Could not update price data.\r\nPlease connect to the internet and try again...", "Update Price Data", JOptionPane.PLAIN_MESSAGE);
			} else if (program.getSettings().getAccounts().isEmpty()) {
				JOptionPane.showMessageDialog(parent, "No price data updated\r\nYou need to add your API Key:\r\nOptions > Manage API Keys > Add.", "Update Price Data", JOptionPane.PLAIN_MESSAGE);
			} else if (!program.getSettings().hasAssets()) {
				JOptionPane.showMessageDialog(parent, "No price data updated\r\nYou need to update your assets:\r\nOptions > Update Asset", "Update Price Data", JOptionPane.PLAIN_MESSAGE);
			} else { //No assets updated
				JOptionPane.showMessageDialog(parent, "No price data updated (not allowed yet)\r\nYou can only update price data once every hour", "Update Price Data", JOptionPane.PLAIN_MESSAGE);
			}
		} else if (value > 0){
			jProgressBar.setIndeterminate(false);
			jProgressBar.setValue(value);
		} else {
			jProgressBar.setIndeterminate(true);
		}

	}

	public class UpdateEveCentralTask extends UpdateTask {

		private boolean updated = false;
		private boolean isOnline = true;

		public UpdateEveCentralTask() {}
		
		@Override
		public void update() throws Throwable {
			program.getSettings().clearEveAssetList();
			updated = EveCentralMarketstatReader.load(program.getSettings(), this);
			if (!updated){
				isOnline = Online.isOnline(program.getSettings());
			}
		}

	}
}
