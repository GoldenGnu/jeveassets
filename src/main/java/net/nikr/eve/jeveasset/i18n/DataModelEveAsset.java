package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Candle
 */
public abstract class DataModelEveAsset extends Bundle {
	public static DataModelEveAsset get() {
		return BundleCache.get(DataModelEveAsset.class);
	}

	public static DataModelEveAsset get(Locale locale) {
		return BundleCache.get(DataModelEveAsset.class, locale);
	}

	public DataModelEveAsset(Locale locale) {
		super(locale);
	}

	public abstract String priceSellMax();
	public abstract String priceSellAvg();
	public abstract String priceSellMedian();
	public abstract String priceSellMin();
	public abstract String priceMidpoint();
	public abstract String priceBuyMax();
	public abstract String priceBuyAvg();
	public abstract String priceBuyMedian();
	public abstract String priceBuyMin();

}
