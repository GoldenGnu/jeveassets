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

package net.nikr.eve.jeveasset.gui.tabs.ratting;

import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.data.api.raw.RawJournalRefType;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.JStatusLabel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.ColorIcon;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.JFreeChartUtil;
import net.nikr.eve.jeveasset.gui.shared.components.JDateChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionList;
import net.nikr.eve.jeveasset.gui.tabs.tracker.QuickDate;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.i18n.TabsRattingTracker;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;


public class RattingTrackerTab extends JMainTabSecondary {

	private enum RattingTrackerAction {
		QUICK_DATE,
		INCLUDE_ZERO,
		LOGARITHMIC
	}

	private static final int PANEL_WIDTH_MINIMUM = 160;
	private static final ZoneId CHART_ZONE = ZoneId.of("GMT");

	//GUI
	private final JComboBox<QuickDate> jQuickDate;
	private final JDateChooser jFrom;
	private final JDateChooser jTo;
	private final JMultiSelectionList<String> jOwners;
	private final JCheckBoxMenuItem jIncludeZero;
	private final JRadioButtonMenuItem jLogarithmic;

	//Graph
	private final JFreeChart jFreeChart;
	private final ChartPanel jChartPanel;
	private final XYLineAndShapeRenderer renderer;
	private final DateAxis domainAxis;
	private final LogarithmicAxis rangeLogarithmicAxis;
	private final NumberAxis rangeLinearAxis;

	//Listener
	private final ListenerClass listener = new ListenerClass();

	//Data
	private final List<String> shownOrder = new ArrayList<>();
	private final Map<String, TimePeriodValues> series = new HashMap<>();
	private final Map<String, Double> seriesMax = new HashMap<>();
	private final Map<String, Double> seriesTotals = new HashMap<>();
	private final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();

	public static final String NAME = "rattingtracker"; //Not to be changed!

	public RattingTrackerTab(Program program) {
		super(program, NAME, TabsRattingTracker.get().title(), Images.TOOL_JOURNAL.getIcon(), true);

		jQuickDate = new JComboBox<>(QuickDate.values());
		jQuickDate.setActionCommand(RattingTrackerAction.QUICK_DATE.name());
		jQuickDate.addActionListener(listener);

		JLabel jFromLabel = new JLabel(TabsRattingTracker.get().from());
		jFrom = new JDateChooser(true);
		jFrom.addDateChangeListener(listener);

		JLabel jToLabel = new JLabel(TabsRattingTracker.get().to());
		jTo = new JDateChooser(true);
		jTo.addDateChangeListener(listener);

		JSeparator jDateSeparator = new JSeparator();

		JDropDownButton jSettings = new JDropDownButton(Images.DIALOG_SETTINGS.getIcon());

		jIncludeZero = new JCheckBoxMenuItem(TabsRattingTracker.get().includeZero());
		jIncludeZero.setSelected(true);
		jIncludeZero.setActionCommand(RattingTrackerAction.INCLUDE_ZERO.name());
		jIncludeZero.addActionListener(listener);
		jSettings.add(jIncludeZero);

		jSettings.addSeparator();

		ButtonGroup buttonGroup = new ButtonGroup();

		JRadioButtonMenuItem jLinear = new JRadioButtonMenuItem(TabsRattingTracker.get().scaleLinear());
		jLinear.setSelected(true);
		jLinear.setActionCommand(RattingTrackerAction.LOGARITHMIC.name());
		jLinear.addActionListener(listener);
		jSettings.add(jLinear);
		buttonGroup.add(jLinear);

		jLogarithmic = new JRadioButtonMenuItem(TabsRattingTracker.get().scaleLogarithmic());
		jLogarithmic.setSelected(false);
		jLogarithmic.setActionCommand(RattingTrackerAction.LOGARITHMIC.name());
		jLogarithmic.addActionListener(listener);
		jSettings.add(jLogarithmic);
		buttonGroup.add(jLogarithmic);

		jOwners = new JMultiSelectionList<>();
		jOwners.getSelectionModel().addListSelectionListener(listener);
		JScrollPane jOwnersScroll = new JScrollPane(jOwners);

		domainAxis = JFreeChartUtil.createDateAxis();
		rangeLogarithmicAxis = JFreeChartUtil.createLogarithmicAxis(true);
		rangeLinearAxis = JFreeChartUtil.createNumberAxis(true);

		renderer = JFreeChartUtil.createRenderer();
		renderer.setDefaultToolTipGenerator(new XYToolTipGenerator() {
			@Override
			public String generateToolTip(XYDataset dataset, int series, int item)	{
				Date date = new Date(dataset.getX(series, item).longValue());
				Number value = dataset.getY(series, item);
				return TabsRattingTracker.get().graphToolTip(dataset.getSeriesKey(series), Formatter.iskFormat(value), Formatter.columnDateOnly(date));
			}
		});

		XYPlot plot = JFreeChartUtil.createPlot(dataset, domainAxis, rangeLinearAxis, renderer);
		jFreeChart = JFreeChartUtil.createChart(plot);
		jChartPanel = JFreeChartUtil.createChartPanel(jFreeChart);

		int gapWidth = 5;
		int labelWidth = Math.max(jFromLabel.getPreferredSize().width, jToLabel.getPreferredSize().width);
		int panelWidth = PANEL_WIDTH_MINIMUM;
		panelWidth = Math.max(panelWidth, jFrom.getPreferredSize().width + labelWidth + gapWidth);
		panelWidth = Math.max(panelWidth, jTo.getPreferredSize().width + labelWidth + gapWidth);
		int dateWidth = panelWidth - labelWidth - gapWidth;
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jChartPanel)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jQuickDate, panelWidth, panelWidth, panelWidth)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
							.addComponent(jFromLabel, labelWidth, labelWidth, labelWidth)
							.addComponent(jToLabel, labelWidth, labelWidth, labelWidth)
						)
						.addGap(gapWidth)
						.addGroup(layout.createParallelGroup()
							.addComponent(jFrom, dateWidth, dateWidth, dateWidth)
							.addComponent(jTo, dateWidth, dateWidth, dateWidth)
						)
					)
					.addComponent(jDateSeparator, panelWidth, panelWidth, panelWidth)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jSettings)
					)
					.addComponent(jOwnersScroll, panelWidth, panelWidth, panelWidth)
				)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jChartPanel)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jQuickDate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addGroup(layout.createParallelGroup()
						.addComponent(jFromLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jFrom, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jToLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jTo, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
					.addComponent(jDateSeparator, 3, 3, 3)
					.addGroup(layout.createParallelGroup()
						.addComponent(jSettings, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
					.addComponent(jOwnersScroll, 70, 70, Integer.MAX_VALUE)
				)
		);
	}

	@Override
	public void updateData() {
		updateGUI();
		createData();
		updateChart();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateStatusbar(); //Must be done after being shown
			}
		});
	}

	@Override
	public void updateCache() { }

	@Override
	public void clearData() { }

	@Override
	public Collection<LocationType> getLocations() {
		return new ArrayList<>(); //No Location
	}

	private void updateGUI() {
		Set<String> ownerSet = new HashSet<>();
		try {
			program.getProfileData().getJournalEventList().getReadWriteLock().readLock().lock();
			for (MyJournal journal : program.getProfileData().getJournalEventList()) {
				if (isRattingEntry(journal)) {
					ownerSet.add(journal.getOwnerName());
				}
			}
		} finally {
			program.getProfileData().getJournalEventList().getReadWriteLock().readLock().unlock();
		}
		List<String> owners = new ArrayList<>(ownerSet);
		Collections.sort(owners);
		listener.valueIsAdjusting = true;
		if (owners.isEmpty()) {
			jOwners.setEnabled(false);
			jOwners.setModel(new AbstractListModel<String>() {
				@Override
				public int getSize() {
					return 1;
				}

				@Override
				public String getElementAt(int index) {
					return TabsRattingTracker.get().noDataFound();
				}
			});
		} else {
			List<String> selectedOwners = jOwners.getSelectedValuesList();
			jOwners.setEnabled(true);
			jOwners.setModel(new AbstractListModel<String>() {
				@Override
				public int getSize() {
					return owners.size();
				}
				@Override
				public String getElementAt(int index) {
					return owners.get(index);
				}
			});
			if (selectedOwners.isEmpty()) {
				jOwners.selectAll();
			} else {
				for (int i = 0; i < owners.size(); i++) {
					if (selectedOwners.contains(owners.get(i))) {
						jOwners.addSelectionInterval(i, i);
					}
				}
			}
		}
		listener.valueIsAdjusting = false;
	}

	private void createData() {
		Date from = getFromDate();
		Date to = getToDate();
		//Remove All
		while (dataset.getSeriesCount() != 0) {
			dataset.removeSeries(0);
		}
		series.clear();
		seriesMax.clear();
		seriesTotals.clear();
		shownOrder.clear();
		TreeMap<Date, Map<String, Double>> values = new TreeMap<>();
		Set<String> names = new HashSet<>();
		String grandTotal = TabsRattingTracker.get().grandTotal();
		List<String> selectedOwners = getSelectedOwners();
		if (selectedOwners.isEmpty()) {
			return;
		}
		try {
			program.getProfileData().getJournalEventList().getReadWriteLock().readLock().lock();
			for (MyJournal journal : program.getProfileData().getJournalEventList()) {
				if (!isRattingEntry(journal)) {
					continue;
				}
				final Date date = journal.getDate();
				//Filter
				if ((from != null && !date.after(from)) || (to != null && !date.before(to))) {
					continue;
				}
				String ownerName = journal.getOwnerName();
				if (!selectedOwners.contains(ownerName)) {
					continue;
				}
				Date bucket = toDay(date);
				Map<String, Double> map = values.get(bucket);
				if (map == null) {
					map = new HashMap<>();
					values.put(bucket, map);
				}
				double amount = journal.getAmount() != null ? journal.getAmount() : 0.0;
				names.add(ownerName);
				double ownerTotal = map.getOrDefault(ownerName, 0.0);
				map.put(ownerName, ownerTotal + amount);
				double total = map.getOrDefault(grandTotal, 0.0);
				map.put(grandTotal, total + amount);
				seriesTotals.put(ownerName, seriesTotals.getOrDefault(ownerName, 0.0) + amount);
				seriesTotals.put(grandTotal, seriesTotals.getOrDefault(grandTotal, 0.0) + amount);
			}
		} finally {
			program.getProfileData().getJournalEventList().getReadWriteLock().readLock().unlock();
		}
		if (values.isEmpty()) {
			return;
		}
		if (names.size() > 1) {
			names.add(grandTotal);
		}
		Date startDate = values.keySet().iterator().next();
		Date endDate = values.lastKey();
		if (from != null) {
			startDate = toDay(from);
		}
		if (to != null) {
			endDate = toDay(to);
		}
		LocalDate startLocal = startDate.toInstant().atZone(CHART_ZONE).toLocalDate();
		LocalDate endLocal = endDate.toInstant().atZone(CHART_ZONE).toLocalDate();
		if (startLocal.isAfter(endLocal)) {
			return;
		}
		for (LocalDate date = startLocal; !date.isAfter(endLocal); date = date.plusDays(1)) {
			Date bucket = Date.from(date.atStartOfDay().atZone(CHART_ZONE).toInstant());
			Map<String, Double> map = values.get(bucket);
			for (String name : names) {
				final double value = map != null ? map.getOrDefault(name, 0.0) : 0.0;
				TimePeriodValues timePeriod = series.get(name);
				if (timePeriod == null) {
					timePeriod = new TimePeriodValues(name);
					series.put(name, timePeriod);
				}
				double max = seriesMax.getOrDefault(name, 0.0);
				max = Math.max(max, value);
				seriesMax.put(name, max);
				SimpleTimePeriod simpleTimePeriod = new SimpleTimePeriod(bucket, bucket);
				timePeriod.add(simpleTimePeriod, value);
			}
		}
		shownOrder.addAll(names);
		Collections.sort(shownOrder, String.CASE_INSENSITIVE_ORDER);
		for (String ownerName : shownOrder) {
			dataset.addSeries(series.get(ownerName));
		}
	}

	private void updateChart() {
		double max = 0;
		int count = 0;
		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			String name = shownOrder.get(i);
			max = Math.max(max, seriesMax.getOrDefault(name, 0.0));
			count = Math.max(count, dataset.getItemCount(i));
		}
		JFreeChartUtil.updateTickScale(domainAxis, rangeLinearAxis, max);
		renderer.setDefaultShapesVisible(count < 2);
	}

	private void updateStatusbar() {
		clearStatusbarLabels();
		for (int i = 0; i < shownOrder.size(); i++) {
			final String name = shownOrder.get(i);
			final Double value = seriesTotals.get(name);
			if (value != null) {
				final Color color = (Color) renderer.getSeriesPaint(i);
				JStatusLabel jStatusLabel = StatusPanel.createLabel(name, new ColorIcon(color), AutoNumberFormat.ISK);
				jStatusLabel.setNumber(value);
				addStatusbarLabel(jStatusLabel);
			}
		}
		program.getStatusPanel().tabChanged();
	}

	private List<String> getSelectedOwners() {
		return jOwners.getSelectedValuesList();
	}

	private Date getFromDate() {
		LocalDate date = jFrom.getDate();
		if (date == null) {
			return null;
		}
		Instant instant = date.atStartOfDay().atZone(CHART_ZONE).toInstant(); //Start of day - GMT
		return Date.from(instant);
	}

	private Date getToDate() {
		LocalDate date = jTo.getDate();
		if (date == null) {
			return null;
		}
		Instant instant = date.atTime(23, 59, 59).atZone(CHART_ZONE).toInstant(); //End of day - GMT
		return Date.from(instant);
	}

	private Date toDay(Date date) {
		Instant instant = date.toInstant();
		LocalDate localDate = instant.atZone(CHART_ZONE).toLocalDate();
		return Date.from(localDate.atStartOfDay().atZone(CHART_ZONE).toInstant());
	}

	private boolean isRattingEntry(MyJournal journal) {
		RawJournalRefType refType = journal.getRefType();
		if (refType == null) {
			return false;
		}
		String name = refType.name();
		return name.contains("BOUNTY") || name.contains("ESS");
	}

	private class ListenerClass implements ActionListener, DateChangeListener, ListSelectionListener {

		boolean valueIsAdjusting = false;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (RattingTrackerAction.QUICK_DATE.name().equals(e.getActionCommand())) {
				QuickDate quickDate = (QuickDate) jQuickDate.getSelectedItem();
				if (quickDate == QuickDate.RESET) {
					jTo.clearDate();
					jFrom.clearDate();
				} else {
					Date toDate = getToDate();
					if (toDate == null) {
						toDate = new Date(); //now
					}
					Date fromDate = quickDate.apply(toDate);
					if (fromDate != null) {
						jFrom.setDate(fromDate);
					}
				}
			} else if (RattingTrackerAction.INCLUDE_ZERO.name().equals(e.getActionCommand())) {
				rangeLogarithmicAxis.setAutoRangeIncludesZero(jIncludeZero.isSelected());
				rangeLinearAxis.setAutoRangeIncludesZero(jIncludeZero.isSelected());
			} else if (RattingTrackerAction.LOGARITHMIC.name().equals(e.getActionCommand())) {
				if (jLogarithmic.isSelected()) {
					jFreeChart.getXYPlot().setRangeAxis(rangeLogarithmicAxis);
				} else {
					jFreeChart.getXYPlot().setRangeAxis(rangeLinearAxis);
				}
			}
		}

		@Override
		public void dateChanged(DateChangeEvent event) {
			Date from = getFromDate();
			Date to = getToDate();
			QuickDate quickDate = (QuickDate) jQuickDate.getSelectedItem();
			if (!quickDate.isValid(from, to)) {
				QuickDate selected = quickDate.getSelected(from, to);
				jQuickDate.setSelectedItem(selected);
			}
			if (from != null && to != null && from.after(to)) {
				jTo.setDate(from);
			}
			createData();
			updateChart();
			updateStatusbar();
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() || valueIsAdjusting) {
				return;
			}
			createData();
			updateChart();
			updateStatusbar();
		}
	}
}
