package net.nikr.eve.jeveasset.gui.tabs.jobs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
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
		panel.add(new JButton(new AbstractAction("Update") {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateData();
			}
		}), BorderLayout.NORTH);

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
		panel.removeAll();
		panel.add(createPanel());
		// TODO update the plot data.

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
		plot.setRangePannable(true);
		SymbolAxis symbolAxis = new SymbolAxis(null, seriesNames.toArray(new String[]{}));
		plot.setDomainAxis(symbolAxis);
		plot.setBackgroundPaint(transparant);
		XYBarRenderer barRenderer = (XYBarRenderer) plot.getRenderer();
		
		barRenderer.setUseYInterval(true);
		barRenderer.setShadowVisible(false);
		plot.setRangeAxis(new DateAxis(null));
		//ChartUtilities.applyCurrentTheme(chart);
		return chart;
	}

	public JPanel createPanel() {
		JFreeChart chart = createChart(createDataset());
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		return chartPanel;
	}

	private IntervalXYDataset createDataset() {
		return new XYTaskDataset(createTasks());
	}

	List<String> seriesNames;

	private TaskSeriesCollection createTasks() {
		TaskSeriesCollection seriesList = new TaskSeriesCollection();
		seriesNames = new ArrayList<String>();

		Map<Long, TaskSeries> seriesMap = new HashMap<Long, TaskSeries>();

		for (IndustryJob job : data.getAll()) {
			Long id = job.getJobID();
			if (!seriesMap.containsKey(id)) {
				TaskSeries series = new TaskSeries(String.valueOf(id));
				seriesMap.put(id, series);
				seriesList.add(series);
				String name = " ";//ApiIdConverter.locationName(id, null, program.getSettings().getConquerableStations(), program.getSettings().getLocations());
				seriesNames.add(name);
			}
				seriesMap.get(id).add(new Task(String.valueOf(id), job.getBeginProductionTime(), job.getEndProductionTime()));
		}

		return seriesList;
	}

}
