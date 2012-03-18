/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.jobs;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.IndustryJob.IndustryActivity;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.i18n.TabsJobs;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.TextAnchor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andrew
 */
public class IndustryPlotTab extends JMainTab {
	private static final Logger LOG = LoggerFactory.getLogger(IndustryPlotTab.class);

	IndustryJobData data;

	JButton updateButton;
	JPanel panel;
	JPanel infoPanel;

	final Map<IndustryJob.IndustryActivity, List<Color>> jobColours;

	public IndustryPlotTab(Program program) {
		super(program, "Industry Plot", Images.TOOL_INDUSTRY_JOBS.getIcon(), true);
		jobColours = new EnumMap<IndustryJob.IndustryActivity, List<Color>>(IndustryJob.IndustryActivity.class);
		jobColours.put(IndustryJob.IndustryActivity.ACTIVITY_COPYING, Arrays.asList(Color.YELLOW, new Color(180, 180, 0)));
		jobColours.put(IndustryJob.IndustryActivity.ACTIVITY_DUPLICATING, Arrays.asList(Color.LIGHT_GRAY));
		jobColours.put(IndustryJob.IndustryActivity.ACTIVITY_MANUFACTURING, Arrays.asList(Color.GREEN, new Color(0, 180, 0)));
		jobColours.put(IndustryJob.IndustryActivity.ACTIVITY_RESEARCHING_METERIAL_PRODUCTIVITY, Arrays.asList(Color.BLUE, new Color(0, 0, 180)));
		jobColours.put(IndustryJob.IndustryActivity.ACTIVITY_RESEARCHING_TECHNOLOGY, Arrays.asList(Color.CYAN, new Color(0, 180, 180)));
		jobColours.put(IndustryJob.IndustryActivity.ACTIVITY_RESEARCHING_TIME_PRODUCTIVITY, Arrays.asList(Color.MAGENTA, new Color(180, 0, 180)));
		jobColours.put(IndustryJob.IndustryActivity.ACTIVITY_REVERSE_ENGINEERING, Arrays.asList(Color.DARK_GRAY));
		jobColours.put(IndustryJob.IndustryActivity.ACTIVITY_REVERSE_INVENTION, Arrays.asList(Color.RED, new Color(180, 0, 0)));

		infoPanel = new JPanel();
		panel = new JPanel(new BorderLayout());
		updateInformationTable(null);
		doLayout();
	}

	private void doLayout() {
		updateButton = new JButton(new AbstractAction("Update") {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				updateData();
			}
		});

		layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );
	}


	@Override
	public void updateTableMenu(JComponent jComponent) { }

	@Override
	protected void showTablePopupMenu(MouseEvent e) { }

	@Override
	public void updateData() {
		if (data == null) {
			data = new IndustryJobData(program);
		}
		data.updateData();
		panel.removeAll();
		panel.add(createPanel(), BorderLayout.CENTER);
		panel.add(updateButton, BorderLayout.SOUTH);
		panel.add(infoPanel, BorderLayout.NORTH);
		doLayout();
	}

	private JFreeChart createChart(IntervalXYDataset dataset) {
		JFreeChart chart = ChartFactory.createXYBarChart(
				null, // title
				null, // x-axis label
				true, // show x-axis as dates.
				"Timing", // y-axis label
				dataset, // dataset
				PlotOrientation.HORIZONTAL, // orientation
				false, // legend
				false, // tooltips
				false // URLs
				);
		Color transparant = new Color(0,0,0,0);
		chart.setBackgroundPaint(transparant);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setRenderer(createBarRenderer());

		plot.setRangePannable(true);
		SymbolAxis symbolAxis = new SymbolAxis(null, seriesNames.toArray(new String[]{}));
		plot.setDomainAxis(symbolAxis);
		plot.setBackgroundPaint(transparant);
		DateAxis xAxis = new DateAxis(null, TimeZone.getTimeZone("GMT"), Locale.ENGLISH);
		xAxis.setVerticalTickLabels(true);
		plot.setRangeAxis(xAxis);

		ValueMarker nowMarker = new ValueMarker(System.currentTimeMillis());
		nowMarker.setLabelOffsetType(LengthAdjustmentType.EXPAND);
		nowMarker.setPaint(Color.BLACK);
		nowMarker.setStroke(new BasicStroke(2.0f));
		plot.addDomainMarker(nowMarker, Layer.FOREGROUND);

		// zoom so that just the last month is visible.

		return chart;
	}
	private XYBarRendererImpl createBarRenderer() {
		XYBarRendererImpl barRenderer = new XYBarRendererImpl(chartDataInformation);
		barRenderer.setUseYInterval(true);
		barRenderer.setShadowVisible(false);
		barRenderer.setDrawBarOutline(false);

		StandardXYItemLabelGenerator labelGenerator
				= new StandardXYItemLabelGenerator("{0} , {1}, {2}",
				NumberFormat.getNumberInstance(),
				DateFormat.getDateTimeInstance()
				);
		barRenderer.setBaseItemLabelGenerator(labelGenerator);
		ItemLabelPosition ipl = new ItemLabelPosition(ItemLabelAnchor.INSIDE1, TextAnchor.BOTTOM_CENTER);
		barRenderer.setBaseNegativeItemLabelPosition(ipl);
		barRenderer.setBasePositiveItemLabelPosition(ipl);

		barRenderer.setBaseToolTipGenerator(
				new StandardXYToolTipGenerator("{0}, {1}, {2}",
				NumberFormat.getNumberInstance(),
				DateFormat.getDateTimeInstance()
				));
		barRenderer.setBaseItemLabelsVisible(true);
		return barRenderer;
	}

	public JPanel createPanel() {
		JFreeChart chart = createChart(createDataset());
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);

		chartPanel.addChartMouseListener(new ChartMouseListener() {
			@Override
			public void chartMouseClicked(ChartMouseEvent cme) {
				LOG.info(cme.getEntity().toString());
				if (cme.getEntity() instanceof XYItemEntity) {
					XYItemEntity entity = (XYItemEntity)cme.getEntity();
					IndustryJob job = chartDataInformation.rowColJobMap.get(entity.getItem()).get(entity.getSeriesIndex());
					updateInformationTable(job);
				}
			}
			@Override
			public void chartMouseMoved(ChartMouseEvent cme) { }
		});

		return chartPanel;
	}

	private void updateInformationTable(IndustryJob job) {
		infoPanel.removeAll();
		infoPanel.add(JobInformationPanel.getPanelForJob(job));
		doLayout();
	}

	private IntervalXYDataset createDataset() {
		return new XYTaskDataset(createTasks());
	}

	List<String> seriesNames;
	ChartDataInformation chartDataInformation;

	private TaskSeriesCollection createTasks() {
		TaskSeriesCollection seriesList = new TaskSeriesCollection();
		seriesNames = new ArrayList<String>();
		chartDataInformation = new ChartDataInformation(jobColours);

		// map of series IDs to the series.
		Map<Long, TaskSeries> seriesMap = new HashMap<Long, TaskSeries>();
		// map of series IDs to series counters.
		Map<Long, Integer> seriesIdListId = new HashMap<Long, Integer>();
		// count how many we have added to each series.
		Map<Integer, AtomicInteger> seriesCounters = new HashMap<Integer, AtomicInteger>();

		List<IndustryJob> jobsSorted = new ArrayList<IndustryJob>(data.getAll());
		Collections.sort(jobsSorted, new Comparator<IndustryJob>() {
			@Override
			public int compare(IndustryJob o1, IndustryJob o2) {
				return (int)Math.signum(o2.getEndProductionTime().getTime()
						- o1.getEndProductionTime().getTime());
			}
		});
		int seriesCounter = 0;
		for (IndustryJob job : jobsSorted) {
			Long id = job.getAssemblyLineID();
			if (!seriesMap.containsKey(id)) {
				// build the new series and make sure that the maps are up-to-date.
				seriesIdListId.put(id, seriesCounter);
				seriesCounters.put(seriesCounter, new AtomicInteger(0));
				TaskSeries series = new TaskSeries(String.valueOf(id));
				seriesMap.put(id, series);
				seriesList.add(series);
				String name = " ";//String.valueOf(id);//ApiIdConverter.locationName(id, null, program.getSettings().getConquerableStations(), program.getSettings().getLocations());
				seriesNames.add(name);
				seriesCounter++;
			}

			int jobSeries = seriesIdListId.get(id);
			chartDataInformation.addIndustryJob(job,
					seriesCounters.get(jobSeries).getAndIncrement(),
					jobSeries);
			seriesMap.get(id).add(new Task(
						job.getActivity().getDescriptionOf(job),
						job.getBeginProductionTime(),
						job.getEndProductionTime()
						)
					);
		}

		return seriesList;
	}

	static class ChartDataInformation {
		final Map<Integer, Map<Integer, IndustryJob>> rowColJobMap;
		final Map<Long, IndustryJob.IndustryActivity> assemblyLineActivities;
		final Map<IndustryJob.IndustryActivity, List<Color>> jobColours;

		ChartDataInformation(final Map<IndustryActivity, List<Color>> jobColours) {
			this.jobColours = jobColours;
			assemblyLineActivities = new HashMap<Long, IndustryActivity>();
			rowColJobMap = new HashMap<Integer, Map<Integer, IndustryJob>>();
		}

		public void addIndustryJob(IndustryJob job, int x, int y) {
			assemblyLineActivities.put(job.getAssemblyLineID(), job.getActivity());
			if (!rowColJobMap.containsKey(x)) {
				rowColJobMap.put(x, new HashMap<Integer, IndustryJob>());
			}
			rowColJobMap.get(x).put(y, job);
		}
	}

	private static class XYBarRendererImpl extends XYBarRenderer {
		private static final long serialVersionUID = 1L;
		final ChartDataInformation cdi;

		XYBarRendererImpl(ChartDataInformation cdi) {
			this.cdi = cdi;
		}

		@Override
		public Paint getItemPaint(int row, int column) {
			List<Color> colours = cdi.jobColours.get(
					cdi.rowColJobMap.get(column).get(row).getActivity()
					);
			return colours.get(column % colours.size());
		}
	}

	static class JobInformationPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		static Map<IndustryActivity, JobInformationPanel> panels = new EnumMap<IndustryActivity, JobInformationPanel>(IndustryActivity.class);
		static JobInformationPanel getPanelForJob(IndustryJob job) {
			IndustryActivity activity;
			if (job == null) {
				activity = IndustryActivity.ACTIVITY_COPYING;
			} else {
				activity = job.getActivity();
			}
			if (!panels.containsKey(activity)) {
				panels.put(activity, createPanel(activity));
			}
			if (job == null) {
				return panels.get(activity);
			} else {
				return panels.get(activity).updateTo(job);
			}
		}
		private static JobInformationPanel createPanel(IndustryActivity activity) {
			switch(activity) {
				case ACTIVITY_COPYING:
					return new CopyingJobPanel();
				case ACTIVITY_DUPLICATING:
				case ACTIVITY_MANUFACTURING:
				case ACTIVITY_RESEARCHING_METERIAL_PRODUCTIVITY:
				case ACTIVITY_RESEARCHING_TECHNOLOGY:
				case ACTIVITY_RESEARCHING_TIME_PRODUCTIVITY:
				case ACTIVITY_REVERSE_ENGINEERING:
				case ACTIVITY_REVERSE_INVENTION:
					return new JobInformationPanel();
				default:
					throw new UnsupportedOperationException("Fix the enum/switch.");
			}
		}

		IndustryJob job;
		JLabel state;
		JLabel activity;
		JLabel name;
		JLabel location;
		JLabel owner;
		JLabel installDate;
		JLabel startDate;
		JLabel endDate;
		JobInformationPanel() {
			state = new JLabel("             ");
			activity = new JLabel("             ");
			name = new JLabel("             ");
			location = new JLabel("             ");
			owner = new JLabel("             ");
			installDate = new JLabel("             ");
			startDate = new JLabel("             ");
			endDate = new JLabel("             ");
			doStandardLayout();
		}

		final void doStandardLayout() {
			setLayout(new GridLayout(2, 9, 5, 2));
			add(new JLabel(TabsJobs.get().columnOwner()));
			add(new JLabel(TabsJobs.get().columnName()));
			add(new JLabel(TabsJobs.get().columnActivity()));
			add(name);
			add(owner);
			add(activity);
		}

		/**
		 * update the panel to this job and
		 * @param job
		 * @return 'this'
		 */
		JobInformationPanel updateTo(IndustryJob job) {
			setJob(job);
			state.setText(job.getState().toString());
			activity.setText(job.getActivity().toString());
			name.setText(job.getName());
			location.setText(job.getLocation());
			owner.setText(job.getOwner());
			installDate.setText(String.valueOf(job.getInstallTime()));
			startDate.setText(String.valueOf(job.getBeginProductionTime()));
			endDate.setText(String.valueOf(job.getEndProductionTime()));
			return this;
		}
		void setJob(IndustryJob job) {
			this.job = job;
		}
	}
	private static class CopyingJobPanel extends JobInformationPanel {
		private static final long serialVersionUID = 1L;
		CopyingJobPanel() {
			super();

		}
		@Override
		JobInformationPanel updateTo(IndustryJob job) {
			return super.updateTo(job);
		}
	}
}
