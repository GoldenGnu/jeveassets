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

package net.nikr.eve.jeveasset.gui.frame;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.ColorUtil;
import net.nikr.eve.jeveasset.gui.shared.CopyHandler;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JGroupLayoutPanel;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.i18n.DialoguesStructure;
import net.nikr.eve.jeveasset.i18n.GuiFrame;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabPriceHistory;
import net.nikr.eve.jeveasset.i18n.TabsOrders;


public class StatusPanel extends JGroupLayoutPanel {

	//GUI
	private final JButton jUpdate;
	private final JLabel jEveTime;
	private final JLabel jApiUpdate;
	private final JFixedToolBar jToolBar;
	private final Timer eveTimer;
	private final Timer updateTimer;

	private final List<Progress> progressStatus = Collections.synchronizedList(new ArrayList<>());
	private final List<JLabel> programStatus = new ArrayList<>();

	public StatusPanel(final Program program) {
		super(program);

		ListenerClass listener = new ListenerClass();

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		jToolBar = new JFixedToolBar();

		boolean update = canUpdate();
		jUpdate = new JButton(GuiFrame.get().programUpdateText(), Images.MISC_UPDATE.getIcon());
		jUpdate.setToolTipText(GuiFrame.get().programUpdateTip());
		jUpdate.setVisible(update);
		jUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				program.update();
			}
		});
		updateTimer = new Timer(3600000, new ActionListener() { //Check for updates once an hour
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean update = canUpdate();
				jUpdate.setVisible(update);
				if (update) {
					doLayout();
					updateTimer.stop(); //Update found, no reason to keep checking for updates
				}
			}
		});
		if (!update) { //Update found, no reason to keep checking for updates
			updateTimer.start();
		}

		jApiUpdate = createIcon(Images.DIALOG_UPDATE.getIcon(), GuiFrame.get().updatable());
		programStatus.add(jApiUpdate);

		jEveTime = createLabel(GuiFrame.get().eve(), Images.MISC_EVE.getIcon(), null);
		programStatus.add(jEveTime);

		eveTimer = new Timer(1000, listener);
		eveTimer.start();

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
		);
	}

	public Progress addProgress(UpdateType updateType, ProgressControl progressShow) {
		Progress progress = new Progress(updateType, progressShow);
		progressStatus.add(progress);
		return progress;
	}

	public boolean updateing(UpdateType updateType) {
		synchronized (progressStatus) {
			for (Progress progress : progressStatus) {
				if (progress.getTaskType() == updateType) {
					return true;
				}
			}
		}
		return false;
	}

	public int updateInProgress() {
		return progressStatus.size();
	}

	public void cancelUpdates() {
		synchronized (progressStatus) {
			for (Progress progress : progressStatus) {
				progress.cancel();
			}
		}
	}

	public void setPauseUpdates(boolean pause) {
		synchronized (progressStatus) {
			for (Progress progress : progressStatus) {
				progress.setPause(pause);
			}
		}
	}

	public void removeProgress(Progress progress) {
		progressStatus.remove(progress);
		doLayout();
	}

	public void tabChanged() {
		doLayout();
	}

	private boolean canUpdate() {
		return (program.checkProgramUpdate() || program.checkDataUpdate()) && !Program.isDevBuild();
	}

	private void doLayout() {
		jToolBar.removeAll();
		addSpace(5);
		if (jUpdate.isVisible()) {
			jToolBar.add(jUpdate);
		}
		synchronized (progressStatus) {
			for (Progress progress : progressStatus) {
				if (progress.isVisible()) {
					jToolBar.add(progress.getProgress());
					addSpace(10);
				}
			}
		}
		for (JLabel jLabel : programStatus) {
			jToolBar.add(jLabel);
			if (jLabel instanceof JStatusLabel) {
				addSpace(6);
			} else {
				addSpace(8);
			}
		}
		for (JLabel jLabel : program.getMainWindow().getSelectedTab().getStatusbarLabels()) {
			jToolBar.add(jLabel);
			if (jLabel instanceof JStatusLabel) {
				addSpace(6);
			} else {
				addSpace(8);
			}
		}
		addSpace(10);
		this.getPanel().updateUI();

	}

	public void timerTicked(final boolean updatable) {
		if (updatable) {
			jApiUpdate.setIcon(Images.DIALOG_UPDATE.getIcon());
			jApiUpdate.setToolTipText(GuiFrame.get().updatable());
		} else {
			jApiUpdate.setIcon(Images.DIALOG_UPDATE_DISABLED.getIcon());
			jApiUpdate.setToolTipText(GuiFrame.get().not());
		}
	}

	public static JLabel createIcon(final Icon icon, final String toolTip) {
		JLabel jLabel = new JLabel();
		jLabel.setIcon(icon);
		jLabel.setHorizontalAlignment(JLabel.CENTER);
		jLabel.setToolTipText(toolTip);
		return jLabel;
	}

	public static JStatusLabel createLabel(final String toolTip, final Icon icon, final AutoNumberFormat format) {
		return new JStatusLabel(toolTip, icon, format);
	}

	private void addSpace(final int width) {
		JLabel jSpace = new JLabel();
		jSpace.setMinimumSize(new Dimension(width, 25));
		jSpace.setPreferredSize(new Dimension(width, 25));
		jSpace.setMaximumSize(new Dimension(width, 25));
		jToolBar.add(jSpace);
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			jEveTime.setText(Formater.eveTime(Settings.getNow()));
		}
	}

	public class Progress {

		private final UpdateType updateType;
		private final String text;
		private final ProgressControl progressControl;
		private final JProgressBar jProgress;
		private final long id = System.currentTimeMillis();
		private boolean done = false;

		public Progress(UpdateType updateType, ProgressControl progressShow) {
			this.updateType = updateType;
			this.progressControl = progressShow;
			switch (updateType) {
				case STRUCTURE:
					text = DialoguesStructure.get().updateTitle();
					break;
				case PUBLIC_MARKET_ORDERS:
					text = TabsOrders.get().updateTitle();
					break;
				case PRICE_HISTORY:
					text = TabPriceHistory.get().updateTitle();
					break;
				default:
					text = "";
			}
			jProgress = new JProgressBar(0, 100);
			Dimension size = new Dimension(Program.getButtonsWidth() * 2, Program.getButtonsHeight());
			jProgress.setMinimumSize(size);
			jProgress.setPreferredSize(size);
			jProgress.setMaximumSize(size);
			jProgress.setVisible(false);

			jProgress.setStringPainted(true);
			if (progressShow.isAuto()) {
				jProgress.setString(text);
			} else {
				jProgress.setString(GuiFrame.get().clickToShow(text));
				jProgress.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						progressShow.show();
					}
				});
			}
		}

		public void setDone(boolean done) {
			this.done = done;
			if (done) {
				jProgress.setString(GuiFrame.get().clickToApply(text));
				jProgress.setValue(100);
			}
		}

		public boolean isDone() {
			return done;
		}

		public boolean isVisible() {
			return jProgress.isVisible();
		}

		public void setVisible(boolean aFlag) {
			jProgress.setVisible(aFlag);
			doLayout();
		}

		public void setIndeterminate(boolean newValue) {
			jProgress.setIndeterminate(newValue);
		}

		public void setValue(int n) {
			jProgress.setValue(n);
		}

		private UpdateType getTaskType() {
			return updateType;
		}

		private void cancel() {
			progressControl.cancel();
		}

		public void setPause(boolean pause) {
			progressControl.setPause(pause);
		}

		private JProgressBar getProgress() {
			return jProgress;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 41 * hash + (int) (this.id ^ (this.id >>> 32));
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Progress other = (Progress) obj;
			if (this.id != other.id) {
				return false;
			}
			return true;
		}
	}

	public static class JStatusLabel extends JLabel {

		private final AutoNumberFormat format;
		private String text = null;
		private Number number = null;

		public JStatusLabel(final String toolTip, final Icon icon, AutoNumberFormat format) {
			this.format = format;
			init(toolTip, icon);
		}

		private void init(final String toolTip, final Icon icon) {
			setIcon(icon);
			if (ColorUtil.isBrightColor(getBackground())) { //Light background color
				setForeground(getBackground().darker().darker().darker());
			} else { //Dark background color
				setForeground(getBackground().brighter().brighter());
			}
			setIconTextGap(3);
			setToolTipText(GuiShared.get().clickToCopyWrap(toolTip));
			setHorizontalAlignment(JLabel.LEFT);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (number != null) {
							CopyHandler.toClipboard(Formater.copyFormat(number));
						} else {
							CopyHandler.toClipboard(text);
						}
						final Icon restore;
						if (icon == null) {
							restore = getIcon();
						} else {
							restore = icon;
						}
						JStatusLabel.super.setText(GuiShared.get().selectionCopiedToClipboard());
						setIcon(Images.EDIT_COPY.getIcon());
						final Timer timer = new Timer(JMenuInfo.COPY_DELAY, null);
						timer.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								updateText();
								setIcon(restore);
								timer.stop();
							}
						});
						timer.start();
					}
				}
			});
		}

		@Override
		public void setText(String text) {
			this.text = text;
			this.number = null;
			updateText();
		}

		public void setNumber(Number number) {
			this.text = null;
			this.number = number;
			updateText();
		}

		public void setNumber(String text, double number) {
			this.text = text;
			this.number = number;
			updateText();
		}

		public void updateText() {
			if (text != null && number != null && format != null) {
				super.setText(text + JMenuInfo.format(number, format));
			} else if (text != null) {
				super.setText(text);
			} else if (number != null && format != null) {
				super.setText(JMenuInfo.format(number, format));
			} else {
				super.setText("");
			}
		}
	}

	public static interface ProgressControl {
		public boolean isAuto();
		public void show();
		public void cancel();
		public void setPause(boolean pause);
	}

	public static enum UpdateType {
		STRUCTURE, PUBLIC_MARKET_ORDERS, PRICE_HISTORY
	}
}
