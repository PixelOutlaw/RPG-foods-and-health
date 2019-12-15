package land.face.foods.managers;

import land.face.foods.FoodsPlugin;
import land.face.foods.objects.NutrientType;
import land.face.foods.objects.RPGFoods;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Crafting implements Listener {

    private final FoodsPlugin plugin;

    public Crafting(FoodsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){

        for (Map.Entry<String, RPGFoods> foodEntry : plugin.rpgFoods.entrySet()){

            if (!event.getRecipe().getResult().getItemMeta().hasCustomModelData()){
                return;
            }

            //total int for custom data value
            int customDataValue = foodEntry.getValue().getCustomData();

            //separate the characters in the custom model data
            String customDataValueString = Integer.toString(customDataValue);
            String first4char = customDataValueString.substring(0,4);
            String lastChar = customDataValueString.substring(5);

            int intForFirst4Char = Integer.parseInt(first4char);
            int intLastChar = Integer.parseInt(lastChar);

            if (event.getRecipe().getResult().getItemMeta().getCustomModelData() == intForFirst4Char)
            {

                ItemStack craftedItem = event.getRecipe().getResult();
                ItemMeta craftedItemMeta = craftedItem.getItemMeta();

                List<String> lore = new ArrayList<String>();
                //multiply config's base nutrient values with last character of the custom model data to create a lore
                for (Map.Entry<NutrientType, Integer> nutrients : plugin.rpgFoods.get(foodEntry).getNutrients().entrySet()){

                    int value = nutrients.getValue() * intLastChar;

                    String symbol;
                    ChatColor chatColor;

                    if (value < 0){
                        symbol = "";
                        chatColor = ChatColor.RED;

                    } else {
                        symbol = "+";
                        chatColor = ChatColor.GREEN;
                    }

                    lore.add(chatColor + symbol + value + " " + nutrients.toString());
                }

                craftedItemMeta.setLore(lore);
                craftedItemMeta.setCustomModelData(intForFirst4Char);
                craftedItem.setItemMeta(craftedItemMeta);

            }
        }

    }



}
