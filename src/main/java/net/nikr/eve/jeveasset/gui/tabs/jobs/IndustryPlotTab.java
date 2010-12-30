package net.nikr.eve.jeveasset.gui.tabs.jobs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.xy.IntervalXYDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andrew
 */
public class IndustryPlotTab extends JMainTab {
	private static final Logger LOG = LoggerFactory.getLogger(IndustryPlotTab.class);

	IndustryJobData data;

	JPanel panel;

	public IndustryPlotTab(Program program) {
		super(program, "Industry Plot", Images.ICON_TOOL_INDUSTRY_JOBS, true);

		panel = new JPanel(new BorderLayout());

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

		panel.setBackground(Color.RED);
		panel.add(createPanel());
		// TODO update the plot data.

	}

	private static JFreeChart createChart(IntervalXYDataset paramIntervalXYDataset) {
		JFreeChart localJFreeChart = ChartFactory.createXYBarChart(
				null, // title
				null, // x-axis label
				true, // show x-axis as dates.
				"Timing", // y-axis label
				paramIntervalXYDataset, // dataset
				PlotOrientation.HORIZONTAL, // orientation
				false, // legend
				false, // tooltips
				false // URLs
				);
		localJFreeChart.setBackgroundPaint(new Color(0,0,0,0));
		XYPlot localXYPlot = (XYPlot) localJFreeChart.getPlot();
	//	localXYPlot.setRangePannable(true); // XXX not available in 1.0.12, but available in 1.0.13? - Candle, 2010-12-27
//		SymbolAxis localSymbolAxis = new SymbolAxis("Series", new String[]{"Team A", "Team B", "Team C", "Team D"});
//		localSymbolAxis.setGridBandsVisible(false);
//		localXYPlot.setDomainAxis(localSymbolAxis);
		XYBarRenderer localXYBarRenderer = (XYBarRenderer) localXYPlot.getRenderer();
		localXYBarRenderer.setUseYInterval(true);
		localXYPlot.setRangeAxis(new DateAxis("Timing"));
//		localXYPlot.setDomainPannable(true);  // XXX not available in 1.0.12, but available in 1.0.13? - Candle, 2010-12-27
		ChartUtilities.applyCurrentTheme(localJFreeChart);
		return localJFreeChart;
	}

	public JPanel createPanel() {
		JFreeChart localJFreeChart = createChart(createDataset());
		ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
	//	localChartPanel.setMouseWheelEnabled(true); // XXX not available in 1.0.12, but available in 1.0.13? - Candle, 2010-12-27
		return localChartPanel;
	}

	private IntervalXYDataset createDataset() {
		return new XYTaskDataset(createTasks());
	}

	private TaskSeriesCollection createTasks() {
		TaskSeriesCollection localTaskSeriesCollection = new TaskSeriesCollection();

		Map<Long, TaskSeries> seriesMap = new HashMap<Long, TaskSeries>();

		for (IndustryJob job : data.getAll()) {
			Long id = job.getContainerID();
			if (!seriesMap.containsKey(id)) {
				TaskSeries series = new TaskSeries(String.valueOf(id));
				seriesMap.put(id, series);
				localTaskSeriesCollection.add(series);
			}
			try {
				seriesMap.get(id).add(new Task(String.valueOf(id), job.getBeginProductionTimeDate(), job.getEndProductionTimeDate()));
			} catch (ParseException pe) {
				LOG.error(pe.getMessage(), pe);
			}
		}

		return localTaskSeriesCollection;
	}

}
