/*
 * Copyright 2009, 2010
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

import java.awt.Color;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import net.nikr.eve.jeveasset.gui.images.ImageGetter;


public abstract class UpdateTask extends SwingWorker<Void, Void> implements PropertyChangeListener {

	private boolean done = false;
	private Throwable throwable = null;
	private JLabel jText;
	private JTextPane jError;
	private List<String> errors;
	private String name;

	public UpdateTask(String name) {
		this.name = name;
		this.addPropertyChangeListener(this);
		jText = new JLabel(name);
		jText.setIcon( ImageGetter.getIcon("bullet_black.png"));

		jError = new JTextPane();
		jError.setEditable(false);
		jError.setFocusable(false);
		jError.setVisible(false);

		errors = new ArrayList<String>();
	}

	public JLabel getTextLabel(){
		return jText;
	}
	public JTextPane getErrorLabel(){
		return jError;
	}

	public void addError(String error){
		errors.add(error);
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

	public void setTaskProgress(float end, float done, int start, int max){
		int progress = Math.round(((done/end)*(max-start))+start);
		this.setProgress(progress);
	}

	public void cancelled(){
		jText.setIcon( ImageGetter.getIcon("bullet_red.png"));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		int value = getProgress();
		if (value == 100){
			if (errors.isEmpty()){
				jText.setIcon( ImageGetter.getIcon("bullet_green.png"));
			} else if (isCancelled()){
				jText.setIcon( ImageGetter.getIcon("bullet_orange.png"));
			} else {
				jText.setIcon( ImageGetter.getIcon("bullet_error.png"));
			}
			showError();
		} else {
			jText.setIcon( ImageGetter.getIcon("bullet_go.png"));
		}
	}

	private void showError(){
		if (!errors.isEmpty()){
			jText.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			jText.setText(name+" (click for more information)");
			StyledDocument doc = new DefaultStyledDocument();
			SimpleAttributeSet italic = new SimpleAttributeSet();
			italic.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);
			italic.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.GRAY);
			SimpleAttributeSet bold = new SimpleAttributeSet();
			bold.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
			try {
				doc.insertString(doc.getLength(), "Failed to update:", bold);
				for (int a = 0; a < errors.size(); a++){
					String error = errors.get(a);
					if (error.contains("(")){
						doc.insertString(doc.getLength(), "\r\n  ", null);
						doc.insertString(doc.getLength(), error.substring(0, error.indexOf("(")), bold);
						doc.insertString(doc.getLength(), error.substring(error.indexOf("(")), null);
					} else {
						doc.insertString(doc.getLength(), "\r\n  " + error, bold);
					}
				}
				doc.insertString(doc.getLength(), "\r\n(see log.txt for details)", italic);
			} catch (BadLocationException badLocationException) {

			}
			jError.setDocument(doc);
			jError.setBackground(jText.getBackground());
			jError.setForeground(jText.getForeground());
			jError.setFont(jText.getFont());
		}
	}
}
