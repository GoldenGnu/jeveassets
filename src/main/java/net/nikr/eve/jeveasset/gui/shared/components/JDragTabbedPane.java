/*
 * Copyright 2009-2022 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * Original code by aterai (https://java-swing-tips.blogspot.com/2008/04/drag-and-drop-tabs-in-jtabbedpane.html)
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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import static javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT;
import static javax.swing.SwingConstants.BOTTOM;
import static javax.swing.SwingConstants.LEFT;
import static javax.swing.SwingConstants.RIGHT;
import static javax.swing.SwingConstants.TOP;
import javax.swing.SwingUtilities;

public class JDragTabbedPane extends JTabbedPane {

	private static final int LINEWIDTH = 3;
	private static final String NAME = "test";
	private final GhostGlassPane glassPane = new GhostGlassPane();
	private final Rectangle lineRect = new Rectangle();
	private final Color lineColor = new Color(0, 100, 255);
	private int sourceTabIndex = -1;

	private final List<TabMoveListener> tabMoveListeners = new ArrayList<>();
	private final Set<Integer> locked = new HashSet<>();

	public void addTabMoveListeners(TabMoveListener tabMoveListener) {
		tabMoveListeners.add(tabMoveListener);
	}

	public void removeTabMoveListeners(TabMoveListener tabMoveListener) {
		tabMoveListeners.remove(tabMoveListener);
	}

	public void addDragLock(Integer index) {
		locked.add(index);
	}

	public void removeDragLock(Integer index) {
		locked.remove(index);
	}	

	private void clickArrowButton(String actionKey) {
		ActionMap map = getActionMap();
		if (map != null) {
			Action action = map.get(actionKey);
			if (action != null && action.isEnabled()) {
				action.actionPerformed(new ActionEvent(
						this, ActionEvent.ACTION_PERFORMED, null, 0, 0));
			}
		}
	}
	private static Rectangle backward = new Rectangle();
	private static Rectangle forward = new Rectangle();
	private static final int RWH = 20;
	private static final int BUTTON_SIZE = 30;//XXX: magic number of scroll button size

	private void autoScrollTest(Point glassPt) {
		Rectangle r = getTabAreaBounds();
		int placement = getTabPlacement();
		if (placement == TOP || placement == BOTTOM) {
			backward.setBounds(r.x, r.y, RWH, r.height);
			forward.setBounds(r.x + r.width - RWH - BUTTON_SIZE, r.y, RWH + BUTTON_SIZE, r.height);
		} else if (placement == LEFT || placement == RIGHT) {
			backward.setBounds(r.x, r.y, r.width, RWH);
			forward.setBounds(r.x, r.y + r.height - RWH - BUTTON_SIZE, r.width, RWH + BUTTON_SIZE);
		}
		backward = SwingUtilities.convertRectangle(getParent(), backward, glassPane);
		forward = SwingUtilities.convertRectangle(getParent(), forward, glassPane);
		if (backward.contains(glassPt)) {
			clickArrowButton("scrollTabsBackwardAction");
		} else if (forward.contains(glassPt)) {
			clickArrowButton("scrollTabsForwardAction");
		}
	}

	public JDragTabbedPane() {
		super();
		final DragSourceListener dsl = new DragSourceListener() {
			@Override
			public void dragEnter(DragSourceDragEvent e) {
				e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
			}

			@Override
			public void dragExit(DragSourceEvent e) {
				e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
				lineRect.setRect(0, 0, 0, 0);
				glassPane.setPoint(new Point(-1000, -1000));
				glassPane.repaint();
			}

			@Override
			public void dragOver(DragSourceDragEvent e) {
				Point glassPt = e.getLocation();
				SwingUtilities.convertPointFromScreen(glassPt, glassPane);
				if (isValidTarget(getTargetTabIndex(glassPt))) {
					e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
					glassPane.setCursor(DragSource.DefaultMoveDrop);
				} else {
					e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
					glassPane.setCursor(DragSource.DefaultMoveNoDrop);
				}
			}

			@Override
			public void dragDropEnd(DragSourceDropEvent e) {
				lineRect.setRect(0, 0, 0, 0);
				sourceTabIndex = -1;
				glassPane.setVisible(false);
				if (hasGhost()) {
					glassPane.setVisible(false);
					glassPane.setImage(null);
				}
			}

			@Override
			public void dropActionChanged(DragSourceDragEvent e) {
			}
		};
		final Transferable t = new Transferable() {
			private final DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);

			@Override
			public Object getTransferData(DataFlavor flavor) {
				return JDragTabbedPane.this;
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				DataFlavor[] f = new DataFlavor[1];
				f[0] = this.FLAVOR;
				return f;
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return flavor.getHumanPresentableName().equals(NAME);
			}
		};
		final DragGestureListener dgl = new DragGestureListener() {
			@Override
			public void dragGestureRecognized(DragGestureEvent e) {
				if (getTabCount() <= 1) {
					return;
				}
				Point tabPt = e.getDragOrigin();
				sourceTabIndex = indexAtLocation(tabPt.x, tabPt.y);
				//"disabled tab problem".
				if (sourceTabIndex < 0 || !isEnabledAt(sourceTabIndex) || locked.contains(sourceTabIndex)) {
					return;
				}
				initGlassPane(e.getComponent(), e.getDragOrigin());
				try {
					e.startDrag(DragSource.DefaultMoveDrop, t, dsl);
				} catch (InvalidDnDOperationException idoe) {
					//No problem
				}
			}
		};
		new DropTarget(glassPane, DnDConstants.ACTION_COPY_OR_MOVE, new CDropTargetListener(), true);
		new DragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, dgl);
	}

	class CDropTargetListener implements DropTargetListener {

		@Override
		public void dragEnter(DropTargetDragEvent e) {
			if (isDragAcceptable(e)) {
				e.acceptDrag(e.getDropAction());
			} else {
				e.rejectDrag();
			}
		}

		@Override
		public void dragExit(DropTargetEvent e) {
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
		}

		private Point _glassPt = new Point();

		@Override
		public void dragOver(final DropTargetDragEvent e) {
			Point glassPt = e.getLocation();
			if (getTabPlacement() == JTabbedPane.TOP
					|| getTabPlacement() == JTabbedPane.BOTTOM) {
				initTargetLeftRightLine(getTargetTabIndex(glassPt));
			} else {
				initTargetTopBottomLine(getTargetTabIndex(glassPt));
			}
			if (hasGhost()) {
				glassPane.setPoint(glassPt);
			}
			if (!_glassPt.equals(glassPt)) {
				glassPane.repaint();
			}
			_glassPt = glassPt;
			autoScrollTest(glassPt);
		}

		@Override
		public void drop(DropTargetDropEvent e) {
			if (isDropAcceptable(e)) {
				int targetTabIndex = getTargetTabIndex(e.getLocation());
				if (!locked.contains(targetTabIndex)) {
					convertTab(targetTabIndex);
					e.dropComplete(true);
					repaint();
					return; //Moved
				}
			}
			e.dropComplete(false);
			repaint();
		}

		private boolean isDragAcceptable(DropTargetDragEvent e) {
			Transferable t = e.getTransferable();
			if (t == null) {
				return false;
			}
			DataFlavor[] f = e.getCurrentDataFlavors();
			if (t.isDataFlavorSupported(f[0]) && sourceTabIndex >= 0) {
				return true;
			}
			return false;
		}

		private boolean isDropAcceptable(DropTargetDropEvent e) {
			Transferable t = e.getTransferable();
			if (t == null) {
				return false;
			}

			DataFlavor[] f = t.getTransferDataFlavors();
			if (t.isDataFlavorSupported(f[0]) && sourceTabIndex >= 0) {
				return true;
			}
			return false;
		}
	}

	private boolean hasGhost = true;

	public void setPaintGhost(boolean flag) {
		hasGhost = flag;
	}

	public boolean hasGhost() {
		return hasGhost;
	}
	private boolean isPaintScrollArea = true;

	public void setPaintScrollArea(boolean flag) {
		isPaintScrollArea = flag;
	}

	public boolean isPaintScrollArea() {
		return isPaintScrollArea;
	}

	private int getTargetTabIndex(Point glassPt) {
		Point tabPt = SwingUtilities.convertPoint(glassPane, glassPt, JDragTabbedPane.this);
		boolean isTB = getTabPlacement() == JTabbedPane.TOP
				|| getTabPlacement() == JTabbedPane.BOTTOM;
		for (int i = 0; i < getTabCount(); i++) {
			Rectangle r = getBoundsAt(i);
			if (isTB) {
				r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
			} else {
				r.setRect(r.x, r.y - r.height / 2, r.width, r.height);
			}
			if (r.contains(tabPt)) {
				return i;
			}
		}
		Rectangle r = getBoundsAt(getTabCount() - 1);
		if (isTB) {
			r.setRect(r.x + r.width / 2, r.y, r.width, r.height);
		} else {
			r.setRect(r.x, r.y + r.height / 2, r.width, r.height);
		}
		return r.contains(tabPt) ? getTabCount() : -1;
	}

	private void convertTab(int targetTabIndex) {
		if (targetTabIndex < 0 || sourceTabIndex == targetTabIndex) {
			return;
		}
		Component cmp = getComponentAt(sourceTabIndex);
		Component tab = getTabComponentAt(sourceTabIndex);
		String str = getTitleAt(sourceTabIndex);
		Icon icon = getIconAt(sourceTabIndex);
		String tip = getToolTipTextAt(sourceTabIndex);
		boolean flg = isEnabledAt(sourceTabIndex);
		int fixedTargetTabIndex = sourceTabIndex > targetTabIndex ? targetTabIndex : targetTabIndex - 1;
		remove(sourceTabIndex);
		insertTab(str, icon, cmp, tip, fixedTargetTabIndex);
		setEnabledAt(fixedTargetTabIndex, flg);
		//When you drag'n'drop a disabled tab, it finishes enabled and selected.
		//pointed out by dlorde
		if (flg) {
			setSelectedIndex(fixedTargetTabIndex);
		}

		//I have a component in all tabs (jlabel with an X to close the tab)
		//and when i move a tab the component disappear.
		//pointed out by Daniel Dario Morales Salas
		setTabComponentAt(fixedTargetTabIndex, tab);

		//Notify listeners
		for (TabMoveListener tabMoveListener : tabMoveListeners) {
			tabMoveListener.tabMoved(sourceTabIndex, fixedTargetTabIndex);
		}
	}

	private void initTargetLeftRightLine(int targetTabIndex) {
		if (isInvalidTarget(targetTabIndex)) {
			lineRect.setRect(0, 0, 0, 0);
		} else if (targetTabIndex == 0) {
			Rectangle r = SwingUtilities.convertRectangle(
					this, getBoundsAt(0), glassPane);
			lineRect.setRect(r.x - LINEWIDTH / 2, r.y, LINEWIDTH, r.height);
		} else {
			Rectangle r = SwingUtilities.convertRectangle(
					this, getBoundsAt(targetTabIndex - 1), glassPane);
			lineRect.setRect(r.x + r.width - LINEWIDTH / 2, r.y, LINEWIDTH, r.height);
		}
	}

	private void initTargetTopBottomLine(int targetTabIndex) {
		if (isInvalidTarget(targetTabIndex)) {
			lineRect.setRect(0, 0, 0, 0);
		} else if (targetTabIndex == 0) {
			Rectangle r = SwingUtilities.convertRectangle(
					this, getBoundsAt(0), glassPane);
			lineRect.setRect(r.x, r.y - LINEWIDTH / 2, r.width, LINEWIDTH);
		} else {
			Rectangle r = SwingUtilities.convertRectangle(
					this, getBoundsAt(targetTabIndex - 1), glassPane);
			lineRect.setRect(r.x, r.y + r.height - LINEWIDTH / 2, r.width, LINEWIDTH);
		}
	}

	private boolean isInvalidTarget(int targetTabIndex) {
		return !isValidTarget(targetTabIndex);
	}

	private boolean isValidTarget(int targetTabIndex) {
		return targetTabIndex >= 0
				&& targetTabIndex != sourceTabIndex
				&& targetTabIndex != sourceTabIndex + 1
				&& !locked.contains(targetTabIndex);
	}

	private void initGlassPane(Component c, Point tabPt) {
		getRootPane().setGlassPane(glassPane);
		if (hasGhost()) {
			Rectangle rect = getBoundsAt(sourceTabIndex);
			BufferedImage image = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.getGraphics();
			c.paint(g);
			rect.x = rect.x < 0 ? 0 : rect.x;
			rect.y = rect.y < 0 ? 0 : rect.y;
			image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
			glassPane.setImage(image);
		}
		Point glassPt = SwingUtilities.convertPoint(c, tabPt, glassPane);
		glassPane.setPoint(glassPt);
		glassPane.setVisible(true);
	}

	private Rectangle getTabAreaBounds() {
		Rectangle tabbedRect = getBounds();
		//pointed out by daryl. NullPointerException: i.e. addTab("Tab",null)
		//Rectangle compRect = getSelectedComponent().getBounds();
		Component comp = getSelectedComponent();
		int idx = 0;
		while (comp == null && idx < getTabCount()) {
			comp = getComponentAt(idx++);
		}
		Rectangle compRect = (comp == null) ? new Rectangle() : comp.getBounds();
		int placement = getTabPlacement();
		if (placement == TOP) {
			tabbedRect.height = tabbedRect.height - compRect.height;
		} else if (placement == BOTTOM) {
			tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
			tabbedRect.height = tabbedRect.height - compRect.height;
		} else if (placement == LEFT) {
			tabbedRect.width = tabbedRect.width - compRect.width;
		} else if (placement == RIGHT) {
			tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
			tabbedRect.width = tabbedRect.width - compRect.width;
		}
		tabbedRect.grow(2, 2);
		return tabbedRect;
	}

	class GhostGlassPane extends JPanel {

		private final AlphaComposite composite;
		private Point location = new Point(0, 0);
		private BufferedImage draggingGhost = null;

		public GhostGlassPane() {
			setOpaque(false);
			composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			//[JDK-6700748] Cursor flickering during D&D when using CellRendererPane with validation - Java Bug System
			//https://bugs.openjdk.java.net/browse/JDK-6700748
			//setCursor(null);
		}

		public void setImage(BufferedImage draggingGhost) {
			this.draggingGhost = draggingGhost;
		}

		public void setPoint(Point location) {
			this.location = location;
		}

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setComposite(composite);
			if (isPaintScrollArea() && getTabLayoutPolicy() == SCROLL_TAB_LAYOUT) {
				g2.setPaint(Color.RED);
				g2.fill(backward);
				g2.fill(forward);
			}
			if (draggingGhost != null) {
				double xx = location.getX() - (draggingGhost.getWidth(this) / 2d);
				double yy = location.getY() - (draggingGhost.getHeight(this) / 2d);
				g2.drawImage(draggingGhost, (int) xx, (int) yy, null);
			}
			if (sourceTabIndex >= 0) {
				g2.setPaint(lineColor);
				g2.fill(lineRect);
			}
		}
	}

	public interface TabMoveListener {
		public void tabMoved(int from, int to);
	}
}
