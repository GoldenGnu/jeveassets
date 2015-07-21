/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.nikr.eve.jeveasset.data;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.DebugList;
import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.Program;

/**
 *
 * @author Niklas
 */
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
