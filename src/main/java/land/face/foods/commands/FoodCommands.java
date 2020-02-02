package land.face.foods.commands;

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

    if (command.getLabel().equalsIgnoreCase("RPGFood") && sender
        .hasPermission("foods.command.stats") && sender instanceof Player) {
      //RPGFood sub commands
      if (args.length == 0) {
        player.sendMessage(ChatColor.RED + "Invalid Arguments!");
        return true;
      }

      if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0] == null) {
        player.sendMessage(
            ChatColor.GREEN + "-=+=-" + ChatColor.GOLD + " RPG Foods & Health " + ChatColor.GREEN
                + "-=+=-");
        player.sendMessage(ChatColor.GRAY + "RPG Food and Health, we cookin' y'all");
        player.sendMessage(ChatColor.WHITE + "/RPGFood Check <Player> " + ChatColor.GRAY
            + "- Displays targets nutritional stats");
        player.sendMessage(
            ChatColor.WHITE + "/RPGFood SetNutrient <Player> <Nutrient> <Amount>" + ChatColor.GRAY
                + "- Sets target nutrient of a player");
        player.sendMessage(ChatColor.WHITE + "/RPGFood Reset <Player>" + ChatColor.GRAY
            + "- Resets targets nutritional stats");
        player
            .sendMessage(ChatColor.WHITE + "/RPGFood Reload" + ChatColor.GRAY + "- Reloads plugin");
        player.sendMessage(ChatColor.GREEN + "============================");
      } else if (args[0].equalsIgnoreCase("check") && args.length >= 2) {

        if (args[1] != null) {
          //show nutritional stats of given player
          Player playerCheck = plugin.getServer().getPlayer(args[1]);

          if (Bukkit.getServer().getOnlinePlayers().contains(playerCheck)) {
            UUID playerUUID = playerCheck.getUniqueId();
            player.sendMessage(ChatColor.GREEN + playerCheck.getName() + "'s Nutritional Stats:"
                + " Protein: " + plugin.healthStatus.get(playerUUID).getProtein()
                + " Dairy: " + plugin.healthStatus.get(playerUUID).getDairy()
                + " Carbohydrates: " + plugin.healthStatus.get(playerUUID).getCarbohydrates()
                + " Produce: " + plugin.healthStatus.get(playerUUID).getProduce()
                + " Health Score: " + plugin.healthStatus.get(playerUUID).getHealthScore()
                + " HealthyBoi: " + plugin.healthStatus.get(playerUUID).getHealthyBoi());
          } else {
            player.sendMessage(ChatColor.RED + "Player is not online or doesn't exist!");
          }

        } else {
          player.sendMessage(ChatColor.RED + "Correct usage /RPGFood Check <Player>");
        }
      } else if (args[0].equalsIgnoreCase("reload")) {
        plugin.disable();
        plugin.enable();
      } else if (args[0].equalsIgnoreCase("setnutrient")) {
        // /food setnutrient player nutrient int

        if (args[1] != null && args[2] != null && args[3] != null) {

          Player playerToAppend = plugin.getServer().getPlayer(args[1]);

          if (playerToAppend.isOnline()) {
            UUID playerUUID = playerToAppend.getUniqueId();

            if (plugin.isInt(args[3])) {
              int amount = Integer.parseInt(args[3]);

              switch (args[2].toLowerCase()) {
                case "protein":
                  plugin.healthStatus.get(playerUUID).hardSetProtein(amount);
                  player.sendMessage(ChatColor.GREEN + "Set Nutrient!");
                  break;
                case "dairy":
                  plugin.healthStatus.get(playerUUID).hardSetDairy(amount);
                  player.sendMessage(ChatColor.GREEN + "Set Nutrient!");
                  break;
                case "carbohydrates":
                  plugin.healthStatus.get(playerUUID).hardSetCarbohydrates(amount);
                  player.sendMessage(ChatColor.GREEN + "Set Nutrient!");
                  break;
                case "produce":
                  plugin.healthStatus.get(playerUUID).hardSetProduce(amount);
                  player.sendMessage(ChatColor.GREEN + "Set Nutrient!");
                  break;
                case "healthyboi":
                  plugin.healthStatus.get(playerUUID).hardSetHealthyBoi(amount);
                  player.sendMessage(ChatColor.GREEN + "Set HealthyBoi!");
                case "healthscore":
                  plugin.healthStatus.get(playerUUID).hardSetHealthScore(amount);
                  player.sendMessage(ChatColor.GREEN + "Set HealthScore!");
                  break;
              }
            } else {
              player.sendMessage(ChatColor.RED + "Incorrect Usage!");
            }

          } else {
            player.sendMessage(ChatColor.RED + "Specified player is not online!");
          }

        } else {
          player.sendMessage(ChatColor.RED + "Incorrect Usage!");
        }


      } else if (args[0].equalsIgnoreCase("reset")) {
        if (args[1] != null) {
          Player playerToReset = plugin.getServer().getPlayer(args[1]);
          plugin.healthStatus.get(playerToReset.getUniqueId()).resetNutrients();
          player.sendMessage(ChatColor.GREEN + "Reset: " + playerToReset.getName());
        } else {
          player.sendMessage(ChatColor.RED + "Incorrect Usage");
        }
      } else if (args[0].equalsIgnoreCase("statcheck")) {

        if (player.getItemInHand() != null) {
          if (player.getItemInHand().getItemMeta().hasCustomModelData() == true) {
            player.sendMessage("" + player.getItemInHand().getItemMeta().getCustomModelData());
          } else {
            player.sendMessage("no custom model data :(");
          }
        }

      }

    }

    return true;
  }
}
