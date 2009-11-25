/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.data;

import java.util.List;
import java.util.Vector;
import uk.me.candle.eve.pricing.options.PricingNumber;
import uk.me.candle.eve.pricing.options.PricingType;


public class PriceDataSettings {

	public final static String SOURCE_EVE_CENTRAL = "eve-central";
	public final static String SOURCE_EVE_METRICS = "eve-metrics";

	public final static String[] SOURCE = {SOURCE_EVE_CENTRAL, SOURCE_EVE_METRICS};

	public final static String REGION_EMPIRE = "Empire";
	public final static String REGION_MARKET_HUBS = "Main Market Hubs";
	public final static String REGION_ALL_AMARR = "All Amarr";
	public final static String REGION_ALL_GALLENTE = "All Gallente";
	public final static String REGION_ALL_MINMATAR = "All Minmatar";
	public final static String REGION_ALL_CALDARI = "All Caldari";
	public final static String REGION_ARIDIA = "Aridia";
	public final static String REGION_DEVOID = "Devoid";
	public final static String REGION_DOMAIN = "Domain (Amarr)";
	public final static String REGION_GENESIS = "Genesis";
	public final static String REGION_KADOR = "Kador";
	public final static String REGION_KOR_AZOR = "Kor-Azor";
	public final static String REGION_TASH_MURKON = "Tash-Murkon";
	public final static String REGION_THE_BLEAK_LANDS = "The Bleak Lands";
	public final static String REGION_BLACK_RISE = "Black Rise";
	public final static String REGION_LONETREK = "Lonetrek";
	public final static String REGION_THE_CITADEL = "The Citadel";
	public final static String REGION_THE_FORGE = "The Forge (Jita)";
	public final static String REGION_ESSENCE = "Essence (Oursalert)";
	public final static String REGION_EVERYSHORE = "Everyshore";
	public final static String REGION_PLACID = "Placid";
	public final static String REGION_SINQ_LAISON = "Sinq Laison";
	public final static String REGION_SOLITUDE = "Solitude";
	public final static String REGION_VERGE_VENDOR = "Verge Vendor";
	public final static String REGION_METROPOLIS = "Metropolis (Hek)";
	public final static String REGION_HEIMATAR = "Heimatar (Rens)";
	public final static String REGION_MOLDEN_HEATH = "Molden Heath";
	public final static String REGION_DERELIK = "Derelik";
	public final static String REGION_KHANID = "Khanid";

	public final static String[] REGIONS = {REGION_EMPIRE
											,REGION_MARKET_HUBS
											,REGION_ALL_AMARR
											,REGION_ALL_GALLENTE
											,REGION_ALL_MINMATAR
											,REGION_ALL_CALDARI
											,REGION_ARIDIA
											,REGION_BLACK_RISE
											,REGION_DERELIK
											,REGION_DEVOID
											,REGION_DOMAIN
											,REGION_ESSENCE
											,REGION_EVERYSHORE
											,REGION_GENESIS
											,REGION_HEIMATAR
											,REGION_KADOR
											,REGION_KHANID
											,REGION_KOR_AZOR
											,REGION_LONETREK
											,REGION_METROPOLIS
											,REGION_MOLDEN_HEATH
											,REGION_PLACID
											,REGION_SINQ_LAISON
											,REGION_SOLITUDE
											,REGION_TASH_MURKON
											,REGION_THE_BLEAK_LANDS
											,REGION_THE_CITADEL
											,REGION_THE_FORGE
											,REGION_VERGE_VENDOR
											};

	private int region;
	private String priceSource;
	private String source;

	public PriceDataSettings(int region, String priceSource, String source) {
		this.region = region;
		this.priceSource = priceSource;
		this.source = source;
	}

	public String getPriceSource() {
		if (!EveAsset.getPriceSources().contains(priceSource)) return EveAsset.getPriceSources().get(0);
		return priceSource;
	}

	public int getRegion() {
		if (region >= REGIONS.length) return 0;
		return region;
	}

	public String getSource(){
		return source;
	}

	public PricingType getDefaultPricingType(){
		if (priceSource.equals(EveAsset.PRICE_BUY_AVG)) return PricingType.MEAN;
		if (priceSource.equals(EveAsset.PRICE_BUY_MAX)) return PricingType.HIGH;
		if (priceSource.equals(EveAsset.PRICE_BUY_MIN)) return PricingType.LOW;
		if (priceSource.equals(EveAsset.PRICE_BUY_MEDIAN)) return PricingType.MEDIAN;
		if (priceSource.equals(EveAsset.PRICE_SELL_AVG)) return PricingType.MEAN;
		if (priceSource.equals(EveAsset.PRICE_SELL_MAX)) return PricingType.HIGH;
		if (priceSource.equals(EveAsset.PRICE_SELL_MIN)) return PricingType.LOW;
		if (priceSource.equals(EveAsset.PRICE_SELL_MEDIAN)) return PricingType.MEDIAN;
		return PricingType.MEDIAN;
	}

	public PricingNumber getDefaultPricingNumber(){
		if (priceSource.equals(EveAsset.PRICE_BUY_AVG)) return PricingNumber.BUY;
		if (priceSource.equals(EveAsset.PRICE_BUY_MAX)) return PricingNumber.BUY;
		if (priceSource.equals(EveAsset.PRICE_BUY_MIN)) return PricingNumber.BUY;
		if (priceSource.equals(EveAsset.PRICE_BUY_MEDIAN)) return PricingNumber.BUY;
		if (priceSource.equals(EveAsset.PRICE_SELL_AVG)) return PricingNumber.SELL;
		if (priceSource.equals(EveAsset.PRICE_SELL_MAX)) return PricingNumber.SELL;
		if (priceSource.equals(EveAsset.PRICE_SELL_MIN)) return PricingNumber.SELL;
		if (priceSource.equals(EveAsset.PRICE_SELL_MEDIAN)) return PricingNumber.SELL;
		return PricingNumber.SELL;
	}

	public List<Long> getRegions(){
		List<Long> regions = new Vector<Long>();
		if (REGIONS[region].equals(REGION_EMPIRE)){
		//Amarr
			regions.add(10000054l); //Amarr: Aridia
			regions.add(10000036l); //Amarr: Devoid
			regions.add(10000043l); //Amarr: Domain
			regions.add(10000067l); //Amarr: Genesis
			regions.add(10000052l); //Amarr: Kador
			regions.add(10000065l); //Amarr: Kor-Azor
			regions.add(10000020l); //Amarr: Tash-Murkon
			regions.add(10000038l); //Amarr: The Bleak Lands
		//Caldari
			regions.add(10000069l); //Caldari: Black Rise
			regions.add(10000016l); //Caldari: Lonetrek
			regions.add(10000033l); //Caldari: The Citadel
			regions.add(10000002l); //Caldari: The Forge
		//Gallente
			regions.add(10000064l); //Gallente: Essence
			regions.add(10000037l); //Gallente: Everyshore
			regions.add(10000048l); //Gallente: Placid
			regions.add(10000032l); //Gallente: Sinq Laison
			regions.add(10000044l); //Gallente: Solitude
			regions.add(10000068l); //Gallente: Verge Vendor
		//Minmatar
			regions.add(10000042l); //Minmatar : Metropolis
			regions.add(10000030l); //Minmatar : Heimatar
			regions.add(10000028l); //Minmatar : Molden Heath
		//Others
			regions.add(10000001l); //Ammatar: Derelik
			regions.add(10000049l); //Khanid: Khanid
		}
		if (REGIONS[region].equals(REGION_MARKET_HUBS)){
			regions.add(10000002l); //Caldari: The Forge (Jita)
			regions.add(10000042l); //Minmatar : Metropolis (Hek)
			regions.add(10000030l); //Minmatar : Heimatar (Rens)
			regions.add(10000064l); //Gallente: Essence (Oursalert)
			regions.add(10000043l); //Amarr: Domain (Amarr)
		}
		if (REGIONS[region].equals(REGION_ALL_AMARR)){
			regions.add(10000054l); //Amarr: Aridia
			regions.add(10000036l); //Amarr: Devoid
			regions.add(10000043l); //Amarr: Domain
			regions.add(10000067l); //Amarr: Genesis
			regions.add(10000052l); //Amarr: Kador
			regions.add(10000065l); //Amarr: Kor-Azor
			regions.add(10000020l); //Amarr: Tash-Murkon
			regions.add(10000038l); //Amarr: The Bleak Lands
		}
		if (REGIONS[region].equals(REGION_ALL_GALLENTE)){
			regions.add(10000064l); //Gallente: Essence
			regions.add(10000037l); //Gallente: Everyshore
			regions.add(10000048l); //Gallente: Placid
			regions.add(10000032l); //Gallente: Sinq Laison
			regions.add(10000044l); //Gallente: Solitude
			regions.add(10000068l); //Gallente: Verge Vendor
		}
		if (REGIONS[region].equals(REGION_ALL_MINMATAR)){
			regions.add(10000042l); //Minmatar : Metropolis
			regions.add(10000030l); //Minmatar : Heimatar
			regions.add(10000028l); //Minmatar : Molden Heath
		}
		if (REGIONS[region].equals(REGION_ALL_CALDARI)){
			regions.add(10000069l); //Caldari: Black Rise
			regions.add(10000016l); //Caldari: Lonetrek
			regions.add(10000033l); //Caldari: The Citadel
			regions.add(10000002l); //Caldari: The Forge
		}
		if (REGIONS[region].equals(REGION_ARIDIA)){
			//Amarr: Aridia
			regions.add(10000054l);
		}
		if (REGIONS[region].equals(REGION_DEVOID)){
			//Amarr: Devoid
			regions.add(10000036l);
		}
		if (REGIONS[region].equals(REGION_DOMAIN)){
			regions.add(10000043l); //Amarr: Domain
		}
		if (REGIONS[region].equals(REGION_GENESIS)){
			regions.add(10000067l); //Amarr: Genesis
		}
		if (REGIONS[region].equals(REGION_KADOR)){
			regions.add(10000052l); //Amarr: Kador
		}
		if (REGIONS[region].equals(REGION_KOR_AZOR)){
			regions.add(10000065l); //Amarr: Kor-Azor
		}
		if (REGIONS[region].equals(REGION_TASH_MURKON)){
			regions.add(10000020l); //Amarr: Tash-Murkon
		}
		if (REGIONS[region].equals(REGION_THE_BLEAK_LANDS)){
			regions.add(10000038l); //Amarr: The Bleak Lands
		}
		if (REGIONS[region].equals(REGION_BLACK_RISE)){
			regions.add(10000069l); //Caldari: Black Rise
		}
		if (REGIONS[region].equals(REGION_LONETREK)){
			regions.add(10000016l); //Caldari: Lonetrek
		}
		if (REGIONS[region].equals(REGION_THE_CITADEL)){
			regions.add(10000033l); //Caldari: The Citadel
		}
		if (REGIONS[region].equals(REGION_THE_FORGE)){
			regions.add(10000002l); //Caldari: The Forge
		}
		if (REGIONS[region].equals(REGION_ESSENCE)){
			regions.add(10000064l); //Gallente: Essence
		}
		if (REGIONS[region].equals(REGION_EVERYSHORE)){
			regions.add(10000037l); //Gallente: Everyshore
		}
		if (REGIONS[region].equals(REGION_PLACID)){
			regions.add(10000048l); //Gallente: Placid
		}
		if (REGIONS[region].equals(REGION_SINQ_LAISON)){
			regions.add(10000032l); //Gallente: Sinq Laison
		}
		if (REGIONS[region].equals(REGION_SOLITUDE)){
			regions.add(10000044l); //Gallente: Solitude
		}
		if (REGIONS[region].equals(REGION_VERGE_VENDOR)){
			regions.add(10000068l); //Gallente: Verge Vendor
		}
		if (REGIONS[region].equals(REGION_METROPOLIS)){
			regions.add(10000042l); //Minmatar : Metropolis
		}
		if (REGIONS[region].equals(REGION_HEIMATAR)){
			regions.add(10000030l); //Minmatar : Heimatar
		}
		if (REGIONS[region].equals(REGION_MOLDEN_HEATH)){
			regions.add(10000028l); //Minmatar : Molden Heath
		}
		if (REGIONS[region].equals(REGION_DERELIK)){
			regions.add(10000001l); //Ammatar: Derelik
		}
		if (REGIONS[region].equals(REGION_KHANID)){
			regions.add(10000049l); //Khanid: Khanid
		}
		return regions;
	}

	@Override
	public boolean equals(Object obj){
		if (obj instanceof PriceDataSettings){
			return equals( (PriceDataSettings) obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + this.region;
		hash = 79 * hash + (this.source != null ? this.source.hashCode() : 0);
		return hash;
	}

	public boolean equals(PriceDataSettings priceDataSettings){
		if (priceDataSettings.getRegion() == this.getRegion() && priceDataSettings.getSource().equals(this.source) ) return true;
		return false;
	}
}
