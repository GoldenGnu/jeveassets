/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.api.raw.RawIndustryJob;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.types.BlueprintType;
import net.nikr.eve.jeveasset.data.settings.types.EditableLocationType;
import net.nikr.eve.jeveasset.data.settings.types.EditablePriceType;
import net.nikr.eve.jeveasset.data.settings.types.EsiType;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.data.settings.types.OwnersType;
import net.nikr.eve.jeveasset.i18n.DataModelIndustryJob;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;

public class MyIndustryJob extends RawIndustryJob implements Comparable<MyIndustryJob>, EditableLocationType, ItemType, EditablePriceType, BlueprintType, OwnersType, EsiType {

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
	private final Item item;
	private final Item output;
	private final OwnerType owner;
	private final String name;
	private final Set<Long> owners = new HashSet<>();
	private final int outputCount;
	private double price;
	private double outputValue;
	private String installer = "";
	private String completedCharacter = "";
	private MyBlueprint blueprint;
	private MyLocation location;
	private boolean esi = true;
	private boolean owned;

	public MyIndustryJob(final RawIndustryJob rawIndustryJob, final Item item, final Item output, final OwnerType owner) {
		super(rawIndustryJob);
		this.item = item;
		this.output = output;
		this.owner = owner;
		this.owners.add(getInstallerID());
		this.owners.add(owner.getOwnerID());
		this.owned = owner.isCharacter();

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
		switch (activity) {
			case ACTIVITY_MANUFACTURING:
				outputCount = getRuns() * item.getProductQuantity();
				break;
			case ACTIVITY_REACTIONS:
				outputCount = getRuns() * item.getProductQuantity();
				break;
			case ACTIVITY_COPYING:
				if (getLicensedRuns() != null) {
					outputCount = getRuns() * getLicensedRuns();
				} else { //Should never happen, but, better safe than sorry
					outputCount = getRuns();
				}
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

	public void setBlueprint(MyBlueprint blueprint) {
		this.blueprint = blueprint;
	}

	@Override
	public boolean isBPO() {
		if (blueprint != null) {
			return blueprint.getRuns() <= 0;
		} else {
			return (getLicensedRuns() == null || getLicensedRuns() <= 0 //BPO should have value -1
					|| activity == IndustryActivity.ACTIVITY_COPYING //BPO only
					|| activity == IndustryActivity.ACTIVITY_RESEARCHING_METERIAL_PRODUCTIVITY //BPO only
					|| activity == IndustryActivity.ACTIVITY_RESEARCHING_TIME_PRODUCTIVITY) //BPO only
					&& activity != IndustryActivity.ACTIVITY_REVERSE_INVENTION //BPC only
					;
		}
	}

	@Override
	public boolean isBPC() {
		return !isBPO();
	}

	@Override
	public int getMaterialEfficiency() {
		if (blueprint != null) {
			return blueprint.getMaterialEfficiency();
		} else {
			return 0;
		}
	}

	@Override
	public int getTimeEfficiency() {
		if (blueprint != null) {
			return blueprint.getTimeEfficiency();
		} else {
			return 0;
		}
	}

	@Override
	public Integer getTypeID() {
		return getBlueprintTypeID();
	}

	public final boolean isCompletedSuccessful() {
		return getStatus() == IndustryJobStatus.DELIVERED;
	}

	public final boolean isDone() {
		return getStatus() == IndustryJobStatus.DELIVERED
				|| getStatus() == IndustryJobStatus.CANCELLED
				|| getStatus() == IndustryJobStatus.REVERTED
				|| getStatus() == IndustryJobStatus.ARCHIVED //Status is unknown -> default to done > true
				;
	}

	public final boolean isNotDeliveredToAssets() {
		return !isDone() //if not done ->  not delivered to assets -> true
				&& (owner.getAssetLastUpdate() == null //if null -> never updated -> not delivered to assets -> true
				|| getCompletedDate() == null //if null -> not completed -> not delivered to assets -> true
				|| owner.getAssetLastUpdate().before(getCompletedDate()) //if assets last updated before completed date -> not delivered to assets -> true
				);
	}

	public final boolean isRemovedFromAssets() {
		return owner.getAssetLastUpdate() != null //if null -> never updated -> not removed from assets -> false
				&& getStartDate() != null //if null -> not started -> not removed from assets -> false
				&& owner.getAssetLastUpdate().after(getStartDate()); //if assets last updated after started date -> removed from assets -> true
	}

	public final boolean isManufacturing() {
		return getActivity() == IndustryActivity.ACTIVITY_MANUFACTURING || getActivity() == IndustryActivity.ACTIVITY_REACTIONS;
	}

	public final boolean isCopying() {
		return getActivity() == IndustryActivity.ACTIVITY_COPYING;
	}

	public final boolean isInvention() {
		return getActivity() == IndustryActivity.ACTIVITY_REVERSE_INVENTION;
	}

	public boolean isOwned() {
		return owned;
	}

	public void setOwned(boolean owned) {
		this.owned = owned;
	}

	public IndustryActivity getActivity() {
		return activity;
	}

	public boolean isExpired() {
		return getEndDate().before(Settings.getNow());
	}

	@Override
	public IndustryJobStatus getStatus() {
		//Update READY (may have changed after loading profile)
		if (isExpired() && super.getStatus() == IndustryJobStatus.ACTIVE) {
			setStatus(IndustryJobStatus.READY);
		}
		return super.getStatus();
	}

	public String getStatusFormatted() {
		return getStatusName(getStatus(), isExpired());
	}

	public static String getStatusName(IndustryJobStatus status) {
		return getStatusName(status, false);
	}

	public static String getStatusName(IndustryJobStatus status, boolean expired) {
		switch (status) {
				case ACTIVE: //Active
					if (expired) {
						return DataModelIndustryJob.get().statusDone();
					} else {
						return DataModelIndustryJob.get().statusActive();
					}
				case PAUSED:
					return DataModelIndustryJob.get().statusPaused();
				case READY:
					return DataModelIndustryJob.get().statusDone();
				case DELIVERED:
					return DataModelIndustryJob.get().statusDelivered();
				case CANCELLED:
					return DataModelIndustryJob.get().statusCancelled();
				case REVERTED:
					return DataModelIndustryJob.get().statusReverted();
				case ARCHIVED:
					return DataModelIndustryJob.get().statusArchived();
				default:
					return DataModelIndustryJob.get().statusUnknown();
			}
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
		if (isManufacturing()) {
			this.outputValue = outputPrice * getRuns() * getProductQuantity();
		} else if (isCopying() && getLicensedRuns() != null) {
			this.outputValue = outputPrice * getRuns() * getLicensedRuns();
		} else {
			this.outputValue = 0;
		}
	}

	public int getOutputCount() {
		return outputCount;
	}

	public String getOutputType() {
		return output.getTypeName();
	}

	public double getOutputVolume() {
		if (isCopying()) {
			return output.getVolumePackaged() * getRuns(); // Volume of the output blueprints (bp runs should not be counted)
		} else {
			return output.getVolumePackaged() * outputCount;
		}
	}

	public String getInstaller() {
		return installer;
	}

	public void setInstaller(String installer) {
		this.installer = installer;
	}

	public String getCompletedCharacter() {
		return completedCharacter;
	}

	public void setCompletedCharacter(String completedCharacter) {
		this.completedCharacter = completedCharacter;
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
	public boolean archive() {
		boolean update = esi; //Update if in esi
		if (esi && (getStatus() == IndustryJobStatus.READY || getStatus() == IndustryJobStatus.ACTIVE)) {
			setStatus(IndustryJobStatus.ARCHIVED);
		}
		this.esi = false;
		return update;
	}

	@Override
	public boolean isESI() {
		return esi;
	}

	@Override
	public void setESI(boolean esi) {
		this.esi = esi;
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public long getItemCount() {
		return 1; //Just one blueprint here
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
