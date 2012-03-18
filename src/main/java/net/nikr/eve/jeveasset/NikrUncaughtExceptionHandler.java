/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

import java.util.Locale;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.i18n.General;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NikrUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
	private final static Logger LOG = LoggerFactory.getLogger(NikrUncaughtExceptionHandler.class);
	private static boolean error = false;
	
	public NikrUncaughtExceptionHandler() { }

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if (!error){
			error = true;
			LOG.error("Uncaught Exception (Thread): " + General.get(Locale.ENGLISH).uncaughtErrorMessage(), e);
			JOptionPane.showMessageDialog(null
					, General.get().uncaughtErrorMessage()
					, General.get().error()
					, JOptionPane.ERROR_MESSAGE
					);
			System.exit(-1);
		}
	}
	
	public void handle(Throwable e){
		if (!error){
			//Workaround:
			StackTraceElement[] stackTraceElements = e.getStackTrace();
			if (stackTraceElements.length > 0
							&& stackTraceElements[0].getClassName().equals("sun.font.FontDesignMetrics")
							&& stackTraceElements[0].getLineNumber() == 492
							&& stackTraceElements[0].getMethodName().equals("charsWidth")
							){
				LOG.warn("sun.font.FontDesignMetrics bug detected");
				return;
			}
			error = true;
			LOG.error("Uncaught Exception (sun.awt.exception.handler):"
					+ General.get(Locale.ENGLISH).uncaughtErrorMessage()
					, e);
			JOptionPane.showMessageDialog(null
					, General.get().uncaughtErrorMessage()
					, General.get().error()
					, JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}
}