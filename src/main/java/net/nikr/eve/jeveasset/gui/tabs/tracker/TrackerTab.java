/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.ToolTipManager;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.AccountBalance;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.MarketOrder;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsTracker;
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


public class TrackerTab extends JMainTab {

	private static final String ACTION_QUICK_DATE = "ACTION_QUICK_DATE";
	private static final String ACTION_UPDATE_DATA = "ACTION_UPDATE_DATA";
	private static final String ACTION_UPDATE_SHOWN = "ACTION_UPDATE_SHOWN";
	private static final String ACTION_All = "ACTION_All";

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

	private enum QuickDate {
		EMPTY(null, null, TabsTracker.get().quickDate())
		,MONTH_ONE(Calendar.MONTH, -1, TabsTracker.get().month1())
		,MONTH_THREE(Calendar.MONTH, -3, TabsTracker.get().months3())
		,MONTH_SIX(Calendar.MONTH, -6, TabsTracker.get().months6())
		,YEAR_ONE(Calendar.YEAR, -1, TabsTracker.get().year1())
		,YEAR_TWO(Calendar.YEAR, -2, TabsTracker.get().years2());

		private Integer field;
		private Integer amount;
		private String title;

		private QuickDate(Integer field, Integer amount, String title) {
			this.field = field;
			this.amount = amount;
			this.title = title;
		}

		public Date apply(Date to) {
			if (field == null || amount == null) {
				return null;
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(to);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.add(field, amount);
			return calendar.getTime();
		}

		public boolean isValid(Date from, Date to) {
			if (to == null) {
				to = new Date(); //now
			}
			if (from == null) {
				return false;
			}
			to = apply(to);
			return from.equals(to);
		}

		public QuickDate getSelected(Date from, Date to) {
			for (QuickDate quickDate : QuickDate.values()) {
				if (quickDate.isValid(from, to)) {
					return quickDate;
				}
			}
			return QuickDate.EMPTY;
		}

		@Override
		public String toString() {
			return title;
		}
	}

	private Listener listener = new Listener();

	private TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
	TimePeriodValues total;
	TimePeriodValues walletBalance;
	TimePeriodValues assets;
	TimePeriodValues sellOrders;
	TimePeriodValues escrows;
	TimePeriodValues escrowsToCover;

	public TrackerTab(Program program) {
		super(program, TabsTracker.get().title(), Images.TOOL_TRACKER.getIcon(), true);

		jOwners = new JComboBox();
		jOwners.setActionCommand(ACTION_UPDATE_DATA);
		jOwners.addActionListener(listener);

		jQuickDate = new JComboBox(QuickDate.values());
		jQuickDate.setActionCommand(ACTION_QUICK_DATE);
		jQuickDate.addActionListener(listener);

		jFrom = createDateChooser(TabsTracker.get().from());
		jTo = createDateChooser(TabsTracker.get().to());

		jAll = new JCheckBox(General.get().all());
		jAll.setSelected(true);
		jAll.setActionCommand(ACTION_All);
		jAll.addActionListener(listener);
		jAll.setFont(new Font(jAll.getFont().getName(), Font.ITALIC, jAll.getFont().getSize()));

		jTotal = new JCheckBox(TabsTracker.get().total());
		jTotal.setSelected(true);
		jTotal.setActionCommand(ACTION_UPDATE_SHOWN);
		jTotal.addActionListener(listener);

		jWalletBalance = new JCheckBox(TabsTracker.get().walletBalanc());
		jWalletBalance.setSelected(true);
		jWalletBalance.setActionCommand(ACTION_UPDATE_SHOWN);
		jWalletBalance.addActionListener(listener);

		jAssets = new JCheckBox(TabsTracker.get().assets());
		jAssets.setSelected(true);
		jAssets.setActionCommand(ACTION_UPDATE_SHOWN);
		jAssets.addActionListener(listener);

		jSellOrders = new JCheckBox(TabsTracker.get().sellOrders());
		jSellOrders.setSelected(true);
		jSellOrders.setActionCommand(ACTION_UPDATE_SHOWN);
		jSellOrders.addActionListener(listener);

		jEscrows = new JCheckBox(TabsTracker.get().escrows());
		jEscrows.setSelected(true);
		jEscrows.setActionCommand(ACTION_UPDATE_SHOWN);
		jEscrows.addActionListener(listener);

		jEscrowsToCover = new JCheckBox(TabsTracker.get().escrowsToCover());
		jEscrowsToCover.setSelected(true);
		jEscrowsToCover.setActionCommand(ACTION_UPDATE_SHOWN);
		jEscrowsToCover.addActionListener(listener);

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
				new SimpleDateFormat("dd-MM-yyyy"),
				new DecimalFormat("#,##0.00 isk")));

		jNextChart = new JFreeChart(plot);
		jNextChart.setAntiAlias(true);
		jNextChart.setBackgroundPaint(jPanel.getBackground());

		ChartPanel jChartPanel = new ChartPanel(jNextChart);
		jChartPanel.addMouseListener(listener);
		jChartPanel.setDomainZoomable(false);
		jChartPanel.setRangeZoomable(false);
		jChartPanel.setPopupMenu(null);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jChartPanel)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOwners)
					.addComponent(jQuickDate)
					.addComponent(jFrom)
					.addComponent(jTo)
					.addComponent(jAll)
					.addComponent(jTotal)
					.addComponent(jWalletBalance)
					.addComponent(jAssets)
					.addComponent(jSellOrders)
					.addComponent(jEscrows)
					.addComponent(jEscrowsToCover)
				)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jChartPanel)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOwners, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jQuickDate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jTotal, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jWalletBalance, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAssets, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSellOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jEscrows, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jEscrowsToCover, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	@Override
	public void updateData() {
		Set<TrackerOwner> owners = new TreeSet<TrackerOwner>(program.getSettings().getTrackerData().keySet());
		if (owners.isEmpty()) {
			jOwners.setEnabled(false);
			jOwners.getModel().setSelectedItem(new TrackerOwner(-1, TabsTracker.get().noDataFound()));
		} else {
			jOwners.setEnabled(true);
			jOwners.setModel(new DefaultComboBoxModel(owners.toArray()));
		}
		createData();
	}

	private JDateChooser createDateChooser(String title) {
		JDateChooser jDate = new JDateChooser(Settings.getNow());
		jDate.setBorder(BorderFactory.createTitledBorder(title));
		jDate.setDateFormatString(Formater.COLUMN_FORMAT);
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
		for (Asset asset : program.getAssetEventList()) {
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
		for (AccountBalance accountBalance : program.getAccountBalanceEventList()) {
			TrackerData trackerData = getTrackerData(data, accountBalance.getOwnerID(), accountBalance.getOwner(), date);
			trackerData.addWalletBalance(accountBalance.getBalance());
			allTracker.addWalletBalance(accountBalance.getBalance());
		}
		//Market Orders
		for (MarketOrder marketOrder : program.getMarketOrdersEventList()) {
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
		//Add everything
		for (Map.Entry<TrackerOwner, TrackerData> entry : data.entrySet()) {
			TrackerOwner trackerOwner = entry.getKey();
			TrackerData trackerData = entry.getValue();
			//New TrackerOwner
			if (!program.getSettings().getTrackerData().containsKey(trackerOwner)) {
				program.getSettings().getTrackerData().put(trackerOwner, new ArrayList<TrackerData>());
			}
			program.getSettings().getTrackerData().get(trackerOwner).add(trackerData);
			
		}
		//Update data
		updateData();
	}

	private double deepAsset(List<Asset> assets) {
		double assetValue = 0;
		for (Asset asset : assets) {
			
			assetValue = assetValue + deepAsset(asset.getAssets());
		}
		return assetValue;
	}

	private void createData() {
		TrackerOwner owner = (TrackerOwner) jOwners.getSelectedItem();
		total = new TimePeriodValues(TabsTracker.get().total());
		walletBalance = new TimePeriodValues(TabsTracker.get().walletBalanc());
		assets = new TimePeriodValues(TabsTracker.get().assets());
		sellOrders = new TimePeriodValues(TabsTracker.get().sellOrders());
		escrows = new TimePeriodValues(TabsTracker.get().escrows());
		escrowsToCover = new TimePeriodValues(TabsTracker.get().escrowsToCover());
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
			for (TrackerData data : program.getSettings().getTrackerData().get(owner)) {
				SimpleTimePeriod date = new SimpleTimePeriod(data.getDate(), data.getDate());
				if ((from == null || data.getDate().after(from)) && (to == null || data.getDate().before(to))) {
					total.add(date, data.getTotal());
					walletBalance.add(date, data.getWalletBalance());
					assets.add(date, data.getAssets());
					sellOrders.add(date, data.getSellOrders());
					escrows.add(date, data.getEscrows());
					escrowsToCover.add(date, data.getEscrowsToCover());
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
			if (max > 1000000000000.0) {     //Higher than 1 Trillion
				rangeAxis.setNumberFormatOverride(Formater.TRILLIONS_FORMAT);
			} else if (max > 1000000000.0) { //Higher than 1 Billion
				rangeAxis.setNumberFormatOverride(Formater.BILLIONS_FORMAT);
			} else if (max > 1000000.0) {    //Higher than 1 Million
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

	private class Listener extends MouseAdapter implements 
			ActionListener, PropertyChangeListener {

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
			if (ACTION_QUICK_DATE.equals(e.getActionCommand())) {
				QuickDate quickDate = (QuickDate) jQuickDate.getSelectedItem();
				Date toDate = jTo.getDate();
				if (toDate == null) {
					toDate = new Date(); //now
				}
				Date fromDate = quickDate.apply(toDate);
				if (fromDate != null) {
					jFrom.setDate(fromDate);
				}
			}
			if (ACTION_UPDATE_DATA.equals(e.getActionCommand())) {
				createData();
			}
			if (ACTION_UPDATE_SHOWN.equals(e.getActionCommand())) {
				updateShown();
				jAll.setSelected(jTotal.isSelected()
						&& jWalletBalance.isSelected()
						&& jAssets.isSelected()
						&& jSellOrders.isSelected()
						&& jEscrows.isSelected()
						&& jEscrowsToCover.isSelected());
			}
			if (ACTION_All.equals(e.getActionCommand())) {
				jTotal.setSelected(jAll.isSelected());
				jWalletBalance.setSelected(jAll.isSelected());
				jAssets.setSelected(jAll.isSelected());
				jSellOrders.setSelected(jAll.isSelected());
				jEscrows.setSelected(jAll.isSelected());
				jEscrowsToCover.setSelected(jAll.isSelected());
				updateShown();
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
	}
}
