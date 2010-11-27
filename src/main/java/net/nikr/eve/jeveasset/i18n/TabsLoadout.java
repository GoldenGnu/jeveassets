package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

public abstract class TabsLoadout extends Bundle {
	public static TabsLoadout get() {
		return BundleCache.get(TabsLoadout.class);
	}

	public static TabsLoadout get(Locale locale) {
		return BundleCache.get(TabsLoadout.class, locale);
	}

	public TabsLoadout(Locale locale) {
		super(locale);
	}

	public abstract String cancel();
	public abstract String character();
	public abstract String collapse();
	public abstract String description();
	public abstract String empty();
	public abstract String expand();
	public abstract String export();
	public abstract String export1();
	public abstract String export2();
	public abstract String name();
	public abstract String name1();
	public abstract String no();
	public abstract String no1();
	public abstract String oK();
	public abstract String ship();
	public abstract String ship1();
	public abstract String whitespace10(Object arg0);
	public abstract String whitespace9(Object arg0);
}
