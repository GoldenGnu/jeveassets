package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Candle
 */
public abstract class General extends Bundle {
	public static General get() {
		return BundleCache.get(General.class);
	}

	public static General get(Locale locale) {
		return BundleCache.get(General.class, locale);
	}

	public General(Locale locale) {
		super(locale);
	}

	public abstract String uncaughtErrorMessage();
	public abstract String error();

}
