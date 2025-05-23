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

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.profile.ProfileManager;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.data.settings.tag.TagUpdate;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel.JStatusLabel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.InstantToolTip;
import net.nikr.eve.jeveasset.gui.shared.JOptionInput;
import net.nikr.eve.jeveasset.gui.shared.MarketDetailsColumn;
import net.nikr.eve.jeveasset.gui.shared.MarketDetailsColumn.MarketDetailsActionListener;
import net.nikr.eve.jeveasset.gui.shared.TextImport;
import net.nikr.eve.jeveasset.gui.shared.TextImport.TextImportHandler;
import net.nikr.eve.jeveasset.gui.shared.components.JAutoCompleteDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDropDownButton;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JOptionsDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JOptionsDialog.OptionEnum;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog;
import net.nikr.eve.jeveasset.gui.shared.components.JTextDialog.TextReturn;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo.AutoNumberFormat;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuStockpile;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuStockpile.BpOptions;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuUI;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.ColumnValueChangeListener;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.StockpileTotal;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileItem;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.Stockpile.SubpileStock;
import net.nikr.eve.jeveasset.gui.tabs.stockpile.StockpileSeparatorTableCell.StockpileCellAction;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;
import net.nikr.eve.jeveasset.io.local.EveFittingReader;
import net.nikr.eve.jeveasset.io.local.SettingsReader;
import net.nikr.eve.jeveasset.io.local.SettingsWriter;
import net.nikr.eve.jeveasset.io.local.StockpileDataReader;
import net.nikr.eve.jeveasset.io.local.StockpileDataWriter;
import net.nikr.eve.jeveasset.io.local.text.TextImportType;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil.HelpLink;


public class StockpileTab extends JMainTabSecondary implements TagUpdate {

	private enum StockpileAction {
		ADD_STOCKPILE,
		DELETE_STOCKPILE_MULTI,
		EDIT_GROUPS,
		SHOPPING_LIST_MULTI,
		SHOW_HIDE,
		IMPORT_TEXT,
		IMPORT_XML_TEXT,
		IMPORT_XML,
		IMPORT_EVE_XML_FIT,
		EXPORT_TEXT,
		EXPORT_XML,
		COLLAPSE,
		EXPAND,
		COLLAPSE_GROUPS,
		EXPAND_GROUPS,
		SUBPILE_TREE
	}

	public enum ImportOptions implements OptionEnum {
		KEEP() {
			@Override
			public String getText() {
				return TabsStockpile.get().importOptionsKeep();
			}
			@Override
			public String getHelp() {
				return TabsStockpile.get().importOptionsKeepHelp();
			}
		},
		TEMPLATE() {
			@Override
			public String getText() {
				return TabsStockpile.get().importOptionsTemplate();
			}
			@Override
			public String getHelp() {
				return TabsStockpile.get().importOptionsTemplateHelp();
			}
		},
		NEW() {
			@Override
			public String getText() {
				return TabsStockpile.get().importOptionsNew();
			}
			@Override
			public String getHelp() {
				return TabsStockpile.get().importOptionsNewHelp();
			}
		},
		RENAME() {
			@Override
			public String getText() {
				return TabsStockpile.get().importOptionsRename();
			}
			@Override
			public String getHelp() {
				return TabsStockpile.get().importOptionsRenameHelp();
			}
		},
		MERGE() {
			@Override
			public String getText() {
				return TabsStockpile.get().importOptionsMerge();
			}
			@Override
			public String getHelp() {
				return TabsStockpile.get().importOptionsMergeHelp();
			}
		},
		OVERWRITE() {
			@Override
			public String getText() {
				return TabsStockpile.get().importOptionsOverwrite();
			}
			@Override
			public String getHelp() {
				return TabsStockpile.get().importOptionsOverwriteHelp();
			}
		},
		ADD() {
			@Override
			public String getText() {
				return TabsStockpile.get().importOptionsAdd();
			}
			@Override
			public String getHelp() {
				return TabsStockpile.get().importOptionsAddHelp();
			}
		},
		SKIP() {
			@Override
			public String getText() {
				return TabsStockpile.get().importOptionsSkip();
			}
			@Override
			public String getHelp() {
				return TabsStockpile.get().importOptionsSkipHelp();
			}
		};

		private boolean all = false;

		@Override
		public boolean isAll() {
			return all;
		}

		@Override
		public void setAll(boolean all) {
			this.all = all;
		}
	}

	private static final MatchAllGroups MATCH_ALL_GROUPS = new MatchAllGroups();

	//StatusBar
	private final JStatusLabel jVolumeNow;
	private final JStatusLabel jVolumeNeeded;
	private final JStatusLabel jValueNow;
	private final JStatusLabel jValueNeeded;

	//Dialogs
	private final JCustomFileChooser jFileChooser;
	private final StockpileDialog stockpileDialog;
	private final StockpileItemDialog stockpileItemDialog;
	private final StockpileShoppingListDialog stockpileShoppingListDialog;
	private final JMultiSelectionDialog<Stockpile> stockpileSelectionDialog;
	private final JMultiSelectionDialog<String> fitsSelectionDialog;
	private final JOptionsDialog stockpileImportDialog;
	private final JTextDialog jTextDialog;
	private final TextImport<TextImportType> textImport;

	//Table
	private final JSeparatorTable jTable;
	private final EnumTableFormatAdaptor<StockpileTableFormat, StockpileItem> tableFormat;
	private final DefaultEventTableModel<StockpileItem> tableModel;
	private final EventList<StockpileItem> eventList;
	private final FilterList<StockpileItem> filterList;
	private final SeparatorList<StockpileItem> separatorList;
	private final DefaultEventSelectionModel<StockpileItem> selectionModel;
	private final StockpileFilterControl filterControl;

	//Toolbar
	private final JFixedToolBar jToolBar;
	private final JCheckBoxMenuItem jShowSubpileTree;
	private final JDropDownButton jCollapse;
	private final JButton jCollapseGroup;
	private final JButton jExpandGroup;
	private final JButton jCollapseStockpile;
	private final JButton jExpandStockpile;
	private final JComboBox<EsiOwner> jOwners;
	private final DefaultComboBoxModel<EsiOwner> ownerModel;
	private final JAutoCompleteDialog<String> jAutoCompleteDialog;

	//Data
	private final StockpileData stockpileData;
	private int toolBarMinWidth;
	private Stockpile template = null;
	private boolean collapsed = false;
	private static boolean updateFirstStockpileGroup = false;

	public static final String NAME = "stockpile"; //Not to be changed!

	public StockpileTab(final Program program) {
		super(program, NAME, TabsStockpile.get().stockpile(), Images.TOOL_STOCKPILE.getIcon(), true);

		stockpileData = new StockpileData(program);

		final ListenerClass listener = new ListenerClass();

		jAutoCompleteDialog = new JAutoCompleteDialog<String>(program, "Edit Group", Images.EDIT_EDIT.getImage(), "Select Group:", false, false) {
			@Override
			protected Comparator<String> getComparator() {
				return GlazedLists.comparableComparator();
			}
			
			@Override
			protected TextFilterator<String> getFilterator() {
				return new TextFilterator<String>() {
					@Override
					public void getFilterStrings(List<String> baseList, String element) {
						baseList.add(element);
					}
				};
			}
			
			@Override
			protected String getValue(Object object) {
				return (String) object;
			}
			
			@Override
			protected boolean isEmpty(String t) {
				return t.isEmpty();
			}
		};

		jFileChooser = new JCustomFileChooser("xml");
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		stockpileDialog = new StockpileDialog(program);
		stockpileItemDialog = new StockpileItemDialog(program);
		stockpileShoppingListDialog = new StockpileShoppingListDialog(program);
		stockpileSelectionDialog = new JMultiSelectionDialog<>(program, TabsStockpile.get().selectStockpiles());
		fitsSelectionDialog = new JMultiSelectionDialog<>(program, TabsStockpile.get().selectFits());
		stockpileImportDialog = new JOptionsDialog(program);

		jTextDialog = new JTextDialog(program.getMainWindow().getFrame());
		textImport = new TextImport<>(program, NAME);

		jToolBar = new JFixedToolBar();
		program.getMainWindow().getFrame().addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateToolbar();
			}

			@Override
			public void componentMoved(ComponentEvent e) { }

			@Override
			public void componentShown(ComponentEvent e) {
				updateToolbar();
			}

			@Override
			public void componentHidden(ComponentEvent e) { }
		});

		JButton jAdd = new JButton(TabsStockpile.get().newStockpile(), Images.LOC_GROUPS.getIcon());
		jAdd.setActionCommand(StockpileAction.ADD_STOCKPILE.name());
		jAdd.addActionListener(listener);
		jToolBar.addButton(jAdd);

		JDropDownButton jEdit = new JDropDownButton(TabsStockpile.get().edit(), Images.EDIT_EDIT.getIcon());
		jToolBar.addButton(jEdit);

		JMenuItem jDelete = new JMenuItem(TabsStockpile.get().delete(), Images.EDIT_DELETE.getIcon());
		jDelete.setActionCommand(StockpileAction.DELETE_STOCKPILE_MULTI.name());
		jDelete.addActionListener(listener);
		jEdit.add(jDelete);

		JMenuItem jGroups = new JMenuItem(TabsStockpile.get().groups(), Images.FILTER_LOAD.getIcon());
		jGroups.setActionCommand(StockpileAction.EDIT_GROUPS.name());
		jGroups.addActionListener(listener);
		jEdit.add(jGroups);

		jToolBar.addSeparator();

		JDropDownButton jShow = new JDropDownButton(TabsStockpile.get().showHide(), Images.EDIT_SHOW.getIcon());
		jToolBar.addButton(jShow);

		JMenuItem jShowStockpiles = new JMenuItem(TabsStockpile.get().showStockpiles(), Images.TOOL_STOCKPILE.getIcon());
		jShowStockpiles.setActionCommand(StockpileAction.SHOW_HIDE.name());
		jShowStockpiles.addActionListener(listener);
		jShow.add(jShowStockpiles);

		jShowSubpileTree = new JCheckBoxMenuItem(TabsStockpile.get().showSubpileTree());
		jShowSubpileTree.setSelected(Settings.get().isShowSubpileTree());
		jShowSubpileTree.setActionCommand(StockpileAction.SUBPILE_TREE.name());
		jShowSubpileTree.addActionListener(listener);
		jShow.add(jShowSubpileTree);

		jToolBar.addSeparator();

		JButton jShoppingList = new JButton(TabsStockpile.get().getShoppingList(), Images.STOCKPILE_SHOPPING_LIST.getIcon());
		jShoppingList.setActionCommand(StockpileAction.SHOPPING_LIST_MULTI.name());
		jShoppingList.addActionListener(listener);
		jToolBar.addButton(jShoppingList);

		JDropDownButton jImport = new JDropDownButton(TabsStockpile.get().importButton(), Images.EDIT_IMPORT.getIcon());
		jToolBar.addButton(jImport);

		JMenuItem jImportTextFormats = new JMenuItem(TabsStockpile.get().importText(), Images.STOCKPILE_SHOPPING_LIST.getIcon());
		jImportTextFormats.setActionCommand(StockpileAction.IMPORT_TEXT.name());
		jImportTextFormats.addActionListener(listener);
		jImport.add(jImportTextFormats);

		JMenuItem jImportEveXmlFit = new JMenuItem(TabsStockpile.get().importEveXml(), Images.MISC_XML.getIcon());
		jImportEveXmlFit.setActionCommand(StockpileAction.IMPORT_EVE_XML_FIT.name());
		jImportEveXmlFit.addActionListener(listener);
		jImport.add(jImportEveXmlFit);

		JMenuItem jImportXml = new JMenuItem(TabsStockpile.get().importStockpilesXml(), Images.TOOL_STOCKPILE.getIcon());
		jImportXml.setActionCommand(StockpileAction.IMPORT_XML.name());
		jImportXml.addActionListener(listener);
		jImport.add(jImportXml);

		JMenuItem jImportText = new JMenuItem(TabsStockpile.get().importStockpilesText(), Images.EDIT_COPY.getIcon());
		jImportText.setActionCommand(StockpileAction.IMPORT_XML_TEXT.name());
		jImportText.addActionListener(listener);
		jImport.add(jImportText);

		JMenuItem jExportXml = new JMenuItem(TabsStockpile.get().exportStockpilesXml(), Images.TOOL_STOCKPILE.getIcon());
		jExportXml.setActionCommand(StockpileAction.EXPORT_XML.name());
		jExportXml.addActionListener(listener);

		JMenuItem jExportText = new JMenuItem(TabsStockpile.get().exportStockpilesText(), Images.EDIT_COPY.getIcon());
		jExportText.setActionCommand(StockpileAction.EXPORT_TEXT.name());
		jExportText.addActionListener(listener);

		jToolBar.addSeparator();

		jToolBar.addSpace(5);

		ownerModel = new DefaultComboBoxModel<>();
		jOwners = new JComboBox<>(ownerModel);
		jToolBar.add(jOwners, 150);

		jToolBar.addSpace(1);

		JLabel jOwnerLabel = new JLabel(Images.MISC_HELP.getIcon());
		jOwnerLabel.setToolTipText(TabsStockpile.get().marketDetailsOwnerToolTip());
		InstantToolTip.install(jOwnerLabel);
		jToolBar.addLabelIcon(jOwnerLabel);

		jToolBar.addSeparator();

		jToolBar.addGlue();

		jCollapse = new JDropDownButton(TabsStockpile.get().collapse(), Images.MISC_COLLAPSE.getIcon());
		jToolBar.addButton(jCollapse);

		JMenuItem jCollapseStockpileMenuItem = new JMenuItem(TabsStockpile.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapseStockpileMenuItem.setActionCommand(StockpileAction.COLLAPSE.name());
		jCollapseStockpileMenuItem.addActionListener(listener);
		jCollapse.add(jCollapseStockpileMenuItem);

		JMenuItem jExpandStockpileMenuItem = new JMenuItem(TabsStockpile.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpandStockpileMenuItem.setActionCommand(StockpileAction.EXPAND.name());
		jExpandStockpileMenuItem.addActionListener(listener);
		jCollapse.add(jExpandStockpileMenuItem);

		JMenuItem jCollapseGroupMenuItem = new JMenuItem(TabsStockpile.get().groupCollapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapseGroupMenuItem.setActionCommand(StockpileAction.COLLAPSE_GROUPS.name());
		jCollapseGroupMenuItem.addActionListener(listener);
		jCollapse.add(jCollapseGroupMenuItem);

		JMenuItem jExpandGroupMenuItem = new JMenuItem(TabsStockpile.get().groupExpand(), Images.MISC_EXPANDED.getIcon());
		jExpandGroupMenuItem.setActionCommand(StockpileAction.EXPAND_GROUPS.name());
		jExpandGroupMenuItem.addActionListener(listener);
		jCollapse.add(jExpandGroupMenuItem);

		jCollapseGroup = new JButton(TabsStockpile.get().groupCollapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapseGroup.setActionCommand(StockpileAction.COLLAPSE_GROUPS.name());
		jCollapseGroup.addActionListener(listener);

		jExpandGroup = new JButton(TabsStockpile.get().groupExpand(), Images.MISC_EXPANDED.getIcon());
		jExpandGroup.setActionCommand(StockpileAction.EXPAND_GROUPS.name());
		jExpandGroup.addActionListener(listener);

		jCollapseStockpile = new JButton(TabsStockpile.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapseStockpile.setActionCommand(StockpileAction.COLLAPSE.name());
		jCollapseStockpile.addActionListener(listener);

		jExpandStockpile = new JButton(TabsStockpile.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpandStockpile.setActionCommand(StockpileAction.EXPAND.name());
		jExpandStockpile.addActionListener(listener);

		//Table Format
		tableFormat = TableFormatFactory.stockpileTableFormat();
		tableFormat.addListener(listener);
		//Backend
		eventList = EventListManager.create();
		//Sorting (per column)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<StockpileItem> sortedListColumn = new SortedList<>(eventList);
		eventList.getReadWriteLock().readLock().unlock();
		//Sorting Total (Ensure that total is always last)
		eventList.getReadWriteLock().readLock().lock();
		SortedList<StockpileItem> sortedListTotal = new SortedList<>(sortedListColumn, new TotalComparator());
		eventList.getReadWriteLock().readLock().unlock();
		//Filter
		eventList.getReadWriteLock().readLock().lock();
		filterList = new FilterList<>(sortedListTotal);
		eventList.getReadWriteLock().readLock().unlock();
		filterList.addListEventListener(listener);
		//Separator
		separatorList = new SeparatorList<>(filterList, new StockpileSeparatorComparator(), 1, Integer.MAX_VALUE);
		separatorList.addListEventListener(new ListEventListener<StockpileItem>() {
			@Override
			public void listChanged(ListEvent<StockpileItem> listChanges) {
				updateGroupFirst();
			}
		});
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		//Table
		jTable = new JStockpileTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new StockpileSeparatorTableCell(program, jTable, separatorList, listener));
		jTable.setSeparatorEditor(new StockpileSeparatorTableCell(program, jTable, separatorList, listener));
		jTable.setCellSelectionEnabled(true);
		//Padding
		PaddingTableCellRenderer.install(jTable, 3);
		//Sorting
		TableComparatorChooser.install(jTable, sortedListColumn, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Market Details
		MarketDetailsColumn.install(eventList, new MarketDetailsActionListener<StockpileItem>() {
			@Override
			public void openMarketDetails(StockpileItem stockpileItem) {
				if (!jOwners.isEnabled()) {
					return;
				}
				EsiOwner esiOwner = jOwners.getItemAt(jOwners.getSelectedIndex());
				JMenuUI.openMarketDetails(program, esiOwner, stockpileItem.getTypeID(), false);
			}
		});
		//Listeners
		installTable(jTable);
		//Scroll
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Filter GUI
		filterControl = new StockpileFilterControl(sortedListTotal);
		filterControl.addExportOption(jExportXml);
		filterControl.addExportOption(jExportText);
		filterControl.setManualLink(new HelpLink("https://wiki.jeveassets.org/manual/stockpile", GuiShared.get().helpStockpile()), getIcon());
		//Menu
		installTableTool(new StockpileTableMenu(), tableFormat, tableModel, jTable, filterControl, StockpileItem.class);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar, jToolBar.getMinimumSize().width, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);

		jVolumeNow = StatusPanel.createLabel(TabsStockpile.get().shownVolumeNow(), Images.ASSETS_VOLUME.getIcon(), AutoNumberFormat.DOUBLE);
		this.addStatusbarLabel(jVolumeNow);

		jValueNow = StatusPanel.createLabel(TabsStockpile.get().shownValueNow(), Images.TOOL_VALUES.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jValueNow);

		jVolumeNeeded = StatusPanel.createLabel(TabsStockpile.get().shownVolumeNeeded(), Images.ASSETS_VOLUME.getIcon(), AutoNumberFormat.DOUBLE);
		this.addStatusbarLabel(jVolumeNeeded);

		jValueNeeded = StatusPanel.createLabel(TabsStockpile.get().shownValueNeeded(), Images.TOOL_VALUES.getIcon(), AutoNumberFormat.ISK);
		this.addStatusbarLabel(jValueNeeded);
	}

	@Override
	public void updateData() {
		//Save separator expanded/collapsed state
		jTable.saveExpandedState();
		//Update Data
		stockpileData.updateData(eventList);
		//Restore separator expanded/collapsed state
		jTable.loadExpandedState();
		//Update owner combobox
		ownerModel.removeAllElements();
		for (EsiOwner owner : program.getProfileManager().getEsiOwners()) {
			if (owner.isOpenWindows()) {
				ownerModel.addElement(owner);
			}
		}
		jOwners.setEnabled(ownerModel.getSize() > 0);
	}

	private void updateOwners() {
		//Update Owners
		stockpileData.updateOwners();
	}

	private void updateSubpile(Stockpile stockpile) {
		//Save separator expanded/collapsed state
		jTable.saveExpandedState();
		//Update Data
		stockpileData.updateSubpile(eventList, stockpile);
		//Restore separator expanded/collapsed state
		jTable.loadExpandedState();
	}

	private void updateStockpile(Stockpile stockpile) {
		//Update Data
		stockpileData.updateStockpile(stockpile);
	}

	@Override
	public void clearData() {
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		filterControl.clearCache();
	}

	@Override
	public void updateCache() {
		filterControl.createCache();
	}

	@Override
	public Collection<LocationType> getLocations() {
		return new ArrayList<>(); //LocationsType
	}

	/**
	 * Needs to be updated before the stockpile tab is shown (for TableMenu > Add
	 */
	public void updateStockpileDialog() {
		stockpileDialog.updateData();
	}

	/**
	 * @param stockpile Stockpile to add item to
	 * @param item Item to add to stockpile
	 * @param merge True: Add new and old item count. False: Skip the new item if it exist
	 * @param saveOnChange Save settings when done
	 * @return
	 */
	public Stockpile addToStockpile(Stockpile stockpile, StockpileItem item, boolean merge, boolean saveOnChange) {
		return addToStockpile(stockpile, Collections.singletonList(item), merge, saveOnChange);
	}

	/**
	 *
	 * @param stockpile Stockpile to add item to
	 * @param items Items to add to stockpile
	 * @param merge  True: Add new and old item count. False: Skip new items if they exist
	 * @param saveOnChange Save settings when done
	 * @return
	 */
	public Stockpile addToStockpile(Stockpile stockpile, Collection<StockpileItem> items, boolean merge, boolean saveOnChange) {
		updateOwners();
		if (stockpile == null) { //new stockpile
			stockpile = stockpileDialog.showAdd();
		}
		if (stockpile != null) { //Add items
			removeStockpile(stockpile);
			boolean save = false;
			for (StockpileItem fromItem : items) {
				//Clone item
				StockpileItem toItem = null;
				//Search for existing
				for (StockpileItem item : stockpile.getItems()) {
					if (item.getItemTypeID() == fromItem.getItemTypeID() && item.isRuns() == fromItem.isRuns()) {
						toItem = item;
						break;
					}
				}
				if (toItem != null) { //Update existing (add counts)
					if (merge) {
						save = true;
						Settings.lock("Stockpile (addTo - Merge)"); //Lock for Stockpile (addTo - Merge)
						toItem.addCountMinimum(fromItem.getCountMinimum());
						Settings.unlock("Stockpile (addTo - Merge)"); //Unlock for Stockpile (addTo - Merge)
					}
				} else { //Add new
					save = true;
					Settings.lock("Stockpile (addTo - New)"); //Lock for Stockpile (addTo - New)
					StockpileItem item = new StockpileItem(stockpile, fromItem);
					stockpile.add(item);
					Settings.unlock("Stockpile (addTo - New)"); //Unlock for Stockpile (addTo - New)
				}
			}
			if (save && saveOnChange) {
				program.saveSettings("Stockpile (addTo)"); //Save Stockpile (Merge);
			}
			addStockpile(stockpile);
		}
		return stockpile;
	}

	private void updateToolbar() {
		if (toolBarMinWidth == 0) {
			jToolBar.remove(jCollapse);
			jToolBar.addButton(jCollapseGroup);
			jToolBar.addButton(jExpandGroup);
			jToolBar.addButton(jCollapseStockpile);
			jToolBar.addButton(jExpandStockpile);
			toolBarMinWidth = jToolBar.getPreferredSize().width;
			collapsed = false;
		}
		int width = jToolBar.getVisibleRect().width;
		if (collapsed && width >= toolBarMinWidth) {
			collapsed = false;
			jToolBar.remove(jCollapse);
			jToolBar.addButton(jCollapseGroup);
			jToolBar.addButton(jExpandGroup);
			jToolBar.addButton(jCollapseStockpile);
			jToolBar.addButton(jExpandStockpile);
		} else if (!collapsed && width < toolBarMinWidth){
			collapsed = true;
			jToolBar.remove(jCollapseGroup);
			jToolBar.remove(jExpandGroup);
			jToolBar.remove(jCollapseStockpile);
			jToolBar.remove(jExpandStockpile);
			jToolBar.addButton(jCollapse);
		}
	}

	private SeparatorList.Separator<?> getSeparator(final Stockpile stockpile) {
		try {
			separatorList.getReadWriteLock().readLock().lock();
			for (int i = 0; i < separatorList.size(); i++) {
				Object object = separatorList.get(i);
				if (object instanceof SeparatorList.Separator) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator) object;
					Object first = separator.first();
					if (first instanceof StockpileItem) {
						StockpileItem firstItem = (StockpileItem) first;
						if (firstItem.getStockpile().equals(stockpile)) {
							return separator;
						}
					}
				}
			}
		} finally {
			separatorList.getReadWriteLock().readLock().unlock();
		}
		return null;
	}

	public void scrollToSctockpile(final Stockpile stockpile) {
		SeparatorList.Separator<?> separator = getSeparator(stockpile);
		if (separator == null) {
			return;
		}
		if (separator.getLimit() > 0) { //Expanded: Scroll
			int row = EventListManager.indexOf(separatorList, separator.first()) - 1;
			Rectangle rect = jTable.getCellRect(row, 0, true);
			rect.setSize(jTable.getVisibleRect().getSize());
			jTable.scrollRectToVisible(rect);
		} else { //Collapsed: Expand and run again...
			try {
				separatorList.getReadWriteLock().writeLock().lock();
				separator.setLimit(Integer.MAX_VALUE);
			} finally {
				separatorList.getReadWriteLock().writeLock().unlock();
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollToSctockpile(stockpile);
				}
			});
		}
	}

	@Override
	public void updateTags() {
		beforeUpdateData();
		tableModel.fireTableDataChanged();
		filterControl.refilter();
		afterUpdateData();
	}

	protected void setMultiplyer(Stockpile stockpile, JTextField jMultiplier) {
		if (stockpile == null) {
			return;
		}
		double multiplier;
		try {
			multiplier = Double.parseDouble(jMultiplier.getText());
		} catch (NumberFormatException ex) {
			multiplier = 1;
		}
		if (multiplier != stockpile.getMultiplier()) {
			stockpile.setMultiplier(multiplier);
			stockpile.updateTotal();
			program.saveSettings("Stockpile: Multiplier changed");
			tableModel.fireTableDataChanged();
		}
	}

	protected void editItem(StockpileItem item) {
		StockpileItem editItem = stockpileItemDialog.showEdit(item);
		if (editItem != null) {
			addToStockpile(editItem.getStockpile(), editItem, false, true);
		}
	}

	protected void removeItem(StockpileItem item) {
		removeItems(Collections.singletonList(item));
	}

	protected void removeItems(Collection<StockpileItem> items) {
		Set<Stockpile> stockpiles = new HashSet<>();
		for (StockpileItem item : items) {
			item.getStockpile().updateTotal();
			stockpiles.add(item.getStockpile());
		}
		if (!items.isEmpty()) {
			updateSubpile(items.iterator().next().getStockpile());
		}
		for (Stockpile stockpile : stockpiles) {
			if (stockpile.isMatchAll()) { //Less items == may match now...
				updateStockpile(stockpile);
			}
		}
		//Lock Table
		beforeUpdateData();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			enableGroupFirstUpdate();
			eventList.removeAll(items);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Unlcok Table
		afterUpdateData();
	}

	public void addStockpile(Stockpile stockpile) {
		if (stockpile == null) {
			return;
		}
		updateStockpile(stockpile);
		updateSubpile(stockpile);
		//Lock Table
		beforeUpdateData();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			enableGroupFirstUpdate();
			eventList.addAll(stockpile.getItems());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Unlcok Table
		afterUpdateData();
		//Load groups states
		String group = Settings.get().getStockpileGroupSettings().getGroup(stockpile);
		boolean expand = Settings.get().getStockpileGroupSettings().isGroupExpanded(group);
		if (!group.isEmpty()) {
			if (expand) { //Expanse - Load stockpiles expanded state
				try {
					separatorList.getReadWriteLock().writeLock().lock();
					for (int i = 0; i < separatorList.size(); i++) {
						Object object = separatorList.get(i);
						if (object instanceof SeparatorList.Separator<?>) {
							SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
							StockpileItem currentItem = (StockpileItem) separator.first();
							if (currentItem.getGroup().equals(group)) {
								if (Settings.get().getStockpileGroupSettings().isStockpileExpanded(currentItem.getStockpile())) {
									separator.setLimit(Integer.MAX_VALUE);
								} else {
									separator.setLimit(0);
								}
							}
						}
					}
				} finally {
					separatorList.getReadWriteLock().writeLock().unlock();
				}
			} else { //Collapse group
				try {
					separatorList.getReadWriteLock().writeLock().lock();
					for (int i = 0; i < separatorList.size(); i++) {
						Object object = separatorList.get(i);
						if (object instanceof SeparatorList.Separator<?>) {
							SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
							StockpileItem currentItem = (StockpileItem) separator.first();
							if (currentItem.getGroup().equals(group)) {
								separator.setLimit(0);
							}
						}
					}
				} finally {
					separatorList.getReadWriteLock().writeLock().unlock();
				}
			}
		}
	}

	private void expandGroups(boolean expand, GroupMatching match) {
		//Changed groups
		List<StockpileItem> stockpileItems = new ArrayList<>();
		for (Stockpile stockpile : getShownStockpiles()) {
			String group = Settings.get().getStockpileGroupSettings().getGroup(stockpile);
			if (match.matches(group) && Settings.get().getStockpileGroupSettings().isGroupExpanded(group) != expand) { //Match group + is changed
				stockpileItems.addAll(stockpile.getItems());
				stockpileItems.addAll(stockpile.getSubpileTableItems());
			}
		}
		//Groups affected
		List<String> groups = new ArrayList<>();
		for (Stockpile stockpile : Settings.get().getStockpiles()) {
			String group = Settings.get().getStockpileGroupSettings().getGroup(stockpile);
			if (match.matches(group)) { //Match group
				groups.add(group);
			}
		}
		//Update group settings (must be done after the ShownStockpiles loop)
		for (String group : groups) {
			Settings.get().getStockpileGroupSettings().setGroupExpanded(group, expand);
		}
		if (!expand) { //Collapse - Save stockpile expanded state
			try {
				separatorList.getReadWriteLock().writeLock().lock();
				for (int i = 0; i < separatorList.size(); i++) {
					Object object = separatorList.get(i);
					if (object instanceof SeparatorList.Separator<?>) {
						SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
						StockpileItem currentItem = (StockpileItem) separator.first();
						if (match.matches(currentItem.getGroup())) {
							Settings.get().getStockpileGroupSettings().setStockpileExpanded(currentItem.getStockpile(), separator.getLimit() != 0);
						}
					}
				}
			} finally {
				separatorList.getReadWriteLock().writeLock().unlock();
			}
		}
		//Lock Table
		beforeUpdateData();
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.removeAll(stockpileItems);
			if (expand) {
				enableGroupFirstUpdate();
			}
			eventList.addAll(stockpileItems);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Unlcok Table
		afterUpdateData();		
		if (expand) { //Expanse - Load stockpiles(s) expanded state
			try {
				separatorList.getReadWriteLock().writeLock().lock();
				for (int i = 0; i < separatorList.size(); i++) {
					Object object = separatorList.get(i);
					if (object instanceof SeparatorList.Separator<?>) {
						SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
						StockpileItem currentItem = (StockpileItem) separator.first();
						if (match.matches(currentItem.getGroup())) {
							if (Settings.get().getStockpileGroupSettings().isStockpileExpanded(currentItem.getStockpile())) {
								separator.setLimit(Integer.MAX_VALUE);
							} else {
								separator.setLimit(0);
							}
						}
					}
				}
			} finally {
				separatorList.getReadWriteLock().writeLock().unlock();
			}
		} else { //Collapse group(s)
			try {
				separatorList.getReadWriteLock().writeLock().lock();
				for (int i = 0; i < separatorList.size(); i++) {
					Object object = separatorList.get(i);
					if (object instanceof SeparatorList.Separator<?>) {
						SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
						StockpileItem currentItem = (StockpileItem) separator.first();
						//if (match.matches(currentItem.getGroup()) && changed.contains(currentItem.getStockpile())) {
						if (match.matches(currentItem.getGroup())) {
							separator.setLimit(0);
						}
					}
				}
			} finally {
				separatorList.getReadWriteLock().writeLock().unlock();
			}
		}
	}

	private void expandGroupStockpiles(boolean expand) {
		Stockpile stockpile = getSelectedStockpile();
		if (stockpile == null) {
			return;
		}
		String group = Settings.get().getStockpileGroupSettings().getGroup(stockpile);
		//Save state
		List<Stockpile> stockpiles = Settings.get().getStockpileGroupSettings().getStockpiles(group);
		Settings.get().getStockpileGroupSettings().setStockpileExpanded(stockpiles, expand);
		//Expand/Collapse shown stockpiles in group
		if (Settings.get().getStockpileGroupSettings().isGroupExpanded(group)) {
			try {
				separatorList.getReadWriteLock().writeLock().lock();
				for (int i = 0; i < separatorList.size(); i++) {
					Object object = separatorList.get(i);
					if (object instanceof SeparatorList.Separator<?>) {
						SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
						StockpileItem currentItem = (StockpileItem) separator.first();
						if (currentItem == null) {
							continue;
						}
						if (group.equals(currentItem.getGroup())) {
							if (expand) {
								separator.setLimit(Integer.MAX_VALUE);
							} else {
								separator.setLimit(0);
							}
						}
					}
				}
			} finally {
				separatorList.getReadWriteLock().writeLock().unlock();
			}
		}
	}

	private void removeGroupNoUpdate(List<Stockpile> removeStockpiles) {
		updateGroups(null, removeStockpiles, Collections.emptyList(), false);
	}

	private void removeGroup(Stockpile stockpile) {
		removeGroup(Collections.singletonList(stockpile));
	}

	private void removeGroup(List<Stockpile> removeStockpiles) {
		updateGroups(null, removeStockpiles, Collections.emptyList());
	}

	private void setGroup(String group, Stockpile stockpile) {
		setGroup(group, Collections.singletonList(stockpile));
	}

	private void setGroup(String group, List<Stockpile> addStockiples) {
		updateGroups(group, Collections.emptyList(), addStockiples);
	}

	private void updateGroups(String group, List<Stockpile> removeStockpiles, List<Stockpile> addStockiples) {
		updateGroups(group, removeStockpiles, addStockiples, true);
	}

	private void updateGroups(String group, List<Stockpile> removeStockpiles, List<Stockpile> addStockiples, boolean updateTable) {
		//Add StockpileItems
		List<StockpileItem> stockpileItems = new ArrayList<>();
		//Updated
		Settings.lock("Stockpile (Stockpile Group)");
		//Add
		for (Stockpile stockpile : addStockiples) {
			Settings.get().getStockpileGroupSettings().setGroup(stockpile, group);
			stockpileItems.addAll(stockpile.getItems());
			stockpileItems.addAll(stockpile.getSubpileTableItems());
		}
		//Remove
		for (Stockpile stockpile : removeStockpiles) {
			Settings.get().getStockpileGroupSettings().removeGroup(stockpile);
			stockpileItems.addAll(stockpile.getItems());
			stockpileItems.addAll(stockpile.getSubpileTableItems());
		}
		Settings.unlock("Stockpile (Stockpile Group)");
		if (updateTable) {
			//Lock Table
			beforeUpdateData();
			try {
				eventList.getReadWriteLock().writeLock().lock();
				eventList.removeAll(stockpileItems);
				enableGroupFirstUpdate();
				eventList.addAll(stockpileItems);
			} finally {
				eventList.getReadWriteLock().writeLock().unlock();
			}
			//Unlcok Table
			afterUpdateData();
		}
	}

	private void loadGroupStockpileExpandedState() {
		try {
			separatorList.getReadWriteLock().writeLock().lock();
			for (int i = 0; i < separatorList.size(); i++) {
				Object object = separatorList.get(i);
				if (object instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
					StockpileItem currentItem = (StockpileItem) separator.first();
					if (Settings.get().getStockpileGroupSettings().isGroupExpanded(currentItem.getGroup())
						&& Settings.get().getStockpileGroupSettings().isStockpileExpanded(currentItem.getStockpile())) {
						separator.setLimit(Integer.MAX_VALUE);
					} else {
						separator.setLimit(0);
					}
				}
			}
		} finally {
			separatorList.getReadWriteLock().writeLock().unlock();
		}
	}

	protected static void enableGroupFirstUpdate() {
		updateFirstStockpileGroup = true;
	}

	private void updateGroupFirst() {
		if (!updateFirstStockpileGroup) {
			return;
		}
		try {
			Map<String, Stockpile> groups = new HashMap<>();
			separatorList.getReadWriteLock().writeLock().lock();
			for (int i = 0; i < separatorList.size(); i++) {
				Object object = separatorList.get(i);
				if (object instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
					StockpileItem stockpileItem = (StockpileItem) separator.first();
					if (stockpileItem == null) { // handle 'late' rendering calls after this separator is invalid
						continue;
					}
					String group = stockpileItem.getGroup();
					//if (Settings.get().getStockpileGroupSettings().isGroupExpanded(group)
					//		&& !group.isEmpty() && !groups.containsKey(group)) {
					if(!group.isEmpty() && !groups.containsKey(group)) {
						groups.put(group, stockpileItem.getStockpile());
					}
				}
			}
			Settings.get().getStockpileGroupSettings().setGroupFirst(groups);
		} finally {
			updateFirstStockpileGroup = false;
			separatorList.getReadWriteLock().writeLock().unlock();
		}
	}

	private String getGroupName(String title, boolean canOverwrite, String original, String last) {
		String group = (String)JOptionInput.showInputDialog(program.getMainWindow().getFrame(), TabsStockpile.get().groupAddName(), title, JOptionPane.PLAIN_MESSAGE, null, null, last);
		if (group == null) {
			return null;
		}
		if (group.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().groupAddEmpty(), title, JOptionPane.WARNING_MESSAGE);
			return getGroupName(title, canOverwrite, original, null);
		}
		Set<String> groups = Settings.get().getStockpileGroupSettings().getGroups();
		if (groups.contains(group)) {
			if (canOverwrite) {
				int returnValue = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), TabsStockpile.get().groupAddExist(), title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (returnValue == JOptionPane.OK_OPTION) {
					return group;
				} else {
					return getGroupName(title, canOverwrite, original, group);
				}
			} else if (original != null && !original.equals(group)) {
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().groupRenameExist(), title, JOptionPane.PLAIN_MESSAGE);
				return getGroupName(title, canOverwrite, original, group);
			}
		}
		return group;
	}

	private void removeStockpile(Stockpile stockpile) {
		removeStockpiles(Collections.singletonList(stockpile));
	}

	private void removeStockpiles(List<Stockpile> stockpiles) {
		List<StockpileItem> stockpileItems = new ArrayList<>();
		for (Stockpile stockpile : stockpiles) {
			stockpileItems.addAll(stockpile.getItems());
		}
		//Lock Table
		beforeUpdateData();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.removeAll(stockpileItems);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Unlcok Table
		afterUpdateData();
	}

	private void importEveXml() {
		jFileChooser.setSelectedFile(new File(""));
		int value = jFileChooser.showOpenDialog(program.getMainWindow().getFrame());
		if (value != JCustomFileChooser.APPROVE_OPTION) {
			return; //Cancel
		}
		Map<String, Map<Integer, Double>> fits = EveFittingReader.load(jFileChooser.getSelectedFile().getAbsolutePath());
		if (fits == null || fits.isEmpty()) {
			return;
		}
		List<String> selectedFits;
		if (fits.size() > 1) { //Select fits to import
			selectedFits = fitsSelectionDialog.show(fits.keySet(), false);
		} else { //one or less, no reason to show selection dialog
			selectedFits = new ArrayList<>(fits.keySet());
		}
		if (selectedFits == null || selectedFits.isEmpty()) {
			return;
		}
		
		Set<String> stockpiles = new HashSet<>();
		for (Stockpile stockpile : Settings.get().getStockpiles()) {
			stockpiles.add(stockpile.getName());
		}
		Set<String> existing = new HashSet<>();
		Set<String> open = new HashSet<>();
		for (String fit : selectedFits) {
			if (stockpiles.contains(fit)) { //Exist
				existing.add(fit);
			} else {
				open.add(fit);
			}
		}
		
		if (open.size() > 1) {
			OptionEnum option = null;
			template = null;
			for (String fit : open) {
				option = importStockpileItems(fits.get(fit), option, options(ImportOptions.TEMPLATE, ImportOptions.NEW, ImportOptions.ADD), fit);
			}
		} else {
			existing.addAll(open);
		}
		OptionEnum option = null;
		for (String fit : existing) {
			option = importStockpileItems(fits.get(fit), option, fit);
		}
	}

	private void importText() {
		TextImportType systemType = TextImportType.EVE_MULTIBUY;
		try {
			systemType = TextImportType.valueOf(Settings.get().getImportSettings(NAME, systemType));
		} catch (IllegalArgumentException ex) {
			//No problem, use default
		}
		importText("", systemType);
	}

	private void importText(String text, TextImportType selected) {
		textImport.importText(text, TextImportType.values(), selected, new TextImportHandler<TextImportType>() {
			@Override
			public void addItems(TextReturn<TextImportType> textReturn) {
				TextImportType importType = textReturn.getType();
				String importText = textReturn.getText();
				Map<Integer, Double> data = importType.importText(importText);
				//Validate Output
				if (data == null || data.isEmpty()) {
					JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().textInvalid(), GuiShared.get().textImport(), JOptionPane.PLAIN_MESSAGE);
					importText(importText, importType); //Again!
					return;
				}
				//Add items
				importStockpileItems(data, importType.getName());
			}
		});
	}

	private OptionEnum importStockpileItems(Map<Integer, Double> data, String name) {
		return importStockpileItems(data, null, options(ImportOptions.NEW, ImportOptions.ADD), name);
	}

	private OptionEnum importStockpileItems(Map<Integer, Double> data, OptionEnum option, String name) {
		return importStockpileItems(data, option, options(ImportOptions.NEW, ImportOptions.ADD), name);
	}

	private OptionEnum importStockpileItems(Map<Integer, Double> data, OptionEnum option, List<OptionEnum> options, String name) {
		return importOptions(data, option, 1, false, options, new StockpileImportAction<Map<Integer, Double>>() {
			@Override
			public String getName(Map<Integer, Double> data) {
				return name;
			}
			@Override
			public boolean action(Map<Integer, Double> data, OptionEnum xmlOptions) {
				if (xmlOptions == ImportOptions.TEMPLATE) {
					xmlOptions.setAll(true); //Always do this for all
					//Blueprint/Formula Options
					BpOptions bpOptions = JMenuStockpile.selectBpImportOptions(program, data.keySet(), false);
					if (bpOptions == null) {
						return false; //Cancelled
					}
					//Create Template Stockpile
					if (template == null) {
						template = stockpileDialog.showTemplate();
					}
					if (template == null) { //Dialog cancelled
						return false; //Retry
					}
					//Create Stockpile from template
					Stockpile stockpile = new Stockpile(name, template);
					//Create items
					List<StockpileItem> items = JMenuStockpile.toStockpileItems(program, bpOptions, stockpile, data.keySet(), data, Collections.emptyMap());
					if (items == null) {
						return false; //Cancelled
					}
					Settings.lock("Stockpile (Import Items Template)");
					for (StockpileItem item : items) {
						stockpile.add(item);
					}
					addSettingStockpile(stockpile, true); //Add imported stockpile to Settings
					Settings.unlock("Stockpile (Import Items Template)");
					program.saveSettings("Stockpile (Import Items Template)");
					//Update UI
					addStockpile(stockpile); //Add imported stockpile to Settings
				} else if (xmlOptions == ImportOptions.NEW) {
					//Blueprint/Formula Options
					BpOptions bpOptions = JMenuStockpile.selectBpImportOptions(program, data.keySet(), false);
					if (bpOptions == null) {
						return false; //Cancelled
					}
					//Create Stockpile
					Stockpile stockpile = stockpileDialog.showAdd(name);
					if (stockpile == null) { //Dialog cancelled
						return false; //Retry
					}
					//Create items
					List<StockpileItem> items = JMenuStockpile.toStockpileItems(program, bpOptions, stockpile, data.keySet(), data, Collections.emptyMap());
					if (items == null) {
						return false; //Cancelled
					}
					Settings.lock("Stockpile (Import Items New)");
					for (StockpileItem item : items) {
						stockpile.add(item);
					}
					Settings.unlock("Stockpile (Import Items New)");
					program.saveSettings("Stockpile (Import Items New)");
					//Update stockpile data
					addStockpile(stockpile);
					scrollToSctockpile(stockpile);
				} else if (xmlOptions == ImportOptions.ADD) {
					//Select Stockpiles
					List<Stockpile> stockpiles = stockpileSelectionDialog.show(getShownStockpiles(), Settings.get().getStockpiles(), TabsStockpile.get().showHidden(), false);
					if (stockpiles == null) {
						return false;
					}
					//Stand-in stockpile (will be replaced)
					Stockpile stockpile = new Stockpile("", 1L, new ArrayList<>(), 1.0, false);
					//Create items
					List<StockpileItem> items = JMenuStockpile.toStockpileItems(program, stockpile, data.keySet(), data, Collections.emptyMap(), false);
					if (items == null) {
						return false; //Cancelled
					}
					//Merge Into
					for (Stockpile existingStockpile : stockpiles) {
						addToStockpile(existingStockpile, items, true, false); //Merge imported stockpile items into existing stockpiles
					}
					program.saveSettings("Stockpile (Import Items Add)");
				}
				//Skip - Do nothing
				return true;
			}
		});
	}

	private void importXml() {
		jFileChooser.setSelectedFile(new File(""));
		int value = jFileChooser.showOpenDialog(program.getMainWindow().getFrame());
		if (value == JCustomFileChooser.APPROVE_OPTION) {
			List<Stockpile> stockpiles = SettingsReader.loadStockpile(jFileChooser.getSelectedFile().getAbsolutePath());
			if (stockpiles != null) {
				importStockpiles(stockpiles);
			} else {
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importXmlFailedMsg(), TabsStockpile.get().importFailedTitle(), JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private void importXmlText() {
		jTextDialog.setLineWrap(true);
		String importText = jTextDialog.importText();
		jTextDialog.setLineWrap(false);
		if (importText == null) {
			return; //Cancel
		}
		List<Stockpile> stockpiles = StockpileDataReader.load(importText);
		if (stockpiles != null) {
			importStockpiles(stockpiles);
		} else {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importTextFailedMsg(), TabsStockpile.get().importFailedTitle(), JOptionPane.WARNING_MESSAGE);
		}
	}

	private void importStockpiles(List<Stockpile> stockpiles) {
		if (stockpiles == null) {
			return;
		}
		stockpiles = stockpileSelectionDialog.show(stockpiles, false);
		if (stockpiles == null) {
			return;
		}
		List<Stockpile> existing = new ArrayList<>();
		List<Stockpile> open = new ArrayList<>();
		for (Stockpile stockpile : stockpiles) {
			if (Settings.get().getStockpiles().contains(stockpile)) { //Exist
				existing.add(stockpile);
			} else {
				open.add(stockpile);
			}
		}
		boolean save = importStockpiles(open, options(ImportOptions.KEEP, ImportOptions.NEW, ImportOptions.ADD, ImportOptions.SKIP));
		save = importStockpiles(existing, options(ImportOptions.RENAME, ImportOptions.MERGE, ImportOptions.OVERWRITE, ImportOptions.ADD, ImportOptions.SKIP)) | save;
		if (save) {
			program.saveSettings("Stockpile (Import)");
		}
	}

	private List<OptionEnum> options(OptionEnum... options) {
		return Arrays.asList(options);
	}

	private boolean importStockpiles(List<Stockpile> stockpiles, List<OptionEnum> options) {
		boolean save = false;
		int count = stockpiles.size();
		OptionEnum option = null;
		for (Stockpile stockpile : stockpiles) {
			option = importOptions(stockpile, option, count, true, options, new StockpileImportAction<Stockpile>() {
				@Override
				public String getName(Stockpile value) {
					return value.getName();
				}
				@Override
				public boolean action(Stockpile value, OptionEnum xmlOptions) {
					if (xmlOptions == ImportOptions.KEEP) {
						Settings.lock("Stockpile (Import New)");
						addSettingStockpile(value, true); //Add
						Settings.unlock("Stockpile (Import New)");
						//Update UI
						addStockpile(value);
					} else if (xmlOptions == ImportOptions.RENAME || xmlOptions == ImportOptions.NEW) {
						Stockpile returnRename = stockpileDialog.showRename(value); //Rename stockpile
						if (returnRename == null) { //Cancel
							return false;
						}
						//Update UI
						addStockpile(returnRename);
					} else if (xmlOptions == ImportOptions.MERGE) {
						int index = Settings.get().getStockpiles().indexOf(value); //Get index of old Stockpile
						Stockpile mergeStockpile = Settings.get().getStockpiles().get(index); //Get old stockpile
						addToStockpile(mergeStockpile, value.getItems(), true, true); //Merge old and imported stockpiles
					} else if (xmlOptions == ImportOptions.OVERWRITE) {
						Settings.lock("Stockpile (Import Overwrite)");
						//Remove
						int index = Settings.get().getStockpiles().indexOf(value); //Get index of old Stockpile
						Stockpile removeStockpile = Settings.get().getStockpiles().get(index); //Get old stockpile
						removeStockpile(removeStockpile); //Remove old stockpile from the UI
						Settings.get().getStockpiles().remove(removeStockpile); //Remove old stockpile from the Settings
						//Add
						addSettingStockpile(value, true); //Add imported stockpile to Settings
						Settings.unlock("Stockpile (Import Overwrite)");
						//Update UI
						addStockpile(value); //Add imported stockpile to Settings
					} else if (xmlOptions == ImportOptions.ADD) {
						List<Stockpile> stockpiles = stockpileSelectionDialog.show(getShownStockpiles(), Settings.get().getStockpiles(), TabsStockpile.get().showHidden(), false);
						if (stockpiles == null) {
							return false;
						}
						for (Stockpile existingStockpile : stockpiles) {
							addToStockpile(existingStockpile, value.getItems(), true, false); //Merge imported stockpile items into existing stockpiles
						}
						program.saveSettings("Stockpile (Import Merge)");
					}
					//Skip - Do nothing
					return true;
				}
			});
			if (option == ImportOptions.KEEP || option == ImportOptions.OVERWRITE || option == ImportOptions.ADD) {
				save = true;
			}
			count--;
		}
		return save;
	}

	private <T> OptionEnum importOptions(T value, OptionEnum option, int count, boolean showAll, List<OptionEnum> options, StockpileImportAction<T> action) {
		if (option == null || !option.isAll()) { //Not decided - ask what to do
			option = stockpileImportDialog.show(action.getName(value),  TabsStockpile.get().importOptions(),  TabsStockpile.get().importOptionsAll(count), count > 1, showAll, options, option);
		}
		boolean ok = action.action(value, option);
		if (!ok) {
			option.setAll(false);
			return importOptions(value, option, count, showAll, options, action); //Retry - if RENAME_ALL, ask again
		}
		return option;
	}

	private List<Stockpile> getShownStockpiles() {
		return getShownStockpiles(program);
	}

	public static List<Stockpile> getShownStockpiles(Program program) {
		return getShownStockpiles(program.getProfileManager());
	}

	public static List<Stockpile> getShownStockpiles(ProfileManager profileManager) {
		List<Stockpile> shown = new ArrayList<>();
		for (Stockpile stockpile : Settings.get().getStockpiles()) {
			if (profileManager.getStockpileIDs().isHidden(stockpile.getStockpileID())) {
				continue;
			}
			shown.add(stockpile);
		}
		return shown;
	}

	public static void addSettingStockpile(Stockpile stockpile, boolean sort) {
		Settings.get().getStockpiles().add(stockpile);
		if (sort) {
			sortSettingStockpile();
		}
	}

	public static void sortSettingStockpile() {
		Collections.sort(Settings.get().getStockpiles());
	}

	private void exportXml() {
		List<Stockpile> stockpiles = stockpileSelectionDialog.show(getShownStockpiles(), Settings.get().getStockpiles(), TabsStockpile.get().showHidden(), false);
		if (stockpiles != null) {
			jFileChooser.setSelectedFile(new File(""));
			int value = jFileChooser.showSaveDialog(program.getMainWindow().getFrame());
			if (value == JCustomFileChooser.APPROVE_OPTION) {
				SettingsWriter.saveStockpiles(stockpiles, jFileChooser.getSelectedFile().getAbsolutePath());
			}
		}
	}

	private void exportText() {
		List<Stockpile> stockpiles = stockpileSelectionDialog.show(getShownStockpiles(), Settings.get().getStockpiles(), TabsStockpile.get().showHidden(), false);
		if (stockpiles != null) {
			String json = StockpileDataWriter.save(stockpiles);
			if (json != null) {
				jTextDialog.setLineWrap(true);
				jTextDialog.exportText(json);
				jTextDialog.setLineWrap(false);
			}
		}
	}

	private Stockpile getSelectedStockpile() {
		int index = jTable.getSelectedRow();
		if (index < 0 || index >= tableModel.getRowCount()) {
			return null;
		}
		Object o = tableModel.getElementAt(index);
		if (o instanceof SeparatorList.Separator<?>) {
			SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) o;
			StockpileItem item = (StockpileItem) separator.first();
			return item.getStockpile();
		}
		return null;
	}

	private class StockpileTableMenu implements TableMenu<StockpileItem> {
		@Override
		public MenuData<StockpileItem> getMenuData() {
			return new MenuData<>(selectionModel.getSelected());
		}

		@Override
		public JMenu getFilterMenu() {
			return filterControl.getMenu(jTable, selectionModel.getSelected());
		}

		@Override
		public JMenu getColumnMenu() {
			return new JMenuColumns<>(program, tableFormat, tableModel, jTable, NAME);
		}

		@Override
		public void addInfoMenu(JPopupMenu jPopupMenu) {
			JMenuInfo.stockpileItem(jPopupMenu, selectionModel.getSelected());
		}

		@Override
		public void addToolMenu(JComponent jComponent) {
			List<StockpileItem> edit = new ArrayList<>();
			List<StockpileItem> delete = new ArrayList<>();
			List<StockpileItem> items = new ArrayList<>();
			ArrayList<Object> selected = new ArrayList<>(selectionModel.getSelected());
			for (Object object : selected) {
				if (object.getClass() == StockpileItem.class) {
					StockpileItem item = (StockpileItem) object;
					edit.add(item);
					delete.add(item);
					items.add(item);
				} else if (object instanceof SubpileStock) {
					SubpileStock item = (SubpileStock) object;
					if (item.isEditable()) {
						edit.add(item);
					}
				}
			}
			jComponent.add(new JStockpileItemMenu(program, edit, delete, items));
			MenuManager.addSeparator(jComponent);
		}
	}

	private class ListenerClass implements ActionListener, ListEventListener<StockpileItem>, ColumnValueChangeListener {
		@Override
		public void listChanged(final ListEvent<StockpileItem> listChanges) {
			List<StockpileItem> items = EventListManager.safeList(filterList);
			//Remove StockpileTotal and SeparatorList.Separator
			for (int i = 0; i < items.size(); i++) {
				Object object = items.get(i);
				if ((object instanceof SeparatorList.Separator) || (object instanceof StockpileTotal)) {
					items.remove(i);
					i--;
				}
			}

			double volumnNow = 0;
			double volumnNeeded = 0;
			double valueNow = 0;
			double valueNeeded = 0;

			for (StockpileItem item : items) {
				volumnNow = volumnNow + item.getVolumeNow();
				if (item.getVolumeNeeded() < 0) { //Only add if negative
					volumnNeeded = volumnNeeded + item.getVolumeNeeded();
				}
				valueNow = valueNow + item.getValueNow();
				if (item.getValueNeeded() < 0) { //Only add if negative
					valueNeeded = valueNeeded + item.getValueNeeded();
				}
			}

			jVolumeNow.setNumber(TabsStockpile.get().now(), volumnNow);
			jValueNow.setNumber(TabsStockpile.get().now(), valueNow);
			jVolumeNeeded.setNumber(TabsStockpile.get().needed(), volumnNeeded);
			jValueNeeded.setNumber(TabsStockpile.get().needed(), valueNeeded);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (StockpileCellAction.SHOPPING_LIST_SINGLE.name().equals(e.getActionCommand())) { //Shopping list single
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile != null) {
					stockpileShoppingListDialog.show(stockpile);
				}
			} else if (StockpileAction.SHOPPING_LIST_MULTI.name().equals(e.getActionCommand())) { //Shopping list multi
				List<Stockpile> stockpiles = stockpileSelectionDialog.show(getShownStockpiles(), Settings.get().getStockpiles(), TabsStockpile.get().showHidden(), false);
				if (stockpiles != null) {
					stockpileShoppingListDialog.show(stockpiles);
				}
			} else if (StockpileAction.SHOW_HIDE.name().equals(e.getActionCommand())) { //Shopping list multi
				List<Stockpile> selected = new ArrayList<>();
				Set<Long> all = new HashSet<>();
				for (Stockpile stockpile : Settings.get().getStockpiles()) {
					all.add(stockpile.getStockpileID());
					if (program.getProfileManager().getStockpileIDs().isShown(stockpile.getStockpileID())) {
						selected.add(stockpile);
					}
				}
				List<Stockpile> stockpiles = stockpileSelectionDialog.show(Settings.get().getStockpiles(), selected, true);
				if (stockpiles == null) {
					return; //Cancel
				}
				Set<Long> hidden = new HashSet<>(all);
				for (Stockpile stockpile : stockpiles) {
					hidden.remove(stockpile.getStockpileID());
				}
				Set<Long> oldData = program.getProfileManager().getStockpileIDs().getHidden();
				if (!oldData.equals(hidden)) {
					//Hide
					Set<Long> hide = new HashSet<>(hidden); //To be hidden
					hide.removeAll(oldData); //Remove already hidden
					//Show
					Set<Long> show = new HashSet<>(oldData); //Currently hidden
					show.removeAll(hidden); //Remove still hidden
					//Update data (must be done after making the removed and added lists)
					program.getProfileManager().getStockpileIDs().setHidden(hidden);
					//Update GUI
					for (Stockpile stockpile : Settings.get().getStockpiles()) {
						long stockpileID = stockpile.getStockpileID();
						if (hide.contains(stockpileID)) { //Hidden
							removeItems(stockpile.getItems());
						} else if (show.contains(stockpileID)) { //Shown
							addStockpile(stockpile);
						} //Else: Not changed
					}
				}
			} else if (StockpileAction.SUBPILE_TREE.name().equals(e.getActionCommand())) {
				List<StockpileItem> updated = new ArrayList<>();
				for (Stockpile stockpile : getShownStockpiles()) {
					updated.addAll(stockpile.getSubpileStocks());
				}
				try {
					eventList.getReadWriteLock().writeLock().lock();
					if (jShowSubpileTree.isSelected()) {
						eventList.addAll(updated);
					} else {
						eventList.removeAll(updated);
					}
				} finally {
					eventList.getReadWriteLock().writeLock().unlock();
				}
				Settings.lock("Show Subpile Tree");
				Settings.get().setShowSubpileTree(jShowSubpileTree.isSelected());
				Settings.unlock("Show Subpile Tree");
				program.saveSettings("Show Subpile Tree");
			} else if (StockpileAction.IMPORT_TEXT.name().equals(e.getActionCommand())) { //Add stockpile (EFT Import)
				importText();
			} else if (StockpileAction.IMPORT_XML.name().equals(e.getActionCommand())) { //Add stockpile (Xml)
				importXml();
			} else if (StockpileAction.IMPORT_EVE_XML_FIT.name().equals(e.getActionCommand())) { //Add stockpile (Xml)
				importEveXml();
			} else if (StockpileAction.IMPORT_XML_TEXT.name().equals(e.getActionCommand())) { //Add stockpile (Xml)
				importXmlText();
			} else if (StockpileAction.EXPORT_XML.name().equals(e.getActionCommand())) { //Export XML
				exportXml();
			} else if (StockpileAction.EXPORT_TEXT.name().equals(e.getActionCommand())) { //Export XML
				exportText();
			} else if (StockpileAction.ADD_STOCKPILE.name().equals(e.getActionCommand())) { //Add stockpile
				Stockpile stockpile = stockpileDialog.showAdd();
				if (stockpile != null) {
					addStockpile(stockpile);
					scrollToSctockpile(stockpile);
				}
			} else if (StockpileCellAction.EDIT_STOCKPILE.name().equals(e.getActionCommand())) { //Edit stockpile
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile != null) {
					boolean updated = stockpileDialog.showEdit(stockpile);
					if (updated) {
						//To tricker resort
						removeStockpile(stockpile);
						addStockpile(stockpile);
					}
				}
			} else if (StockpileCellAction.CLONE_STOCKPILE.name().equals(e.getActionCommand())) { //Clone stockpile
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile != null) {
					Stockpile cloneStockpile = stockpileDialog.showClone(stockpile);
					if (cloneStockpile != null) {
						addStockpile(cloneStockpile);
					}
				}
			} else if (StockpileCellAction.HIDE_STOCKPILE.name().equals(e.getActionCommand())) { //Hide stockpile
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile == null) {
					return;
				}
				program.getProfileManager().getStockpileIDs().hide(stockpile.getStockpileID());
				removeItems(stockpile.getItems());
			} else if (StockpileCellAction.DELETE_STOCKPILE.name().equals(e.getActionCommand())) { //Delete stockpile
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile != null) {
					int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), stockpile.getName(), TabsStockpile.get().deleteStockpileTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (value == JOptionPane.OK_OPTION) {
						Settings.lock("Stockpile (Delete Stockpile)");
						//Remove stockpile
						Settings.get().getStockpiles().remove(stockpile);
						//Remove Group
						Settings.get().getStockpileGroupSettings().removeGroup(stockpile);
						StockpileSeparatorTableCell.updateGroups(this);
						//Remove subpile links
						for (Stockpile parentStockpile : stockpile.getSubpiles().keySet()) {
							parentStockpile.removeSubpileLink(stockpile);
						}
						stockpile.getSubpiles().clear(); //Remove all Subpiles
						updateSubpile(stockpile); //Remove SubpileItems from Table
						//Remove deleted stockpile from all subpiles
						for (Stockpile parentStockpile : stockpile.getSubpileLinks()) {
							parentStockpile.getSubpiles().remove(stockpile);
							updateSubpile(parentStockpile);
						}
						Settings.unlock("Stockpile (Delete Stockpile)");
						program.saveSettings("Stockpile (Delete Stockpile)");
						removeStockpile(stockpile);
					}
				}
			} else if (StockpileAction.DELETE_STOCKPILE_MULTI.name().equals(e.getActionCommand())) { //Delete stockpiles
				List<Stockpile> stockpiles = stockpileSelectionDialog.show(getShownStockpiles(), Settings.get().getStockpiles(), TabsStockpile.get().showHidden(), false);
				if (stockpiles == null || stockpiles.isEmpty()) {
					return;
				}
				String msg;
				if (stockpiles.size() > 1) {
					msg = TabsStockpile.get().deleteStockpileMsg(stockpiles.size());
				} else {
					msg = stockpiles.get(0).getName();
				}
				int value = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), msg, TabsStockpile.get().deleteStockpileTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (value != JOptionPane.OK_OPTION) {
					return;
				}
				//Remove Groups
				removeGroupNoUpdate(stockpiles);
				//Update Table Cell
				StockpileSeparatorTableCell.updateGroups(this);
				Settings.lock("Stockpile (Delete Stockpile)");
				for (Stockpile stockpile : stockpiles) {
					//Remove stockpile
					Settings.get().getStockpiles().remove(stockpile);
					//Remove subpile links
					for (Stockpile parentStockpile : stockpile.getSubpiles().keySet()) {
						parentStockpile.removeSubpileLink(stockpile);
					}
					stockpile.getSubpiles().clear(); //Remove all Subpiles
					updateSubpile(stockpile); //Remove SubpileItems from Table
					//Remove deleted stockpile from all subpiles
					for (Stockpile parentStockpile : stockpile.getSubpileLinks()) {
						parentStockpile.getSubpiles().remove(stockpile);
						updateSubpile(parentStockpile);
					}
				}
				Settings.unlock("Stockpile (Delete Stockpile)");
				//Remove stockpiles from GUI
				removeStockpiles(stockpiles);
				program.saveSettings("Stockpile (Delete Stockpile)");
				
			} else if (StockpileCellAction.ADD_ITEM.name().equals(e.getActionCommand())) { //Add item
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile != null) {
					List<StockpileItem> stockpileItems = stockpileItemDialog.showAdd(stockpile);
					if (stockpileItems != null) { //Edit/Add/Update existing or cancel
						addToStockpile(stockpile, stockpileItems, false, true);
					}
				}
			} else if (StockpileAction.COLLAPSE_GROUPS.name().equals(e.getActionCommand())) {
				expandGroups(false, MATCH_ALL_GROUPS);
			} else if (StockpileAction.EXPAND_GROUPS.name().equals(e.getActionCommand())) {
				expandGroups(true, MATCH_ALL_GROUPS);
			} else if (StockpileAction.EDIT_GROUPS.name().equals(e.getActionCommand())) {
				Set<String> groups = Settings.get().getStockpileGroupSettings().getGroups();
				jAutoCompleteDialog.updateData(groups);
				String group = jAutoCompleteDialog.show();
				if (group == null || group.isEmpty()) {
					return;
				}
				List<Stockpile> oldStockpiles = Settings.get().getStockpileGroupSettings().getStockpiles(group);
				List<Stockpile> newStockpiles = stockpileSelectionDialog.show(getShownStockpiles(), oldStockpiles, Settings.get().getStockpiles(), TabsStockpile.get().showHidden(), true);
				if (newStockpiles == null) {
					return;
				}
				List<Stockpile> added = new ArrayList<>(newStockpiles);
				added.removeAll(oldStockpiles);
				List<Stockpile> removed = new ArrayList<>(oldStockpiles);
				removed.removeAll(newStockpiles);
				updateGroups(group, removed, added);
				//Update Table Cell
				StockpileSeparatorTableCell.updateGroups(this);
				//Save Settings
				program.saveSettings("Stockpile (Stockpile Edit Groups)");
			} else if (StockpileCellAction.GROUP_RENAME.name().equals(e.getActionCommand())) {
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile == null) {
					return;
				}
				String oldGroup = Settings.get().getStockpileGroupSettings().getGroup(stockpile);
				if (oldGroup == null || oldGroup.isEmpty()) {
					return;
				}
				String newGroup = getGroupName(TabsStockpile.get().groupRenameTitle(), false, oldGroup, oldGroup);
				if (newGroup == null || newGroup.isEmpty() || newGroup.equals(oldGroup)) {
					return;
				}
				List<Stockpile> stockpiles = Settings.get().getStockpileGroupSettings().getStockpiles(oldGroup);
				//Backup expanded
				boolean expanded = Settings.get().getStockpileGroupSettings().isGroupExpanded(oldGroup);
				//Update
				setGroup(newGroup, stockpiles);
				//Update Table Cell
				StockpileSeparatorTableCell.updateGroups(this);
				//Save Settings
				program.saveSettings("Stockpile (Stockpile Rename Group)");
				//Restore expanded
				expandGroups(expanded, new MatchGroup(newGroup));
			} else if (StockpileAction.COLLAPSE.name().equals(e.getActionCommand())) { //Collapse all
				jTable.expandSeparators(false);
				Settings.get().getStockpileGroupSettings().setStockpileExpanded(Settings.get().getStockpiles(), false);
			} else if (StockpileAction.EXPAND.name().equals(e.getActionCommand())) { //Expand all
				jTable.expandSeparators(true);
				Settings.get().getStockpileGroupSettings().setStockpileExpanded(Settings.get().getStockpiles(), true);
			} else if (StockpileCellAction.GROUP_EXPAND.name().equals(e.getActionCommand())) {
				expandGroupStockpiles(true);
			} else if (StockpileCellAction.GROUP_COLLAPSE.name().equals(e.getActionCommand())) {
				expandGroupStockpiles(false);
			} else if (StockpileCellAction.GROUP_TOGGLE_COLLAPSE.name().equals(e.getActionCommand())) {
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile == null) {
					return;
				}
				String group = Settings.get().getStockpileGroupSettings().getGroup(stockpile);
				boolean expand = !Settings.get().getStockpileGroupSettings().isGroupExpanded(group);
				expandGroups(expand, new MatchGroup(group));
			} else if (StockpileCellAction.GROUP_NEW.name().equals(e.getActionCommand())) {
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile == null) {
					return;
				}
				String group = getGroupName(TabsStockpile.get().groupAddTitle(), true, null, null);
				if (group == null) {
					return; //Cancelled
				}
				String oldGroup = Settings.get().getStockpileGroupSettings().getGroup(stockpile);
				if (oldGroup.equals(group)) {
					return; //No change
				}
				setGroup(group, stockpile); //Change or add group
				//Update Table Cell
				StockpileSeparatorTableCell.updateGroups(this);
				//Save Settings
				program.saveSettings("Stockpile (Stockpile New Group)");
			} else if (StockpileCellAction.GROUP_CHANGE_ADD.name().equals(e.getActionCommand())) {
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile == null) {
					return;
				}
				Object source = e.getSource();
				String newGroup;
				if (source instanceof JCheckBoxMenuItem) {
					newGroup = ((JCheckBoxMenuItem)source).getText();
				} else {
					return;
				}
				String oldGroup = Settings.get().getStockpileGroupSettings().getGroup(stockpile);
				if (newGroup.equals(oldGroup)) {
					removeGroup(stockpile); //Remove from group
				} else {
					setGroup(newGroup, stockpile); //Change or add group
				}
				//Update Table Cell
				StockpileSeparatorTableCell.updateGroups(this);
				//Save Settings
				program.saveSettings("Stockpile (Stockpile Add Group)");
			} else if (StockpileCellAction.SUBPILES.name().equals(e.getActionCommand())) {
				Stockpile stockpile = getSelectedStockpile();
				if (stockpile != null) {
					List<Stockpile> listData = new ArrayList<>();
					listData.clear();
					listData.addAll(Settings.get().getStockpiles());
					listData.remove(stockpile); //Remove self
					remove(listData, stockpile, stockpile.getSubpileLinks()); //Remove interlinked
					Collections.sort(listData);

					List<Stockpile> stockpiles = stockpileSelectionDialog.show(listData, stockpile.getSubpiles().keySet(), true);
					if (stockpiles == null) {
						return;
					}
					Settings.lock("Stockpile (Updated Subpiles)");
					//Remove old Links
					for (Stockpile parentStockpile : stockpile.getSubpiles().keySet()) {
						parentStockpile.removeSubpileLink(stockpile);
					}
					Map<Stockpile, Double> old = new HashMap<>(stockpile.getSubpiles()); //Copy
					stockpile.getSubpiles().clear();
					for (Stockpile parentStockpile : stockpiles) {
						Double value = old.get(parentStockpile);
						if (value != null) {
							stockpile.getSubpiles().put(parentStockpile, value);
						} else {
							stockpile.getSubpiles().put(parentStockpile, 1.0);
						}
						parentStockpile.addSubpileLink(stockpile);
					}
					Settings.unlock("Stockpile (Updated Subpiles)");
					updateSubpile(stockpile);
					program.saveSettings("Stockpile (Updated subpiles)");
				}
			}
		}

		private void remove(List<Stockpile> listData, Stockpile parentLink, List<Stockpile> subpileLinks) {
			for (Stockpile subpileLink : subpileLinks) {
				listData.remove(subpileLink);
				remove(listData, parentLink, subpileLink.getSubpileLinks());
			}
		}


		@Override
		public void columnValueChanged() {
			program.saveSettings("Stockpile: Target changed");
		}
	}

	public static class StockpileSeparatorComparator implements Comparator<StockpileItem> {
		@Override
		public int compare(final StockpileItem o1, final StockpileItem o2) {
			return o1.getSeparator().compareTo(o2.getSeparator());
		}
	}

	public class StockpileFilterControl extends FilterControl<StockpileItem> {

		public StockpileFilterControl(EventList<StockpileItem> exportEventList) {
			super(program.getMainWindow().getFrame(),
					NAME,
					tableFormat,
					eventList,
					exportEventList,
					filterList
					);
		}

		@Override
		protected void afterFilter() {
			jTable.loadExpandedState();
			//Load Expanded State 
			loadGroupStockpileExpandedState();
		}

		@Override
		protected void beforeFilter() {
			enableGroupFirstUpdate();
			jTable.saveExpandedState();
		}

		@Override
		public void saveSettings(final String msg) {
			program.saveSettings("Stockpile Table: " + msg); //Save Stockpile Filters and Export Settings
		}
	}

	public static class TotalComparator implements Comparator<StockpileItem> {

		private final Comparator<StockpileItem> comparator;

		public TotalComparator() {
			List<Comparator<StockpileItem>> comparators = new ArrayList<>();
			comparators.add(new StockpileSeparatorComparator());
			comparators.add(new InnerSubpileComparator());
			comparators.add(new InnerTotalComparator());
			comparator = GlazedLists.chainComparators(comparators);
		}

		@Override
		public int compare(final StockpileItem o1, final StockpileItem o2) {
			return comparator.compare(o1, o2);
		}

		private static class InnerSubpileComparator implements Comparator<StockpileItem> {
			@Override
			public int compare(final StockpileItem o1, final StockpileItem o2) {
				if ((o1 instanceof SubpileItem) && (o2 instanceof SubpileItem)) {
					SubpileItem item1 = (SubpileItem) o1;
					SubpileItem item2 = (SubpileItem) o2;
					return item1.getOrder().compareTo(item2.getOrder()); //Equal (both SubpileItem)
				} else if (o1 instanceof SubpileItem) {
					return -1; //Before
				} else if (o2 instanceof SubpileItem) {
					return 1; //After
				} else {
					return 0; //Equal (not SubpileItem)
				}
			}
		}

		private static class InnerTotalComparator implements Comparator<StockpileItem> {
			@Override
			public int compare(final StockpileItem o1, final StockpileItem o2) {
				if ((o1 instanceof StockpileTotal) && (o2 instanceof StockpileTotal)) {
					return 0; //Equal (both StockpileTotal)
				} else if (o1 instanceof StockpileTotal) {
					return 1; //After
				} else if (o2 instanceof StockpileTotal) {
					return -1; //Before
				} else {
					return 0; //Equal (not StockpileTotal)
				}
			}
		}
	}

	private static interface StockpileImportAction<T> {
		public String getName(T value);
		public boolean action(T value, OptionEnum xmlOptions);
	}

	private static interface GroupMatching {
		public boolean matches(String group);
	}

	private static class MatchAllGroups implements GroupMatching {
		@Override
		public boolean matches(String group) {
			return !group.isEmpty();
		}
	}

	private static class MatchGroup implements GroupMatching {

		private final String group;

		public MatchGroup(String group) {
			this.group = group;
		}

		@Override
		public boolean matches(String group) {
			return this.group.equals(group);
		}
	}
}
