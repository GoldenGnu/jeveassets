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

package net.nikr.eve.jeveasset.gui.tabs.journal;

import java.awt.Dimension;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.GroupLayout;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.my.MyJournal;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.JFreeChartUtil;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.TabsJournal;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;

public class JournalChartDialog extends JDialogCentered {

	private static final ZoneId CHART_ZONE = ZoneId.of("GMT");
	private static final int MIN_WIDTH = 720;
	private static final int MIN_HEIGHT = 420;

	private final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
	private final XYLineAndShapeRenderer renderer;
	private final DateAxis domainAxis;
	private final NumberAxis rangeAxis;
	private final ChartPanel jChartPanel;
	private final JButton jClose;

	public JournalChartDialog(final Program program) {
		super(program, TabsJournal.get().chartTitle(), Images.TOOL_JOURNAL.getImage());

		domainAxis = JFreeChartUtil.createDateAxis();
		rangeAxis = JFreeChartUtil.createNumberAxis(true);
		renderer = JFreeChartUtil.createRenderer();
		renderer.setDefaultToolTipGenerator(new XYToolTipGenerator() {
			@Override
			public String generateToolTip(XYDataset dataset, int series, int item)	{
				Date date = new Date(dataset.getX(series, item).longValue());
				Number value = dataset.getY(series, item);
				return "<html><b>" + dataset.getSeriesKey(series) + ":</b> "
						+ Formatter.iskFormat(value)
						+ "<br><b>Date:</b> "
						+ Formatter.columnDateOnly(date);
			}
		});

		XYPlot plot = JFreeChartUtil.createPlot(dataset, domainAxis, rangeAxis, renderer);
		JFreeChart jFreeChart = JFreeChartUtil.createChart(plot);
		jChartPanel = JFreeChartUtil.createChartPanel(jFreeChart);
		jChartPanel.setPreferredSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

		jClose = new JButton(TabsJournal.get().close());
		jClose.addActionListener(e -> getDialog().setVisible(false));

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(jChartPanel)
				.addComponent(jClose, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jChartPanel)
				.addComponent(jClose, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
		);

		getDialog().setResizable(true);
		getDialog().setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		getDialog().setModalityType(java.awt.Dialog.ModalityType.MODELESS);
	}

	public void showChart(final List<MyJournal> journals) {
		updateData(journals);
		setVisible(true);
	}

	private void updateData(final List<MyJournal> journals) {
		while (dataset.getSeriesCount() != 0) {
			dataset.removeSeries(0);
		}
		if (journals == null || journals.isEmpty()) {
			return;
		}
		TreeMap<Date, Map<String, Double>> values = new TreeMap<>();
		Set<String> owners = new HashSet<>();
		for (MyJournal journal : journals) {
			Date day = toDay(journal.getDate());
			Map<String, Double> map = values.get(day);
			if (map == null) {
				map = new HashMap<>();
				values.put(day, map);
			}
			String ownerName = journal.getOwnerName();
			owners.add(ownerName);
			double amount = journal.getAmount() != null ? journal.getAmount() : 0.0;
			map.put(ownerName, map.getOrDefault(ownerName, 0.0) + amount);
		}
		List<String> names = new ArrayList<>(owners);
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		LocalDate startLocal = values.firstKey().toInstant().atZone(CHART_ZONE).toLocalDate();
		LocalDate endLocal = values.lastKey().toInstant().atZone(CHART_ZONE).toLocalDate();
		for (LocalDate date = startLocal; !date.isAfter(endLocal); date = date.plusDays(1)) {
			Date bucket = Date.from(date.atStartOfDay().atZone(CHART_ZONE).toInstant());
			Map<String, Double> map = values.get(bucket);
			for (String name : names) {
				double value = map != null ? map.getOrDefault(name, 0.0) : 0.0;
				TimePeriodValues timePeriod = getSeries(name);
				timePeriod.add(new SimpleTimePeriod(bucket, bucket), value);
			}
		}

		double max = 0.0;
		int count = 0;
		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			count = Math.max(count, dataset.getItemCount(i));
			for (int j = 0; j < dataset.getItemCount(i); j++) {
				Number value = dataset.getY(i, j);
				if (value != null) {
					max = Math.max(max, value.doubleValue());
				}
			}
		}
		JFreeChartUtil.updateTickScale(domainAxis, rangeAxis, max);
		renderer.setDefaultShapesVisible(count < 2);
	}

	private TimePeriodValues getSeries(final String name) {
		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			if (dataset.getSeries(i).getKey().equals(name)) {
				return dataset.getSeries(i);
			}
		}
		TimePeriodValues series = new TimePeriodValues(name);
		dataset.addSeries(series);
		return series;
	}

	private Date toDay(Date date) {
		Instant instant = date.toInstant();
		LocalDate localDate = instant.atZone(CHART_ZONE).toLocalDate();
		return Date.from(localDate.atStartOfDay().atZone(CHART_ZONE).toInstant());
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jClose;
	}

	@Override
	protected JButton getDefaultButton() {
		return jClose;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() { }
}
