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
import javax.swing.SwingWorker;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.JUpdateWindow;
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
		if (updateEveCentralTask.throwable != null){
			Log.error("Uncaught Exception (SwingWorker): Please email the latest error.txt in the logs directory to niklaskr@gmail.com", updateEveCentralTask.throwable);
		}
		if (value == 100 && updateEveCentralTask.done){
			updateEveCentralTask.done = false;
			if (updateEveCentralTask.updated){
				program.assetsChanged();
			}
			program.getStatusPanel().setShowingEveCentralUpdate();

			jProgressBar.setValue(0);
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
				JOptionPane.showMessageDialog(parent, "No price data updated (not allowed yet)\r\n", "Update Price Data", JOptionPane.PLAIN_MESSAGE);
			}
		} else {
			jProgressBar.setValue(0);
			jProgressBar.setIndeterminate(true);
		}

	}

	class UpdateEveCentralTask extends SwingWorker<Void, Void> {

		private boolean updated = false;
		private boolean isOnline = true;
		private boolean done = false;
		private Throwable throwable = null;

		public UpdateEveCentralTask() {}

		@Override
		public Void doInBackground() {
			setProgress(0);
			try {
				program.getSettings().clearEveAssetList();
				updated = EveCentralMarketstatReader.load(program.getSettings());
				if (!updated){
					isOnline = Online.isOnline(program.getSettings());
				}
			} catch (Throwable ex) {
				throwable = ex;
			}
			return null;
        }

		@Override
		public void done() {
			done = true;
			setProgress(100);
		}

	}
}
