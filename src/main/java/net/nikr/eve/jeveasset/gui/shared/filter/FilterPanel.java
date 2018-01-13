/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Colors;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JDateChooser;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.AllColumn;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;


class FilterPanel<E> {

	private enum FilterPanelAction {
		FILTER, FILTER_TIMER, REMOVE
	}

	private final JPanel jPanel;
	private final GroupLayout layout;

	private final JCheckBox jEnabled;
	private final JComboBox<LogicType> jLogic;
	private final JComboBox<EnumTableColumn<E>> jColumn;
	private final JComboBox<CompareType> jCompare;
	private final JTextField jText;
	private final JComboBox<EnumTableColumn<E>> jCompareColumn;
	private final JDateChooser jDate;

	private final JLabel jSpacing;
	private final JButton jRemove;

	private final Timer timer;

	private final FilterGui<E> gui;
	private final FilterControl<E> filterControl;
	private final List<EnumTableColumn<E>> allColumns;
	private final List<EnumTableColumn<E>> numericColumns;
	private final List<EnumTableColumn<E>> dateColumns;

	private boolean loading = false;

	FilterPanel(final FilterGui<E> gui, final FilterControl<E> filterControl) {
		this.gui = gui;
		this.filterControl = filterControl;

		ListenerClass listener = new ListenerClass();

		allColumns = new ArrayList<EnumTableColumn<E>>();
		allColumns.add(new AllColumn<E>());
		allColumns.addAll(filterControl.getColumns());

		numericColumns = new ArrayList<EnumTableColumn<E>>();
		for (EnumTableColumn<E> object : filterControl.getColumns()) {
			if (filterControl.isNumeric(object)) {
				numericColumns.add(object);
			}
		}

		dateColumns = new ArrayList<EnumTableColumn<E>>();
		for (EnumTableColumn<E> object : filterControl.getColumns()) {
			if (filterControl.isDate(object)) {
				dateColumns.add(object);
			}
		}

		jEnabled = new JCheckBox();
		jEnabled.setSelected(true);
		jEnabled.addActionListener(listener);
		jEnabled.setActionCommand(FilterPanelAction.FILTER.name());

		jLogic = new JComboBox<LogicType>(LogicType.values());
		jLogic.setPrototypeDisplayValue(LogicType.AND);
		jLogic.addActionListener(listener);
		jLogic.setActionCommand(FilterPanelAction.FILTER.name());

		JComboBox<String> jComboBox = new JComboBox<String>();
		FontMetrics fontMetrics = jComboBox.getFontMetrics(jComboBox.getFont());
		EnumTableColumn<E>  longestColumn = null;
		for (EnumTableColumn<E> column : allColumns) {
			if (longestColumn == null || fontMetrics.stringWidth(longestColumn.getColumnName()) < fontMetrics.stringWidth(column.getColumnName())) {
				longestColumn = column;
			}
		}

		jColumn = new JComboBox<EnumTableColumn<E>>(new ListComboBoxModel<EnumTableColumn<E>>(allColumns));
		jColumn.setPrototypeDisplayValue(longestColumn);
		jColumn.addActionListener(listener);
		jColumn.setActionCommand(FilterPanelAction.FILTER.name());

		jCompare = new JComboBox<CompareType>();
		jCompare.setPrototypeDisplayValue(CompareType.CONTAINS_NOT_COLUMN);
		jCompare.addActionListener(listener);
		jCompare.setActionCommand(FilterPanelAction.FILTER.name());

		jText = new JTextField();
		jText.getDocument().addDocumentListener(listener);
		jText.addKeyListener(listener);

		jCompareColumn = new JComboBox<EnumTableColumn<E>>();
		jCompareColumn.setPrototypeDisplayValue(longestColumn);
		jCompareColumn.addActionListener(listener);
		jCompareColumn.setActionCommand(FilterPanelAction.FILTER.name());

		jDate = new JDateChooser(false);
		jDate.addDateChangeListener(listener);

		jSpacing = new JLabel();

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
				.addComponent(jEnabled, 30, 30, 30)
				.addComponent(jLogic, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jColumn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jCompare, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jText, 100, 100, Integer.MAX_VALUE)
				.addComponent(jCompareColumn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jDate, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jSpacing, 0, 0, Integer.MAX_VALUE)
				.addComponent(jRemove, 30, 30, 30)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jEnabled, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jLogic, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jColumn, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jCompare, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jText, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jCompareColumn, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jDate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSpacing, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jRemove, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
		updateNumeric(false);
	}

	private FilterPanel<E> getThis() {
		return this;
	}

	JPanel getPanel() {
		return jPanel;
	}

	FilterMatcher<E> getMatcher() {
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
		return new FilterMatcher<E>(filterControl, logic, column, compare, text, enabled);
	}

	Filter getFilter() {
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
		return new Filter(logic, column, compare, text, enabled);
	}

	void setFilter(final Filter filter) {
		loading = true;
		jEnabled.setEnabled(true);
		jLogic.setSelectedItem(filter.getLogic());
		jColumn.setSelectedItem(filter.getColumn());
		jCompare.setSelectedItem(filter.getCompareType());
		if (isColumnCompare()) {
			jCompareColumn.setSelectedItem(filterControl.valueOf(filter.getText()));
		} else if (isDateCompare()) {
			setDateString(Formater.columnStringToDate(filter.getText()));
		} else {
			jText.setText(filter.getText());
			timer.stop();
		}
		loading = false;
	}

	private void setDateString(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime());
		LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.of("GMT")).toLocalDate();
		jDate.setDate(localDate);
	}

	private String getDataString() {
		LocalDate date = jDate.getDate();
		Instant instant = date.atStartOfDay().atZone(ZoneId.of("GMT")).toInstant(); //End of day - GMT
		return  Formater.columnDate(Date.from(instant));
	}

	private void refilter() {
		if (!loading) {
			gui.refilter();
		}
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
		jCompare.setModel(new ListComboBoxModel<CompareType>(compareTypes));
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
			compareColumns = new ArrayList<EnumTableColumn<E>>(numericColumns);
		} else if (isDateCompare()) {
			compareColumns = new ArrayList<EnumTableColumn<E>>(dateColumns);
		} else {
			compareColumns = new ArrayList<EnumTableColumn<E>>(filterControl.getColumns());
		}
		jCompareColumn.setModel(new ListComboBoxModel<EnumTableColumn<E>>(compareColumns));
		for (Object column : compareColumns) {
			if (column.equals(object) && saveIndex) {
				jCompareColumn.setSelectedItem(column);
			}
		}
	}

	private void processFilterAction(final ActionEvent e) {
		if (jColumn.equals(e.getSource())) {
			updateNumeric(true);
		}
		if (jCompare.equals(e.getSource())) {
			updateCompare(true);
		}
		if (jEnabled.isSelected()) {
			jText.setBackground(Color.WHITE);
		} else {
			jText.setBackground(Colors.LIGHT_RED.getColor());
		}
		timer.stop();
		refilter();
	}

	private class ListenerClass implements ActionListener, KeyListener, DocumentListener, DateChangeListener {

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
				gui.remove(getThis());
				gui.addEmpty();
				refilter();
			}
			if (FilterPanelAction.FILTER.name().equals(e.getActionCommand())) {
				processFilterAction(e);
			}
			if (FilterPanelAction.FILTER_TIMER.name().equals(e.getActionCommand())) {
				if (!Settings.get().isFilterOnEnter()) {
					processFilterAction(e);
				}
			}
		}

		@Override
		public void dateChanged(DateChangeEvent event) {
			refilter();
		}
	}
}
