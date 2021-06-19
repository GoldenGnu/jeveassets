/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.shared.menu;

import com.udojava.evalex.Expression;
import java.math.MathContext;
import java.util.HashSet;
import net.nikr.eve.jeveasset.TestUtil;
import org.junit.Test;


public class ExpressionExceptionTest extends TestUtil {

	@Test
	public void testNullPointerException() {
		eval("\"");
	}

	@Test
	public void testStringIndexOutOfBoundsException() {
		eval("100\"");
	}

	@Test
	public void testArithmeticException() {
		eval("100/0");
	}

	@Test
	public void testNumberFormatException() {
		eval("1.0.");
	}

	@Test
	public void testExpressionException() {
		eval("");
	}

	private void eval(String eval) {
		JFormulaDialog.safeEval(new HashSet<>(), new Expression(eval, MathContext.UNLIMITED));
	}

}
