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

package net.nikr.eve.jeveasset.tests.mocks;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;
import org.junit.Test;


public class OverwriteTest {

	private void test(Class<?> c) {
		Method methods[] = c.getMethods();
		for (Method method : methods) {
			if (method.getDeclaringClass() != c
					&& method.getDeclaringClass() != Object.class
					&& ((method.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
					&& ((method.getModifiers() & Modifier.FINAL) != Modifier.FINAL)) {
				fail(c.getSimpleName()+" - overwrite method: "+method.toString());
			}
		}
	}
	
	@Test
	public void testOverwrite() {
		test(FakeProgram.class);
		test(FakeSettings.class);
		test(FakeProgress.class);
	}
}
