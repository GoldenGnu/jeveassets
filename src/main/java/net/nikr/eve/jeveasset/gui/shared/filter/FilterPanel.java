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
package net.nikr.eve.jeveasset.gui.shared.filter;

import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.synth.SynthFormattedTextFieldUI;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.TextManager;
import net.nikr.eve.jeveasset.gui.shared.components.JDateChooser;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.AllColumn;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;


class FilterPanel<E> implements Comparable<FilterPanel<E>> {

	private enum FilterPanelAction {
		FILTER, FILTER_TIMER, GROUP_TIMER, REMOVE, CLONE
	}

	private final JPanel jPanel;
	private final GroupLayout layout;

	private final JCheckBox jEnabled;
	private final JComboBox<LogicType> jLogic;
	private final JSpinner jGroup;
	private final JComboBox<EnumTableColumn<E>> jColumn;
	private final JComboBox<CompareType> jCompare;
	private final JTextField jText;
	private final JComboBox<EnumTableColumn<E>> jCompareColumn;
	private final JDateChooser jDate;

	private final JLabel jSpacing;
	private final JButton jRemove;
	private final JButton jClone;

	private final Timer timer;
	private final Timer groupTimer;

	private final FilterGui<E> gui;
	private final FilterControl<E> filterControl;
	private final SimpleTableFormat<E> tableFormat;
	private final List<EnumTableColumn<E>> allColumns;
	private final List<EnumTableColumn<E>> numericColumns;
	private final List<EnumTableColumn<E>> dateColumns;
	private final SpinnerNumberModel groupModel;

	private final Executor fades = Executors.newSingleThreadExecutor();
	private boolean loading = false;
	private boolean moving = false;

	FilterPanel(final FilterGui<E> gui, final FilterControl<E> filterControl, SimpleTableFormat<E> tableFormat) {
		this.gui = gui;
		this.filterControl = filterControl;
		this.tableFormat = tableFormat;

		ListenerClass listener = new ListenerClass();

		groupTimer = new Timer(500, listener);
		groupTimer.setActionCommand(FilterPanelAction.GROUP_TIMER.name());

		groupModel = new SpinnerNumberModel(0, 0, 0, 1);
		groupModel.addChangeListener(listener);
		jGroup = new JSpinner(groupModel);
		jGroup.setEnabled(false);

		allColumns = new ArrayList<>();
		allColumns.add(new AllColumn<>());
		allColumns.addAll(tableFormat.getAllColumns());

		numericColumns = new ArrayList<>();
		for (EnumTableColumn<E> object : tableFormat.getAllColumns()) {
			if (filterControl.isNumeric(object)) {
				numericColumns.add(object);
			}
		}

		dateColumns = new ArrayList<>();
		for (EnumTableColumn<E> object : tableFormat.getAllColumns()) {
			if (filterControl.isDate(object)) {
				dateColumns.add(object);
			}
		}

		jEnabled = new JCheckBox();
		jEnabled.setSelected(true);
		jEnabled.addActionListener(listener);
		jEnabled.setActionCommand(FilterPanelAction.FILTER.name());

		jLogic = new JComboBox<>(LogicType.values());
		jLogic.setPrototypeDisplayValue(LogicType.AND);
		jLogic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean oldValue = loading;
				loading = true;
				Dimension preferredSize = ((JSpinner.DefaultEditor) jGroup.getEditor()).getTextField().getPreferredSize();
				groupModel.removeChangeListener(listener);
				if (isAnd()) {
					groupModel.setMinimum(0);
					groupModel.setValue(0);
					jGroup.setEditor(new JSpinner.DefaultEditor(jGroup));
					jGroup.setModel(new SpinnerListModel(Collections.singletonList("")));
					jGroup.setEditor(new JSpinner.DefaultEditor(jGroup));
					jGroup.setEnabled(false);
				} else {
					groupModel.addChangeListener(listener);
					jGroup.setModel(groupModel);
					jGroup.setEditor(new JSpinner.NumberEditor(jGroup));
					groupModel.setMinimum(1);
					if (groupModel.getNumber().intValue() == 0) {
						groupModel.setValue(1);
					}
					jGroup.setEnabled(true);
				}
				((JSpinner.DefaultEditor) jGroup.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
				((JSpinner.DefaultEditor) jGroup.getEditor()).getTextField().setPreferredSize(preferredSize);
				((JSpinner.DefaultEditor) jGroup.getEditor()).getTextField().setMaximumSize(preferredSize);
				((JSpinner.DefaultEditor) jGroup.getEditor()).getTextField().setMinimumSize(preferredSize);
				((JSpinner.DefaultEditor) jGroup.getEditor()).getTextField().setEditable(false);
				((JSpinner.DefaultEditor) jGroup.getEditor()).getTextField().setFocusable(false);
				loading = oldValue;
				if (!loading) {
					groupChanged();
				}
			}
		});
		loading = true;
		jLogic.setSelectedIndex(0);
		loading = false;
		updateGroupColor();

		JComboBox<String> jComboBox = new JComboBox<>();
		FontMetrics fontMetrics = jComboBox.getFontMetrics(jComboBox.getFont());
		EnumTableColumn<E> longestColumn = null;
		for (EnumTableColumn<E> column : allColumns) {
			if (longestColumn == null || fontMetrics.stringWidth(longestColumn.getColumnName()) < fontMetrics.stringWidth(column.getColumnName())) {
				longestColumn = column;
			}
		}

		jColumn = new JComboBox<>(new ListComboBoxModel<>(allColumns));
		jColumn.setPrototypeDisplayValue(longestColumn);
		jColumn.addActionListener(listener);
		jColumn.setActionCommand(FilterPanelAction.FILTER.name());

		jCompare = new JComboBox<>();
		jCompare.setPrototypeDisplayValue(CompareType.CONTAINS_NOT_COLUMN);
		jCompare.addActionListener(listener);
		jCompare.setActionCommand(FilterPanelAction.FILTER.name());

		jText = new JTextField();
		jText.getDocument().addDocumentListener(listener);
		jText.addKeyListener(listener);
		TextManager.installTextComponent(jText);

		jCompareColumn = new JComboBox<>();
		jCompareColumn.setPrototypeDisplayValue(longestColumn);
		jCompareColumn.addActionListener(listener);
		jCompareColumn.setActionCommand(FilterPanelAction.FILTER.name());

		jDate = new JDateChooser(false);
		jDate.addDateChangeListener(listener);

		jSpacing = new JLabel();

		jClone = new JButton();
		jClone.setIcon(Images.EDIT_COPY.getIcon());
		jClone.addActionListener(listener);
		jClone.setActionCommand(FilterPanelAction.CLONE.name());

		jRemove = new JButton();
		jRemove.setIcon(Images.EDIT_DELETE.getIcon());
		jRemove.addActionListener(listener);
		jRemove.setActionCommand(FilterPanelAction.REMOVE.name());

		timer = new Timer(500, listener);
		timer.setActionCommand(FilterPanelAction.FILTER_TIMER.name());

		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jEnabled, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth())
				.addGap(0)
				.addComponent(jLogic, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jGroup, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jColumn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jCompare, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jText, 50, 50, Integer.MAX_VALUE)
				.addComponent(jCompareColumn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jDate, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jSpacing, 0, 0, Integer.MAX_VALUE)
				.addComponent(jClone, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth())
				.addComponent(jRemove, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jEnabled, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jLogic, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jGroup, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jColumn, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jCompare, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jText, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jCompareColumn, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jDate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSpacing, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jClone, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jRemove, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
		updateNumeric(false);
	}

	protected void updateColumns() {
		EnumTableColumn<E> selectedItem = jColumn.getItemAt(jColumn.getSelectedIndex());
		allColumns.clear();
		allColumns.add(new AllColumn<>());
		allColumns.addAll(tableFormat.getAllColumns());
		numericColumns.clear();
		for (EnumTableColumn<E> object : tableFormat.getAllColumns()) {
			if (filterControl.isNumeric(object)) {
				numericColumns.add(object);
			}
		}

		dateColumns.clear();
		for (EnumTableColumn<E> object : tableFormat.getAllColumns()) {
			if (filterControl.isDate(object)) {
				dateColumns.add(object);
			}
		}
		jColumn.setActionCommand("comboBoxChanged");
		jColumn.setModel(new ListComboBoxModel<>(allColumns));
		if (allColumns.contains(selectedItem)) {
			jColumn.setSelectedItem(selectedItem);
			jColumn.setActionCommand(FilterPanelAction.FILTER.name()); //After: Do not refilter
		} else if (!allColumns.isEmpty()) {
			jColumn.setActionCommand(FilterPanelAction.FILTER.name()); //Before: refilter
			jColumn.setSelectedIndex(0);
		}
	}

	boolean isAnd() {
		return ((LogicType) jLogic.getSelectedItem()) == LogicType.AND;
	}

	boolean isMoving() {
		return moving;
	}

	Integer getGroup() {
		return groupModel.getNumber().intValue();
	}

	JPanel getPanel() {
		return jPanel;
	}

	FilterMatcher<E> getMatcher() {
		int group = getGroup();
		boolean enabled = jEnabled.isSelected();
		LogicType logic = (LogicType) jLogic.getSelectedItem();
		EnumTableColumn<?> column = (EnumTableColumn<?>) jColumn.getSelectedItem();
		CompareType compare = (CompareType) jCompare.getSelectedItem();
		String text;
		if (isColumnCompare()) {
			EnumTableColumn<?> compareColumn = (EnumTableColumn<?>) jCompareColumn.getSelectedItem();
			text = compareColumn.name();
		} else if (isDateCompare()) {
			text = getDataString();
		} else {
			text = jText.getText();
		}
		return new FilterMatcher<>(tableFormat, filterControl, group, logic, column, compare, text, enabled);
	}

	Filter getFilter() {
		int group = getGroup();
		boolean enabled = jEnabled.isSelected();
		LogicType logic = (LogicType) jLogic.getSelectedItem();
		EnumTableColumn<?> column = (EnumTableColumn<?>) jColumn.getSelectedItem();
		CompareType compare = (CompareType) jCompare.getSelectedItem();
		String text;
		if (isColumnCompare()) {
			EnumTableColumn<?> compareColumn = (EnumTableColumn<?>) jCompareColumn.getSelectedItem();
			text = compareColumn.name();
		} else if (isDateCompare()) {
			text = getDataString();
		} else {
			text = jText.getText();
		}
		return new Filter(group, logic, column, compare, text, enabled);
	}

	void setFilter(final Filter filter) {
		boolean oldValue = loading;
		loading = true;
		jEnabled.setSelected(filter.isEnabled());
		jLogic.setSelectedItem(filter.getLogic());
		groupModel.setValue(filter.getGroup());
		updateGroupColor();
		jColumn.setSelectedItem(filter.getColumn());
		jCompare.setSelectedItem(filter.getCompareType());
		if (isColumnCompare()) {
			try {
				EnumTableColumn<E> enumColumn = tableFormat.valueOf(filter.getText());
				if (enumColumn != null) {
					jCompareColumn.setSelectedItem(enumColumn);
				}
			} catch (IllegalArgumentException ex) {
				//ignore missing columns...
			}
		} else if (isDateCompare()) {
			setDateString(Formatter.columnStringToDate(filter.getText()));
		} else {
			jText.setText(filter.getText());
			timer.stop();
		}
		loading = oldValue;
	}

	@Override
	public int compareTo(FilterPanel<E> o) {
		if (this.isAnd() && o.isAnd()) {
			return 0;
		} else if (this.isAnd()) {
			return 1;
		} else if (o.isAnd()) {
			return -1;
		} else {
			return this.getGroup().compareTo(o.getGroup());
		}
	}

	private void setDateString(Date date) {
		jDate.setDate(date);
	}

	private String getDataString() {
		LocalDate date = jDate.getDate();
		Instant instant = date.atStartOfDay().atZone(ZoneId.of("GMT")).toInstant(); //End of day - GMT
		return Formatter.columnDate(Date.from(instant));
	}

	private void refilter() {
		if (!loading) {
			gui.refilter();
		}
	}

	private boolean isInvalidRegex() {
		if (!isRegexCompare()) {
			return false;
		}
		String text = jText.getText();
		try {
			Pattern.compile(text, Pattern.CASE_INSENSITIVE);
			return false;
		} catch (PatternSyntaxException ex) {
			return true;
		}
	}

	private boolean isRegexCompare() {
		CompareType compareType = (CompareType) jCompare.getSelectedItem();
		return CompareType.isRegexCompare(compareType);
	}

	private boolean isColumnCompare() {
		CompareType compareType = (CompareType) jCompare.getSelectedItem();
		return CompareType.isColumnCompare(compareType);
	}

	private boolean isNumericCompare() {
		CompareType compareType = (CompareType) jCompare.getSelectedItem();
		return CompareType.isNumericCompare(compareType);
	}

	private boolean isDateCompare() {
		CompareType compareType = (CompareType) jCompare.getSelectedItem();
		return CompareType.isDateCompare(compareType);
	}

	private void updateNumeric(final boolean saveIndex) {
		Object object = jCompare.getSelectedItem();
		CompareType[] compareTypes;
		if (filterControl.isNumeric((EnumTableColumn<?>) jColumn.getSelectedItem())) {
			compareTypes = CompareType.valuesNumeric();
		} else if (filterControl.isDate((EnumTableColumn<?>) jColumn.getSelectedItem())) {
			compareTypes = CompareType.valuesDate();
		} else if (filterControl.isAll((EnumTableColumn<?>) jColumn.getSelectedItem())) {
			compareTypes = CompareType.valuesAll();
		} else {
			compareTypes = CompareType.valuesString();
		}
		jCompare.setModel(new ListComboBoxModel<>(compareTypes));
		for (CompareType compareType : compareTypes) {
			if (compareType.equals(object) && saveIndex) {
				jCompare.setSelectedItem(compareType);
			}
		}
		updateCompare(saveIndex);
	}

	private void updateCompare(final boolean saveIndex) {
		if (isColumnCompare()) { //Column
			jText.setVisible(false);
			jCompareColumn.setVisible(true);
			jDate.setVisible(false);
			jSpacing.setVisible(true);
		} else if (isDateCompare()) { //Date
			jText.setVisible(false);
			jCompareColumn.setVisible(false);
			jDate.setVisible(true);
			jSpacing.setVisible(true);
		} else { //String
			jText.setVisible(true);
			jCompareColumn.setVisible(false);
			jDate.setVisible(false);
			jSpacing.setVisible(false);
		}
		Object object = jCompareColumn.getSelectedItem();
		List<EnumTableColumn<E>> compareColumns;
		if (isNumericCompare()) {
			compareColumns = new ArrayList<>(numericColumns);
		} else if (isDateCompare()) {
			compareColumns = new ArrayList<>(dateColumns);
		} else {
			compareColumns = new ArrayList<>(tableFormat.getAllColumns());
		}
		jCompareColumn.setModel(new ListComboBoxModel<>(compareColumns));
		for (Object column : compareColumns) {
			if (column.equals(object) && saveIndex) {
				jCompareColumn.setSelectedItem(column);
			}
		}
	}

	private void processFilterAction(final ActionEvent e) {
		//Get the initial loading state so we can reset it at the end.
		//We need to do this in the even that we were loading true when we entered we do not want to blindly
		//set false after one pass.
		//This should mean that no events triggered hear would cause additional refreshes.
		boolean oldValue = loading;
		loading = true;
		if (jColumn.equals(e.getSource())) {
			updateNumeric(true);
		}
		if (jCompare.equals(e.getSource())) {
			updateCompare(true);
		}
		updateEnabledColor();
		timer.stop();
		loading = oldValue;
		refilter();
	}

	void updateGroupSize(int size) {
		boolean oldValue = loading;
		loading = true;
		if (size > 9) {
			size = 9;
		}
		groupModel.setMaximum(size);
		if (groupModel.getNumber().intValue() > size) {
			groupModel.setValue(size);
		}
		loading = oldValue;
	}

	protected void repaint() {
		updateEnabledColor();
		updateGroupColor();
	}

	private void updateEnabledColor() {
		if (jEnabled.isSelected()) {
			if (isInvalidRegex()) {
				ColorSettings.config(jText, ColorEntry.GLOBAL_ENTRY_WARNING);
			} else {
				ColorSettings.configReset(jText);
			}
		} else {
			ColorSettings.config(jText, ColorEntry.GLOBAL_ENTRY_INVALID);
		}
	}

	private void updateGroupColor() {
		Color color = getGroupColor();
		Border border = jGroup.getBorder();
		JFormattedTextField jTextField = ((JSpinner.DefaultEditor) jGroup.getEditor()).getTextField();
		if ("Nimbus".equals(UIManager.getLookAndFeel().getName())) {
			if (color == Color.GRAY) {
				jTextField.setUI(null);
				jTextField.updateUI();
			} else {
				jTextField.setUI(new SynthFormattedTextFieldUI() {
				@Override
				protected void paint(javax.swing.plaf.synth.SynthContext context, java.awt.Graphics g) {
					g.setColor(color);
					g.fillRect(3, 3, getComponent().getWidth()-3, getComponent().getHeight()-6);
					super.paint(context, g);
				};
			});
			}
		} else {
			if (color == Color.GRAY) {
				jTextField.setOpaque(false);
			} else {
				jTextField.setBackground(color);
				jTextField.setOpaque(true);
			}
			if (border instanceof CompoundBorder) {
				CompoundBorder compoundBorder = (CompoundBorder) border;
				if (color == Color.GRAY) {
					jGroup.setBorder(BorderFactory.createCompoundBorder(compoundBorder.getOutsideBorder(), BorderFactory.createLineBorder(jGroup.getBackground(), 2)));
				} else {
					jGroup.setBorder(BorderFactory.createCompoundBorder(compoundBorder.getOutsideBorder(), BorderFactory.createLineBorder(color, 2)));
				}
			}
		}
	}

	private Color getGroupColor() {
		switch (getGroup()) {
			case 0: return Color.GRAY;
			case 1: return ColorSettings.background(ColorEntry.FILTER_OR_GROUP_1);
			case 2: return ColorSettings.background(ColorEntry.FILTER_OR_GROUP_2);
			case 3: return ColorSettings.background(ColorEntry.FILTER_OR_GROUP_3);
			case 4: return ColorSettings.background(ColorEntry.FILTER_OR_GROUP_4);
			case 5: return ColorSettings.background(ColorEntry.FILTER_OR_GROUP_5);
			case 6: return ColorSettings.background(ColorEntry.FILTER_OR_GROUP_6);
			case 7: return ColorSettings.background(ColorEntry.FILTER_OR_GROUP_7);
			default: return Color.GRAY;
		}
	}

	private void groupChanged() {
		refilter();
		if (!loading) {
			if (gui.fade(FilterPanel.this)) {
				fades.execute(new FadeThread());
			} else {
				updateGroupColor();
				gui.updateGroupSize();
				gui.update();
			}
		}
	}

	private class ListenerClass implements ActionListener, KeyListener, DocumentListener, DateChangeListener, ChangeListener {

		@Override
		public void insertUpdate(final DocumentEvent e) {
			timer.stop();
			timer.start();
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			timer.stop();
			timer.start();
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
			timer.stop();
			timer.start();
		}

		@Override
		public void keyTyped(final KeyEvent e) { }

		@Override
		public void keyPressed(final KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				refilter();
			}
		}

		@Override
		public void keyReleased(final KeyEvent e) { }

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (FilterPanelAction.REMOVE.name().equals(e.getActionCommand())) {
				gui.remove(FilterPanel.this);
				gui.addEmpty();
				refilter();
			} else if (FilterPanelAction.CLONE.name().equals(e.getActionCommand())) {
				gui.clone(FilterPanel.this);
				refilter();
				jText.requestFocusInWindow();
				jCompareColumn.requestFocusInWindow();
				jDate.getComponentToggleCalendarButton().requestFocusInWindow();
			} else if (FilterPanelAction.FILTER.name().equals(e.getActionCommand())) {
				processFilterAction(e);
			} else if (FilterPanelAction.FILTER_TIMER.name().equals(e.getActionCommand())) {
				if (!Settings.get().isFilterOnEnter()) {
					processFilterAction(e);
				}
			} else if (FilterPanelAction.GROUP_TIMER.name().equals(e.getActionCommand())) {
				groupTimer.stop();
				groupChanged();
			}
		}

		@Override
		public void dateChanged(DateChangeEvent e) {
			refilter();
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if (!loading) {
				groupTimer.stop();
				groupTimer.start();
			}
		}
	}

	private class FadeThread implements Runnable, ActionListener {

		private final List<FadeComponent> components = new ArrayList<>();
		private final Timer moveTimer = new Timer(50, this);
		private int from;
		private int to;
		private int index;

		public FadeThread() {
			components.add(new FadeComponent(jEnabled));
			components.add(new FadeComponent(jPanel));
		}

		@Override
		public void run() {
			if (!gui.fade(FilterPanel.this)) {
				return;
			}
			moving = true;
			Fade fadeIn = new Fade(components, 100, Color.GRAY);
			fadeIn.start(true);

			from = gui.getFromIndex(FilterPanel.this);
			to = gui.getToIndex(FilterPanel.this);
			index = from;
			moveTimer.start();
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException ex) {
					//No problem
				}
			}
			Fade fadeOut = new Fade(components, 750);
			fadeOut.start(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (from < to) {
				index++;
			} else if (from > to) {
				index--;
			} else {
				//Should never happen
			}
			gui.move(FilterPanel.this, index);
			if (index == to) {
				moveTimer.stop();
				updateGroupColor();
				moving = false;
				gui.update();
				synchronized (this) {
					notifyAll();
				}
			}
		}
	}

	private static class Fade implements ActionListener {

		private static final float MS_PER_FRAME = 20; //(100/3);

		private final int frames;
		private final Map<Component, List<Color>> map = new HashMap<>();
		private final Timer timer;
		private int frameCount = 0;

		private Fade(List<FadeComponent> fadeComponents, int duration) {
			this(fadeComponents, duration, null);
		}

		private Fade(List<FadeComponent> fadeComponents, int duration, Color to) {
			this.frames = (int) (duration / MS_PER_FRAME);

			for (FadeComponent fadeComponent : fadeComponents) {
				Color from = fadeComponent.getComponent().getBackground();
				List<Color> colors = new ArrayList<>();
				Color toColor = to == null ? fadeComponent.getColor() : to;
				float redDiff = (from.getRed() - toColor.getRed()) / frames;
				float blueDiff = (from.getBlue() - toColor.getBlue()) / frames;
				float greenDiff = (from.getGreen() - toColor.getGreen()) / frames;
				for (int i = 1; i <= frames; i++) {
					colors.add(new Color((int) (from.getRed() - (i * redDiff)),
							 (int) (from.getGreen() - (i * greenDiff)),
							 (int) (from.getBlue() - (i * blueDiff))));
				}
				colors.add(toColor);
				map.put(fadeComponent.getComponent(), colors);
			}
			timer = new Timer((int) MS_PER_FRAME, this);
		}

		public void start(boolean wait) {
			timer.start();
			if (wait) {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException ex) {
						//Done waiting
					}
				}
			}
		}

		public void stop() {
			timer.stop();
			for (Map.Entry<Component, List<Color>> entry : map.entrySet()) {
				entry.getKey().setBackground(entry.getValue().get(entry.getValue().size() - 1));
			}
			synchronized (this) {
				notifyAll();
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			frameCount++;
			if (frameCount >= frames) {
				timer.stop();
				for (Map.Entry<Component, List<Color>> entry : map.entrySet()) {
					entry.getKey().setBackground(entry.getValue().get(entry.getValue().size() - 1));
				}
				synchronized (this) {
					notifyAll();
				}
			} else {
				for (Map.Entry<Component, List<Color>> entry : map.entrySet()) {
					entry.getKey().setBackground(entry.getValue().get(frameCount));
				}
			}
		}
	}

	private static class FadeComponent {

		private final Component jComponent;
		private final Color color;

		public FadeComponent(Component jComponent) {
			this.jComponent = jComponent;
			this.color = jComponent.getBackground();
		}

		public Component getComponent() {
			return jComponent;
		}

		public Color getColor() {
			return color;
		}
	}
}
