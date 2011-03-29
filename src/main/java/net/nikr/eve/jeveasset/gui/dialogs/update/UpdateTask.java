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

package net.nikr.eve.jeveasset.gui.dialogs.update;

import java.awt.Cursor;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.i18n.DialoguesUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class UpdateTask extends SwingWorker<Void, Void> implements PropertyChangeListener {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateTask.class);

	private boolean done = false;
	private Throwable throwable = null;
	private JLabel jText;
	//private JTextPane jError;
	private Map<String, String> errors;
	private String name;
	private boolean errorShown = false;

	

	public UpdateTask(String name) {
		this.name = name;
		this.addPropertyChangeListener(this);
		jText = new JLabel(name);
		jText.setIcon(Images.UPDATE_NOT_STARTED.getIcon());

		errors = new HashMap<String, String>();
	}

	public String getName() {
		return name;
	}

	public JLabel getTextLabel(){
		return jText;
	}

	public void addError(String human, String error){
		errors.put(human, error);
	}

	public boolean hasError(){
		return !errors.isEmpty();
	}

	public abstract void update() throws Throwable;

	@Override
	public Void doInBackground() {
		setProgress(0);
		try {
			update();
		} catch (Throwable ex) {
			throwable = ex;
		}
		return null;
	}

	@Override
	public void done() {
		done = true;
		setProgress(100);
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public boolean isTaskDone(){
		return done;
	}

	public void setTaskDone(boolean done) {
		this.done = done;
	}

	protected void setTaskProgress(int progress){
		this.setProgress(progress);
	}

	public void setError(JTextPane jError){
		if (!errors.isEmpty()){
			StyledDocument doc = new DefaultStyledDocument();
			SimpleAttributeSet errorAttributeSet = new SimpleAttributeSet();
			errorAttributeSet.addAttribute(StyleConstants.CharacterConstants.Foreground, jText.getBackground().darker().darker());

			try {
				boolean first = true;
				for (Map.Entry<String, String> entry : errors.entrySet()){
					if (first){
						first = false;
					} else {
						doc.insertString(doc.getLength(), "\n\r", null);
					}
					doc.insertString(doc.getLength(), entry.getKey(), null);
					doc.insertString(doc.getLength(), "\r\n"+processError(entry.getValue()), errorAttributeSet);
				}
			} catch (BadLocationException ex) {
				LOG.warn("Ignoring exception: " + ex.getMessage(), ex);
			}
			jError.setDocument(doc);
		}
	}

	public void setTaskProgress(float end, float done, int start, int max){
		int progress = Math.round(((done/end)*(max-start))+start);
		this.setProgress(progress);
	}

	public void cancelled(){
		jText.setIcon(Images.UPDATE_CANCELLED.getIcon());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		int value = getProgress();
		if (value == 100){
			if (errors.isEmpty()){
				jText.setIcon(Images.UPDATE_DONE_OK.getIcon());
			} else if (isCancelled()){
				jText.setIcon(Images.UPDATE_DONE_SOME.getIcon());
			} else {
				jText.setIcon(Images.UPDATE_DONE_ERROR.getIcon());
			}
			if (!errors.isEmpty()){
				jText.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				jText.setText(DialoguesUpdate.get().clickToShow(name));
			}
		} else {
			jText.setIcon(Images.UPDATE_WORKING.getIcon());
		}
	}

	public void showError(boolean b){
		if (!errors.isEmpty()){
			Font font = jText.getFont();
			if (b){
				errorShown = true;
				jText.setFont( new Font(font.getName(), Font.BOLD, font.getSize()) );
				jText.setText(DialoguesUpdate.get().clickToHide(name));
				
			} else {
				errorShown = false;
				jText.setFont( new Font(font.getName(), Font.PLAIN, font.getSize()) );
				jText.setText(DialoguesUpdate.get().clickToShow(name));
			}
		}
	}

	public boolean isErrorShown() {
		return errorShown;
	}

	private String processError(String error){
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
			error = error.substring(0, start)+time+" GMT"+error.substring(end);
			error = error.replace("retry after", "\r\n" + DialoguesUpdate.get().nextUpdate());
		}
		return error;
	}
}
