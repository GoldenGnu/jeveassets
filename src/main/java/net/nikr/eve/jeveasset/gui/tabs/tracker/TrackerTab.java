/*
 * Copyright 2009-2015 Contributors (see credits.txt)
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionList;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableTab;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsTracker;
import net.nikr.eve.jeveasset.i18n.TabsValues;
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
		DELETE,
		PROFILE
	}

	private final int PANEL_WIDTH = 140;
	private final int LABEL_WIDTH = 45;

	private final NumberFormat iskFormat = new DecimalFormat("#,##0.00 isk");
	private final DateFormat dateFormat = new SimpleDateFormat(Formater.COLUMN_DATE);

	private final JFreeChart jNextChart;
	private final JDateChooser jFrom;
	private final JDateChooser jTo;
	private final JMultiSelectionList jOwners;
	private final JComboBox jQuickDate;
	private final JCheckBox jAll;
	private final JCheckBox jTotal;
	private final JCheckBox jWalletBalance;
	private final JCheckBox jAssets;
	private final JCheckBox jSellOrders;
	private final JCheckBox jEscrows;
	private final JCheckBox jEscrowsToCover;
	private final JCheckBox jManufacturing;
	private final JCheckBox jContractCollateral;
	private final JCheckBox jAllProfiles;
	private final JPopupMenu jPopupMenu;
	private final JMenuItem jIskValue;
	private final JMenuItem jDateValue;
	private final JTrackerEditDialog jEditDialog;
	private final JOwnerDialog jOwnerDialog;
	private final ChartPanel jChartPanel;
	private final JTextArea jHelp;

	private final ListenerClass listener = new ListenerClass();

	private final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
	TimePeriodValues total;
	TimePeriodValues walletBalance;
	TimePeriodValues assets;
	TimePeriodValues sellOrders;
	TimePeriodValues escrows;
	TimePeriodValues escrowsToCover;
	TimePeriodValues manufacturing;
	TimePeriodValues contractCollateral;

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

		jOwnerDialog = new JOwnerDialog(program);

		JSeparator jOwnersSeparator = new JSeparator();

		jOwners = new JMultiSelectionList();
		jOwners.getSelectionModel().addListSelectionListener(listener);
		JScrollPane jOwnersScroll = new JScrollPane(jOwners);

		JSeparator jDateSeparator = new JSeparator();

		jQuickDate = new JComboBox(QuickDate.values());
		jQuickDate.setActionCommand(TrackerAction.QUICK_DATE.name());
		jQuickDate.addActionListener(listener);

		JLabel jFromLabel = new JLabel(TabsTracker.get().from());
		jFrom = createDateChooser();

		JLabel jToLabel = new JLabel(TabsTracker.get().to());
		jTo = createDateChooser();

		JSeparator jDataSeparator = new JSeparator();

		jAllProfiles = new JCheckBox(TabsTracker.get().allProfiles());
		jAllProfiles.setActionCommand(TrackerAction.PROFILE.name());
		jAllProfiles.addActionListener(listener);
		
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

		jContractCollateral = new JCheckBox(TabsTracker.get().contractCollateral());
		jContractCollateral.setSelected(true);
		jContractCollateral.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jContractCollateral.addActionListener(listener);

		jHelp = new JTextArea();
		jHelp.setEditable(false);
		jHelp.setFocusable(false);
		jHelp.setOpaque(false);
		jHelp.setBorder(null);
		jHelp.setWrapStyleWord(true);
		jHelp.setLineWrap(true);
		jHelp.setFont(jPanel.getFont());
		jHelp.setText(TabsTracker.get().help());

		DateAxis domainAxis = new DateAxis();
		domainAxis.setDateFormatOverride(dateFormat);
		domainAxis.setVerticalTickLabels(true);
		domainAxis.setAutoTickUnitSelection(true);
		domainAxis.setAutoRange(true);
		domainAxis.setTickLabelFont(jFromLabel.getFont());

		NumberAxis rangeAxis = new NumberAxis();
		rangeAxis.setAutoRange(true);
		rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
		rangeAxis.setTickLabelFont(jFromLabel.getFont());

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
		jNextChart.getLegend().setItemFont(jFrom.getFont());

		jChartPanel = new ChartPanel(jNextChart);
		jChartPanel.addMouseListener(listener);
		jChartPanel.setDomainZoomable(false);
		jChartPanel.setRangeZoomable(false);
		jChartPanel.setPopupMenu(null);
		jChartPanel.addChartMouseListener(listener);
		jChartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
		jChartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
		jChartPanel.setMinimumDrawWidth(10);
		jChartPanel.setMinimumDrawHeight(10);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jChartPanel)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAllProfiles, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jOwnersScroll, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jOwnersSeparator, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jQuickDate, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
							.addComponent(jFromLabel, LABEL_WIDTH, LABEL_WIDTH, LABEL_WIDTH)
							.addComponent(jToLabel, LABEL_WIDTH, LABEL_WIDTH, LABEL_WIDTH)
						)
						.addGap(0)
						.addGroup(layout.createParallelGroup()	
							.addComponent(jFrom, PANEL_WIDTH - LABEL_WIDTH, PANEL_WIDTH - LABEL_WIDTH, PANEL_WIDTH - LABEL_WIDTH)
							.addComponent(jTo, PANEL_WIDTH - LABEL_WIDTH, PANEL_WIDTH - LABEL_WIDTH, PANEL_WIDTH - LABEL_WIDTH)
						)
					)
					.addComponent(jDateSeparator, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jAll, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jTotal, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jWalletBalance, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jAssets, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jSellOrders, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jEscrows, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jEscrowsToCover, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jManufacturing, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jContractCollateral, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jDataSeparator, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jHelp, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jDataSeparator, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addComponent(jChartPanel)
				.addGroup(layout.createSequentialGroup()
					.addGap(5)
					.addComponent(jAllProfiles, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addGap(5)
					.addComponent(jOwnersScroll, 70, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					.addGap(10)
					.addComponent(jOwnersSeparator, 3, 3, 3)
					.addGap(10)
					.addComponent(jQuickDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup()
						.addComponent(jFromLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(jFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jToLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(jTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addGap(10)
					.addComponent(jDateSeparator, 3, 3, 3)
					.addGap(10)
					.addComponent(jAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jTotal, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jWalletBalance, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAssets, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSellOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jEscrows, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jEscrowsToCover, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jManufacturing, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jContractCollateral, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addGap(10)
					.addComponent(jDataSeparator, 3, 3, 3)
					.addGap(0)
					.addComponent(jHelp)
				)
		);
	}

	@Override
	public void updateData() {
		updateOwners();
		createData();
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

	public void createTrackerDataPoint() {
		Map<String, Value> data = ValueTableTab.createDataSet(program);
		
		//Add everything
		for (Map.Entry<String, Value> entry : data.entrySet()) {
			String owner = entry.getKey();
			Value value = entry.getValue();
			if (owner.equals(TabsValues.get().grandTotal())) {
				continue;
			}
			//New TrackerOwner
			List<Value> list = Settings.get().getTrackerData().get(owner);
			if (list == null) {
				list = new ArrayList<Value>();
				Settings.get().getTrackerData().put(owner, list);
			}
			list.add(value);
			
		}
		//Update data
		updateData();
	}

	private void updateOwners() {
		Set<String> owners = new TreeSet<String>(Settings.get().getTrackerData().keySet());
		final List<String> ownersList;
		if (jAllProfiles.isSelected()) {
			ownersList = new ArrayList<String>(owners);
		} else {
			ownersList = new ArrayList<String>();
			for (String s : owners) {
				if (program.getOwners(false).contains(s)) {
					ownersList.add(s);
				}
			}
		}
		if (owners.isEmpty()) {
			jOwners.setEnabled(false);
			jOwners.setModel(new AbstractListModel() {
				@Override
				public int getSize() {
					return 1;
				}

				@Override
				public Object getElementAt(int index) {
					return TabsTracker.get().noDataFound();
				}
			});
		} else {
			jOwners.setEnabled(true);
			jOwners.setModel(new AbstractListModel() {
				@Override
				public int getSize() {
					return ownersList.size();
				}

				@Override
				public Object getElementAt(int index) {
					return ownersList.get(index);
				}
			});
			jOwners.selectAll();
		}
	}

	private void createData() {
		Object[] owners = jOwners.getSelectedValues();
		total = new TimePeriodValues(TabsTracker.get().total());
		walletBalance = new TimePeriodValues(TabsTracker.get().walletBalance());
		assets = new TimePeriodValues(TabsTracker.get().assets());
		sellOrders = new TimePeriodValues(TabsTracker.get().sellOrders());
		escrows = new TimePeriodValues(TabsTracker.get().escrows());
		escrowsToCover = new TimePeriodValues(TabsTracker.get().escrowsToCover());
		manufacturing = new TimePeriodValues(TabsTracker.get().manufacturing());
		contractCollateral = new TimePeriodValues(TabsTracker.get().contractCollateral());
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
		if (owners != null && owners.length > 0) { //No data set...
			Map<SimpleTimePeriod, Value> cache = new TreeMap<SimpleTimePeriod, Value>();
			for (Object o : owners) {
				String owner = (String) o;
				for (Value data : Settings.get().getTrackerData().get(owner)) {
					SimpleTimePeriod date = new SimpleTimePeriod(data.getDate(), data.getDate());
					if ((from == null || data.getDate().after(from)) && (to == null || data.getDate().before(to))) {
						Value value = cache.get(date);
						if (value == null) {
							value = new Value(data.getDate());
							cache.put(date, value);
						}
						value.addAssets(data.getAssets());
						value.addEscrows(data.getEscrows());
						value.addEscrowsToCover(data.getEscrowsToCover());
						value.addManufacturing(data.getManufacturing());
						value.addContractCollateral(data.getContractCollateral());
						value.addSellOrders(data.getSellOrders());
						value.addBalance(data.getBalance());
					}
				}
			}
			for (Map.Entry<SimpleTimePeriod, Value> entry : cache.entrySet()) {
				total.add(entry.getKey(), entry.getValue().getTotal());
				walletBalance.add(entry.getKey(), entry.getValue().getBalance());
				assets.add(entry.getKey(), entry.getValue().getAssets());
				sellOrders.add(entry.getKey(), entry.getValue().getSellOrders());
				escrows.add(entry.getKey(), entry.getValue().getEscrows());
				escrowsToCover.add(entry.getKey(), entry.getValue().getEscrowsToCover());
				manufacturing.add(entry.getKey(), entry.getValue().getManufacturing());
				contractCollateral.add(entry.getKey(), entry.getValue().getContractCollateral());
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
		if (jContractCollateral.isSelected() && contractCollateral != null) {
			dataset.addSeries(contractCollateral);
			updateRender(dataset.getSeriesCount() - 1, Color.PINK);
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

	private String getSelectedOwner(boolean all) {
		if (jOwners.getSelectedIndices().length == 1) {
			return (String) jOwners.getSelectedValue();
		} else {
			Object[] owners = jOwners.getSelectedValues();
			List<Object> list = new ArrayList<Object>();
			if (all) {
				list.add(General.get().all());
			}
			for (Object owner : owners) {
				Value value = getSelectedValue((String)owner);
				if (value != null) {
					list.add(owner);
				}
			}
			return jOwnerDialog.show(list.toArray());
		}
	}

	private Value getSelectedValue(String owner) {
		String date = Formater.simpleDate(new Date((long)jNextChart.getXYPlot().getDomainCrosshairValue()));
		for (Value value : Settings.get().getTrackerData().get(owner)) {
			if (date.equals(Formater.simpleDate(value.getDate()))) {
				return value;
			}
		}
		return null;

	}

	private class ListenerClass extends MouseAdapter implements 
			ActionListener, PropertyChangeListener, PopupMenuListener,
			ChartMouseListener, ListSelectionListener {

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
						&& jManufacturing.isSelected()
						&& jContractCollateral.isSelected());
			} else if (TrackerAction.ALL.name().equals(e.getActionCommand())) {
				jTotal.setSelected(jAll.isSelected());
				jWalletBalance.setSelected(jAll.isSelected());
				jAssets.setSelected(jAll.isSelected());
				jSellOrders.setSelected(jAll.isSelected());
				jEscrows.setSelected(jAll.isSelected());
				jEscrowsToCover.setSelected(jAll.isSelected());
				jManufacturing.setSelected(jAll.isSelected());
				jContractCollateral.setSelected(jAll.isSelected());
				updateShown();
			} else if (TrackerAction.EDIT.name().equals(e.getActionCommand())) {
				jNextChart.getXYPlot().setDomainCrosshairVisible(true);
				String owner = getSelectedOwner(false);
				if (owner == null) {
					return;
				}
				Value value = getSelectedValue(owner);
				if (value == null) {
					return;
				}
				boolean update = jEditDialog.showEdit(value);
				if (update) {
					createData();
				}
				jNextChart.getXYPlot().setDomainCrosshairVisible(false);
			} else if (TrackerAction.DELETE.name().equals(e.getActionCommand())) {
				jNextChart.getXYPlot().setDomainCrosshairVisible(true);
				String owner = getSelectedOwner(true);
				if (owner == null) {
					return;
				}
				List<String> owners = new ArrayList<String>();
				if (owner.equals(General.get().all())) {
					for (Object obj : jOwners.getSelectedValues()) {
						owners.add((String)obj);
					}
				} else {
					owners.add(owner);
				}
				Map<String, Value> values = new HashMap<String, Value>();
				for (String s : owners) {
					Value value = getSelectedValue(s);
					if (value != null) {
						values.put(s, value);
					}
				}
				if (values.isEmpty()) {
					return;
				}
				
				int retrunValue = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsTracker.get().deleteSelected(), TabsTracker.get().delete(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (retrunValue == JOptionPane.OK_OPTION) {
					Settings.lock();
					for (Map.Entry<String, Value> entry : values.entrySet()) {
						//Remove value
						Settings.get().getTrackerData().get(entry.getKey()).remove(entry.getValue());
						//Remove empty owner
						if (Settings.get().getTrackerData().get(entry.getKey()).isEmpty()) {
							Settings.get().getTrackerData().remove(entry.getKey());
							updateOwners();
						} 
					}
					Settings.unlock();
					program.saveSettings("Save Tracker Data (Delete)");
					createData();
				}
				jNextChart.getXYPlot().setDomainCrosshairVisible(false);
			} else if (TrackerAction.PROFILE.name().equals(e.getActionCommand())) {
				updateOwners();
				updateData();
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
						if (total.isEmpty()) {
							return;
						}
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

		@Override
		public void valueChanged(ListSelectionEvent e) {
			createData();
		}
	}
}
