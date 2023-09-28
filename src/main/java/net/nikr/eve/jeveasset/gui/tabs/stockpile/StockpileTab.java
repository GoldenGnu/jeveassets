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
		IMPORT_EFT,
		IMPORT_ISK_PER_HOUR,
		IMPORT_MULTIBUY,
		IMPORT_SHOPPING_LIST,
		IMPORT_TEXT,
		IMPORT_XML,
		IMPORT_EVE_XML_FIT,
		EXPORT_TEXT,
		EXPORT_XML,
		COLLAPSE,
		EXPAND,
		COLLAPSE_GROUPS,
		EXPAND_GROUPS
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
	private final TextImport textImport;

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

		jFileChooser = JCustomFileChooser.createFileChooser(program.getMainWindow().getFrame(), "xml");
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		stockpileDialog = new StockpileDialog(program);
		stockpileItemDialog = new StockpileItemDialog(program);
		stockpileShoppingListDialog = new StockpileShoppingListDialog(program);
		stockpileSelectionDialog = new JMultiSelectionDialog<>(program, TabsStockpile.get().selectStockpiles());
		fitsSelectionDialog = new JMultiSelectionDialog<>(program, TabsStockpile.get().selectFits());
		stockpileImportDialog = new JOptionsDialog(program);

		jTextDialog = new JTextDialog(program.getMainWindow().getFrame());
		textImport = new TextImport(program);

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

		JButton jShowHide = new JButton(TabsStockpile.get().showHide(), Images.EDIT_SHOW.getIcon());
		jShowHide.setActionCommand(StockpileAction.SHOW_HIDE.name());
		jShowHide.addActionListener(listener);
		jToolBar.addButton(jShowHide);

		jToolBar.addSeparator();

		JButton jShoppingList = new JButton(TabsStockpile.get().getShoppingList(), Images.STOCKPILE_SHOPPING_LIST.getIcon());
		jShoppingList.setActionCommand(StockpileAction.SHOPPING_LIST_MULTI.name());
		jShoppingList.addActionListener(listener);
		jToolBar.addButton(jShoppingList);

		JDropDownButton jImport = new JDropDownButton(TabsStockpile.get().importButton(), Images.EDIT_IMPORT.getIcon());
		jToolBar.addButton(jImport);

		JMenuItem jImportEFT = new JMenuItem(TabsStockpile.get().importEft(), Images.TOOL_SHIP_LOADOUTS.getIcon());
		jImportEFT.setActionCommand(StockpileAction.IMPORT_EFT.name());
		jImportEFT.addActionListener(listener);
		jImport.add(jImportEFT);

		JMenuItem jImportIskPerHour = new JMenuItem(TabsStockpile.get().importIskPerHour(), Images.TOOL_VALUES.getIcon());
		jImportIskPerHour.setActionCommand(StockpileAction.IMPORT_ISK_PER_HOUR.name());
		jImportIskPerHour.addActionListener(listener);
		jImport.add(jImportIskPerHour);

		JMenuItem jImportEveMultibuy = new JMenuItem(TabsStockpile.get().importEveMultibuy(), Images.MISC_EVE.getIcon());
		jImportEveMultibuy.setActionCommand(StockpileAction.IMPORT_MULTIBUY.name());
		jImportEveMultibuy.addActionListener(listener);
		jImport.add(jImportEveMultibuy);

		JMenuItem jImportEveXmlFit = new JMenuItem(TabsStockpile.get().importEveXml(), Images.MISC_XML.getIcon());
		jImportEveXmlFit.setActionCommand(StockpileAction.IMPORT_EVE_XML_FIT.name());
		jImportEveXmlFit.addActionListener(listener);
		jImport.add(jImportEveXmlFit);

		JMenuItem jImportShoppingList = new JMenuItem(TabsStockpile.get().importShoppingList(), Images.STOCKPILE_SHOPPING_LIST.getIcon());
		jImportShoppingList.setActionCommand(StockpileAction.IMPORT_SHOPPING_LIST.name());
		jImportShoppingList.addActionListener(listener);
		jImport.add(jImportShoppingList);

		JMenuItem jImportXml = new JMenuItem(TabsStockpile.get().importStockpilesXml(), Images.TOOL_STOCKPILE.getIcon());
		jImportXml.setActionCommand(StockpileAction.IMPORT_XML.name());
		jImportXml.addActionListener(listener);
		jImport.add(jImportXml);

		JMenuItem jImportText = new JMenuItem(TabsStockpile.get().importStockpilesText(), Images.EDIT_COPY.getIcon());
		jImportText.setActionCommand(StockpileAction.IMPORT_TEXT.name());
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
			toolBarMinWidth = jToolBar.getMinimumSize().width;
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
			if (stockpile.isContractsMatchAll()) { //Less items == may match now...
				updateStockpile(stockpile);
			}
		}
		//Lock Table
		beforeUpdateData();
		//Update list
		try {
			eventList.getReadWriteLock().writeLock().lock();
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
			eventList.addAll(stockpile.getItems());
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		//Unlcok Table
		afterUpdateData();
	}

	private void expandGroups(boolean expand, GroupMatching match) {
		List<StockpileItem> stockpileItems = new ArrayList<>();
		List<StockpileItem> ignoreItems = new ArrayList<>();
		List<Stockpile> changed = new ArrayList<>();
		List<String> groups = new ArrayList<>();
		for (Stockpile stockpile : getShownStockpiles()) {
			String group = Settings.get().getStockpileGroupSettings().getGroup(stockpile);
			if (match.matches(group) && Settings.get().getStockpileGroupSettings().isGroupExpanded(group) != expand) { //Match group + is changed
				stockpileItems.addAll(stockpile.getItems());
				stockpileItems.addAll(stockpile.getSubpileItems());
				if (Settings.get().getStockpileGroupSettings().isGroupFirst(stockpile)) {
					ignoreItems.add(stockpile.getIgnoreItem());
				}
				changed.add(stockpile);
				groups.add(group);
			}
		}
		//Update groups (must be done after the ShownStockpiles loop)
		for (String group : groups) {
			Settings.get().getStockpileGroupSettings().setGroupExpanded(group, expand);
		}
		if (expand) { //Expand
			//Lock Table
			beforeUpdateData();
			try {
				eventList.getReadWriteLock().writeLock().lock();
				eventList.removeAll(ignoreItems);
				eventList.addAll(stockpileItems);
			} finally {
				eventList.getReadWriteLock().writeLock().unlock();
			}
			//Unlcok Table
			afterUpdateData();
			//Load state
			try {
				separatorList.getReadWriteLock().writeLock().lock();
				for (int i = 0; i < separatorList.size(); i++) {
					Object object = separatorList.get(i);
					if (object instanceof SeparatorList.Separator<?>) {
						SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
						StockpileItem currentItem = (StockpileItem) separator.first();
						if (match.matches(currentItem.getGroup()) && changed.contains(currentItem.getStockpile())) {
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
		} else { //Collapse
			//Save state
			try {
				separatorList.getReadWriteLock().writeLock().lock();
				for (int i = 0; i < separatorList.size(); i++) {
					Object object = separatorList.get(i);
					if (object instanceof SeparatorList.Separator<?>) {
						SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
						StockpileItem currentItem = (StockpileItem) separator.first();
						if (match.matches(currentItem.getGroup()) && changed.contains(currentItem.getStockpile())) {
							Settings.get().getStockpileGroupSettings().setStockpileExpanded(currentItem.getStockpile(), separator.getLimit() != 0);
						}
					}
				}
			} finally {
				separatorList.getReadWriteLock().writeLock().unlock();
			}
			//Lock Table
			beforeUpdateData();
			try {
				eventList.getReadWriteLock().writeLock().lock();
				eventList.removeAll(stockpileItems);
				eventList.addAll(ignoreItems);
			} finally {
				eventList.getReadWriteLock().writeLock().unlock();
			}
			//Unlcok Table
			afterUpdateData();
			//Collapse
			try {
				separatorList.getReadWriteLock().writeLock().lock();
				for (int i = 0; i < separatorList.size(); i++) {
					Object object = separatorList.get(i);
					if (object instanceof SeparatorList.Separator<?>) {
						SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
						StockpileItem currentItem = (StockpileItem) separator.first();
						if (match.matches(currentItem.getGroup()) && changed.contains(currentItem.getStockpile())) {
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
		if (!Settings.get().getStockpileGroupSettings().isGroupExpanded(group)) { //Group is collapsed (Just update settings)
			for (Stockpile s : getShownStockpiles()) {
				if (group.equals(Settings.get().getStockpileGroupSettings().getGroup(s))) {
					Settings.get().getStockpileGroupSettings().setStockpileExpanded(s, expand);
				}
			}
			return;
		}
		try {
			separatorList.getReadWriteLock().writeLock().lock();
			for (int i = 0; i < separatorList.size(); i++) {
				Object object = separatorList.get(i);
				if (object instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
					StockpileItem currentItem = (StockpileItem) separator.first();
					if (currentItem instanceof JSeparatorTable.IgnoreSeparator || currentItem == null) {
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

	private void removeFromGroup(Stockpile stockpile, boolean update) {
		removeFromGroup(Collections.singletonList(stockpile), update);
	}

	private void removeFromGroup(List<Stockpile> stockpiles, boolean update) {
		List<StockpileItem> stockpileItems = new ArrayList<>();
		for (Stockpile stockpile : stockpiles) {
			stockpileItems.addAll(stockpile.getItems());
			stockpileItems.addAll(stockpile.getSubpileItems());
		}
		//Remove
		Map<String, Stockpile> oldFirst = new HashMap<>();
		Settings.lock("Stockpile (Stockpile Group)");
		for (Stockpile stockpile : stockpiles) {
			String previousGroup = Settings.get().getStockpileGroupSettings().getGroup(stockpile);
			if (!Settings.get().getStockpileGroupSettings().isGroupExpanded(previousGroup)) {
				Stockpile groupFirst = Settings.get().getStockpileGroupSettings().getGroupFirst(previousGroup);
				oldFirst.put(previousGroup, groupFirst);
			}
			Settings.get().getStockpileGroupSettings().removeGroup(stockpile);
		}
		Settings.unlock("Stockpile (Stockpile Group)");
		if (update) {
			//Lock Table
			beforeUpdateData();
			try {
				//Update
				eventList.getReadWriteLock().writeLock().lock();
				eventList.removeAll(stockpileItems);
				eventList.addAll(stockpileItems);
			} finally {
				eventList.getReadWriteLock().writeLock().unlock();
			}
			//Unlcok Table
			afterUpdateData();
		}
		//Update Groups First
		for (Map.Entry<String, Stockpile> entry : oldFirst.entrySet()) {
			updateGroupFirst(entry.getKey(), entry.getValue(), Settings.get().getStockpileGroupSettings().getGroupFirst(entry.getKey()));
		}
	}

	private void addToGroup(String group, Stockpile stockpile) {
		addToGroup(group, Collections.singletonList(stockpile));
	}

	private void addToGroup(String group, List<Stockpile> stockpiles) {
		List<StockpileItem> stockpileItems = new ArrayList<>();
		for (Stockpile stockpile : stockpiles) {
			stockpileItems.addAll(stockpile.getItems());
			stockpileItems.addAll(stockpile.getSubpileItems());
		}
		//Add
		Map<String, Stockpile> oldFirst = new HashMap<>();
		oldFirst.put(group, Settings.get().getStockpileGroupSettings().getGroupFirst(group));
		Settings.lock("Stockpile (Stockpile Group)");
		for (Stockpile stockpile : stockpiles) {
			String previousGroup = Settings.get().getStockpileGroupSettings().getGroup(stockpile);
			if (!Settings.get().getStockpileGroupSettings().isGroupExpanded(previousGroup)) {
				Stockpile groupFirst = Settings.get().getStockpileGroupSettings().getGroupFirst(group);
				oldFirst.put(previousGroup, groupFirst);
			}
			Settings.get().getStockpileGroupSettings().setGroup(stockpile, group);
		}
		Settings.unlock("Stockpile (Stockpile Group)");
		if (Settings.get().getStockpileGroupSettings().isGroupExpanded(group)) { //Expanded
			//Lock Table
			beforeUpdateData();
			try {
				//Update
				eventList.getReadWriteLock().writeLock().lock();
				eventList.removeAll(stockpileItems);
				eventList.addAll(stockpileItems);
			} finally {
				eventList.getReadWriteLock().writeLock().unlock();
			}
			//Unlcok Table
			afterUpdateData();
		} else { //Collapsed
			//Save state
			try {
				separatorList.getReadWriteLock().writeLock().lock();
				for (int i = 0; i < separatorList.size(); i++) {
					Object object = separatorList.get(i);
					if (object instanceof SeparatorList.Separator<?>) {
						SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
						StockpileItem currentItem = (StockpileItem) separator.first();
						if (stockpiles.contains(currentItem.getStockpile())) {
							Settings.get().getStockpileGroupSettings().setStockpileExpanded(currentItem.getStockpile(), separator.getLimit() != 0);
							break; //Search Done
						}
					}
				}
			} finally {
				separatorList.getReadWriteLock().writeLock().unlock();
			}
			//Lock Table
			beforeUpdateData();
			try {
				//Remove (if group was expanded)
				eventList.getReadWriteLock().writeLock().lock();
				eventList.removeAll(stockpileItems);
			} finally {
				eventList.getReadWriteLock().writeLock().unlock();
			}
			//Unlcok Table
			afterUpdateData();
		}
		//Update Groups First
		for (Map.Entry<String, Stockpile> entry : oldFirst.entrySet()) {
			updateGroupFirst(entry.getKey(), entry.getValue(), Settings.get().getStockpileGroupSettings().getGroupFirst(entry.getKey()));
		}
	}

	private void updateGroupFirst(String group, Stockpile oldFirst, Stockpile newFirst) {
		if (newFirst == null) {
			//Lock Table
			beforeUpdateData();
			try {
				//Remove
				eventList.getReadWriteLock().writeLock().lock();
				eventList.remove(oldFirst.getIgnoreItem());
			} finally {
				eventList.getReadWriteLock().writeLock().unlock();
			}
			//Unlcok Table
			afterUpdateData();
		} else if (oldFirst == null) {
			//Lock Table
			beforeUpdateData();
			try {
				//Update
				eventList.getReadWriteLock().writeLock().lock();
				eventList.add(newFirst.getIgnoreItem());
			} finally {
				eventList.getReadWriteLock().writeLock().unlock();
			}
		} else if (!oldFirst.equals(newFirst)) {
			//Lock Table
			beforeUpdateData();
			try {
				//Update
				eventList.getReadWriteLock().writeLock().lock();
				eventList.remove(oldFirst.getIgnoreItem());
				eventList.add(newFirst.getIgnoreItem());
			} finally {
				eventList.getReadWriteLock().writeLock().unlock();
			}
			//Unlcok Table
			afterUpdateData();
			//Collapse
			for (int i = 0; i < separatorList.size(); i++) {
				Object object = separatorList.get(i);
				if (object instanceof SeparatorList.Separator<?>) {
					SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) object;
					StockpileItem currentItem = (StockpileItem) separator.first();
					if (group.equals(currentItem.getGroup())) {
						separator.setLimit(0);
					}
				}
			}
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
		if (value != JFileChooser.APPROVE_OPTION) {
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

	private void importText(TextImportType type) {
		textImport.importText(type, new TextImportHandler() {
			@Override
			public void addItems(Map<Integer, Double> data) {
				importStockpileItems(data, type.getName());
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
					Settings.lock("Stockpile (Import)"); //Lock for Stockpile (Import)
					for (StockpileItem item : items) {
						stockpile.add(item);
					}
					addStockpile(program, stockpile); //Add imported stockpile to Settings
					Settings.unlock("Stockpile (Import)"); //Unlock for Stockpile (Import)
					program.saveSettings("Stockpile (Import)"); //Save Stockpile (Import)
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
					Settings.lock("Stockpile (Import)"); //Lock for Stockpile (Import)
					for (StockpileItem item : items) {
						stockpile.add(item);
					}
					Settings.unlock("Stockpile (Import)"); //Unlock for Stockpile (Import)
					program.saveSettings("Stockpile (Import)"); //Save Stockpile (Import)
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
					program.saveSettings("Stockpile (Import)"); //Save Stockpile (Merge);
				}
				//Skip - Do nothing
				return true;
			}
		});
	}

	private void importXml() {
		jFileChooser.setSelectedFile(new File(""));
		int value = jFileChooser.showOpenDialog(program.getMainWindow().getFrame());
		if (value == JFileChooser.APPROVE_OPTION) {
			List<Stockpile> stockpiles = SettingsReader.loadStockpile(jFileChooser.getSelectedFile().getAbsolutePath());
			if (stockpiles != null) {
				importStockpiles(stockpiles);
			} else {
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), TabsStockpile.get().importXmlFailedMsg(), TabsStockpile.get().importFailedTitle(), JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private void importText() {
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
		Collections.sort(Settings.get().getStockpiles());
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
						Settings.lock("Stockpile (Import new)");
						addStockpile(program, value); //Add
						Settings.unlock("Stockpile (Import new)");
						//Update UI
						addStockpile(value);
					} else if (xmlOptions == ImportOptions.RENAME || xmlOptions == ImportOptions.NEW) {
						Stockpile returnRename = stockpileDialog.showRename(value); //Rename stockpile
						if (returnRename != null) { //OK
							addStockpile(returnRename); //Update UI
						} else { //Cancel
							return false;
						}
					} else if (xmlOptions == ImportOptions.MERGE) {
						int index = Settings.get().getStockpiles().indexOf(value); //Get index of old Stockpile
						Stockpile mergeStockpile = Settings.get().getStockpiles().get(index); //Get old stockpile
						addToStockpile(mergeStockpile, value.getItems(), true, true); //Merge old and imported stockpiles
					} else if (xmlOptions == ImportOptions.OVERWRITE) {
						Settings.lock("Stockpile (Import Options overwrite)"); //Lock settings
						//Remove
						int index = Settings.get().getStockpiles().indexOf(value); //Get index of old Stockpile
						Stockpile removeStockpile = Settings.get().getStockpiles().get(index); //Get old stockpile
						removeStockpile(removeStockpile); //Remove old stockpile from the UI
						Settings.get().getStockpiles().remove(removeStockpile); //Remove old stockpile from the Settings
						//Add
						addStockpile(program, value); //Add imported stockpile to Settings
						Settings.unlock("Stockpile (Import Options overwrite)"); //Unlock settings
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
						program.saveSettings("Stockpile (Import)"); //Save Stockpile (Merge);
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

	public static void addStockpile(Program program, Stockpile stockpile) {
		Settings.get().getStockpiles().add(stockpile);
	}

	private void exportXml() {
		List<Stockpile> stockpiles = stockpileSelectionDialog.show(getShownStockpiles(), Settings.get().getStockpiles(), TabsStockpile.get().showHidden(), false);
		if (stockpiles != null) {
			jFileChooser.setSelectedFile(new File(""));
			int value = jFileChooser.showSaveDialog(program.getMainWindow().getFrame());
			if (value == JFileChooser.APPROVE_OPTION) {
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
			} else if (StockpileAction.COLLAPSE.name().equals(e.getActionCommand())) { //Collapse all
				jTable.expandSeparators(false);
				Settings.get().getStockpileGroupSettings().setStockpilesExpanded(getShownStockpiles(), false);
			} else if (StockpileAction.EXPAND.name().equals(e.getActionCommand())) { //Expand all
				jTable.expandSeparators(true);
				Settings.get().getStockpileGroupSettings().setStockpilesExpanded(getShownStockpiles(), true);
			} else if (StockpileAction.IMPORT_EFT.name().equals(e.getActionCommand())) { //Add stockpile (EFT Import)
				importText(TextImportType.EFT);
			} else if (StockpileAction.IMPORT_ISK_PER_HOUR.name().equals(e.getActionCommand())) { //Add stockpile (Isk Per Hour)
				importText(TextImportType.ISK_PER_HOUR);
			} else if (StockpileAction.IMPORT_MULTIBUY.name().equals(e.getActionCommand())) { //Add stockpile (Eve Multibuy)
				importText(TextImportType.EVE_MULTIBUY);
			} else if (StockpileAction.IMPORT_SHOPPING_LIST.name().equals(e.getActionCommand())) { //Add stockpile (Shopping List)
				importText(TextImportType.STCOKPILE_SHOPPING_LIST);
			} else if (StockpileAction.IMPORT_XML.name().equals(e.getActionCommand())) { //Add stockpile (Xml)
				importXml();
			} else if (StockpileAction.IMPORT_EVE_XML_FIT.name().equals(e.getActionCommand())) { //Add stockpile (Xml)
				importEveXml();
			} else if (StockpileAction.IMPORT_TEXT.name().equals(e.getActionCommand())) { //Add stockpile (Xml)
				importText();
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
				removeFromGroup(stockpiles, false);
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
				if (!removed.isEmpty()) {
					removeFromGroup(removed, added.isEmpty());
				}
				if (!added.isEmpty()) {
					addToGroup(group, added);
				}
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
				removeFromGroup(stockpiles, false);
				addToGroup(newGroup, stockpiles);
				//Update Table Cell
				StockpileSeparatorTableCell.updateGroups(this);
				//Save Settings
				program.saveSettings("Stockpile (Stockpile Rename Group)");
				//Restore expanded
				expandGroups(expanded, new MatchGroup(newGroup));
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
				if (!oldGroup.isEmpty()) {
					removeFromGroup(stockpile, false); //Change group
				}
				addToGroup(group, stockpile); //Change or add group
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
					removeFromGroup(stockpile, true); //Remove from group
				}
				if (!oldGroup.isEmpty()) {
					removeFromGroup(stockpile, false); //Change group
				}
				if (!newGroup.equals(oldGroup)) {
					addToGroup(newGroup, stockpile); //Change or add group
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
		}

		@Override
		protected void beforeFilter() {
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
