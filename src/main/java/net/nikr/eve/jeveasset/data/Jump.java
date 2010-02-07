package net.nikr.eve.jeveasset.data;

/**
 *
 * @author Candle
 */
public class Jump {
	Location from;
	Location to;

	public Jump(Location from, Location to) {
		this.from = from;
		this.to = to;
	}

	public Location getFrom() {
		return from;
	}

	public Location getTo() {
		return to;
	}
}
