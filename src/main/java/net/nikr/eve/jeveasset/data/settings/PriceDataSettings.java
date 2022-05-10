/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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

import java.util.Objects;
import javax.swing.Icon;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DataModelPriceDataSettings;
import uk.me.candle.eve.pricing.options.LocationType;
import uk.me.candle.eve.pricing.options.PricingFetch;
import uk.me.candle.eve.pricing.options.PricingNumber;
import uk.me.candle.eve.pricing.options.PricingType;


public class PriceDataSettings {
	
	public enum PriceSource {
		EVEMARKETER(PricingFetch.EVEMARKETER, true, true, false, LocationType.REGION, 10000002L, Images.LINK_EVEMARKETER.getIcon()) {
			@Override public PriceMode[] getPriceTypes() {
				return PriceMode.values();
			}
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().sourceEvemarketer();
			}
		},
		FUZZWORK(PricingFetch.FUZZWORK, true, false, true, LocationType.REGION, 10000002L, Images.LINK_FUZZWORK.getIcon()) {
			@Override public PriceMode[] getPriceTypes() {
				return PriceMode.values();
			}
			@Override String getI18N() {
				return DataModelPriceDataSettings.get().sourceFuzzwork();
			}
		},
		;
		private final PricingFetch pricingFetch;
		private final boolean supportsRegion;
		private final boolean supportsSystem;
		private final boolean supportsStation;
		private final LocationType defaultLocationType;
		private final Long defaultLocationID;
		private final Icon icon;

		private PriceSource(final PricingFetch pricingFetch,
				final boolean supportsRegion,
				final boolean supportsSystem,
				final boolean supportsStation,
				final LocationType defaultLocationType,
				final Long defaultLocationID,
				final Icon icon) {
			this.pricingFetch = pricingFetch;
			this.supportsRegion = supportsRegion;
			this.supportsSystem = supportsSystem;
			this.supportsStation = supportsStation;
			this.defaultLocationType = defaultLocationType;
			this.defaultLocationID = defaultLocationID;
			this.icon = icon;
		}

		public abstract PriceMode[] getPriceTypes();
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
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

		public boolean supportsRegion() {
			return supportsRegion;
		}

		public boolean supportsStation() {
			return supportsStation;
		}

		public boolean supportsSystem() {
			return supportsSystem;
		}

		public Icon getIcon() {
			return icon;
		}

		public boolean isValid(LocationType locationType, Long locationID) {
			if (null != locationType && locationID != null) {
				switch (locationType) {
					case REGION:
						return supportsRegion();
					case SYSTEM:
						return supportsSystem();
					case STATION:
						return supportsStation();
				}
			}
			return false; //Should never happen
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

	public PriceDataSettings() {
		locationType = LocationType.REGION;
		locationID = getDefaultLocationID();
		priceSource = getDefaultPriceSource();
		priceType = PriceMode.getDefaultPriceType();
		priceReprocessedType = PriceMode.getDefaultPriceType();
	}

	public PriceDataSettings(final LocationType locationType, final Long locationID, final PriceSource priceSource, final PriceMode priceType, final PriceMode priceReprocessedType) {
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

	public static Long getDefaultLocationID() {
		return 10000002L;
	}

	public static PriceSource getDefaultPriceSource() {
		return PriceSource.EVEMARKETER;
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
