/*
 * Copyright 2009-2024 Contributors (see credits.txt)
 *
 * Original code by Darryl (http://tips4java.wordpress.com/2009/02/01/menu-scroller/)
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

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * A class that provides scrolling capabilities to a long menu dropdown or popup
 * menu. A number of items can optionally be frozen at the top and/or bottom of
 * the menu. <P> <B>Implementation note:</B> The default number of items to
 * display at a time is 15, and the default scrolling interval is 125
 * milliseconds. <P>
 *
 * @author Darryl, http://tips4java.wordpress.com/2009/02/01/menu-scroller/
 */
public class MenuScroller {

	private MenuContainer menu;
	private Component[] menuItems;
	private MenuScrollItem upItem;
	private MenuScrollItem downItem;
	private final MenuScrollListener menuListener = new MenuScrollListener();
	private final MouseScrollListener mouseListener = new MouseScrollListener();
	private int scrollCount;
	private int interval;
	private int topFixedCount;
	private int bottomFixedCount;
	private int firstIndex = 0;
	private int keepVisibleIndex = -1;

	/**
	 * Constructs a
	 * <code>MenuScroller</code> that scrolls a popup menu with the default
	 * number of items to display at a time, and default scrolling interval.
	 *
	 * @param menu the popup menu
	 */
	public MenuScroller(final JPopupMenu menu) {
		this(menu, 15);
	}

	/**
	 * Constructs a
	 * <code>MenuScroller</code> that scrolls a popup menu with the specified
	 * number of items to display at a time, and specified scrolling interval.
	 *
	 * @param menu the popup menu
	 * @param interval the scroll interval, in milliseconds
	 * @throws IllegalArgumentException if scrollCount or interval is 0 or
	 * negative
	 */
	public MenuScroller(final JPopupMenu menu, final int interval) {
		this(menu, interval, 0, 0);
	}

	/**
	 * Constructs a
	 * <code>MenuScroller</code> that scrolls a popup menu with the specified
	 * number of items to display in the scrolling region, the specified
	 * scrolling interval, and the specified numbers of items fixed at the top
	 * and bottom of the popup menu.
	 *
	 * @param menu the popup menu
	 * @param interval the scroll interval, in milliseconds
	 * @param topFixedCount the number of items to fix at the top. May be 0
	 * @param bottomFixedCount the number of items to fix at the bottom. May be
	 * 0
	 * @throws IllegalArgumentException if scrollCount or interval is 0 or
	 * negative or if topFixedCount or bottomFixedCount is negative
	 */
	public MenuScroller(final JPopupMenu menu, final int interval, final int topFixedCount, final int bottomFixedCount) {
		this(new MenuContainer(menu), interval, topFixedCount, bottomFixedCount);
	}

	public MenuScroller(final JMenu menu) {
		this(menu, 15);
	}

	public MenuScroller(final JMenu menu, final int interval) {
		this(menu, interval, 0, 0);
	}

	public MenuScroller(final JMenu menu, final int interval, final int topFixedCount, final int bottomFixedCount) {
		this(new MenuContainer(menu), interval, topFixedCount, bottomFixedCount);
	}

	private MenuScroller(final MenuContainer menu, final int interval, final int topFixedCount, final int bottomFixedCount) {
		if (interval <= 0) {
			throw new IllegalArgumentException("interval must be greater than 0");
		}
		if (topFixedCount < 0 || bottomFixedCount < 0) {
			throw new IllegalArgumentException("topFixedCount and bottomFixedCount cannot be negative");
		}

		upItem = new MenuScrollItem(MenuIcon.UP, -1);
		downItem = new MenuScrollItem(MenuIcon.DOWN, +1);
		scrollCount = 0;
		setInterval(interval);
		setTopFixedCount(topFixedCount);
		setBottomFixedCount(bottomFixedCount);

		this.menu = menu;
		
		menu.addListener(menuListener, mouseListener);
	}

	/**
	 * Returns the scroll interval in milliseconds.
	 *
	 * @return the scroll interval in milliseconds
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * Sets the scroll interval in milliseconds.
	 *
	 * @param interval the scroll interval in milliseconds
	 * @throws IllegalArgumentException if interval is 0 or negative
	 */
	public final void setInterval(final int interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("interval must be greater than 0");
		}
		upItem.setInterval(interval);
		downItem.setInterval(interval);
		this.interval = interval;
	}

	/**
	 * Returns the number of items fixed at the top of the menu or popup menu.
	 *
	 * @return the number of items
	 */
	public int getTopFixedCount() {
		return topFixedCount;
	}

	/**
	 * Sets the number of items to fix at the top of the menu or popup menu.
	 *
	 * @param topFixedCount the number of items
	 */
	public final void setTopFixedCount(final int topFixedCount) {
		if (firstIndex <= topFixedCount) {
			firstIndex = topFixedCount;
		} else {
			firstIndex += (topFixedCount - this.topFixedCount);
		}
		this.topFixedCount = topFixedCount;
	}

	/**
	 * Returns the number of items fixed at the bottom of the menu or popup
	 * menu.
	 *
	 * @return the number of items
	 */
	public int getBottomFixedCount() {
		return bottomFixedCount;
	}

	/**
	 * Sets the number of items to fix at the bottom of the menu or popup menu.
	 *
	 * @param bottomFixedCount the number of items
	 */
	public final void setBottomFixedCount(final int bottomFixedCount) {
		this.bottomFixedCount = bottomFixedCount;
	}

	/**
	 * Scrolls the specified item into view each time the menu is opened. Call
	 * this method with
	 * <code>null</code> to restore the default behavior, which is to show the
	 * menu as it last appeared.
	 *
	 * @param item the item to keep visible
	 * @see #keepVisible(int)
	 */
	public void keepVisible(final JMenuItem item) {
		if (item == null) {
			keepVisibleIndex = -1;
		} else {
			int index = menu.getComponentIndex(item);
			keepVisibleIndex = index;
		}
	}

	/**
	 * Scrolls the item at the specified index into view each time the menu is
	 * opened. Call this method with
	 * <code>-1</code> to restore the default behavior, which is to show the
	 * menu as it last appeared.
	 *
	 * @param index the index of the item to keep visible
	 * @see #keepVisible(javax.swing.JMenuItem)
	 */
	public void keepVisible(final int index) {
		keepVisibleIndex = index;
	}

	/**
	 * Removes this MenuScroller from the associated menu and restores the
	 * default behavior of the menu.
	 */
	public void dispose() {
		if (menu != null) {
			menu.removeListener(menuListener, mouseListener);
			menu = null;
		}
	}

	private void refreshMenu() {
		if (menuItems != null && menuItems.length > 0) {
			int maxScroll = menuItems.length - (topFixedCount + bottomFixedCount + 2);
			if (scrollCount == maxScroll) { //Show All
				menu.setPreferredSize(null);
				return;
			}
			firstIndex = Math.max(topFixedCount, firstIndex);
			firstIndex = Math.min(menuItems.length - bottomFixedCount - scrollCount, firstIndex);
			upItem.setEnabled(firstIndex > topFixedCount);
			downItem.setEnabled(firstIndex + scrollCount < menuItems.length - bottomFixedCount);

			menu.removeAll();
			//Top Fixed
			for (int i = 0; i < topFixedCount && i < menuItems.length; i++) {
				menu.add(menuItems[i]);
			}
			//Scroll
			menu.add(upItem);
			for (int i = firstIndex; i < scrollCount + firstIndex; i++) {
				menu.add(menuItems[i]);
			}
			menu.add(downItem);

			//Bottom Fixed
			for (int i = menuItems.length - bottomFixedCount; i < menuItems.length; i++) {
				menu.add(menuItems[i]);
			}
			int preferredWidth = 0;
			for (Component item : menuItems) {
				preferredWidth = Math.max(preferredWidth, item.getPreferredSize().width);
			}
			menu.setPreferredSize(null); //Reset height
			menu.setPreferredSize(new Dimension(preferredWidth, menu.getPreferredSize().height));

			JComponent parent = (JComponent) upItem.getParent();
			parent.revalidate();
			parent.repaint();
		}
	}

	public void scrollCountForScreen() {
		int maxHeight = 0;
		if (menuItems != null && menuItems.length > 0) {
			for (Component item : menuItems) {
				maxHeight = Math.max(maxHeight, item.getPreferredSize().height);
			}
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int height = screenSize.height;
			int maxScroll = (height / maxHeight) - (topFixedCount + bottomFixedCount + 2); // 2 just takes the menu up a bit from the bottom which looks nicer
			scrollCount = Math.min(maxScroll, menuItems.length - (topFixedCount + bottomFixedCount + 2));
		}
	}

	private class MenuScrollListener implements PopupMenuListener, MenuListener {

		@Override
		public void menuSelected(MenuEvent e) {
			setMenuItems();
		}

		@Override
		public void menuDeselected(MenuEvent e) {
			restoreMenuItems();
		}

		@Override
		public void menuCanceled(MenuEvent e) {
			//restoreMenuItems();
		}

		@Override
		public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
			setMenuItems();
		}

		@Override
		public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
			restoreMenuItems();
		}

		@Override
		public void popupMenuCanceled(final PopupMenuEvent e) {
			restoreMenuItems();
		}

		private void setMenuItems() {
			menuItems = menu.getComponents();
			scrollCountForScreen();
			if (keepVisibleIndex >= topFixedCount
					&& keepVisibleIndex <= menuItems.length - bottomFixedCount
					&& (keepVisibleIndex > firstIndex + scrollCount
					|| keepVisibleIndex < firstIndex)) {
				firstIndex = Math.min(firstIndex, keepVisibleIndex);
				firstIndex = Math.max(firstIndex, keepVisibleIndex - scrollCount + 1);
			}
			if (menuItems.length > topFixedCount + scrollCount + bottomFixedCount) {
				refreshMenu();
			}
		}

		private void restoreMenuItems() {
			menu.removeAll();
			for (Component component : menuItems) {
				menu.add(component);
			}
		}
	}

	private class MouseScrollListener implements MouseWheelListener {

		@Override
		public void mouseWheelMoved(final MouseWheelEvent mwe) {
			firstIndex += mwe.getWheelRotation();
			mwe.consume();
			refreshMenu();
		}
	}

	private class MenuScrollItem extends JMenuItem implements ChangeListener, MouseListener{

		private Timer timer;

		public MenuScrollItem(final MenuIcon icon, final int increment) {
			setIcon(icon);
			setDisabledIcon(icon);
			timer = new Timer(interval, new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					firstIndex += increment;
					refreshMenu();
				}
			});
			addChangeListener(this);
			addMouseListener(this);
		}

		public void setInterval(final int interval) {
			timer.setDelay(interval);
		}

		@Override
		protected void processMouseEvent(MouseEvent e) {
			if (e.getButton() == MouseEvent.NOBUTTON) { //Ignore mouse clicks
				super.processMouseEvent(e); //Process mouse event like normal
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) { }

		@Override
		public void mousePressed(MouseEvent e) { }

		@Override
		public void mouseReleased(MouseEvent e) { }

		@Override
		public void mouseEntered(MouseEvent e) {
			if (!isArmed()) {
				setArmed(true);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (isArmed()) {
				setArmed(false);
			}
		}

		@Override
		public void stateChanged(final ChangeEvent e) {
			if (isArmed() && !timer.isRunning()) {
				timer.start();
			}
			if (!isArmed() && timer.isRunning()) {
				timer.stop();
			}
		}
	}

	private static enum MenuIcon implements Icon {

		UP(7, 3, 7),
		DOWN(3, 7, 3);
		private final int[] xPoints = {1, 5, 9};
		private final int[] yPoints;
		private final boolean bright;

		MenuIcon(final int... yPoints) {
			this.yPoints = yPoints;
			bright = ColorUtil.isBrightColor(new JPanel().getBackground());
		}

		@Override
		public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
			Dimension size = c.getSize();
			Graphics g2 = g.create(size.width / 2 - 5, size.height / 2 - 5, 10, 10);
			if (bright) {
				if (c.isEnabled()) {
					g2.setColor(Color.BLACK);
				} else {
					g2.setColor(Color.LIGHT_GRAY);
				}
			} else {
				if (c.isEnabled()) {
					g2.setColor(Color.GRAY.brighter());
				} else {
					g2.setColor(Color.GRAY.darker());
				}
			}
			g2.fillPolygon(xPoints, yPoints, 3);
			if (bright) {
				if (c.isEnabled()) {
					g2.setColor(Color.DARK_GRAY);
				} else {	
					g2.setColor(Color.LIGHT_GRAY);
				}
			} else {
				if (c.isEnabled()) {
					g2.setColor(Color.LIGHT_GRAY);
				} else {
					g2.setColor(Color.DARK_GRAY);
				}
			}
			g2.drawPolygon(xPoints, yPoints, 3);
			g2.dispose();
		}

		@Override
		public int getIconWidth() {
			return 0;
		}

		@Override
		public int getIconHeight() {
			return 10;
		}
	}

	private static class MenuContainer {
		private final JPopupMenu jPopupMenu;
		private final JMenu jMenu;
		private final JComponent component;

		public MenuContainer(JPopupMenu jPopupMenu) {
			this.jPopupMenu = jPopupMenu;
			this.jMenu = null;
			this.component = jPopupMenu;
		}		

		public MenuContainer(JMenu jMenu) {
			this.jPopupMenu = null;
			this.jMenu = jMenu;
			this.component = jMenu;
		}		

		private int getComponentIndex(JMenuItem item) {
			if (jPopupMenu != null) {
				return jPopupMenu.getComponentIndex(item);
			} else {
				int ncomponents = jMenu.getComponentCount();
				Component[] components = jMenu.getComponents();
				for (int i = 0 ; i < ncomponents ; i++) {
					Component comp = components[i];
					if (comp == item)
						return i;
				}
				return -1;
			}
		}

		private void setPreferredSize(Dimension preferredSize) {
			component.setPreferredSize(preferredSize);
		}

		private void removeAll() {
			component.removeAll();
		}

		private void add(Component menuItem) {
			component.add(menuItem);
		}

		private Dimension getPreferredSize() {
			return component.getPreferredSize();
		}

		private Component[] getComponents() {
			if (jPopupMenu != null) {
				return jPopupMenu.getComponents();
			} else {
				return jMenu.getMenuComponents();
			}
		}

		private void addListener(MenuScrollListener menuListener, MouseScrollListener mouseListener) {
			if (jPopupMenu != null) {
				jPopupMenu.addPopupMenuListener(menuListener);
				jPopupMenu.addMouseWheelListener(mouseListener);
			} else {
				jMenu.addMenuListener(menuListener);
				JPopupMenu popup = jMenu.getPopupMenu();
				if (popup != null) {
					popup.addMouseWheelListener(mouseListener);
				}
			}
		}

		private void removeListener(MenuScrollListener menuListener, MouseScrollListener mouseListener) {
			if (jPopupMenu != null) {
				jPopupMenu.removePopupMenuListener(menuListener);
				jPopupMenu.removeMouseWheelListener(mouseListener);
			} else {
				jMenu.removeMenuListener(menuListener);
				JPopupMenu popup = jMenu.getPopupMenu();
				if (popup != null) {
					popup.removeMouseWheelListener(mouseListener);
				}
			}
		}
	}
}
