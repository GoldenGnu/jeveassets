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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static net.nikr.eve.jeveasset.data.settings.Settings.getNow;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class ManufacturingSettings {

	public static enum ManufacturingFacility {
		STATION(0,0) {
			@Override
			public String getText() {
				return DialoguesSettings.get().manufacturingFacilityStation();
			}
		},
		ENGINEERING_COMPLEX_MEDIUM(1, 3){
			@Override
			public String getText() {
				return DialoguesSettings.get().manufacturingFacilityMedium();
			}
		},
		ENGINEERING_COMPLEX_LARGE(1, 4){
			@Override
			public String getText() {
				return DialoguesSettings.get().manufacturingFacilityLarge();
			}
		},
		ENGINEERING_COMPLEX_XLARGE(1, 5){
			@Override
			public String getText() {
				return DialoguesSettings.get().manufacturingFacilityXLarge();
			}
		};

		private final double materialBonus;
		private final double feeBonus;

		private ManufacturingFacility(double materialBonus, double feeBonus) {
			this.materialBonus = materialBonus;
			this.feeBonus = feeBonus;
		}

		public abstract String getText();

		@Override
		public String toString() {
			return getText();
		}

		public double getMaterialBonus() {
			return materialBonus;
		}

		public double getFeeBonus() {
			return feeBonus;
		}

		public static ManufacturingFacility getDefault() {
			return ENGINEERING_COMPLEX_MEDIUM;
		}
	}

	public static enum ManufacturingRigs {
		NONE(0) {
			@Override
			public String getText() {
				return DialoguesSettings.get().manufacturingRigsNone();
			}
		},
		T1(2.0) {
			@Override
			public String getText() {
				return DialoguesSettings.get().manufacturingRigsT1();
			}
		},
		T2(2.40) {
			@Override
			public String getText() {
				return DialoguesSettings.get().manufacturingRigsT2();
			}
		};

		private final double materialBonus;

		private ManufacturingRigs(double materialBonus) {
			this.materialBonus = materialBonus;
		}

		public abstract String getText();

		@Override
		public String toString() {
			return getText();
			//return getText() + " (" + materialBonus + "%)";
		}

		public double getMaterialBonus() {
			return materialBonus;
		}

		public static ManufacturingRigs getDefault() {
			return NONE;
		}
	}

	public static enum ManufacturingSecurity {
		HIGHSEC(1) {
			@Override
			public String getText() {
				return DialoguesSettings.get().manufacturingSecurityHighSec();
			}
		},
		LOWSEC(1.9) {
			@Override
			public String getText() {
				return DialoguesSettings.get().manufacturingSecurityLowSec();
			}
		},
		NULLSEC(2.1) {
			@Override
			public String getText() {
				return DialoguesSettings.get().manufacturingSecurityNullSec();
			}
		};

		private final double rigBonus;

		private ManufacturingSecurity(double rigBonus) {
			this.rigBonus = rigBonus;
		}

		public abstract String getText();

		public double getRigBonus() {
			return rigBonus;
		}

		@Override
		public String toString() {
			return getText();
			//return getText() + " (" + rigBonus + "%)";
		}

		public static ManufacturingSecurity getDefault() {
			return HIGHSEC;
		}
	}

	private Map<Integer, Double> prices = new HashMap<>(); //Adjusted Prices
	private Map<Integer, Float> systems = new HashMap<>(); //System Indexs
	private Date nextUpdate = getNow();
	//Defaults
	private int systemID = 30000142; //Jita for now
	private int materialEfficiency = 0;
	private ManufacturingFacility facility = ManufacturingFacility.getDefault();
	private ManufacturingRigs rigs = ManufacturingRigs.getDefault();
	private ManufacturingSecurity security = ManufacturingSecurity.getDefault();
	private double tax = 0;

	public Map<Integer, Double> getPrices() {
		return prices;
	}

	public void setPrices(Map<Integer, Double> prices) {
		this.prices = prices;
	}

	public Map<Integer, Float> getSystems() {
		return systems;
	}

	public void setSystems(Map<Integer, Float> systems) {
		this.systems = systems;
	}

	public Date getNextUpdate() {
		return nextUpdate;
	}

	public boolean isSystemsNeedsUpdating() {
		return systems.isEmpty() 
				|| Settings.getNow().after(new Date(nextUpdate.getTime() + TimeUnit.HOURS.toMillis(23))); //Last updated 1 day ago
	}

	public void setNextUpdate(Date nextUpdate) {
		this.nextUpdate = nextUpdate;
	}

	public int getSystemID() {
		return systemID;
	}

	public void setSystemID(int systemID) {
		this.systemID = systemID;
	}

	public int getMaterialEfficiency() {
		return materialEfficiency;
	}

	public void setMaterialEfficiency(int materialEfficiency) {
		this.materialEfficiency = materialEfficiency;
	}

	public ManufacturingFacility getFacility() {
		return facility;
	}

	public void setFacility(ManufacturingFacility facility) {
		this.facility = facility;
	}

	public ManufacturingRigs getRigs() {
		return rigs;
	}

	public void setRigs(ManufacturingRigs rigs) {
		this.rigs = rigs;
	}

	public ManufacturingSecurity getSecurity() {
		return security;
	}

	public void setSecurity(ManufacturingSecurity security) {
		this.security = security;
	}

	public double getTax() {
		return tax;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}
}
