/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.Icon;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DataModelPriceDataSettings;
import uk.me.candle.eve.pricing.options.LocationType;
import uk.me.candle.eve.pricing.options.PriceType;
import uk.me.candle.eve.pricing.options.PricingFetch;


public class PriceDataSettings {

	public enum PriceSource {
		FUZZWORK(PricingFetch.FUZZWORK, LocationType.REGION, 10000002L, Images.LINK_FUZZWORK.getIcon()) {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().sourceFuzzwork();
			}
		},
		/*
		EVE_TYCOON(PricingFetch.EVE_TYCOON, LocationType.REGION, 10000002L, Images.LINK_EVE_TYCOON.getIcon()) {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().sourceEveTycoon();
			}
		},
		*/
		JANICE(PricingFetch.JANICE, LocationType.STATION, 1L, Images.LINK_JANICE.getIcon()) {
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().sourceJanice();
			}
		},
		;
		private final PricingFetch pricingFetch;
		private final LocationType defaultLocationType;
		private final Long defaultLocationID;
		private final Icon icon;

		private PriceSource(final PricingFetch pricingFetch,
				final LocationType defaultLocationType,
				final Long defaultLocationID,
				final Icon icon) {
			this.pricingFetch = pricingFetch;
			this.defaultLocationType = defaultLocationType;
			this.defaultLocationID = defaultLocationID;
			this.icon = icon;
		}

		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}

		public List<PriceMode> getSupportedPriceModes() {
			List<PriceMode> priceModes = new ArrayList<>();
			for (PriceMode priceMode : PriceMode.values()) {
				if (pricingFetch.getSupportedPricingTypes().contains(priceMode.getPricingType())) {
					priceModes.add(priceMode);
				} else if (priceMode == PriceMode.PRICE_MIDPOINT
						&& pricingFetch.getSupportedPricingTypes().contains(PriceType.SELL_LOW)
						&& pricingFetch.getSupportedPricingTypes().contains(PriceType.BUY_HIGH)
						) {
					priceModes.add(priceMode);
				}
			}
			return priceModes;
		}

		public LocationType getDefaultLocationType() {
			return defaultLocationType;
		}

		public Long getDefaultLocationID() {
			return defaultLocationID;
		}

		public PricingFetch getPricingFetch() {
			return pricingFetch;
		}

		public boolean supportRegions() {
			return pricingFetch.getSupportedLocationTypes().contains(LocationType.REGION);
		}

		public boolean supportStations() {
			return pricingFetch.getSupportedLocationTypes().contains(LocationType.STATION);
		}

		public boolean supportSystems() {
			return pricingFetch.getSupportedLocationTypes().contains(LocationType.SYSTEM);
		}

		public Icon getIcon() {
			return icon;
		}

		public boolean isValid(LocationType locationType, Long locationID) {
			if (null != locationType && locationID != null) {
				switch (locationType) {
					case REGION:
						return supportRegions();
					case SYSTEM:
						return supportSystems();
					case STATION:
						return supportStations();
				}
			}
			return false; //Should never happen
		}
	}

	public enum PriceMode {
		PRICE_SELL_MAX(PriceType.SELL_HIGH) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceSellMax();
			}
		},
		PRICE_SELL_AVG(PriceType.SELL_MEAN) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceSellAvg();
			}
		},
		PRICE_SELL_MEDIAN(PriceType.SELL_MEDIAN) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceSellMedian();
			}
		},
		PRICE_SELL_PERCENTILE(PriceType.SELL_PERCENTILE) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceSellPercentile();
			}
		},
		PRICE_SELL_MIN(PriceType.SELL_LOW) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceSellMin();
			}
		},
		PRICE_MIDPOINT(null) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceMidpoint();
			}
		},
		PRICE_BUY_MAX(PriceType.BUY_HIGH) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceBuyMax();
			}
		},
		PRICE_BUY_PERCENTILE(PriceType.BUY_PERCENTILE) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceBuyPercentile();
			}
		},
		PRICE_BUY_AVG(PriceType.BUY_MEAN) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceBuyAvg();
			}
		},
		PRICE_BUY_MEDIAN(PriceType.BUY_MEDIAN) {
			@Override
			String getI18N() {
				return DataModelPriceDataSettings.get().priceBuyMedian();
			}
		},
		PRICE_BUY_MIN(PriceType.BUY_LOW) {
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

		PriceType priceType;

		private PriceMode(PriceType priceType) {
			this.priceType = priceType;
		}

		public PriceType getPricingType() {
			return priceType;
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

		public static double getDefaultPrice(final PriceData priceData, final PriceMode priceMode) {
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
					if (priceData.getSellMin() > 0 && priceData.getBuyMax() > 0) { //Working as intended
						return (priceData.getSellMin() + priceData.getBuyMax()) / 2;
					} else if (priceData.getBuyMax() > 0) { //Using BuyMax (fallback)
						return priceData.getBuyMax();
					} else { //Using SellMin (fallback)
						return priceData.getSellMin(); //SellMin or Zero
					}
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
	private final Long locationID;
	private final PriceSource priceSource;
	private PriceMode priceType;
	private PriceMode priceReprocessedType;
	private PriceMode priceManufacturingType;
	private String janiceKey;

	public PriceDataSettings() {
		locationType = LocationType.REGION;
		locationID = getDefaultLocationID();
		priceSource = getDefaultPriceSource();
		priceType = PriceMode.getDefaultPriceType();
		priceReprocessedType = PriceMode.getDefaultPriceType();
		priceManufacturingType = PriceMode.getDefaultPriceType();
	}

	public PriceDataSettings(final LocationType locationType, final Long locationID, final PriceSource priceSource, final PriceMode priceType, final PriceMode priceReprocessedType, final PriceMode priceManufacturingType, final String janiceKey) {
		if (locationType != null && locationID != null) {
			this.locationType = locationType;
			this.locationID = locationID;
		} else {
			this.locationType = LocationType.REGION;
			this.locationID = getDefaultLocationID();
		}
		this.priceSource = priceSource;
		this.priceType = priceType;
		this.priceReprocessedType = priceReprocessedType;
		this.priceManufacturingType = priceManufacturingType;
		if (janiceKey == null) { //empty string instead of null
			this.janiceKey = "";
		} else {
			this.janiceKey = janiceKey;
		}
	}

	public PriceSource getSource() {
		return priceSource;
	}

	public Long getLocationID() {
		return locationID;
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

	public double getDefaultPriceManufacturing(final PriceData priceData) {
		return PriceMode.getDefaultPrice(priceData, priceManufacturingType);
	}

	public static Long getDefaultLocationID() {
		return 10000002L;
	}

	public static PriceSource getDefaultPriceSource() {
		return PriceSource.FUZZWORK;
	}

	public void setPriceType(final PriceMode priceSource) {
		this.priceType = priceSource;
	}

	public void setPriceReprocessedType(final PriceMode reprocessedPriceType) {
		this.priceReprocessedType = reprocessedPriceType;
	}

	public void setPriceManufacturingType(PriceMode priceManufacturingType) {
		this.priceManufacturingType = priceManufacturingType;
	}

	public PriceMode getPriceType() {
		return priceType;
	}

	public PriceMode getPriceReprocessedType() {
		return priceReprocessedType;
	}

	public PriceMode getPriceManufacturingType() {
		return priceManufacturingType;
	}

	public String getJaniceKey() {
		return janiceKey;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
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
		if (!Objects.equals(this.locationID, other.locationID)) {
			return false;
		}
		if (this.priceSource != other.priceSource) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 29 * hash + Objects.hashCode(this.locationType);
		hash = 29 * hash + Objects.hashCode(this.locationID);
		hash = 29 * hash + Objects.hashCode(this.priceSource);
		return hash;
	}
}
