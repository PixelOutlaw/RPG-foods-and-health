package land.face.foods.listener;

import land.face.foods.FoodsPlugin;
import land.face.foods.objects.NutrientType;
import land.face.foods.objects.RPGFoods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CraftingListener implements Listener {

  private final FoodsPlugin plugin;

  public CraftingListener(FoodsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCraftItemEvent(CraftItemEvent event) {

    Bukkit.getServer().getLogger()
        .info(event.getRecipe().getResult().getItemMeta().getDisplayName());
    Bukkit.getServer().getLogger().info(event.getRecipe().getResult().toString());

    ItemStack craftedItem = event.getRecipe().getResult();
    ItemMeta craftedItemMeta = craftedItem.getItemMeta();

    List<String> lore = new ArrayList<>();

    String iName = ChatColor.stripColor(craftedItem.getItemMeta().getDisplayName());
    //String craftedItemName = craftedItem.getItemMeta().getDisplayName();
    Bukkit.getServer().getLogger()
        .info("Crafted Display Name: " + craftedItem.getItemMeta().getDisplayName());

    RPGFoods food = plugin.getFoodsManager().getFoodFromDisplay(iName);
    if (food == null) {
      Bukkit.getServer().getLogger().info("Food not found dawg");
      return;
    }

    Bukkit.getServer().getLogger().info("crafted item name: " + iName +
        " rpgfood: " + food.getFoodName());

    if (food.getFoodName().equals(iName)) {
      for (Map.Entry<NutrientType, Integer> nutrients : food.getNutrients().entrySet()) {
        int value = nutrients.getValue();
        String symbol;
        ChatColor chatColor;

        if (value < 0) {
          symbol = "";
          chatColor = ChatColor.RED;

        } else {
          symbol = "+";
          chatColor = ChatColor.GREEN;
        }

        lore.add(chatColor + symbol + value + " " + nutrients.getKey().toString());
      }

      craftedItemMeta.setLore(lore);
      //craftedItemMeta.setCustomModelData(intForFirst4Char);
      craftedItem.setItemMeta(craftedItemMeta);
    }
  }
}

  /*
        if (!event.getRecipe().getResult().getItemMeta().hasCustomModelData()){
            plugin.getServer().getLogger().info("custom data not found on craft");
            return;
        }*/

//total int for custom data value
        /*int customDataValue = event.getRecipe().getResult().getItemMeta().getCustomModelData();

        //separate the characters in the custom model data
        String customDataValueString = Integer.toString(customDataValue);
        String first4char = customDataValueString.substring(0,4);
        String lastChar = customDataValueString.substring(5);

        plugin.getServer().getLogger().info(customDataValueString);
        plugin.getServer().getLogger().info(first4char + " " + lastChar);


        int intForFirst4Char = Integer.parseInt(first4char);
        int intLastChar = Integer.parseInt(lastChar);

        ItemStack craftedItem = event.getRecipe().getResult();
        ItemMeta craftedItemMeta = craftedItem.getItemMeta();*/
