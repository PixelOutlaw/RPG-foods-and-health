package land.face.foods.managers;

import land.face.foods.FoodsPlugin;
import org.bukkit.Bukkit;
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
            UUID uuid = player.getUniqueId();

            int healthyStats = 0;

            //protein
            if (plugin.healthStatus.get(uuid).getProtein() < 20){
                plugin.healthStatus.get(uuid).setHealthyBoi(-1);
            } else {
                healthyStats++;
            }

            //carbohydrates
            if (plugin.healthStatus.get(uuid).getCarbohydrates() < 20){
                plugin.healthStatus.get(uuid).setHealthyBoi(-1);
            } else {
                healthyStats++;
            }

            //dairy
            if (plugin.healthStatus.get(uuid).getDairy() < 20){
                plugin.healthStatus.get(uuid).setHealthyBoi(-1);
            } else {
                healthyStats++;
            }

            //vegetables
            if (plugin.healthStatus.get(uuid).getVegetables() < 20){
                plugin.healthStatus.get(uuid).setHealthyBoi(-1);
            } else {
                healthyStats++;
            }

            //healthscore
            if (healthyStats >= 3){
                plugin.healthStatus.get(uuid).setHealthyBoi(1);
            }

            if (plugin.healthStatus.get(uuid).getHealthyBoi() >= 10){
                //increase healthscore
                plugin.healthStatus.get(uuid).setHealthScore(1);
            } else if(plugin.healthStatus.get(uuid).getHealthyBoi() <= -10){
                //decrease healthscore
                plugin.healthStatus.get(uuid).setHealthScore(-1);

            }
        }

        plugin.saveRPGFood();
    }
}
