/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package land.face.foods.listener;

import land.face.foods.FoodsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;

import static org.bukkit.Bukkit.getServer;

public class FoodListener implements Listener {

  private final FoodsPlugin plugin;

  public FoodListener(FoodsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onInteractEvent(PlayerInteractEvent event) {

    //when the player interacts
    Player player = event.getPlayer();

    if (player.getItemInHand().getType() != Material.AIR){
      //Material heldMaterial = player.getItemInHand().getType();

      ItemStack heldItem = player.getItemInHand();
      ItemMeta heldItemMeta = heldItem.getItemMeta();
      String heldItemName = heldItemMeta.getDisplayName();
      heldItemName = ChatColor.stripColor(heldItemName);
      //plugin.getServer().getLogger().info("Held Item: " + heldItemName);

      //check if player is on a food cooldown
      if (plugin.rpgFoods.containsKey(heldItemName) && plugin.checkPlayerCoolDown(player.getUniqueId())){
        if (!heldItemMeta.hasCustomModelData()){
          return;
        }
        int customDataValue = plugin.rpgFoods.get(heldItemName).getCustomData();

        //separate the characters in the custom model data

        if (customDataValue == heldItemMeta.getCustomModelData()){
          //reset the player's cooldown
          plugin.globalFoodCoolDown.put(player.getUniqueId(), System.currentTimeMillis() + plugin.consumptionCoolDown);
          //player consumes food
          event.setCancelled(true);
          plugin.consumeFood(player.getUniqueId(), plugin.rpgFoods.get(heldItemName), heldItem);
          heldItem.setAmount(heldItem.getAmount() - 1);

          //plugin.getServer().getLogger().info("Cooldown: " + plugin.globalFoodCoolDown.get(player.getUniqueId()));
        }
      } else if (plugin.rpgFoods.containsKey(heldItemName)){
        if (!heldItemMeta.hasCustomModelData()){
          return;
        }
        if (plugin.rpgFoods.get(heldItemName).getCustomData() == heldItemMeta.getCustomModelData()){
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event){
    //when a player joins
    Player player = event.getPlayer();

    File fileName = new File(plugin.getDataFolder() + "/data", player.getUniqueId().toString() + ".yml");
    if (!fileName.exists()) {
      plugin.createPlayerFile(player.getUniqueId());
      return;
    }
    //plugin.getServer().getLogger().info("Filename : " + fileName.getName() + "player: " + player.getName());
    plugin.loadPlayerFile(fileName, player.getUniqueId());
  }

  
}