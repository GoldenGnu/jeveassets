package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

public abstract class TabsOrders extends Bundle {
	public static TabsOrders get() {
		return BundleCache.get(TabsOrders.class);
	}

	public static TabsOrders get(Locale locale) {
		return BundleCache.get(TabsOrders.class, locale);
	}

	public TabsOrders(Locale locale) {
		super(locale);
	}

	public abstract String buy();
	public abstract String buy1();
	public abstract String character();
	public abstract String columnName();
	public abstract String columnQuantity();
	public abstract String columnPrice();
	public abstract String columnExpiresIn();
	public abstract String columnRange();
	public abstract String columnRemainingValue();
	public abstract String columnStatus();
	public abstract String columnMinVolume();
	public abstract String columnLocation();
	public abstract String market();
	public abstract String no();
	public abstract String sell();
	public abstract String sell1();
	public abstract String state();
	public abstract String whitespace(Object arg0);
}
