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
package net.nikr.eve.jeveasset.gui.tabs.jobs;

import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import java.util.Date;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.types.BlueprintType;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.data.types.PriceType;
import net.nikr.eve.jeveasset.i18n.DataModelIndustryJob;


public class IndustryJob extends ApiIndustryJob implements Comparable<IndustryJob>, LocationType, ItemType, BlueprintType, PriceType {

	public enum IndustryJobState {
		STATE_ALL() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateAll();
			}
		},
		STATE_NOT_DELIVERED() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateNotDelivered();
			}
		},
		STATE_DELIVERED() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateDelivered();
			}
		},
		STATE_FAILED() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateFailed();
			}
		},
		STATE_READY() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateReady();
			}
		},
		STATE_ACTIVE() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateActive();
			}
		},
		STATE_PENDING() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().statePending();
			}
		},
		STATE_ABORTED() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateAborted();
			}
		},
		STATE_GM_ABORTED() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateGmAborted();
			}
		},
		STATE_IN_FLIGHT() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateInFlight();
			}
		},
		STATE_DESTROYED() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateDestroyed();
			}
		};
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}
	}

	public enum IndustryActivity {
		ACTIVITY_ALL() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().activityAll();
			}
		},
		ACTIVITY_NONE() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().activityNone();
			}
		},
		ACTIVITY_MANUFACTURING() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().activityManufacturing();
			}
		},
		ACTIVITY_RESEARCHING_TECHNOLOGY() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().activityResearchingTechnology();
			}
		},
		ACTIVITY_RESEARCHING_TIME_PRODUCTIVITY() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().activityResearchingTimeProductivity();
			}
		},
		ACTIVITY_RESEARCHING_METERIAL_PRODUCTIVITY() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().activityResearchingMeterialProductivity();
			}
		},
		ACTIVITY_COPYING() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().activityCopying();
			}
			@Override
			public String getDescriptionOf(final IndustryJob job) {
				// "Copying: Xyz Blueprint making 5 copies with 1500 runs each."
				return DataModelIndustryJob.get().descriptionCopying(
						String.valueOf(job.getInstalledItemTypeID()),
						job.getRuns(),
						job.getLicensedProductionRuns()
						);
			}
		},
		ACTIVITY_DUPLICATING() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().activityDuplicating();
			}
		},
		ACTIVITY_REVERSE_ENGINEERING() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().activityReverseEngineering();
			}
		},
		ACTIVITY_REVERSE_INVENTION() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().activityReverseInvention();
			}
		};
		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}
		/**
		 *
		 * @param job
		 * @return a single line, human readable description of the job.
		 */
		public String getDescriptionOf(final IndustryJob job) {
			return toString();
		}
	}

	private IndustryActivity activity;
	private IndustryJobState state;
	private final Item item;
	private final Owner owner;
	private final Location location;
	private final int portion;
	private double price;
	private double outputValue;
	private int outputCount;

	public IndustryJob(final ApiIndustryJob apiIndustryJob, final Item item, final Location location, final Owner owner, final int portion) {
		this.setJobID(apiIndustryJob.getJobID());
		this.setContainerID(apiIndustryJob.getContainerID());
		this.setInstalledItemID(apiIndustryJob.getInstalledItemID());
		this.setInstalledItemLocationID(apiIndustryJob.getInstalledItemLocationID());
		this.setInstalledItemQuantity(apiIndustryJob.getInstalledItemQuantity());
		this.setInstalledItemProductivityLevel(apiIndustryJob.getInstalledItemProductivityLevel());
		this.setInstalledItemMaterialLevel(apiIndustryJob.getInstalledItemMaterialLevel());
		this.setInstalledItemLicensedProductionRunsRemaining(apiIndustryJob.getInstalledItemLicensedProductionRunsRemaining());
		this.setOutputLocationID(apiIndustryJob.getOutputLocationID());
		this.setInstallerID(apiIndustryJob.getInstallerID());
		this.setRuns(apiIndustryJob.getRuns());
		this.setAssemblyLineID(apiIndustryJob.getAssemblyLineID());
		this.setLicensedProductionRuns(apiIndustryJob.getLicensedProductionRuns());
		this.setInstalledInSolarSystemID(apiIndustryJob.getInstalledInSolarSystemID());
		this.setContainerLocationID(apiIndustryJob.getContainerLocationID());
		this.setMaterialMultiplier(apiIndustryJob.getMaterialMultiplier());
		this.setCharMaterialMultiplier(apiIndustryJob.getCharMaterialMultiplier());
		this.setTimeMultiplier(apiIndustryJob.getTimeMultiplier());
		this.setCharTimeMultiplier(apiIndustryJob.getCharTimeMultiplier());
		this.setInstalledItemTypeID(apiIndustryJob.getInstalledItemTypeID()); //Fixed
		this.setOutputTypeID(apiIndustryJob.getOutputTypeID());
		this.setContainerTypeID(apiIndustryJob.getContainerTypeID());
		this.setInstalledItemCopy(apiIndustryJob.getInstalledItemCopy());
		this.setCompleted(apiIndustryJob.isCompleted());
		this.setCompletedSuccessfully(apiIndustryJob.isCompletedSuccessfully());
		this.setInstalledItemFlag(apiIndustryJob.getInstalledItemFlag());
		this.setOutputFlag(apiIndustryJob.getOutputFlag());
		this.setActivityID(apiIndustryJob.getActivityID());
		this.setCompletedStatus(apiIndustryJob.getCompletedStatus());
		this.setInstallTime(apiIndustryJob.getInstallTime());
		this.setBeginProductionTime(apiIndustryJob.getBeginProductionTime());
		this.setEndProductionTime(apiIndustryJob.getEndProductionTime());
		this.setPauseProductionTime(apiIndustryJob.getPauseProductionTime());
		this.item = item;
		this.location = location;
		this.owner = owner;
		this.portion = portion;

		switch (this.getActivityID()) {
			case 0:
				activity = IndustryActivity.ACTIVITY_NONE;
				break;
			case 1:
				activity = IndustryActivity.ACTIVITY_MANUFACTURING;
				break;
			case 2:
				activity = IndustryActivity.ACTIVITY_RESEARCHING_TECHNOLOGY;
				break;
			case 3:
				activity = IndustryActivity.ACTIVITY_RESEARCHING_TIME_PRODUCTIVITY;
				break;
			case 4:
				activity = IndustryActivity.ACTIVITY_RESEARCHING_METERIAL_PRODUCTIVITY;
				break;
			case 5:
				activity = IndustryActivity.ACTIVITY_COPYING;
				break;
			case 6:
				activity = IndustryActivity.ACTIVITY_DUPLICATING;
				break;
			case 7:
				activity = IndustryActivity.ACTIVITY_REVERSE_ENGINEERING;
				break;
			case 8:
				activity = IndustryActivity.ACTIVITY_REVERSE_INVENTION;
				break;
		}
		Date start = this.getBeginProductionTime();
		Date end = this.getEndProductionTime();
		switch (this.getCompletedStatus()) {
			case 0:
				if (this.isCompleted()) {
					state = IndustryJobState.STATE_FAILED;
				} else if (start.before(Settings.getNow())) {
					if (end.before(Settings.getNow())) {
						state = IndustryJobState.STATE_READY;
					} else {
						state = IndustryJobState.STATE_ACTIVE;
					}
				} else {
					state = IndustryJobState.STATE_PENDING;
				}
				break;
			case 1:
				state = IndustryJobState.STATE_DELIVERED;
				break;
			case 2:
				state = IndustryJobState.STATE_ABORTED;
				break;
			case 3:
				state = IndustryJobState.STATE_GM_ABORTED;
				break;
			case 4:
				state = IndustryJobState.STATE_IN_FLIGHT;
				break;
			case 5:
				state = IndustryJobState.STATE_DESTROYED;
				break;
		}
		switch(activity) {
			case ACTIVITY_MANUFACTURING:
				outputCount = getRuns() * portion;
				break;
			case ACTIVITY_COPYING:
				outputCount = getRuns();
				break;
			default:
				outputCount = 1;
				break;
		}
	}

	@Override
	public int compareTo(final IndustryJob o) {
		return 0;
	}

	public IndustryActivity getActivity() {
		return activity;
	}

	public IndustryJobState getState() {
		return state;
	}

	public void setDynamicPrice(double price) {
		this.price = price;
	}

	@Override
	public Double getDynamicPrice() {
		return price;
	}

	public double getOutputValue() {
		return outputValue;
	}

	public void setOutputPrice(double outputPrice) {
		if (getState() == IndustryJobState.STATE_ACTIVE && getActivity() == IndustryActivity.ACTIVITY_MANUFACTURING){
			this.outputValue = outputPrice * (double) getRuns() * getPortion();
		} else {
			this.outputValue = 0;
		}
	}

	public int getOutputCount() {
		return outputCount;
	}

	@Override
	public boolean isBPO() {
		return !isBPC();
	}

	@Override
	public boolean isBPC() {
		return getInstalledItemCopy() > 0;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	public String getOwner() {
		return owner.getName();
	}

	public long getOwnerID() {
		return owner.getOwnerID();
	}

	public int getPortion() {
		return portion;
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + (this.owner != null ? this.owner.hashCode() : 0);
		hash = 37 * hash + (int) (this.getJobID() ^ (this.getJobID() >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final IndustryJob other = (IndustryJob) obj;
		if (this.owner != other.owner && (this.owner == null || !this.owner.equals(other.owner))) {
			return false;
		}
		return this.getJobID() == other.getJobID();
	}
}
