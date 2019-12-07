package land.face.foods.listener;

import land.face.foods.FoodsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FoodCommands implements CommandExecutor {

    private FoodsPlugin plugin;

    public FoodCommands(FoodsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        if (command.getLabel().equalsIgnoreCase("RPGFood") && sender.hasPermission("foods.command.stats") && sender instanceof Player) {
            //RPGFood sub commands
            if (args.length == 0){
                player.sendMessage(ChatColor.RED + "Invalid Arguments!");
                return true;
            }

            if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0] == null) {
                player.sendMessage(ChatColor.GREEN + "-=+=-" + ChatColor.GOLD + " RPG Foods & Health " + ChatColor.GREEN + "-=+=-");
                player.sendMessage(ChatColor.GRAY + "RPG Food and Health, we cookin' y'all");
                player.sendMessage(ChatColor.WHITE + "/RPGFood Check <Player> " + ChatColor.GRAY + "- Displays targets nutritional stats");
                player.sendMessage(ChatColor.GREEN + "============================");
            } else if (args[0].equalsIgnoreCase("check") && args.length >= 2){

                if (args[1] != null){
                    //show nutritional stats of given player
                    Player playerCheck = plugin.getServer().getPlayer(args[1]);

                    if (Bukkit.getServer().getOnlinePlayers().contains(playerCheck)){
                        UUID playerUUID = playerCheck.getUniqueId();
                        player.sendMessage(ChatColor.GREEN + playerCheck.getName() + "'s Nutritional Stats:"
                                + " Protein: " + plugin.healthStatus.get(playerUUID).getProtein()
                                + " Dairy: " + plugin.healthStatus.get(playerUUID).getDairy()
                                + " Carbohydrates: " + plugin.healthStatus.get(playerUUID).getCarbohydrates()
                                + " Vegetables: " + plugin.healthStatus.get(playerUUID).getVegetables()
                                + " Health Score: " + plugin.healthStatus.get(playerUUID).getHealthScore()
                                + " HealthyBoi: " + plugin.healthStatus.get(playerUUID).getHealthyBoi());
                    }  else {
                        player.sendMessage(ChatColor.RED + "Player is not online or doesn't exist!");
                    }

                } else {
                    player.sendMessage(ChatColor.RED + "Correct usage /RPGFood Check <Player>");
                }
            } else if (args[0].equalsIgnoreCase("reload")){
                plugin.disable();
                plugin.enable();
            }

        }

        return true;
    }
}
