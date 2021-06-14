package net.nikr.eve.jeveasset.gui.shared.menu;

import com.udojava.evalex.Expression;
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
		JFormulaDialog.safeEval(new HashSet<>(), new Expression(eval));
	}
	
}
