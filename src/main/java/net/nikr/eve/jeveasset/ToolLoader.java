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
import net.nikr.eve.jeveasset.gui.tabs.contracts.ContractsTab;
import net.nikr.eve.jeveasset.gui.tabs.items.ItemsTab;
import net.nikr.eve.jeveasset.gui.tabs.jobs.IndustryJobsTab;
import net.nikr.eve.jeveasset.gui.tabs.journal.JournalTab;
import net.nikr.eve.jeveasset.gui.tabs.loadout.LoadoutsTab;
import net.nikr.eve.jeveasset.gui.tabs.materials.MaterialsTab;
import net.nikr.eve.jeveasset.gui.tabs.mining.ExtractionsTab;
import net.nikr.eve.jeveasset.gui.tabs.mining.MiningGraphTab;
import net.nikr.eve.jeveasset.gui.tabs.mining.MiningTab;
import net.nikr.eve.jeveasset.gui.tabs.orders.MarketOrdersTab;
import net.nikr.eve.jeveasset.gui.tabs.overview.OverviewTab;
import net.nikr.eve.jeveasset.gui.tabs.prices.PriceChangesTab;
import net.nikr.eve.jeveasset.gui.tabs.prices.PriceHistoryTab;
import net.nikr.eve.jeveasset.gui.tabs.reprocessed.ReprocessedTab;
import net.nikr.eve.jeveasset.gui.tabs.routing.RoutingTab;
import net.nikr.eve.jeveasset.gui.tabs.skills.SkillsTab;
import net.nikr.eve.jeveasset.gui.tabs.slots.SlotsTab;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileTab;
import net.nikr.eve.jeveasset.gui.tabs.tracker.TrackerTab;
import net.nikr.eve.jeveasset.gui.tabs.transaction.TransactionTab;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueRetroTab;
import net.nikr.eve.jeveasset.gui.tabs.values.ValueTableTab;
import net.nikr.eve.jeveasset.i18n.TabsContracts;
import net.nikr.eve.jeveasset.i18n.TabsItems;
import net.nikr.eve.jeveasset.i18n.TabsJobs;
import net.nikr.eve.jeveasset.i18n.TabsJournal;
import net.nikr.eve.jeveasset.i18n.TabsLoadout;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;
import net.nikr.eve.jeveasset.i18n.TabsMining;
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
	
	public static final ToolTab<ValueRetroTab> VALUE = new ToolTab<>(TabsValues.get().oldTitle(), ValueRetroTab.NAME, new ToolOpen<ValueRetroTab>() {
										@Override
										public ValueRetroTab getTool(Program program, boolean init) {
											return program.getValueTab(init);
										}
									});
	public static final ToolTab<ValueTableTab> ISK = new ToolTab<>(TabsValues.get().title(), ValueTableTab.NAME, new ToolOpen<ValueTableTab>() {
										@Override
										public ValueTableTab getTool(Program program, boolean init) {
											return program.getIskTab(init);
										}
									});
	public static final ToolTab<PriceHistoryTab> PRICE_HISTORY = new ToolTab<>(TabsPriceHistory.get().title(), PriceHistoryTab.NAME, new ToolOpen<PriceHistoryTab>() {
										@Override
										public PriceHistoryTab getTool(Program program, boolean init) {
											return program.getPriceHistoryTab(init);
										}
									});
	public static final ToolTab<PriceChangesTab> PRICE_CHANGES = new ToolTab<>(TabsPriceChanges.get().title(), PriceChangesTab.NAME, new ToolOpen<PriceChangesTab>() {
										@Override
										public PriceChangesTab getTool(Program program, boolean init) {
											return program.getPriceChangesTab(init);
										}
									});
	public static final ToolTab<MaterialsTab> MATERIALS = new ToolTab<>(TabsMaterials.get().materials(), MaterialsTab.NAME, new ToolOpen<MaterialsTab>() {
										@Override
										public MaterialsTab getTool(Program program, boolean init) {
											return program.getMaterialsTab(init);
										}
									});
	public static final ToolTab<LoadoutsTab> LOADOUTS = new ToolTab<>(TabsLoadout.get().ship(), LoadoutsTab.NAME, new ToolOpen<LoadoutsTab>() {
										@Override
										public LoadoutsTab getTool(Program program, boolean init) {
											return program.getLoadoutsTab(init);
										}
									});
	public static final ToolTab<MarketOrdersTab> MARKET_ORDERS = new ToolTab<>(TabsOrders.get().market(), MarketOrdersTab.NAME, new ToolOpen<MarketOrdersTab>() {
										@Override
										public MarketOrdersTab getTool(Program program, boolean init) {
											return program.getMarketOrdersTab(init);
										}
									});
	public static final ToolTab<JournalTab> JOURNAL = new ToolTab<>(TabsJournal.get().title(), JournalTab.NAME, new ToolOpen<JournalTab>() {
										@Override
										public JournalTab getTool(Program program, boolean init) {
											return program.getJournalTab(init);
										}
									});
	public static final ToolTab<TransactionTab> TRANSACTION = new ToolTab<>(TabsTransaction.get().title(), TransactionTab.NAME, new ToolOpen<TransactionTab>() {
										@Override
										public TransactionTab getTool(Program program, boolean init) {
											return program.getTransactionsTab(init);
										}
									});
	public static final ToolTab<IndustryJobsTab> INDUSTRY_JOBS = new ToolTab<>(TabsJobs.get().industry(), IndustryJobsTab.NAME, new ToolOpen<IndustryJobsTab>() {
										@Override
										public IndustryJobsTab getTool(Program program, boolean init) {
											return program.getIndustryJobsTab(init);
										}
									});
	public static final ToolTab<SlotsTab> INDUSTRY_SLOTS = new ToolTab<>(TabsSlots.get().title(), SlotsTab.NAME, new ToolOpen<SlotsTab>() {
										@Override
										public SlotsTab getTool(Program program, boolean init) {
											return program.getSlotsTab(init);
										}
									});
	public static final ToolTab<OverviewTab> OVERVIEW = new ToolTab<>(TabsOverview.get().overview(), OverviewTab.NAME, new ToolOpen<OverviewTab>() {
										@Override
										public OverviewTab getTool(Program program, boolean init) {
											return program.getOverviewTab(init);
										}
									});
	public static final ToolTab<RoutingTab> ROUTING = new ToolTab<>(TabsRouting.get().routingTitle(), RoutingTab.NAME, new ToolOpen<RoutingTab>() {
										@Override
										public RoutingTab getTool(Program program, boolean init) {
											return program.getRoutingTab(init);
										}
									});
	public static final ToolTab<StockpileTab> STOCKPILE = new ToolTab<>(TabsStockpile.get().stockpile(), StockpileTab.NAME, new ToolOpen<StockpileTab>() {
										@Override
										public StockpileTab getTool(Program program, boolean init) {
											return program.getStockpileTab(init);
										}
									});
	public static final ToolTab<ItemsTab> ITEMS = new ToolTab<>(TabsItems.get().items(), ItemsTab.NAME, new ToolOpen<ItemsTab>() {
										@Override
										public ItemsTab getTool(Program program, boolean init) {
											return program.getItemsTab(init);
										}
									});
	public static final ToolTab<TrackerTab> TRACKER = new ToolTab<>(TabsTracker.get().title(), TrackerTab.NAME, new ToolOpen<TrackerTab>() {
										@Override
										public TrackerTab getTool(Program program, boolean init) {
											return program.getTrackerTab(init);
										}
									});
	public static final ToolTab<ReprocessedTab> REPROCESSED = new ToolTab<>(TabsReprocessed.get().title(), ReprocessedTab.NAME, new ToolOpen<ReprocessedTab>() {
										@Override
										public ReprocessedTab getTool(Program program, boolean init) {
											return program.getReprocessedTab(init);
										}
									});
	public static final ToolTab<ContractsTab> CONTRACTS = new ToolTab<>(TabsContracts.get().title(), ContractsTab.NAME, new ToolOpen<ContractsTab>() {
										@Override
										public ContractsTab getTool(Program program, boolean init) {
											return program.getContractsTab(init);
										}
									});
	public static final ToolTab<TreeTab> TREE = new ToolTab<>(TabsTree.get().title(), TreeTab.NAME, new ToolOpen<TreeTab>() {
										@Override
										public TreeTab getTool(Program program, boolean init) {
											return program.getTreeTab(init);
										}
									});
	public static final ToolTab<SkillsTab> SKILLS = new ToolTab<>(TabsSkills.get().skills(), SkillsTab.NAME, new ToolOpen<SkillsTab>() {
										@Override
										public SkillsTab getTool(Program program, boolean init) {
											return program.getSkillsTab(init);
										}
									});
	public static final ToolTab<MiningTab> MINING_LOG = new ToolTab<>(TabsMining.get().miningLog(), MiningTab.NAME, new ToolOpen<MiningTab>() {
										@Override
										public MiningTab getTool(Program program, boolean init) {
											return program.getMiningTab(init);
										}
									});
	public static final ToolTab<MiningGraphTab> MINING_GRAPH = new ToolTab<>(TabsMining.get().miningGraph(), MiningGraphTab.NAME, new ToolOpen<MiningGraphTab>() {
										@Override
										public MiningGraphTab getTool(Program program, boolean init) {
											return program.getMiningGraphTab(init);
										}
									});
	public static final ToolTab<ExtractionsTab> EXTRACTIONS = new ToolTab<>(TabsMining.get().extractions(), ExtractionsTab.NAME, new ToolOpen<ExtractionsTab>() {
										@Override
										public ExtractionsTab getTool(Program program, boolean init) {
											return program.getExtractionsTab(init);
										}
									});
	private static final int IDLE_DELAY_MS = 10000;
	private static ToolLoader loader;
	private static double max;
	private static Map<String, ToolTab<?>> toolTabs;
	private final Object SYNC_LOCK = new Object();
	private final Program program;
	private final Set<String> tools = new HashSet<>();
	private boolean ok = false;
	private Timer timer;
	private String tool = null;
	private Progress progress = null;
	private Component lastFocusOwner;

	public static synchronized Map<String, ToolTab<?>> getTools(Program program) {
		if (toolTabs == null) {
			toolTabs = new HashMap<>();
			add(VALUE);
			add(ISK);
			add(PRICE_HISTORY);
			add(PRICE_CHANGES);
			add(MATERIALS);
			add(LOADOUTS);
			add(MARKET_ORDERS);
			add(JOURNAL);
			add(TRANSACTION);
			add(INDUSTRY_JOBS);
			add(INDUSTRY_SLOTS);
			add(OVERVIEW);
			add(ROUTING);
			add(STOCKPILE);
			add(ITEMS);
			add(TRACKER);
			add(REPROCESSED);
			add(CONTRACTS);
			add(TREE);
			add(SKILLS);
			add(MINING_LOG);
			add(MINING_GRAPH);
			add(EXTRACTIONS);
		}
		return toolTabs;
	}

	public static void openTool(Program program, String title) {
		ToolTab<?> tool = getTools(program).get(title);
		if (tool != null) {
			program.getMainWindow().addTab(tool.getTool(program, true));
		}
	}

	public static void initTool(Program program, String title) {
		ToolTab<?> tool = getTools(program).get(title);
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
		Collection<ToolTab<?>> values = getTools(program).values();
		int index = 0;
		int size = values.size();
		for (ToolTab<?> tool : values) {
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

	private static <T extends JMainTab> void add(ToolTab<T> toolTab) {
		toolTabs.put(toolTab.getTitle(), toolTab);
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

	public void openToolInner() {
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
		lastFocusOwner.removeKeyListener(this);
		//lastFocusOwner.removeMouseWheelListener(this);
		lastFocusOwner.removeMouseListener(this);
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
		//lastFocusOwner.addMouseWheelListener(this);
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

	public static class ToolTab<T extends JMainTab> {
		private final String title;
		private final String name;
		private final ToolOpen<T> toolOpen;

		public ToolTab(String title, String name, ToolOpen<T> toolOpen) {
			this.title = title;
			this.name = name;
			this.toolOpen = toolOpen;
		}

		public String getTitle() {
			return title;
		}

		public String getName() {
			return name;
		}

		public T getTool(Program program, boolean init) {
			return toolOpen.getTool(program, init);
		}
	}

	public static interface ToolOpen<T extends JMainTab> {
		public T getTool(Program program, boolean init);
	}

}
