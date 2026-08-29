/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import picocli.CommandLine;


public class CliOptionsTest {

	@Test
	public void refreshOptionsEnableCliModeAndAreExclusive() {
		for (String option : new String[] {"-r", "-refresh"}) {
			CliOptions options = parse(option);
			assertTrue(options.isRefresh());
			assertTrue(options.isCLI());
			assertFalse(options.isUpdate());
			assertFalse(options.isExport());
		}
		assertInvalid("-r", "-u");
		assertInvalid("-refresh", "-export", "-csv");
	}

	private CliOptions parse(String... args) {
		CliOptions options = new CliOptions();
		options.parse(args);
		return options;
	}

	private void assertInvalid(String... args) {
		try {
			parse(args);
			fail("Expected invalid CLI option combination");
		} catch (CommandLine.ParameterException ex) {
			//Expected
		}
	}
}
