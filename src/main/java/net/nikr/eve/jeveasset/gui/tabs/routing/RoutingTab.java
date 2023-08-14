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
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
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
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyAsset;
import net.nikr.eve.jeveasset.data.sde.Jump;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.data.settings.RouteResult;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JImportDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JImportDialog.ImportReturn;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuUI;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels.StringFilterator;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation.LocationType;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsRouting;
import net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter;
import net.nikr.eve.jeveasset.io.local.SettingsReader;
import net.nikr.eve.jeveasset.io.local.SettingsWriter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.me.candle.eve.graph.DisconnectedGraphException;
import uk.me.candle.eve.graph.Edge;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.distances.Jumps;
import uk.me.candle.eve.routing.BruteForce;
import uk.me.candle.eve.routing.Crossover;
import uk.me.candle.eve.routing.NearestNeighbour;
import uk.me.candle.eve.routing.Progress;
import uk.me.candle.eve.routing.RoutingAlgorithm;
import uk.me.candle.eve.routing.SimpleUnisexMutatorHibrid2Opt;
import uk.me.candle.eve.routing.cancel.CancelService;

/**
 *
 * @author Candle
 */
public class RoutingTab extends JMainTabSecondary {

	private static final Logger LOG = LoggerFactory.getLogger(RoutingTab.class);

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
		ROUTE_IMPORT,
		ROUTE_EXPORT,
		AVOID_ADD,
		AVOID_REMOVE,
		AVOID_CLEAR,
		AVOID_SAVE,
		AVOID_LOAD,
		AVOID_MANAGE,
		SAVE
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
	private JList<SolarSystem> jAvoid;
	private EditableListModel<SolarSystem> avoidModel = new EditableListModel<>();
	private JButton jAvoidAdd;
	private JButton jAvoidRemove;
	private JButton jAvoidClear;
	private JButton jAvoidSave;
	private JDropDownButton jAvoidLoad;
	private JLabel jSecurityIcon;
	private JComboBox<Double> jSecurityMinimum;
	private JLabel jSecuritySeparatorLabel;
	private JComboBox<Double> jSecurityMaximum;
	//Progress
	private JProgressBar jProgress;
	private JButton jCalculate;
	//Result
	private JTextArea jResult;
	private JTextArea jFullResult;
	private JTextArea jInfo;
	private List<ResultToolbar> resultToolbars = new ArrayList<>();
	//Dialogs
	private JTextDialog jImportSystemsDialog;
	private JStationDialog jStationDialog;
	private JSystemDialog jSystemDialog;
	private JSaveAvoidDialog jSaveSystemDialog;
	private JManageAvoidDialog jManageAvoidDialog;
	private JRouteSaveDialog jSaveRouteDialog;
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

		jImportSystemsDialog = new JTextDialog(program.getMainWindow().getFrame());
		jStationDialog = new JStationDialog(program);
		jSystemDialog = new JSystemDialog(program);
		jSaveSystemDialog = new JSaveAvoidDialog(program);
		jManageAvoidDialog = new JManageAvoidDialog(this, program);
		jSaveRouteDialog = new JRouteSaveDialog(program);
		jManageRoutesDialog = new JRouteManageDialog(this, program);
		jRouteEditDialog = new JRouteEditDialog(program);
		jRouteSelectionDialog = new JMultiSelectionDialog<>(program, TabsRouting.get().resultSelectRoutes());
		jFileChooser = JCustomFileChooser.createFileChooser(program.getMainWindow().getFrame(), "xml");
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

		Comparator<SolarSystem> comp = new Comparator<SolarSystem>() {
			@Override
			public int compare(final SolarSystem o1, final SolarSystem o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		};

		jAvailable = new MoveJList<>(new EditableListModel<>());
		jAvailable.getEditableModel().setSortComparator(comp);
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
		jWaypoints.getEditableModel().setSortComparator(comp);
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

		JPanel jAvoidPanel = new JPanel();
		jAvoidPanel.setBorder(BorderFactory.createTitledBorder(TabsRouting.get().avoid()));
		GroupLayout avoidLayout = new GroupLayout(jAvoidPanel);
		jAvoidPanel.setLayout(avoidLayout);
		avoidLayout.setAutoCreateGaps(true);
		avoidLayout.setAutoCreateContainerGaps(true);

		JPanel jSecurityPanel = new JPanel();
		jSecurityPanel.setBorder(BorderFactory.createTitledBorder(TabsRouting.get().security()));
		GroupLayout securityLayout = new GroupLayout(jSecurityPanel);
		jSecurityPanel.setLayout(securityLayout);
		securityLayout.setAutoCreateGaps(true);
		securityLayout.setAutoCreateContainerGaps(true);

		avoidModel.setSortComparator(comp);
		avoidModel.addAll(Settings.get().getRoutingSettings().getAvoid().values());

		jAvoid = new JList<>(avoidModel);
		jAvoid.addMouseListener(listener);
		jAvoid.addListSelectionListener(listener);

		JFixedToolBar jToolBar = new JFixedToolBar(JFixedToolBar.Orientation.VERTICAL);

		jAvoidAdd = new JButton(TabsRouting.get().avoidAdd(), Images.EDIT_ADD.getIcon());
		jAvoidAdd.setActionCommand(RoutingAction.AVOID_ADD.name());
		jAvoidAdd.addActionListener(listener);
		jToolBar.addButton(jAvoidAdd);

		jAvoidRemove = new JButton(TabsRouting.get().avoidRemove(), Images.EDIT_DELETE.getIcon());
		jAvoidRemove.setActionCommand(RoutingAction.AVOID_REMOVE.name());
		jAvoidRemove.addActionListener(listener);
		jAvoidRemove.setEnabled(false);
		jToolBar.addButton(jAvoidRemove);

		jAvoidClear = new JButton(TabsRouting.get().avoidClear(), Images.FILTER_CLEAR.getIcon());
		jAvoidClear.setActionCommand(RoutingAction.AVOID_CLEAR.name());
		jAvoidClear.addActionListener(listener);
		jToolBar.addButton(jAvoidClear);

		jAvoidSave = new JButton(TabsRouting.get().avoidSave(), Images.FILTER_SAVE.getIcon());
		jAvoidSave.setActionCommand(RoutingAction.AVOID_SAVE.name());
		jAvoidSave.addActionListener(listener);
		jToolBar.addButton(jAvoidSave);

		jAvoidLoad = new JDropDownButton(TabsRouting.get().avoidLoad(), Images.FILTER_LOAD.getIcon());
		jToolBar.addButton(jAvoidLoad);

		JScrollPane jAvoidScroll = new JScrollPane(jAvoid);

		Double[] security = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

		jSecurityIcon = new JLabel();

		jSecurityMinimum = new JComboBox<>(security);
		jSecurityMinimum.setSelectedItem(Settings.get().getRoutingSettings().getSecMin());
		jSecurityMinimum.setActionCommand(RoutingAction.SAVE.name());
		jSecurityMinimum.addActionListener(listener);

		jSecuritySeparatorLabel = new JLabel(" - ");

		jSecurityMaximum = new JComboBox<>(security);
		jSecurityMaximum.setSelectedItem(Settings.get().getRoutingSettings().getSecMax());
		jSecurityMaximum.setActionCommand(RoutingAction.SAVE.name());
		jSecurityMaximum.addActionListener(listener);

		updateFilterLabels();
		updateSavedFilters();

		avoidLayout.setHorizontalGroup(
			avoidLayout.createSequentialGroup()
				.addComponent(jAvoidScroll, 300, 300, Integer.MAX_VALUE)
				.addComponent(jToolBar)
		);
		avoidLayout.setVerticalGroup(
			avoidLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
				.addComponent(jAvoidScroll, 160, 160, Integer.MAX_VALUE)
				.addComponent(jToolBar)
		);
		securityLayout.setHorizontalGroup(
			securityLayout.createSequentialGroup()
				.addComponent(jSecurityIcon)
				.addComponent(jSecurityMinimum, 80, 80, Short.MAX_VALUE)
				.addComponent(jSecuritySeparatorLabel)
				.addComponent(jSecurityMaximum, 80, 80, Short.MAX_VALUE)
		);
		securityLayout.setVerticalGroup(
			securityLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
				.addComponent(jSecurityIcon, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSecurityMinimum, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSecuritySeparatorLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				.addComponent(jSecurityMaximum, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
		filterLayout.setHorizontalGroup(
			filterLayout.createSequentialGroup()
				.addComponent(jAvoidPanel)
				.addComponent(jSecurityPanel)
		);
		filterLayout.setVerticalGroup(
			filterLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
				.addComponent(jAvoidPanel)
				.addComponent(jSecurityPanel)
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
		double secMin;
		double secMax;
		if (jSecurityMinimum != null) {
			secMin = (Double) jSecurityMinimum.getSelectedItem();
		} else {
			secMin = 0.0f;
		}
		if (jSecurityMaximum != null) {
			secMax = (Double) jSecurityMaximum.getSelectedItem();
		} else {
			secMax = 1.0f;
		}
		int count = 0;
		for (Jump jump : StaticData.get().getJumps()) { // this way we exclude the locations that are unreachable.
			count++;
			SplashUpdater.setSubProgress((int) (count * 100.0 / StaticData.get().getJumps().size()));

			SolarSystem from = systemCache.get(jump.getFrom().getSystemID());
			SolarSystem to = systemCache.get(jump.getTo().getSystemID());
			if (from == null) {
				from = SolarSystem.create(systemCache, jump.getFrom());
			}
			if (to == null) {
				to = SolarSystem.create(systemCache, jump.getTo());
			}
			if (all || (jump.getFrom().getSecurityObject().getDouble() >= secMin
						&& jump.getTo().getSecurityObject().getDouble() >= secMin
						&& jump.getFrom().getSecurityObject().getDouble() <= secMax
						&& jump.getTo().getSecurityObject().getDouble() <= secMax
						&& !Settings.get().getRoutingSettings().getAvoid().keySet().contains(jump.getFrom().getSystemID())
						&& !Settings.get().getRoutingSettings().getAvoid().keySet().contains(jump.getTo().getSystemID())
					)) {
				filteredGraph.addEdge(new Edge<>(from, to));
			}
		}
		SplashUpdater.setSubProgress(100);
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
			if (lastSecMin != (Double) jSecurityMinimum.getSelectedItem()
					|| lastSecMax != (Double) jSecurityMaximum.getSelectedItem()
					|| !lastAvoid.equals(new ArrayList<>(Settings.get().getRoutingSettings().getAvoid().keySet()))) {
				buildGraph(false);
				lastSecMin = (Double) jSecurityMinimum.getSelectedItem();
				lastSecMax = (Double) jSecurityMaximum.getSelectedItem();
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
		builder.append(Formatter.securityFormat(jSecurityMinimum.getSelectedItem()));
		builder.append(" - ");
		builder.append(Formatter.securityFormat(jSecurityMaximum.getSelectedItem()));
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
		jAvoidAdd.setEnabled(b);
		if (b) {
			jAvoidRemove.setEnabled(jAvoid.getSelectedIndices().length > 0);
			jAvoidClear.setEnabled(!avoidModel.getAll().isEmpty());
			jAvoidSave.setEnabled(!avoidModel.getAll().isEmpty());
			jAvoidLoad.setEnabled(!Settings.get().getRoutingSettings().getPresets().isEmpty());
		} else {
			jAvoidRemove.setEnabled(b);
			jAvoidClear.setEnabled(b);
			jAvoidSave.setEnabled(b);
			jAvoidLoad.setEnabled(b);
		}
		jSecurityMinimum.setEnabled(b);
		jSecuritySeparatorLabel.setEnabled(b);
		jSecurityMaximum.setEnabled(b);
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

	private void updateFilterLabels() {
		double secMin = Settings.get().getRoutingSettings().getSecMin();
		double secMax = Settings.get().getRoutingSettings().getSecMax();
		int size = Settings.get().getRoutingSettings().getAvoid().size();
		jFilterSecurity.setText(Formatter.securityFormat(secMin) + " - " + Formatter.securityFormat(secMax));
		if (secMin == 0.0) {
			jSecurityIcon.setIcon(Images.UPDATE_CANCELLED.getIcon());
			jFilterSecurityIcon.setIcon(Images.UPDATE_CANCELLED.getIcon());
		} else if (secMin >= 0.5) {
			jSecurityIcon.setIcon(Images.UPDATE_DONE_OK.getIcon());
			jFilterSecurityIcon.setIcon(Images.UPDATE_DONE_OK.getIcon());
		} else {
			jSecurityIcon.setIcon(Images.UPDATE_DONE_SOME.getIcon());
			jFilterSecurityIcon.setIcon(Images.UPDATE_DONE_SOME.getIcon());
		}
		jFilterSystem.setText(String.valueOf(size));
		jAvoidClear.setEnabled(!avoidModel.getAll().isEmpty());
		jAvoidSave.setEnabled(!avoidModel.getAll().isEmpty());
		jAvoidLoad.setEnabled(!Settings.get().getRoutingSettings().getPresets().isEmpty());
	}

	private void updateSavedFilters() {
		jAvoidLoad.removeAll();

		JMenuItem jManage = new JMenuItem(TabsRouting.get().avoidManage(), Images.DIALOG_SETTINGS.getIcon());
		jManage.setActionCommand(RoutingAction.AVOID_MANAGE.name());
		jManage.addActionListener(listener);
		jAvoidLoad.add(jManage);

		if (!Settings.get().getRoutingSettings().getPresets().isEmpty()) {
			jAvoidLoad.addSeparator();
		}

		ArrayList<String> presets = new ArrayList<>(Settings.get().getRoutingSettings().getPresets().keySet());
		Collections.sort(presets);
		for (String name : presets) {
			JMenuItem jMenuItem = new JLoadMenuItem(name, Settings.get().getRoutingSettings().getPresets().get(name));
			jMenuItem.setActionCommand(RoutingAction.AVOID_LOAD.name());
			jMenuItem.addActionListener(listener);
			jAvoidLoad.add(jMenuItem);
		}
		jAvoidLoad.setEnabled(!Settings.get().getRoutingSettings().getPresets().isEmpty());
		jManageAvoidDialog.updateData();
	}

	public void loadFilter(Set<Long> systemIds) {
		avoidModel.clear();
		Settings.lock("Routing (Load Filter)");
		Settings.get().getRoutingSettings().getAvoid().clear();
		for (Long systemID : systemIds) {
			SolarSystem system = new SolarSystem(ApiIdConverter.getLocation(systemID));
			Settings.get().getRoutingSettings().getAvoid().put(system.getSystemID(), system);
			avoidModel.add(system);
		}
		Settings.unlock("Routing (Load Filter)");
		program.saveSettings("Routing (Load Filter)");
		updateFilterLabels();
	}

	public void deleteFilters(List<String> list) {
		Settings.lock("Routing (Delete Filters)");
		for (String filter : list) {
			Settings.get().getRoutingSettings().getPresets().remove(filter);
		}
		Settings.unlock("Routing (Delete Filters)");
		program.saveSettings("Routing (Delete Filters)");
		updateSavedFilters();
	}

	public void renameFilter(String name, String oldName) {
		Settings.lock("Routing (Rename Filter)");
		Set<Long> systemIDs = Settings.get().getRoutingSettings().getPresets().remove(oldName);
		Settings.get().getRoutingSettings().getPresets().put(name, systemIDs);
		Settings.unlock("Routing (Rename Filter)");
		program.saveSettings("Routing (Rename Filter)");
		updateSavedFilters();
	}

	public void mergeFilters(String name, List<String> list) {
		Set<Long> systemIDs = new HashSet<>();
		Settings.lock("Routing (Merge Filters)");
		for (String mergeName : list) {
			systemIDs.addAll(Settings.get().getRoutingSettings().getPresets().get(mergeName));
		}
		Settings.get().getRoutingSettings().getPresets().put(name, systemIDs);
		Settings.unlock("Routing (Merge Filters)");
		program.saveSettings("Routing (Merge Filters)");
		updateSavedFilters();
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

	private void removeSystems() {
		Settings.lock("Routing (Delete Systems)");
		for (SolarSystem system : jAvoid.getSelectedValuesList()) {
			avoidModel.remove(system);
			Settings.get().getRoutingSettings().getAvoid().remove(system.getSystemID());
		}
		Settings.unlock("Routing (Delete Systems)");
		program.saveSettings("Routing (Delete Systems)");
		updateFilterLabels();
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
			} else if (RoutingAction.AVOID_REMOVE.name().equals(e.getActionCommand())) {
				removeSystems();
			} else if (RoutingAction.AVOID_CLEAR.name().equals(e.getActionCommand())) {
				Settings.lock("Routing (Clear Systems)");
				Settings.get().getRoutingSettings().getAvoid().clear();
				avoidModel.clear();
				updateFilterLabels();
				Settings.unlock("Routing (Clear Systems)");
				program.saveSettings("Routing (Clear Systems)");
			} else if (RoutingAction.AVOID_ADD.name().equals(e.getActionCommand())) {
				SolarSystem system = jSystemDialog.show();
				if (system != null) {
					Settings.lock("Routing (Add System)");
					Settings.get().getRoutingSettings().getAvoid().put(system.getSystemID(), system);
					avoidModel.clear();
					avoidModel.addAll(Settings.get().getRoutingSettings().getAvoid().values());
					Settings.unlock("Routing (Add System)");
					program.saveSettings("Routing (Add System)");
					updateFilterLabels();
				}
			} else if (RoutingAction.AVOID_SAVE.name().equals(e.getActionCommand())) {
				jSaveSystemDialog.updateData(new ArrayList<>(Settings.get().getRoutingSettings().getPresets().keySet()));
				String name = jSaveSystemDialog.show();
				if (name != null) {
					Settings.lock("Routing (Save Filter)");
					Set<Long> systemIDs = new HashSet<>();
					for (SolarSystem system : avoidModel.getAll()) {
						systemIDs.add(system.getSystemID());
					}
					Settings.get().getRoutingSettings().getPresets().put(name, systemIDs);
					Settings.unlock("Routing (Save Filter)");
					program.saveSettings("Routing (Save Filter)");
					updateSavedFilters();
				}
			} else if (RoutingAction.AVOID_LOAD.name().equals(e.getActionCommand())) {
				Object source = e.getSource();
				if (source instanceof JLoadMenuItem) {
					JLoadMenuItem menuItem = (JLoadMenuItem) source;
					loadFilter(menuItem.getSystemIDs());
				}
			} else if (RoutingAction.AVOID_MANAGE.name().equals(e.getActionCommand())) {
				jManageAvoidDialog.updateData();
				jManageAvoidDialog.setVisible(true);
			} else if (RoutingAction.SAVE.name().equals(e.getActionCommand())) {
				double min = (Double) jSecurityMinimum.getSelectedItem();
				double max = (Double) jSecurityMaximum.getSelectedItem();
				if (max < min) {
					max = min;
					jSecurityMaximum.setSelectedItem(min);
				}
				Settings.lock("Routing (Security)");
				Settings.get().getRoutingSettings().setSecMin(min);
				Settings.get().getRoutingSettings().setSecMax(max);
				Settings.unlock("Routing (Security)");
				program.saveSettings("Routing (Security)");
				updateFilterLabels();
			} else if (RoutingAction.IMPORT_SYSTEMS.name().equals(e.getActionCommand())) {
				String importText = jImportSystemsDialog.importText();
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
			} else if (RoutingAction.ROUTE_IMPORT.name().equals(e.getActionCommand())) {
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
				} else if (e.getSource().equals(jAvoid) && jAvoid.isEnabled()) {
					removeSystems();
				}
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getSource().equals(jAvailable) || e.getSource().equals(jWaypoints)) {
				validateLists();
			} else if (e.getSource().equals(jAvoid)) {
				jAvoidRemove.setEnabled(jAvoid.getSelectedIndices().length > 0);
			}
		}
	}

	private class ResultToolbar {

		private final JToolBar jToolBar;
		private final JLabel jName;
		private final JButton jEveUiSetRoute;
		private final JButton jEditRoute;
		private final JButton jExportRoute;
		private final JButton jImportRoute;
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

			jImportRoute = new JButton(TabsRouting.get().resultImport(), Images.EDIT_IMPORT.getIcon());
			jImportRoute.setHorizontalAlignment(JButton.LEFT);
			jImportRoute.setActionCommand(RoutingAction.ROUTE_IMPORT.name());
			jImportRoute.addActionListener(listener);

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
					.addComponent(jImportRoute, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, 100)
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
					.addComponent(jImportRoute)
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

	private static class JLoadMenuItem extends JMenuItem {

		private final Set<Long> systemIDs;

		public JLoadMenuItem(String name, Set<Long> systemIDs) {
			super(name, Images.FILTER_LOAD.getIcon());
			this.systemIDs = systemIDs;
		}

		public Set<Long> getSystemIDs() {
			return systemIDs;
		}
	}
}
