/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import org.slf4j.LoggerFactory;


public class DetectEdtViolationRepaintManager extends RepaintManager {

	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Program.class);

	/**
	 * Used to ensure we only print a stack trace once per abusing thread. May
	 * be null if the option is disabled.
	 */
	private ThreadLocal<Boolean> alreadyWarnedLocal;

	/**
	 * Installs a new instance of DetectEdtViolationRepaintManager which does
	 * not warn repeatedly, as the current repaint manager.
	 */
	public static void install() {
		install(false);
	}

	/**
	 * Installs a new instance of DetectEdtViolationRepaintManager as the
	 * current repaint manager.
	 *
	 * @param warnRepeatedly whether multiple warnings should be logged for each
	 * violating thread
	 */
	public static void install(boolean warnRepeatedly) {
		RepaintManager.setCurrentManager(new DetectEdtViolationRepaintManager(warnRepeatedly));
		LOG.info("Installed new DetectEdtViolationRepaintManager");
	}

	/**
	 * Creates a new instance of DetectEdtViolationRepaintManager.
	 *
	 * @param warnRepeatedly whether multiple warnings should be logged for each
	 * violating thread
	 */
	private DetectEdtViolationRepaintManager(boolean warnRepeatedly) {
		if (!warnRepeatedly) {
			this.alreadyWarnedLocal = new ThreadLocal<Boolean>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void addInvalidComponent(JComponent component) {
		checkThreadViolations();
		super.addInvalidComponent(component);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void addDirtyRegion(JComponent component, int x, int y,
			int w, int h) {
		checkThreadViolations();
		super.addDirtyRegion(component, x, y, w, h);
	}

	/**
	 * Checks if the calling thread is called in the event dispatch thread. If
	 * not an exception will be printed to the console.
	 */
	private void checkThreadViolations() {
		if (alreadyWarnedLocal != null && Boolean.TRUE.equals(alreadyWarnedLocal.get())) {
			return;
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			if (alreadyWarnedLocal != null) {
				alreadyWarnedLocal.set(Boolean.TRUE);
			}
			LOG.warn("painting on non-EDT thread", new Exception());
		}
	}
}
