/*
 * Copyright 2009-2018 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;

/**
 *
 * @author Andrew
 */
public abstract class DialoguesUpdate extends Bundle {

	public static DialoguesUpdate get() {
		return BundleServiceFactory.getBundleService().get(DialoguesUpdate.class);
	}

	public DialoguesUpdate(final Locale locale) {
		super(locale);
	}

	public abstract String updating();
	public abstract String ok();
	public abstract String cancel();
	public abstract String cancelQuestion();
	public abstract String cancelQuestionTitle();
	public abstract String errors(String mouseTask);
	public abstract String minimize();

	// used in UpdateDialog
	public abstract String firstAccount();
	public abstract String allAccounts();
	public abstract String update();
	public abstract String contracts();
	public abstract String marketOrders();
	public abstract String industryJobs();
	public abstract String accounts();
	public abstract String accountBlances();
	public abstract String assets();
	public abstract String priceData();
	public abstract String priceDataAll();
	public abstract String priceDataNew();
	public abstract String priceDataNone();
	public abstract String nextUpdate();
	public abstract String noAccounts();
	public abstract String now();
	public abstract String conqStations();
	public abstract String citadel();
	public abstract String balance();
	public abstract String journal();
	public abstract String transactions();
	public abstract String names();
	public abstract String blueprints();
	public abstract String containerLogs();
	public abstract String structures();
	public abstract String step1();
	public abstract String step2();
	public abstract String step3();
	public abstract String step4();

	public abstract String clickToShow(String name);
	public abstract String clickToHide(String name);
}
