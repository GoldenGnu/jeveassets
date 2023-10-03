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
package net.nikr.eve.jeveasset;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.nikr.eve.jeveasset.CliExport.ExportTool;
import net.nikr.eve.jeveasset.data.settings.ExportSettings;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.ColumnSelection;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.DecimalSeparator;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.ExportFormat;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.FilterSelection;
import net.nikr.eve.jeveasset.data.settings.ExportSettings.LineDelimiter;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.PicocliException;


@Command(sortOptions = false,
		synopsisHeading = "",
		customSynopsis = "Usage: java [-Djava.awt.headless=true] -jar jeveassets.jar [-h] [-v] [-p] [-z] [-u] [-e [OPTIONS]]",
		description="%n  -Djava.awt.headless=true   Run without a GUI%n"
				+ "                             Java parameter (must be specified before -jar)"
		)
public class CliOptions {

	private static final Logger LOG = Logger.getLogger(CliOptions.class.getName());

	public static final String ASSETS = "assets";
	public static final String CONTRACTS = "contracts";
	public static final String INDUSTRY_JOBS = "industryjobs";
	public static final String SLOTS = "slots";
	public static final String ISK = "isk";
	public static final String ITEMS = "items";
	public static final String JOURNAL = "journal";
	public static final String LOADOUTS = "fittings";
	public static final String MATERIALS = "materials";
	public static final String MARKET_ORDERS = "marketorders";
	public static final String MINING = "mining";
	public static final String OVERVIEW = "overview";
	public static final String REPROCESSED = "reprocessed";
	public static final String ROUTING = "routing"; //ToDo
	public static final String SKILLS = "skills";
	public static final String STOCKPILE = "stockpile";
	public static final String TRACKER = "tracker"; //ToDo
	public static final String TRANSACTIONS = "transactions";
	public static final String TREE_LOCATION = "tree-location";
	public static final String TREE_CATEGORY = "tree-category";
	private static final String REPROCESSED_NAMES = "-"+REPROCESSED+"-names";
	private static final String REPROCESSED_IDS = "-"+REPROCESSED+"-ids";
	private static final String END_GROUP = "%n";
	private static final String TOOLS_BEFORE = "Export ";
	private static final String TOOLS_AFTER = " Data";
	private static final String FILTER_BEFORE = "Use saved filter for ";
	private static final String FILTER_AFTER = " export";
	private static final String FILTER_CMD = "-filter";
	private static final String FILTER_LABEL = "filter";
	private static final String VIEW_BEFORE = "Use saved view for ";
	private static final String VIEW_AFTER = " export";
	private static final String VIEW_CMD = "-view";
	private static final String VIEW_LABEL = "view";

	private static final CliOptions CLI_OPTIONS = new CliOptions();

	public static CliOptions get() {
		return CLI_OPTIONS;
	}

	public static CommandLine set(final String[] args) {
		CommandLine cmd = new CommandLine(CLI_OPTIONS);
		try {
			cmd.parseArgs(args);
		} catch (PicocliException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
			System.exit(-1);
		}
		if (CLI_OPTIONS.help) {
			cmd.setUsageHelpWidth(110);
			cmd.setUsageHelpAutoWidth(true);
			cmd.usage(System.out);
			System.exit(0);
		}
		if (CLI_OPTIONS.version) {
			System.out.println(Program.PROGRAM_NAME + " " + Program.PROGRAM_VERSION);
			System.exit(0);
		}
		return cmd;
	}

	@Option(names = {"-h", "-help", "/?"}, usageHelp = true, description = "Display this help message")
	boolean help;
	@Option(names = {"-v", "-version"}, versionHelp = true, description = "Display version information")
	boolean version;
	@Option(names = {"-p", "-portable"}, description = "Run jEveAssets portable%nSave all data in the jEveAssets program directory ")
	boolean portable;
	@Option(names = {"-z", "-lazysave"}, description = "Only save to disk on update and exit%n"
			+ "    Warning:%n"
			+ "    This may cause you to lose data if jEveAssets exit unexpectedly" + END_GROUP)
	boolean lazySave;

	@ArgGroup(exclusive = false, heading = "Update Options:%n")
	UpdateOptions updateOptions;

	static class UpdateOptions {
		@Option(names = { "-u", "-update"}, required = true, description = "Update Data%nUpdate all profiles and accounts%nAll data with cache expired will be updated" + END_GROUP)
		boolean update;
	}

	@ArgGroup(exclusive = false, heading = "Export Options:%n")
	ExportOptions exportOptions;

	//-u -e -html -csv -sql
	//-u -e -html -csv -sql -nodate
	//-u -e -html -csv -sql -nodate -assets-filter "Propulsion Modules" -reprocessed-ids 12066 -reprocessed-names "100MN Afterburner II"
	//-e -a -html -nodate -assets-filter "Propulsion Modules" -assets-view "Simple"
	//-e -a -html -nodate -assets-filter -assets-view
	public static class ExportOptions {
		@Option(names = { "-e", "-export"}, required = true, description = "Export Data%n")
		boolean export;
		@Option(names = { "-f", "-output" }, paramLabel = "directory", description = "Output directory")
		File output;
		@Option(names = "-nodate", description = "Do not include date and time in the filenames")
		boolean noDate;
		@Option(names = "-noformula", description = "Do not include formula columns")
		boolean noFormulas;
		@Option(names = "-nojumps", description = "Do not include jump columns"
				+ END_GROUP + "%nFormat Help:%nYou can select as many formats as you want")
		boolean noJumps;
		@ArgGroup(exclusive = false)
		ExportOptionsFormat exportOptionsFormat;
		@ArgGroup(exclusive = false)
		ExportOptionsTools tools;
		@ArgGroup(exclusive = false)
		ExportOptionsFilters filters = new ExportOptionsFilters();
		@ArgGroup(exclusive = false)
		ExportOptionsView views = new ExportOptionsView();
	}

	static class ExportOptionsFormat {
		@ArgGroup(exclusive = false)
		ExportOptionsCsv csv;
		@ArgGroup(exclusive = false)
		ExportOptionsHtml html;
		@ArgGroup(exclusive = false)
		ExportOptionsSql sql;
	}

	static class ExportOptionsCsv {
		@Option(names = "-csv", required = true, description = "Export to CSV (default)")
		boolean csv;
		@Option(names = "-comma", description = "    Use comma as decimal separator and semicolon as field delimiter%n"
											+ "    Default is dot as decimal separator and comma as field delimiter")
		boolean comma;
		@ArgGroup(exclusive = true)
		ExportOptionsLine line;
	}

	static class ExportOptionsLine {
		@Option(names = "-dos", description = "    Use DOS (\\r\\n) line terminator (default)")
		boolean dos;
		@Option(names = "-unix", description = "    Use UNIX (\\n) line terminator")
		boolean unix;
		@Option(names = "-mac", description = "    Use MAC (\\r) line terminator"
				+ END_GROUP)
		boolean mac;
	}

	static class ExportOptionsHtml {
		@Option(names = "-html", required = true, description = "Export to HTML")
		boolean html;
		@Option(names = "-nostyle", description = "    Do not style the HTML" )
		boolean noStyle;
		@Option(names = "-igb", description = "    Add in-game browser links")
		boolean igb;
		@Option(names = "-header", arity = "1", paramLabel = "row", description = "    Header every X row"
				+ END_GROUP)
		int headers = 0;
	}

	static class ExportOptionsSql {
		@Option(names = "-sql", required = true, description = "Export to SQL")
		boolean sql;
		@Option(names = "-droptable", description = "    Drop Table (if exist)")
		boolean dropTable;
		@Option(names = "-createtable", description = "    Create Table (if not exist)")
		boolean createTable;
		@Option(names = "-extended", description = "    Extended Inserts"
				+ END_GROUP +"%nTools Help:%nOmit all the tool parameters to export all tools")
		boolean extendedInserts;
	}

	static class ExportOptionsTools {
		/**
		 * Free letters: b w
		 */
		@Option(names = {"-a" ,"-"+ASSETS}, description = TOOLS_BEFORE+"Assets"+TOOLS_AFTER)
		boolean assets;
		@Option(names = {"-c" ,"-"+CONTRACTS}, description = TOOLS_BEFORE+"Contracts"+TOOLS_AFTER)
		boolean contracts;
		@Option(names = {"-i" ,"-"+INDUSTRY_JOBS}, description = TOOLS_BEFORE+"Industry Jobs"+TOOLS_AFTER)
		boolean industryJobs;
		@Option(names = {"-n" ,"-"+SLOTS}, description = TOOLS_BEFORE+"Slots"+TOOLS_AFTER)
		boolean slots;
		@Option(names = {"-k" ,"-"+ISK}, description = TOOLS_BEFORE+"Isk"+TOOLS_AFTER)
		boolean isk;
		@Option(names = {"-x" ,"-"+ITEMS}, description = TOOLS_BEFORE+"Items"+TOOLS_AFTER)
		boolean items;
		@Option(names = {"-j" ,"-"+JOURNAL}, description = TOOLS_BEFORE+"Journal"+TOOLS_AFTER)
		boolean journal;
		@Option(names = {"-y" ,"-"+LOADOUTS}, description = TOOLS_BEFORE+"Ship Fittings"+TOOLS_AFTER)
		boolean loadouts;
		@Option(names = {"-m" ,"-"+MATERIALS}, description = TOOLS_BEFORE+"Materials"+TOOLS_AFTER)
		boolean materials;
		@Option(names = {"-o" ,"-"+MARKET_ORDERS}, description = TOOLS_BEFORE+"Market Orders"+TOOLS_AFTER)
		boolean marketOrders;
		@Option(names = {"-d" ,"-"+MINING}, description = TOOLS_BEFORE+"Mining"+TOOLS_AFTER)
		boolean mining;
		@Option(names = {"-q" ,"-"+SKILLS}, description = TOOLS_BEFORE+"Skills"+TOOLS_AFTER)
		boolean skills;
		@Option(names = {"-s" ,"-"+STOCKPILE}, description = TOOLS_BEFORE+"Stockpile"+TOOLS_AFTER)
		boolean stockpile;
		@Option(names = {"-t" ,"-"+TRANSACTIONS}, description = TOOLS_BEFORE+"Transaction"+TOOLS_AFTER)
		boolean transaction;
		@Option(names = {"-g" ,"-"+TREE_CATEGORY}, description = TOOLS_BEFORE+"Tree Category"+TOOLS_AFTER)
		boolean treeCategory;
		@Option(names = {"-l" ,"-"+TREE_LOCATION}, description = TOOLS_BEFORE+"Tree Location"+TOOLS_AFTER)
		boolean treeLocation;
		@Option(names = {"-vs", "-"+OVERVIEW+"-stations"}, description = TOOLS_BEFORE+"Overview Stations"+TOOLS_AFTER)
		boolean overviewStations;
		@Option(names = {"-vp", "-"+OVERVIEW+"-planets"}, description = TOOLS_BEFORE+"Overview Planets"+TOOLS_AFTER)
		boolean overviewPlanets;
		@Option(names = {"-vy", "-"+OVERVIEW+"-systems"}, description = TOOLS_BEFORE+"Overview Systems"+TOOLS_AFTER)
		boolean overviewSystems;
		@Option(names = {"-vc", "-"+OVERVIEW+"-constellations"}, description = TOOLS_BEFORE+"Overview Constellations"+TOOLS_AFTER)
		boolean overviewConstellations;
		@Option(names = {"-vr", "-"+OVERVIEW+"-regions"}, description = TOOLS_BEFORE+"Overview Regions"+TOOLS_AFTER)
		boolean overviewRegions;
		@Option(names = {"-vg", "-"+OVERVIEW+"-groups"}, description = TOOLS_BEFORE+"Overview Groups"+TOOLS_AFTER)
		boolean overviewGroups;
		@Option(names = {"-rn",REPROCESSED_NAMES}, arity = "1..", split = ",", paramLabel = "typeName", description = TOOLS_BEFORE+"Reprocessed"+TOOLS_AFTER+" for typeName(s)")
		Set<String> reprocessedNames;
		@Option(names = {"-ri",REPROCESSED_IDS}, arity = "1..", split = ",", paramLabel = "typeID", description = TOOLS_BEFORE+"Reprocessed"+TOOLS_AFTER+" for typeID(s)"
				+ END_GROUP + END_GROUP + "%nFilters Help:%nOmit the parameter value to use the current filter%nOmit the parameter to use no filter")
		Set<Integer> reprocessedIDs;
	}

	static class ExportOptionsFilters {
		@Option(names = "-"+ASSETS+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Assets"+FILTER_AFTER)
		String assetsFilter;
		@Option(names = "-"+CONTRACTS+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Contracts"+FILTER_AFTER)
		String contractsFilter;
		@Option(names = "-"+INDUSTRY_JOBS+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Industry Jobs"+FILTER_AFTER)
		String industryJobsFilter;
		@Option(names = "-"+SLOTS+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Slots"+FILTER_AFTER)
		String slotsFilter;
		@Option(names = "-"+ISK+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Isk"+FILTER_AFTER)
		String iskFilter;
		@Option(names = "-"+ITEMS+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Items"+FILTER_AFTER)
		String itemsFilter;
		@Option(names = "-"+JOURNAL+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Journal"+FILTER_AFTER)
		String journalFilter;
		@Option(names = "-"+LOADOUTS+"-names", arity = "1..", split = ",", paramLabel = "typeName|itemName", description = "Export Ship Fittings matching ships typeName or itemName")
		Set<String> loadoutsNames;
		@Option(names = "-"+LOADOUTS+"-ids", arity = "1..", split = ",", paramLabel = "typeID", description = "Export Ship Fittings matching ships typeID")
		Set<Integer> loadoutsIDs;
		@Option(names = "-"+MATERIALS+"-owner", fallbackValue = "", arity = "0..1", paramLabel = "owner", description = "Materials owner name")
		String materialsOwner;
		@Option(names = "-"+MATERIALS+"-pi", fallbackValue = "", description = "Materials include PI")
		boolean materialsPI;
		@Option(names = "-"+MATERIALS+"-ore", fallbackValue = "", description = "Materials include Ore")
		boolean materialsOre;
		@Option(names = "-"+MARKET_ORDERS+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Market Orders"+FILTER_AFTER)
		String marketOrdersFilter;
		@Option(names = "-"+MINING+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Mining"+FILTER_AFTER)
		String miningFilter;
		//Reprocesseddoes not support filters
		@Option(names = "-"+OVERVIEW+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Asset filter to use with Overview")
		String overviewFilter;
		@Option(names = "-"+SKILLS+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Skills"+FILTER_AFTER)
		String skillsFilter;
		@Option(names = "-"+STOCKPILE+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Stockpile"+FILTER_AFTER)
		String stockpileFilter;
		@Option(names = "-"+TRANSACTIONS+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Transaction"+FILTER_AFTER)
		String transactionFilter;
		@Option(names = "-"+TREE_CATEGORY+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Tree Category"+FILTER_AFTER)
		String treeFilterCategory;
		@Option(names = "-"+TREE_LOCATION+FILTER_CMD, fallbackValue = "", arity = "0..1", paramLabel = FILTER_LABEL, description = FILTER_BEFORE+"Tree Location"+FILTER_AFTER
				+ END_GROUP + END_GROUP + "%nViews Help:%nOmit the parameter value to use the current columns%nOmit the parameter to include all columns")
		String treeFilterLocation;
	}

	static class ExportOptionsView {
		@Option(names = "-"+ASSETS+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Assets"+VIEW_AFTER)
		String assetsView;
		@Option(names = "-"+CONTRACTS+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Contracts"+VIEW_AFTER)
		String contractsView;
		@Option(names = "-"+INDUSTRY_JOBS+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Industry Jobs"+VIEW_AFTER)
		String industryJobsView;
		@Option(names = "-"+SLOTS+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Slots"+VIEW_AFTER)
		String slotsView;
		@Option(names = "-"+ISK+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Isk"+VIEW_AFTER)
		String iskView;
		@Option(names = "-"+ITEMS+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Items"+VIEW_AFTER)
		String itemsView;
		@Option(names = "-"+JOURNAL+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Journal"+VIEW_AFTER)
		String journalView;
		//Loadouts does not support views
		@Option(names = "-"+MATERIALS+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Materials"+VIEW_AFTER)
		String materialsView;
		@Option(names = "-"+MARKET_ORDERS+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Market Orders"+VIEW_AFTER)
		String marketOrdersView;
		@Option(names = "-"+MINING+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Mining"+VIEW_AFTER)
		String miningView;
		@Option(names = "-"+REPROCESSED+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Reprocessed"+VIEW_AFTER)
		String reprocessedView;
		//Overview does not support views
		@Option(names = "-"+SKILLS+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Skills"+VIEW_AFTER)
		String skillsView;
		@Option(names = "-"+STOCKPILE+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Stockpile"+VIEW_AFTER)
		String stockpileView;
		@Option(names = "-"+TRANSACTIONS+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Transaction"+VIEW_AFTER)
		String transactionView;
		@Option(names = "-"+TREE_CATEGORY+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Tree Category"+VIEW_AFTER)
		String treeViewCategory;
		@Option(names = "-"+TREE_LOCATION+VIEW_CMD, fallbackValue = "", arity = "0..1", paramLabel = VIEW_LABEL, description = VIEW_BEFORE+"Tree Location"+VIEW_AFTER
				+ END_GROUP + END_GROUP)
		String treeViewLocation;
	}

	@ArgGroup(exclusive = false, heading = "Development Options (use at your own risk):%n")
	DevOptions devOptions;

	static class DevOptions {
		@Option(names = { "-debug" }, description = "Dev Command: Enable debug logging and other debug shenanigans")
		boolean debug;
		@Option(names = { "-edtdebug" }, description = "Dev Command: Detect painting outside EDT")
		boolean edtdebug;
		@Option(names = { "-noupdate" }, description = "Dev Command: Disable all updates")
		boolean forceNoUpdate;
		@Option(names = { "-forceupdate" }, description = "Dev Command: Ignore update times%n"
				+ "    Warning:%n"
				+ "    Using -forceupdate may get you banned from ESI%n"
				+ "    It does not give you access to any new data%n"
				+ "    You will just get a cached result and a lot of bad karma… ")
		boolean forceUpdate;
		@Option(names = { "-jmemory" }, description = "Dev Command: Notify jEveAssets It's being run with jmemory.jar")
		boolean jMemory;
	}

	private Map<ExportTool, List<ExportSettings>> settings = null;

	public Map<ExportTool, List<ExportSettings>> getExportSettings() {
		if (settings == null) {
			settings = new HashMap<>();
			if (exportOptions == null) {
				return settings;
			}
			ExportOptionsTools tools = exportOptions.tools;
			ExportOptionsFilters filters = exportOptions.filters;
			ExportOptionsView views = exportOptions.views;
			if (tools == null || tools.assets) createExportSettings(ExportTool.ASSETS, filters.assetsFilter, views.assetsView);
			if (tools == null || tools.contracts) createExportSettings(ExportTool.CONTRACTS, filters.contractsFilter, views.contractsView);
			if (tools == null || tools.industryJobs) createExportSettings(ExportTool.INDUSTRY_JOBS, filters.industryJobsFilter, views.industryJobsView);
			if (tools == null || tools.slots) createExportSettings(ExportTool.SLOTS, filters.slotsFilter, views.slotsView);
			if (tools == null || tools.isk) createExportSettings(ExportTool.ISK, filters.iskFilter, views.iskView);
			if (tools == null || tools.items) createExportSettings(ExportTool.ITEMS, filters.itemsFilter, views.itemsView);
			if (tools == null || tools.journal) createExportSettings(ExportTool.JOURNAL, filters.journalFilter, views.journalView);
			if (tools == null || tools.loadouts) createExportSettings(ExportTool.LOADOUTS, null, null);
			if (tools == null || tools.materials) createExportSettings(ExportTool.MATERIALS, filters.materialsOwner, views.materialsView);
			if (tools == null || tools.marketOrders) createExportSettings(ExportTool.MARKET_ORDERS, filters.marketOrdersFilter, views.marketOrdersView);
			if (tools == null || tools.mining) createExportSettings(ExportTool.MINING, filters.miningFilter, views.miningView);
			if (tools != null && (tools.reprocessedIDs != null || tools.reprocessedNames != null)) createExportSettings(ExportTool.REPROCESSED, null, views.reprocessedView);
			if (tools == null || tools.overviewStations) createExportSettings(ExportTool.OVERVIEW_STATIONS, filters.overviewFilter, null);
			if (tools == null || tools.overviewPlanets) createExportSettings(ExportTool.OVERVIEW_PLANETS, filters.overviewFilter, null);
			if (tools == null || tools.overviewSystems) createExportSettings(ExportTool.OVERVIEW_SYSTEMS, filters.overviewFilter, null);
			if (tools == null || tools.overviewConstellations) createExportSettings(ExportTool.OVERVIEW_CONSTELLATIONS, filters.overviewFilter, null);
			if (tools == null || tools.overviewRegions) createExportSettings(ExportTool.OVERVIEW_REGIONS, filters.overviewFilter, null);
			if (tools == null || tools.overviewGroups) createExportSettings(ExportTool.OVERVIEW_GROUPS, filters.overviewFilter, null);
			if (tools == null || tools.skills) createExportSettings(ExportTool.SKILLS, filters.skillsFilter, views.skillsView);
			if (tools == null || tools.stockpile) createExportSettings(ExportTool.STOCKPILE, filters.stockpileFilter, views.stockpileView);
			if (tools == null || tools.transaction) createExportSettings(ExportTool.TRANSACTIONS, filters.transactionFilter, views.transactionView);
			if (tools == null || tools.treeLocation) createExportSettings(ExportTool.TREE_LOCATION, filters.treeFilterLocation, views.treeViewLocation);
			if (tools == null || tools.treeCategory) createExportSettings(ExportTool.TREE_CATEGORY, filters.treeFilterCategory, views.treeViewCategory);
		}
		return settings;
	}

	private void createExportSettings(ExportTool exportTool, String filterName, String viewName) {
		ExportOptionsFormat exportOptionsFormat = exportOptions.exportOptionsFormat;
		if (exportOptionsFormat != null) {
			//CSV
			if (exportOptionsFormat.csv != null && exportOptionsFormat.csv.csv) {
				ExportSettings exportSettings = new ExportSettings(exportTool.getToolName());
				if (exportOptionsFormat.csv.comma) {
					exportSettings.setDecimalSeparator(DecimalSeparator.COMMA);
				}
				if (exportOptionsFormat.csv.line != null) {
					if (exportOptionsFormat.csv.line.unix) {
						exportSettings.setCsvLineDelimiter(LineDelimiter.UNIX);
					} else if (exportOptionsFormat.csv.line.mac) {
						exportSettings.setCsvLineDelimiter(LineDelimiter.MAC);
					}
				}
				set(exportSettings, exportTool, ExportFormat.CSV, filterName, viewName);
			}
			//HTML
			if (exportOptionsFormat.html != null && exportOptionsFormat.html.html) {
				ExportSettings exportSettings = new ExportSettings(exportTool.getToolName());
				exportSettings.setHtmlRepeatHeader(exportOptionsFormat.html.headers);
				exportSettings.setHtmlIGB(exportOptionsFormat.html.igb);
				exportSettings.setHtmlStyled(!exportOptionsFormat.html.noStyle);
				set(exportSettings, exportTool, ExportFormat.HTML, filterName, viewName);
			}
			//SQL
			if (exportOptionsFormat.sql != null && exportOptionsFormat.sql.sql) {
				ExportSettings exportSettings = new ExportSettings(exportTool.getToolName());
				exportSettings.setSqlDropTable(exportOptionsFormat.sql.dropTable);
				exportSettings.setSqlCreateTable(exportOptionsFormat.sql.createTable);
				exportSettings.setSqlExtendedInserts(exportOptionsFormat.sql.extendedInserts);
				exportSettings.setSqlTableName(Settings.get().getExportSettings(exportTool.getToolName()).getSqlTableName());
				set(exportSettings, exportTool, ExportFormat.SQL, filterName, viewName);
			}
		} else { //Default CSV
			set(new ExportSettings(exportTool.getToolName()), exportTool, ExportFormat.CSV, filterName, viewName);
		}
	}

	private void set(ExportSettings exportSettings, ExportTool exportTool, ExportFormat exportFormat, String filterName, String viewName) {
		StringBuilder builder = new StringBuilder();
		builder.append(getOutputDirectory());
		builder.append(File.separator);
		if (!exportOptions.noDate) {
			builder.append(Formatter.fileDate(new Date()));
			builder.append("_");
		}
		builder.append(exportTool.getFilename());
		builder.append("_export.");
		builder.append(exportFormat.getExtension());
		exportSettings.setFilename(builder.toString());
		//Format
		exportSettings.setExportFormat(exportFormat);
		//Filter
		if (filterName == null) {
			exportSettings.setFilterName("");
			exportSettings.setFilterSelection(FilterSelection.NONE);
		} else if (filterName.isEmpty()) {
			exportSettings.setFilterName("");
			exportSettings.setFilterSelection(FilterSelection.CURRENT);
		} else {
			exportSettings.setFilterName(filterName);
			exportSettings.setFilterSelection(FilterSelection.SAVED);
		}
		//Formula Columns
		exportSettings.setFormulas(!exportOptions.noFormulas);
		//Jump Columns
		exportSettings.setJumps(!exportOptions.noJumps);
		//Columns
		if (viewName == null) { //All columns
			exportSettings.setColumnSelection(ColumnSelection.SELECTED);
			exportSettings.putTableExportColumns(null); //null = all
			exportSettings.setViewName(null);
		} else if (viewName.isEmpty()) { //Current shown columns in order
			exportSettings.setColumnSelection(ColumnSelection.SHOWN);
			exportSettings.setViewName(null);
		} else { //Saved View
			exportSettings.setColumnSelection(ColumnSelection.SAVED);
			exportSettings.setViewName(viewName);
		}
		//Add
		List<ExportSettings> list = settings.get(exportTool);
		if (list == null) {
			list = new ArrayList<>();
			settings.put(exportTool, list);
		}
		list.add(exportSettings);
	}

	public String getMaterialsOwner() {
		if (exportOptions == null || exportOptions.filters == null
				|| exportOptions.filters.materialsOwner == null || exportOptions.filters.materialsOwner.isEmpty()) {
			return null;
		}
		return exportOptions.filters.materialsOwner;
	}

	public boolean isMaterialsPI() {
		if (exportOptions == null || exportOptions.filters == null) {
			return false;
		}
		return exportOptions.filters.materialsPI;
	}

	public boolean isMaterialsOre() {
		if (exportOptions == null || exportOptions.filters == null) {
			return false;
		}
		return exportOptions.filters.materialsOre;
	}

	public Set<String> getReprocessedNames() {
		if (exportOptions == null
				|| exportOptions.tools == null
				|| exportOptions.tools.reprocessedNames == null
				|| exportOptions.tools.reprocessedNames.isEmpty()) {
			return null;
		}
		return exportOptions.tools.reprocessedNames;
	}

	public Set<Integer> getReprocessedIDs() {
		if (exportOptions == null
				|| exportOptions.tools == null
				|| exportOptions.tools.reprocessedIDs == null
				|| exportOptions.tools.reprocessedIDs.isEmpty()) {
			return null;
		}
		return exportOptions.tools.reprocessedIDs;
	}

	public Set<String> getLoadoutsNames() {
		if (exportOptions == null
				|| exportOptions.filters == null
				|| exportOptions.filters.loadoutsNames == null
				|| exportOptions.filters.loadoutsNames.isEmpty()) {
			return null;
		}
		return exportOptions.filters.loadoutsNames;
	}

	public Set<Integer> getLoadoutsIDs() {
		if (exportOptions == null
				|| exportOptions.filters == null
				|| exportOptions.filters.loadoutsIDs == null
				|| exportOptions.filters.loadoutsIDs.isEmpty()) {
			return null;
		}
		return exportOptions.filters.loadoutsIDs;
	}

	public File getOutputDirectory() {
		//Output
		if (exportOptions.output == null) {
			exportOptions.output = new File(FileUtil.getPathExports());
		}
		return exportOptions.output;
	}

	public boolean isPortable() {
		return portable;
	}

	public boolean isLazySave() {
		return lazySave;
	}

	public boolean isDebug() {
		if (devOptions == null) {
			return false;
		}
		return devOptions.debug;
	}

	public boolean isEDTdebug() {
		if (devOptions == null) {
			return false;
		}
		return devOptions.edtdebug;
	}

	public boolean isForceNoUpdate() {
		if (devOptions == null) {
			return false;
		}
		return devOptions.debug && devOptions.forceNoUpdate;
	}

	public boolean isForceUpdate() {
		if (devOptions == null) {
			return false;
		}
		return devOptions.debug && devOptions.forceUpdate;
	}

	public boolean isUpdate() {
		if (updateOptions == null) {
			return false;
		}
		return updateOptions.update;
	}

	public boolean isCLI() {
		return isUpdate() || isExport();
	}

	public boolean isExport() {
		if (exportOptions == null) {
			return false;
		}
		return exportOptions.export;
	}

	public boolean isJmemory() {
		if (devOptions == null) {
			return false;
		}
		return devOptions.jMemory;
	}

	public void setPortable(boolean portable) {
		this.portable = portable;
	}
}