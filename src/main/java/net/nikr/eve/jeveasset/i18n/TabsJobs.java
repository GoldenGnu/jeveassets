package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

public abstract class TabsJobs extends Bundle {
	public static TabsJobs get() {
		return BundleCache.get(TabsJobs.class);
	}

	public static TabsJobs get(Locale locale) {
		return BundleCache.get(TabsJobs.class, locale);
	}

	public TabsJobs(Locale locale) {
		super(locale);
	}

	public abstract String activity();
	public abstract String character();
	public abstract String all();
	public abstract String industry();
	public abstract String install();
	public abstract String no();
	public abstract String state();
	public abstract String whitespace(Object arg0);
	public abstract String columnState();
	public abstract String columnActivity();
	public abstract String columnName();
	public abstract String columnLocation();
	public abstract String columnOwner();
	public abstract String columnInstallDate();
	public abstract String columnEndDate();
	public abstract String columnBpMe();
	public abstract String columnBpPe();
}
