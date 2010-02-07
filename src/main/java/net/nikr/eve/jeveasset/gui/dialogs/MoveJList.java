package net.nikr.eve.jeveasset.gui.dialogs;

import javax.swing.JList;
import javax.swing.ListModel;
import net.nikr.log.Log;

public class MoveJList<T> extends JList {
	private static final long serialVersionUID = 1l;

	public MoveJList() {
		setModel(new EditableListModel<T>());
	}

	@SuppressWarnings("unchecked") // dealing with the non-generics ListModel
	public MoveJList(ListModel dataModel) {
		EditableListModel<T> m = new EditableListModel<T>();
		for (int i = 0; i < m.getSize(); ++i) {
			m.add((T) m.getElementAt(i));
		}
		setModel(m);
	}

	@SuppressWarnings("unchecked") // dealing with the non-generics ListModel/JList
	public EditableListModel<T> getEditableModel() {
		return (EditableListModel<T>) getModel();
	}

	/**
	 *
	 * @param to
	 * @param limit
	 * @return true if all the items were added.
	 */
	@SuppressWarnings("unchecked") // dealing with the non-generics ListModel/JList
	public boolean move(MoveJList<T> to, int limit) {
		EditableListModel<T> fModel = getEditableModel();
		EditableListModel<T> tModel = to.getEditableModel();
		for (Object obj : getSelectedValues()) {
			T ss = (T) obj;
			if (fModel.contains(ss)) {
				if (to.getModel().getSize() < limit) {
					Log.debug("Moving " + ss);
					if (fModel.remove(ss)) {
						tModel.add(ss);
					}
				} else {
					setSelectedIndices(new int[]{});
					to.setSelectedIndices(new int[]{});
					return false;
				}
			}
		}
		setSelectedIndices(new int[]{});
		to.setSelectedIndices(new int[]{});
		return true;
	}
}
