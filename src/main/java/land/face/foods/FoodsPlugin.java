package land.face.foods;

import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

import land.face.foods.commands.FoodCommands;
import land.face.foods.listener.EatListener;
import land.face.foods.listener.FoodListener;
import land.face.foods.listener.CraftingListener;
import land.face.foods.managers.FoodsManager;
import land.face.foods.objects.NutrientType;
import land.face.foods.tasks.FoodTask;
import land.face.foods.objects.HealthStatus;
import land.face.strife.StrifePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class FoodsPlugin extends FacePlugin {

  private MasterConfiguration settings;
  private VersionedSmartYamlConfiguration configYAML;
  private VersionedSmartYamlConfiguration langYAML;

  private StrifePlugin strifePlugin;

  private FoodsManager foodsManager;

  private FoodTask foodTask = new FoodTask(this);

  public int nutrientDecreaseRate = 0;

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

    Bukkit.getLogger()
        .info("Successfully enabled HealthAndFoods-v" + getDescription().getVersion());

    //default config
    getConfig().options().copyDefaults(true);

    configYAML.addDefault("maxHealth", 0);
    configYAML.addDefault("minHealth", 0);
    configYAML.addDefault("consumptionCoolDown", 0);
    configYAML.addDefault("nutrientDecreaseRate", 0);

    saveConfig();

    nutrientDecreaseRate = configYAML.getInt("nutrientDecreaseRate", 30);

    foodsManager = new FoodsManager(this);

    //events
    Bukkit.getPluginManager().registerEvents(new FoodListener(this), this);
    Bukkit.getPluginManager().registerEvents(new CraftingListener(this), this);
    Bukkit.getPluginManager().registerEvents(new EatListener(this), this);

    //commands
    this.getCommand("RPGFood").setExecutor(new FoodCommands(this));

    //create rpg food file
    createRPGFoodFile();
    foodsManager.loadFoodObjects(new File(getDataFolder(), "RPGFoods.yml"));

    //load online players incase of a reload
    for (Player player : Bukkit.getOnlinePlayers()) {
      File fileName = new File(getDataFolder() + "/data", player.getUniqueId().toString() + ".yml");
      if (!fileName.exists()) {
        createPlayerFile(player.getUniqueId());
        return;
      }
      //plugin.getServer().getLogger().info("Filename : " + fileName.getName() + "player: " + player.getName());
      foodsManager.loadPlayerFile(fileName, player.getUniqueId());
    }

    // foodtask
    foodTask.runTaskTimer(this,
        20L * 30, // Start save after 30seconds
        20L * 30 // Run every 30 seconds after that
    );

  }

  @Override
  public void disable() {

    saveRPGFood();

    HandlerList.unregisterAll(this);
    Bukkit.getScheduler().cancelTasks(this);
  }

  public void saveRPGFood() {
    //save method, save all to file.
    this.getServer().getLogger().info("Saving");
    //loop through all players uuid files and save their healthyboi map to it
    for (Map.Entry<UUID, HealthStatus> player : foodsManager.getHealthStatus().entrySet()) {

      File fileName = new File(this.getDataFolder() + "/data", player.getKey().toString() + ".yml");
      FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(fileName);

      playerConfig.set("healthyBoi", player.getValue().getHealthyBoi());
      playerConfig.set(NutrientType.PROTEIN.getDataName(), player.getValue().getProtein());
      playerConfig.set(NutrientType.DAIRY.getDataName(), player.getValue().getDairy());
      playerConfig.set(NutrientType.CARBOHYDRATE.getDataName(), player.getValue().getCarbohydrates());
      playerConfig.set(NutrientType.PRODUCE.getDataName(), player.getValue().getProduce());
      playerConfig.set("healthScore", player.getValue().getHealthScore());
      try {
        playerConfig.save(fileName);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void createRPGFoodFile() {
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
      playerConfig.set(NutrientType.DAIRY.getDataName(), 0);
      playerConfig.set(NutrientType.CARBOHYDRATE.getDataName(), 0);
      playerConfig.set(NutrientType.PROTEIN.getDataName(), 0);
      playerConfig.set(NutrientType.PRODUCE.getDataName(), 0);
      playerConfig.set("healthScore", 0);

      try {
        playerConfig.save(fileName);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public StrifePlugin getStrifePlugin() {
    return strifePlugin;
  }

  private VersionedSmartYamlConfiguration defaultSettingsLoad(String name) {
    return new VersionedSmartYamlConfiguration(new File(getDataFolder(), name),
        getResource(name), VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
  }

  public FoodsManager getFoodsManager() {
    return foodsManager;
  }

  public MasterConfiguration getSettings() {
    return settings;
  }

  public static boolean isInt(String s) {
    try {
      Integer.parseInt(s);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }
}
