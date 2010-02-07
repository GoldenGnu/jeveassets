package net.nikr.eve.jeveasset.data;

import uk.me.candle.eve.graph.Node;

/**
 *
 * @author Candle
 */
public class SolarSystem extends Node {
	Location location;

	public SolarSystem(Location location) {
		super(location.getName());
		this.location = location;
	}

	public String getSecurity() {
		return location.getSecurity();
	}

	public int getRegion() {
		return location.getRegion();
	}

	public int getId() {
		return location.getId();
	}

	public int getSolarSystemID() {
		return location.getSolarSystemID();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SolarSystem other = (SolarSystem) obj;
		if (this.location != other.location && (this.location == null || !this.location.equals(other.location))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + (this.location != null ? this.location.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return getName();
	}
}
