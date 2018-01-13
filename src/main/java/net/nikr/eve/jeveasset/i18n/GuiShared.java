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

package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;

public abstract class GuiShared extends Bundle {

	public static GuiShared get() {
		return BundleServiceFactory.getBundleService().get(GuiShared.class);
	}

	public GuiShared(final Locale locale) {
		super(locale);
	}

	public abstract String add();
	public abstract String addFilter();
	public abstract String addStockpileItem();
	public abstract String background();
	public abstract String checkAll();
	public abstract String chruker();
	public abstract String containerDelete();
	public abstract String containerEdit();
	public abstract String containerText();
	public abstract String copy();
	public abstract String custom();
	public abstract String cut();
	public abstract String delete();
	public abstract String dotlan();
	public abstract String edit();
	public abstract String emptyString();
	public abstract String errorLoadingSettingsMsg();
	public abstract String errorLoadingSettingsTitle();
	public abstract String eveAddicts();
	public abstract String eveCentral();
	public abstract String eveMarketdata();
	public abstract String eveMarketeer();
	public abstract String eveMarketer();
	public abstract String eveMarkets();
	public abstract String eveOnline();
	public abstract String foreground();
	public abstract String fuzzwork();
	public abstract String item();
	public abstract String itemDelete();
	public abstract String itemEdit();
	public abstract String itemNameTitle();
	public abstract String itemPriceTitle();
	public abstract String updateStructures();
	public abstract String jumps();
	public abstract String jumpsAddCustom();
	public abstract String jumpsAddSelected();
	public abstract String jumpsClear();
	public abstract String location();
	public abstract String locationClear();
	public abstract String locationClearConfirm(String location);
	public abstract String locationClearConfirmAll(int size);
	public abstract String locationEmpty();
	public abstract String locationID();
	public abstract String locationName();
	public abstract String locationRename();
	public abstract String locationSystem();
	public abstract String lookup();
	public abstract String newStockpile();
	public abstract String ok();
	public abstract String openLinks(int size);
	public abstract String openLinksTitle();
	public abstract String overwrite();
	public abstract String overwriteFile();
	public abstract String paste();
	public abstract String region();
	public abstract String selectionAverage();
	public abstract String selectionCount();
	public abstract String selectionInventionSuccess();
	public abstract String selectionManufactureJobsValue();
	public abstract String selectionOrdersBoth();
	public abstract String selectionOrdersBothAvg();
	public abstract String selectionOrdersBothCount();
	public abstract String selectionOrdersBuy();
	public abstract String selectionOrdersBuyAvg();
	public abstract String selectionOrdersBuyCount();
	public abstract String selectionOrdersEscrow();
	public abstract String selectionOrdersSell();
	public abstract String selectionOrdersSellAvg();
	public abstract String selectionOrdersSellCount();
	public abstract String selectionOrdersToCover();
	public abstract String selectionTitle();
	public abstract String selectionTitleBoth();
	public abstract String selectionTitleBuy();
	public abstract String selectionTitleNeeded();
	public abstract String selectionTitleNow();
	public abstract String selectionTitleSell();
	public abstract String selectionValue();
	public abstract String selectionValueNeeded();
	public abstract String selectionValueNow();
	public abstract String selectionValueReprocessed();
	public abstract String selectionVolume();
	public abstract String selectionVolumeNeeded();
	public abstract String selectionVolumeNow();
	public abstract String station();
	public abstract String stockpile();
	public abstract String system();
	public abstract String tableColumns();
	public abstract String tableColumnsReset();
	public abstract String tableColumnsTip();
	public abstract String tableColumnsTitle();
	public abstract String tableResizeText();
	public abstract String tableResizeWindow();
	public abstract String tableResizeNone();
	public abstract String tableSettings();
	public abstract String tags();
	public abstract String tagsEdit();
	public abstract String tagsEditTitle();
	public abstract String tagsName(String name, Integer count);
	public abstract String tagsNew();
	public abstract String tagsNewMsg();
	public abstract String tagsNewTitle();
	public abstract String ui();
	public abstract String uiWaypoint();
	public abstract String uiWaypointBeginning();
	public abstract String uiWaypointClear();
	public abstract String uiWaypointFail();
	public abstract String uiWaypointOk();
	public abstract String uiWaypointTitle();
	public abstract String uiCharacterInvalidMsg();
	public abstract String uiCharacterMsg();
	public abstract String uiCharacterTitle();
	public abstract String uiContract();
	public abstract String uiContractFail();
	public abstract String uiContractOk();
	public abstract String uiContractTitle();
	public abstract String uiLocationTitle();
	public abstract String uiMarket();
	public abstract String uiMarketFail();
	public abstract String uiMarketOk();
	public abstract String uiMarketTitle();
	public abstract String uiOwner();
	public abstract String uiOwnerFail();
	public abstract String uiOwnerMsg();
	public abstract String uiOwnerOk();
	public abstract String uiOwnerTitle();
	public abstract String uiStation();
	public abstract String uiSystem();
	public abstract String updating();

	public abstract String today(Object arg0);
	public abstract String whitespace37(Object arg0, Object arg1);
	public abstract String files(Object arg0);
	public abstract String deleteView();
	public abstract String deleteViews(int size);
	public abstract String editViews();
	public abstract String enterViewName();
	public abstract String loadView();
	public abstract String manageViews();
	public abstract String noViewName();
	public abstract String overwriteView();
	public abstract String renameView();
	public abstract String saveView();
	public abstract String saveViewMsg();
	
	//Update
	public abstract String beta();
	public abstract String devBuild();
	public abstract String feedbackMsg(String programName, String versionType);
	public abstract String newBuildMsg(String buildType, String programName);
	public abstract String newBuildTitle();
	public abstract String newVersionTitle();
	public abstract String newVersionMsg(String programName);
	public abstract String noNewVersionTitle();
	public abstract String noNewVersionMsg();
	public abstract String protableMsg(int i);
	public abstract String releaseCandidate();
	public abstract String stable();
	
	//Filters
	public abstract String saveFilter();
	public abstract String enterFilterName();
	public abstract String save();
	public abstract String cancel();
	public abstract String noFilterName();
	public abstract String overwriteDefaultFilter();
	public abstract String overwriteFilter();
	public abstract String addField();
	public abstract String clearField();
	public abstract String loadFilter();
	public abstract String showFilters();
	public abstract String manageFilters();
	public abstract String nothingToSave();
	public abstract String filterManager();
	public abstract String managerExport();
	public abstract String managerImport();
	public abstract String managerImportFailMsg();
	public abstract String managerImportFailTitle();
	public abstract String managerLoad();
	public abstract String managerRename();
	public abstract String managerDelete();
	public abstract String managerClose();
	public abstract String renameFilter();
	public abstract String deleteFilter();
	public abstract String deleteFilters(int size);
	public abstract String mergeFilters();
	public abstract String managerMerge();
	public abstract String filterAll();
	public abstract String filterAnd();
	public abstract String filterOr();
	public abstract String filterContains();
	public abstract String filterContainsNot();
	public abstract String filterEquals();
	public abstract String filterEqualsNot();
	public abstract String filterGreaterThan();
	public abstract String filterLastDays();
	public abstract String filterLastHours();
	public abstract String filterLessThan();
	public abstract String filterBefore();
	public abstract String filterAfter();
	public abstract String filterEqualsDate();
	public abstract String filterEqualsNotDate();
	public abstract String filterContainsColumn();
	public abstract String filterContainsNotColumn();
	public abstract String filterEqualsColumn();
	public abstract String filterEqualsNotColumn();
	public abstract String filterGreaterThanColumn();
	public abstract String filterLessThanColumn();
	public abstract String filterBeforeColumn();
	public abstract String filterAfterColumn();
	public abstract String filterUntitled();
	public abstract String filterEmpty();
	public abstract String filterShowing(int rowCount, int size, String filterName);
	public abstract String popupMenuAddField();
	public abstract String export();
	public abstract String exportTableData();
	
	//Text Dialog
	public abstract String textLoadFailMsg();
	public abstract String textLoadFailTitle();
	public abstract String textSaveFailMsg();
	public abstract String textSaveFailTitle();
	public abstract String textToClipboard();
	public abstract String textToFile();
	public abstract String textFromClipboard();
	public abstract String textFromFile();
	public abstract String textClose();
	public abstract String textImport();
	public abstract String textExport();
}
