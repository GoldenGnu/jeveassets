/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.tracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EveKitOwner;
import net.nikr.eve.jeveasset.data.profile.ProfileData;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.tabs.values.DataSetCreator;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.i18n.TabsTracker;
import net.nikr.eve.jeveasset.io.evekit.AbstractEveKitGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitAccountBalanceGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitAssetGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitBlueprintsGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitContractItemsGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitContractsGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitIndustryJobsGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitMarketOrdersGetter;
import net.nikr.eve.jeveasset.io.evekit.EveKitShipGetter;
import net.nikr.eve.jeveasset.io.online.CitadelGetter;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker;


public class EveKitTrackImportUpdateTask extends UpdateTask {

	private final Program program;
	private final List<EveKitOwner> owners;
	private final DateInterval dateInterval;
	private final Merge merge;
	private ReturnValue returnValue;

	public EveKitTrackImportUpdateTask(Program program, List<EveKitOwner> owners, DateInterval dateInterval, Merge merge) {
		super(TabsTracker.get().eveKitImportTaskTitle(dateInterval.getTitle(), merge.toString()));
		setIcon(Images.MISC_EVEKIT.getIcon());
		this.program = program;
		this.owners = owners;
		this.dateInterval = dateInterval;
		this.merge = merge;
	}

	@Override
	public void update() {
		setProgress(0);
		boolean imported = false;
		boolean completed  = false;
		boolean error  = false;
		returnValue = ReturnValue.CANCELLED;
		Date date;
		Date lifeStart = getLifeStart();
		if (lifeStart == null) {
			return;
		}
		Calendar calendar = Calendar.getInstance(); 
		switch (dateInterval) {
			case DAY:
				//Today at 12 (24hour clock)
				calendar.set(Calendar.HOUR_OF_DAY, 12);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				break;
			case WEEK:
				//Last Monday
				calendar.set(Calendar.HOUR_OF_DAY, 12);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				break;
			case MONTH:
				//1st of this month
				calendar.set(Calendar.HOUR_OF_DAY, 12);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				break;
		}
		date = calendar.getTime();
		long days = daysBetween(date, lifeStart) + dateInterval.getUpdatesDays();
		long day = 0;

		CitadelGetter.update(null); //Need location names before we update

		while (true) {
			if (day != 0) { //Ignore first update
				//Get next date 
				date = updateDate(date);
			}
			//Update total progress
			day = day + dateInterval.getUpdatesDays();
			setProgress(0);
			List<EveKitOwner> clones = new ArrayList<EveKitOwner>();
			for (EveKitOwner owner : owners) { //Find owners without data
				if (!haveData(date, owner)) {
					EveKitOwner eveKitOwner = new EveKitOwner(owner.getAccessKey(), owner.getAccessCred(), owner.getExpire(), owner.getAccessMask(), owner.isCorporation(), owner.getLimit(), owner.getAccountName());
					eveKitOwner.setOwnerName(owner.getOwnerName());
					eveKitOwner.setOwnerID(owner.getOwnerID());
					eveKitOwner.setShowOwner(true);
					clones.add(eveKitOwner);
				}
			}
			if (clones.isEmpty()) { //All owners have data, try next date
				//Update Progress
				setTotalProgress(days, day, 0, 100);
				continue;
			}
			if (isCancelled()) { //No data return by the API OR Task is cancelled
				break;
			}

			//Update from EveKit at date
			long at = date.getTime();

			//Create Tasks
			List<AbstractEveKitGetter> updatersStep1 = new ArrayList<AbstractEveKitGetter>();
			List<AbstractEveKitGetter> updatersStep2 = new ArrayList<AbstractEveKitGetter>();
			for (EveKitOwner owner : clones) { //Step 2
				updatersStep1.add(new EveKitAssetGetter(this, owner, at));
				updatersStep1.add(new EveKitAccountBalanceGetter(this, owner, at));
				updatersStep1.add(new EveKitBlueprintsGetter(this, owner, at));
				updatersStep1.add(new EveKitContractsGetter(this, owner, at));
				updatersStep1.add(new EveKitIndustryJobsGetter(this, owner, at));
				updatersStep1.add(new EveKitMarketOrdersGetter(this, owner, at));
				updatersStep2.add(new EveKitContractItemsGetter(this, owner));
				updatersStep2.add(new EveKitShipGetter(this, owner, at));
			}

			if (isCancelled()) { //No data return by the API OR Task is cancelled
				break;
			}

			//Running Step 1 tasks
			ThreadWoker.start(this, updatersStep1, 0, 70);

			if (isCancelled()) { //No data return by the API OR Task is cancelled
				break;
			}

			//Checking Step 1 for errors
			for (AbstractEveKitGetter getter : updatersStep1) {
				if (getter.hasError()) {
					error = true;
					break;
				}
			}
			if (error) {
				break;
			}

			if (isCancelled()) { //No data return by the API OR Task is cancelled
				break;
			}

			//Running Step 2 tasks
			ThreadWoker.start(this, updatersStep2, 70, 100);

			if (isCancelled()) { //No data return by the API OR Task is cancelled
				break;
			}

			//Checking Step 2 for errors
			for (AbstractEveKitGetter getter : updatersStep2) {
				if (getter.hasError()) {
					error = true;
					break;
				}
			}
			if (error) {
				break;
			}

			if (isCancelled()) { //No data return by the API OR Task is cancelled
				break;
			}

			//Check if we're still getting data from the API
			boolean end = true;
			List<EveKitOwner> updatedClones = new ArrayList<EveKitOwner>();
			for (EveKitOwner owner : clones) {
				if (!isEmpty(owner)) {
					end = false;
					updatedClones.add(owner);
				}
			}
			if (merge == Merge.OVERWRITE) {
				for (EveKitOwner owner : owners) { //Find owners without data
					haveData(date, owner);
				}
			}
			if (!end) { //If accounts was not empty
				imported = true;
			} else { //If accounts was empty
				completed = true;
			}
			//End logic
			if (end || isCancelled()) { //No data return by the API OR Task is cancelled
				break;
			}
			//Only create data point if we have date to save!

			//Add data to Manager
			ProfileManager manager = new ProfileManager();
			manager.getEveKitOwners().addAll(updatedClones);
			//Create Profile
			ProfileData profile = new ProfileData(manager);
			//Update missing prices
			if (!program.getPriceDataGetter().updateNew(profile, null)) {
				error = true;
				break;
			}
			//Update data
			profile.updateEventLists();
			//Create Tracker point
			DataSetCreator.createTrackerDataPoint(profile, date);
			//Update Progress
			setTotalProgress(days, day, 0, 100);
		}
		if (error) {
			returnValue = ReturnValue.ERROR;
		} else if (completed) {
			setTotalProgress(days, days, 0, 100);
			if (imported) {
				returnValue = ReturnValue.COMPLETED;
			} else {
				returnValue = ReturnValue.NOTHING_NEW;
			}
		}
		setProgress(100);
	}

	private boolean isEmpty(EveKitOwner owner) {
		return owner.getAccountBalances().isEmpty()
				&& owner.getAssets().isEmpty()
				&& owner.getBlueprints().isEmpty()
				&& owner.getContracts().isEmpty()
				&& owner.getIndustryJobs().isEmpty()
				&& owner.getMarketOrders().isEmpty();
				
	}

	private Date getLifeStart() {
		setProgress(0);
		List<EveKitOwner> clones = new ArrayList<EveKitOwner>();
		for (EveKitOwner owner : owners) { //Find owners without data
			EveKitOwner eveKitOwner = new EveKitOwner(owner.getAccessKey(), owner.getAccessCred(), owner.getExpire(), owner.getAccessMask(), owner.isCorporation(), owner.getLimit(), owner.getAccountName());
			eveKitOwner.setOwnerName(owner.getOwnerName());
			eveKitOwner.setOwnerID(owner.getOwnerID());
			eveKitOwner.setShowOwner(true);
			clones.add(eveKitOwner);
		}
		if (clones.isEmpty()) { //No clones found
			returnValue = ReturnValue.ERROR;
			return null;
		}
		if (isCancelled()) { //No data return by the API OR Task is cancelled
			return null;
		}
		List<AbstractEveKitGetter> updaters = new ArrayList<AbstractEveKitGetter>();
		Date date = null;
		for (EveKitOwner owner : clones) {
			updaters.add(new EveKitAssetGetter(this, owner, true));
			updaters.add(new EveKitAccountBalanceGetter(this, owner, true));
			updaters.add(new EveKitBlueprintsGetter(this, owner, true));
			updaters.add(new EveKitContractsGetter(this, owner, true));
			updaters.add(new EveKitIndustryJobsGetter(this, owner, true));
			updaters.add(new EveKitMarketOrdersGetter(this, owner, true));
			
		}

		if (isCancelled()) { //No data return by the API OR Task is cancelled
			return null;
		}

		ThreadWoker.start(this, updaters);

		if (isCancelled()) { //No data return by the API OR Task is cancelled
			return null;
		}

		for (AbstractEveKitGetter getter : updaters) {
			if (getter.hasError()) {
				returnValue = ReturnValue.ERROR;
				return null;
			}
			date = frist(date, getter.getLifeStart());
		}
		
		if (isCancelled()) { //No data return by the API OR Task is cancelled
			return null;
		}

		return date;
	}

	private Date frist(Date date1, Date date2) {
		if (date1 == null) {
			return date2;
		}
		if (date2 == null) {
			return date1;
		}
		if (date1.before(date2)) {
			return date1;
		} else {
			return date2;
		}
	}

	private Date updateDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		switch (dateInterval) {
			case DAY:
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				break;
			case WEEK:
				calendar.add(Calendar.DAY_OF_MONTH, -7);
				break;
			case MONTH:
				calendar.add(Calendar.MONTH, -1);
				break;
		}
		
		return calendar.getTime();
	}

	public ReturnValue getReturnValue() {
		return returnValue;
	}

	private boolean haveData(Date date, EveKitOwner owner) {
		switch (merge) {
			case OVERWRITE:
				return haveDateOverwrite(date, owner);
			case KEEP:
				return haveDateKeep(date, owner); //OK
			case MERGE:
				return haveDateEveKit(date, owner); //OK
			default:
				return haveDateKeep(date, owner); //OK
			
		}
	}

	private boolean haveDateKeep(Date date, EveKitOwner owner) {
		List<Value> values = Settings.get().getTrackerData().get(owner.getOwnerName());
		if (values == null) {
			return false;
		}
		Calendar fromCalendar = Calendar.getInstance(); 
		fromCalendar.setTime(date);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 1);
		fromCalendar.set(Calendar.MILLISECOND, 0);
		Date from = fromCalendar.getTime();

		Calendar toCalendar = Calendar.getInstance(); 
		toCalendar.setTime(date);
		toCalendar.set(Calendar.HOUR_OF_DAY, 23);
		toCalendar.set(Calendar.MINUTE, 59);
		toCalendar.set(Calendar.SECOND, 59);
		toCalendar.set(Calendar.MILLISECOND, 0);
		Date to = toCalendar.getTime();
		
		for (Value value : values) {
			if (from.before(value.getDate()) && to.after(value.getDate())) {
				return true;
			}
		}
		return false;
	}
	private boolean haveDateOverwrite(Date date, EveKitOwner owner) {
		List<Value> values = Settings.get().getTrackerData().get(owner.getOwnerName());
		if (values == null) {
			return false;
		}

		if (!haveDateEveKit(date, owner)) {
			return false;
		}

		Calendar eveKitCalendar = Calendar.getInstance(); 
		eveKitCalendar.setTime(date);
		eveKitCalendar.set(Calendar.HOUR_OF_DAY, 12);
		eveKitCalendar.set(Calendar.MINUTE, 0);
		eveKitCalendar.set(Calendar.SECOND, 0);
		eveKitCalendar.set(Calendar.MILLISECOND, 0);
		Date eveKit = eveKitCalendar.getTime();

		Calendar fromCalendar = Calendar.getInstance(); 
		fromCalendar.setTime(date);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 1);
		fromCalendar.set(Calendar.MILLISECOND, 0);
		Date from = fromCalendar.getTime();

		Calendar toCalendar = Calendar.getInstance(); 
		toCalendar.setTime(date);
		toCalendar.set(Calendar.HOUR_OF_DAY, 23);
		toCalendar.set(Calendar.MINUTE, 59);
		toCalendar.set(Calendar.SECOND, 59);
		toCalendar.set(Calendar.MILLISECOND, 0);
		Date to = toCalendar.getTime();

		List<Value> removeValues = new ArrayList<Value>();
		for (Value value : values) {
			if (from.before(value.getDate()) && to.after(value.getDate()) && !eveKit.equals(value.getDate())) {
				removeValues.add(value);
			}
		}
		Settings.get().getTrackerData().get(owner.getOwnerName()).removeAll(removeValues);
		return true;
	}

	private boolean haveDateEveKit(Date date, EveKitOwner owner) {
		List<Value> values = Settings.get().getTrackerData().get(owner.getOwnerName());
		if (values == null) {
			return false;
		}

		Calendar eveKitCalendar = Calendar.getInstance(); 
		eveKitCalendar.setTime(date);
		eveKitCalendar.set(Calendar.HOUR_OF_DAY, 12);
		eveKitCalendar.set(Calendar.MINUTE, 0);
		eveKitCalendar.set(Calendar.SECOND, 0);
		eveKitCalendar.set(Calendar.MILLISECOND, 0);
		Date eveKit = eveKitCalendar.getTime();

		for (Value value : values) {
			if (eveKit.equals(value.getDate())) {
				return true;
			}
		}
		return false;
	}

	public static enum ReturnValue {
		NOTHING_NEW, CANCELLED, COMPLETED, ERROR
	}

	public static enum Merge {
		KEEP(TabsTracker.get().eveKitImportMergeKeep(), TabsTracker.get().eveKitImportMergeKeepInfo()),
		MERGE(TabsTracker.get().eveKitImportMergeMerge(), TabsTracker.get().eveKitImportMergeMergeInfo()),
		OVERWRITE(TabsTracker.get().eveKitImportMergeOverwrite(), TabsTracker.get().eveKitImportMergeOverwriteInfo()),
		;

		private final String title;
		private final String info;

		private Merge(String title, String info) {
			this.title = title;
			this.info = info;
		}

		public String getInfo() {
			return info;
		}

		@Override
		public String toString() {
			return title;
		}
	}

	public static enum DateInterval {
		DAY(TabsTracker.get().eveKitImportIntervalDayTime(), TabsTracker.get().eveKitImportIntervalDay(), 30, 1),
		WEEK(TabsTracker.get().eveKitImportIntervalWeekTime(), TabsTracker.get().eveKitImportIntervalWeek(), 4, 7),
		MONTH(TabsTracker.get().eveKitImportIntervalMonthTime(), TabsTracker.get().eveKitImportIntervalMonth(), 1, 30),
		;

		private final String time;
		private final String title;
		private final int updatesMonth;
		private final int updatesDay;

		private DateInterval(String time, String title, int updatesMonth, int updatesDay) {
			this.time = time;
			this.title = title;
			this.updatesMonth = updatesMonth;
			this.updatesDay = updatesDay;
		}

		public int getUpdatesMonth() {
			return updatesMonth;
		}

		public int getUpdatesDays() {
			return updatesDay;
		}

		public String getTitle() {
			return title;
		}

		public String getTime() {
			return time;
		}

		@Override
		public String toString() {
			return getTime();
		}
	}

	public static long daysBetween(Date day1, Date day2) {
		long diff = Math.abs(day1.getTime() - day2.getTime());
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}
}
