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
package net.nikr.eve.jeveasset.gui.tabs.routing;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.RouteFinder;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.RouteResult;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.TextImport;
import net.nikr.eve.jeveasset.gui.shared.TextImport.TextImportHandler;
import net.nikr.eve.jeveasset.gui.shared.components.JAutoCompleteDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JImportDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JImportDialog.ImportReturn;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog.TextReturn;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuUI;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels.StringFilterator;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation.LocationType;
import net.nikr.eve.jeveasset.gui.tabs.routing.JRouteEditDialog.Route;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsRouting;
import net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter;
import net.nikr.eve.jeveasset.io.local.SettingsReader;
import net.nikr.eve.jeveasset.io.local.SettingsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.me.candle.eve.graph.DisconnectedGraphException;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.distances.Jumps;
import uk.me.candle.eve.routing.BruteForce;
import uk.me.candle.eve.routing.Crossover;
import uk.me.candle.eve.routing.NearestNeighbour;
import uk.me.candle.eve.routing.Progress;
import uk.me.candle.eve.routing.RoutingAlgorithm;
import uk.me.candle.eve.routing.SimpleUnisexMutatorHibrid2Opt;
import uk.me.candle.eve.routing.cancel.CancelService;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog.SimpleTextImport;

/**
 *
 * @author Candle
 */
public class RoutingTab extends JMainTabSecondary {

	private static final Logger LOG = LoggerFactory.getLogger(RoutingTab.class);

	enum ImportSystemType implements SimpleTextImport {
		SYSTEM_NAMES(TabsRouting.get().resultImportNames(), SYSTEM_NAMES_EXAMPLE, Images.STOCKPILE_SHOPPING_LIST.getIcon()),
		SYSTEM_IDS(TabsRouting.get().resultImportIDs(), SYSTEM_IDS_EXAMPLE, Images.LOC_SYSTEM.getIcon());
		private final String type;
		private final String example;
		private final Icon icon;

		private ImportSystemType(String type, String example, Icon icon) {
			this.type = type;
			this.example = example;
			this.icon = icon;
		}

		@Override
		public String getType() {
			return type;
		}

		@Override
		public String getExample() {
			return example;
		}

		@Override
		public Icon getIcon() {
			return icon;
		}
		
	}

	private static final String SYSTEM_NAMES_EXAMPLE =
			"Jita\n" +
			"Sobaseki\n" +
			"Malkalen\n" +
			"New Caldari\n" +
			"Niyabainen\n" +
			"Perimeter\n" +
			"Maurasi";

	private static final String SYSTEM_IDS_EXAMPLE = "30000142 30001363 30001393 30000145 30000143 30000144 30000140";

	private enum RoutingAction {
		ADD,
		REMOVE,
		IMPORT_SYSTEMS,
		ADD_SYSTEM,
		ADD_STATION,
		SOURCE,
		ALGORITHM,
		ALGORITHM_HELP,
		CALCULATE,
		EVE_UI,
		ROUTE_SAVE,
		ROUTE_EDIT,
		ROUTE_MANAGE,
		IMPORT_ROUTE_XML,
		IMPORT_ROUTE,
		ROUTE_EXPORT,
	}
	//Routing
	private JLabel jAlgorithmLabel;
	private JComboBox<RoutingAlgorithmContainer> jAlgorithm;
	private JButton jAlgorithmInfo;
	private JLabel jFilterLabel;
	private JLabel jFilterSecurityIcon;
	private JLabel jFilterSecurity;
	private JLabel jFilterSystemIcon;
	private JLabel jFilterSystem;
	private JLabel jSourceLabel;
	private JComboBox<SourceItem> jSource;
	private JToggleButton jSystems;
	private JToggleButton jStations;
	private JLabel jStartLabel;
	private JComboBox<String> jStart;
	private EventList<String> startEventList;
	private MoveJList<SolarSystem> jAvailable;
	private JLabel jAvailableRemaining;
	private JButton jAdd;
	private JButton jRemove;
	private JButton jImportSystems;
	private JButton jAddSystem;
	private JButton jAddStation;
	private MoveJList<SolarSystem> jWaypoints;
	private JLabel jWaypointsRemaining;
	//Filter
	private JAvoid jAvoid;
	//Progress
	private JProgressBar jProgress;
	private JButton jCalculate;
	//Result
	private JTextArea jResult;
	private JTextArea jFullResult;
	private JTextArea jInfo;
	private List<ResultToolbar> resultToolbars = new ArrayList<>();
	//Dialogs
	private TextImport<ImportSystemType> textImport;
	private JTextDialog jImportSystemsDialog;
	private JAutoCompleteDialog<MyLocation> jStationDialog;
	private JAutoCompleteDialog<SolarSystem> jSystemDialog;
	private JAutoCompleteDialog<String> jSaveRouteDialog;
	private JRouteManageDialog jManageRoutesDialog;
	private JRouteEditDialog jRouteEditDialog;
	private JMultiSelectionDialog<String> jRouteSelectionDialog;
	private JCustomFileChooser jFileChooser;
	private JImportDialog jImportDialog;

	private ListenerClass listener;
	private RouteFind routeFind;

	//Data
	private final Map<Long, SolarSystem> systemCache = new HashMap<>();
	private final Set<SolarSystem> available = new HashSet<>();
	protected Graph<SolarSystem> filteredGraph;
	private double lastSecMin = 0.0;
	private double lastSecMax = 1.0;
	private List<Long> lastAvoid = new ArrayList<>();
	private boolean uiEnabled = true;
	private RouteResult routeResult = null;

	public static final String NAME = "routing"; //Not to be changed!
	/**
	 *
	 * @param load does nothing except change the signature.
	 */
	protected RoutingTab(final boolean load) {
		super(load);
	}

	public RoutingTab(final Program program) {
		super(program, NAME, TabsRouting.get().routingTitle(), Images.TOOL_ROUTING.getIcon(), true);

		listener = new ListenerClass();

		textImport = new TextImport<>(program, NAME);
		jImportSystemsDialog = new JTextDialog(program.getMainWindow().getFrame());
		jStationDialog = new JAutoCompleteDialog<>(program, TabsRouting.get().addStationTitle(), Images.TOOL_ROUTING.getImage(), TabsRouting.get().addStationSelect(), true, JAutoCompleteDialog.LOCATION_OPTIONS);
		jSystemDialog = new JAutoCompleteDialog<>(program, TabsRouting.get().addSystemTitle(), Images.TOOL_ROUTING.getImage(), TabsRouting.get().addSystemSelect(), true, JAutoCompleteDialog.SOLAR_SYSTEM_OPTIONS);
		jSaveRouteDialog = new JAutoCompleteDialog<>(program, TabsRouting.get().routeSaveTitle(), Images.TOOL_ROUTING.getImage(), TabsRouting.get().routeSaveMsg(), false, JAutoCompleteDialog.STRING_OPTIONS);
		jManageRoutesDialog = new JRouteManageDialog(this, program);
		jRouteEditDialog = new JRouteEditDialog(this, program);
		jRouteSelectionDialog = new JMultiSelectionDialog<>(program, TabsRouting.get().resultSelectRoutes());
		jFileChooser = new JCustomFileChooser("xml");
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jImportDialog = new JImportDialog(program, new JImportDialog.ImportOptions() {
			@Override public boolean isRenameSupported() {
				return true;
			}
			@Override public boolean isMergeSupported() {
				return false;
			}
			@Override public boolean isOverwriteSupported() {
				return true;
			}
			@Override public boolean isSkipSupported() {
				return true;
			}
			@Override public String getTextRenameHelp() {
				return TabsRouting.get().importOptionsRenameHelp();
			}
			@Override public String getTextMergeHelp() {
				return "";
			}
			@Override public String getTextOverwriteHelp() {
				return TabsRouting.get().importOptionsOverwriteHelp();
			}
			@Override public String getTextSkipHelp() {
				return TabsRouting.get().importOptionsSkipHelp();
			}
			@Override public String getTextAll(int count) {
				return TabsRouting.get().importOptionsAll(count);
			}
		});

	//Routing
		JPanel jRoutingPanel = new JPanel();
		GroupLayout routingLayout = new GroupLayout(jRoutingPanel);
		jRoutingPanel.setLayout(routingLayout);
		routingLayout.setAutoCreateGaps(true);
		routingLayout.setAutoCreateContainerGaps(true);

		jAlgorithmLabel = new JLabel(TabsRouting.get().algorithm());

		jAlgorithm = new JComboBox<>(new ListComboBoxModel<>(RoutingAlgorithmContainer.getRegisteredList()));
		jAlgorithm.setSelectedIndex(0);
		jAlgorithm.setActionCommand(RoutingAction.ALGORITHM.name());
		jAlgorithm.addActionListener(listener);

		jAlgorithmInfo = new JButton(Images.MISC_HELP.getIcon());
		jAlgorithmInfo.setActionCommand(RoutingAction.ALGORITHM_HELP.name());
		jAlgorithmInfo.addActionListener(listener);

		jFilterLabel = new JLabel(TabsRouting.get().filters());
		jFilterSecurityIcon = new JLabel();
		jFilterSecurity = new JLabel();
		jFilterSystemIcon = new JLabel(Images.LOC_SYSTEM.getIcon());
		jFilterSystem = new JLabel();
		jFilterSystem.setIconTextGap(4);

		jSourceLabel = new JLabel(TabsRouting.get().source());

		jSource = new JComboBox<>();
		jSource.setActionCommand(RoutingAction.SOURCE.name());
		jSource.addActionListener(listener);

		ButtonGroup buttonGroup = new ButtonGroup();
		jStations = new JToggleButton(Images.LOC_STATION.getIcon());
		jStations.setHorizontalAlignment(JToggleButton.LEFT);
		jStations.setActionCommand(RoutingAction.SOURCE.name());
		jStations.addActionListener(listener);
		buttonGroup.add(jStations);
		jSystems = new JToggleButton(Images.LOC_SYSTEM.getIcon());
		jSystems.setHorizontalAlignment(JToggleButton.LEFT);
		jSystems.setSelected(true);
		jSystems.setActionCommand(RoutingAction.SOURCE.name());
		jSystems.addActionListener(listener);
		buttonGroup.add(jSystems);

		//Start system
		jStartLabel = new JLabel(TabsRouting.get().startSystem());

		jStart = new JComboBox<>();
		jStart.setEnabled(false);
		startEventList = EventListManager.create();
		AutoCompleteSupport<String> startAutoComplete = AutoCompleteSupport.install(jStart, EventModels.createSwingThreadProxyList(startEventList), new StringFilterator());
		startAutoComplete.setStrict(true);
		try {
			startEventList.getReadWriteLock().writeLock().lock();
			startEventList.add(TabsRouting.get().startEmpty());
		} finally {
			startEventList.getReadWriteLock().writeLock().unlock();
		}
		jStart.setSelectedItem(TabsRouting.get().startEmpty());

		jAvoid = new JAvoid(program, Settings.get().getRoutingSettings().getAvoidSettings(), true);

		jAvailable = new MoveJList<>(new EditableListModel<>());
		jAvailable.getEditableModel().setSortComparator(JAvoid.SOLAR_SYSTEM_COMPARATOR);
		jAvailable.addMouseListener(listener);
		jAvailable.addListSelectionListener(listener);

		jAvailableRemaining = new JLabel();

		jAdd = new JButton(TabsRouting.get().add());
		jAdd.setActionCommand(RoutingAction.ADD.name());
		jAdd.addActionListener(listener);

		jRemove = new JButton(TabsRouting.get().remove());
		jRemove.setActionCommand(RoutingAction.REMOVE.name());
		jRemove.addActionListener(listener);

		jImportSystems = new JButton(Images.EDIT_IMPORT.getIcon());
		jImportSystems.setActionCommand(RoutingAction.IMPORT_SYSTEMS.name());
		jImportSystems.addActionListener(listener);

		jAddSystem = new JButton(TabsRouting.get().addSystem(), Images.LOC_SYSTEM.getIcon());
		jAddSystem.setHorizontalAlignment(JToggleButton.LEFT);
		jAddSystem.setActionCommand(RoutingAction.ADD_SYSTEM.name());
		jAddSystem.addActionListener(listener);

		jAddStation = new JButton(TabsRouting.get().addStation(), Images.LOC_STATION.getIcon());
		jAddStation.setHorizontalAlignment(JToggleButton.LEFT);
		jAddStation.setActionCommand(RoutingAction.ADD_STATION.name());
		jAddStation.addActionListener(listener);

		jWaypoints = new MoveJList<>(new EditableListModel<>());
		jWaypoints.getEditableModel().setSortComparator(JAvoid.SOLAR_SYSTEM_COMPARATOR);
		jWaypoints.addMouseListener(listener);
		jWaypoints.addListSelectionListener(listener);

		jWaypointsRemaining = new JLabel();

		JScrollPane jAvailableScroll = new JScrollPane(jAvailable);
		JScrollPane jWaypointsScroll = new JScrollPane(jWaypoints);

		routingLayout.setHorizontalGroup(
			routingLayout.createSequentialGroup()
				.addGroup(routingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(routingLayout.createSequentialGroup()
						.addGroup(routingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jAlgorithmLabel)
							.addComponent(jSourceLabel)
						)
						.addGroup(routingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addGroup(routingLayout.createSequentialGroup()
								.addComponent(jAlgorithm)
								.addGap(5)
								.addComponent(jAlgorithmInfo, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth())
							)
							.addComponent(jSource)
						)
					)
					.addComponent(jAvailableScroll, 300, 300, Short.MAX_VALUE)
					.addGroup(routingLayout.createSequentialGroup()
						.addComponent(jAvailableRemaining, 0, 0, Short.MAX_VALUE)
						.addComponent(jSystems, 65, 65, 65)
						.addComponent(jStations, 65, 65, 65)
					)
				)
				.addGroup(routingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(jAdd, 80, 80, 80)
					.addComponent(jRemove, 80, 80, 80)
				)
				.addGroup(routingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(routingLayout.createSequentialGroup()
						.addGroup(routingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jStartLabel)
							.addComponent(jFilterLabel)
						)
						.addGroup(routingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addGroup(routingLayout.createSequentialGroup()
								.addComponent(jFilterSecurityIcon)
								.addGap(0)
								.addComponent(jFilterSecurity)
								.addGap(20)
								.addComponent(jFilterSystemIcon)
								.addGap(4)
								.addComponent(jFilterSystem)
							)
							.addComponent(jStart)
						)
					)
					.addComponent(jWaypointsScroll, 300, 300, Integer.MAX_VALUE)
					.addGroup(routingLayout.createSequentialGroup()
						.addComponent(jWaypointsRemaining, 0, 0, Integer.MAX_VALUE)
						.addComponent(jImportSystems, Program.getIconButtonsWidth(), Program.getIconButtonsWidth(), Program.getIconButtonsWidth())
						.addComponent(jAddSystem, 65, 65, 65)
						.addComponent(jAddStation, 65, 65, 65)
					)
				)
		);
		routingLayout.setVerticalGroup(
			routingLayout.createSequentialGroup()
				.addGroup(routingLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
					.addComponent(jAlgorithmLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAlgorithm, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAlgorithmInfo, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFilterLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFilterSecurityIcon, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFilterSecurity, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFilterSystemIcon, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jFilterSystem, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(routingLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
					.addComponent(jSourceLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSource, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jStartLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jStart, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(routingLayout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
					.addComponent(jAvailableScroll, 130, 130, Integer.MAX_VALUE)
					.addComponent(jWaypointsScroll, 130, 130, Integer.MAX_VALUE)
					.addGroup(routingLayout.createSequentialGroup()
						.addComponent(jAdd, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
						.addComponent(jRemove, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					)
				)
				.addGroup(routingLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
					.addComponent(jAvailableRemaining, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSystems, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jStations, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jWaypointsRemaining, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jImportSystems, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAddStation, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jAddSystem, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	//Filters
		JPanel jFilterPanel = new JPanel();
		GroupLayout filterLayout = new GroupLayout(jFilterPanel);
		jFilterPanel.setLayout(filterLayout);
		filterLayout.setAutoCreateGaps(true);
		filterLayout.setAutoCreateContainerGaps(true);

		updateFilterLabels();

		filterLayout.setHorizontalGroup(
			filterLayout.createSequentialGroup()
				.addComponent(jAvoid.getAvoidPanel())
				.addComponent(jAvoid.getSecurityPanel())
		);
		filterLayout.setVerticalGroup(
			filterLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
				.addComponent(jAvoid.getAvoidPanel())
				.addComponent(jAvoid.getSecurityPanel())
		);
	//Progress
		jProgress = new JProgressBar();
		jProgress.setValue(0);
		jProgress.setMaximum(100);
		jProgress.setMinimum(0);

		jCalculate = new JButton(TabsRouting.get().calculate());
		jCalculate.setActionCommand(RoutingAction.CALCULATE.name());
		jCalculate.addActionListener(listener);
	//Result
		JPanel jResultPanel = new JPanel();
		GroupLayout resultLayout = new GroupLayout(jResultPanel);
		jResultPanel.setLayout(resultLayout);
		resultLayout.setAutoCreateGaps(true);
		resultLayout.setAutoCreateContainerGaps(false);

		JPanel jFullResultPanel = new JPanel();
		GroupLayout fullResultLayout = new GroupLayout(jFullResultPanel);
		jFullResultPanel.setLayout(fullResultLayout);
		fullResultLayout.setAutoCreateGaps(true);
		fullResultLayout.setAutoCreateContainerGaps(false);

		JPanel jInfoPanel = new JPanel();
		GroupLayout infoLayout = new GroupLayout(jInfoPanel);
		jInfoPanel.setLayout(infoLayout);
		infoLayout.setAutoCreateGaps(true);
		infoLayout.setAutoCreateContainerGaps(false);

		ResultToolbar jResultPanelToolBar = new ResultToolbar();
		resultToolbars.add(jResultPanelToolBar);

		jResult = new JTextArea();
		jResult.setEditable(false);
		jResult.setFont(jPanel.getFont());

		ResultToolbar jFullResultToolBar = new ResultToolbar();
		resultToolbars.add(jFullResultToolBar);

		jFullResult = new JTextArea();
		jFullResult.setEditable(false);
		jFullResult.setFont(jPanel.getFont());

		ResultToolbar jInfoToolBar = new ResultToolbar();
		resultToolbars.add(jInfoToolBar);

		jInfo = new JTextArea();
		jInfo.setEditable(false);
		jInfo.setFont(jPanel.getFont());

		final JScrollPane jResultScroll = new JScrollPane(jResult, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JScrollPane jFullResultScroll = new JScrollPane(jFullResult, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jFullResultScroll.getVerticalScrollBar().setModel(jResultScroll.getVerticalScrollBar().getModel());
		jFullResultScroll.setWheelScrollingEnabled(false);
		jFullResultScroll.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				jResultScroll.dispatchEvent(e);
			}
		});
		JScrollPane jInfoScroll = new JScrollPane(jInfo);

		resultLayout.setHorizontalGroup(
			resultLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jResultPanelToolBar.getComponent())
				.addComponent(jResultScroll)
		);
		resultLayout.setVerticalGroup(
			resultLayout.createSequentialGroup()
				.addComponent(jResultPanelToolBar.getComponent())
				.addComponent(jResultScroll)
		);

		fullResultLayout.setHorizontalGroup(
			fullResultLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jFullResultToolBar.getComponent())
				.addComponent(jFullResultScroll)
		);
		fullResultLayout.setVerticalGroup(
			fullResultLayout.createSequentialGroup()
				.addComponent(jFullResultToolBar.getComponent())
				.addComponent(jFullResultScroll)
		);

		infoLayout.setHorizontalGroup(
			infoLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jInfoToolBar.getComponent())
				.addComponent(jInfoScroll)
		);
		infoLayout.setVerticalGroup(
			infoLayout.createSequentialGroup()
				.addComponent(jInfoToolBar.getComponent())
				.addComponent(jInfoScroll)
		);

		JTabbedPane jResultTabs = new JTabbedPane();
		jResultTabs.addTab(TabsRouting.get().resultTabShort(), jResultPanel);
		jResultTabs.addTab(TabsRouting.get().resultTabFull(), jFullResultPanel);
		jResultTabs.addTab(TabsRouting.get().resultTabInfo(), jInfoPanel);

		JTabbedPane jSystemTabs = new JTabbedPane();
		jSystemTabs.addTab(TabsRouting.get().routingTab(), jRoutingPanel);
		jSystemTabs.addTab(TabsRouting.get().filtersTab(), jFilterPanel);

		// widths are defined in here.
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jSystemTabs)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jProgress, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
					.addComponent(jCalculate)
				)
				.addComponent(jResultTabs)
			);
		// heights are defined here.
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jSystemTabs, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
					.addComponent(jProgress, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCalculate, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addComponent(jResultTabs)
		);
		buildGraph(true); //Build default Graph (0.0/All sec - no avoids)
		jSystemDialog.updateData(filteredGraph.getNodes()); //Will be replaced by valid systems by processRouteInner()
		jAvoid.updateSystemDialog(filteredGraph.getNodes()); //Will be replaced by valid systems by processRouteInner()
	}

	@Override
	public void updateData() {
		//Do everything the constructor does...
		jAvailable.getEditableModel().clear();
		jWaypoints.getEditableModel().clear();
		overviewGroupsChanged();
		jAlgorithm.setSelectedIndex(0);
		jResult.setText(TabsRouting.get().emptyResult());
		jResult.setCaretPosition(0);
		jResult.setEnabled(false);
		jFullResult.setText(TabsRouting.get().emptyResult());
		jFullResult.setCaretPosition(0);
		jFullResult.setEnabled(false);
		jInfo.setText(TabsRouting.get().emptyResult());
		jInfo.setCaretPosition(0);
		jInfo.setEnabled(false);

		List<MyLocation> stations = new ArrayList<>();
		for (MyLocation location : StaticData.get().getLocations()) {
			if (location.isStation()) { //Not Planet
				stations.add(location);
			}
		}
		jStationDialog.updateData(stations);

		updateRemaining();
		processFilteredAssets();
		updateRoutes();
	}

	public void overviewGroupsChanged() {
		List<SourceItem> sources = new ArrayList<>();
		for (Entry<String, OverviewGroup> entry : Settings.get().getOverviewGroups().entrySet()) {
			sources.add(new SourceItem(entry.getKey(), true));
		}
		Collections.sort(sources);
		sources.add(0, new SourceItem(TabsRouting.get().filteredAssets()));
		sources.add(0, new SourceItem(General.get().all()));
		jSource.setModel(new ListComboBoxModel<>(sources));
	}

	@Override
	public void clearData() {
		RoutingAlgorithm.setCache(false); //Clear cache
	}

	@Override
	public void updateCache() {}

	@Override
	public Collection<net.nikr.eve.jeveasset.data.settings.types.LocationType> getLocations() {
		return new ArrayList<>(); //No Location
	}

	public void updateRoutes() {
		for (ResultToolbar resultToolbar : resultToolbars) {
			resultToolbar.update();
		}
		jManageRoutesDialog.updateData();
		jSaveRouteDialog.updateData(Settings.get().getRoutingSettings().getRoutes().keySet());
	}

	public SolarSystem getSolarSystem() {
		return jSystemDialog.show();
	}

	private void updateRemaining() {
		updateWaypointsRemaining();
		updateAvailableRemaining();
		validateLists();
	}

	private void updateWaypointsRemaining() {
		int max = ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).getWaypointLimit();
		int cur = getWaypointsSize();
		if (max < cur) {
			jWaypointsRemaining.setForeground(Color.RED);
		} else if (max == cur) {
			jWaypointsRemaining.setForeground(Color.BLUE);
		} else {
			jWaypointsRemaining.setForeground(Color.BLACK);
		}
		jWaypointsRemaining.setText(TabsRouting.get().allowed(cur, max));
	}

	private void updateAvailableRemaining() {
		int cur = jAvailable.getModel().getSize();
		int tot = cur + getWaypointsSize();
		jAvailableRemaining.setText(TabsRouting.get().total(cur, tot));
	}

	protected final void buildGraph(boolean all) {
		// build the graph.
		// filter the solarsystems based on the settings.
		if (filteredGraph != null) {
			filteredGraph.clear();
		}
		filteredGraph = new Graph<>(new Jumps<>());
		RouteFinder.generateGraph(systemCache, filteredGraph, all ? null : Settings.get().getRoutingSettings().getAvoidSettings());
	}

	protected void processFilteredAssets() {
		// select the active places.
		List<MyAsset> assets;
		SourceItem source = (SourceItem) jSource.getSelectedItem();
		if (source.getName().equals(General.get().all())) { //ALL
			assets = new ArrayList<>(program.getAssetsList());
		} else if (source.getName().equals(TabsRouting.get().filteredAssets())) { //FILTERS
			assets = program.getAssetsTab().getFilteredAssets();
		} else { //OVERVIEW GROUP
			assets = new ArrayList<>();
			OverviewGroup group = Settings.get().getOverviewGroups().get(source.getName());
			for (OverviewLocation location : group.getLocations()) {
				for (MyAsset asset : program.getAssetsList()) {
					if ((location.getName().equals(asset.getLocation().getLocation()))
						|| (location.getType() == LocationType.TYPE_SYSTEM && location.getName().equals(asset.getLocation().getSystem()))
						|| (location.getType() == LocationType.TYPE_CONSTELLATION && location.getName().equals(asset.getLocation().getConstellation()))
						|| (location.getType() == LocationType.TYPE_REGION && location.getName().equals(asset.getLocation().getRegion()))
						) {
						assets.add(asset);
					}
				}
			}
		}
		SortedSet<SolarSystem> allLocs = new TreeSet<>(new Comparator<SolarSystem>() {
			@Override
			public int compare(final SolarSystem o1, final SolarSystem o2) {
				String n1 = o1.getName();
				String n2 = o2.getName();
				return n1.compareToIgnoreCase(n2);
			}
		});
		for (MyAsset ea : assets) {
			SolarSystem loc = null;
			if (jSystems.isSelected()) { //System
				loc = systemCache.get(ea.getLocation().getSystemID());
			} else if (ea.getLocation().isStation()) { //Not planet
				loc = new SolarSystem(ea.getLocation());
			}
			if (loc != null && (loc.getRegionID() < 11000000 || loc.getRegionID() > 13000000)) { //Ignore Wormhole and Abyssal Regions
				allLocs.add(loc);
			} else {
				LOG.debug("ignoring {}", ea.getLocation().getLocation());
			}
		}
		available.clear();
		available.addAll(allLocs);
		jAvailable.getEditableModel().addAll(allLocs);
		for (SolarSystem system : jWaypoints.getEditableModel().getAll()) {
			jAvailable.getEditableModel().remove(system);
		}
		if (jSystems.isSelected()) {
			jSystems.setText(TabsRouting.get().checked());
			jStations.setText(TabsRouting.get().unchecked());
		} else {
			jSystems.setText(TabsRouting.get().unchecked());
			jStations.setText(TabsRouting.get().checked());
		}
		updateRemaining();
	}

	/**
	 * Moves the selected items in the 'from' JList to the 'to' JList.
	 *
	 * @param from
	 * @param to
	 * @return true if all the items were moved.
	 */
	private void move(final MoveJList<SolarSystem> from, final MoveJList<SolarSystem> to) {
		for (SolarSystem ss : from.getSelectedValuesList()) {
			if (from.getEditableModel().contains(ss)) {
				LOG.debug("Moving {}", ss);
				if (from.getEditableModel().remove(ss)) {
					to.getEditableModel().add(ss);
				}
			}
		}
		from.setSelectedIndices(new int[]{});
		to.setSelectedIndices(new int[]{});
		List<SolarSystem> systems = new ArrayList<>(jAvailable.getEditableModel().getAll());
		for (SolarSystem system : systems) {
			if (!available.contains(system)) {
				jAvailable.getEditableModel().remove(system);
			}
		}
		to.requestFocusInWindow();
		updateRemaining();
	}

	private void processRoute() {
		//Disable the UI controls
		setUIEnabled(false);
		//Reset Progress
		jProgress.setValue(0);
		jProgress.setIndeterminate(true);
		//Create Thread
		routeFind = new RouteFind();
		//Add progress listener
		routeFind.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
					int progress = (Integer) evt.getNewValue();
					if (jProgress.isIndeterminate() && progress > 0) {
						jProgress.setIndeterminate(false);
					}
					jProgress.setValue(progress);
				}
			}
		});
		//Start Thread
		routeFind.execute();
	}

	private void processRouteInner() {
		try {
			//Update Graph if needed (AKA filter has changed)
			if (lastSecMin != jAvoid.getSecurityMinimum()
				|| lastSecMax != jAvoid.getSecurityMaximum()
				|| !lastAvoid.equals(new ArrayList<>(Settings.get().getRoutingSettings().getAvoid().keySet()))) {
				buildGraph(false);
				lastSecMin = jAvoid.getSecurityMinimum();
				lastSecMax = jAvoid.getSecurityMaximum();
				lastAvoid = new ArrayList<>(Settings.get().getRoutingSettings().getAvoid().keySet());
			}
			//Warning for 2 or less systems
			if (getWaypointsSize() <= 2) {
				Program.ensureEDT(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsRouting.get().noSystems(), TabsRouting.get().noSystemsTitle(), JOptionPane.INFORMATION_MESSAGE);
					}
				});
				return;
			}
			routeResult = null;
			//Clear previous results
			Program.ensureEDT(new Runnable() {
				@Override
				public void run() {
					jResult.setText(TabsRouting.get().emptyResult());
					jResult.setCaretPosition(0);
					jResult.setEnabled(false);
					jFullResult.setText(TabsRouting.get().emptyResult());
					jFullResult.setCaretPosition(0);
					jFullResult.setEnabled(false);
					jResult.setCaretPosition(0);
					jInfo.setText(TabsRouting.get().emptyResult());
					jInfo.setCaretPosition(0);
					jInfo.setEnabled(false);
					for (ResultToolbar resultToolbar : resultToolbars) {
						resultToolbar.setEnabledResult(false);
						resultToolbar.update();
					}
				}
			});
			//Update all SolarSystem with the latest from the new Graph
			//This is needed to get the proper Edge(s) parsed to the routing Algorithm
			Map<Long, List<SolarSystem>> stationsMap = new HashMap<>();
			Set<SolarSystem> waypoints = new HashSet<>();
			for (SolarSystem solarSystem : jWaypoints.getEditableModel().getAll()) {
				if (solarSystem.isStation()) { //Not Planet
					List<SolarSystem> stations = stationsMap.get(solarSystem.getSystemID());
					if (stations == null) {
						stations = new ArrayList<>();
						stationsMap.put(solarSystem.getSystemID(), stations);
					}
					stations.add(solarSystem);
				}
				waypoints.add(systemCache.get(solarSystem.getSystemID()));
			}
			List<SolarSystem> inputWaypoints = new ArrayList<>(waypoints);
			//Move frist system to the top....
			String text = jStart.getItemAt(jStart.getSelectedIndex());
			if (!text.contains(TabsRouting.get().startEmpty())) {
				Collections.sort(inputWaypoints, new Comparator<SolarSystem>() {
					@Override
					public int compare(SolarSystem o1, SolarSystem o2) {
						if (o1.getName().equals(text) && o2.getName().equals(text)) {
							return 0; //Equal
						} else if (o1.getName().equals(text)) {
							return -1; //Before
						} else if (o2.getName().equals(text)) {
							return 1; //After
						} else {
							return o1.getName().compareTo(o2.getName());
						}
					}
				});
			}
			//Start route finding:
			RoutingAlgorithmContainer algorithm = (RoutingAlgorithmContainer) jAlgorithm.getSelectedItem();
			List<SolarSystem> nodeRoute = executeRouteFinding(inputWaypoints, algorithm);
			if (nodeRoute.isEmpty()) { //Cancelled
				algorithm.resetCancelService();
				return;
			} else { //Completed!
				Program.ensureEDT(new Runnable() {
					@Override
					public void run() {
						jProgress.setValue(jProgress.getMaximum());
					}
				});
			}
			SolarSystem last = null;
			List<List<SolarSystem>> route = new ArrayList<>();
			for (SolarSystem current : nodeRoute) {
				if (last != null) {
					route.add(new ArrayList<>(filteredGraph.routeBetween(last, current)));
				}
				last = current;
			}
			if (last != null) {
				route.add(new ArrayList<>(filteredGraph.routeBetween(last, nodeRoute.get(0))));
			}
			setRouteResult(new RouteResult(route, stationsMap, inputWaypoints.size(), algorithm.getName(), algorithm.getLastTimeTaken(), algorithm.getLastDistance(), getAvoidString(), getSecurityString()));
		} catch (DisconnectedGraphException dce) {
			Program.ensureEDT(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(program.getMainWindow().getFrame(),
							 dce.getMessage(),
							 TabsRouting.get().error(),
							 JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}

	public String getSecurityString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(Formatter.securityFormat(jAvoid.getSecurityMinimum()));
		builder.append(" - ");
		builder.append(Formatter.securityFormat(jAvoid.getSecurityMaximum()));
		return builder.toString();
	}

	public String getAvoidString() {
		final StringBuilder builder = new StringBuilder();
		for (SolarSystem avoidSystem : Settings.get().getRoutingSettings().getAvoid().values()) {
			if (!builder.toString().isEmpty()) {
				builder.append(", ");
			}
			builder.append(avoidSystem.getName());
		}
		if (builder.toString().isEmpty()) {
			builder.append(TabsRouting.get().avoidNone());
		}
		return builder.toString();
	}

	public void setRouteResult(RouteResult routeResult) {
		this.routeResult = routeResult;
	//Route Result
		final StringBuilder fullRouteString = new StringBuilder();
		final StringBuilder routeString = new StringBuilder();
		boolean first = true;
		for (List<SolarSystem> systems : routeResult.getRoute()) {
			if (first) {
				first = false;
			} else {
				fullRouteString.append('\n');
				routeString.append('\n');
			}
			boolean firstFull = true;
			for (SolarSystem routeSystem : systems) {
				if (firstFull) {
					firstFull = false;
					fullRouteString.append(routeSystem.getName());
				} else {
					fullRouteString.append(TabsRouting.get().resultArrow());
					fullRouteString.append(routeSystem.getName());
				}
			}
			routeString.append(systems.get(0).getName());
			List<SolarSystem> stations = routeResult.getStations().get(systems.get(0).getSystemID());
			if (stations != null) {
				for (SolarSystem station : stations) {
					fullRouteString.append("\n    • ");
					fullRouteString.append(station.getName());
					routeString.append("\n    • ");
					routeString.append(station.getName());
				}
			}
		}
		//Set results
		Program.ensureEDT(new Runnable() {
			@Override
			public void run() {
				jResult.setText(routeString.toString());
				jResult.setEnabled(true);
				jResult.setCaretPosition(0);
				jFullResult.setText(fullRouteString.toString());
				jFullResult.setEnabled(true);
				jFullResult.setCaretPosition(0);
				jInfo.setText(TabsRouting.get().resultText(routeResult.getAlgorithmName(),
					routeResult.getJumps(),
					routeResult.getWaypoints(),
					routeResult.getSecurity(),
					routeResult.getAvoid(),
					Formatter.milliseconds(routeResult.getAlgorithmTime())));
				jInfo.setEnabled(true);
				jInfo.setCaretPosition(0);
				for (ResultToolbar resultToolbar : resultToolbars) {
					resultToolbar.setEnabledResult(true);
					resultToolbar.update();
				}
			}
		});
	}

	protected Graph<SolarSystem> getGraph() {
		return filteredGraph;
	}

	private List<SolarSystem> executeRouteFinding(final List<SolarSystem> inputWaypoints, final RoutingAlgorithmContainer algorithm) {
		return algorithm.execute(routeFind, filteredGraph, inputWaypoints);
	}

	private void setUIEnabled(final boolean b) {
		uiEnabled = b;
		//Routing
		jAlgorithmLabel.setEnabled(b);
		jAlgorithm.setEnabled(b);
		jAlgorithmInfo.setEnabled(b);
		jFilterLabel.setEnabled(b);
		jFilterSecurity.setEnabled(b);
		jFilterSystem.setEnabled(b);
		jSourceLabel.setEnabled(b);
		jSource.setEnabled(b);
		jStartLabel.setEnabled(b);
		if (jStart.getItemAt(jStart.getSelectedIndex()).contains(TabsRouting.get().startEmpty())) {
			jStart.setEnabled(false);
		} else {
			jStart.setEnabled(b);
		}
		jAvailable.setEnabled(b);
		jAvailableRemaining.setEnabled(b);
		jAdd.setEnabled(b);
		jRemove.setEnabled(b);
		jImportSystems.setEnabled(b);
		jAddSystem.setEnabled(b);
		jAddStation.setEnabled(b);
		jSystems.setEnabled(b);
		jStations.setEnabled(b);
		jWaypoints.setEnabled(b);
		jWaypointsRemaining.setEnabled(b);
		//Filters
		jAvoid.setEnabled(b);
		for (ResultToolbar resultToolbar : resultToolbars) {
			resultToolbar.setEnabled(b);
		}
		//Process
		if (b) {
			jCalculate.setText(TabsRouting.get().calculate());
		} else {
			jCalculate.setText(TabsRouting.get().cancel());
		}
	}

	private void cancelProcessing() {
		((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).getCancelService().cancel();
	}

	private int getWaypointsSize() {
		Set<Long> waypoints = new HashSet<>();
		for (SolarSystem solarSystem : jWaypoints.getEditableModel().getAll()) {
			waypoints.add(solarSystem.getSystemID());
		}
		return waypoints.size();
	}

	private void validateLists() {
		int waypointsSize = getWaypointsSize();
		if (uiEnabled) {
			jRemove.setEnabled(jWaypoints.getSelectedIndices().length > 0);
			jAdd.setEnabled(jAvailable.getSelectedIndices().length > 0);
		}
		jCalculate.setEnabled(waypointsSize <= ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).getWaypointLimit());
		if (jWaypoints.getEditableModel().getAll().isEmpty()) {
			try {
				startEventList.getReadWriteLock().writeLock().lock();
				startEventList.clear();
				startEventList.add(TabsRouting.get().startEmpty());
			} finally {
				startEventList.getReadWriteLock().writeLock().unlock();
			}
			jStart.setSelectedItem(TabsRouting.get().startEmpty());
			jStart.setEnabled(false);
		} else {
			String selected = jStart.getItemAt(jStart.getSelectedIndex());
			Set<String> systems = new TreeSet<>();
			for (SolarSystem system : jWaypoints.getEditableModel().getAll()) {
				systems.add(system.getName());
			}
			try {
				startEventList.getReadWriteLock().writeLock().lock();
				startEventList.clear();
				startEventList.addAll(systems);
			} finally {
				startEventList.getReadWriteLock().writeLock().unlock();
			}
			jStart.setEnabled(true);
			if (systems.contains(selected)) {
				jStart.setSelectedItem(selected);
			} else {
				jStart.setSelectedItem(systems.iterator().next());
			}
		}
	}

	public void addLocation(MyLocation location) {
		if (location == null) {
			return; //Cancel
		}
		SolarSystem system = systemCache.get(location.getSystemID());
		if (system == null) {
			return; //Ignore system that was not found
		}
		SolarSystem solarSystem = new SolarSystem(location);
		if (!jWaypoints.getEditableModel().contains(solarSystem)
			&& !jAvailable.getEditableModel().contains(solarSystem)) {
			//New
			jWaypoints.getEditableModel().add(solarSystem);
		} else if (jAvailable.getEditableModel().contains(solarSystem)) {
			//In available: moving to waypoints
			jAvailable.getEditableModel().remove(solarSystem);
			jWaypoints.getEditableModel().add(solarSystem);
		} //Else: Already in waypoints - do nothing
		updateRemaining();
	}

	private ImportReturn importOptions(final RouteResult routeResult, final String routeName, ImportReturn importReturn, final int count) {
		if (importReturn != ImportReturn.OVERWRITE_ALL
				&& importReturn != ImportReturn.MERGE_ALL
				&& importReturn != ImportReturn.RENAME_ALL
				&& importReturn != ImportReturn.SKIP_ALL) { //Not decided - ask what to do
			importReturn = jImportDialog.show(routeName, count);
		}
		//Rename
		if (importReturn == ImportReturn.RENAME || importReturn == ImportReturn.RENAME_ALL) {
			String name = jSaveRouteDialog.show(routeName);
			if (name == null) {
				return importOptions(routeResult, routeName, ImportReturn.RENAME, count);
			}
			Settings.get().getRoutingSettings().getRoutes().put(name, routeResult);
			updateRoutes();
		}
		//Overwrite
		if (importReturn == ImportReturn.OVERWRITE || importReturn == ImportReturn.OVERWRITE_ALL) {
			Settings.get().getRoutingSettings().getRoutes().put(routeName, routeResult);
			updateRoutes();
		}
		//Skip - Do nothing
		return importReturn;
	}

	private boolean makeRoute(List<Route> list) {
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsRouting.get().resultImportRouteEmpty(), TabsRouting.get().resultImportRoute(), JOptionPane.PLAIN_MESSAGE);
			return false;
		}
		if (list.size() < 2) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsRouting.get().resultImportRouteInvalid(), TabsRouting.get().resultImportRoute(), JOptionPane.PLAIN_MESSAGE);
			return false;
		}
		RouteResult result;
		try {
			result = JRouteEditDialog.makeRouteResult(this, systemCache, filteredGraph, list, TabsRouting.get().resultImported());
		} catch (DisconnectedGraphException ex) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(),
					ex.getMessage(),
					TabsRouting.get().error(),
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (jResult.isEnabled()) {
			int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsRouting.get().resultOverwrite(), TabsRouting.get().resultImportRoute(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (value != JOptionPane.OK_OPTION) {
				return false;
			}
		}
		setRouteResult(result);
		return true;
	}

	private void importText() {
		ImportSystemType systemType = ImportSystemType.SYSTEM_NAMES;
		try {
			systemType = ImportSystemType.valueOf(Settings.get().getImportSettings(NAME, systemType));
		} catch (IllegalArgumentException ex) {
			//No problem, use default
		}
		importText("", systemType);
	}

	private void importText(String text, ImportSystemType selected) {
		textImport.importText(text, ImportSystemType.values(), selected, new TextImportHandler<ImportSystemType>() {
			@Override
			public void addItems(TextReturn<ImportSystemType> textReturn) {
				String importText = textReturn.getText();
				ImportSystemType importType = textReturn.getType();
				if (importText == null || importType == null) {
					return; //Cancel
				}
				List<Route> list = new ArrayList<>();
				if (importType == ImportSystemType.SYSTEM_NAMES) {
					//Build lookup map
					Map<String, SolarSystem> systems = new HashMap<>();
					for (SolarSystem node : systemCache.values()) {
						systems.put(node.getSystem().toLowerCase(), node);
					}
					//For each line, check if the line matches a system name

					for (String line : importText.split("[\r\n]+")) {
						SolarSystem system = systems.get(line.toLowerCase().trim());
						if (system != null) {
							list.add(new Route(system.getSystemID(), system.getName()));
						}
					}
				} else if (importType == ImportSystemType.SYSTEM_IDS) {
					//For each line, check if the line matches a system name
					for (String line : importText.split("\\s+")) {
						try {
							long systemID = Long.parseLong(line.toLowerCase().trim());
							SolarSystem system = systemCache.get(systemID);
							if (system != null) {
								list.add(new Route(system.getSystemID(), system.getName()));
							}
						} catch (NumberFormatException ex) {
							//Try next line...
						}
					}
				}

				boolean update = makeRoute(list);
				if (!update) {
					importText(importText, importType);
				}
			}
		});
	}

	private void updateFilterLabels() {
		jAvoid.updateFilterLabels();
		double secMin = Settings.get().getRoutingSettings().getSecMin();
		double secMax = Settings.get().getRoutingSettings().getSecMax();
		int size = Settings.get().getRoutingSettings().getAvoid().size();
		jFilterSecurity.setText(Formatter.securityFormat(secMin) + " - " + Formatter.securityFormat(secMax));
		if (secMin == 0.0) {
			jFilterSecurityIcon.setIcon(Images.UPDATE_CANCELLED.getIcon());
		} else if (secMin >= 0.5) {
			jFilterSecurityIcon.setIcon(Images.UPDATE_DONE_OK.getIcon());
		} else {
			jFilterSecurityIcon.setIcon(Images.UPDATE_DONE_SOME.getIcon());
		}
		jFilterSystem.setText(String.valueOf(size));
	}

	private class ListenerClass extends MouseAdapter implements ActionListener, ListSelectionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			LOG.debug(e.getActionCommand());
			if (RoutingAction.ADD.name().equals(e.getActionCommand())) {
				move(jAvailable, jWaypoints);
			} else if (RoutingAction.REMOVE.name().equals(e.getActionCommand())) {
				move(jWaypoints, jAvailable);
			} else if (RoutingAction.CALCULATE.name().equals(e.getActionCommand())) {
				if (jCalculate.getText().equals(TabsRouting.get().cancel())) {
					cancelProcessing();
				} else {
					if (jResult.isEnabled()) {
						int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsRouting.get().resultOverwrite(), TabsRouting.get().calculate(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (value != JOptionPane.OK_OPTION) {
							return;
						}
					}
					processRoute();
				}
			} else if (RoutingAction.SOURCE.name().equals(e.getActionCommand())) {
				jAvailable.getEditableModel().clear();
				processFilteredAssets();
			} else if (RoutingAction.ALGORITHM.name().equals(e.getActionCommand())) {
				updateRemaining();
			} else if (RoutingAction.ALGORITHM_HELP.name().equals(e.getActionCommand())) {
				RoutingAlgorithmContainer rac = ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem());
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), rac.getBasicDescription(), rac.getName(), JOptionPane.INFORMATION_MESSAGE);
			} else if (RoutingAction.IMPORT_SYSTEMS.name().equals(e.getActionCommand())) {
				String importText = jImportSystemsDialog.importText("", SYSTEM_NAMES_EXAMPLE);
				if (importText == null || importText.isEmpty()) {
					return;
				}
				//Build lookup map
				Map<String, SolarSystem> systems = new HashMap<>();
				for (SolarSystem node : systemCache.values()) {
					systems.put(node.getSystem().toLowerCase(), node);
				}
				//For each line, check if the line matches a system name
				for (String line : importText.split("[\r\n]+")) {
					SolarSystem system = systems.get(line.toLowerCase().trim());
					if (system != null) {
						if (!jWaypoints.getEditableModel().contains(system)
							&& !jAvailable.getEditableModel().contains(system)) {
							//New
							jWaypoints.getEditableModel().add(system);
						} else if (jAvailable.getEditableModel().contains(system)) {
							//In available: moving to waypoints
							jAvailable.getEditableModel().remove(system);
							jWaypoints.getEditableModel().add(system);
						} //Else: Already in waypoints - do nothing
					}
				}
				updateRemaining();
			} else if (RoutingAction.ADD_STATION.name().equals(e.getActionCommand())) {
				MyLocation station = jStationDialog.show();
				addLocation(station);
			} else if (RoutingAction.ADD_SYSTEM.name().equals(e.getActionCommand())) {
				SolarSystem system = jSystemDialog.show();
				if (system != null) {
					if (!jWaypoints.getEditableModel().contains(system)
						&& !jAvailable.getEditableModel().contains(system)) {
						//New
						jWaypoints.getEditableModel().add(system);
					} else if (jAvailable.getEditableModel().contains(system)) {
						//In available: moving to waypoints
						jAvailable.getEditableModel().remove(system);
						jWaypoints.getEditableModel().add(system);
					} //Else: Already in waypoints - do nothing
					updateRemaining();
				}
			} else if (RoutingAction.EVE_UI.name().equals(e.getActionCommand())) {
				EsiOwner owner = JMenuUI.selectOwner(program, JMenuUI.EsiOwnerRequirement.AUTOPILOT);
				if (owner == null) {
					return;
				}
				JMenuUI.getLockWindow(program).show(GuiShared.get().updating(), new JMenuUI.EsiUpdate(owner) {
					@Override
					protected void updateESI() throws Throwable {
						boolean clear = true;
						for (List<SolarSystem> systems : routeResult.getRoute()) {
							SolarSystem system = systems.get(0);
							List<SolarSystem> stations = routeResult.getStations().get(system.getSystemID());
							if (stations != null && !stations.isEmpty()) { //Station(s)
								for (SolarSystem station : stations) {
									getApi().postUiAutopilotWaypoint(false, clear, station.getLocationID(), AbstractEsiGetter.DATASOURCE, null);
								}
							} else { //System
								getApi().postUiAutopilotWaypoint(false, clear, system.getSystemID(), AbstractEsiGetter.DATASOURCE, null);
							}
							if (clear) {
								clear = false;
							}
						}
					}
					@Override
					protected void ok() {
						JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsRouting.get().resultUiOk(), GuiShared.get().uiWaypointTitle(), JOptionPane.PLAIN_MESSAGE);
					}
					@Override
					protected void fail() {
						JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsRouting.get().resultUiFail(), GuiShared.get().uiWaypointTitle(), JOptionPane.PLAIN_MESSAGE);
					}
				});
			} else if (RoutingAction.ROUTE_SAVE.name().equals(e.getActionCommand())) {
				String name = jSaveRouteDialog.show();
				if (name == null) {
					return; //Cancel
				}
				Settings.lock("Routing (Save Route)");
				Settings.get().getRoutingSettings().getRoutes().put(name, routeResult);
				Settings.unlock("Routing (Save Route)");
				program.saveSettings("Routing (Save Route)");
				updateRoutes();
			} else if (RoutingAction.ROUTE_EDIT.name().equals(e.getActionCommand())) {
				buildGraph(false);
				RouteResult result = jRouteEditDialog.show(systemCache, filteredGraph, routeResult);
				if (result == null) {
					return;
				}
				setRouteResult(result);
			} else if (RoutingAction.ROUTE_MANAGE.name().equals(e.getActionCommand())) {
				jManageRoutesDialog.updateData();
				jManageRoutesDialog.setVisible(true);
			} else if (RoutingAction.IMPORT_ROUTE_XML.name().equals(e.getActionCommand())) {
				jFileChooser.setSelectedFile(null);
				jFileChooser.setCurrentDirectory(null);
				int returnValue = jFileChooser.showOpenDialog(program.getMainWindow().getFrame());
				if (returnValue != JCustomFileChooser.APPROVE_OPTION) {
					return;
				}
				File file = jFileChooser.getSelectedFile();
				if (file == null || !file.exists()) {
					return;
				}
				Map<String, RouteResult> routes = SettingsReader.loadRoutes(file.getAbsolutePath());
				if (routes == null) {
					routes = new HashMap<>();
				}
				List<String> selected = jRouteSelectionDialog.show(routes.keySet(), false);
				if (selected == null) {
					return;
				}
				List<String> added = new ArrayList<>();
				List<String> existing = new ArrayList<>();
				for (String routeName : selected) {
					if (Settings.get().getRoutingSettings().getRoutes().containsKey(routeName)) {
						existing.add(routeName);
					} else {
						added.add(routeName);
					}
				}

				int count = existing.size();
				ImportReturn importReturn = null;
				jImportDialog.resetToDefault();
				Settings.lock("Routing (Import Route)");
				for (String routeName : added) {
					RouteResult result = routes.get(routeName);
					Settings.get().getRoutingSettings().getRoutes().put(routeName, result);
				}
				updateRoutes();
				for (String routeName : existing) {
					RouteResult routeResult = routes.get(routeName);
					importReturn = importOptions(routeResult, routeName, importReturn, count);
					count--;
				}
				Settings.unlock("Routing (Import Route)");
				program.saveSettings("Routing (Import Route)");
			} else if (RoutingAction.IMPORT_ROUTE.name().equals(e.getActionCommand())) {
				importText();
			} else if (RoutingAction.ROUTE_EXPORT.name().equals(e.getActionCommand())) {
				List<String> selected = jRouteSelectionDialog.show(Settings.get().getRoutingSettings().getRoutes().keySet(), false);
				if (selected == null) {
					return;
				}
				jFileChooser.setSelectedFile(null);
				jFileChooser.setCurrentDirectory(null);
				int returnValue = jFileChooser.showSaveDialog(program.getMainWindow().getFrame());
				if (returnValue != JCustomFileChooser.APPROVE_OPTION) {
					return;
				}
				File file = jFileChooser.getSelectedFile();
				if (file == null) {
					return;
				}
				Map<String, RouteResult> routes = new HashMap<>();
				for (String routeName : selected) {
					RouteResult result = Settings.get().getRoutingSettings().getRoutes().get(routeName);
					if (result != null) {
						routes.put(routeName, result);
					}
				}
				SettingsWriter.saveRoutes(routes, file.getAbsolutePath());
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount()% 2 == 0
						&& !e.isControlDown()
						&& !e.isShiftDown()
						) {
				if (e.getSource().equals(jAvailable) && jAvailable.isEnabled()) {
					move(jAvailable, jWaypoints);
				} else if (e.getSource().equals(jWaypoints) && jWaypoints.isEnabled()) {
					move(jWaypoints, jAvailable);
				}
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getSource().equals(jAvailable) || e.getSource().equals(jWaypoints)) {
				validateLists();
			}
		}
	}

	private class ResultToolbar {

		private final JToolBar jToolBar;
		private final JLabel jName;
		private final JButton jEveUiSetRoute;
		private final JButton jEditRoute;
		private final JButton jExportRoute;
		private final JButton jSaveRoute;
		private final JDropDownButton jLoadRoute;
		private final JMenuItem jManageRoutes;
		private final Font plain;
		private final Font italic;

		public ResultToolbar() {
			jToolBar = new JToolBar();
			GroupLayout layout = new GroupLayout(jToolBar);
			jToolBar.setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(false);

			jToolBar.setFloatable(false);
			jToolBar.setRollover(true);

			jName = new JLabel();
			plain = jName.getFont();
			italic = new Font(plain.getName(), Font.ITALIC, plain.getSize());

			jEveUiSetRoute = new JButton(TabsRouting.get().resultUiWaypoints(), Images.MISC_EVE.getIcon());
			jEveUiSetRoute.setActionCommand(RoutingAction.EVE_UI.name());
			jEveUiSetRoute.addActionListener(listener);
			jEveUiSetRoute.setEnabled(false);

			jEditRoute = new JButton(TabsRouting.get().resultEdit(), Images.EDIT_EDIT.getIcon());
			jEditRoute.setHorizontalAlignment(JButton.LEFT);
			jEditRoute.setActionCommand(RoutingAction.ROUTE_EDIT.name());
			jEditRoute.addActionListener(listener);
			jEditRoute.setEnabled(false);

			jExportRoute = new JButton(TabsRouting.get().resultExport(), Images.DIALOG_CSV_EXPORT.getIcon());
			jExportRoute.setHorizontalAlignment(JButton.LEFT);
			jExportRoute.setActionCommand(RoutingAction.ROUTE_EXPORT.name());
			jExportRoute.addActionListener(listener);
			jExportRoute.setEnabled(false);

			JDropDownButton jImport = new JDropDownButton(TabsRouting.get().resultImport(), Images.EDIT_IMPORT.getIcon());

			JMenuItem jImportXml = new JMenuItem(TabsRouting.get().resultImportXml(), Images.TOOL_ROUTING.getIcon());
			jImportXml.setHorizontalAlignment(JButton.LEFT);
			jImportXml.setActionCommand(RoutingAction.IMPORT_ROUTE_XML.name());
			jImportXml.addActionListener(listener);
			jImport.add(jImportXml);

			jImport.addSeparator();

			JMenuItem jImportText = new JMenuItem(TabsRouting.get().resultImportText(), Images.STOCKPILE_SHOPPING_LIST.getIcon());
			jImportText.setHorizontalAlignment(JButton.LEFT);
			jImportText.setActionCommand(RoutingAction.IMPORT_ROUTE.name());
			jImportText.addActionListener(listener);
			jImport.add(jImportText);

			jSaveRoute = new JButton(TabsRouting.get().resultSave(), Images.FILTER_SAVE.getIcon());
			jSaveRoute.setHorizontalAlignment(JButton.LEFT);
			jSaveRoute.setActionCommand(RoutingAction.ROUTE_SAVE.name());
			jSaveRoute.addActionListener(listener);
			jSaveRoute.setEnabled(false);

			jLoadRoute = new JDropDownButton(TabsRouting.get().resultLoad(), Images.FILTER_LOAD.getIcon());
			jLoadRoute.setHorizontalAlignment(JButton.LEFT);

			layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(jName, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					.addGap(0, 0, Integer.MAX_VALUE)
					.addComponent(jEveUiSetRoute)
					.addComponent(jEditRoute, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, 100)
					.addComponent(jExportRoute, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, 100)
					.addComponent(jImport, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, 100)
					.addComponent(jSaveRoute, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, 100)
					.addComponent(jLoadRoute, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, 100)
					.addContainerGap()
			);
			layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jName)
					.addComponent(jEveUiSetRoute)
					.addComponent(jEditRoute)
					.addComponent(jExportRoute)
					.addComponent(jImport)
					.addComponent(jSaveRoute)
					.addComponent(jLoadRoute)
			);

			jManageRoutes = new JMenuItem(TabsRouting.get().resultManage(), Images.DIALOG_SETTINGS.getIcon());
			jManageRoutes.setActionCommand(RoutingAction.ROUTE_MANAGE.name());
			jManageRoutes.addActionListener(listener);
		}

		public JToolBar getComponent() {
			return jToolBar;
		}

		public void update() {
			jLoadRoute.removeAll();

			jLoadRoute.setEnabled(!Settings.get().getRoutingSettings().getRoutes().isEmpty());

			if (!Settings.get().getRoutingSettings().getRoutes().isEmpty()) {
				jLoadRoute.add(jManageRoutes);
				jLoadRoute.addSeparator();
			}

			if (routeResult != null) {
				String name = null;
				for (Map.Entry<String, RouteResult> entry : Settings.get().getRoutingSettings().getRoutes().entrySet()) {
					if (entry.getValue().equals(routeResult)) {
						name = entry.getKey();
						break;
					}
				}
				if (name == null) {
					name = TabsRouting.get().resultUntitled();
					jName.setFont(italic);
					jName.setEnabled(false);
				} else {
					jName.setFont(plain);
					jName.setEnabled(true);
				}
				jName.setText(name);
			} else {
				jName.setText(TabsRouting.get().resultEmpty());
				jName.setFont(italic);
				jName.setEnabled(false);
			}

			for (Map.Entry<String, RouteResult> entry : Settings.get().getRoutingSettings().getRoutes().entrySet()) {
				JMenuItem jMenuItem = new JMenuItem(entry.getKey(), Images.FILTER_LOAD.getIcon());
				jMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setRouteResult(entry.getValue());
						updateRoutes();
					}
				});
				jLoadRoute.add(jMenuItem);
			}
			jExportRoute.setEnabled(!Settings.get().getRoutingSettings().getRoutes().isEmpty());
		}

		public void setEnabledResult(boolean b) {
			jEveUiSetRoute.setEnabled(b);
			jEditRoute.setEnabled(b);
			jSaveRoute.setEnabled(b);
		}

		public void setEnabled(boolean b) {
			if (b) {
				jLoadRoute.setEnabled(!Settings.get().getRoutingSettings().getRoutes().isEmpty());
			} else {
				jLoadRoute.setEnabled(b);
			}
		}
	}

	/**
	 * A GUI compatible container for the routing algorithms.
	 */
	private static class RoutingAlgorithmContainer {

		private RoutingAlgorithm<SolarSystem> contained;

		public RoutingAlgorithmContainer(final RoutingAlgorithm<SolarSystem> contained) {
			this.contained = contained;
		}

		public int getWaypointLimit() {
			return contained.getWaypointLimit();
		}

		public String getName() {
			return contained.getName();
		}

		public String getTechnicalDescription() {
			return contained.getTechnicalDescription();
		}

		public String getBasicDescription() {
			return contained.getBasicDescription();
		}

		public List<SolarSystem> execute(final Progress progress, final Graph<SolarSystem> g, final List<SolarSystem> assetLocations) {
			return contained.execute(progress, g, assetLocations);
		}

		public long getLastTimeTaken() {
			return contained.getLastTimeTaken();
		}

		public int getLastDistance() {
			return contained.getLastDistance();
		}

		public CancelService getCancelService() {
			return contained.getCancelService();
		}

		public void resetCancelService() {
			contained.resetCancelService();
		}

		@Override
		public String toString() {
			return getName();
		}

		public static List<RoutingAlgorithmContainer> getRegisteredList() {
			List<RoutingAlgorithmContainer> list = new ArrayList<>();
			list.add(new RoutingAlgorithmContainer(new BruteForce<>()));
			list.add(new RoutingAlgorithmContainer(new SimpleUnisexMutatorHibrid2Opt<>()));
			list.add(new RoutingAlgorithmContainer(new Crossover<>()));
			list.add(new RoutingAlgorithmContainer(new NearestNeighbour<>()));
			return list;
		}
	}

	private class RouteFind extends SwingWorker<Void, Void> implements Progress {

		private int maximum = 1;
		private int minimum = 0;
		private int value = 0;
		private int oldProgress = 0;

		@Override
		protected Void doInBackground() throws Exception {
			processRouteInner();
			return null;
		}

		@Override
		protected void done() {
			setUIEnabled(true);
			jProgress.setValue(0);
			jProgress.setIndeterminate(false);
		}

		@Override
		public int getMaximum() {
			return maximum;
		}

		@Override
		public void setMaximum(int maximum) {
			this.maximum = maximum;
		}

		@Override
		public int getMinimum() {
			return minimum;
		}

		@Override
		public void setMinimum(int minimum) {
			this.minimum = minimum;
		}

		@Override
		public int getValue() {
			return value;
		}

		@Override
		public void setValue(int value) {
			this.value = value;
			int progress = (int) Math.floor(value * 100.0 / getMaximum());
			if (progress < 0) {
				progress = 0;
			}
			if (progress > 100) {
				progress = 100;
			}
			if (progress != oldProgress) {
				oldProgress = progress;
				setProgress(oldProgress);
			}
		}
	}

	static class SourceItem implements Comparable<SourceItem> {

		private final String name;
		private final boolean group;

		public SourceItem(final String name) {
			this.name = name;
			this.group = false;
		}

		public SourceItem(final String name, final boolean group) {
			this.name = name;
			this.group = group;
		}

		@Override
		public String toString() {
			if (group) {
				return TabsRouting.get().overviewGroup(name);
			} else {
				return name;
			}
		}

		public String getName() {
			return name;
		}

		@Override
		public int compareTo(final SourceItem o) {
			return this.getName().compareToIgnoreCase(o.getName());
		}
	}
}
