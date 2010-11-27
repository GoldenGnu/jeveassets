package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

public abstract class TabsValues extends Bundle {
	public static TabsValues get() {
		return BundleCache.get(TabsValues.class);
	}

	public static TabsValues get(Locale locale) {
		return BundleCache.get(TabsValues.class, locale);
	}

	public TabsValues(Locale locale) {
		super(locale);
	}

	public abstract String assets();
	public abstract String best();
	public abstract String best1();
	public abstract String best2();
	public abstract String character();
	public abstract String corporation();
	public abstract String escrows();
	public abstract String grand();
	public abstract String market();
	public abstract String no();
	public abstract String no1();
	public abstract String none();
	public abstract String select();
	public abstract String sell();
	public abstract String total();
	public abstract String values();
	public abstract String wallet();
}
