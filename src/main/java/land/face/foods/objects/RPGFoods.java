package land.face.foods.objects;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RPGFoods {

    //rpg food objects

    private String foodName;
    private Material foodItem;
    private Map<NutrientType, Integer> nutrients = new HashMap<>();
    //private List<String> nutrients = new ArrayList<String>();
    private int foodRestored;
    private int healthRestored;
    private List<String> strifeBuffs = new ArrayList<>();
    private List<PotionEffect> potionEffects = new ArrayList<>();
    private int customData;

    public String getFoodName(){
        return foodName;
    }

    public void setFoodName(String nameOfFood) {
        foodName = nameOfFood;
    }

    public Material getFoodItem(){
        return foodItem;
    }

    public void setFoodItem(String itemName){
        foodItem = Material.getMaterial(itemName);
    }

    public Map<NutrientType, Integer> getNutrients(){
        return nutrients;
    }

    public int getFoodRestored(){
        return foodRestored;
    }

    public void setFoodRestored(int amount){
        foodRestored = amount;
    }

    public int getHealthRestored(){
        return healthRestored;
    }

    public void setHealthRestored(int amount){
        healthRestored = amount;
    }

    public List<String> getStrifeBuffs(){
        return strifeBuffs;
    }

    public void setStrifeBuffs(String strifeBuffName){
        strifeBuffs.add(strifeBuffName);
    }

    public List<PotionEffect> getPotionEffects(){
        return potionEffects;
    }

    public void setPotionEffects(PotionEffect potion){
        potionEffects.add(potion);
    }

    public int getCustomData(){
        return customData;
    }

    public void setCustomData(int data){
        customData = data;
    }


}
