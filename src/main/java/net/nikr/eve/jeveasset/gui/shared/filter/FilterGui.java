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

package net.nikr.eve.jeveasset.gui.shared.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.i18n.GuiShared;


class FilterGui<E> {

	private enum FilterGuiAction {
		ADD,
		CLEAR,
		SAVE,
		MANAGER,
		SHOW_FILTERS,
		EXPORT
	}

	private final JPanel jPanel;
	private final GroupLayout layout;
	private final JFixedToolBar jToolBarLeft;
	private final JFixedToolBar jToolBarRight;
	private final JButton jExportButton;
	private final JDropDownButton jExportMenu;
	private final JDropDownButton jLoadFilter;
	private final JCheckBox jShowFilters;
	private final JLabel jShowing;
	private final JFrame jFrame;

	private final FilterControl<E> filterControl;

	private final List<FilterPanel<E>> filterPanels = new ArrayList<FilterPanel<E>>();
	private final FilterSave filterSave;
	private final FilterManager<E> filterManager;

	private final ExportDialog<E> exportDialog;

	ListenerClass listener = new ListenerClass();

	FilterGui(final JFrame jFrame, final FilterControl<E> filterControl) {
		this.jFrame = jFrame;
		this.filterControl = filterControl;

		exportDialog = new ExportDialog<E>(jFrame, filterControl.getName(), filterControl, filterControl, Collections.singletonList(filterControl.getExportEventList()), filterControl.getColumns());

		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		jToolBarLeft = new JFixedToolBar();

		//Add
		JButton jAddField = new JButton(GuiShared.get().addField());
		jAddField.setIcon(Images.EDIT_ADD.getIcon());
		jAddField.setActionCommand(FilterGuiAction.ADD.name());
		jAddField.addActionListener(listener);
		jToolBarLeft.addButton(jAddField);

		//Reset
		JButton jClearFields = new JButton(GuiShared.get().clearField());
		jClearFields.setIcon(Images.FILTER_CLEAR.getIcon());
		jClearFields.setActionCommand(FilterGuiAction.CLEAR.name());
		jClearFields.addActionListener(listener);
		jToolBarLeft.addButton(jClearFields);

		jToolBarLeft.addSeparator();

		//Save Filter
		JButton jSaveFilter = new JButton(GuiShared.get().saveFilter());
		jSaveFilter.setIcon(Images.FILTER_SAVE.getIcon());
		jSaveFilter.setActionCommand(FilterGuiAction.SAVE.name());
		jSaveFilter.addActionListener(listener);
		jToolBarLeft.addButton(jSaveFilter);

		//Load Filter
		jLoadFilter = new JDropDownButton(GuiShared.get().loadFilter());
		jLoadFilter.setIcon(Images.FILTER_LOAD.getIcon());
		jLoadFilter.keepVisible(2);
		jLoadFilter.setTopFixedCount(2);
		jLoadFilter.setInterval(125);
		jToolBarLeft.addButton(jLoadFilter);

		jToolBarLeft.addSeparator();

		//Export
		jExportButton = new JButton(GuiShared.get().export());
		jExportButton.setIcon(Images.DIALOG_CSV_EXPORT.getIcon());
		jExportButton.setActionCommand(FilterGuiAction.EXPORT.name());
		jExportButton.addActionListener(listener);
		jToolBarLeft.addButton(jExportButton);

		jExportMenu = new JDropDownButton(GuiShared.get().export(), Images.DIALOG_CSV_EXPORT.getIcon());
		jExportMenu.setVisible(false);
		jToolBarLeft.addButton(jExportMenu);

		JMenuItem jExportMenuItem = new JMenuItem(GuiShared.get().exportTableData());
		jExportMenuItem.setIcon(Images.DIALOG_CSV_EXPORT.getIcon());
		jExportMenuItem.setActionCommand(FilterGuiAction.EXPORT.name());
		jExportMenuItem.addActionListener(listener);
		jExportMenu.add(jExportMenuItem);

		jToolBarLeft.addSeparator();

		//Show Filters
		jShowFilters = new JCheckBox(GuiShared.get().showFilters());
		jShowFilters.setActionCommand(FilterGuiAction.SHOW_FILTERS.name());
		jShowFilters.addActionListener(listener);
		jShowFilters.setSelected(true);
		jToolBarLeft.addButton(jShowFilters, 70, SwingConstants.CENTER);

		jToolBarRight = new JFixedToolBar();

		jToolBarRight.addSpace(10);

		//Showing
		jShowing = new JLabel();
		jToolBarRight.add(jShowing);



		updateFilters();
		add();

		filterSave = new FilterSave(jFrame);
		filterManager = new FilterManager<E>(jFrame, filterControl.getName(), this, filterControl.getFilters(), filterControl.getDefaultFilters());
	}

	JPanel getPanel() {
		return jPanel;
	}

	public void setColumns(final List<EnumTableColumn<E>> enumColumns) {
		exportDialog.setColumns(enumColumns);
	}

	final void addExportOption(final JMenuItem jMenuItem) {
		if (!jExportMenu.isVisible()) { //First
			jExportMenu.setVisible(true);
			jExportButton.setVisible(false);
		}
		jExportMenu.add(jMenuItem);
	}

	void updateShowing() {
		jShowing.setText(GuiShared.get().filterShowing(EventListManager.size(filterControl.getFilterList()), EventListManager.size(filterControl.getExportEventList()), getCurrentFilterName()));
	}

	String getCurrentFilterName() {
		String filterName = GuiShared.get().filterUntitled();
		List<Filter> filters = getFilters(true);
		if (filters.isEmpty()) {
			filterName = GuiShared.get().filterEmpty();
		} else {
			if (filterControl.getAllFilters().containsValue(filters)) {
				for (Map.Entry<String, List<Filter>> entry : filterControl.getAllFilters().entrySet()) {
					if (entry.getValue().equals(filters)) {
						filterName = entry.getKey();
						break;
					}
				}
			}
		}
		return filterName;
	}

	List<Filter> getFilters(boolean includeDisabled) {
		List<Filter> filters = new ArrayList<Filter>();
		for (FilterPanel<E> filterPanel : filterPanels) {
			Filter filter = filterPanel.getFilter();
			if (!filter.isEmpty() && (filter.isEnabled() || includeDisabled)) {
				filters.add(filter);
			}
		}
		return filters;
	}

	private List<FilterMatcher<E>> getMatchers() {
		List<FilterMatcher<E>> matchers = new ArrayList<FilterMatcher<E>>();
		for (FilterPanel<E> filterPanel : filterPanels) {
			FilterMatcher<E> matcher = filterPanel.getMatcher();
			if (!matcher.isEmpty()) {
				matchers.add(matcher);
			}
		}
		return matchers;
	}

	private void update() {
		jPanel.removeAll();
		GroupLayout.ParallelGroup horizontalGroup = layout.createParallelGroup();
		GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
		//Toolbars
		horizontalGroup.addGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBarLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addGap(0)
				.addComponent(jToolBarRight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
		);
		verticalGroup
				.addGroup(layout.createParallelGroup()
					.addComponent(jToolBarLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jToolBarRight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
		);
		//Filters
		if (jShowFilters.isSelected()) {
			for (FilterPanel<E> filterPanel : filterPanels) {
				verticalGroup.addComponent(filterPanel.getPanel());
				horizontalGroup.addComponent(filterPanel.getPanel());
			}
		}
		layout.setHorizontalGroup(horizontalGroup);
		layout.setVerticalGroup(verticalGroup);
	}

	void remove(final FilterPanel<E> filterPanel) {
		filterPanels.remove(filterPanel);
		update();
	}

	private void add() {
		add(new FilterPanel<E>(this, filterControl));
	}

	private void add(final FilterPanel<E> filterPanel) {
		filterPanels.add(filterPanel);
		update();
	}

	void addEmpty() {
		if (filterPanels.isEmpty()) {
			add();
		}
	}

	private void clearEmpty() {
		if (filterPanels.size() == 1 && filterPanels.get(0).getFilter().isEmpty()) {
			remove(filterPanels.get(0));
		}
	}

	void clear() {
		while (filterPanels.size() > 0) {
			remove(filterPanels.get(0));
		}
		addEmpty();
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

	void setFilters(final List<Filter> filters) {
		while (filterPanels.size() > 0) {
			remove(filterPanels.get(0));
		}
		addFilters(filters);
	}

	void addFilter(final Filter filter) {
		addFilters(Collections.singletonList(filter));
	}

	void addFilters(final List<Filter> filters) {
		clearEmpty(); //Remove single empty filter...
		for (Filter filter : filters) {
			FilterPanel<E> filterPanel = new FilterPanel<E>(this, filterControl);
			filterPanel.setFilter(filter);
			add(filterPanel);
		}
		addEmpty(); //Add single filter (if empty)
		refilter();
	}

	final void updateFilters() {
		jLoadFilter.removeAll();
		JMenuItem jMenuItem;

		jMenuItem = new JMenuItem(GuiShared.get().manageFilters(), Images.DIALOG_SETTINGS.getIcon());
		jMenuItem.setActionCommand(FilterGuiAction.MANAGER.name());
		jMenuItem.addActionListener(listener);
		jMenuItem.setRolloverEnabled(true);
		jLoadFilter.add(jMenuItem);

		List<String> filters = new ArrayList<String>(filterControl.getFilters().keySet());
		Collections.sort(filters, new CaseInsensitiveComparator());

		List<String> defaultFilters = new ArrayList<String>(filterControl.getDefaultFilters().keySet());
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

	void saveSettings(String msg) {
		filterControl.saveSettings(msg);
	}

	void refilter() {
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
			filterControl.getFilterList().setMatcher(new FilterLogicalMatcher<E>(matchers));
		}

		filterControl.afterFilter();
		updateShowing();
	}

	String getFilterName() {
		return filterSave.show(new ArrayList<String>(filterControl.getFilters().keySet()), new ArrayList<String>(filterControl.getDefaultFilters().keySet()));
	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (FilterGuiAction.ADD.name().equals(e.getActionCommand())) {
				add();
				return;
			}
			if (FilterGuiAction.CLEAR.name().equals(e.getActionCommand())) {
				clear();
				return;
			}
			if (FilterGuiAction.MANAGER.name().equals(e.getActionCommand())) {
				filterManager.setVisible(true);
				return;
			}
			if (FilterGuiAction.SHOW_FILTERS.name().equals(e.getActionCommand())) {
				update();
				return;
			}
			if (FilterGuiAction.SAVE.name().equals(e.getActionCommand())) {
				List<Filter> filters = getFilters(true);
				if (filters.isEmpty()) {
					JOptionPane.showMessageDialog(jFrame, GuiShared.get().nothingToSave(), GuiShared.get().saveFilter(), JOptionPane.PLAIN_MESSAGE);
				} else {
					String name = filterSave.show(new ArrayList<String>(filterControl.getFilters().keySet()), new ArrayList<String>(filterControl.getDefaultFilters().keySet()));
					if (name != null && !name.isEmpty()) {
						Settings.lock("Filter (New)"); //Lock for Filter (New)
						filterControl.getFilters().put(name, filters);
						Settings.unlock("Filter (New)"); //Unlock for Filter (New)
						saveSettings("Filter (New)"); //Save Filter (New)
						updateFilters();
					}
				}
				return;
			}
			if (FilterGuiAction.EXPORT.name().equals(e.getActionCommand())) {
				exportDialog.setVisible(true);
				return;
			}
			loadFilter(e.getActionCommand(), (e.getModifiers() & ActionEvent.CTRL_MASK) != 0);
		}
	}
}
