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
package net.nikr.eve.jeveasset.gui.tabs.prices;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.components.JManageDialog;
import net.nikr.eve.jeveasset.i18n.TabsPriceHistory;


public class JManageItemsDialog extends JManageDialog {

	private final PriceHistoryTab priceHistoryTab;

	public JManageItemsDialog(Program program, PriceHistoryTab priceHistoryTab) {
		super(program, program.getMainWindow().getFrame(), TabsPriceHistory.get().manageTitle(), true, false);
		this.priceHistoryTab = priceHistoryTab;
	}

	@Override
	protected void load(String name) {
		priceHistoryTab.setItems(Settings.get().getPriceHistorySets().get(name));
		setVisible(false);
	}

	@Override
	protected void merge(String name, List<String> list) {
		Set<Integer> typeIDs = new HashSet<>();
		for (String s : list) {
			typeIDs.addAll(Settings.get().getPriceHistorySets().get(s));
			if (typeIDs.size() > priceHistoryTab.MAXIMUM_SHOWN) {
				JOptionPane.showMessageDialog(parent, TabsPriceHistory.get().mergeMax(priceHistoryTab.MAXIMUM_SHOWN), TabsPriceHistory.get().merge(), JOptionPane.PLAIN_MESSAGE);
				return;
			}
		}
		Settings.lock("Price History (Merge Sets)");
		Settings.get().getPriceHistorySets().put(name, typeIDs);
		Settings.unlock("Price History (Merge Sets)");
		program.saveSettings("Price History (Merge Set)");
		updateData();
		priceHistoryTab.updateSaved();
	}

	@Override
	protected void rename(String name, String oldName) {
		Settings.lock("Price History (Rename Set)");
		Set<Integer> typeIDs = Settings.get().getPriceHistorySets().remove(oldName);
		Settings.get().getPriceHistorySets().put(name, typeIDs);
		Settings.unlock("Price History (Rename Set)");
		program.saveSettings("Price History (Rename Set)");
		updateData();
		priceHistoryTab.updateSaved();

	}

	@Override
	protected void delete(List<String> list) {
		Settings.lock("Price History (Delete Sets)");
		Settings.get().getPriceHistorySets().keySet().removeAll(list);
		Settings.unlock("Price History (Delete Sets)");
		program.saveSettings("Price History (Delete Sets)");
		updateData();
		priceHistoryTab.updateSaved();
	}

	@Override
	protected void export(List<String> list) {
		//Export is not supported
	}

	@Override
	protected void importData() {
		//Import is not supported
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			updateData();
		}
		super.setVisible(b);
	}

	private void updateData() {
		update(Settings.get().getPriceHistorySets().keySet());
	}

	@Override protected String textDeleteMultipleMsg(int size) { return TabsPriceHistory.get().deleteHistorySets(size); }
	@Override protected String textDelete() { return TabsPriceHistory.get().deleteHistorySet(); }
	@Override protected String textEnterName() { return TabsPriceHistory.get().enterName(); }
	@Override protected String textNoName() { return TabsPriceHistory.get().empty(); }
	@Override protected String textMerge() { return TabsPriceHistory.get().merge(); }
	@Override protected String textRename() { return TabsPriceHistory.get().rename(); }
	@Override protected String textOverwrite() { return TabsPriceHistory.get().overwrite(); }

}
