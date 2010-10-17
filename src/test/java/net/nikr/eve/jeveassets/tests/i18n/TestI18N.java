package net.nikr.eve.jeveassets.tests.i18n;

import net.nikr.eve.jeveasset.i18n.DialoguesProfiles;
import net.nikr.eve.jeveasset.i18n.DialoguesCsvExport;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;
import net.nikr.eve.jeveasset.i18n.DataModelPriceDataSettings;
import java.util.Locale;
import net.nikr.eve.jeveasset.i18n.DataModelAssetFilter;
import net.nikr.eve.jeveasset.i18n.DataModelEveAsset;
import net.nikr.eve.jeveasset.i18n.DialoguesAddSystem;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
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

	@Test
	public void testDataModelPriceDataSettings_en() throws Exception {
		DataModelPriceDataSettings g = Bundle.load(DataModelPriceDataSettings.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.regionDerelik());
	}

	@Test
	public void testDialoguesAccount_en() throws Exception {
		DialoguesAccount g = Bundle.load(DialoguesAccount.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.helpText());
	}

	@Test
	public void testDialoguesAddSystem_en() throws Exception {
		DialoguesAddSystem g = Bundle.load(DialoguesAddSystem.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.filterResult(5));
	}

	@Test
	public void testDialoguesCsvExport_en() throws Exception {
		DialoguesCsvExport g = Bundle.load(DialoguesCsvExport.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.allAssets());
	}

	@Test
	public void testDialoguesProfiles_en() throws Exception {
		DialoguesProfiles g = Bundle.load(DialoguesProfiles.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.deleteProfileConfirm("delete me!"));
	}

	@Test
	public void testDialoguesSettings_en() throws Exception {
		DialoguesSettings g = Bundle.load(DialoguesSettings.class, Locale.ENGLISH,
				Bundle.LoadIgnoreMissing.NO,
				Bundle.LoadIgnoreExtra.NO,
				Bundle.LoadIgnoreParameterMisMatch.NO
				);
		assertNotNull(g.enterFilter());
	}
}
