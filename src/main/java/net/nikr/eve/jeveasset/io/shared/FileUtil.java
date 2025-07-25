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
package net.nikr.eve.jeveasset.io.shared;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import javax.swing.JOptionPane;
import net.nikr.eve.jeveasset.CliOptions;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileUtil extends FileUtilSimple {
	private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

	private static final String PATH_SOUNDS = "sounds";
	private static final String PATH_ASSET_ADDED = "data" + File.separator + "added.json";
	private static final String PATH_ASSET_ADDED_DATABASE = "data" + File.separator + "addedsql.db";
	private static final String PATH_STOCKPILE_IDS_DATABASE = "data" + File.separator + "stockpileids.db";
	private static final String PATH_PRICE_HISTORY_DATABASE = "data" + File.separator + "pricehistory.db";
	private static final String PATH_TRACKER_DATA = "data" + File.separator + "tracker.json";
	private static final String PATH_SETTINGS = "data" + File.separator + "settings.xml";
	private static final String PATH_ITEMS = "data" + File.separator + "items.xml";
	private static final String PATH_ITEMS_UPDATES = "data" + File.separator + "items_updates.xml";
	private static final String PATH_JUMPS = "data" + File.separator + "jumps.xml";
	private static final String PATH_LOCATIONS = "data" + File.separator + "locations.xml";
	private static final String PATH_FLAGS = "data" + File.separator + "flags.xml";
	private static final String PATH_PRICE_DATA = "data" + File.separator + "pricedata.dat";
	private static final String PATH_ASSETS = "data" + File.separator + "assets.xml";
	private static final String PATH_CONQUERABLE_STATIONS = "data" + File.separator + "conquerable_stations.xml";
	private static final String PATH_CITADEL = "data" + File.separator + "citadel.xml";
	private static final String PATH_README = "readme.txt";
	private static final String PATH_LICENSE = "license.txt";
	private static final String PATH_CREDITS = "credits.txt";
	private static final String PATH_CHANGELOG = "changelog.txt";
	private static final String PATH_PROFILES = "profiles";
	private static final String PATH_DATA = "data";
	private static final String PATH_DATA_VERSION = "data" + File.separator + "data.dat";
	private static final String PATH_PACKAGE_MANAGER = "packagemanager.properties";
	private static final String PATH_MEMORY = "jmemory.jar";
	private static final String PATH_EXPORT = "exports";

	private static enum FileType {
		STATIC_DATA, USER_FILES
	}

	private static boolean testPath = false;

	public static void enableTestPath() {
		testPath = true;
	}

	public static boolean onMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
	}

	public static String getPathDataVersion() {
		return getStaticFile(PATH_DATA_VERSION);
	}

	public static String getPathRunMemory() {
		return getStaticFile(PATH_MEMORY);
	}

	public static String getPathPackageManager() {
		return getStaticFile(PATH_PACKAGE_MANAGER);
	}

	public static String getPathRunUpdate() {
		File userDir = new File(System.getProperty("user.home", "."));
		File file = new File(userDir.getAbsolutePath() + File.separator + ".jupdate" + File.separator + "jupdate.jar");
		File parentDir = file.getParentFile();
		if (!parentDir.exists() && !parentDir.mkdirs()) {
			throw new RuntimeException("Failed to create .jUpdate directory");
		}
		return file.getAbsolutePath();
	}

	public static String getPathSounds(String filename) {
		return getUserFile(PATH_SOUNDS + File.separator + filename);
	}

	public static String getPathSoundsDirectory() {
		return getUserFile(PATH_SOUNDS);
	}

	public static String getUserFile(final String filename) {
		return getLocalFile(filename, FileType.USER_FILES);
	}

	public static String getStaticFile(final String filename) {
		return getLocalFile(filename, FileType.STATIC_DATA);
	}

	/**
	 *
	 * @param filename the name of the data file to obtain
	 * @param fileType
	 * @return
	 */
	private static String getLocalFile(final String filename, FileType fileType) {
		File file;
		File ret;
		if (fileType == FileType.USER_FILES && testPath) {
			ret = new File(FileUtilSimple.getLocalFile("test-output" + File.separator + filename));
		} else if (fileType == FileType.USER_FILES && !CliOptions.get().isPortable()) {
			File userDir = new File(System.getProperty("user.home", "."));
			if (onMac()) { // preferences are stored in user.home/Library/Preferences
				file = new File(userDir, "Library" + File.separator + "Preferences" + File.separator + "JEveAssets");
			} else {
				file = new File(userDir.getAbsolutePath() + File.separator + ".jeveassets");
			}
			ret = new File(file.getAbsolutePath() + File.separator + filename);
		} else { //jEveAssets program directory
			ret = new File(FileUtilSimple.getLocalFile(filename));
		}
		File parent = ret.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
			JOptionPane.showMessageDialog(null, "Failed to create directory " + parent.getAbsolutePath(), Program.PROGRAM_NAME + " - Critical Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		return ret.getAbsolutePath();
	}

	public static String getExtension(final File file) {
		String extension = null;
		if (file == null) {
			return null;
		}
		String filename = file.getName();
		int i = filename.lastIndexOf('.');
		if (i > 0 && i < filename.length() - 1) {
			extension = filename.substring(i + 1).toLowerCase();
		}
		return extension;
	}

	public static void autoImportFileUtil() {
		if (Program.isDevBuild() && !Settings.isTestMode()) { //Need import
			CliOptions.get().setPortable(false);
			Path settingsFrom = Paths.get(getPathSettings());
			Path trackerFrom = Paths.get(getPathTrackerData());
			Path assetAddedFrom = Paths.get(getPathAssetAdded());
			Path assetAddedDatabaseFrom = Paths.get(getPathAssetAddedDatabase());
			Path stockpileIDsDatabaseFrom = Paths.get(getPathStockpileIDsDatabase());
			Path priceHistoryDatabasFrom = Paths.get(getPathPriceHistoryDatabase());
			Path citadelFrom = Paths.get(getPathCitadel());
			Path priceFrom = Paths.get(getPathPriceData());
			Path profilesFrom = Paths.get(getPathProfilesDirectory());
			Path itemsUpdatesFrom = Paths.get(getPathItemsUpdates());
			CliOptions.get().setPortable(true);
			Path settingsTo = Paths.get(getPathSettings());
			Path trackerTo = Paths.get(getPathTrackerData());
			Path assetAddedTo = Paths.get(getPathAssetAdded());
			Path assetAddedDatabaseTo = Paths.get(getPathAssetAddedDatabase());
			Path stockpileIDsDatabaseTo = Paths.get(getPathStockpileIDsDatabase());
			Path priceHistoryDatabasTo = Paths.get(getPathPriceHistoryDatabase());
			Path citadelTo = Paths.get(getPathCitadel());
			Path priceTo = Paths.get(getPathPriceData());
			Path profilesTo = Paths.get(getPathProfilesDirectory());
			Path itemsUpdatesTo = Paths.get(getPathItemsUpdates());
			if (Files.exists(settingsFrom) && !Files.exists(settingsTo)) {
				LOG.info("Importing settings");
				try {
					Files.copy(settingsFrom, settingsTo);
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
			if (Files.exists(trackerFrom) && !Files.exists(trackerTo)) {
				LOG.info("Importing tracker data");
				try {
					Files.copy(trackerFrom, trackerTo);
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
			if (Files.exists(assetAddedFrom) && !Files.exists(assetAddedTo)) {
				LOG.info("Importing asset added");
				try {
					Files.copy(assetAddedFrom, assetAddedTo);
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
			if (Files.exists(assetAddedDatabaseFrom) && !Files.exists(assetAddedDatabaseTo)) {
				LOG.info("Importing asset added");
				try {
					Files.copy(assetAddedDatabaseFrom, assetAddedDatabaseTo);
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
			if (Files.exists(stockpileIDsDatabaseFrom) && !Files.exists(stockpileIDsDatabaseTo)) {
				LOG.info("Importing stockpile IDs");
				try {
					Files.copy(stockpileIDsDatabaseFrom, stockpileIDsDatabaseTo);
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
			if (Files.exists(priceHistoryDatabasFrom) && !Files.exists(priceHistoryDatabasTo)) {
				LOG.info("Importing price history");
				try {
					Files.copy(priceHistoryDatabasFrom, priceHistoryDatabasTo);
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
			if (Files.exists(citadelFrom) && !Files.exists(citadelTo)) {
				LOG.info("Importing citadels");
				try {
					Files.copy(citadelFrom, citadelTo);
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
			if (Files.exists(priceFrom) && !Files.exists(priceTo)) {
				LOG.info("Importing prices");
				try {
					Files.copy(priceFrom, priceTo);
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
			if (Files.exists(itemsUpdatesFrom) && !Files.exists(itemsUpdatesTo)) {
				LOG.info("Importing items updates");
				try {
					Files.copy(itemsUpdatesFrom, itemsUpdatesTo);
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
			if (Files.exists(profilesFrom) && !Files.exists(profilesTo)) {
				PathMatcher xmlMatcher = FileSystems.getDefault().getPathMatcher("glob:*.xml");
				PathMatcher dbMatcher = FileSystems.getDefault().getPathMatcher("glob:*.db");
				try {
					LOG.info("Importing profiles");
					Files.walkFileTree(profilesFrom, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
							if (dir.equals(profilesFrom)) {
								Files.createDirectories(profilesTo.resolve(profilesFrom.relativize(dir)));
								return FileVisitResult.CONTINUE;
							} else {
								return FileVisitResult.SKIP_SUBTREE;
							}
						}

						@Override
						public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
							if (xmlMatcher.matches(file.getFileName()) || dbMatcher.matches(file.getFileName())) {
								Files.copy(file, profilesTo.resolve(profilesFrom.relativize(file)));
							}
							return FileVisitResult.CONTINUE;
						}
					});
					LOG.info("	OK");
				} catch (IOException ex) {
					LOG.info("	FAILED");
				}
			}
		}
	}

	public static String getPathExports() {
		return getUserFile(PATH_EXPORT);
	}

	public static String getPathSettings() {
		return getUserFile(PATH_SETTINGS);
	}

	public static String getPathTrackerData() {
		return getUserFile(PATH_TRACKER_DATA);
	}

	public static String getPathAssetAdded() {
		return getUserFile(PATH_ASSET_ADDED);
	}

	public static String getPathAssetAddedDatabase() {
		return getUserFile(PATH_ASSET_ADDED_DATABASE);
	}

	public static String getPathStockpileIDsDatabase() {
		return getUserFile(PATH_STOCKPILE_IDS_DATABASE);
	}

	public static String getPathPriceHistoryDatabase() {
		return getUserFile(PATH_PRICE_HISTORY_DATABASE);
	}

	public static String getPathConquerableStations() {
		return getUserFile(PATH_CONQUERABLE_STATIONS);
	}

	public static String getPathCitadel() {
		return getUserFile(PATH_CITADEL);
	}

	public static String getPathPriceData() {
		return getUserFile(PATH_PRICE_DATA);
	}

	public static String getPathAssetsOld() {
		return getUserFile(PATH_ASSETS);
	}

	public static String getPathProfilesDirectory() {
		return getUserFile(PATH_PROFILES);
	}

	public static String getPathProfile(String filename) {
		return getUserFile(PATH_PROFILES + File.separator + filename);
	}

	public static String getPathDataDirectory() {
		return getUserFile(PATH_DATA);
	}

	public static String getPathItemsUpdates() {
		return getUserFile(PATH_ITEMS_UPDATES);
	}

	public static String getPathJumps() {
		return getStaticFile(PATH_JUMPS);
	}

	public static String getPathFlags() {
		return getStaticFile(PATH_FLAGS);
	}

	public static String getPathStaticDataDirectory() {
		return getStaticFile(PATH_DATA);
	}

	public static String getPathItems() {
		return getStaticFile(PATH_ITEMS);
	}

	public static String getPathLocations() {
		return getStaticFile(PATH_LOCATIONS);
	}

	public static String getPathReadme() {
		return getStaticFile(PATH_README);
	}

	public static String getPathLicense() {
		return getStaticFile(PATH_LICENSE);
	}

	public static String getPathCredits() {
		return getStaticFile(PATH_CREDITS);
	}

	public static String getPathChangeLog() {
		return getStaticFile(PATH_CHANGELOG);
	}

	public static String getUserDirectory() {
		File userDir = new File(System.getProperty("user.home", "."));
		return userDir.getAbsolutePath() + File.separator;
	}
}
