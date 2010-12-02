package net.nikr.eve.jeveasset.gui.shared.table;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Candle
 */
public class EnumTableFormatAdaptor<T extends Enum<T> & TableColumn<Q>, Q> implements AdvancedTableFormat<Q> {
	private static final Logger LOG = LoggerFactory.getLogger(EnumTableFormatAdaptor.class);
	T[] values;

	public EnumTableFormatAdaptor(Class<T> enumClass) {
		this.values = enumClass.getEnumConstants();
	}
	
	private T[] getTypeArray() {
		return values;
	}

	public List<T> getColumns() {
		return Collections.unmodifiableList(Arrays.asList(getTypeArray()));
	}

	private T getColumnEnum(int i) {
		return getTypeArray()[i];
	}

	@Override public Class getColumnClass(int i) {
		return getColumnEnum(i).getType();
	}

	@Override public Comparator getColumnComparator(int i) {
		return getColumnEnum(i).getComparator();
	}

	@Override public int getColumnCount() {
		return getTypeArray().length;
	}

	@Override public String getColumnName(int i) {
		return getColumnEnum(i).getColumnName();
	}

	@Override public Object getColumnValue(Q e, int i) {
		return getColumnEnum(i).getColumnValue(e);
	}
}
