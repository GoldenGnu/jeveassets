package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

public abstract class TabsOverview extends Bundle {
	public static TabsOverview get() {
		return BundleCache.get(TabsOverview.class);
	}

	public static TabsOverview get(Locale locale) {
		return BundleCache.get(TabsOverview.class, locale);
	}

	public TabsOverview(Locale locale) {
		super(locale);
	}

	public abstract String all();
	public abstract String add();
	public abstract String addGroup();
	public abstract String allAssets();
	public abstract String cancel();
	public abstract String character();
	public abstract String deleteGroup();
	public abstract String deleteTheGroup(Object arg0);
	public abstract String edit();
	public abstract String filteredAssets();
	public abstract String groups();
	public abstract String groupName();
	public abstract String location();
	public abstract String locations();
	public abstract String none();
	public abstract String oK();
	public abstract String overview();
	public abstract String region();
	public abstract String regions();
	public abstract String removeTheLocation(Object arg0);
	public abstract String removeLocation();
	public abstract String renameGroup();
	public abstract String source();
	public abstract String stations();
	public abstract String systems();
	public abstract String the();
	public abstract String the1();
	public abstract String view();
	public abstract String whitespace();
	public abstract String whitespace1(Object arg0);
	public abstract String whitespace2(Object arg0);
	public abstract String whitespace3(Object arg0);
	public abstract String whitespace4(Object arg0);
	public abstract String columnName();
	public abstract String columnSystem();
	public abstract String columnRegion();
	public abstract String columnVolume();
	public abstract String columnValue();
	public abstract String columnCount();
	public abstract String columnAverageValue();
	public abstract String columnReprocessedValue();
}
