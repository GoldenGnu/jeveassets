package net.nikr.eve.jeveasset.i18n;

import java.util.Date;
import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Andrew
 */
public abstract class DialoguesAbout extends Bundle {
	public static DialoguesAbout get() {
		return BundleCache.get(DialoguesAbout.class);
	}
	public static DialoguesAbout get(Locale locale) {
		return BundleCache.get(DialoguesAbout.class, locale);
	}
	public DialoguesAbout(Locale locale) {
		super(locale);
	}
	public abstract String about();
	public abstract String close();
	public abstract String updates();
	public abstract String updatesInProgress();
}
