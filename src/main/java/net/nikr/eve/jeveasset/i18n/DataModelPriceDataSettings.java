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
 * @author Candle
 */
public abstract class DataModelPriceDataSettings extends Bundle {

	public static DataModelPriceDataSettings get() {
		return BundleServiceFactory.getBundleService().get(DataModelPriceDataSettings.class);
	}

	public DataModelPriceDataSettings(final Locale locale) {
		super(locale);
	}

	public abstract String sourceEveAddicts();
	public abstract String sourceEveCentral();
	public abstract String sourceEveMarketdata();
	public abstract String sourceEveMarketeer();
	public abstract String sourceEvemarketer();
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
	public abstract String priceSellMax();
	public abstract String priceSellAvg();
	public abstract String priceSellMedian();
	public abstract String priceSellMin();
	public abstract String priceSellPercentile();
	public abstract String priceMidpoint();
	public abstract String priceBuyMax();
	public abstract String priceBuyAvg();
	public abstract String priceBuyMedian();
	public abstract String priceBuyPercentile();
	public abstract String priceBuyMin();
}
