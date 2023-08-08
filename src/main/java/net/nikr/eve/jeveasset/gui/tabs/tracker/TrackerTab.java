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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.TrackerData;
import net.nikr.eve.jeveasset.data.settings.TrackerSettings;
import net.nikr.eve.jeveasset.data.settings.TrackerSettings.DisplayType;
import net.nikr.eve.jeveasset.data.settings.TrackerSettings.ShowOption;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.JStatusLabel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JFreeChartUtil;
import net.nikr.eve.jeveasset.gui.shared.JFreeChartUtil.SimpleRenderer;
import net.nikr.eve.jeveasset.gui.shared.ColorIcon;
import net.nikr.eve.jeveasset.gui.shared.ColorUtil;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.JOptionInput;
import net.nikr.eve.jeveasset.gui.shared.components.CheckBoxNode;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDateChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow.LockWorkerAdaptor;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionList;
import net.nikr.eve.jeveasset.gui.shared.components.JSelectionDialog;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.MenuItemValue;
import net.nikr.eve.jeveasset.gui.tabs.values.AssetValue;
import net.nikr.eve.jeveasset.gui.tabs.values.DataSetCreator;
import net.nikr.eve.jeveasset.gui.tabs.values.Value;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsTracker;
import net.nikr.eve.jeveasset.io.local.SettingsReader;
import net.nikr.eve.jeveasset.io.local.TrackerDataReader;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;


public class TrackerTab extends JMainTabSecondary {

	private enum TrackerAction {
		QUICK_DATE,
		UPDATE_DATA,
		UPDATE_SHOWN,
		IMPORT_FILE,
		INCLUDE_ZERO,
		LOGARITHMIC,
		ALL,
		EDIT,
		DELETE,
		NOTE_ADD,
		NOTE_DELETE,
		PROFILE,
		FILTER_ASSETS,
		FILTER_WALLET_BALANCE,
		FILTER_SKILL_POINTS
	}

	private final Shape NO_FILTER = new Rectangle(-3, -3, 6, 6);
	private final Shape FILTER_AND_DEFAULT = new Ellipse2D.Float(-3.0f, -3.0f, 6.0f, 6.0f);
	private final int PANEL_WIDTH_MINIMUM = 160;

	//GUI
	private final JDateChooser jFrom;
	private final JDateChooser jTo;
	private final JMultiSelectionList<String> jOwners;
	private final JComboBox<QuickDate> jQuickDate;
	private final JDropDownButton jShow;
	private final JCheckBoxMenuItem jAll;
	private final JCheckBoxMenuItem jTotal;
	private final JCheckBoxMenuItem jWalletBalance;
	private final JButton jWalletBalanceFilters;
	private final JCheckBoxMenuItem jAssets;
	private final JButton jAssetsFilters;
	private final JCheckBoxMenuItem jSellOrders;
	private final JCheckBoxMenuItem jEscrows;
	private final JCheckBoxMenuItem jEscrowsToCover;
	private final JCheckBoxMenuItem jManufacturing;
	private final JCheckBoxMenuItem jContractCollateral;
	private final JCheckBoxMenuItem jContractValue;
	private final JCheckBoxMenuItem jSkillPointsValue;
	private final JButton jSkillPointsFilters;
	private final JCheckBox jAllProfiles;
	private final JCheckBox jCharacterCorporations;
	private final JMenuItem jImportFile;
	private final JCheckBoxMenuItem jIncludeZero;
	private final JRadioButtonMenuItem jLogarithmic;
	private final JPopupMenu jPopupMenu;
	private final JMenuItem jAddNote;
	private final JMenu jEditNote;
	private final List<MenuItemValue> values;
	private final JStatusLabel jTotalStatus;
	private final JStatusLabel jWalletBalanceStatus;
	private final JStatusLabel jAssetsStatus;
	private final JStatusLabel jSellOrdersStatus;
	private final JStatusLabel jEscrowsStatus;
	private final JStatusLabel jEscrowsToCoverStatus;
	private final JStatusLabel jManufacturingStatus;
	private final JStatusLabel jContractCollateralStatus;
	private final JStatusLabel jContractValueStatus;
	private final JStatusLabel jSkillPointsStatus;

	//Graph
	private final JFreeChart jFreeChart;
	private final ChartPanel jChartPanel;
	private final MyRenderer renderer;
	private final TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
	private final DateAxis domainAxis;
	private final LogarithmicAxis rangeLogarithmicAxis;
	private final NumberAxis rangeLinearAxis;
	private TimePeriodValues walletBalance;
	private TimePeriodValues assets;
	private TimePeriodValues sellOrders;
	private TimePeriodValues escrows;
	private TimePeriodValues escrowsToCover;
	private TimePeriodValues manufacturing;
	private TimePeriodValues contractCollateral;
	private TimePeriodValues contractValue;
	private TimePeriodValues skillPointsValue;

	//Dialog
	private final JTrackerEditDialog jEditDialog;
	private final JSelectionDialog<String> jSelectionDialog;
	private final TrackerFilterDialog filterDialog;
	private final TrackerAssetFilterDialog assetFilterDialog;
	private final TrackerSkillPointsFilterDialog skillPointsFilterDialog;
	private final JCustomFileChooser jFileChooser;
	private final JLockWindow jLockWindow;

	//Listener
	private final ListenerClass listener = new ListenerClass();

	//Data
	private Map<SimpleTimePeriod, Value> cache;
	private final Map<String, CheckBoxNode> accountNodes = new TreeMap<>();
	private final Map<String, CheckBoxNode> assetNodes = new TreeMap<>();
	private Integer assetColumn = null;
	private Integer walletColumn = null;
	private boolean updateLock = false;

	public static final String NAME = "tracker"; //Not to be changed!

	public TrackerTab(Program program) {
		super(program, NAME, TabsTracker.get().title(), Images.TOOL_TRACKER.getIcon(), true);

		filterDialog = new TrackerFilterDialog(program);
		assetFilterDialog = new TrackerAssetFilterDialog(program);
		skillPointsFilterDialog = new TrackerSkillPointsFilterDialog(program);
		TrackerSettings trackerSettings = Settings.get().getTrackerSettings();

		List<String> extensions = new ArrayList<>();
		extensions.add("xml");
		extensions.add("zip");
		extensions.add("json");
		extensions.add("backup");
		jFileChooser = JCustomFileChooser.createFileChooser(program.getMainWindow().getFrame(), extensions);
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		jLockWindow = new JLockWindow(program.getMainWindow().getFrame());

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

		values = JMenuInfo.createDefault(jPopupMenu);

		jMenuItem = new JMenuItem(TabsTracker.get().edit(), Images.EDIT_EDIT.getIcon());
		jMenuItem.setActionCommand(TrackerAction.NOTE_ADD.name());
		jMenuItem.addActionListener(listener);
		jEditNote.add(jMenuItem);

		jMenuItem = new JMenuItem(TabsTracker.get().delete(), Images.EDIT_DELETE.getIcon());
		jMenuItem.setActionCommand(TrackerAction.NOTE_DELETE.name());
		jMenuItem.addActionListener(listener);
		jEditNote.add(jMenuItem);

		jEditDialog = new JTrackerEditDialog(program);

		jSelectionDialog = new JSelectionDialog<>(program);

		JSeparator jDateSeparator = new JSeparator();

		jQuickDate = new JComboBox<>(QuickDate.values());
		jQuickDate.setActionCommand(TrackerAction.QUICK_DATE.name());
		jQuickDate.addActionListener(listener);

		JLabel jFromLabel = new JLabel(TabsTracker.get().from());
		jFrom = new JDateChooser(true);
		if (trackerSettings.getFromDate() != null) {
			jFrom.setDate(dateToLocalDate(trackerSettings.getFromDate()));
		}
		jFrom.addDateChangeListener(listener);

		JLabel jToLabel = new JLabel(TabsTracker.get().to());
		jTo = new JDateChooser(true);
		if (trackerSettings.getToDate() != null) {
			jTo.setDate(dateToLocalDate(trackerSettings.getToDate()));
		}
		jTo.addDateChangeListener(listener);

		jShow = new JDropDownButton(TabsTracker.get().show(), Images.LOC_INCLUDE.getIcon());
		jShow.setHorizontalAlignment(JButton.LEFT);
		jShow.setIcon(trackerSettings.hasShowOption(ShowOption.ALL) ? Images.LOC_INCLUDE.getIcon() : Images.EDIT_EDIT_WHITE.getIcon());

		jAll = new JCheckBoxMenuItem(General.get().all());
		jAll.setSelected(trackerSettings.hasShowOption(ShowOption.ALL));
		jAll.setActionCommand(TrackerAction.ALL.name());
		jAll.addActionListener(listener);
		jAll.setFont(new Font(jAll.getFont().getName(), Font.ITALIC, jAll.getFont().getSize()));
		jShow.add(jAll, true);

		jTotal = new JCheckBoxMenuItem(TabsTracker.get().total());
		jTotal.setSelected(trackerSettings.hasShowOption(ShowOption.ALL) || trackerSettings.hasShowOption(ShowOption.TOTAL));
		jTotal.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jTotal.addActionListener(listener);
		jShow.add(jTotal, true);

		jWalletBalance = new JCheckBoxMenuItem(TabsTracker.get().walletBalance());
		jWalletBalance.setSelected(trackerSettings.hasShowOption(ShowOption.ALL) || trackerSettings.hasShowOption(ShowOption.WALLET));
		jWalletBalance.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jWalletBalance.addActionListener(listener);
		jShow.add(jWalletBalance, true);

		jWalletBalanceFilters = new JButton(TabsTracker.get().walletBalanceFilters());
		jWalletBalanceFilters.setIcon(Images.LOC_INCLUDE.getIcon());
		jWalletBalanceFilters.setHorizontalAlignment(JButton.LEFT);
		jWalletBalanceFilters.setActionCommand(TrackerAction.FILTER_WALLET_BALANCE.name());
		jWalletBalanceFilters.addActionListener(listener);

		jAssets = new JCheckBoxMenuItem(TabsTracker.get().assets());
		jAssets.setSelected(trackerSettings.hasShowOption(ShowOption.ALL) || trackerSettings.hasShowOption(ShowOption.ASSET));
		jAssets.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jAssets.addActionListener(listener);
		jShow.add(jAssets, true);

		jAssetsFilters = new JButton(TabsTracker.get().assetsFilters());
		jAssetsFilters.setIcon(Images.LOC_INCLUDE.getIcon());
		jAssetsFilters.setHorizontalAlignment(JButton.LEFT);
		jAssetsFilters.setActionCommand(TrackerAction.FILTER_ASSETS.name());
		jAssetsFilters.addActionListener(listener);

		jSellOrders = new JCheckBoxMenuItem(TabsTracker.get().sellOrders());
		jSellOrders.setSelected(trackerSettings.hasShowOption(ShowOption.ALL) || trackerSettings.hasShowOption(ShowOption.SELL_ORDER));
		jSellOrders.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jSellOrders.addActionListener(listener);
		jShow.add(jSellOrders, true);

		jEscrows = new JCheckBoxMenuItem(TabsTracker.get().escrows());
		jEscrows.setSelected(trackerSettings.hasShowOption(ShowOption.ALL) || trackerSettings.hasShowOption(ShowOption.ESCROW));
		jEscrows.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jEscrows.addActionListener(listener);
		jShow.add(jEscrows, true);

		jEscrowsToCover = new JCheckBoxMenuItem(TabsTracker.get().escrowsToCover());
		jEscrowsToCover.setSelected(trackerSettings.hasShowOption(ShowOption.ALL) || trackerSettings.hasShowOption(ShowOption.ESCROW_TO_COVER));
		jEscrowsToCover.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jEscrowsToCover.addActionListener(listener);
		jShow.add(jEscrowsToCover, true);

		jManufacturing = new JCheckBoxMenuItem(TabsTracker.get().manufacturing());
		jManufacturing.setSelected(trackerSettings.hasShowOption(ShowOption.ALL) || trackerSettings.hasShowOption(ShowOption.MANUFACTURING));
		jManufacturing.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jManufacturing.addActionListener(listener);
		jShow.add(jManufacturing, true);

		jContractCollateral = new JCheckBoxMenuItem(TabsTracker.get().contractCollateral());
		jContractCollateral.setSelected(trackerSettings.hasShowOption(ShowOption.ALL) || trackerSettings.hasShowOption(ShowOption.COLLATERAL));
		jContractCollateral.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jContractCollateral.addActionListener(listener);
		jShow.add(jContractCollateral, true);

		jContractValue = new JCheckBoxMenuItem(TabsTracker.get().contractValue());
		jContractValue.setSelected(trackerSettings.hasShowOption(ShowOption.ALL) || trackerSettings.hasShowOption(ShowOption.CONTRACT));
		jContractValue.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jContractValue.addActionListener(listener);
		jShow.add(jContractValue, true);

		jSkillPointsValue = new JCheckBoxMenuItem(TabsTracker.get().skillPointValue());
		jSkillPointsValue.setSelected(trackerSettings.hasShowOption(ShowOption.ALL) || trackerSettings.hasShowOption(ShowOption.SKILL_POINT));
		jSkillPointsValue.setActionCommand(TrackerAction.UPDATE_SHOWN.name());
		jSkillPointsValue.addActionListener(listener);
		jShow.add(jSkillPointsValue, true);

		jSkillPointsFilters = new JButton(TabsTracker.get().skillPointFilters());
		jSkillPointsFilters.setIcon(Images.LOC_INCLUDE.getIcon());
		jSkillPointsFilters.setHorizontalAlignment(JButton.LEFT);
		jSkillPointsFilters.setActionCommand(TrackerAction.FILTER_SKILL_POINTS.name());
		jSkillPointsFilters.addActionListener(listener);

		JSeparator jOwnersSeparator = new JSeparator();

		jAllProfiles = new JCheckBox(TabsTracker.get().allProfiles());
		jAllProfiles.setSelected(trackerSettings.isAllProfiles());
		jAllProfiles.setActionCommand(TrackerAction.PROFILE.name());
		jAllProfiles.addActionListener(listener);

		jCharacterCorporations = new JCheckBox(TabsTracker.get().characterCorporations());
		jCharacterCorporations.setSelected(trackerSettings.isCharacterCorporations());
		jCharacterCorporations.setActionCommand(TrackerAction.PROFILE.name());
		jCharacterCorporations.addActionListener(listener);

		jOwners = new JMultiSelectionList<>();
		jOwners.getSelectionModel().addListSelectionListener(listener);
		JScrollPane jOwnersScroll = new JScrollPane(jOwners);

		jTotalStatus = StatusPanel.createLabel(TabsTracker.get().statusTotal(), new ColorIcon(Color.RED.darker()), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jTotalStatus);

		jWalletBalanceStatus = StatusPanel.createLabel(TabsTracker.get().statusBalance(), new ColorIcon(Color.BLUE.darker()), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jWalletBalanceStatus);

		jAssetsStatus = StatusPanel.createLabel(TabsTracker.get().statusAssets(), new ColorIcon(Color.GREEN.darker().darker()), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jAssetsStatus);

		jSellOrdersStatus = StatusPanel.createLabel(TabsTracker.get().statusSellOrders(), new ColorIcon(Color.CYAN.darker()), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jSellOrdersStatus);

		jEscrowsStatus = StatusPanel.createLabel(TabsTracker.get().statusEscrows(), new ColorIcon(Color.BLACK), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jEscrowsStatus);

		jEscrowsToCoverStatus = StatusPanel.createLabel(TabsTracker.get().statusEscrowsToCover(), new ColorIcon(Color.GRAY), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jEscrowsToCoverStatus);

		jManufacturingStatus = StatusPanel.createLabel(TabsTracker.get().statusManufacturing(), new ColorIcon(Color.MAGENTA), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jManufacturingStatus);

		jContractCollateralStatus = StatusPanel.createLabel(TabsTracker.get().statusContractCollateral(), new ColorIcon(Color.PINK), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jContractCollateralStatus);

		jContractValueStatus = StatusPanel.createLabel(TabsTracker.get().statusContractValue(), new ColorIcon(Color.ORANGE), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jContractValueStatus);

		jSkillPointsStatus = StatusPanel.createLabel(TabsTracker.get().statusSkillPointValue(), new ColorIcon(Color.YELLOW), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jSkillPointsStatus);

		JLabel jHelp = new JLabel(TabsTracker.get().help());
		jHelp.setIcon(Images.MISC_HELP.getIcon());

		JLabel jNoFilter = new JLabel(TabsTracker.get().helpLegacyData());
		jNoFilter.setIcon(new ShapeIcon(NO_FILTER));

		JLabel jFilter = new JLabel(TabsTracker.get().helpNewData());
		jFilter.setIcon(new ShapeIcon(FILTER_AND_DEFAULT));

		JDropDownButton jSettings = new JDropDownButton(Images.DIALOG_SETTINGS.getIcon(), JDropDownButton.RIGHT);

		jImportFile = new JMenuItem(TabsTracker.get().importFile(), Images.EDIT_IMPORT.getIcon());
		jImportFile.setSelected(true);
		jImportFile.setActionCommand(TrackerAction.IMPORT_FILE.name());
		jImportFile.addActionListener(listener);
		jSettings.add(jImportFile);

		jSettings.addSeparator();

		jIncludeZero = new JCheckBoxMenuItem(TabsTracker.get().includeZero());
		jIncludeZero.setSelected(trackerSettings.isIncludeZero());
		jIncludeZero.setActionCommand(TrackerAction.INCLUDE_ZERO.name());
		jIncludeZero.addActionListener(listener);
		jSettings.add(jIncludeZero);

		jSettings.addSeparator();

		ButtonGroup buttonGroup = new ButtonGroup();

		JRadioButtonMenuItem jLinear = new JRadioButtonMenuItem(TabsTracker.get().scaleLinear());
		jLinear.setSelected(trackerSettings.getDisplayType() == DisplayType.LINEAR);
		jLinear.setActionCommand(TrackerAction.LOGARITHMIC.name());
		jLinear.addActionListener(listener);
		jSettings.add(jLinear);
		buttonGroup.add(jLinear);

		jLogarithmic = new JRadioButtonMenuItem(TabsTracker.get().scaleLogarithmic());
		jLogarithmic.setSelected(trackerSettings.getDisplayType() == DisplayType.LOGARITHMIC);
		jLogarithmic.setActionCommand(TrackerAction.LOGARITHMIC.name());
		jLogarithmic.addActionListener(listener);
		jSettings.add(jLogarithmic);
		buttonGroup.add(jLogarithmic);

		domainAxis = JFreeChartUtil.createDateAxis();
		rangeLogarithmicAxis = JFreeChartUtil.createLogarithmicAxis(trackerSettings.isIncludeZero());
		rangeLinearAxis = JFreeChartUtil.createNumberAxis(trackerSettings.isIncludeZero());

		renderer = new MyRenderer();
		renderer.setDefaultToolTipGenerator(new XYToolTipGenerator() {
			@Override
			public String generateToolTip(XYDataset dataset, int series, int item)	{
				Date date = new Date(dataset.getX(series, item).longValue());
				Number isk = dataset.getY(series, item);
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("<html>");
				stringBuilder.append("<b>");
				stringBuilder.append(dataset.getSeriesKey(series));
				stringBuilder.append(":</b> ");
				stringBuilder.append(Formatter.iskFormat(isk));
				stringBuilder.append("<br>");
				stringBuilder.append("<b>");
				stringBuilder.append(TabsTracker.get().date());
				stringBuilder.append(":</b> ");
				stringBuilder.append(Formatter.columnDateOnly(date));
				TrackerNote trackerNote = trackerSettings.getNotes().get(new TrackerDate(date));
				if (trackerNote != null) {
					stringBuilder.append("<br><b>");
					stringBuilder.append(TabsTracker.get().note());
					stringBuilder.append(":</b> ");
					stringBuilder.append(trackerNote.getNote());
				}
				return stringBuilder.toString();
			}
		});

		XYPlot plot;
		if (trackerSettings.getDisplayType() == DisplayType.LINEAR) {
			plot = JFreeChartUtil.createPlot(dataset, domainAxis, rangeLinearAxis, renderer);
		} else {
			plot = JFreeChartUtil.createPlot(dataset, domainAxis, rangeLogarithmicAxis, renderer);
		}
		plot.setDrawingSupplier(new MyDrawingSupplier());
		jFreeChart = JFreeChartUtil.createChart(plot);
		jChartPanel = JFreeChartUtil.createChartPanel(jFreeChart);
		jChartPanel.addChartMouseListener(listener);

		int gapWidth = 5;
		int labelWidth = Math.max(jFromLabel.getPreferredSize().width, jToLabel.getPreferredSize().width);
		int panelWidth = Math.max(PANEL_WIDTH_MINIMUM, jCharacterCorporations.getPreferredSize().width);
		panelWidth = Math.max(panelWidth, jFrom.getPreferredSize().width + labelWidth + gapWidth);
		panelWidth = Math.max(panelWidth, jTo.getPreferredSize().width + labelWidth + gapWidth);
		int dateWidth = panelWidth - labelWidth - gapWidth;

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
						.addComponent(jSettings, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth())
						.addGap(6)
					)
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
					.addComponent(jDateSeparator, panelWidth, panelWidth, panelWidth)
					.addComponent(jShow, panelWidth, panelWidth, panelWidth)
					.addComponent(jAssetsFilters, panelWidth, panelWidth, panelWidth)
					.addComponent(jWalletBalanceFilters, panelWidth, panelWidth, panelWidth)
					.addComponent(jSkillPointsFilters, panelWidth, panelWidth, panelWidth)
					.addComponent(jOwnersSeparator, panelWidth, panelWidth, panelWidth)
					.addComponent(jAllProfiles, panelWidth, panelWidth, panelWidth)
					.addComponent(jCharacterCorporations, panelWidth, panelWidth, panelWidth)
					.addComponent(jOwnersScroll, panelWidth, panelWidth, panelWidth)
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
					.addComponent(jShow, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAssetsFilters, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jWalletBalanceFilters, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSkillPointsFilters, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOwnersSeparator, 3, 3, 3)
					.addComponent(jAllProfiles, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCharacterCorporations, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jOwnersScroll, 70, 70, Integer.MAX_VALUE)
				)
		);
		DataSetCreator.purgeInvalidTrackerAssetValues();
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
	public void repaintTable() {
		updateShown();
	}

	@Override
	public void clearData() { }

	@Override
	public void updateCache() { }

	@Override
	public Collection<net.nikr.eve.jeveasset.data.settings.types.LocationType> getLocations() {
		return new ArrayList<>(); //No Location
	}

	public void checkAll() {
		if (!Settings.get().isAskedCheckAllTracker()) {
			Settings.lock("Tracker: Check All");
			Settings.get().setAskedCheckAllTracker(true);
			Settings.unlock("Tracker: Check All");
			program.saveSettings("Tracker: Check All");
			boolean isAll = true;
			for (CheckBoxNode node : assetNodes.values()) {
				if (!node.isSelected()) {
					isAll = false;
					break;
				}
			}
			boolean empty = false;
			try {
				TrackerData.readLock();
				empty = TrackerData.get().isEmpty();
			} finally {
				TrackerData.readUnlock();
			}
			if (!isAll && !empty) {
				int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsTracker.get().checkAllLocationsMsg(), TabsTracker.get().checkAllLocationsTitle(), JOptionPane.OK_CANCEL_OPTION);
				if (value == JOptionPane.OK_OPTION) {
					for (CheckBoxNode node : assetNodes.values()) {
						if (node.isParent()) {
							continue;
						}
						node.setSelected(true);
					}
					updateSettings();
					createData();
					updateButtonIcons();
				} else {
					showLocationFilter();
				}
			}
		}
	}

	public void showSkillPointsFilter() {
		boolean save = skillPointsFilterDialog.show();
		if (save) {
			//Tracker
			if (program.getMainWindow().isOpen(this)) {
				createData();
				updateButtonIcons();
			}
			//Isk
			if (program.getMainWindow().isOpen(program.getValueTableTab())) {
				program.getValueTableTab().updateData();
			}
		}
	}

	private void updateFilterButtons() {
		if (updateLock) {
			return;
		}
		List<String> owners = jOwners.getSelectedValuesList();
		boolean balanceFilter = false;
		boolean assetsFilter = false;
		try {
			TrackerData.readLock();
			for (String owner : owners) {
				for (Value data : TrackerData.get().get(owner)) {
					//Get all account wallet account keys
					if (!data.getBalanceFilter().isEmpty()) {
						balanceFilter = true;
					}
					//Get all asset IDs
					if (!data.getAssetsFilter().isEmpty()) {
						assetsFilter = true;
					}
					if (balanceFilter && assetsFilter) {
						break;
					}
				}
			}
		} finally {
			TrackerData.readUnlock();
		}
		jWalletBalanceFilters.setEnabled(balanceFilter);
		jAssetsFilters.setEnabled(assetsFilter);
	}

	private void updateOwners() {
		updateLock = true;
		Set<String> trackerOwners;
		try {
			TrackerData.readLock();
			trackerOwners = new TreeSet<>(TrackerData.get().keySet());
		} finally {
			TrackerData.readUnlock();
		}
		Set<String> uniqueOwners;
		if (jAllProfiles.isSelected()) {
			uniqueOwners = new HashSet<>(trackerOwners);
		} else { //Profile owners
			uniqueOwners = new HashSet<>();
			boolean characterCorporations = jCharacterCorporations.isSelected();
			for (OwnerType owner : program.getOwnerTypes()) {
				if (trackerOwners.contains(owner.getOwnerName())) {
					uniqueOwners.add(owner.getOwnerName());
				}
				if (characterCorporations && owner.getCorporationName() != null && trackerOwners.contains(owner.getCorporationName())) {
					uniqueOwners.add(owner.getCorporationName());
				}
			}
		}
		final List<String> ownersList = new ArrayList<>(uniqueOwners);
		if (ownersList.isEmpty()) {
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
			List<String> owners = Settings.get().getTrackerSettings().getSelectedOwners();
			if (owners == null) {
				jOwners.selectAll();
			} else {
				ListModel<String> model = jOwners.getModel();
				for (int i = 0; i < model.getSize(); i++) {
					String ownerName = model.getElementAt(i);
					if (owners.contains(ownerName)) {
						jOwners.addSelectionInterval(i, i);
					}
				}
			}
		}
		updateLock = false;
	}

	private void updateNodes() {
		accountNodes.clear();
		assetNodes.clear();
	//Find all saved Keys/IDs
		Set<String> walletIDs = new TreeSet<>();
		Set<AssetValue> assetsIDs = new TreeSet<>();
		try {
			TrackerData.readLock();
			for (List<Value> list : TrackerData.get().values()) {
				for (Value data : list) {
					//Get all account wallet account keys
					walletIDs.addAll(data.getBalanceFilter().keySet());
					//Get all asset IDs
					assetsIDs.addAll(data.getAssetsFilter().keySet());
				}
			}
		} finally {
			TrackerData.readUnlock();
		}

		//WALLET - Make nodes for found wallet account keys
		CheckBoxNode corporationWalletNode = new CheckBoxNode(null, TabsTracker.get().corporationWallet(), TabsTracker.get().corporationWallet(), false);
		for (String id : walletIDs) {
			accountNodes.put(id, new CheckBoxNode(corporationWalletNode, id, TabsTracker.get().division(id), selectNode(id)));
		}
		String characterWalletID = "0";
		CheckBoxNode characterWalletNode = new CheckBoxNode(null, characterWalletID, TabsTracker.get().characterWallet(), selectNode(characterWalletID));
		accountNodes.put(characterWalletID, characterWalletNode);

		//ASSETS - Make nodes for found asset IDs
		CheckBoxNode assetNode = new CheckBoxNode(null, TabsTracker.get().assets(), TabsTracker.get().assets(), false);
		assetNodes.put(assetNode.getNodeID(), assetNode);
		CheckBoxNode knownLocationsNode = new CheckBoxNode(assetNode, TabsTracker.get().knownLocations(), TabsTracker.get().knownLocations(), false);
		assetNodes.put(knownLocationsNode.getNodeID(), knownLocationsNode);
		CheckBoxNode unknownLocationsNode = new CheckBoxNode(assetNode, TabsTracker.get().unknownLocations(), TabsTracker.get().unknownLocations(), false);
		assetNodes.put(unknownLocationsNode.getNodeID(), unknownLocationsNode);

		Map<String, CheckBoxNode> nodeCache = new HashMap<>();
		for (AssetValue assetValue : assetsIDs) {
			String location = assetValue.getLocation();
			String flag = assetValue.getFlag();
			String id = assetValue.getID();
			CheckBoxNode locationNode = nodeCache.get(location);
			if (locationNode == null) {
				if (location.startsWith("[Unknown Location #")) {
					locationNode = new CheckBoxNode(unknownLocationsNode, location, location, selectNode(location));
				} else {
					locationNode = new CheckBoxNode(knownLocationsNode, location, location, selectNode(location));
				}
				nodeCache.put(location, locationNode);
				assetNodes.put(locationNode.getNodeID(), locationNode);
			}

			CheckBoxNode flagNode = nodeCache.get(id);
			if (flagNode == null) {
				flagNode = new CheckBoxNode(locationNode, id, ApiIdConverter.getFlagName(flag), selectNode(id));
				nodeCache.put(id, flagNode);
			}
			assetNodes.put(id, flagNode);
		}
		//For locations with office, you should have the option to exclude all values in corp hangars
		for (CheckBoxNode locationNode : nodeCache.values()) {
			if (locationNode.isParent()) {
				String id = locationNode.getNodeID() + " > unique ID";
				CheckBoxNode otherNode = new CheckBoxNode(locationNode, id, TabsTracker.get().other(), selectNode(id));
				assetNodes.put(otherNode.getNodeID(), otherNode);
			}
		}
	}

	private boolean selectNode(String id) {
		Boolean selected = Settings.get().getTrackerSettings().getFilters().get(id);
		if (selected != null) {
			return selected;
		} else {
			return Settings.get().getTrackerSettings().isSelectNew();
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
		skillPointsValue = new TimePeriodValues(TabsTracker.get().skillPointValue());
		Date from = getFromDate();
		Date to = getToDate();
		cache = new TreeMap<>();
		Map<String, CheckBoxNode> accountNodesMap = new HashMap<>(accountNodes);
		Map<String, CheckBoxNode> assetNodesMap = new HashMap<>(assetNodes);
		Map<Date, Boolean> assetColumns = new TreeMap<>();
		Map<Date, Boolean> walletColumns = new TreeMap<>();
		if (owners != null) { //No data set...
			try {
				TrackerData.readLock();
				Map<Date, Map<String, Value>> trackerDataByDate = getTrackerDataByDate(owners);
				Map<String, Value> lastMap = new HashMap<>();
				for (Map.Entry<Date, Map<String, Value>> dateEntry : trackerDataByDate.entrySet()) {
					final Date date = dateEntry.getKey();
					final Value value;
					if ((from == null || date.after(from)) && (to == null || date.before(to))) {
						value = new Value(date);
						cache.put(new SimpleTimePeriod(date, date), value);
					} else {
						continue;
					}
					for (Map.Entry<String, Value> ownerEntry : dateEntry.getValue().entrySet()) {
						Value data = ownerEntry.getValue();
						if (data == null) {
							Value last = lastMap.get(ownerEntry.getKey());
							if (last != null) {
								data = last;
							}
						}
						if (data == null) {
							continue;
						} else {
							lastMap.put(ownerEntry.getKey(), data);
						}
						if (data.getAssetsFilter().isEmpty()) {
							value.addAssets(data.getAssetsTotal());
							//Default
							Boolean assetBoolean = assetColumns.get(date);
							if (assetBoolean == null) {
								assetColumns.put(date, false);
							}
						} else {
							assetColumns.put(date, true);
							for (Map.Entry<AssetValue, Double> entry : data.getAssetsFilter().entrySet()) {
								if (assetNodesMap.get(entry.getKey().getID()).isSelected()) {
									value.addAssets(entry.getValue());
								}
							}
						}
						value.addEscrows(data.getEscrows());
						value.addEscrowsToCover(data.getEscrowsToCover());
						value.addManufacturing(data.getManufacturing());
						value.addContractCollateral(data.getContractCollateral());
						value.addContractValue(data.getContractValue());
						TrackerSkillPointFilter skillPointFilter = Settings.get().getTrackerSettings().getSkillPointFilters().get(ownerEntry.getKey());
						if (skillPointFilter != null) {
							if (skillPointFilter.isEnabled()) {
								value.addSkillPointValue(data.getSkillPoints(), skillPointFilter.getMinimum());
							}
						} else {
							value.addSkillPointValue(data.getSkillPoints(), 0);
						}
						value.addSellOrders(data.getSellOrders());
						if (data.getBalanceFilter().isEmpty()) {
							value.addBalance(data.getBalanceTotal());
							//Default
							Boolean walletBoolean = walletColumns.get(date);
							if (walletBoolean == null) {
								walletColumns.put(date, false);
							}
						} else {
							walletColumns.put(date, true);
							for (Map.Entry<String, Double> entry : data.getBalanceFilter().entrySet()) {
								if (accountNodesMap.get(entry.getKey()).isSelected()) {
									value.addBalance(entry.getValue());
								}
							}
						}
					}
				}
			} finally {
				TrackerData.readUnlock();
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
				skillPointsValue.add(entry.getKey(), entry.getValue().getSkillPointValue());
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

	private Map<Date, Map<String, Value>> getTrackerDataByDate(final List<String> owners) {
		Map<Date, Map<String, Value>> trackerDataByDate = new TreeMap<>();
		Map<String, Value> empty = new HashMap<>();
		for (String owner : owners) {
			empty.put(owner, null);
		}
		for (String owner : owners) {
			for (Value data : TrackerData.get().get(owner)) {
				Date key = data.getDate();
				Map<String, Value> map = trackerDataByDate.get(key);
				if (map == null) {
					map = new HashMap<>(empty);
					trackerDataByDate.put(key, map);
				}
				map.put(owner, data);
			}
		}
		return trackerDataByDate;
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
		isAll = true;
		for (TrackerSkillPointFilter filter : Settings.get().getTrackerSettings().getSkillPointFilters().values()) {
			if (!filter.isEmpty()) {
				isAll = false;
				break;
			}
		}
		if (isAll) {
			jSkillPointsFilters.setIcon(Images.LOC_INCLUDE.getIcon());
		} else {
			jSkillPointsFilters.setIcon(Images.EDIT_EDIT_WHITE.getIcon());
		}
	}

	private void updateShown() {
		//Remove All
		while (dataset.getSeriesCount() != 0) {
			dataset.removeSeries(0);
		}
		renderer.clear();
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
			if (jSkillPointsValue.isSelected() && skillPointsValue != null) {
				t += entry.getValue().getSkillPointValue();
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
			jTotalStatus.setNumber(lastTotal - firstTotal);
		} else {
			jTotalStatus.setNumber(0.0);
		}
		jWalletBalanceStatus.setVisible(jWalletBalance.isSelected());
		if (first != null && last != null) {
			jWalletBalanceStatus.setNumber(last.getBalanceTotal() - first.getBalanceTotal());
		} else {
			jWalletBalanceStatus.setNumber(0.0);
		}
		jAssetsStatus.setVisible(jAssets.isSelected());
		if (first != null && last != null) {
			jAssetsStatus.setNumber(last.getAssetsTotal() - first.getAssetsTotal());
		} else {
			jAssetsStatus.setNumber(0.0);
		}
		jSellOrdersStatus.setVisible(jSellOrders.isSelected());
		if (first != null && last != null) {
			jSellOrdersStatus.setNumber(last.getSellOrders() - first.getSellOrders());
		} else {
			jSellOrdersStatus.setNumber(0.0);
		}
		jEscrowsStatus.setVisible(jEscrows.isSelected());
		if (first != null && last != null) {
			jEscrowsStatus.setNumber(last.getEscrows() - first.getEscrows());
		} else {
			jEscrowsStatus.setNumber(0.0);
		}
		jEscrowsToCoverStatus.setVisible(jEscrowsToCover.isSelected());
		if (first != null && last != null) {
			jEscrowsToCoverStatus.setNumber(last.getEscrowsToCover() - first.getEscrowsToCover());
		} else {
			jEscrowsToCoverStatus.setNumber(0.0);
		}
		jManufacturingStatus.setVisible(jManufacturing.isSelected());
		if (first != null && last != null) {
			jManufacturingStatus.setNumber(last.getManufacturing() - first.getManufacturing());
		} else {
			jManufacturingStatus.setNumber(0.0);
		}
		jContractCollateralStatus.setVisible(jContractCollateral.isSelected());
		if (first != null && last != null) {
			jContractCollateralStatus.setNumber(last.getContractCollateral() - first.getContractCollateral());
		} else {
			jContractCollateralStatus.setNumber(0.0);
		}
		jContractValueStatus.setVisible(jContractValue.isSelected());
		if (first != null && last != null) {
			jContractValueStatus.setNumber(last.getContractValue() - first.getContractValue());
		} else {
			jContractValueStatus.setNumber(0.0);
		}
		jSkillPointsStatus.setVisible(jSkillPointsValue.isSelected());
		if (first != null && last != null) {
			jSkillPointsStatus.setNumber(last.getSkillPointValue()- first.getSkillPointValue());
		} else {
			jSkillPointsStatus.setNumber(0.0);
		}
		//Update Shown
		boolean bright = ColorUtil.isBrightColor(jPanel.getBackground());
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
			renderer.add(dataset.getSeriesCount() - 1, minColumn);
			updateRender(bright, dataset.getSeriesCount() - 1, Color.RED.darker());
		}
		if (jWalletBalance.isSelected() && walletBalance != null) {
			dataset.addSeries(walletBalance);
			renderer.add(dataset.getSeriesCount() - 1, walletColumn);
			updateRender(bright, dataset.getSeriesCount() - 1, Color.BLUE.darker());

		}
		if (jAssets.isSelected() && assets != null) {
			dataset.addSeries(assets);
			renderer.add(dataset.getSeriesCount() - 1, assetColumn);
			updateRender(bright, dataset.getSeriesCount() - 1, Color.GREEN.darker().darker());
		}
		if (jSellOrders.isSelected() && sellOrders != null) {
			dataset.addSeries(sellOrders);
			updateRender(bright, dataset.getSeriesCount() - 1, Color.CYAN.darker());
		}
		if (jEscrows.isSelected() && escrows != null) {
			dataset.addSeries(escrows);
			updateRender(bright, dataset.getSeriesCount() - 1, Color.BLACK);
		}
		if (jEscrowsToCover.isSelected() && escrowsToCover != null) {
			dataset.addSeries(escrowsToCover);
			updateRender(bright, dataset.getSeriesCount() - 1, Color.GRAY);
		}
		if (jManufacturing.isSelected() && manufacturing != null) {
			dataset.addSeries(manufacturing);
			updateRender(bright, dataset.getSeriesCount() - 1, Color.MAGENTA);
		}
		if (jContractCollateral.isSelected() && contractCollateral != null) {
			dataset.addSeries(contractCollateral);
			updateRender(bright, dataset.getSeriesCount() - 1, Color.PINK);
		}
		if (jContractValue.isSelected() && contractValue != null) {
			dataset.addSeries(contractValue);
			updateRender(bright, dataset.getSeriesCount() - 1, Color.ORANGE);
		}
		if (jSkillPointsValue.isSelected() && skillPointsValue != null) {
			dataset.addSeries(skillPointsValue);
			updateRender(bright, dataset.getSeriesCount() - 1, Color.YELLOW);
		}
		//Add empty dataset
		if (dataset.getSeriesCount() == 0) {
			TimePeriodValues timePeriodValues = new TimePeriodValues(TabsTracker.get().empty());
			dataset.addSeries(timePeriodValues);
			updateRender(bright, dataset.getSeriesCount() - 1, Color.BLACK);
		}
		rangeLogarithmicAxis.setAutoRange(true);
		rangeLinearAxis.setAutoRange(true);
		jFreeChart.getXYPlot().getDomainAxis().setAutoRange(true);

		JFreeChartUtil.updateTickScale(domainAxis, rangeLinearAxis, dataset);
	}

	private void updateRender(boolean bright, int index, Color color) {
		if (Settings.get().isEasyChartColors()) {
			if (bright) {
				if (ColorUtil.luminance(color) > 0.8) {
					color = color.darker();
				}
			} else {
				if (ColorUtil.luminance(color) < 0.2) {
					color = color.brighter();
				}
			}
		}
		renderer.setSeriesPaint(index, color);
		renderer.setSeriesStroke(index, new BasicStroke(1));
	}

	private void updateSettings() {
		Settings.lock("Tracker Filters: Update");
		Settings.get().getTrackerSettings().getFilters().clear();
		for (CheckBoxNode checkBoxNode : assetNodes.values()) {
			if (checkBoxNode.isParent()) {
				continue;
			}
			Settings.get().getTrackerSettings().getFilters().put(checkBoxNode.getNodeID(), checkBoxNode.isSelected());
		}
		for (CheckBoxNode checkBoxNode : accountNodes.values()) {
			Settings.get().getTrackerSettings().getFilters().put(checkBoxNode.getNodeID(), checkBoxNode.isSelected());
		}
		Settings.get().getTrackerSettings().setSelectNew(filterDialog.isSelectNew());
		Settings.unlock("Tracker Filters: Update");
		program.saveSettings("Tracker Filters: Update");
	}

	private String getSelectedOwner(boolean all) {
		List<String> owners = jOwners.getSelectedValuesList();
		if (owners.size() == 1) {
			return jOwners.getSelectedValue();
		} else {
			List<String> list = new ArrayList<>();
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
		String date = Formatter.simpleDate(new Date((long)jFreeChart.getXYPlot().getDomainCrosshairValue()));
		try {
			TrackerData.readLock();
			for (Value value : TrackerData.get().get(owner)) {
				if (date.equals(Formatter.simpleDate(value.getDate()))) {
					return value;
				}
			}
		} finally {
			TrackerData.readUnlock();
		}
		return null;
	}

	private void addNote() {
		Date date = new Date((long)jFreeChart.getXYPlot().getDomainCrosshairValue());
		TrackerNote trackerNote = Settings.get().getTrackerSettings().getNotes().get(new TrackerDate(date));
		String newNote;
		if (trackerNote == null) {
			newNote = JOptionInput.showInputDialog(program.getMainWindow().getFrame(), TabsTracker.get().notesEditMsg(), TabsTracker.get().notesEditTitle(), JOptionPane.PLAIN_MESSAGE);
		} else {
			newNote = (String) JOptionInput.showInputDialog(program.getMainWindow().getFrame(), TabsTracker.get().notesEditMsg(), TabsTracker.get().notesEditTitle(), JOptionPane.PLAIN_MESSAGE, null, null, trackerNote.getNote());
		}
		if (newNote != null) {
			Settings.lock("Tracker Notes (Set Note)");
			Settings.get().getTrackerSettings().getNotes().put(new TrackerDate(date), new TrackerNote(newNote));
			Settings.unlock("Tracker Notes (Set Note)");
			program.saveSettings("Tracker Data (Set Note)");
		}
	}

	private void removeNote() {
		Date date = new Date((long)jFreeChart.getXYPlot().getDomainCrosshairValue());
		TrackerNote trackerNote = Settings.get().getTrackerSettings().getNotes().get(new TrackerDate(date));
		int returnValue = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsTracker.get().notesDeleteMsg(trackerNote.getNote()), TabsTracker.get().notesDeleteTitle(), JOptionPane.OK_CANCEL_OPTION);
		if (returnValue == JOptionPane.OK_OPTION) {
			Settings.lock("Tracker Notes (Delete Note)");
			Settings.get().getTrackerSettings().getNotes().remove(new TrackerDate(date));
			Settings.unlock("Tracker Notes (Delete Note)");
			program.saveSettings("Tracker Data (Delete Note)");
		}
	}

	private class MyDrawingSupplier extends DefaultDrawingSupplier {
		@Override
		public Shape getNextShape() {
			return FILTER_AND_DEFAULT;
		}
	}

	private class MyRenderer extends SimpleRenderer {

		private final Map<Integer, Integer> renders = new HashMap<>();

		public MyRenderer() {
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
		private final Color color;

		public ShapeIcon(Shape shape, Color color) {
			this.shape = shape;
			this.color = color;
		}

		public ShapeIcon(Shape shape) {
			this(shape, Color.BLACK);
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2d = (Graphics2D)g.create();

			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			g2d.setRenderingHints(rh);

			g2d.setColor(color);
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

	private LocalDate dateToLocalDate(Date date) {
		Instant instant = date.toInstant();
		return LocalDateTime.ofInstant(instant, ZoneId.of("GMT")).toLocalDate();
	}

	private void showLocationFilter() {
		boolean save = assetFilterDialog.showLocations(assetNodes);
		if (save) { //Need refilter
			updateSettings();
			createData();
			updateButtonIcons();
		}
	}

	private class ListenerClass implements
			ActionListener, PopupMenuListener,
			ChartMouseListener, ListSelectionListener, DateChangeListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			TrackerSettings trackerSettings = Settings.get().getTrackerSettings();
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
				trackerSettings.setFromDate(getFromDate());
				trackerSettings.setToDate(getToDate());
				updateSettings();
			} else if (TrackerAction.INCLUDE_ZERO.name().equals(e.getActionCommand())) {
				rangeLogarithmicAxis.setAutoRangeIncludesZero(jIncludeZero.isSelected());
				rangeLinearAxis.setAutoRangeIncludesZero(jIncludeZero.isSelected());
				trackerSettings.setIncludeZero(jIncludeZero.isSelected());
				updateSettings();
			} else if (TrackerAction.LOGARITHMIC.name().equals(e.getActionCommand())) {
				if (jLogarithmic.isSelected()) {
					jFreeChart.getXYPlot().setRangeAxis(rangeLogarithmicAxis);
					trackerSettings.setDisplayType(DisplayType.LOGARITHMIC);
				} else {
					jFreeChart.getXYPlot().setRangeAxis(rangeLinearAxis);
					trackerSettings.setDisplayType(DisplayType.LINEAR);
				}
				updateSettings();
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
						&& jContractValue.isSelected()
						&& jSkillPointsValue.isSelected());
				if (jAll.isSelected()) {
					jShow.setIcon(Images.LOC_INCLUDE.getIcon());
					trackerSettings.getShowOptions().clear();
					trackerSettings.getShowOptions().add(ShowOption.ALL);
				} else {
					jShow.setIcon(Images.EDIT_EDIT_WHITE.getIcon());
					trackerSettings.getShowOptions().clear();
					if (jTotal.isSelected()) {
						trackerSettings.getShowOptions().add(ShowOption.TOTAL);
					}
					if (jWalletBalance.isSelected()) {
						trackerSettings.getShowOptions().add(ShowOption.WALLET);
					}
					if (jAssets.isSelected()) {
						trackerSettings.getShowOptions().add(ShowOption.ASSET);
					}
					if (jSellOrders.isSelected()) {
						trackerSettings.getShowOptions().add(ShowOption.SELL_ORDER);
					}
					if (jEscrows.isSelected()) {
						trackerSettings.getShowOptions().add(ShowOption.ESCROW);
					}
					if (jEscrowsToCover.isSelected()) {
						trackerSettings.getShowOptions().add(ShowOption.ESCROW_TO_COVER);
					}
					if (jManufacturing.isSelected()) {
						trackerSettings.getShowOptions().add(ShowOption.MANUFACTURING);
					}
					if (jContractCollateral.isSelected()) {
						trackerSettings.getShowOptions().add(ShowOption.COLLATERAL);
					}
					if (jContractValue.isSelected()) {
						trackerSettings.getShowOptions().add(ShowOption.CONTRACT);
					}
					if (jSkillPointsValue.isSelected()) {
						trackerSettings.getShowOptions().add(ShowOption.SKILL_POINT);
					}
				}
				updateSettings();
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
				jSkillPointsValue.setSelected(jAll.isSelected());
				trackerSettings.getShowOptions().clear();
				if (jAll.isSelected()) {
					jShow.setIcon(Images.LOC_INCLUDE.getIcon());
					trackerSettings.getShowOptions().add(ShowOption.ALL);
				} else {
					jShow.setIcon(Images.EDIT_EDIT_WHITE.getIcon());
				}
				updateShown();
				updateSettings();
			} else if (TrackerAction.EDIT.name().equals(e.getActionCommand())) {
				jFreeChart.getXYPlot().setDomainCrosshairVisible(true);
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
				jFreeChart.getXYPlot().setDomainCrosshairVisible(false);
			} else if (TrackerAction.DELETE.name().equals(e.getActionCommand())) {
				jFreeChart.getXYPlot().setDomainCrosshairVisible(true);
				String owner = getSelectedOwner(true);
				if (owner == null) {
					return;
				}
				List<String> owners = new ArrayList<>();
				if (owner.equals(General.get().all())) {
					owners.addAll(jOwners.getSelectedValuesList());
				} else {
					owners.add(owner);
				}
				Map<String, Value> values = new HashMap<>();
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
					for (Map.Entry<String, Value> entry : values.entrySet()) {
						//Remove value
						TrackerData.remove(entry.getKey(), entry.getValue());
					}
					TrackerData.save("Deleted");
					updateData();
				}
				jFreeChart.getXYPlot().setDomainCrosshairVisible(false);
			} else if (TrackerAction.NOTE_DELETE.name().equals(e.getActionCommand())) {
				jFreeChart.getXYPlot().setDomainCrosshairVisible(true);
				removeNote();
				jFreeChart.getXYPlot().setDomainCrosshairVisible(false);
			} else if (TrackerAction.NOTE_ADD.name().equals(e.getActionCommand())) {
				jFreeChart.getXYPlot().setDomainCrosshairVisible(true);
				addNote();
				jFreeChart.getXYPlot().setDomainCrosshairVisible(false);
			} else if (TrackerAction.PROFILE.name().equals(e.getActionCommand())) {
				if (!updateLock) {
					boolean allProfiles = jAllProfiles.isSelected();
					boolean characterCorporations = jCharacterCorporations.isSelected();
					trackerSettings.setAllProfiles(allProfiles);
					trackerSettings.setCharacterCorporations(characterCorporations);
					program.saveSettings("Tracker (Owner Settings)");
				}
				updateData();
			} else if (TrackerAction.FILTER_WALLET_BALANCE.name().equals(e.getActionCommand())) {
				boolean save = filterDialog.showWallet(accountNodes);
				if (save) { //Need refilter
					updateSettings();
					createData();
					updateButtonIcons();
				}
			} else if (TrackerAction.FILTER_ASSETS.name().equals(e.getActionCommand())) {
				showLocationFilter();
			} else if (TrackerAction.FILTER_SKILL_POINTS.name().equals(e.getActionCommand())) {
				showSkillPointsFilter();
			} else if (TrackerAction.IMPORT_FILE.name().equals(e.getActionCommand())) {
				jFileChooser.setCurrentDirectory(new File(FileUtil.getPathDataDirectory()));
				int value = jFileChooser.showOpenDialog(program.getMainWindow().getFrame());
				if (value != JFileChooser.APPROVE_OPTION) {
					return; //Cancel
				}
				jLockWindow.show(TabsTracker.get().importFileImport(), new ImportFileLockWorker(jFileChooser.getSelectedFile()));
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
			Settings.get().getTrackerSettings().setFromDate(getFromDate());
			Settings.get().getTrackerSettings().setToDate(getToDate());
			createData();
		}

		@Override
		public void chartMouseClicked(final ChartMouseEvent cme) {
			if (cme.getTrigger().getClickCount() % 2 == 0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (cache.isEmpty()) {
							return;
						}
						jFreeChart.getXYPlot().setDomainCrosshairVisible(true);
						double xValue = jFreeChart.getXYPlot().getDomainCrosshairValue();
						double yValue = jFreeChart.getXYPlot().getRangeCrosshairValue();
						RectangleEdge xEdge = jFreeChart.getXYPlot().getDomainAxisEdge();
						RectangleEdge yEdge = jFreeChart.getXYPlot().getRangeAxisEdge();
						Rectangle2D dataArea = jChartPanel.getScreenDataArea(); // jChartPanel.getChartRenderingInfo().getPlotInfo().getSubplotInfo(0).getDataArea();
						int x = (int) jFreeChart.getXYPlot().getDomainAxis().valueToJava2D(xValue, dataArea, xEdge);
						int y = (int) jFreeChart.getXYPlot().getRangeAxis().valueToJava2D(yValue, dataArea, yEdge);
						Date date = new Date((long)xValue);
						values.clear();
						JMenuItem jIskValue = JMenuInfo.createMenuItem(values, jPopupMenu, yValue, JMenuInfo.AutoNumberFormat.ISK, TabsTracker.get().selectionIsk(), TabsTracker.get().selectionShortIsk(), Images.TOOL_VALUES.getIcon());
						JMenuItem jDateValue = JMenuInfo.createMenuItem(values, jPopupMenu, Formatter.columnDateOnly(date), TabsTracker.get().selectionDate(), TabsTracker.get().selectionShortDate(), Images.EDIT_DATE.getIcon());
						TrackerNote trackerNote = Settings.get().getTrackerSettings().getNotes().get(new TrackerDate(date));
						JMenuItem jNote;
						if (trackerNote != null) {
							jAddNote.setVisible(false);
							jEditNote.setVisible(true);
							jNote = JMenuInfo.createMenuItem(values, jPopupMenu, trackerNote.getNote(), TabsTracker.get().selectionNote(), TabsTracker.get().selectionShortNote(), Images.SETTINGS_USER_NAME.getIcon());
						} else {
							jAddNote.setVisible(true);
							jEditNote.setVisible(false);
							jNote = null;
						}
						jPopupMenu.addPopupMenuListener(new PopupMenuListener() {
							@Override
							public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }

							@Override
							public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
								jPopupMenu.remove(jIskValue);
								jPopupMenu.remove(jDateValue);
								if (jNote != null) {
									jPopupMenu.remove(jNote);
								}
							}

							@Override
							public void popupMenuCanceled(PopupMenuEvent e) { }
						});
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
			jFreeChart.getXYPlot().setDomainCrosshairVisible(false);
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) { }

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (!updateLock) {
				List<String> selectedOwners = jOwners.getSelectedValuesList();
				Settings.get().getTrackerSettings().setSelectedOwners(selectedOwners);
				program.saveSettings("Tracker (Owners Selection)");
			}
			updateFilterButtons();
			createData();
		}
	}

	private class ImportFileLockWorker extends LockWorkerAdaptor {

		private File file;
		private Map<String, List<Value>> trackerData = null;

		public ImportFileLockWorker(File file) {
			this.file = file;
		}

		@Override
		public void task() {
			File unzippedFile = null;
			String extension = FileUtil.getExtension(file);
			if (extension.equals("zip")) { //Unzip file (if needed)
				ZipInputStream zis = null;
				try {
					zis = new ZipInputStream(new FileInputStream(file));
					ZipEntry zipEntry = zis.getNextEntry();
					while(zipEntry != null) {
						String filename = zipEntry.getName();
						if (filename.equals("settings.xml") || filename.equals("tracker.json")) {
							unzippedFile = new File(FileUtil.getPathDataDirectory() + File.separator + "temp_" + filename);
							if (unzippedFile.toPath().normalize().startsWith(FileUtil.getPathDataDirectory() + File.separator)) { //Make sure path is correct
								FileOutputStream fos = new FileOutputStream(unzippedFile);
								byte[] buffer = new byte[1024];
								int len;
								while ((len = zis.read(buffer)) > 0) {
									fos.write(buffer, 0, len);
								}
								fos.close();
								//Set file and extension to the unzipped file
								file = unzippedFile;
								extension = FileUtil.getExtension(file);
								break;
							}
						}
						zipEntry = zis.getNextEntry();
					}	zis.closeEntry();
					zis.close();
				} catch (IOException ex) {
					//Ignore errors
				} finally {
					try {
						if (zis != null) {
							zis.close();
						}
					} catch (IOException ex) {

					}
				}
			}
			switch (extension) { //Load data (if possible)
				case "xml":
				case "backup":
					trackerData = SettingsReader.loadTracker(file.getAbsolutePath());
					break;
				case "json":
					trackerData = TrackerDataReader.load(file.getAbsolutePath(), false);
					break;
				default:
					trackerData = null;
					break;
			}
			if (unzippedFile != null) { //Clean up temp file
				unzippedFile.delete();
			}
		}

		@Override
		public void hidden() {
			if (trackerData == null) { //Invalid file
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsTracker.get().importFileInvalidMsg(), TabsTracker.get().importFileInvalidTitle(), JOptionPane.WARNING_MESSAGE);
				return;
			}
			//Overwrite?
			int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsTracker.get().importFileOverwriteMsg(), TabsTracker.get().importFileOverwriteTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (value != JOptionPane.OK_OPTION) {
				return; //Cancel
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					jLockWindow.show(TabsTracker.get().importFileImport(), new LockWorkerAdaptor() {
						@Override
						public void task() {
							TrackerData.addAll(trackerData);
							TrackerData.save("File Import", true);
						}

						@Override
						public void gui() {
							updateData();
						}
					});
				}
			});
		}
	}

}
