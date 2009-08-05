/*
 * Copyright 2009, Niklas Kyster Rasmussen
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class MarketstatSettings {

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
	private int age;
	private int quantity;

	public MarketstatSettings(int region, int age, int quantity) {
		this.region = region;
		this.age = age;
		this.quantity = quantity;
	}

	public int getAge() {
		return age;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getRegion() {
		return region;
	}
	public String getOutput() throws UnsupportedEncodingException{
		String data = "";
		if ( getAge() > 0) data = data + "&" + URLEncoder.encode("hours", "UTF-8") + "=" + URLEncoder.encode(String.valueOf( (getAge()*24) ), "UTF-8");
		if ( getQuantity() > 0) data = data + "&" + URLEncoder.encode("minQ", "UTF-8") + "=" + URLEncoder.encode(String.valueOf( getQuantity() ), "UTF-8");
		if (REGIONS[region].equals(REGION_EMPIRE)){
		//Amarr
			//Amarr: Aridia
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000054), "UTF-8");
			//Amarr: Devoid
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000036), "UTF-8");
			//Amarr: Domain
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000043), "UTF-8");
			//Amarr: Genesis
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000067), "UTF-8");
			//Amarr: Kador
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000052), "UTF-8");
			//Amarr: Kor-Azor
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000065), "UTF-8");
			//Amarr: Tash-Murkon
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000020), "UTF-8");
			//Amarr: The Bleak Lands
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000038), "UTF-8");
		//Caldari
			//Caldari: Black Rise
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000069), "UTF-8");
			//Caldari: Lonetrek
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000016), "UTF-8");
			//Caldari: The Citadel
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000033), "UTF-8");
			//Caldari: The Forge
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000002), "UTF-8");
		//Gallente
			//Gallente: Essence
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000064), "UTF-8");
			//Gallente: Everyshore
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000037), "UTF-8");
			//Gallente: Placid
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000048), "UTF-8");
			//Gallente: Sinq Laison
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000032), "UTF-8");
			//Gallente: Solitude
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000044), "UTF-8");
			//Gallente: Verge Vendor
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000068), "UTF-8");
		//Minmatar
			//Minmatar : Metropolis
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000042), "UTF-8");
			//Minmatar : Heimatar
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000030), "UTF-8");
			//Minmatar : Molden Heath
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000028), "UTF-8");
		//Others
			//Ammatar: Derelik
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000001), "UTF-8");
			//Khanid: Khanid
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000049), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_MARKET_HUBS)){
			//Caldari: The Forge (Jita)
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000002), "UTF-8");
			//Minmatar : Metropolis (Hek)
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000042), "UTF-8");
			//Minmatar : Heimatar (Rens)
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000030), "UTF-8");
			//Gallente: Essence (Oursalert)
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000064), "UTF-8");
			//Amarr: Domain (Amarr)
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000043), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_ALL_AMARR)){
			//Amarr: Aridia
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000054), "UTF-8");
			//Amarr: Devoid
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000036), "UTF-8");
			//Amarr: Domain
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000043), "UTF-8");
			//Amarr: Genesis
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000067), "UTF-8");
			//Amarr: Kador
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000052), "UTF-8");
			//Amarr: Kor-Azor
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000065), "UTF-8");
			//Amarr: Tash-Murkon
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000020), "UTF-8");
			//Amarr: The Bleak Lands
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000038), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_ALL_GALLENTE)){
			//Gallente: Essence
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000064), "UTF-8");
			//Gallente: Everyshore
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000037), "UTF-8");
			//Gallente: Placid
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000048), "UTF-8");
			//Gallente: Sinq Laison
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000032), "UTF-8");
			//Gallente: Solitude
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000044), "UTF-8");
			//Gallente: Verge Vendor
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000068), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_ALL_MINMATAR)){
			//Minmatar : Metropolis
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000042), "UTF-8");
			//Minmatar : Heimatar
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000030), "UTF-8");
			//Minmatar : Molden Heath
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000028), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_ALL_CALDARI)){
			//Caldari: Black Rise
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000069), "UTF-8");
			//Caldari: Lonetrek
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000016), "UTF-8");
			//Caldari: The Citadel
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000033), "UTF-8");
			//Caldari: The Forge
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000002), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_ARIDIA)){
			//Amarr: Aridia
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000054), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_DEVOID)){
			//Amarr: Devoid
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000036), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_DOMAIN)){
			//Amarr: Domain
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000043), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_GENESIS)){
			//Amarr: Genesis
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000067), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_KADOR)){
			//Amarr: Kador
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000052), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_KOR_AZOR)){
			//Amarr: Kor-Azor
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000065), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_TASH_MURKON)){
			//Amarr: Tash-Murkon
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000020), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_THE_BLEAK_LANDS)){
			//Amarr: The Bleak Lands
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000038), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_BLACK_RISE)){
			//Caldari: Black Rise
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000069), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_LONETREK)){
			//Caldari: Lonetrek
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000016), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_THE_CITADEL)){
			//Caldari: The Citadel
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000033), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_THE_FORGE)){
			//Caldari: The Forge
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000002), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_ESSENCE)){
			//Gallente: Essence
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000064), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_EVERYSHORE)){
			//Gallente: Everyshore
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000037), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_PLACID)){
			//Gallente: Placid
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000048), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_SINQ_LAISON)){
			//Gallente: Sinq Laison
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000032), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_SOLITUDE)){
			//Gallente: Solitude
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000044), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_VERGE_VENDOR)){
			//Gallente: Verge Vendor
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000068), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_METROPOLIS)){
			//Minmatar : Metropolis
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000042), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_HEIMATAR)){
			//Minmatar : Heimatar
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000030), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_MOLDEN_HEATH)){
			//Minmatar : Molden Heath
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000028), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_DERELIK)){
			//Ammatar: Derelik
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000001), "UTF-8");
		}
		if (REGIONS[region].equals(REGION_KHANID)){
			//Khanid: Khanid
			data = data + "&" + URLEncoder.encode("regionlimit", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(10000049), "UTF-8");
		}
		return data;
	}
}
