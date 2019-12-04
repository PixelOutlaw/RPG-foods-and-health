package land.face.foods;

import com.sun.javafx.collections.MappingChange;
import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

import land.face.foods.listener.FoodCommands;
import land.face.foods.listener.FoodListener;
import land.face.foods.managers.FoodTask;
import land.face.foods.managers.HealthStatus;
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

  //nutrient maps tracker
  public Map<UUID, HealthStatus> healthStatus = new HashMap<UUID, HealthStatus>();

  //tasks
  FoodTask foodTask = new FoodTask(this);

  FileConfiguration config = this.getConfig();

  //loading config data
  public int maxHealth = 0;
  public int minHealth = 0;

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

}
