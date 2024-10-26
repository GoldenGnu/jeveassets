/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
import com.udojava.evalex.LazyFunction;
import com.udojava.evalex.LazyOperator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.table.ColumnManager;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.NumberValue;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JFormulaDialog<T extends Enum<T> & EnumTableColumn<Q>, Q> extends JDialogCentered {

	public static MathContext FORMULA_PRECISION = MathContext.DECIMAL64; //64bit == Double size

	private enum FormulaDialogAction {
		OK, CANCEL
	}

	private final JTextField jName;
	private final JTextField jFormula;
	private final JButton jOK;

	private final List<String> columnNames = new ArrayList<>();
	private final List<String> columnStaticNames = new ArrayList<>();
	private final ColumnManager<T, Q> columnManager;

	private Formula returnValue = null;

	public JFormulaDialog(Program program, ColumnManager<T, Q> columnManager) {
		super(program, GuiShared.get().formulaTitle(), Images.MISC_FORMULA.getImage());

		this.columnManager = columnManager;

		ListenerClass listener = new ListenerClass();

		JLabel jNameLabel = new JLabel(GuiShared.get().formulaName());

		jName = new JTextField();
		jName.addCaretListener(listener);

		JLabel jFormulaLabel = new JLabel(GuiShared.get().formulaString());

		jFormula = new JTextField();
		jFormula.addCaretListener(listener);

		JDropDownButton jColumns = new JDropDownButton(GuiShared.get().formulaColumns());

		for (T t : columnManager.getEnumConstants()) {
			if (isAssignableFrom(t.getType())) {
				String column = getSoftName(t);
				JMenuItem jMenuItem = new JMenuItem(t.getColumnName());
				if (isNumber(t.getType())) {
					jMenuItem.setIcon(Images.MISC_NUMBER.getIcon());
				} else if (isDate(t.getType())) {
					jMenuItem.setIcon(Images.MISC_DATE.getIcon());
					jMenuItem.setToolTipText(GuiShared.get().formulaDateToolTip());
				}
				jMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							jFormula.getDocument().insertString(jFormula.getCaretPosition(), column, null);
							jFormula.requestFocusInWindow();
						} catch (BadLocationException ex) {
							//No problem
						}
					}
				});
				jColumns.add(jMenuItem);
			}
		}
		JDropDownButton jFunctions = new JDropDownButton(GuiShared.get().formulaFunctions());

		ExpressionValues expression = new ExpressionValues();
		for (LazyFunction function : expression.getFunctions()) {
			StringBuilder builder = new StringBuilder();
			for (int i = 1; i < function.getNumParams(); i++) {
				builder.append(",");
			}
			builder.append(")");
			String after = builder.toString();
			String before = function.getName().toLowerCase() + "(";
			JMenuItem jMenuItem = new JMenuItem(before + after);
			jMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						int start = jFormula.getSelectionStart();
						int end = jFormula.getSelectionEnd();
						jFormula.getDocument().insertString(end, after, null);
						jFormula.getDocument().insertString(start, before, null);
						jFormula.setCaretPosition(start + before.length());
						jFormula.requestFocusInWindow();
					} catch (BadLocationException ex) {
						//No problem
					}
				}
			});
			jFunctions.add(jMenuItem);
		}

		JDropDownButton jOperators = new JDropDownButton(GuiShared.get().formulaOperations());
		for (LazyOperator operator : expression.getOperators()) {
			JMenuItem jMenuItem = new JMenuItem(operator.getOper());
			jMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						jFormula.getDocument().insertString(jFormula.getCaretPosition(), operator.getOper(), null);
						jFormula.requestFocusInWindow();
					} catch (BadLocationException ex) {
						//No problem
					}
				}
			});
			jOperators.add(jMenuItem);
		}

		//Static columns - does not change
		for (T t : columnManager.getEnumConstants()) {
			columnStaticNames.add(toColumnName(t.getColumnName()));
		}
		//System names (reserved for jump columns) - does not change
		for (MyLocation location : StaticData.get().getLocations()) {
			if (location.isSystem()) {
				columnStaticNames.add(toColumnName(location.getSystem()));
			}
		}

		jOK = new JButton(GuiShared.get().ok());
		jOK.setActionCommand(FormulaDialogAction.OK.name());
		jOK.addActionListener(listener);

		JButton jCancel = new JButton(GuiShared.get().cancel());
		jCancel.setActionCommand(FormulaDialogAction.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jNameLabel)
						.addComponent(jFormulaLabel)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jName)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jColumns, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
							.addComponent(jFunctions, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
							.addComponent(jOperators, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						)
						.addComponent(jFormula)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
							.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
						)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(jNameLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jName, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(jColumns, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFunctions, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOperators, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(jFormulaLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFormula, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jName;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {
		validate();
	}

	@Override
	protected void save() {
		returnValue = new Formula(jName.getText(), getExpressionString(), null);
		setVisible(false);
	}

	public Formula edit(Formula formula) {
		reset(formula.getColumnName(), formula.getOriginalExpression());
		setVisible(true);
		return returnValue;
	}

	public Formula add() {
		reset("", "");
		setVisible(true);
		return returnValue;
	}

	private void reset(String name, String expression) {
		returnValue = null;
		columnNames.clear();
		for (Formula f : columnManager.getFormulas()) {
			columnNames.add(toColumnName(f.getColumnName()));
		}
		columnNames.addAll(columnStaticNames);
		columnNames.remove(toColumnName(name)); //Remove current name (that is still vaild)
		jName.setText(name);
		jFormula.setText(fromExpressionString(expression));
		validate();
	}

	public static String getHardName(EnumTableColumn<?> t) {
		return t.name().replace("_", "");
	}

	private static String getSoftName(EnumTableColumn<?> t) {
		return "[" + t.getColumnName() + "]";
	}

	private String toColumnName(String text) {
		return text.toLowerCase();
	}

	private String getExpressionString() {
		return toExpressionString(jFormula.getText());
	}

	private String toExpressionString(String text) {
		for (T t : columnManager.getEnumConstants()) {
			if (isAssignableFrom(t.getType())) {
				text = text.replace(getSoftName(t), getHardName(t));
			}
		}
		return text;
	}

	private String fromExpressionString(String text) {
		for (T t : columnManager.getEnumConstants()) {
			if (isAssignableFrom(t.getType())) {
				text = replaceAll(t, text);
			}
		}
		return text;
	}

	public static String replaceAll(EnumTableColumn<?> enumColumn, String text) {
		return text.replaceAll("\\b" + getHardName(enumColumn) + "\\b", getSoftName(enumColumn).replace("$", "\\$")); //$ is reserved for
	}

	private Expression getExpression() {
		return new Expression(getExpressionString(), FORMULA_PRECISION);
	}

	private boolean isFomulaValid() {
		Expression expression = getExpression();
		Set<String> hardNames = new HashSet<>();
		for (T t : columnManager.getEnumConstants()) {
			if (isAssignableFrom(t.getType())) {
				String hardName = getHardName(t);
				expression.setVariable(hardName, new BigDecimal(2.0));
				hardNames.add(hardName);
			}
		}
		return safeEval(hardNames, expression);
	}

	private boolean isAssignableFrom(Class<?> c) {
		return Number.class.isAssignableFrom(c)
				|| NumberValue.class.isAssignableFrom(c)
				|| Date.class.isAssignableFrom(c);
	}

	private boolean isNumber(Class<?> c) {
		return Number.class.isAssignableFrom(c)
				|| NumberValue.class.isAssignableFrom(c);
	}

	private boolean isDate(Class<?> c) {
		return Date.class.isAssignableFrom(c);
	}

	public static boolean safeEval(Set<String> hardNames, Expression expression) {
		try {
			if (!hardNames.containsAll(expression.getUsedVariables())) {
				return false; //Invalid variable
			}
			expression.eval();
			return true;
		} catch (RuntimeException ex) {
			return false;
		}
	}

	private boolean isNameValid() {
		String name = jName.getText();
		return !name.isEmpty() && !columnNames.contains(toColumnName(name));
	}

	private void validate() {
		boolean nameValid = isNameValid();
		boolean fomulaValid = isFomulaValid();
		if (nameValid) {
			ColorSettings.configReset(jName);
		} else {
			ColorSettings.config(jName, ColorEntry.GLOBAL_ENTRY_INVALID);
		}
		if (fomulaValid) {
			ColorSettings.configReset(jFormula);
		} else {
			ColorSettings.config(jFormula, ColorEntry.GLOBAL_ENTRY_INVALID);
		}
		jOK.setEnabled(nameValid && fomulaValid);
	}

	private class ListenerClass implements ActionListener, CaretListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (FormulaDialogAction.OK.name().equals(e.getActionCommand())) {
				save();
			} else if (FormulaDialogAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
		}

		@Override
		public void caretUpdate(CaretEvent e) {
			validate();
		}
	}

	private static class ExpressionValues extends Expression {

		public ExpressionValues() {
			super("", FORMULA_PRECISION);
		}

		public Collection<LazyOperator> getOperators() {
			return operators.values();
		}

		public Collection<com.udojava.evalex.LazyFunction> getFunctions() {
			return functions.values();
		}
	}

	public static class Formula implements Comparable<Formula> {
		private final String columnName;
		private final Expression expression;
		private final Map<Object, Object> values = new HashMap<>();
		private final Collection<String> usedVariables;
		private final Collection<String> variableColumns = new ArrayList<>();
		private final boolean isBoolean;
		private Integer index;

		public Formula(String columnName, String expressionString, Integer index) {
			this.expression = new Expression(expressionString, FORMULA_PRECISION);
			this.columnName = columnName;
			this.index = index;
			this.usedVariables = expression.getUsedVariables();
			this.isBoolean = expression.isBoolean();
		}

		public String getColumnName() {
			return columnName;
		}

		public String getOriginalExpression() {
			return expression.getOriginalExpression();
		}

		public Expression getExpression() {
			return expression;
		}

		public boolean isBoolean() {
			return isBoolean;
		}

		public Integer getIndex() {
			return index;
		}

		public Map<Object, Object> getValues() {
			return values;
		}

		public Collection<String> getUsedVariables() {
			return usedVariables;
		}

		public Collection<String> getVariableColumns() {
			return variableColumns;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 47 * hash + Objects.hashCode(this.columnName);
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
			final Formula other = (Formula) obj;
			if (!Objects.equals(this.columnName, other.columnName)) {
				return false;
			}
			return true;
		}

		@Override
		public int compareTo(Formula other) {
			return this.columnName.compareTo(other.columnName);
		}
	}
}
