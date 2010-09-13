package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Candle
 */
public abstract class DataModelPriceDataSettings extends Bundle {
	public static DataModelPriceDataSettings get() {
		return BundleCache.get(DataModelPriceDataSettings.class);
	}
	public static DataModelPriceDataSettings get(Locale locale) {
		return BundleCache.get(DataModelPriceDataSettings.class, locale);
	}
	public DataModelPriceDataSettings(Locale locale) {
		super(locale);
	}
	public abstract String regionEmpire();
	public abstract String regionMarketHubs();
	public abstract String regionAllAmarr();
	public abstract String regionAllGallente();
	public abstract String regionAllMinmatar();
	public abstract String regionAllCaldari();
	public abstract String regionAridia();
	public abstract String regionDevoid();
	public abstract String regionDomain();
	public abstract String regionGenesis();
	public abstract String regionKador();
	public abstract String regionKorAzor();
	public abstract String regionTashMurkon();
	public abstract String regionTheBleakLands();
	public abstract String regionBlackRise();
	public abstract String regionLonetrek();
	public abstract String regionTheCitadel();
	public abstract String regionTheForge();
	public abstract String regionEssence();
	public abstract String regionEveryshore();
	public abstract String regionPlacid();
	public abstract String regionSinqLaison();
	public abstract String regionSolitude();
	public abstract String regionVergeVendor();
	public abstract String regionMetropolis();
	public abstract String regionHeimatar();
	public abstract String regionMoldenHeath();
	public abstract String regionDerelik();
	public abstract String regionKhanid();
}
