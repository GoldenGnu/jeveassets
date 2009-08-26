/*
 * Copyright 2009
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

import javax.swing.SwingWorker;


public abstract class UpdateTask extends SwingWorker<Void, Void> {

	private boolean done = false;
	private Throwable throwable = null;

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

}
