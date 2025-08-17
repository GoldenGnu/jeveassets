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
package net.nikr.eve.jeveasset;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.Progress;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.tabs.agents.AgentsTab;
import net.nikr.eve.jeveasset.gui.tabs.agents.AgentsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTab;
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.items.ItemTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.items.ItemsTab;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobsTab;
import net.nikr.eve.jeveasset.gui.tabs.journal.JournalTab;
import net.nikr.eve.jeveasset.gui.tabs.journal.JournalTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutsTab;
import net.nikr.eve.jeveasset.gui.tabs.loyalty.LoyaltyPointsTab;
import net.nikr.eve.jeveasset.gui.tabs.loyalty.LoyaltyPointsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialsTab;
import net.nikr.eve.jeveasset.gui.tabs.mining.ExtractionsTab;
import net.nikr.eve.jeveasset.gui.tabs.mining.ExtractionsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.mining.MiningGraphTab;
import net.nikr.eve.jeveasset.gui.tabs.mining.MiningTab;
import net.nikr.eve.jeveasset.gui.tabs.mining.MiningTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.prices.PriceChangesTab;
import net.nikr.eve.jeveasset.gui.tabs.prices.PriceChangesTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.prices.PriceHistoryTab;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedTab;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.routing.RoutingTab;
import net.nikr.eve.jeveasset.gui.tabs.skills.SkillsTab;
import net.nikr.eve.jeveasset.gui.tabs.skills.SkillsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.slots.SlotsTab;
import net.nikr.eve.jeveasset.gui.tabs.slots.SlotsTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.standing.NpcStandingTab;
import net.nikr.eve.jeveasset.gui.tabs.standing.NpcStandingTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileExtendedTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTab;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerTab;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTab;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueRetroTab;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableFormat;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableTab;
import net.nikr.eve.jeveasset.i18n.TabsAgents;
import net.nikr.eve.jeveasset.i18n.TabsAssets;
import net.nikr.eve.jeveasset.i18n.TabsContracts;
import net.nikr.eve.jeveasset.i18n.TabsItems;
import net.nikr.eve.jeveasset.i18n.TabsJobs;
import net.nikr.eve.jeveasset.i18n.TabsJournal;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;
import net.nikr.eve.jeveasset.i18n.TabsLoyaltyPoints;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;
import net.nikr.eve.jeveasset.i18n.TabsMining;
import net.nikr.eve.jeveasset.i18n.TabsNpcStanding;
import net.nikr.eve.jeveasset.i18n.TabsOrders;
import net.nikr.eve.jeveasset.i18n.TabsOverview;
import net.nikr.eve.jeveasset.i18n.TabsPriceChanges;
import net.nikr.eve.jeveasset.i18n.TabsPriceHistory;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;
import net.nikr.eve.jeveasset.i18n.TabsRouting;
import net.nikr.eve.jeveasset.i18n.TabsSkills;
import net.nikr.eve.jeveasset.i18n.TabsSlots;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.i18n.TabsTracker;
import net.nikr.eve.jeveasset.i18n.TabsTransaction;
import net.nikr.eve.jeveasset.i18n.TabsTree;
import net.nikr.eve.jeveasset.i18n.TabsValues;


public class ToolLoader implements ActionListener, KeyListener, MouseListener, MouseWheelListener, AWTEventListener, PropertyChangeListener {

	public static enum ToolTab {
		ASSETS(TabsAssets.get().assets(), AssetsTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getAssetsTab();
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return AssetTableFormat.valueOf(column);
			}
		},
		VALUE(TabsValues.get().oldTitle(), ValueRetroTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getValueTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return null;
			}
		},
		ISK(TabsValues.get().title(), ValueTableTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getIskTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return ValueTableFormat.valueOf(column);
			}
		},
		PRICE_HISTORY(TabsPriceHistory.get().title(), PriceHistoryTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getPriceHistoryTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return null;
			}
		},
		PRICE_CHANGES(TabsPriceChanges.get().title(), PriceChangesTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getPriceChangesTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return PriceChangesTableFormat.valueOf(column);
			}
		},
		MATERIALS(TabsMaterials.get().materials(), MaterialsTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getMaterialsTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				try {
					return MaterialExtendedTableFormat.valueOf(column);
				} catch (IllegalArgumentException ex) {
					return MaterialTableFormat.valueOf(column);
				}
			}
		},
		LOADOUTS(TabsLoadout.get().ship(), LoadoutsTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getLoadoutsTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				try {
					return LoadoutExtendedTableFormat.valueOf(column);
				} catch (IllegalArgumentException ex) {
					return LoadoutTableFormat.valueOf(column);
				}
			}
		},
		MARKET_ORDERS(TabsOrders.get().market(), MarketOrdersTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getMarketOrdersTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return MarketTableFormat.valueOf(column);
			}
		},
		JOURNAL(TabsJournal.get().title(), JournalTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getJournalTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return JournalTableFormat.valueOf(column);
			}
		},
		TRANSACTION(TabsTransaction.get().title(), TransactionTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getTransactionsTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return TransactionTableFormat.valueOf(column);
			}
		},
		INDUSTRY_JOBS(TabsJobs.get().industry(), IndustryJobsTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getIndustryJobsTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return IndustryJobTableFormat.valueOf(column);
			}
		},
		INDUSTRY_SLOTS(TabsSlots.get().title(), SlotsTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getSlotsTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return SlotsTableFormat.valueOf(column);
			}
		},
		OVERVIEW(TabsOverview.get().overview(), OverviewTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getOverviewTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return OverviewTableFormat.valueOf(column);
			}
		},
		ROUTING(TabsRouting.get().routingTitle(), RoutingTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getRoutingTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return null;
			}
		},
		STOCKPILE(TabsStockpile.get().stockpile(), StockpileTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getStockpileTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				try {
					return StockpileExtendedTableFormat.valueOf(column);
				} catch (IllegalArgumentException exception) {
					return StockpileTableFormat.valueOf(column);
				}
			}
		},
		ITEMS(TabsItems.get().items(), ItemsTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getItemsTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return ItemTableFormat.valueOf(column);
			}
		},
		TRACKER(TabsTracker.get().title(), TrackerTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getTrackerTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return null;
			}
		},
		REPROCESSED(TabsReprocessed.get().title(), ReprocessedTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getReprocessedTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				try {
					return ReprocessedExtendedTableFormat.valueOf(column);
				} catch (IllegalArgumentException ex) {
					return ReprocessedTableFormat.valueOf(column);
				}
			}
		},
		CONTRACTS(TabsContracts.get().title(), ContractsTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getContractsTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return ContractsTableFormat.valueOf(column);
			}
		},
		TREE(TabsTree.get().title(), TreeTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getTreeTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return TreeTableFormat.valueOf(column);
			}
		},
		SKILLS(TabsSkills.get().skills(), SkillsTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getSkillsTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return SkillsTableFormat.valueOf(column);
			}
		},
		MINING_LOG(TabsMining.get().miningLog(), MiningTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getMiningTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return MiningTableFormat.valueOf(column);
			}
		},
		MINING_GRAPH(TabsMining.get().miningGraph(), MiningGraphTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getMiningGraphTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return null;
			}
		},
		EXTRACTIONS(TabsMining.get().extractions(), ExtractionsTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getExtractionsTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return ExtractionsTableFormat.valueOf(column);
			}
		},
		LOYALTY_POINTS(TabsLoyaltyPoints.get().loyaltyPoints(), LoyaltyPointsTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getLoyaltyPointsTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return LoyaltyPointsTableFormat.valueOf(column);
			}
		},
		NPC_STANDING(TabsNpcStanding.get().npcStanding(), NpcStandingTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getNpcStandingTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return NpcStandingTableFormat.valueOf(column);
			}
		},
		AGENTS(TabsAgents.get().agents(), AgentsTab.NAME){
			@Override
			public JMainTab getTool(Program program, boolean init) {
				return program.getAgentsTab(init);
			}

			@Override
			public EnumTableColumn<?> getColumn(String column) throws IllegalArgumentException {
				return AgentsTableFormat.valueOf(column);
			}
		};

		private final String title;
		private final String name;

		private ToolTab(String title, String name) {
			this.title = title;
			this.name = name;
		}

		public String getTitle() {
			return title;
		}

		public String getName() {
			return name;
		}

		public abstract JMainTab getTool(Program program, boolean init);
		public abstract EnumTableColumn<?> getColumn(final String column) throws IllegalArgumentException;

		public EnumTableColumn<?> getColumn(final String column, final String toolName) {
			if (toolName.equals(getName())) {
				try {
					return getColumn(column);
				} catch (IllegalArgumentException exception) {
					
				}
			}
			return null;
		}
	}

	private static final int IDLE_DELAY_MS = 10000;

	private static ToolLoader loader;
	private static double max;
	private static Map<String, ToolTab> toolTabs;
	private final Object SYNC_LOCK = new Object();
	private final Program program;
	private final Set<String> tools = new HashSet<>();
	private boolean ok = false;
	private Timer timer;
	private String tool = null;
	private Progress progress = null;
	private Component lastFocusOwner;

	private ToolLoader(Program program) {
		this.program = program;
		tools.addAll(getToolTitles(program));
		tools.removeAll(Settings.get().getShowTools());
		if (tools.isEmpty()) {
			stopBackgroundToolLoading();
			return;
		}
		timer = new Timer(IDLE_DELAY_MS, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok = true;
				openTool();
			}
		});
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_MOTION_EVENT_MASK);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("permanentFocusOwner", this);
		timer.start();
	}

	public static synchronized Map<String, ToolTab> getTools(Program program) {
		if (toolTabs == null) {
			toolTabs = new HashMap<>();
			for (ToolTab toolTab : ToolTab.values()) {
				toolTabs.put(toolTab.getTitle(), toolTab);
			}
		}
		return toolTabs;
	}

	private static void openTool(Program program, String title) {
		ToolTab tool = getTools(program).get(title);
		if (tool != null) {
			program.getMainWindow().addTab(tool.getTool(program, true));
		}
	}

	private static void initTool(Program program, String title) {
		ToolTab tool = getTools(program).get(title);
		if (tool != null) {
			tool.getTool(program, true);
		}
	}

	public static void initTools(Program program, Collection<String> titles) {
		int index = 0;
		int size = titles.size();
		for (String title : titles) {
			initTool(program, title);
			index++;
			setProgress(index, size);
		}
	}

	public static void initAllTools(Program program) {
		SplashUpdater.setProgress(52);
		Collection<ToolTab> values = getTools(program).values();
		int index = 0;
		int size = values.size();
		for (ToolTab tool : values) {
			tool.getTool(program, true);
			index++;
			setProgress(index, size);
		}
	}

	private static void setProgress(final float done, final float end) {
		setProgress(done, end, 53, 84);
	}

	private static  void setProgress(final float done, final float end, final int minimum, final int maximum) {
		int progress = Math.round(((done / end) * (maximum - minimum)) + minimum);
		if (progress > 100) {
			progress = 100;
		} else if (progress < 0) {
			progress = 0;
		}
		SplashUpdater.setProgress(progress);
	}

	public static void openTools(Program program, Collection<String> titles) {
		for (String title : titles) {
			openTool(program, title);
		}
	}

	public static HashSet<String> getToolTitles(Program program) {
		return new HashSet<>(getTools(program).keySet());
	}

	public static synchronized void init(Program program) {
		Set<String> tools = new HashSet<>();
		tools.addAll(getToolTitles(program));
		tools.removeAll(Settings.get().getShowTools());
		max = tools.size();
	}

	public static synchronized void startBackgroundToolLoading(Program program) {
		if (loader == null) {
			loader = new ToolLoader(program);
		}
	}

	public static synchronized void stopBackgroundToolLoading() {
		if (loader != null) {
			loader.done();
			loader = null;
		}
	}

	private void openTool() {
		Window currentWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
		boolean window = currentWindow == null || currentWindow.equals(program.getMainWindow().getFrame());
		if (ok && window && tool == null && !tools.isEmpty()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					openToolInner();
				}
			});
		} else {
			synchronized (SYNC_LOCK) {
				if (timer != null) {
					timer.start();
				}
			}
		}
	}

	private void openToolInner() {
		synchronized (SYNC_LOCK) {
			if (timer != null) {
				timer.stop();
			}
		}
		addProgress();
		tool = tools.iterator().next();
		tools.remove(tool);
		initTool(program, tool);
		tool = null;
		progress.setValue((int)((max -  tools.size()) / max * 100.0));
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				openTool();
			}
		});
		if (tools.isEmpty()) {
			stopBackgroundToolLoading();
			removeProgress();
		}
	}

	private void addProgress() {
		if (progress == null) {
			progress = program.getStatusPanel().addProgress(StatusPanel.UpdateType.TOOLS, new StatusPanel.ProgressControl() {
				@Override
				public boolean isAuto() {
					return true;
				}
				@Override
				public void show() { }

				@Override
				public void cancel() { }

				@Override
				public void setPause(boolean pause) { }
			});
			progress.setVisible(true);
		}
	}

	private void removeProgress() {
		program.getStatusPanel().removeProgress(progress);
		progress = null;
	}

	private void done() {
		removeProgress();
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner", this);
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		removeListeners();
		synchronized (SYNC_LOCK) {
			timer.stop();
			timer = null;
		}
	}

	private void updateListeners() {
		Component currentFocusOwner = program.getMainWindow().getFrame().getFocusOwner();
		if (lastFocusOwner != null && (currentFocusOwner == null || !lastFocusOwner.equals(currentFocusOwner))) {
			removeListeners();
		}
		if (currentFocusOwner != null && !tools.isEmpty()) {
			addListeners();
		}
	}

	private void removeListeners() {
		if (lastFocusOwner != null) {
			lastFocusOwner.removeKeyListener(this);
			lastFocusOwner.removeMouseListener(this);
		}
		if (lastFocusOwner instanceof JComboBox) {
			((JComboBox)lastFocusOwner).removeActionListener(this);
		}
		if (lastFocusOwner instanceof AbstractButton) {
			((AbstractButton)lastFocusOwner).removeActionListener(this);
		}
	}

	private void addListeners() {
		Component currentFocusOwner = program.getMainWindow().getFrame().getFocusOwner();
		lastFocusOwner = currentFocusOwner;
		lastFocusOwner.addMouseListener(this);
		lastFocusOwner.addKeyListener(this);
		if (lastFocusOwner instanceof JComboBox) {
			((JComboBox)lastFocusOwner).addActionListener(this);
		}
		if (lastFocusOwner instanceof AbstractButton) {
			((AbstractButton)lastFocusOwner).addActionListener(this);
		}
	}

	private void restart() {
		synchronized (SYNC_LOCK) {
			if (timer != null) {
				timer.stop();
				timer.start();
			}
		}
		removeProgress();
		ok = false;
	}

	@Override
	public void eventDispatched(AWTEvent event) { restart(); }
	@Override public void mouseWheelMoved(MouseWheelEvent e) { restart(); }
	@Override public void mouseClicked(MouseEvent e) { restart(); }
	@Override public void mousePressed(MouseEvent e) { restart(); }
	@Override public void mouseReleased(MouseEvent e) { restart(); }
	@Override public void mouseEntered(MouseEvent e) { }
	@Override public void mouseExited(MouseEvent e) { }
	@Override public void actionPerformed(final ActionEvent e) { restart(); }
	@Override public void keyTyped(KeyEvent e) { restart(); }
	@Override public void keyPressed(KeyEvent e) { restart(); }
	@Override public void keyReleased(KeyEvent e) { restart(); }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		updateListeners();
	}
}
