package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Candle
 */
public abstract class DataModelIndustryJob extends Bundle {

	public static DataModelIndustryJob get() {
		return BundleCache.get(DataModelIndustryJob.class);
	}

	public static DataModelIndustryJob get(Locale locale) {
		return BundleCache.get(DataModelIndustryJob.class, locale);
	}

	public DataModelIndustryJob(Locale locale) {
		super(locale);
	}

	public abstract String stateAll();
	public abstract String stateDelivered();
	public abstract String stateFailed();
	public abstract String stateReady();
	public abstract String stateActive();
	public abstract String statePending();
	public abstract String stateAborted();
	public abstract String stateGmAborted();
	public abstract String stateInFlight();
	public abstract String stateDestroyed();
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

}
