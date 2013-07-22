/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import net.nikr.eve.jeveasset.gui.shared.MenuScroller;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JDropDownButton extends JButton {

	private boolean showPopupMenuMouse = true;
	private boolean showPopupMenuKey = true;
	private boolean mouseOverThis = false;
	private boolean mousePressedThis = false;
	//private JButton jButton;
	private int popupHorizontalAlignment;
	private int popupVerticalAlignment;
	private final JPopupMenu jPopupMenu;
	private final MenuScroller menuScroller;

	public JDropDownButton() {
		this(GuiShared.get().emptyString(), null, LEFT, BOTTOM);
	}

	public JDropDownButton(final Icon icon) {
		this("", icon, LEFT, BOTTOM);
	}

	public JDropDownButton(final String text) {
		this(text, null, LEFT, BOTTOM);
	}

	public JDropDownButton(final String text, final Icon icon) {
		this(text, icon, LEFT, BOTTOM);
	}

	public JDropDownButton(final int popupHorizontalAlignment) {
		this(GuiShared.get().emptyString(), null, popupHorizontalAlignment, BOTTOM);
	}
	public JDropDownButton(final int popupHorizontalAlignment, final int popupVerticalAlignment) {
		this(GuiShared.get().emptyString(), null, popupHorizontalAlignment, popupVerticalAlignment);
	}

	public JDropDownButton(final String text, final int popupHorizontalAlignment) {
		this(text, null, popupHorizontalAlignment, BOTTOM);
	}
	public JDropDownButton(final String text, final int popupHorizontalAlignment, final int popupVerticalAlignment) {
		this(text, null, popupHorizontalAlignment, popupVerticalAlignment);
	}

	public JDropDownButton(final String text, final Icon icon, final int popupHorizontalAlignment, final int popupVerticalAlignment) {
		super(text, icon);
		if (popupHorizontalAlignment != LEFT && popupHorizontalAlignment != RIGHT && popupHorizontalAlignment != CENTER) {
			throw new IllegalArgumentException("Must be SwingConstants.RIGHT, SwingConstants.LEFT, or SwingConstants.CENTER");
		}
		if (popupVerticalAlignment != TOP && popupVerticalAlignment != BOTTOM && popupVerticalAlignment != CENTER) {
			throw new IllegalArgumentException("Must be SwingConstants.TOP, SwingConstants.BOTTOM, or SwingConstants.CENTER");
		}

		ListenerClass listener = new ListenerClass();

		this.popupHorizontalAlignment = popupHorizontalAlignment;
		this.popupVerticalAlignment = popupVerticalAlignment;
		this.setText(text);
		this.addMouseListener(listener);
		this.addKeyListener(listener);
		jPopupMenu = new JPopupMenu();
		jPopupMenu.addPopupMenuListener(listener);
		menuScroller = new MenuScroller(jPopupMenu);
	}

	@Override
	public void removeAll() {
		jPopupMenu.removeAll();
	}

	public void addSeparator() {
		jPopupMenu.addSeparator();
	}

	@Override
	public Component add(final Component component) {
		if (component instanceof JMenuItem) {
			return add((JMenuItem) component);
		}
		return super.add(component);
	}

	public JMenuItem add(final JMenuItem jMenuItem, boolean keepOpen) {
		if (keepOpen) {
			//jPopupMenu.setLightWeightPopupEnabled(true);
			jMenuItem.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					showPopupMenu();
					jMenuItem.setArmed(true);
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					jMenuItem.setArmed(true);
				}
				@Override
				public void mouseExited(MouseEvent e) {
					jMenuItem.setArmed(false);
				}
			});
		}
		return add(jMenuItem);
	}

	public JMenuItem add(final JMenuItem jMenuItem) {
		jPopupMenu.add(jMenuItem);
		return jMenuItem;
	}

	public int getPopupHorizontalAlignment() {
		return popupHorizontalAlignment;
	}

	public void setPopupHorizontalAlignment(final int popupHorizontalAlignment)  {
		if (popupHorizontalAlignment != LEFT && popupHorizontalAlignment != RIGHT && popupHorizontalAlignment != CENTER) {
			throw new IllegalArgumentException("Must be SwingConstants.RIGHT, SwingConstants.LEFT, or SwingConstants.CENTER");
		}
		this.popupHorizontalAlignment = popupHorizontalAlignment;
	}

	public int getPopupVerticalAlignment() {
		return popupVerticalAlignment;
	}

	public void setPopupVerticalAlignment(final int popupVerticalAlignment) {
		if (popupVerticalAlignment != TOP && popupVerticalAlignment != BOTTOM && popupVerticalAlignment != CENTER) {
			throw new IllegalArgumentException("Must be SwingConstants.TOP, SwingConstants.BOTTOM, or SwingConstants.CENTER");
		}
		this.popupVerticalAlignment = popupVerticalAlignment;
	}

	public void keepVisible(final int index) {
		menuScroller.keepVisible(index);
	}
	public final void setTopFixedCount(final int topFixedCount) {
		menuScroller.setTopFixedCount(topFixedCount);
	}
	public final void setBottomFixedCount(final int bottomFixedCount) {
		menuScroller.setBottomFixedCount(bottomFixedCount);
	}
	public final void setInterval(final int interval) {
		menuScroller.setInterval(interval);
	}

	private void showPopupMenu() {
		if (!this.isEnabled()) {
			return;
		}
		int verticalPosition = this.getHeight();
		int horizontalPosition = 0;
		Dimension popupMenuSize = jPopupMenu.getPreferredSize();
		switch (popupHorizontalAlignment) {
		case LEFT: //OK
			horizontalPosition = 0;
			break;
		case RIGHT: //OK
			horizontalPosition = this.getWidth() - popupMenuSize.width;
			break;
		case CENTER: //OK
			horizontalPosition = (this.getWidth() / 2) - (popupMenuSize.width / 2);
			break;
		}
		switch (popupVerticalAlignment) {
			case TOP: //OK
				verticalPosition = -popupMenuSize.height + 2;
				break;
			case BOTTOM: //OK
				verticalPosition = this.getHeight() - 2;
				break;
			case CENTER: //OK
				verticalPosition = -(popupMenuSize.height / 2) + (this.getHeight() / 2);
		}
		jPopupMenu.show(this, horizontalPosition, verticalPosition);
		this.getModel().setRollover(true);
	}

	private class ListenerClass implements PopupMenuListener, MouseListener, KeyListener {
		@Override
		public void popupMenuWillBecomeVisible(final PopupMenuEvent e) { }

		@Override
		public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) { }

		@Override
		public void popupMenuCanceled(final PopupMenuEvent e) {
			if (mouseOverThis) {
				showPopupMenuMouse = false;
			} else {
				showPopupMenuMouse = true;
			}
			getModel().setRollover(false);
		}

		@Override
		public void mouseClicked(final MouseEvent e) { }

		@Override
		public void mousePressed(final MouseEvent e) {
			mousePressedThis = true;
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			if (mousePressedThis) {
				if (showPopupMenuMouse) {
					showPopupMenu();
				} else {
					showPopupMenuMouse = true;
				}
				return;
			}
			mousePressedThis = false;
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			mouseOverThis = true;

		}

		@Override
		public void mouseExited(final MouseEvent e) {
			mouseOverThis = false;
			if (jPopupMenu.isShowing()) {
				getModel().setRollover(true);
			}
		}

		@Override
		public void keyTyped(final KeyEvent e) {
			if (showPopupMenuKey) {
				showPopupMenu();
			}
		}

		@Override
		public void keyPressed(final KeyEvent e) {
			if (e.getKeyCode()  == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
				showPopupMenuKey = true;
			}
		}

		@Override
		public void keyReleased(final KeyEvent e) {
			showPopupMenuKey = false;
		}
	}
}
