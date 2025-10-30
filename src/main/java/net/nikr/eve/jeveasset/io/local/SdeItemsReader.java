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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.nikr.eve.jeveasset.data.sde.IndustryMaterial;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class SdeItemsReader {
	private static final Logger LOG = LoggerFactory.getLogger(SdeItemsReader.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static void load(Map<Integer, Item> items) {
		String sdeDir = FileUtil.getPathSdeDirectory();

		File typesFile = resolveFile(new File(sdeDir), "types.jsonl");
		File categoriesFile = resolveFile(new File(sdeDir), "categories.jsonl");
		File groupsFile = resolveFile(new File(sdeDir), "groups.jsonl");
        File blueprintProductsFile = resolveFile(new File(sdeDir), "blueprints.jsonl");
		File typeMaterialsFile = resolveFile(new File(sdeDir), "typeMaterials.jsonl");

		if (!typesFile.exists()) {
			LOG.error("SDE types.jsonl file not found at: " + typesFile.getAbsolutePath());
			return;
		}

        Map<Integer, String> categories = loadCategories(categoriesFile);
        Map<Integer, GroupInfo> groups = loadGroups(groupsFile);
		Map<Integer, BlueprintProduct> blueprintProducts = loadBlueprintProducts(blueprintProductsFile);
		Map<Integer, TypeMaterials> typeMaterials = loadTypeMaterials(typeMaterialsFile);

		loadItems(typesFile, categories, groups, blueprintProducts, typeMaterials, items);

		linkBlueprints(items, blueprintProducts);
	}

	private static Map<Integer, String> loadCategories(File file) {
		Map<Integer, String> result = new HashMap<>();
		if (!file.exists()) {
			return result;
		}
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				JsonNode node = objectMapper.readTree(line);
                int id = getInt(node, "id", "categoryId", "category_id", "_key");
				String name = getLocalized(node.get("name"));
				result.put(id, name);
			}
		} catch (Exception ex) {
			LOG.warn("Failed to load categories: " + ex.getMessage());
		}
		return result;
	}

    private static class GroupInfo {
        final String name;
        final int categoryId;
        GroupInfo(String name, int categoryId) { this.name = name; this.categoryId = categoryId; }
    }

    private static Map<Integer, GroupInfo> loadGroups(File file) {
        Map<Integer, GroupInfo> result = new HashMap<>();
		if (!file.exists()) {
			return result;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				JsonNode node = objectMapper.readTree(line);
                int id = getInt(node, "id", "groupId", "group_id", "_key");
                String name = getLocalized(node.get("name"));
                int categoryId = getInt(node, "categoryId", "category_id", "categoryID");
                result.put(id, new GroupInfo(name, categoryId));
			}
		} catch (Exception ex) {
			LOG.warn("Failed to load groups: " + ex.getMessage());
		}
		return result;
	}

	private static Map<Integer, BlueprintProduct> loadBlueprintProducts(File file) {
		Map<Integer, BlueprintProduct> result = new HashMap<>();
		if (!file.exists()) {
			return result;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				JsonNode node = objectMapper.readTree(line);
                int blueprintTypeId = getInt(node, "blueprintTypeID", "blueprintTypeId", "_key");
                JsonNode mfg = node.path("activities").path("manufacturing");
                JsonNode products = mfg.path("products");
                if (products.isArray() && products.size() > 0) {
                    JsonNode product = products.get(0);
                    int productTypeId = getInt(product, "typeID", "typeId");
                    int quantity = product.path("quantity").asInt(1);
                    result.put(blueprintTypeId, new BlueprintProduct(blueprintTypeId, productTypeId, quantity));
                }
			}
		} catch (Exception ex) {
			LOG.warn("Failed to load blueprint products: " + ex.getMessage());
		}
		return result;
	}

	private static Map<Integer, TypeMaterials> loadTypeMaterials(File file) {
		Map<Integer, TypeMaterials> result = new HashMap<>();
		if (!file.exists()) {
			return result;
		}
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				JsonNode node = objectMapper.readTree(line);
                int typeId = getInt(node, "typeId", "type_id", "_key");
                TypeMaterials tm = result.computeIfAbsent(typeId, k -> new TypeMaterials());
                if (node.has("materials") && node.get("materials").isArray()) {
                    for (JsonNode m : node.get("materials")) {
                        int materialTypeId = getInt(m, "materialTypeID", "materialTypeId", "material_type_id", "typeID", "typeId");
                        int quantity = m.path("quantity").asInt();
                        tm.manufacturingMaterials.add(new IndustryMaterial(materialTypeId, quantity));
                    }
                }
			}
		} catch (Exception ex) {
            LOG.warn("Failed to load type materials: " + ex.getMessage());
		}
		return result;
	}

    private static void loadItems(File typesFile, Map<Integer, String> categories, Map<Integer, GroupInfo> groups,
			Map<Integer, BlueprintProduct> blueprintProducts, Map<Integer, TypeMaterials> typeMaterials,
			Map<Integer, Item> items) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(typesFile), StandardCharsets.UTF_8))) {
			String line;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				try {
					JsonNode node = objectMapper.readTree(line);
                    Item item = parseItem(node, categories, groups, blueprintProducts, typeMaterials);
					if (item != null) {
						items.put(item.getTypeID(), item);
						count++;
					}
				} catch (Exception ex) {
					LOG.warn("Failed to parse item: " + ex.getMessage());
				}
			}
			LOG.info("Loaded " + count + " items from SDE");
		} catch (Exception ex) {
			LOG.error("Failed to load items from SDE: " + ex.getMessage(), ex);
		}
	}

    private static Item parseItem(JsonNode node, Map<Integer, String> categories, Map<Integer, GroupInfo> groups,
			Map<Integer, BlueprintProduct> blueprintProducts, Map<Integer, TypeMaterials> typeMaterials) {
        int typeID = getInt(node, "id", "typeId", "type_id", "_key");
		String name = node.has("name") ? getLocalized(node.get("name")) : "";

        int groupID = getInt(node, "groupId", "group_id", "groupID");
        GroupInfo gi = groups.get(groupID);
        String group = gi != null ? gi.name : "";
        int categoryID = gi != null ? gi.categoryId : -1;
        String category = categoryID >= 0 ? categories.getOrDefault(categoryID, "") : "";

        long basePrice = node.has("basePrice") ? node.get("basePrice").asLong() : -1;
		float volume = node.has("volume") ? (float) node.get("volume").asDouble() : -1f;
		float packagedVolume = node.has("packagedVolume") ? (float) node.get("packagedVolume").asDouble() : volume;
		float capacity = node.has("capacity") ? (float) node.get("capacity").asDouble() : 0f;

		int meta = node.has("metaLevel") ? node.get("metaLevel").asInt() : -1;
		String tech = node.has("techLevel") ? String.valueOf(node.get("techLevel").asInt()) : "";
		boolean marketGroup = node.has("marketGroupId");
		int portion = node.has("portionSize") ? node.get("portionSize").asInt() : 1;

		BlueprintProduct bp = blueprintProducts.get(typeID);
		int productTypeID = (bp != null) ? bp.productTypeId : 0;
		int productQuantity = (bp != null) ? bp.quantity : 1;

		String slot = null;
		if (node.has("slot")) {
			slot = getLocalized(node.get("slot"));
		}

		String chargeSize = null;
        if (node.has("chargeSize") || node.has("charge_size")) {
            int sz = node.has("chargeSize") ? node.get("chargeSize").asInt() : node.get("charge_size").asInt();
            chargeSize = ItemsReader.getChargeSize(sz);
		}

		Item item = new Item(typeID, name, group, category, basePrice, volume, packagedVolume, capacity, meta, tech, marketGroup, portion, productTypeID, productQuantity, slot, chargeSize, null);

		TypeMaterials tm = typeMaterials.get(typeID);
		if (tm != null) {
			for (ReprocessedMaterial rm : tm.reprocessedMaterials) {
				item.addReprocessedMaterial(rm);
			}
			for (IndustryMaterial im : tm.manufacturingMaterials) {
				item.addManufacturingMaterial(im);
			}
			for (IndustryMaterial im : tm.reactionMaterials) {
				item.addReactionMaterial(im);
			}
		}

		return item;
	}

	private static int getInt(JsonNode node, String... keys) {
		for (String k : keys) {
			if (node.has(k) && node.get(k) != null && node.get(k).isInt()) {
				return node.get(k).asInt();
			}
			if (node.has(k) && node.get(k) != null && node.get(k).canConvertToInt()) {
				return node.get(k).asInt();
			}
		}
		throw new IllegalArgumentException("Missing int field; tried keys: " + Arrays.toString(keys));
	}

	private static String getLocalized(JsonNode nameNode) {
		if (nameNode == null) return "";
		if (nameNode.has("en")) return nameNode.get("en").asText("");
		// Some entries may be a flat string
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

	private static void linkBlueprints(Map<Integer, Item> items, Map<Integer, BlueprintProduct> blueprintProducts) {
		for (Map.Entry<Integer, BlueprintProduct> entry : blueprintProducts.entrySet()) {
			Item blueprint = items.get(entry.getKey());
			if (blueprint != null && blueprint.isBlueprint()) {
				Item product = items.get(entry.getValue().productTypeId);
				if (product != null) {
					product.addBlueprintID(entry.getKey());
				}
			}
		}
	}

	private static class BlueprintProduct {
		final int productTypeId;
		final int quantity;

		BlueprintProduct(int blueprintTypeId, int productTypeId, int quantity) {
			this.productTypeId = productTypeId;
			this.quantity = quantity;
		}
	}

	private static class TypeMaterials {
		final java.util.List<ReprocessedMaterial> reprocessedMaterials = new java.util.ArrayList<>();
		final java.util.List<IndustryMaterial> manufacturingMaterials = new java.util.ArrayList<>();
		final java.util.List<IndustryMaterial> reactionMaterials = new java.util.ArrayList<>();
	}
}
