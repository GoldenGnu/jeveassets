/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.profile.Profile;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.TrackerData;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateDialog.PriceDataTask;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateDialog.Step1Task;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateDialog.Step2Task;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateDialog.Step3Task;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateDialog.Step4Task;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.tabs.values.AssetValue;
import net.nikr.eve.jeveasset.gui.tabs.values.DataSetCreator;
import net.nikr.eve.jeveasset.io.local.ProfileReader;
import net.nikr.eve.jeveasset.io.local.ProfileWriter;
import net.nikr.eve.jeveasset.io.online.PriceDataGetter;


public class CliUpdate {

	void update() {
		PriceDataGetter priceDataGetter = new PriceDataGetter();
		priceDataGetter.load();

		ProfileManager profileManager = new ProfileManager();
		profileManager.searchProfile();

		SplashUpdater.setText("Updating DATA");
		SplashUpdater.setProgress(0);
		int count = 0;
		Date date = Settings.getNow();
		for (Profile profile : profileManager.getProfiles()) {
			profileManager.clear();
			ProfileReader.load(profileManager, profile.getFilename());
			ProfileData profileData = new ProfileData(profileManager);
			profileData.updateEventLists();
			List<UpdateTask> updateTasks = new ArrayList<>();
			updateTasks.add(new Step1Task(profileManager));
			updateTasks.add(new Step2Task(profileManager, true, true, true, true, true, true, true, true, true, true));
			updateTasks.add(new Step3Task(profileManager, true));
			updateTasks.add(new Step4Task(profileManager));
			updateTasks.add(new PriceDataTask(priceDataGetter, profileData, false));
			for (UpdateTask updateTask : updateTasks) {
				updateTask.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						SplashUpdater.setSubProgress(updateTask.getProgress());
					}
				});
				updateTask.update();
			}
			AssetValue.updateData();
			profileData.updateEventLists();
			DataSetCreator.createTrackerDataPoint(profileData, date);
			TrackerData.save("Added", true);
			ProfileWriter.save(profileManager, profile.getFilename());
			Settings.saveSettings();
			count++;
			SplashUpdater.setProgress( (int)(count * 100.0 / profileManager.getProfiles().size()));
		}
	}
	
}
