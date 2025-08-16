/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import java.util.List;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;


public class EventModels {

	//EventModels.createTableModel
	public static <E> DefaultEventTableModel<E> createTableModel(EventList<E> source, TableFormat<E> tableFormat) {
		// XXX - Workaround for java bug: https://bugs.openjdk.java.net/browse/JDK-8068824
		//return new DefaultEventTableModel<E>(createSwingThreadProxyList(source), tableFormat);
		return new FixedEventTableModel<>(createSwingThreadProxyList(source), tableFormat);
	}

	//EventModels.createSelectionModel
	public static <E> DefaultEventSelectionModel<E> createSelectionModel(EventList<E> source) {
		return new DefaultEventSelectionModel<>(createSwingThreadProxyList(source));
	}

	public static <E> EventList<E> createSwingThreadProxyList(EventList<E> source) {
		final EventList<E> result;
		source.getReadWriteLock().readLock().lock();
		try {
			result = GlazedListsSwing.swingThreadProxyList(source);
		} finally {
			source.getReadWriteLock().readLock().unlock();
		}
		return result;
	}

	// XXX - Workaround for java bug: https://bugs.openjdk.java.net/browse/JDK-8068824
	public static class FixedEventTableModel<E> extends DefaultEventTableModel<E> {

		private JTable jTable;

		public FixedEventTableModel(EventList<E> source, TableFormat<E> tableFormat) {
			super(source, tableFormat);
		}

		public FixedEventTableModel(EventList<E> source, boolean disposeSource, TableFormat<E> tableFormat) {
			super(source, disposeSource, tableFormat);
		}

		public void setTable(JTable jTable) {
			this.jTable = jTable;
		}

		@Override
		public void fireTableStructureChanged() {
			if (jTable != null) {
				jTable.getTableHeader().setDraggedColumn(null);
			}
			super.fireTableStructureChanged();
		}

	}

	public static class StringFilterator implements TextFilterator<String> {
		@Override
		public void getFilterStrings(final List<String> baseList, final String element) {
			if (element.length() > 0) {
				baseList.add(element);
			}
		}
	}

	public static class LocationFilterator implements TextFilterator<MyLocation> {
		@Override
		public void getFilterStrings(final List<String> baseList, final MyLocation element) {
			baseList.add(element.getLocation());
		}
	}

	public static class ItemFilterator implements TextFilterator<Item> {
		@Override
		public void getFilterStrings(List<String> baseList, Item element) {
			baseList.add(element.getTypeName());
		}
	}

	public static class ViewFilterator implements TextFilterator<View> {
		@Override
		public void getFilterStrings(final List<String> baseList, final View element) {
			baseList.add(element.getName());
		}
	}

	public static class SolarSystemFilterator implements TextFilterator<SolarSystem> {
		@Override
		public void getFilterStrings(final List<String> baseList, final SolarSystem element) {
			baseList.add(element.getName());
		}
	}
}