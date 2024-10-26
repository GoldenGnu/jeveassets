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
package net.nikr.eve.jeveasset.gui.shared;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import net.nikr.eve.jeveasset.data.settings.types.MarketDetailType;
import net.nikr.eve.jeveasset.gui.shared.components.JButtonNull;


public class MarketDetailsColumn {
	public static <E extends MarketDetailType> void install(EventList<E> eventList, MarketDetailsActionListener<E> marketDetailsActionListener) {
		eventList.addListEventListener(new ListEventListener<E>() {
			@Override @SuppressWarnings("deprecation")
			public void listChanged(ListEvent<E> listChanges) {
				try {
					eventList.getReadWriteLock().readLock().lock();
					while(listChanges.next()) {
						switch (listChanges.getType()) {
							case ListEvent.DELETE:
								E oldMarketDetails = listChanges.getOldValue();
								JButton jOldButton = oldMarketDetails.getButton();
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
								E newMarketDetails = eventList.get(index);
								JButton jNewButton = newMarketDetails.getButton();
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
										marketDetailsActionListener.openMarketDetails(newMarketDetails);
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

	public static interface MarketDetailsActionListener<E extends MarketDetailType> {
		public void openMarketDetails(E marketDetails);
	}
}
