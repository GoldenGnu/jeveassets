/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PriceDataSettings {

	private final static Logger LOG = LoggerFactory.getLogger(PriceDataSettings.class);

	public final static String SOURCE_EVE_CENTRAL = "eve-central";
	public final static String SOURCE_EVE_METRICS = "eve-metrics";

	public final static String[] SOURCES = {SOURCE_EVE_CENTRAL, SOURCE_EVE_METRICS};

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

	public final static String[] REGIONS_EVE_CENTRAL = {REGION_EMPIRE
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

	public final static String[] REGIONS_EVE_METRICS = {
											REGION_ARIDIA
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
	private String source;

	public PriceDataSettings(int region, String source) {
		this.region = region;
		this.source = source;
	}

	public int getRegion() {
		if (source.equals(SOURCE_EVE_CENTRAL) && region >= REGIONS_EVE_CENTRAL.length){
			LOG.warn("PriceDataSettings: region index is larger then the region array (eve-central)");
			return 0;
		}
		if (source.equals(SOURCE_EVE_METRICS) && region >= REGIONS_EVE_METRICS.length){
			LOG.warn("PriceDataSettings: region index is larger then the region array (eve-metrics)");
			return 0;
		}
		return region;
	}

	public String getSource(){
		return source;
	}

	public List<Long> getRegions(){
		List<Long> regions = new ArrayList<Long>();
		String sRegion = "";
		if (source.equals(SOURCE_EVE_CENTRAL)){
			sRegion = REGIONS_EVE_CENTRAL[getRegion()];
		}
		if (source.equals(SOURCE_EVE_METRICS)){
			sRegion = REGIONS_EVE_METRICS[getRegion()];
		}
		if (sRegion.equals(REGION_EMPIRE)){
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
		if (sRegion.equals(REGION_MARKET_HUBS)){
			regions.add(10000002l); //Caldari: The Forge (Jita)
			regions.add(10000042l); //Minmatar : Metropolis (Hek)
			regions.add(10000030l); //Minmatar : Heimatar (Rens)
			regions.add(10000064l); //Gallente: Essence (Oursalert)
			regions.add(10000043l); //Amarr: Domain (Amarr)
		}
		if (sRegion.equals(REGION_ALL_AMARR)){
			regions.add(10000054l); //Amarr: Aridia
			regions.add(10000036l); //Amarr: Devoid
			regions.add(10000043l); //Amarr: Domain
			regions.add(10000067l); //Amarr: Genesis
			regions.add(10000052l); //Amarr: Kador
			regions.add(10000065l); //Amarr: Kor-Azor
			regions.add(10000020l); //Amarr: Tash-Murkon
			regions.add(10000038l); //Amarr: The Bleak Lands
		}
		if (sRegion.equals(REGION_ALL_GALLENTE)){
			regions.add(10000064l); //Gallente: Essence
			regions.add(10000037l); //Gallente: Everyshore
			regions.add(10000048l); //Gallente: Placid
			regions.add(10000032l); //Gallente: Sinq Laison
			regions.add(10000044l); //Gallente: Solitude
			regions.add(10000068l); //Gallente: Verge Vendor
		}
		if (sRegion.equals(REGION_ALL_MINMATAR)){
			regions.add(10000042l); //Minmatar : Metropolis
			regions.add(10000030l); //Minmatar : Heimatar
			regions.add(10000028l); //Minmatar : Molden Heath
		}
		if (sRegion.equals(REGION_ALL_CALDARI)){
			regions.add(10000069l); //Caldari: Black Rise
			regions.add(10000016l); //Caldari: Lonetrek
			regions.add(10000033l); //Caldari: The Citadel
			regions.add(10000002l); //Caldari: The Forge
		}
		if (sRegion.equals(REGION_ARIDIA)){
			//Amarr: Aridia
			regions.add(10000054l);
		}
		if (sRegion.equals(REGION_DEVOID)){
			//Amarr: Devoid
			regions.add(10000036l);
		}
		if (sRegion.equals(REGION_DOMAIN)){
			regions.add(10000043l); //Amarr: Domain
		}
		if (sRegion.equals(REGION_GENESIS)){
			regions.add(10000067l); //Amarr: Genesis
		}
		if (sRegion.equals(REGION_KADOR)){
			regions.add(10000052l); //Amarr: Kador
		}
		if (sRegion.equals(REGION_KOR_AZOR)){
			regions.add(10000065l); //Amarr: Kor-Azor
		}
		if (sRegion.equals(REGION_TASH_MURKON)){
			regions.add(10000020l); //Amarr: Tash-Murkon
		}
		if (sRegion.equals(REGION_THE_BLEAK_LANDS)){
			regions.add(10000038l); //Amarr: The Bleak Lands
		}
		if (sRegion.equals(REGION_BLACK_RISE)){
			regions.add(10000069l); //Caldari: Black Rise
		}
		if (sRegion.equals(REGION_LONETREK)){
			regions.add(10000016l); //Caldari: Lonetrek
		}
		if (sRegion.equals(REGION_THE_CITADEL)){
			regions.add(10000033l); //Caldari: The Citadel
		}
		if (sRegion.equals(REGION_THE_FORGE)){
			regions.add(10000002l); //Caldari: The Forge
		}
		if (sRegion.equals(REGION_ESSENCE)){
			regions.add(10000064l); //Gallente: Essence
		}
		if (sRegion.equals(REGION_EVERYSHORE)){
			regions.add(10000037l); //Gallente: Everyshore
		}
		if (sRegion.equals(REGION_PLACID)){
			regions.add(10000048l); //Gallente: Placid
		}
		if (sRegion.equals(REGION_SINQ_LAISON)){
			regions.add(10000032l); //Gallente: Sinq Laison
		}
		if (sRegion.equals(REGION_SOLITUDE)){
			regions.add(10000044l); //Gallente: Solitude
		}
		if (sRegion.equals(REGION_VERGE_VENDOR)){
			regions.add(10000068l); //Gallente: Verge Vendor
		}
		if (sRegion.equals(REGION_METROPOLIS)){
			regions.add(10000042l); //Minmatar : Metropolis
		}
		if (sRegion.equals(REGION_HEIMATAR)){
			regions.add(10000030l); //Minmatar : Heimatar
		}
		if (sRegion.equals(REGION_MOLDEN_HEATH)){
			regions.add(10000028l); //Minmatar : Molden Heath
		}
		if (sRegion.equals(REGION_DERELIK)){
			regions.add(10000001l); //Ammatar: Derelik
		}
		if (sRegion.equals(REGION_KHANID)){
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
