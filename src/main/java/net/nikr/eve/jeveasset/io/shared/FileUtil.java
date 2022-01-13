/*
 * Copyright 2009-2021 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileUtil extends FileUtilSimple {
	private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

	private static final String PATH_ASSET_ADDED = "data" + File.separator + "added.json";
	private static final String PATH_ASSET_ADDED_DATABASE = "data" + File.separator + "addedsql.db";
	private static final String PATH_TRACKER_DATA = "data" + File.separator + "tracker.json";
	private static final String PATH_CONTRACT_PRICES = "data" + File.separator + "contract_prices.json";
	private static final String PATH_SETTINGS = "data" + File.separator + "settings.xml";
	private static final String PATH_ITEMS = "data" + File.separator + "items.xml";
	private static final String PATH_ITEMS_UPDATES = "data" + File.separator + "items_updates.xml";
	private static final String PATH_JUMPS = "data" + File.separator + "jumps.xml";
	private static final String PATH_LOCATIONS = "data" + File.separator + "locations.xml";
	private static final String PATH_FLAGS = "data" + File.separator + "flags.xml";
	private static final String PATH_PRICE_DATA = "data" + File.separator + "pricedata.dat";
	private static final String PATH_ASSETS = "data" + File.separator + "assets.xml";
	private static final String PATH_CONQUERABLE_STATIONS = "data" + File.separator + "conquerable_stations.xml";
	private static final String PATH_LOGS = "data" + File.separator + "asset_logs.xml";
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

	public static boolean onMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
	}

	public static String getPathDataVersion() {
		return getLocalFile(PATH_DATA_VERSION, false);
	}

	public static String getPathRunMemory() {
		return getLocalFile(PATH_MEMORY, false);
	}

	public static String getPathPackageManager() {
		return getLocalFile(PATH_PACKAGE_MANAGER, false);
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

	/**
	 *
	 * @param filename the name of the data file to obtain
	 * @param dynamic true if the file is expecting to be written to, false for
	 * things like the items and locations.
	 * @return
	 */
	public static String getLocalFile(final String filename, final boolean dynamic) {
		File file;
		File ret;
		if (dynamic) {
			File userDir = new File(System.getProperty("user.home", "."));
			if (onMac()) { // preferences are stored in user.home/Library/Preferences
				file = new File(userDir, "Library" + File.separator + "Preferences" + File.separator + "JEveAssets");
			} else {
				file = new File(userDir.getAbsolutePath() + File.separator + ".jeveassets");
			}
			ret = new File(file.getAbsolutePath() + File.separator + filename);
			File parent = ret.getParentFile();
			if (!parent.exists()
					&& !parent.mkdirs()) {
				throw new RuntimeException("failed to create directories for " + parent.getAbsolutePath());
			}
		} else {
			ret = new File(FileUtilSimple.getLocalFile(filename));
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
		if (i > 0 &&  i < filename.length() - 1) {
			extension = filename.substring(i + 1).toLowerCase();
		}
		return extension;
	}

	public static void autoImportFileUtil() {
		if (Program.isDevBuild() && !Settings.isTestMode()) { //Need import
			Program.setPortable(false);
			Path settingsFrom = Paths.get(getPathSettings());
			Path trackerFrom = Paths.get(getPathTrackerData());
			Path assetAddedFrom = Paths.get(getPathAssetAdded());
			Path assetAddedDatabaseFrom = Paths.get(getPathAssetAddedDatabase());
			Path citadelFrom = Paths.get(getPathCitadel());
			Path priceFrom = Paths.get(getPathPriceData());
			Path profilesFrom = Paths.get(getPathProfilesDirectory());
			Path contractPricesFrom = Paths.get(getPathContractPrices());
			Path itemsUpdatesFrom = Paths.get(getPathItemsUpdates());
			Program.setPortable(true);
			Path settingsTo = Paths.get(getPathSettings());
			Path trackerTo = Paths.get(getPathTrackerData());
			Path assetAddedTo = Paths.get(getPathAssetAdded());
			Path assetAddedDatabaseTo = Paths.get(getPathAssetAddedDatabase());
			Path citadelTo = Paths.get(getPathCitadel());
			Path priceTo = Paths.get(getPathPriceData());
			Path profilesTo = Paths.get(getPathProfilesDirectory());
			Path contractPricesTo = Paths.get(getPathContractPrices());
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
			if (Files.exists(contractPricesFrom) && !Files.exists(contractPricesTo)) {
				LOG.info("Importing contract prices");
				try {
					Files.copy(contractPricesFrom, contractPricesTo);
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
				PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.xml");
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
							if (matcher.matches(file.getFileName())) {
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

	public static String getPathSettings() {
		return getLocalFile(PATH_SETTINGS, !Program.isPortable());
	}

	public static String getPathTrackerData() {
		return getLocalFile(PATH_TRACKER_DATA, !Program.isPortable());
	}

	public static String getPathContractPrices() {
		return getLocalFile(PATH_CONTRACT_PRICES, !Program.isPortable());
	}

	public static String getPathAssetAdded() {
		return getLocalFile(PATH_ASSET_ADDED, !Program.isPortable());
	}

	public static String getPathAssetAddedDatabase() {
		return getLocalFile(PATH_ASSET_ADDED_DATABASE, !Program.isPortable());
	}

	public static String getPathConquerableStations() {
		return getLocalFile(PATH_CONQUERABLE_STATIONS, !Program.isPortable());
	}

	public static String getPathCitadel() {
		return getLocalFile(PATH_CITADEL, !Program.isPortable());
	}

	public static String getPathJumps() {
		return getLocalFile(PATH_JUMPS, false);
	}

	public static String getPathFlags() {
		return getLocalFile(PATH_FLAGS, false);
	}

	public static String getPathPriceData() {
		return getLocalFile(PATH_PRICE_DATA, !Program.isPortable());
	}

	public static String getPathAssetsOld() {
		return getLocalFile(PATH_ASSETS, !Program.isPortable());
	}

	public static String getPathProfilesDirectory() {
		return getLocalFile(PATH_PROFILES, !Program.isPortable());
	}

	public static String getPathStaticDataDirectory() {
		return getLocalFile(PATH_DATA, false);
	}

	public static String getPathDataDirectory() {
		return getLocalFile(PATH_DATA, !Program.isPortable());
	}

	public static String getPathItems() {
		return getLocalFile(PATH_ITEMS, false);
	}
	
	public static String getPathItemsUpdates() {
		return getLocalFile(PATH_ITEMS_UPDATES, !Program.isPortable());
	}

	public static String getPathLocations() {
		return getLocalFile(PATH_LOCATIONS, false);
	}

	public static String getPathLogs() {
		return getLocalFile(PATH_LOGS, !Program.isPortable());
	}

	public static String getPathReadme() {
		return getLocalFile(PATH_README, false);
	}

	public static String getPathLicense() {
		return getLocalFile(PATH_LICENSE, false);
	}

	public static String getPathCredits() {
		return getLocalFile(PATH_CREDITS, false);
	}

	public static String getPathChangeLog() {
		return getLocalFile(PATH_CHANGELOG, false);
	}

	public static String getUserDirectory() {
		File userDir = new File(System.getProperty("user.home", "."));
		return userDir.getAbsolutePath() + File.separator;
	}
}
