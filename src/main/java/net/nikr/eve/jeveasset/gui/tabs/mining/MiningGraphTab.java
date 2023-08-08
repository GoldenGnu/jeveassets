/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.tabs.mining;

import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyMining;
import net.nikr.eve.jeveasset.data.sde.Item;
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
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.gui.tabs.tracker.QuickDate;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsMining;
import net.nikr.eve.jeveasset.i18n.TabsTracker;
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


public class MiningGraphTab extends JMainTabSecondary {

	private enum MiningGraphAction {
		TYPE_CHANGE,
		QUICK_DATE,
		INCLUDE_ZERO,
		LOGARITHMIC,
		SHOW_ALL,
		UPDATE_SHOWN,
	}

	private enum Type {
		ORE(AutoNumberFormat.ISK) {
			@Override
			public double toValue(MyMining mining) {
				return mining.getValue();
			}
			@Override
			public String getText() {
				return TabsMining.get().valueOre();
			}
		},
		REPROCESSED(AutoNumberFormat.ISK) {
			@Override
			public double toValue(MyMining mining) {
				return mining.getValueReprocessed();
			}
			@Override
			public String getText() {
				return TabsMining.get().valueReprocessed();
			}
		},
		REPROCESSED_MAX(AutoNumberFormat.ISK) {
			@Override
			public double toValue(MyMining mining) {
				return mining.getValueReprocessedMax();
			}
			@Override
			public String getText() {
				return TabsMining.get().valueReprocessedMax();
			}
		},
		VOLUME(AutoNumberFormat.DOUBLE) {
			@Override
			public double toValue(MyMining mining) {
				return mining.getVolumeTotal();
			}
			@Override
			public String getText() {
				return TabsMining.get().volume();
			}
		},
		COUNT(AutoNumberFormat.ITEMS) {
			@Override
			public double toValue(MyMining mining) {
				return mining.getCount();
			}
			@Override
			public String getText() {
				return TabsMining.get().count();
			}
		};

		private final AutoNumberFormat format;

		private Type(AutoNumberFormat format) {
			this.format = format;
		}

		public String format(Number value) {
			return JMenuInfo.format(value, format);
		}

		private AutoNumberFormat getAutoNumberFormat() {
			return format;
		}

		public abstract double toValue(MyMining mining);
		public abstract String getText();

		@Override
		public String toString() {
			return getText();
		}
	}

	private final int PANEL_WIDTH_MINIMUM = 215;

	//GUI
	private final JComboBox<Type> jType;
	private final JComboBox<QuickDate> jQuickDate;
	private final JDateChooser jFrom;
	private final JDateChooser jTo;
	private final JDropDownButton jShow;
	private final JCheckBoxMenuItem jAll;
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
	private final TotalComparator comparator = new TotalComparator();
	private final List<String> shownOrder = new ArrayList<>();
	private final Map<String, TimePeriodValues> series = new HashMap<>();
	private final Map<String, Double> seriesMax = new HashMap<>();
	private final Map<String, Double> seriesTotals = new HashMap<>();
	private final Map<String, String> seriesGroup = new HashMap<>();
	private final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
	private final Map<String, JCheckBoxMenuItem> items = new TreeMap<>();

	//Settings ToDo
	private Date fromDate = null;
	private Date toDate = null;
	public static final String NAME = "mininggraph"; //Not to be changed!


	public MiningGraphTab(Program program) {
		super(program, NAME, TabsMining.get().miningGraph(), Images.TOOL_MINING_GRAPH.getIcon(), true);

		jType = new JComboBox<>(Type.values());
		jType.setActionCommand(MiningGraphAction.TYPE_CHANGE.name());
		jType.addActionListener(listener);

		JSeparator jTypeSeparator = new JSeparator();

		jQuickDate = new JComboBox<>(QuickDate.values());
		jQuickDate.setActionCommand(MiningGraphAction.QUICK_DATE.name());
		jQuickDate.addActionListener(listener);

		JLabel jFromLabel = new JLabel(TabsMining.get().from());
		jFrom = new JDateChooser(true);
		if (fromDate != null) {
			jFrom.setDate(dateToLocalDate(fromDate));
		}
		jFrom.addDateChangeListener(listener);

		JLabel jToLabel = new JLabel(TabsMining.get().to());
		jTo = new JDateChooser(true);
		if (toDate != null) {
			jTo.setDate(dateToLocalDate(toDate));
		}
		jTo.addDateChangeListener(listener);

		JSeparator jDateSeparator = new JSeparator();

		jShow = new JDropDownButton(TabsMining.get().show(), Images.LOC_INCLUDE.getIcon());
		jShow.setHorizontalAlignment(JButton.LEFT);

		jAll = new JCheckBoxMenuItem(General.get().all());
		jAll.setSelected(true);
		jAll.setActionCommand(MiningGraphAction.SHOW_ALL.name());
		jAll.addActionListener(listener);
		jAll.setFont(new Font(jAll.getFont().getName(), Font.ITALIC, jAll.getFont().getSize()));

		JSeparator jShowSeparator = new JSeparator();

		JDropDownButton jSettings = new JDropDownButton(Images.DIALOG_SETTINGS.getIcon());

		jIncludeZero = new JCheckBoxMenuItem(TabsMining.get().includeZero());
		jIncludeZero.setSelected(true);
		jIncludeZero.setActionCommand(MiningGraphAction.INCLUDE_ZERO.name());
		jIncludeZero.addActionListener(listener);
		jSettings.add(jIncludeZero);

		jSettings.addSeparator();

		ButtonGroup buttonGroup = new ButtonGroup();

		JRadioButtonMenuItem jLinear = new JRadioButtonMenuItem(TabsMining.get().scaleLinear());
		jLinear.setSelected(true);
		jLinear.setActionCommand(MiningGraphAction.LOGARITHMIC.name());
		jLinear.addActionListener(listener);
		jSettings.add(jLinear);
		buttonGroup.add(jLinear);

		jLogarithmic = new JRadioButtonMenuItem(TabsMining.get().scaleLogarithmic());
		jLogarithmic.setSelected(false);
		jLogarithmic.setActionCommand(MiningGraphAction.LOGARITHMIC.name());
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
				Type type = jType.getItemAt(jType.getSelectedIndex());
				return TabsMining.get().graphToolTip(dataset.getSeriesKey(series), type.format(value), Formatter.columnDateOnly(date));
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
					.addComponent(jType, panelWidth, panelWidth, panelWidth)
					.addComponent(jTypeSeparator, panelWidth, panelWidth, panelWidth)
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
					.addComponent(jShow, panelWidth, panelWidth, panelWidth)
					.addComponent(jShowSeparator, panelWidth, panelWidth, panelWidth)
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
					.addComponent(jType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTypeSeparator, 3, 3, 3)
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
					.addComponent(jShow, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jShowSeparator, 3, 3, 3)
					.addGroup(layout.createParallelGroup()
						.addComponent(jSettings, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
					.addComponent(jOwnersScroll, 70, 70, Integer.MAX_VALUE)
				)
		);
	}

	@Override
	public void updateData() {
		createData();
		updateGUI();
		updateShown();
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
		Set<String> ownerSet = new HashSet<>(program.getOwnerNames(false));
		try {
			program.getProfileData().getMiningEventList().getReadWriteLock().readLock().lock();
			for (MyMining mining : program.getProfileData().getMiningEventList()) {
				ownerSet.add(mining.getCharacterName());
				if(mining.isForCorporation()) {
					ownerSet.add(mining.getCorporationName());
				}
			}
		} finally {
			program.getProfileData().getMiningEventList().getReadWriteLock().readLock().unlock();
 		}
		List<String> owners = new ArrayList<>(ownerSet);
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
					return TabsTracker.get().noDataFound();
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
			
			if (items.isEmpty()) {
				jOwners.selectAll();
			} else {
				ListModel<String> model = jOwners.getModel();
				for (int i = 0; i < model.getSize(); i++) {
					String ownerName = model.getElementAt(i);
					if (selectedOwners.contains(ownerName)) {
						jOwners.addSelectionInterval(i, i);
					}
				}
			}
		}

		createData();

		jShow.removeAll();
		if (shownOrder.isEmpty()) {
			jShow.setEnabled(false);
		} else {
			jShow.setEnabled(true);
			jShow.add(jAll, true);
			List<String> itemsList = new ArrayList<>(shownOrder);
			Collections.sort(itemsList, comparator);
			for (String s : itemsList) {
				JCheckBoxMenuItem jMenuItem = items.get(s);
				if (jMenuItem == null) { //Create new item
					jMenuItem = new JCheckBoxMenuItem(s);
					jMenuItem.setSelected(true);
					jMenuItem.setActionCommand(MiningGraphAction.UPDATE_SHOWN.name());
					jMenuItem.addActionListener(listener);
					items.put(s, jMenuItem);
				}
				jShow.add(jMenuItem, true);
			}
		}
		listener.valueIsAdjusting = false;
		updateShown();
	}

	private void createData() {
		Date from = getFromDate();
		Date to = getToDate();
		Type type = jType.getItemAt(jType.getSelectedIndex());
		//Remove All
		while (dataset.getSeriesCount() != 0) {
			dataset.removeSeries(0);
		}
		series.clear();
		shownOrder.clear();
		seriesMax.clear();
		seriesTotals.clear();
		seriesGroup.clear();
		//dataset
		Map<String, Set<String>> groupCounts = new HashMap<>();
		Map<Date, Map<String, Double>> values = new TreeMap<>();
		Set<String> names = new HashSet<>();
		final String grandTotal = TabsMining.get().grandTotal();
		List<String> selectedOwners = getSelectedOwners();
		try {
			program.getProfileData().getMiningEventList().getReadWriteLock().readLock().lock();
			for (MyMining mining : program.getProfileData().getMiningEventList()) {
				final Date date = mining.getDate();
				//Filter
				if ((from != null && !date.after(from)) || (to != null && !date.before(to))) {
					continue;
				}
				if (!selectedOwners.contains(mining.getCharacterName()) || (mining.isForCorporation() && !selectedOwners.contains(mining.getCorporationName()))) {
					continue;
				}
				//Type
				final String typeName = getTypeName(mining.getItem());
				final double value = type.toValue(mining);
				Map<String, Double> map = values.get(date);
				if (map == null) {
					map = new HashMap<>();
					values.put(date, map);
				}
				names.add(typeName);
				Double typeTotal = map.getOrDefault(typeName, 0.0);
				map.put(typeName, typeTotal + value);
				//Group Total
				final String groupName = TabsMining.get().groupTotal(mining.getItem().getGroup());
				double groupTotal = map.getOrDefault(groupName, 0.0);
				names.add(groupName);
				map.put(groupName, groupTotal + value);
				Set<String> set = groupCounts.get(groupName);
				if (set == null) {
					set = new HashSet<>();
					groupCounts.put(groupName, set);
				}
				set.add(typeName);
				seriesGroup.put(typeName, groupName);
				//GrandTotal
				double total = map.getOrDefault(grandTotal, 0.0);
				names.add(grandTotal);
				map.put(grandTotal, total + value);
				//Totals
				double d;
				d = seriesTotals.getOrDefault(typeName, 0.0);
				d = d + value;
				seriesTotals.put(typeName, d);
				d = seriesTotals.getOrDefault(groupName, 0.0);
				d = d + value;
				seriesTotals.put(groupName, d);
				d = seriesTotals.getOrDefault(grandTotal, 0.0);
				d = d + value;
				seriesTotals.put(grandTotal, d);
			}
		} finally {
			program.getProfileData().getMiningEventList().getReadWriteLock().readLock().unlock();
 		}
		//Remove group totals for groups with only one entry
		for (Map.Entry<String, Set<String>> entry : groupCounts.entrySet()) {
			if (entry.getValue().size() < 2) {
				names.remove(entry.getKey());
			}
		}
		//Remove grand total if only one entry
		if (names.size() == 1) {
			names.remove(grandTotal);
		}
		for (Map.Entry<Date, Map<String, Double>> entry : values.entrySet()) {
			final Date date = entry.getKey();
			Map<String, Double> map = entry.getValue();
			for (String name : names) {
				final double value = map.getOrDefault(name, 0.0);
				TimePeriodValues timePeriod = series.get(name);
				if (timePeriod == null) {
					timePeriod = new TimePeriodValues(name);
					series.put(name, timePeriod);
				}
				double max = seriesMax.getOrDefault(name, 0.0);
				max = Math.max(max, value);
				seriesMax.put(name, max);
				SimpleTimePeriod simpleTimePeriod = new SimpleTimePeriod(date, date);
				timePeriod.add(simpleTimePeriod, value);
			}
		}
		shownOrder.addAll(names);
		Collections.sort(shownOrder, comparator);
		//Add all
		for (String typeName : shownOrder) {
			dataset.addSeries(series.get(typeName));
		}
	}

	private String getTypeName(Item item) {
		if (item.getTypeName().equals(item.getGroup())) {
			return TabsMining.get().groupBasic(item.getTypeName());
		} else {
			String oreType = item.getTypeName().replace(item.getGroup(), "").trim();
			return TabsMining.get().groupName(item.getGroup(), oreType);
		}
	}

	private List<String> getSelectedItems() {
		List<String> selected = new ArrayList<>();
		for (Map.Entry<String, JCheckBoxMenuItem> entry : items.entrySet()) {
			if (!entry.getValue().isSelected()) {
				continue; //Not selected
			}
			selected.add(entry.getKey());
		}
		return selected;
	}

	private List<String> getSelectedOwners() {
		return jOwners.getSelectedValuesList();
	}

	private void updateStatusbar() {
		Type type = jType.getItemAt(jType.getSelectedIndex());
		List<String> selected = getSelectedItems();
		Set<String> shownGroups = new HashSet<>();
		Set<String> shownTypes = new HashSet<>();
		for (String typeName : selected) {
			String groupName = seriesGroup.get(typeName);
			if (groupName == null) { //Group or Grand total
				shownGroups.add(typeName);
			} else {
				shownTypes.add(typeName);
			}
		}
		clearStatusbarLabels();
		for (int i = 0; i < shownOrder.size(); i++) {
			final String typeName = shownOrder.get(i);
			final Double value = seriesTotals.get(typeName);
			String group = seriesGroup.get(typeName);
			if (value != null &&
					(shownGroups.contains(typeName) //Shown group
					|| (shownTypes.contains(typeName) && !shownGroups.contains(group)))) { //Shown Type
				final Color color = (Color) renderer.getSeriesPaint(i);
				JStatusLabel jStatusLabel = StatusPanel.createLabel(typeName, new ColorIcon(color), type.getAutoNumberFormat());
				jStatusLabel.setNumber(value);
				addStatusbarLabel(jStatusLabel);
			}
		}
		program.getStatusPanel().tabChanged();
	}

	private void updateShown() {
		List<String> selected = getSelectedItems();
		double max = 0;
		int count = 0;
		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			String typeName = shownOrder.get(i);
			boolean fromVisible = renderer.isSeriesVisible(i);
			boolean toVisible = selected.contains(typeName);
			if (fromVisible != toVisible) {
				renderer.setSeriesVisible(i, toVisible);
			}
			max = Math.max(max, seriesMax.get(typeName));
			count = Math.max(count, dataset.getItemCount(i));
		}
		JFreeChartUtil.updateTickScale(domainAxis, rangeLinearAxis, max);
		renderer.setDefaultShapesVisible(count < 2);
		updateStatusbar();
	}

	private LocalDate dateToLocalDate(Date date) {
		Instant instant = date.toInstant();
		return LocalDateTime.ofInstant(instant, ZoneId.of("GMT")).toLocalDate();
	}

	private Date getFromDate() {
		LocalDate date = jFrom.getDate();
		if (date == null) {
			return null;
		}
		Instant instant = date.atStartOfDay().atZone(ZoneId.of("GMT")).toInstant(); //Start of day - GMT
		return Date.from(instant);
	}

	private Date getToDate() {
		LocalDate date = jTo.getDate();
		if (date == null) {
			return null;
		}
		Instant instant = date.atTime(23, 59, 59).atZone(ZoneId.of("GMT")).toInstant(); //End of day - GMT
		return Date.from(instant);
	}

	private class ListenerClass implements ActionListener, DateChangeListener, ListSelectionListener {

		boolean valueIsAdjusting = false;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (MiningGraphAction.TYPE_CHANGE.name().equals(e.getActionCommand())) {
				createData();
				updateShown();
			} else if (MiningGraphAction.QUICK_DATE.name().equals(e.getActionCommand())) {
				QuickDate quickDate = (QuickDate) jQuickDate.getSelectedItem();
				if (quickDate == QuickDate.RESET) {
					jTo.setDate(null);
					jFrom.setDate(null);
				} else {
					Date toDate = getToDate();
					if (toDate == null) {
						toDate = new Date(); //now
					}
					Date fromDate = quickDate.apply(toDate);
					if (fromDate != null) {
						jFrom.setDate(dateToLocalDate(fromDate));
					}
				}
			} else if (MiningGraphAction.INCLUDE_ZERO.name().equals(e.getActionCommand())) {
				rangeLogarithmicAxis.setAutoRangeIncludesZero(jIncludeZero.isSelected());
				rangeLinearAxis.setAutoRangeIncludesZero(jIncludeZero.isSelected());
			} else if (MiningGraphAction.LOGARITHMIC.name().equals(e.getActionCommand())) {
				if (jLogarithmic.isSelected()) {
					jFreeChart.getXYPlot().setRangeAxis(rangeLogarithmicAxis);
				} else {
					jFreeChart.getXYPlot().setRangeAxis(rangeLinearAxis);
				}
			} else if (MiningGraphAction.SHOW_ALL.name().equals(e.getActionCommand())) {
				if (valueIsAdjusting) {
					return;
				}
				valueIsAdjusting = true;
				for (JCheckBoxMenuItem jMenuItem : items.values()) {
					jMenuItem.setSelected(jAll.isSelected());
				}
				valueIsAdjusting = false;
				updateShown();
			} else if (MiningGraphAction.UPDATE_SHOWN.name().equals(e.getActionCommand())) {
				if (valueIsAdjusting) {
					return;
				}
				valueIsAdjusting = true;
				jAll.setSelected(getSelectedItems().size() == items.size());
				valueIsAdjusting = false;
				updateShown();
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
				jTo.setDate(dateToLocalDate(from));
			}
			createData();
			updateShown();
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() || valueIsAdjusting) {
				return;
			}
			createData();
			updateShown();
		}
	}

	public class TotalComparator implements Comparator<String> {

		private final String grandTotal = TabsMining.get().grandTotal();
		private final String groupTotal = TabsMining.get().groupTotal("");

	@Override
		public int compare(String o1, String o2) {
			boolean t1 = o1.equals(grandTotal);
			boolean t2 = o2.equals(grandTotal);
			if (t1) {
				return -1;
			} else if (t2) {
				return 1;
			}
			boolean g1 = o1.contains(groupTotal);
			boolean g2 = o2.contains(groupTotal);
			if (g1) {
				String type = o1.replace(groupTotal, "").trim();
				if (o2.contains(type)) {
					return -1;
				}
			}
			if (g2) {
				String type = o2.replace(groupTotal, "").trim();
				if (o1.contains(type)) {
					return 1;
				}
			}
			return o1.compareToIgnoreCase(o2);
		}

	}

}
