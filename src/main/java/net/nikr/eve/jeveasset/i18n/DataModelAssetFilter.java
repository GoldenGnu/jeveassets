package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Candle
 */
public abstract class DataModelAssetFilter extends Bundle {
	public static DataModelAssetFilter get() {
		return BundleCache.get(DataModelAssetFilter.class);
	}

	public static DataModelAssetFilter get(Locale locale) {
		return BundleCache.get(DataModelAssetFilter.class, locale);
	}

	public DataModelAssetFilter(Locale locale) {
		super(locale);
	}

	public abstract String modeContain();
	public abstract String modeContainNot();
	public abstract String modeEqual();
	public abstract String modeEqualNot();
	public abstract String modeGreaterThan();
	public abstract String modeLessThan();
	public abstract String modeGreaterThanColumn();
	public abstract String modeLessThanColumn();
	
	public abstract String and();
	public abstract String or();


}
