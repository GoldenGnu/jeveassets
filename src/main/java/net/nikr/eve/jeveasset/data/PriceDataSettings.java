/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
import java.util.Collections;
import java.util.List;
import net.nikr.eve.jeveasset.i18n.DataModelPriceDataSettings;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;
import uk.me.candle.eve.pricing.options.LocationType;
import uk.me.candle.eve.pricing.options.PricingFetch;
import uk.me.candle.eve.pricing.options.PricingNumber;
import uk.me.candle.eve.pricing.options.PricingType;


public class PriceDataSettings {

	public enum PriceSource {
		EVE_CENTRAL(PricingFetch.EVE_CENTRAL, true, false, true, false) {
			@Override public PriceMode[] getPriceTypes() {
				return PriceMode.values();
			}
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().sourceEveCentral();
			}
		},
		EVE_MARKETDATA(PricingFetch.EVE_MARKETDATA, false, true, false, false) {
			@Override public PriceMode[] getPriceTypes() {
				return new PriceMode[]{PriceMode.PRICE_SELL_PERCENTILE, PriceMode.PRICE_MIDPOINT, PriceMode.PRICE_BUY_PERCENTILE};
			}
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().sourceEveMarketdata();
			}
		},
		/*
		EVEMARKETEER(PricingFetch.EVEMARKETEER, false, true, true, true) {
			@Override public PriceMode[] getPriceTypes() {
				return PriceMode.values();
			}
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().sourceEveMarketeer();
			}
		},
		*/
		EVE_ADDICTS(PricingFetch.EVE_ADDICTS, false, true, false, true) {
			@Override public PriceMode[] getPriceTypes() {
				return new PriceMode[]{PriceMode.PRICE_SELL_AVG, PriceMode.PRICE_SELL_PERCENTILE, PriceMode.PRICE_MIDPOINT, PriceMode.PRICE_BUY_PERCENTILE, PriceMode.PRICE_BUY_AVG};
			}
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().sourceEveAddicts();
			}
		};
		private final PricingFetch pricingFetch;
		private final boolean supportsMultipleRegions;
		private final boolean supportsSingleRegion;
		private final boolean supportsSystem;
		private final boolean supportsStation;

		private PriceSource(final PricingFetch pricingFetch,
				final boolean supportsMultipleRegions,
				final boolean supportsSingleRegion,
				final boolean supportsSystem,
				final boolean supportsStation) {
			this.pricingFetch = pricingFetch;
			this.supportsMultipleRegions = supportsMultipleRegions;
			this.supportsSingleRegion = supportsSingleRegion;
			this.supportsSystem = supportsSystem;
			this.supportsStation = supportsStation;
		}

		public abstract PriceMode[] getPriceTypes();
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}

		public PricingFetch getPricingFetch() {
			return pricingFetch;
		}

		public boolean supportsMultipleRegions() {
			return supportsMultipleRegions;
		}

		public boolean supportsSingleRegion() {
			return supportsSingleRegion;
		}

		public boolean supportsStation() {
			return supportsStation;
		}

		public boolean supportsSystem() {
			return supportsSystem;
		}
	}

	public enum RegionType {
		NOT_CONFIGURABLE() {
			@Override String getI18N() {
				return DialoguesSettings.get().notConfigurable();
			}
			@Override public List<Long> getRegions() {
				return new ArrayList<Long>();
			}
		},
		EMPIRE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionEmpire();
			}
			@Override public List<Long> getRegions() {
				List<Long> regions = new ArrayList<Long>();
				//Amarr
				regions.add(10000054L); //Amarr: Aridia
				regions.add(10000036L); //Amarr: Devoid
				regions.add(10000043L); //Amarr: Domain
				regions.add(10000067L); //Amarr: Genesis
				regions.add(10000052L); //Amarr: Kador
				regions.add(10000065L); //Amarr: Kor-Azor
				regions.add(10000020L); //Amarr: Tash-Murkon
				regions.add(10000038L); //Amarr: The Bleak Lands
				//Caldari
				regions.add(10000069L); //Caldari: Black Rise
				regions.add(10000016L); //Caldari: Lonetrek
				regions.add(10000033L); //Caldari: The Citadel
				regions.add(10000002L); //Caldari: The Forge
				//Gallente
				regions.add(10000064L); //Gallente: Essence
				regions.add(10000037L); //Gallente: Everyshore
				regions.add(10000048L); //Gallente: Placid
				regions.add(10000032L); //Gallente: Sinq Laison
				regions.add(10000044L); //Gallente: Solitude
				regions.add(10000068L); //Gallente: Verge Vendor
				//Minmatar
				regions.add(10000042L); //Minmatar : Metropolis
				regions.add(10000030L); //Minmatar : Heimatar
				regions.add(10000028L); //Minmatar : Molden Heath
				//Others
				regions.add(10000001L); //Ammatar: Derelik
				regions.add(10000049L); //Khanid: Khanid
				return regions;
			}
		},
		MARKET_HUBS() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionMarketHubs();
			}
			@Override public List<Long> getRegions() {
				List<Long> regions = new ArrayList<Long>();
				regions.add(10000002L); //Caldari: The Forge (Jita)
				regions.add(10000042L); //Minmatar : Metropolis (Hek)
				regions.add(10000030L); //Minmatar : Heimatar (Rens)
				regions.add(10000064L); //Gallente: Essence (Oursalert)
				regions.add(10000043L); //Amarr: Domain (Amarr)
				return regions;
			}
		},
		ALL_AMARR() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionAllAmarr();
			}
			@Override public List<Long> getRegions() {
				List<Long> regions = new ArrayList<Long>();
				regions.add(10000054L); //Amarr: Aridia
				regions.add(10000036L); //Amarr: Devoid
				regions.add(10000043L); //Amarr: Domain
				regions.add(10000067L); //Amarr: Genesis
				regions.add(10000052L); //Amarr: Kador
				regions.add(10000065L); //Amarr: Kor-Azor
				regions.add(10000020L); //Amarr: Tash-Murkon
				regions.add(10000038L); //Amarr: The Bleak Lands
				return regions;
			}
		},
		ALL_GALLENTE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionAllGallente();
			}
			@Override public List<Long> getRegions() {
				List<Long> regions = new ArrayList<Long>();
				regions.add(10000064L); //Gallente: Essence
				regions.add(10000037L); //Gallente: Everyshore
				regions.add(10000048L); //Gallente: Placid
				regions.add(10000032L); //Gallente: Sinq Laison
				regions.add(10000044L); //Gallente: Solitude
				regions.add(10000068L); //Gallente: Verge Vendor
				return regions;
			}
		},
		ALL_MINMATAR() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionAllMinmatar();
			}
			@Override public List<Long> getRegions() {
				List<Long> regions = new ArrayList<Long>();
				regions.add(10000042L); //Minmatar : Metropolis
				regions.add(10000030L); //Minmatar : Heimatar
				regions.add(10000028L); //Minmatar : Molden Heath
				return regions;
			}
		},
		ALL_CALDARI() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionAllCaldari();
			}
			@Override public List<Long> getRegions() {
				List<Long> regions = new ArrayList<Long>();
				regions.add(10000069L); //Caldari: Black Rise
				regions.add(10000016L); //Caldari: Lonetrek
				regions.add(10000033L); //Caldari: The Citadel
				regions.add(10000002L); //Caldari: The Forge
				return regions;
			}
		},
		ARIDIA() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionAridia();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000054L); //Amarr: Aridia
			}
		},
		DEVOID() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionDevoid();
			}

			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000036L); //Amarr: Devoid
			}
		},
		DOMAIN() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionDomain();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000043L); //Amarr: Domain
			}
		},
		GENESIS() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionGenesis();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000067L); //Amarr: Genesis
			}
		},
		KADOR() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionKador();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000052L); //Amarr: Kador
			}
		},
		KOR_AZOR() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionKorAzor();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000065L); //Amarr: Kor-Azor
			}
		},
		TASH_MURKON() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionTashMurkon();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000020L); //Amarr: Tash-Murkon
			}
		},
		THE_BLEAK_LANDS() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionTheBleakLands();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000038L); //Amarr: The Bleak Lands
			}
		},
		BLACK_RISE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionBlackRise();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000069L); //Caldari: Black Rise
			}
		},
		LONETREK() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionLonetrek();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000016L); //Caldari: Lonetrek
			}
		},
		THE_CITADEL() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionTheCitadel();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000033L); //Caldari: The Citadel
			}
		},
		THE_FORGE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionTheForge();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000002L); //Caldari: The Forge
			}
		},
		ESSENCE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionEssence();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000064L); //Gallente: Essence
			}
		},
		EVERYSHORE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionEveryshore();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000037L); //Gallente: Everyshore
			}
		},
		PLACID() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionPlacid();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000048L); //Gallente: Placid
			}
		},
		SINQ_LAISON() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionSinqLaison();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000032L); //Gallente: Sinq Laison
			}
		},
		SOLITUDE() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionSolitude();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000044L); //Gallente: Solitude
			}
		},
		VERGE_VENDOR() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionVergeVendor();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000068L); //Gallente: Verge Vendor
			}
		},
		METROPOLIS() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionMetropolis();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000042L); //Minmatar : Metropolis
			}
		},
		HEIMATAR() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionHeimatar();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000030L); //Minmatar : Heimatar
			}
		},
		MOLDEN_HEATH() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionMoldenHeath();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000028L); //Minmatar : Molden Heath
			}
		},
		DERELIK() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionDerelik();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000001L); //Ammatar: Derelik
			}
		},
		KHANID() {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().regionKhanid();
			}
			@Override public List<Long> getRegions() {
				return Collections.singletonList(10000049L); //Khanid: Khanid
			}
		};
		private static List<RegionType> singleLocations = null;
		private static List<RegionType> multipleLocations = null;

		abstract String getI18N();
		public abstract List<Long> getRegions();

		@Override
		public String toString() {
			return getI18N();
		}
		public static List<RegionType> getSingleLocations() {
			if (singleLocations == null) {
				singleLocations = new ArrayList<RegionType>();
				for (RegionType regionType : RegionType.values()) {
					if (regionType.getRegions().size() == 1) {
						singleLocations.add(regionType);
					}
				}
			}
			return singleLocations;
		}
		public static List<RegionType> getMultipleLocations() {
			if (multipleLocations == null) {
				multipleLocations = new ArrayList<RegionType>();
				for (RegionType regionType : RegionType.values()) {
					if (!regionType.getRegions().isEmpty()) { //Ignore NOT_CONFIGURABLE
						multipleLocations.add(regionType);
					}
				}
			}
			return multipleLocations;
		}
	}

	public enum PriceMode {
		PRICE_SELL_MAX(PricingType.HIGH, PricingNumber.SELL) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceSellMax();
			}
		},
		PRICE_SELL_AVG(PricingType.MEAN, PricingNumber.SELL) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceSellAvg();
			}
		},
		PRICE_SELL_MEDIAN(PricingType.MEDIAN, PricingNumber.SELL) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceSellMedian();
			}
		},
		PRICE_SELL_PERCENTILE(PricingType.PERCENTILE, PricingNumber.SELL) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceSellPercentile();
			}
		},
		PRICE_SELL_MIN(PricingType.LOW, PricingNumber.SELL) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceSellMin();
			}
		},
		PRICE_MIDPOINT(null, null) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceMidpoint();
			}
		},
		PRICE_BUY_MAX(PricingType.HIGH, PricingNumber.BUY) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceBuyMax();
			}
		},
		PRICE_BUY_PERCENTILE(PricingType.PERCENTILE, PricingNumber.BUY) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceBuyPercentile();
			}
		},
		PRICE_BUY_AVG(PricingType.MEAN, PricingNumber.BUY) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceBuyAvg();
			}
		},
		PRICE_BUY_MEDIAN(PricingType.MEDIAN, PricingNumber.BUY) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceBuyMedian();
			}
		},
		PRICE_BUY_MIN(PricingType.LOW, PricingNumber.BUY) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceBuyMin();
			}
		};
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}

		PricingType pricingType;
		PricingNumber pricingNumber;

		private PriceMode(PricingType pricingType, PricingNumber pricingNumber) {
			this.pricingType = pricingType;
			this.pricingNumber = pricingNumber;
		}

		public PricingType getPricingType() {
			return pricingType;
		}

		public PricingNumber getPricingNumber() {
			return pricingNumber;
		}

		public static void setDefaultPrice(final PriceData priceData, final PriceMode priceMode, final double price) {
			if (priceData != null) {
				if (priceMode == PriceMode.PRICE_SELL_MAX) {
					priceData.setSellMax(price);
				}
				if (priceMode == PriceMode.PRICE_SELL_AVG) {
					priceData.setSellAvg(price);
				}
				if (priceMode == PriceMode.PRICE_SELL_MEDIAN) {
					priceData.setSellMedian(price);
				}
				if (priceMode == PriceMode.PRICE_SELL_PERCENTILE) {
					priceData.setSellPercentile(price);
				}
				if (priceMode == PriceMode.PRICE_SELL_MIN) {
					priceData.setSellMin(price);
				}
				if (priceMode == PriceMode.PRICE_MIDPOINT) {
					//Ignore calculated prices
				}
				if (priceMode == PriceMode.PRICE_BUY_MAX) {
					priceData.setBuyMax(price);
				}
				if (priceMode == PriceMode.PRICE_BUY_AVG) {
					priceData.setBuyAvg(price);
				}
				if (priceMode == PriceMode.PRICE_BUY_MEDIAN) {
					priceData.setBuyMedian(price);
				}
				if (priceMode == PriceMode.PRICE_BUY_PERCENTILE) {
					priceData.setBuyPercentile(price);
				}
				if (priceMode == PriceMode.PRICE_BUY_MIN) {
					priceData.setBuyMin(price);
				}
			} 
		}

		private static double getDefaultPrice(final PriceData priceData, final PriceMode priceMode) {
			if (priceData != null) {
				if (priceMode == PriceMode.PRICE_SELL_MAX) {
					return priceData.getSellMax();
				}
				if (priceMode == PriceMode.PRICE_SELL_AVG) {
					return priceData.getSellAvg();
				}
				if (priceMode == PriceMode.PRICE_SELL_MEDIAN) {
					return priceData.getSellMedian();
				}
				if (priceMode == PriceMode.PRICE_SELL_PERCENTILE) {
					return priceData.getSellPercentile();
				}
				if (priceMode == PriceMode.PRICE_SELL_MIN) {
					return priceData.getSellMin();
				}
				if (priceMode == PriceMode.PRICE_MIDPOINT) {
					return (priceData.getSellMin() + priceData.getBuyMax()) / 2;
				}
				if (priceMode == PriceMode.PRICE_BUY_MAX) {
					return priceData.getBuyMax();
				}
				if (priceMode == PriceMode.PRICE_BUY_AVG) {
					return priceData.getBuyAvg();
				}
				if (priceMode == PriceMode.PRICE_BUY_MEDIAN) {
					return priceData.getBuyMedian();
				}
				if (priceMode == PriceMode.PRICE_BUY_PERCENTILE) {
					return priceData.getBuyPercentile();
				}
				if (priceMode == PriceMode.PRICE_BUY_MIN) {
					return priceData.getBuyMin();
				}
			}
			return 0;
		}

		public static PriceMode getDefaultPriceType() {
			return PriceMode.PRICE_MIDPOINT;
		}
	}

	//Default
	private final LocationType locationType;
	private final List<Long> locations;
	private final PriceSource priceSource;
	private PriceMode priceType;
	private PriceMode priceReprocessedType;

	public PriceDataSettings() {
		locationType = LocationType.REGION;
		locations = getDefaultRegionType().getRegions();
		priceSource = getDefaultPriceSource();
		priceType = PriceMode.getDefaultPriceType();
		priceReprocessedType =  PriceMode.getDefaultPriceType();
	}

	public PriceDataSettings(final LocationType locationType, final List<Long> locations, final PriceSource priceSource, final PriceMode priceType, final PriceMode priceReprocessedType) {
		if (locationType != null && locations != null && !locations.isEmpty()) {
			this.locationType = locationType;
			this.locations = locations;
		} else {
			this.locationType = LocationType.REGION;
			this.locations = getDefaultRegionType().getRegions();
		}
		this.priceSource = priceSource;
		this.priceType = priceType;
		this.priceReprocessedType = priceReprocessedType;
	}

	public PriceSource getSource() {
		return priceSource;
	}

	public List<Long> getLocations() {
		return locations;
	}

	public LocationType getLocationType() {
		return locationType;
	}

	public double getDefaultPrice(final PriceData priceData) {
		return PriceMode.getDefaultPrice(priceData, priceType);
	}

	public double getDefaultPriceReprocessed(final PriceData priceData) {
		return PriceMode.getDefaultPrice(priceData, priceReprocessedType);
	}

	public static RegionType getDefaultRegionType() {
		return RegionType.THE_FORGE;
	}

	public static PriceSource getDefaultPriceSource() {
		return PriceSource.EVE_CENTRAL;
	}

	public void setPriceType(final PriceMode priceSource) {
		this.priceType = priceSource;
	}

	public void setPriceReprocessedType(final PriceMode reprocessedPriceType) {
		this.priceReprocessedType = reprocessedPriceType;
	}

	public PriceMode getPriceType() {
		return priceType;
	}

	public PriceMode getPriceReprocessedType() {
		return priceReprocessedType;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PriceDataSettings other = (PriceDataSettings) obj;
		if (this.locationType != other.locationType) {
			return false;
		}
		if (this.locations != other.locations && (this.locations == null || !this.locations.equals(other.locations))) {
			return false;
		}
		if (this.priceSource != other.priceSource) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 19 * hash + (this.locationType != null ? this.locationType.hashCode() : 0);
		hash = 19 * hash + (this.locations != null ? this.locations.hashCode() : 0);
		hash = 19 * hash + (this.priceSource != null ? this.priceSource.hashCode() : 0);
		return hash;
	}
}
