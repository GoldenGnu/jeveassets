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

package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;

/**
 *
 * @author Candle
 */
public abstract class DataModelIndustryJob extends Bundle {

	public static DataModelIndustryJob get() {
		return BundleServiceFactory.getBundleService().get(DataModelIndustryJob.class);
	}

	public DataModelIndustryJob(final Locale locale) {
		super(locale);
	}

	public abstract String stateAll();
	public abstract String statePaused();
	public abstract String stateActive();
	public abstract String stateDone();
	public abstract String stateReverted();
	public abstract String stateCancelled();
	public abstract String stateDelivered();
	public abstract String activityAll();
	public abstract String activityNone();
	public abstract String activityManufacturing();
	public abstract String activityResearchingTechnology();
	public abstract String activityResearchingTimeProductivity();
	public abstract String activityResearchingMeterialProductivity();
	public abstract String activityCopying();
	public abstract String activityDuplicating();
	public abstract String activityReverseEngineering();
	public abstract String activityReverseInvention();
	public abstract String activityReactions();
	public abstract String activityUnknown();
	public abstract String descriptionCopying(String blueprintName, int copyCount, int copyRuns);
}
