/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LookAndFeelPreview {

	private static final Logger LOG = LoggerFactory.getLogger(LookAndFeelPreview.class);

	public static void install(String lookAndFeelClass, JDropDownButton jDropDownButton, JComponent jComponent, SettingsDialog settingsDialog) {
		LookAndFeelPreviewListener listener = new LookAndFeelPreviewListener(lookAndFeelClass, jDropDownButton, jComponent, settingsDialog);
	}

	private LookAndFeelPreview() { }

	private static class LookAndFeelPreviewListener implements PopupMenuListener, MouseListener, WindowListener {
		private static final PreviewMock previewWindowMock = new PreviewMock();
		private final String lookAndFeelClass;
		private final JComponent jComponent;
		private final SettingsDialog settingsDialog;
		private BufferedImage bufferedImage;

		public LookAndFeelPreviewListener(String lookAndFeelClass, JDropDownButton jDropDownButton, JComponent jComponent, SettingsDialog settingsDialog) {
			this.lookAndFeelClass = lookAndFeelClass;
			this.jComponent = jComponent;
			this.settingsDialog = settingsDialog;
			jComponent.addMouseListener(this);
			jDropDownButton.getPopupMenu().addPopupMenuListener(this);
			settingsDialog.getDialog().addWindowListener(this);
			settingsDialog.getDialog().setGlassPane(previewWindowMock);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			show();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			hide();
		}

		@Override
		public void mouseClicked(MouseEvent e) { }

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			hide();
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
			hide();
		}

		@Override
		public void windowOpened(WindowEvent e) {
			if (bufferedImage == null) {
				try {
					LookAndFeel currentLAF = UIManager.getLookAndFeel();
					UIManager.setLookAndFeel(lookAndFeelClass);
					PreviewComponent previewComponent = new PreviewComponent();
					SwingUtilities.updateComponentTreeUI(previewComponent.getOuter());
					settingsDialog.addCard(previewComponent.getOuter(), lookAndFeelClass);
					settingsDialog.getDialog().revalidate();
					bufferedImage = createImage(previewComponent.getInner());
					settingsDialog.removeCard(previewComponent.getOuter());
					UIManager.setLookAndFeel(currentLAF);
					SwingUtilities.updateComponentTreeUI(previewWindowMock);
					SwingUtilities.updateComponentTreeUI(previewComponent.getOuter());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
					LOG.error(ex.getMessage(), ex);
				}
			}
		}

		@Override
		public void windowClosing(WindowEvent e) {}

		@Override
		public void windowClosed(WindowEvent e) {}

		@Override
		public void windowIconified(WindowEvent e) {}

		@Override
		public void windowDeiconified(WindowEvent e) {}

		@Override
		public void windowActivated(WindowEvent e) {}

		@Override
		public void windowDeactivated(WindowEvent e) {	}

		private void show() {
			previewWindowMock.config(bufferedImage, jComponent);
			previewWindowMock.setVisible(true);
		}

		private void hide() {
			previewWindowMock.setVisible(false);
		}

	}

	public static BufferedImage createImage(Component comp) {
		int w = comp.getWidth();
		int h = comp.getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		comp.paint(g);
		return bi;
	}

	private static class PreviewComponent {

		private final JPanel jInner;
		private final JPanel jOuter;

		private PreviewComponent() {
			jInner = new JPanel();
			GroupLayout innerLayout = new GroupLayout(jInner);
			jInner.setLayout(innerLayout);
			innerLayout.setAutoCreateGaps(true);
			innerLayout.setAutoCreateContainerGaps(true);

			String[] comboBoxValues = {"ComboBox"};
			JComboBox<String> jComboBox = new JComboBox<>(comboBoxValues);

			JTextField jTextField = new JTextField("TextField");

			JButton jButton = new JButton("Button");

			JLabel jLabel = new JLabel("Label");

			JCheckBox jCheckBox = new JCheckBox("CheckBox");
			jCheckBox.setSelected(true);

			JRadioButton jRadioButton = new JRadioButton("RadioButton");
			jRadioButton.setSelected(true);
			int height = 22;
			height = Math.max(height, new JComboBox<>().getPreferredSize().height);
			height = Math.max(height, new JTextField().getPreferredSize().height);
			height = Math.max(height, new JButton().getPreferredSize().height);

			innerLayout.setHorizontalGroup(
					innerLayout.createSequentialGroup()
						.addGroup(innerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jTextField, 120, 120, 120)
							.addComponent(jLabel, 120, 120, 120)
							.addComponent(jCheckBox, 120, 120, 120)
						)
						.addGroup(innerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jComboBox, 120, 120, 120)
							.addComponent(jButton, 120, 120, 120)
							.addComponent(jRadioButton, 120, 120, 120)
						)
			);
			innerLayout.setVerticalGroup(
					innerLayout.createSequentialGroup()
						.addGroup(innerLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(jTextField, height, height, height)
							.addComponent(jComboBox, height, height, height)
						)
						.addGroup(innerLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(jLabel, height, height, height)
							.addComponent(jButton, height, height, height)
						)
						.addGroup(innerLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(jCheckBox, height, height, height)
							.addComponent(jRadioButton, height, height, height)
						)
			);

			jOuter = new JPanel();
			GroupLayout outerLayout = new GroupLayout(jOuter);
			jOuter.setLayout(outerLayout);
			outerLayout.setAutoCreateGaps(false);
			outerLayout.setAutoCreateContainerGaps(false);

			outerLayout.setHorizontalGroup(
					outerLayout.createSequentialGroup()
						.addComponent(jInner)
						.addGap(0, 0, Integer.MAX_VALUE)
			);
			outerLayout.setVerticalGroup(
					outerLayout.createSequentialGroup()
						.addComponent(jInner)
						.addGap(0, 0, Integer.MAX_VALUE)
			);
		}

		public JPanel getInner() {
			return jInner;
		}

		public JPanel getOuter() {
			return jOuter;
		}

	}

	private static class PreviewMock extends JComponent {

		private static final int OFFSET = 5;
		private static final int BORDER = 2;

		private BufferedImage bufferedImage;
		private JComponent jComponent;
		private Point point;

		private PreviewMock() { }

		public void config(BufferedImage bufferedImage, JComponent jComponent) {
			this.bufferedImage = bufferedImage;
			this.jComponent = jComponent;
			point = SwingUtilities.convertPoint(jComponent, new Point(0, 0), this);
		}

		public BufferedImage getBufferedImage() {
			return bufferedImage;
		}
		@Override
		public void paint(Graphics g) {
			if (bufferedImage != null) {
				Graphics2D graphics2D = (Graphics2D) g;
				graphics2D.setColor(Color.BLACK);
				graphics2D.fillRect(point.x + jComponent.getWidth() + OFFSET, point.y, bufferedImage.getWidth() + BORDER * 2, bufferedImage.getHeight() + BORDER * 2);
				graphics2D.drawImage(bufferedImage, point.x + jComponent.getWidth() + OFFSET + BORDER, point.y + BORDER, null);
			}
		}
	}
}
