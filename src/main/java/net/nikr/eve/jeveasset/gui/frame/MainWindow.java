/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.frame;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;
import net.nikr.eve.jeveasset.CliOptions;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.TextManager;
import net.nikr.eve.jeveasset.gui.shared.components.JDragTabbedPane;
import net.nikr.eve.jeveasset.gui.shared.components.JDragTabbedPane.TabMoveListener;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow.LockWorkerAdaptor;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.i18n.GuiFrame;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainWindow {

	private static final Logger LOG = LoggerFactory.getLogger(MainWindow.class);

	//GUI
	private final MainMenu mainMenu;
	private final JFrame jFrame;
	private final JDragTabbedPane jTabbedPane;
	private final StatusPanel statusPanel;
	private final JLockWindow jLockWindow;

	private final Timer timer;

	//Data
	private final Program program;
	private final List<JMainTab> tabs = new ArrayList<>();
	private final List<JMainTab> closed = new ArrayList<>();

	public MainWindow(final Program program) {
		this.program = program;

		ListenerClass listener = new ListenerClass();

		timer = new Timer(1000, listener);

		//Frame
		jFrame = new JFrame();
		updateTitle();
		setSizeAndLocation(Settings.get().getWindowSize(),  Settings.get().getWindowLocation(), Settings.get().isWindowMaximized());
		jFrame.setAlwaysOnTop(Settings.get().isWindowAlwaysOnTop());
		List<Image> icons = new ArrayList<>();
		icons.add(Images.TOOL_ASSETS.getImage());
		icons.add(Images.MISC_ASSETS_32.getImage());
		icons.add(Images.MISC_ASSETS_64.getImage());
		jFrame.setIconImages(icons);
		jFrame.addWindowListener(listener);
		jFrame.addComponentListener(listener);
		jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		jLockWindow = new JLockWindow(jFrame);

		JPanel jPanel = new JPanel();
		jPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK), "reOpenTab");
		jPanel.getActionMap().put("reOpenTab", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (closed.isEmpty()) {
					return;
				}
				addTab(closed.get(closed.size() - 1), true);
			}
		});
		GroupLayout layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(false);
		layout.setAutoCreateContainerGaps(false);
		jFrame.getContentPane().add(jPanel);

		mainMenu = new MainMenu(program);
		jFrame.setJMenuBar(mainMenu);

		jTabbedPane = new JDragTabbedPane();
		jTabbedPane.addChangeListener(listener);
		jTabbedPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON2) {
					int index = jTabbedPane.indexAtLocation(e.getX(), e.getY());
					if (index < 0) {
						return;
					}
					JMainTab jMainTab = tabs.get(index);
					if (jMainTab.isCloseable()) {
						removeTab(jMainTab);
					}
				}
			}
		});
		jTabbedPane.addTabMoveListeners(listener);
		jTabbedPane.addDragLock(0);

		statusPanel = new StatusPanel(program);
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(jTabbedPane, 0, 0, Short.MAX_VALUE)
				.addComponent(statusPanel.getPanel(), 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jTabbedPane, 0, 0, Short.MAX_VALUE)
				.addComponent(statusPanel.getPanel(), Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
		);
	}

	public final void updateTitle() {
		jFrame.setTitle(GuiFrame.get().windowTitle(
						Program.PROGRAM_NAME,
						Program.PROGRAM_VERSION,
						CliOptions.get().isPortable() ? 1 : 0,
						program.getProfileManager().getProfiles().size(),
						program.getProfileManager().getActiveProfile().getName()
						));
	}

	public void addTab(final JMainTab jMainTab) {
		addTab(jMainTab, true);
	}

	public void addTab(final JMainTab jMainTab, final boolean focus) {
		if (!tabs.contains(jMainTab)) {
			LOG.info("Opening tab: " + jMainTab.getTitle());
			TextManager.installAll(jMainTab.getPanel());
			jMainTab.beforeUpdateData();
			jMainTab.updateData();
			jMainTab.afterUpdateData();
			if (jFrame.isVisible()) {
				jLockWindow.show(GuiShared.get().updating(), new LockWorkerAdaptor() {
					@Override
					public void task() {
						jMainTab.updateCache();
					}
				});
			} else {
				jMainTab.updateCache();
			}
			tabs.add(jMainTab);
			closed.remove(jMainTab);
			jTabbedPane.addTab(jMainTab.getTitle(), jMainTab.getIcon(), jMainTab.getPanel());
			jTabbedPane.setTabComponentAt(jTabbedPane.getTabCount() - 1, new TabCloseButton(jMainTab));
			if (Settings.get().isSaveToolsOnExit() && !Settings.get().getShowTools().contains(jMainTab.getTitle())) {
				Settings.get().getShowTools().add(jMainTab.getTitle());
				program.saveSettings("Showing Tool");
			}
		}
		if (focus) {
			LOG.info("Focusing tab: " + jMainTab.getTitle());
			jTabbedPane.setSelectedComponent(jMainTab.getPanel());
		}
	}

	public boolean isOpen(final JMainTab jMainTab){
		return tabs.contains(jMainTab);
	}

	public JMainTab getSelectedTab() {
		return tabs.get(jTabbedPane.getSelectedIndex());
	}

	private void removeTab(final JMainTab jMainTab) {
		LOG.info("Closing tab: " + jMainTab.getTitle());
		int index = tabs.indexOf(jMainTab);
		jTabbedPane.removeTabAt(index);
		tabs.remove(index);
		closed.add(jMainTab);
		jMainTab.clearData();
		if (Settings.get().isSaveToolsOnExit()) {
			boolean removed = Settings.get().getShowTools().remove(jMainTab.getTitle());
			if (removed) {
				program.saveSettings("Hidding Tool");
			}
		}
	}

	public List<JMainTab> getTabs() {
		return tabs;
	}

	public MainMenu getMenu() {
		return mainMenu;
	}

	public void show() {
		jFrame.setVisible(true);
	}

	public final void setSizeAndLocation(final Dimension windowSize, final Point windowLocation, final boolean windowMaximized) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		//Fix size
		if (windowSize.width > screen.width) {
			windowSize.width = screen.width;
		}
		if (windowSize.height > screen.height) {
			windowSize.height = screen.height;
		}
		if (windowSize.width < 200) {
			windowSize.width = 200;
		}
		if (windowSize.height < 200) {
			windowSize.height = 200;
		}

		//Fix location
		if (windowLocation.x + windowSize.width > screen.width) {
			windowLocation.x = screen.width - windowSize.width;
		}
		if (windowLocation.y + windowSize.height > screen.height) {
			windowLocation.y = screen.height - windowSize.height;
		}
		if (windowLocation.x < 0) {
			windowLocation.x = 0;
		}
		if (windowLocation.y < 0) {
			windowLocation.y = 0;
		}

		//Set location, size, and state
		jFrame.setLocation(windowLocation);
		jFrame.setSize(windowSize);
		if (windowMaximized) {
			jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			jFrame.setExtendedState(JFrame.NORMAL);
		}
	}

	public JFrame getFrame() {
		return jFrame;
	}

	public StatusPanel getStatusPanel() {
		return statusPanel;
	}

	public void updateSettings() {
		if (Settings.get().isWindowAutoSave()) {
			Settings.get().setWindowMaximized(isMaximized());
			if (!isMaximized()) {
				Settings.get().setWindowSize(jFrame.getSize());
				Settings.get().setWindowLocation(jFrame.getLocation());
			}
		}
	}

	private boolean isMaximized() {
		return ((jFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH);
	}

	private class ListenerClass implements WindowListener, ChangeListener, ComponentListener, ActionListener, TabMoveListener {
		private short move = 0;
		private short resize = 0;

		@Override
		public void windowOpened(final WindowEvent e) { }

		@Override
		public void windowClosing(final WindowEvent e) {
			program.exit();
		}

		@Override
		public void windowClosed(final WindowEvent e) { }

		@Override
		public void windowIconified(final WindowEvent e) { }

		@Override
		public void windowDeiconified(final WindowEvent e) { }

		@Override
		public void windowActivated(final WindowEvent e) { }

		@Override
		public void windowDeactivated(final WindowEvent e) { }

		@Override
		public void stateChanged(final ChangeEvent e) {
			program.tabChanged();
		}

		@Override
		public void componentResized(ComponentEvent e) {
			if (Settings.get().isWindowAutoSave() && !isMaximized()) {
				if (move > 1) { //Ignore the two first updates
					program.saveSettings("Window Resized");
				} else {
					move++;
				}
			}
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			if (Settings.get().isWindowAutoSave()) {
				if (resize > 1) { //Ignore the two first updates
					timer.stop();
					timer.start();
				} else {
					resize++;
				}
			}
		}

		@Override
		public void componentShown(ComponentEvent e) { }

		@Override
		public void componentHidden(ComponentEvent e) { }

		@Override
		public void actionPerformed(ActionEvent e) {
			timer.stop();
			if (isMaximized()) {
				program.saveSettings("Window Maximized");
			} else {
				program.saveSettings("Window Moved");
			}
		}

		@Override
		public void tabMoved(int from, int to) {
			LOG.info("Moving tab: " + tabs.get(from).getTitle() + " from: " + from + " to: " + to);
			JMainTab jMainTab = tabs.remove(from);
			tabs.add(to, jMainTab);
			if (Settings.get().isSaveToolsOnExit()) {
				Settings.get().getShowTools().remove(jMainTab.getTitle());
				Settings.get().getShowTools().add(to, jMainTab.getTitle());
				program.saveSettings("Moved Tool");
			}
		}
	}

	private class TabCloseButton extends JPanel {

		public TabCloseButton(final JMainTab jMainTab) {
			super(new FlowLayout(FlowLayout.LEFT, 0, 0));
			this.setOpaque(false);
			JLabel jTitle = new JLabel(jMainTab.getTitle(), jMainTab.getIcon(), SwingConstants.LEFT);
			add(jTitle);
			if (jMainTab.isCloseable()) {
				JButton jClose = new JButton();
				jClose.setToolTipText(GuiFrame.get().close());
				jClose.setIcon(Images.TAB_CLOSE.getIcon());
				jClose.setRolloverIcon(Images.TAB_CLOSE_ACTIVE.getIcon());
				jClose.setUI(new BasicButtonUI());
				jClose.setPreferredSize(new Dimension(16, 16));
				jClose.setOpaque(false);
				jClose.setContentAreaFilled(false);
				jClose.setFocusable(false);
				jClose.setBorderPainted(false);
				jClose.setRolloverEnabled(true);
				jClose.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(final MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON2) {
							removeTab(jMainTab);
						}
					}
				});
				jClose.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						removeTab(jMainTab);
					}
				});
				add(jClose);
			}
		}
	}
}
