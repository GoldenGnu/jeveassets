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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
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
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyMining;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.settings.Colors;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.JStatusLabel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.ChartUtil;
import net.nikr.eve.jeveasset.gui.shared.ColorIcon;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.InstantToolTip;
import net.nikr.eve.jeveasset.gui.shared.components.JDateChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionList;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.gui.tabs.tracker.QuickDate;
import net.nikr.eve.jeveasset.i18n.TabsMining;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
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

	private enum PriceHistoryAction {
		QUICK_DATE,
		INCLUDE_ZERO,
		LOGARITHMIC
	}

	private final Shape LEGEND = new Rectangle(-5, -5, 10, 10);
	private final Shape ITEM_SHAPE = new Ellipse2D.Float(-3.0f, -3.0f, 6.0f, 6.0f);
	private final int PANEL_WIDTH_MINIMUM = 215;
	private final XYLineAndShapeRenderer renderer;

	//GUI
	private final JComboBox<QuickDate> jQuickDate;
	private final JDateChooser jFrom;
	private final JDateChooser jTo;
	private final JMultiSelectionList<String> jItems;

	//Graph
	private final JCheckBoxMenuItem jIncludeZero;
	private final JRadioButtonMenuItem jLogarithmic;
	private final DateAxis domainAxis;
	private final LogarithmicAxis rangeLogarithmicAxis;
	private final NumberAxis rangeLinearAxis;
	private final ChartPanel jChartPanel;
	private final JFreeChart jNextChart;

	private final ListenerClass listener = new ListenerClass();

	//Data
	private final TotalComparator comparator = new TotalComparator();
	private final DefaultListModel<String> itemsModel;
	private final List<String> shownOrder = new ArrayList<>();
	private final Map<String, TimePeriodValues> series = new HashMap<>();
	private final Map<String, Double> seriesMax = new HashMap<>();
	private final Map<String, Double> seriesTotals = new HashMap<>();
	private final Map<String, String> seriesGroup = new HashMap<>();
	private final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();

	//Settings ToDo
	private Date fromDate = null;
	private Date toDate = null;
	public static final String NAME = "mininggraph"; //Not to be changed!


	public MiningGraphTab(Program program) {
		super(program, NAME, TabsMining.get().miningGraph(), Images.TOOL_MINING_GRAPH.getIcon(), true);

		jQuickDate = new JComboBox<>(QuickDate.values());
		jQuickDate.setActionCommand(PriceHistoryAction.QUICK_DATE.name());
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

		JDropDownButton jSettings = new JDropDownButton(Images.DIALOG_SETTINGS.getIcon());

		jIncludeZero = new JCheckBoxMenuItem(TabsMining.get().includeZero());
		jIncludeZero.setSelected(true);
		jIncludeZero.setActionCommand(PriceHistoryAction.INCLUDE_ZERO.name());
		jIncludeZero.addActionListener(listener);
		jSettings.add(jIncludeZero);

		jSettings.addSeparator();

		ButtonGroup buttonGroup = new ButtonGroup();

		JRadioButtonMenuItem jLinear = new JRadioButtonMenuItem(TabsMining.get().scaleLinear());
		jLinear.setSelected(true);
		jLinear.setActionCommand(PriceHistoryAction.LOGARITHMIC.name());
		jLinear.addActionListener(listener);
		jSettings.add(jLinear);
		buttonGroup.add(jLinear);

		jLogarithmic = new JRadioButtonMenuItem(TabsMining.get().scaleLogarithmic());
		jLogarithmic.setSelected(false);
		jLogarithmic.setActionCommand(PriceHistoryAction.LOGARITHMIC.name());
		jLogarithmic.addActionListener(listener);
		jSettings.add(jLogarithmic);
		buttonGroup.add(jLogarithmic);

		itemsModel = new DefaultListModel<>();
		jItems = new JMultiSelectionList<>(itemsModel);
		jItems.getSelectionModel().addListSelectionListener(listener);
		jItems.addMouseListener(listener);
		JScrollPane jOwnersScroll = new JScrollPane(jItems);

		Font font = new JLabel().getFont();

		domainAxis = new DateAxis();
		domainAxis.setDateFormatOverride(ChartUtil.getDateFormat());
		domainAxis.setVerticalTickLabels(true);
		domainAxis.setAutoTickUnitSelection(true);
		domainAxis.setAutoRange(true);
		domainAxis.setTickLabelFont(font);
		domainAxis.setTickLabelPaint(Colors.TEXTFIELD_FOREGROUND.getColor());

		rangeLogarithmicAxis = new LogarithmicAxis("");
		rangeLogarithmicAxis.setStrictValuesFlag(false);
		rangeLogarithmicAxis.setNumberFormatOverride(Formatter.AUTO_FORMAT);
		rangeLogarithmicAxis.setTickLabelFont(jFromLabel.getFont());
		rangeLogarithmicAxis.setTickLabelPaint(Colors.TEXTFIELD_FOREGROUND.getColor());
		rangeLogarithmicAxis.setAutoRangeIncludesZero(true);

		rangeLinearAxis = new NumberAxis();
		rangeLinearAxis.setAutoRange(true);
		rangeLinearAxis.setNumberFormatOverride(Formatter.AUTO_FORMAT);
		rangeLinearAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
		rangeLinearAxis.setTickLabelFont(font);
		rangeLinearAxis.setTickLabelPaint(Colors.TEXTFIELD_FOREGROUND.getColor());
		rangeLinearAxis.setAutoRangeIncludesZero(true);

		renderer = new MyRender();
		renderer.setDefaultToolTipGenerator(new XYToolTipGenerator() {
			@Override
			public String generateToolTip(XYDataset dataset, int series, int item)	{
				Date date = new Date(dataset.getX(series, item).longValue());
				Number isk = dataset.getY(series, item);
				return TabsMining.get().graphToolTip(dataset.getSeriesKey(series), Formatter.iskFormat(isk), Formatter.columnDateOnly(date));
			}
		});

		XYPlot plot = new XYPlot(dataset, domainAxis, rangeLinearAxis, renderer);
		plot.setBackgroundPaint(Colors.TEXTFIELD_BACKGROUND.getColor());
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.setDomainCrosshairLockedOnData(true);
		plot.setDomainCrosshairStroke(new BasicStroke(1));
		plot.setDomainCrosshairPaint(Color.BLACK);
		plot.setDomainCrosshairVisible(false);
		plot.setRangeCrosshairLockedOnData(true);
		plot.setRangeCrosshairVisible(false);

		jNextChart = new JFreeChart(plot);
		jNextChart.setAntiAlias(true);
		jNextChart.setBackgroundPaint(Colors.COMPONENT_BACKGROUND.getColor());
		jNextChart.addProgressListener(null);
		jNextChart.getLegend().setItemFont(font);
		jNextChart.getLegend().setItemPaint(Colors.TEXTFIELD_FOREGROUND.getColor());
		jNextChart.getLegend().setBackgroundPaint(Colors.COMPONENT_BACKGROUND.getColor());
		//jNextChart.setTextAntiAlias(false);
		//jNextChart.setAntiAlias(false);
		/*
		Map<RenderingHints.Key,Object> rh = new HashMap<>();
		rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
		//rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);


		//rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
		//rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		//rh.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		rh.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT);
		//rh.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);

		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);
		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR);
		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		//rh.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		rh.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
		//rh.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

		//rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		//rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

		//rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
		//rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

		//rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
		//rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);

		//rh.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		rh.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
		//rh.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		jNextChart.setRenderingHints(new RenderingHints(rh));
		*/

		jChartPanel = new ChartPanel(jNextChart);
		InstantToolTip.install(jChartPanel);
		jChartPanel.setDomainZoomable(false);
		jChartPanel.setRangeZoomable(false);
		jChartPanel.setPopupMenu(null);
		//jChartPanel.addChartMouseListener(listener);
		jChartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
		jChartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
		jChartPanel.setMinimumDrawWidth(10);
		jChartPanel.setMinimumDrawHeight(10);

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
		boolean firstUpdate = itemsModel.size() == 0;
		Set<String> ok = new HashSet<>();
		Set<String> remove = new HashSet<>();
		for (int i = 0; i < itemsModel.getSize(); i++) {
			String name = itemsModel.get(i);
			if (!shownOrder.contains(name)) {
				remove.add(name);
			} else {
				ok.add(name);
			}
		}
		for (String name : remove) {
			itemsModel.removeElement(name);
		}
		Set<String> add = new HashSet<>(shownOrder);
		add.removeAll(ok);
		for (int i = 0; i < shownOrder.size(); i++) {
			String name = shownOrder.get(i);
			if (add.contains(name)) {
				itemsModel.add(i, name);
			}
		}
		if (firstUpdate) {
			jItems.selectAll();
		}
	}

	private void createData() {
		Date from = getFromDate();
		Date to = getToDate();
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
		Map<Date, Map<String, Double>> values = new HashMap<>();
		Set<String> names = new HashSet<>();
		final String grandTotal = TabsMining.get().grandTotal();
		for (MyMining mining : program.getProfileData().getMiningEventList()) {
			final Date date = mining.getDate();
			//Filter
			if ((from != null && !date.after(from)) || (to != null && !date.before(to))) {
				continue;
			}
			//Type
			final String typeName = getTypeName(mining.getItem());
			final double value = mining.getValueOre();
			Map<String, Double> map = values.get(date);
			if (map == null) {
				map = new HashMap<>();
				values.put(date, map);
			}
			names.add(typeName);
			map.put(typeName, value);
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

	private void updateStatusbar() {
		List<String> selected = jItems.getSelectedValuesList();
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
				JStatusLabel jStatusLabel = StatusPanel.createLabel(typeName, new ColorIcon(color), AutoNumberFormat.ISK);
				jStatusLabel.setNumber(value);
				addStatusbarLabel(jStatusLabel);
			}
		}
		program.getStatusPanel().tabChanged();
	}

	private void updateShown() {
		List<String> selected = jItems.getSelectedValuesList();
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
		ChartUtil.updateTickScale(domainAxis, rangeLinearAxis, max);
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

	private class ListenerClass implements ActionListener, MouseListener, DateChangeListener, ListSelectionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (PriceHistoryAction.QUICK_DATE.name().equals(e.getActionCommand())) {
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
			} else if (PriceHistoryAction.INCLUDE_ZERO.name().equals(e.getActionCommand())) {
				rangeLogarithmicAxis.setAutoRangeIncludesZero(jIncludeZero.isSelected());
				rangeLinearAxis.setAutoRangeIncludesZero(jIncludeZero.isSelected());
			} else if (PriceHistoryAction.LOGARITHMIC.name().equals(e.getActionCommand())) {
				if (jLogarithmic.isSelected()) {
					jNextChart.getXYPlot().setRangeAxis(rangeLogarithmicAxis);
				} else {
					jNextChart.getXYPlot().setRangeAxis(rangeLinearAxis);
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) { }

		@Override
		public void mousePressed(MouseEvent e) { }

		@Override
		public void mouseReleased(MouseEvent e) { }

		@Override
		public void mouseEntered(MouseEvent e) { }

		@Override
		public void mouseExited(MouseEvent e) { }

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
			if (e.getValueIsAdjusting()) {
				return;
			}
			updateShown();
		}
	}

	private class MyRender extends XYLineAndShapeRenderer {

		public MyRender() {
			super(true, false);
		}

		@Override
		public LegendItem getLegendItem(int datasetIndex, int series) {
			LegendItem original = super.getLegendItem(datasetIndex, series);
			LegendItem fixed = new LegendItem(
				original.getLabel(),
				original.getDescription(),
				original.getToolTipText(),
				"", //urlText
				LEGEND,
				original.getLinePaint(), //set fill paint to line paint
				original.getOutlineStroke(),
				Color.BLACK
			);
			fixed.setSeriesIndex(series);
			fixed.setDatasetIndex(datasetIndex);
			return fixed;
		}

		@Override
		public Shape getItemShape(int row, int column) {
			return ITEM_SHAPE;
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
