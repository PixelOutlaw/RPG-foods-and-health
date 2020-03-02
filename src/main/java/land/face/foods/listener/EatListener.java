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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import land.face.foods.FoodsPlugin;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EatListener implements Listener {

  private final FoodsPlugin plugin;
  private Map<UUID, Integer> eatTicksLeft = new HashMap<>();

  public EatListener(FoodsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onFoodChange(final FoodLevelChangeEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerConsumeFood(PlayerInteractEvent event) {
    if (event.getPlayer().getCooldown(Material.APPLE) > 0) {
      return;
    }
    if (event.getAction() == Action.RIGHT_CLICK_AIR
        || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      ItemStack stack;
      if (event.getHand() == EquipmentSlot.HAND) {
        stack = event.getPlayer().getEquipment().getItemInMainHand();
      } else if (event.getHand() == EquipmentSlot.OFF_HAND) {
        stack = event.getPlayer().getEquipment().getItemInOffHand();
      } else {
        return;
      }
      if (!stack.getType().isEdible()) {
        return;
      }
      event.setCancelled(true);

      // Obtain RPG Food, or use normal logic if not found
      //RPGFoods rpgFoods = plugin.getFoodsManager().getFoods(heldItemName);

      int foodAmount = food(stack.getType());
      float energy = foodAmount * 2f;

      plugin.getStrifePlugin().getEnergyManager().changeEnergy(event.getPlayer(), energy);
      plugin.getStrifePlugin().getEnergyRegenTask()
          .addEnergy(event.getPlayer().getUniqueId(), foodAmount * 3, 3600);

      event.getPlayer().getWorld().playSound(event.getPlayer().getEyeLocation(),
          Sound.ENTITY_GENERIC_EAT, 1, 1);
      setCooldowns(event.getPlayer(), 3600);
      stack.setAmount(stack.getAmount() - 1);

      // Do RPG Food effects
      // plugin.consumeFood(event.getPlayer(), rpgFoods, heldItem);
    }
  }

  @EventHandler
  public void onJoinSetFoodCooldown(final PlayerJoinEvent event) {
    if (eatTicksLeft.containsKey(event.getPlayer().getUniqueId())) {
      setCooldowns(event.getPlayer(), eatTicksLeft.get(event.getPlayer().getUniqueId()));
      eatTicksLeft.remove(event.getPlayer().getUniqueId());
    }
  }

  @EventHandler
  public void onRespawnSetFoodCooldown(final PlayerRespawnEvent event) {
    if (eatTicksLeft.containsKey(event.getPlayer().getUniqueId())) {
      setCooldowns(event.getPlayer(), eatTicksLeft.get(event.getPlayer().getUniqueId()));
      eatTicksLeft.remove(event.getPlayer().getUniqueId());
    }
  }

  @EventHandler
  public void onQuitSaveCooldown(final PlayerQuitEvent event) {
    if (event.getPlayer().getCooldown(Material.APPLE) > 0) {
      eatTicksLeft
          .put(event.getPlayer().getUniqueId(), event.getPlayer().getCooldown(Material.APPLE));
    }
  }

  @EventHandler
  public void onKickSaveCooldown(final PlayerKickEvent event) {
    if (event.getPlayer().getCooldown(Material.APPLE) > 0) {
      eatTicksLeft
          .put(event.getPlayer().getUniqueId(), event.getPlayer().getCooldown(Material.APPLE));
    }
  }

  @EventHandler
  public void onDeathSaveCooldown(final PlayerDeathEvent event) {
    if (event.getEntity().getCooldown(Material.APPLE) > 0) {
      eatTicksLeft
          .put(event.getEntity().getUniqueId(), event.getEntity().getCooldown(Material.APPLE));
    }
  }

  private void setCooldowns(Player player, int ticks) {
    player.setCooldown(Material.APPLE, ticks);
    player.setCooldown(Material.BAKED_POTATO, ticks);
    player.setCooldown(Material.BEEF, ticks);
    player.setCooldown(Material.BEETROOT, ticks);
    player.setCooldown(Material.BEETROOT_SOUP, ticks);
    player.setCooldown(Material.BREAD, ticks);
    player.setCooldown(Material.CARROT, ticks);
    player.setCooldown(Material.CHICKEN, ticks);
    player.setCooldown(Material.CHORUS_FRUIT, ticks);
    player.setCooldown(Material.COD, ticks);
    player.setCooldown(Material.COOKED_BEEF, ticks);
    player.setCooldown(Material.COOKED_CHICKEN, ticks);
    player.setCooldown(Material.COOKED_COD, ticks);
    player.setCooldown(Material.COOKED_MUTTON, ticks);
    player.setCooldown(Material.COOKED_PORKCHOP, ticks);
    player.setCooldown(Material.COOKED_RABBIT, ticks);
    player.setCooldown(Material.COOKED_SALMON, ticks);
    player.setCooldown(Material.COOKIE, ticks);
    player.setCooldown(Material.DRIED_KELP, ticks);
    player.setCooldown(Material.ENCHANTED_GOLDEN_APPLE, ticks);
    player.setCooldown(Material.GOLDEN_APPLE, ticks);
    player.setCooldown(Material.GOLDEN_CARROT, ticks);
    player.setCooldown(Material.HONEY_BOTTLE, ticks);
    player.setCooldown(Material.MELON_SLICE, ticks);
    player.setCooldown(Material.MUSHROOM_STEW, ticks);
    player.setCooldown(Material.MUTTON, ticks);
    player.setCooldown(Material.POISONOUS_POTATO, ticks);
    player.setCooldown(Material.PORKCHOP, ticks);
    player.setCooldown(Material.POTATO, ticks);
    player.setCooldown(Material.PUFFERFISH, ticks);
    player.setCooldown(Material.PUMPKIN_PIE, ticks);
    player.setCooldown(Material.RABBIT, ticks);
    player.setCooldown(Material.RABBIT_STEW, ticks);
    player.setCooldown(Material.ROTTEN_FLESH, ticks);
    player.setCooldown(Material.SALMON, ticks);
    player.setCooldown(Material.SPIDER_EYE, ticks);
    player.setCooldown(Material.SUSPICIOUS_STEW, ticks);
    player.setCooldown(Material.SWEET_BERRIES, ticks);
    player.setCooldown(Material.TROPICAL_FISH, ticks);
  }

  // TODO: Turn this crap into a configurable 'defaults' map, jeez
  private int food(Material material) {
    switch (material) {
      case APPLE:
        return 2;
      case BAKED_POTATO:
      case BEEF:
      case BEETROOT:
      case BEETROOT_SOUP:
      case BREAD:
      case CARROT:
      case CHICKEN:
      case CHORUS_FRUIT:
      case COD:
      case COOKED_BEEF:
      case COOKED_CHICKEN:
      case COOKED_COD:
      case COOKED_MUTTON:
      case COOKED_PORKCHOP:
      case COOKED_RABBIT:
      case COOKED_SALMON:
      case COOKIE:
      case DRIED_KELP:
        return 5;
      case ENCHANTED_GOLDEN_APPLE:
      case GOLDEN_APPLE:
        return 20;
      case GOLDEN_CARROT:
      case HONEY_BOTTLE:
      case MELON_SLICE:
      case MUSHROOM_STEW:
      case MUTTON:
      case POISONOUS_POTATO:
      case PORKCHOP:
      case POTATO:
      case PUFFERFISH:
      case PUMPKIN_PIE:
      case RABBIT:
      case RABBIT_STEW:
      case ROTTEN_FLESH:
      case SALMON:
      case SPIDER_EYE:
      case SUSPICIOUS_STEW:
      case SWEET_BERRIES:
      case TROPICAL_FISH:
        return 5;
      default:
        return 0;
    }
  }
}