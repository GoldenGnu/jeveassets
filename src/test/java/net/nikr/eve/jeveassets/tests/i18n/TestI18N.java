package net.nikr.eve.jeveassets.tests.i18n;

import java.util.Locale;
import net.nikr.eve.jeveasset.i18n.DataModelAssetFilter;
import net.nikr.eve.jeveasset.i18n.DataModelEveAsset;
import net.nikr.eve.jeveasset.i18n.General;
import org.junit.Test;
import uk.me.candle.translations.Bundle;
import static org.junit.Assert.*;

/**
 *
 * @author Candle
 */
public class TestI18N {

	@Test public void testGeneralBundle_en() throws Exception {
		General g = Bundle.load(General.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.uncaughtErrorMessage());
	}

	@Test
	public void testDataModelAssetFilterBundle_en() throws Exception {
		DataModelAssetFilter g = Bundle.load(DataModelAssetFilter.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.modeContain());
	}

	@Test
	public void testDataModelEveAssetBundle_en() throws Exception {
		DataModelEveAsset g = Bundle.load(DataModelEveAsset.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.priceSellMax());
	}
}
