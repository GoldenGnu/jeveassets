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
package net.nikr.eve.jeveasset.gui.shared.filter;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
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
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.AllColumn;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;


class FilterPanel<E> {

	private enum FilterPanelAction {
		FILTER, FILTER_TIMER, REMOVE
	}

	private JPanel jPanel;
	private GroupLayout layout;

	private JCheckBox jEnabled;
	private JComboBox jLogic;
	private JComboBox jColumn;
	private JComboBox jCompare;
	private JTextField jText;
	private JComboBox jCompareColumn;
	private JDateChooser jDate;

	private JLabel jSpacing;
	private JButton jRemove;

	private Timer timer;

	private FilterGui<E> gui;
	private FilterControl<E> filterControl;
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

		jLogic = new JComboBox(LogicType.values());
		jLogic.addActionListener(listener);
		jLogic.setActionCommand(FilterPanelAction.FILTER.name());

		jColumn = new JComboBox(allColumns.toArray());
		jColumn.addActionListener(listener);
		jColumn.setActionCommand(FilterPanelAction.FILTER.name());

		jCompare = new JComboBox();
		jCompare.addActionListener(listener);
		jCompare.setActionCommand(FilterPanelAction.FILTER.name());

		jText = new JTextField();
		jText.getDocument().addDocumentListener(listener);
		jText.addKeyListener(listener);

		jCompareColumn = new JComboBox();
		jCompareColumn.addActionListener(listener);
		jCompareColumn.setActionCommand(FilterPanelAction.FILTER.name());

		jDate = new JDateChooser(Settings.getNow());
		jDate.setDateFormatString(Formater.COLUMN_FORMAT);
		JCalendar jCalendar = jDate.getJCalendar();
		jCalendar.setTodayButtonText("Today");
		jCalendar.setTodayButtonVisible(true);
		JTextFieldDateEditor dateEditor = (JTextFieldDateEditor) jDate.getDateEditor().getUiComponent();
		dateEditor.setEnabled(false);
		dateEditor.setBorder(null);
		dateEditor.setDisabledTextColor(Color.BLACK);
		jDate.addPropertyChangeListener(listener);

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
				.addComponent(jLogic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jColumn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jCompare, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jText, 100, 100, Integer.MAX_VALUE)
				.addComponent(jCompareColumn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jSpacing, 0, 0, Integer.MAX_VALUE)
				.addComponent(jRemove, 30, 30, 30)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jEnabled, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jLogic, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jColumn, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jCompare, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jText, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jCompareColumn, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jDate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jSpacing, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
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
		return new Filter(logic, column, compare, text);
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
			jDate.setDate(Formater.columnStringToDate(filter.getText()));
		} else {
			jText.setText(filter.getText());
			timer.stop();
		}
		loading = false;
	}

	private String getDataString() {
		return  Formater.columnDate(jDate.getDate());
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
		jCompare.setModel(new DefaultComboBoxModel(compareTypes));
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
		Object[] compareColumns;
		if (isNumericCompare()) {
			compareColumns = numericColumns.toArray();
		} else if (isDateCompare()) {
			compareColumns = dateColumns.toArray();
		} else {
			compareColumns = filterControl.getColumns().toArray();
		}
		jCompareColumn.setModel(new DefaultComboBoxModel(compareColumns));
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
			jText.setBackground(new Color(255, 200, 200));
		}
		timer.stop();
		refilter();
	}

	private class ListenerClass implements ActionListener, KeyListener, DocumentListener, PropertyChangeListener {

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
		public void propertyChange(final PropertyChangeEvent evt) {
			if ("date".equals(evt.getPropertyName())) {
				refilter();
			}
		}
	}
}
