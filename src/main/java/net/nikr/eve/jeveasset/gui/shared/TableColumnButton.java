/*
 * Copyright 2009-2026 Contributors (see credits.txt)
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

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import net.nikr.eve.jeveasset.gui.shared.components.JButtonNull;


public class TableColumnButton {
	public static <E> void install(EventList<E> eventList, ButtonActionListener<E> buttonActionListener) {
		eventList.addListEventListener(new ListEventListener<E>() {
			@Override @SuppressWarnings("deprecation")
			public void listChanged(ListEvent<E> listChanges) {
				try {
					eventList.getReadWriteLock().readLock().lock();
					while(listChanges.next()) {
						switch (listChanges.getType()) {
							case ListEvent.DELETE:
								E oldItem = listChanges.getOldValue();
								JButton jOldButton = buttonActionListener.getButton(oldItem);
								//check null button
								if (jOldButton == null || jOldButton instanceof JButtonNull) {
									continue;
								}
								//remove old ActionListener(s)
								for (ActionListener actionListener : jOldButton.getActionListeners()) {
									jOldButton.removeActionListener(actionListener);
								}
								break;
							case ListEvent.INSERT:
								int index = listChanges.getIndex();
								//check index
								if (index < 0 || index >= eventList.size()) {
									continue;
								}
								E newItem = eventList.get(index);
								JButton jNewButton = buttonActionListener.getButton(newItem);
								//check null button
								if (jNewButton == null || jNewButton instanceof JButtonNull) {
									continue;
								}
								//remove old ActionListener(s)
								for (ActionListener actionListener : jNewButton.getActionListeners()) {
									jNewButton.removeActionListener(actionListener);
								}
								//add new ActionListener
								jNewButton.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										buttonActionListener.buttonClicked(newItem);
									}
								});
								break;
						}
					}
				} finally {
					eventList.getReadWriteLock().readLock().unlock();
				}
			}
		});
	}

	public static interface ButtonActionListener<E> {
		public void buttonClicked(E item);
		public JButton getButton(E item);
	}
}
