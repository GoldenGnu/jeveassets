/*
 * Copyright 2009-2023 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared.filter;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.SettingsUpdateListener;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil.HelpLink;


class FilterGui<E> {

	private enum FilterGuiAction {
		ADD,
		CLEAR,
		SAVE,
		MANAGER,
		SHOW_FILTERS,
		EXPORT,
		MANUAL_FILTER,
		MANUAL_TOOL
	}

	private final JPanel jPanel;
	private final GroupLayout layout;
	private final JFixedToolBar jToolBar;
	private final JButton jExportButton;
	private final JDropDownButton jExportMenu;
	private final JDropDownButton jLoadFilter;
	private final JButton jFilterHelp;
	private final JDropDownButton jHelp;
	private final JMenuItem jToolHelpMenuItem;
	private final JCheckBox jShowFilters;
	private final JLabel jShowing;
	private final JFrame jFrame;

	private final FilterControl<E> filterControl;
	private final SimpleTableFormat<E> tableFormat;

	private final List<FilterPanel<E>> filterPanels = new ArrayList<>();
	private final FilterSave filterSave;
	private final FilterManager<E> filterManager;

	private final ExportDialog<E> exportDialog;
	private boolean multiUpdate = false;
	private HelpLink helpLink = null;

	private final ListenerClass listener = new ListenerClass();

	protected FilterGui(final JFrame jFrame, final FilterControl<E> filterControl, SimpleTableFormat<E> tableFormat) {
		this.jFrame = jFrame;
		this.filterControl = filterControl;
		this.tableFormat = tableFormat;

		exportDialog = new ExportDialog<>(jFrame, filterControl.getName(), filterControl, filterControl, tableFormat, filterControl.getExportEventList());

		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		jToolBar = new JFixedToolBar();

		//Add
		JButton jAddField = new JButton(GuiShared.get().addField());
		jAddField.setToolTipText(GuiShared.get().addFieldToolTip());
		jAddField.setIcon(Images.EDIT_ADD.getIcon());
		jAddField.setActionCommand(FilterGuiAction.ADD.name());
		jAddField.addActionListener(listener);
		jToolBar.addButton(jAddField);

		//Reset
		JButton jClearFields = new JButton(GuiShared.get().clearField());
		jClearFields.setToolTipText(GuiShared.get().clearFieldToolTip());
		jClearFields.setIcon(Images.FILTER_CLEAR.getIcon());
		jClearFields.setActionCommand(FilterGuiAction.CLEAR.name());
		jClearFields.addActionListener(listener);
		jToolBar.addButton(jClearFields);

		jToolBar.addSeparator();

		//Save Filter
		JButton jSaveFilter = new JButton(GuiShared.get().saveFilter());
		jSaveFilter.setToolTipText(GuiShared.get().saveFilterToolTip());
		jSaveFilter.setIcon(Images.FILTER_SAVE.getIcon());
		jSaveFilter.setActionCommand(FilterGuiAction.SAVE.name());
		jSaveFilter.addActionListener(listener);
		jToolBar.addButton(jSaveFilter);

		//Load Filter
		jLoadFilter = new JDropDownButton(GuiShared.get().loadFilter());
		jLoadFilter.setToolTipText(GuiShared.get().loadFilterToolTip());
		jLoadFilter.setIcon(Images.FILTER_LOAD.getIcon());
		jLoadFilter.keepVisible(2);
		jLoadFilter.setTopFixedCount(2);
		jLoadFilter.setInterval(125);
		jToolBar.addButton(jLoadFilter);

		jToolBar.addSeparator();

		//Export
		jExportButton = new JButton(GuiShared.get().export());
		jExportButton.setToolTipText(GuiShared.get().exportToolTip());
		jExportButton.setIcon(Images.DIALOG_CSV_EXPORT.getIcon());
		jExportButton.setActionCommand(FilterGuiAction.EXPORT.name());
		jExportButton.addActionListener(listener);
		jToolBar.addButton(jExportButton);

		jExportMenu = new JDropDownButton(GuiShared.get().export(), Images.DIALOG_CSV_EXPORT.getIcon());
		jExportMenu.setToolTipText(GuiShared.get().exportToolTip());
		jExportMenu.setVisible(false);
		jToolBar.addButton(jExportMenu);

		JMenuItem jExportMenuItem = new JMenuItem(GuiShared.get().exportTableData());
		jExportMenuItem.setIcon(Images.DIALOG_CSV_EXPORT.getIcon());
		jExportMenuItem.setActionCommand(FilterGuiAction.EXPORT.name());
		jExportMenuItem.addActionListener(listener);
		jExportMenu.add(jExportMenuItem);

		jToolBar.addSeparator();

		//Show Filters
		jShowFilters = new JCheckBox(GuiShared.get().showFilters());
		jShowFilters.setToolTipText(GuiShared.get().showFiltersToolTip());
		jShowFilters.setActionCommand(FilterGuiAction.SHOW_FILTERS.name());
		jShowFilters.addActionListener(listener);
		jShowFilters.setSelected(true);
		jToolBar.add(jShowFilters);

		jToolBar.addGlue(5);

		//Showing
		jShowing = new JLabel();
		jShowing.setHorizontalAlignment(JLabel.RIGHT);
		jToolBar.add(jShowing);

		jToolBar.addSpace(5);

		jHelp = new JDropDownButton(Images.MISC_HELP.getIcon());
		jHelp.setVisible(false);
		jToolBar.addButtonIcon(jHelp);

		JMenuItem jFilterHelpMenuItem = new JMenuItem(GuiShared.get().helpFilter(), Images.MISC_HELP.getIcon());
		jFilterHelpMenuItem.setActionCommand(FilterGuiAction.MANUAL_FILTER.name());
		jFilterHelpMenuItem.addActionListener(listener);
		jHelp.add(jFilterHelpMenuItem);

		jToolHelpMenuItem = new JMenuItem(Images.SETTINGS_TOOLS.getIcon());
		jToolHelpMenuItem.setActionCommand(FilterGuiAction.MANUAL_TOOL.name());
		jToolHelpMenuItem.addActionListener(listener);
		jHelp.add(jToolHelpMenuItem);

		jFilterHelp = new JButton(Images.MISC_HELP.getIcon());
		jFilterHelp.setActionCommand(FilterGuiAction.MANUAL_FILTER.name());
		jFilterHelp.addActionListener(listener);
		jToolBar.addButtonIcon(jFilterHelp);

		updateFilters();
		add();

		filterSave = new FilterSave(jFrame);
		filterManager = new FilterManager<>(jFrame, filterControl.getName(), this, tableFormat.getAllColumns(), filterControl.getFilters(), filterControl.getDefaultFilters());
	}

	protected JPanel getPanel() {
		return jPanel;
	}

	public void updateColumns() {
		for (FilterPanel<E> filterPanel : filterPanels) {
			filterPanel.updateColumns();
		}
	}

	/***
	 * @return Is the filter panel shown
	 */
	public boolean isFilterShown() {
		return jShowFilters.isSelected();
	}

	/***
	 * @param shown Whether to show the filter panel
	 */
	public void setFilterShown(boolean shown) {
		jShowFilters.setSelected(shown);
		update();
	}

	protected final void addExportOption(final JMenuItem jMenuItem) {
		if (!jExportMenu.isVisible()) { //First
			jExportMenu.setVisible(true);
			jExportButton.setVisible(false);
		}
		jExportMenu.add(jMenuItem);
	}

	protected final void setManualLink(final HelpLink helpLink, Icon icon) {
		this.helpLink = helpLink;
		if (helpLink != null) {
			jToolHelpMenuItem.setText(helpLink.getTitle());
			jToolHelpMenuItem.setIcon(icon);
			jHelp.setVisible(true);
			jFilterHelp.setVisible(false);
		} else {
			jHelp.setVisible(false);
			jFilterHelp.setVisible(true);
		}
	}

	protected void updateShowing() {
		jShowing.setText(GuiShared.get().filterShowing(EventListManager.size(filterControl.getFilterList()), EventListManager.size(filterControl.getEventList()), getCurrentFilterName()));
	}

	protected String getCurrentFilterName() {
		if (isFiltersEmpty()) {
			return GuiShared.get().filterEmpty();
		}
		List<Filter> filters = getFilters();
		if (filterControl.getAllFilters().containsValue(filters)) {
			for (Map.Entry<String, List<Filter>> entry : filterControl.getAllFilters().entrySet()) {
				if (entry.getValue().equals(filters)) {
					return entry.getKey();
				}
			}
		}
		return GuiShared.get().filterUntitled();
	}

	protected List<Filter> getFilters() {
		List<Filter> filters = new ArrayList<>();
		for (FilterPanel<E> filterPanel : filterPanels) {
			filters.add(filterPanel.getFilter());
		}
		return filters;
	}

	protected boolean isFiltersEmpty() {
		//One filter that is empty. Otherwise not empty
		return filterPanels.size() == 1 && filterPanels.get(0).getFilter().isEmpty();
	}

	private List<FilterMatcher<E>> getMatchers() {
		List<FilterMatcher<E>> matchers = new ArrayList<>();
		for (FilterPanel<E> filterPanel : filterPanels) {
			FilterMatcher<E> matcher = filterPanel.getMatcher();
			if (!matcher.isEmpty()) {
				matchers.add(matcher);
			}
		}
		return matchers;
	}

	protected void update() {
		update(true);
	}

	private void update(boolean sort) {
		//Save focus owner
		Component focusOwner = jFrame.getFocusOwner();
		//Update group
		updateGroupSize();
		jPanel.removeAll();
		GroupLayout.ParallelGroup horizontalGroup = layout.createParallelGroup();
		GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
		//Toolbars
		horizontalGroup.addComponent(jToolBar, jToolBar.getMinimumSize().width, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE);
		verticalGroup.addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
		//Filters
		if (jShowFilters.isSelected()) {
			if (sort) {
				Collections.sort(filterPanels);
			}
			int group = -1;
			for (FilterPanel<E> filterPanel : filterPanels) {
				if (!filterPanel.isMoving()) {
					if (group > -1 && group != filterPanel.getGroup()) {
						FilterPanelSeparator separator = new FilterPanelSeparator(group);
						horizontalGroup.addComponent(separator.getPanel());
						verticalGroup.addGap(0).addComponent(separator.getPanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addGap(0);
					}
					group = filterPanel.getGroup();
				}
				horizontalGroup.addComponent(filterPanel.getPanel());
				verticalGroup.addComponent(filterPanel.getPanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
			}
		}
		layout.setHorizontalGroup(horizontalGroup);
		layout.setVerticalGroup(verticalGroup);
		//Load focus owner
		if (focusOwner != null) {
			focusOwner.requestFocusInWindow();
		}
	}

	protected void updateGroupSize() {
		Set<Integer> groups = new HashSet<>();
		for (FilterPanel<E> filterPanel : filterPanels) {
			if (!filterPanel.isAnd()) {
				groups.add(filterPanel.getGroup());
			}
		}
		int groupSize = groups.size() + 1;
		for (FilterPanel<E> filterPanel : filterPanels) {
			filterPanel.updateGroupSize(groupSize);
		}
	}

	protected int getFromIndex(FilterPanel<E> filterPanel) {
		return filterPanels.indexOf(filterPanel);
	}

	protected int getToIndex(FilterPanel<E> filterPanel) {
		List<FilterPanel<E>> list = new ArrayList<>(filterPanels);
		Collections.sort(list);
		return list.indexOf(filterPanel);
	}

	protected void move(FilterPanel<E> filterPanel, int index) {
		filterPanels.remove(filterPanel);
		filterPanels.add(index, filterPanel);
		update(false);
	}

	protected boolean fade(FilterPanel<E> filterPanel) {
		int index = filterPanels.indexOf(filterPanel);
		List<FilterPanel<E>> list = new ArrayList<>(filterPanels);
		Collections.sort(list);
		return index != list.indexOf(filterPanel);
	}

	protected void clone(final FilterPanel<E> filterPanel) {
		int index = filterPanels.indexOf(filterPanel);
		FilterPanel<E> clone = new FilterPanel<>(this, filterControl, tableFormat);
		clone.setFilter(filterPanel.getFilter());
		filterPanels.add(index, clone);
		if (!multiUpdate) {
			update();
		}
	}

	protected void remove(final FilterPanel<E> filterPanel) {
		filterPanels.remove(filterPanel);
		if (!multiUpdate) {
			update();
		}
	}

	private void add() {
		add(new FilterPanel<>(this, filterControl, tableFormat));
	}

	private void add(final FilterPanel<E> filterPanel) {
		filterPanels.add(filterPanel);
		if (!multiUpdate) {
			update();
		}
	}

	protected void addEmpty() {
		if (filterPanels.isEmpty()) {
			add();
		}
	}

	private void clearEmpty() {
		if (isFiltersEmpty()) {
			remove(filterPanels.get(0));
		}
	}

	protected void clear() {
		multiUpdate = true;
		while (!filterPanels.isEmpty()) {
			remove(filterPanels.get(0));
		}
		addEmpty();
		multiUpdate = false;
		update();
		refilter();
	}

	private void loadFilter(final String filterName, final boolean add) {
		if (filterName == null) {
			return;
		}
		if (filterControl.getAllFilters().containsKey(filterName)) {
			List<Filter> filters = filterControl.getAllFilters().get(filterName);
			if (add) {
				addFilters(filters);
			} else {
				setFilters(filters);
			}
		}
	}

	protected void setFilters(final List<Filter> filters) {
		multiUpdate = true;
		while (!filterPanels.isEmpty()) {
			remove(filterPanels.get(0));
		}
		multiUpdate = false;
		addFilters(filters);
	}

	protected void addFilter(final Filter filter) {
		addFilters(Collections.singletonList(filter));
	}

	protected void addFilters(final List<Filter> filters) {
		multiUpdate = true;
		clearEmpty(); //Remove single empty filter...
		for (Filter filter : filters) {
			FilterPanel<E> filterPanel = new FilterPanel<>(this, filterControl, tableFormat);
			filterPanel.setFilter(filter);
			add(filterPanel);
		}
		addEmpty(); //Add single filter (if empty)
		multiUpdate = false;
		update();
		refilter();
	}

	protected final void updateFilters() {
		jLoadFilter.removeAll();
		JMenuItem jMenuItem;

		jMenuItem = new JMenuItem(GuiShared.get().manageFilters(), Images.DIALOG_SETTINGS.getIcon());
		jMenuItem.setActionCommand(FilterGuiAction.MANAGER.name());
		jMenuItem.addActionListener(listener);
		jMenuItem.setRolloverEnabled(true);
		jLoadFilter.add(jMenuItem);

		List<String> filters = new ArrayList<>(filterControl.getFilters().keySet());
		Collections.sort(filters, new CaseInsensitiveComparator());

		List<String> defaultFilters = new ArrayList<>(filterControl.getDefaultFilters().keySet());
		Collections.sort(defaultFilters, new CaseInsensitiveComparator());

		if (!filters.isEmpty() || !defaultFilters.isEmpty()) {
			jLoadFilter.addSeparator();
		}

		for (String s : defaultFilters) {
			jMenuItem = new JMenuItem(s, Images.FILTER_LOAD_DEFAULT.getIcon());
			jMenuItem.setRolloverEnabled(true);
			jMenuItem.setActionCommand(s);
			jMenuItem.addActionListener(listener);
			jLoadFilter.add(jMenuItem);
		}

		for (String s : filters) {
			jMenuItem = new JMenuItem(s, Images.FILTER_LOAD.getIcon());
			jMenuItem.setRolloverEnabled(true);
			jMenuItem.setActionCommand(s);
			jMenuItem.addActionListener(listener);
			jLoadFilter.add(jMenuItem);
		}
		updateShowing();
		filterControl.updateFilters();
	}

	protected void saveSettings(String msg) {
		filterControl.saveSettings(msg);
	}

	protected void refilter() {
		filterControl.beforeFilter();
		List<FilterMatcher<E>> matchers = getMatchers();
		boolean empty = true;
		for (FilterMatcher<E> matcher : matchers) {
			if (!matcher.isEmpty()) {
				empty = false;
				break;
			}
		}
		if (empty) {
			filterControl.getFilterList().setMatcher(null);
		} else {
			filterControl.getFilterList().setMatcher(new FilterLogicalMatcher<>(matchers));
		}

		filterControl.afterFilter();
		updateShowing();
		fireSettingsUpdate();
	}

	protected String getFilterName() {
		return filterSave.show(new ArrayList<>(filterControl.getFilters().keySet()), new ArrayList<>(filterControl.getDefaultFilters().keySet()));
	}

	/**
	 * Loop though set update listeners and trigger their action.
	 */
	private void fireSettingsUpdate() {
		for (SettingsUpdateListener updateListener : filterControl.getSettingsUpdateListenerList()) {
			updateListener.settingChanged();
		}
	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (FilterGuiAction.ADD.name().equals(e.getActionCommand())) {
				add();
				updateShowing();
				fireSettingsUpdate();
			} else if (FilterGuiAction.CLEAR.name().equals(e.getActionCommand())) {
				clear();
			} else if (FilterGuiAction.MANAGER.name().equals(e.getActionCommand())) {
				filterManager.setVisible(true);
			} else if (FilterGuiAction.SHOW_FILTERS.name().equals(e.getActionCommand())) {
				update();
				Settings.get().getCurrentTableFiltersShown().put(filterControl.getName(), isFilterShown());
				fireSettingsUpdate();
			} else if (FilterGuiAction.SAVE.name().equals(e.getActionCommand())) {
				if (isFiltersEmpty()) {
					JOptionPane.showMessageDialog(jFrame, GuiShared.get().nothingToSave(), GuiShared.get().saveFilter(), JOptionPane.PLAIN_MESSAGE);
				} else {
					String name = filterSave.show(new ArrayList<>(filterControl.getFilters().keySet()), new ArrayList<>(filterControl.getDefaultFilters().keySet()));
					if (name != null && !name.isEmpty()) {
						Settings.lock("Filter (New)"); //Lock for Filter (New)
						filterControl.getFilters().put(name, getFilters());
						Settings.unlock("Filter (New)"); //Unlock for Filter (New)
						saveSettings("Filter (New)"); //Save Filter (New)
						updateFilters();
					}
				}
			} else if (FilterGuiAction.EXPORT.name().equals(e.getActionCommand())) {
				exportDialog.setVisible(true);
			} else if (FilterGuiAction.MANUAL_FILTER.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(new HelpLink("https://wiki.jeveassets.org/manual/filters", GuiShared.get().helpFilter()), jFrame);
			} else if (FilterGuiAction.MANUAL_TOOL.name().equals(e.getActionCommand())) {
				DesktopUtil.browse(helpLink, jFrame);
			} else {
				loadFilter(e.getActionCommand(), (e.getModifiers() & ActionEvent.CTRL_MASK) != 0);
			}
		}
	}

}
