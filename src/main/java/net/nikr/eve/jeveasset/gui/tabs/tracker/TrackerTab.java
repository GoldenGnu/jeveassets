/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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

import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.AbstractListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.CheckBoxNode;
import net.nikr.eve.jeveasset.gui.shared.components.JDateChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionList;
import net.nikr.eve.jeveasset.gui.shared.components.JSelectionDialog;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.gui.tabs.values.Value.AssetValue;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsTracker;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;


public class TrackerTab extends JMainTabSecondary {

	private enum TrackerAction {
		QUICK_DATE,
		UPDATE_DATA,
		UPDATE_SHOWN,
		INCLUDE_ZERO,
		ALL,
		EDIT,
		DELETE,
		NOTE_ADD,
		NOTE_DELETE,
		PROFILE,
		FILTER_ASSETS,
		FILTER_WALLET_BALANCE
	}

	private final int PANEL_WIDTH = 160;
	private final int LABEL_WIDTH = 45;

	private final NumberFormat iskFormat = new DecimalFormat("#,##0.00 isk");
	private final DateFormat dateFormat = new SimpleDateFormat(Formater.COLUMN_DATE);

	private final JFreeChart jNextChart;
	private final JDateChooser jFrom;
	private final JDateChooser jTo;
	private final JMultiSelectionList<String> jOwners;
	private final JComboBox<QuickDate> jQuickDate;
	private final JCheckBox jAll;
	private final JCheckBox jTotal;
	private final JCheckBox jWalletBalance;
	private final JButton jWalletBalanceFilters;
	private final JCheckBox jAssets;
	private final JButton jAssetsFilters;
	private final JCheckBox jSellOrders;
	private final JCheckBox jEscrows;
	private final JCheckBox jEscrowsToCover;
	private final JCheckBox jManufacturing;
	private final JCheckBox jContractCollateral;
	private final JCheckBox jContractValue;
	private final JCheckBox jAllProfiles;
	private final JCheckBoxMenuItem jIncludeZero;
	private final JPopupMenu jPopupMenu;
	private final JMenuItem jNote;
	private final JMenuItem jIskValue;
	private final JMenuItem jDateValue;
	private final JTrackerEditDialog jEditDialog;
	private final JSelectionDialog<String> jSelectionDialog;
	private final ChartPanel jChartPanel;
	private final TrackerFilterDialog filterDialog;
	private final MyRender render;
	private final Shape NO_FILTER = new Rectangle(-3, -3, 6, 6);
	private final Shape FILTER_AND_DEFAULT = new Ellipse2D.Float(-3.0f, -3.0f, 6.0f, 6.0f);
	private final JMenuItem jAddNote;
	private final JMenu jEditNote;

	private final JLabel jTotalStatus;
	private final JLabel jWalletBalanceStatus;
	private final JLabel jAssetsStatus;
	private final JLabel jSellOrdersStatus;
	private final JLabel jEscrowsStatus;
	private final JLabel jEscrowsToCoverStatus;
	private final JLabel jManufacturingStatus;
	private final JLabel jContractCollateralStatus;
	private final JLabel jContractValueStatus;

	private final ListenerClass listener = new ListenerClass();

	private final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
	private TimePeriodValues walletBalance;
	private TimePeriodValues assets;
	private TimePeriodValues sellOrders;
	private TimePeriodValues escrows;
	private TimePeriodValues escrowsToCover;
	private TimePeriodValues manufacturing;
	private TimePeriodValues contractCollateral;
	private TimePeriodValues contractValue;
	private Map<SimpleTimePeriod, Value> cache;
	private final Map<String, CheckBoxNode> accountNodes = new TreeMap<String, CheckBoxNode>();
	private final Map<String, CheckBoxNode> assetNodes = new TreeMap<String, CheckBoxNode>();
	private Integer assetColumn = null;
	private Integer walletColumn = null;
	private boolean updateLock = false;

	public TrackerTab(Program program) {
		super(program, TabsTracker.get().title(), Images.TOOL_TRACKER.getIcon(), true);

		filterDialog = new TrackerFilterDialog(program);

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

		jPopupMenu.addSeparator();

		jAddNote = new JMenuItem(TabsTracker.get().notesAdd(), Images.EDIT_ADD.getIcon());
		jAddNote.setActionCommand(TrackerAction.NOTE_ADD.name());
		jAddNote.addActionListener(listener);
		jPopupMenu.add(jAddNote);

		jEditNote = new JMenu(TabsTracker.get().note());
		jEditNote.setIcon(Images.SETTINGS_USER_NAME.getIcon());
		jPopupMenu.add(jEditNote);

		jMenuItem = new JMenuItem(TabsTracker.get().edit(), Images.EDIT_EDIT.getIcon());
		jMenuItem.setActionCommand(TrackerAction.NOTE_ADD.name());
		jMenuItem.addActionListener(listener);
		jEditNote.add(jMenuItem);

		jMenuItem = new JMenuItem(TabsTracker.get().delete(), Images.EDIT_DELETE.getIcon());
		jMenuItem.setActionCommand(TrackerAction.NOTE_DELETE.name());
		jMenuItem.addActionListener(listener);
		jEditNote.add(jMenuItem);

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
		jDateValue.setDisabledIcon(Images.EDIT_DATE.getIcon());
		jPopupMenu.add(jDateValue);

		jNote = new JMenuItem();
		jNote.setEnabled(false);
		jNote.setForeground(Color.BLACK);
		jNote.setDisabledIcon(Images.SETTINGS_USER_NAME.getIcon());
		jPopupMenu.add(jNote);

		jEditDialog = new JTrackerEditDialog(program);

		jSelectionDialog = new JSelectionDialog<String>(program);

		JSeparator jDateSeparator = new JSeparator();

		jQuickDate = new JComboBox<QuickDate>(QuickDate.values());
		jQuickDate.setActionCommand(TrackerAction.QUICK_DATE.name());
		jQuickDate.addActionListener(listener);

		JLabel jFromLabel = new JLabel(TabsTracker.get().from());
		jFrom = createDateChooser();

		JLabel jToLabel = new JLabel(TabsTracker.get().to());
		jTo = createDateChooser();
		
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

		jWalletBalanceFilters = new JButton(Images.LOC_INCLUDE.getIcon());
		jWalletBalanceFilters.setActionCommand(TrackerAction.FILTER_WALLET_BALANCE.name());
		jWalletBalanceFilters.addActionListener(listener);

		jAssets = new JCheckBox(TabsTracker.get().assets());
		jAssets.setSelected(true);
		jAssets.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jAssets.addActionListener(listener);

		jAssetsFilters = new JButton(Images.LOC_INCLUDE.getIcon());
		jAssetsFilters.setActionCommand(TrackerAction.FILTER_ASSETS.name());
		jAssetsFilters.addActionListener(listener);

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

		jContractValue = new JCheckBox(TabsTracker.get().contractValue());
		jContractValue.setSelected(true);
		jContractValue.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jContractValue.addActionListener(listener);

		JSeparator jOwnersSeparator = new JSeparator();

		jAllProfiles = new JCheckBox(TabsTracker.get().allProfiles());
		jAllProfiles.setActionCommand(TrackerAction.PROFILE.name());
		jAllProfiles.addActionListener(listener);

		jOwners = new JMultiSelectionList<String>();
		jOwners.getSelectionModel().addListSelectionListener(listener);
		JScrollPane jOwnersScroll = new JScrollPane(jOwners);

		jTotalStatus = StatusPanel.createLabel(TabsTracker.get().statusTotal(), new ColorIcon(Color.RED.darker()));
		this.addStatusbarLabel(jTotalStatus);

		jWalletBalanceStatus = StatusPanel.createLabel(TabsTracker.get().statusBalance(), new ColorIcon(Color.BLUE.darker()));
		this.addStatusbarLabel(jWalletBalanceStatus);

		jAssetsStatus = StatusPanel.createLabel(TabsTracker.get().statusAssets(), new ColorIcon(Color.GREEN.darker().darker()));
		this.addStatusbarLabel(jAssetsStatus);

		jSellOrdersStatus = StatusPanel.createLabel(TabsTracker.get().statusSellOrders(), new ColorIcon(Color.CYAN.darker()));
		this.addStatusbarLabel(jSellOrdersStatus);

		jEscrowsStatus = StatusPanel.createLabel(TabsTracker.get().statusEscrows(), new ColorIcon(Color.BLACK));
		this.addStatusbarLabel(jEscrowsStatus);

		jEscrowsToCoverStatus = StatusPanel.createLabel(TabsTracker.get().statusEscrowsToCover(), new ColorIcon(Color.GRAY));
		this.addStatusbarLabel(jEscrowsToCoverStatus);

		jManufacturingStatus = StatusPanel.createLabel(TabsTracker.get().statusManufacturing(), new ColorIcon(Color.MAGENTA));
		this.addStatusbarLabel(jManufacturingStatus);

		jContractCollateralStatus = StatusPanel.createLabel(TabsTracker.get().statusContractCollateral(), new ColorIcon(Color.PINK));
		this.addStatusbarLabel(jContractCollateralStatus);

		jContractValueStatus = StatusPanel.createLabel(TabsTracker.get().statusContractValue(), new ColorIcon(Color.ORANGE));
		this.addStatusbarLabel(jContractValueStatus);

		JLabel jHelp = new JLabel(TabsTracker.get().help());
		jHelp.setIcon(Images.MISC_HELP.getIcon());

		JLabel jNoFilter = new JLabel(TabsTracker.get().helpLegacyData());
		jNoFilter.setIcon(new ShapeIcon(NO_FILTER));

		JLabel jFilter = new JLabel(TabsTracker.get().helpNewData());
		jFilter.setIcon(new ShapeIcon(FILTER_AND_DEFAULT));

		JDropDownButton jSettings = new JDropDownButton(Images.DIALOG_SETTINGS.getIcon());

		jIncludeZero = new JCheckBoxMenuItem(TabsTracker.get().includeZero());
		jIncludeZero.setSelected(true);
		jIncludeZero.setActionCommand(TrackerAction.INCLUDE_ZERO.name());
		jIncludeZero.addActionListener(listener);
		jSettings.add(jIncludeZero);

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

		//XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, new XYLineAndShapeRenderer(true, true));
		render = new MyRender();
		XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, render);
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.getRenderer().setDefaultToolTipGenerator(new XYToolTipGenerator() {
			@Override
			public String generateToolTip(XYDataset dataset, int series, int item)	{
				Date date = new Date(dataset.getX(series, item).longValue());
				Number isk = dataset.getY(series, item);
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("<html>");
				stringBuilder.append("<b>");
				stringBuilder.append(dataset.getSeriesKey(series));
				stringBuilder.append(":</b> ");
				stringBuilder.append(iskFormat.format(isk));
				stringBuilder.append("<br>");
				stringBuilder.append("<b>");
				stringBuilder.append(TabsTracker.get().date());
				stringBuilder.append(":</b> ");
				stringBuilder.append(dateFormat.format(date));
				TrackerNote trackerNote = Settings.get().getTrackerNotes().get(new TrackerDate(date));
				if (trackerNote != null) {
					stringBuilder.append("<br><b>");
					stringBuilder.append(TabsTracker.get().note());
					stringBuilder.append(":</b> ");
					stringBuilder.append(trackerNote.getNote());
				}
				return stringBuilder.toString();
			}
		});
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

		int AssetsGapWidth = PANEL_WIDTH - jAssets.getPreferredSize().width - jAssetsFilters.getPreferredSize().width;
		if (AssetsGapWidth < 0) {
			AssetsGapWidth = 0;
		}
		int WalletGapWidth = PANEL_WIDTH - jWalletBalance.getPreferredSize().width - jWalletBalanceFilters.getPreferredSize().width;
		if (WalletGapWidth < 0) {
			WalletGapWidth = 0;
		}
		
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
						.addComponent(jHelp)
						.addGap(20)
						.addComponent(jNoFilter)
						.addGap(20)
						.addComponent(jFilter)
						.addGap(20, 20, Integer.MAX_VALUE)
						.addComponent(jSettings)
						.addGap(6)
					)
					.addComponent(jChartPanel)
				)
				.addGroup(layout.createParallelGroup()
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
					.addGroup(layout.createSequentialGroup()
						.addComponent(jWalletBalance)
						.addGap(0, 0, WalletGapWidth)
						.addComponent(jWalletBalanceFilters)
					)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jAssets)
						.addGap(0, 0, AssetsGapWidth)
						.addComponent(jAssetsFilters)
					)
					.addComponent(jSellOrders, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jEscrows, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jEscrowsToCover, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jManufacturing, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jContractCollateral, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jContractValue, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jOwnersSeparator, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jAllProfiles, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
					.addComponent(jOwnersScroll, PANEL_WIDTH, PANEL_WIDTH, PANEL_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
							.addComponent(jHelp, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
							.addComponent(jNoFilter, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
							.addComponent(jFilter, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
							.addComponent(jSettings, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
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
					.addComponent(jAll, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jTotal, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addGroup(layout.createParallelGroup()
						.addComponent(jWalletBalance, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jWalletBalanceFilters, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jAssets, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jAssetsFilters, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
					.addComponent(jSellOrders, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jEscrows, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jEscrowsToCover, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jManufacturing, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContractCollateral, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jContractValue, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOwnersSeparator, 3, 3, 3)
					.addComponent(jAllProfiles, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOwnersScroll, 70, 70, Integer.MAX_VALUE)
				)
		);
	}

	@Override
	public void updateData() {
		updateNodes(); //Must be first or NPE!
		updateButtonIcons();
		updateOwners();
		createData();
		updateFilterButtons();
	}

	@Override
	public void clearData() { }

	@Override
	public void updateCache() { }

	private JDateChooser createDateChooser() {
		JDateChooser jDate = new JDateChooser(true);
		jDate.addDateChangeListener(listener);
		return jDate;
	}

	private void updateFilterButtons() {
		Set<String> walletIDs = new TreeSet<String>();
		Set<AssetValue> assetsIDs = new TreeSet<AssetValue>();
		List<String> owners = jOwners.getSelectedValuesList();
		for (String owner : owners) {
			for (Value data : Settings.get().getTrackerData().get(owner)) {
				//Get all account wallet account keys
				walletIDs.addAll(data.getBalanceFilter().keySet());
				//Get all asset IDs
				assetsIDs.addAll(data.getAssetsFilter().keySet());
			}
		}
		jWalletBalanceFilters.setEnabled(!walletIDs.isEmpty());
		jAssetsFilters.setEnabled(!assetsIDs.isEmpty());
	}

	private void updateOwners() {
		updateLock = true;
		Set<String> owners = new TreeSet<String>(Settings.get().getTrackerData().keySet());
		final List<String> ownersList;
		if (jAllProfiles.isSelected()) {
			ownersList = new ArrayList<String>(owners);
		} else {
			ownersList = new ArrayList<String>();
			for (String s : owners) {
				if (program.getOwnerNames(false).contains(s)) {
					ownersList.add(s);
				}
			}
		}
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
			jOwners.setEnabled(true);
			jOwners.setModel(new AbstractListModel<String>() {
				@Override
				public int getSize() {
					return ownersList.size();
				}

				@Override
				public String getElementAt(int index) {
					return ownersList.get(index);
				}
			});
			jOwners.selectAll();
		}
		updateLock = false;
	}

	private void updateNodes() {
		accountNodes.clear();
		assetNodes.clear();
	//Find all saved Keys/IDs
		Set<String> walletIDs = new TreeSet<String>();
		Set<AssetValue> assetsIDs = new TreeSet<AssetValue>();
		for (List<Value> values : Settings.get().getTrackerData().values()) {
			for (Value data : values) {
				//Get all account wallet account keys
				walletIDs.addAll(data.getBalanceFilter().keySet());
				//Get all asset IDs
				assetsIDs.addAll(data.getAssetsFilter().keySet());
			}
		}

		//WALLET - Make nodes for found wallet account keys
		CheckBoxNode corporationWalletNode = new CheckBoxNode(null, TabsTracker.get().corporationWallet(), TabsTracker.get().corporationWallet(), false);
		for (String id : walletIDs) {
			accountNodes.put(id, new CheckBoxNode(corporationWalletNode, id, TabsTracker.get().division(id), selectNode(id)));
		}
		String charecterWalletID = "0";
		CheckBoxNode charecterWalletNode = new CheckBoxNode(null, charecterWalletID, TabsTracker.get().characterWallet(), selectNode(charecterWalletID));
		accountNodes.put(charecterWalletID, charecterWalletNode);

		//ASSETS - Make nodes for found asset IDs
		CheckBoxNode assetNode = new CheckBoxNode(null, TabsTracker.get().assets(), TabsTracker.get().assets(), false);
		
		Map<String, CheckBoxNode> nodeCache = new HashMap<String, CheckBoxNode>();
		for (AssetValue assetValue : assetsIDs) {
			String location = assetValue.getLocation();
			String flag = assetValue.getFlag();
			String id = assetValue.getID();
			CheckBoxNode locationNode = nodeCache.get(location);
			if (locationNode == null) {
				locationNode = new CheckBoxNode(assetNode, location, location, selectNode(location));
				nodeCache.put(location, locationNode);
			}

			CheckBoxNode flagNode = nodeCache.get(id);
			if (flagNode == null) {
				flagNode = new CheckBoxNode(locationNode, id, flag, selectNode(id));
				nodeCache.put(id, flagNode);
			}
			assetNodes.put(id, flagNode);
		}
		//For locations with office, you should have the option to exclude all values in corp hangars
		for (CheckBoxNode locationNode : nodeCache.values()) {
			if (locationNode.isParent()) {
				String id = locationNode.getNodeId() + " > unique ID";
				CheckBoxNode otherNode = new CheckBoxNode(locationNode, id, TabsTracker.get().other(), selectNode(id));
				assetNodes.put(locationNode.getNodeId(), otherNode);
			}
		}
	}

	private boolean selectNode(String id) {
		Boolean selected = Settings.get().getTrackerFilters().get(id);
		if (selected != null) {
			return selected;
		} else {
			return Settings.get().isTrackerSelectNew();
		}
	}

	private void createData() {
		if (updateLock) {
			return;
		}
		List<String> owners = jOwners.getSelectedValuesList();
		walletBalance = new TimePeriodValues(TabsTracker.get().walletBalance());
		assets = new TimePeriodValues(TabsTracker.get().assets());
		sellOrders = new TimePeriodValues(TabsTracker.get().sellOrders());
		escrows = new TimePeriodValues(TabsTracker.get().escrows());
		escrowsToCover = new TimePeriodValues(TabsTracker.get().escrowsToCover());
		manufacturing = new TimePeriodValues(TabsTracker.get().manufacturing());
		contractCollateral = new TimePeriodValues(TabsTracker.get().contractCollateral());
		contractValue = new TimePeriodValues(TabsTracker.get().contractValue());
		Date from = getFromDate();
		Date to = getToDate();
		cache = new TreeMap<SimpleTimePeriod, Value>();
		Map<Date, Boolean> assetColumns = new TreeMap<Date, Boolean>();
		Map<Date, Boolean> walletColumns = new TreeMap<Date, Boolean>();
		if (owners != null) { //No data set...
			for (String owner : owners) {
				for (Value data : Settings.get().getTrackerData().get(owner)) {
					SimpleTimePeriod date = new SimpleTimePeriod(data.getDate(), data.getDate());
					if ((from == null || data.getDate().after(from)) && (to == null || data.getDate().before(to))) {
						Value value = cache.get(date);
						if (value == null) {
							value = new Value(data.getDate());
							cache.put(date, value);
						}

						//Default
						Boolean assetBoolean = assetColumns.get(data.getDate());
						if (assetBoolean == null) {
							assetColumns.put(data.getDate(), false);
						}
						Boolean walletBoolean = walletColumns.get(data.getDate());
						if (walletBoolean == null) {
							walletColumns.put(data.getDate(), false);
						}
						if (data.getAssetsFilter().isEmpty()) {
							value.addAssets(data.getAssetsTotal());
						} else {
							assetColumns.put(data.getDate(), true);
							for (Map.Entry<AssetValue, Double> entry : data.getAssetsFilter().entrySet()) {
								if (assetNodes.get(entry.getKey().getID()).isSelected()) {
									value.addAssets(entry.getKey(), entry.getValue());
								}
							}
						}
						value.addEscrows(data.getEscrows());
						value.addEscrowsToCover(data.getEscrowsToCover());
						value.addManufacturing(data.getManufacturing());
						value.addContractCollateral(data.getContractCollateral());
						value.addContractValue(data.getContractValue());
						value.addSellOrders(data.getSellOrders());
						if (data.getBalanceFilter().isEmpty()) {
							value.addBalance(data.getBalanceTotal());
						} else {
							walletColumns.put(data.getDate(), true);
							for (Map.Entry<String, Double> entry : data.getBalanceFilter().entrySet()) {
								if (accountNodes.get(entry.getKey()).isSelected()) {
									value.addBalance(entry.getKey(), entry.getValue());
								}
							}
						}
					}
				}
			}
			for (Map.Entry<SimpleTimePeriod, Value> entry : cache.entrySet()) {
				walletBalance.add(entry.getKey(), entry.getValue().getBalanceTotal());
				assets.add(entry.getKey(), entry.getValue().getAssetsTotal());
				sellOrders.add(entry.getKey(), entry.getValue().getSellOrders());
				escrows.add(entry.getKey(), entry.getValue().getEscrows());
				escrowsToCover.add(entry.getKey(), entry.getValue().getEscrowsToCover());
				manufacturing.add(entry.getKey(), entry.getValue().getManufacturing());
				contractCollateral.add(entry.getKey(), entry.getValue().getContractCollateral());
				contractValue.add(entry.getKey(), entry.getValue().getContractValue());
			}
		}
		int count;
		count = 0;
		assetColumn = assetColumns.size(); //Default
		for (Map.Entry<Date, Boolean> entry : assetColumns.entrySet()) {
			if (entry.getValue()) {
				assetColumn = count;
				break;
			}
			count++;
		}
		count = 0;
		walletColumn = walletColumns.size(); //Default
		for (Map.Entry<Date, Boolean> entry : walletColumns.entrySet()) {
			if (entry.getValue()) {
				walletColumn = count;
				break;
			}
			count++;
		}
		updateShown();
	}

	private void updateButtonIcons() {
		boolean isAll;
		boolean isSome;
		//Assets
		isAll = true;
		isSome = false;
		for (CheckBoxNode node : assetNodes.values()) {
			if (!node.isSelected()) {
				isAll = false;
			}
			if (node.isSelected()) {
				isSome = true;
			}
		}
		if (isAll) {
			jAssetsFilters.setIcon(Images.LOC_INCLUDE.getIcon());
		} else if (isSome) {
			jAssetsFilters.setIcon(Images.EDIT_EDIT_WHITE.getIcon());
		} else {
			jAssetsFilters.setIcon(Images.UPDATE_DONE_ERROR.getIcon());
		}
		//Wallet
		isAll = true;
		isSome = false;
		for (CheckBoxNode node : accountNodes.values()) {
			if (!node.isSelected()) {
				isAll = false;
			}
			if (node.isSelected()) {
				isSome = true;
			}
		}
		if (isAll) {
			jWalletBalanceFilters.setIcon(Images.LOC_INCLUDE.getIcon());
		} else if (isSome) {
			jWalletBalanceFilters.setIcon(Images.EDIT_EDIT_WHITE.getIcon());
		} else {
			jWalletBalanceFilters.setIcon(Images.UPDATE_DONE_ERROR.getIcon());
		}
	}

	private void updateShown() {
		//Remove All
		while (dataset.getSeriesCount() != 0) {
			dataset.removeSeries(0);
		}
		render.clear();
		TimePeriodValues total = new TimePeriodValues(TabsTracker.get().total());
		Value first = null;
		Value last = null;
		Double firstTotal = null;
		Double lastTotal = null;
		for (Map.Entry<SimpleTimePeriod, Value> entry : cache.entrySet()) {
			double t = 0;
			if (jWalletBalance.isSelected() && walletBalance != null) {
				t += entry.getValue().getBalanceTotal();
			}
			if (jAssets.isSelected() && assets != null) {
				t += entry.getValue().getAssetsTotal();
			}
			if (jSellOrders.isSelected() && sellOrders != null) {
				t += entry.getValue().getSellOrders();
			}
			if (jEscrows.isSelected() && escrows != null) {
				t += entry.getValue().getEscrows();
			}
			//Escrows To Cover is not money you own, It's technically money you owe
			//Therefor it's not included in the total
			//See: https://forums.eveonline.com/default.aspx?g=posts&m=6607898#post6607898
			//if (jEscrowsToCover.isSelected() && escrowsToCover != null) {
			//	t += entry.getValue().getEscrowsToCover();
			//}
			if (jManufacturing.isSelected() && manufacturing != null) {
				t += entry.getValue().getManufacturing();
			}
			if (jContractCollateral.isSelected() && contractCollateral != null) {
				t += entry.getValue().getContractCollateral();
			}
			if (jContractValue.isSelected() && contractValue != null) {
				t += entry.getValue().getContractValue();
			}
			total.add(entry.getKey(), t);
			if (firstTotal == null) {
				firstTotal = t;
			}
			lastTotal = t;
			if (first == null) {
				first = entry.getValue();
			}
			last = entry.getValue();
		}
		if (firstTotal != null && lastTotal != null) {
			jTotalStatus.setText(Formater.iskFormat(lastTotal - firstTotal));
		} else {
			jTotalStatus.setText(Formater.iskFormat(0.0));
		}
		jWalletBalanceStatus.setVisible(jWalletBalance.isSelected());
		if (first != null && last != null) {
			jWalletBalanceStatus.setText(Formater.iskFormat(last.getBalanceTotal() - first.getBalanceTotal()));
		} else {
			jWalletBalanceStatus.setText(Formater.iskFormat(0.0));
		}
		jAssetsStatus.setVisible(jAssets.isSelected());
		if (first != null && last != null) {
			jAssetsStatus.setText(Formater.iskFormat(last.getAssetsTotal() - first.getAssetsTotal()));
		} else {
			jAssetsStatus.setText(Formater.iskFormat(0.0));
		}
		jSellOrdersStatus.setVisible(jSellOrders.isSelected());
		if (first != null && last != null) {
			jSellOrdersStatus.setText(Formater.iskFormat(last.getSellOrders() - first.getSellOrders()));
		} else {
			jSellOrdersStatus.setText(Formater.iskFormat(0.0));
		}
		jEscrowsStatus.setVisible(jEscrows.isSelected());
		if (first != null && last != null) {
			jEscrowsStatus.setText(Formater.iskFormat(last.getEscrows() - first.getEscrows()));
		} else {
			jEscrowsStatus.setText(Formater.iskFormat(0.0));
		}
		jEscrowsToCoverStatus.setVisible(jEscrowsToCover.isSelected());
		if (first != null && last != null) {
			jEscrowsToCoverStatus.setText(Formater.iskFormat(last.getEscrowsToCover() - first.getEscrowsToCover()));
		} else {
			jEscrowsToCoverStatus.setText(Formater.iskFormat(0.0));
		}
		jManufacturingStatus.setVisible(jManufacturing.isSelected());
		if (first != null && last != null) {
			jManufacturingStatus.setText(Formater.iskFormat(last.getManufacturing() - first.getManufacturing()));
		} else {
			jManufacturingStatus.setText(Formater.iskFormat(0.0));
		}
		jContractCollateralStatus.setVisible(jContractCollateral.isSelected());
		if (first != null && last != null) {
			jContractCollateralStatus.setText(Formater.iskFormat(last.getContractCollateral() - first.getContractCollateral()));
		} else {
			jContractCollateralStatus.setText(Formater.iskFormat(0.0));
		}
		jContractValueStatus.setVisible(jContractValue.isSelected());
		if (first != null && last != null) {
			jContractValueStatus.setText(Formater.iskFormat(last.getContractValue() - first.getContractValue()));
		} else {
			jContractValueStatus.setText(Formater.iskFormat(0.0));
		}
		if (jTotal.isSelected()) { //Update total
			dataset.addSeries(total);
			Integer minColumn = null;
			if (jWalletBalance.isSelected() && walletColumn != null) {
				minColumn = walletColumn;
			}
			if (jAssets.isSelected() && assetColumn != null) {
				if (minColumn != null) {
					minColumn = Math.min(minColumn, assetColumn);
				} else {
					minColumn = assetColumn;
				}
			}
			render.add(dataset.getSeriesCount() - 1, minColumn);
			updateRender(dataset.getSeriesCount() - 1, Color.RED.darker());
		}
		if (jWalletBalance.isSelected() && walletBalance != null) {
			dataset.addSeries(walletBalance);
			render.add(dataset.getSeriesCount() - 1, walletColumn);
			updateRender(dataset.getSeriesCount() - 1, Color.BLUE.darker());

		}
		if (jAssets.isSelected() && assets != null) {
			dataset.addSeries(assets);
			render.add(dataset.getSeriesCount() - 1, assetColumn);
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
		if (jContractValue.isSelected() && contractValue != null) {
			dataset.addSeries(contractValue);
			updateRender(dataset.getSeriesCount() - 1, Color.ORANGE);
		}
		//Add empty dataset
		if (dataset.getSeriesCount() == 0) {
			TimePeriodValues timePeriodValues = new TimePeriodValues(TabsTracker.get().empty());
			dataset.addSeries(timePeriodValues);
			updateRender(dataset.getSeriesCount() - 1, Color.BLACK);
		}
		jNextChart.getXYPlot().getRangeAxis().setAutoRange(true);
		jNextChart.getXYPlot().getDomainAxis().setAutoRange(true);
		Number maxNumber = DatasetUtils.findMaximumRangeValue(dataset);
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
	}

	private void updateSettings() {
		Settings.lock("Tracker Filters: Update");
		Settings.get().getTrackerFilters().clear();
		for (CheckBoxNode checkBoxNode : assetNodes.values()) {
			Settings.get().getTrackerFilters().put(checkBoxNode.getNodeId(), checkBoxNode.isSelected());
		}
		for (CheckBoxNode checkBoxNode : accountNodes.values()) {
			Settings.get().getTrackerFilters().put(checkBoxNode.getNodeId(), checkBoxNode.isSelected());
		}
		Settings.get().setTrackerSelectNew(filterDialog.isSelectNew());
		Settings.unlock("Tracker Filters: Update");
		Settings.saveSettings();
	}

	private String getSelectedOwner(boolean all) {
		List<String> owners = jOwners.getSelectedValuesList();
		if (owners.size() == 1) {
			return jOwners.getSelectedValue();
		} else {
			List<String> list = new ArrayList<String>();
			if (all) {
				list.add(General.get().all());
			}
			for (String owner : owners) {
				Value value = getSelectedValue(owner);
				if (value != null) {
					list.add(owner);
				}
			}
			return jSelectionDialog.show(TabsTracker.get().selectOwner(), list);
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

	private void addNote() {
		Date date = new Date((long)jNextChart.getXYPlot().getDomainCrosshairValue());
		TrackerNote trackerNote = Settings.get().getTrackerNotes().get(new TrackerDate(date));
		String newNote;
		if (trackerNote == null) {
			newNote = JOptionPane.showInputDialog(program.getMainWindow().getFrame(), TabsTracker.get().notesEditMsg(), TabsTracker.get().notesEditTitle(), JOptionPane.PLAIN_MESSAGE);
		} else {
			newNote = (String) JOptionPane.showInputDialog(program.getMainWindow().getFrame(), TabsTracker.get().notesEditMsg(), TabsTracker.get().notesEditTitle(), JOptionPane.PLAIN_MESSAGE, null, null, trackerNote.getNote());
		}
		if (newNote != null) {
			Settings.lock("Tracker Data (Set Note)");
			Settings.get().getTrackerNotes().put(new TrackerDate(date), new TrackerNote(newNote));
			Settings.unlock("Tracker Data (Set Note)");
			program.saveSettings("Tracker Data (Set Note)");
		}
	}

	private void removeNote() {
		Date date = new Date((long)jNextChart.getXYPlot().getDomainCrosshairValue());
		TrackerNote trackerNote = Settings.get().getTrackerNotes().get(new TrackerDate(date));
		int returnValue = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsTracker.get().notesDeleteMsg(trackerNote.getNote()), TabsTracker.get().notesDeleteTitle(), JOptionPane.OK_CANCEL_OPTION);
		if (returnValue == JOptionPane.OK_OPTION) {
			Settings.lock("Tracker Data (Delete Note)");
			Settings.get().getTrackerNotes().remove(new TrackerDate(date));
			Settings.unlock("Tracker Data (Delete Note)");
			program.saveSettings("Tracker Data (Delete Note)");
		}
	}

	private class MyRender extends XYLineAndShapeRenderer {

		Map<Integer, Integer> renders = new HashMap<Integer, Integer>();
		
		public MyRender() {
			super(true, true);
		}

		@Override
		public Shape getItemShape(int row, int column) {
			Integer findColumn = renders.get(row);
			if (findColumn != null && findColumn > column) {
				return NO_FILTER;
			} else {
				return FILTER_AND_DEFAULT;
			}
		}

		public void clear() {
			renders.clear();
		}

		public void add(int row, Integer column) {
			if (column != null) {
				renders.put(row, column);
			}
		}
	}

	private static class ShapeIcon implements Icon {

		private final Shape shape;

		public ShapeIcon(Shape shape) {
			this.shape = shape;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2d = (Graphics2D)g.create();

			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			g2d.setRenderingHints(rh);

			g2d.setColor(Color.BLACK);
			g2d.translate(x - shape.getBounds().x, y - shape.getBounds().y);
			g2d.fill(shape);
			g2d.dispose();
		}

		@Override
		public int getIconWidth() {
			return shape.getBounds().width;
		}

		@Override
		public int getIconHeight() {
			return shape.getBounds().height;
		}
	}

	private Date getFromDate() {
		LocalDate date = jFrom.getDate();
		if (date == null) {
			return null;
		}
		Instant instant = date.atStartOfDay().atZone(ZoneId.of("GMT")).toInstant();  //Start of day - GMT
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

	private LocalDate dateToLocalDate(Date date) {
		Instant instant = date.toInstant();
		return LocalDateTime.ofInstant(instant, ZoneId.of("GMT")).toLocalDate();
	}

	private class ListenerClass extends MouseAdapter implements 
			ActionListener, PopupMenuListener,
			ChartMouseListener, ListSelectionListener, DateChangeListener {

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
			} else if (TrackerAction.INCLUDE_ZERO.name().equals(e.getActionCommand())) {
				NumberAxis domainAxis = (NumberAxis)jNextChart.getXYPlot().getRangeAxis();
				domainAxis.setAutoRangeIncludesZero(jIncludeZero.isSelected());
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
						&& jContractCollateral.isSelected()
						&& jContractValue.isSelected());
			} else if (TrackerAction.ALL.name().equals(e.getActionCommand())) {
				jTotal.setSelected(jAll.isSelected());
				jWalletBalance.setSelected(jAll.isSelected());
				jAssets.setSelected(jAll.isSelected());
				jSellOrders.setSelected(jAll.isSelected());
				jEscrows.setSelected(jAll.isSelected());
				jEscrowsToCover.setSelected(jAll.isSelected());
				jManufacturing.setSelected(jAll.isSelected());
				jContractCollateral.setSelected(jAll.isSelected());
				jContractValue.setSelected(jAll.isSelected());
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
					owners.addAll(jOwners.getSelectedValuesList());
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
					Settings.lock("Tracker Data (Delete)");
					for (Map.Entry<String, Value> entry : values.entrySet()) {
						//Remove value
						Settings.get().getTrackerData().get(entry.getKey()).remove(entry.getValue());
						//Remove empty owner
						if (Settings.get().getTrackerData().get(entry.getKey()).isEmpty()) {
							Settings.get().getTrackerData().remove(entry.getKey());
						} 
					}
					Settings.unlock("Tracker Data (Delete)");
					program.saveSettings("Tracker Data (Delete)");
					updateData();
				}
				jNextChart.getXYPlot().setDomainCrosshairVisible(false);
			} else if (TrackerAction.NOTE_DELETE.name().equals(e.getActionCommand())) {
				jNextChart.getXYPlot().setDomainCrosshairVisible(true);
				removeNote();
				jNextChart.getXYPlot().setDomainCrosshairVisible(false);
			} else if (TrackerAction.NOTE_ADD.name().equals(e.getActionCommand())) {
				jNextChart.getXYPlot().setDomainCrosshairVisible(true);
				addNote();
				jNextChart.getXYPlot().setDomainCrosshairVisible(false);
			} else if (TrackerAction.PROFILE.name().equals(e.getActionCommand())) {
				updateOwners();
				updateData();
			} else if (TrackerAction.FILTER_WALLET_BALANCE.name().equals(e.getActionCommand())) {
				boolean save = filterDialog.showWallet(accountNodes);
				if (save) { //Need refilter
					updateSettings();
					createData();
					updateButtonIcons();
				}
			} else if (TrackerAction.FILTER_ASSETS.name().equals(e.getActionCommand())) {
				boolean save = filterDialog.showLocations(assetNodes);
				if (save) { //Need refilter
					updateSettings();
					createData();
					updateButtonIcons();
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
				jTo.setDate(dateToLocalDate(from));
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
						if (cache.isEmpty()) {
							return;
						}
						jNextChart.getXYPlot().setDomainCrosshairVisible(true);
						double xValue = jNextChart.getXYPlot().getDomainCrosshairValue();
						double yValue = jNextChart.getXYPlot().getRangeCrosshairValue();
						RectangleEdge xEdge = jNextChart.getXYPlot().getDomainAxisEdge();
						RectangleEdge yEdge = jNextChart.getXYPlot().getRangeAxisEdge();
						Rectangle2D dataArea = jChartPanel.getScreenDataArea(); // jChartPanel.getChartRenderingInfo().getPlotInfo().getSubplotInfo(0).getDataArea();
						int x = (int) jNextChart.getXYPlot().getDomainAxis().valueToJava2D(xValue, dataArea, xEdge);
						int y = (int) jNextChart.getXYPlot().getRangeAxis().valueToJava2D(yValue, dataArea, yEdge);
						Date date = new Date((long)xValue);
						jIskValue.setText(iskFormat.format(yValue));
						jDateValue.setText(dateFormat.format(date));
						TrackerNote trackerNote = Settings.get().getTrackerNotes().get(new TrackerDate(date));
						if (trackerNote != null) {
							jAddNote.setVisible(false);
							jEditNote.setVisible(true);
							jNote.setVisible(true);
							jNote.setText(trackerNote.getNote());
						} else {
							jAddNote.setVisible(true);
							jEditNote.setVisible(false);
							jNote.setVisible(false);
						}
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
			updateFilterButtons();
			createData();
		}
	}

	public static class ColorIcon implements Icon {

		private final Color color;

		public ColorIcon(Color color) {
			this.color = color;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2d = (Graphics2D) g;
			//Render settings
			//g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			//Border
			g2d.setColor(Color.BLACK);
			g2d.fillOval(x + 2, y + 2, getIconWidth() - 4, getIconHeight() - 4);

			//Background
			g2d.setColor(color);
			g2d.fillOval(x + 3, y + 3, getIconWidth() - 6, getIconHeight() - 6);
		}

		@Override
		public int getIconWidth() {
			return 16;
		}

		@Override
		public int getIconHeight() {
			return 16;
		}
	}
}
