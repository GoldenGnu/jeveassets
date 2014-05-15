/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.dialogs.update;

import java.awt.Cursor;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.text.*;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class UpdateTask extends SwingWorker<Void, Void> {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateTask.class);

	private final JLabel jText;
	private final List<ErrorClass> errors;
	private final String name;

	private boolean errorShown = false;
	private boolean taskDone = false;

	public UpdateTask(final String name) {
		this.name = name;
		this.addPropertyChangeListener(new ListenerClass());
		jText = new JLabel(name);
		jText.setIcon(Images.UPDATE_NOT_STARTED.getIcon());

		errors = new ArrayList<ErrorClass>();
	}

	public String getName() {
		return name;
	}

	public JLabel getTextLabel() {
		return jText;
	}

	public void addError(final String owner, final String error) {
		errors.add(new ErrorClass(owner, error));
	}

	public boolean hasError() {
		return !errors.isEmpty();
	}

	public abstract void update();

	@Override
	public Void doInBackground() {
		setProgress(0);
		update();
		return null;
	}

	@Override
	public void done() {
		try {
			get();
		} catch (CancellationException ex) {
			LOG.info("Update cancelled by user");
		} catch (Exception ex) { //InterruptedException, ExecutionException
			LOG.error(ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}
		taskDone = true;
		setProgress(100);
	}

	public boolean isTaskDone() {
		return taskDone;
	}

	public void setTaskDone(final boolean done) {
		this.taskDone = done;
	}

	protected void setTaskProgress(final int progress) {
		this.setProgress(progress);
	}

	public void setError(final JTextPane jError) {
		if (!errors.isEmpty()) {
			StyledDocument doc = new DefaultStyledDocument();
			SimpleAttributeSet errorAttributeSet = new SimpleAttributeSet();
			errorAttributeSet.addAttribute(StyleConstants.CharacterConstants.Foreground, jText.getBackground().darker().darker());

			try {
				boolean first = true;
				for (ErrorClass errorClass : errors) {
					if (first) {
						first = false;
					} else {
						doc.insertString(doc.getLength(), "\n\r", null);
					}
					doc.insertString(doc.getLength(), errorClass.getOwner(), null);
					doc.insertString(doc.getLength(), "\r\n" + processError(errorClass.getError()), errorAttributeSet);
				}
			} catch (BadLocationException ex) {
				LOG.warn("Ignoring exception: " + ex.getMessage(), ex);
			}
			jError.setDocument(doc);
		}
	}

	public void setTaskProgress(final float end, final float done, final int start, final int max) {
		int progress = Math.round(((done / end) * (max - start)) + start);
		this.setProgress(progress);
	}

	public void cancelled() {
		jText.setIcon(Images.UPDATE_CANCELLED.getIcon());
	}

	public void showError(final boolean b) {
		if (!errors.isEmpty()) {
			Font font = jText.getFont();
			if (b) {
				errorShown = true;
				jText.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
				jText.setText(DialoguesUpdate.get().clickToHide(name));
			} else {
				errorShown = false;
				jText.setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));
				jText.setText(DialoguesUpdate.get().clickToShow(name));
			}
		}
	}

	public boolean isErrorShown() {
		return errorShown;
	}

	private String processError(String error) {
		if (error == null) {
			return "";
		}
		Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Matcher m = p.matcher(error);
		while (m.find()) {
			int start = m.start();
			int end = m.end();
			String time = error.substring(start, end);
			try {
				Date date = df.parse(time);
				time = Formater.weekdayAndTime(date);
			} catch (ParseException ex) {
				time = error.substring(start, end);
			}
			error = error.substring(0, start) + time + error.substring(end);
			error = error.replace("retry after", "\r\n" + DialoguesUpdate.get().nextUpdate());
		}
		return error;
	}

	private class ListenerClass implements PropertyChangeListener {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			int value = getProgress();
			if (value == 100) {
				if (errors.isEmpty()) {
					jText.setIcon(Images.UPDATE_DONE_OK.getIcon());
				} else if (isCancelled()) {
					jText.setIcon(Images.UPDATE_DONE_SOME.getIcon());
				} else {
					jText.setIcon(Images.UPDATE_DONE_ERROR.getIcon());
				}
				if (!errors.isEmpty()) {
					jText.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					jText.setText(DialoguesUpdate.get().clickToShow(name));
				}
			} else {
				jText.setIcon(Images.UPDATE_WORKING.getIcon());
			}
		}
	}

	private static class ErrorClass {
		private final String owner;
		private final String error;

		public ErrorClass(String owner, String error) {
			this.owner = owner;
			this.error = error;
		}

		public String getOwner() {
			return owner;
		}

		public String getError() {
			return error;
		}
	}
}
