package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Candle
 */
public abstract class Dialogues extends Bundle {
	public static Dialogues get() {
		return BundleCache.get(Dialogues.class);
	}

	public static Dialogues get(Locale locale) {
		return BundleCache.get(Dialogues.class, locale);
	}

	public Dialogues(Locale locale) {
		super(locale);
	}

	public abstract String dialogueNameAccountImport();

	public abstract String previousArrow();
	public abstract String nextArrow();
	public abstract String cancel();
	public abstract String ok();

	public abstract String accountAlreadyImported();
	public abstract String noInternetConnection();
	public abstract String accountNotValid();
	public abstract String accountValid();
	public abstract String accountAlreadyImportedText();
	public abstract String noInternetConnectionText();
	public abstract String accountNotValidText();
	public abstract String accountValidText();

	public abstract String userId();
	public abstract String apiKey();
	public abstract String helpText();
	public abstract String validatingMessage();
}