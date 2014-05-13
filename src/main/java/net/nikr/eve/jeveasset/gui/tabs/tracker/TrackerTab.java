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

package net.nikr.eve.jeveasset.gui.tabs.tracker;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.MyAccountBalance;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.tabs.assets.MyAsset;
import net.nikr.eve.jeveasset.gui.tabs.jobs.MyIndustryJob;
import net.nikr.eve.jeveasset.gui.tabs.orders.MyMarketOrder;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsTracker;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.ui.RectangleEdge;


public class TrackerTab extends JMainTab {

	private enum TrackerAction {
		QUICK_DATE,
		UPDATE_DATA,
		UPDATE_SHOWN,
		ALL,
		EDIT,
		DELETE
	}

	private final int PANEL_WIDTH = 140;

	private NumberFormat iskFormat = new DecimalFormat("#,##0.00 isk");
	private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	private JFreeChart jNextChart;
	private JDateChooser jFrom;
	private JDateChooser jTo;
	private JComboBox jOwners;
	private JComboBox jQuickDate;
	private JCheckBox jAll;
	private JCheckBox jTotal;
	private JCheckBox jWalletBalance;
	private JCheckBox jAssets;
	private JCheckBox jSellOrders;
	private JCheckBox jEscrows;
	private JCheckBox jEscrowsToCover;
	private JCheckBox jManufacturing;
	private JPopupMenu jPopupMenu;
	private JMenuItem jIskValue;
	private JMenuItem jDateValue;
	private JTrackerEditDialog jEditDialog;
	private ChartPanel jChartPanel;
	private JTextArea jHelp;

	private ListenerClass listener = new ListenerClass();

	private TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
	TimePeriodValues total;
	TimePeriodValues walletBalance;
	TimePeriodValues assets;
	TimePeriodValues sellOrders;
	TimePeriodValues escrows;
	TimePeriodValues escrowsToCover;
	TimePeriodValues manufacturing;

	public TrackerTab(Program program) {
		super(program, TabsTracker.get().title(), Images.TOOL_TRACKER.getIcon(), true);

		jPopupMenu = new JPopupMenu();
		jPopupMenu.addPopupMenuListener(listener);

		JMenuItem jMenuItem;
		jMenuItem = new JMenuItem(TabsTracker.get().edit(), Images.EDIT_EDIT.getIcon());
		jMenuItem.setActionCommand(TrackerAction.EDIT.name());
		jMenuItem.addActionListener(listener);
		jPopupMenu.add(jMenuItem);

		jMenuItem = new JMenuItem(TabsTracker.get().delete(), Images.EDIT_DELETE.getIcon());
		jMenuItem.setActionCommand(TrackerAction.DELETE.name());
		jMenuItem.addActionListener(listener);
		jPopupMenu.add(jMenuItem);

		JMenuInfo.createDefault(jPopupMenu);

		jIskValue = new JMenuItem();
		jIskValue.setEnabled(false);
		jIskValue.setForeground(Color.BLACK);
		jIskValue.setHorizontalAlignment(SwingConstants.RIGHT);
		jIskValue.setDisabledIcon(Images.TOOL_VALUES.getIcon());
		jPopupMenu.add(jIskValue);

		jDateValue = new JMenuItem();
		jDateValue.setEnabled(false);
		jDateValue.setForeground(Color.BLACK);
		jDateValue.setHorizontalAlignment(SwingConstants.RIGHT);
		//jDate.setDisabledIcon(Images.TOOL_VALUES.getIcon());
		jPopupMenu.add(jDateValue);

		jEditDialog = new JTrackerEditDialog(program);

		jOwners = new JComboBox();
		jOwners.setActionCommand(TrackerAction.UPDATE_DATA.name());
		jOwners.addActionListener(listener);

		jQuickDate = new JComboBox(QuickDate.values());
		jQuickDate.setActionCommand(TrackerAction.QUICK_DATE.name());
		jQuickDate.addActionListener(listener);

		jFrom = createDateChooser();
		JPanel jFromPanel = wrapDateChooser(TabsTracker.get().from(), jFrom);
		jTo = createDateChooser();
		JPanel jToPanel = wrapDateChooser(TabsTracker.get().to(), jTo);

		jAll = new JCheckBox(General.get().all());
		jAll.setSelected(true);
		jAll.setActionCommand(TrackerAction.ALL.name());
		jAll.addActionListener(listener);
		jAll.setFont(new Font(jAll.getFont().getName(), Font.ITALIC, jAll.getFont().getSize()));

		jTotal = new JCheckBox(TabsTracker.get().total());
		jTotal.setSelected(true);
		jTotal.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jTotal.addActionListener(listener);

		jWalletBalance = new JCheckBox(TabsTracker.get().walletBalance());
		jWalletBalance.setSelected(true);
		jWalletBalance.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jWalletBalance.addActionListener(listener);

		jAssets = new JCheckBox(TabsTracker.get().assets());
		jAssets.setSelected(true);
		jAssets.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jAssets.addActionListener(listener);

		jSellOrders = new JCheckBox(TabsTracker.get().sellOrders());
		jSellOrders.setSelected(true);
		jSellOrders.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jSellOrders.addActionListener(listener);

		jEscrows = new JCheckBox(TabsTracker.get().escrows());
		jEscrows.setSelected(true);
		jEscrows.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jEscrows.addActionListener(listener);

		jEscrowsToCover = new JCheckBox(TabsTracker.get().escrowsToCover());
		jEscrowsToCover.setSelected(true);
		jEscrowsToCover.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jEscrowsToCover.addActionListener(listener);

		jManufacturing = new JCheckBox(TabsTracker.get().manufacturing());
		jManufacturing.setSelected(true);
		jManufacturing.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jManufacturing.addActionListener(listener);

		jHelp = new JTextArea();
		jHelp.setEditable(false);
		jHelp.setOpaque(false);
		jHelp.setBorder(null);
		jHelp.setWrapStyleWord(true);
		jHelp.setLineWrap(true);
		jHelp.setFont(jPanel.getFont());
		jHelp.setText(TabsTracker.get().help());

		DateAxis domainAxis = new DateAxis(TabsTracker.get().date());
		domainAxis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));
		domainAxis.setVerticalTickLabels(true);
		domainAxis.setAutoTickUnitSelection(true);
		domainAxis.setAutoRange(true);

		NumberAxis rangeAxis = new NumberAxis(TabsTracker.get().isk());
		rangeAxis.setAutoRange(true);
		rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());

		XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, new XYLineAndShapeRenderer(true, true));
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.getRenderer().setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				"{0}: {2} ({1})",
				dateFormat,
				iskFormat));
		plot.setDomainCrosshairLockedOnData(true);
		plot.setDomainCrosshairStroke(new BasicStroke(1));
		plot.setDomainCrosshairPaint(Color.BLACK);
		plot.setDomainCrosshairVisible(false);
		plot.setRangeCrosshairLockedOnData(true);
		plot.setRangeCrosshairVisible(false);

		jNextChart = new JFreeChart(plot);
		jNextChart.setAntiAlias(true);
		jNextChart.setBackgroundPaint(jPanel.getBackground());
		jNextChart.addProgressListener(null);

		jChartPanel = new ChartPanel(jNextChart);
		jChartPanel.addMouseListener(listener);
		jChartPanel.setDomainZoomable(false);
		jChartPanel.setRangeZoomable(false);
		jChartPanel.setPopupMenu(null);
		jChartPanel.addChartMouseListener(listener);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jChartPanel)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOwners, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jQuickDate, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jFromPanel, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jToPanel, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jAll, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jTotal, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jWalletBalance, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jAssets, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jSellOrders, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jEscrows, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jEscrowsToCover, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jManufacturing, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jHelp, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jChartPanel)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOwners, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jQuickDate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jFromPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jToPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jTotal, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jWalletBalance, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAssets, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSellOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jEscrows, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jEscrowsToCover, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jManufacturing, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jHelp, 35, 35, 35)
				)
		);
	}

	@Override
	public void updateData() {
		Set<TrackerOwner> owners = new TreeSet<TrackerOwner>(Settings.get().getTrackerData().keySet());
		if (owners.isEmpty()) {
			jOwners.setEnabled(false);
			jOwners.getModel().setSelectedItem(new TrackerOwner(-1, TabsTracker.get().noDataFound()));
		} else {
			jOwners.setEnabled(true);
			jOwners.setModel(new DefaultComboBoxModel(owners.toArray()));
		}
		createData();
	}

	private JPanel wrapDateChooser(String title, JDateChooser jDate) {
		JPanel jDatePanel = new JPanel();
		jDatePanel.setBorder(BorderFactory.createTitledBorder(title));
		jDatePanel.add(jDate);
		return jDatePanel;
	}

	private JDateChooser createDateChooser() {
		JDateChooser jDate = new JDateChooser(Settings.getNow());
		jDate.setDateFormatString(Formater.COLUMN_DATE);
		jDate.setCalendar(null);
		JCalendar jCalendar = jDate.getJCalendar();
		jCalendar.setTodayButtonText(TabsTracker.get().today());
		jCalendar.setTodayButtonVisible(true);
		jCalendar.setNullDateButtonText(TabsTracker.get().clear());
		jCalendar.setNullDateButtonVisible(true);
		JTextFieldDateEditor dateEditor = (JTextFieldDateEditor) jDate.getDateEditor().getUiComponent();
		dateEditor.setEnabled(false);
		dateEditor.setBorder(null);
		dateEditor.setDisabledTextColor(Color.BLACK);
		dateEditor.setHorizontalAlignment(JTextFieldDateEditor.CENTER);
		jDate.addPropertyChangeListener(listener);
		return jDate;
	}

	private TrackerData getTrackerData(final Map<TrackerOwner, TrackerData> data, final long ownerID, final String owner, final Date date) {
		TrackerOwner trackerOwner = new TrackerOwner(ownerID, owner);
		TrackerData trackerData = data.get(trackerOwner);
		if (trackerData == null) {
			trackerData = new TrackerData(date);
			data.put(trackerOwner, trackerData);
		}
		return trackerData;
	}

	public void createTrackerDataPoint() {
		Date date = new Date();
		Map<TrackerOwner, TrackerData> data = new HashMap<TrackerOwner, TrackerData>();
		//All
		TrackerData allTracker = new TrackerData(date);
		data.put(new TrackerOwner(), allTracker);
		for (MyAsset asset : program.getAssetEventList()) {
			//Skip market orders
			if (asset.getFlag().equals(General.get().marketOrderSellFlag())) {
				continue; //Ignore market sell orders
			}
			if (asset.getFlag().equals(General.get().marketOrderBuyFlag())) {
				continue; //Ignore market buy orders
			}
			//Skip contracts
			if (asset.getFlag().equals(General.get().contractIncluded())) {
				continue; //Ignore contracts included
			}
			if (asset.getFlag().equals(General.get().contractExcluded())) {
				continue; //Ignore contracts excluded
			}
			//Assets
			TrackerData trackerData = getTrackerData(data, asset.getOwnerID(), asset.getOwner(), date);
			trackerData.addAssets(asset.getDynamicPrice() * asset.getCount());
			allTracker.addAssets(asset.getDynamicPrice() * asset.getCount());
		}
		//Account Balance
		for (MyAccountBalance accountBalance : program.getAccountBalanceEventList()) {
			TrackerData trackerData = getTrackerData(data, accountBalance.getOwnerID(), accountBalance.getOwner(), date);
			trackerData.addWalletBalance(accountBalance.getBalance());
			allTracker.addWalletBalance(accountBalance.getBalance());
		}
		//Market Orders
		for (MyMarketOrder marketOrder : program.getMarketOrdersEventList()) {
			TrackerData trackerData = getTrackerData(data, marketOrder.getOwnerID(), marketOrder.getOwner(), date);
			if (marketOrder.getOrderState() == 0) {
				if (marketOrder.getBid() < 1) { //Sell Orders
					trackerData.addSellOrders(marketOrder.getPrice() * marketOrder.getVolRemaining());
					allTracker.addSellOrders(marketOrder.getPrice() * marketOrder.getVolRemaining());
				} else { //Buy Orders
					trackerData.addEscrows(marketOrder.getEscrow());
					allTracker.addEscrows(marketOrder.getEscrow());
					trackerData.addEscrowsToCover((marketOrder.getPrice() * marketOrder.getVolRemaining()) - marketOrder.getEscrow());
					allTracker.addEscrowsToCover((marketOrder.getPrice() * marketOrder.getVolRemaining()) - marketOrder.getEscrow());
				}
			}
		}
		//Industrys Job: Manufacturing
		for (MyIndustryJob industryJob : program.getIndustryJobsEventList()) {
			TrackerData trackerData = getTrackerData(data, industryJob.getOwnerID(), industryJob.getOwner(), date);
			//Manufacturing and not completed
			if (industryJob.getActivity() == MyIndustryJob.IndustryActivity.ACTIVITY_MANUFACTURING && !industryJob.isCompleted()) {
				double manufacturingTotal = industryJob.getPortion() * industryJob.getRuns() * ApiIdConverter.getPrice(industryJob.getOutputTypeID(), false);
				trackerData.addManufacturing(manufacturingTotal);
				allTracker.addManufacturing(manufacturingTotal);
			}
		}
		//Add everything
		for (Map.Entry<TrackerOwner, TrackerData> entry : data.entrySet()) {
			TrackerOwner trackerOwner = entry.getKey();
			TrackerData trackerData = entry.getValue();
			//New TrackerOwner
			if (!Settings.get().getTrackerData().containsKey(trackerOwner)) {
				Settings.get().getTrackerData().put(trackerOwner, new ArrayList<TrackerData>());
			}
			Settings.get().getTrackerData().get(trackerOwner).add(trackerData);
			
		}
		//Update data
		updateData();
	}

	private void createData() {
		TrackerOwner owner = (TrackerOwner) jOwners.getSelectedItem();
		total = new TimePeriodValues(TabsTracker.get().total());
		walletBalance = new TimePeriodValues(TabsTracker.get().walletBalance());
		assets = new TimePeriodValues(TabsTracker.get().assets());
		sellOrders = new TimePeriodValues(TabsTracker.get().sellOrders());
		escrows = new TimePeriodValues(TabsTracker.get().escrows());
		escrowsToCover = new TimePeriodValues(TabsTracker.get().escrowsToCover());
		manufacturing = new TimePeriodValues(TabsTracker.get().manufacturing());
		Date from = jFrom.getDate();
		if (from != null) { //Start of day
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(from);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			from = calendar.getTime();
		}
		Date to = jTo.getDate();
		if (to != null) { //End of day
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(to);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			calendar.set(Calendar.MILLISECOND, 0);
			to = calendar.getTime();
		}
		if (!owner.isEmpty()) { //No data set...
			for (TrackerData data : Settings.get().getTrackerData().get(owner)) {
				SimpleTimePeriod date = new SimpleTimePeriod(data.getDate(), data.getDate());
				if ((from == null || data.getDate().after(from)) && (to == null || data.getDate().before(to))) {
					total.add(date, data.getTotal());
					walletBalance.add(date, data.getWalletBalance());
					assets.add(date, data.getAssets());
					sellOrders.add(date, data.getSellOrders());
					escrows.add(date, data.getEscrows());
					escrowsToCover.add(date, data.getEscrowsToCover());
					manufacturing.add(date, data.getManufacturing());
				}
			}
		}
		updateShown();
	}

	private void updateShown() {
		//Remove All
		while (dataset.getSeriesCount() != 0) {
			dataset.removeSeries(0);
		}

		if (jTotal.isSelected() && total != null) {
			dataset.addSeries(total);
			updateRender(dataset.getSeriesCount() - 1, Color.RED.darker());
		}
		if (jWalletBalance.isSelected() && walletBalance != null) {
			dataset.addSeries(walletBalance);
			updateRender(dataset.getSeriesCount() - 1, Color.BLUE.darker());

		}
		if (jAssets.isSelected() && assets != null) {
			dataset.addSeries(assets);
			updateRender(dataset.getSeriesCount() - 1, Color.GREEN.darker().darker());
		}
		if (jSellOrders.isSelected() && sellOrders != null) {
			dataset.addSeries(sellOrders);
			updateRender(dataset.getSeriesCount() - 1, Color.CYAN.darker());
		}
		if (jEscrows.isSelected() && escrows != null) {
			dataset.addSeries(escrows);
			updateRender(dataset.getSeriesCount() - 1, Color.BLACK);
		}
		if (jEscrowsToCover.isSelected() && escrowsToCover != null) {
			dataset.addSeries(escrowsToCover);
			updateRender(dataset.getSeriesCount() - 1, Color.GRAY);
		}
		if (jManufacturing.isSelected() && manufacturing != null) {
			dataset.addSeries(manufacturing);
			updateRender(dataset.getSeriesCount() - 1, Color.MAGENTA);
		}
		//Add empty dataset
		if (dataset.getSeriesCount() == 0) {
			TimePeriodValues timePeriodValues = new TimePeriodValues(TabsTracker.get().empty());
			dataset.addSeries(timePeriodValues);
			updateRender(dataset.getSeriesCount() - 1, Color.BLACK);
		}
		jNextChart.getXYPlot().getRangeAxis().setAutoRange(true);
		jNextChart.getXYPlot().getDomainAxis().setAutoRange(true);
		Number maxNumber = DatasetUtilities.findMaximumRangeValue(dataset);
		NumberAxis rangeAxis = (NumberAxis) jNextChart.getXYPlot().getRangeAxis();
		rangeAxis.setNumberFormatOverride(Formater.LONG_FORMAT); //Default
		if (maxNumber != null && (maxNumber instanceof Double)) {
			double max = (Double) maxNumber;
			if (max > 1000000000000.0) {	 //Higher than 1 Trillion
				rangeAxis.setNumberFormatOverride(Formater.TRILLIONS_FORMAT);
			} else if (max > 1000000000.0) { //Higher than 1 Billion
				rangeAxis.setNumberFormatOverride(Formater.BILLIONS_FORMAT);
			} else if (max > 1000000.0) {	 //Higher than 1 Million
				rangeAxis.setNumberFormatOverride(Formater.MILLIONS_FORMAT);
			}
		}
	}

	private void updateRender(int index, Color color) {
		XYItemRenderer renderer = jNextChart.getXYPlot().getRenderer();
		renderer.setSeriesPaint(index, color);
		renderer.setSeriesStroke(index, new BasicStroke(1));
		renderer.setSeriesShape(index, new Ellipse2D.Float(-3.0f, -3.0f, 6.0f, 6.0f));
	}

	private TrackerData getSelectedTrackerData() {
		String date = Formater.simpleDate(new Date((long)jNextChart.getXYPlot().getDomainCrosshairValue()));
		TrackerOwner owner = (TrackerOwner) jOwners.getSelectedItem();
		if (owner == null || owner.isEmpty()) {
			return null;
		}
		for (TrackerData trackerData : Settings.get().getTrackerData().get(owner)) {
			if (date.equals(Formater.simpleDate(trackerData.getDate()))) {
				return trackerData;
			}
		}
		return null;

	}

	private class ListenerClass extends MouseAdapter implements 
			ActionListener, PropertyChangeListener, PopupMenuListener,
			ChartMouseListener {

		private int defaultDismissTimeout;
		private int defaultInitialDelay;

		@Override
		public void mouseEntered(MouseEvent me) {
			defaultDismissTimeout = ToolTipManager.sharedInstance().getDismissDelay();
			defaultInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();
			ToolTipManager.sharedInstance().setDismissDelay(60000);
			ToolTipManager.sharedInstance().setInitialDelay(0);
		}

		@Override
		public void mouseExited(MouseEvent me) {
			ToolTipManager.sharedInstance().setDismissDelay(defaultDismissTimeout);
			ToolTipManager.sharedInstance().setInitialDelay(defaultInitialDelay);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (TrackerAction.QUICK_DATE.name().equals(e.getActionCommand())) {
				QuickDate quickDate = (QuickDate) jQuickDate.getSelectedItem();
				Date toDate = jTo.getDate();
				if (toDate == null) {
					toDate = new Date(); //now
				}
				Date fromDate = quickDate.apply(toDate);
				if (fromDate != null) {
					jFrom.setDate(fromDate);
				}
			} else if (TrackerAction.UPDATE_DATA.name().equals(e.getActionCommand())) {
				createData();
			} else if (TrackerAction.UPDATE_SHOWN.name().equals(e.getActionCommand())) {
				updateShown();
				jAll.setSelected(jTotal.isSelected()
						&& jWalletBalance.isSelected()
						&& jAssets.isSelected()
						&& jSellOrders.isSelected()
						&& jEscrows.isSelected()
						&& jEscrowsToCover.isSelected()
						&& jManufacturing.isSelected());
			} else if (TrackerAction.ALL.name().equals(e.getActionCommand())) {
				jTotal.setSelected(jAll.isSelected());
				jWalletBalance.setSelected(jAll.isSelected());
				jAssets.setSelected(jAll.isSelected());
				jSellOrders.setSelected(jAll.isSelected());
				jEscrows.setSelected(jAll.isSelected());
				jEscrowsToCover.setSelected(jAll.isSelected());
				jManufacturing.setSelected(jAll.isSelected());
				updateShown();
			} else if (TrackerAction.EDIT.name().equals(e.getActionCommand())) {
				jNextChart.getXYPlot().setDomainCrosshairVisible(true);
				TrackerData trackerData = getSelectedTrackerData();
				if (trackerData != null) {
					boolean update = jEditDialog.showEdit(trackerData);
					if (update) {
						createData();
					}
				}
				jNextChart.getXYPlot().setDomainCrosshairVisible(false);
			} else if (TrackerAction.DELETE.name().equals(e.getActionCommand())) {
				jNextChart.getXYPlot().setDomainCrosshairVisible(true);
				TrackerOwner owner = (TrackerOwner) jOwners.getSelectedItem();
				TrackerData trackerData = getSelectedTrackerData();
				if (trackerData != null && owner != null && !owner.isEmpty()) {
					int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsTracker.get().deleteSelected(), TabsTracker.get().delete(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (value == JOptionPane.OK_OPTION) {
						Settings.get().getTrackerData().get(owner).remove(trackerData);
						createData();
					}
				}
				jNextChart.getXYPlot().setDomainCrosshairVisible(false);
			}
			
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Date from = jFrom.getDate();
			Date to = jTo.getDate();
			QuickDate quickDate = (QuickDate) jQuickDate.getSelectedItem();
			if (!quickDate.isValid(from, to)) {
				QuickDate selected = quickDate.getSelected(from, to);
				jQuickDate.setSelectedItem(selected);
			}
			if (from != null && to != null && from.after(to)) {
				jTo.setDate(from);
			}
			createData();
		}

		boolean mouseClicked = false;
		

		@Override
		public void chartMouseClicked(final ChartMouseEvent cme) {
			if (cme.getTrigger().getClickCount() % 2 == 0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						jNextChart.getXYPlot().setDomainCrosshairVisible(true);
						Point2D p = jChartPanel.translateScreenToJava2D(cme.getTrigger().getPoint());
						double xValue = jNextChart.getXYPlot().getDomainCrosshairValue();
						double yValue = jNextChart.getXYPlot().getRangeCrosshairValue();
						RectangleEdge xEdge = jNextChart.getXYPlot().getDomainAxisEdge();
						RectangleEdge yEdge = jNextChart.getXYPlot().getRangeAxisEdge();
						Rectangle2D dataArea = jChartPanel.getScreenDataArea(); // jChartPanel.getChartRenderingInfo().getPlotInfo().getSubplotInfo(0).getDataArea();
						int x = (int) jNextChart.getXYPlot().getDomainAxis().valueToJava2D(xValue, dataArea, xEdge);
						int y = (int) jNextChart.getXYPlot().getRangeAxis().valueToJava2D(yValue, dataArea, yEdge);
						jIskValue.setText(iskFormat.format(yValue));
						jDateValue.setText(dateFormat.format(new Date((long)xValue)));
						jPopupMenu.show((Component)cme.getTrigger().getSource(), x, y);
					}
				});
			}
		}

		@Override
		public void chartMouseMoved(ChartMouseEvent cme) { }

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			jNextChart.getXYPlot().setDomainCrosshairVisible(false);
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
			
		}
	}
}
