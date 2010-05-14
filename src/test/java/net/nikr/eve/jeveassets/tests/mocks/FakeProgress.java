package net.nikr.eve.jeveassets.tests.mocks;

import uk.me.candle.eve.routing.Progress;

/**
 *
 * @author Candle
 */
public class FakeProgress implements Progress {
	int value;
	int min;
	int max;

	@Override
	public int getMaximum() {
		return max;
	}

	@Override
	public void setMaximum(int max) {
		this.max = max;
	}

	@Override
	public int getMinimum() {
		return min;
	}

	@Override
	public void setMinimum(int min) {
		this.min = min;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public void setValue(int value) {
		this.value = value;
	}
}
