package net.nikr.eve.jeveasset.i18n;

import java.util.Date;
import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Andrew
 */
public abstract class DialoguesUpdate extends Bundle {
	public static DialoguesUpdate get() {
		return BundleCache.get(DialoguesUpdate.class);
	}
	public static DialoguesUpdate get(Locale locale) {
		return BundleCache.get(DialoguesUpdate.class, locale);
	}
	public DialoguesUpdate(Locale locale) {
		super(locale);
	}
	public abstract String updating();
	public abstract String ok();
	public abstract String cancel();
	public abstract String cancelQuestion();
	public abstract String cancelQuestionTitle();
	public abstract String errors(String mouseTask);

	// used in UpdateDialog
	public abstract String update();
	public abstract String all();
	public abstract String marketOrders();
	public abstract String industryJobs();
	public abstract String accounts();
	public abstract String accountBlances();
	public abstract String assets();
	public abstract String priceData();
	public abstract String nextUpdate();
	public abstract String nowAll();
	public abstract String nowSome();
	public abstract String nextUpdateTime(Date datetime);
	public abstract String conqStations();
	public abstract String balance();
	
	public abstract String clickToShow(String name);
	public abstract String clickToHide(String name);
}
