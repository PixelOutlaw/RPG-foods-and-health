package land.face.foods.managers;

import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import land.face.foods.FoodsPlugin;
import land.face.foods.objects.HealthStatus;
import land.face.foods.objects.NutrientType;
import land.face.foods.objects.RPGFoods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FoodsManager {

  private FoodsPlugin plugin;

  private Map<UUID, HealthStatus> healthStatus = new HashMap<>();
  private Map<String, RPGFoods> rpgFoods = new HashMap<>();

  private double maxHealth;
  private double minHealth;

  public FoodsManager(FoodsPlugin plugin) {
    this.plugin = plugin;
    maxHealth = plugin.getSettings().getDouble("config.max-health-multiplier", 1.25);
    minHealth = plugin.getSettings().getDouble("config.min-health-multiplier", 0.8);
  }

  public RPGFoods getFoods(String foodName) {
    return rpgFoods.get(foodName);
  }

  public Map<UUID, HealthStatus> getHealthStatus() {
    return healthStatus;
  }

  public void consumeFood(Player player, RPGFoods food) {
    // CHANGE TO READ NUTRIENTS FROM LORE
    Map<NutrientType, Integer> itemsNutrients = getItemStats(null);
    Bukkit.getLogger().info(itemsNutrients.toString());

    //nutrients
    for (Map.Entry<NutrientType, Integer> nutrients : itemsNutrients.entrySet()){
      Bukkit.getLogger().info(nutrients.getKey() + " " + nutrients.getValue() );
      switch(nutrients.getKey()){
        case PROTEIN:
          healthStatus.get(player.getUniqueId()).setProtein(nutrients.getValue());
          break;
        case DAIRY:
          healthStatus.get(player.getUniqueId()).setDairy(nutrients.getValue());
          break;
        case CARBOHYDRATE:
          healthStatus.get(player.getUniqueId()).setCarbohydrates(nutrients.getValue());
          break;
        case PRODUCE:
          healthStatus.get(player.getUniqueId()).setProduce(nutrients.getValue());
          break;
      }
    }

    //potion effects
    for (PotionEffect potions : food.getPotionEffects()){
      player.addPotionEffect(potions);
      //getServer().getLogger().info(potions.toString());
    }

    //strife buffs
    for (String buff : food.getStrifeBuffs()){

    }

    //health restored
    double maxPlayerHealth = player.getMaxHealth();

    if (player.getHealth() + food.getHealthRestored() >= maxPlayerHealth){
      player.setHealth(player.getMaxHealth());
    } else {
      player.setHealth(player.getHealth() + food.getHealthRestored());
    }

    //food restored
    int maxPlayerFood = 20;
    if (player.getFoodLevel() + food.getFoodRestored() > maxPlayerFood){
      player.setFoodLevel(20);
    } else {
      player.setFoodLevel(player.getFoodLevel() + food.getFoodRestored());
    }
  }

  public void consumeFood(UUID uuid, RPGFoods food, ItemStack itemStack) {
    consumeFood(Bukkit.getPlayer(uuid), food);
  }

  public double getHealthMultiplier(UUID uuid) {
    return minHealth + (maxHealth - minHealth) * (healthStatus.get(uuid).getHealthScore() / 100D);
  }

  public Map<NutrientType, Integer> getItemStats(ItemStack stack) {
    return getItemStats(stack, 1);
  }

  public Map<NutrientType, Integer> getItemStats(ItemStack stack, Integer multiplier) {
    if (stack == null || stack.getType() == Material.AIR) {
      return new HashMap<>();
    }
    Map<NutrientType, Integer> itemStats = new HashMap<>();

    List<String> lore = ItemStackExtensionsKt.getLore(stack);
    if (lore.isEmpty()) {
      return itemStats;
    }
    List<String> strippedLore = (lore);

    for (String s : strippedLore) {
      Integer amount = 0;
      String retained = CharMatcher.forPredicate(Character::isLetter).or(CharMatcher.is(' '))
          .retainFrom(s).trim();
      NutrientType attribute = NutrientType.fromName(retained);
      if (attribute == null) {
        continue;
      }
      amount += NumberUtils.toInt(CharMatcher.digit().or(CharMatcher.is('-')).retainFrom(s));
      if (amount == 0) {
        continue;
      }

      if (itemStats.containsKey(attribute)) {
        amount += itemStats.get(attribute);
      }
      itemStats.put(attribute, amount);
    }
    return itemStats;
  }

  public RPGFoods getFoodFromDisplay(String displayName) {
    for (RPGFoods foods : rpgFoods.values()) {
      if (displayName.equals(foods.getFoodName())) {
        return foods;
      }
    }
    return null;
  }

  public void loadFoodObjects(File rpgFoodFile) {
    FileConfiguration foodConfig = YamlConfiguration.loadConfiguration(rpgFoodFile);
    for (String mainKey : foodConfig.getKeys(false)) {

      for (String key : foodConfig.getConfigurationSection(mainKey).getKeys(false)) {

        RPGFoods food = new RPGFoods();
        food.setFoodName(foodConfig.getString(mainKey + "." + key + ".display-name"));
        food.setFoodItem(foodConfig.getString(mainKey));
        food.setCustomData(foodConfig.getInt(mainKey + "." + key + ".custom-data"));
        food.setFoodRestored(foodConfig.getInt(mainKey + "." + key + ".food-restored"));
        food.setHealthRestored(foodConfig.getInt(mainKey + "." + key + ".health-restored"));

        for (String strifeBuffs : foodConfig
            .getStringList(mainKey + "." + key + ".buffs-applied")) {
          if (strifeBuffs == null) {
            return;
          }
          food.setStrifeBuffs(strifeBuffs);
        }

        for (String nutrient : foodConfig
            .getConfigurationSection(mainKey + "." + key + ".nutrients").getKeys(false)) {
          //getServer().getLogger().info(nutrients);
          NutrientType type;
          try {
            type = NutrientType.valueOf(nutrient.toUpperCase());
          } catch (Exception e) {
            Bukkit.getServer().getLogger().warning("Failed to load unknown nutrient: " + nutrient);
            continue;
          }
          food.getNutrients()
              .put(type, foodConfig.getInt(mainKey + "." + key + ".nutrients" + "." + nutrient));
        }

        if (foodConfig.getConfigurationSection(mainKey + "." + key + ".potion-effects") != null) {

          for (String potions : foodConfig
              .getConfigurationSection(mainKey + "." + key + ".potion-effects").getKeys(false)) {

            //String potionType = foodConfig.getString(mainKey + "." + key + ".potion-effects." + potions);
            PotionEffectType potionEffectType = PotionEffectType.getByName(potions);

            PotionEffect potion = new PotionEffect(potionEffectType,
                foodConfig.getInt(mainKey + "." + key + ".potion-effects." + potions + ".duration"),
                foodConfig
                    .getInt(mainKey + "." + key + ".potion-effects." + potions + ".intensity"));
            food.setPotionEffects(potion);
          }
        }
        rpgFoods.put(food.getFoodName(), food);
      }
    }
  }

  public void loadPlayerFile(File file, UUID uuid) {
    //create a new instance of healthstatus and add it to the health status map uuid,healthstatus
    //set values in the new health status
    FileConfiguration playerConfig2 = YamlConfiguration.loadConfiguration(file);

    HealthStatus HS = new HealthStatus();

    HS.setUUID(uuid);
    HS.setProtein(playerConfig2.getInt(NutrientType.PROTEIN.getDataName()));
    HS.setCarbohydrates(playerConfig2.getInt(NutrientType.CARBOHYDRATE.getDataName()));
    HS.setDairy(playerConfig2.getInt(NutrientType.DAIRY.getDataName()));
    HS.setProduce(playerConfig2.getInt(NutrientType.PRODUCE.getDataName()));
    HS.setHealthScore(playerConfig2.getInt("healthScore"));
    HS.setHealthyBoi(playerConfig2.getInt("healthyBoi"));

    healthStatus.put(uuid, HS);
  }

  private List<String> stripColor(List<String> strings) {
    List<String> stripped = new ArrayList<>();
    for (String s : strings) {
      stripped.add(ChatColor.stripColor(s));
    }
    return stripped;
  }

}
