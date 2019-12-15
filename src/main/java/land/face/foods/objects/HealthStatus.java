package land.face.foods.objects;

import org.bukkit.entity.Player;

import java.util.UUID;

public class HealthStatus {

    private int maxNutrient = 100;
    private int minNutrient = 0;
    private int protein;
    private int carbohydrates;
    private int dairy;
    private int produce;
    private int healthScore;
    private int healthyBoi;

    private UUID uuid;

    public void setUUID(UUID playerUUID){
        this.uuid = playerUUID;
    }

    public int getProtein(){
        return protein;
    }

    public void setProtein(int amount){
        if (protein + amount >= maxNutrient){
            protein = maxNutrient;
        } else if (protein + amount <= minNutrient){
            protein = minNutrient;
        } else {
            protein += amount;
        }
    }

    public int getCarbohydrates(){
        return carbohydrates;
    }

    public void setCarbohydrates(int amount){
        if (carbohydrates + amount >= maxNutrient){
            carbohydrates = maxNutrient;
        } else if (carbohydrates + amount <= minNutrient){
            carbohydrates = minNutrient;
        } else {
            carbohydrates += amount;
        }
    }

    public int getDairy(){
        return dairy;
    }

    public void setDairy(int amount){
        if (dairy + amount >= maxNutrient){
            dairy = maxNutrient;
        } else if (dairy + amount <= minNutrient){
            dairy = minNutrient;
        } else {
            dairy += amount;
        }
    }

    public int getProduce(){
        return produce;
    }

    public void setProduce(int amount){
        if (produce + amount >= maxNutrient){
            produce = maxNutrient;
        } else if (produce + amount <= minNutrient){
            produce = minNutrient;
        } else {
            produce += amount;
        }
    }

    public int getHealthScore(){
        return healthScore;
    }

    public void setHealthScore(int amount){
        healthScore += amount;
    }

    public int getHealthyBoi(){
        return healthyBoi;
    }

    public void setHealthyBoi(int amount){
        healthyBoi += amount;
    }

    //HARD SETTERS

    public void hardSetProtein(int amount){
        protein = amount;
    }

    public void hardSetDairy(int amount){
        dairy = amount;
    }

    public void hardSetCarbohydrates(int amount){
        carbohydrates = amount;
    }

    public void hardSetProduce(int amount){
        produce = amount;
    }

    public void hardSetHealthyBoi(int amount){
        healthyBoi = amount;
    }

    public void hardSetHealthScore(int amount){
        healthScore = amount;
    }

    public void resetNutrients(){
        protein = 0;
        dairy = 0;
        carbohydrates = 0;
        produce = 0;
        healthScore = 0;
        healthyBoi = 0;
    }
}