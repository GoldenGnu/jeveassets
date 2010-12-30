package net.nikr.eve.jeveasset.i18n;

import java.util.Date;
import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

public abstract class GuiFrame extends Bundle {
	public static GuiFrame get() {
		return BundleCache.get(GuiFrame.class);
	}

	public static GuiFrame get(Locale locale) {
		return BundleCache.get(GuiFrame.class, locale);
	}

	public GuiFrame(Locale locale) {
		super(locale);
	}

	public abstract String about();
	public abstract String accounts();
	public abstract String change();
	public abstract String close();
	public abstract String credits();
	public abstract String eve();
	public abstract String exit();
	public abstract String export();
	public abstract String file();
	public abstract String help();
	public abstract String industry();
	public abstract String industryPlot();
	public abstract String license();
	public abstract String market();
	public abstract String materials();
	public abstract String not();
	public abstract String options();
	public abstract String options1();
	public abstract String overview();
	public abstract String profiles();
	public abstract String readme();
	public abstract String routing();
	public abstract String ship();
	public abstract String table();
	public abstract String tools();
	public abstract String updatable();
	public abstract String update();
	public abstract String update1();
	public abstract String values();
	public abstract String windowTitle(String programName, String programVersion, int portable, int profileCount, String activeProfileName);
	public abstract String eveTime(Date date);
}
