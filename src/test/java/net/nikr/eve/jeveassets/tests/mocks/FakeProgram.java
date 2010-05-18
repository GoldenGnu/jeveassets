package net.nikr.eve.jeveassets.tests.mocks;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.matchers.MatcherEditor.Event;
import java.awt.Window;
import java.awt.event.ActionEvent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.FiltersManagerDialog;
import net.nikr.eve.jeveasset.gui.dialogs.SaveFilterDialog;
import net.nikr.eve.jeveasset.gui.frame.AssetsTab;
import net.nikr.eve.jeveasset.gui.frame.MainWindow;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.ToolPanel;
import net.nikr.eve.jeveasset.gui.shared.JProgramPanel;

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
	public void addPanel(JProgramPanel jProgramPanel) { }

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
	public void filtersChanged() {
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
	public ToolPanel getToolPanel() {
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

	@Override
	public void tableUpdated() {
		throw new UnsupportedOperationException("Not implemented");
	}

}
