package land.face.foods;

import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import land.face.foods.listener.FoodCommands;
import land.face.foods.listener.FoodListener;
import land.face.foods.managers.Crafting;
import land.face.foods.objects.NutrientType;
import land.face.foods.tasks.FoodTask;
import land.face.foods.objects.HealthStatus;
import land.face.foods.objects.RPGFoods;
import land.face.strife.StrifePlugin;
import land.face.strife.data.buff.LoadedBuff;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FoodsPlugin extends FacePlugin {

  private MasterConfiguration settings;
  private VersionedSmartYamlConfiguration configYAML;
  private VersionedSmartYamlConfiguration langYAML;

  private StrifePlugin strifePlugin;

  //nutrient maps tracker
  public Map<UUID, HealthStatus> healthStatus = new HashMap<UUID, HealthStatus>();
  //rpg foods map
  public Map<String, RPGFoods> rpgFoods = new HashMap<String, RPGFoods>();
  //global food cooldown
  public Map<UUID, Long> globalFoodCoolDown = new HashMap<UUID, Long>();

  //tasks
  FoodTask foodTask = new FoodTask(this);

  FileConfiguration config = this.getConfig();

  public final String proteinString = "protein";
  public final String dairyString = "dairy";
  public final String carbohydrateString = "carbohydrates";
  public final String produceString = "produce";

  //loading config data
  public int maxHealth = 0;
  public int minHealth = 0;
  public int consumptionCoolDown = 0;
  public int nutrientDecreaseRate = 0;

  public void createRPGFoodFile(){
    File rpgFoodFile = new File(getDataFolder(), "RPGFoods.yml");

    if (!rpgFoodFile.exists()) {
      try {
        rpgFoodFile.createNewFile();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

  }


  public void createPlayerFile(UUID uuid) {

    File fileName = new File(getDataFolder() + "/data", uuid + ".yml");

    if (!fileName.exists()) {
      try {
        fileName.createNewFile();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(fileName);

      try {
        playerConfig.save(fileName);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      playerConfig.set("healthyBoi", 0);
      playerConfig.set("protein", 0);
      playerConfig.set("dairy", 0);
      playerConfig.set("carbohydrates", 0);
      playerConfig.set("produce", 0);
      playerConfig.set("healthScore", 0);

      try {
        playerConfig.save(fileName);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    //set defaults to meats, grains, vegetables, dairy

  }

  public void saveRPGFood(){
    //save method, save all to file.
    this.getServer().getLogger().info("Saving");
    //loop through all players uuid files and save their healthyboi map to it
    for (Map.Entry<UUID, HealthStatus> player : healthStatus.entrySet()) {

      File fileName = new File(this.getDataFolder() + "/data", player.getKey().toString() + ".yml");
      FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(fileName);

      playerConfig.set("healthyBoi", player.getValue().getHealthyBoi());
      playerConfig.set(proteinString, player.getValue().getProtein());
      playerConfig.set(dairyString, player.getValue().getDairy());
      playerConfig.set(carbohydrateString, player.getValue().getCarbohydrates());
      playerConfig.set(produceString, player.getValue().getProduce());
      playerConfig.set("healthScore", player.getValue().getHealthScore());
      try {
        playerConfig.save(fileName);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void enable() {

    List<VersionedSmartYamlConfiguration> configurations = new ArrayList<>();
    configurations.add(configYAML = defaultSettingsLoad("config.yml"));
    configurations.add(langYAML = defaultSettingsLoad("language.yml"));

    for (VersionedSmartYamlConfiguration config : configurations) {
      if (config.update()) {
        getLogger().info("Updating " + config.getFileName());
      }
    }

    settings = MasterConfiguration.loadFromFiles(configYAML, langYAML);

    strifePlugin = StrifePlugin.getInstance();

    Bukkit.getPluginManager().registerEvents(new FoodListener(this), this);

    Bukkit.getLogger().info("Successfully enabled HealthAndFoods-v" + getDescription().getVersion());

    //default config
    getConfig().options().copyDefaults(true);

    config.addDefault("maxHealth", 0);
    config.addDefault("minHealth", 0);
    config.addDefault("consumptionCoolDown", 0);
    config.addDefault("nutrientDecreaseRate", 0);

    saveConfig();

    maxHealth = config.getInt("maxHealth");
    minHealth = config.getInt("minHealth");
    consumptionCoolDown = config.getInt("consumptionCoolDown");
    nutrientDecreaseRate = config.getInt("nutrientDecreaseRate");


    //events
    getServer().getPluginManager().registerEvents(new FoodListener(this), this);
    getServer().getPluginManager().registerEvents(new Crafting(this), this);

    //commands
    this.getCommand("RPGFood").setExecutor(new FoodCommands(this));

    //create rpg food file
    createRPGFoodFile();
    loadFoodObjects();

    //load online players incase of a reload
    for (Player player : Bukkit.getOnlinePlayers()){
      File fileName = new File(getDataFolder() + "/data", player.getUniqueId().toString() + ".yml");
      if (!fileName.exists()) {
        createPlayerFile(player.getUniqueId());
        return;
      }
      //plugin.getServer().getLogger().info("Filename : " + fileName.getName() + "player: " + player.getName());
      loadPlayerFile(fileName, player.getUniqueId());
    }

    //foodtask
    foodTask.runTaskTimer(this,
            20L  * 30, // Start save after 30seconds
            20L * 30 // Run every 30 seconds after that
    );

  }

  @Override
  public void disable() {

    saveRPGFood();

    HandlerList.unregisterAll(this);
    Bukkit.getScheduler().cancelTasks(this);
  }

  public StrifePlugin getStrifePlugin() {
    return strifePlugin;
  }

  private VersionedSmartYamlConfiguration defaultSettingsLoad(String name) {
    return new VersionedSmartYamlConfiguration(new File(getDataFolder(), name),
        getResource(name), VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
  }


  public void loadPlayerFile(File file, UUID uuid){
    //create a new instance of healthstatus and add it to the health status map uuid,healthstatus
    //set values in the new health status
    FileConfiguration playerConfig2 = YamlConfiguration.loadConfiguration(file);

    HealthStatus HS = new HealthStatus();

    HS.setUUID(uuid);
    HS.setProtein(playerConfig2.getInt(proteinString));
    HS.setCarbohydrates(playerConfig2.getInt(carbohydrateString));
    HS.setDairy(playerConfig2.getInt(dairyString));
    HS.setProduce(playerConfig2.getInt(produceString));
    HS.setHealthScore(playerConfig2.getInt("healthScore"));
    HS.setHealthyBoi(playerConfig2.getInt("healthyBoi"));

    healthStatus.put(uuid, HS);
  }

  public int getHealthMultiplier(UUID uuid){
    int healthMultiplier = minHealth + (maxHealth - minHealth) * (healthStatus.get(uuid).getHealthScore() / 100);
    return healthMultiplier;
  }

  public void loadFoodObjects(){
    //load in food objects :(

    File rpgFoodFile = new File(getDataFolder(), "RPGFoods.yml");

    FileConfiguration foodConfig = YamlConfiguration.loadConfiguration(rpgFoodFile);

    for (String mainKey : foodConfig.getKeys(false)){

      for (String key : foodConfig.getConfigurationSection(mainKey).getKeys(false)){

        RPGFoods food = new RPGFoods();

        food.setFoodName(foodConfig.getString(mainKey + "." + key +".display-name"));
        food.setFoodItem(foodConfig.getString(mainKey));
        food.setCustomData(foodConfig.getInt(mainKey + "." + key +".custom-data"));
        food.setFoodRestored(foodConfig.getInt(mainKey + "." + key +".food-restored"));
        food.setHealthRestored(foodConfig.getInt(mainKey + "." + key +".health-restored"));

        for (String strifeBuffs : foodConfig.getStringList(mainKey + "." + key + ".buffs-applied")){
          if (strifeBuffs == null){
            return;
          }
          food.setStrifeBuffs(strifeBuffs);
        }

        for (String nutrient : foodConfig.getConfigurationSection(mainKey + "." + key + ".nutrients").getKeys(false)){
          //getServer().getLogger().info(nutrients);
          NutrientType type;
          try {
            type = NutrientType.valueOf(nutrient.toUpperCase());
          } catch (Exception e) {
            getServer().getLogger().warning("Failed to load unknown nutrient: " + nutrient);
            continue;
          }
          food.getNutrients().put(type, foodConfig.getInt(mainKey + "." + key + ".nutrients" + "." + nutrient));
        }

        if (foodConfig.getConfigurationSection(mainKey + "." + key + ".potion-effects") != null){

          for (String potions: foodConfig.getConfigurationSection(mainKey + "." + key + ".potion-effects").getKeys(false)){


            //String potionType = foodConfig.getString(mainKey + "." + key + ".potion-effects." + potions);
            PotionEffectType potionEffectType = PotionEffectType.getByName(potions);

            PotionEffect potion = new PotionEffect(potionEffectType,
                    foodConfig.getInt(mainKey + "." + key +".potion-effects." + potions +".duration"),
                    foodConfig.getInt(mainKey + "." + key +".potion-effects." + potions +".intensity"));
            food.setPotionEffects(potion);
          }

        }




        rpgFoods.put(food.getFoodName(), food);
        //getServer().getLogger().info(food.getFoodName());

      }

    }



  }

  public boolean checkPlayerCoolDown(UUID uuid){

    if (globalFoodCoolDown.containsKey(uuid)){

      if (globalFoodCoolDown.get(uuid) <= System.currentTimeMillis()){
        return true;
      } else {
        return false;
      }
    } else {
      return true;
    }
  }

  public void consumeFood(UUID uuid, RPGFoods rpgFoods, ItemStack itemStack){

    //CHANGE TO READ NUTRIENTS FROM LORE
    Map<NutrientType, Integer> itemsNutrients = getItemStats(itemStack);
    getServer().getLogger().info(itemsNutrients.toString());
    //apply food's stats and buffs to the player
    Player player = Bukkit.getPlayer(uuid);


    //nutrients
    for (Map.Entry<NutrientType, Integer> nutrients : itemsNutrients.entrySet()){
      getServer().getLogger().info(nutrients.getKey() + " " + nutrients.getValue() );
      switch(nutrients.getKey()){
        case PROTEIN:
          healthStatus.get(uuid).setProtein(nutrients.getValue());
          break;
        case DAIRY:
          healthStatus.get(uuid).setDairy(nutrients.getValue());
          break;
        case CARBOHYDRATE:
          healthStatus.get(uuid).setCarbohydrates(nutrients.getValue());
          break;
        case PRODUCE:
          healthStatus.get(uuid).setProduce(nutrients.getValue());
          break;
      }
    }

    //potion effects
    for (PotionEffect potions : rpgFoods.getPotionEffects()){
      player.addPotionEffect(potions);
      //getServer().getLogger().info(potions.toString());
    }

    //strife buffs
    for (String buff : rpgFoods.getStrifeBuffs()){

    }

    //health restored
    double maxPlayerHealth = player.getMaxHealth();

    if (player.getHealth() + rpgFoods.getHealthRestored() >= maxPlayerHealth){
      player.setHealth(player.getMaxHealth());
    } else {
      player.setHealth(player.getHealth() + rpgFoods.getHealthRestored());
    }

    //food restored
    int maxPlayerFood = 20;
    if (player.getFoodLevel() + rpgFoods.getFoodRestored() > maxPlayerFood){
      player.setFoodLevel(20);
    } else {
      player.setFoodLevel(player.getFoodLevel() + rpgFoods.getFoodRestored());
    }
  }

  public static boolean isInt(String s) {
    try {
      Integer.parseInt(s);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }

  //STAT READING
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
    List<String> strippedLore = stripColor(lore);

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

  private List<String> stripColor(List<String> strings) {
    List<String> stripped = new ArrayList<>();
    for (String s : strings) {
      stripped.add(ChatColor.stripColor(s));
    }
    return stripped;
  }


}
