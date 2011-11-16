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

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JDropDownButton extends JButton implements PopupMenuListener, MouseListener, KeyListener {

	private boolean showPopupMenuMouse = true;
	private boolean showPopupMenuKey = true;
	private boolean mouseOverThis = false;
	private boolean mousePressedThis = false;
	//private JButton jButton;
	private int popupHorizontalAlignment;
	private int popupVerticalAlignment;
	private JPopupMenu jPopupMenu;

	public JDropDownButton() {
		this(GuiShared.get().emptyString(), null, LEFT, BOTTOM);
	}

	public JDropDownButton(String text) {
		this(text, null, LEFT, BOTTOM);
	}

	public JDropDownButton(String text, Icon icon) {
		this(text, icon, LEFT, BOTTOM);
	}

	public JDropDownButton(int popupHorizontalAlignment) throws IllegalArgumentException {
		this(GuiShared.get().emptyString(), null, popupHorizontalAlignment, BOTTOM);
	}
	public JDropDownButton(int popupHorizontalAlignment, int popupVerticalAlignment) throws IllegalArgumentException {
		this(GuiShared.get().emptyString(), null, popupHorizontalAlignment, popupVerticalAlignment);
	}

	public JDropDownButton(String text, int popupHorizontalAlignment) throws IllegalArgumentException {
		this(text, null, popupHorizontalAlignment, BOTTOM);
	}
	public JDropDownButton(String text, int popupHorizontalAlignment, int popupVerticalAlignment) throws IllegalArgumentException {
		this(text, null, popupHorizontalAlignment, popupVerticalAlignment);
	}

	public JDropDownButton(String text, Icon icon, int popupHorizontalAlignment, int popupVerticalAlignment) throws IllegalArgumentException {
		super(text, icon);
		if (popupHorizontalAlignment != LEFT && popupHorizontalAlignment != RIGHT && popupHorizontalAlignment != CENTER){
			throw new IllegalArgumentException("Must be SwingConstants.RIGHT, SwingConstants.LEFT, or SwingConstants.CENTER");
		}
		if (popupVerticalAlignment != TOP && popupVerticalAlignment != BOTTOM && popupVerticalAlignment != CENTER){
			throw new IllegalArgumentException("Must be SwingConstants.TOP, SwingConstants.BOTTOM, or SwingConstants.CENTER");
		}
		this.popupHorizontalAlignment = popupHorizontalAlignment;
		this.popupVerticalAlignment = popupVerticalAlignment;
		this.setText(text);
		this.addMouseListener(this);
		this.addKeyListener(this);
		jPopupMenu = new JPopupMenu();
		jPopupMenu.addPopupMenuListener(this);
	}

	@Override
	public void removeAll(){
		jPopupMenu.removeAll();
	}

	public void addSeparator() {
		jPopupMenu.addSeparator();
	}

	@Override
	public Component add(Component component) {
		if (component instanceof JMenuItem) return add((JMenuItem) component);
		return super.add(component);
    }
	
	public JMenuItem add(JMenuItem jMenuItem){
		jPopupMenu.add(jMenuItem);
		return jMenuItem;
	}

	public int getPopupHorizontalAlignment() {
		return popupHorizontalAlignment;
	}

	public void setPopupHorizontalAlignment(int popupHorizontalAlignment) throws  IllegalArgumentException {
		if (popupHorizontalAlignment != LEFT && popupHorizontalAlignment != RIGHT && popupHorizontalAlignment != CENTER){
			throw new IllegalArgumentException("Must be SwingConstants.RIGHT, SwingConstants.LEFT, or SwingConstants.CENTER");
		}
		this.popupHorizontalAlignment = popupHorizontalAlignment;
	}

	public int getPopupVerticalAlignment() {
		return popupVerticalAlignment;
	}

	public void setPopupVerticalAlignment(int popupVerticalAlignment) throws  IllegalArgumentException {
		if (popupVerticalAlignment != TOP && popupVerticalAlignment != BOTTOM && popupVerticalAlignment != CENTER){
			throw new IllegalArgumentException("Must be SwingConstants.TOP, SwingConstants.BOTTOM, or SwingConstants.CENTER");
		}
		this.popupVerticalAlignment = popupVerticalAlignment;
	}

	private void showPopupMenu(){
		if (!this.isEnabled()) return;
		int verticalPosition = this.getHeight();
		int horizontalPosition = 0;
		Dimension popupMenuSize = jPopupMenu.getPreferredSize();
		switch (popupHorizontalAlignment){
		case LEFT: //OK
			horizontalPosition = 0;
			break;
		case RIGHT: //OK
			horizontalPosition = this.getWidth()-popupMenuSize.width;
			break;
		case CENTER: //OK
			horizontalPosition = (this.getWidth()/2)-(popupMenuSize.width/2);
			break;
		}
		switch (popupVerticalAlignment){
			case TOP: //OK
				verticalPosition = -popupMenuSize.height + 2;
				break;
			case BOTTOM: //OK
				verticalPosition = this.getHeight() - 2;
				break;
			case CENTER: //OK
				verticalPosition = -(popupMenuSize.height/2) + (this.getHeight()/2);
		}
		jPopupMenu.show(this, horizontalPosition, verticalPosition);
		this.getModel().setRollover(true);
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
		if (mouseOverThis){
			showPopupMenuMouse = false;
		} else {
			showPopupMenuMouse = true;
		}
		this.getModel().setRollover(false);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressedThis = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (mousePressedThis){
			if (showPopupMenuMouse){
				showPopupMenu();
			} else {
				showPopupMenuMouse = true;
			}
			return;
		}
		mousePressedThis = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource().equals(this)){
			mouseOverThis = true;
		}

	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (e.getSource().equals(this)){
			mouseOverThis = false;
		}
		if (jPopupMenu.isShowing()) this.getModel().setRollover(true);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (showPopupMenuKey){
			showPopupMenu();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if ( (e.getKeyCode()  == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER)){
			showPopupMenuKey = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		showPopupMenuKey = false;
	}
	
}
