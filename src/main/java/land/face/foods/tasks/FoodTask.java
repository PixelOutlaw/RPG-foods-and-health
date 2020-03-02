package land.face.foods.tasks;

import land.face.foods.FoodsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class FoodTask extends BukkitRunnable {

  private FoodsPlugin plugin;

  public FoodTask(FoodsPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void run() {
    //every 30 seconds, get online player's foods scores and give them a counter.
    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
      plugin.getServer().getLogger().info("Food Task");
      if (player.getGameMode() != GameMode.SURVIVAL) {
        return;
      }
      UUID uuid = player.getUniqueId();

      //reduce player's nutrient stats

      plugin.getFoodsManager().getHealthStatus().get(uuid).setProtein(plugin.nutrientDecreaseRate);
      plugin.getFoodsManager().getHealthStatus().get(uuid).setCarbohydrates(plugin.nutrientDecreaseRate);
      plugin.getFoodsManager().getHealthStatus().get(uuid).setDairy(plugin.nutrientDecreaseRate);
      plugin.getFoodsManager().getHealthStatus().get(uuid).setProduce(plugin.nutrientDecreaseRate);

      //evaluate players nutrient stats

      int healthyStats = 0;

      //protein
      if (plugin.getFoodsManager().getHealthStatus().get(uuid).getProtein() < 20) {
        plugin.getFoodsManager().getHealthStatus().get(uuid).setHealthyBoi(-1);
      } else {
        healthyStats++;
      }

      //carbohydrates
      if (plugin.getFoodsManager().getHealthStatus().get(uuid).getCarbohydrates() < 20) {
        plugin.getFoodsManager().getHealthStatus().get(uuid).setHealthyBoi(-1);
      } else {
        healthyStats++;
      }

      //dairy
      if (plugin.getFoodsManager().getHealthStatus().get(uuid).getDairy() < 20) {
        plugin.getFoodsManager().getHealthStatus().get(uuid).setHealthyBoi(-1);
      } else {
        healthyStats++;
      }

      //vegetables
      if (plugin.getFoodsManager().getHealthStatus().get(uuid).getProduce() < 20) {
        plugin.getFoodsManager().getHealthStatus().get(uuid).setHealthyBoi(-1);
      } else {
        healthyStats++;
      }

      //healthscore
      if (healthyStats >= 3) {
        plugin.getFoodsManager().getHealthStatus().get(uuid).setHealthyBoi(1);
      }

      if (plugin.getFoodsManager().getHealthStatus().get(uuid).getHealthyBoi() >= 10) {
        //increase healthscore
        plugin.getFoodsManager().getHealthStatus().get(uuid).setHealthScore(1);
      } else if (plugin.getFoodsManager().getHealthStatus().get(uuid).getHealthyBoi() <= -10) {
        //decrease healthscore
        plugin.getFoodsManager().getHealthStatus().get(uuid).setHealthScore(-1);
      }
    }

    plugin.saveRPGFood();
  }
}
