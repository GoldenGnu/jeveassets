/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.SplashUpdater;
import net.nikr.eve.jeveasset.data.Asset;
import net.nikr.eve.jeveasset.data.Jump;
import net.nikr.eve.jeveasset.data.OverviewGroup;
import net.nikr.eve.jeveasset.data.OverviewLocation;
import net.nikr.eve.jeveasset.data.OverviewLocation.LocationType;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.SolarSystem;
import net.nikr.eve.jeveasset.gui.dialogs.addsystem.AddSystemController;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
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

	private final static Logger LOG = LoggerFactory.getLogger(RoutingTab.class);

	public static final String ACTION_ADD = "ACTION_ADD";
	public static final String ACTION_REMOVE = "ACTION_REMOVE";
	public static final String ACTION_ADD_SYSTEM = "ACTION_ADD_SYSTEM";
	public static final String ACTION_SOURCE = "ACTION_SOURCE";
	public static final String ACTION_CALCULATE = "ACTION_CALCULATE";
	public static final String ACTION_CANCEL = "ACTION_CANCEL";
	
	private JButton jAdd;
	private JButton jRemove;
	private JButton jCalculate;
	private JButton jAddSystem;
	private JComboBox jAlgorithm;
	private JComboBox jSource;
	private JTextArea jDescription;
	private MoveJList<SolarSystem> jAvailable;
	private MoveJList<SolarSystem> jWaypoints;
	private JLabel jAvailableRemaining;
	private JLabel jWaypointsRemaining; // waypoint count
	private JLabel jSourceLabel; // waypoint count
	private JLabel jAlgorithmLabel; // waypoint count
	private ProgressBar jProgress;
	private JButton jCancel;
	private JTextArea jLastResultArea;

	protected Graph filteredGraph;
	/**
	 *
	 * @param load does nothing except change the signature.
	 */
	protected RoutingTab(boolean load) {
		super(load);
	}

	public RoutingTab(Program program) {

		super(program, TabsRouting.get().routing(), Images.TOOL_ROUTING.getIcon(), true);

		ListenerClass listener = new ListenerClass();

		jAdd = new JButton(TabsRouting.get().whitespace());
		jAdd.setActionCommand(ACTION_ADD);
		jAdd.addActionListener(listener);

		jRemove = new JButton(TabsRouting.get().whitespace1());
		jRemove.setActionCommand(ACTION_REMOVE);
		jRemove.addActionListener(listener);

		jAddSystem = new JButton(TabsRouting.get().add());
		jAddSystem.setActionCommand(ACTION_ADD_SYSTEM);
		jAddSystem.addActionListener(listener);

		jSourceLabel = new JLabel(TabsRouting.get().source());

		jSource = new JComboBox();
		jSource.setActionCommand(ACTION_SOURCE);
		jSource.addActionListener(listener);

		jAlgorithmLabel = new JLabel(TabsRouting.get().algorithm());

		jAlgorithm = new JComboBox(RoutingAlgorithmContainer.getRegisteredList().toArray());
		jAlgorithm.setSelectedIndex(0);
		jAlgorithm.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				changeAlgorithm();
			}
		});

		jProgress = new ProgressBar();
		jProgress.setValue(0);
		jProgress.setMaximum(1);
		jProgress.setMinimum(0);

		jDescription = new JTextArea();
		setAlgorithmDescriptionText(); // sets the desciption text.
		jDescription.setEditable(false);
		jDescription.setWrapStyleWord(true);
		jDescription.setLineWrap(true);
		jDescription.setFont(jAlgorithm.getFont());
		Comparator<SolarSystem> comp = new Comparator<SolarSystem>() {
			@Override
			public int compare(SolarSystem o1, SolarSystem o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};

		jAvailable = new MoveJList<SolarSystem>(new EditableListModel<SolarSystem>());
		jAvailable.getEditableModel().setSortComparator(comp);
		jWaypoints = new MoveJList<SolarSystem>(new EditableListModel<SolarSystem>());
		jWaypoints.getEditableModel().setSortComparator(comp);
		jWaypointsRemaining = new JLabel();
		jAvailableRemaining = new JLabel();
		updateRemaining();

		jCalculate = new JButton(TabsRouting.get().calculate());
		jCalculate.setActionCommand(ACTION_CALCULATE);
		jCalculate.addActionListener(listener);

		jCancel = new JButton(TabsRouting.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(listener);
		jCancel.setEnabled(false);

		jLastResultArea = new JTextArea();
		jLastResultArea.setEditable(false);

		JScrollPane descrSP = new JScrollPane(jDescription);
		jDescription.setCaretPosition(0);
		JScrollPane availSP = new JScrollPane(jAvailable);
		JScrollPane waypoSP = new JScrollPane(jWaypoints);
		JScrollPane routeSP = new JScrollPane(jLastResultArea);

		// widths are defined in here.
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jSourceLabel)
							.addComponent(jSource, 100, 100, 200)
							.addComponent(jAlgorithmLabel)
							.addComponent(jAlgorithm, 100, 100, 200)
						)
						.addComponent(jProgress, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
						.addComponent(descrSP, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addComponent(availSP, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
									.addComponent(jAvailableRemaining, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
									.addGroup(layout.createSequentialGroup()
										.addComponent(jCalculate, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
										.addComponent(jCancel, javax.swing.GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
									)
								)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addComponent(jAdd, 80, 80, 80)
									.addComponent(jRemove, 80, 80, 80)
									//.addComponent(jAddSystem, 80, 80, 80)
								)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addComponent(waypoSP, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
									.addComponent(jWaypointsRemaining, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
									.addComponent(routeSP, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
								)
							)
						)
						.addContainerGap()
					)
				);
		// heights are defined here.
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(jSourceLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jSource, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jAlgorithmLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jAlgorithm, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					)
					.addComponent(jProgress, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(descrSP, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(waypoSP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(availSP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jAdd, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
							.addComponent(jRemove, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
							//.addComponent(jAddSystem, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(jAvailableRemaining, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jWaypointsRemaining, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(jCalculate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(routeSP, GroupLayout.PREFERRED_SIZE, 100, Short.MAX_VALUE)
					)
					.addContainerGap()
				)
			);
		//Only need to build the graph once
		buildGraph(program.getSettings());
	}

	private void changeAlgorithm() {
		setAlgorithmDescriptionText();
		updateRemaining();
	}

	private void setAlgorithmDescriptionText() {
		RoutingAlgorithmContainer rac = ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem());
		jDescription.setText(TabsRouting.get().whitespace2(rac.getBasicDescription(),
				rac.getTechnicalDescription()));
		jDescription.setCaretPosition(0); //This should work
	}

	private void updateRemaining() {
		updateWaypointsRemaining();
		updateAvailableRemaining();
	}
	
	private void updateWaypointsRemaining() {
		int max = ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).getWaypointLimit();
		int cur = jWaypoints.getModel().getSize();
		if (max < cur) {
			jWaypointsRemaining.setForeground(Color.RED);
		} else {
			jWaypointsRemaining.setForeground(Color.BLACK);
		}
		jWaypointsRemaining.setText(TabsRouting.get().whitespace3(cur, max));
	}

	private void updateAvailableRemaining() {
		int cur = jAvailable.getModel().getSize();
		int tot = cur + jWaypoints.getModel().getSize();
		jAvailableRemaining.setText(TabsRouting.get().whitespace4(cur, tot));
	}

	protected final void buildGraph(Settings settings) {
		// build the graph.
		// filter the solarsystems based on the settings.
		filteredGraph = new Graph();
		int count = 0;
		for (Jump jump : settings.getJumps()) { // this way we exclude the locations that are unreachable.
			count++;
			SplashUpdater.setSubProgress((int)(count * 100 / settings.getJumps().size()));
			SolarSystem f = null;
			SolarSystem t = null;
			for (Node n : filteredGraph.getNodes()) {
				SolarSystem s = (SolarSystem)n;
				if (s.getSystemID() == jump.getFrom().getSystemID()) f = s;
				if (s.getSystemID() == jump.getTo().getSystemID()) t = s;
			}
			if (f == null) f = new SolarSystem(jump.getFrom());
			if (t == null) t = new SolarSystem(jump.getTo());
			filteredGraph.addEdge(new Edge(f, t));
		}
	}

	protected void processFilteredAssets(Settings settings) {
		// select the active places.
		SortedSet<SolarSystem> allLocs = new TreeSet<SolarSystem>(new Comparator<SolarSystem>() {
			@Override
			public int compare(SolarSystem o1, SolarSystem o2) {
				String n1 = o1.getName();
				String n2 = o2.getName();
				return n1.compareTo(n2);
			}
		});
		List<Asset> assets;
		SourceItem source = (SourceItem)jSource.getSelectedItem();
		if (source.getName().equals(TabsRouting.get().all())){ //ALL
			 assets = new ArrayList<Asset>(program.getEveAssetEventList());
		} else if (source.getName().equals(TabsRouting.get().filteredAssets())) { //FILTERS
			assets = program.getAssetsTab().getFilteredAssets();
		} else { //OVERVIEW GROUP
			assets = new ArrayList<Asset>();
			OverviewGroup group = program.getSettings().getOverviewGroups().get(source.getName());
			for (OverviewLocation location : group.getLocations()){
				for (Asset eveAsset : program.getEveAssetEventList()){
					if ((location.getName().equals(eveAsset.getLocation()))
						|| (location.getType() == LocationType.TYPE_SYSTEM && location.getName().equals(eveAsset.getSystem()))
						|| (location.getType() == LocationType.TYPE_REGION && location.getName().equals(eveAsset.getRegion()))
						){
						assets.add(eveAsset);
					}
				}
			}
			
		}
		for (Asset ea : assets) {
			SolarSystem loc = findNodeForLocation(filteredGraph, ea.getSolarSystemID());
			if (loc != null) {
				allLocs.add(loc);
			} else {
				LOG.debug("ignoring {}", ea.getLocation());
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
	private SolarSystem findNodeForLocation(Graph g, long locationID) {
		if (locationID < 0) {
			throw new RuntimeException(TabsRouting.get().unknown(locationID));
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
		//throw new RuntimeException("Unknown Location: " + locationID + ", ssid = " + ssid);
	}

	/**
	 * Moves the selectewd items in the 'from' JList to the 'to' JList.
	 *
	 * @param from
	 * @param to
	 * @param limit
	 * @return true if all the items were moved.
	 */
	private boolean move(MoveJList<SolarSystem> from, MoveJList<SolarSystem> to, int limit) {
		boolean b = from.move(to, limit);
		updateRemaining();
		return b;
	}

	private void processRoute() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				processRouteInner();
			}
		}, TabsRouting.get().route()).start();
	}

	private void processRouteInner() {
		jProgress.setValue(0);
		if (jWaypoints.getModel().getSize() <= 2) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsRouting.get().there(), TabsRouting.get().not(), JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		try {
			// disable the UI controls
			setUIEnabled(false);
			List<Node> inputWaypoints = new ArrayList<Node>(jWaypoints.getEditableModel().getAll());

			List<Node> route = executeRouteFinding(inputWaypoints);

			StringBuilder sb = new StringBuilder();
			for (Node ss : route) {
				sb.append(ss.getName());
				sb.append('\n');
			}
			int time = (int)Math.floor(((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).getLastTimeTaken() / 1000);
			sb.append(TabsRouting.get().generating());
			sb.append(TabsRouting.get().second(time));
			sb.append(TabsRouting.get().whitespace5());

			JOptionPane.showMessageDialog(program.getMainWindow().getFrame()
							, TabsRouting
									.get()
									.a(((RoutingAlgorithmContainer) jAlgorithm
											.getSelectedItem())
											.getLastDistance(),
											(int) Math
													.floor(((RoutingAlgorithmContainer) jAlgorithm
															.getSelectedItem())
															.getLastTimeTaken() / 1000))
							, TabsRouting.get().route1()
							, JOptionPane.INFORMATION_MESSAGE);

			jLastResultArea.setText(sb.toString());
			jLastResultArea.setEnabled(true);

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

	protected List<Node> executeRouteFinding(List<Node> inputWaypoints) {
		List<Node> route = ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).execute(jProgress, filteredGraph, inputWaypoints);
		return route;
	}

	private void setUIEnabled(boolean b) {
		jAdd.setEnabled(b);
		jRemove.setEnabled(b);
		jCalculate.setEnabled(b);
		jAlgorithm.setEnabled(b);
		jDescription.setEnabled(b);
		jAvailable.setEnabled(b);
		jWaypoints.setEnabled(b);
		jWaypointsRemaining.setEnabled(b);
		jAvailableRemaining.setEnabled(b);
		jAlgorithmLabel.setEnabled(b);
		jSourceLabel.setEnabled(b);
		jSource.setEnabled(b);
		jAddSystem.setEnabled(b);
		jCancel.setEnabled(!b);
	}

	//FIXME cancel processing in the routing tool doesn't work very well
	private void cancelProcessing() {
		((RoutingAlgorithmContainer)jAlgorithm.getSelectedItem()).getCancelService().cancel();
	}

	@Override
	public void updateTableMenu(JComponent jComponent){
		jComponent.removeAll();
		jComponent.setEnabled(false);
	}

	private class ListenerClass implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			LOG.debug(e.getActionCommand());
			if (ACTION_ADD.equals(e.getActionCommand())) {
				move(jAvailable, jWaypoints, ((RoutingAlgorithmContainer) jAlgorithm.getSelectedItem()).getWaypointLimit());
			} else if (ACTION_REMOVE.equals(e.getActionCommand())) {
				move(jWaypoints, jAvailable, Integer.MAX_VALUE);
			} else if (ACTION_CALCULATE.equals(e.getActionCommand())) {
				processRoute();
			} else if (ACTION_CANCEL.equals(e.getActionCommand())) {
				cancelProcessing();
			} else if (ACTION_SOURCE.equals(e.getActionCommand())) {
				jAvailable.getEditableModel().clear();
				jWaypoints.getEditableModel().clear();
				processFilteredAssets(program.getSettings());
			} else if (ACTION_ADD_SYSTEM.equals(e.getActionCommand())) {
				//jAddSystem
				AddSystemController system = new AddSystemController(program);
			}

		}
	}

	@Override
	public void updateData() {
		//Do everything the constructor does...
		jAvailable.getEditableModel().clear();
		jWaypoints.getEditableModel().clear();
		List<SourceItem> sources = new ArrayList<SourceItem>();
		for (Entry<String, OverviewGroup> entry : program.getSettings().getOverviewGroups().entrySet()){
			sources.add(new SourceItem(entry.getKey(), true));
		}
		Collections.sort(sources);
		sources.add(0, new SourceItem(TabsRouting.get().filteredAssets()));
		sources.add(0, new SourceItem(TabsRouting.get().all()));
		jSource.setModel( new DefaultComboBoxModel(sources.toArray()) );
		jAlgorithm.setSelectedIndex(0);
		jLastResultArea.setText(TabsRouting.get().once());
		jLastResultArea.setCaretPosition(0);
		jLastResultArea.setEnabled(false);
		updateRemaining();
		jCancel.setEnabled(false);
		processFilteredAssets(program.getSettings());
	}

	@Override
	protected void showTablePopupMenu(MouseEvent e) {}

	/**
	 * A GUI compatible container for the routing algorithms.
	 */
	private static class RoutingAlgorithmContainer {

		RoutingAlgorithm contained;

		public RoutingAlgorithmContainer(RoutingAlgorithm contained) {
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

		public List<Node> execute(Progress progress, Graph g, List<? extends Node> assetLocations) {
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

	class DummyProgress implements Progress {

		@Override
		public int getMaximum() {
			return 0;
		}

		@Override
		public void setMaximum(int maximum) {
		}

		@Override
		public int getMinimum() {
			return 0;
		}

		@Override
		public void setMinimum(int minimum) {
		}

		@Override
		public int getValue() {
			return 0;
		}

		@Override
		public void setValue(int value) {
		}
	}

	class ProgressBar extends JProgressBar implements Progress {

		private static final long serialVersionUID = 1l;
	}

	class SourceItem implements Comparable<SourceItem> {

		private String name;
		private boolean group;

		public SourceItem(String name) {
			this.name = name;
			this.group = false;
		}

		public SourceItem(String name, boolean group) {
			this.name = name;
			this.group = group;
		}

		@Override
		public String toString(){
			if (group){
				return TabsRouting.get().overviewGroup(name);
			} else {
				return name;
			}
		}

		public String getName() {
			return name;
		}

		@Override
		public int compareTo(SourceItem o) {
			return this.getName().compareTo(o.getName());
		}
	}
}
