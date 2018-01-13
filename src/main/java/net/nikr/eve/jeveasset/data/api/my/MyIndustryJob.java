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
package net.nikr.eve.jeveasset.data.api.my;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.raw.RawBlueprint;
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.BlueprintType;
import net.nikr.eve.jeveasset.data.settings.types.EditableLocationType;
import net.nikr.eve.jeveasset.data.settings.types.EditablePriceType;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.data.settings.types.OwnersType;
import net.nikr.eve.jeveasset.i18n.DataModelIndustryJob;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;

public class MyIndustryJob extends RawIndustryJob implements Comparable<MyIndustryJob>, EditableLocationType, ItemType, EditablePriceType, BlueprintType, OwnersType {

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
		STATE_DELIVERED() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateDelivered();
			}
		},
		STATE_CANCELLED() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateCancelled();
			}
		},
		STATE_REVERTED() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().stateReverted();
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
		},
		ACTIVITY_REACTIONS() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().activityReactions();
			}
		},
		ACTIVITY_UNKNOWN() {
			@Override
			String getI18N() {
				return DataModelIndustryJob.get().activityUnknown();
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
	private final OwnerType owner;
	private final String name;
	private final Set<Long> owners = new HashSet<Long>();
	private double price;
	private double outputValue;
	private int outputCount;
	private String installer = "";
	private RawBlueprint blueprint;
	private MyLocation location;

	public MyIndustryJob(final RawIndustryJob rawIndustryJob, final Item item, final OwnerType owner) {
		super(rawIndustryJob);
		this.item = item;
		this.owner = owner;
		owners.add(getInstallerID());
		owners.add(owner.getOwnerID());

		switch (getActivityID()) {
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
			case 9:
				activity = IndustryActivity.ACTIVITY_REACTIONS;
				break;
			case 11:
				activity = IndustryActivity.ACTIVITY_REACTIONS;
				break;
			default:
				activity = IndustryActivity.ACTIVITY_UNKNOWN;
				break;
		}
		switch (getStatus()) {
			case ACTIVE: //Active
				if (getEndDate().before(Settings.getNow())) {
					state = IndustryJobState.STATE_DONE;
				} else {
					state = IndustryJobState.STATE_ACTIVE;
				}
				break;
			case PAUSED:
				state = IndustryJobState.STATE_PAUSED;
				break;
			case READY:
				state = IndustryJobState.STATE_DONE;
				break;
			case DELIVERED:
				state = IndustryJobState.STATE_DELIVERED;
				break;
			case CANCELLED:
				state = IndustryJobState.STATE_CANCELLED;
				break;
			case REVERTED:
				state = IndustryJobState.STATE_REVERTED;
				break;
		}
		switch (activity) {
			case ACTIVITY_MANUFACTURING:
				outputCount = getRuns() * item.getProductQuantity();
				break;
			case ACTIVITY_REACTIONS:
				outputCount = getRuns() * item.getProductQuantity();
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

	public void setBlueprint(RawBlueprint blueprint) {
		this.blueprint = blueprint;
	}

	@Override
	public boolean isBPO() {
		if (blueprint != null) {
			return blueprint.getRuns() <= 0;
		} else {
			return getLicensedRuns() == null || getLicensedRuns() <= 0;
		}
	}

	@Override
	public boolean isBPC() {
		return !isBPO();
	}

	public int getMaterialEfficiency() {
		if (blueprint != null) {
			return blueprint.getMaterialEfficiency();
		} else {
			return 0;
		}
	}

	public int getTimeEfficiency() {
		if (blueprint != null) {
			return blueprint.getTimeEfficiency();
		} else {
			return 0;
		}
	}

	public final boolean isCompleted() {
		return getCompletedDate() != null && getCompletedDate().after(Settings.getNow());
	}

	public final boolean isDelivered() {
		return getState() == IndustryJobState.STATE_DELIVERED
				|| getState() == IndustryJobState.STATE_CANCELLED
				|| getState() == IndustryJobState.STATE_REVERTED
				;
	}

	public final boolean isManufacturing() {
		return getActivity() == IndustryActivity.ACTIVITY_MANUFACTURING || getActivity() == IndustryActivity.ACTIVITY_REACTIONS;
	}

	public final boolean isInvention() {
		return getActivity() == IndustryActivity.ACTIVITY_REVERSE_INVENTION;
	}

	public IndustryActivity getActivity() {
		return activity;
	}

	public IndustryJobState getState() {
		//Update STATE_DONE (may have changed after loading profile)
		if (getEndDate().before(Settings.getNow()) && state == IndustryJobState.STATE_ACTIVE) {
			state = IndustryJobState.STATE_DONE;
		}
		return state;
	}

	@Override
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
			this.outputValue = outputPrice * getRuns() * getProductQuantity();
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
	public Set<Long> getOwners() {
		return owners;
	}

	@Override
	public long getLocationID() {
		if (ApiIdConverter.isLocationOK(getStationID())) {
			return getStationID();
		}
		if (ApiIdConverter.isLocationOK(getBlueprintLocationID())) {
			return getBlueprintLocationID();
		}
		if (ApiIdConverter.isLocationOK(getOutputLocationID())) {
			return getOutputLocationID();
		}
		return getStationID();
	}

	@Override
	public MyLocation getLocation() {
		return location;
	}

	@Override
	public void setLocation(MyLocation location) {
		this.location = location;
	}

	public OwnerType getOwner() {
		return owner;
	}

	public String getOwnerName() {
		return owner.getOwnerName();
	}

	public long getOwnerID() {
		return owner.getOwnerID();
	}

	public int getProductQuantity() {
		return item.getProductQuantity();
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 37 * hash + Objects.hashCode(this.getJobID());
		hash = 37 * hash + (int) (this.owner.getOwnerID() ^ (this.owner.getOwnerID() >>> 32));
		return hash;
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
		final MyIndustryJob other = (MyIndustryJob) obj;
		if (!Objects.equals(this.getJobID(), other.getJobID())) {
			return false;
		}
		return this.owner.getOwnerID() == other.owner.getOwnerID();
	}
}
