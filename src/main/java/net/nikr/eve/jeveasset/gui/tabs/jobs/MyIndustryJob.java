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

import com.beimin.eveapi.model.shared.IndustryJob;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.data.types.PriceType;
import net.nikr.eve.jeveasset.i18n.DataModelIndustryJob;


public class MyIndustryJob extends IndustryJob implements Comparable<MyIndustryJob>, LocationType, ItemType, PriceType {

	public enum IndustryJobState {
		STATE_ALL() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateAll();
			}
		},
		STATE_PAUSED() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().statePaused();
			}
		},
		STATE_ACTIVE() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateActive();
			}
		},
		STATE_DONE() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateDone();
			}
		},
		STATE_FAILED() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateFailed();
			}
		},
		STATE_CANCELLED() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateCancelled();
			}
		},
		STATE_DELIVERED() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateDelivered();
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
			public String getDescriptionOf(final MyIndustryJob job) {
				// "Copying: Xyz Blueprint making 5 copies with 1500 runs each."
				return DataModelIndustryJob.get().descriptionCopying(
						String.valueOf(job.getBlueprintTypeID()),
						job.getRuns(),
						job.getLicensedRuns()
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
		public String getDescriptionOf(final MyIndustryJob job) {
			return toString();
		}
	}

	private IndustryActivity activity;
	private IndustryJobState state;
	private final Item item;
	private final Owner owner;
	private final MyLocation location;
	private final int portion;
	private final String name;
	private double price;
	private double outputValue;
	private int outputCount;
	private String installer;

	public MyIndustryJob(final IndustryJob apiIndustryJob, final Item item, final MyLocation location, final Owner owner, final int portion, final int productTypeID) {
		setJobID(apiIndustryJob.getJobID());
		setInstallerID(apiIndustryJob.getInstallerID());
		setInstallerName(apiIndustryJob.getInstallerName());
		setFacilityID(apiIndustryJob.getFacilityID());
		setSolarSystemID(apiIndustryJob.getSolarSystemID());
		setSolarSystemName(apiIndustryJob.getSolarSystemName());
		setStationID(apiIndustryJob.getStationID());
		setActivityID(apiIndustryJob.getActivityID());
		setBlueprintID(apiIndustryJob.getBlueprintID());
		setBlueprintTypeID(apiIndustryJob.getBlueprintTypeID());
		setBlueprintTypeName(apiIndustryJob.getBlueprintTypeName());
		setBlueprintLocationID(apiIndustryJob.getBlueprintLocationID());
		setOutputLocationID(apiIndustryJob.getOutputLocationID());
		setRuns(apiIndustryJob.getRuns());
		setCost(apiIndustryJob.getCost());
		setTeamID(apiIndustryJob.getTeamID());
		setLicensedRuns(apiIndustryJob.getLicensedRuns());
		setProbability(apiIndustryJob.getProbability());
		setProductTypeID(productTypeID);
		setProductTypeName(apiIndustryJob.getProductTypeName());
		setStatus(apiIndustryJob.getStatus());
		setTimeInSeconds(apiIndustryJob.getTimeInSeconds());
		setStartDate(apiIndustryJob.getStartDate());
		setEndDate(apiIndustryJob.getEndDate());
		setPauseDate(apiIndustryJob.getPauseDate());
		setCompletedDate(apiIndustryJob.getCompletedDate());
		setCompletedCharacterID(apiIndustryJob.getCompletedCharacterID());
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
		switch (getStatus()) {
			case 1: //Active
				if (getEndDate().before(Settings.getNow())) {
					state = IndustryJobState.STATE_DONE;
				} else {
					state = IndustryJobState.STATE_ACTIVE;
				}
				break;
			case 2:
				state = IndustryJobState.STATE_PAUSED;
				break;
			case 102:
				state = IndustryJobState.STATE_CANCELLED;
				break;
			case 104:
				state = IndustryJobState.STATE_DELIVERED;
				break;
			case 105 :
				state = IndustryJobState.STATE_FAILED;
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
		this.name = item.getTypeName();
	}

	@Override
	public int compareTo(final MyIndustryJob o) {
		return 0;
	}

	public final boolean isCompleted() {
		return getCompletedDate().after(Settings.getNow());
	}

	public final boolean isDelivered() {
		return getState() == IndustryJobState.STATE_DELIVERED;
	}

	public final boolean isManufacturing() {
		return getActivity() == IndustryActivity.ACTIVITY_MANUFACTURING;
	}

	public final boolean isInvention() {
		return getActivity() == IndustryActivity.ACTIVITY_REVERSE_INVENTION;
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

	public String getName() {
		return name;
	}

	public void setOutputPrice(double outputPrice) {
		if (isManufacturing() && !isDelivered()) {
			this.outputValue = outputPrice * getRuns() * getPortion();
		} else {
			this.outputValue = 0;
		}
	}

	public int getOutputCount() {
		return outputCount;
	}

	public String getInstaller() {
		return installer;
	}

	public void setInstaller(String installer) {
		this.installer = installer;
	}

	@Override
	public MyLocation getLocation() {
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
		final MyIndustryJob other = (MyIndustryJob) obj;
		if (this.owner != other.owner && (this.owner == null || !this.owner.equals(other.owner))) {
			return false;
		}
		return this.getJobID() == other.getJobID();
	}
}
