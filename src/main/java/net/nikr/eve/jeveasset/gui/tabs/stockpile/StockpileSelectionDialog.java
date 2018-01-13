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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionList;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class StockpileSelectionDialog extends JDialogCentered {

	private enum StockpileSelection {
		OK, CANCEL
	}

	//GUI
	private final JMultiSelectionList<Stockpile> jList;
	private final JButton jOK;

	//Data
	private List<Stockpile> stockpiles;

	public StockpileSelectionDialog(final Program program) {
		super(program, TabsStockpile.get().selectStockpiles());

		ListenerClass listener = new ListenerClass();

		jList = new JMultiSelectionList<Stockpile>();
		jList.addListSelectionListener(listener);
		JScrollPane jListScroll = new JScrollPane(jList);

		jOK = new JButton(TabsStockpile.get().ok());
		jOK.setActionCommand(StockpileSelection.OK.name());
		jOK.addActionListener(listener);
		jOK.setEnabled(false);

		JButton jCancel = new JButton(TabsStockpile.get().cancel());
		jCancel.setActionCommand(StockpileSelection.CANCEL.name());
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(jListScroll, 300, 300, 300)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
					.addComponent(jCancel, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jListScroll, 200, 200, 200)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCancel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
		);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jOK;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	public List<Stockpile> show(List<Stockpile> stockpiles) {
		jList.setModel(new DataListModel<Stockpile>(stockpiles));
		this.stockpiles = null;
		this.setVisible(true);
		return this.stockpiles;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		stockpiles = new ArrayList<Stockpile>(jList.getSelectedValuesList());
		this.setVisible(false);
	}

	private class ListenerClass implements ListSelectionListener, ActionListener {
		@Override
		public void valueChanged(final ListSelectionEvent e) {
			jOK.setEnabled(jList.getSelectedIndices().length > 0);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (StockpileSelection.OK.name().equals(e.getActionCommand())) {
				save();
			}
			if (StockpileSelection.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			}
		}
	}

	private static class DataListModel<T> extends AbstractListModel<T> {

		private final List<T> data;

		public DataListModel(final List<T> data) {
			this.data = data;
		}

		@Override
		public int getSize() {
			return data.size();
		}

		@Override
		public T getElementAt(final int index) {
			return data.get(index);
		}
	}
}
