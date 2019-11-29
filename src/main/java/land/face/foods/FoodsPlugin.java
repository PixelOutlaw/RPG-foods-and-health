package land.face.foods;

import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import land.face.foods.listener.FoodListener;
import land.face.strife.StrifePlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class FoodsPlugin extends FacePlugin {

  private MasterConfiguration settings;
  private VersionedSmartYamlConfiguration configYAML;
  private VersionedSmartYamlConfiguration langYAML;

  private StrifePlugin strifePlugin;

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
  }

  @Override
  public void disable() {
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
}
