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

package net.nikr.eve.jeveasset.gui.shared.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import net.nikr.eve.jeveasset.gui.shared.Formatter;


public class JTreemap extends JComponent {

	public interface SelectionListener {
		void itemSelected(String name);
	}

	private static final double GAP = 1.0;
	private static final double MAX_ASPECT_RATIO = 5.0;
	private static final double MAX_TILE_ASPECT_RATIO = 4.0;
	private static final int LABEL_PADDING = 4;
	private static final int OVERLAY_ALPHA_CENTER = 0;
	private static final int OVERLAY_ALPHA_EDGE = 70;

	private final SelectionListener selectionListener;
	private final List<Tile> tiles = new ArrayList<>();
	private Dimension lastSize;
	private boolean layoutDirty = true;
	private Tile hovered;
	private final Font labelFont;

	public JTreemap(final SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
		setOpaque(true);
		Color background = UIManager.getColor("Panel.background");
		if (background != null) {
			setBackground(background);
		}
		labelFont = UIManager.getFont("Label.font");
		setToolTipText("");

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				layoutDirty = true;
				repaint();
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Tile tile = findTile(e.getPoint());
				if (tile != hovered) {
					hovered = tile;
					repaint();
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				if (hovered != null) {
					hovered = null;
					repaint();
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!SwingUtilities.isLeftMouseButton(e)) {
					return;
				}
				Tile tile = findTile(e.getPoint());
				if (tile != null && selectionListener != null) {
					selectionListener.itemSelected(tile.name);
				}
			}
		});
	}

	public void setItems(final Map<String, Double> valuesByName) {
		tiles.clear();
		hovered = null;
		if (valuesByName != null) {
			for (Map.Entry<String, Double> entry : valuesByName.entrySet()) {
				String name = entry.getKey();
				if (name == null || name.isEmpty()) {
					name = "Unknown";
				}
				double value = entry.getValue() == null ? 0.0 : entry.getValue();
				if (value <= 0) {
					continue;
				}
				tiles.add(new Tile(name, value));
			}
		}
		Collections.sort(tiles, new Comparator<Tile>() {
			@Override
			public int compare(Tile o1, Tile o2) {
				return Double.compare(o2.value, o1.value);
			}
		});
		layoutDirty = true;
		repaint();
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		Tile tile = findTile(event.getPoint());
		if (tile == null) {
			return null;
		}
		return "<html><b>" + escapeHtml(tile.name) + "</b><br>" + Formatter.iskFormat(tile.value) + "</html>";
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (tiles.isEmpty()) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		updateLayoutIfNeeded();
		for (Tile tile : tiles) {
			if (tile.rect == null || tile.rect.width <= 0 || tile.rect.height <= 0) {
				continue;
			}
			Rectangle2D.Double paintRect = inset(tile.rect, GAP);
			if (paintRect.width <= 0 || paintRect.height <= 0) {
				continue;
			}
			g2.setColor(tile.color);
			g2.fill(paintRect);
			paintOverlay(g2, paintRect);
			g2.setColor(tile.borderColor);
			g2.draw(paintRect);
			paintLabel(g2, tile, paintRect);
		}
		if (hovered != null && hovered.rect != null) {
			Rectangle2D.Double paintRect = inset(hovered.rect, GAP);
			g2.setColor(hovered.highlightColor);
			g2.draw(paintRect);
		}
		g2.dispose();
	}

	private void updateLayoutIfNeeded() {
		Dimension size = getSize();
		if (size.width <= 0 || size.height <= 0) {
			return;
		}
		if (!layoutDirty && lastSize != null && lastSize.equals(size)) {
			return;
		}
		lastSize = size;
		layoutDirty = false;
		layoutTiles(size);
	}

	private void layoutTiles(Dimension size) {
		double total = 0.0;
		for (Tile tile : tiles) {
			total += tile.value;
			tile.rect = null;
		}
		if (total <= 0) {
			return;
		}
		Insets insets = getInsets();
		double x = insets.left;
		double y = insets.top;
		double width = size.getWidth() - insets.left - insets.right;
		double height = size.getHeight() - insets.top - insets.bottom;
		if (width <= 0 || height <= 0) {
			return;
		}
		double aspect = width / height;
		if (aspect > MAX_TILE_ASPECT_RATIO) {
			double newWidth = height * MAX_TILE_ASPECT_RATIO;
			x += (width - newWidth) / 2.0;
			width = newWidth;
		} else if ((1.0 / aspect) > MAX_TILE_ASPECT_RATIO) {
			double newHeight = width * MAX_TILE_ASPECT_RATIO;
			y += (height - newHeight) / 2.0;
			height = newHeight;
		}
		double area = width * height;
		for (Tile tile : tiles) {
			tile.area = tile.value / total * area;
		}
		List<Tile> row = new ArrayList<>();
		RowLayout rowLayout = null;
		int index = 0;
		while (index < tiles.size()) {
			Tile tile = tiles.get(index);
			if (row.isEmpty()) {
				row.add(tile);
				rowLayout = chooseRowLayout(row, width, height);
				if (!rowLayout.feasible) {
					rowLayout = forceLayout(row, width, height);
				}
				index++;
				continue;
			}
			row.add(tile);
			double effectiveSide = effectiveSide(width, height);
			double worstAfter = worst(row, effectiveSide);
			RowLayout candidate = chooseRowLayout(row, width, height);
			if (row.size() == 1 || (candidate.feasible && worstAfter <= MAX_ASPECT_RATIO)) {
				rowLayout = candidate;
				index++;
			} else {
				row.remove(row.size() - 1);
				if (rowLayout == null || !rowLayout.feasible) {
					rowLayout = forceLayout(row, width, height);
				}
				layoutRow(row, x, y, width, height, rowLayout);
				if (rowLayout.horizontal) {
					y += rowLayout.thickness;
					height -= rowLayout.thickness;
				} else {
					x += rowLayout.thickness;
					width -= rowLayout.thickness;
				}
				row.clear();
				rowLayout = null;
			}
		}
		if (!row.isEmpty()) {
			if (rowLayout == null || !rowLayout.feasible) {
				rowLayout = chooseRowLayout(row, width, height);
				if (!rowLayout.feasible) {
					rowLayout = forceLayout(row, width, height);
				}
			}
			layoutRow(row, x, y, width, height, rowLayout);
		}
	}

	private void layoutRow(List<Tile> row, double x, double y, double width, double height, RowLayout layout) {
		if (row.isEmpty()) {
			return;
		}
		if (layout.horizontal) {
			double rowHeight = layout.thickness;
			double currentX = x;
			for (int i = 0; i < row.size(); i++) {
				Tile tile = row.get(i);
				double tileWidth = tile.area / rowHeight;
				if (tileWidth < 0) {
					tileWidth = 0;
				}
				tile.rect = new Rectangle2D.Double(currentX, y, tileWidth, rowHeight);
				currentX += tileWidth;
			}
		} else {
			double rowWidth = layout.thickness;
			double currentY = y;
			for (int i = 0; i < row.size(); i++) {
				Tile tile = row.get(i);
				double tileHeight = tile.area / rowWidth;
				if (tileHeight < 0) {
					tileHeight = 0;
				}
				tile.rect = new Rectangle2D.Double(x, currentY, rowWidth, tileHeight);
				currentY += tileHeight;
			}
		}
	}

	private RowLayout chooseRowLayout(List<Tile> row, double width, double height) {
		RowLayout horizontal = computeRowLayout(row, width, height, true);
		RowLayout vertical = computeRowLayout(row, width, height, false);
		if (horizontal.feasible && !vertical.feasible) {
			return horizontal;
		}
		if (!horizontal.feasible && vertical.feasible) {
			return vertical;
		}
		if (!horizontal.feasible && !vertical.feasible) {
			if (horizontal.maxRatio < vertical.maxRatio) {
				return horizontal;
			}
			if (vertical.maxRatio < horizontal.maxRatio) {
				return vertical;
			}
			return width >= height ? horizontal : vertical;
		}
		if (horizontal.maxRatio < vertical.maxRatio) {
			return horizontal;
		}
		if (vertical.maxRatio < horizontal.maxRatio) {
			return vertical;
		}
		return width >= height ? horizontal : vertical;
	}

	private RowLayout computeRowLayout(List<Tile> row, double width, double height, boolean horizontal) {
		if (row.isEmpty() || width <= 0 || height <= 0) {
			return new RowLayout(false, horizontal, 0.0, Double.MAX_VALUE);
		}
		double rowArea = sumArea(row);
		if (rowArea <= 0) {
			return new RowLayout(false, horizontal, 0.0, Double.MAX_VALUE);
		}
		double thickness = horizontal ? (rowArea / width) : (rowArea / height);
		if (thickness <= 0) {
			return new RowLayout(false, horizontal, 0.0, Double.MAX_VALUE);
		}
		double maxRatio = maxRatioForThickness(row, thickness);
		boolean feasible = maxRatio <= MAX_TILE_ASPECT_RATIO;
		return new RowLayout(feasible, horizontal, thickness, maxRatio);
	}

	private RowLayout forceLayout(List<Tile> row, double width, double height) {
		if (row.isEmpty() || width <= 0 || height <= 0) {
			return new RowLayout(false, true, 0.0, Double.MAX_VALUE);
		}
		double rowArea = sumArea(row);
		boolean horizontal = width >= height;
		double thickness = horizontal ? (rowArea / width) : (rowArea / height);
		double maxRatio = maxRatioForThickness(row, thickness);
		return new RowLayout(true, horizontal, thickness, maxRatio);
	}

	private double maxRatioForThickness(List<Tile> row, double thickness) {
		if (row.isEmpty() || thickness <= 0) {
			return Double.MAX_VALUE;
		}
		double maxRatio = 0.0;
		for (Tile tile : row) {
			if (tile.area <= 0) {
				continue;
			}
			double ratio = tile.area / (thickness * thickness);
			if (ratio < 1.0) {
				ratio = 1.0 / ratio;
			}
			maxRatio = Math.max(maxRatio, ratio);
		}
		return maxRatio;
	}

	private double sumArea(List<Tile> tiles) {
		double sum = 0.0;
		for (Tile tile : tiles) {
			sum += tile.area;
		}
		return sum;
	}

	private double worst(List<Tile> tiles, double side) {
		double min = Double.MAX_VALUE;
		double max = 0.0;
		double total = 0.0;
		for (Tile tile : tiles) {
			min = Math.min(min, tile.area);
			max = Math.max(max, tile.area);
			total += tile.area;
		}
		if (min == 0 || total == 0) {
			return Double.MAX_VALUE;
		}
		double sideSquared = side * side;
		double totalSquared = total * total;
		double ratio1 = sideSquared * max / totalSquared;
		double ratio2 = totalSquared / (sideSquared * min);
		return Math.max(ratio1, ratio2);
	}

	private double effectiveSide(double width, double height) {
		double shortSide = Math.min(width, height);
		double longSide = Math.max(width, height);
		if (shortSide <= 0) {
			return shortSide;
		}
		double aspect = longSide / shortSide;
		double bias = 1.0 + Math.min(0.5, (aspect - 1.0) * 0.25);
		return shortSide * bias;
	}

	private static class RowLayout {
		private final boolean feasible;
		private final boolean horizontal;
		private final double thickness;
		private final double maxRatio;

		private RowLayout(boolean feasible, boolean horizontal, double thickness, double maxRatio) {
			this.feasible = feasible;
			this.horizontal = horizontal;
			this.thickness = thickness;
			this.maxRatio = maxRatio;
		}
	}

	private Tile findTile(Point point) {
		updateLayoutIfNeeded();
		for (Tile tile : tiles) {
			if (tile.rect != null && tile.rect.contains(point)) {
				return tile;
			}
		}
		return null;
	}

	private void paintLabel(Graphics2D g2, Tile tile, Rectangle2D.Double rect) {
		if (labelFont != null) {
			g2.setFont(labelFont);
		}
		FontMetrics metrics = g2.getFontMetrics();
		int maxWidth = (int) (rect.width - LABEL_PADDING * 2);
		int maxHeight = (int) (rect.height - LABEL_PADDING * 2);
		if (maxWidth <= 10 || maxHeight < metrics.getHeight()) {
			return;
		}
		g2.setColor(tile.textColor);
		String name = truncate(tile.name, metrics, maxWidth);
		if (!name.isEmpty()) {
			int x = (int) (rect.x + LABEL_PADDING);
			int y = (int) (rect.y + LABEL_PADDING + metrics.getAscent());
			g2.drawString(name, x, y);
		}
		if (maxHeight >= metrics.getHeight() * 2 + 2) {
			String value = Formatter.iskFormat(tile.value);
			String valueText = truncate(value, metrics, maxWidth);
			if (!valueText.isEmpty()) {
				int x = (int) (rect.x + LABEL_PADDING);
				int y = (int) (rect.y + LABEL_PADDING + metrics.getAscent() + metrics.getHeight() + 2);
				g2.drawString(valueText, x, y);
			}
		}
	}

	private void paintOverlay(Graphics2D g2, Rectangle2D.Double rect) {
		if (rect.width <= 4 || rect.height <= 4) {
			return;
		}
		double radius = Math.max(rect.width, rect.height) / 2.0;
		if (radius <= 1) {
			return;
		}
		Point2D center = new Point2D.Double(rect.getCenterX(), rect.getCenterY());
		RadialGradientPaint paint = new RadialGradientPaint(
			center,
			(float) radius,
			new float[] {0f, 1f},
			new Color[] {new Color(0, 0, 0, OVERLAY_ALPHA_CENTER), new Color(0, 0, 0, OVERLAY_ALPHA_EDGE)}
		);
		Graphics2D gOverlay = (Graphics2D) g2.create();
		gOverlay.setClip(rect);
		gOverlay.setPaint(paint);
		gOverlay.fill(rect);
		gOverlay.dispose();
	}

	private Rectangle2D.Double inset(Rectangle2D.Double rect, double inset) {
		double value = inset / 2.0;
		double x = rect.x + value;
		double y = rect.y + value;
		double width = rect.width - inset;
		double height = rect.height - inset;
		return new Rectangle2D.Double(x, y, width, height);
	}

	private String truncate(String text, FontMetrics metrics, int maxWidth) {
		if (text == null) {
			return "";
		}
		if (metrics.stringWidth(text) <= maxWidth) {
			return text;
		}
		String ellipsis = "...";
		int ellipsisWidth = metrics.stringWidth(ellipsis);
		int available = maxWidth - ellipsisWidth;
		if (available <= 0) {
			return "";
		}
		int length = text.length();
		while (length > 0 && metrics.stringWidth(text.substring(0, length)) > available) {
			length--;
		}
		if (length <= 0) {
			return "";
		}
		return text.substring(0, length) + ellipsis;
	}

	private String escapeHtml(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	private static Color colorForName(String name) {
		int hash = name.hashCode();
		float hue = ((hash >>> 16) & 0xFF) / 255f;
		float saturation = 0.45f + ((hash >>> 8) & 0x7F) / 255f * 0.35f;
		float brightness = 0.85f;
		return Color.getHSBColor(hue, saturation, brightness);
	}

	private static Color darken(Color color, double factor) {
		int r = (int) Math.max(0, color.getRed() * factor);
		int g = (int) Math.max(0, color.getGreen() * factor);
		int b = (int) Math.max(0, color.getBlue() * factor);
		return new Color(r, g, b, color.getAlpha());
	}

	private static boolean isDark(Color color) {
		double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255.0;
		return luminance < 0.5;
	}

	private static class Tile {
		private final String name;
		private final double value;
		private final Color color;
		private final Color borderColor;
		private final Color highlightColor;
		private final Color textColor;
		private double area;
		private Rectangle2D.Double rect;

		private Tile(String name, double value) {
			this.name = name;
			this.value = value;
			this.color = colorForName(name);
			this.borderColor = darken(color, 0.85);
			this.highlightColor = darken(color, 0.65);
			this.textColor = isDark(color) ? Color.WHITE : Color.BLACK;
		}
	}
}
