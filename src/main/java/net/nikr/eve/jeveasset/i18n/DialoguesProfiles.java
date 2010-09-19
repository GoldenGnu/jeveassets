package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Andrew
 */
public abstract class DialoguesProfiles extends Bundle {
	public static DialoguesProfiles get() {
		return BundleCache.get(DialoguesProfiles.class);
	}
	public static DialoguesProfiles get(Locale locale) {
		return BundleCache.get(DialoguesProfiles.class, locale);
	}
	public DialoguesProfiles(Locale locale) {
		super(locale);
	}
	public abstract String ok();
	public abstract String cancel();
	public abstract String profiles();
	public abstract String load();
	public abstract String newP();
	public abstract String rename();
	public abstract String delete();
	public abstract String defaultP();
	public abstract String close();
	public abstract String loadProfile();
	public abstract String loadingProfile();
	public abstract String profileLoaded();
	public abstract String newProfile();
	public abstract String typeName();
	public abstract String nameAlreadyExists();
	public abstract String creatingProfile();
	public abstract String renameProfile();
	public abstract String enterNewName();
	public abstract String cannotDeleteActive();
	public abstract String cannotDeleteDefault();
	public abstract String deleteProfileConfirm(String name);
	public abstract String deleteProfile();
	public abstract String clearFilter();
	public abstract String profileLoadedMsg();

}
