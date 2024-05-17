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
package net.nikr.eve.jeveasset.io.online;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import net.nikr.eve.jeveasset.data.sde.IndustryMaterial;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.ReprocessedMaterial;
import net.nikr.eve.jeveasset.io.esi.EsiItemsGetter;
import net.nikr.eve.jeveasset.io.shared.ThreadWoker;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EveRefGetter {

	private static final Logger LOG = LoggerFactory.getLogger(EveRefGetter.class);
	private static final Gson GSON = new GsonBuilder().create();
	/**
	* HttpLoggingInterceptor
	*/
	private static final HttpLoggingInterceptor HTTP_LOGGING_INTERCEPTOR = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
									@Override
									public void log(String string) {
										LOG.debug(string);
									}
								});
	/**
	 * HTTP Client
	 */
	private final OkHttpClient client;

	private static EveRefGetter getter;

	private EveRefGetter() {
		if (LOG.isDebugEnabled()) {
			client = new OkHttpClient().newBuilder()
				.addNetworkInterceptor(HTTP_LOGGING_INTERCEPTOR)
				.build();
			HTTP_LOGGING_INTERCEPTOR.setLevel(HttpLoggingInterceptor.Level.BASIC);
		} else {
			 client = new OkHttpClient().newBuilder().build();
		}
	}	

	public static Item getItem(Item item) {
		//Get Type Data
		EveRefType type = getType(item.getTypeID());
		//Tech Level
		String tech = item.getTech();
		Integer metaGroupID = type.getMetaGroupID();
		if (metaGroupID != null) {
			tech = EsiItemsGetter.getTechLevel(metaGroupID);
		}
		//BlueprintIDs
		Set<Integer> blueprintTypeIDs = new HashSet<>();
		Map<String, ProducedByBlueprints> producedByBlueprints = type.getProducedByBlueprints();
		if (producedByBlueprints != null) {
			for (ProducedByBlueprints blueprints : producedByBlueprints.values()) {
				if (blueprints.getBlueprintActivity().equalsIgnoreCase("manufacturing")) {
					blueprintTypeIDs.add(blueprints.getBlueprintTypeID());
				}
			}
		}
		//Reproccesed
		List<ReprocessedMaterial> reprocessedMaterials = new ArrayList<>();
		if (type.getTypeMaterials() != null && item.isMarketGroup()) {
			for (TypeMaterial material : type.getTypeMaterials().values()) {
				reprocessedMaterials.add(new ReprocessedMaterial(material.getMaterialTypeID(), material.getQuantity(), type.getPortionSize()));
			}
		}
		int productTypeID = EsiItemsGetter.PRODUCT_TYPE_ID_DEFAULT;
		int productQuantity = EsiItemsGetter.PRODUCT_QUANTITY_DEFAULT;
		List<IndustryMaterial> manufacturingMaterials = new ArrayList<>();
		List<IndustryMaterial> reactionMaterials = new ArrayList<>();
		if (get(type.isBlueprint(), false)) {
			//Get Blueprint Data
			EveRefBlueprint blueprint = getBlueprint(item.getTypeID());
			//Blueprints
			for (EveRefMaterial eveRefMaterial : blueprint.getManufacturing().getMaterials().values()) {
				manufacturingMaterials.add(new IndustryMaterial(eveRefMaterial.getTypeID(), eveRefMaterial.getQuantity()));
			}
			Map<String, EveRefMaterial> manufacturingProducts = blueprint.getManufacturing().getProducts();
			if (!manufacturingProducts.isEmpty()) {
				EveRefMaterial material = manufacturingProducts.values().iterator().next();
				productTypeID = material.getTypeID();
				productQuantity = material.getQuantity();
			}
			//Reactions
			for (EveRefMaterial eveRefMaterial : blueprint.getReaction().getMaterials().values()) {
				reactionMaterials.add(new IndustryMaterial(eveRefMaterial.getTypeID(), eveRefMaterial.getQuantity()));
			}
			Map<String, EveRefMaterial> reactionProducts = blueprint.getReaction().getProducts();
			if (!reactionProducts.isEmpty()) {
				EveRefMaterial material = reactionProducts.values().iterator().next();
				productTypeID = material.getTypeID();
				productQuantity = material.getQuantity();
			}
			
		}
		return new Item(item, get(type.getBasePrice(), EsiItemsGetter.BASE_PRICE_DEFAULT).longValue(), tech, productTypeID, productQuantity, blueprintTypeIDs, reprocessedMaterials, manufacturingMaterials, reactionMaterials);
	}

	private static <V> V get(V value, V defaultValue) {
		if (value != null) {
			return value;
		}
		return defaultValue;
	}

	public static EveRefBlueprint getBlueprint(int TypeID) {
		if (getter == null) {
			getter = new EveRefGetter();
		}
		return getter.blueprint(TypeID);
	}

	public static EveRefType getType(int TypeID) {
		if (getter == null) {
			getter = new EveRefGetter();
		}
		return getter.type(TypeID);
	}

	private EveRefBlueprint blueprint(final int typeID) {
		Update<EveRefBlueprint> update = new Update<EveRefBlueprint>(new Updater() {
			@Override
			public Call getCall() {
				Request.Builder request = new Request.Builder()
					.url("https://ref-data.everef.net/blueprints/" + typeID)
					.addHeader("User-Agent", System.getProperty("http.agent"));
				return client.newCall(request.build());
			}

			@Override
			public Type getType() {
				return new TypeToken<EveRefBlueprint>() {}.getType();
			}
		}) {
		};
		return ThreadWoker.startReturn(null, update); //Return null on failure
	}

	private EveRefType type(final int typeID) {
		Update<EveRefType> update = new Update<EveRefType>(new Updater() {
			@Override
			public Call getCall() {
				Request.Builder request = new Request.Builder()
					.url("https://ref-data.everef.net/types/" + typeID)
					.addHeader("User-Agent", System.getProperty("http.agent"));
				return client.newCall(request.build());
			}

			@Override
			public Type getType() {
				return new TypeToken<EveRefType>() {}.getType();
			}
		}) {
		};
		return ThreadWoker.startReturn(null, update); //Return null on failure
	}

	private abstract class Update<T> implements Callable<T> {

		private final Updater updater;

		public Update(Updater updater) {
			this.updater = updater;
		}

		@Override
		public T call() throws Exception {
			long start = System.currentTimeMillis();
			T results = null;
			try {
				results = GSON.fromJson(updater.getCall().execute().body().string(), updater.getType());
				if (results == null) {
					LOG.error("Error fetching price", new Exception("results is null"));
				}
			} catch (IllegalArgumentException | IOException | JsonParseException ex) {
				LOG.error("Error fetching price", ex);
			}
			long duration = System.currentTimeMillis() - start;
			LOG.info("Completed in " + duration + "ms");
			return results;
		}
	
		
	}

	private static interface Updater {
		public Call getCall();
		public Type getType();
	}

	public static class EveRefBlueprint {

		private Map<String, EveRefActivity> activities;
		@SerializedName(value = "blueprint_type_id")
		private Long blueprintTypeID;
		@SerializedName(value = "max_production_limit")
		private Long maxProductionLimit;

		public Map<String, EveRefActivity> getActivities() {
			return activities;
		}

		public EveRefActivity getCopying() {
			return activities.getOrDefault("copying", new EveRefActivity());
		}
		
		public EveRefActivity getInvention() {
			return activities.getOrDefault("invention", new EveRefActivity());
		}
	
		public EveRefActivity getManufacturing() {
			return activities.getOrDefault("manufacturing", new EveRefActivity());
		}

		public EveRefActivity getReaction() {
			return activities.getOrDefault("reaction", new EveRefActivity());
		}
	
		public EveRefActivity getResearchMaterial() {
			return activities.getOrDefault("research_material", new EveRefActivity());
		}
	
		public EveRefActivity getResearchTime() {
			return activities.getOrDefault("research_time", new EveRefActivity());
		}

		public Long getBlueprintTypeID() {
			return blueprintTypeID;
		}
	
		public Long getMaxProductionLimit() {
			return maxProductionLimit;
		}
	}

	public static class EveRefActivity {

		private Map<String, EveRefMaterial> materials = new HashMap<>();
		private Map<String, EveRefMaterial> products = new HashMap<>();
		private Long time;
		@SerializedName(value = "required_skills")
		private Map<String, Long> requiredSkills;

		public Map<String, EveRefMaterial> getMaterials() {
			return materials;
			
		}

		public Map<String, EveRefMaterial> getProducts() {
			return products;
		}

		public Long getTime() {
			return time;
		}

		public Map<String, Long> getRequiredSkills() {
			return requiredSkills;
		}
	}

	public static class EveRefMaterial {

		private Double probability;
		private Integer quantity;
		@SerializedName(value = "type_id")
		private Integer typeID;

		public Double getProbability() {
			return probability;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public Integer getTypeID() {
			return typeID;
		}
	}

	public class EveRefType {
		@SerializedName(value = "type_id")
		private Long typeID;
		@SerializedName(value = "base_price")
		private Double basePrice;
		private Double capacity;
		private Map<String, String> description;
		@SerializedName(value = "dogma_attributes")
		private Map<String, DogmaAttribute> dogmaAttributes;
		@SerializedName(value = "dogma_effects")
		private Map<String, DogmaEffect> dogmaEffects;
		@SerializedName(value = "faction_id")
		private Long factionID;
		@SerializedName(value = "graphic_id")
		private Long graphicID;
		@SerializedName(value = "group_id")
		private Long groupID;
		@SerializedName(value = "icon_id")
		private Long iconID;
		@SerializedName(value = "market_group_id")
		private Long marketGroupID;
		private Double mass;
		private Map<String, List<Long>> masteries;
		@SerializedName(value = "meta_group_id")
		private Integer metaGroupID;
		private Map<String, String> name;
		@SerializedName(value = "packaged_volume")
		private Double packagedVolume;
		@SerializedName(value = "portion_size")
		private Integer portionSize;
		private Boolean published;
		@SerializedName(value = "race_id")
		private Long raceID;
		private Double radius;
		@SerializedName(value = "sof_faction_name")
		private String sofFactionName;
		@SerializedName(value = "sof_material_set_id")
		private Long sofMaterialSetID;
		@SerializedName(value = "sound_id")
		private Long soundID;
		private Traits traits;
		@SerializedName(value = "variation_parent_type_id")
		private Long variationParentTypeID;
		private Double volume;
		@SerializedName(value = "required_skills")
		private Map<String, Long> requiredSkills;
		@SerializedName(value = "applicable_mutaplasmid_type_ids")
		private List<Long> applicableMutaplasmidTypeIDS;
		@SerializedName(value = "creating_mutaplasmid_type_ids")
		private List<Long> creatingMutaplasmidTypeIDS;
		@SerializedName(value = "type_variations")
		private Map<String, List<Long>> typeVariations;
		@SerializedName(value = "ore_variations")
		private Map<String, List<Long>> oreVariations;
		@SerializedName(value = "is_ore")
		private Boolean ore;
		@SerializedName(value = "produced_by_blueprints")
		private Map<String, ProducedByBlueprints> producedByBlueprints;
		@SerializedName(value = "type_materials")
		private Map<String, TypeMaterial> typeMaterials;
		@SerializedName(value = "can_fit_types")
		private List<Long> canFitTypes;
		@SerializedName(value = "can_be_fitted_with_types")
		private List<Long> canBeFittedWithTypes;
		@SerializedName(value = "is_skill")
		private Boolean skill;
		@SerializedName(value = "is_mutaplasmid")
		private Boolean mutaplasmid;
		@SerializedName(value = "is_dynamic_item")
		private Boolean dynamicItem;
		@SerializedName(value = "is_blueprint")
		private Boolean blueprint;

		public Long getTypeID() { return typeID; }
		public Double getBasePrice() { return basePrice; }
		public Double getCapacity() { return capacity; }
		public Map<String, String> getDescription() { return description; }
		public Map<String, DogmaAttribute> getDogmaAttributes() { return dogmaAttributes; }
		public Map<String, DogmaEffect> getDogmaEffects() { return dogmaEffects; }
		public Long getFactionID() { return factionID; }
		public Long getGraphicID() { return graphicID; }
		public Long getGroupID() { return groupID; }
		public Long getIconID() { return iconID; }
		public Long getMarketGroupID() { return marketGroupID; }
		public Double getMass() { return mass; }
		public Map<String, List<Long>> getMasteries() { return masteries; }
		public Integer getMetaGroupID() { return metaGroupID; }
		public Map<String, String> getName() { return name; }
		public Double getPackagedVolume() { return packagedVolume; }
		public Integer getPortionSize() { return portionSize; }
		public Boolean isPublished() { return published; }
		public Long getRaceID() { return raceID; }
		public Double getRadius() { return radius; }
		public String getSofFactionName() { return sofFactionName; }
		public Long getSofMaterialSetID() { return sofMaterialSetID; }
		public Long getSoundID() { return soundID; }
		public Traits getTraits() { return traits; }
		public Long getVariationParentTypeID() { return variationParentTypeID; }
		public Double getVolume() { return volume; }
		public Map<String, Long> getRequiredSkills() { return requiredSkills; }
		public List<Long> getApplicableMutaplasmidTypeIDS() { return applicableMutaplasmidTypeIDS; }
		public List<Long> getCreatingMutaplasmidTypeIDS() { return creatingMutaplasmidTypeIDS; }
		public Map<String, List<Long>> getTypeVariations() { return typeVariations; }
		public Map<String, List<Long>> getOreVariations() { return oreVariations; }
		public Boolean isOre() { return ore; }
		public Map<String, ProducedByBlueprints> getProducedByBlueprints() { return producedByBlueprints; }
		public Map<String, TypeMaterial> getTypeMaterials() { return typeMaterials; }
		public List<Long> getCanFitTypes() { return canFitTypes; }
		public List<Long> getCanBeFittedWithTypes() { return canBeFittedWithTypes; }
		public Boolean isSkill() { return skill; }
		public Boolean isMutaplasmid() { return mutaplasmid; }
		public Boolean isDynamicItem() { return dynamicItem; }
		public Boolean isBlueprint() { return blueprint; }
	}

	
	public class DogmaAttribute {
		@SerializedName(value = "attribute_id")
		private Long attributeID;
		private Double value;

		public Long getAttributeID() { return attributeID; }
		public Double getValue() { return value; }
	}

	public class DogmaEffect {
		@SerializedName(value = "effect_id")
		private Long effectID;
		@SerializedName(value = "is_default")
		private Boolean isDefault;

		public Long getEffectID() { return effectID; }
		public Boolean isDefault() { return isDefault; }
	}

	public class ProducedByBlueprints {
		@SerializedName(value = "blueprint_type_id")
		private Integer blueprintTypeID;
		@SerializedName(value = "blueprint_activity")
		private String blueprintActivity;

		public Integer getBlueprintTypeID() { return blueprintTypeID; }
		public String getBlueprintActivity() { return blueprintActivity; }
	}

	public class Traits {
		@SerializedName(value = "misc_bonuses")
		private Map<String, RoleBonus> miscBonuses;
		@SerializedName(value = "role_bonuses")
		private Map<String, RoleBonus> roleBonuses;
		private Map<String, Map<String, RoleBonus>> types;

		public Map<String, RoleBonus> getMiscBonuses() { return miscBonuses; }
		public Map<String, RoleBonus> getRoleBonuses() { return roleBonuses; }
		public Map<String, Map<String, RoleBonus>> getTypes() { return types; }
	}

	public class RoleBonus {
		private Double bonus;
		@SerializedName(value = "bonus_text")
		private Map<String, String> bonusText;
		private Long importance;
		@SerializedName(value = "is_positive")
		private Boolean positive;
		@SerializedName(value = "unit_id")
		private Long unitID;

		public Double getBonus() { return bonus; }
		public Map<String, String> getBonusText() { return bonusText; }
		public Long getImportance() { return importance; }
		public Boolean isPositive() { return positive; }
		public Long getUnitID() { return unitID; }
	}

	public class TypeMaterial {
		@SerializedName(value = "material_type_id")
		private Integer materialTypeID;
		private Integer quantity;

		public Integer getMaterialTypeID() { return materialTypeID; }
		public Integer getQuantity() { return quantity; }
	}
}