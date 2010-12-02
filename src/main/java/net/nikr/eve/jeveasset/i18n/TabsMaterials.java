package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

public abstract class TabsMaterials extends Bundle {
	public static TabsMaterials get() {
		return BundleCache.get(TabsMaterials.class);
	}

	public static TabsMaterials get(Locale locale) {
		return BundleCache.get(TabsMaterials.class, locale);
	}

	public TabsMaterials(Locale locale) {
		super(locale);
	}

	public abstract String collapse();
	public abstract String columnName();
	public abstract String columnCount();
	public abstract String columnValue();
	public abstract String expand();
	public abstract String materials();
	public abstract String no();
	public abstract String whitespace(Object arg0);
}
