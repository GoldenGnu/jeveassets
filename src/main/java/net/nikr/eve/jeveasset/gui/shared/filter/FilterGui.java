/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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

import ca.odell.glazedlists.FilterList;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
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

	private JPanel jPanel;
	private GroupLayout layout;
	private JToolBar jToolBar;
	private JDropDownButton jLoadFilter;
	private JCheckBox jShowFilters;
	private JLabel jShowing;
	private JFrame jFrame;

	private FilterControl<E> filterControl;

	private List<FilterPanel<E>> filterPanels = new ArrayList<FilterPanel<E>>();
	private FilterSave filterSave;
	private FilterManager<E> filterManager;

	private ExportDialog<E> exportDialog;

	ListenerClass listener = new ListenerClass();

	FilterGui(final JFrame jFrame, final FilterControl<E> filterControl) {
		this.jFrame = jFrame;
		this.filterControl = filterControl;

		exportDialog = new ExportDialog<E>(jFrame, filterControl.getName(), filterControl, filterControl, filterControl.getEventLists(), filterControl.getColumns());

		jPanel = new JPanel();

		layout = new GroupLayout(jPanel);
		jPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(false);

		jToolBar = new JToolBar();
		jToolBar.setFloatable(false);
		jToolBar.setRollover(true);

		//Add
		JButton jAddField = new JButton(GuiShared.get().addField());
		jAddField.setIcon(Images.EDIT_ADD.getIcon());
		jAddField.setActionCommand(FilterGuiAction.ADD.name());
		jAddField.addActionListener(listener);
		addToolButton(jAddField);

		//Reset
		JButton jClearFields = new JButton(GuiShared.get().clearField());
		jClearFields.setIcon(Images.FILTER_CLEAR.getIcon());
		jClearFields.setActionCommand(FilterGuiAction.CLEAR.name());
		jClearFields.addActionListener(listener);
		addToolButton(jClearFields);

		addToolSeparator();

		//Save Filter
		JButton jSaveFilter = new JButton(GuiShared.get().saveFilter());
		jSaveFilter.setIcon(Images.FILTER_SAVE.getIcon());
		jSaveFilter.setActionCommand(FilterGuiAction.SAVE.name());
		jSaveFilter.addActionListener(listener);
		addToolButton(jSaveFilter);

		//Load Filter
		jLoadFilter = new JDropDownButton(GuiShared.get().loadFilter());
		jLoadFilter.setIcon(Images.FILTER_LOAD.getIcon());
		jLoadFilter.keepVisible(2);
		jLoadFilter.setTopFixedCount(2);
		jLoadFilter.setInterval(125);
		addToolButton(jLoadFilter);

		addToolSeparator();

		//Export
		JButton jExport = new JButton(GuiShared.get().export());
		jExport.setIcon(Images.DIALOG_CSV_EXPORT.getIcon());
		jExport.setActionCommand(FilterGuiAction.EXPORT.name());
		jExport.addActionListener(listener);
		addToolButton(jExport);

		addToolSeparator();

		//Show Filters
		jShowFilters = new JCheckBox(GuiShared.get().showFilters());
		jShowFilters.setActionCommand(FilterGuiAction.SHOW_FILTERS.name());
		jShowFilters.addActionListener(listener);
		jShowFilters.setSelected(true);
		addToolButton(jShowFilters, 70);

		//Showing
		jShowing = new JLabel();



		updateFilters();
		add();

		filterSave = new FilterSave(jFrame);
		filterManager = new FilterManager<E>(jFrame, this, filterControl.getFilters(), filterControl.getDefaultFilters());
	}

	JPanel getPanel() {
		return jPanel;
	}

	final void addToolButton(final AbstractButton jButton) {
		addToolButton(jButton, 90);
	}
	final void addToolButton(final AbstractButton jButton, final int width) {
		if (width > 0) {
			jButton.setMinimumSize(new Dimension(width, Program.BUTTONS_HEIGHT));
			jButton.setMaximumSize(new Dimension(width, Program.BUTTONS_HEIGHT));
		}
		jButton.setHorizontalAlignment(SwingConstants.LEFT);
		jToolBar.add(jButton);
	}
	final void addToolSeparator() {
		jToolBar.addSeparator();
	}

	void updateShowing() {
		int showing = 0;
		for (FilterList<E> filterList : filterControl.getFilterLists()) {
			showing = showing + filterList.size();
		}
		jShowing.setText(GuiShared.get().filterShowing(showing, filterControl.getTotalSize(), getCurrentFilterName()));
	}

	String getCurrentFilterName() {
		String filterName = GuiShared.get().filterUntitled();
		if (getFilters().isEmpty()) {
			filterName = GuiShared.get().filterEmpty();
		} else {
			if (filterControl.getAllFilters().containsValue(getFilters())) {
				for (Map.Entry<String, List<Filter>> entry : filterControl.getAllFilters().entrySet()) {
					if (entry.getValue().equals(getFilters())) {
						filterName = entry.getKey();
						break;
					}
				}
			}
		}
		return filterName;
	}

	List<Filter> getFilters() {
		List<Filter> filters = new ArrayList<Filter>();
		for (FilterPanel<E> filterPanel : filterPanels) {
			Filter filter = filterPanel.getFilter();
			if (!filter.isEmpty()) {
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
		horizontalGroup.addGroup(
			layout.createSequentialGroup()
				.addComponent(jToolBar)
				.addGap(0, 0, Short.MAX_VALUE)
				.addComponent(jShowing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		);

		GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
		final int TOOLBAR_HEIGHT = jToolBar.getInsets().top + jToolBar.getInsets().bottom + Program.BUTTONS_HEIGHT;
		verticalGroup
				.addGroup(layout.createParallelGroup()
					.addComponent(jToolBar, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT, TOOLBAR_HEIGHT)
					.addComponent(jShowing, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
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

	void refilter() {
		filterControl.beforeFilter();
		List<FilterMatcher<E>> matchers = getMatchers();
		if (matchers.isEmpty()) {
			for (FilterList<E> filterList : filterControl.getFilterLists()) {
				filterList.setMatcher(null);
			}
		} else {
			for (FilterList<E> filterList : filterControl.getFilterLists()) {
				filterList.setMatcher(new FilterLogicalMatcher<E>(matchers));
			}
		}

		filterControl.afterFilter();
		updateShowing();
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
				if (getMatchers().isEmpty()) {
					JOptionPane.showMessageDialog(jFrame, GuiShared.get().nothingToSave(), GuiShared.get().saveFilter(), JOptionPane.PLAIN_MESSAGE);
				} else {
					String name = filterSave.show(new ArrayList<String>(filterControl.getFilters().keySet()), new ArrayList<String>(filterControl.getDefaultFilters().keySet()));
					if (name != null && !name.isEmpty()) {
						filterControl.getFilters().put(name, getFilters());
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
