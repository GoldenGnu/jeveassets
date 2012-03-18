/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JMainTab;
import net.nikr.eve.jeveasset.i18n.GuiFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainWindow implements WindowListener, ChangeListener {

	private final static Logger LOG = LoggerFactory.getLogger(MainWindow.class);

	//GUI
	private MainMenu mainMenu;
	private JFrame jFrame;
	private JTabbedPane jTabbedPane;
	private StatusPanel statusPanel;

	//Data
	private Program program;
	private List<JMainTab> tabs = new ArrayList<JMainTab>();
	
	public MainWindow(Program program){
		this.program = program;
		//Frame
		jFrame = new JFrame();
		updateTitle();
		this.setSizeAndLocation(program.getSettings().getWindowSize(),  program.getSettings().getWindowLocation(), program.getSettings().isWindowMaximized());
		jFrame.setIconImage(Images.TOOL_ASSETS.getImage());
		jFrame.addWindowListener(this);
		jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel jPanel = new JPanel();
		GroupLayout layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(false);
		layout.setAutoCreateContainerGaps(false);
		jFrame.getContentPane().add(jPanel);

		mainMenu = new MainMenu(program);
		jFrame.setJMenuBar( mainMenu );

		jTabbedPane = new JTabbedPane();
		jTabbedPane.addChangeListener(this);

		statusPanel = new StatusPanel(program);
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(jTabbedPane, 0, 0, Short.MAX_VALUE)
				.addComponent(statusPanel.getPanel(), 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jTabbedPane, 0, 0, Short.MAX_VALUE)
				.addComponent(statusPanel.getPanel(), 25, 25, 25)
		);
	}

	final public void updateTitle(){
    jFrame.setTitle(GuiFrame.get().windowTitle(
            Program.PROGRAM_NAME,
            Program.PROGRAM_VERSION,
            Settings.isPortable() ? 1 : 0,
            program.getSettings().getProfiles().size(),
            program.getSettings().getActiveProfile().getName()
            ));
	}
	
	public void addTab(JMainTab jMainTab){
		addTab(jMainTab, true);
	}

	public void addTab(JMainTab jMainTab, boolean focus){
		if (!tabs.contains(jMainTab)){
			LOG.info("Opening tab: "+jMainTab.getTitle());
			jMainTab.updateData();
			tabs.add(jMainTab);
			jTabbedPane.addTab(jMainTab.getTitle(), jMainTab.getIcon(), jMainTab.getPanel());
			jTabbedPane.setTabComponentAt(jTabbedPane.getTabCount() - 1, new TabCloseButton(jMainTab));
		} else {
			LOG.info("Focusing tab: "+jMainTab.getTitle());
		}
		if (focus) jTabbedPane.setSelectedComponent(jMainTab.getPanel());
	}

	public JMainTab getSelectedTab(){
		return tabs.get(jTabbedPane.getSelectedIndex());
	}

	public void removeTab(JMainTab jMainTab){
		LOG.info("Closing tab: "+jMainTab.getTitle());
		int index = tabs.indexOf(jMainTab);
		jTabbedPane.removeTabAt(index);
		tabs.remove(index);
	}

	public List<JMainTab> getTabs() {
		return tabs;
	}

	public MainMenu getMenu() {
		return mainMenu;
	}

	public void show(){
		jFrame.setVisible(true);
	}

	public final void setSizeAndLocation(Dimension windowSize, Point windowLocation, boolean windowMaximized) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		//Fix size
		if (windowSize.width > screen.width) windowSize.width = screen.width;
		if (windowSize.height > screen.height) windowSize.height = screen.height;
		if (windowSize.width < 200) windowSize.width = 200;
		if (windowSize.height < 200) windowSize.height = 200;

		//Fix location
		if (windowLocation.x + windowSize.width > screen.width){
			windowLocation.x = screen.width - windowSize.width;
		}
		if (windowLocation.y + windowSize.height > screen.height){
			windowLocation.y = screen.height - windowSize.height;
		}
		if (windowLocation.x < 0) windowLocation.x = 0;
		if (windowLocation.y < 0) windowLocation.y = 0;

		//Set location, size, and state
		jFrame.setLocation(windowLocation);
		jFrame.setSize(windowSize);
		if (windowMaximized){
			jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			jFrame.setExtendedState(JFrame.NORMAL);
		}
    }

	public void setEnabled(boolean b) {
		if (b){
			jFrame.setCursor(null);
		} else {
			jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
       jFrame.setEnabled(b);
    }

	public JFrame getFrame(){
		return jFrame;
	}

	public StatusPanel getStatusPanel() {
		return statusPanel;
	}

	public void updateSettings(){
		if (program.getSettings().isWindowAutoSave()){
			program.getSettings().setWindowMaximized( (jFrame.getState() == JFrame.MAXIMIZED_BOTH) );
			if (jFrame.getExtendedState() != JFrame.MAXIMIZED_BOTH){
				program.getSettings().setWindowSize(jFrame.getSize());
				program.getSettings().setWindowLocation(jFrame.getLocation());
			}
		}
	}
	
	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		program.exit();
	}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void stateChanged(ChangeEvent e) {
		program.tabChanged();
	}

	private class TabCloseButton extends JPanel{

		public TabCloseButton(final JMainTab jMainTab) {
			super(new FlowLayout(FlowLayout.LEFT, 0, 0));
			this.setOpaque(false);
			JLabel jTitle = new JLabel(jMainTab.getTitle(), jMainTab.getIcon(), SwingConstants.LEFT);
			add(jTitle);
			if (jMainTab.isCloseable()){
				this.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON2){
							removeTab(jMainTab);
						} else if (e.getButton() == MouseEvent.BUTTON1){
							jTabbedPane.setSelectedComponent(jMainTab.getPanel());
						}
					}
				});
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
					public void mousePressed(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON2){
							removeTab(jMainTab);
						}
					}
				});
				jClose.addActionListener( new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						removeTab(jMainTab);
					}
				});
				add(jClose);
			}
		}
	}
}