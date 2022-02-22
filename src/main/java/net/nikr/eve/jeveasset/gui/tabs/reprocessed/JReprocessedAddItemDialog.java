package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import java.util.Comparator;
import java.util.List;

import ca.odell.glazedlists.TextFilterator;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JAutoCompleteDialog;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;

public class JReprocessedAddItemDialog extends JAutoCompleteDialog<Item> {

	public JReprocessedAddItemDialog(final Program program) {
		super(program, TabsReprocessed.get().addItem(), Images.TOOL_REPROCESSED.getImage(), 
				TabsReprocessed.get().selectItem(), false, true);
	}

	@Override
	protected Comparator<Item> getComparator() {
		return new ItemComparator();
	}

	@Override
	protected TextFilterator<Item> getFilterator() {
		return new Filterator();
	}

	@Override
	protected Item getValue(Object object) {
		if(object instanceof Item){
			return (Item)object;
		}
		return null;
	}

	private static class Filterator implements TextFilterator<Item> {
		@Override
		public void getFilterStrings(List<String> baseList, Item element) {
			baseList.add(element.getTypeName());
		}
	}

	private static class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item o1, Item o2) {
			return o1.compareTo(o2);
		}
	}
}
