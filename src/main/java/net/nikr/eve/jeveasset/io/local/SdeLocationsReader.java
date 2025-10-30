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

package net.nikr.eve.jeveasset.io.local;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class SdeLocationsReader {
	private static final Logger LOG = LoggerFactory.getLogger(SdeLocationsReader.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static void load(Map<Long, MyLocation> locations) {
		String sdeDir = FileUtil.getPathSdeDirectory();

        File systemsFile = resolveFile(new File(sdeDir), "mapSolarSystems.jsonl");
        File stationsFile = resolveFile(new File(sdeDir), "npcStations.jsonl");
        File constellationsFile = resolveFile(new File(sdeDir), "mapConstellations.jsonl");
        File regionsFile = resolveFile(new File(sdeDir), "mapRegions.jsonl");
        File npcCorporationsFile = resolveFile(new File(sdeDir), "npcCorporations.jsonl");
        File stationOperationsFile = resolveFile(new File(sdeDir), "stationOperations.jsonl");

		if (!systemsFile.exists()) {
			LOG.error("SDE mapSolarSystems.jsonl file not found at: " + systemsFile.getAbsolutePath());
			return;
		}

        Map<Integer, ConstellationInfo> constellations = loadConstellations(constellationsFile);
        Map<Integer, RegionInfo> regions = loadRegions(regionsFile);
        // Pre-register regions so lookups don’t render as Unknown
        for (Map.Entry<Integer, RegionInfo> e : regions.entrySet()) {
            int regionID = e.getKey();
            String region = e.getValue().name;
            MyLocation regionLoc = MyLocation.create(0, "", 0, "", 0, "", regionID, region, "0.0", false, false);
            locations.put(regionLoc.getLocationID(), regionLoc);
        }
        // Pre-register constellations so lookups don’t render as Unknown
        for (Map.Entry<Integer, ConstellationInfo> e : constellations.entrySet()) {
            int constellationID = e.getKey();
            ConstellationInfo ci = e.getValue();
            RegionInfo ri = regions.get(ci.regionId);
            String region = ri != null ? ri.name : "";
            MyLocation consLoc = MyLocation.create(0, "", 0, "", constellationID, ci.name, ci.regionId, region, "0.0", false, false);
            locations.put(consLoc.getLocationID(), consLoc);
        }
        loadSystems(systemsFile, constellations, regions, locations);
        Map<Integer, String> corpNames = loadNpcCorporationNames(npcCorporationsFile);
        Map<Integer, String> opNames = loadStationOperationNames(stationOperationsFile);
        loadStations(stationsFile, corpNames, opNames, locations);
	}

	private static Map<Integer, ConstellationInfo> loadConstellations(File file) {
		Map<Integer, ConstellationInfo> result = new HashMap<>();
		if (!file.exists()) {
			return result;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				JsonNode node = objectMapper.readTree(line);
                int id = getInt(node, "id", "constellationId", "constellation_id", "constellationID", "_key");
                String name = getLocalized(node.get("name"));
                int regionId = getInt(node, "regionId", "region_id", "regionID");
				result.put(id, new ConstellationInfo(id, name, regionId));
			}
		} catch (Exception ex) {
			LOG.warn("Failed to load constellations: " + ex.getMessage());
		}
		return result;
	}

	private static Map<Integer, RegionInfo> loadRegions(File file) {
		Map<Integer, RegionInfo> result = new HashMap<>();
		if (!file.exists()) {
			return result;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				JsonNode node = objectMapper.readTree(line);
                int id = getInt(node, "id", "regionId", "region_id", "regionID", "_key");
                String name = getLocalized(node.get("name"));
				result.put(id, new RegionInfo(id, name));
			}
		} catch (Exception ex) {
			LOG.warn("Failed to load regions: " + ex.getMessage());
		}
		return result;
	}

	private static void loadSystems(File systemsFile, Map<Integer, ConstellationInfo> constellations, Map<Integer, RegionInfo> regions, Map<Long, MyLocation> locations) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(systemsFile), StandardCharsets.UTF_8))) {
			String line;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				try {
					JsonNode node = objectMapper.readTree(line);
                    int systemID = getInt(node, "id", "solarSystemId", "solar_system_id", "solarSystemID", "_key");
                    String system = getLocalized(node.get("name"));
                    int constellationID = getInt(node, "constellationId", "constellation_id", "constellationID");

					ConstellationInfo constInfo = constellations.get(constellationID);
					String constellation = (constInfo != null) ? constInfo.name : "";
					int regionID = (constInfo != null) ? constInfo.regionId : 0;

					RegionInfo regionInfo = regions.get(regionID);
					String region = (regionInfo != null) ? regionInfo.name : "";

                    double security = node.has("security") ? node.get("security").asDouble() : node.path("securityStatus").asDouble(0.0);
					String securityStr = String.format("%.1f", security);

					MyLocation location = MyLocation.create(0, "", systemID, system, constellationID, constellation, regionID, region, securityStr, false, false);
					locations.put(location.getLocationID(), location);
					count++;
				} catch (Exception ex) {
					LOG.warn("Failed to parse system: " + ex.getMessage());
				}
			}
			LOG.info("Loaded " + count + " systems from SDE");
		} catch (Exception ex) {
			LOG.error("Failed to load systems from SDE: " + ex.getMessage(), ex);
		}
	}

    private static void loadStations(File stationsFile, Map<Integer, String> corpNames, Map<Integer, String> opNames, Map<Long, MyLocation> locations) {
		if (!stationsFile.exists()) {
			return;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stationsFile), StandardCharsets.UTF_8))) {
			String line;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				try {
					JsonNode node = objectMapper.readTree(line);
                    long stationID = node.has("stationId") ? node.get("stationId").asLong() : (node.has("_key") ? node.get("_key").asLong() : node.path("id").asLong());
                    String station = node.has("stationName") ? getLocalized(node.get("stationName")) : (node.has("name") ? getLocalized(node.get("name")) : "");
                    int systemID = getInt(node, "solarSystemId", "solar_system_id", "solarSystemID");
                    int ownerId = node.has("ownerID") ? node.get("ownerID").asInt() : 0;
                    boolean useOp = node.has("useOperationName") && node.get("useOperationName").asBoolean(false);
                    int opId = node.has("operationID") ? node.get("operationID").asInt() : 0;
                    int celestialIndex = node.has("celestialIndex") ? node.get("celestialIndex").asInt() : 0;
                    // Synthesize name if absent in SDE: use owner corp name and station type
                    if (station == null || station.isEmpty()) {
                        int typeId = node.has("typeID") ? node.get("typeID").asInt() : node.path("stationTypeID").asInt(0);
                        String typeName = "";
                        if (typeId != 0) {
                            Item t = ApiIdConverter.getItem(typeId);
                            typeName = t != null ? t.getTypeName() : "";
                        }
                        String ownerName = corpNames.getOrDefault(ownerId, "");
                        String base;
                        if (useOp && opNames.containsKey(opId)) {
                            base = (ownerName.isEmpty() ? "" : ownerName + " ") + opNames.get(opId);
                        } else if (!ownerName.isEmpty() && !typeName.isEmpty()) {
                            base = ownerName + " " + typeName;
                        } else if (!typeName.isEmpty()) {
                            base = typeName;
                        } else {
                            base = "Station #" + stationID;
                        }
                        // Prefix with system + roman planet index to match legacy naming
                        MyLocation sysLoc = locations.get((long) systemID);
                        if (sysLoc != null && celestialIndex > 0) {
                            station = sysLoc.getSystem() + " " + toRoman(celestialIndex) + " - " + base;
                        } else if (sysLoc != null) {
                            station = sysLoc.getSystem() + " - " + base;
                        } else {
                            station = base;
                        }
                    }

					MyLocation systemLocation = locations.get((long) systemID);
					if (systemLocation != null) {
						String system = systemLocation.getSystem();
						long constellationID = systemLocation.getConstellationID();
						String constellation = systemLocation.getConstellation();
						long regionID = systemLocation.getRegionID();
						String region = systemLocation.getRegion();
						String security = systemLocation.getSecurity();

						MyLocation location = MyLocation.create(stationID, station, systemID, system, constellationID, constellation, regionID, region, security, false, false);
						locations.put(location.getLocationID(), location);
						count++;
					}
				} catch (Exception ex) {
					LOG.warn("Failed to parse station: " + ex.getMessage());
				}
			}
			LOG.info("Loaded " + count + " stations from SDE");
		} catch (Exception ex) {
			LOG.warn("Failed to load stations from SDE: " + ex.getMessage());
		}
	}

    private static Map<Integer, String> loadStationOperationNames(File stationOperationsFile) {
        Map<Integer, String> result = new HashMap<>();
        if (!stationOperationsFile.exists()) {
            return result;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stationOperationsFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    JsonNode node = objectMapper.readTree(line);
                    int id = getInt(node, "operationID", "_key");
                    String name = getLocalized(node.get("operationName"));
                    result.put(id, name);
                } catch (Exception ignore) {}
            }
        } catch (Exception ex) {
            LOG.warn("Failed to load station operations: " + ex.getMessage());
        }
        return result;
    }

    private static String toRoman(int number) {
        // Supports 1..3999 which is more than enough for planetary indices
        int[] values = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        String[] numerals = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
        StringBuilder sb = new StringBuilder();
        int n = Math.max(0, number);
        for (int i = 0; i < values.length; i++) {
            while (n >= values[i]) {
                n -= values[i];
                sb.append(numerals[i]);
            }
        }
        return sb.length() > 0 ? sb.toString() : String.valueOf(number);
    }

    private static Map<Integer, String> loadNpcCorporationNames(File npcCorporationsFile) {
        Map<Integer, String> result = new HashMap<>();
        if (!npcCorporationsFile.exists()) {
            return result;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(npcCorporationsFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    JsonNode node = objectMapper.readTree(line);
                    int corpId = getInt(node, "corporationID", "corporationId", "_key");
                    String name = getLocalized(node.get("name"));
                    result.put(corpId, name);
                } catch (Exception ex) {
                    // ignore malformed line
                }
            }
        } catch (Exception ex) {
            LOG.warn("Failed to load NPC corporation names: " + ex.getMessage());
        }
        return result;
    }

	private static class ConstellationInfo {
		final String name;
		final int regionId;

		ConstellationInfo(int id, String name, int regionId) {
			this.name = name;
			this.regionId = regionId;
		}
	}

	private static class RegionInfo {
		final String name;

		RegionInfo(int id, String name) {
			this.name = name;
		}
	}
    private static int getInt(JsonNode node, String... keys) {
        for (String k : keys) {
            if (node.has(k) && node.get(k) != null && node.get(k).canConvertToInt()) {
                return node.get(k).asInt();
            }
        }
        throw new IllegalArgumentException("Missing int field");
    }

    private static String getLocalized(JsonNode nameNode) {
        if (nameNode == null) return "";
        if (nameNode.has("en")) return nameNode.get("en").asText("");
        if (nameNode.isTextual()) return nameNode.asText("");
        return "";
    }

    private static File resolveFile(File dir, String filename) {
        File direct = new File(dir, filename);
        if (direct.exists()) return direct;
        try {
            Path found = Files.walk(dir.toPath())
                .filter(p -> p.getFileName().toString().equals(filename))
                .findFirst()
                .orElse(null);
            return found != null ? found.toFile() : direct;
        } catch (Exception ex) {
            return direct;
        }
    }
}
