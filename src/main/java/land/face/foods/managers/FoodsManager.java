package land.face.foods.managers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import land.face.foods.FoodsPlugin;
import land.face.foods.objects.NutrientType;
import land.face.foods.objects.RPGFoods;
import land.face.strife.data.buff.LoadedBuff;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FoodsManager {

  private FoodsPlugin plugin;

  private Map<String, RPGFoods> rpgFoods = new HashMap<>();

  public FoodsManager(FoodsPlugin plugin) {
    this.plugin = plugin;
  }

  public RPGFoods getFoods(String foodName) {
    return rpgFoods.get(foodName);
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
}
