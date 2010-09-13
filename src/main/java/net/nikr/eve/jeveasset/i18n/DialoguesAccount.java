package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Candle
 */
public abstract class DialoguesAccount extends Bundle {
	public static DialoguesAccount get() {
		return BundleCache.get(DialoguesAccount.class);
	}

	public static DialoguesAccount get(Locale locale) {
		return BundleCache.get(DialoguesAccount.class, locale);
	}

	public DialoguesAccount(Locale locale) {
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

	public abstract String dialogueNameAccountManagement();

	public abstract String add();
	public abstract String collapse();
	public abstract String expand();
	public abstract String showAssets();
	public abstract String checkAll();
	public abstract String uncheckAll();
	public abstract String checkSelected();
	public abstract String uncheckSelected();
	public abstract String showCorp();
	public abstract String close();

	public abstract String corpAssetSettings();
	public abstract String corpAssetsChanged();


public abstract String deleteAccountQuestion();
public abstract String deleteAccount();
}