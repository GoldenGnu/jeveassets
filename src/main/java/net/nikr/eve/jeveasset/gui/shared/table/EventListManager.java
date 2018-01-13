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
package net.nikr.eve.jeveasset.gui.shared.table;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.DebugList;
import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.Program;


public class EventListManager<E> {
	public EventList<E> create() {
		if (Program.isDebug()) {
			DebugList<E> debugList = new DebugList<E>();
			debugList.setLockCheckingEnabled(true);
			return debugList;
		} else {
			return new BasicEventList<E>();
		}
	}
	public static <E> List<E> safeList(EventList<E> eventList) {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return new ArrayList<E>(eventList);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}
	public static <E> boolean isEmpty(EventList<E> eventList) {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return eventList.isEmpty();
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}
	public static <E> boolean contains(EventList<E> eventList, E e) {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return eventList.contains(e);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}
	public static <E> int size(EventList<E> eventList) {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return eventList.size();
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}
	public static <E> E get(EventList<E> eventList, int index) {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return eventList.get(index);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}
	public static <E> int indexOf(EventList<E> eventList, Object object) {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return eventList.indexOf(object);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}
}
