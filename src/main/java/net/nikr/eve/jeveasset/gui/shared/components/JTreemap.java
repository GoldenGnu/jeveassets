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
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
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
	private static final double MAX_TILE_ASPECT_RATIO = 4.0;
	private static final int MIN_TILE_SIZE = 6;
	private static final int LABEL_PADDING = 4;
	private static final int AMBIENT_LIGHT = 30;
	private static final double LIGHT_X = 0.09759;
	private static final double LIGHT_Y = 0.19518;
	private static final double LIGHT_Z = 0.9759;
	private static final double CUSHION_HEIGHT = 0.35;

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
			paintCushion(g2, tile, paintRect);
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
		double scale = area / total;
		if (!Double.isFinite(scale) || scale <= 0) {
			return;
		}
		double minValue = MIN_TILE_SIZE / scale;
		List<Tile> layoutTiles = new ArrayList<>();
		for (Tile tile : tiles) {
			if (tile.value >= minValue) {
				layoutTiles.add(tile);
			}
		}
		if (layoutTiles.isEmpty()) {
			return;
		}
		Rectangle2D.Double remaining = new Rectangle2D.Double(x, y, width, height);
		int index = 0;
		while (index < layoutTiles.size()) {
			List<Tile> row = squarifyRow(layoutTiles, index, remaining, scale);
			if (row.isEmpty()) {
				break;
			}
			Rectangle2D.Double next = layoutRow(remaining, scale, row);
			if (next == null) {
				break;
			}
			index += row.size();
			remaining = next;
		}
	}

	private List<Tile> squarifyRow(List<Tile> tiles, int startIndex, Rectangle2D.Double rect, double scale) {
		List<Tile> row = new ArrayList<>();
		double length = Math.max(rect.width, rect.height);
		if (length <= 0 || scale <= 0) {
			return row;
		}
		double scaledLengthSquare = (length * length) / scale;
		double sum = 0.0;
		double lastWorst = -1.0;
		for (int i = startIndex; i < tiles.size(); i++) {
			Tile tile = tiles.get(i);
			double nextSum = sum + tile.value;
			if (nextSum <= 0 || tile.value <= 0) {
				break;
			}
			double sumSquare = nextSum * nextSum;
			double worst = row.isEmpty()
				? Math.max(scaledLengthSquare * tile.value / sumSquare, sumSquare / (scaledLengthSquare * tile.value))
				: Math.max(scaledLengthSquare * row.get(0).value / sumSquare, sumSquare / (scaledLengthSquare * tile.value));
			if (lastWorst >= 0.0 && worst > lastWorst) {
				break;
			}
			lastWorst = worst;
			row.add(tile);
			sum = nextSum;
		}
		if (row.isEmpty() || lastWorst > MAX_TILE_ASPECT_RATIO) {
			row.clear();
		}
		return row;
	}

	private Rectangle2D.Double layoutRow(Rectangle2D.Double rect, double scale, List<Tile> row) {
		if (row.isEmpty()) {
			return null;
		}
		double sum = sumValues(row);
		if (sum <= 0) {
			return null;
		}
		double primary = Math.max(rect.width, rect.height);
		if (primary <= 0) {
			return null;
		}
		double secondary = (sum * scale) / primary;
		if (secondary < MIN_TILE_SIZE) {
			return null;
		}
		boolean horizontal = rect.width >= rect.height;
		double offset = 0.0;
		double remaining = primary;
		for (int i = 0; i < row.size(); i++) {
			Tile tile = row.get(i);
			double childSize = tile.value / sum * primary;
			if (childSize > remaining) {
				childSize = remaining;
			}
			remaining -= childSize;
			if (childSize >= MIN_TILE_SIZE) {
				if (horizontal) {
					tile.rect = new Rectangle2D.Double(rect.x + offset, rect.y, childSize, secondary);
				} else {
					tile.rect = new Rectangle2D.Double(rect.x, rect.y + offset, secondary, childSize);
				}
				offset += childSize;
			}
		}
		if (horizontal) {
			return new Rectangle2D.Double(rect.x, rect.y + secondary, rect.width, rect.height - secondary);
		}
		return new Rectangle2D.Double(rect.x + secondary, rect.y, rect.width - secondary, rect.height);
	}

	private double sumValues(List<Tile> tiles) {
		double sum = 0.0;
		for (Tile tile : tiles) {
			sum += tile.value;
		}
		return sum;
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

	private void paintCushion(Graphics2D g2, Tile tile, Rectangle2D.Double rect) {
		int width = (int) Math.round(rect.width);
		int height = (int) Math.round(rect.height);
		if (width < 2 || height < 2) {
			g2.setColor(tile.color);
			g2.fill(rect);
			return;
		}
		BufferedImage cushion = getCushion(tile, width, height);
		if (cushion == null) {
			g2.setColor(tile.color);
			g2.fill(rect);
			return;
		}
		int x = (int) Math.round(rect.x);
		int y = (int) Math.round(rect.y);
		g2.drawImage(cushion, x, y, width, height, null);
	}

	private BufferedImage getCushion(Tile tile, int width, int height) {
		if (tile.cushion != null && tile.cushionWidth == width && tile.cushionHeight == height) {
			return tile.cushion;
		}
		BufferedImage cushion = renderCushion(tile, width, height);
		tile.cushion = cushion;
		tile.cushionWidth = width;
		tile.cushionHeight = height;
		return cushion;
	}

	private BufferedImage renderCushion(Tile tile, int width, int height) {
		if (width <= 0 || height <= 0) {
			return null;
		}
		double xx2 = 0.0;
		double xx1 = 0.0;
		double yy2 = 0.0;
		double yy1 = 0.0;
		if (width > 1) {
			xx2 = squareRidge(xx2, CUSHION_HEIGHT, 0, width - 1);
			xx1 = linearRidge(xx1, CUSHION_HEIGHT, 0, width - 1);
		}
		if (height > 1) {
			yy2 = squareRidge(yy2, CUSHION_HEIGHT, 0, height - 1);
			yy1 = linearRidge(yy1, CUSHION_HEIGHT, 0, height - 1);
		}
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int maxRed = Math.max(0, tile.color.getRed() - AMBIENT_LIGHT);
		int maxGreen = Math.max(0, tile.color.getGreen() - AMBIENT_LIGHT);
		int maxBlue = Math.max(0, tile.color.getBlue() - AMBIENT_LIGHT);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double nx = 2.0 * xx2 * x + xx1;
				double ny = 2.0 * yy2 * y + yy1;
				double denom = Math.sqrt(nx * nx + ny * ny + 1.0);
				double cosa = (nx * LIGHT_X + ny * LIGHT_Y + LIGHT_Z) / denom;
				int red = (int) (maxRed * cosa + 0.5);
				int green = (int) (maxGreen * cosa + 0.5);
				int blue = (int) (maxBlue * cosa + 0.5);
				if (red < 0) {
					red = 0;
				}
				if (green < 0) {
					green = 0;
				}
				if (blue < 0) {
					blue = 0;
				}
				red = Math.min(255, red + AMBIENT_LIGHT);
				green = Math.min(255, green + AMBIENT_LIGHT);
				blue = Math.min(255, blue + AMBIENT_LIGHT);
				int rgb = (0xFF << 24) | (red << 16) | (green << 8) | blue;
				image.setRGB(x, y, rgb);
			}
		}
		return image;
	}

	private double squareRidge(double squareCoefficient, double height, int x1, int x2) {
		if (x2 != x1) {
			squareCoefficient -= 4.0 * height / (x2 - x1);
		}
		return squareCoefficient;
	}

	private double linearRidge(double linearCoefficient, double height, int x1, int x2) {
		if (x2 != x1) {
			linearCoefficient += 4.0 * height * (x2 + x1) / (x2 - x1);
		}
		return linearCoefficient;
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
		private Rectangle2D.Double rect;
		private BufferedImage cushion;
		private int cushionWidth;
		private int cushionHeight;

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
