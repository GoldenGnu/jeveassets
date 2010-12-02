package net.nikr.eve.jeveasset.gui.shared.table;

import java.util.Comparator;

/**
 *
 * @author Candle
 */
public interface TableColumn<Q> {
	Class getType();
	Comparator getComparator();
	String getColumnName();
	Object getColumnValue(Q from);
}
