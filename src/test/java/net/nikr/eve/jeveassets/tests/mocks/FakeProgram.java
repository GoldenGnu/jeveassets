/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveassets.tests.mocks;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.matchers.MatcherEditor.Event;
import java.awt.Window;
import java.awt.event.ActionEvent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.tabs.assets.FiltersManagerDialog;
import net.nikr.eve.jeveasset.gui.tabs.assets.SaveFilterDialog;
import net.nikr.eve.jeveasset.gui.tabs.assets.AssetsTab;
import net.nikr.eve.jeveasset.gui.frame.MainWindow;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;

/**
 * any method called will throw an exception. extend and override only the ones that are needed to perform the tests.
 * @author Candle
 */
public abstract class FakeProgram extends Program {

	public FakeProgram() {
		super(false);
	}

	@Override
	public Settings getSettings() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public AssetsTab getAssetsTab() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void changedMatcher(Event<EveAsset> matcherEvent) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void checkForProgramUpdates(Window parent) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void exit() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void savedFiltersChanged() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public EventList<EveAsset> getEveAssetEventList() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public FiltersManagerDialog getFiltersManagerDialog() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public MainWindow getMainWindow() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public String getProgramDataVersion() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public SaveFilterDialog getSaveFilterDialog() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public StatusPanel getStatusPanel() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void saveSettings() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void showAbout() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void showSettings() {
		throw new UnsupportedOperationException("Not implemented");
	}
}
