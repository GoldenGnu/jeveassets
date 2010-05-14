package net.nikr.eve.jeveassets.tests.routing.mocks;

import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveassets.tests.mocks.FakeProgram;
import net.nikr.eve.jeveassets.tests.mocks.FakeSettings;

/**
 *
 * @author Candle
 */
public class RoutingMockProgram extends FakeProgram {

	FakeSettings fakeSettings;

	public RoutingMockProgram(RoutingMockSettings routingMockSettings) {
		this.fakeSettings = routingMockSettings;
	}

	@Override
	public Settings getSettings() {
		if (fakeSettings == null) {
			fakeSettings = new RoutingMockSettings();
		}
		return fakeSettings;
	}
}
