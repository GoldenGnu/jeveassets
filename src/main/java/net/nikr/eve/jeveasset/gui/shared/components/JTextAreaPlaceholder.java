/*
 * Copyright 2009-2024 Contributors (see credits.txt)
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
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * JTextArea with information text displayed when empty.
 */
public class JTextAreaPlaceholder extends JTextArea {
	/**
	 * Placeholder font.
	 */
	private Font placeholderFont = null;
	/**
	 * Placeholder italic font.
	 */
	private boolean italic = true;
	/**
	 * Placeholder mock used for painting.
	 */
	private JTextArea jPlaceholder;
	/**
	 * Text to display when empty.
	 */
	private String placeholderText;
	/**
	 * Paint placeholder.
	 */
	private boolean paintPlaceholder;

	/**
	 * Create a textfield with hint.
	 */
	public JTextAreaPlaceholder() {
		this(null, null);
	}

	public JTextAreaPlaceholder(String placeholder) {
		this(null, placeholder);
	}
	/**
	 * Create a textfield with hint.
	 *
	 * @param text
	 * @param placeholder Text displayed when empty
	 */
	public JTextAreaPlaceholder(String text, String placeholder) {
		super(text);
		if (placeholder == null) {
			placeholder = "";
		}
		this.placeholderText = placeholder;
		getMock().setOpaque(false);
		getMock().setBackground(new Color(0,0,0,0)); //Nimbus LaF
		getMock().setForeground(getDisabledTextColor());
		placeholderFont = getFont();
		updatePlaceholderBorder();
		updatePlaceholderFont();

		addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				updateShown();
			}

			@Override
			public void focusGained(FocusEvent e) {
				updateShown();
			}
		});
		getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateShown();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateShown();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateShown();
			}
		});
		addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateSize();
			}

			@Override
			public void componentMoved(ComponentEvent e) { }

			@Override
			public void componentShown(ComponentEvent e) {
				updateSize();
			}

			@Override
			public void componentHidden(ComponentEvent e) { }
		});
	}

	private JTextArea getMock() {
		if (jPlaceholder == null) {
			jPlaceholder = new JTextArea();
		}
		return jPlaceholder;
	}

	@Override
	public void setFont(Font f) {
		placeholderFont = f;
		updatePlaceholderFont();
		super.setFont(f);
	}

	@Override
	public void setColumns(int columns) {
		getMock().setColumns(columns);
		super.setColumns(columns);
	}

	@Override
	public void setRows(int rows) {
		getMock().setRows(rows);
		super.setRows(rows);
	}

	@Override
	public void setTabSize(int size) {
		getMock().setTabSize(size);
		super.setTabSize(size);
	}

	@Override
	public void setWrapStyleWord(boolean word) {
		getMock().setWrapStyleWord(word);
		super.setWrapStyleWord(word);
	}

	@Override
	public void setLineWrap(boolean wrap) {
		getMock().setLineWrap(wrap);
		super.setLineWrap(wrap);
	}

	@Override
	public void setBorder(Border border) {
		super.setBorder(border);
		updatePlaceholderBorder();
	}

	private void updatePlaceholderBorder() {
		Insets insets = getInsets();
		getMock().setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
		revalidate();
		repaint();
	}

	private void updateShown() {
		updateShown(false);
	}

	private void updateShown(boolean forceRepaint) {
		boolean paint = getText().isEmpty() && placeholderText != null && !placeholderText.isEmpty();
		boolean repaint = forceRepaint || paint != paintPlaceholder;
		getMock().setText(placeholderText);
		paintPlaceholder = paint;
		if (repaint) {
			revalidate();
			repaint();
		}
	}

	private void updateSize() {
		Dimension size = getSize();
		boolean repaint = getMock().getSize() != size;
		getMock().setSize(size);
		if (repaint) {
			revalidate();
			repaint();
		}
	}

	public void setPlaceholderText(String placeholder) {
		this.placeholderText = placeholder;
		updateShown(true);
	}

	public void setPlaceholderForeground(Color fg) {
		getMock().setForeground(fg);
		revalidate();
		repaint();
	}

	public boolean isPlaceholderItalic() {
		return italic;
	}

	public void setPlaceholderFont(Font f) {
		placeholderFont = f;
		updatePlaceholderFont();
	}

	public void setPlaceholderItalic(boolean italic) {
		this.italic = italic;
		updatePlaceholderFont();
	}

	private void updatePlaceholderFont() {
		if (italic) {
			getMock().setFont(placeholderFont.deriveFont(Font.ITALIC));
		} else {
			getMock().setFont(placeholderFont);
		}
		revalidate();
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		if (paintPlaceholder) {
			super.paint(g);
			getMock().paint(g);
		} else {
			super.paint(g);
		}
	}
}
