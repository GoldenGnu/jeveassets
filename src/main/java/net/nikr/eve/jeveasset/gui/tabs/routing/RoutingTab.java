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
package net.nikr.eve.jeveasset.gui.tabs.routing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.Jump;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.SolarSystem;
import net.nikr.eve.jeveasset.data.StaticData;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.tabs.assets.Asset;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewGroup;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewLocation.LocationType;
import net.nikr.eve.jeveasset.i18n.General;
import net.nikr.eve.jeveasset.i18n.TabsRouting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.me.candle.eve.graph.DisconnectedGraphException;
import uk.me.candle.eve.graph.Edge;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.Progress;
import uk.me.candle.eve.routing.RoutingAlgorithm;
import uk.me.candle.eve.routing.cancel.CancelService;

/**
 *
 * @author Candle
 */
public class RoutingTab extends JMainTab  {

	private static final  Logger LOG = LoggerFactory.getLogger(RoutingTab.class);

	private enum RoutingAction {
		ADD,
		REMOVE,
		ADD_SYSTEM,
		SOURCE,
		ALGORITHM,
		ALGORITHM_HELP,
		CALCULATE,
		CANCEL
	}

	private JButton jAdd;
	private JButton jRemove;
	private JButton jCalculate;
	private JButton jAddSystem;
	private JComboBox jAlgorithm;
	private JButton jAlgorithmInfo;
	private JComboBox jSource;
	private MoveJList<SolarSystem> jAvailable;
	private MoveJList<SolarSystem> jWaypoints;
	private JLabel jStartLabel;
	private JTextField jStart;
	private JLabel jAvailableRemaining;
	private JLabel jWaypointsRemaining; // waypoint count
	private JLabel jSourceLabel; // waypoint count
	private JLabel jAlgorithmLabel; // waypoint count
	private ProgressBar jProgress;
	private JButton jCancel;
	private JTextArea jResult;
	private JTextArea jFullResult;
	private JTextArea jInfo;
	private JSystemDialog jSystemDialog;

	protected Graph filteredGraph;
	/**
	 *
	 * @param load does nothing except change the signature.
	 */
	protected RoutingTab(final boolean load) {
		super(load);
	}

	public RoutingTab(final Program program) {
		super(program, TabsRouting.get().routingTitle(), Images.TOOL_ROUTING.getIcon(), true);

		ListenerClass listener = new ListenerClass();

		jSystemDialog = new JSystemDialog(program);

		jAdd = new JButton(TabsRouting.get().add());
		jAdd.setActionCommand(RoutingAction.ADD.name());
		jAdd.addActionListener(listener);

		jRemove = new JButton(TabsRouting.get().remove());
		jRemove.setActionCommand(RoutingAction.REMOVE.name());
		jRemove.addActionListener(listener);

		jAddSystem = new JButton(TabsRouting.get().addSystem());
		jAddSystem.setActionCommand(RoutingAction.ADD_SYSTEM.name());
		jAddSystem.addActionListener(listener);

		jSourceLabel = new JLabel(TabsRouting.get().source());

		jSource = new JComboBox();
		jSource.setActionCommand(RoutingAction.SOURCE.name());
		jSource.addActionListener(listener);

		jAlgorithmLabel = new JLabel(TabsRouting.get().algorithm());

		jAlgorithm = new JComboBox(RoutingAlgorithmContainer.getRegisteredList().toArray());
		jAlgorithm.setSelectedIndex(0);
		jAlgorithm.setActionCommand(RoutingAction.ALGORITHM.name());
		jAlgorithm.addActionListener(listener);

		jAlgorithmInfo = new JButton(Images.MISC_HELP.getIcon());
		jAlgorithmInfo.setActionCommand(RoutingAction.ALGORITHM_HELP.name());
		jAlgorithmInfo.addActionListener(listener);

		jProgress = new ProgressBar();
		jProgress.setValue(0);
		jProgress.setMaximum(1);
		jProgress.setMinimum(0);

		Comparator<SolarSystem> comp = new Comparator<SolarSystem>() {
			@Override
			public int compare(final SolarSystem o1, final SolarSystem o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		};

		jAvailable = new MoveJList<SolarSystem>(new EditableListModel<SolarSystem>());
		jAvailable.getEditableModel().setSortComparator(comp);
		jAvailable.addMouseListener(listener);
		jAvailable.addListSelectionListener(listener);
		jWaypoints = new MoveJList<SolarSystem>(new EditableListModel<SolarSystem>());
		jWaypoints.getEditableModel().setSortComparator(comp);
		jWaypoints.addMouseListener(listener);
		jWaypoints.addListSelectionListener(listener);
		jWaypointsRemaining = new JLabel();
		jAvailableRemaining = new JLabel();

		//Start system
		jStartLabel = new JLabel(TabsRouting.get().startSystem());
		jStart = new JTextField();
		jStart.setEditable(false);
		jStart.setFocusable(false);

		updateRemaining();

		jCalculate = new JButton(TabsRouting.get().calculate());
		jCalculate.setActionCommand(RoutingAction.CALCULATE.name());
		jCalculate.addActionListener(listener);

		jCancel = new JButton(TabsRouting.get().cancel());
		jCancel.setActionCommand(RoutingAction.CANCEL.name());
		jCancel.addActionListener(listener);
		jCancel.setEnabled(false);

		jResult = new JTextArea();
		jResult.setEditable(false);
		jResult.setFont(jPanel.getFont());

		jFullResult = new JTextArea();
		jFullResult.setEditable(false);
		jFullResult.setFont(jPanel.getFont());

		jInfo = new JTextArea();
		jInfo.setEditable(false);
		jInfo.setFont(jPanel.getFont());

		JScrollPane jAvailableScroll = new JScrollPane(jAvailable);
		JScrollPane jWaypointsScroll = new JScrollPane(jWaypoints);
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

		JTabbedPane jTabbedPane = new JTabbedPane();
		jTabbedPane.addTab(TabsRouting.get().resultTabShort(), jResultScroll);
		jTabbedPane.addTab(TabsRouting.get().resultTabFull(), jFullResultScroll);
		jTabbedPane.addTab(TabsRouting.get().resultTabInfo(), jInfoScroll);

		// widths are defined in here.
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup()
								.addComponent(jAlgorithmLabel)
								.addComponent(jSourceLabel)
							)
							.addGroup(layout.createParallelGroup()
								.addGroup(layout.createSequentialGroup()
									.addComponent(jAlgorithm)
									.addGap(10)
									.addComponent(jAlgorithmInfo)
								)
								.addComponent(jSource)
							)
						)
						.addComponent(jAvailableScroll, 200, 200, Short.MAX_VALUE)
						.addComponent(jAvailableRemaining, 200, 200, Short.MAX_VALUE)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jAdd, 80, 80, 80)
						.addComponent(jRemove, 80, 80, 80)
						.addComponent(jAddSystem, 80, 80, 80)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jStartLabel)
							.addComponent(jStart)
						)
						.addComponent(jWaypointsScroll, 200, 200, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jWaypointsRemaining, 200, 200, 200)
							.addGap(0, 0, Integer.MAX_VALUE)
							.addComponent(jCalculate, 120, 120, 120)
						)
					)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jProgress, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
					.addComponent(jCancel)
				)
				.addComponent(jTabbedPane)
			);
		// heights are defined here.
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
					.addComponent(jAlgorithmLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAlgorithm, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAlgorithmInfo, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jSourceLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSource, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStartLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStart, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jAvailableScroll, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					)
					.addComponent(jWaypointsScroll, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jAdd, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jAddSystem, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
					.addComponent(jAvailableRemaining, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jWaypointsRemaining, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCalculate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
					.addComponent(jProgress, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jTabbedPane)
				
			);
		//Only need to build the graph once
		buildGraph();
		jSystemDialog.updateData(getGraph().getNodes());
	}

	@Override
	public void clearData() {
		RoutingAlgorithm.setCache(false); //Clear cache
	}

	@Override
	public void updateData() {
		RoutingAlgorithm.setCache(true);
		//Do everything the constructor does...
		jAvailable.getEditableModel().clear();
		jWaypoints.getEditableModel().clear();
		List<SourceItem> sources = new ArrayList<SourceItem>();
		for (Entry<String, OverviewGroup> entry : Settings.get().getOverviewGroups().entrySet()) {
			sources.add(new SourceItem(entry.getKey(), true));
		}
		Collections.sort(sources);
		sources.add(0, new SourceItem(TabsRouting.get().filteredAssets()));
		sources.add(0, new SourceItem(General.get().all()));
		jSource.setModel(new DefaultComboBoxModel(sources.toArray()));
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
		
		updateRemaining();
		processFilteredAssets();
	}

	public void addSystems(int max) {
		Set<SolarSystem> allLocs = new HashSet<SolarSystem>();
		allLocs.addAll(jAvailable.getEditableModel().getAll());
		allLocs.addAll(jWaypoints.getEditableModel().getAll());
		int count = 0;
		for (Location location : StaticData.get().getLocations().values()) {
			if (count >= max) {
				break;
			}
			SolarSystem loc = findNodeForLocation(filteredGraph, location.getSystemID());
			if (loc != null) {
				boolean add = allLocs.add(loc);
				if (add) {
					count++;
				}
			} else {
				LOG.debug("ignoring {}", location);
			}
		}
		jAvailable.getEditableModel().clear();
		jWaypoints.getEditableModel().clear();
		jAvailable.getEditableModel().addAll(allLocs);
		updateRemaining();
	}

	private void updateRemaining() {
		updateWaypointsRemaining();
		updateAvailableRemaining();
		validateLists();
	}

	private void updateWaypointsRemaining() {
		int max = ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).getWaypointLimit();
		int cur = jWaypoints.getModel().getSize();
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
		int tot = cur + jWaypoints.getModel().getSize();
		jAvailableRemaining.setText(TabsRouting.get().total(cur, tot));
	}

	protected final void buildGraph() {
		// build the graph.
		// filter the solarsystems based on the settings.
		filteredGraph = new Graph();
		int count = 0;
		for (Jump jump : StaticData.get().getJumps()) { // this way we exclude the locations that are unreachable.
			count++;
			SplashUpdater.setSubProgress((int) (count * 100.0 / StaticData.get().getJumps().size()));
			SolarSystem f = null;
			SolarSystem t = null;
			for (Node n : filteredGraph.getNodes()) {
				SolarSystem s = (SolarSystem) n;
				if (s.getSystemID() == jump.getFrom().getSystemID()) {
					f = s;
				}
				if (s.getSystemID() == jump.getTo().getSystemID()) {
					t = s;
				}
			}
			if (f == null) {
				f = new SolarSystem(jump.getFrom());
			}
			if (t == null) {
				t = new SolarSystem(jump.getTo());
			}
			filteredGraph.addEdge(new Edge(f, t));
		}
	}

	protected void processFilteredAssets() {
		// select the active places.
		SortedSet<SolarSystem> allLocs = new TreeSet<SolarSystem>(new Comparator<SolarSystem>() {
			@Override
			public int compare(final SolarSystem o1, final SolarSystem o2) {
				String n1 = o1.getName();
				String n2 = o2.getName();
				return n1.compareToIgnoreCase(n2);
			}
		});
		jAvailable.getEditableModel().addAll(allLocs);
		List<Asset> assets;
		SourceItem source = (SourceItem) jSource.getSelectedItem();
		if (source.getName().equals(General.get().all())) { //ALL
			 assets = new ArrayList<Asset>(program.getAssetEventList());
		} else if (source.getName().equals(TabsRouting.get().filteredAssets())) { //FILTERS
			assets = program.getAssetsTab().getFilteredAssets();
		} else { //OVERVIEW GROUP
			assets = new ArrayList<Asset>();
			OverviewGroup group = Settings.get().getOverviewGroups().get(source.getName());
			for (OverviewLocation location : group.getLocations()) {
				for (Asset asset : program.getAssetEventList()) {
					if ((location.getName().equals(asset.getLocation().getLocation()))
						|| (location.getType() == LocationType.TYPE_SYSTEM && location.getName().equals(asset.getLocation().getSystem()))
						|| (location.getType() == LocationType.TYPE_REGION && location.getName().equals(asset.getLocation().getRegion()))
						) {
						assets.add(asset);
					}
				}
			}
		}
		for (Asset ea : assets) {
			SolarSystem loc = findNodeForLocation(filteredGraph, ea.getLocation().getSystemID());
			if (loc != null) {
				allLocs.add(loc);
			} else {
				LOG.debug("ignoring {}", ea.getLocation().getLocation());
			}
		}

		jAvailable.getEditableModel().addAll(allLocs);
		updateRemaining();
	}

	/**
	 *
	 * @param g
	 * @param locationID
	 * @return null if the system is unreachable (e.g. w-space)
	 */
	private SolarSystem findNodeForLocation(final Graph g, final long locationID) {
		if (locationID < 0) {
			throw new RuntimeException("Unknown Location:" + locationID);
		}
		for (Node n : g.getNodes()) {
			if (n instanceof SolarSystem) {
				SolarSystem ss = (SolarSystem) n;
				if (ss.getSystemID() == locationID) {
					return ss;
				}
			}
		}
		return null;
	}

	/**
	 * Moves the selectewd items in the 'from' JList to the 'to' JList.
	 *
	 * @param from
	 * @param to
	 * @param limit
	 * @return true if all the items were moved.
	 */
	private boolean move(final MoveJList<SolarSystem> from, final MoveJList<SolarSystem> to, final int limit) {
		boolean b = from.move(to, limit);
		to.requestFocusInWindow();
		updateRemaining();
		return b;
	}

	private void processRoute() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				processRouteInner();
			}
		}, TabsRouting.get().routingThread()).start();
	}

	private void processRouteInner() {
		jProgress.setValue(0);
		if (jWaypoints.getModel().getSize() <= 2) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsRouting.get().noSystems(), TabsRouting.get().noSystemsTitle(), JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		try {
			// disable the UI controls
			setUIEnabled(false);
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
			List<Node> inputWaypoints = new ArrayList<Node>(jWaypoints.getEditableModel().getAll());
			final String text = jStart.getText();
			//Move frist system to the top....
			if (!text.contains(TabsRouting.get().startEmpty())) {
				Collections.sort(inputWaypoints, new Comparator<Node>() {
					@Override
					public int compare(Node o1, Node o2) {
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
			List<Node> route = executeRouteFinding(inputWaypoints);
			RoutingAlgorithmContainer algorithm = (RoutingAlgorithmContainer) jAlgorithm.getSelectedItem();
			if (route.isEmpty()) { //Cancelled
				algorithm.resetCancelService();
				return;
			} else { //Completed!
				jProgress.setValue(jProgress.getMaximum());
			}
			//Info
			final StringBuilder infoString = new StringBuilder();
			String name = algorithm.getName();
			int time = (int) Math.floor(algorithm.getLastTimeTaken() / 1000);
			int jumps = algorithm.getLastDistance();
			infoString.append(TabsRouting.get().resultText(name, jumps, inputWaypoints.size(), time));
			//Full Route
			Node lastNode = null;
			final StringBuilder fullRouteString = new StringBuilder();
			final StringBuilder routeString = new StringBuilder();
			for (Node node : route) {
				if (lastNode != null) {
					boolean first = true;
					for (Node subNode : filteredGraph.routeBetween(lastNode, node)) {
						if (first) {
							first = false;
							fullRouteString.append(subNode.getName());
						} else {
							fullRouteString.append(TabsRouting.get().resultArrow());
							fullRouteString.append(subNode.getName());
						}
					}
					fullRouteString.append('\n');
					routeString.append('\n');
				}
				routeString.append(node.getName());
				lastNode = node;
			}
			boolean first = true;
			for (Node subNode : filteredGraph.routeBetween(lastNode, route.get(0))) {
				if (first) {
					first = false;
					fullRouteString.append(subNode.getName());
				} else {
					fullRouteString.append(TabsRouting.get().resultArrow()); //⇨→⇒
					fullRouteString.append(subNode.getName());
				}
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					jResult.setText(routeString.toString());
					jResult.setEnabled(true);
					jResult.setCaretPosition(0);
					jFullResult.setText(fullRouteString.toString());
					jFullResult.setEnabled(true);
					jFullResult.setCaretPosition(0);
					jInfo.setText(infoString.toString());
					jInfo.setEnabled(true);
				}
			});
		} catch (DisconnectedGraphException dce) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame()
							, dce.getMessage()
							, TabsRouting.get().error()
							, JOptionPane.ERROR_MESSAGE);
		} finally {
			setUIEnabled(true);
			jProgress.setValue(0);
		}
	}

	protected Graph getGraph() {
		return filteredGraph;
	}

	protected List<Node> executeRouteFinding(final List<Node> inputWaypoints) {
		List<Node> route = ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).execute(jProgress, filteredGraph, inputWaypoints);
		return route;
	}

	private void setUIEnabled(final boolean b) {
		jAdd.setEnabled(b);
		jRemove.setEnabled(b);
		jCalculate.setEnabled(b);
		jAlgorithm.setEnabled(b);
		jAvailable.setEnabled(b);
		jWaypoints.setEnabled(b);
		jWaypointsRemaining.setEnabled(b);
		jAvailableRemaining.setEnabled(b);
		jAlgorithmLabel.setEnabled(b);
		jSourceLabel.setEnabled(b);
		jSource.setEnabled(b);
		jAddSystem.setEnabled(b);
		jAlgorithmInfo.setEnabled(b);
		jStartLabel.setEnabled(b);
		if (jStart.getText().contains(TabsRouting.get().startEmpty())) {
			jStart.setEnabled(false);
		} else {
			jStart.setEnabled(b);
		}
		jCancel.setEnabled(!b);
	}

	private void cancelProcessing() {
		((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).getCancelService().cancel();
	}

	private void validateLists() {
		jRemove.setEnabled(jWaypoints.getSelectedValues().length > 0);
		jAdd.setEnabled(jAvailable.getSelectedValues().length > 0 && jWaypoints.getModel().getSize() < ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).getWaypointLimit());
		jAddSystem.setEnabled(jWaypoints.getModel().getSize() < ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).getWaypointLimit());
		if (jWaypoints.getSelectedValues().length == 1) { //Selected OK
			jStart.setText(jWaypoints.getSelectedValue().toString());
			jStart.setEnabled(true);
		} else { //Empty List
			List<? extends SolarSystem> all = jWaypoints.getEditableModel().getAll();
			if (!all.isEmpty()) {
				jStart.setText(TabsRouting.get().startEmptyAuto(all.get(0).getName()));
			} else {
				jStart.setText(TabsRouting.get().startEmpty());
			}
			jStart.setEnabled(false);
		}
	}

	private class ListenerClass extends MouseAdapter implements ActionListener, ListSelectionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			LOG.debug(e.getActionCommand());
			if (RoutingAction.ADD.name().equals(e.getActionCommand())) {
				move(jAvailable, jWaypoints, ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).getWaypointLimit());
			} else if (RoutingAction.REMOVE.name().equals(e.getActionCommand())) {
				move(jWaypoints, jAvailable, Integer.MAX_VALUE);
			} else if (RoutingAction.CALCULATE.name().equals(e.getActionCommand())) {
				if (jResult.isEnabled()) {
					int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsRouting.get().resultOverwrite(), TabsRouting.get().calculate(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (value != JOptionPane.OK_OPTION) {
						return;
					}
				}
				processRoute();
			} else if (RoutingAction.CANCEL.name().equals(e.getActionCommand())) {
				cancelProcessing();
			} else if (RoutingAction.SOURCE.name().equals(e.getActionCommand())) {
				jAvailable.getEditableModel().clear();
				jWaypoints.getEditableModel().clear();
				processFilteredAssets();
			} else if (RoutingAction.ALGORITHM.name().equals(e.getActionCommand())) {
				updateRemaining();
			} else if (RoutingAction.ALGORITHM_HELP.name().equals(e.getActionCommand())) {
				RoutingAlgorithmContainer rac = ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem());
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), rac.getBasicDescription(), rac.getName(), JOptionPane.INFORMATION_MESSAGE);
			} else if (RoutingAction.ADD_SYSTEM.name().equals(e.getActionCommand())) {
				SolarSystem system = jSystemDialog.show();
				if (system != null ) {
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
			}

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() % 2 == 0) {
				if (e.getSource().equals(jAvailable) && jAvailable.isEnabled()) {
					move(jAvailable, jWaypoints, ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).getWaypointLimit());
				} else if (e.getSource().equals(jWaypoints) && jWaypoints.isEnabled()) {
					move(jWaypoints, jAvailable, Integer.MAX_VALUE);
				}
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			validateLists();
		}
	}

	/**
	 * A GUI compatible container for the routing algorithms.
	 */
	private static class RoutingAlgorithmContainer {

		private RoutingAlgorithm contained;

		public RoutingAlgorithmContainer(final RoutingAlgorithm contained) {
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

		public List<Node> execute(final Progress progress, final Graph g, final List<? extends Node> assetLocations) {
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
			List<RoutingAlgorithmContainer> list = new ArrayList<RoutingAlgorithmContainer>();
			for (RoutingAlgorithm ra : RoutingAlgorithm.getRegisteredList()) {
				list.add(new RoutingAlgorithmContainer(ra));
			}
			return list;
		}
	}

	static class ProgressBar extends JProgressBar implements Progress {

		private static final long serialVersionUID = 1L;
	}

	static class SourceItem implements Comparable<SourceItem> {

		private String name;
		private boolean group;

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
