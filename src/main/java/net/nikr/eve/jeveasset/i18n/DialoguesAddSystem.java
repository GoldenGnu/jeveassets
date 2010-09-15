package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Candle
 */
public abstract class DialoguesAddSystem extends Bundle {
	public static DialoguesAddSystem get() {
		return BundleCache.get(DialoguesAddSystem.class);
	}

	public static DialoguesAddSystem get(Locale locale) {
		return BundleCache.get(DialoguesAddSystem.class, locale);
	}

	public DialoguesAddSystem(Locale locale) {
		super(locale);
	}

	public abstract String defaultFilterResult();
	public abstract String filterResult(int resultCount);
	public abstract String defaultSelectedSystem();
	public abstract String addSystem();
	public abstract String syetemFilter();
	public abstract String filterStatus();
	public abstract String selectedSystem();
	public abstract String add();
	public abstract String cancel();
	public abstract String treeLabel(String systemName);

}
