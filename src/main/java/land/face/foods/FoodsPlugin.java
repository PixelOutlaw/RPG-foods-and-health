package land.face.foods;

import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

import land.face.foods.listener.FoodCommands;
import land.face.foods.listener.FoodListener;
import land.face.foods.tasks.FoodTask;
import land.face.foods.objects.HealthStatus;
import land.face.foods.objects.RPGFoods;
import land.face.strife.StrifePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
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

  //tasks
  FoodTask foodTask = new FoodTask(this);

  FileConfiguration config = this.getConfig();

  //loading config data
  public int maxHealth = 0;
  public int minHealth = 0;

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
      playerConfig.set("vegetables", 0);
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
      playerConfig.set("protein", player.getValue().getProtein());
      playerConfig.set("dairy", player.getValue().getDairy());
      playerConfig.set("carbohydrates", player.getValue().getCarbohydrates());
      playerConfig.set("vegetables", player.getValue().getVegetables());
      playerConfig.set("healthScore", player.getValue().getHealthScore());
      try {
        playerConfig.save(fileName);
      } catch (IOException e) {
        e.printStackTrace();
      }    }
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

    saveConfig();

    maxHealth = config.getInt("maxHealth");
    minHealth = config.getInt("minHealth");

    //events
    getServer().getPluginManager().registerEvents(new FoodListener(this), this);

    //commands
    this.getCommand("RPGFood").setExecutor(new FoodCommands(this));

    //create rpg food file
    createRPGFoodFile();
    loadFoodObjects();

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
    HS.setProtein(playerConfig2.getInt("protein"));
    HS.setCarbohydrates(playerConfig2.getInt("carbohydrates"));
    HS.setDairy(playerConfig2.getInt("dairy"));
    HS.setVegetables(playerConfig2.getInt("vegetables"));
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
          food.setStrifeBuffs(strifeBuffs);
        }

        for (String nutrients : foodConfig.getConfigurationSection(mainKey + "." + key + ".nutrients").getKeys(false)){
          food.setNutrients(nutrients, foodConfig.getInt(mainKey + "." + key + ".nutrients" + "." + nutrients));
        }

        for (String potions : foodConfig.getConfigurationSection(mainKey + "." + key + ".potion-effects").getKeys(false)){

          //String potionType = foodConfig.getString(mainKey + "." + key + ".potion-effects." + potions);
          PotionEffectType potionEffectType = PotionEffectType.getByName(potions);

          PotionEffect potion = new PotionEffect(potionEffectType,
                  foodConfig.getInt(mainKey + "." + key +".potion-effects." + potions +".intensity"),
                  foodConfig.getInt(mainKey + "." + key +".potion-effects." + potions +".duration"));
          food.setPotionEffects(potion);
        }



        rpgFoods.put(food.getFoodName(), food);
        //getServer().getLogger().info(food.getFoodName() + food.getNutrients() + food.getPotionEffects() +"");

      }

    }



  }

}
